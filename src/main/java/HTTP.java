
import java.io.*;
import java.util.*;

import javax.swing.DefaultListModel;

/**
 Handles general HTTP IO
 @author Cory
 */
public class HTTP 
{
   final private String CRLF = "\r\n";
   final private int CHUNKSIZE = 1024;
   private String reqFileName;
   private FileInputStream fileInput;
   private DefaultListModel activeRequests;
   
   public HTTP(DefaultListModel l)
   {
	   activeRequests = l;
   }
   
   /**
   Returns a status line
   @param code status code
   @param phrase status meaning
   @return a complete status line
   */
   private String statusLine(int code, String phrase)
   {
      return "HTTP/1.0 " + code + " " + phrase + CRLF;
   }
   
   /**
   Returns a content type header according to the filename
   @param fileName the file's name
   @return the content type header line
   */
   private String contentType(String fileNameMC)
   {
      String fileName = fileNameMC.toLowerCase();
      String returnOut = "Content-type: ";
      if(fileName.endsWith(".htm") || fileName.endsWith(".html"))
         returnOut += "text/html";
      else if(fileName.endsWith(".xml"))
         returnOut += "application/xml";
      else if(fileName.endsWith(".jpg") || fileName.endsWith(".jpeg"))
         returnOut += "image/jpeg";
      else if(fileName.endsWith(".png"))
         returnOut += "image/png";
      else if(fileName.endsWith(".gif"))
         returnOut += "image/gif";
      else if(fileName.endsWith(".ico"))
         returnOut += "image/x-icon";
      else if(fileName.endsWith(".css"))
         returnOut += "text/css";
      else if(fileName.endsWith(".mp3"))
         returnOut += "audio/mpeg";
      else if(fileName.endsWith(".ogg"))
         returnOut += "audio/ogg";
      else if(fileName.endsWith(".mp4"))
         returnOut += "video/mp4";
      else if(fileName.endsWith(".otf"))
         returnOut += "application/x-font-otf";
      else if(fileName.endsWith(".ttf"))
         returnOut += "application/x-font-ttf";
      else if(fileName.endsWith(".woff"))
         returnOut += "application/x-font-woff";
      else
         returnOut += "application/octet-stream";
      return returnOut + CRLF;
   }
   
   /**
   Reads the request and responds with a byte stream
   @param lineInput the first line of the HTTP request
   @param httpOutput the output stream
   */
   public void processRequest(String lineInput, DataOutputStream httpOutput)
   {
      try{
         byte[] bytebuffer = new byte[CHUNKSIZE];
         StringTokenizer st = new StringTokenizer(lineInput);
         st.nextElement();
         reqFileName = "." + st.nextToken();
         System.out.println("GET request received: " + reqFileName);
         if(openfile(httpOutput))
         {
            int numBytes = fileInput.read(bytebuffer);
            while(numBytes == CHUNKSIZE)
            {
               httpOutput.write(bytebuffer, 0, numBytes);
               numBytes = fileInput.read(bytebuffer);
            }
            System.out.println("File sent: " + reqFileName);
         }
      }
      catch(IOException ex)
      {
         System.out.println("Error (" + ex.toString() +
               ") in request for file: " + reqFileName);
      }
   }
   
