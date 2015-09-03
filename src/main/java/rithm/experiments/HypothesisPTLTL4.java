package rithm.experiments;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import rithm.core.ParserPlugin;
import rithm.core.ProgState;
import rithm.core.RitHMResultCollection;
import rithm.core.RitHMSpecification;
import rithm.core.RitHMSpecificationCollection;
import rithm.defaultcore.DefaultPredicateEvaluator;
import rithm.defaultcore.DefaultProgramState;
import rithm.defaultcore.DefaultRiTHMSpecification;
import rithm.defaultcore.DefaultRiTHMSpecificationCollection;
import rithm.ltl.LTL3MonValuation;
import rithm.ltl.LTLMonitor;
import rithm.ltl.PTLTLMonitor;
import rithm.mtl.TwoValuedValuation;
import rithm.parsertools.ltl.LTLParser;
import rithm.parsertools.ptltl.PTLTLParser;

public class HypothesisPTLTL4 {
	public static void main(String[] args) throws InterruptedException {
		PTLTLMonitor ptltlM;
		LTLMonitor ltlM;
		/** The rs coll. */
		RitHMSpecificationCollection rsColl1,rsColl2;
		
		/** The rres coll. */
		RitHMResultCollection rresColl1,rresColl2;
		
		/** The parser. */
		ParserPlugin parser1, parser2;
		
		ptltlM = new PTLTLMonitor();
		ltlM = new LTLMonitor();
    	rsColl1 = new DefaultRiTHMSpecificationCollection();
    	rsColl2 = new DefaultRiTHMSpecificationCollection();
    	parser1 = new PTLTLParser("Linear Temporal Logic (Past)");
    	parser2 = new LTLParser("Linear Temporal Logic");
//    	
    	ptltlM.setOutFile("test31.log");
    	ptltlM.setPlotFile("test41.log");
    	ptltlM.setParser(parser1);
       	ptltlM.setMonitorValuation(new TwoValuedValuation());
    	ptltlM.setPredicateEvaluator(new DefaultPredicateEvaluator());
//    	
    	ltlM.setOutFile("test32.log");
    	ltlM.setPlotFile("test42.log");
    	ltlM.setParser(parser2);
    	ltlM.setMonitorValuation(new LTL3MonValuation());
    	ltlM.setPredicateEvaluator(new DefaultPredicateEvaluator());
    	
    	int buffSize = Integer.parseInt(args[1]);
    	Random rnd = new Random();
    	
    	RitHMSpecification rSpec11 =  new DefaultRiTHMSpecification("[*](a2->(a1&&!(X*a1)))");
    	RitHMSpecification rSpec12 =  new DefaultRiTHMSpecification("[*](a3->(a2&&!(X*a2)))");
    	RitHMSpecification rSpec13 =  new DefaultRiTHMSpecification("[*](a4->(a3&&!(X*a3)))");
    	RitHMSpecification rSpec14 =  new DefaultRiTHMSpecification("[*](a5->(a3&&!(X*a4)))");
    	RitHMSpecification rSpec15 =  new DefaultRiTHMSpecification("[*](a6->(a3&&!(X*a5)))");
    	RitHMSpecification rSpec21 =  new DefaultRiTHMSpecification("[](Xa2->(X a1 && ! a1))");
    	RitHMSpecification rSpec22 =  new DefaultRiTHMSpecification("[](Xa3->(X a2 && ! a2))");
    	RitHMSpecification rSpec23 =  new DefaultRiTHMSpecification("[](Xa4->(X a3 && ! a3))");
    	RitHMSpecification rSpec24 =  new DefaultRiTHMSpecification("[](Xa5->(X a4 && ! a4))");
    	RitHMSpecification rSpec25 =  new DefaultRiTHMSpecification("[](Xa6->(X a5 && ! a5))");
    	rsColl1.add(rSpec11);
    	rsColl1.add(rSpec12);
    	rsColl1.add(rSpec13);
    	rsColl1.add(rSpec14);
    	rsColl1.add(rSpec15);
    	rsColl2.add(rSpec21);
    	rsColl2.add(rSpec22);
    	rsColl2.add(rSpec23);
    	rsColl2.add(rSpec24);
    	rsColl2.add(rSpec25);
    	ptltlM.synthesizeMonitors(rsColl1);
    	ltlM.synthesizeMonitors(rsColl2);
    	long beg, end;
//    	for(int t = 0;t < Integer.parseInt(args[2]);t++){
    	beg = System.nanoTime();
    	switch (args[0]) {
    	case "LTL":
    		for(int i =0; i < buffSize;i++){
    			ProgState ps = new DefaultProgramState(i);
    			ps.setValue("a1", Integer.toString(rnd.nextInt(10)%2));
    			ps.setValue("a2", Integer.toString(rnd.nextInt(10)%2));
    			ps.setValue("a3", Integer.toString(rnd.nextInt(10)%2));
    			ps.setValue("a4", Integer.toString(rnd.nextInt(10)%2));
    			ps.setValue("a5", Integer.toString(rnd.nextInt(10)%2));
    			ps.setValue("a6", Integer.toString(rnd.nextInt(10)%2));
    			//	        	ptltlM.fillBuffer(ps);
    			ltlM.fillBuffer(ps);
    			rresColl2 = ltlM.runMonitor(true);
    			ltlM.clearBuffer();
    		}

    		break;
    	case "PTLTL":
    		for(int i =0; i < buffSize;i++){
    			ProgState ps = new DefaultProgramState(i);
    			ps.setValue("a1", Integer.toString(rnd.nextInt(10)%2));
    			ps.setValue("a2", Integer.toString(rnd.nextInt(10)%2));
    			ps.setValue("a3", Integer.toString(rnd.nextInt(10)%2));
    			ps.setValue("a4", Integer.toString(rnd.nextInt(10)%2));
    			ps.setValue("a5", Integer.toString(rnd.nextInt(10)%2));
    			ps.setValue("a6", Integer.toString(rnd.nextInt(10)%2));
    			ptltlM.fillBuffer(ps);
    			//	        	ltlM.fillBuffer(ps);
    			rresColl1 = ptltlM.runMonitor(true);
    			ptltlM.clearBuffer();
    		}
    	default:
    		break;
    	}
    	end = System.nanoTime();
//    	System.out.println(avg/Integer.parseInt(args[2]));
	    System.out.println("Exec Time "+ args[0] + ":" +  TimeUnit.MILLISECONDS.convert((end-beg),TimeUnit.NANOSECONDS));
	}
}
