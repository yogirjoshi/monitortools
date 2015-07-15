package rithm.driver;

import rithm.core.DataFactory;
import rithm.core.RitHMMonitor;
import rithm.core.RitHMPlugin;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
public class PluginLoaderTests extends TestCase {

	public PluginLoaderTests( String testName )
	{
		super( testName );

	}

	/**
	 * Suite.
	 *
	 * @return the suite of tests being tested
	 */
	public static Test suite()
	{
		return new TestSuite( PluginLoaderTests.class );
	}
	public void testcheckInterfaceReq1()
	{
		Class<?> clazz = RitHMPlugin.class;
		assertTrue(RitHMPlugin.class.isAssignableFrom(DataFactory.class));
	}
}
