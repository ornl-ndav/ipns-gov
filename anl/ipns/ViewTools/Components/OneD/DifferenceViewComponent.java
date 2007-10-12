/*
 * File: DifferenceViewComponent.java
 *
 *  Copyright (C) 2006, Andrew Moe
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307, USA.
 *
 * 
 *           
 * Contact : Dennis Mikkelson <mikkelsond@uwstout.edu>
 *           Department of Mathematics, Statistics and Computer Science
 *           University of Wisconsin-Stout
 *           Menomonie, WI 54751, USA
 *
 * This work was supported by the National Science Foundation under grant
 * number DMR-0218882, and by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 * 
 * Modified:
 *
 *  $Log$
 *  Revision 1.12  2007/10/12 22:36:22  amoe
 *  Made the right and left separated axes not be displayed if the displayY
 *  flags are false.
 *
 *  Revision 1.11  2006/10/06 03:54:02  amoe
 *  Edited DifferenceAxisOverlay.paint() so that when the
 *  differenceGraph is not shifted, the difference axis would turn off and
 *  the regular axis would be displayed normally.
 *
 *  Revision 1.10  2006/08/09 19:18:32  amoe
 *  - Changed default difference graph title to a new minimized form.
 *  - Decreased space between the difference axis labels and the axis line.
 *
 *  Revision 1.9  2006/08/01 14:21:29  dennis
 *  Replaced Integer.parseInt() with Double.parseDouble() when
 *  masking tick mark labels on part of the axis.  The Integer
 *  version works as long as the tick labels are integers, but
 *  gives a number format exception if the label has a non-zero
 *  fractional part.  This fixes a bug where the difference
 *  view failed for some graphs, but worked for other similar
 *  graphs.
 *
 *  Revision 1.8  2006/07/25 16:40:08  amoe
 *  - Changed difference graph titles to a more compact form.
 *  - Created initTransparancies().
 *  - Created inner class DifferenceAxisOverlay to create an axis for
 *    displayed difference graphs.
 *
 *  Revision 1.7  2006/07/21 20:56:30  amoe
 *  Fixed the warning messages for incompatible datasets and added
 *  display information to the debug output.
 *
 *  Revision 1.6  2006/07/20 23:43:31  amoe
 *  Fixed NullPointedException problem when buildShiftDiffGraph() would
 *  try to build a shifted difference graph with a null error list.
 *
 *  Revision 1.5  2006/07/14 21:51:48  amoe
 *  Changed both zeroLine and differenceLine to be black and solid lines
 *
 *  Revision 1.4  2006/07/13 16:46:12  amoe
 *  - Added license and log tag.
 *  - Changed certain strings that said "Selected Difference" to
 *    "Difference Options" to keep naming consistency.
 *
 */
package gov.anl.ipns.ViewTools.Components.OneD;

import gov.anl.ipns.Util.Numeric.Format;
import gov.anl.ipns.Util.Sys.SharedMessages;
import gov.anl.ipns.ViewTools.Components.*;
import gov.anl.ipns.ViewTools.Components.ViewControls.*;
import DataSetTools.dataset.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

import gov.anl.ipns.ViewTools.Components.Transparency.AnnotationOverlay;
import gov.anl.ipns.ViewTools.Components.Transparency.AxisOverlay2D;
import gov.anl.ipns.ViewTools.Components.Transparency.CalibrationUtil;
import gov.anl.ipns.ViewTools.Components.Transparency.LegendOverlay;
import gov.anl.ipns.ViewTools.Panels.Transforms.CoordBounds;

/**
 * This class calculates the difference between y-values, with respect to their x-values
 * (i.e. Difference = GraphZero - GraphOne).  Then it displays the new graph of differences
 * upon the Selected Graph View.  DifferenceViewComponent only calculates the difference of
 * graphs that are "selected".
 */
public class DifferenceViewComponent extends FunctionViewComponent
{	
	private IVirtualArrayList1D fvcGraphs;			//all of the FunctionViewComponent graphs
	private IVirtualArrayList1D fvcGraphWithDiff;	//the graphs to be display for difference view
	private Vector fvcGraphDiffVec;					//vector of all graphs for fvcGraphWithDiff
	private DataArray1D differenceGraph;			//the calculated DataArray difference graph
	private DataArray1D shiftedDiffGraph;			//the shifted version of differenceGraph
	private DataArray1D zeroLine;					//holds the real zero line.
	private DataArray1D differenceLine;				//holds the shifted zero line for differenceGraph	
	
	private int selectedIndexes[];					//the selected graphs indexes from fvcGraphs
	
	private boolean flawedDifference	= false;	//true if the difference graph is flawed
	private boolean displayDiff			= true;		//true if the difference graph is on
	private boolean plotShiftedDiff		= true;		//plot the shifted diff graph
	private int numSelected;						//number of selected graphs
	private int numGraphs;							//number of total graphs
	
	//graph selected for difference
	// diffGraph = graphZero - graphOne
	private int graphZero;
	private int graphOne;
	private String graphZeroTitle;
	private String graphOneTitle;
	
	//min&max values for selected and difference graphs
	private float selectedGraphMax;
	private float selectedGraphMin;
	private float diffGraphMax;
	private float diffGraphMin;
	private float shift;
	
	private ControlCheckboxButton diff_Checkbox;	//view control for Difference Options
	private transient DifferenceOptions diffOptions;//options menu for DifferenceViewComponent
	
	/**
	 * Constructor that takes in a virtual array and initiates the calculation and display
	 * of the difference graph.  It also incorporates the DifferenceViewComponent's options in a
	 * FunctionControls object.
	 *  
	 *  @param varr The IVirtual array containing data for producing the graph(s).
	 */
	public DifferenceViewComponent(IVirtualArrayList1D varr)
	{
		super(varr);				
		
		fvcGraphs = varr;
		selectedIndexes = fvcGraphs.getSelectedIndexes();
		numSelected = selectedIndexes.length;
		numGraphs = fvcGraphs.getNumGraphs();
		
		int pointedAtGraph = fvcGraphs.getPointedAtGraph();		
		
		// deciding which graph index is GraphZero and GraphOne, when two graphs are selected
		if((numSelected == 2) && (selectedIndexes[1]==pointedAtGraph))
		{				
			//switching graphZero and graphOne indexes if the last selected graph isPointedAt
			graphZero = selectedIndexes[1];
			graphOne = selectedIndexes[0];
		}
		else if(numSelected >= 2)
		{
			//setting default graph indexes for difference
			graphZero = selectedIndexes[0];
			graphOne = selectedIndexes[1];
		}
		
		
		fvcGraphDiffVec= new Vector(0);
		fvcGraphDiffVec.setSize(numGraphs);
		//System.out.println("fvcGraphDiffVec size: "+ fvcGraphDiffVec.size());
		//System.out.println("fvcGraphDiffVec capacity: "+ fvcGraphDiffVec.capacity());
		for(int i=0;i<numGraphs;i++)
		{
			fvcGraphDiffVec.set(i, new DataArray1D( fvcGraphs.getXValues(i),fvcGraphs.getYValues(i),fvcGraphs.getErrorValues(i),
					fvcGraphs.getGraphTitle(i),fvcGraphs.isSelected(i),false) );
		}
		( (DataArray1D)(fvcGraphDiffVec.get(pointedAtGraph)) ).setPointedAt(true);  //re-setting the pointed at graph
		
		
		
		//don't display diff if there are less than 2 selected graphs
		//if(numSelected >= 2)
		//{
			displayDiffGraph(true);
		//}		
		
		diffOptions = new DifferenceOptions();
		incorporateControls();		
	}
	
