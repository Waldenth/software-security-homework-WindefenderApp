package HackerTools;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.plaf.FontUIResource;
import javax.swing.table.DefaultTableModel;

public class FileProcManager {
    public static ArrayList<String> fileDirectory_list = new ArrayList<String>();
    public static ArrayList<String> file_list = new ArrayList<String>();
    private static String[] fileDirectory;
    public static Client_fileManagerUI client_fileManagerUI = new Client_fileManagerUI(
            null);// 文件管理系统使用的成员变量

    public static ArrayList<String[]> proc_list = new ArrayList<String[]>();// 进程管理系统使用的成员变量
    public static Client_procManagerUI client_procManagerUI = new Client_procManagerUI(
            null);

    public static String route_forSocket = "root";

    @SuppressWarnings("unchecked")
    public static void client_fileManagerSocket1() throws Exception {
        String serverName = Index.ipAddress;
        int port = 6666;
        try {
            System.out.println("连接到主机：" + serverName + " ，端口号：" + port);
            Socket client = new Socket(serverName, port);
            System.out.println("远程主机地址：" + client.getRemoteSocketAddress());
            OutputStream outToServer = client.getOutputStream();
            DataOutputStream out = new DataOutputStream(outToServer);

            if (route_forSocket.equals("root")) {
                out.writeUTF("File_check");
            } else {
                out.writeUTF(route_forSocket);
            }

            ObjectInputStream ois = new ObjectInputStream(
                    new BufferedInputStream(client.getInputStream()));
            Object obj = ois.readObject();
            if (obj != null) {
                fileDirectory_list = (ArrayList<String>) obj;// 把接收到的对象转化为user
                if (route_forSocket.equals("root")) {
                    fileDirectory = new String[fileDirectory_list.size()];
                    for (int i = 0; i < fileDirectory_list.size(); i++) {
                        fileDirectory[i] = fileDirectory_list.get(i);
                    }
                }//如果是根目录，则不需要加../
                else {
                    fileDirectory = new String[fileDirectory_list.size() + 1];
                    fileDirectory[0] = "..";
                    for (int i = 1; i < fileDirectory_list.size() + 1; i++) {
                        fileDirectory[i] = fileDirectory_list.get(i-1);
                    }
                }////如果不是根目录，则加../
                try {
                    client_fileManagerUI = new Client_fileManagerUI(
                            fileDirectory);
                    client_fileManagerUI.drawFileList();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, "绘制列表发生错误");
                }

            }
            ois.close();
            client.close();

        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "请开启服务端后重试");
            System.exit(1);
        }
    }

    @SuppressWarnings("unchecked")
    public static void client_fileManagerSocket_upload(File file_upload) throws Exception {
        String serverName = Index.ipAddress;
        int port = 6669;
        try {
            System.out.println("连接到主机：" + serverName + " ，端口号：" + port);
            Socket client = new Socket(serverName, port);
            System.out.println("远程主机地址：" + client.getRemoteSocketAddress());

            //发送
            OutputStream out =
                    client.getOutputStream();
            byte[] buf = new byte[1024];
            int len = 0;
            FileInputStream fis = new FileInputStream(file_upload);
            while ((len = fis.read(buf)) != -1) {
                out.write(buf, 0, len);
            }

            //接收
            DataInputStream ois = new DataInputStream(
                    new BufferedInputStream(client.getInputStream()));
            //String data = ois.readUTF().toString();
            //System.out.println(data);

            ois.close();
            client.close();

        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "请开启服务端后重试");
            System.exit(1);
        }
    }
    /* 文件列表传输socket */

    /*---------------进程列表传输socket-------------*/
    @SuppressWarnings("unchecked")
    public static void client_procManagerSocket1() throws Exception {
        String serverName = Index.ipAddress;
        int port = 6667;
        try {
            System.out.println("连接到主机：" + serverName + " ，端口号：" + port);
            Socket client = new Socket(serverName, port);
            System.out.println("远程主机地址：" + client.getRemoteSocketAddress());
            OutputStream outToServer = client.getOutputStream();
            DataOutputStream out = new DataOutputStream(outToServer);

            out.writeUTF("refresh");

            ObjectInputStream ois = new ObjectInputStream(
                    new BufferedInputStream(client.getInputStream()));
            Object obj = ois.readObject();
            if (obj != null) {
                proc_list = (ArrayList<String[]>) obj;// 把接收到的对象转化为user
                String[][] proc = new String[proc_list.size()][];
                for (int i = 0; i < proc_list.size(); i++) {
                    proc[i] = proc_list.get(i);
                }

                try {
                    client_procManagerUI = new Client_procManagerUI(proc);
                    client_procManagerUI.drawProcTable();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, "绘制列表发生错误");
                }

            }
            ois.close();
            client.close();

        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "请开启服务端后重试");
            System.exit(1);
        }
    }

    @SuppressWarnings("unchecked")
    public static void client_procManagerSocket_forDelete(int pid)
            throws Exception {
        String serverName = Index.ipAddress;
        int port = 6667;
        try {
            System.out.println("连接到主机：" + serverName + " ，端口号：" + port);
            Socket client = new Socket(serverName, port);
            System.out.println("远程主机地址：" + client.getRemoteSocketAddress());
            OutputStream outToServer = client.getOutputStream();
            DataOutputStream out = new DataOutputStream(outToServer);

            out.writeUTF(String.valueOf(pid));

            ObjectInputStream ois = new ObjectInputStream(
                    new BufferedInputStream(client.getInputStream()));
            /*
             * String obj = (String)ois.readObject(); if(obj.equals("200")){
             * System.out.println("************删除成功!"); }
             */
            ois.close();
            client.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /*---------------进程列表传输socket-------------*/
}

class Client_fileManagerUI {
    public  JFrame jf = new JFrame();// 文件管理器主窗口声明
    public  JTextField textField = new JTextField(65); // 文件管理器的路径显示
    public  JLabel clicked_route = new JLabel(); // 文件管理器的选中路径显示
    public static String route_forSocket = "";

    private String[] fileDirectory;

    public Client_fileManagerUI(String[] fileDirectory) {
        this.fileDirectory = fileDirectory;
    }

    public void drawFileList() {
        int frame_width = 800;
        int frame_height = 600;

        FontUIResource fontUIResource = new FontUIResource(new Font("宋体",
                Font.PLAIN, 12));
        // jf = new JFrame("测试窗口");
        jf.setLayout(null);
        jf.setUndecorated(true);
        jf.setLocationRelativeTo(null);
        jf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // 创建一个 JList 实例
        final JList<String> list = new JList<String>();
        // 允许可间断的多选
        list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        // 设置选项数据（内部将自动封装成 ListModel ）
        list.setListData(this.fileDirectory);

        // 添加选项选中状态被改变的监听器
        list.addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {
                if (list.getValueIsAdjusting()) {
                    // 获取所有被选中的选项索引
                    int[] indices = list.getSelectedIndices();
                    // 获取选项数据的 ListModel
                    ListModel<String> listModel = list.getModel();
                    // 输出选中的选项
                    for (int index : indices) {
                        System.out.println("选中: " + index + " = "
                                + listModel.getElementAt(index));
                        clicked_route.setText("当前选中   "
                                + listModel.getElementAt(index)+"       注：文件上传的路径与上方文本框中路径相同");

                        route_forSocket = listModel.getElementAt(index);
                    }
                    System.out.println();
                }
            }

        });

        list.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (list.getSelectedIndex() != -1) {
                    if (e.getClickCount() == 2) {
                        // 双击的时候，向对方传输路径

                        try {
                            System.out.println(FileProcManager.route_forSocket);
                            if(route_forSocket.equals("..")){
                                int pos=FileProcManager.route_forSocket.lastIndexOf("\\");
                                FileProcManager.route_forSocket=FileProcManager.route_forSocket.substring(0,pos+1);
                                System.out.println(FileProcManager.route_forSocket);
                            }
                            else {
                                FileProcManager.route_forSocket = route_forSocket;
                            }

                            FileProcManager.client_fileManagerSocket1();

                            jf.dispose();
                        } catch (Exception e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }
                    }

                }
            }
        });

        // 设置默认选中项
        list.setSelectedIndex(1);
        float height = this.fileDirectory.length * 20.1f;
        System.out.println(height);
        list.setPreferredSize(new Dimension(400, (int) height));

        // 创建一个带有滚动条的panel
        JScrollPane panel = new JScrollPane(list);
        panel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        panel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        panel.setPreferredSize(new Dimension(400, 300));
        panel.setBounds(0, 80, 400, 300);

        // 创建textfield使用的panel
        JPanel panel_textfield = new JPanel();
        textField = new JTextField(65);
        textField.setPreferredSize(new Dimension(400, 20));
        textField.setBounds(0, 55, 400, 30);
        textField.setText(FileProcManager.route_forSocket);
        panel_textfield.add(textField);
        panel_textfield.setPreferredSize(new Dimension(400, 300));
        panel_textfield.setBounds(0, 50, 400, 30);

        // 创建toolbar使用的panel
        JPanel panel_toolbar = new JPanel();
        panel_toolbar.setBounds(0, 0, 800, 50);
        ToolBar toolBar = new ToolBar();
        // 添加 工具栏 到 内容面板 的 顶部
        panel_toolbar.add(toolBar.toolBar);// 添加toolbar

        JPanel panel_button = new JPanel();
        panel_button = buttonBind(panel_button, jf); // 添加下方按钮
        panel_button.setBounds(0, 415, 400, 50);

        clicked_route = new JLabel("注：文件上传的路径与上方文本框中路径相同");
        clicked_route.setBounds(3, 385, 400, 15);

        jf.getRootPane().setWindowDecorationStyle(JRootPane.PLAIN_DIALOG);
        // 添加到内容面板容器
        jf.setSize(800, 600);
        jf.add(panel);
        jf.add(panel_textfield);
        jf.add(panel_toolbar);
        jf.add(clicked_route);
        jf.add(panel_button);
        jf.setTitle("文件管理");
        jf.setLocationRelativeTo(null);
        jf.setVisible(true);
    }

    private JPanel buttonBind(JPanel panel, final JFrame jf) { // 引入table为了刷新数据

        final JTextField textField;

        panel.setPreferredSize(new Dimension(400, 100));
        panel.setLayout(null);

        /* 按钮事件的绑定 */

        /* 按钮事件的绑定 */

        /*-------上传文件组件(包括上方的textfield)-----------*/

        JPanel panel_upload = new JPanel();
        final JFileChooser fileChooser = new JFileChooser();

        final JLabel label = new JLabel();
        label.setText("文件：");
        panel_upload.add(label);

        textField = new JTextField();
        textField.setColumns(23);
        panel_upload.add(textField);

        final JButton button = new JButton("选择文件");
        button.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {

                int i = fileChooser.showOpenDialog(jf.getContentPane());// 显示文件选择对话框

                // 判断用户单击的是否为“打开”按钮
                if (i == JFileChooser.APPROVE_OPTION) {

                    File selectedFile = fileChooser.getSelectedFile();// 获得选中的文件对象
                    textField.setText(selectedFile.getName());// 显示选中文件的名称
                    try {
                        FileProcManager.client_fileManagerSocket_upload(selectedFile);
                    } catch (Exception e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                }
            }
        });
        panel_upload.add(button);
        panel_upload.setBounds(0,420,400,30);
        jf.add(panel_upload);

        return panel;
    }
}

