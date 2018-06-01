package Chat_Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class ClientGui extends javax.swing.JFrame {

    String userName ; 
    String serverIP;
    int serverPort ;
    Socket mySocket;
    BufferedReader reader;
    PrintWriter writeToServer;
    ArrayList<String> userList = new ArrayList();
    Boolean connected = false;
    
    private static final String connect = "<connect>";
    private static final String disconnect = "<disconnect>";
    private static final String privateMsg = "<set_msg>";
    private static final String listUsers = "<get_users>";
    private final static String messageAll = "<set_msg_all>";
    
    /**
     * Ctor , init parameter and event close window
     */
    public ClientGui() {
        initComponents();
        
        //if i close the main window i need to disconect becouse it cant disconect automaticaly
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                disconnect();
            }
        });
    }
    
    
    /**
     * Called after send button pushed
     * If we connected check if its private or regular message
     * and send to the server.
     */
    private void send() {
        if (!this.connected) {
            this.jChatTextArea.append("Connect First!\n");
            return ;
        }
        
        String msg = "";
        //if text field empty return 
        if ((this.jUserWriteTextArea.getText()).equals("")) {
            this.jUserWriteTextArea.setText("");
            return;
        }
        
        try {
            //client want to send private message
            if (this.jPrivateMsgCheckBox.isSelected()){
                //<set_msg><userName><target>:message
                msg = this.privateMsg + "<" + this.userName +"><"+ this.jTargateField.getText()+"><:"+ this.jUserWriteTextArea.getText()+">";
            } else {
                //<set_msg_all><userName>:message , send message to all clients
                msg = this.messageAll + "<" + this.userName + ">:" + this.jUserWriteTextArea.getText();
            }
            //send message to server
            this.writeToServer.println(msg);
            this.writeToServer.flush(); 
        } catch (Exception ex) {
            this.jChatTextArea.append(ex.getMessage());
        }  
    } 
    

    /**
     * Close the socket.
     * set new connection available.
     */
    public void disconnect() {
        
        try {         
            //<disconnect>userName
            String disconnectMessage = (this.disconnect  +"<" + this.userName + ">" );
            this.writeToServer.println(disconnectMessage); 
            this.writeToServer.flush(); 
            Thread.sleep(500);   
            this.mySocket.close(); 
            this.connected = false;
            this.jUserNameTextArea.setEditable(true);
            this.jPortTextArea.setEditable(true);
            this.jConnectButton.setText("Connect");

        } catch (IOException | InterruptedException e) {
            this.jChatTextArea.append(e.getMessage());
        }
    }

    /**
     * Read input fields 
     * open connection to the server
     */
    private void connect() {
        
        try {
            if (!this.connected) {

                this.userName = this.jUserNameTextArea.getText();
                this.serverIP = this.jIPTextArea.getText();
                this.serverPort = Integer.parseInt(this.jPortTextArea.getText() );
                this.jUserNameTextArea.setEditable(false);
                this.jIPTextArea.setEditable(false);
                this.jPortTextArea.setEditable(false);

                try {
                    //we wait here until server connects us .
                    this.mySocket = new Socket(this.serverIP, this.serverPort);
                }catch(Exception e){
                    this.jChatTextArea.append("ERROR Cant connected to the server maybe server closed , or parameters wrong \n");
                    this.jUserNameTextArea.setEditable(true);
                    this.jIPTextArea.setEditable(true);
                    this.jPortTextArea.setEditable(true);
                    return;
                }
                //read from server
                this.reader = new BufferedReader(new InputStreamReader(this.mySocket.getInputStream()));
                //write to the server
                this.writeToServer = new PrintWriter(this.mySocket.getOutputStream());

                //write to server <connect><oleg>
                this.writeToServer.println(this.connect + "<" + this.userName  +">");
                //flush buffer
                this.writeToServer.flush();

                this.connected = true; 


                //thread starts to wait for messages
                Thread listener = new ClientListenerThread(this.reader, this.jChatTextArea );
                listener.start();

                //change button name to disconnect
                this.jConnectButton.setText("Disconnect");
            } 
        }catch(Exception ex){
            this.jChatTextArea.append("Invalid parameters\n");
        }
    }
   

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jUserNameTextArea = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jIPTextArea = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jPortTextArea = new javax.swing.JTextField();
        jUserWriteTextArea = new javax.swing.JTextField();
        jsendButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jChatTextArea = new javax.swing.JTextArea();
        jConnectButton = new javax.swing.JButton();
        jCleanButton = new javax.swing.JButton();
        jWhoIsOnlientButton = new javax.swing.JButton();
        jTargateField = new javax.swing.JTextField();
        jPrivateMsgCheckBox = new javax.swing.JCheckBox();
        jLabel4 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("Nick Name");

        jUserNameTextArea.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jUserNameTextAreaActionPerformed(evt);
            }
        });

        jLabel2.setText("Server IP");

        jIPTextArea.setEnabled(true);
        jIPTextArea.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jIPTextAreaActionPerformed(evt);
            }
        });

        jLabel3.setText("Server Port");

        jPortTextArea.setEnabled(true);

        jsendButton.setText("Send");
        jsendButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jsendButtonActionPerformed(evt);
            }
        });

        jChatTextArea.setColumns(20);
        jChatTextArea.setRows(5);
        jScrollPane1.setViewportView(jChatTextArea);

        jConnectButton.setText("Connect");
        jConnectButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jConnectButtonActionPerformed(evt);
            }
        });

        jCleanButton.setText("Clear");
        jCleanButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCleanButtonActionPerformed(evt);
            }
        });

        jWhoIsOnlientButton.setText("Who's Online");
        jWhoIsOnlientButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jWhoIsOnlientButtonActionPerformed(evt);
            }
        });

        jTargateField.setEnabled(false);
        jTargateField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTargateFieldActionPerformed(evt);
            }
        });

        jPrivateMsgCheckBox.setText("Private MSG");
        jPrivateMsgCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jPrivateMsgCheckBoxActionPerformed(evt);
            }
        });

        jLabel4.setText("User Name");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jConnectButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jUserNameTextArea, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jIPTextArea, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPortTextArea, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(25, 25, 25))
                    .addComponent(jUserWriteTextArea)
                    .addComponent(jCleanButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jWhoIsOnlientButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPrivateMsgCheckBox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jsendButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(15, 15, 15))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jTargateField)
                        .addGap(26, 26, 26))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jConnectButton)
                    .addComponent(jUserNameTextArea, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(jIPTextArea, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(jPortTextArea, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jCleanButton, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 227, Short.MAX_VALUE)
                        .addComponent(jWhoIsOnlientButton, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPrivateMsgCheckBox)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTargateField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(113, 113, 113))
                    .addComponent(jScrollPane1))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jsendButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jUserWriteTextArea, javax.swing.GroupLayout.DEFAULT_SIZE, 35, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * clean the screen.
     * @param evt 
     */
    private void jCleanButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCleanButtonActionPerformed
        this.jChatTextArea.setText("");
    }//GEN-LAST:event_jCleanButtonActionPerformed

    /**
     * 
     * 
     * @param evt 
     */
    private void jPrivateMsgCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jPrivateMsgCheckBoxActionPerformed
        if (this.jPrivateMsgCheckBox.isSelected()) {
            this.jTargateField.setEnabled(true);
        } else {
            this.jTargateField.setEnabled(false);
        }
    }//GEN-LAST:event_jPrivateMsgCheckBoxActionPerformed

    private void jsendButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jsendButtonActionPerformed
        send();
    }//GEN-LAST:event_jsendButtonActionPerformed

    private void jConnectButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jConnectButtonActionPerformed
        if (!this.connected) {
            this.connect();
        } else {
            disconnect();
        }
    }//GEN-LAST:event_jConnectButtonActionPerformed

    private void jWhoIsOnlientButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jWhoIsOnlientButtonActionPerformed
        if (this.connected) {
            //<get_users><userName>
            this.writeToServer.println(this.listUsers + "<" + this.userName + ">");
            this.writeToServer.flush(); // flushes the buffer            
        } else {
            this.jChatTextArea.append("Please connect!\n");
        }
    }//GEN-LAST:event_jWhoIsOnlientButtonActionPerformed

    private void jIPTextAreaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jIPTextAreaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jIPTextAreaActionPerformed

    private void jTargateFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTargateFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTargateFieldActionPerformed

    private void jUserNameTextAreaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jUserNameTextAreaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jUserNameTextAreaActionPerformed

    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ClientGui.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ClientGui().setVisible(true);
            }
        });
    }
    
   


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea jChatTextArea;
    private javax.swing.JButton jCleanButton;
    private javax.swing.JButton jConnectButton;
    private javax.swing.JTextField jIPTextArea;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JTextField jPortTextArea;
    private javax.swing.JCheckBox jPrivateMsgCheckBox;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jTargateField;
    private javax.swing.JTextField jUserNameTextArea;
    private javax.swing.JTextField jUserWriteTextArea;
    private javax.swing.JButton jWhoIsOnlientButton;
    private javax.swing.JButton jsendButton;
    // End of variables declaration//GEN-END:variables
}
