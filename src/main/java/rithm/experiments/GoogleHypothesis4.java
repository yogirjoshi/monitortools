package rithm.experiments;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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

public class GoogleHypothesis4 {
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
		RitHMSpecification rSpec1 =  new DefaultRiTHMSpecification("FORALL(XOBJ)[](! a(XOBJ))");
		rsColl.add(rSpec1);
		foltlmonitor.synthesizeMonitors(rsColl);
		Random rn = new Random();
		foltlmonitor.parallelMode = Boolean.parseBoolean(args[1]);
		System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", args[2]);
		long count = 0;
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(System.in));
			String line;

			while ((line = br.readLine()) != null) {
				boolean uknown = false;	
				String csved[] = line.split(",");
				String jobid = csved[2], taskId = csved[3];
				ProgState ps = new DefaultProgramState(count++);
				ps.setValue("XOBJ", jobid+"-"+taskId);
				if(csved[5].equals("3"))
					ps.setValue("a", "1");
				else
					ps.setValue("a", "0");
		
				foltlmonitor.fillBuffer(ps);
				int objCount = foltlmonitor.getNoOfObjects(rSpec1) ;
				if(foltlmonitor.getNoOfObjects(rSpec1) >= Integer.parseInt(args[0]))
					break;
//				if(objCount % 1000000 == 0)
//					System.out.println(objCount);
			}
		}catch(IOException ie){
			System.err.println(ie.getMessage());
		}
		foltlmonitor.runMonitor(false);
	}
}