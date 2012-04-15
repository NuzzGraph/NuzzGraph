package nuzzgraph.dbclient.test

import org.scalatest.FunSuite
import nuzzgraph.server.core.{ServerStatus, ServerController}
import nuzzgraph.dbclient.main.DBClientMain

/**
 *
 * User: Mark Nuzzolilo
 * Date: 4/9/12
 * Time: 11:43 PM
 */

class DBClientMain_test extends FunSuite
{
  test("Connect to server")
  {
    DBClientMain.connectToServer
    assert(ServerController.getStatus == ServerStatus.Available)
  }

}
