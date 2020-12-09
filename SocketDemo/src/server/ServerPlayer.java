package server;

import javax.swing.*;
import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

public class ServerPlayer {
    public static int index=0;
    public static MyDrawPanel photo=new MyDrawPanel("receive/getData");
    public static void play(){
        JFrame frame=new JFrame("Neptune");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(303,470);
        frame.setLocationRelativeTo(null);
        frame.add(photo);
        frame.setVisible(true);
        Timer player=new Timer();
        player.schedule(new TimerTask() {
            @Override
            public void run() {
                frame.repaint();
                if(index>=406)
                    index=0;
            }
        },0,100);
    }
    static class MyDrawPanel extends JPanel {
        private String filepath;
        public MyDrawPanel(String filepath){
            this.filepath=filepath;
        }
        public void paintComponent(Graphics g){
            Image image=new ImageIcon(filepath+String.valueOf(index)+".png").getImage();
            g.drawImage(image,0,0,this);
            index++;
        }
    }
}


