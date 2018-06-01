package Chat_Server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import javax.swing.JTextArea;


public class ServerThread extends Thread {

    private ArrayList<PrintWriter> listOfClientsWriters;
    private ArrayList<String> onlineUsers;
    private JTextArea serverTextArea;
    private boolean isWorking = true;
    private final ServerSocket serverSocket;

    
    /**
     * ctor get chat window ans client socket
     * @param serverTextArea
     * @param serverSocket 
     */
    public ServerThread(JTextArea serverTextArea , ServerSocket serverSocket) {
        this.serverTextArea = serverTextArea;
        this.serverSocket = serverSocket;

    }

    /**
     * write to all clients that server is closed.
     */
    public void stopServer(){
        for(PrintWriter p : this.listOfClientsWriters){
            p.println("<set_msg_all>:Sorry, Server Closed!\n");
            p.flush();
            p.close();
        }
        this.serverTextArea = null;
    }

    @Override
    public void run() {
        //list that hold a writers to client
        this.listOfClientsWriters = new ArrayList();
        //list that hold a nick names .
        this.onlineUsers = new ArrayList();
        
        try {            
            this.serverTextArea.append("IP : " +InetAddress.getLocalHost().getHostAddress()+"\n");
            this.serverTextArea.append("HostName : " +InetAddress.getLocalHost().getHostName()+"\n");
            this.serverTextArea.append("port : "+ this.serverSocket.getLocalPort()+"\n");
            
            while (this.isWorking) {
                //wait until someone connects , client socket , מאזין
                Socket clientSocket = this.serverSocket.accept();
                //when a client connects, we'll assign it a process to handle it.
                Thread listener = new ClientHandler(clientSocket, this.serverTextArea ,this.onlineUsers, this.listOfClientsWriters);
                listener.start();
                
            }
        } 
        catch (IOException ex) {
            //serverTextArea.append("connection Error \n");
            stopServer();
            this.isWorking = false;
        } 
    }
}
