package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ClientGUI {
    private static final String logoPath="resource/logo.jpg";

    public static void start(){
        int choose= JOptionPane.showConfirmDialog(
                null,"Would you want to start Screen Monitor?\n","Tip",JOptionPane.YES_NO_OPTION);
        if(choose==JOptionPane.YES_OPTION){
            JOptionPane.showMessageDialog(null,"Start Screen Monitor","Tip",JOptionPane.INFORMATION_MESSAGE);
            Flag.setIsWorking(true);
            trayStart();

            PhotoSender.startListenPort();
            while(Flag.isWorking){
                Screen.capture();
                System.out.println("capture finish!");
                PhotoSender.send();
            }
        }else{
            JOptionPane.showMessageDialog(null,"You have refused\nThe program will exit","Tip",JOptionPane.INFORMATION_MESSAGE);
        }
    }
    public static void trayStart(){
        if(SystemTray.isSupported()){
            TrayIcon trayIcon = null;
            Image programLogo= Toolkit.getDefaultToolkit().getImage(logoPath);
            SystemTray tray = SystemTray.getSystemTray();
            ActionListener listener = new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    JFrame frame = new JFrame();
                    frame.setSize(300,150);
                    frame.setLayout(null);
                    frame.setLocationRelativeTo(null);
                    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    if(Flag.isWorking){
                        JLabel Tips=new JLabel("Screen Monitor is working");
                        Tips.setBounds(50,20,200,50);
                        frame.getContentPane().add(Tips);
                    }else{ // for test
                        JLabel Tips=new JLabel("Screen Monitor is stopped");
                        Tips.setBounds(50,20,200,50);
                        frame.getContentPane().add(Tips);
                    }
                    frame.setVisible(true);
                }
            };

            // 创建弹出菜单
            PopupMenu popup = new PopupMenu();
            // 主界面选项
            MenuItem mainFrameItem = new MenuItem("Main Frame");
            mainFrameItem.addActionListener(listener);
            // 退出程序选项
            MenuItem exitItem = new MenuItem("exit");
            exitItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (JOptionPane.showConfirmDialog(null, "Exit program in 3s?") == 0) {
                        Flag.setIsWorking(false);
                        try{
                            Thread.sleep(3000);
                        }catch (Exception exception){
                            exception.printStackTrace();
                        }
                        System.exit(0);
                    }
                }
            });
            popup.add(mainFrameItem);
            popup.add(exitItem);
            trayIcon = new TrayIcon(programLogo, "Monitor", popup);// 创建trayIcon
            trayIcon.addActionListener(listener);
            try {
                tray.add(trayIcon);
            } catch (AWTException e1) {
                e1.printStackTrace();
            }

        }else{
            JOptionPane.showMessageDialog(
                    null,"You OS is not supported Tray\n The program will exit","Error",JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[]args){
        start();
    }
}