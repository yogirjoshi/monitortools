package rithm.foltl;

import java.util.Random;

import rithm.core.ParserPlugin;
import rithm.core.ProgState;
import rithm.core.RitHMResultCollection;
import rithm.core.RitHMSpecification;
import rithm.core.RitHMSpecificationCollection;
import rithm.defaultcore.DefaultPredicateEvaluator;
import rithm.defaultcore.DefaultProgramState;
import rithm.defaultcore.DefaultRiTHMSpecification;
import rithm.defaultcore.DefaultRiTHMSpecificationCollection;
import rithm.ltl.FOLTLMonitor;
import rithm.ltl.LTL3MonValuation;
import rithm.parsertools.foltl.FOLTLParser;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class FOLTLTest extends TestCase{
	/** The mtl m. */
	protected FOLTLMonitor foltlmonitor;
	
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
    public FOLTLTest(String testName )
    {
        super( testName );
        foltlmonitor = new FOLTLMonitor();
    	rsColl = new DefaultRiTHMSpecificationCollection();
    	parser = new FOLTLParser("Linear Temporal Logic (Past)");
    	foltlmonitor.setParser(parser);
    	foltlmonitor.setMonitorValuation(new LTL3MonValuation());
    	foltlmonitor.setPredicateEvaluator(new DefaultPredicateEvaluator());
    }

    /**
     * Suite.
     *
     * @return the suite of tests being tested
     */
    public static Test suite()
    {	
        return new TestSuite( FOLTLTest.class );
    }
    public void testBig()
    {
    	foltlmonitor.parallelMode = true;
    	rsColl.clear();
    	RitHMSpecification rSpec1 =  new DefaultRiTHMSpecification("FORALL(XOBJ)(a(XOBJ)-><>b(XOBJ))");
    	rsColl.add(rSpec1);
    	foltlmonitor.synthesizeMonitors(rsColl);
    	Random rn = new Random();
    	for(int i= 0; i < 200;i++){

    		for(int j = 0; j < 2; j++)
    		{
        		ProgState ps = new DefaultProgramState(i);
        		ps.setValue("XOBJ", Integer.toString(i));
        		ps.setValue("a", Integer.toString(rn.nextInt() % 2));
        		ps.setValue("b", Integer.toString(rn.nextInt() % 2));
        		foltlmonitor.fillBuffer(ps);
    		}
    	}
    	foltlmonitor.runMonitor(false);
    }
    /**
     * Rigourous Test :-).
     */
//    public void testBufferFOLTL()
//    {
//    	RitHMSpecification rSpec1 =  new DefaultRiTHMSpecification("EXISTS(YOBJ)FORALL(XOBJ)(a(XOBJ)-><>b(XOBJ))");
////    	RitHMSpecification rSpec2 =  new DefaultRiTHMSpecification("[*](<*>b-><*>a)");
////    	rsColl.add(rSpec2);
//    	rsColl.add(rSpec1);
//
//    	foltlmonitor.synthesizeMonitors(rsColl);
//    	
//    	ProgState ps1 = new DefaultProgramState(0);
//    	ps1.setValue("a", "1");
//    	ps1.setValue("b", "0");
//    	ps1.setValue("YOBJ", "1");
//    	ps1.setValue("XOBJ", "1");
//    	foltlmonitor.fillBuffer(ps1);
//    	
//    	ProgState ps4 = new DefaultProgramState(0);
//    	ps4.setValue("a", "0");
//    	ps4.setValue("b", "1");
//    	ps4.setValue("YOBJ", "1");
//    	ps4.setValue("XOBJ", "1");
//    	foltlmonitor.fillBuffer(ps4);
//    	
//    	ProgState ps2 = new DefaultProgramState(1);
//    	ps2.setValue("a", "1");
//    	ps2.setValue("b", "1");
//    	ps2.setValue("YOBJ", "1");
//    	ps2.setValue("XOBJ", "2");
//    	foltlmonitor.fillBuffer(ps2);
//    	
//    	ProgState ps3 = new DefaultProgramState(2);
//    	ps3.setValue("a", "0");
//    	ps3.setValue("b", "1");
//    	ps3.setValue("YOBJ", "2");
//    	ps3.setValue("XOBJ", "3");
//    	foltlmonitor.fillBuffer(ps3);
//    	foltlmonitor.runMonitor(false);
////    	rresColl = foltlmonitor.runMonitor(true);
////    	System.out.println(rresColl.getResult(rSpec1).getTruthValueDescription());
////    	assertTrue(Boolean.valueOf(rresColl.getResult(rSpec1).getTruthValueDescription()));
//    }
//    
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
