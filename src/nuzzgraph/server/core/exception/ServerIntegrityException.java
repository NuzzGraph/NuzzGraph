package nuzzgraph.server.core.exception;

/**
 * Created by IntelliJ IDEA.
 * User: Admin
 * Date: 3/20/12
 * Time: 12:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class ServerIntegrityException extends Exception
{
    public ServerIntegrityException()
    {
        super();
    }

    public ServerIntegrityException(String message)
    {
        super(message);
    }
}
