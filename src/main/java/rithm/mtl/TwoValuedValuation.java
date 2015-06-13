package rithm.mtl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import rithm.core.MonValuation;
import rithm.core.RiTHMTruthValue;
import rithm.defaultcore.DefaultRiTHMTruthValue;

public class TwoValuedValuation implements MonValuation {
	protected HashMap<String, String> semantics;
	protected ArrayList<String> truthValues;
	
	public TwoValuedValuation()
	{
		semantics = new HashMap<String, String>();
		truthValues = new ArrayList<String>();
		truthValues.add("true");
		truthValues.add("false");
		semantics.put("true", "Satisfied");
		semantics.put("false", "Violated");
	}
	@Override
	public void setValues(ArrayList<RiTHMTruthValue> TruthValues) {
		// TODO Auto-generated method stub
	}

	@Override
	public void setSemanticDescription(HashMap<RiTHMTruthValue, String> Desc) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getSemanticDescription(RiTHMTruthValue Semantic) {
		// TODO Auto-generated method stub
		return this.semantics.get(Semantic);
	}

	@Override
	public RiTHMTruthValue getDefaultValuation() {
		// TODO Auto-generated method stub
		return new DefaultRiTHMTruthValue("unknown");
	}
	
}
