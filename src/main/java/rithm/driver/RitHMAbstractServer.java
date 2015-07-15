package rithm.driver;

import java.net.Socket;
import java.util.ArrayList;

// TODO: Auto-generated Javadoc
/**
 * The Class RiTHMAbstractServer.
 *
 * @author y2joshi
 */
public class RitHMAbstractServer extends Thread{
	
	/** The started client threads. */
	protected ArrayList<Thread> startedClientThreads;
	
	/**
	 * Instantiates a new ri thm abstract server.
	 */
	public RitHMAbstractServer()
	{
		super();
		startedClientThreads = new ArrayList<Thread>();
	}
	
	/**
	 *  Adds a client thread to startedClientThreads.
	 *
	 * @param cThread {@link RitHMClientHandler} {@link Thread}
	 */
	public void addClientThread(Thread cThread)
	{
		startedClientThreads.add(cThread);
	}
	
	/**
	 *  Waits for each thread in startedClientThreads to complete its execution.
	 */
	public void waitForClientThreads()
	{
		try
		{
			for(Thread t: startedClientThreads)
			{
				t.join();
				startedClientThreads.remove(t);
			}
		}catch(InterruptedException ie)
		{
			ie.printStackTrace();
		}
	}
	
}
