import java.io.*;
import java.net.Socket;
import java.util.*;

import javax.swing.DefaultListModel;
/**
 The HTTP server thread class
 @author Cory
 */
public class HTTPRequest extends Thread
{
   final int PAT1 = 5;
   final int PAT2 = 19;
   final boolean logOutput = false;
   private Socket sock;
   private DataOutputStream writeSock;
   private BufferedReader readSock;
   private DefaultListModel activeRequests;
   
   /**
   Constructor that passes the socket
   @param s current socket
   */
   public HTTPRequest(Socket s, DefaultListModel l)
   {
      try{
		 activeRequests = l;
		 sock = s;
		 writeSock = new DataOutputStream(sock.getOutputStream());
		 readSock = new BufferedReader(new InputStreamReader(sock.getInputStream()));
      }
      catch(IOException ex){
         System.out.println("Something went wrong: " + ex.toString());
      }
   }
   
   /**
   Start interpreting the request
   */
   public void run()
   {
      HTTP processor = new HTTP(activeRequests);
      try{
         String inLine = readSock.readLine();
         processor.processRequest(inLine, writeSock);
         sock.close();
      }
      catch(IOException | java.lang.NullPointerException err){
         System.out.println("Something went wrong: " + err.toString());
      }
   }
}
