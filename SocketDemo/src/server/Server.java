package server;

import java.io.*;
import java.lang.ref.PhantomReference;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static int maxIndex=80;
    public static int port=8000;
    public static int curIndex=0;

    public static void main(String[]args){
        ServerSocket ss=null;
        try{
            ss=new ServerSocket(port);
        }catch (Exception e){
            e.printStackTrace();
        }

        while(true){
            FileOutputStream savePhoto=null;
            Socket PhotoReceiver=null;
            InputStream inReader=null;
            DataOutputStream outWriter=null;
            try{
                PhotoReceiver=ss.accept();
                inReader=PhotoReceiver.getInputStream();
                outWriter=new DataOutputStream(PhotoReceiver.getOutputStream());
                if(curIndex<maxIndex)
                    outWriter.writeUTF("continue");
                else{
                    outWriter.writeUTF("stop");
                    break;
                }

                savePhoto=new FileOutputStream("receive/"+"getData"+String.valueOf(curIndex)+".png");
                byte[]buffer=inReader.readAllBytes();
                savePhoto.write(buffer);
            }catch (Exception e){
                e.printStackTrace();
            }
            try {
                outWriter.close();
                inReader.close();
                PhotoReceiver.close();
                savePhoto.close();
            }catch (Exception e){
                e.printStackTrace();
            }
            curIndex++;
        }
    }

}
