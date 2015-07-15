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

import rithm.commands.RitHMMonitorCommand;
import rithm.commands.RitHMParameters;
import rithm.commands.RitHMReplyCommand;
import rithm.commands.RitHMSetupCommand;
import rithm.commands.RitHMParameterValidator;
import rithm.core.DataFactory;
import rithm.core.ParserPlugin;
import rithm.core.PredicateEvaluator;
import rithm.core.ProgState;
import rithm.core.RitHMMonitor;
import rithm.core.RitHMResultCollection;
import rithm.core.RitHMSpecification;
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

// TODO: Auto-generated Javadoc
/**
 * The Class RiTHMClientHandler.
 */
public class RitHMClientHandler extends Thread {
	
	/** The is secure mode. */
	protected boolean isSecureMode;
	
	/** The conf by client. */
	protected boolean confByClient;
	
	/** The cli secure socket. */
	protected SSLSocket cliSecureSocket;
	
	/** The cli socket. */
	protected Socket cliSocket;
	
	/** The d in stream. */
	protected DataInputStream dInStream;
	
	/** The d out stream. */
	protected DataOutputStream dOutStream;
	
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
	
	/** The specs from file. */
	protected boolean specsFromFile;
	
	/** The is processing datafile. */
	protected boolean isProcessingDatafile;
	
	/** The to disconnect. */
	protected boolean toDisconnect;
	
	/** The mon started. */
	protected boolean monStarted;
	
	/** The Constant CONFIG. */
	final static char CONFIG ='C';
	
	/** The Constant DISCONNECT. */
	final static char DISCONNECT ='D';
	
	/** The Constant RUNMONITOR. */
	final static char RUNMONITOR ='M';
	
	/** The Constant JSONDATA. */
	final static char JSONDATA ='J';
	
	/** The Constant logger. */
	final static Logger logger = Logger.getLogger(RitHMClientHandler.class);
	
	/**
	 * Instantiates a new ri thm client handler.
	 *
	 * @param cliSocket the cli socket
	 */
	public RitHMClientHandler(SSLSocket cliSocket)
	{
		super();
		this.cliSecureSocket = cliSocket;
		initializeParams(false);
		isSecureMode = true;
	}
	
	/**
	 * Instantiates a new ri thm client handler.
	 *
	 * @param cliSocket the cli socket
	 */
	public RitHMClientHandler(Socket cliSocket)
	{
		super();
		this.cliSocket = cliSocket;
		initializeParams(false);
		isSecureMode = false;

	}
	
	/**
	 * Instantiates a new ri thm client handler.
	 *
	 * @param cliSocket the cli socket
	 * @param confByClient the conf by client
	 */
	public RitHMClientHandler(SSLSocket cliSocket, boolean confByClient)
	{
		super();
		this.cliSecureSocket = cliSocket;
		initializeParams(confByClient);
		isSecureMode = true;
	}
	
	/**
	 * Instantiates a new ri thm client handler.
	 *
	 * @param cliSocket the cli socket
	 * @param confByClient the conf by client
	 */
	public RitHMClientHandler(Socket cliSocket, boolean confByClient)
	{
		super();
		this.cliSocket = cliSocket;
		initializeParams(confByClient);
		isSecureMode = false;
	}
	
	/**
	 * Initialize params.
	 *
	 * @param confByClient the conf by client
	 */
	private void initializeParams(boolean confByClient)
	{
		toDisconnect = false;
		monStarted = false;
		this.confByClient = confByClient;
	}

	/**
	 * Process command.
	 *
	 * @param commandObj the command obj
	 * @return the ri thm reply command
	 */
	public RitHMReplyCommand processCommand(RitHMSetupCommand commandObj)
	{
		RitHMReplyCommand replyObj = new RitHMReplyCommand("ok");
		String commandSting = commandObj.getCommandString();

		switch (commandSting){
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
					pEvaluator = new DefaultPredicateEvaluator();
					break;
				}
			break;
		default:
			replyObj.setCommandString("unknown_command");
			break;
		}
		return replyObj;
	}
	
	/**
	 * Start monitor thread.
	 *
	 * @param JSONStr the JSON str
	 * @return the ri thm reply command
	 */
	public RitHMReplyCommand runMonitorThread(String JSONStr)
	{
		// TODO: Start monitor thread on a port and wait for stop monitor setup command
		// TODO stop thread when stop command is issued
		// TODO: Wait for thread to process prog states/ jsons
		// TODO: Add condition class to create trigger for monitoring
		RitHMReplyCommand replyObj = new RitHMReplyCommand("ok");
		RitHMResultCollection rRes ;
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
				if(pState != null)
					if(pState.getTimestamp()< 0)
						rithmMon.runMonitor();
			}
			rRes = rithmMon.runMonitor();
			Iterator<RitHMSpecification> rSpecIter = rRes.iterator();
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
				Iterator<RitHMSpecification> rSpecIter = rRes.iterator();
				while(rSpecIter.hasNext())
				{
					RitHMSpecification rSpec = rSpecIter.next();
					logger.debug(rRes.getResult(rSpec).getTruthValueDescription());
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
	
	/**
	 * Read message.
	 *
	 * @return the byte[]
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private byte[] readMessage() throws IOException
	{
		short size = dInStream.readShort();
		byte[] message = new byte[size];
		dInStream.readFully(message);
		return message;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		// TODO Auto-generated method stub
		Gson gs = new Gson();
		RitHMReplyCommand replyCommand =null ;
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
				{
//					rtParams = confiure rtparams from config file
					runMonitorThread(null);	
				}
				byte[] message;
				char command = dInStream.readChar();
				String messageStr;
				switch (command) {
				case CONFIG:
					message = readMessage();
					messageStr = new String(message);
					RitHMSetupCommand rsCommand =
							gs.fromJson(messageStr, RitHMSetupCommand.class);
					logger.debug(rsCommand.getRiTHMParameters().specParserClass);
					replyCommand = processCommand(rsCommand);
					break;
				case DISCONNECT:
					toDisconnect=true;
					break;
				case RUNMONITOR:
					break;	
				case JSONDATA:
					message = readMessage();
					replyCommand = runMonitorThread(new String(message));
					break;
				default:
					break;
				}
				logger.debug(gs.toJson(replyCommand));
				dOutStream.writeShort(gs.toJson(replyCommand).getBytes().length);
				dOutStream.write(gs.toJson(replyCommand).getBytes());
				dOutStream.flush();
			}
		}
		catch(EOFException e)
		{
			logger.fatal(e.getMessage());
		}
		catch (IOException e) {
			// TODO: handle exception
			logger.fatal(e.getMessage());
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
