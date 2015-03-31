package rithm.mtl;
import rithm.core.MonValuation;
import rithm.core.MonitoringEventListener;
import rithm.core.ParserPlugin;
import rithm.core.PredicateEvaluator;
import rithm.core.ProgState;
import rithm.core.RiTHMMonitor;
import rithm.core.RiTHMResultCollection;
import rithm.core.RiTHMSpecificationCollection;
import rithm.parsertools.mtl.*;
public class MTLMonitor implements RiTHMMonitor{

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
