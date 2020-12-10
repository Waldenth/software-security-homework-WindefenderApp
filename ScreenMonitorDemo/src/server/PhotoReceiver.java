package server;

import javax.swing.*;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.Socket;

public class PhotoReceiver {
    private static String clientIP="";
    public static int port=8000;
    public static int curIndex=0;
    private static int MaxCaptureIndex=200;
    public static void receive(){
        while(true){
            FileOutputStream savePhoto=null;
            Socket PhotoReceiver=null;
            InputStream inReader=null;
            try{
                PhotoReceiver=new Socket(clientIP,port);
                inReader=PhotoReceiver.getInputStream();
                savePhoto=new FileOutputStream("receive/"+"getData"+String.valueOf(curIndex)+".jpg");
                byte[]buffer=inReader.readAllBytes();
                savePhoto.write(buffer);
            }catch (Exception e) {
                JOptionPane.showMessageDialog(
                        null,"The Client has stopped","Error",JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
                System.exit(-1);
            }
            try {
                inReader.close();
                PhotoReceiver.close();
                savePhoto.close();
            }catch (Exception e){
                e.printStackTrace();
            }
            curIndex++;
            if(curIndex>MaxCaptureIndex){
                curIndex=0;
                System.out.println("receive finish!");
                Player.canPlay=true;
                break;
            }
        }
    }
    public static void main(String[]args){
        receive();
    }
    public static void setClientIP(String ip){
        clientIP=ip;
    }
}
