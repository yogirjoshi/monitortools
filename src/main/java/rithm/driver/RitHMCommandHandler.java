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
import java.util.Properties;

import rithm.commands.RitHMMonitorCommand;
import rithm.commands.RitHMParameters;
import rithm.commands.RitHMReplyCommand;
import rithm.commands.RitHMSetupCommand;
import rithm.commands.RitHMParameterValidator;
import rithm.core.DataFactory;
import rithm.core.MonitoringEventListener;
import rithm.core.ParserPlugin;
import rithm.core.PredicateEvaluator;
import rithm.core.PredicateState;
import rithm.core.ProgState;
import rithm.core.RitHMMonitor;
import rithm.core.RitHMMonitorTrigger;
import rithm.core.RitHMResultCollection;
import rithm.core.RitHMSpecification;
import rithm.core.RitHMTruthValue;
import rithm.datatools.CSVDataFactory;
import rithm.datatools.XMLDataFactory;
import rithm.defaultcore.DefaultMonitoringEventListener;
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

// TODO: Auto-generated Javadoc
/**
 * The Class RiTHMCommandHandler.
 */
public class RitHMCommandHandler extends Thread implements RitHMMonitorTrigger{
	
	/** The r validator. */
	public RitHMParameterValidator rValidator;
	
	/** The r res. */
	protected RitHMResultCollection rRes;
	
	/** The rt params. */
	protected RitHMParameters rtParams;	
	
	/** The d factory. */
	protected DataFactory dFactory;
	
	/** The rithm parser. */
	protected ParserPlugin rithmParser;
	
	/** The rithm mon. */
	protected RitHMMonitor rithmMon;
	
	/** The p evaluator. */
	protected PredicateEvaluator pEvaluator = null;
	
	protected MonitoringEventListener mEventListener = null;
	/** The pipe mode. */
	protected boolean pipeMode = false;
	
	/** The specs from file. */
	protected boolean specsFromFile;
	
	/** The is processing datafile. */
	protected boolean isProcessingDatafile;
	
	/** The cmd line. */
	protected CommandLine cmdLine;
	
	/** The options. */
	protected Options options;
	
	/** The hlp formatter. */
	protected HelpFormatter hlpFormatter;
	
	/** The spec pipes. */
	protected ArrayList<ArrayList<RitHMSpecification>> specPipes;
	
	/** The res spec map. */
	protected HashMap<RitHMSpecification, String> resSpecMap;
	
	/** The spec parser map. */
	protected HashMap<RitHMSpecification, String> specParserMap;
	
	/** The spec mon map. */
	protected HashMap<RitHMSpecification, String> specMonMap;
	
	/** The spec pred eval type map. */
	protected HashMap<RitHMSpecification,String> specPredEvalTypeMap;
	
	/** The spec pred eval path map. */
	protected HashMap<RitHMSpecification, String> specPredEvalPathMap;
	
	/** The Constant logger. */
	final static Logger logger = Logger.getLogger(RitHMCommandHandler.class);
	
	protected enum Triggertype{
		EVT ,
		BUFT,
		TT,
		DEF
	}

	protected Triggertype myTriggerType;
	
	protected int eventCount = 0;
	protected int timeInterval = 0;
	@Override
	public void setTriggerProperties(Properties triggerProperties) {
		// TODO Auto-generated method stub
		Properties tProperties;
		myTriggerType = Triggertype.DEF;
		if(rtParams.customArgumentsProperties.contains("T")){
			tProperties = (Properties)rtParams.customArgumentsProperties.get("T");

			if(tProperties.getProperty("eventCount") != null){
				eventCount = Integer.parseInt((String)tProperties.get("eventCount"));
				myTriggerType = Triggertype.BUFT;
				return;
			}
			if(tProperties.getProperty("timeInterval") != null){
				eventCount = Integer.parseInt((String)tProperties.get("timeInterval"));
				myTriggerType = Triggertype.TT;
				return;
			}
		}

	}

	/**
	 * Instantiates a new ri thm command handler.
	 *
	 * @param cmdLine the cmd line
	 * @param options the options
	 * @param hlpFormatter the hlp formatter
	 */
	public RitHMCommandHandler(CommandLine cmdLine, Options options,HelpFormatter hlpFormatter)
	{
		this.cmdLine = cmdLine;
		this.options = options;
		this.hlpFormatter = hlpFormatter;
		rValidator = new RitHMParameterValidator((short)2);
		rValidator.setCmdOptions(cmdLine, options, hlpFormatter);
	}
	
