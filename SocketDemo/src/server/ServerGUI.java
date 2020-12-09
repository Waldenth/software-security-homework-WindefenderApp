package server;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.TimerTask;
import java.util.Timer;

public class ServerGUI {
    public static boolean isfinihed=false;
    public static void main(String[]args){
        JFrame frame=new JFrame("Monitor");
        JButton button=new JButton("start");
        JPanel panel=new JPanel();
        frame.setLayout(null);
        frame.setSize(300,300);
        frame.setLocationRelativeTo(null);
        frame.add(panel);
        panel.setBounds(0,100,300,50);
        panel.add(button);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        final Object someObject=new Object();
        Timer readyPlay=new Timer();
        readyPlay.schedule(new TimerTask() {
            @Override
            public void run() {
                if(isfinihed){
                    ServerPlayer.play();
                    readyPlay.cancel();
                }
            }
        },0,2000);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Thread getPhoto=new Thread(){
                    public void run(){
                        synchronized (someObject){
                            System.out.println("ready to connect!");
                            ServerReceive.receive();
                            isfinihed=true;
                        }
                    }
                };
                getPhoto.start();
            }
        });
    }
}