	/**
	 *	This method takes in a changed array of data and re-calculates the difference 
	 *	graph if needed.  The graph is then redrawn accordingly.
	 *
	 *	@param pin_varr The IVirtual array containing data for producing the graph(s).
	 */
	public void dataChanged(IVirtualArrayList1D pin_varr)
	{		
		boolean fullBuild = false;
		int[] oldSelectedIndexes = selectedIndexes;
		int oldNumSelected = numSelected;
		int oldNumGraphs = numGraphs;		
		
		fvcGraphs = pin_varr;		
		
		//updating the new selected indexes
		selectedIndexes = fvcGraphs.getSelectedIndexes();
		numSelected = selectedIndexes.length;
		numGraphs = fvcGraphs.getNumGraphs();		
		
		//if fvcGraphs size has changed, the vector must be remade
		if(oldNumGraphs != numGraphs)
		{
			int pa = fvcGraphs.getPointedAtGraph();
			fvcGraphDiffVec= new Vector(0);
			fvcGraphDiffVec.setSize(numGraphs);
			for(int i=0;i<numGraphs;i++)
			{
				fvcGraphDiffVec.set(i, new DataArray1D( fvcGraphs.getXValues(i),fvcGraphs.getYValues(i),
						fvcGraphs.getErrorValues(i),fvcGraphs.getGraphTitle(i),fvcGraphs.isSelected(i),
						false) );
			}
			//re-setting the pointed at graph
			( (DataArray1D)(fvcGraphDiffVec.get(pa)) ).setPointedAt(true);
		}		
		else  //updating the current vector
		{
			//clearing old selected indexes
			for(int i=0;i<oldNumSelected;i++)
			{
				((DataArray1D) fvcGraphDiffVec.get(oldSelectedIndexes[i]) ).setSelected(false);
			}
			
			//updating selected indexes for graphs
			for(int i=0;i<numSelected;i++)
			{
				((DataArray1D)fvcGraphDiffVec.get(selectedIndexes[i])).setSelected(true);
			}
			
		}		
		
		
		//if before there were less than 2 selected, but more than 2 selected now, must full build
		//because there was no graph before
		if((oldNumSelected < 2) && (numSelected >= 2))
		{
			fullBuild = true;
			graphZero = selectedIndexes[0];
			graphOne = selectedIndexes[1];
		}		
				
		//if diff graph indexes are not currently (still) selected, change to 
		//the next free selected index
		if(Arrays.binarySearch(selectedIndexes,graphZero)<0)
		{
			fullBuild = true;
			
			if(numSelected >= 2)
			{
				int i = 0;
				graphZero = -1;   //clearing graphZero, because it's index isn't usefull
				do
				{
					if(selectedIndexes[i] != graphOne)
					{
						graphZero = selectedIndexes[i];
					}
					i++;
				}while(graphZero == -1);
			}
		}		
		if(Arrays.binarySearch(selectedIndexes,graphOne)<0)
		{
			fullBuild = true;
			
			if(numSelected >= 2)
			{
				int i = 0;
				graphOne = -1;   //clearing graphOne, because it's index isn't usefull
				do
				{
					if(selectedIndexes[i] != graphZero)
					{
						graphOne = selectedIndexes[i];
					}
					i++;
				}while(graphOne == -1);
			}
		}
		
		displayDiffGraph(fullBuild);
		
		
		//updating difference controls
		if(numSelected >= 2)
		{
			diff_Checkbox.setSelected(true);
		}
		else
		{
			diff_Checkbox.setSelected(false);
		}
        
		//update DifferenceOptions graph selection lists?
	}
	
	/**
	 *	This method is responsible for displaying the difference graph.
	 *
	 *	@param fullBuild TRUE if the display needs to be updated with new difference data.
	 *					 FALSE if the display needs to be updated with the current difference data.
	 */
	public void displayDiffGraph(boolean fullBuild) //fullBuild == true when new diff data must be calculated
	{		
		if(((numSelected >= 2) && fullBuild) && displayDiff)  //buildling new diff data from selected data
		{
			buildDiffGraph();
			buildShiftDiffGraph();					
															/*TODO until Virtual Arrays are fixed/updated,
															 * this part should just add a new DataArray1D 
															 * graph (difference graph) to the end of the 
															 * Virtual Array, instead of just making a new 
															 * one.*/
			/*
			int pa = fvcGraphs.getPointedAtGraph();
			fvcGraphDiffVec= new Vector(0);
			
			for(int i=0;i<numGraphs;i++)
			{
				fvcGraphDiffVec.add( new DataArray1D( fvcGraphs.getXValues(i),fvcGraphs.getYValues(i),fvcGraphs.getErrorValues(i),
						fvcGraphs.getGraphTitle(i),fvcGraphs.isSelected(i),false) );
			}
			( (DataArray1D)(fvcGraphDiffVec.get(pa)) ).setPointedAt(true);  //re-setting the pointed at graph
			*/
			
			
			//if the difference graph doesn't exist in vector, add it
			//there will be one more element in the vector if it does exist
			if(numGraphs < fvcGraphDiffVec.size())
			{
				//setting differenceGraph to the vector of selected graphs
				if(plotShiftedDiff)
				{
					fvcGraphDiffVec.set(fvcGraphDiffVec.size()-1,getShiftedDiffGraph());
				}
				else
				{
					fvcGraphDiffVec.set(fvcGraphDiffVec.size()-1,getDifferenceGraph());
				}				
			}
			else
			{
				//adding differenceGraph to the vector of selected graphs
				if(plotShiftedDiff)
				{
					fvcGraphDiffVec.add(getShiftedDiffGraph());
				}
				else
				{
					fvcGraphDiffVec.add(getDifferenceGraph());
				}
			}
			
			//creating the new Virtual Array with the difference graph included
			fvcGraphWithDiff = new VirtualArrayList1D(fvcGraphDiffVec);
			fvcGraphWithDiff.setTitle(fvcGraphs.getTitle());
			
			//setting axis info
			for(int i=0;(i<numGraphs)&&(fvcGraphs.getAxisInfo(i)!=null);i++)
			{
				fvcGraphWithDiff.setAxisInfo(i,fvcGraphs.getAxisInfo(i));
			}
			
			super.dataChanged(fvcGraphWithDiff);
			
			//displaying default zero lines and colors
			setGraphDefaults();
		}
		else if(((numSelected >= 2) && !fullBuild) && displayDiff) //updating display from available data
		{			
			
			
			fvcGraphWithDiff = new VirtualArrayList1D(fvcGraphDiffVec);
			
			//restoring axis information
			fvcGraphWithDiff.setTitle(fvcGraphs.getTitle());			
			for(int i=0;(i<numGraphs)&&(fvcGraphs.getAxisInfo(i)!=null);i++)
			{
				fvcGraphWithDiff.setAxisInfo(i,fvcGraphs.getAxisInfo(i));
			}
			
			super.dataChanged(fvcGraphWithDiff);			
			
			//displaying default zero lines and colors
			setGraphDefaults();
		}
		else if((numSelected < 2) || !fullBuild) //less than 2 selected and no-full build
		{
			super.dataChanged(fvcGraphs);
		}
		else  
		{
			super.dataChanged(fvcGraphs);
			//System.out.println("UNHANDLED SCENARIO: DifferenceViewComponent displayDiffGraph()");
		}

	}
	
