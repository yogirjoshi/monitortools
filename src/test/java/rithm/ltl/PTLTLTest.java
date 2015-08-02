package rithm.ltl;

import rithm.core.ParserPlugin;
import rithm.core.ProgState;
import rithm.core.RitHMResultCollection;
import rithm.core.RitHMSpecification;
import rithm.core.RitHMSpecificationCollection;
import rithm.defaultcore.DefaultPredicateEvaluator;
import rithm.defaultcore.DefaultProgramState;
import rithm.defaultcore.DefaultRiTHMSpecification;
import rithm.defaultcore.DefaultRiTHMSpecificationCollection;
import rithm.mtl.MTLMonitor;
import rithm.mtl.MTLTest;
import rithm.mtl.TwoValuedValuation;
import rithm.parsertools.mtl.MTLParser;
import rithm.parsertools.ptltl.PTLTLParser;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class PTLTLTest extends TestCase{
	/** The mtl m. */
	protected PTLTLMonitor ptltlM;
	
	/** The rs coll. */
	protected RitHMSpecificationCollection rsColl;
	
	/** The rres coll. */
	RitHMResultCollection rresColl;
	
	/** The parser. */
	ParserPlugin parser;
    
    /**
     * Instantiates a new MTL test.
     *
     * @param testName the test name
     */
    public PTLTLTest(String testName )
    {
        super( testName );
    	ptltlM = new PTLTLMonitor();
    	rsColl = new DefaultRiTHMSpecificationCollection();
    	parser = new PTLTLParser("Linear Temporal Logic (Past)");
    	ptltlM.setOutFile("test3.log");
    	ptltlM.setPlotFile("test4.log");
    	ptltlM.setParser(parser);
       	ptltlM.setMonitorValuation(new TwoValuedValuation());
    	ptltlM.setPredicateEvaluator(new DefaultPredicateEvaluator());
    }

    /**
     * Suite.
     *
     * @return the suite of tests being tested
     */
    public static Test suite()
    {	
        return new TestSuite( PTLTLTest.class );
    }

    /**
     * Rigourous Test :-).
     */
    public void testTemporalBooleanCombination1()
    {
    	RitHMSpecification rSpec1 =  new DefaultRiTHMSpecification("[*](<*>b-><*>a)");
//    	RitHMSpecification rSpec2 =  new DefaultRiTHMSpecification("[*](<*>b-><*>a)");
//    	rsColl.add(rSpec2);
    	rsColl.add(rSpec1);

    	ptltlM.synthesizeMonitors(rsColl);
    	
    	ProgState ps1 = new DefaultProgramState(0);
    	ps1.setValue("a", "0");
    	ps1.setValue("b", "1");
    	ptltlM.fillBuffer(ps1);
    	
    	ProgState ps2 = new DefaultProgramState(1);
    	ps2.setValue("a", "0");
    	ps2.setValue("b", "0");
    	ptltlM.fillBuffer(ps2);
    	
    	ProgState ps3 = new DefaultProgramState(2);
    	ps3.setValue("a", "0");
    	ps3.setValue("b", "1");
    	ptltlM.fillBuffer(ps3);
    	
    	rresColl = ptltlM.runMonitor(true);
    	System.out.println(rresColl.getResult(rSpec1).getTruthValueDescription());
    	assertTrue(Boolean.valueOf(rresColl.getResult(rSpec1).getTruthValueDescription()));
    }
    
//    /**
//     * Test globally nested1.
//     */
//    public void testGloballyNested1()
//    {
//    	RitHMSpecification rSpec1 =  new DefaultRiTHMSpecification("[]{1,2}(<>{0,1}a)");
//    	rsColl.add(rSpec1);
//    	ptltlM.synthesizeMonitors(rsColl);
//    	
//    	ProgState ps1 = new DefaultProgramState(0);
//    	ps1.setValue("a", "0");
//    	ptltlM.fillBuffer(ps1);
//    	
//    	ProgState ps2 = new DefaultProgramState(1);
//    	ps2.setValue("a", "0");
//    	ptltlM.fillBuffer(ps2);
//    	
//    	ProgState ps3 = new DefaultProgramState(2);
//    	ps3.setValue("a", "1");
//    	ptltlM.fillBuffer(ps3);
//    	
//    	rresColl = ptltlM.runMonitor(true);
//    	assertTrue(Boolean.valueOf(rresColl.getResult(rSpec1).getTruthValueDescription()));
//    }
//    
//    /**
//     * Test globally simle1.
//     */
//    public void testGloballySimle1()
//    {
//    	RitHMSpecification rSpec1 =  new DefaultRiTHMSpecification("[]{1,2}a");
//    	rsColl.add(rSpec1);
//    	ptltlM.synthesizeMonitors(rsColl);
//    	
//    	ProgState ps1 = new DefaultProgramState(0);
//    	ps1.setValue("a", "0");
//    	ptltlM.fillBuffer(ps1);
//    	
//    	ProgState ps2 = new DefaultProgramState(1);
//    	ps2.setValue("a", "1");
//    	ptltlM.fillBuffer(ps2);
//    	
//    	ProgState ps3 = new DefaultProgramState(2);
//    	ps3.setValue("a", "1");
//    	ptltlM.fillBuffer(ps3);
//    	
//    	rresColl = ptltlM.runMonitor(true);
//    	assertTrue(Boolean.valueOf(rresColl.getResult(rSpec1).getTruthValueDescription()));
//    }
//    
//    /**
//     * Test eventually nested2.
//     */
//    public void testEventuallyNested2()
//    {
//    	RitHMSpecification rSpec1 = new DefaultRiTHMSpecification("aU{0,1}(<>{1,1}b)");
//    	rsColl.add(rSpec1);
//    	ptltlM.synthesizeMonitors(rsColl);
//    	
//    	ProgState ps1= new DefaultProgramState(0);
//    	ps1.setValue("a", "1");
//    	ps1.setValue("b", "0");
//    	ptltlM.fillBuffer(ps1);
////    	System.out.println(ps1);
//    	
//    	ProgState ps2= new DefaultProgramState(1);
//    	ps2.setValue("a", "0");
//    	ps2.setValue("b", "0");
//    	ptltlM.fillBuffer(ps2);
//    	
//    	ProgState ps3= new DefaultProgramState(2);
//    	ps3.setValue("b", "1");
//    	ps3.setValue("a", "0");
//    	ptltlM.fillBuffer(ps3);
//    	
//    	rresColl = ptltlM.runMonitor(true);
//    	assertTrue(Boolean.valueOf(rresColl.getResult(rSpec1).getTruthValueDescription()));
//    }
//    
//    /**
//     * Test eventually nested1.
//     */
//    public void testEventuallyNested1()
//    {
//    	RitHMSpecification rSpec1 = new DefaultRiTHMSpecification("<>{0,1}(<>{1,5}b)");
//    	rsColl.add(rSpec1);
//    	ptltlM.synthesizeMonitors(rsColl);
//    	
//    	ProgState ps1= new DefaultProgramState(0);
//    	ps1.setValue("b", "0");
//    	ptltlM.fillBuffer(ps1);
////    	System.out.println(ps1);
//    	
//    	ProgState ps2= new DefaultProgramState(1);
//    	ps2.setValue("b", "0");
//    	ptltlM.fillBuffer(ps2);
//    	
//    	ProgState ps3= new DefaultProgramState(2);
//    	ps3.setValue("b", "1");
//    	ptltlM.fillBuffer(ps3);
//    	
//    	rresColl = ptltlM.runMonitor(true);
//    	assertTrue(Boolean.valueOf(rresColl.getResult(rSpec1).getTruthValueDescription()));
//    }
//    
//    /**
//     * Test eventually basic1.
//     */
//    public void testEventuallyBasic1()
//    {
//    	RitHMSpecification rSpec1 = new DefaultRiTHMSpecification("<>{1,5}b");
//    	rsColl.add(rSpec1);
//    	ptltlM.synthesizeMonitors(rsColl);
//    	
//    	ProgState ps1= new DefaultProgramState(0);
//    	ps1.setValue("b", "1");
//    	ptltlM.fillBuffer(ps1);
////    	System.out.println(ps1);
//    	
//    	ProgState ps2= new DefaultProgramState(1);
//    	ps2.setValue("b", "0");
//    	ptltlM.fillBuffer(ps2);
//    	
//    	ProgState ps3= new DefaultProgramState(2);
//    	ps3.setValue("b", "1");
//    	ptltlM.fillBuffer(ps3);
//    	
//    	rresColl = ptltlM.runMonitor(true);
//    	assertTrue(Boolean.valueOf(rresColl.getResult(rSpec1).getTruthValueDescription()));
//    }
//    
//    /**
//     * Test until basic1.
//     */
//    public void testUntilBasic1()
//    {
//
//    	
//    	RitHMSpecification rSpec1 = new DefaultRiTHMSpecification("aU{1,2}b");
//    	rsColl.add(rSpec1);
////    	mtlM.setFormulas(rsColl);
//    	ptltlM.synthesizeMonitors(rsColl);
//    	
//    	ProgState ps1= new DefaultProgramState(0);
//    	ps1.setValue("a", "1");
//    	ps1.setValue("b", "0");
//    	ptltlM.fillBuffer(ps1);
////    	System.out.println(ps1);
//    	
//    	ProgState ps2= new DefaultProgramState(1);
//    	ps2.setValue("a", "1");
//    	ps2.setValue("b", "0");
//    	ptltlM.fillBuffer(ps2);
//    	
//    	ProgState ps3= new DefaultProgramState(2);
//    	ps3.setValue("a", "1");
//    	ps3.setValue("b", "1");
//    	ptltlM.fillBuffer(ps3);
//    	
//    	rresColl = ptltlM.runMonitor(true);
////    	System.out.println(rresColl.getResult(rSpec1).getTruthValueDescription());
//    	assertTrue(Boolean.valueOf(rresColl.getResult(rSpec1).getTruthValueDescription()));
//    }
//    
//    /**
//     * Test eventually pase basic1.
//     */
//    public void testEventuallyPaseBasic1()
//    {
//
//    	
//    	RitHMSpecification rSpec1 = new DefaultRiTHMSpecification("[]{0,2}(a-><*>{0,2}b)");
//    	rsColl.add(rSpec1);
////    	mtlM.setFormulas(rsColl);
//    	ptltlM.synthesizeMonitors(rsColl);
//    	
//    	ProgState ps1= new DefaultProgramState(0);
//    	ps1.setValue("a", "1");
//    	ps1.setValue("b", "1");
//    	ptltlM.fillBuffer(ps1);
////    	System.out.println(ps1);
//    	
//    	ProgState ps2= new DefaultProgramState(1);
//    	ps2.setValue("a", "1");
//    	ps2.setValue("b", "0");
//    	ptltlM.fillBuffer(ps2);
//    	
//    	ProgState ps3= new DefaultProgramState(2);
//    	ps3.setValue("a", "1");
//    	ps3.setValue("b", "0");
//    	ptltlM.fillBuffer(ps3);
//    	
//    	rresColl = ptltlM.runMonitor(true);
////    	System.out.println(rresColl.getResult(rSpec1).getTruthValueDescription());
//    	assertTrue(Boolean.valueOf(rresColl.getResult(rSpec1).getTruthValueDescription()));
//    }
}
