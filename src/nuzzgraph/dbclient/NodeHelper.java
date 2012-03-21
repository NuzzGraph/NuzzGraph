package nuzzgraph.dbclient;

import com.tinkerpop.blueprints.pgm.Vertex;

/**
 * User: Mark Nuzzolilo
 * Date: 3/21/12
 * Time: 1:18 AM
 */
public class NodeHelper
{
    static int vertexClusterId;

    public static int GetNodeId(Vertex v)
    {
        String vId = v.getId().toString();
        return Integer.parseInt(vId.substring(vId.indexOf(":") + 1));
    }
}
