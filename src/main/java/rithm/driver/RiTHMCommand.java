package rithm.driver;

import java.util.HashMap;

import rithm.core.ProgState;

public class RiTHMCommand {
	protected String commandString;
	protected HashMap<String, Object> params;
	
	public String getCommandString()
	{
		return commandString;
	}
	public Object getParam(String key)
	{
		return params.get(key);
	}
}
