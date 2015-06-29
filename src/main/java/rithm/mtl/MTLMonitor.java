package rithm.mtl;
import java.io.BufferedReader;

import org.apache.log4j.Logger;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
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

import org.antlr.v4.runtime.tree.ParseTree;

import rithm.basemonitors.RiTHMBaseMonitor;
import rithm.core.MonValuation;
import rithm.core.MonitoringEventListener;
import rithm.core.ParserPlugin;
import rithm.core.PredicateEvaluator;
import rithm.core.PredicateState;
import rithm.core.ProgState;
import rithm.core.RiTHMMonitor;
import rithm.core.RiTHMProgStateCollection;
import rithm.core.RiTHMResultCollection;
import rithm.core.RiTHMSpecification;
import rithm.core.RiTHMSpecificationCollection;
import rithm.core.RiTHMTruthValue;
import rithm.defaultcore.DefaultPredicateState;
import rithm.defaultcore.DefaultProgStateCollection;
import rithm.defaultcore.DefaultRiTHMSpecification;
import rithm.defaultcore.DefaultRiTHMSpecificationCollection;
import rithm.defaultcore.DefaultRiTHMSpecificationResult;
import rithm.parsertools.mtl.*;
public class MTLMonitor extends RiTHMBaseMonitor implements RiTHMMonitor{
	
	protected RiTHMResultCollection currSpecStatus;
	protected RiTHMProgStateCollection bufferProgs;

	protected RiTHMMTLVisitor mtlMon;
	protected HashMap<RiTHMSpecification, ParseTree> specsTrees;
	protected HashMap<RiTHMSpecification, String> specToTreeNode;
	
	final static Logger logger = Logger.getLogger(MTLMonitor.class);
	public MTLMonitor()
	{
		currSpecStatus = new DefaultRiTHMSpecificationResult();
		bufferProgs = new DefaultProgStateCollection();
		mtlMon = new RiTHMMTLVisitor(bufferProgs);
		specsTrees = new HashMap<RiTHMSpecification, ParseTree>();
		currSpecs = new DefaultRiTHMSpecificationCollection();
		specToTreeNode = new HashMap<>();
	}
	
	@Override
	public boolean synthesizeMonitors(RiTHMSpecificationCollection Specs) {
		// TODO Auto-generated method stub
		for(RiTHMSpecification rSpec: Specs)
		{
			currSpecs.add(rSpec);
			specsTrees.put(rSpec, parser.getTreeforSpec(rSpec));
		}
		return true;
	}

	@Override
	public boolean synthesizeMonitors(String specDetails, boolean isFile) {
		// TODO Auto-generated method stub
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
            	RiTHMSpecification rSpec = new DefaultRiTHMSpecification(line);
            	currSpecs.add(rSpec);
            	specsTrees.put(rSpec, parser.getTreeforSpec(rSpec));
            }
        } catch (IOException e) {
        	logger.fatal(e.getMessage());
        	return false;
        } 

        finally {
            try {
                reader.close();
            } catch (IOException e) {
            	logger.fatal(e.getMessage());
            	return false;
            }
        }
        return true;
	}

	@Override
	public RiTHMResultCollection runMonitor() {
		// TODO Auto-generated method stub

 		for(int i =0; i < currSpecs.length();i++)
		{
			String resName = mtlMon.visit(specsTrees.get(currSpecs.at(i)));
			
			RiTHMTruthValue tempTval = currSpecStatus.getResult(currSpecs.at(i));
			currSpecStatus.setResult(currSpecs.at(i), mtlMon.getTruthValuation(resName,0));
			specToTreeNode.put(currSpecs.at(i), resName);
			if(tempTval != null){
				if(tempTval.getTruthValueDescription().equals(currSpecStatus.getResult(currSpecs.at(i)).getTruthValueDescription()))
				{
					for(MonitoringEventListener mel: mlist)
						mel.MonValuationChanged(currSpecs.at(i), currSpecStatus.getResult(currSpecs.at(i)));
				}
			}
				
		}
		BufferedWriter outWriter =null, plotWriter = null;
 		try{

 			outWriter = new BufferedWriter(new FileWriter(new File(outFileName)));
 			plotWriter = new BufferedWriter(new FileWriter(new File(plotFileName)));
 			outWriter.write("<html>");
 			outWriter.write("<body>");
	
	 		for(int j = 0 ; j < currSpecs.length();j++)
	 		{
	 			for(int i =0; i < bufferProgs.length();i++)
	 			{
	 				String res = specToTreeNode.get(currSpecs.at(j));
	 				if(pipeMode){
		 					if(resPred.size() <= i)
		 						resPred.add(new DefaultPredicateState());
		 				
		 				if(Boolean.parseBoolean(mtlMon.getTruthValuation(res, i).getTruthValueDescription()))
		 					resPred.get(i).setValue(specResmap.get(currSpecs.at(j)), 
		 					Boolean.parseBoolean(mtlMon.getTruthValuation(res, i).getTruthValueDescription()));
	 				
		 				resPred.get(i).setTimestamp(mtlMon.getTruthValuation(res, i).getTimetamp());
	 				}
	 				outWriter.write("Event-No:<b>" + (i+1) +"</b><br>");
	 				outWriter.write("timestamp: " + mtlMon.getTruthValuation(res, i).getTimetamp() + "<br>");
	 				outWriter.write("<div style=\"background: #B0B0B0 \">");
	 				if(mtlMon.getTruthValuation(res, i).getTruthValueDescription().equals("true"))
	 					outWriter.write(currSpecs.at(j).getTextDescription() + ":" + "<font color=\"Lime\">"+  mtlMon.getTruthValuation(res, i).getTruthValueDescription() + "</font>");
	 				else
	 					outWriter.write(currSpecs.at(j).getTextDescription() + ":" + "<font color=\"Red\">"+  mtlMon.getTruthValuation(res, i).getTruthValueDescription() + "</font>");
	 				outWriter.write("</div>");
	 				
	 				plotWriter.write(currSpecs.at(j).getTextDescription() + "," + mtlMon.getTruthValuation(res, i).getTimetamp()+ "," + mtlMon.getTruthValuation(res, i).getTruthValueDescription()+ "\n");
	 			}
	 		}
 		}catch(IOException ioe){
 			logger.fatal(ioe.getMessage());
 		}finally{
 			try {
				outWriter.close();
				plotWriter.close();
			} catch (IOException ioe) {
				logger.fatal(ioe.getMessage());
			}
 		}
		return currSpecStatus;
	}

	@Override
	public boolean fillBuffer(ProgState ps) {
		bufferProgs.add(ps);
		return true;
	}

	@Override
	public void setPredicateEvaluator(PredicateEvaluator pe) {
		// TODO Auto-generated method stub
		super.setPredicateEvaluator(pe);
		mtlMon.setPredicateEvaluator(pe);
	}
}
