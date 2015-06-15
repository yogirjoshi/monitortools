package rithm.driver;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import rithm.core.RiTHMParameters;
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

import javax.net.ssl.SSLSocket;

import org.apache.log4j.Logger;

public class RiTHMClientHandler extends Thread {
	protected SSLSocket cliSocket;
	protected ObjectInputStream oInStream;
	protected ObjectOutputStream oOutStream;
	protected RiTHMParameters rithParams;	
	protected boolean specsFromFile;
	final static Logger logger = Logger.getLogger(RiTHMClientHandler.class);
	public RiTHMClientHandler(SSLSocket cliSocket)
	{
		super();
		this.cliSocket = cliSocket;
		rithParams = new RiTHMParameters();
	}
	public RiTHMReplyCommand processCommand(RiTHMSetupCommand commandObj)
	{
		String commandSting = commandObj.getCommandString();
		switch (commandSting) {
		case "specContents":
			rithParams.specFile = (String)commandObj.getParam("filename");
			specsFromFile =false;
			break;
		case "specFile":
			rithParams.specFile = (String)commandObj.getParam("filename");
			specsFromFile = true;
			break;
		case "dataFile":
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
			rithParams.rithmMon.setParser(rithParams.rithmParser);
			if(rithParams.pEvaluator == null)
				rithParams.pEvaluator = new DefaultPredicateEvaluator();
			rithParams.rithmMon.setPredicateEvaluator(rithParams.pEvaluator);
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
			break;
		}
		return null;
	}
	public RiTHMReplyCommand startMonitorThread(RiTHMMonitorCommand commandObj)
	{
		// TODO: Start monitor thread on a port and wait for stop monitor setup command
		// TODO stop thread when stop command is issued
		// TODO: Wait for thread to process prog states/ jsons
		return null;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			oInStream = new ObjectInputStream(cliSocket.getInputStream());
			oOutStream = new ObjectOutputStream(cliSocket.getOutputStream());
			Object cliObj = oInStream.readObject();
			RiTHMReplyCommand replyObj = null;
			if(cliObj instanceof RiTHMSetupCommand)
				replyObj = processCommand((RiTHMSetupCommand)cliObj);
			if(cliObj instanceof RiTHMMonitorCommand)
				replyObj = startMonitorThread((RiTHMMonitorCommand)cliObj);
			oOutStream.writeObject(replyObj);
		} catch (IOException e) {
			// TODO: handle exception
		}
		catch (ClassNotFoundException e) {
			// TODO: handle exception
		}
	}
	

}
