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
// TODO: Auto-generated Javadoc

/**
 * The Class RiTHMSecureServer.
 */
public class RitHMSecureServer extends RitHMAbstractServer{

	/** The Constant logger. */
	final static Logger logger = Logger.getLogger(RitHMSecureServer.class);
	
	/** The key store path. */
	protected String keyStorePath;
	
	/** The key store pass. */
	protected String keyStorePass;
	
	/** The is secure mode. */
	protected boolean isSecureMode;
	
	/** The conf by client. */
	protected boolean confByClient;
	
	/** The port no. */
	protected int portNo;
	
	/** The prop file name. */
	protected String propFileName = null;
	
	/**
	 * Instantiates a new ri thm secure server.
	 *
	 * @param propFileName the prop file name
	 */
	public RitHMSecureServer(String propFileName)
	{
		super();
		this.propFileName = propFileName;
		InputStream is = null;
		try
		{
			is = new FileInputStream(propFileName);
			Properties prop = new Properties();
			prop.load(is);
			isSecureMode = Boolean.getBoolean(prop.getProperty("secureMode"));
			portNo = Integer.parseInt(prop.getProperty("port"));
			confByClient = Boolean.getBoolean(prop.getProperty("remoteConfig"));
			if(isSecureMode)
			{
				keyStorePath = prop.getProperty("keyStore");
				keyStorePass = prop.getProperty("keyStorePassword");
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
	
	/**
	 * Instantiates a new ri thm secure server.
	 *
	 * @param portNo the port no
	 * @param isSecureMode the is secure mode
	 * @param confByClient the conf by client
	 * @param keyStorePath the key store path
	 * @param keyStorePass the key store pass
	 */
	public RitHMSecureServer(int portNo, boolean isSecureMode, boolean confByClient, String keyStorePath, String keyStorePass)
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
	
	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
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
				RitHMClientHandler rcHandler=null;
				SSLSocket sslsocket = null;
				Socket nonsslsocket = null;
				if(isSecureMode)
				{
					sslsocket = (SSLSocket) sslserversocket.accept();
					rcHandler = new RitHMClientHandler(sslsocket,confByClient);
					logger.info("New client connected with IP:" + sslsocket.getInetAddress().toString());
				}
				else
				{
					nonsslsocket = serverSocket.accept();
					rcHandler = new RitHMClientHandler(nonsslsocket,confByClient);
					logger.info("New client connected with IP:" + nonsslsocket.getInetAddress().toString());
				}

				addClientThread(rcHandler);
				rcHandler.start();
			}
			waitForClientThreads();
		} 
		catch (IOException ie)
		{
			logger.fatal(ie.getMessage());
		}

    }
	
}
