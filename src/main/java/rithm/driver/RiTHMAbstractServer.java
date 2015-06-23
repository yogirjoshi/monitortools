package rithm.driver;

import java.net.Socket;
import java.util.ArrayList;

public class RiTHMAbstractServer extends Thread{
	protected ArrayList<Thread> startedClientThreads;
	public RiTHMAbstractServer()
	{
		super();
		startedClientThreads = new ArrayList<Thread>();
	}
	public void addClientThread(Thread cThread)
	{
		startedClientThreads.add(cThread);
	}
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