class Client_procManagerUI {
    public JFrame jf = new JFrame(); // 进程管理器主窗口声明
    public String[][] procList;
    private int pid = -1;// 创建用于删除的pid，当其值不为-1时允许删除

    public Client_procManagerUI(String[][] procList) {
        this.procList = procList;
    }

    public void drawProcTable() {
        // JFrame窗口设置
        FontUIResource fontUIResource = new FontUIResource(new Font("宋体",
                Font.PLAIN, 12));
        // jf = new JFrame("测试窗口");
        jf.setLayout(null);
        jf.setUndecorated(true);
        jf.setLocationRelativeTo(null);
        jf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        /* 表格创建与选中事件监听 */
        // 创建一个 JTable 实例
        DefaultTableModel tableModel = new DefaultTableModel(
                Arrays.copyOfRange(this.procList, 1, this.procList.length),
                this.procList[0]);
        final JTable table = new JTable(tableModel);
        // 允许可间断的多选
        // 设置行高
        table.setRowHeight(30);
        // 第一列列宽设置为40
        table.getColumnModel().getColumn(0).setPreferredWidth(40);
        // 设置滚动面板视口大小（超过该大小的行数据，需要拖动滚动条才能看到）
        table.setPreferredScrollableViewportSize(new Dimension(400, 300));
        // 添加选项选中状态被改变的监听器
        table.setCellSelectionEnabled(true);
        ListSelectionModel cellSelectionModel = table.getSelectionModel();
        cellSelectionModel
                .setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        cellSelectionModel
                .addListSelectionListener(new ListSelectionListener() {
                    public void valueChanged(ListSelectionEvent e) {
                        try {
                            String selectedData = null;
                            int[] selectedRow = table.getSelectedRows();
                            int[] selectedColumns = table.getSelectedColumns();
                            table.setColumnSelectionInterval(0, 4);
                            selectedData = (String) table.getValueAt(
                                    selectedRow[0], selectedColumns[1]);
                            pid = Integer.parseInt(selectedData);

                            System.out.println(pid);
                        } catch (Exception e2) {
                            // TODO: handle exception
                        }
                    }
                });
        // 设置默认选中项
        table.tableChanged(null);

        System.out.println(procList + "22222");
        // 创建一个带有滚动条的panel
        JScrollPane panel = new JScrollPane(table);
        panel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        panel.setPreferredSize(new Dimension(400, 300));
        panel.setBounds(0, 40, 400, 300);

        JPanel panel_button = new JPanel();
        panel_button = buttonBind(panel_button, table, jf); // 添加下方按钮
        panel_button.setBounds(0, 350, 400, 50);

        JPanel panel_table = new JPanel(new BorderLayout());
        ToolBar toolBar = new ToolBar();
        // 添加 工具栏 到 内容面板 的 顶部
        panel_table.add(toolBar.toolBar);// 添加toolbar
        panel_table.setBounds(0, 0, 800, 40);

        jf.getRootPane().setWindowDecorationStyle(JRootPane.PLAIN_DIALOG);
        // 添加到内容面板容器
        jf.setSize(800, 600);
        jf.setLocationRelativeTo(null);
        jf.add(panel_table);
        jf.add(panel);
        jf.add(panel_button);
        jf.setUndecorated(true);
        jf.setVisible(true);
    }

