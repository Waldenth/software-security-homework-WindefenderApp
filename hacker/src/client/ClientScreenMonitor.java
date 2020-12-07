package client;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Class: 宿主机屏幕监控管理类
 * */
public class ClientScreenMonitor {
    /**连接端口*/
    public static int port=8000;
    /** 宿主机IP */
    public static String ipAddress=null;
    /** Socket*/
    public static Socket screenSocket=null;
    /**cache dir Path*/
    public static String dirPath=null;
    /** work flag*/
    public static volatile boolean isWorking=false;
    /** picture index*/
    public static int k=0;
    /** speed */
    private static int speed=300;

    public ClientScreenMonitor(String ipAddress,int port) throws UnknownHostException, IOException {
        if(port!=0)
            this.port=port;
        // 图片缓存
        File pictureCache=new File("MonitorCache");
        if(!pictureCache.exists()){
            pictureCache.mkdir();
        }
        dirPath=pictureCache.getAbsolutePath();
        //screenSocket=new Socket(ipAddress,port);
        //System.out.println("Success connect to target Device");
    }

    /**建立连接*/
    public static boolean establishConnection(){
        try {
            screenSocket = new Socket(ipAddress, port);
        }catch (Exception e){
            e.printStackTrace();
        }
        return true;
    }

    /**存储截图,注意调用前必须调用establishConnection,并确定是true,
     * 并且还需将isWorking设置成true
     * */
    public static void receivePicture()throws Exception{
        k=0;
        while(isWorking) {
            //获取宿主机截图套间字数据
            InputStream getData = screenSocket.getInputStream();
            //准备写入到缓存文件夹
            FileOutputStream fos = new FileOutputStream(dirPath +"/screen_capture" +k + ".png");
            k++;
            byte[] buf=new byte[4096];
            int len=0;
            while((len=getData.read(buf))!=-1){
                fos.write(buf,0,len);
            }
            fos.close();
            getData.close();
            Thread.sleep(speed);
        }
        screenSocket.close();
        k=0;
    }
    public void setSpeed(int speed){
        this.speed=speed;
    }
    public void setFlag(boolean flag){
        this.isWorking=flag;
    }
    public void setPort(int port){
        this.port=port;
    }
}
