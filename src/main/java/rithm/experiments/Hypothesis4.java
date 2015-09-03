package rithm.experiments;

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

public class Hypothesis4 {
public static void main(String[] args) {
	FOLTLMonitor foltlmonitor;
	
	/** The rs coll. */
	RitHMSpecificationCollection rsColl;
	
	/** The rres coll. */
	RitHMResultCollection rresColl;
	
	/** The parser. */
	ParserPlugin parser;
    foltlmonitor = new FOLTLMonitor();
	rsColl = new DefaultRiTHMSpecificationCollection();
	parser = new FOLTLParser("Linear Temporal Logic (Past)");
	foltlmonitor.setParser(parser);
	foltlmonitor.setMonitorValuation(new LTL3MonValuation());
	foltlmonitor.setPredicateEvaluator(new DefaultPredicateEvaluator());

	rsColl.clear();
	RitHMSpecification rSpec1 =  new DefaultRiTHMSpecification("FORALL(XOBJ)(a(XOBJ)-><>b(XOBJ))");
	rsColl.add(rSpec1);
	foltlmonitor.synthesizeMonitors(rsColl);
	Random rn = new Random();
	foltlmonitor.parallelMode = Boolean.parseBoolean(args[1]);
	System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", args[2]);
	for(int i= 0; i < Integer.parseInt(args[0]);i++){

		for(int j = 0; j < Integer.parseInt(args[3]); j++)
		{
    		ProgState ps = new DefaultProgramState(i);
    		ps.setValue("XOBJ", Integer.toString(i));
    		ps.setValue("a", Integer.toString(rn.nextInt() % 2));
    		ps.setValue("b", Integer.toString(rn.nextInt() % 2));
    		foltlmonitor.fillBuffer(ps);
		}
//		if(i % 1000000 == 0)
//			System.out.println(i);
	}
	foltlmonitor.runMonitor(false);
}
}
