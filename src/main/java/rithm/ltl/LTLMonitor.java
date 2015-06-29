package rithm.ltl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Properties;

import org.apache.log4j.HTMLLayout;
import org.apache.log4j.Logger;
import org.apache.log4j.RollingFileAppender;
import org.apache.log4j.SimpleLayout;

import rithm.basemonitors.RiTHMBaseMonitor;
import rithm.core.MonState;
import rithm.core.MonValuation;
import rithm.core.MonitoringEventListener;
import rithm.core.ParserPlugin;
import rithm.core.PredicateEvaluator;
import rithm.core.PredicateState;
import rithm.core.ProgState;
import rithm.core.RiTHMMonitor;
import rithm.core.RiTHMResultCollection;
import rithm.core.RiTHMSpecification;
import rithm.core.RiTHMSpecificationCollection;
import rithm.core.RiTHMTruthValue;
import rithm.parsertools.ltl.LTLParser;
import rithm.defaultcore.*;
import rithm.driver.RiTHMClientHandler;
import rithm.driver.RiTHMSecureServer;

public class LTLMonitor extends RiTHMBaseMonitor implements RiTHMMonitor
{

	protected RiTHMResultCollection currSpecStatus; 
	
	
	protected HashMap<String, MonState> initialStates;
	protected HashMap<String, MonState> currentStates;
	
	protected Properties propSet;
	protected String ltltoolsDirname;
	protected String ltlMonOutDirname;
	
	protected ArrayList<String> specList;
	
