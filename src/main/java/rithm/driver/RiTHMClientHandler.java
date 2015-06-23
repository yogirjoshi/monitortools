package rithm.driver;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.spec.PSSParameterSpec;
import java.util.Iterator;

import rithm.commands.RiTHMMonitorCommand;
import rithm.commands.RiTHMParameters;
import rithm.commands.RiTHMReplyCommand;
import rithm.commands.RiTHMSetupCommand;
import rithm.commands.RiTHMParameterValidator;
import rithm.core.DataFactory;
import rithm.core.ParserPlugin;
import rithm.core.PredicateEvaluator;
import rithm.core.ProgState;
import rithm.core.RiTHMMonitor;
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
	protected DataInputStream dInStream;
	protected DataOutputStream dOutStream;
	
	protected RiTHMParameters rtParams;	
	protected DataFactory dFactory;
	protected ParserPlugin rithmParser;
	protected RiTHMMonitor rithmMon;
	protected PredicateEvaluator pEvaluator = null;
	
	protected boolean specsFromFile;
	protected boolean isProcessingDatafile;
	protected boolean toDisconnect;
	protected boolean monStarted;
	final static char CONFIG ='C';
	final static char DISCONNECT ='D';
	final static char RUNMONITOR ='M';
	final static char JSONDATA ='J';
	final static Logger logger = Logger.getLogger(RiTHMClientHandler.class);
	public RiTHMClientHandler(SSLSocket cliSocket)
	{
		super();
		this.cliSecureSocket = cliSocket;
		initializeParams(false);
		isSecureMode = true;
	}
	public RiTHMClientHandler(Socket cliSocket)
	{
		super();
		this.cliSocket = cliSocket;
		initializeParams(false);
		isSecureMode = false;

	}
	public RiTHMClientHandler(SSLSocket cliSocket, boolean confByClient)
	{
		super();
		this.cliSecureSocket = cliSocket;
		initializeParams(confByClient);
		isSecureMode = true;
	}
	public RiTHMClientHandler(Socket cliSocket, boolean confByClient)
	{
		super();
		this.cliSocket = cliSocket;
		initializeParams(confByClient);
		isSecureMode = false;
	}
	private void initializeParams(boolean confByClient)
	{
		toDisconnect = false;
		monStarted = false;
		this.confByClient = confByClient;
	}
	public RiTHMReplyCommand processCommand(RiTHMSetupCommand commandObj)
	{
		RiTHMReplyCommand replyObj = new RiTHMReplyCommand("ok");
		String commandSting = commandObj.getCommandString();
		switch (commandSting) {
		case "config":
			rtParams = commandObj.getRiTHMParameters(); 
			if(rtParams.isProcessingDatafile){
				switch (rtParams.dataFile) {
				case "XML":
					dFactory = new XMLDataFactory(rtParams.dataFile);
					break;
				case "CSV":
					dFactory = new CSVDataFactory(rtParams.dataFile);
					break;
				default:
					break;
				}
				break;	
			}
			else
				logger.info("Data to be received on socket as "+ rtParams.dataFormat);

			switch (rtParams.specParserClass) {
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

			switch (rtParams.monitorClass) {
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
			if(rtParams.pEvaluatorName != null)
				switch (rtParams.pEvaluatorName) {
				case "lua":	
					pEvaluator = 
					new ScriptPredicateEvaluator(rtParams.pEvaluatorPath, "luaj", false);
					break;
				case "js":
					pEvaluator = 
					new ScriptPredicateEvaluator(rtParams.pEvaluatorPath, "JavaScript", false);
					break;
				default:
					break;
				}
			break;
		default:
			replyObj.setCommandString("unknown_command");
			break;
		}
		return replyObj;
	}
	public RiTHMReplyCommand startMonitorThread(String JSONStr)
	{
		// TODO: Start monitor thread on a port and wait for stop monitor setup command
		// TODO stop thread when stop command is issued
		// TODO: Wait for thread to process prog states/ jsons
		// TODO: Add condition class to create trigger for monitoring
		RiTHMReplyCommand replyObj = new RiTHMReplyCommand("ok");
		RiTHMResultCollection rRes ;
		if(!monStarted)
		{
			rithmMon.setParser(rithmParser);
			if(pEvaluator == null)
				pEvaluator = new DefaultPredicateEvaluator();
			rithmMon.setPredicateEvaluator(pEvaluator);
//			rithParams.rithmParser.
			rithmMon.setOutFile(rtParams.outFileName);
			if(rtParams.specsFromFile)
				rithmMon.synthesizeMonitors(rtParams.specFile , rtParams.specsFromFile);
			else
				rithmMon.synthesizeMonitors(rtParams.specString , rtParams.specsFromFile);
			monStarted = true;
		}
		if(isProcessingDatafile)
		{
			
			ProgState pState = dFactory.getNextProgState();
			while( pState != null)
			{
				rithmMon.fillBuffer(pState);
				pState = dFactory.getNextProgState();
				if(pState.getTimestamp()< 0)
					rithmMon.runMonitor();
			}
			rRes = rithmMon.runMonitor();
			Iterator<RiTHMSpecification> rSpecIter = rRes.iterator();
			while(rSpecIter.hasNext())
				replyObj.response.put(rSpecIter.next().getTextDescription(), rRes.getResult(rSpecIter.next()).getTruthValueDescription());
		}
		else
		{
			//start monitor thread and change to mon mode and keep accepting data commands
			//when receive next status command send reply with truth values
			ProgState pState;
			Gson gson = new Gson();
			pState = gson.fromJson(JSONStr,DefaultProgramState.class);
			if(pState.getTimestamp() < 0)
			{
				rRes = rithmMon.runMonitor();
				Iterator<RiTHMSpecification> rSpecIter = rRes.iterator();
				while(rSpecIter.hasNext())
				{
					RiTHMSpecification rSpec = rSpecIter.next();
					logger.info(rRes.getResult(rSpec).getTruthValueDescription());
					replyObj.response.put(rSpec.getTextDescription(), rRes.getResult(rSpec).getTruthValueDescription());
				}
//				for(RiTHMSpecification rSpec: rithParams.rithmParser.getSpecs())
				//TODO add result object to reply
			}
			else
			{
				logger.debug(JSONStr);
				rithmMon.fillBuffer(pState);
			}
		}
		return replyObj;
	}
	private byte[] readMessage() throws IOException
	{
		short size = dInStream.readShort();
		byte[] message = new byte[size];
		dInStream.readFully(message);
		return message;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		Gson gs = new Gson();
		RiTHMReplyCommand replyCommand =null ;
		try {
			if(isSecureMode)
			{
				dInStream = new DataInputStream(cliSecureSocket.getInputStream());
				dOutStream = new DataOutputStream(cliSecureSocket.getOutputStream());
			}
			else
			{
				dInStream = new DataInputStream(cliSocket.getInputStream());
				dOutStream = new DataOutputStream(cliSocket.getOutputStream());
			}

			while(!toDisconnect){
				if(!confByClient && !monStarted)
					startMonitorThread(null);		
				byte[] message;
				char command = dInStream.readChar();
				switch (command) {
				case CONFIG:
					message = readMessage();
					RiTHMSetupCommand rsCommand =
							gs.fromJson(new String(message), RiTHMSetupCommand.class);
					replyCommand = processCommand(rsCommand);
					break;
				case DISCONNECT:
					toDisconnect=true;
					break;
				case RUNMONITOR:
					break;	
				case JSONDATA:
					message = readMessage();
					replyCommand = startMonitorThread(new String(message));
					break;
				default:
					break;
				}
				logger.info(gs.toJson(replyCommand));
				dOutStream.writeShort(gs.toJson(replyCommand).getBytes().length);
				dOutStream.write(gs.toJson(replyCommand).getBytes());
				dOutStream.flush();
			}
		}
		catch(EOFException e)
		{
			e.printStackTrace();
		}
		catch (IOException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		finally
		{
			try {
				dInStream.close();dOutStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	

}
