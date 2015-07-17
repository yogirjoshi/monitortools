/*
 * 
 */
package rithm.driver;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;

import org.apache.log4j.Appender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;

import javax.swing.DefaultBoundedRangeModel;

import org.antlr.v4.parse.ANTLRParser.optionsSpec_return;
import org.apache.commons.cli.*;


// TODO: Auto-generated Javadoc
/**
 * The Class RitHMBrewer.
 */
public class RitHMBrewer {

	/** The hlp formatter. */
	public static HelpFormatter hlpFormatter;

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 * @throws InterruptedException the interrupted exception
	 * @throws FileNotFoundException the file not found exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void main(String args[]) throws InterruptedException, FileNotFoundException, IOException
	{
		Options options = new Options();
		CommandLine cmdLine = null;
		RitHMCommandHandler rCmdHandler;
		CommandLineParser parser = new DefaultParser();
		Option specFileOpt = OptionBuilder.withArgName("file")
										  .hasArg()
										  .withDescription("Path to file containing specifications")
										  .create("specFile");
		options.addOption(specFileOpt);
		
		Option specsOpt = OptionBuilder.withArgName("string")
									   .hasArg()
									   .withDescription("Specifications separated by # (if not specified with specFile)")
									   .create("specifications");
		options.addOption(specsOpt);
		
//		options.addOption("specFile",true,"file containing specifications to be verified");
//		options.addOption("specifications",true,"specifications to be verified");
		
		Option dataFileOpt = OptionBuilder.withArgName("file")
				   .hasArg()
				   .withDescription("Path to file containing an execution trace")
				   .create("dataFile");
		options.addOption(dataFileOpt);
//		options.addOption("dataFile", true, "file containing trace to be validated");
		Option dataFormatOpt = OptionBuilder.withArgName("string")
				   .hasArg()
				   .withDescription("Encoding Format of Data (if not using dataFile)")
				   .create("dataFormat");
		options.addOption(dataFormatOpt);
//		options.addOption("dataFormat", true, "format of dataFile (e.g. JSON)");
		Option outputFileOpt = OptionBuilder.withArgName("file")
				   .hasArg()
				   .isRequired(false)
				   .withDescription("Path where Verbose Output of Monitor Verdicts will be written")
				   .create("outputFile");
		options.addOption(outputFileOpt);
//		options.addOption("outputFile", true, "output log of the monitor");
		
		Option monitorClassOpt = OptionBuilder.withArgName("pluginClass")
				   .hasArg()
				   .isRequired(false)
				   .withDescription("Monitor Plugin to be Used (e.g. LTL/MTL/RegEx)")
				   .create("monitorClass");
		options.addOption(monitorClassOpt);
//		options.addOption("monitorClass", true,"monitor plugin name (e.g. LTL/MTL)");
		
		Option traceParserClassOpt = OptionBuilder.withArgName("pluginClass")
				   .hasArg()
				   .withDescription("Parser Plugin for excecution trace (e.g. XML/CSV)")
				   .create("traceParserClass");
		options.addOption(traceParserClassOpt);
//		options.addOption("traceParserClass", true,"trace parser plugin name (e.g. XML/CSV)");
		
		Option specParserClassOpt = OptionBuilder.withArgName("pluginClass")
				   .hasArg()
				   .withDescription("Specification Parser Plugin (e.g. VLTL/LTL/MTL)")
				   .create("specParserClass");
		options.addOption(specParserClassOpt);
//		options.addOption("specParserClass", true,"specification parser plugin name (e.g. VLTL/LTL/MTL)");
		
		Option serverModeOpt = OptionBuilder.hasArg(false)
				   .isRequired(false)
				   .withDescription("Whether to start RiTHM as a server")
				   .create("serverMode");
		options.addOption(serverModeOpt);
		
//		options.addOption("serverMode", true,"Start RiTHM as server (e.g. true/false)");
		Option pipeModeOpt = OptionBuilder.hasArg(false)
				   .isRequired(false)
				   .withDescription("Whether to start RiTHM in pipe mode")
				   .create("pipeMode");
		options.addOption(pipeModeOpt);
		
		Option monEventListenerOpt = OptionBuilder.withArgName("pluginClass")
				   .hasArg()
				   .isRequired(false)
				   .withDescription("A Listener which listens to changes in Monitor's Verdict ")
				   .create("monEventListener");
		options.addOption(monEventListenerOpt);
		
//		options.addOption("pipeMode", true,"Piping between monitors (e.g. true/false)");
		
		Option configFileOpt = OptionBuilder.withArgName("file")
				   .hasArg()
				   .isRequired(false)
				   .withDescription("Path to Configuration file")
				   .create("configFile");
		options.addOption(configFileOpt);
		
//		options.addOption("configFile", true,"file containing configuration parameters");
		options.addOption("help", false,"Help about RiTHM");
		Option predicateEvaluatorTypeOpt = OptionBuilder.withArgName("string")
				   .hasArg()
				   .isRequired(false)
				   .withDescription("Predicate Evaluator Type Name (lua/js)")
				   .create("predicateEvaluatorType");
		options.addOption(predicateEvaluatorTypeOpt);
		
//		options.addOption("predicateEvaluatorType", true,"predicate evaluator type name (lua/js)");
		Option predicateEvaluatorScriptFileOpt = OptionBuilder.withArgName("file")
				   .hasArg()
				   .isRequired(false)
				   .withDescription("Path to Predicate Evaluator Script")
				   .create("predicateEvaluatorScriptFile");
		options.addOption(predicateEvaluatorScriptFileOpt);
		
		Option triggerOpt = OptionBuilder.withArgName("property=value")
				   .hasArgs(2)
				   .isRequired(false)
				   .withDescription("Options to control Monitor Invocation")
				   .create("T");
		options.addOption(triggerOpt);
		
//		options.addOption("predicateEvaluatorScriptFile", true,"predicate evaluator script file path");
		hlpFormatter = new HelpFormatter();
		
		try
		{
			cmdLine = parser.parse(options, args);
		}
		catch(ParseException pe)
		{
			
		}finally{
			
		}
		rCmdHandler = new RitHMCommandHandler(cmdLine, options, hlpFormatter);
		String confFile = rCmdHandler.rValidator.fetchConfigFile();
		if(confFile != null)
			rCmdHandler.rValidator.loadPropFile(confFile);
		
		if(rCmdHandler.rValidator.fetchBoolDualMode("serverMode") || rCmdHandler.rValidator.fetchBoolDualMode("pipeMode"))
		{
			if(confFile == null)
			{
				rCmdHandler.rValidator.printHelp();
				System.err.println("serverMode/pipeMode should be used in configFile ONLY");
				return;
			}
			RitHMSecureServer rsRiTHMSecureServer = new RitHMSecureServer(confFile);
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
