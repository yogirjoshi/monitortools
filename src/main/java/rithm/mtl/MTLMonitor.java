package rithm.mtl;
import java.io.BufferedReader;

import org.apache.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import org.antlr.v4.runtime.tree.ParseTree;

import rithm.core.MonValuation;
import rithm.core.MonitoringEventListener;
import rithm.core.ParserPlugin;
import rithm.core.PredicateEvaluator;
import rithm.core.PredicateState;
import rithm.core.ProgState;
import rithm.core.RiTHMMonitor;
import rithm.core.RiTHMProgStateCollection;
import rithm.core.RiTHMResultCollection;
import rithm.core.RiTHMSpecification;
import rithm.core.RiTHMSpecificationCollection;
import rithm.core.RiTHMTruthValue;
import rithm.defaultcore.DefaultPredicateState;
import rithm.defaultcore.DefaultProgStateCollection;
import rithm.defaultcore.DefaultRiTHMSpecification;
import rithm.defaultcore.DefaultRiTHMSpecificationCollection;
import rithm.defaultcore.DefaultRiTHMSpecificationResult;
import rithm.parsertools.mtl.*;
public class MTLMonitor implements RiTHMMonitor{
	
	protected RiTHMResultCollection currSpecStatus;
	protected RiTHMSpecificationCollection currSpecs;
//	protected HashMap<String, RiTHMTruthValue> ;
	protected RiTHMProgStateCollection buffer;
	protected PredicateEvaluator pe;
	protected String outFile;
	protected MonValuation valuation;
	protected ParserPlugin mtlParser;
	protected RiTHMMTLVisitor mtlMon;
	protected HashMap<RiTHMSpecification, ParseTree> specsTrees;
	protected ArrayList<MonitoringEventListener> mlist;
	final static Logger logger = Logger.getLogger(MTLMonitor.class);
	public MTLMonitor()
	{
		mlist = new ArrayList<MonitoringEventListener>();
		currSpecStatus = new DefaultRiTHMSpecificationResult();
		buffer = new DefaultProgStateCollection();
		pe = null;
		mtlParser = new MTLParser("Metric Temporal Logic");
		mtlMon = new RiTHMMTLVisitor(buffer);
		specsTrees = new HashMap<RiTHMSpecification, ParseTree>();
		currSpecs = new DefaultRiTHMSpecificationCollection();
	}
	@Override
	public boolean setFormulas(RiTHMSpecificationCollection Specs) {
		// TODO Auto-generated method stub
		currSpecs = Specs;
		return false;
	}

	@Override
	public boolean synthesizeMonitors(RiTHMSpecificationCollection Specs) {
		// TODO Auto-generated method stub
		for(RiTHMSpecification rSpec: Specs)
		{
			currSpecs.add(rSpec);
			specsTrees.put(rSpec, mtlParser.getTreeforSpec(rSpec));
		}
		return true;
	}

	@Override
	public boolean synthesizeMonitors(String specDetails, boolean isFile) {
		// TODO Auto-generated method stub
		BufferedReader reader = null;
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
            String line = null;
            while ((line = reader.readLine()) != null) {
            	RiTHMSpecification rSpec = new DefaultRiTHMSpecification(line);
            	currSpecs.add(rSpec);
            	specsTrees.put(rSpec, mtlParser.getTreeforSpec(rSpec));
            }
        } catch (IOException e) {
        	System.err.println(e.getMessage());
        	return false;
        } 

        finally {
            try {
                reader.close();
            } catch (IOException e) {
            	System.err.println(e.getMessage());
            	return false;
            }
        }
        return true;
	}

	@Override
	public RiTHMResultCollection runMonitor() {
		// TODO Auto-generated method stub
		for(int i =0; i < currSpecs.length();i++)
		{
			String resName = mtlMon.visit(specsTrees.get(currSpecs.at(i)));
			RiTHMTruthValue tempTval = currSpecStatus.getResult(currSpecs.at(i));
			currSpecStatus.setResult(currSpecs.at(i), mtlMon.getTruthValuation(resName));
			if(tempTval != null){
				if(tempTval.getTruthValueDescription().equals(currSpecStatus.getResult(currSpecs.at(i)).getTruthValueDescription()))
				{
					for(MonitoringEventListener mel: mlist)
						mel.MonValuationChanged(currSpecs.at(i), currSpecStatus.getResult(currSpecs.at(i)));
				}
			}
				
		}
		return currSpecStatus;
	}

	@Override
	public boolean setTraceFile(String FileName) {
		// TODO Auto-generated method stub
	
		return false;
	}

	@Override
	public boolean fillBuffer(ProgState ps) {
		buffer.add(ps);
//		buffer.add((PredicateState)pe.evaluatePredicates());
		return true;
	}

	@Override
	public void setMonitoringEventListener(MonitoringEventListener mel) {
		// TODO Auto-generated method stub
		this.mlist.add(mel);
	}

	@Override
	public void setMonitorValuation(MonValuation val) {
		// TODO Auto-generated method stub
		this.valuation = val;

	}

	@Override
	public void setPredicateEvaluator(PredicateEvaluator pe) {
		// TODO Auto-generated method stub
		this.pe  = pe;
		mtlMon.setPredicateEvaluator(pe);
	}

	@Override
	public void setOutFile(String outFile) {
		// TODO Auto-generated method stub
		this.outFile = outFile;
	}

	@Override
	public void setParser(ParserPlugin parser) {
		// TODO Auto-generated method stub
		this.mtlParser = parser;
	}

}
