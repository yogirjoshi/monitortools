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

public class Hypothesis5 {
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
	RitHMSpecification rSpec1 =  new DefaultRiTHMSpecification("FORALL(YOBJ)FORALL(XOBJ)[](a(XOBJ)->[](b(XOBJ)-><>c(XOBJ)))");
	rsColl.add(rSpec1);
	foltlmonitor.synthesizeMonitors(rsColl);
	foltlmonitor.parallelMode = Boolean.parseBoolean(args[1]);
	Random rn = new  Random();
	System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", args[2]);
	for(int i= 0; i < Integer.parseInt(args[0]);i++){

		for(int j = 0; j < Integer.parseInt(args[3]); j++)
		{
    		ProgState ps = new DefaultProgramState(i);
    		ps.setValue("YOBJ", Integer.toString(rn.nextInt() % 10));
    		ps.setValue("XOBJ", Integer.toString(i));
    		ps.setValue("a", Integer.toString(rn.nextInt() % 2));
    		ps.setValue("b", Integer.toString(rn.nextInt() % 2));
    		ps.setValue("C", Integer.toString(rn.nextInt() % 2));
    		foltlmonitor.fillBuffer(ps);
		}
//		if(i % 1000000 == 0)
//			System.out.println(i);
	}
	foltlmonitor.runMonitor(false);
}
}
