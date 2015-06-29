package rithm.driver;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Array;
import java.net.Socket;
import java.security.spec.PSSParameterSpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

import rithm.commands.RiTHMMonitorCommand;
import rithm.commands.RiTHMParameters;
import rithm.commands.RiTHMReplyCommand;
import rithm.commands.RiTHMSetupCommand;
import rithm.commands.RiTHMParameterValidator;
import rithm.core.DataFactory;
import rithm.core.ParserPlugin;
import rithm.core.PredicateEvaluator;
import rithm.core.PredicateState;
import rithm.core.ProgState;
import rithm.core.RiTHMMonitor;
import rithm.core.RiTHMResultCollection;
import rithm.core.RiTHMSpecification;
import rithm.core.RiTHMTruthValue;
import rithm.datatools.CSVDataFactory;
import rithm.datatools.XMLDataFactory;
import rithm.defaultcore.DefaultPredicateEvaluator;
import rithm.defaultcore.DefaultProgramState;
import rithm.defaultcore.DefaultRiTHMSpecification;
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

import javax.net.ssl.SSLSocket;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.log4j.Logger;

import com.google.gson.Gson;

public class RiTHMCommandHandler extends Thread {
	
	public RiTHMParameterValidator rValidator;
	
	protected RiTHMResultCollection rRes;
	protected RiTHMParameters rtParams;	
	protected DataFactory dFactory;
	protected ParserPlugin rithmParser;
	protected RiTHMMonitor rithmMon;
	protected PredicateEvaluator pEvaluator = null;

	protected boolean pipeMode = false;
	protected boolean specsFromFile;
	protected boolean isProcessingDatafile;
	protected CommandLine cmdLine;
	protected Options options;
	protected HelpFormatter hlpFormatter;
	
	protected ArrayList<ArrayList<RiTHMSpecification>> specPipes;
	protected HashMap<RiTHMSpecification, String> resSpecMap;
	protected HashMap<RiTHMSpecification, String> specParserMap;
	protected HashMap<RiTHMSpecification, String> specMonMap;
	protected HashMap<RiTHMSpecification,String> specPredEvalTypeMap;
	protected HashMap<RiTHMSpecification, String> specPredEvalPathMap;
	
	final static Logger logger = Logger.getLogger(RiTHMCommandHandler.class);

