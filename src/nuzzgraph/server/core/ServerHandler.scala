/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nuzzgraph.server.core

/**
 *
 * @author Admin
 */

import org.eclipse.jetty.server.Request
import org.eclipse.jetty.server.handler.AbstractHandler
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import java.io.IOException
import java.util.Arrays

class ServerHandler extends AbstractHandler {
  /**
   * Entry point for incoming request at servername:port
   * @param target The portion of the URL to the right of the server location
   * @param baseRequest The Request object associated with this request
   * @param request The HttpServletRequest object associated with this request
   * @param response The HttpServletResponse object associated with this response
   * @throws IOException
   * @throws ServletException
   */
  def handle(target: String, baseRequest: Request, request: HttpServletRequest, response: HttpServletResponse): Unit = {
    var url: String = target
    var tokens: Array[String] = url.split("/")
    var responseOutput: String = ""
    if (tokens.length > 0) {
      var controllerType: String = tokens(1).toLowerCase
      var parameters: Array[String] = null
      if (tokens.length > 2) {
        parameters = Arrays.copyOfRange(tokens, 2, tokens.length)
      }
      else {
        parameters = null
      }
      controllerType match {
        case "test" =>
          responseOutput = "Test successful."
          break //todo: break is not supported
        case "node" =>
          responseOutput = NodeController.processRequest(parameters)
          break //todo: break is not supported
      }
    }
    response.setContentType("application/json;charset=utf-8")
    response.setStatus(HttpServletResponse.SC_OK)
    baseRequest.setHandled(true)
    response.getWriter.print(responseOutput)
  }
}
