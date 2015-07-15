package rithm.mtl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import rithm.core.MonValuation;
import rithm.core.RitHMTruthValue;
import rithm.defaultcore.DefaultRiTHMTruthValue;

// TODO: Auto-generated Javadoc
/**
 * The Class TwoValuedValuation.
 */
public class TwoValuedValuation implements MonValuation {
	
	/** The semantics. */
	protected HashMap<String, String> semantics;
	
	/** The truth values. */
	protected ArrayList<String> truthValues;
	
	/**
	 * Instantiates a new two valued valuation.
	 */
	public TwoValuedValuation()
	{
		semantics = new HashMap<String, String>();
		truthValues = new ArrayList<String>();
		truthValues.add("true");
		truthValues.add("false");
		semantics.put("true", "Satisfied");
		semantics.put("false", "Violated");
	}
	
	/* (non-Javadoc)
	 * @see rithm.core.MonValuation#setValues(java.util.ArrayList)
	 */
	@Override
	public void setValues(ArrayList<RitHMTruthValue> TruthValues) {
		// TODO Auto-generated method stub
	}

	/* (non-Javadoc)
	 * @see rithm.core.MonValuation#setSemanticDescription(java.util.HashMap)
	 */
	@Override
	public void setSemanticDescription(HashMap<RitHMTruthValue, String> Desc) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see rithm.core.MonValuation#getSemanticDescription(rithm.core.RiTHMTruthValue)
	 */
	@Override
	public String getSemanticDescription(RitHMTruthValue Semantic) {
		// TODO Auto-generated method stub
		return this.semantics.get(Semantic);
	}

	/* (non-Javadoc)
	 * @see rithm.core.MonValuation#getDefaultValuation()
	 */
	@Override
	public RitHMTruthValue getDefaultValuation() {
		// TODO Auto-generated method stub
		return new DefaultRiTHMTruthValue("unknown");
	}
	
}
