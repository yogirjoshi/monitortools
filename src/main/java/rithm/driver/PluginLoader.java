package rithm.driver;
import java.lang.reflect.*;
import java.util.ServiceLoader;

import org.apache.log4j.Logger;

import rithm.commands.RitHMParameterValidator;
import rithm.core.ParserPlugin;
import rithm.core.RitHMPlugin;
public class PluginLoader {
	final static Logger logger = Logger.getLogger(PluginLoader.class);
	@SuppressWarnings("rawtypes")
	protected static <T> RitHMPlugin loadPluginWithType(Class<T> classType, String pluginName)
	{
		RitHMPlugin result = null; 
		if(!RitHMPlugin.class.isAssignableFrom(classType))
		{
			logger.fatal(classType.toString() + "must extend RitHMPlugin");
			throw new IllegalArgumentException("Not a valid RitHM Plugin!!!");
		}
		ServiceLoader<RitHMPlugin> srvLoader = ServiceLoader.load(RitHMPlugin.class);
		for(RitHMPlugin genPlugin: srvLoader)
		{
			RitHMPlugin rPlugin = (RitHMPlugin)genPlugin; 
			if(rPlugin.getName().equals(pluginName))
				return rPlugin;
		}
		if(result == null){
			logger.fatal(pluginName + "is not Loaded!!!");
			throw new IllegalArgumentException(pluginName + "is Not a valid RitHM Plugin!!!");
		}
		return result;
	}
}
