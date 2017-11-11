/**
 * 
 */
package org.bimal.client.controller;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;

import org.bimal.client.utility.AppConstant;

/**
 * @author thapa_bimal
 *
 */
public class TCPClient implements Runnable {
	 
	// This is the main function of the class.
	  public static void main(String[] args) {
		 connectToServer();
		 sendMsg();
	  }

	  // This is the function which connect to the server.
	  private static void connectToServer() {
		 try {
		     AppConstant.clientSocket = new Socket(AppConstant.host, AppConstant.portNumber);
	         AppConstant.inputFromUser = new BufferedReader(new InputStreamReader(System.in));
		     AppConstant.outputStream = new PrintStream(AppConstant.clientSocket.getOutputStream());
		     AppConstant.inFromUser = new DataInputStream(AppConstant.clientSocket.getInputStream());
		   } catch (UnknownHostException e) {
	      System.out.println(e.toString());
	    } catch (IOException e) {
	    	System.out.println(e.toString());
		}
	}

	  // This is the function for sending message to other clients through server.
	  private static void sendMsg() {
		  if (AppConstant.clientSocket != null && AppConstant.inFromUser != null) {
		      try {
		        new Thread(new TCPClient()).start();
		        while (!AppConstant.closed) {
		          AppConstant.outputStream.println(AppConstant.inputFromUser.readLine().trim());
		        }
		        AppConstant.outputStream.close();
		        AppConstant.inFromUser.close();
		        AppConstant.clientSocket.close();
		      } catch (IOException e) {
		        System.out.println(e.toString());
		      }
		    }
	  }
	  
	// This is the thread to read from the server continuously.
	  public void run() {
	    String fromServer;
	    try {
	      while ((fromServer = AppConstant.inFromUser.readLine()) != null) {
	        System.out.println(fromServer);
	      }
	      AppConstant.closed = true;
	    } catch (IOException e) {
	      System.out.println(e.toString());
	    }
	  }
	}