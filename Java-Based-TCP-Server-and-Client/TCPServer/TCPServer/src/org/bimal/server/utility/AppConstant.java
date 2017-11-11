/**
 * 
 */
package org.bimal.server.utility;

import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * @author thapa_bimal
 *
 */
public class AppConstant {
	  public static final int portNumber = 8080;
	  public static ServerSocket welcomeSocket = null;
	  public static Socket connectionSocket = null;
	  public static final int maxClientNum = 5;
	  public static PrintStream outputStream = null;
	  public static List<String> userList = new ArrayList<String>();
}
