package rithm.driver;
import java.io.File;

import javax.swing.DefaultBoundedRangeModel;

import org.antlr.v4.parse.ANTLRParser.optionsSpec_return;
import org.apache.commons.cli.*;

import rithm.core.DataFactory;
import rithm.core.ParserPlugin;
import rithm.core.PredicateEvaluator;
import rithm.core.ProgState;
import rithm.core.RiTHMMonitor;
import rithm.datatools.CSVDataFactory;
import rithm.datatools.XMLDataFactory;
import rithm.defaultcore.DefaultPredicateEvaluator;
import rithm.defaultcore.ScriptPredicateEvaluator;
import rithm.ltl.LTL3MonValuation;
import rithm.ltl.LTL4MonValuation;
import rithm.ltl.LTL4Monitor;
import rithm.ltl.LTLMonitor;
import rithm.parsertools.ltl.LTLParser;
import rithm.parsertools.ltl.VerboseLTLParser;
public class RiTHMBrewer {
	public static PredicateEvaluator pEvaluator;
	public static String specFile;
	public static String dataFile;
	public static String outputFile;
	public static HelpFormatter hlpFormatter;
	public static DataFactory dFactory;
	public static RiTHMMonitor rithmMon;
	public static ParserPlugin rithmParser;
	public static String pEvaluatorName;
	public static String pEvaluatorPath;
	public static boolean processCmdArguments(CommandLine cmdLine, Options options)
	{
		if(cmdLine.hasOption("help"))
		{
			hlpFormatter.printHelp("RiTHMBrewer", options);
		}
		if(!cmdLine.hasOption("predicateEvaluator"))
		{
			pEvaluator = new DefaultPredicateEvaluator();
		}
		else
		{
			// TODO add code to load Predicate Evaluator using URLLoader
			pEvaluatorName= cmdLine.getOptionValue("predicateEvaluator");
			if(!cmdLine.hasOption("predicateEvaluatorPath"))
			{
				hlpFormatter.printHelp("RiTHMBrewer", options);
				return false;
			}
			pEvaluatorPath = cmdLine.getOptionValue("predicateEvaluatorPath");
			File f = new File(pEvaluatorPath);
			if(!f.exists())
				return false;
			pEvaluator = new ScriptPredicateEvaluator(pEvaluatorPath, null, pEvaluatorName);
		}
		if(cmdLine.hasOption("specFile"))
			specFile = cmdLine.getOptionValue("specFile");
		else
		{
			hlpFormatter.printHelp("RiTHMBrewer", options);
			return false;
		}
		if(cmdLine.hasOption("dataFile"))
			dataFile = cmdLine.getOptionValue("dataFile");
		else
		{
			hlpFormatter.printHelp("RiTHMBrewer", options);
			return false;
		}
		if(cmdLine.hasOption("outputFile"))
			outputFile = cmdLine.getOptionValue("outputFile");
		else
		{
			hlpFormatter.printHelp("RiTHMBrewer", options);
			return false;
		}
		
		if(cmdLine.hasOption("traceParserClass"))
		{
			String tParserClass;
			tParserClass = cmdLine.getOptionValue("traceParserClass");
			if(tParserClass.equals("XML"))
				dFactory = new XMLDataFactory(dataFile);
//			if(tParserClass.equals("CSV"))
//				dFactory =  new CSVDataFactory(dataFile);
		}
		else
		{
			hlpFormatter.printHelp("RiTHMBrewer", options);
			return false;
		}
		if(cmdLine.hasOption("specParserClass"))
		{
			String specParserClass;
			specParserClass = cmdLine.getOptionValue("specParserClass");
			if(specParserClass.equals("LTL"))
				rithmParser = new LTLParser("LTL");
			if(specParserClass.equals("VLTL"))
				rithmParser = new VerboseLTLParser("VLTL");
		}
		else
		{
			hlpFormatter.printHelp("RiTHMBrewer", options);
			return false;
		}
		if(cmdLine.hasOption("monitorClass"))
		{
			String monClass;
			monClass = cmdLine.getOptionValue("monitorClass");
			if(monClass.equals("LTL3"))
			{
				rithmMon = new LTLMonitor();
				rithmMon.setMonitorValuation(new LTL3MonValuation());
			}
			if(monClass.equals("LTL4"))
			{
				rithmMon = new LTL4Monitor();
				rithmMon.setMonitorValuation(new LTL4MonValuation());
			}
			rithmMon.setParser(rithmParser);
			rithmMon.setPredicateEvaluator(pEvaluator);
		}
		else
		{
			hlpFormatter.printHelp("RiTHMBrewer", options);
			return false;
		}
		return true;
	}
	public static void main(String args[])
	{
		Options options = new Options();
		CommandLine cmdLine;
		CommandLineParser parser = new GnuParser();
		
		options.addOption("specFile",true,"File containing specifications to be verified");
		options.addOption("dataFile", true, "Datafile containing trace to be validated");
		options.addOption("outputFile", true, "Monitor output to be written in this file");
		options.addOption("monitorClass", true,"Monitor Plugin name");
		options.addOption("traceParserClass", true,"Trace Parser Plugin name");
		options.addOption("specParserClass", true,"Specification Parser Plugin name");
		options.addOption("help", false,"Help about RiTHM");
		options.addOption("predicateEvaluator", true,"Predicate Evaluator Plugin name");
		options.addOption("predicateEvaluatorPath", true,"Predicate Evaluator File (JAR/JavaScript/Lua) path");
		options.addOption("predicateListFile", true,"Path to file which lists Predicate Line by Line");
		hlpFormatter = new HelpFormatter();
		try
		{
			cmdLine = parser.parse(options, args);
		}
		catch(ParseException pe)
		{
			pe.printStackTrace();
			return;
		}
		if(!processCmdArguments(cmdLine, options))
			return;
		rithmMon.synthesizeMonitors(specFile);
		rithmMon.setOutFile(outputFile);
		
		ProgState pState = dFactory.getNextProgState();
		while( pState != null)
		{
			rithmMon.fillBuffer(pState);
			pState = dFactory.getNextProgState();
		}
		rithmMon.runMonitor();
	}
}
