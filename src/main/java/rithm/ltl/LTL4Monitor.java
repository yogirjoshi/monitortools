package rithm.ltl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
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
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.antlr.v4.parse.ANTLRParser.throwsSpec_return;
import org.apache.log4j.Logger;

import rithm.basemonitors.RitHMBaseMonitor;
import rithm.core.MonState;
import rithm.core.MonValuation;
import rithm.core.MonitoringEventListener;
import rithm.core.ParserPlugin;
import rithm.core.PredicateEvaluator;
import rithm.core.PredicateState;
import rithm.core.ProgState;
import rithm.core.RitHMMonitor;
import rithm.core.RitHMResultCollection;
import rithm.core.RitHMSpecification;
import rithm.core.RitHMSpecificationCollection;
import rithm.core.RitHMTruthValue;
import rithm.defaultcore.DefaultMonState;
import rithm.defaultcore.DefaultPredicateState;
import rithm.defaultcore.DefaultRiTHMSpecification;
import rithm.defaultcore.DefaultRiTHMSpecificationResult;
import rithm.defaultcore.DefaultRiTHMTruthValue;

// TODO: Auto-generated Javadoc
/**
 * The Class LTL4Monitor.
 */
public class LTL4Monitor extends RitHMBaseMonitor implements RitHMMonitor
{
	
	/** The curr spec status. */
	protected RitHMResultCollection currSpecStatus; 
	
	/** The initial states. */
	protected HashMap<String, MonState> initialStates;
	
	/** The current states. */
	protected HashMap<String, MonState> currentStates;
	
	/** The prop set. */
	protected Properties propSet;
	
	/** The ltltools dirname. */
	protected String ltltoolsDirname;
	
	/** The ltl mon out dirname. */
	protected String ltlMonOutDirname;

	/** The spec list. */
	protected ArrayList<String> specList;
	
	/** The Constant logger. */
	final static Logger logger = Logger.getLogger(LTL4Monitor.class);
	
