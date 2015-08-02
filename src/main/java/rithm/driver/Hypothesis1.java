package rithm.driver;

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
import rithm.ltl.LTL3MonValuation;
import rithm.ltl.LTLMonitor;
import rithm.ltl.PTLTLMonitor;
import rithm.mtl.TwoValuedValuation;
import rithm.parsertools.ltl.LTLParser;
import rithm.parsertools.ptltl.PTLTLParser;

public class Hypothesis1 {
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
    	for(int i =0; i < buffSize;i++){
        	ProgState ps = new DefaultProgramState(i);
    		for(int j = 0; j < 10; j++){
            	ps.setValue("a"+j, Integer.toString(rnd.nextInt(1)));
    		}
        	ptltlM.fillBuffer(ps);
        	ltlM.fillBuffer(ps);
    	}
    	
    	RitHMSpecification rSpec11 =  new DefaultRiTHMSpecification("[*](a2->(a1&&!(X*a1)))");
    	RitHMSpecification rSpec12 =  new DefaultRiTHMSpecification("[*](a3->(a2&&!(X*a2)))");
    	RitHMSpecification rSpec13 =  new DefaultRiTHMSpecification("[*](a4->(a3&&!(X*a3)))");
    	RitHMSpecification rSpec21 =  new DefaultRiTHMSpecification("[](Xa2->(Xa1&&!a1))");
    	RitHMSpecification rSpec22 =  new DefaultRiTHMSpecification("[](Xa3->(Xa2&&!a2))");
    	RitHMSpecification rSpec23 =  new DefaultRiTHMSpecification("[](Xa4->(Xa3&&!a3))");
    	rsColl1.add(rSpec11);
    	rsColl1.add(rSpec12);
    	rsColl1.add(rSpec13);
    	rsColl2.add(rSpec21);
    	rsColl2.add(rSpec22);
    	rsColl2.add(rSpec23);
    	ptltlM.synthesizeMonitors(rsColl1);
    	ltlM.synthesizeMonitors(rsColl2);
    	long beg, end, avg = 0;
    	for(int t = 0;t < Integer.parseInt(args[2]);t++){

	    	beg = System.nanoTime();
	    	switch (args[0]) {
			case "LTL":
		    	for(int i =0; i < buffSize;i++){
		        	ProgState ps = new DefaultProgramState(i);
		    		for(int j = 0; j < 10; j++){
		            	ps.setValue("a"+j, Integer.toString(rnd.nextInt(1)));
		    		}
	//	        	ptltlM.fillBuffer(ps);
		        	ltlM.fillBuffer(ps);
					rresColl2 = ltlM.runMonitor(true);
					ltlM.clearBuffer();
		    	}
	
				break;
			case "PTLTL":
				for(int i =0; i < buffSize;i++){
		        	ProgState ps = new DefaultProgramState(i);
		    		for(int j = 0; j < 10; j++){
		            	ps.setValue("a"+j, Integer.toString(rnd.nextInt(1)));
		    		}
		        	ptltlM.fillBuffer(ps);
	//	        	ltlM.fillBuffer(ps);
					rresColl1 = ptltlM.runMonitor(true);
					ptltlM.clearBuffer();
		    	}
			default:
				break;
			}
	    	end = System.nanoTime();
	    	avg = avg +  end-beg;
    		Thread.sleep(Integer.parseInt(args[3]));
    	}
    	System.out.println(avg/Integer.parseInt(args[2]));
	}
}
