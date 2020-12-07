package client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Class: 宿主机CMDos操纵类
 * 构造类需要指定ip,端口,要执行的cmd命令
 * */
public class ClientCmd {
    /** 宿主机IP */
    public static String ipAddress=null;
    /** CMDos连接端口 */
    public static int port=7888;
    /** cmd命令*/
    private static String cmdCommand=null;

    /**构造方法
     * 需要指定ip,端口,要执行的cmd命令
     * */
    public ClientCmd(int commandPort,String ipAddress,String cmdCommand){
        if(commandPort!=0)
            this.port=commandPort;
        this.cmdCommand=cmdCommand;
        this.ipAddress=ipAddress;
    }
    /**与宿主机建立连接,执行构造时的cmd命令,返回结果*/
    public String receiveResult() throws UnknownHostException, IOException {
        Socket commandSocket=new Socket(ipAddress,port);
        //把输出流封装在DataOutputStream中
        DataOutputStream out=new DataOutputStream(commandSocket.getOutputStream());
        //服务端向套间字写入要传到宿主机上执行的cmd命令
        out.writeUTF(cmdCommand);

        //构造输入流读取类,获取在宿主机上执行cmd后的结果字符串,windows cmd是gbk编码
        BufferedReader cmdResultReader=new BufferedReader(
                new InputStreamReader(
                        commandSocket.getInputStream(),"GBK"
                )
        );
        StringBuilder result=new StringBuilder();
        String tmpLine=null;
        while((tmpLine=cmdResultReader.readLine())!=null){
            if(tmpLine.length()!=0) {
                result.append(tmpLine+"\r\n");
            }
        }
        System.out.println("Log[cmd]:"+result);
        return result.toString();
    }
}
