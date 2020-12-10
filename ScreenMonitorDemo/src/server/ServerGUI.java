package server;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.TimerTask;
import java.util.Timer;

public class ServerGUI {
    public static boolean hasReceived=false;
    public static void main(String[]args){
        JFrame frame=new JFrame("Screen Monior");
        frame.setSize(400,400);
        frame.setLayout(null);
        frame.setLocationRelativeTo(null);

        JPanel panel1=new JPanel();
        panel1.setBounds(0,0,400,200);
        JPanel panel2=new JPanel();
        panel2.setBounds(0,200,400,200);
        panel1.setLayout(null);
        panel2.setLayout(null);

        JButton button=new JButton("start");
        button.setBounds(150,80,100,50);
        panel2.add(button);
        frame.add(panel1);

        JTextField ipText=new JTextField();
        ipText.setBounds(100,100,200,40);
        panel1.add(ipText);


        frame.add(panel2);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String ip=ipText.getText();
                PhotoReceiver.setClientIP(ip);
                if(!(new File("receive/getData200.jpg").exists()))
                    PhotoReceiver.receive();
                hasReceived=true;
            }
        });

        Timer canPlay=new Timer();
        canPlay.schedule(new TimerTask() {
            @Override
            public void run() {
                if(hasReceived){
                    Player.play();
                    canPlay.cancel();
                }
            }
        },0,1000);

    }

}
