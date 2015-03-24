package rithm.ltl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import rithm.core.MonValuation;
import rithm.core.RiTHMTruthValue;
import rithm.defaultcore.*;
public class LTL4MonValuation implements MonValuation{


	protected String currValuation;
	protected HashMap<String, String> semantics;
	protected ArrayList<String> truthValues;
	public LTL4MonValuation()
	{
		semantics = new HashMap<String, String>();
		truthValues = new ArrayList<String>();
		truthValues.add("yellow");
		truthValues.add("green");
		truthValues.add("red");
		truthValues.add("darkseagreen1");
		truthValues.add("pink");
		semantics.put("yellow", "Validation status Unknown");
		semantics.put("green", "Satisfied");
		semantics.put("red", "Violated");
		semantics.put("darkseagreen1", "Presumably Satisfied");
		semantics.put("pink", "Presumably Violated");
	}

	public RiTHMTruthValue getDefaultValuation() {
		// TODO Auto-generated method stub
		return new DefaultRiTHMTruthValue(this.semantics.get("yellow"));
	}
	public String getSemanticDescription(RiTHMTruthValue rithmTruthVal) {
		// TODO Auto-generated method stub
		return this.semantics.get(rithmTruthVal.getTruthValueDescription());
	}

	public void setValues(Collection<RiTHMTruthValue> truthValues) {
		// TODO Auto-generated method stub
//		if(truthValues instanceof ArrayList<?>)
//		{
		for(RiTHMTruthValue each_truth_value: truthValues)
			this.truthValues.add(each_truth_value.getTruthValueDescription());
//		}
	}

	public void setSemanticDescription(HashMap<RiTHMTruthValue, String> desc) {
		// TODO Auto-generated method stub
		for(RiTHMTruthValue each_key : desc.keySet())
		{
			this.semantics.put(each_key.getTruthValueDescription(), desc.get(each_key));
		}
		
	}

	public String getSemanticDescription(String semanticDesc) {
		// TODO Auto-generated method stub
		return this.semantics.get(semanticDesc);
	}

}
