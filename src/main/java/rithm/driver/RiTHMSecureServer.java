package rithm.driver;
import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.print.DocFlavor.STRING;
import javax.swing.text.StyledEditorKit.BoldAction;

import org.apache.log4j.Logger;

import rithm.mtl.MTLMonitor;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;
public class RiTHMSecureServer extends RiTHMAbstractServer{

	final static Logger logger = Logger.getLogger(RiTHMSecureServer.class);
	protected String keyStorePath;
	protected String keyStorePass;
	protected boolean isSecureMode;
	protected boolean confByClient;
	protected int portNo;
	public RiTHMSecureServer(String propFileName)
	{
		super();
		InputStream is = null;
		try
		{
			is = new FileInputStream(propFileName);
			Properties prop = new Properties();
			prop.load(is);
			isSecureMode = Boolean.getBoolean(prop.getProperty("secure"));
			portNo = Integer.parseInt(prop.getProperty("port"));
			confByClient = Boolean.getBoolean(prop.getProperty("remoteconfig"));
			if(isSecureMode)
			{
				keyStorePath = prop.getProperty("key_store");
				keyStorePass = prop.getProperty("key_store_password");
				System.setProperty("javax.net.ssl.keyStore", keyStorePath); 
				System.setProperty("javax.net.ssl.keyStorePassword", keyStorePass);
			}	
		}
		catch(NumberFormatException ne)
		{
			logger.fatal("Invalid Port No in the configuration");
		}
		catch(IOException io)
		{
			logger.fatal("Fatal IO error, check files");
		}finally{
			if(is != null)
			{
				try {
					is.close();
				} catch (IOException ie) {
					// TODO: handle exception
					logger.fatal("Error Closing the configuration file");
				}
			}
		}
	}
	public RiTHMSecureServer(int portNo, boolean isSecureMode, boolean confByClient, String keyStorePath, String keyStorePass)
	{
		super();
		this.portNo = portNo;
		this.isSecureMode = isSecureMode;
		this.confByClient = confByClient;
		if(isSecureMode)
		{
			this.keyStorePath = keyStorePath;
			this.keyStorePass = keyStorePass;
			System.setProperty("javax.net.ssl.keyStore", keyStorePath); 
			System.setProperty("javax.net.ssl.keyStorePassword", keyStorePass);
		}
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		SSLServerSocket sslserversocket = null;
		SSLServerSocketFactory sslserversocketfactory = null;
		ServerSocket serverSocket = null;
		ServerSocketFactory serverSocketFactory = null;
		try {
			logger.info("Starting in SSL mode:" + isSecureMode);
			if(isSecureMode)
			{
				sslserversocketfactory =
					(SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
				sslserversocket =
					(SSLServerSocket) sslserversocketfactory.createServerSocket(portNo);
			}
			else
			{
				serverSocketFactory =
					ServerSocketFactory.getDefault();
				serverSocket =
					serverSocketFactory.createServerSocket(portNo);
			}
			while(!Thread.interrupted())
			{
				RiTHMClientHandler rcHandler=null;
				SSLSocket sslsocket = null;
				Socket nonsslsocket = null;
				if(isSecureMode)
				{
					sslsocket = (SSLSocket) sslserversocket.accept();
					rcHandler = new RiTHMClientHandler(sslsocket,confByClient);
					logger.info("Connected client with IP" + sslsocket.getInetAddress().toString());
				}
				else
				{
					nonsslsocket = serverSocket.accept();
					rcHandler = new RiTHMClientHandler(nonsslsocket,confByClient);
					logger.info("Connected client with IP" + nonsslsocket.getInetAddress().toString());
				}

				
				addClientThread(rcHandler);
				rcHandler.start();
			}
			waitForClientThreads();
		} 
		catch (IOException ie)
		{
			ie.printStackTrace();
		}
//		catch (Exception) {
//			exception.printStackTrace();
//		}
    }
	
}
