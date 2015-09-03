package rithm.ltl;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.BinaryOperator;

import org.apache.log4j.Logger;

import com.sun.management.OperatingSystemMXBean;

import rithm.basemonitors.RitHMBaseMonitor;
import rithm.core.MonState;
import rithm.core.MonitoringEventListener;
import rithm.core.PredicateState;
import rithm.core.ProgState;
import rithm.core.RitHMMonitor;
import rithm.core.RitHMResultCollection;
import rithm.core.RitHMSpecification;
import rithm.core.RitHMSpecificationCollection;
import rithm.core.RitHMTruthValue;
import rithm.defaultcore.DefaultMonState;
import rithm.defaultcore.DefaultPredicateState;
import rithm.defaultcore.DefaultRiTHMSpecification;
import rithm.defaultcore.DefaultRiTHMTruthValue;

class RedOperator implements BinaryOperator<RitHMTruthValue>{

	@Override
	public RitHMTruthValue apply(RitHMTruthValue t, RitHMTruthValue u) {
		// TODO Auto-generated method stub
		if(t.getTruthValueDescription().equals("Unknown") || u.getTruthValueDescription().equals("Unknown"))
			return new DefaultRiTHMTruthValue("Unknown");
		
		if(t.getTruthValueDescription().equals("Sat") &&  u.getTruthValueDescription().equals("Sat"))
			return new DefaultRiTHMTruthValue("Sat");
		else
			return new DefaultRiTHMTruthValue("UnSat");
	}
	
}

public class FOLTLMonitor extends LTLMonitor implements RitHMMonitor{
	public boolean parallelMode;
	final static Logger logger = Logger.getLogger(FOLTLMonitor.class);
	protected HashMap<RitHMSpecification, NestedMon> sliceStruct;
	protected HashMap<RitHMSpecification,ArrayList<String>> specObjNames;
	public FOLTLMonitor() {
		// TODO Auto-generated constructor stub
		super();
		this.parallelMode = false;
		sliceStruct = new HashMap<RitHMSpecification, NestedMon>();
		specObjNames = new HashMap<RitHMSpecification, ArrayList<String>>();
	}
	public long getJVMCpuTime() {
		long lastProcessCpuTime = 0;
		try {
			if (ManagementFactory.getOperatingSystemMXBean() instanceof OperatingSystemMXBean) {
				lastProcessCpuTime=((com.sun.management.OperatingSystemMXBean)ManagementFactory.getOperatingSystemMXBean()).getProcessCpuTime();
			}
		}
		catch (  ClassCastException e) {
			System.out.println(e.getMessage());
		}finally{
			return lastProcessCpuTime;
		}
	}
	@Override
	public boolean synthesizeMonitors(String specDetails, boolean isFile) {
		// TODO Auto-generated method stub
		super.synthesizeMonitors(specDetails, isFile);
		for(RitHMSpecification rSpec: parser.getSpecs()){
			specObjNames.put(rSpec, parser.getObjectIDs(rSpec.getTextDescription()));
		}
		return true;
	}
	
	
	@Override
	public boolean synthesizeMonitors(RitHMSpecificationCollection specs) {
		// TODO Auto-generated method stub
		super.synthesizeMonitors(specs);
		for(RitHMSpecification rSpec: parser.getSpecs()){
			specObjNames.put(rSpec, parser.getObjectIDs(rSpec.getTextDescription()));
		}
		return true;
	}
	public int getNoOfObjects(RitHMSpecification rSpec){
		if(!sliceStruct.containsKey(rSpec))
			return 0;
		else
			return sliceStruct.get(rSpec).innerMons.size();
	}
	@Override
	public boolean fillBuffer(ProgState ps) {
		// TODO Auto-generated method stub
		pe.SetProgStateObj(ps);
		PredicateState currPred = (PredicateState)pe.evaluatePredicates();
//		logger.debug(currPred.toString());
		for(int j = 0; j < parser.getSpecs().length(); j++){
			RitHMSpecification rSpec = parser.getSpecs().at(j);
			ArrayList<String> objNames = specObjNames.get(rSpec);
			String currObjID = "ROOT";
			NestedMon currMon;
			if(!sliceStruct.containsKey(rSpec))
				sliceStruct.put(rSpec,new NestedMon(false));
			currMon = sliceStruct.get(rSpec);
			for(int i = 0; i < objNames.size();i++){
				String aObjName = objNames.get(i);
				currObjID = currPred.getObjID(aObjName);
				if(i == objNames.size()-1){
					if(!currMon.innerMons.containsKey(currObjID)){
						NestedMon nMon =  new NestedMon(true);
						nMon.objID = aObjName;
						nMon.myState = new DefaultMonState((DefaultMonState)currentStates.get(Integer.toString(j)));
						currMon.innerMons.put(currObjID, nMon);
//						logger.debug("Added Leaf " + nMon.objID  + "=" +  currObjID);
					}
				}else{
					if(!currMon.innerMons.containsKey(currObjID)){
						NestedMon nMon =  new NestedMon(false);
						nMon.objID = aObjName;
						currMon.innerMons.put(currObjID, nMon);
//						logger.debug("Added " + nMon.objID + "=" +  currObjID);
					}
				}
				currMon = currMon.innerMons.get(currObjID);
			}
			//TODO fix this
			currMon.myState = runLTLMonitor(j, currPred, currMon.myState);
//			System.out.println(currMon.events.size());
		}
		return true;
	}

