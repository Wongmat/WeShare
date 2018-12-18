import java.io.DataOutputStream;
import java.util.HashMap;
import java.util.Set;

public class Chatroom extends HashMap<String, DataOutputStream> {
		 private String password;
		 private String name;
		 public Chatroom (String name, String password) {
			 this.password = password;
			 this.name = name;
		 }
		 
		 public Chatroom (String name) {
			 this.password = null;
			 this.name = name;
		 }

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
		 
		 public Set<String> getMembers () {
			 return this.keySet();
		 }
		 
		 public int getNumberOfMembers () {
			 return this.size();
		 }

		public void add(String username, DataOutputStream out) {
			this.put(username, out);
			
		}
		
		public boolean isPrivate() {
			return (this.getPassword() != null) ? true : false;
			
		}
		
		public void joinRoom (String username, String password, DataOutputStream out) throws Exception {
			if (this.containsKey(username)) throw new Exception("You're already a member of this room!", new Throwable("ALREADY A MEMBER"));
			System.out.println(this.getPassword());
			System.out.println("Entered:" + password);
			if (this.getPassword().equals(password)) {
				System.out.println("Password: " + this.password);
				System.out.println("Entered Password: " + password);
			synchronized(this) {
			    this.add(username, out);
			   }
			
			 }
			
			else throw new Exception("Incorrect Password", new Throwable("AUTHENTICATION ERROR"));
		}
		}
		 
		 
	 
