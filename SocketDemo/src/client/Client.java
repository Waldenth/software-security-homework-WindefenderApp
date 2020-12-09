package client;

import java.io.*;
import java.net.Socket;

public class Client {
    private static String serverIP="127.0.0.1";
    public static int num=0;
    public static void main(String[]args){
        while (true){
            FileInputStream sendPhoto=null;
            Socket PhotoSender=null;
            DataInputStream inReader=null;
            OutputStream outWriter=null;
            try{
                PhotoSender=new Socket(serverIP,8000);
                outWriter=new DataOutputStream(PhotoSender.getOutputStream());
                inReader=new DataInputStream(PhotoSender.getInputStream());
                if(inReader.readUTF().equals("stop")){
                    break;
                }else{
                    File targetFile=new File("resource/data "+String.valueOf(num)+".png");
                    sendPhoto=new FileInputStream(targetFile);
                    byte []data=new byte[(int)targetFile.length()];
                    sendPhoto.read(data);
                    outWriter.write(data);
                }
                PhotoSender.shutdownOutput();
            }catch (Exception e){
                e.printStackTrace();
            }
            try{
                outWriter.close();
                inReader.close();
                PhotoSender.close();
                sendPhoto.close();
            }catch (Exception e){
                e.printStackTrace();
            }
            num++;
        }
    }
}
