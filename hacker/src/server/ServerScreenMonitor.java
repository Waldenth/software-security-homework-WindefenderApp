package server;

import java.awt.Dimension;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

import javax.imageio.ImageIO;


public class ServerScreenMonitor {
    int screenport = 8000;
    String ipaddress = null;
    static String path = null;

    public ServerScreenMonitor(int port) throws IOException {
        screenport = port;
        File file = new File("");
        path = file.getAbsolutePath(); // 获取当前的路径
    }

    public static void screencapture() throws Exception {
        int k = 0;
        while (true) {
            File file = new File("picture"); // 创建文件夹存储图片
            if (!file.exists()) {
                file.mkdir();
            }

            Dimension screensizeDimension = Toolkit.getDefaultToolkit()
                    .getScreenSize();
            Rectangle rect = new Rectangle(0, 0, screensizeDimension.width,
                    screensizeDimension.height);
            BufferedImage bufimage = new Robot().createScreenCapture(rect); // 获得截图
            Point p = MouseInfo.getPointerInfo().getLocation();
            BufferedImage cursor = ImageIO.read(new File(path + "\\"
                    + "guangbiao.jpg")); // 将光标加入到图片中
            bufimage.createGraphics().drawImage(cursor, p.x, p.y, null);
            String path1 = path + "\\picture\\" + k + ".png";
            ImageIO.write(bufimage, "PNG", new File(path1));

            Thread.currentThread(); // 20ms截图一次
            Thread.sleep(20);
            k = k + 1;
            if (k == 500)
                k = 0;
        }
    }

    public void send() throws Exception {
        File file = new File("picture"); // 创建文件夹存储图片
        if (!file.exists()) {
            file.mkdir();
        }

        Thread.currentThread();
        Thread.sleep(300);
        int k = 0;
        String path2 = null;

        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(screenport);
            serverSocket.setSoTimeout(999999999);
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        if (serverSocket != null) {
            while (true) {
                // System.out.println(screenport);
                // System.out.println(ipaddress);
                try {
                    path2 = path + "\\picture\\" + k + ".png";
                    if ((new File(path2)).exists()) { // 检查是否存在图片，如果不存在则暂停100ms
                        Socket server = serverSocket.accept();
                        System.out.println("正在监听" + screenport + "端口");
                        path2 = path + "\\picture\\" + k + ".png";
                        System.out.println(path2);
                        FileInputStream fis = new FileInputStream(path2); // 打开文件
                        OutputStream out = server.getOutputStream();
                        byte[] buf = new byte[4096];
                        int len = 0;
                        while ((len = fis.read(buf)) != -1) {
                            out.write(buf, 0, len);
                        }

                        InputStream in = server.getInputStream();
					/*byte[] bufin = new byte[1024];
					int num = in.read(bufin);*/
                        // System.out.println(new String(bufin,0,num));
                        fis.close();
                        out.close();
                        in.close();
                        server.close();
                        serverSocket.setSoTimeout(999999999);
                        Thread.currentThread(); // 20ms发送一次
                        Thread.sleep(20);
                        /*
                         * if(k>10) { //防止在硬盘中存入太多的截图使得占用空间过大 File file=new
                         * File(path2); file.delete(); };
                         */
                        k++;
                        server.close();
                    } else {

                        Thread.currentThread();
                        Thread.sleep(100);
                    }
                }catch (SocketTimeoutException s) {
                    System.out.println("Socket timed out!");
                    break;
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }
    }

    public void setport(int changeport) { // 设置端口
        this.screenport = changeport;
    }

    public int getport() { // 获得端口
        return screenport;
    }
}
