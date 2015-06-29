package rithm.driver;

import java.io.IOException;

import rithm.client.RiTHMClient;
import rithm.core.ProgState;
import rithm.defaultcore.DefaultPredicateEvaluator;
import rithm.defaultcore.DefaultProgramState;
import rithm.defaultcore.DefaultRiTHMSpecificationCollection;
import rithm.mtl.MTLMonitor;
import rithm.mtl.MTLTest;
import rithm.mtl.TwoValuedValuation;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class RiTHMDriverTests extends TestCase {
	public RiTHMDriverTests( String testName )
    {
        super( testName );

    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( RiTHMDriverTests.class );
    }
    public void testServerConnection()
    {
    	RiTHMSecureServer rServer = 
    			new RiTHMSecureServer(7800, true, true, "/home/y2joshi/YJkeystore.jks","123456");
    	rServer.start();
    	RiTHMClient rClient = 
    			new RiTHMClient("localhost", 7800, 
    					"/home/y2joshi/truststore.jks","123456");
    	String propfilePath = 
    			RiTHMSecureServer.class.getClassLoader().getResource("rithm.properties").getFile();
    	if(propfilePath != null)
    		assertTrue(rClient.readPropertyFile(propfilePath));
    	else
    		fail();
    	rClient.connect();

    	ProgState ps1 = new DefaultProgramState(1);
    	ps1.setValue("a", "1");
//    	ps1.setValue("b", "0");
    	
    	ProgState ps2 = new DefaultProgramState(2);
//    	ps2.setValue("a", "0");
    	ps2.setValue("b", "1");
    	
    	ProgState ps3 = new DefaultProgramState(-1);
//    	ps3.setValue("a", "0");
    	ps3.setValue("b", "1");
    	
    	try {
        	rClient.sendConfiJSON();
        	rClient.readReply();
			rClient.sendProgState(ps1);
			rClient.readReply();
			rClient.sendProgState(ps2);
			rClient.readReply();
			rClient.sendProgState(ps3);
			rClient.readReply();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	assertTrue(rClient.isConnected());
    	rServer.interrupt();
    }
}