	/**
	 * This method returns the calculated difference graph
	 *
	 *  @return differenceGraph 
	 */
	public DataArray1D getDifferenceGraph()
	{
		return differenceGraph;
	}
	
	/**
	 * This method will return the shifted version of the 
	 * difference graph.
	 *
	 *  @return shiftedDiffGraph 
	 */
	public DataArray1D getShiftedDiffGraph()
	{
		return shiftedDiffGraph;
	}
	
	/**
	 * This method extracts the digits from a graph title and 
	 * returns them as a String.  
	 *
	 *	@param index is the index of the graph in the main virtual array
	 *  @return String the String of digits.
	 */
	public String getGraphID(int index)
	{
		return (fvcGraphs.getGraphTitle(index)).replaceAll("[^0-9]","");
	}
	
	/**
	 * This method will set GraphZero (the minuend or i.e.:
	 * Difference = GraphZero - GraphOne)
	 */
	public void setGraphZero(int g0)
	{
		graphZero = g0;
	}
	
	/**
	 * This method will set GraphOne (the subtrahend or i.e.:
	 * Difference = GraphZero - GraphOne)
	 */
	public void setGraphOne(int g1)
	{
		graphOne = g1;
	}	
	
	/**
	 *  This function will return an array of 17 ViewControls 
	 *  which are used by the Function View Component.
	 *  [0] - Control to choose a selected line. (Line Selected)
	 *  [1] - Control to choose the style of the chosen line. (Line Style)
	 *  [2] - Control to choose the thickness of the chosen line. (Line Width)
	 *  [3] - Control to display point markers for the chosen line. (Point Marker)
	 *  [4] - Control to choose the size of the point markers for the line. (Point Marker Size)
	 *  [5] - Control to display error bars for the choosen line. (Error Bars)
	 *  [6] - Button Control to select the color of the chosen line. (Line Color)
	 *  [7] - Button Control to select the color of the point markers. (Point Marker Color)
	 *  [8] - Button Control to select the color of the error bars. (Error Bar Color)
	 *  [9] - Control to offset the selected lines. (Shift)
	 *  [10]- Control to set a shift factor to offset the selected lines by. (Shift Factor)
	 *  [11]- Control to select the axis overlay. (Axis Checkbox)
	 *  [12]- Control to select the annotation overlay. (Annotation Checkbox)
	 *  [13]- Control to select the legend overlay. (Legend Checkbox)
	 *  [14]- Control to select a range for the graph to display. (Graph Range)
	 *  [15]- Control to show the location of the cursor. (Cursor)
	 *  [16]- Control to display logarithmic axes. (Logarith Axes)
	 *  [17]- Control to select the Difference Options. (Difference Options)
	 *
	 *  @return ViewControl[] the array of view controls
	 */
	public ViewControl[] getControlList()
	{		
		ViewControl[] vcontrol = super.getControlList();
	  	ViewControl[] vcontrolWithDiff = new ViewControl[vcontrol.length+1];
	  	
	  	for(int i=0;i<vcontrol.length;i++)
	  	{
	  		vcontrolWithDiff[i] = vcontrol[i];
	  	}
	  	vcontrolWithDiff[vcontrolWithDiff.length-1] = diff_Checkbox;
	  	
	  	/*System.out.println("TITLE\tCONT-VALUE");    	  
	  	for(int a = 0;a<vcontrolWithDiff.length;a++)
	  	{
	  		System.out.println("["+a+"] ." + vcontrolWithDiff[a].getTitle() + 
                             ".\t" + vcontrolWithDiff[a].getControlValue());
	  	}*/
	  	
	  	return vcontrolWithDiff;
	}
	
	/**
	 *  This method disposes of the DifferenceOptions frame.
	 */
	public void kill()
	{
		diffOptions.dispose();
		super.kill();		
	}
	
	protected void initTransparancies()											
	{										//this method overrides initTransparancies()
											//in FunctionViewComponent
		 //create transparencies
	     AnnotationOverlay top = new AnnotationOverlay( this );
	     top.setVisible( false );    // initialize this overlay to off.

	                                 // Default ticks inward for this component
	     AxisOverlay2D bottom = new DifferenceAxisOverlay(true);

	     LegendOverlay leg_overlay = new LegendOverlay( this );
	  
	     transparencies.add( leg_overlay ); 
	     transparencies.add( top );
	     transparencies.add( bottom );  // add the transparency to the vector
	}
	
