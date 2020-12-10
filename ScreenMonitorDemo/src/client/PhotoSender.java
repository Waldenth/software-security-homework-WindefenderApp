package client;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class PhotoSender {
    public static int curIndex=0;
    public static int port=8000;
    public static ServerSocket ss=null;
    public static void startListenPort(){
        try{
            ss = new ServerSocket(port);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public static void send(){
        while(true){
            if(!Flag.isWorking){
                System.out.println("stop send");
                break;
            }
            Socket sender=null;
            OutputStream outWriter=null;
            FileInputStream fileReader=null;
            try{
                sender=ss.accept();
                outWriter=new DataOutputStream(sender.getOutputStream());
                File targetFile=new File("captureCache/screenShot"+String.valueOf(curIndex)+".jpg");
                byte[]data=new byte[(int)targetFile.length()];
                fileReader=new FileInputStream(targetFile);
                fileReader.read(data);
                outWriter.write(data);
                sender.shutdownOutput();
            }catch (Exception e){
                e.printStackTrace();
            }
            try{
                fileReader.close();
                outWriter.close();
                sender.close();
            }catch (Exception e){
                e.printStackTrace();
            }
            curIndex++;
            if(curIndex>=Flag.MaxCaptureIndex) {
                curIndex = 0;
                break;
            }
        }
    }
}
