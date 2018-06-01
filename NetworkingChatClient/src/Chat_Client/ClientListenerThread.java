package Chat_Client;

import java.io.BufferedReader;
import java.io.IOException;
import javax.swing.JTextArea;

public class ClientListenerThread extends Thread {

    private BufferedReader reader;
    private JTextArea jChatTextArea;
    
    private final String privateMsg = "<set_msg>";
    private final String listUsers = "<get_users>";
    private final String messageAll = "<set_msg_all>";
    private final String system   = "<system>";
    private final String error = "<ERROR>";

    public ClientListenerThread(BufferedReader reader, JTextArea chatTextArea) {
        this.reader = reader;
        this.jChatTextArea = chatTextArea;
    }

    @Override
    public void run() {
        String msg;

        try {
            //stuck until go message
            while ((msg = this.reader.readLine()) != null) {
                if (msg.startsWith(this.messageAll)) {
                    this.jChatTextArea.append(this.system + msg.substring(this.messageAll.length(), msg.length()) + "\n");
                }else if (msg.startsWith(this.listUsers)){
                    this.jChatTextArea.append(msg.substring(this.listUsers.length(), msg.length()) + "\n");
                }else if(msg.startsWith(this.error)){
                    this.jChatTextArea.append(msg.substring(this.error.length(), msg.length()) + "\n");
                }else if(msg.startsWith(system)){         
                    this.jChatTextArea.append(msg + "\n");
                }else if (msg.startsWith(this.privateMsg)){
                    this.jChatTextArea.append(msg.substring(this.privateMsg.length(), msg.length()) + "\n");
                }
                this.jChatTextArea.setCaretPosition(this.jChatTextArea.getDocument().getLength()); 
            }
        } catch (IOException ex) {
            this.jChatTextArea.append(ex.getMessage() + "\n");
        }
    }

}
