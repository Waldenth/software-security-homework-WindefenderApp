package DefenderTools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.DrbgParameters.NextBytes;

import javax.swing.JOptionPane;


public class Defender {
    public static void main(String[] args) {
        int a = 6666;
        if (getPID("6666") != null) {
            //String s2=getProgramName(getPID("4021"));
            if (getProgramName(getPID("6666")).equals("javaw.exe")) {
                //System.out.println(getProgramName(getPID("6666")));
                openDio(a, getProgramName(getPID("6666")));//根据映像名称关闭进程

            }
        }


        //端口6667
        //System.out.println(getPID("6667"));
        int b = 6667;
        if (getPID("6667") != null) {
            //String s2=getProgramName(getPID("4021"));
            if (getProgramName(getPID("6667")).equals("javaw.exe")) {
                //System.out.println(getProgramName(getPID("6667")));
                openDio(b, getProgramName(getPID("6667")));//根据映像名称关闭进程
            }
        }

        //端口6668
        //System.out.println(getPID("6668"));
        int c = 6668;
        if (getPID("6668") != null) {
            //String s2=getProgramName(getPID("4021"));
            if (getProgramName(getPID("6668")).equals("javaw.exe")) {
                //System.out.println(getProgramName(getPID("6668")));
                openDio(c, getProgramName(getPID("6668")));//根据映像名称关闭进程
            }
        }

        //端口6669
        // System.out.println(getPID("6669"));
        int d = 6669;
        if (getPID("6669") != null) {
            //String s2=getProgramName(getPID("4021"));
            if (getProgramName(getPID("6669")).equals("javaw.exe")) {
                //System.out.println(getProgramName(getPID("6669")));
                openDio(d, getProgramName(getPID("6669")));//根据映像名称关闭进程
            }
        }
        //端口7888
        // System.out.println(getPID("7888"));
        int e = 7888;
        if (getPID("7888") != null) {
            //String s2=getProgramName(getPID("4021"));
            if (getProgramName(getPID("7888")).equals("javaw.exe")) {
                //System.out.println(getProgramName(getPID("7888")));
                openDio(e, getProgramName(getPID("7888")));//根据映像名称关闭进程
            }
        }
    }
    //}


    // 得到进程ID

    public static String getPID(String port) {

        InputStream is = null;

        BufferedReader br = null;

        String pid = null;

        try {

            String[] args = new String[]{"cmd.exe", "/c", "netstat -aon|findstr", port};

            is = Runtime.getRuntime().exec(args).getInputStream();

            br = new BufferedReader(new InputStreamReader(is));

            String temp = br.readLine();

            if (temp != null) {

                String[] strs = temp.split("\\s");

                pid = strs[strs.length - 1];

            }

        } catch (IOException e) {

            e.printStackTrace();

        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return pid;
    }


    //根据进程ID得到映像名称

    public static String getProgramName(String pid) {

        InputStream is = null;

        BufferedReader br = null;

        String programName = null;

        try {

            String[] args = new String[]{"cmd.exe", "/c", "tasklist|findstr", pid};

            is = Runtime.getRuntime().exec(args).getInputStream();

            br = new BufferedReader(new InputStreamReader(is));

            String temp = br.readLine();

            if (temp != null) {

                String[] strs = temp.split("\\s");

                programName = strs[0];

            }

        } catch (IOException e) {

            e.printStackTrace();

        } finally {

            try {

                br.close();

            } catch (IOException e) {

                e.printStackTrace();

            }

        }

        return programName;

    }


    //根据映像名称关闭进程

    public static void killTask(String programName) {

        String[] args = new String[]{"Taskkill", "/f", "/IM", programName};

        try {

            Runtime.getRuntime().exec(args);

        } catch (IOException e) {

            e.printStackTrace();

        }

    }

    public static void openDio(Integer a, String programName) {
        int res = 0;
        switch (a) {
            case 6666:
                res = JOptionPane.showConfirmDialog(null, "是否允许远程访问文件管理", "警告", JOptionPane.YES_NO_OPTION);
                break;
            case 6667:
                res = JOptionPane.showConfirmDialog(null, "是否允许远程访问进程管理", "警告", JOptionPane.YES_NO_OPTION);
                break;
            case 6668:
                res = JOptionPane.showConfirmDialog(null, "是否允许远程访问摄像头记录", "警告", JOptionPane.YES_NO_OPTION);
                break;
            case 6669:
                res = JOptionPane.showConfirmDialog(null, "是否允许远程访问文件上传", "警告", JOptionPane.YES_NO_OPTION);
                break;
            case 7888:
                res = JOptionPane.showConfirmDialog(null, "是否允许远程访问cmd命令", "警告", JOptionPane.YES_NO_OPTION);
                break;
            default:
                break;
        }

        if (res == JOptionPane.NO_OPTION) {
            killTask(programName);
            return;
        } else {
            return;
        }
    }
}