	private void buildDiffGraph()
	{
		//reset flawed diffference flag
		flawedDifference = false;
		
		if(numSelected >= 2)
		{
			graphZeroTitle = getGraphID(graphZero);
			graphOneTitle = getGraphID(graphOne);
			
			// initializing x and y values from the two graphs
			float x0[] = fvcGraphs.getXValues(graphZero);
		    float y0[] = fvcGraphs.getYValues(graphZero);
		    float e0[] = fvcGraphs.getErrorValues(graphZero); //test
		    float x1[] = fvcGraphs.getXValues(graphOne);
		    float y1[] = fvcGraphs.getYValues(graphOne);		     
		    float e1[] = fvcGraphs.getErrorValues(graphOne); //test
		    // arrays for graph difference
		    float x2[];
		    float y2[];
		    float e2[];
		    
		    //if the selected bounds do not match, the difference is flawed
		    if((x0.length != x1.length) || x0[0] != x1[0])
		    {
		    	//could try: data.Compatible when checking flaw
		    	flawedDifference = true;
		    	
		    	SharedMessages.addmsg("WARNING in DifferenceViewComponent: Graphs ["+ getGraphID(graphZero)+
		    			"] and ["+getGraphID(graphOne)+ "] have unmatched bounds. Difference is flawed.");
		    	
		    	/*System.out.println("Graphs ["+ getGraphID(graphZero)+"] and ["+getGraphID(graphOne)+
		    			"] have unmatched bounds. Difference is flawed.");*/
		    }
		    
		    //creating Data object, so Data.subtract() can be used
		    Data d0;  	//corresponds to graphZero
		    Data d1; 	//corresponds to graphOne
		    Data d2;  	//corresponds to differenceGraph
		    XScale xs0 = new VariableXScale(x0);
		    XScale xs1 = new VariableXScale(x1);		    
		    
		    //if there is one more x-value than y-values, its a histogram
		    if (x0.length == (y0.length+1))
		    {
		    	d0 = new HistogramTable(xs0,y0,e0,1);
		    }
		    else if(x0.length == y0.length)
		    {
		    	d0 = new FunctionTable(xs0,y0,e0,1);
		    }
		    else
		    {
		    	d0 = new HistogramTable(xs0,y0,e0,1);  
		    }		    
		    
		    if (x1.length == (y1.length+1))
		    {
		    	d1 = new HistogramTable(xs1,y1,e1,1);
		    }
		    else if(x1.length == y1.length)
		    {
		    	d1 = new FunctionTable(xs1,y1,e1,1);
		    }
		    else
		    {
		    	d1 = new HistogramTable(xs1,y1,e1,1);
		    }		    
		    
		    
		    //Checking for flawed Difference
		    if(d0.isHistogram() != d1.isHistogram())
		    {
		    	flawedDifference = true;
		    	
		    	SharedMessages.addmsg("WARNING in DifferenceViewComponent:  Finding the difference " +
		    			"between a Histogram and a Function may give a flawed result.");
		    }
		    
		    
		    //Finding differenceGraph
		    d2 = d0.subtract(d1);		    
		    x2 = d2.getX_values();
		    y2 = d2.getY_values();
		    e2 = d2.getErrors();
		    differenceGraph = new DataArray1D(x2,y2,e2,"["+graphZeroTitle+"]-["+graphOneTitle+"]",true,false);	    
		    		    
		    //debugDiffOutput();	    
		}
		else //if there isn't two graphs to compare, diffGraph is null
		{
			differenceGraph = null;
		}
	}
	
	private void buildShiftDiffGraph()  //creating the shifted diff graph, from normal diff
	{
		if(differenceGraph != null)
		{		
			//Initializing data arrays
			//float y0[] = fvcGraphs.getYValues(graphZero);
			//float y1[] = fvcGraphs.getYValues(graphOne);
			float y2[] = differenceGraph.getYArray();
			float e2[] = differenceGraph.getErrorArray();
			
			//shifted data array
			float y2Shifted[] = new float[y2.length];
			float e2Shifted[] = new float[y2.length];
			float zLine[] = new float[y2.length];
			float dLine[] = new float[y2.length];		
			
			//Initializing and finding Min and Max
			selectedGraphMax = fvcGraphs.getYValues(graphZero)[0];
			selectedGraphMin = fvcGraphs.getYValues(graphZero)[0];
		    diffGraphMax = y2[0];
		    diffGraphMin = y2[0];	    
		    
		    //finding min and max for all selected graphs
		    for(int a=0;a<numSelected;a++)
		    {
		    	float yVal[] = fvcGraphs.getYValues(selectedIndexes[a]);	    	
		    	for(int b=0;b<yVal.length;b++)
		    	{
		    		if(yVal[b]>selectedGraphMax)
		    		{
		    			selectedGraphMax = yVal[b];
		    		}
		    		else if(yVal[b]<selectedGraphMin)
		    		{
		    			selectedGraphMin = yVal[b];
		    		}
		    	}
		    }
		    //find min and max for difference graph
		    for(int i=0;i<y2.length;i++)
		    {
		    	if(y2[i]>diffGraphMax)
		    	{
		    		diffGraphMax = y2[i];
		    	}
		    	else if(y2[i]<diffGraphMin)
		    	{
		    		diffGraphMin = y2[i];
		    	}
		    }
						
		    //System.out.println("\n\nselectedGraphMax: "+selectedGraphMax + "\nselectedGraphMin: "+selectedGraphMin + "\ndiffGraphMax: "+diffGraphMax+"\ndiffGraphMin: "+diffGraphMin);
		    
		    //calculating shift amount, now that min/maxs are now known
		    shift = (float)(Math.abs(diffGraphMax-selectedGraphMin) + .1*(selectedGraphMax-selectedGraphMin));
		    
		    //if there is no error list, it won't be included in the ShiftedDiffGraph
		    if(e2 != null)
		    {
		    	//setting shifted diff Graph
		    	for(int i=0;i<y2Shifted.length;i++)
		    	{
		    		y2Shifted[i] = y2[i] - shift;
		    		e2Shifted[i] = e2[i] - shift; 
		    	}
		    	
		    	shiftedDiffGraph = new DataArray1D(differenceGraph.getXArray(),y2Shifted,e2Shifted,
		    			"["+graphZeroTitle+"]-["+graphOneTitle+"]",true,false);
		    }
		    else
		    {
		    	//setting shifted diff Graph
		    	for(int i=0;i<y2Shifted.length;i++)
		    	{
		    		y2Shifted[i] = y2[i] - shift; 
		    	}
		    	
		    	shiftedDiffGraph = new DataArray1D(differenceGraph.getXArray(),y2Shifted,null,
		    			"["+graphZeroTitle+"]-["+graphOneTitle+"]",true,false);
		    }
	
		    //zero line
		    for(int i=0;i<zLine.length;i++)
		    {
		    	zLine[i] = 0;
		    }
		    zeroLine = new DataArray1D(fvcGraphs.getXValues(graphZero),zLine);
		    
		    //diff line
		    for(int i=0;i<dLine.length;i++)
		    {
		    	dLine[i] = 0 - shift;
		    }
		    differenceLine = new DataArray1D(differenceGraph.getXArray(),dLine);		    
		}
		else
		{
			shiftedDiffGraph = null; 
		}
	}
	
