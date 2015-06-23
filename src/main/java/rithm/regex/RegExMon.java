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

import rithm.core.MonState;
import rithm.core.MonValuation;
import rithm.core.MonitoringEventListener;
import rithm.core.ParserPlugin;
import rithm.core.PredicateEvaluator;
import rithm.core.PredicateState;
import rithm.core.ProgState;
import rithm.core.RiTHMLogMessages;
import rithm.core.RiTHMMonitor;
import rithm.core.RiTHMResultCollection;
import rithm.core.RiTHMSpecification;
import rithm.core.RiTHMSpecificationCollection;
import rithm.core.RiTHMTruthValue;
import rithm.defaultcore.DefaultPredicateState;
import rithm.defaultcore.DefaultRegExPredicateState;
import rithm.defaultcore.DefaultRiTHMSpecification;
import rithm.defaultcore.DefaultRiTHMSpecificationResult;
import rithm.defaultcore.DefaultRiTHMTruthValue;
import dk.brics.automaton.RegExp;
import dk.brics.automaton.RunAutomaton;
public class RegExMon implements RiTHMMonitor{
	
	protected HashMap<String, Character> alphabetList;
	protected ArrayList<RegExp> regExList;
	protected ArrayList<RunAutomaton> runAutomataList;
	protected String outFileName;
	protected HashMap<String, MonState> currentStates;
	protected ArrayList<PredicateState> buffer;
	protected PredicateEvaluator pe;
	protected int specCount = 0;
	protected RiTHMResultCollection specStatus;
	protected ArrayList<MonitoringEventListener> meList;
	final static Logger logger = Logger.getLogger(RegExMon.class);
	
	public RegExMon()
	{
		alphabetList = new HashMap<String, Character>();
		regExList = new ArrayList<RegExp>();
		runAutomataList = new ArrayList<RunAutomaton>();
		specStatus = new DefaultRiTHMSpecificationResult();
		buffer = new ArrayList<PredicateState>();
		
	}
	
	
	@Override
	public RiTHMTruthValue getTruthValueAt(
			RiTHMSpecification spec, int i) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not supported yet.");
	}


	@Override
	public boolean setFormulas(RiTHMSpecificationCollection Specs) {
		// TODO Auto-generated method stub
		for(RiTHMSpecification eachSpec:Specs)
		{
			if(!checkAndAddAlphabet(eachSpec.getTextDescription()))
				synthesizeAutomaton(eachSpec.getTextDescription());
		}
		return true;
	}

	@Override
	public boolean synthesizeMonitors(RiTHMSpecificationCollection Specs) {
		// TODO Auto-generated method stub
		for(RiTHMSpecification eachSpec:Specs)
		{
			if(!checkAndAddAlphabet(eachSpec.getTextDescription()))
				synthesizeAutomaton(eachSpec.getTextDescription());
		}
		return true;
	}	
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
	private void synthesizeAutomaton(String line)
	{
		RegExp rExp = new RegExp(line);
		regExList.add(specCount,rExp);
		RunAutomaton currAutomaton = new RunAutomaton(rExp.toAutomaton(),true);
		runAutomataList.add(specCount++,currAutomaton);
		RiTHMSpecification rSpec = new DefaultRiTHMSpecification(rExp.toString());
		specStatus.setResult(rSpec, new DefaultRiTHMTruthValue(Integer.toString(currAutomaton.getInitialState())));
		logger.info("Created Automaton for Regular expression " + line);
	}
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
			logger.error(RiTHMLogMessages.RITHM_ERROR + e.getMessage());
		}
		finally{
			try {
				reader.close();
			} catch (IOException e2) {
				// TODO: handle exception
				logger.error(RiTHMLogMessages.RITHM_ERROR + e2.getMessage());
			}
		}
		return true;
	}

	@Override
	public RiTHMResultCollection runMonitor() {
		// TODO Auto-generated method stub
		BufferedWriter outWriter;
		try
		{
			outWriter = new BufferedWriter(new FileWriter(new File(outFileName)));
			outWriter.write("<html>");
			outWriter.write("<body>");
			for(int i =0; i < buffer.size();i++)
			{
//				System.out.println("__________________________________________________________________");
//				System.out.println("Event " + Integer.toString(i));
				outWriter.write("Event:" + Integer.toString(i));
				outWriter.write("<div style=\"background: #B0B0B0 \">");
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
						outWriter.write("Specification: " + regExList.get(j).toString() + " => " + "<font color=\"Green\">" + "Satisfied" + "</font>");
					else
						outWriter.write("Specification: " + regExList.get(j).toString() + " => " + "<font color=\"Red\">" + "Not Satisfied" + "</font>");
					specStatus.setResult(new DefaultRiTHMSpecification(regExList.get(j).toString()), new DefaultRiTHMTruthValue(Integer.toString(currStatee)));
				}
				outWriter.write("</div>");
			}

			outWriter.write("</body>");
			outWriter.write("</html>");
			outWriter.close();
		}catch(IOException io)
		{
			logger.error(RiTHMLogMessages.RITHM_ERROR + io.getMessage());
		}
		return specStatus;
	}
	
	@Override
	public List<RiTHMTruthValue> getTruthValueCollection(RiTHMSpecification spec) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not supported yet.");
	}


	@Override
	public boolean setTraceFile(String FileName) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean fillBuffer(ProgState ps) {
		// TODO Auto-generated method stub
		pe.SetProgStateObj(ps);
		buffer.add((PredicateState)pe.evaluatePredicates());
		return true;
	}

	@Override
	public void setMonitoringEventListener(MonitoringEventListener mel) {
		// TODO Auto-generated method stub
		this.meList.add(mel);
	}

	@Override
	public void setMonitorValuation(MonValuation val) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setPredicateEvaluator(PredicateEvaluator pe) {
		// TODO Auto-generated method stub
		this.pe = pe;
	}

	@Override
	public void setOutFile(String outFile) {
		// TODO Auto-generated method stub
		this.outFileName = outFile;
	}

	@Override
	public void setParser(ParserPlugin parser) {
		// TODO Auto-generated method stub
	}

}
