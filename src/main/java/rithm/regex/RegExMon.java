package rithm.regex;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import rithm.core.*;
import dk.brics.automaton.*;
public class RegExMon implements RiTHMMonitor{
	ArrayList<String> predicateList;
	ArrayList<RegExp> regExList;
	ArrayList<RunAutomaton> runAutomataList;
	public RegExMon()
	{
		predicateList = new ArrayList<String>();
		regExList = new ArrayList<RegExp>();
		runAutomataList = new ArrayList<RunAutomaton>();
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
					predicateList.add(line.substring(line.indexOf("=")+1));
				}
				else
				{
					regExList.add(new RegExp(line));
					runAutomataList.add(new RunAutomaton(new RegExp(line).toAutomaton(),true));
				}
			}
		} catch (IOException e) {
			// TODO: handle exception
		}
		return false;
	}

	@Override
	public RiTHMResultCollection runMonitor() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean SetTraceFile(String FileName) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean FillBuffer(ProgState ps) {
		// TODO Auto-generated method stub
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
		
	}

	@Override
	public void setOutFile(String outFile) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setParser(ParserPlugin parser) {
		// TODO Auto-generated method stub
		
	}

}
