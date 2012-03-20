/*
 * Copyright 1999-2010 Luca Garulli (l.garulli--at--orientechnologies.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.orientechnologies.orient.test.database.auto;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.testng.Assert;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.db.graph.OGraphDatabasePool;
import com.orientechnologies.orient.core.db.graph.OGraphElement;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.orientechnologies.orient.core.tx.OTransaction.TXTYPE;

@Test
public class GraphDatabaseTest {
	private OGraphDatabase	database;
	private String					url;

	@Parameters(value = "url")
	public GraphDatabaseTest(String iURL) {
		database = new OGraphDatabase(iURL);
		url = iURL;
	}

	@Test
	public void testPool() throws IOException {
		final OGraphDatabase[] dbs = new OGraphDatabase[OGraphDatabasePool.global().getMaxSize()];

		for (int i = 0; i < 10; ++i) {
			for (int db = 0; db < dbs.length; ++db)
				dbs[db] = OGraphDatabasePool.global().acquire(url, "admin", "admin");
			for (int db = 0; db < dbs.length; ++db)
				dbs[db].close();
		}
	}

	@Test
	public void populate() {
		database.open("admin", "admin");

		try {

			OClass vehicleClass = database.createVertexType("GraphVehicle");
			database.createVertexType("GraphCar", vehicleClass);
			database.createVertexType("GraphMotocycle", "GraphVehicle");

			ODocument carNode = (ODocument) database.createVertex("GraphCar").field("brand", "Hyundai").field("model", "Coupe")
					.field("year", 2003).save();
			ODocument motoNode = (ODocument) database.createVertex("GraphMotocycle").field("brand", "Yamaha")
					.field("model", "X-City 250").field("year", 2009).save();

			database.createEdge(carNode, motoNode).save();

			List<ODocument> result = database.query(new OSQLSynchQuery<ODocument>("select from GraphVehicle"));
			Assert.assertEquals(result.size(), 2);
			for (ODocument v : result) {
				Assert.assertTrue(v.getSchemaClass().isSubClassOf(vehicleClass));
			}

		} finally {
			database.close();
		}

		database.open("admin", "admin");
		try {

			database.getMetadata().getSchema().reload();

			List<ODocument> result = database.query(new OSQLSynchQuery<ODocument>("select from GraphVehicle"));
			Assert.assertEquals(result.size(), 2);

			ODocument edge1 = null;
			ODocument edge2 = null;

			for (ODocument v : result) {
				Assert.assertTrue(v.getSchemaClass().isSubClassOf("GraphVehicle"));

				if (v.getClassName().equals("GraphCar")) {
					Assert.assertEquals(database.getOutEdges(v).size(), 1);
					edge1 = (ODocument) database.getOutEdges(v).iterator().next();
				} else {
					Assert.assertEquals(database.getInEdges(v).size(), 1);
					edge2 = (ODocument) database.getInEdges(v).iterator().next();
				}
			}

			Assert.assertEquals(edge1, edge2);

		} finally {
			database.close();
		}
	}

	@Test(dependsOnMethods = "populate")
	public void testSQLAgainstGraph() {
		database.open("admin", "admin");

		try {
			ODocument tom = (ODocument) database.createVertex().field("name", "Tom").save();
			ODocument ferrari = (ODocument) database.createVertex("GraphCar").field("brand", "Ferrari").save();
			ODocument maserati = (ODocument) database.createVertex("GraphCar").field("brand", "Maserati").save();
			ODocument porsche = (ODocument) database.createVertex("GraphCar").field("brand", "Porsche").save();
			database.createEdge(tom, ferrari).field("label", "drives").save();
			database.createEdge(tom, maserati).field("label", "drives").save();
			database.createEdge(tom, porsche).field("label", "owns").save();

			Assert.assertNotNull(database.getOutEdges(tom, "drives"));
			Assert.assertFalse(database.getOutEdges(tom, "drives").isEmpty());

			List<OGraphElement> result = database.query(new OSQLSynchQuery<OGraphElement>(
					"select out[in.@class = 'GraphCar'].in from V where name = 'Tom'"));
			Assert.assertEquals(result.size(), 1);

			result = database.query(new OSQLSynchQuery<OGraphElement>(
					"select out[label='drives'][in.brand = 'Ferrari'].in from V where name = 'Tom'"));
			Assert.assertEquals(result.size(), 1);

			result = database.query(new OSQLSynchQuery<OGraphElement>("select out[in.brand = 'Ferrari'].in from V where name = 'Tom'"));
			Assert.assertEquals(result.size(), 1);

		} finally {
			database.close();
		}
	}

	@Test
	public void testDictionary() {
		database.open("admin", "admin");

		try {
			ODocument rootNode = database.createVertex().field("id", 54254454);
			database.setRoot("test123", rootNode);
			rootNode.save();

			database.close();
			database.open("admin", "admin");

			ODocument secroot = database.getRoot("test123");
			Assert.assertEquals(secroot.getIdentity(), rootNode.getIdentity());
		} finally {
			database.close();
		}
	}

	@Test
	public void testSubVertexQuery() {
		database.open("admin", "admin");

		try {
			database.createVertexType("newV").createProperty("f_int", OType.INTEGER).createIndex(OClass.INDEX_TYPE.UNIQUE);
			database.getMetadata().getSchema().save();

			database.createVertex("newV").field("f_int", 2).save();
			database.createVertex("newV").field("f_int", 1).save();
			database.createVertex("newV").field("f_int", 3).save();

			// query 1
			String q = "select * from V where f_int between 0 and 10";
			List<ODocument> resB = database.query(new OSQLSynchQuery<ODocument>(q));
			System.out.println(q + ": ");
			for (OIdentifiable v : resB) {
				System.out.println(v);
			}

			// query 2
			q = "select * from newV where f_int between 0 and 10";
			List<ODocument> resB2 = database.query(new OSQLSynchQuery<ODocument>(q));
			System.out.println(q + ": ");
			for (OIdentifiable v : resB2) {
				System.out.println(v);
			}
		} finally {
			database.close();
		}
	}

	public void testNotDuplicatedIndexTxChanges() throws IOException {
		database.open("admin", "admin");

		try {
			OClass oc = database.createVertexType("vertexA");
			oc.createProperty("name", OType.STRING);
			oc.createIndex("vertexA_name_idx", OClass.INDEX_TYPE.UNIQUE, "name");

			// FIRST: create a couple of records
			ODocument docA = database.createVertex("vertexA");
			docA.field("name", "myKey");
			database.save(docA);

			ODocument docB = database.createVertex("vertexA");
			docA.field("name", "anotherKey");
			database.save(docB);

			database.begin();
			database.delete(docB);
			database.delete(docA);
			ODocument docKey = database.createVertex("vertexA");
			docKey.field("name", "myKey");
			database.save(docKey);
			database.commit();

		} finally {
			database.close();
		}
	}

	public void testAutoEdge() throws IOException {
		database.open("admin", "admin");

		try {
			ODocument docA = database.createVertex();
			docA.field("name", "selfEdgeTest");
			database.createEdge(docA, docA).save();

			docA.reload();

		} finally {
			database.close();
		}
	}

	//
	// @Test
	// public void testTxDictionary() {
	// database.open("admin", "admin");
	//
	// database.begin();
	//
	// try {
	// ODocument rootNode = database.createVertex().field("id", 54254454);
	// database.setRoot("test123", rootNode);
	// rootNode.save();
	//
	// database.commit();
	//
	// database.close();
	// database.open("admin", "admin");
	//
	// ODocument secroot = database.getRoot("test123");
	// Assert.assertEquals(secroot.getIdentity(), rootNode.getIdentity());
	// } finally {
	// database.close();
	// }
	// }

	/**
	 * @author bill@tobecker.com
	 */
	public void testTxField() {
		database.open("admin", "admin");

		if (database.getVertexType("PublicCert") == null)
			database.createVertexType("PublicCert");

		// Step 1
		// create a public cert with some field set
		ODocument publicCert = (ODocument) database.createVertex("PublicCert").field("address", "drevil@myco.mn.us").save();

		// Step 2
		// update the public cert field in transaction
		database.begin(TXTYPE.OPTIMISTIC);
		publicCert.field("address", "newaddress@myco.mn.us").save();
		database.commit();

		// Step 3
		// try transaction with a rollback
		database.begin(TXTYPE.OPTIMISTIC);
		ODocument publicCertNew = (ODocument) database.createVertex("PublicCert").field("address", "iagor@myco.mn.us").save();
		database.rollback();

		// Step 4
		// just show what is there
		List<ODocument> result = database.query(new OSQLSynchQuery<ODocument>("select * from PublicCert"));

		for (ODocument d : result) {
			System.out.println("(-1) Vertex: " + d);
		}

		// Step 5
		// try deleting all the stuff
		database.command(new OCommandSQL("delete from PublicCert")).execute();

		database.close();
	}
