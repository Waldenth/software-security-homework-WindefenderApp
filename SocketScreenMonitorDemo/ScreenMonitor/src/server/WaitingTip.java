package server;

import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JLabel;

public class WaitingTip {
	public static JFrame jf=new JFrame("Wait");
	public static JLabel waitTipLabel = new JLabel("wait image data....");
	public static void show() {
		jf.setSize(300,150);
		jf.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		jf.setLocationRelativeTo(null);
		waitTipLabel.setBounds(20, 30, 200, 50);
		waitTipLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
		jf.getContentPane().add(waitTipLabel);
		jf.setVisible(true);
	}
}
