package rithm.mtl;

import rithm.core.*;
import rithm.defaultcore.*;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class MTLTest extends TestCase {
	protected MTLMonitor mtlM;
	protected RiTHMSpecificationCollection rsColl;
	RiTHMResultCollection rresColl;
    public MTLTest( String testName )
    {
        super( testName );
    	mtlM = new MTLMonitor();
    	rsColl = new DefaultRiTHMSpecificationCollection();

       	mtlM.setMonitorValuation(new TwoValuedValuation());
    	mtlM.setPredicateEvaluator(new DefaultPredicateEvaluator(true));
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( MTLTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testTemporalBooleanCombination1()
    {
    	RiTHMSpecification rSpec1 =  new DefaultRiTHMSpecification("b->[]{1,2}(<>{0,1}a)");
    	rsColl.add(rSpec1);
    	mtlM.synthesizeMonitors(rsColl);
    	
    	ProgState ps1 = new DefaultProgramState(0);
    	ps1.setValue("a", "0");
    	ps1.setValue("b", "1");
    	mtlM.fillBuffer(ps1);
    	
    	ProgState ps2 = new DefaultProgramState(1);
    	ps2.setValue("a", "0");
    	ps2.setValue("b", "0");
    	mtlM.fillBuffer(ps2);
    	
    	ProgState ps3 = new DefaultProgramState(2);
    	ps3.setValue("a", "1");
    	ps3.setValue("b", "0");
    	mtlM.fillBuffer(ps3);
    	
    	rresColl = mtlM.runMonitor();
    	assertTrue(Boolean.valueOf(rresColl.getResult(rSpec1).getTruthValueDescription()));
    }
    
    public void testGloballyNested1()
    {
    	RiTHMSpecification rSpec1 =  new DefaultRiTHMSpecification("[]{1,2}(<>{0,1}a)");
    	rsColl.add(rSpec1);
    	mtlM.synthesizeMonitors(rsColl);
    	
    	ProgState ps1 = new DefaultProgramState(0);
    	ps1.setValue("a", "0");
    	mtlM.fillBuffer(ps1);
    	
    	ProgState ps2 = new DefaultProgramState(1);
    	ps2.setValue("a", "0");
    	mtlM.fillBuffer(ps2);
    	
    	ProgState ps3 = new DefaultProgramState(2);
    	ps3.setValue("a", "1");
    	mtlM.fillBuffer(ps3);
    	
    	rresColl = mtlM.runMonitor();
    	assertTrue(Boolean.valueOf(rresColl.getResult(rSpec1).getTruthValueDescription()));
    }
    public void testGloballySimle1()
    {
    	RiTHMSpecification rSpec1 =  new DefaultRiTHMSpecification("[]{1,2}a");
    	rsColl.add(rSpec1);
    	mtlM.synthesizeMonitors(rsColl);
    	
    	ProgState ps1 = new DefaultProgramState(0);
    	ps1.setValue("a", "0");
    	mtlM.fillBuffer(ps1);
    	
    	ProgState ps2 = new DefaultProgramState(1);
    	ps2.setValue("a", "1");
    	mtlM.fillBuffer(ps2);
    	
    	ProgState ps3 = new DefaultProgramState(2);
    	ps3.setValue("a", "1");
    	mtlM.fillBuffer(ps3);
    	
    	rresColl = mtlM.runMonitor();
    	assertTrue(Boolean.valueOf(rresColl.getResult(rSpec1).getTruthValueDescription()));
    }
    public void testEventuallyNested2()
    {
    	RiTHMSpecification rSpec1 = new DefaultRiTHMSpecification("aU{0,1}(<>{1,1}b)");
    	rsColl.add(rSpec1);
    	mtlM.synthesizeMonitors(rsColl);
    	
    	ProgState ps1= new DefaultProgramState(0);
    	ps1.setValue("a", "1");
    	ps1.setValue("b", "0");
    	mtlM.fillBuffer(ps1);
//    	System.out.println(ps1);
    	
    	ProgState ps2= new DefaultProgramState(1);
    	ps2.setValue("a", "0");
    	ps2.setValue("b", "0");
    	mtlM.fillBuffer(ps2);
    	
    	ProgState ps3= new DefaultProgramState(2);
    	ps3.setValue("b", "1");
    	ps3.setValue("a", "0");
    	mtlM.fillBuffer(ps3);
    	
    	rresColl = mtlM.runMonitor();
    	assertTrue(Boolean.valueOf(rresColl.getResult(rSpec1).getTruthValueDescription()));
    }
    
    public void testEventuallyNested1()
    {
    	RiTHMSpecification rSpec1 = new DefaultRiTHMSpecification("<>{0,1}(<>{1,5}b)");
    	rsColl.add(rSpec1);
    	mtlM.synthesizeMonitors(rsColl);
    	
    	ProgState ps1= new DefaultProgramState(0);
    	ps1.setValue("b", "0");
    	mtlM.fillBuffer(ps1);
//    	System.out.println(ps1);
    	
    	ProgState ps2= new DefaultProgramState(1);
    	ps2.setValue("b", "0");
    	mtlM.fillBuffer(ps2);
    	
    	ProgState ps3= new DefaultProgramState(2);
    	ps3.setValue("b", "1");
    	mtlM.fillBuffer(ps3);
    	
    	rresColl = mtlM.runMonitor();
    	assertTrue(Boolean.valueOf(rresColl.getResult(rSpec1).getTruthValueDescription()));
    }
    
    public void testEventuallyBasic1()
    {
    	RiTHMSpecification rSpec1 = new DefaultRiTHMSpecification("<>{1,5}b");
    	rsColl.add(rSpec1);
    	mtlM.synthesizeMonitors(rsColl);
    	
    	ProgState ps1= new DefaultProgramState(0);
    	ps1.setValue("b", "1");
    	mtlM.fillBuffer(ps1);
//    	System.out.println(ps1);
    	
    	ProgState ps2= new DefaultProgramState(1);
    	ps2.setValue("b", "0");
    	mtlM.fillBuffer(ps2);
    	
    	ProgState ps3= new DefaultProgramState(2);
    	ps3.setValue("b", "1");
    	mtlM.fillBuffer(ps3);
    	
    	rresColl = mtlM.runMonitor();
    	assertTrue(Boolean.valueOf(rresColl.getResult(rSpec1).getTruthValueDescription()));
    }
    public void testUntilBasic1()
    {

    	
    	RiTHMSpecification rSpec1 = new DefaultRiTHMSpecification("aU{1,2}b");
    	rsColl.add(rSpec1);
//    	mtlM.setFormulas(rsColl);
    	mtlM.synthesizeMonitors(rsColl);
    	
    	ProgState ps1= new DefaultProgramState(0);
    	ps1.setValue("a", "1");
    	ps1.setValue("b", "0");
    	mtlM.fillBuffer(ps1);
//    	System.out.println(ps1);
    	
    	ProgState ps2= new DefaultProgramState(1);
    	ps2.setValue("a", "1");
    	ps2.setValue("b", "0");
    	mtlM.fillBuffer(ps2);
    	
    	ProgState ps3= new DefaultProgramState(2);
    	ps3.setValue("a", "1");
    	ps3.setValue("b", "1");
    	mtlM.fillBuffer(ps3);
    	
    	rresColl = mtlM.runMonitor();
//    	System.out.println(rresColl.getResult(rSpec1).getTruthValueDescription());
    	assertTrue(Boolean.valueOf(rresColl.getResult(rSpec1).getTruthValueDescription()));
    }
}