	/**
	 * Instantiates a new LT l4 monitor.
	 */
	public LTL4Monitor()
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
			logger.fatal(ioException.getMessage());
		}
		specList = new ArrayList<String>();
	}
	
	/* (non-Javadoc)
	 * @see rithm.core.RiTHMMonitor#synthesizeMonitors(rithm.core.RiTHMSpecificationCollection)
	 */
	public boolean synthesizeMonitors(RitHMSpecificationCollection specs) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not yet supported");
	}
	
	/* (non-Javadoc)
	 * @see rithm.core.RiTHMMonitor#fillBuffer(rithm.core.ProgState)
	 */
	public boolean fillBuffer(ProgState ps) {
		// TODO Auto-generated method stub
		pe.SetProgStateObj(ps);
		buffer.add((PredicateState)pe.evaluatePredicates());
		return true;
	}

	/* (non-Javadoc)
	 * @see rithm.basemonitors.RiTHMBaseMonitor#setFormulas(rithm.core.RiTHMSpecificationCollection)
	 */
	public boolean setFormulas(RitHMSpecificationCollection specs) {
		// TODO Auto-generated method stub
		super.setFormulas(specs);
		for(int i =0; i < specs.length();i++)
			currSpecStatus.setResult(specs.at(i), this.valuation.getDefaultValuation());
		return false;
	}
	
	/**
	 * Creates the monsfrom tools.
	 *
	 * @param line the line
	 * @param origFormat the orig format
	 * @param Filenames the filenames
	 * @param specCount the spec count
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws InterruptedException the interrupted exception
	 */
	protected void createMonsfromTools(String line, String origFormat, ArrayList<String> Filenames, int specCount) throws IOException, InterruptedException
	{
//		System.out.println(line);
    	ProcessBuilder p = new ProcessBuilder();
    	p.directory(new File(ltltoolsDirname));
    	p.command("/bin/bash", "./ltl2monLTL4", "\""+ line +"\"");
    	p.redirectOutput(new File(ltlMonOutDirname + Integer.toString(specCount) + ".txt"));
    	p.redirectError(new File(ltlMonOutDirname + Integer.toString(specCount) + "err.txt"));
    	Filenames.add(ltlMonOutDirname + Integer.toString(specCount) + ".txt");
    	specList.add(specCount, origFormat);
    	Process ps = p.start();
    	ps.waitFor();
	}
	
	/**
	 * Creates the lookup table.
	 *
	 * @param states the states
	 * @param Filenames the filenames
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	protected void createLookupTable(ArrayList<DefaultMonState> states, ArrayList<String> Filenames) throws IOException {
		String Pattern1 = "(\"\\([-]*[0-9]+,[ ]+[-]*[0-9]+\\)\")[ ]+->[ ]+(\"\\([-]*[0-9]+,[ ]+[-]*[0-9]+\\)\")[ ]+\\[label[ ]+=[ ]+\"\\(([0-9a-z]+(&&[0-9a-z]+)*)\\)\"\\]";
		String Pattern2 = "(\"\\([-]*[0-9]+,[ ]+[-]*[0-9]+\\)\")[ ]+->[ ]+(\"\\([-]*[0-9]+,[ ]+[-]*[0-9]+\\)\")[ ]+\\[label[ ]+=[ ]+\"(\\(<empty>\\))\"\\]";
		String Pattern3 = "(\"\\([-]*[0-9]+,[ ]+[-]*[0-9]+\\)\")[ ]+\\[label=\"\\([-]*[0-9]+,[ ]+[-]*[0-9]+\\)\",[ ]+style=[a-z]+,[ ]+color=([a-z0-9]+)\\]";
		int spec_count = 0;
		String line;
		BufferedReader br;
		Pattern regex1 = Pattern.compile(Pattern1);
		Pattern regex2 = Pattern.compile(Pattern2);
		Pattern regex3 = Pattern.compile(Pattern3);
		for(String Filename: Filenames)
		{
			File errFile = new File(ltlMonOutDirname + Integer.toString(spec_count) + "err.txt");
			if(errFile.length() > 0)
				logger.fatal("Error in running ltl3tools! Please contact the support team!");
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
					logger.debug("Predicate -> <empty>");
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
	
	/* (non-Javadoc)
	 * @see rithm.core.RiTHMMonitor#synthesizeMonitors(java.lang.String, boolean)
	 */
	public boolean synthesizeMonitors(String specDetails, boolean isFile) {
		// TODO Auto-generated method stub
		ArrayList<String> Filenames = new ArrayList<String>();
		int specCount = 0;
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
            	String origFormat = line;
            	line = parser.rewriteSpec(new DefaultRiTHMSpecification(line));
            	createMonsfromTools(line,origFormat, Filenames, specCount);
            	specCount++;
            }
        } catch (IOException e) {
        	logger.fatal(e.getMessage());
        	return false;
        } 
        catch(InterruptedException e){
        	logger.fatal(e.getMessage());
        	return false;
        }
        finally {
            try {
                reader.close();
            } catch (Exception e) {
            	logger.fatal(e.getMessage());
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
			logger.fatal(e.getMessage());
			return false;
		}
		return true;
	}

	/**
	 * Sets the pred state.
	 *
	 * @param dpState the dp state
	 * @param predsNeeded the preds needed
	 */
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
	
	/* (non-Javadoc)
	 * @see rithm.core.RiTHMMonitor#runMonitor()
	 */
	public RitHMResultCollection runMonitor(boolean isLastInvocation) {
		// TODO Auto-generated method stub
		openVerboseFiles();
		writeMonitoriLogFile("<html>");
		writeMonitoriLogFile("<body>");
		for(int i =0; i < buffer.size();i++)
		{

			DefaultPredicateState topState = (DefaultPredicateState)buffer.get(i);
			writeMonitoriLogFile("Event:" + Integer.toString(i) +" Timestamp:" + topState.gettimeStamp());
			for(int j = 0; j < currentStates.size();j++)
			{
				DefaultPredicateState dpPredState = new DefaultPredicateState((DefaultPredicateState)buffer.get(i));
				ArrayList<String> predsForthisSpec = parser.getPredsForSpec(specList.get(j));

				setPredState(dpPredState, predsForthisSpec);

				DefaultMonState nextState = (DefaultMonState)currentStates.get(Integer.toString(j)).GetNextMonState(dpPredState);

				if(nextState != null)
				{
					for(MonitoringEventListener ml: mlist)
						if(nextState != currentStates.get(Integer.toString(j)))
							ml.MonValuationChanged(new DefaultRiTHMSpecification(specList.get(j)), new DefaultRiTHMTruthValue(nextState.getValuation()));

					if(nextState.getValuation().equals("Violated") && isResetOnViolation())
						currentStates.put(Integer.toString(j),initialStates.get(Integer.toString(j)));
					else
						currentStates.put(Integer.toString(j),nextState);
					DefaultMonState ms1 = nextState;
					writeMonitoriLogFile("<div style=\"background: #B0B0B0 \">");

					if(ms1.getValuation().equals("Satisfied"))
					{
						//							outWriter.write("<div style=\"background: LightGreen\">");
						writeMonitoriLogFile("Specification: " + specList.get(j) + " => " + "<font color=\"Lime\">" + ms1.getValuation() + "</font>");
						//							outWriter.write("</div>");
					}
					if(ms1.getValuation().equals("Violated"))
					{
						//							outWriter.write("<div style=\"background: #FF9900\">");
						writeMonitoriLogFile("Specification: " + specList.get(j) + " => " + "<font color=\"Red\">" + ms1.getValuation() + "</font>");
						//							outWriter.write("</div>");
					}
					if(ms1.getValuation().equals("Validation status Unknown"))
					{
						//							outWriter.write("<div style=\"background: yellow\">");
						writeMonitoriLogFile("Specification: " + specList.get(j) + " => " + "<font color=\"Yellow\">" + ms1.getValuation() + "</font>");
						//							outWriter.write("</div>");
					}
					if(ms1.getValuation().equals("Presumably Satisfied"))
					{
						//							outWriter.write("<div style=\"background: yellow\">");
						writeMonitoriLogFile("Specification: " + specList.get(j) + " => " + "<font color=\"#728C00\">" + ms1.getValuation() + "</font>");
						//							outWriter.write("</div>");
					}
					if(ms1.getValuation().equals("Presumably Violated"))
					{
						//							outWriter.write("<div style=\"background: yellow\">");
						writeMonitoriLogFile("Specification: " + specList.get(j) + " => " + "<font color=\"Brown\">" + ms1.getValuation() + "</font>");
						//							outWriter.write("</div>");
					}
					writeMonitoriLogFile("</div>");
					currSpecStatus.setResult(new DefaultRiTHMSpecification(specList.get(j)),new DefaultRiTHMTruthValue(ms1.getValuation()));

					writeMonitorPlotFile(specList.get(j) + "," + topState.gettimeStamp() + "," + ms1.getValuation() + "\n");

				}
				else
				{
					logger.fatal("State is null !! FSM based monitor creation for LTL failed !!");
				}
			}
		}
		writeMonitoriLogFile("</body>");
		writeMonitoriLogFile("</html>");
		try {
			if(isLastInvocation)
				closeVerboseFiles();
		} catch (IOException ie) {
			// TODO Auto-generated catch block
			logger.fatal(ie.getMessage());
		}
		return currSpecStatus;
	}

}
