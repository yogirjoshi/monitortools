package rithm.driver;

import java.util.HashMap;

import rithm.core.ProgState;

public class RiTHMCommand {
	protected String commandString;
	protected HashMap<String, Object> params;
	
	public RiTHMCommand(String cosString)
	{
		this.commandString = cosString;
		params = null;
	}
	public RiTHMCommand()
	{
//		this.commandString = cosString;
		params = null;
	}
	public String getCommandString()
	{
		return commandString;
	}
	public void setCommandString(String cString)
	{
		this.commandString = cString;
	}
	public Object getParam(String key)
	{
		return params.get(key);
	}
	public void setParam(String key, Object val)
	{
		params.put(key, val);
	}
}