//
//	@Test
//	public void testSomething() throws Exception {
//		database.open("admin", "admin");
//
//		if (database.getVertexType("Foo") != null)
//			database.createVertexType("Foo");
//		if (database.getVertexType("Bletch") != null)
//			database.createVertexType("Bletch");
//		if (database.getVertexType("Bar") != null)
//			database.createEdgeType("Bar");
//
//		try {
//			// Step 1
//			// Create a foo
//			ODocument foo1 = (ODocument) database.createVertex("Foo").field("address", "testing").save();
//
//			// Step 2 create a bunch of foos and connect them to THE foo
//			for (int i = 0; i < 18; ++i) {
//				database.begin(TXTYPE.OPTIMISTIC);
//
//				try {
//					ODocument foo = (ODocument) database.createVertex("Bletch").field("address", "test" + i).save();
//
//					database.createEdge(foo.getIdentity(), foo1.getIdentity(), "Bar").save();
//					database.commit();
//				} catch (Exception e) {
//					e.printStackTrace();
//					database.rollback();
//				}
//			}
//
//			// just show what is there
//			List<ODocument> result = database.query(new OSQLSynchQuery<ODocument>("select * from Foo"));
//
//			// Step 3
//			for (ODocument d : result) {
//				System.out.println("Vertex: " + d);
//
//				// Step 4
//				Set<OIdentifiable> result1 = database.getInEdges(d);
//
//				for (OIdentifiable e : result1) {
//					System.out.println("In Edge: " + e);
//				}
//			}
//
//		} finally {
//			database.close();
//		}
//	}
}
