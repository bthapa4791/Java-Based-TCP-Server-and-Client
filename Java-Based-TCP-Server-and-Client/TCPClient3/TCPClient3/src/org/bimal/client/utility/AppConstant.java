/**
 * 
 */
package org.bimal.client.utility;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.PrintStream;
import java.net.Socket;

/**
 * @author thapa_bimal
 *
 */
public class AppConstant {
	public static int portNumber = 8080;
	public static String host = "localhost";
    public static Socket clientSocket = null;
    public static PrintStream outputStream = null;
    public static DataInputStream inFromUser = null;
    public static BufferedReader inputFromUser = null;
    public static boolean closed = false;
}
