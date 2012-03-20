package nuzzgraph.server.core;

import com.tinkerpop.blueprints.pgm.Vertex;

import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: Mark Nuzzolilo
 * Date: 3/19/12
 * Time: 10:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class NodePropertiesContainer extends HashMap<String, String>
{
    public NodePropertiesContainer()
    {
        super();
    }

    public NodePropertiesContainer(Vertex v)
    {
        super();
        for (String key : v.getPropertyKeys())
        {
            super.put(key, (String)v.getProperty(key));
        }
    }


}
