package rithm.regex;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import rithm.basemonitors.RitHMBaseMonitor;
import rithm.core.MonState;
import rithm.core.MonValuation;
import rithm.core.MonitoringEventListener;
import rithm.core.ParserPlugin;
import rithm.core.PredicateEvaluator;
import rithm.core.PredicateState;
import rithm.core.ProgState;
import rithm.core.RitHMLogMessages;
import rithm.core.RitHMMonitor;
import rithm.core.RitHMResultCollection;
import rithm.core.RitHMSpecification;
import rithm.core.RitHMSpecificationCollection;
import rithm.core.RitHMTruthValue;
import rithm.defaultcore.DefaultPredicateState;
import rithm.defaultcore.DefaultRegExPredicateState;
import rithm.defaultcore.DefaultRiTHMSpecification;
import rithm.defaultcore.DefaultRiTHMSpecificationResult;
import rithm.defaultcore.DefaultRiTHMTruthValue;
import dk.brics.automaton.RegExp;
import dk.brics.automaton.RunAutomaton;
// TODO: Auto-generated Javadoc

/**
 * The Class RegExMon.
 */
public class RegExMon extends RitHMBaseMonitor implements RitHMMonitor{
	
	/** The alphabet list. */
	protected HashMap<String, Character> alphabetList;
	
	/** The reg ex list. */
	protected ArrayList<RegExp> regExList;
	
	/** The run automata list. */
	protected ArrayList<RunAutomaton> runAutomataList;
	
	/** The out file name. */
	protected String outFileName;
	
	/** The current states. */
	protected HashMap<String, MonState> currentStates;
	
	/** The buffer. */
	protected ArrayList<PredicateState> buffer;
	
	/** The pe. */
	protected PredicateEvaluator pe;
	
	/** The spec count. */
	protected int specCount = 0;
	
	/** The spec status. */
	protected RitHMResultCollection specStatus;

	/** The Constant logger. */
	final static Logger logger = Logger.getLogger(RegExMon.class);
	
	/**
	 * Instantiates a new reg ex mon.
	 */
	public RegExMon()
	{
		alphabetList = new HashMap<String, Character>();
		regExList = new ArrayList<RegExp>();
		runAutomataList = new ArrayList<RunAutomaton>();
		specStatus = new DefaultRiTHMSpecificationResult();
		buffer = new ArrayList<PredicateState>();
		
	}
	