	private void incorporateControls()  //this method puts the difference checkbox
	{									//into mainControls
		
		//mainpanel equiv to ViewControlsPanel FunctionControls.buildControls().mainpanel
		ViewControlsPanel mainpanel = mainControls.get_panel();
		Component mainpanelComp[] = mainpanel.getPanel().getComponents();
		
		//controlpanel equiv to JPanel FunctionControls.buildControls().controlpanel
		JPanel controlpanel = (JPanel)mainpanelComp[0];		
		Component controlpanelComp[] = controlpanel.getComponents();
		
		//control_box equiv to Box FunctionControls.buildControls().control_box
		Box control_box = (Box)controlpanelComp[0];
		Component control_boxComp[] = control_box.getComponents();		
		
		/*//leftBox eqiv to Box FunctionControls.buildControls().leftBox
		Box leftBox = (Box)control_boxComp[0];
		Component leftBoxComp[] = leftBox.getComponents();		
		
		//RboxPanel eqiv to JPanel FunctionControls.buildControls().RboxPanel
		JPanel RboxPanel = (JPanel)control_boxComp[1];
		Component RboxPanelComp[] = RboxPanel.getComponents();		
		
		//rightBox eqiv to Box FunctionControls.buildControls().rightBox
		Box rightBox = (Box)RboxPanelComp[0];
		Component rightBoxComp[] = rightBox.getComponents();*/
		
		//looking for the RBoxPanel
		boolean searching = true;
		int i = 0;
		int index = 1;  //default position is 1		
		while((i < control_boxComp.length) && (searching == true))
		{		
			if(control_boxComp[i].getName().equals("RboxPanel"))
			{
				index = i;
				searching = false;
			}
			i++;
		}
		
		JPanel RboxPanel = (JPanel)control_boxComp[index];
		Component RboxPanelComp[] = RboxPanel.getComponents();
		
		Box rightBox = (Box)RboxPanelComp[0];
		Component rightBoxComp[] = rightBox.getComponents();
		
		//initializing difference checkbox and adding it to rightBox in mainControls
		diff_Checkbox = new ControlCheckboxButton("Difference Options");
		
		if(numSelected >= 2)
		{
			diff_Checkbox.setSelected(true);
		}
		
		diff_Checkbox.addActionListener( new ControlListener( ) );		
		rightBox.add(diff_Checkbox);  //adding diff view control below the overlay view controls		
		
		mainControls.get_frame().pack();
		
	}
	
	private void setGraphDefaults()
	{		
		if(plotShiftedDiff)  //displaying default colors and zero lines (if shifted)
		{
			//setting differenceGraph to default color
			//the diff line will be green if the difference became flawed
			if(flawedDifference)
			{
				gjp.setColor(Color.green,gjp.getNum_graphs()-1,false);
			}
			else
			{
				gjp.setColor(Color.magenta,gjp.getNum_graphs()-1,false);
			}
			
			//setting zeroLine
			gjp.setData(zeroLine.getXArray(),zeroLine.getYArray(),gjp.getNum_graphs(),false);
			gjp.setColor(Color.black,gjp.getNum_graphs()-1,false);
			
			//setting diffLine
			gjp.setData(differenceLine.getXArray(),differenceLine.getYArray(),gjp.getNum_graphs(),false);
			gjp.setColor(Color.black,gjp.getNum_graphs()-1,false);
		}
		else
		{
			//setting differenceGraph to default color
			if(flawedDifference)
			{
				gjp.setColor(Color.green,gjp.getNum_graphs()-1,false);
			}
			else
			{
				gjp.setColor(Color.magenta,gjp.getNum_graphs()-1,false);
			}
		}
	}
	
	private void debugDiffOutput()   //prints out difference graph data to console
	{
		boolean displayErrors = false;
		
		//System.out.println("Num_Selected: " + selectedGraphs.getNumSelectedGraphs() + "\nNum_total: "+selectedViews.getNumGraphs());
		
		//display graph zero
		float x0[] = fvcGraphs.getXValues(graphZero);
		float y0[] = fvcGraphs.getYValues(graphZero);
		float e0[] = fvcGraphs.getErrorValues(graphZero);
		System.out.println("\n\n["+graphZeroTitle+"] (GraphZero)");		
		System.out.println("X's length: "+x0.length);		
		for(int i=0;i<x0.length;i++)
		{
			if(i<=10||i>=(x0.length-10))
			{
				System.out.print(x0[i]+"|");
			}
			if(i==10)
			{
				System.out.print("###|");
			}
		}		
		System.out.println("\nY's length: "+y0.length);
		for(int i=0;i<y0.length;i++)
		{
			if(i<=10||i>=(y0.length-10))
			{
				System.out.print(y0[i]+"|");
			}
			if(i==10)
			{
				System.out.print("###|");
			}
		}
		if((e0 != null) && (displayErrors))
		{
			System.out.println("\nE's length: "+e0.length);
			for(int i=0;i<e0.length;i++)
			{
				if(i<=10||i>=(e0.length-10))
				{
					System.out.print(e0[i]+"|");
				}
				if(i==10)
				{
					System.out.print("###|");
				}
			}
		}
		System.out.println();
		
		
		//display graph one
		float x1[] = fvcGraphs.getXValues(graphOne);
		float y1[] = fvcGraphs.getYValues(graphOne);
		float e1[] = fvcGraphs.getErrorValues(graphOne);
		System.out.println("\n["+graphOneTitle+"] (GraphOne)");		
		System.out.println("X's length: "+x1.length);		
		for(int i=0;i<x1.length;i++)
		{
			if(i<=10||i>=(x1.length-10))
			{
				System.out.print(x1[i]+"|");
			}
			if(i==10)
			{
				System.out.print("###|");
			}
		}		
		System.out.println("\nY's length: "+y1.length);
		for(int i=0;i<y1.length;i++)
		{
			if(i<=10||i>=(y1.length-10))
			{
				System.out.print(y1[i]+"|");
			}
			if(i==10)
			{
				System.out.print("###|");
			}
		}
		if((e1 != null) && (displayErrors))
		{
			System.out.println("\nE's length: "+e1.length);
			for(int i=0;i<e1.length;i++)
			{
				if(i<=10||i>=(e1.length-10))
				{
					System.out.print(e1[i]+"|");
				}
				if(i==10)
				{
					System.out.print("###|");
				}
			}
		}
		System.out.println();
		
		
		//display difference graph
		float x2[] = differenceGraph.getXArray();
		float y2[] = differenceGraph.getYArray();
		float e2[] = differenceGraph.getErrorArray();		
		System.out.println("\n["+graphZeroTitle+"]-["+graphOneTitle+"] (Diff)");		
		System.out.println("X's length: "+x2.length);		
		for(int i=0;i<x2.length;i++)
		{
			if(i<=10||i>=(x2.length-10))
			{
				System.out.print(x2[i]+"|");
			}
			if(i==10)
			{
				System.out.print("###|");
			}
		}		
		System.out.println("\nY's length: "+y2.length);
		for(int i=0;i<y2.length;i++)
		{
			if(i<=10||i>=(y2.length-10))
			{
				System.out.print(y2[i]+"|");
			}
			if(i==10)
			{
				System.out.print("###|");
			}
		}
		if((e2 != null) && (displayErrors))
		{
			System.out.println("\nE's length: "+e2.length);
			for(int i=0;i<e2.length;i++)
			{
				if(i<=10||i>=(e2.length-10))
				{
					System.out.print(e2[i]+"|");
				}
				if(i==10)
				{
					System.out.print("###|");
				}
			}
		}
		System.out.println();
	}
	