   /**
   Loads up fileInput, outputs a 404 doc if file can't be found
   @param httpOutput the output stream
   @return true if file exists
   @throws IOException in the case of an error
   */
   private boolean openfile(DataOutputStream httpOutput) throws IOException
   {
      try{
    	 if(reqFileName.equals("./"))
    	 {
    		 httpOutput.writeBytes(statusLine(200,"OK"));
    	      httpOutput.writeBytes(contentType("./index.html"));
    	      httpOutput.writeBytes(CRLF);
    	      httpOutput.writeBytes("<html><head>\n" +
    	         "<title>Request Manager Button</title>\n"
    	         + "<link rel=\"shortcut icon\" type=\"image/x-icon\" href=\"https://beam.pro/_latest/img/favicon/favicon.ico?build=5639e05\">"
    	         + "<script type=\"text/javascript\">"
    	         + "function removeone() {"
    	         + "var d = new Date();"
	         	 +"var xhttp = new XMLHttpRequest();"
    	      	 +"xhttp.onreadystatechange = function() {"
	  			 +"if (this.readyState == 4 && this.status == 200) {"
    			 +"d = new Date();"
     			 +"}"
				 +"};"
  				 +"xhttp.open(\"GET\", \"removeone?id=\"+d.getTime(), true);"
				 +"xhttp.send();"
				 +"}"
				 + "function removebottom() {"
    	         + "var d = new Date();"
	         	 +"var xhttp = new XMLHttpRequest();"
    	      	 +"xhttp.onreadystatechange = function() {"
	  			 +"if (this.readyState == 4 && this.status == 200) {"
    			 +"d = new Date();"
     			 +"}"
				 +"};"
  				 +"xhttp.open(\"GET\", \"removebottom?id=\"+d.getTime(), true);"
				 +"xhttp.send();"
				 +"}"
    	         + "</script>"
    	         + "<style>"
    	         + "body{background-color:#1A237E;text-align:center;padding-top:100px;}"
    	         + "button{background-color:#1A237E;background-color:rgba(255,255,255,0);"
    	         + "color:#fff;border:5px solid #fff;cursor:pointer;font-size:xx-large;}"
    	         + "button:hover{background-color:#303F9F;background-color:rgba(255,255,255,.3);}"
    	         + "</style>"
    	         + "</head>"
    	         + "<body>\n"
    	         + "<button onclick=\"removeone()\">Remove One</button><br/><br/>"
    	         + "<button onclick=\"removebottom()\" title=\"from bottom of the list\">Remove Bottom</button>\n"
    	         + "</body></html>\r\n");
    	      return false;
    	 }
    	 if(reqFileName.startsWith("./removeone?id="))
    	 {
    		 if(activeRequests.size() > 0)
    			 activeRequests.remove(0);
    		 httpOutput.writeBytes(statusLine(200,"OK"));
  			 httpOutput.writeBytes(contentType("./index.html"));
  			 httpOutput.writeBytes(CRLF);
  			 httpOutput.writeBytes("\r\n");
  			 return false;
    	 }
    	 else if(reqFileName.startsWith("./removebottom?id="))
    	 {
    		 if(activeRequests.size() > 0)
    			 activeRequests.remove(activeRequests.size()-1);
    		 httpOutput.writeBytes(statusLine(200,"OK"));
  			 httpOutput.writeBytes(contentType("./index.html"));
  			 httpOutput.writeBytes(CRLF);
  			 httpOutput.writeBytes("\r\n");
  			 return false;
    	 }
         /*fileInput = new FileInputStream(reqFileName);
         httpOutput.writeBytes(statusLine(200,"OK"));
         httpOutput.writeBytes(contentType(reqFileName));
         httpOutput.writeBytes(CRLF);*/
    	 output404(httpOutput);
    	 return false;
      }
      catch(FileNotFoundException ex)
      {
         try{
            output404(httpOutput);
         }
         catch(IOException why)
         {
            System.out.println("Something went wrong: " + why.toString());
         }
         return false;
      }
   }
   
   /**
   Outputs the 404 HTML doc
   @param httpOutput the output stream
   @throws IOException in the case of an error
   */
   private void output404(DataOutputStream httpOutput) throws IOException
   {
      System.out.println("File wasn't found: " + reqFileName);
      httpOutput.writeBytes(statusLine(404,"Not Found"));
      httpOutput.writeBytes(contentType("./404.html"));
      httpOutput.writeBytes(CRLF);
      httpOutput.writeBytes("<html><head>\n" +
         "<title>Not Found</title>\n" +
         "</head><body>\n" +
         "Not Found\n" +
         "</body></html>\r\n");
   }
}