	public RiTHMCommandHandler(CommandLine cmdLine, Options options,HelpFormatter hlpFormatter)
	{
		this.cmdLine = cmdLine;
		this.options = options;
		this.hlpFormatter = hlpFormatter;
		rValidator = new RiTHMParameterValidator((short)2);
		rValidator.setCmdOptions(cmdLine, options, hlpFormatter);
	}
	protected void setUpMonInstance(boolean isProcessingDatafile, 
									  String dataFile,
									  String specParserClass,
									  String monitorClass,
									  String traceParserClass,
									  String pEvaluatorName,
									  String pEvaluatorPath
									  )
	{
		if(isProcessingDatafile){
			switch (traceParserClass) {
			case "XML":
				logger.debug("Created DataFactory for " + dataFile);
				dFactory = new XMLDataFactory(rtParams.dataFile);
				break;
			case "CSV":
				dFactory = new CSVDataFactory(rtParams.dataFile);
				break;
			default:
				break;
			}
		}
		else
			logger.info("Data to be received on socket as "+ rtParams.dataFormat);
		
		switch (specParserClass) {
		case "LTL":
			rithmParser = new LTLParser("LTL");
			break;
		case "VLTL":
			rithmParser = new VerboseLTLParser("Verbose LTL");
			break;
		case "MTL":
			rithmParser = new MTLParser("MTL");
			break;
		}

		switch (monitorClass) {
		case "LTL3":
			rithmMon = new LTLMonitor();
			rithmMon.setMonitorValuation(new LTL3MonValuation());
			break;
		case "LTL4":
			rithmMon = new LTL4Monitor();
			rithmMon.setMonitorValuation(new LTL4MonValuation());
			break;
		case "MTL":	
			rithmMon = new MTLMonitor();
			rithmMon.setMonitorValuation(new TwoValuedValuation());

			break;
		default:
			break;
		}
		if(pipeMode){
			rithmMon.setpipeMode(true);
		}
		if(pEvaluatorName != null)
			switch (pEvaluatorName) {
			case "lua":	
				pEvaluator = 
				new ScriptPredicateEvaluator(pEvaluatorPath, "luaj", false);
				break;
			case "js":
				pEvaluator = 
				new ScriptPredicateEvaluator(pEvaluatorPath, "JavaScript", false);
				break;
			default:
				break;
			}
	}
	public boolean processCommand()
	{
		if(rtParams.pipeCount > 0)
			pipeMode = true;
		
		if(!pipeMode){
			setUpMonInstance(rtParams.isProcessingDatafile,
							 rtParams.dataFile,
							 rtParams.specParserClass,
							 rtParams.monitorClass,  
							 rtParams.traceParserClass,   
							 rtParams.pEvaluatorName, 
							 rtParams.pEvaluatorPath  
							 );
			runMonitor(rtParams.outFileName, rtParams.specsFromFile, true,rtParams.outFileName, null);
		}else{
			specPipes = new ArrayList<ArrayList<RiTHMSpecification>>();
			resSpecMap = new HashMap<>();
			specMonMap = new HashMap<>();
			specParserMap = new HashMap<>();   
			specPredEvalTypeMap = new HashMap<>();
			specPredEvalPathMap = new HashMap<>();
			int j = 0;
			for(int i = 0; i < rtParams.specsForPipes.size();i++)
			{
				String specsForthisPipe = rtParams.specsForPipes.get(i);
				String []specsAsString = specsForthisPipe.split("#");
				String []parsersForSpecs= rtParams.parsersForPipes.get(i).split("#");
				String []monsForSpecs = rtParams.monitorsForPipes.get(i).split("#");
				String predType = rtParams.predEvalNamesrPipes.get(i);
				String predScriptPath = rtParams.predEvalsForPipes.get(i);
				
				ArrayList<RiTHMSpecification> specListthisPipe = new ArrayList<>();
				j = 0;
				for(String specWithRes: Arrays.asList(specsAsString))
				{
					String []splitByeq = specWithRes.split("=");
					if(splitByeq.length != 2)
					{
						logger.fatal("In-valid syntax " + specWithRes);
						return false;
					}	
					RiTHMSpecification currSpec = new DefaultRiTHMSpecification(splitByeq[1]);
					specListthisPipe.add(currSpec);
					resSpecMap.put(currSpec, splitByeq[0]);
					logger.debug(splitByeq[0] + "=" + currSpec.getTextDescription());
					specParserMap.put(currSpec,parsersForSpecs[j]);
					logger.debug(currSpec.getTextDescription() + " to be parsed with "+parsersForSpecs[j]);
					specMonMap.put(currSpec, monsForSpecs[j]);
					logger.debug(currSpec.getTextDescription() +" to be monitored with " + monsForSpecs[j]);
					j++;
				}
				specPredEvalTypeMap.put(specListthisPipe.get(0), predType);
				specPredEvalPathMap.put(specListthisPipe.get(0), predScriptPath);
				specPipes.add(specListthisPipe);
			}
			ArrayList<PredicateState> nextList; 
			int i = 0;
			for(ArrayList<RiTHMSpecification> specListPipe: specPipes)
			{
				j = 0;
				nextList = null;
				for(RiTHMSpecification rSpec: specListPipe)
				{
					if(j > 0)
						nextList = (ArrayList<PredicateState>)rithmMon.getTruthValueasPredicate();
					rtParams.specString = rSpec.getTextDescription();
					rtParams.specParserClass = specParserMap.get(rSpec);
					rtParams.monitorClass = specMonMap.get(rSpec);
					if(j == 0){
						rtParams.pEvaluatorName = specPredEvalTypeMap.get(rSpec);
						rtParams.pEvaluatorPath = specPredEvalPathMap.get(rSpec);
					}
					else
					{
						rtParams.pEvaluatorName = null;
					}
					setUpMonInstance(rtParams.isProcessingDatafile,
							 rtParams.dataFile,
							 rtParams.specParserClass,
							 rtParams.monitorClass,   
							 rtParams.traceParserClass,   
							 rtParams.pEvaluatorName, 
							 rtParams.pEvaluatorPath  
							 );
					runMonitor(rtParams.outFileName+ Integer.toString(i) + Integer.toString(j),
							false,
							(j ==0),
							rtParams.plotFileName + Integer.toString(i) + Integer.toString(j),
							nextList);
					j++;
				}
				i++;
			}
		}
		return true;
	}
	public void runMonitor(String outFileName, 
							boolean specsFromFile, 
							boolean firstStage,
							String plotFilename,
							ArrayList<PredicateState> predicatesNextStage
							)
	{
		// TODO: Start monitor thread on a port and wait for stop monitor setup command
		// TODO stop thread when stop command is issued
		// TODO: Wait for thread to process prog states/ jsons
		// TODO: Add condition class to create trigger for monitoring
		rRes = null;
		rithmMon.setParser(rithmParser);
		if(pEvaluator == null)
			pEvaluator = new DefaultPredicateEvaluator();
		rithmMon.setPredicateEvaluator(pEvaluator);
		logger.debug("Log file of monitor = " + outFileName);
		logger.debug("Plot file of monitor = " + plotFilename);
		rithmMon.setOutFile(outFileName);
		rithmMon.setPlotFile(plotFilename);
		if(rtParams.specsFromFile && !pipeMode)
			rithmMon.synthesizeMonitors(rtParams.specFile , specsFromFile);
		else
		{
			rithmMon.synthesizeMonitors(rtParams.specString , specsFromFile);
			if(pipeMode)
				rithmMon.setResultPredicateName(new DefaultRiTHMSpecification(rtParams.specString),resSpecMap.get(new DefaultRiTHMSpecification(rtParams.specString)));
		}
		
		if(rtParams.isProcessingDatafile)
		{	
			ProgState pState = null;
			if(firstStage){
				pState = dFactory.getNextProgState();
				//add code to fill in predicatestate here when running later stages of pipe
				while( pState != null)
				{

					rithmMon.fillBuffer(pState);
					pState = dFactory.getNextProgState();
					if(pState!= null)
						if(pState.getTimestamp()< 0)
							rithmMon.runMonitor();
				}
				rRes = rithmMon.runMonitor();
			}else{
				rithmMon.setBuffer(predicatesNextStage);
				rRes = rithmMon.runMonitor();
			}
		}
		pEvaluator=null;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
//		RiTHMParameterValidator rValidator = new RiTHMParameterValidator((short)2);
//		rValidator.setCmdOptions(cmdLine, options, hlpFormatter);
		String confFile = rValidator.fetchConfigFile();
		if(confFile == null)
			rtParams = rValidator.validate(null);
		else
		{
			rValidator.setMode((short)1);
			rtParams = rValidator.validate(confFile);
		}
		processCommand();
	}
	
}
