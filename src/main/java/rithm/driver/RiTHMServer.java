package rithm.driver;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

import org.apache.log4j.Logger;

import rithm.mtl.MTLMonitor;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
public class RiTHMServer extends Thread{

	final static Logger logger = Logger.getLogger(RiTHMServer.class);
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			SSLServerSocketFactory sslserversocketfactory =
					(SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
			SSLServerSocket sslserversocket =
					(SSLServerSocket) sslserversocketfactory.createServerSocket(7800);

			while(true)
			{
				SSLSocket sslsocket = (SSLSocket) sslserversocket.accept();
				logger.info("Connected client with IP" + sslsocket.getInetAddress().toString());
				RiTHMClientHandler rcHandler = new RiTHMClientHandler(sslsocket);
				rcHandler.start();
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
    }
	
}
