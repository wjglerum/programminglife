package core;
//TODO add correct comments and formatting
/* -*-mode:java; c-basic-offset:2; indent-tabs-mode:nil -*- */
/**
 * This program will demonstrate the port forwarding like option -L of
 * ssh command; the given port on the local host will be forwarded to
 * the given remote host and port on the remote side.
 *   $ CLASSPATH=.:../build javac PortForwardingL.java
 *   $ CLASSPATH=.:../build java PortForwardingL
 * You will be asked username, hostname, port:host:hostport and passwd. 
 * If everything works fine, you will get the shell prompt.
 * Try the port on localhost.
 *
 */
import com.jcraft.jsch.*;
import java.awt.*;
import javax.swing.*;

public class PortForwardingL{
	static int lport;
	static String rhost;
	static int rport;
	static String host=null;
	static Session session;
	
	public static void startPortForwarding(){

		try{
			JSch jsch=new JSch();

			host=JOptionPane.showInputDialog("Enter username@hostname",
					System.getProperty("user.name")+
					"@student-linux.tudelft.nl"); 

			String user=host.substring(0, host.indexOf('@'));
			host=host.substring(host.indexOf('@')+1);

			session=jsch.getSession(user, host, 22);

			String foo=JOptionPane.showInputDialog("Enter -L port:host:hostport",
					"5432:watson:5432");
			lport=Integer.parseInt(foo.substring(0, foo.indexOf(':')));
			foo=foo.substring(foo.indexOf(':')+1);
			rhost=foo.substring(0, foo.indexOf(':'));
			rport=Integer.parseInt(foo.substring(foo.indexOf(':')+1));

			// username and password will be given via UserInfo interface.
			UserInfo ui=new MyUserInfo();
			session.setUserInfo(ui);

			session.connect();	

			int assigned_port=session.setPortForwardingL(lport, rhost, rport);
			System.out.println("localhost:"+assigned_port+" -> "+rhost+":"+rport);
		}
		catch(Exception e){
			System.out.println(e);
		}
	}
	
	public static void stopPortForwarding() {
		session.disconnect();
	}
	
	public static class MyUserInfo implements UserInfo, UIKeyboardInteractive{
		public String getPassword(){ return passwd; }
		public boolean promptYesNo(String str){
			Object[] options={ "yes", "no" };
			int foo=JOptionPane.showOptionDialog(null, 
					str,
					"Warning", 
					JOptionPane.DEFAULT_OPTION, 
					JOptionPane.WARNING_MESSAGE,
					null, options, options[0]);
			return foo==0;
		}

		String passwd;
		JTextField passwordField=(JTextField)new JPasswordField(20);

		public String getPassphrase(){ return null; }
		public boolean promptPassphrase(String message){ return true; }
		public boolean promptPassword(String message){
			Object[] ob={passwordField}; 
			int result=
					JOptionPane.showConfirmDialog(null, ob, message,
							JOptionPane.OK_CANCEL_OPTION);
			if(result==JOptionPane.OK_OPTION){
				passwd=passwordField.getText();
				return true;
			}
			else{ return false; }
		}
		public void showMessage(String message){
			JOptionPane.showMessageDialog(null, message);
		}
		final GridBagConstraints gbc = 
				new GridBagConstraints(0,0,1,1,1,1,
						GridBagConstraints.NORTHWEST,
						GridBagConstraints.NONE,
						new Insets(0,0,0,0),0,0);
		private Container panel;
		public String[] promptKeyboardInteractive(String destination,
				String name,
				String instruction,
				String[] prompt,
				boolean[] echo){
			panel = new JPanel();
			panel.setLayout(new GridBagLayout());

			gbc.weightx = 1.0;
			gbc.gridwidth = GridBagConstraints.REMAINDER;
			gbc.gridx = 0;
			panel.add(new JLabel(instruction), gbc);
			gbc.gridy++;

			gbc.gridwidth = GridBagConstraints.RELATIVE;

			JTextField[] texts=new JTextField[prompt.length];
			for(int i=0; i<prompt.length; i++){
				gbc.fill = GridBagConstraints.NONE;
				gbc.gridx = 0;
				gbc.weightx = 1;
				panel.add(new JLabel(prompt[i]),gbc);

				gbc.gridx = 1;
				gbc.fill = GridBagConstraints.HORIZONTAL;
				gbc.weighty = 1;
				if(echo[i]){
					texts[i]=new JTextField(20);
				}
				else{
					texts[i]=new JPasswordField(20);
				}
				panel.add(texts[i], gbc);
				gbc.gridy++;
			}

			if(JOptionPane.showConfirmDialog(null, panel, 
					destination+": "+name,
					JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE)
					==JOptionPane.OK_OPTION){
				String[] response=new String[prompt.length];
				for(int i=0; i<prompt.length; i++){
					response[i]=texts[i].getText();
				}
				return response;
			}
			else{
				return null;  // cancel
			}
		}
	}
}