package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

public class ServerCmd {
    public static int commandport = 7888;
    public static String cmd = null;

    public static String executeCommand() { // Ö´ÐÐÃüÁî
        StringBuilder sb = new StringBuilder();
        try {
            Process process = Runtime.getRuntime().exec(cmd);
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(process.getInputStream(),"GBK"));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line+"\n");
            }
        } catch (Exception e) {
            return e.toString();
        }
        System.out.println(sb);
        return sb.toString();
    }

    public static void sendResult() throws UnknownHostException, IOException {
        ServerSocket commandSocket = null;
        try {
            commandSocket = new ServerSocket(commandport);
            commandSocket.setSoTimeout(999999999);
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        if (commandSocket != null) {
            while (Flag.isWorking) {
                try {
                    Socket socket = commandSocket.accept();
                    DataInputStream in = new DataInputStream(
                            socket.getInputStream());
                    cmd = in.readUTF().toString();
					/*PrintWriter commandWriter = new PrintWriter(
							socket.getOutputStream());*/
                    if (cmd != null) {
                        BufferedWriter out=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(),"GBK"));
                        out.write(executeCommand());
                        out.close();
                        /*commandWriter.println(executecommand());*/
                        System.out.println("ÊÕµ½ÃüÁî");
                    } else {
                        System.out.println("ÃüÁî²»ÄÜÎª¿Õ");
                    }
                    /*commandWriter.close();*/
                    socket.close();
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
