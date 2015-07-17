package rithm.driver;
import java.lang.reflect.*;
import java.util.ServiceLoader;

import org.apache.log4j.Logger;

import rithm.commands.RitHMParameterValidator;
import rithm.core.DataFactory;
import rithm.core.MonitoringEventListener;
import rithm.core.ParserPlugin;
import rithm.core.PredicateEvaluator;
import rithm.core.RitHMMonitor;
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
	public static void main(String args[]){
		ServiceLoader<RitHMPlugin> srvLoader = ServiceLoader.load(RitHMPlugin.class);
		
		String leftAlignFormat = "| %-20s | %-20s |%n";

		System.out.format("+----------------------+----------------------+%n");
		System.out.printf("| Plugin Name          | Plugin Type          |%n");
		System.out.format("+----------------------+----------------------+%n");

		StringBuilder name = new StringBuilder();
		for(RitHMPlugin genPlugin: srvLoader){

			if(genPlugin instanceof RitHMMonitor)
				name.append("monitorClass");
			if(genPlugin instanceof DataFactory)
				name.append("traceParserClass");
			if(genPlugin instanceof ParserPlugin)
				name.append("specParserClass");
			if(genPlugin instanceof PredicateEvaluator)
				name.append("predicateEvaluator");
			if(genPlugin instanceof MonitoringEventListener)
				name.append("monEventListener");
			System.out.format(leftAlignFormat, genPlugin.getName(),name.toString());
			name.setLength(0);
			System.out.format("+----------------------+----------------------+%n");
		}
	}
}