	private class DifferenceAxisOverlay extends AxisOverlay2D
	{
		int precision;
		
		boolean displayDiffAxis = displayYop;
		
		public DifferenceAxisOverlay(boolean bol)
		{
			super(fvc,bol);
		}
		
		public void paint(Graphics g)
		{		   	        
	    	// Linear Axis
	        if((axesdrawn == Y_AXIS || axesdrawn == DUAL_AXES) && 
	        		(y_scale == AxisInfo.LINEAR) && 
	        		(plotShiftedDiff) )
	        {
	        	Graphics2D g2d = (Graphics2D)g;
	        	g2d.setFont(fvc.getFont());
			    
	        	// Reset precision, make sure it is always consistent.		    
	        	precision = fvc.getPrecision();
	        	
	        	//paint separated right axis
	        	if(displayYop)
	        	  paintRightSeparatedAxis(g2d);
	        	
	        	if(displayY)
	        	  paintLeftSeparatedAxis(g2d);
	        	
	        	displayYop = false;
	        	//displayY   = false;
	        	super.paint(g);
	        	displayYop = displayDiffAxis;
	        	//displayY   = true;
	        }
	        else
	        {
	        	super.paint(g);
	        }
		}
		
		private void paintRightSeparatedAxis(Graphics2D g2d)
		{
			FontMetrics fontdata = g2d.getFontMetrics();   
		    String num = "";

		    CoordBounds local_bounds = fvc.getLocalCoordBounds();
		    
		    //making sure that y1 < y2
		    if( local_bounds.getY1() > local_bounds.getY2() )
		    {
		        local_bounds.invertBounds();
		    }
		    
		    // min/max for y-axis
		    float ymin = (local_bounds.getY1()+shift);
			float ymax = (local_bounds.getY2()+shift);
		    
		    //get the dimension of the center panel (imagejpanel)
		    int yaxis = (int)( fvc.getRegionInfo().getHeight() );		    
		    int ystart = (int)( fvc.getRegionInfo().getLocation().getY() );
		    
		    
		    int xaxis  = (int)( fvc.getRegionInfo().getWidth() );
		    int xstart = (int)( fvc.getRegionInfo().getLocation().getX() );
		    
		    CalibrationUtil yutil = new CalibrationUtil( ymin, ymax, precision, 
		        					 Format.ENGINEER );
		    
		    displayDiffAxis = displayYop;
		    
		    float[] values = yutil.subDivide();
		    float ystep = values[0];
		    float starty = values[1];
		    int numysteps = (int)values[2];
		    
		    int ytick_length = 5;
		    
		    int ypixel = 0;	      // where to place major ticks
		    int ysubpixel = 0;    // where to place minor ticks
		    
		    int exp_index = 0;
		    
		    
		    float pmin = ystart + yaxis;
		    float pmax = ystart;
		    float a = 0;
		    float amin = ymin - starty;
		    
		    
		    //yskip is the space between calibrations: 1 = every #, 2 = every other		    
		    int yskip = 1;
		    while( (yaxis*yskip/numysteps) < fontdata.getHeight() && yskip < numysteps)
		    {
		       yskip++;
		    }		    
		    int rem = numysteps%yskip;
		    
		    
		    //create axis
		    for( int ysteps = numysteps - 1; ysteps >= 0; ysteps-- )
		    {		    	
		    	a = ysteps * ystep;		        
		        ypixel = (int)( (pmax - pmin) * ( a - amin) / (ymax - ymin) + pmin);
		        ysubpixel = (int)( (pmax - pmin) * ( a - amin  + ystep/2 ) / (ymax - ymin) + pmin);
		    	
		        num = yutil.standardize(ystep * (float)ysteps + starty);
		        exp_index = num.lastIndexOf('E');

		        if( ypixel <= pmin && ypixel >= pmax )
		        {
		        	g2d.setColor(Color.MAGENTA);
		        	num = removeTrailingZeros( num.substring(0,exp_index) );
		        	
		        	if( (((float)(ysteps-rem)/(float)yskip) == ((ysteps-rem)/yskip)) 
		        			&& (Float.parseFloat(num) <= selectedGraphMin + shift ))
		        	{		        		
		        		//draw labels for ticks
		        		if(Double.parseDouble(num) >= 0)
		        		{	
		        			g2d.drawString( num,(xstart+xaxis) + ytick_length + 7 ,
		        					ypixel + fontdata.getHeight()/4);
		        		}
		        		else    //if the num label is negative, make room for "-" sign
		        		{
		        			g2d.drawString( num, (xstart+xaxis) + ytick_length + 0 ,
			        			ypixel + fontdata.getHeight()/4 );
		        		}
		        	}	        	
		        	
		        	g2d.setColor(Color.BLACK);
		        	
		        	//Major ticks
		        	if(ticksinoutY == 0)	//draw ticks outside
		        	{  
		        		if(displayDiffAxis == true)
		        		{
		        			//display y-axis opposite
		        			g2d.drawLine( (xstart + xaxis), ypixel - 1, 
		        				(((xstart + xaxis) + ytick_length)- 1) , ypixel - 1 );
		        		}
			         }
			         else					//draw ticks inside
			         {			           
			        	 if(displayDiffAxis == true)
			        	 {
			        		 //display y-axis opposite
			        		 g2d.drawLine( (xstart + xaxis)-1, ypixel - 1, 
			        				 ((xstart + xaxis) - ytick_length), ypixel - 1 );
			        	 }
			         }
		        }
		        
		        //if subpixel is between top and bottom of imagejpanel, draw it
		        if( ysubpixel <= pmin && ysubpixel >= pmax )
		        {
		        	//Minor ticks
		        	if (ticksinoutY == 0)
		        	{
		        		//draw ticks outside
		        		if(displayDiffAxis == true)
		        		{
		        			//display y-axis opposite
		        			g2d.drawLine( (xstart + xaxis), ysubpixel - 1,
		        				(((xstart + xaxis) + (ytick_length-2))- 1), ysubpixel - 1 );
		        		}
		        	}
		        	else
		        	{
		        		//draw ticks inside
		        		if(displayDiffAxis == true)
		        		{
		        			//display y-axis opposite
		        			g2d.drawLine((xstart + xaxis)-1 , ysubpixel - 1, 
		        				(((xstart + xaxis) - (ytick_length-2))), ysubpixel - 1 );
		        		}
		        	}
		        }
		        
		        // if a tick mark should be drawn at the end, draw it
		        // there may be a tick mark needed after the 
		        // last tick. ( end refers to smallest y value )
		        if( ysteps == 0 && (pmin - ypixel) > yaxis/(2*numysteps) ) 
		        {    	  
		        	if (ticksinoutY == 0)
		        	{
		        		//draw tick outside
		        		if(displayDiffAxis == true)
		        		{
		        			//display y-axis opposite
		        			g2d.drawLine( xstart + xaxis, 
		        				(int)(ysubpixel + ( (pmin - pmax) * ystep / (ymax - ymin) ) ),
		        				((xstart + xaxis) + (ytick_length-2)) - 1, 
		        				(int)(ysubpixel + ( (pmin - pmax) * ystep / (ymax - ymin) ) ) );
		        		}    			  
		        	}
		        	else
		        	{
		         		//draw tick inside  
		        		if(displayDiffAxis == true)
		        		{
		        			//display y-axis opposite
		        			g2d.drawLine( (xstart + xaxis)-1, 
		        					(int)(ysubpixel + ( (pmin - pmax) * ystep / (ymax - ymin) ) ),
		        					(((xstart + xaxis) - (ytick_length-2))),
		        					(int)(ysubpixel + ( (pmin - pmax) * ystep / (ymax - ymin) ) ) );
		        		}
		        	}
		        }
		    }			
		}
		
