/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nuzzgraph.server.core;

/**
 *
 * @author Admin
 */
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

public class ServerHandler extends AbstractHandler
{
    public void handle(String target, Request baseRequest,HttpServletRequest request,HttpServletResponse response) 
        throws IOException, ServletException
    {
        String url = target;
        String[] tokens = url.split("/");
        String responseOutput = "";
        
        //get controller type and parameters
        if (tokens.length > 0)
        {
            String controllerType = tokens[1].toLowerCase();

            //Initialize controller parameters
            String[] parameters;
            if (tokens.length > 2) //URL contains additional characters
            {
                parameters = Arrays.copyOfRange(tokens, 2, tokens.length);
            }
            else
            {
                parameters = null;
            }



            switch(controllerType)
            {
                case "test":
                    responseOutput = "Test successful.";
                    break;
                case "node":
                    responseOutput = NodeController.processRequest(parameters);
                    break;
            }
        }
        response.setContentType("application/json;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);
        response.getWriter().print(responseOutput);
    }
}

