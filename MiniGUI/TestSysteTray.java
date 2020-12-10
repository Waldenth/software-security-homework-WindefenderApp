
import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

public class TestSysteTray {
	public static void main(String args[]) {

		TrayIcon trayIcon = null;
		if (SystemTray.isSupported()) // 判断系统是否支持系统托盘
		{
			SystemTray tray = SystemTray.getSystemTray(); // 创建系统托盘
			Image image = Toolkit.getDefaultToolkit().getImage("image/logo.jpg");// 载入图片,这里要写你的图标路径哦

			ActionListener listener = new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					JFrame frame = new JFrame();
					frame.setBounds(400, 400, 200, 200);
					JLabel label = new JLabel("welcome JDK1.6");
					frame.add(label);
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
					if (JOptionPane.showConfirmDialog(null, "确定退出系统") == 0) {
						System.exit(0);
					}
				}
			});

			popup.add(mainFrameItem);
			popup.add(exitItem);

			trayIcon = new TrayIcon(image, "seara", popup);// 创建trayIcon
			trayIcon.addActionListener(listener);
			try {
				tray.add(trayIcon);
			} catch (AWTException e1) {
				e1.printStackTrace();
			}
		}
	}
}