	/**
	 * Sets the up mon instance.
	 *
	 * @param isProcessingDatafile the is processing datafile
	 * @param dataFile the data file
	 * @param specParserClass the spec parser class
	 * @param monitorClass the monitor class
	 * @param traceParserClass the trace parser class
	 * @param pEvaluatorName the evaluator name
	 * @param pEvaluatorPath the evaluator path
	 */
	protected void setUpMonInstance(boolean isProcessingDatafile, 
									  String dataFile,
									  String specParserClass,
									  String monitorClass,
									  String traceParserClass,
									  String pEvaluatorName,
									  String pEvaluatorPath,
									  String monEventListenerName
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
				dFactory = (DataFactory)PluginLoader.loadPluginWithType(DataFactory.class, traceParserClass);
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
		default:
			rithmParser = (ParserPlugin)PluginLoader.loadPluginWithType(ParserPlugin.class, specParserClass);
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
			rithmMon = (RitHMMonitor)PluginLoader.loadPluginWithType(RitHMMonitor.class, monitorClass);
			//Assumed that valuation is set internally by the plugin
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
				pEvaluator = (PredicateEvaluator)PluginLoader.loadPluginWithType(PredicateEvaluator.class, pEvaluatorName);
				break;
			}
		if(monEventListenerName != null){
			mEventListener = (MonitoringEventListener)PluginLoader.loadPluginWithType(MonitoringEventListener.class, monEventListenerName);
		}
	}
	public void extractPipeInfo(){
		specPipes = new ArrayList<ArrayList<RitHMSpecification>>();
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
			
			ArrayList<RitHMSpecification> specListthisPipe = new ArrayList<>();
			j = 0;
			for(String specWithRes: Arrays.asList(specsAsString))
			{
				String []splitByeq = specWithRes.split("=");
				if(splitByeq.length != 2)
				{
					logger.fatal("In-valid syntax " + specWithRes);
				}	
				RitHMSpecification currSpec = new DefaultRiTHMSpecification(splitByeq[1]);
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
	}
	/**
	 * Process command.
	 *
	 * @return true, if successful
	 */
	public boolean processCommand()
	{
		if(rtParams.pipeCount > 0)
			pipeMode = true;
		setTriggerProperties(null);
		if(!pipeMode){
			
			setUpMonInstance(rtParams.isProcessingDatafile,
							 rtParams.dataFile,
							 rtParams.specParserClass,
							 rtParams.monitorClass,  
							 rtParams.traceParserClass,   
							 rtParams.pEvaluatorName, 
							 rtParams.pEvaluatorPath,
							 rtParams.monEventListenerName
							 );
			runMonitor(rtParams.outFileName, rtParams.specsFromFile, true,rtParams.plotFileName, null);
		}else{
			extractPipeInfo();
			ArrayList<PredicateState> nextList; 
			int i = 0, j = 0;
			for(ArrayList<RitHMSpecification> specListPipe: specPipes)
			{
				j = 0;
				nextList = null;
				for(RitHMSpecification rSpec: specListPipe)
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
							 rtParams.pEvaluatorPath,
							 rtParams.monEventListenerName
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
	
	/**
	 * Run monitor.
	 *
	 * @param outFileName the out file name
	 * @param specsFromFile the specs from file
	 * @param firstStage the first stage
	 * @param plotFilename the plot filename
	 * @param predicatesNextStage the predicates next stage
	 */
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
		if(rtParams.resetOnViolation)
			rithmMon.setResetOnViolation(rtParams.resetOnViolation);
		if(pEvaluator == null)
			pEvaluator = new DefaultPredicateEvaluator();
		rithmMon.setPredicateEvaluator(pEvaluator);
		
		if(outFileName != null)
			logger.debug("Log file of monitor = " + outFileName);
		if(plotFilename != null)
			logger.debug("Plot file of monitor = " + plotFilename);
		rithmMon.setOutFile(outFileName);
		rithmMon.setPlotFile(plotFilename);
		if(mEventListener == null)
			mEventListener = new DefaultMonitoringEventListener();
		rithmMon.setMonitoringEventListener(mEventListener);
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
				int evtCnt = 0;
				while( pState != null)
				{
					rithmMon.fillBuffer(pState);
					pState = dFactory.getNextProgState();
//					if(pState!= null)
					switch(myTriggerType){
					case BUFT :
						if(evtCnt >= eventCount){
							rithmMon.runMonitor(false);
							evtCnt = 0;
						}
						else
							evtCnt++;
						break;
					case EVT :	
						rithmMon.runMonitor(false);
						break;
					case TT:
						//TODO not implemented yet
						break;
					default:
						break;
					}
				}
				rRes = rithmMon.runMonitor(true);
				dFactory.closeDataSource();
			}else{
				rithmMon.setBuffer(predicatesNextStage);
				rRes = rithmMon.runMonitor(true);
			}

		}
		pEvaluator=null;
	}

	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
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
		if(rtParams != null)
			processCommand();
	}
	
}