		private void paintLeftSeparatedAxis(Graphics2D g2d)
		{
			FontMetrics fontdata = g2d.getFontMetrics();   
		    String num = "";

		    CoordBounds local_bounds = fvc.getLocalCoordBounds();
		    
		    //making sure that y1 < y2
		    if( local_bounds.getY1() > local_bounds.getY2() )
		    {
		        local_bounds.invertBounds();
		    }
		    
		    // min/max for y-axis
		    float ymin = (local_bounds.getY1());
			float ymax = (local_bounds.getY2());
		    
		    //get the dimension of the center panel (imagejpanel)
		    int yaxis = (int)( fvc.getRegionInfo().getHeight() );		    
		    int ystart = (int)( fvc.getRegionInfo().getLocation().getY() );
		    
		    int xstart = (int)( fvc.getRegionInfo().getLocation().getX() );
		    
		    CalibrationUtil yutil = new CalibrationUtil( ymin, ymax, precision, 
		        					 Format.ENGINEER );		    
		    
		    float[] values = yutil.subDivide();
		    float ystep = values[0];
		    float starty = values[1];
		    int numysteps = (int)values[2];
		    
		    int ytick_length = 5;
		    
		    int ypixel = 0;	      // where to place major ticks
		    int ysubpixel = 0;    // where to place minor ticks
		    
		    int exp_index = 0;
		    
		    
		    float pmin = ystart + yaxis;
		    float pmax = ystart;
		    float a = 0;
		    float amin = ymin - starty;
		    
		    
		    //yskip is the space between calibrations: 1 = every #, 2 = every other		    
		    int yskip = 1;
		    while( (yaxis*yskip/numysteps) < fontdata.getHeight() && yskip < numysteps)
		    {
		       yskip++;
		    }		    
		    int rem = numysteps%yskip;
		    
		    
		    //create axis
		    for( int ysteps = numysteps - 1; ysteps >= 0; ysteps-- )
		    {
		    	
		    	a = ysteps * ystep;		        
		        ypixel = (int)( (pmax - pmin) * ( a - amin) / (ymax - ymin) + pmin);
		        ysubpixel = (int)( (pmax - pmin) * ( a - amin  + ystep/2 ) / (ymax - ymin) + pmin);
		    	
		        num = yutil.standardize(ystep * (float)ysteps + starty);
		        exp_index = num.lastIndexOf('E');
		        
		        if( ypixel <= pmin && ypixel >= pmax )
		        {
		        	//g2d.setColor(Color.MAGENTA);
		        	num = removeTrailingZeros( num.substring(0,exp_index) );
		        	
		        	//paint number label if in selected graph range
		        	if( (((float)(ysteps-rem)/(float)yskip) == ((ysteps-rem)/yskip)) && 
		        			( (Float.parseFloat(num) >= diffGraphMax-shift)) )
		        	{		        		
		        		g2d.drawString( num, xstart - ytick_length - fontdata.stringWidth(num),
		              		  ypixel + fontdata.getHeight()/4 );
		        	}	        	
		        	
		        	g2d.setColor(Color.BLACK);
		        	
		        	//Major ticks
		        	if(ticksinoutY == 0)	//draw ticks outside
		        	{  
		        		//draw ticks outside
		        		g2d.drawLine( xstart - 1, ypixel - 1, 
		        				((xstart - ytick_length) - 1) + 1, ypixel - 1 );
			        }
			        else					//draw ticks inside
			        {			           
			    		 //draw ticks inside
			    		 g2d.drawLine( xstart  , ypixel - 1, 
			    		 		(xstart + ytick_length) - 1 , ypixel - 1 );
			        }
		        }
		        
		        //if subpixel is between top and bottom of imagejpanel, draw it
		        if( ysubpixel <= pmin && ysubpixel >= pmax )
		        {
		        	//Minor ticks
		        	if (ticksinoutY == 0)
		        	{
			       		 //draw ticks outside
			       		 g2d.drawLine( xstart - 1, ysubpixel - 1,
			       				 ((xstart - (ytick_length - 2)) - 1) + 1, ysubpixel - 1 );
		        	}
		        	else
		        	{
			   	    	 //draw ticks inside
			   	    	 g2d.drawLine( xstart, ysubpixel - 1,
			   	    			 (xstart + (ytick_length - 2)) - 1 , ysubpixel - 1 );
		        	}
		        }
		        
		        // if a tick mark should be drawn at the end, draw it
		        // there may be a tick mark needed after the 
		        // last tick. ( end refers to smallest y value )
		        if( ysteps == 0 && (pmin - ypixel) > yaxis/(2*numysteps) ) 
		        {   		        	
		        	if (ticksinoutY == 0)	//draw tick outside
		        	{
			       		 //draw  ticks outside
			       		 g2d.drawLine( xstart - 1, 
			   	                (int)(ysubpixel + ( (pmin - pmax) * ystep / (ymax - ymin) ) ),
			   	                ((xstart - (ytick_length - 2)) - 1) + 1, 
			   	                (int)(ysubpixel + ( (pmin - pmax) * ystep / (ymax - ymin) ) ) );		        		   			  
		        	}
		        	else
		        	{
			       		 //draw ticks inside
			           	 g2d.drawLine( xstart, 
			                      (int)(ysubpixel + ( (pmin - pmax) * ystep / (ymax - ymin) ) ),
			                      ((xstart + (ytick_length - 2)) - 1),
			                      (int)(ysubpixel + ( (pmin - pmax) * ystep / (ymax - ymin) ) ) );		         		
		        	}
		        }
		    }
		}
	}	
	
