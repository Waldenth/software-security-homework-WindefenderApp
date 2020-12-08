package server;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

import javax.swing.JFrame;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;
import com.github.sarxos.webcam.WebcamUtils;
import com.github.sarxos.webcam.util.ImageUtils;


/**
 * 服务端(宿主机)程序
 * Class: 摄像头控制类
 * */
public class GetCamera {
    /**连接端口*/
    public static int port=6668;
    /**cache dir Path*/
    public static String dirPath=null;
    /** work flag*/
    //public static volatile boolean isWorking=false;
    /** picture index*/
    public static int index=0;
    /** speed */
    private static int speed=300;
    /**
     * 测试摄像头,拍摄并存储图片
     * */

    /*
    public static void setFlag(boolean flag){
        isWorking=flag;
    }
    */
    /**
     * 摄像头截图,并在自己对应ip端口写入图片数据,待client建立连接后
     * 从该端口读走数据
     * */
    public static void CapturePhoto()throws IOException,InterruptedException{
        if(Flag.isWorking){
            index=0;
            final Webcam webcam = Webcam.getDefault();
            webcam.setViewSize(WebcamResolution.VGA.getSize());
            WebcamPanel panel = new WebcamPanel(webcam);
            panel.setFPSDisplayed(true);
            panel.setDisplayDebugInfo(true);
            panel.setImageSizeDisplayed(true);
            panel.setMirrored(true);

            final JFrame window = new JFrame("摄像头");
            window.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    webcam.close();
                    window.dispose();
                }
            });
            window.setVisible(false);

            File captureCache=new File("CaptureCache");
            if(!captureCache.exists()){
                captureCache.mkdir();
            }
            dirPath=captureCache.getAbsolutePath();

            ServerSocket serverSocket = null;
            try {
                serverSocket = new ServerSocket(6668);
                serverSocket.setSoTimeout(999999999);
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

            if(serverSocket!=null){
                Socket server = serverSocket.accept();
                while(Flag.isWorking){
                    FileInputStream fis=null;
                    try{
                        if(index<200){
                            final String fileName=dirPath+"/"+"camera_capture"+index;
                            WebcamUtils.capture(webcam,fileName,ImageUtils.FORMAT_PNG);
                            File photo=new File(dirPath+"/"+"camera_capture"+index+".png");
                            fis=new FileInputStream(photo.getAbsolutePath());
                            OutputStream out=server.getOutputStream();
                            byte[]buffer=new byte[1024];
                            int len=0;
                            while((len=fis.read(buffer))!=-1){
                                out.write(buffer,0,len);
                            }
                            out.close();
                            server.close();
                            photo.delete();
                            index++;
                            Thread.sleep(speed);
                        }else{
                            index=0;
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }finally {
                        if(fis!=null){
                            fis.close();
                        }
                    }
                }
            }
        }else{
            System.out.println("没有设置开启标志");
        }
        index=0;
    }

    public static void cameraTest() throws IOException, InterruptedException {
        int num = 0; //截图编号
        final Webcam webcam = Webcam.getDefault();
        webcam.setViewSize(WebcamResolution.VGA.getSize());
        WebcamPanel panel = new WebcamPanel(webcam);
        panel.setFPSDisplayed(true);
        panel.setDisplayDebugInfo(true);
        panel.setImageSizeDisplayed(true);
        panel.setMirrored(true);

        final JFrame window = new JFrame("摄像头");
        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                webcam.close();
                window.dispose();
            }
        });

        File file = new File("picture"); // 创建文件夹存储图片
        if (!file.exists()) {
            file.mkdir();
        }

        window.setVisible(false);

        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(6668);
            serverSocket.setSoTimeout(999999999);
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        if (serverSocket != null) {
            while (true) {
                try {
                    if(num<500){
                        System.out.println("等待远程连接，端口号为：" + serverSocket.getLocalPort() + "...");
                        Socket server = serverSocket.accept();

                        final String fileName =file.getAbsolutePath()+"/"+ "photo" + num;
                        System.out.println(num);
                        WebcamUtils.capture(webcam, fileName, ImageUtils.FORMAT_PNG);
                        num++;

                        File pathFile = new File(".");
                        String path = pathFile.getAbsolutePath();
                        int k = num-1;// 图片记录

                        File file_delete=new File(file.getAbsolutePath()+ "photo" + k + ".png");
                        FileInputStream fis = new FileInputStream(file.getAbsolutePath()+"/"+ "photo" + k + ".png");
                        OutputStream out = server.getOutputStream();
                        byte[] buf = new byte[1024];
                        int len = 0;
                        while ((len = fis.read(buf)) != -1) {
                            out.write(buf, 0, len);
                        }
                        server.shutdownOutput();

                        //????为什么要客户端要搞个接受输入?
                        InputStream in = server.getInputStream();

                        byte[] bufin = new byte[1024];
                        int cnt = in.read(bufin);
                        // System.out.println(new String(bufin,0,num));
                        fis.close();
                        out.close();
                        in.close();
                        server.close();
                        file_delete.delete();//文件删除
                        k++;

                        Thread.sleep(200);
                    }
                    else{
                        num=0;
                    }
                } catch (SocketTimeoutException s) {
                    System.out.println("Socket timed out!");
                    break;
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }
    }
}

