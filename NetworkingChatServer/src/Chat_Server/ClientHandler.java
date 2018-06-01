package Chat_Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import javax.swing.JTextArea;

class ClientHandler extends Thread {

    private BufferedReader readerFromClient;
    private JTextArea serverTextArea;
    private ArrayList<String> userNames;
    private ArrayList<PrintWriter> listOfClientsWriters;
    
    private final String connect = "<connect>";
    private final String disconnect = "<disconnect>";
    private final String privateMsg = "<set_msg>";
    private final String listUsers = "<get_users>";
    private final String messageAll = "<set_msg_all>";
    private final String system   = "<system>";
    private final String error = "<ERROR>";
    /**
     * Constructor
     * @param clientSocket - client server socket
     * @param serverTextArea - GUI server text field
     * @param userNames - List of all user names
     * @param listOfClientsWriters  - List of all user streams
     */
    public ClientHandler(Socket clientSocket, JTextArea serverTextArea, ArrayList<String> userNames, ArrayList<PrintWriter> listOfClientsWriters ){
        
        try {
            //list of all clients names
            this.userNames = userNames;
            //list of all writers to clients
            this.listOfClientsWriters = listOfClientsWriters;
            //Text area
            this.serverTextArea = serverTextArea;
            //reader from this specific client , get this from socket
            this.readerFromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); 
            //add new writer
            this.listOfClientsWriters.add(new PrintWriter(clientSocket.getOutputStream()));
        }
        catch (IOException ex) {
            serverTextArea.append(ex.getMessage() + "\n");
        }
    }

    @Override
    /**
     * Main Thread Function
     * Listen to client and do the needed operation.
     */
    public void run() {
        try {    
            String message;

            while ((message = this.readerFromClient.readLine()) != null) {
                
                if (message.startsWith(this.connect)) {
                    userConnect(message);
                } 
                else if (message.startsWith(this.disconnect)) {
                    userDisconnect(message);
                } 
                else if (message.startsWith(this.messageAll)) {
                    sendToAllClients(message + "\n");
                } 
                else if (message.startsWith(this.privateMsg)) {
                    privateMessage(message );
                }
                else if (message.startsWith(this.listUsers)) {
                    sendToClientHowOnline( message);
                } 
                else {
                    this.serverTextArea.append("No Conditions were met. \n");
                }
            } 
        } 
        catch (IOException ex) {
            this.serverTextArea.append(ex.getMessage() + "\n");
        } 
    } 
    
    /**
     * function get a message and return to the client list of all connected users.
     * @param message - <get_users><userName>
     */
    void sendToClientHowOnline(String message){
        String userName = message.substring(this.listUsers.length(),message.length());
        String allUsers = getUsers();
        
        int userIndex = this.userNames.indexOf(userName);
        PrintWriter p = this.listOfClientsWriters.get(userIndex);
        
        p.println(this.system + "<users list>:" + allUsers +"\n");
        p.flush();
        this.serverTextArea.append("Server send clients list to " + userName+"\n");
        this.serverTextArea.setCaretPosition(this.serverTextArea.getDocument().getLength());
    }

    /**
     * Send message to all the userNames
     * @param message - string message
     */
    public void sendToAllClients(String message) {
        
        this.serverTextArea.append("Sending to all clients " + message );
        this.serverTextArea.setCaretPosition(this.serverTextArea.getDocument().getLength());
        for (PrintWriter p : this.listOfClientsWriters) {
            try {
                p.println(message);
                p.flush();

            } catch (Exception ex) {
                this.serverTextArea.append(ex.getMessage() + "\n");
            }
        }    

    }
    
    /**
     * Sends a private message
     * Private Message - <set_msg_all><userName><targetUser><msg>
     * @param from - from hom to send
     * @param to- to hom to send
     * @param message - the message
     */
    public void privateMessage(String message) {
        
        String msg[] = message.split("><");
        String from = "<" + msg[1] + ">";
        String to = "<" +msg[2]+ ">";
        String messageToSend = msg[3].substring(0 , msg[3].length()-1);
        
        
        int targetUserIndex = this.userNames.indexOf(to);
        int UserIndex = this.userNames.indexOf(from);
        
        //if target user not exist
        if(targetUserIndex == -1){
            this.listOfClientsWriters.get(UserIndex).println(this.error + this.system +"<targer client" + to +" not exist>"+"\n");
            this.listOfClientsWriters.get(UserIndex).flush();
            return ;
        }
        
        PrintWriter pUser = this.listOfClientsWriters.get(UserIndex);
        PrintWriter pTarger = this.listOfClientsWriters.get(targetUserIndex);
    
        try {
            pTarger.println(this.privateMsg+from + messageToSend + "\n");            
            pTarger.flush();
            pUser.println(this.privateMsg +from + messageToSend);
            pUser.flush();
            this.serverTextArea.append(from +" send message to " +to + "\n");
            this.serverTextArea.setCaretPosition(this.serverTextArea.getDocument().getLength());
        } catch (Exception ex) {
            this.serverTextArea.append(ex.getMessage() +"\n");
        }
    }

    private void userConnect(String message) {
        String userName = message.substring(this.connect.length(), message.length());
        //send to all clients : <system><userName>:entered
        sendToAllClients(this.system+  userName + ":connected." + "\n" );
        //add to list of user names
        this.userNames.add(userName);
        int index = userNames.indexOf(userName);
        PrintWriter p = this.listOfClientsWriters.get(index);
        p.println(this.system+":hello " +userName + " you are connected now." + "\n");
        p.flush();
        //append to server window that new client connected
        this.serverTextArea.append("Client " + userName + " connected."+  "\n");
        this.serverTextArea.setCaretPosition(this.serverTextArea.getDocument().getLength()); 
    }

    private void userDisconnect(String message) {
        String userName = message.substring(this.disconnect.length(), message.length());
        //send to all clients : <system><userName>:disconnected
        sendToAllClients(this.system+ userName +":disconnected." + "\n");
        
        //add to list of user names
        int userIndex = this.userNames.indexOf(userName); 
        this.listOfClientsWriters.get(userIndex).println(this.system + ":bye bye "+ userName + " you are disconnected now. "+"\n");
        this.listOfClientsWriters.get(userIndex).flush();
        
        this.serverTextArea.append("Client " + userName + " disconnected."+ "\n");
        this.serverTextArea.setCaretPosition(this.serverTextArea.getDocument().getLength()); 
        
        this.userNames.remove(userName);
        this.listOfClientsWriters.remove(userIndex);
        //append to server window that client disconnect


    }   
    
    /**
     * Make userNames list
     * @return string of all the userNames connected.
     */
    private String getUsers() {
        String s = "";
        for (String usr : this.userNames) {
            s += usr + ", ";
        }
        return s;
    }
}