	private class ControlListener implements ActionListener 
	{
		public void actionPerformed( ActionEvent ae ) 
		{
			String actionMessage = ae.getActionCommand(  );
			
			if(actionMessage.equals("Checkbox Changed"))
			{				
				displayDiff = ((ControlCheckboxButton)ae.getSource()).isSelected();				
				displayDiffGraph(false);
			}
			else if(actionMessage.equals("Button Pressed"))
			{
				if(!diffOptions.isVisible())
				{
					diffOptions = new DifferenceOptions();
					diffOptions.setVisible(true);
				}
				else
				{
					diffOptions.toFront();
				}
			}			 
		}
	}	
	
	private class DifferenceOptions extends JFrame
	{		
		private JCheckBox diffShifter;
		private JComboBox g0;
		private JComboBox g1;
		private JButton closeButton;
		private JLabel diffEquals;
		private JLabel minus;
		
		public DifferenceOptions()
		{
			super("Difference Options");
			this.getContentPane().setLayout( new GridLayout(2,0) );
			this.setDefaultCloseOperation( JFrame.HIDE_ON_CLOSE );
	        
	        int g0index = Arrays.binarySearch(selectedIndexes,graphZero);
			int g1index = Arrays.binarySearch(selectedIndexes,graphOne);
	        
	        //filling a list of graphs titles
	        String[] graphList = new String[selectedIndexes.length];
	        for(int i=0;i<graphList.length;i++)
	        {
	        	graphList[i]=fvcGraphs.getGraphTitle(selectedIndexes[i]);
	        }
	        
	        //these are setting the graph selected combo boxes
	        g0 = new JComboBox(graphList);
	        g0.setName("g0");
	        g0.setSelectedIndex(g0index);
	        g0.addActionListener( new OptionListener() );
	        g1 = new JComboBox(graphList);
	        g1.setName("g1");
	        g1.setSelectedIndex(g1index);
	        g1.addActionListener( new OptionListener() );
	        
	        //Option lables
	        diffEquals = new JLabel("Difference = ");
	        minus = new JLabel(" - ");
	        
	        diffShifter = new JCheckBox("Shift Difference",plotShiftedDiff);
	        diffShifter.addActionListener(new OptionListener() );

	        //this jbutton is for closing the window
	        closeButton = new JButton("Close");
	        closeButton.addActionListener( new OptionListener() );	        
	        
	        //this groups the graph selectors together
	        Box selectBox = new Box(0);
	        
	        selectBox.add(diffEquals);
	        selectBox.add(g0);
	        selectBox.add(minus);
	        selectBox.add(g1);	        
	        
	        //this puts the close buttion in its own group
	        JPanel closePanel = new JPanel(new GridLayout() );
	        closePanel.add(diffShifter);
	        closePanel.add(closeButton);	        
	        
	        //adding content to the frame
	        this.getContentPane().add( selectBox );
	        this.getContentPane().add( closePanel );
	        this.pack();	        
		}
		
		private class OptionListener implements ActionListener
		{
			public void actionPerformed( ActionEvent e )
			{
				if( e.getSource() instanceof JComboBox )
		        {	
					JComboBox temp = ((JComboBox)e.getSource());
					
					//choosing graphs for the difference
					if( temp.getName().equals("g0") )
			        {
						setGraphZero(selectedIndexes[temp.getSelectedIndex()]);
						displayDiffGraph(true);
			        }
					else if (temp.getName().equals("g1"))
					{
						setGraphOne(selectedIndexes[temp.getSelectedIndex()]);
						displayDiffGraph(true);
					}
		        }
				else if(e.getSource() instanceof JCheckBox)
				{
					plotShiftedDiff = ((JCheckBox)(e.getSource())).isSelected();
					
					//switching the diff/shifted-diff graph at the end of the fvc graph
					if(plotShiftedDiff)
					{						
						fvcGraphDiffVec.setElementAt(shiftedDiffGraph,fvcGraphDiffVec.size()-1);
					}
					else
					{
						fvcGraphDiffVec.setElementAt(differenceGraph,fvcGraphDiffVec.size()-1);
					}
					
					displayDiffGraph(false);					
				}
				else if(e.getSource() instanceof JButton)  //closing from the close button
				{
					diffOptions.dispose();
				}
			}
		}	
	}
	
	/*
	public static void main( String[] args )
	{
		System.out.println("Difference View Component");
		
		float x0 [] = {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15};
		//float x0 [] = {51,52,53,54,55,56,57,58,59,60,61,62,63,64,65,66,67,68,69,70,71,72};				
		float x1 [] = {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15};
		//float x1 [] = {51,52,53,54,55,56,57,58,59,60,61,62,63,64,65,66,67,68,69,70,71,72};
		//float x1 [] = {8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23};
		
		float y0 [] = {11,12,13,14,15,16,17,18,19,20,21,22,23,24,25};
		float y1 [] = {11,21,31,41,31,29,28,27,26,25,24,23,22,21,20};		
		
		float e0 [] = {1,2,1,2,1,2,1,2,1,2,1,2,1,2,1};
		float e1 [] = {1,2,1,2,1,2,1,2,1,2,1,2,1,2,1};		
		
		DataArray1D datArr1 = new DataArray1D(x0,y0,e0,"datArr1",true,false);
		DataArray1D datArr2 = new DataArray1D(x1,y1,e1,"datArr2",true,false);
		
		Vector datArrVec = new Vector();
		datArrVec.add(datArr1);
		datArrVec.add(datArr2);
		
		VirtualArrayList1D virtArr = new VirtualArrayList1D(datArrVec);	
		
		//execute DVC
		DifferenceViewComponent dvc = new DifferenceViewComponent(virtArr);
		float dXArr[] = dvc.getDifferenceGraph().getXArray();
		float dYArr[] = dvc.getDifferenceGraph().getYArray();
		float dEArr[] = dvc.getDifferenceGraph().getErrorArray();
		System.out.println("Xlen: "+dXArr.length+"\tYlen: "+dYArr.length+"\tFlawed?: "+dvc.flawedDifference
				+"\tSelected? "+dvc.differenceGraph.isSelected()+"\tPointedAt? "+ dvc.differenceGraph.isPointedAt() );
		
		System.out.println("\nX's: ");		
		for(int i = 0;i<dXArr.length;i++)
		{
			System.out.print(dXArr[i]+", ");
		}
		
		System.out.println("\nY's: ");		
		for(int i = 0;i<dYArr.length;i++)
		{
			System.out.print(dYArr[i]+", ");
		}
		
		System.out.println("\nE's: ");	
		for(int i = 0;i<dEArr.length;i++)
		{
			System.out.print(dEArr[i]+", ");
		}
	}*/
}