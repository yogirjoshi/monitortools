package rithm.ltl;

import java.util.ArrayList;
import java.util.HashMap;

import rithm.core.DataFactory;
import rithm.core.ProgState;
import rithm.core.RitHMMonitor;
import rithm.core.RitHMSpecification;
import rithm.core.RitHMTruthValue;
import rithm.datatools.CSVDataFactory;
import rithm.datatools.XMLDataFactory;
import rithm.defaultcore.*;
// TODO: Auto-generated Javadoc

/**
 * The Class MonitorDriver.
 */
public class MonitorDriver {
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		DataFactory dFactory = null;
		if(args.length < 4)
		{
			System.err.println("Usage: MonitorDriver <SpecificationFile> <DataFile> <HtmlOutput> <montype>");
			return;
		}
		if(args[3].contains("XML"))
			dFactory = new XMLDataFactory(args[1]);
		
		if(args[3].contains("CSV"))
			dFactory = new CSVDataFactory(args[1]);
		
//		XMLDataFactory xdFactory = new XMLDataFactory("/home/y2joshi/Input1.xml");
//		XMLDataFactory xdFactory = new XMLDataFactory("/home/y2joshi/TestDataTools.xml");
//		XMLDataFactory xdFactory = new XMLDataFactory("/home/y2joshi/TraceQnxThread.xml");
		RitHMMonitor l;
		l = new LTLMonitor();
		l.setMonitorValuation(new LTL3MonValuation());
		l.setPredicateEvaluator(new DefaultPredicateEvaluator());
		
//		l.SynthesizeMonitors("/home/y2joshi/specqnx");
//		l.setOutFile("/home/y2joshi/out.html");
		
		l.synthesizeMonitors(args[0], true);
		l.setOutFile(args[2]);
		
		ProgState pState = dFactory.getNextProgState();
		while( pState != null)
		{
			l.fillBuffer(pState);
			pState = dFactory.getNextProgState();
		}
		l.runMonitor();
	}
}
