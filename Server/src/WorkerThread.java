import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class WorkerThread extends Thread {
Socket socket;
DataInputStream in;
DataOutputStream out;
byte[] buffer;

public WorkerThread(Socket socket) throws IOException {
buffer = new byte[1024];
this.socket = socket;
this.in = new DataInputStream(socket.getInputStream());
this.out = new DataOutputStream(socket.getOutputStream());
}

@Override
public void run() {
	server.serve(socket, in, out, buffer);
}




}


