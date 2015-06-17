package rithm.driver;

import rithm.core.ProgState;

public class RiTHMMonitorCommand extends RiTHMCommand{
	protected ProgState currProgState;
	public RiTHMMonitorCommand(ProgState pState)
	{
		super();
		this.currProgState = pState;
	}
	public ProgState getProgState()
	{
		return currProgState;
	}
}