    private JPanel buttonBind(JPanel panel, final JTable table, final JFrame jf) { // 引入table为了刷新数据
        panel.setPreferredSize(new Dimension(400, 100));
        panel.setLayout(null);
        JButton button_Delete = new JButton("删除进程");
        JButton button_Refresh = new JButton("刷新");
        button_Delete.setBounds(50, 0, 100, 40);
        button_Refresh.setBounds(250, 0, 100, 40);// 按钮与容器的基本参数设置
        /* 按钮事件的绑定 */
        button_Refresh.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                final Thread thread = new Thread() {
                    public void run() {
                        try {
                            // 重新打开一个窗口，作为刷新
                            FileProcManager socket_server = new FileProcManager();
                            FileProcManager.client_procManagerSocket1();
                            FileProcManager.client_procManagerUI.jf
                                    .setVisible(true);

                            // 把原窗口去除
                            jf.dispose();
                            // socket_server.client_procManagerSocket1();
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                };
                thread.start(); // 新建一个线程以刷新表单
            }
        });
        button_Delete.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                final Thread thread = new Thread() {
                    public void run() {
                        try {
                            if (pid != -1) {
                                // 重新打开一个窗口，作为刷新
                                FileProcManager socket_server = new FileProcManager();
                                socket_server
                                        .client_procManagerSocket_forDelete(pid);
                                FileProcManager.client_procManagerSocket1();
                                FileProcManager.client_procManagerUI.jf
                                        .setVisible(true);

                                // 把原窗口去除
                                jf.dispose();
                                // socket_server.client_procManagerSocket1();
                            }
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                };
                thread.start(); // 新建一个线程以刷新表单
            }
        });
        /* 按钮事件的绑定 */
        panel.add(button_Delete);
        panel.add(button_Refresh);// 容器中添加按钮
        return panel;
    }
}
