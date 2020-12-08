package server;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
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
                AutoRun();
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
                    case 1:
                        Thread ServerCamera=new Thread(){
                            public void run(){
                                synchronized (someObject){
                                    /*
                                    CameraManager cameraManager=new CameraManager();
                                    cameraManager.setPort(port_camera);
                                    cameraManager.serverCameraRecord();
                                    */

                                };

                                try{
                                    Thread.sleep(100);
                                }catch (InterruptedException e){
                                    e.printStackTrace();
                                }
                            }
                        };
                        ServerCamera.start();
                        break;
                    case 2:
                        Thread ServerCmd=new Thread(){
                            public void run(){
                                synchronized (someObject){

                                };
                                try{
                                    Thread.sleep(100);
                                }catch (InterruptedException e){
                                    e.printStackTrace();
                                }
                            }
                        };
                        ServerCmd.start();
                        break;
                    case 3:
                        break;

                }
            }
        }
        commandNumberStream.close();
        getCommandNumberStream.close();
        CommandInteraction.close();
    }
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

class CameraManager {
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
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
