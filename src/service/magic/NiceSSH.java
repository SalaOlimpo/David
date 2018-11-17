package service.magic;

import java.io.InputStream;
import java.net.InetAddress;
import java.util.Properties;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import service.utils.Constants;

/**
 * Class used to implement SHH function.
 * 
 * @author	SalaOlimpo
 * @since	24.08.2018
 */
public class NiceSSH {
	
	private JSch		jsch;
	private Session		sess;
	private ChannelExec	channel;
	
	public NiceSSH(String host) throws Exception {
		this(host, Constants.SSH.USER, Constants.SSH.PASS);
	}
	public NiceSSH(String host, String user, String pass) throws Exception {
		InetAddress address = InetAddress.getByName(host);
		jsch = new JSch();
		
		Properties props = new Properties(); 
	    props.put("StrictHostKeyChecking", "no");
	    
		sess = jsch.getSession(user, address.getHostAddress(), Constants.SSH.PORT);
		sess.setConfig(props);
		sess.setPassword(pass);
		sess.connect();
	}


	public String execCmd(String cmd, boolean wait) throws Exception {
		channel = (ChannelExec) sess.openChannel("exec");
		channel.setCommand(cmd);
		channel.setInputStream(null);
		channel.setErrStream(System.err);
		
		String result  = "";
		InputStream in = channel.getInputStream();

		channel.connect();

		byte[] tmp = new byte[1024];
		while(true) {
			while(in.available() > 0) {
				int i = in.read(tmp, 0, 1024);
				if(i < 0) break;
				result += new String(tmp, 0, i);
			}
			if(channel.isClosed()) {
				if(in.available()>0) continue; 
				break;
			}
			try{Thread.sleep(100);}catch(Exception ee){}
		}
		channel.disconnect();

		return result;
	}
	
	public void closeSSH() {
		sess.disconnect();
	}
}
