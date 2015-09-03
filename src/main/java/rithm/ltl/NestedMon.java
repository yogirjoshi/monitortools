package rithm.ltl;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import rithm.core.MonState;
import rithm.core.PredicateState;
import rithm.core.RitHMTruthValue;

public class NestedMon {
	protected String objID;
	protected RitHMTruthValue currTruthval;
	protected ConcurrentHashMap<String, NestedMon> innerMons;
	protected ArrayList<PredicateState> events;
	protected MonState myState;
	protected boolean isLeaf;
	public NestedMon(boolean leaf){
		if(!leaf){
			isLeaf = false;
			innerMons = new ConcurrentHashMap<>();
		}
		else{
			isLeaf = true;
//			events = new ArrayList<>();
		}
	}
}
