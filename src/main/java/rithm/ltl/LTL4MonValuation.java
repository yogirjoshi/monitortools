package rithm.ltl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import rithm.core.MonValuation;
import rithm.core.RitHMTruthValue;
import rithm.defaultcore.*;
// TODO: Auto-generated Javadoc

/**
 * The Class LTL4MonValuation.
 */
public class LTL4MonValuation implements MonValuation{


	/** The curr valuation. */
	protected String currValuation;
	
	/** The semantics. */
	protected HashMap<String, String> semantics;
	
	/** The truth values. */
	protected ArrayList<String> truthValues;
	
	/**
	 * Instantiates a new LT l4 mon valuation.
	 */
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

	/* (non-Javadoc)
	 * @see rithm.core.MonValuation#getDefaultValuation()
	 */
	public RitHMTruthValue getDefaultValuation() {
		// TODO Auto-generated method stub
		return new DefaultRiTHMTruthValue(this.semantics.get("yellow"));
	}
	
	/* (non-Javadoc)
	 * @see rithm.core.MonValuation#getSemanticDescription(rithm.core.RiTHMTruthValue)
	 */
	public String getSemanticDescription(RitHMTruthValue rithmTruthVal) {
		// TODO Auto-generated method stub
		return this.semantics.get(rithmTruthVal.getTruthValueDescription());
	}

	/* (non-Javadoc)
	 * @see rithm.core.MonValuation#setValues(java.util.ArrayList)
	 */
	public void setValues(ArrayList<RitHMTruthValue> truthValues) {
		// TODO Auto-generated method stub
//		if(truthValues instanceof ArrayList<?>)
//		{
		for(RitHMTruthValue each_truth_value: truthValues)
			this.truthValues.add(each_truth_value.getTruthValueDescription());
//		}
	}

	/* (non-Javadoc)
	 * @see rithm.core.MonValuation#setSemanticDescription(java.util.HashMap)
	 */
	public void setSemanticDescription(HashMap<RitHMTruthValue, String> desc) {
		// TODO Auto-generated method stub
		for(RitHMTruthValue each_key : desc.keySet())
		{
			this.semantics.put(each_key.getTruthValueDescription(), desc.get(each_key));
		}
		
	}

	/**
	 * Gets the semantic description.
	 *
	 * @param semanticDesc the semantic desc
	 * @return the semantic description
	 */
	public String getSemanticDescription(String semanticDesc) {
		// TODO Auto-generated method stub
		return this.semantics.get(semanticDesc);
	}

}
