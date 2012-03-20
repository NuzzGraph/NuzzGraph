/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nuzzgraph.server.core;

import nuzzgraph.server.core.exception.ServerIntegrityException;

public class NodeController
{
    public static String processRequest(String[] args)
    {
        String output;
        NodeInstance node;

        //args[0] = node id
        //args[1] = function name
        try
        {
            if (args == null || args.length < 1)
                return "";
            
            long id = Long.parseLong(args[0]);
    
            ServerController.getGraphDB().getVertex(id);
            try
            {
                node = NodeInstance.get(id);
            }
            catch (ServerIntegrityException ex)
            {
                output = ex.getMessage();
                return output;
            }
    
            try
            {
                output = "JSON NOT IMPLEMENTED";
                //output = JsonConvert.SerializeObject(node, Formatting.Indented);
            }
            catch (Exception e)
            {
                output = "Output is of type " + node.getClass() +
                    " but it cannot be displayed.";
                return output;
            }
        }
        catch (Exception e)
        {
            output = "Error processing request." + System.getProperty("line.separator") +
                    e.toString();
        }
        return output;
    }
}