	final static Logger logger = Logger.getLogger(LTLMonitor.class);
	public LTLMonitor()
	{
		buffer = new ArrayList<PredicateState>();
		currSpecStatus = new DefaultRiTHMSpecificationResult();
		initialStates = new HashMap<String, MonState>();
		currentStates = new HashMap<String, MonState>();
		mlist = new ArrayList<MonitoringEventListener>();
		propSet = new Properties();
		try
		{
			propSet.load(Thread.currentThread().getContextClassLoader()
		             .getResourceAsStream("ltl3tools.properties"));
			ltltoolsDirname = (String)propSet.getProperty("ltl3toolsDirectory");
			ltlMonOutDirname = (String)propSet.getProperty("ltl3MonOutputDirectory");
		}
		catch(IOException ioException)
		{
			System.err.println(ioException.getMessage());
		}
		specList = new ArrayList<String>();
	}
	public boolean synthesizeMonitors(RiTHMSpecificationCollection specs) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not yet supported");
	}
	public boolean fillBuffer(ProgState ps) {
		// TODO Auto-generated method stub
		pe.SetProgStateObj(ps);;
		buffer.add((PredicateState)pe.evaluatePredicates());
		return false;
	}
	
	public boolean setFormulas(RiTHMSpecificationCollection specs) {
		// TODO Auto-generated method stub
		super.setFormulas(specs);
		for(int i =0; i < specs.length();i++)
			currSpecStatus.setResult(specs.at(i), this.valuation.getDefaultValuation());
		return false;
	}
	

	protected void createMonsfromTools(String line, String origFormat, ArrayList<String> Filenames, int specCount) throws IOException, InterruptedException
	{
    	ProcessBuilder p = new ProcessBuilder();
    	p.directory(new File(ltltoolsDirname));
    	p.command("/bin/bash", "./ltl2monLTL3", "\""+ line +"\"");
    	p.redirectOutput(new File(ltlMonOutDirname + Integer.toString(specCount) + ".txt"));
    	Filenames.add(ltlMonOutDirname + Integer.toString(specCount) + ".txt");
    	specList.add(specCount, origFormat);
    	Process ps = p.start();
    	ps.waitFor();
	}
	protected void createLookupTable(ArrayList<DefaultMonState> states, ArrayList<String> Filenames) throws IOException {
		String Pattern1 = "(\"\\([-]*[0-9]+,[ ]+[-]*[0-9]+\\)\")[ ]+->[ ]+(\"\\([-]*[0-9]+,[ ]+[-]*[0-9]+\\)\")[ ]+\\[label[ ]+=[ ]+\"\\(([0-9a-z]+(&&[0-9a-z]+)*)\\)\"\\]";
		String Pattern2 = "(\"\\([-]*[0-9]+,[ ]+[-]*[0-9]+\\)\")[ ]+->[ ]+(\"\\([-]*[0-9]+,[ ]+[-]*[0-9]+\\)\")[ ]+\\[label[ ]+=[ ]+\"(\\(<empty>\\))\"\\]";
		String Pattern3 = "(\"\\([-]*[0-9]+,[ ]+[-]*[0-9]+\\)\")[ ]+\\[label=\"\\([-]*[0-9]+,[ ]+[-]*[0-9]+\\)\",[ ]+style=[a-z]+,[ ]+color=([a-z]+)\\]";
		int spec_count = 0;
		String line;
		BufferedReader br;
		Pattern regex1 = Pattern.compile(Pattern1);
		Pattern regex2 = Pattern.compile(Pattern2);
		Pattern regex3 = Pattern.compile(Pattern3);
		for(String Filename: Filenames)
		{

			states.clear();
			br = new BufferedReader(new FileReader(Filename));
			String spec = br.readLine();
			while((line  = br.readLine()) != null)
			{
				Matcher m1 = regex1.matcher(line);
				Matcher m2 = regex2.matcher(line);
				Matcher m3 = regex3.matcher(line);
				if(m1.find())
				{
					String State1 = m1.group(1);
					DefaultMonState ds1 = new DefaultMonState(State1, "");
					if(!states.contains(ds1))
						states.add(ds1);
					else
						ds1 = states.get(states.indexOf(ds1));
					String State2 = m1.group(2);
					DefaultMonState ds2 = new DefaultMonState(State2, "");
					if(!states.contains(ds2))
						states.add(ds2);
					else
						ds2 = states.get(states.indexOf(ds2));

					DefaultPredicateState dp1 = new DefaultPredicateState();
					for (String retval: m1.group(3).split("&&")){
						dp1.setValue(retval, true);
						logger.debug("Predicate ->" + retval );
					}
					ds1.SetTransition(dp1, ds2);
					logger.debug(ds1.getState() + " to " + ds2.getState() );
				}
				if(m2.find())
				{
					String State1 = m2.group(1);
					DefaultMonState ds1 = new DefaultMonState(State1, "");
					if(!states.contains(ds1))
						states.add(ds1);
					else
						ds1 = states.get(states.indexOf(ds1));
					String State2 = m2.group(2);
					DefaultMonState ds2 = new DefaultMonState(State2, "");
					if(!states.contains(ds2))
						states.add(ds2);
					else
						ds2 = states.get(states.indexOf(ds2));

					ds1.SetTransition(new DefaultPredicateState(),ds2);
					logger.debug("Predicate ->" + "<empty>" );
					logger.debug(ds1.getState() + " to " + ds2.getState() );
				}
				if(m3.find())
				{
					int id = states.indexOf(new DefaultMonState(m3.group(1), ""));
					DefaultMonState state = states.get(id);
					state.setValuation(this.valuation.getSemanticDescription(new DefaultRiTHMTruthValue(m3.group(2))));
					logger.debug(state.getState() + " valuation ->" + state.getValuation());
					if(state.getState().contains("(0, 0)"))
					{
						this.initialStates.put(Integer.toString(spec_count), state);
						this.currentStates.put(Integer.toString(spec_count), state);
						currSpecStatus.setResult(new DefaultRiTHMSpecification(specList.get(spec_count)), this.valuation.getDefaultValuation());
					}
				}
			}
			br.close();
			spec_count++;
		}
			
	}
	public boolean synthesizeMonitors(String specDetails, boolean isFile) {
		// TODO Auto-generated method stub
		ArrayList<String> Filenames = new ArrayList<String>();
		int specCount = 0;
		logger.debug(specDetails);
		BufferedReader reader = null;
        try {
        	if(isFile)
        	{
        		reader = new BufferedReader(new FileReader(specDetails));
        	}
        	else
        	{
        		InputStream is = new ByteArrayInputStream(specDetails.getBytes());
        		reader = new BufferedReader(new InputStreamReader(is));
        	}
            String line = null;
            while ((line = reader.readLine()) != null) {
            	parser.appendSpec(new DefaultRiTHMSpecification(line));
	            logger.debug(line);	
            	String origFormat = line;
            	line = parser.rewriteSpec(new DefaultRiTHMSpecification(line));
            	createMonsfromTools(line,origFormat, Filenames, specCount);
            	specCount++;
            }
        } catch (IOException e) {
        	logger.debug(e.getMessage());
        	return false;
        } 
        catch(InterruptedException e){
        	logger.debug(e.getMessage());
        	return false;
        }
        finally {
            try {
                reader.close();
            } catch (Exception e) {
            	logger.debug(e.getMessage());
            	return false;
            }
        }
		ArrayList<DefaultMonState> states = new ArrayList<DefaultMonState>();
		try
		{
			createLookupTable(states,Filenames);
		}
		catch(IOException e)
		{
			logger.debug(e.getMessage());
			return false;
		}
		return true;
	}

	public void setPredState(DefaultPredicateState dpState,ArrayList<String> predsNeeded)
	{
		Iterator it = dpState.getpredValues().entrySet().iterator();
		while(it.hasNext())
		{
			Map.Entry<String, Boolean> pairs = (Map.Entry<String, Boolean>)it.next();
			if(!predsNeeded.contains(pairs.getKey()))
				it.remove();
		}
	}
	public RiTHMResultCollection runMonitor() {
		// TODO Auto-generated method stub
		BufferedWriter outWriter = null, plotWriter = null;
		try
		{
			outWriter = new BufferedWriter(new FileWriter(new File(outFileName)));
			plotWriter = new BufferedWriter(new FileWriter(new File(plotFileName)));
			outWriter.write("<html>");
			outWriter.write("<body>");
			for(int i =0; i < buffer.size();i++)
			{
				DefaultPredicateState topState = (DefaultPredicateState)buffer.get(i);
				outWriter.write("Event:" + Integer.toString(i) +" Timestamp:" + topState.gettimeStamp());
				for(int j = 0; j < currentStates.size();j++)
				{
					DefaultPredicateState dpPredState = new DefaultPredicateState((DefaultPredicateState)buffer.get(i));

					ArrayList<String> predsForthisSpec = parser.getPredsForSpec(specList.get(j));
					
					setPredState(dpPredState, predsForthisSpec);
//					System.out.println(dpPredState);
					DefaultMonState nextState = (DefaultMonState)currentStates.get(Integer.toString(j)).GetNextMonState(dpPredState);
					
					if(nextState != null)
					{
						currentStates.put(Integer.toString(j),nextState);
						DefaultMonState ms1 = (DefaultMonState)currentStates.get(Integer.toString(j));
						outWriter.write("<div style=\"background: #B0B0B0 \">");
						
						if(ms1.getValuation().equals("Satisfied"))
							outWriter.write("Specification: " + specList.get(j) + " => " + "<font color=\"Lime\">" + ms1.getValuation() + "</font>");
						if(ms1.getValuation().equals("Violated"))
							outWriter.write("Specification: " + specList.get(j) + " => " + "<font color=\"Red\">" + ms1.getValuation() + "</font>");
						if(ms1.getValuation().equals("Validation status Unknown"))
							outWriter.write("Specification: " + specList.get(j) + " => " + "<font color=\"Yellow\">" + ms1.getValuation() + "</font>");
						outWriter.write("</div>");
						currSpecStatus.setResult(new DefaultRiTHMSpecification(specList.get(j)),new DefaultRiTHMTruthValue(ms1.getValuation()));
						
						plotWriter.write(specList.get(j) + "," + topState.gettimeStamp() + "," + ms1.getValuation() + "\n");
						
						for(MonitoringEventListener ml: mlist)
							ml.MonValuationChanged(new DefaultRiTHMSpecification(specList.get(j)), new DefaultRiTHMTruthValue(ms1.getValuation()));
					}
					else
					{
						logger.fatal("State is null !! Something is wrong!!");
					}
				}
			}
			outWriter.write("</body>");
			outWriter.write("</html>");
		}catch(IOException io)
		{
			logger.fatal(io.getMessage());
		}finally{
			try {
				outWriter.close();
				plotWriter.close();
			} catch (IOException ie) {
				// TODO Auto-generated catch block
				logger.fatal(ie.getMessage());
			}
		}
		return currSpecStatus;
	}

}