	@Override
	public RitHMResultCollection runMonitor(boolean isLastInvocation) {
		long beg = System.nanoTime();
		long begCPU = getJVMCpuTime();
		RitHMSpecificationCollection rSpecCollection = parser.getSpecs();
		for(int i = 0; i < rSpecCollection.length();i++){
			NestedMon rootMonforSpec = sliceStruct.get(rSpecCollection.at(i));
			RitHMTruthValue retVal = traversNestedMon(i, rootMonforSpec);
			currSpecStatus.setResult(rSpecCollection.at(i), retVal);
//			logger.debug(rSpecCollection.at(i).getTextDescription() + retVal.getTruthValueDescription());
		}
		long end = System.nanoTime();
		long endCPU = getJVMCpuTime();
		System.out.println("Exec Time " + parallelMode + ":"
		+ TimeUnit.MILLISECONDS.convert((end-beg),TimeUnit.NANOSECONDS));
		System.out.println("CPU Time  " + parallelMode + ":"
		+ TimeUnit.MILLISECONDS.convert((endCPU-begCPU),TimeUnit.NANOSECONDS));
		return currSpecStatus;
		//run on every events LTL monitor
	}
	protected MonState runLTLMonitor(int specCount,PredicateState event, MonState currState){
		DefaultMonState ms1 = null;;

		DefaultPredicateState dpPredState = (DefaultPredicateState)event;

		ArrayList<String> predsForthisSpec = parser.getPredsForSpec(specList.get(specCount));
		setPredState(dpPredState, predsForthisSpec);
		//			logger.debug(i);
		//			logger.debug(i + "=" + topState.toString() );
		currState= (DefaultMonState)currState.GetNextMonState(dpPredState);

		if(currState != null)
		{

			ms1 = (DefaultMonState)currState;
			//					if(ms1.getValuation().equals("Satisfied"))
			//						writeMonitoriLogFile("Specification: " + specList.get(j) + " => " + "<font color=\"Lime\">" + ms1.getValuation() + "</font>");
			//					if(ms1.getValuation().equals("Violated"))
			//						writeMonitoriLogFile("Specification: " + specList.get(j) + " => " + "<font color=\"Red\">" + ms1.getValuation() + "</font>");
			//					if(ms1.getValuation().equals("Validation status Unknown"))
			//						writeMonitoriLogFile("Specification: " + specList.get(j) + " => " + "<font color=\"Yellow\">" + ms1.getValuation() + "</font>");
			//					writeMonitoriLogFile("</div>");

		}
		else
		{
			logger.fatal("State is null !! FSM based monitor creation for LTL failed !!");
		}
		return currState;
	}
	protected RitHMTruthValue traversNestedMon(int specCount, NestedMon aNode){
		RedOperator reducer = new RedOperator();

		if(aNode.isLeaf){
			RitHMTruthValue retVal;
//			aNode.myState = runLTLMonitor(specCount, aNode.events, aNode.myState);
			retVal = new DefaultRiTHMTruthValue(aNode.myState.getValuation());
			return retVal;
		}else{
			
			for(Map.Entry<String, NestedMon> childNodeEntry: aNode.innerMons.entrySet()){
				childNodeEntry.getValue().currTruthval = traversNestedMon(specCount, childNodeEntry.getValue());
			}
			
//			if(parallelMode && aNode.innerMons.size() >= 1000000){
			if(parallelMode){
				return  aNode.innerMons.values().parallelStream()
						.map((oneMon) -> oneMon.currTruthval)
						.reduce(reducer)
						.get();
			}else{
				return  aNode
						.innerMons
						.values()
						.stream()
						.map((oneMon) -> oneMon.currTruthval)
						.reduce(reducer)
						.get();
			}
						
		}
	}
	
	@Override
	public void clearBuffer() {
		// TODO Auto-generated method stub
		super.clearBuffer();
	}


	
}
