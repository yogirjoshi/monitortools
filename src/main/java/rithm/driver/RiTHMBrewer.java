package rithm.driver;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

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
import rithm.mtl.MTLMonitor;
import rithm.mtl.TwoValuedValuation;
import rithm.parsertools.ltl.LTLParser;
import rithm.parsertools.ltl.VerboseLTLParser;
import rithm.parsertools.mtl.MTLParser;

public class RiTHMBrewer {

	public static HelpFormatter hlpFormatter;

	public static void main(String args[]) throws InterruptedException, FileNotFoundException, IOException
	{
		Options options = new Options();
		CommandLine cmdLine;
		CommandLineParser parser = new GnuParser();
		
		options.addOption("specFile",true,"file containing specifications to be verified");
		options.addOption("specifications",true,"specifications to be verified");
		options.addOption("dataFile", true, "file containing trace to be validated");
		options.addOption("dataFormat", true, "format of dataFile (e.g. JSON)");
		options.addOption("outputFile", true, "output log of the monitor");
		options.addOption("monitorClass", true,"monitor plugin name (e.g. LTL/MTL)");
		options.addOption("traceParserClass", true,"trace parser plugin name (e.g. XML/CSV)");
		options.addOption("specParserClass", true,"specification parser plugin name (e.g. LTL/MTL)");
		options.addOption("serverMode", true,"Start RiTHM as server (e.g. true/false)");
		options.addOption("pipeMode", true,"Piping between monitors (e.g. true/false)");
		options.addOption("configFile", true,"file containing configuration parameters");
		options.addOption("help", false,"Help about RiTHM");
		options.addOption("predicateEvaluatorType", true,"predicate evaluator type name (lua/js)");
		options.addOption("predicateEvaluatorScriptFile", true,"predicate evaluator script file path");
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
		RiTHMCommandHandler rCmdHandler = new RiTHMCommandHandler(cmdLine, options, hlpFormatter);
		String confFile = rCmdHandler.rValidator.fetchConfigFile();
		if(confFile != null)
			rCmdHandler.rValidator.loadPropFile(confFile);
		if(rCmdHandler.rValidator.fetchBoolDualMode("serverMode") || rCmdHandler.rValidator.fetchBoolDualMode("pipeMode"))
		{
			if(confFile == null)
			{
				hlpFormatter.printHelp("RiTHMBrewer","RiTHM Options",options,"Check RiTHMLog for more log messages",true);
				System.err.println("serverMode/pipeMode should be used with configFile ONLY");
				return;
			}
			RiTHMSecureServer rsRiTHMSecureServer = new RiTHMSecureServer(confFile);
			rsRiTHMSecureServer.start();
			rsRiTHMSecureServer.join();
		}
		else
		{
			rCmdHandler.run();
			rCmdHandler.join();
		}

	}
}
