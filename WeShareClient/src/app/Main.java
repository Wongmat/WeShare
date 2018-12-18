package app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Optional;

public class Main extends Application {
    private Socket socket;
    private String ipaddr = "localhost";
    private int port = 12345;
    private static DataInputStream in;
    private static DataOutputStream out;
    private static byte[] buffer = new byte[1024];
    private static String username;

    @Override
    public void start(Stage primaryStage) throws IOException {
        if (serverOnline(ipaddr, port)) {
            socket = new Socket(ipaddr, port);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            Controller.nameDialog dialog = new Controller.nameDialog();
            Optional<String> response = dialog.showAndWait();
            if (response.isPresent()) username = response.get();
            Parent root = FXMLLoader.load(getClass().getResource("mainTest.fxml"));
            primaryStage.setTitle("WeShare Client");
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
            primaryStage.setOnCloseRequest((WindowEvent) -> {
                System.exit(0);
            });
            File dir = new File("./" + username + "/Received");
            if (!dir.exists()) dir.mkdirs();
        } else {

            Alert alert = new Alert(Alert.AlertType.ERROR, "WeShare servers are currently offline. Please try again later.");
            alert.showAndWait();
        }

        ReceiverThread receiverThread = new ReceiverThread(in);
        receiverThread.start();
        sendName(username);

    }

    public static String receiveMsg() throws IOException {
        int len = in.readInt();
        in.read(buffer, 0, len);
        return new String(buffer, 0, len);
    }

    public static boolean serverOnline(String host, int port) {
        Socket s = null;
        try {
            s = new Socket(host, port);
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            if (s != null)
                try {
                    s.close();
                } catch (Exception e) {
                }
        }
    }


    public static void main(String[] args) {
        launch(args);
    }

    public static void sendMsg(String destination, String msg) throws IOException {
        out.writeChar('>');
        out.writeInt(destination.length());
        out.write(destination.getBytes());
        out.writeInt(msg.length());
        out.write(msg.getBytes());
    }

    public static HashMap <String, Double> getReceivedDir() {
        File dir = new File("./" + username + "/Received/" + Controller.getInstance().getActiveTab());
        HashMap <String, Double> output = new HashMap<>();
        for (File file: dir.listFiles()) {
            output.put(file.getName(), file.length() * 0.001);
        }
        return output;
    }

    public static String getUsername() {
        return username;
    }

    public static void InfoReq() throws IOException {
        out.writeChar('?'); //didn't have time to finish this function
    }

    public static void sendName(String msg) throws IOException {
        out.writeInt(msg.length());
        out.write(msg.getBytes());
    }

    public static void CreateOrJoin(String destination, String password, char command) throws IOException {
        out.writeChar(command);
        out.writeInt(destination.length());
        out.write(destination.getBytes());
        if (password != null) {
            out.writeInt(password.length());
            out.write(password.getBytes());
        }
    }

    public static void LeaveRoom(String roomName) throws IOException {
        out.writeChar('-');
        out.writeInt(roomName.length());
        out.write(roomName.getBytes());
    }

    static String recFile(String destination, String username, DataInputStream in) {
        String filename;
        FileOutputStream fout = null; //create file output stream for future new file on local system
        try {

            int len = in.readInt(); //read FILENAME length
            in.read(buffer, 0, len); //read FILENAME
            filename = new String(buffer, 0, len); //create FILENAME String
            File file = new File("./" + username + "/Received/" + destination, filename); //create new file with FILENAME at this room's dir
            fout = new FileOutputStream(file); //connect new file with output stream
            long size = in.readLong(); //read FILE length
            System.out.println(
                    String.format("Receiving file %s (%d bytes)", filename, size));
            while (size > 0) {
                len = in.read(buffer, 0, (int) Math.min(size, buffer.length)); //read FILE
                fout.write(buffer, 0, len); //write FILE to newly created file
                size -= len;
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

    static boolean sendFile(String destination, char command, File file) {
        // if (acceptance(command) == true) {
        boolean result = true;
        FileInputStream fin = null; //creates file input stream for connecting to file on local system
        try {
            fin = new FileInputStream(file); //link file input stream with local file
            int len;
            long size = file.length(); //get file size
            out.writeChar(command); //write command
            out.writeInt(destination.length()); //write destination length
            out.write(destination.getBytes()); //write destination
            out.writeInt(file.getName().length()); // not include the path, write FILE NAME length
            out.write(file.getName().getBytes()); // write FILE NAME
            out.writeLong(size); // write FILE size
            while (size > 0) {
                len = fin.read(buffer, 0, (int) Math.min(size, buffer.length));
                out.write(buffer, 0, len); //write FILE
                size -= len;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            result = false;
        } finally {
            try {
                if (fin != null)
                    fin.close();
            } catch (IOException ex) {
            }
        }
        return result;
        // } else return false;
    }

    class ReceiverThread extends Thread {
        DataInputStream in;

        public ReceiverThread(DataInputStream in) {
            this.in = in;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    char command = in.readChar();
                    System.out.println("Command: " + command);
                    String sender = receiveMsg();
                    String destination = receiveMsg();
                    if(!sender.equals("SERVER"))  {

                        Controller.getInstance().showUserName(destination,sender + ":\n");
                    }
                    System.out.println("User: " + sender + ", Room: " + destination);



                    switch (command) {
                        case '>':
                            String msg = Main.receiveMsg();
                            Controller.getInstance().showMessages(destination, msg);
                            break;
                        case '%':
                            String imgName = Main.recFile(destination, username, in);
                            if (imgName != null) {
                                System.out.println("worked");
                                Controller.getInstance().showImage(destination, imgName);
                            } else Controller.getInstance().showMessages("Hello","Download not successful!");
                            break;
                        case '#':
                            String audioName = Main.recFile(destination, username, in);
                            if (audioName != null) {
                                Controller.getInstance().showAudio(destination, audioName);
                            } else Controller.getInstance().showMessages("Hello","Download not successful!");
                            break;
                        case '$':
                            String fileName = Main.recFile(destination, username, in);
                            if (fileName != null) {
                                Controller.getInstance().showFile(destination, fileName);
                            } else Controller.getInstance().showMessages("Hello","Download not successful!");
                            break;

                        case 'x':
                            String type = Main.receiveMsg(); //get error type
                            msg = Main.receiveMsg(); //get error message
                            Controller.getInstance().ErrorPopup(type, msg);
                            break;
                        case '+':
                            String roomName = Main.receiveMsg();
                            File dir = new File("./" + username + "/Received/" + roomName);
                            if (!dir.exists()) dir.mkdir();
                            Controller.getInstance().addTab(roomName);
                            break;

                        case '?':
                            int size = in.readInt();
                            System.out.print("Size: " + size);
                            HashMap<String, Integer> map = new HashMap<>();
                            for (int i = 1; i <= size; i++) {
                                String room = receiveMsg();
                                int online = in.readInt();
                                map.put(room, online);
                            }
                            for (String key: map.keySet()) {
                                System.out.println("Key: " + key + map.get(key));
                            }

                            break;

                    }
                }


            } catch (IOException e) {
                e.printStackTrace();

            }
        }
    }
}

