package rithm.basemonitors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import rithm.core.MonValuation;
import rithm.core.MonitoringEventListener;
import rithm.core.ParserPlugin;
import rithm.core.PredicateEvaluator;
import rithm.core.PredicateState;
import rithm.core.ProgState;
import rithm.core.RiTHMMonitor;
import rithm.core.RiTHMSpecification;
import rithm.core.RiTHMSpecificationCollection;
import rithm.core.RiTHMTruthValue;

public abstract class RiTHMBaseMonitor implements RiTHMMonitor{
	
	protected String outFileName;
	protected String plotFileName;
	protected boolean pipeMode = false;
	protected RiTHMSpecificationCollection currSpecs;
	protected ArrayList<MonitoringEventListener> mlist;
	protected MonValuation valuation;
	protected PredicateEvaluator pe;
	protected ParserPlugin parser;
	protected HashMap<RiTHMSpecification, String> specResmap;
	protected ArrayList<PredicateState> resPred;
	protected ArrayList<PredicateState> predList;
	protected ArrayList<PredicateState> buffer;
	public RiTHMBaseMonitor()
	{
		mlist = new ArrayList<MonitoringEventListener>();
		predList = null;
	}
	@Override
	public void setpipeMode(boolean pipeMode) {
		// TODO Auto-generated method stub
		this.pipeMode = pipeMode;
		if(pipeMode)
		{
			specResmap = new HashMap<RiTHMSpecification, String>();
			resPred = new ArrayList<PredicateState>();
		}
		else
		{
			specResmap = null;
			resPred = null;
		}
	}

	@Override
	public boolean getpipeMode() {
		// TODO Auto-generated method stub
		return pipeMode;
	}

	@Override
	public boolean setFormulas(RiTHMSpecificationCollection specs) {
		if(specs == null)
			throw new IllegalArgumentException("specs cannot be null");
		// TODO Auto-generated method stub
		currSpecs = specs;
		return true;
	}

	@Override
	public boolean setTraceFile(String FileName) {
		// TODO Auto-generated method stub
		return false;
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
	}

	@Override
	public void setOutFile(String outFile) {
		// TODO Auto-generated method stub
		this.outFileName = outFile;
	}

	@Override
	public void setParser(ParserPlugin parser) {
		// TODO Auto-generated method stub
		this.parser = parser;
	}
	
	@Override
	public void setResultPredicateName(RiTHMSpecification spec, String resName) {
		// TODO Auto-generated method stub
		if(pipeMode)
			specResmap.put(spec, resName);
		else
			throw new UnsupportedOperationException("Only supported in pipeMode");
	}
	@Override
	public List<PredicateState> getTruthValueasPredicate() {
		// TODO Auto-generated method stub
		if(pipeMode)
			return resPred;
		else
			throw new UnsupportedOperationException("Only supported in pipeMode");
	}

	@Override
	public PredicateState getTruthValueasPredicateAt(RiTHMSpecification spec, int i) {
		// TODO Auto-generated method stub
		if(pipeMode)
			return resPred.get(i);
		else
			throw new UnsupportedOperationException("Only supported in pipeMode");
	}
	@Override
	public boolean setBuffer(List<PredicateState> ps) {
		// TODO Auto-generated method stub
		if(ps instanceof ArrayList<?>)
			this.buffer = (ArrayList<PredicateState>)ps;
		else
			throw new IllegalArgumentException("Currently only ArrayList is supported!!!!");
		return false;
	}
	@Override
	public void setPlotFile(String plotFile) {
		// TODO Auto-generated method stub
		this.plotFileName = plotFile;
	}
	
}
