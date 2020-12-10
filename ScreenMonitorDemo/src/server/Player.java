package server;

import javax.swing.*;
import java.awt.*;


public class Player {
    public static volatile int index=0;
    public static Image image=null;
    public static MyDrawPanel photo=new MyDrawPanel("receive/getData");
    public static volatile boolean canPlay=false;
    public static volatile int receiveThreadNum=0;
    public static void play(){
        JFrame frame=new JFrame("Neptune");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1500,1000);
        frame.setLocationRelativeTo(null);
        frame.add(photo);
        frame.setVisible(true);
        while(true){
            try {
                Thread.sleep(200);
            }catch (Exception e){
                e.printStackTrace();
            }
            frame.repaint();
            image.flush();
            if(index==150){
                Thread receiveThread=new Thread(){
                    public void run(){
                        try {
                            receiveThreadNum++;
                            if(receiveThreadNum==1) {
                                System.out.println("start new receive");
                                PhotoReceiver.receive();
                            }else
                                System.out.println("give up");
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        receiveThreadNum--;
                    }
                };
                receiveThread.start();
            }
            if(index>200)
                index=0;
        }
    }
    static class MyDrawPanel extends JPanel {
        private String filepath;
        public MyDrawPanel(String filepath){
            this.filepath=filepath;
        }
        public void paintComponent(Graphics g){
            image=new ImageIcon(filepath+String.valueOf(index)+".jpg").getImage();
            g.drawImage(image,0,0,this);
            index++;
        }
    }
    public static void  main(String[]args){
        play();
    }
}
