package HackerTools;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JTextArea;

public class CmdComponent {
    public JFrame commandJFrame=new JFrame();
    int commandport=7888;
    public CmdComponent(int commandport) {
        // TODO Auto-generated constructor stub
        this.commandport=commandport;
    }
    public void recieveresult() throws IOException {
        ServerSocket commandSocket=new ServerSocket(commandport);
        Socket socket = commandSocket.accept();
        InputStreamReader commandStreamrReader = new InputStreamReader(socket.getInputStream());
        BufferedReader commandReader = new BufferedReader(commandStreamrReader);
        StringBuilder commandStringBuilder = new StringBuilder();
        String line;
        while((line=commandReader.readLine())!=null)
        {
            commandStringBuilder .append(line+"\n");
        }
        JFrame commandJFrame=new JFrame();
        JTextArea commandjtextarea = new JTextArea();
        commandJFrame.add(commandjtextarea);
        line=commandStringBuilder+"";
        commandjtextarea.setText(line);
        commandJFrame.setSize(400, 300);
        commandJFrame.setVisible(true);
        commandSocket.close();
    }
}
