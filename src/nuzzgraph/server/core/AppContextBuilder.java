package nuzzgraph.server.core;


import org.eclipse.jetty.webapp.WebAppContext;

public class AppContextBuilder 
{
	private WebAppContext webAppContext;
	
	public WebAppContext buildWebAppContext()
	{
            webAppContext = new WebAppContext();
            webAppContext.setDescriptor(webAppContext + "/WEB-INF/web.xml");
            webAppContext.setResourceBase(".");
            webAppContext.setContextPath("/runJetty");
            return webAppContext;
	}
}
