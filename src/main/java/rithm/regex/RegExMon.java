package rithm.regex;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import rithm.core.*;
import rithm.defaultcore.DefaultMonState;
import rithm.defaultcore.DefaultPredicateState;
import rithm.defaultcore.DefaultRegExPredicateState;
import rithm.defaultcore.DefaultRiTHMSpecification;
import rithm.defaultcore.DefaultRiTHMSpecificationCollection;
import rithm.defaultcore.DefaultRiTHMSpecificationResult;
import rithm.defaultcore.DefaultRiTHMTruthValue;
import dk.brics.automaton.*;
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
	public RegExMon()
	{
		alphabetList = new HashMap<String, Character>();
		regExList = new ArrayList<RegExp>();
		runAutomataList = new ArrayList<RunAutomaton>();
		specStatus = new DefaultRiTHMSpecificationResult();
	}
	@Override
	public boolean SetFormulas(RiTHMSpecificationCollection Specs) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean SynthesizeMonitors(RiTHMSpecificationCollection Specs) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean SynthesizeMonitors(String Filename) {
		// TODO Auto-generated method stub
		BufferedReader reader= null;String line;
		try {
			reader = new BufferedReader(new FileReader(Filename));
			while((line = reader.readLine()) != null)
			{
				if(line.indexOf("=") != -1)
				{
					alphabetList.put(line.substring(line.indexOf("=")+1), line.charAt(0));
				}
				else
				{
					RegExp rExp = new RegExp(line);
					regExList.add(specCount,rExp);
					RunAutomaton currAutomaton = new RunAutomaton(rExp.toAutomaton(),true);
					runAutomataList.add(specCount++,currAutomaton);
					RiTHMSpecification rSpec = new DefaultRiTHMSpecification(rExp.toString());
					specStatus.setResult(rSpec, new DefaultRiTHMTruthValue(Integer.toString(currAutomaton.getInitialState())));
				}
			}
		} catch (IOException e) {
			// TODO: handle exception
			System.out.println(e.getMessage());
		}
		finally{
			try {
				reader.close();
			} catch (IOException e2) {
				// TODO: handle exception
				System.out.println(e2.getMessage());
			}
		}
		return false;
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
					specStatus.setResult(new DefaultRiTHMSpecification(regExList.get(j).toString()), new DefaultRiTHMTruthValue(Integer.toString(currStatee)));
				}
			}
			outWriter.write("</body>");
			outWriter.write("</html>");
			outWriter.close();
		}catch(IOException io)
		{
			System.out.println(io.getMessage());
		}
		return specStatus;
	}

	@Override
	public boolean SetTraceFile(String FileName) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean FillBuffer(ProgState ps) {
		// TODO Auto-generated method stub
		pe.SetProgStateObj(ps);
		buffer.add((PredicateState)pe.EvaluatePredicates());
		return false;
	}

	@Override
	public void SetMonitoringEventListener(MonitoringEventListener mel) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void SetMonitorValuation(MonValuation val) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void SetPredicateEvaluator(PredicateEvaluator pe) {
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
