/**
 * 
 */
package org.bimal.server.controller;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.bimal.server.utility.AppConstant;

/**
 * @author thapa_bimal
 *
 */
public class TCPServer {

	  private static final forMultipleclient[] clientThread = new forMultipleclient[AppConstant.maxClientNum];

	  public static void main(String args[]) {
		  
		// This method will create server socket.
		  createServerSocket();
		  
		// This method will create client socket.
		  createClientSocket();
	  }

	private static void createServerSocket() {
		try {
		      AppConstant.welcomeSocket= new ServerSocket(AppConstant.portNumber);
		      System.out.println("*** This is the conference server running at port number " + AppConstant.portNumber + ". ***" );
		    } catch (IOException e) {
		      System.out.println(e.toString());
		    }
		}
	
	private static void createClientSocket() {
		while (true) {
		      try {
		        AppConstant.connectionSocket = AppConstant.welcomeSocket.accept();
		        int i = 0;
		        for (i = 0; i < AppConstant.maxClientNum; i++) {
		          if (clientThread[i] == null) {
		            (clientThread[i] = new forMultipleclient(AppConstant.connectionSocket, clientThread)).start();
		            break;
		          }
		        }
		        if (i == AppConstant.maxClientNum) {
		          AppConstant.outputStream  = new PrintStream(AppConstant.connectionSocket.getOutputStream());
		          AppConstant.outputStream.println("Sorry, Please try again later!");
		          AppConstant.outputStream.close();
		          AppConstant.connectionSocket.close();
		        }
		      } catch (IOException e) {
		        System.out.println(e.toString());
		      }
		    }
		}
	}

	// This a inner class which extends thread for connecting multiple clients to each other through sever.
	class forMultipleclient extends Thread {
		
		  private Socket connectionSocket = null;
		  private final forMultipleclient[] clientclientThreads;
		  private int maxClientNum;
		  private String clientName = null;
		  private DataInputStream inFromUser = null;
		  private PrintStream outputStream = null;
		  private String name;
		  private Date date;
		  DateFormat dateFormat;
		  
		  public forMultipleclient(Socket connectionSocket, forMultipleclient[] clientclientThreads) {
		    this.connectionSocket = connectionSocket;
		    this.clientclientThreads = clientclientThreads;
		    maxClientNum = clientclientThreads.length;
		  }

		  public void run() {
		    int maxClientNum = this.maxClientNum;
		    forMultipleclient[] clientThreads = this.clientclientThreads;

		    try {
		      dateFormat = new SimpleDateFormat("hh:mma");
		      date = new Date();
		      createStreamClient();
		      AppConstant.userList.add(name);
		      outputStream.println(dateFormat.format(date) + " Thankyou for connecting to the conference server.\n" + "'" +name+ "'" + ",you now have access to the conference.\n"
		      		+ "Just type the message and press enter to send the message to all users.\n"
		    		+ "Type >>whisper username message and press enter if you want to send message to specific user.\n"
		    		+ "Type >>userlist and press enter if you want to get the list of current user in the system.\n"
		    		+ "Type >>quit and press enter if you want to leave.");
		     
		      synchronized (this) {
		        for (int i = 0; i < maxClientNum; i++) {
		          if (clientThreads[i] != null && clientThreads[i] == this) {
		            clientName = name;
		            break;
		          }
		        }
		        for (int i = 0; i < maxClientNum; i++) {
		          if (clientThreads[i] != null && clientThreads[i] != this) {
		            clientThreads[i].outputStream.println("!!! User named " + name
		                + " is now in the chatting system !!!");
		          }
		        }
		      }
		      while (true) {
		    	dateFormat = new SimpleDateFormat("hh:mma");
		        date = new Date(); 
		        String stringFromUser = inFromUser.readLine();
		        if (stringFromUser.startsWith(">>quit")) {
		          break;
		        }else if(stringFromUser.equalsIgnoreCase(">>userlist")) {
		        	System.out.println("Current users in the system: " + AppConstant.userList);
		        	for (int i = 0; i < maxClientNum; i++) {
		                  if (clientThreads[i] != null && clientThreads[i] == this && clientThreads[i].clientName != null) {
		                    clientThreads[i].outputStream.println(dateFormat.format(date) + " Current users in the system: " + AppConstant.userList);
		                    break;
		                  }
		               }
		        }else if (stringFromUser.startsWith(">>whisper")) {
		          String[] private_message = stringFromUser.split(" ", 3);
		          if (private_message.length > 1 && private_message[2] != null) 
		          {
		            private_message[2] = private_message[2].trim();
		            if (!private_message[2].isEmpty()) {
		              synchronized (this) {
		                for (int i = 0; i < maxClientNum; i++) {
		                  if (clientThreads[i] != null && clientThreads[i] != this && clientThreads[i].clientName != null && clientThreads[i].clientName.equals(private_message[1])) {
		                    clientThreads[i].outputStream.println(dateFormat.format(date) + " Private Message from " + name + ": " + private_message[2]);
		                    
		                    this.outputStream.println(dateFormat.format(date) + " >>" + name + ": " + private_message[2]);
		                    break;
		                  }
		                }
		              }
		            }
		          }
		        } else {
		          synchronized (this) {
		            for (int i = 0; i < maxClientNum; i++) {
		              if (clientThreads[i] != null && clientThreads[i].clientName != null) {
		                clientThreads[i].outputStream.println(dateFormat.format(date) + " >>" + name + ": " + stringFromUser);
		              }
		            }
		          }
		        }
		      }
		      synchronized (this) {
		        for (int i = 0; i < maxClientNum; i++) {
		          if (clientThreads[i] != null && clientThreads[i] != this && clientThreads[i].clientName != null) {
		            clientThreads[i].outputStream.println(dateFormat.format(date) + " !!! " + name + " quit the conference. !!!");
		          }
		        }
		      }		      
		      outputStream.println("!!! Thankyou for using the service " + name + " !!!\n Good Bye");

		      synchronized (this) {
		        for (int i = 0; i < maxClientNum; i++) {
		          if (clientThreads[i] == this) {
		            clientThreads[i] = null;
		          }
		        }
		      }
		      outputStream.close();
		      inFromUser.close();
		      connectionSocket.close();
		    } catch (IOException e) {
	    }
	}

		private void createStreamClient() {
			try {
				inFromUser = new DataInputStream(connectionSocket.getInputStream());
				outputStream = new PrintStream(connectionSocket.getOutputStream());
			      while (true) {
			        outputStream.println("!!! Welcome to the conference system. !!! \nPlease enter your name to begin:");
			        name = inFromUser.readLine().trim();
			        if (name == null && name.isEmpty()) {
						outputStream.println("Please enter your name to begin:");
					} else if (name.indexOf('>') == -1) {
			          break;
			        } else {
			          outputStream.println(" The specific character '>' is invalid for user name.");
			        }
			        
			      }
			} catch (IOException e) {
				System.out.println(e.toString());
			}
		}
	}