	/* (non-Javadoc)
	 * @see rithm.basemonitors.RiTHMBaseMonitor#setFormulas(rithm.core.RiTHMSpecificationCollection)
	 */
	@Override
	public boolean setFormulas(RitHMSpecificationCollection Specs) {
		// TODO Auto-generated method stub
		super.setFormulas(Specs);
		for(RitHMSpecification eachSpec:Specs)
		{
			if(!checkAndAddAlphabet(eachSpec.getTextDescription()))
				synthesizeAutomaton(eachSpec.getTextDescription());
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see rithm.core.RiTHMMonitor#synthesizeMonitors(rithm.core.RiTHMSpecificationCollection)
	 */
	@Override
	public boolean synthesizeMonitors(RitHMSpecificationCollection Specs) {
		// TODO Auto-generated method stub
		for(RitHMSpecification eachSpec:Specs)
		{
			if(!checkAndAddAlphabet(eachSpec.getTextDescription()))
				synthesizeAutomaton(eachSpec.getTextDescription());
		}
		return true;
	}	
	
	/**
	 * Check and add alphabet.
	 *
	 * @param line the line
	 * @return true, if successful
	 */
	private boolean checkAndAddAlphabet(String line)
	{
		if(line.indexOf("=") != -1)
		{
			if(line.indexOf("=") == line.length()-1)
				alphabetList.put("", line.charAt(0));
			else
				alphabetList.put(line.substring(line.indexOf("=")+1), line.charAt(0));
			logger.info("Added definition of character " + line.charAt(0));
			return true;
		}
		return false;
	}
	
	/**
	 * Synthesize automaton.
	 *
	 * @param line the line
	 */
	private void synthesizeAutomaton(String line)
	{
		RegExp rExp = new RegExp(line);
		regExList.add(specCount,rExp);
		RunAutomaton currAutomaton = new RunAutomaton(rExp.toAutomaton(),true);
		runAutomataList.add(specCount++,currAutomaton);
		RitHMSpecification rSpec = new DefaultRiTHMSpecification(rExp.toString());
		specStatus.setResult(rSpec, new DefaultRiTHMTruthValue(Integer.toString(currAutomaton.getInitialState())));
		logger.info("Created Automaton for Regular expression " + line);
	}
	
	/* (non-Javadoc)
	 * @see rithm.core.RiTHMMonitor#synthesizeMonitors(java.lang.String, boolean)
	 */
	@Override
	public boolean synthesizeMonitors(String specDetails, boolean isFile) {
		// TODO Auto-generated method stub
		BufferedReader reader= null;String line;
		try {
			if(isFile)
        	{
        		reader = new BufferedReader(new FileReader(specDetails));
        	}
        	else
        	{
        		InputStream is = new ByteArrayInputStream(specDetails.getBytes());
        		reader = new BufferedReader(new InputStreamReader(is));
        	}
			while((line = reader.readLine()) != null)
				if(!checkAndAddAlphabet(line))
					synthesizeAutomaton(line);
		} catch (IOException e) {
			// TODO: handle exception
			logger.error(RitHMLogMessages.RITHM_ERROR + e.getMessage());
		}
		finally{
			try {
				reader.close();
			} catch (IOException e2) {
				// TODO: handle exception
				logger.error(RitHMLogMessages.RITHM_ERROR + e2.getMessage());
			}
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see rithm.core.RiTHMMonitor#runMonitor()
	 */
	@Override
	public RitHMResultCollection runMonitor(boolean isLastInvocation) {
		// TODO Auto-generated method stub
		openVerboseFiles();
		writeMonitoriLogFile("<html>");
		writeMonitoriLogFile("<body>");
		for(int i =0; i < buffer.size();i++)
		{
			//				System.out.println("__________________________________________________________________");
			//				System.out.println("Event " + Integer.toString(i));
			writeMonitoriLogFile("Event:" + Integer.toString(i));
			writeMonitoriLogFile("<div style=\"background: #B0B0B0 \">");
			DefaultRegExPredicateState topState = new DefaultRegExPredicateState((DefaultPredicateState)buffer.get(i));
			Character currAlphabet = alphabetList.get(topState.getPredicateString());
			if(currAlphabet == null)
			{
				currAlphabet = 'x';
			}
			for(int j = 0; j < specCount; j++)
			{
				int currStatee = Integer.parseInt((specStatus.getResult(new DefaultRiTHMSpecification(regExList.get(j).toString())).getTruthValueDescription()));
				RunAutomaton r = runAutomataList.get(j);
				currStatee = r.step(currStatee, currAlphabet);
				if(r.isAccept(currStatee))
					writeMonitoriLogFile("Specification: " + regExList.get(j).toString() + " => " + "<font color=\"Green\">" + "Satisfied" + "</font>");
				else
					writeMonitoriLogFile("Specification: " + regExList.get(j).toString() + " => " + "<font color=\"Red\">" + "Not Satisfied" + "</font>");
				specStatus.setResult(new DefaultRiTHMSpecification(regExList.get(j).toString()), new DefaultRiTHMTruthValue(Integer.toString(currStatee)));
			}
			writeMonitoriLogFile("</div>");
		}
		writeMonitoriLogFile("</body>");
		writeMonitoriLogFile("</html>");
		try {
			if(isLastInvocation)
				closeVerboseFiles();
		} catch (IOException ioe) {
			logger.fatal(ioe.getMessage());
		}
		return specStatus;
	}
	
	/* (non-Javadoc)
	 * @see rithm.core.RiTHMMonitor#fillBuffer(rithm.core.ProgState)
	 */
	@Override
	public boolean fillBuffer(ProgState ps) {
		// TODO Auto-generated method stub
		pe.SetProgStateObj(ps);
		buffer.add((PredicateState)pe.evaluatePredicates());
		return true;
	}

}
