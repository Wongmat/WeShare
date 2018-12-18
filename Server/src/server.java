import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class server {
	 static HashMap < String, Chatroom > Rooms = new HashMap < > ();
	 public static void main(String[] args) throws IOException {
	  ServerSocket listener = new ServerSocket(12345);
	  Rooms.put("News", new Chatroom("News"));
	  while (true) {
	   System.out.println("Server listening at TCP port " + listener.getLocalPort());
	   Socket socket = listener.accept();
	   WorkerThread worker = new WorkerThread(socket);
	   worker.start();
	   System.out.println(
	    String.format("Established connection to client %s:%d.",
	     socket.getInetAddress().getHostAddress(), socket.getPort()));
	  }
	 }
	 

	 
	 public static void serve(Socket socket, DataInputStream in, DataOutputStream out, byte[] buffer) {
		  try {
		   String key = null;
		   String username = readStr(in, buffer);
		   for (String roomName: Rooms.keySet()) { //reload tabs on client if they're already members of this room
			  if (Rooms.get(roomName).containsKey(username)) {
				  sendSuccess(roomName, out);
			  }
				   
			   }
		   
		   while (true) {
		    char command = in.readChar();
		    System.out.println(command);
		    switch (command) {
		    
		     case '+':
		      key = readStr(in, buffer);
		      try {
		      String roomName = createRoom(key, username, null, out);
			  sendSuccess(roomName, out);
			  Relay("SERVER", key + " created.", key);
			    Relay("SERVER", username + " has entered " + key + ".", key);
		      } catch (Exception e) {
		    	  if (e.getCause().toString().equals("DUPLICATE FOUND")) sendError(out, "SERVER", "DUPLICATE FOUND", e.getMessage());
		    	  else sendError(out, "SERVER", e.getCause().toString().split(":")[1], e.getMessage());
		      }
		      break;
		      
		     case '-':
		    	 key = readStr(in, buffer);
		       leaveRoom(username, key, out);
		      break;
		      
		     case '*':
			      key = readStr(in, buffer);
			      String password = readStr(in, buffer);
			      try {
			      String roomName = createRoom(key, username, password, out);
			      sendSuccess(roomName, out);
			      Relay("SERVER", key + " created.", key);
				  Relay("SERVER", username + " has entered " + key + ".", key);
			      } catch (Exception e) {
			    	  if (e.getCause().toString().equals("DUPLICATE FOUND")) sendError(out, "SERVER", "DUPLICATE FOUND", e.getMessage());
			    	  else sendError(out, "SERVER", e.getCause().toString().split(":")[1], e.getMessage());
			      }
			      
			      break;
		      
		     case '^':
			      key = readStr(in, buffer);
			      try {
			      String roomName = enterRoom(username, key, null, out);
			      sendSuccess(roomName, out);
			      Relay("SERVER", username + " has entered " + key + ".", key);
			      } catch (Exception e) {
			    	  if (e.getCause().toString().equals("NOT FOUND")) sendError(out, "SERVER", "NOT FOUND", '"' + key + '"' + " doesn't exist!");
			    	  else sendError(out, "SERVER", e.getCause().toString().split(":")[1], e.getMessage());
			      }
			      break; 
			      
		     case '&':
			      key = readStr(in, buffer);
			      password = readStr(in,buffer);
			      try {
			      String roomName = enterRoom(username, key, password, out);
			      sendSuccess(roomName, out);
			      Relay("SERVER", username + " has entered " + key + ".", key);
			      } catch (Exception e) {
			    	  if (e.getCause().toString().equals("notfound")) {
			    		  sendError(out, "SERVER", "NOT FOUND", "'" + key + "' " + "doesn't exist!");
			    	  } else sendError(out, "SERVER", e.getCause().toString().split(":")[1], e.getMessage());
			      }
			      break; 
		      
		      
		     case '>':
		    	 	   String destination = readStr(in, buffer);
				       String msg = readStr(in, buffer);
				       Relay(username, msg, destination);

		    	 break;
		      
		     case '%':
		    	 	destination = readStr(in, buffer);
		        	String filename = recFile(in, buffer);
		        	relayFile(filename, '%', username, buffer, destination);
		        	//sendFile(in, out, '%', username, buffer);
		         break;
		     
		     case '#':
		    	 	destination = readStr(in, buffer);
		    	 	filename = recFile(in, buffer);
		        	relayFile(filename, '#', username, buffer, destination);
		        	//sendFile(in, out, '%', username, buffer);
		         break;
		         
		     case '$':
		    	 	destination = readStr(in, buffer);
		    	 	filename = recFile(in, buffer);
		        	relayFile(filename, '$', username, buffer, destination);
		        	//sendFile(in, out, '%', username, buffer);
			         break;
			         
		     case '?':
		    	 ArrayList < Chatroom > publicRooms = new ArrayList < > ();
		    	 for (String roomKey : Rooms.keySet()) {
		    		 Chatroom chatroom = Rooms.get(roomKey);
		    		 if (!chatroom.isPrivate()) {
		    			 publicRooms.add(chatroom);
		    		 }
		    	 }
		    	  //write number of public rooms
			    	 int size = publicRooms.size();
			    	 out.writeChar('?');
			    	 out.writeInt("SERVER".length());
			    	 out.write("SERVER".getBytes());
			    	 out.writeInt(size);
		    	 for(Chatroom chatroom: publicRooms) { 
		    		 out.writeInt(chatroom.getName().length()); //write length of room name
		    		 System.out.println(chatroom.getName());
		    		 out.write(chatroom.getName().getBytes()); //write room name
		    		 out.writeInt(chatroom.getNumberOfMembers()); //write room member number
		    	 
		    	 
		    	 }
		    	 
			         break;
		         
		     default:
		      break;
		    }
		   }
		  } catch (IOException ex) {
		   System.out.println(String.format("Disconnected client %s:%d",
		    socket.getInetAddress().getHostAddress(),
		    socket.getPort()
		   ));
		  }
		 }
	 
	 	static void sendError(DataOutputStream out, String sender, String type, String msg) throws IOException {
	 	  out.writeChar('x');
	 	 out.writeInt(sender.length()); //Write sender length
		  out.write(sender.getBytes());
		  out.writeInt("E".length()); //Write destination length
		  out.write("E".getBytes());//Write destination
		  out.writeInt(type.length()); //Write error type length
		  out.write(type.getBytes()); //Write error type
		  out.writeInt(msg.length()); //Write error message length
		  out.write(msg.getBytes()); //Write error message
		 }

		 static void sendStr(DataOutputStream out, String username, String key, String msg) throws IOException {
		  out.writeChar('>');
		  out.writeInt(username.length());
		  out.write(username.getBytes());
		  out.writeInt(key.length());
		  out.write(key.getBytes());
		  out.writeInt(msg.length());
		  out.write(msg.getBytes());
		 }
		 
		 static String readStr(DataInputStream in, byte[] buffer) throws IOException {
			 int len = in.readInt();
			 in.read(buffer, 0, len);
			 return new String(buffer, 0, len);
		 }
		 
		 static void sendSuccess(String key, DataOutputStream out) throws IOException { 
			 	out.writeChar('+'); //let client know it was successful
			    out.writeInt("SERVER".length());
			    out.write("SERVER".getBytes());
			    out.writeInt("S".length()); 
			    out.write("S".getBytes()); 
			    out.writeInt(key.length()); //write room name's length
			    out.write(key.getBytes()); //write room name
		 }

		 static String createRoom(String key, String username, String password, DataOutputStream out) throws Exception {
			 
		  synchronized(Rooms) {
		   if (!Rooms.containsKey(key)) {
		    Chatroom chatroom = (password == null) ? new Chatroom(key) : new Chatroom(key, password);
		    chatroom.add(username, out);
		    Rooms.put(key, chatroom);
		    
		    return key;

		   } else throw new Exception('"' + key + '"' + " already exists! Please use another name.", new Throwable("DUPLICATE FOUND"));
		  }
		 }

		 static String enterRoom(String username, String key, String password, DataOutputStream out) throws Exception {
			 synchronized(Rooms) {
		  if (Rooms.containsKey(key)) {
			  Chatroom chatroom = Rooms.get(key);
			  System.out.print(chatroom.isPrivate());
			  System.out.print(password);
			  if (chatroom.isPrivate() && (password == null)) throw new Exception("This room is private!", new Throwable("PRIVATE"));
			  chatroom.joinRoom(username, password, out); 
			  return chatroom.getName();
		  } else throw new Exception("Room not found", new Throwable("NOT FOUND"));
		 }
		 }

		 static void leaveRoom(String username, String key, DataOutputStream out) throws IOException {
		  if (Rooms.containsKey(key)) {
			  Chatroom chatroom = Rooms.get(key);
		   synchronized(chatroom) {
		    chatroom.remove(username);
		    Relay("SERVER", username + " has left " + key + ".", key);
		   }
		  }
		 }

		 static void Relay(String sender, String msg, String key) {
		  ArrayList < String > trash = new ArrayList < > ();
		  Chatroom chatroom = Rooms.get(key);
		  
		  for (String member: chatroom.getMembers()) {
			  System.out.println("Room : " + chatroom.getName() + "Member: " + member);
		   DataOutputStream memberOut = chatroom.get(member);
		   try {
		    sendStr(memberOut, sender, key, msg);
		    
		   } catch (IOException e) {
		    trash.add(member);
		   }
		  }
		  for (String member: trash) {
			   chatroom.remove(member);
		 }
		 }
		 
		 static boolean sendFile(String filename, String destination, DataOutputStream out, char command, String username, byte[] buffer) throws IOException {
		     boolean result = true;
		     FileInputStream fin = null; //create file input stream to bring in file on local system
		     //try {
		         File file = new File(filename); 
		         if (!file.exists() || file.isDirectory()) //check if file exists
		             throw new IOException("Invalid file.");
		         fin = new FileInputStream(file); //connect file input stream with file
		         int len;
		         long size = file.length(); //get file size
		         //sendStr(out, "SERVER", "Sending " + filename);
		         out.writeChar(command); //write command
		         out.writeInt(username.length()); //write username length
		         out.write(username.getBytes()); //write username
		         out.writeInt(destination.length()); //write destination length
		         out.write(destination.getBytes()); //write destination
		         out.writeInt(file.getName().length()); // not include the path, write FILENAME length
		         out.write(file.getName().getBytes()); //write FILENAME
		         out.writeLong(size); //write FILE size
		         while(size > 0) {
		             len = fin.read(buffer, 0, (int) Math.min(size, buffer.length));
		             out.write(buffer, 0, len); //write file
		             size -= len;
		         }
		     fin.close();
		     return result;
		 }
		 
		 static String recFile(DataInputStream in, byte[] buffer) {
		        String filename;
		        FileOutputStream fout = null; //create file output stream for new file to be downloaded
		        try {
		            int len = in.readInt(); //read FILENAME length
		            in.read(buffer, 0, len); //read FILENAME to buffer
		            filename = new String(buffer, 0, len); //create  FILENAME String from buffer
		            File file = new File(filename); //create new local file with filename
		            fout = new FileOutputStream(file); //connect output stream to this new file
		            long size = in.readLong(); //read FILE size
		            System.out.println(
		                    String.format("Receiving file %s (%d bytes)", filename, size));
		            while (size > 0) {
		                len = in.read(buffer, 0, (int) Math.min(size, buffer.length)); //read to buffer
		                fout.write(buffer, 0, len); //write buffer contents to new file
		                size -= len; //decrement size to keep reading
		            }
		            System.out.println("Completed");
		        } catch (IOException ex) {
		            ex.printStackTrace();
		            return null;
		        } finally {
		            try {
		                fout.close();
		            } catch (IOException ex) {
		            }
		        }
		        return filename;
		    }
		 
		 static void relayFile(String filename, char command, String username, byte[] buffer, String key) {
			  ArrayList < String > trash = new ArrayList < > ();
			  System.out.println("This is the room: " + Rooms.get(key));
			  Chatroom chatroom = Rooms.get(key);
			  
			  for (String member: chatroom.getMembers()) {
			   DataOutputStream memberOut = chatroom.get(member);
			   try {
			    sendFile(filename, key, memberOut, command, username, buffer);
			    
			   } catch (IOException e) {
			    trash.add(member);
			   }
			  }
			  for (String member: trash) {
				  chatroom.remove(member);
			  }
			 }
		 

		    
}
		

