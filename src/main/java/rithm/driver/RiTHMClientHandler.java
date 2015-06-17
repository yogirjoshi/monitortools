package rithm.driver;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.spec.PSSParameterSpec;

import rithm.core.ProgState;
import rithm.core.RiTHMParameters;
import rithm.core.RiTHMResultCollection;
import rithm.core.RiTHMSpecification;
import rithm.datatools.CSVDataFactory;
import rithm.datatools.XMLDataFactory;
import rithm.defaultcore.DefaultPredicateEvaluator;
import rithm.defaultcore.DefaultProgramState;
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

import org.apache.log4j.Logger;

import com.google.gson.Gson;

public class RiTHMClientHandler extends Thread {
	protected boolean isSecureMode;
	protected boolean confByClient;
	protected SSLSocket cliSecureSocket;
	protected Socket cliSocket;
	protected ObjectInputStream oInStream;
	protected ObjectOutputStream oOutStream;
	protected RiTHMParameters rithParams;	
	protected boolean specsFromFile;
	protected boolean isProcessingDatafile;
	protected boolean toDisconnect;
	protected boolean monStarted;
	protected boolean monJsonMode;
	final static Logger logger = Logger.getLogger(RiTHMClientHandler.class);
	public RiTHMClientHandler(SSLSocket cliSocket, boolean monJsonMode)
	{
		super();
		this.cliSecureSocket = cliSocket;
		initializeParams(false, monJsonMode);
		isSecureMode = true;

	}
	public RiTHMClientHandler(Socket cliSocket, boolean monJsonMode)
	{
		super();
		this.cliSocket = cliSocket;
		initializeParams(false, monJsonMode);
		isSecureMode = false;
	}
	public RiTHMClientHandler(SSLSocket cliSocket, boolean confByClient, boolean monJsonMode)
	{
		super();
		this.cliSecureSocket = cliSocket;
		initializeParams(confByClient, monJsonMode);
		isSecureMode = true;
	}
	public RiTHMClientHandler(Socket cliSocket, boolean confByClient, boolean monJsonMode)
	{
		super();
		this.cliSocket = cliSocket;
		initializeParams(confByClient, monJsonMode);
		isSecureMode = false;
	}
	private void initializeParams(boolean confByClient, boolean monJsonMode)
	{
		toDisconnect = false;
		monStarted = false;
		rithParams = new RiTHMParameters();
		this.confByClient = confByClient;
		this.monJsonMode = monJsonMode;
	}
	public RiTHMReplyCommand processCommand(RiTHMSetupCommand commandObj)
	{
		RiTHMReplyCommand replyObj = new RiTHMReplyCommand("ok");
		String commandSting = commandObj.getCommandString();
		switch (commandSting) {
		case "disConnect":
			toDisconnect = true;
			if(monStarted)
				monStarted = false;
			break;
		case "specContents":
			rithParams.specFile = (String)commandObj.getParam("specifications");
			specsFromFile =false;
			break;
		case "specFile":
			rithParams.specFile = (String)commandObj.getParam("filename");
			specsFromFile = true;
			break;
		case "dataFile":
			isProcessingDatafile = true;
			rithParams.dataFile = (String)commandObj.getParam("filename");
			break;	
		case "traceParserClass":
			String typeF = (String)commandObj.getParam("type");
			switch (typeF) {
			case "XML":
				rithParams.dFactory = new XMLDataFactory(rithParams.dataFile);
				break;
			case "CSV":
				rithParams.dFactory = new CSVDataFactory(rithParams.dataFile);
				break;
			default:
				break;
			}
			break;	
		case "specParserClass":
			typeF = (String)commandObj.getParam("type");
			switch (typeF) {
				case "LTL":
					rithParams.rithmParser = new LTLParser("LTL");
					break;
				case "VLTL":
					rithParams.rithmParser = new VerboseLTLParser("Verbose LTL");
					break;
				case "MTL":
					rithParams.rithmParser = new MTLParser("MTL");
					break;
			}
		case "monitorClass":
			typeF = (String)commandObj.getParam("type");
			switch (typeF) {
			case "LTL3":
				rithParams.rithmMon = new LTLMonitor();
				rithParams.rithmMon.setMonitorValuation(new LTL3MonValuation());
				break;
			case "LTL4":
				rithParams.rithmMon = new LTL4Monitor();
				rithParams.rithmMon.setMonitorValuation(new LTL4MonValuation());
				break;
			case "MTL":	
				rithParams.rithmMon = new MTLMonitor();
				rithParams.rithmMon.setMonitorValuation(new TwoValuedValuation());
				
				break;
			default:
				break;
			}

			break;	
		case "predicateEvaluator":
			typeF = (String)commandObj.getParam("type");
			String scriptStr = (String)commandObj.getParam("script");
			switch (typeF) {
			case "lua":	
				rithParams.pEvaluator = new ScriptPredicateEvaluator(scriptStr, "luaj", false);
				break;
			case "js":
				rithParams.pEvaluator = new ScriptPredicateEvaluator(scriptStr, "JavaScript", false);
				break;
			default:
				break;
			}
		default:
			replyObj.commandString = "unknown_command";
			break;
		}
		return replyObj;
	}
	public RiTHMReplyCommand startMonitorThread(RiTHMMonitorCommand commandObj, String JSONStr)
	{
		// TODO: Start monitor thread on a port and wait for stop monitor setup command
		// TODO stop thread when stop command is issued
		// TODO: Wait for thread to process prog states/ jsons
		// TODO: Add condition class to create trigger for monitoring
		RiTHMReplyCommand replyObj = new RiTHMReplyCommand("ok");
		if(!monStarted)
		{
			rithParams.rithmMon.setParser(rithParams.rithmParser);
			if(rithParams.pEvaluator == null)
				rithParams.pEvaluator = new DefaultPredicateEvaluator();
			rithParams.rithmMon.setPredicateEvaluator(rithParams.pEvaluator);
//			rithParams.rithmParser.
			rithParams.rithmMon.synthesizeMonitors(rithParams.specFile , specsFromFile);
			monStarted = true;
		}
		if(isProcessingDatafile)
		{
			
			ProgState pState = rithParams.dFactory.getNextProgState();
			while( pState != null)
			{
				rithParams.rithmMon.fillBuffer(pState);
				pState = rithParams.dFactory.getNextProgState();
			}
			rithParams.rithmMon.runMonitor();
		}
		else
		{
			//start monitor thread and change to mon mode and keep accepting data commands
			//when receive next status command send reply with truth values
			ProgState pState;
			if(!monJsonMode)
			{
				pState = commandObj.getProgState();
			}
			else
			{
				Gson gson = new Gson();
				pState = gson.fromJson(JSONStr,DefaultProgramState.class);
			}
			if(pState.getTimestamp() < 0)
			{
				RiTHMResultCollection rRes = rithParams.rithmMon.runMonitor();
//				for(RiTHMSpecification rSpec: rithParams.rithmParser.getSpecs())
				//TODO add result object to reply
			}
			else
				rithParams.rithmMon.fillBuffer(pState);
		}
		return replyObj;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			if(isSecureMode)
			{
				oInStream = new ObjectInputStream(cliSecureSocket.getInputStream());
				oOutStream = new ObjectOutputStream(cliSecureSocket.getOutputStream());
			}
			else
			{
				oInStream = new ObjectInputStream(cliSocket.getInputStream());
				oOutStream = new ObjectOutputStream(cliSocket.getOutputStream());
			}

			while(!toDisconnect){
				if(!confByClient && !monStarted)
					startMonitorThread(null, null);
				Object cliObj = oInStream.readObject();
				RiTHMReplyCommand replyObj = null;
				if(cliObj instanceof RiTHMSetupCommand)
					replyObj = processCommand((RiTHMSetupCommand)cliObj);
				if(cliObj instanceof RiTHMMonitorCommand)
					replyObj = startMonitorThread((RiTHMMonitorCommand)cliObj,null);
				if(cliObj instanceof String)
					replyObj = startMonitorThread(null,(String)cliObj);
				
				
				oOutStream.writeObject(replyObj);
			}
		} catch (IOException e) {
			// TODO: handle exception
		}
		catch (ClassNotFoundException e) {
			// TODO: handle exception
		}
	}
	

}
