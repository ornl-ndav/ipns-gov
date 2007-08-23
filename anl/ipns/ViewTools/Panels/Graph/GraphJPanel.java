/*
 * File:  GraphJPanel.java
 *
 * Copyright (C) 1999, Dennis Mikkelson
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
 * Contact : Dennis Mikkelson <mikkelsond@uwstout.edu>
 *           Department of Mathematics, Statistics and Computer Science
 *           University of Wisconsin-Stout
 *           Menomonie, WI 54751, USA
 *
 * This work was supported by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 * Modified:
 *
 * $Log$
 * Revision 1.62  2007/08/23 05:46:52  dennis
 * Reduced the number of times the data is scanned to find the min
 * and max values.  It is now just done when the list of graphs
 * is changed.  This reduces the amount of calculation that is
 * being done when the SelectedGraph view is loaded.
 * NOTE: This could be made still more efficient by just scanning
 * the last graph added when the graphs are added one at a time.
 * Alternatively, perhaps it would be best to add a new method
 * that takes a whole vector of graphs at one time, so that
 * the work of updating min/max values does not get repeated
 * so many times.
 *
 * Revision 1.61  2007/07/29 20:45:15  dennis
 * Changed local_transform and global_transform to be private
 * in CoordJPanel class, to keep better control over who can
 * change them, and how they can be changed.
 *
 * Revision 1.60  2007/06/14 15:00:26  dennis
 * Now overrides paintComponent() instead of paint() method.
 * paintComponent() method now creates a Graphics2D "copy" of the
 * Graphics object it is given, and uses the copy to draw.  This
 * avoids changing the state of the Graphics object that was
 * passed in.
 * Also did some minor reformatting for readability.
 *
 * Revision 1.59  2007/06/08 20:01:19  dennis
 * Now forces Y_min < Y_max before making bounds that
 * are ultimately inverted.
 *
 * Revision 1.58  2006/07/31 02:35:25  dennis
 * Trap index out of bounds error in getY_value() method.
 *
 * Revision 1.57  2006/07/10 20:32:18  amoe
 * - Added code in paint to draw a Bar symbol.
 * - Added final constant BAR.
 *
 * Revision 1.56  2005/08/12 21:03:07  dennis
 * Did some "tuning" of calculation for the Y and X range, since
 * it is fairly expensive to compare all values in all graphs,
 * and this calculation is done several times.  Specifically,
 * we removed redundant comparison with max if the value was
 * already found to be less than min.  Also, replaced 6 array
 * references with 1.
 * NOTE: Some structural changes are needed to fix this properly.
 * 1. The vector of graphs should be private, so that it is only
 *    changeable by get/set methods.
 * 2. The min and max should be updated only when a graph is
 *    added or deleted.  Currently any time the min is requested,
 *    the min, max and min positive values are recalculated.
 *    When the min or min positive value is requested, the
 *    previously calculated values are returned.  While this
 *    reduces the amount of calculation somewhat, it is somewhat
 *    "fragil".  Also, the min/max methods are used by the controls
 *    and other higher level classes, so they can be called many
 *    times.
 *
 * Revision 1.55  2005/08/12 20:23:01  dennis
 * Now forces the x and y ranges to be non-degenerate.  This fixes
 * a problem where no axis was drawn by the SelectedGraph view.
 *
 * Revision 1.54  2005/06/17 19:22:08  kramer
 *
 * Made the method to get a BasicStroke object that is used to draw lines of
 * different types (i.e. solid, dotted, ....) in this class public and
 * static.  That way other viewer can draw lines of different type in a
 * consistent way.
 *
 * Revision 1.53  2005/05/06 16:47:58  serumb
 * Fixed java doc error.
 *
 * Revision 1.52  2005/03/11 19:52:01  serumb
 * Added get and set Object State methods and changed get and set Stroke
 * methods to use an integer key as a paremeter.
 *
 * Revision 1.51  2005/02/04 22:55:21  millermi
 * - Added ZoomListener to flag if local bounds are zoomed.
 * - Added check in SetDataBounds() to determine if local bounds are
 *   zoomed.
 *
 * Revision 1.50  2005/02/01 03:15:44  millermi
 * - Added method updatePointedAtGraph() which does not reset
 *   bounds. Replaces calls to setData() when pointed at is changed.
 *
 * Revision 1.49  2005/01/24 22:38:38  millermi
 * - Added positive check for log x axis values in paint().
 *
 * Revision 1.48  2004/12/05 05:58:20  millermi
 * - Fixed Eclipse warnings.
 *
 * Revision 1.47  2004/11/17 22:22:43  serumb
 * Mapped the error bar values to log coords for the log axis.
 *
 * Revision 1.46  2004/11/11 19:50:06  millermi
 * - Added getScaleFactor() so outside classes have access to the
 *   y-axis scale factor.
 *
 * Revision 1.45  2004/11/05 22:10:29  millermi
 * - Added methods getPositiveXmin() and getPositiveYmin()
 * - Replaced getXmin() and getYmin() with getPositiveXmin() and
 *   getPositiveYmin() in instances where log bounds are needed.
 * - Removed getScale() and setLogScale() since tru-log does not need
 *   these methods.
 *
 * Revision 1.44  2004/10/07 02:23:34  serumb
 * Made changes so Y_Log_scale works with negative numbers
 *
 * Revision 1.43  2004/08/17 03:50:46  ffr
 * Bugfix: Test the first point of each graph against the minimum and maximum in getXmin and getYmin
 *
 * Revision 1.42  2004/07/28 19:35:28  robertsonj
 * Used the TruLogScale function to map the original set of numbers 
 * logarithmically
 *
 * Revision 1.41  2004/04/21 02:36:12  millermi
 * - Added validation check to setData() and min/max calculations
 *   to make sure data wasn't null.
 *
 * Revision 1.40  2004/03/15 23:53:55  dennis
 * Removed unused imports, after factoring out the View components,
 * Math and other utils.
 *
 * Revision 1.39  2004/03/12 01:43:05  rmikk
 * Fixed Package Names
 *
 * Revision 1.38  2004/02/27 20:18:19  serumb
 * Set the data bounds to the proper min and max values.
 *
 * Revision 1.37  2004/01/09 20:32:13  serumb
 * Utilize getLocalLogWorldCoords to correct log
 * transformations.
 *
 * Revision 1.36  2004/01/06 22:49:11  serumb
 * Put in the correct bounds for the log scale util.
 *
 * Revision 1.35  2003/11/21 18:18:41  dennis
 * Now maintains a 5% border around the automatically calculated
 * bounds, in the y-direction.
 *
 * Revision 1.34  2003/11/05 20:44:08  serumb
 * Fix java docs.
 *
 * Revision 1.33  2003/10/30 17:13:36  serumb
 * Changed the paint function so that it marks all the points when
 * the point markers are selected.
 *
 * Revision 1.32  2003/10/16 15:18:11  serumb
 * Fixed java doc errors.
 *
 * Revision 1.31  2003/09/19 16:59:50  serumb
 * Fixed null pointer exception.
 *
 * Revision 1.30  2003/08/29 18:55:31  serumb
 * Added methods for getting the line, error bar, and point marker colors.
 *
 * Revision 1.29  2003/08/07 15:18:32  serumb
 * Changed paint method so transparent lines are not drawn.
 *
 * Revision 1.28  2003/08/05 23:21:45  serumb
 * Added methods for getting the log scale and for getting which axes
 * are logarithmically scaled.
 *
 * Revision 1.27  2003/07/31 15:16:56  dennis
 * Fixed off-by-one error with first_index.
 *
 * Revision 1.26  2003/07/30 20:57:19  serumb
 * Added function for checking if the axies are logarithimic,
 * and scale the data to the log scale.
 *
 * Revision 1.25  2003/07/18 15:05:58  serumb
 * Set the error value to the correct index.
 *
 * Revision 1.23  2003/07/03 16:12:41  serumb
 * Moved local_transform.MapTo(x_copy, y_copy) to after the if
 * block that uses origional x_copy and y_copy values.
 *
 * Revision 1.22  2003/07/02 22:37:28  dennis
 * Replaced code to map the graph data to pixel coordinates.
 *
 * Revision 1.20  2003/07/02 21:47:12  serumb
 * Updated java docs comments.
 *
 * Revision 1.17  2003/06/30 21:57:25  dennis
 * Removed shift by "first_index" that was improperly added.
 * Arrays x_xopy and y_copy in paint() only contain copies of the
 * data in the current zoom region.  They are indexed starting a
 * index 0 and should not have been made larger and indexed starting
 * at "first_index".
 *
 * Revision 1.16  2003/06/25 21:44:29  serumb
 * Added setErrorColor method, cleaned up strokeType method, and changed the 
 * paint method so it only draws point marks and error bars on the visible 
 * portion of the graph.
 *
 * Revision 1.15  2003/06/23 20:16:58  dennis
 * Fixed "off by one" error on check for valid GraphData index in
 * methods setErrors(), setColor(), setMarkColor(), setStroke(),
 * getStroke(), setLineWidth(), setMarkType(), setMarkSize().
 * Clearing graph vector now done with .clear() method.
 *
 * Revision 1.14  2003/06/20 16:19:35  serumb
 * Added method to set errors and added functionality for drawing error bars.
 *
 * Revision 1.13  2003/06/13 19:48:21  serumb
 * Added methods for setting mark color, getting the stroke for a line index,
 * getting a stroke type, setting the line width, and setting the mark size.
 * Also added functionality to put the point markers at the midpoints for
 * the histogram data.
 *
 * Revision 1.12  2003/06/09 22:34:54  serumb
 * SetStroke now takes in the line width as a parameter.
 *
 * Revision 1.11  2003/06/06 14:47:34  serumb
 * added different line styles(dotted, dashed, dash-dot-dot)
 * added point marks(dot, plus, star, box, cross)
 *
 * Revision 1.10  2002/11/27 23:13:18  pfpeterson
 * standardized header
 *
 * Revision 1.9  2002/10/04 14:41:34  dennis
 * getY_value() method now handles case where x_value is NaN.
 * ("Pointed At" x is initially NaN.)
 *
 * Revision 1.8  2002/06/14 20:52:11  dennis
 * Added field: auto_data_bound
 * Added methods: setX_bounds(), autoX_bounds(), set_auto_data_bound()
 *                is_autoX_bounds(), is_autoY_bounds()
 * To avoid recalculating the range of x and y values in some cases.
 * (Needed to fix problem with XRange update in ScrolledGraphView)
 *
 */

package gov.anl.ipns.ViewTools.Panels.Graph;

import java.awt.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import java.awt.geom.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import gov.anl.ipns.Util.Numeric.*;
import gov.anl.ipns.ViewTools.Components.*;
import gov.anl.ipns.ViewTools.Panels.Transforms.*;

  
/**
 *  A GraphJPanel Test object maintains and draws a list of graphs.
 */ 

public class GraphJPanel extends    CoordJPanel 
                         implements Serializable,
				    IPreserveState
{
      
/**
 * "x offset factor" - This constant String is a key for referencing the State
 * information about the x offset factor for the graphs when they are displayed 
 * in the GraphJPanel.
 */
 public static final String X_OFFSET_FACTOR = "x offset factor";

/**
 * "y offset factor" - This constant String is a key for referencing the State
 * information about the y offset factor for the graphs when they are displayed
 * in the GraphJPanel.
 */
  public static final String Y_OFFSET_FACTOR = "y offset factor";

/**
 * "log scale x" - This constant String is a key for referencing the State
 * information about the logarithmic x axis for the GraphJPanel. This 
 * log scale is a boolean value to determine if the log axis is drawn
 * for the x values.
 */
  public static final String LOG_SCALE_X = "log scale x";

/**
 * "log scale y" - This constant String is a key for referencing the State
 * information about the logarithmic y axis for the GraphJPanel. This
 * log scale is a boolean value to determine if the log axis is drawn
 * for the y values.
 */
  public static final String LOG_SCALE_Y = "log scale y";

/**
 * "Graph Data" - This constant String is a key for referencing the state 
 * information about the Graph Data.  Since the Graph Data has its own 
 * state, this value is of type ObjectState, and contains the state of the 
 * Graph Data.
 */
  public static final String GRAPH_DATA = "Graph Data";


  public  transient Vector  graphs;

  private transient boolean         y_bound_set = false;
  private transient boolean         x_bound_set = false;
  private int                       x_offset_factor = 0;  
  private int                       y_offset_factor = 0;  
  private transient boolean         remove_hidden_lines = false;
  private transient CoordBounds     auto_data_bound;
  private boolean         log_scale_x = false;
  private boolean         log_scale_y = false;
  private transient boolean min_max_x_valid = false;
  private transient boolean min_max_y_valid = false;
  private transient float maxy;
  private transient float minx;
  private transient float maxx;
  private transient float miny;
  private transient float min_positive_x;
  private transient float min_positive_y;
  private transient boolean 
	  reset_local = true; // This variable is used to determine
                                      // whether or not the local bounds
				      // are zoomed or global.

  public static final int DOT   = 1;
  public static final int PLUS  = 2;
  public static final int STAR  = 3;
  public static final int BOX   = 4;
  public static final int CROSS = 5;
  public static final int BAR	= 6;
  
  public static final int DOTTED   = 6;
  public static final int DASHED   = 7;
  public static final int LINE     = 8;
  public static final int DASHDOT  = 9;
  public static final int TRANSPARENT  = 10;
  public static final int ERROR_AT_POINT  = 11;
  public static final int ERROR_AT_TOP    = 12;
  
/* --------------------- Default Constructor ------------------------------ */

  public GraphJPanel()
  { 
    GraphData gd = new GraphData();
    addActionListener( new ZoomListener() );
    graphs = new Vector();
    graphs.addElement( gd );

    min_max_x_valid = false;
    min_max_y_valid = false;

    h_scroll = false;
    v_scroll = false;

    y_bound_set = false;
    x_bound_set = false;

    set_auto_data_bound();
  }

 // setState() and getState() are required by IPreserveState interface
 /**
  * This method will set the current state variables of the object to state
  * variables wrapped in the ObjectState passed in.
  *
  *  @param  new_state
  */
  public void setObjectState( ObjectState new_state )
  {
    boolean redraw = false;  // if any values change redraw.

    Object temp = new_state.get(GRAPH_DATA);
    for( int x = 0; x < graphs.size(); x++){
      String GRAPH_DATAX = GRAPH_DATA + x;	    
      temp = new_state.get(GRAPH_DATAX);
      if ( temp != null)
      {
      ((GraphData)graphs.elementAt(x)).setObjectState(
				    (ObjectState)temp);
      redraw = true;
      }
    }
                             // recalculate min & max after restoring graphs
    min_max_x_valid = false;  
    min_max_y_valid = false;
    getXmin();
    getYmin();
                                                    
    temp = new_state.get(X_OFFSET_FACTOR);
    if ( temp != null)
    {
      x_offset_factor = ((Integer)temp).intValue();
      redraw = true;
    } 

    temp = new_state.get(Y_OFFSET_FACTOR);
    if ( temp != null)
    {
      y_offset_factor = ((Integer)temp).intValue();
      redraw = true;
    }
    
    temp = new_state.get(LOG_SCALE_X);
    if ( temp != null)
    {
      log_scale_x = ((Boolean)temp).booleanValue();
      redraw = true;
    }

    temp = new_state.get(LOG_SCALE_Y);
    if ( temp != null)
    {
      log_scale_y = ((Boolean)temp).booleanValue();
      redraw = true;
    }

    if( redraw )
       repaint();
  }
	

  /**
   * This method will get the current values of the state variables for this
   * object. These variables will be wrapped in an ObjectState. Keys will be
   * put in alphabetic order.
   */
    public ObjectState getObjectState(boolean isDefault)
    {
      ObjectState state = new ObjectState();
      for(int x =0; x < graphs.size(); x++){
      String GRAPH_DATAX = GRAPH_DATA + x;
      state.insert(GRAPH_DATAX, 
		  ((GraphData)graphs.elementAt(x)).getObjectState(isDefault) );
      }
      state.insert(LOG_SCALE_X, new Boolean(log_scale_x) );
      state.insert(LOG_SCALE_Y, new Boolean(log_scale_y) );
      state.insert(X_OFFSET_FACTOR, new Integer(x_offset_factor) );
      state.insert(Y_OFFSET_FACTOR, new Integer(y_offset_factor) );

      if(! isDefault){
      }

      return state;
    } 
		      
  
/* ------------------------------- setData -------------------------------- */
/**
 *  Set the data for the "0th" graph to the specified x and y values.  The
 *  bounds of the graph will be recalculated based on all graphs currently 
 *  in this GraphJPanel object and the graphs will be redrawn. The graph will
 *  be drawn as a histogram, if there is one more x-value than y-values.  If
 *  there are the same number of x and y values, the graph will be drawn using
 *  a simple line graph.  This routine is included for compatibility with 
 *  previous versions.
 *
 *  @param  x_vals  array of floats for the x values of the graph
 *  @param  y_vals  array of floats for the y values of the graph.  
 *
 */   
  public void setData( float x_vals[], float y_vals[] )
  {
    setData( x_vals, y_vals, 0, true );
  }


/* ------------------------------- setData -------------------------------- */
/**
 *  Set the data for the specified graph to the specified x and y values.  The
 *  bounds of the graph will be recalculated based on all graphs currently
 *  in this GraphJPanel object and the graphs may be redrawn. A graph will
 *  be drawn as a histogram, if there is one more x-value than y-values.  If
 *  there are the same number of x and y values, the graph will be drawn using
 *  a simple line graph. 
 *
 *  @param  x_vals     array of floats for the x values of the graph
 *  @param  y_vals     array of floats for the y values of the graph.  
 *  @param  graph_num  the index of the graph to draw in the list of graphs.
 *                     The index must be at least zero and no more than the
 *                     number of graphs currently held in this GraphJPanel.  If
 *                     The index is less than the number of graphs currently 
 *                     held, the current graph with that index will be changed.
 *                     If the index equals the number of graphs currently held,
 *                     a new graph will be add to the list of graphs and filled
 *                     out with the specified data.  If the graph_num is not
 *                     valid, this method has no effect and returns false.
 *  @param  redraw     if this is true, redraw all the graphs
 *
 *  @return            true if the graph_num is valid, false otherwise. 
 *
 */ 
  public boolean setData( float   x_vals[], 
                          float   y_vals[], 
                          int     graph_num, 
                          boolean redraw )
  {
    if ( graph_num < 0 || graph_num > graphs.size() )  // only allow adding one
      return false;
    
    if( x_vals == null || x_vals.length == 0 ||
        y_vals == null || y_vals.length == 0 )
      return false;
    
    if ( graph_num == graphs.size() )                  // add a new graph
      graphs.addElement( new GraphData() ); 
   
    GraphData gd = (GraphData)graphs.elementAt( graph_num );   
    gd.x_vals = x_vals;
    gd.y_vals = y_vals;
    
    min_max_x_valid = false;
    min_max_y_valid = false;

    set_auto_data_bound();
    SetDataBounds();

    if ( redraw )
      repaint(); 

    return true;
  }
  
 /**
  * Call this method to update the pointed at graph. This method is used in
  * in place of setData() because it will not reset the bounds.
  *
  *  @param  x - X values of the graph.
  *  @param  y - Y values of the graph.
  */ 
  public void updatePointedAtGraph( float[] x, float[] y )
  {
    GraphData gd = (GraphData)graphs.elementAt(0);   
    gd.x_vals = x;
    gd.y_vals = y;

    min_max_x_valid = false;
    min_max_y_valid = false;

    set_auto_data_bound();
    SetDataBounds();
    repaint();
  }


/* ----------------------------- clearData ------------------------------- */
/**
 *  Remove all graphs and create a default simple graph consisting of a line
 *  joining (0,0) with (1,1).
 */
  public void clearData()
  {
    graphs.clear();
    graphs.addElement( new GraphData() );    

    min_max_x_valid = false;
    min_max_y_valid = false;

    set_auto_data_bound();
    SetDataBounds();
  }


/* ----------------------------- setErrors -------------------------------- */
/*  gets the errorbounds for each point of a line. */
/**
 *  Set the errors for the specified graph.  
 *
 *  @param  errors     the array of error values
 *
 *  @param  error_loc  the integer constant that tells where the error bars
 *                     ar to be drawn.
 *
 *  @param  graph_num  the index of the graph.
 *                     The index must be at least zero and less than the
 *                     number of graphs currently held in this GraphJPanel.  
 *                     If the graph_num is not valid, this method has no 
 *                     effect and returns false.
 *  @param  redraw     if this is true, redraw all the graphs
 *
 *  @return            true if the graph_num is valid, false otherwise.
 */
  public boolean setErrors( float[] errors, int error_loc,
                            int graph_num, boolean redraw )
  {
    if ( graph_num < 0 || graph_num >= graphs.size() )    // no such graph
      return false;
 
    GraphData gd = (GraphData)graphs.elementAt( graph_num );
    gd.setErrorVals(error_loc, errors);

    if ( redraw )
      repaint(); 
    return true;
  } 

/* ----------------------------- setErrorColor -------------------------------- */
/**
 *  Set the error color for the specified graph.  

 *  @param  color      the color of the error bars
 *
 *  @param  graph_num  the index of the graph whose color is set.
 *                     The index must be at least zero and less than the
 *                     number of graphs currently held in this GraphJPanel.  
 *                     If the graph_num is not valid, this method has no 
 *                     effect and returns false.
 *  @param  redraw     if this is true, redraw all the graphs
 *
 *  @return            true if the graph_num is valid, false otherwise.
 */
  public boolean setErrorColor( Color color, int graph_num, boolean redraw )
  {
    if ( graph_num < 0 || graph_num >= graphs.size() )    // no such graph
      return false;

    GraphData gd = (GraphData)graphs.elementAt( graph_num );
    gd.errorcolor = color; 

    if ( redraw )
      repaint(); 

    return true;
  }

/* ----------------------------- setColor -------------------------------- */
/**
 *  Set the color for the specified graph.  
 *
 *  @param  color      the color of the error bars
 *
 *  @param  graph_num  the index of the graph whose color is set.
 *                     The index must be at least zero and less than the
 *                     number of graphs currently held in this GraphJPanel.  
 *                     If the graph_num is not valid, this method has no 
 *                     effect and returns false.
 *  @param  redraw     if this is true, redraw all the graphs
 *
 *  @return            true if the graph_num is valid, false otherwise.
 */
  public boolean setColor( Color color, int graph_num, boolean redraw )
  {
    if ( graph_num < 0 || graph_num >= graphs.size() )    // no such graph
      return false;

    GraphData gd = (GraphData)graphs.elementAt( graph_num );
    gd.color = color; 

    if ( redraw )
      repaint(); 

    return true;
  }


/* ----------------------------- setMarkColor -------------------------------- */
/**
 *  Set the mark color for the specified graph.  
 *
 *  @param  color      the color of the error bars
 *
 *  @param  graph_num  the index of the graph whose color is set.
 *                     The index must be at least zero and less than the
 *                     number of graphs currently held in this GraphJPanel.  
 *                     If the graph_num is not valid, this method has no 
 *                     effect and returns false.
 *  @param  redraw     if this is true, redraw all the graphs
 *
 *  @return            true if the graph_num is valid, false otherwise.
 */
  public boolean setMarkColor( Color color, int graph_num, boolean redraw )
  {
    if ( graph_num < 0 || graph_num >= graphs.size() )    // no such graph
      return false;

    GraphData gd = (GraphData)graphs.elementAt( graph_num );
    gd.markcolor = color; 

    if ( redraw )
      repaint(); 

    return true;
  }

/* ----------------------------- getMarkColor --------------------------------*/
/**
 *  Gets the mark color for the specified graph.  
 *
 *  @param  graph_num  the index of the graph whose color is gotten.
 *                     The index must be at least zero and less than the
 *                     number of graphs currently held in this GraphJPanel.  
 *                     If the graph_num is not valid, this method has no 
 *                     effect and returns black.
 **/
  public Color getMarkColor( int graph_num )
  {
    if ( graph_num < 0 || graph_num >= graphs.size() )    // no such graph
      return Color.black;

    GraphData gd = (GraphData)graphs.elementAt( graph_num );
    return gd.markcolor;
  }

/* ----------------------------- getColor --------------------------------*/
/**
 *  Gets the mark color for the specified graph.  
 *
 *  @param  graph_num  the index of the graph whose color is gotten.
 *                     The index must be at least zero and less than the
 *                     number of graphs currently held in this GraphJPanel.  
 *                     If the graph_num is not valid, this method has no 
 *                     effect and returns black.
 **/
  public Color getColor( int graph_num )
  {
    if ( graph_num < 0 || graph_num >= graphs.size() )    // no such graph
      return Color.black;

    GraphData gd = (GraphData)graphs.elementAt( graph_num );
    return gd.color;
  }
/* ----------------------------- getErrorColor --------------------------------*/
/**
 *  Gets the mark color for the specified graph.  
 *
 *  @param  graph_num  the index of the graph whose color is gotten.
 *                     The index must be at least zero and less than the
 *                     number of graphs currently held in this GraphJPanel.  
 *                     If the graph_num is not valid, this method has no 
 *                     effect and returns black.
 **/
  public Color getErrorColor( int graph_num )
  {
    if ( graph_num < 0 || graph_num >= graphs.size() )    // no such graph
      return Color.black;

    GraphData gd = (GraphData)graphs.elementAt( graph_num );
    return gd.errorcolor;
  }

/*------------------------------ setStroke --------------------------------*/
/**
 *  Set the Stroke for the particular line in the graph.  
 *
 *  @param  strokeType the basic stroke type for a graph.
 *
 *  @param  graph_num  the index of the graph.
 *                     The index must be at least zero and less than the
 *                     number of graphs currently held in this GraphJPanel.  
 *                     If the graph_num is not valid, this method has no 
 *                     effect and returns false.
 *  @param  redraw     if this is true, redraw all the graphs
 *
 *  @return            true if the graph_num is valid, false otherwise.
 */
public boolean setStroke(int strokeType, int graph_num, boolean redraw)
  {
    if ( graph_num < 0 || graph_num >= graphs.size() )    // no such graph
      return false;

    GraphData gd = (GraphData)graphs.elementAt( graph_num );
    gd.linetype = strokeType; 

    if ( redraw )
      repaint(); 

    return true;
  }
/*------------------------------ setTransparent --------------------------------*/
/**
 *  Set the transparent variable to make a line in a graph transparent.  
 *
 *  @param  transparent the boolean value to tell if the line is transparent.
 *
 *  @param  graph_num   the index of the graph.
 *                      The index must be at least zero and less than the
 *                      number of graphs currently held in this GraphJPanel.  
 *                      If the graph_num is not valid, this method has no 
 *                      effect and returns false.
 *  @param  redraw      if this is true, redraw all the graphs
 *
 *  @return             true if the graph_num is valid, false otherwise.
 */

public boolean setTransparent(boolean transparent, int graph_num,
                              boolean redraw)
  {
    if ( graph_num < 0 || graph_num >= graphs.size() )    // no such graph
      return false;

    GraphData gd = (GraphData)graphs.elementAt( graph_num );
    gd.transparent = transparent; 

    if ( redraw )
      repaint(); 
    
    return true;
  }


/*------------------------------ getStroke --------------------------------*/
/**
 *  Get the stroke for a graph.  
 *
 *  @param  graph_num  the index of the graph.
 *                     The index must be at least zero and less than the
 *                     number of graphs currently held in this GraphJPanel.  
 *                     If the graph_num is not valid, the defult is returned.
 *
 *  @return            the Stroke type.
 */

public int getStroke(int graph_num)
  { 
    if ( graph_num < 0 || graph_num >= graphs.size() )    // no such graph
       return LINE;

    GraphData gd = (GraphData)graphs.elementAt( graph_num );
    return gd.linetype;
  }


/*------------------------------ StrokeType --------------------------------*/
/**
 *  Makes the different stroke types to be returned 
 *
 *  @param  key         the integer constant for the stroke types.
 *
 *  @param  graph_num   the index of the graph.
 *                      The index must be at least zero and less than the
 *                      number of graphs currently held in this GraphJPanel.  
 *                      If the graph_num is not valid, this method has no 
 *                      effect and returns false.
 *
 *  @return             the stroke type for the particular key.
 */

public BasicStroke strokeType(int key, int graph_num)
{
    if (graph_num < 0 || graph_num >= graphs.size() )    // no such graph
       return new BasicStroke();

    GraphData gd = (GraphData)graphs.elementAt( graph_num );
    return createStroke(key, gd.linewidth);
}


public static BasicStroke createStroke(int key, float linewidth)
{
    if (key == DASHED)
    {   
       float dash1[] = {10.0f};
       BasicStroke dashed = new BasicStroke(linewidth, 
                                       BasicStroke.CAP_SQUARE, 
                                       BasicStroke.JOIN_BEVEL, 
                                       10.0f, dash1, 0.0f);
       return dashed;
    }
    else if (key == DOTTED)
    { 
       float dots1[] = {0,6,0,6};
       BasicStroke dotted = new BasicStroke(linewidth, BasicStroke.CAP_ROUND,
                        BasicStroke.JOIN_BEVEL,
                        0, dots1, 0);
       return dotted;
    }
    else if (key == LINE)
    {
       BasicStroke stroke = new BasicStroke(linewidth);
       return stroke;
    }
    else if (key ==DASHDOT)
    {
       float[] dash2 = {6.0f, 4.0f, 2.0f, 4.0f, 2.0f, 4.0f};
       BasicStroke dashdot = new BasicStroke(linewidth, BasicStroke.CAP_BUTT,
            BasicStroke.JOIN_BEVEL, 10.0f, dash2, 0.0f);
       return dashdot;
    }
    else if (key == TRANSPARENT)
    {
       //float clear[] = {0.0f, 1000.0f};
       BasicStroke transparent = new BasicStroke(0.0f);
       return transparent;
    }    
    else 
    {
       System.out.println("ERROR: no Stroke of this type, default is returned");
       return new BasicStroke();
    }
}


/*-------------------------- setLineWidth ---------------------------------*/
/**
 *  Set the line width for the graph.  
 *
 *  @param  linewidth   the integer value for the line width
 *
 *  @param  graph_num   the index of the graph.
 *                      The index must be at least zero and less than the
 *                      number of graphs currently held in this GraphJPanel.  
 *                      If the graph_num is not valid, this method has no 
 *                      effect and returns false.
 *  @param  redraw      if this is true, redraw all the graphs
 *
 *  @return             true if the graph_num is valid, false otherwise.
 */

public boolean setLineWidth(int linewidth, int graph_num, boolean redraw)
{
    if ( graph_num < 0 || graph_num >= graphs.size() )    // no such graph
      return false;
 
    GraphData gd = (GraphData)graphs.elementAt( graph_num );
    gd.linewidth = linewidth; 

    if ( redraw )
      repaint(); 

    return true;
 }


/*-------------------------- setMarkType ---------------------------------*/
/**
 *  Set the mark type for the points on the graph.  
 *
 *  @param  marktype    the integer constant for the mark type.
 *
 *  @param  graph_num   the index of the graph.
 *                      The index must be at least zero and less than the
 *                      number of graphs currently held in this GraphJPanel.  
 *                      If the graph_num is not valid, this method has no 
 *                      effect and returns false.
 *  @param  redraw      if this is true, redraw all the graphs
 *
 *  @return             true if the graph_num is valid, false otherwise.
 */

public boolean setMarkType(int marktype, int graph_num, boolean redraw)
{
    if ( graph_num < 0 || graph_num >= graphs.size() )    // no such graph
      return false;
    GraphData gd = (GraphData)graphs.elementAt( graph_num );
    gd.marktype = marktype; 
	
    if ( redraw )
      repaint(); 

    return true;
 }


/*-------------------------- setMarkSize ---------------------------------*/
/**
 *  Set the mark size for the graph.
 *
 *  @param  size        the integer value for the mark size.
 *
 *  @param  graph_num   the index of the graph.
 *                      The index must be at least zero and less than the
 *                      number of graphs currently held in this GraphJPanel.  
 *                      If the graph_num is not valid, this method has no 
 *                      effect and returns false.
 *  @param  redraw      if this is true, redraw all the graphs
 *
 *  @return             true if the graph_num is valid, false otherwise.
 */
public boolean setMarkSize(int size, int graph_num, boolean redraw)
{
    if ( graph_num < 0 || graph_num >= graphs.size() )    // no such graph
      return false;
 
    GraphData gd = (GraphData)graphs.elementAt( graph_num );
    gd.marksize = size; 
	
    if ( redraw )
      repaint(); 

    return true;
 }


/* --------------------------- setLogScaleX  ------------------------ */
/**
  *  Set the boolean value to check wether or not to scale the x
  *  values for the graph.
  *
  *  @param  x_log       the boolean value to determine if the x values 
  *                      should be scaled.
  **/
  public void setLogScaleX(boolean x_log)
  {
    log_scale_x = x_log;
  }  


/* --------------------------- setLogScaleY  ------------------------ */
/**
 *  Set the boolean value to check wether or not to scale the y
 *  values for the graph.
 *
 *  @param  y_log       the boolean value to determine if the y values 
 *                      should be scaled.
 */
  public void setLogScaleY(boolean y_log)
  {
    log_scale_y = y_log;
  } 


/* --------------------------- getLogScaleX  ------------------------ */
/**
  *  Gets the boolean value to check wether or not to scale the x
  *  values for the graph.
  *
  *  @return  log_scale_x    the boolean value to determine if the x values 
  *                          should be scaled.
  **/
  public boolean getLogScaleX()
  {
    return log_scale_x;
  }  


/* --------------------------- getLogScaleY  ------------------------ */
/**
 *  Gets the boolean value to check wether or not to scale the y
 *  values for the graph.
 *
 *  @return  log_scale_y  the boolean value to determine if the y values 
 *                        should be scaled.
 */
  public boolean getLogScaleY()
  {
    return log_scale_y;
  } 

 
/**
 * Set the number of pixels that additional graphs are offset vertically
 * and horizontally.
 *
 * @param  x_offset the number of pixels that successive graphs are offset
 *                  from each other in the x direction.
 * 
 * @param  y_offset the number of pixels that successive graphs are offset
 *                  from each other in the y direction.
 */
  public void setMultiplotOffsets( int x_offset, int y_offset )
  {
    x_offset_factor = x_offset;
    y_offset_factor = y_offset;
  }


/* --------------------------- setRemoveHiddenLines ---------------------- */
/**
 * Determines whether or not hidden lines are to be removed when multiple
 * graphs are drawn.  The graphs are draw from back to front.  If hidden
 * line removal has been enabled, the region under the graph is filled 
 * with the background color to erase any part of previous lines that
 * would be visible under the next line. 
 *
 * @param  hide   flag that indicates whether or not the hidden lines are
 *                removed.
 */
  public void setRemoveHiddenLines( boolean hide )
  {
    remove_hidden_lines = hide;
  }


/* -------------------------- getNum_graphs ------------------------------ */
/**
 *  Get the number of graphs currently held by this GraphJPanel.
 *
 *  @return  the number of graphs currently held.  This will always be at least
 *           1, since it always maintains at least one default graph.
 */ 
  public int getNum_graphs()
  {
    return  graphs.size();
  }


/* ------------------------------- getY_value ----------------------------- */
/**
 *  Get the y value corresponding to the specified x_value on the spcified
 *  graph held by this GraphJPanel.  If the graph_number is not valid, this
 *  returns Float.NaN.  If the x_value is outside of the interval of x values 
 *  for the graph, this returns 0.  In other cases, the tablulated y values are
 *  interpolated to obtain an approximate y value at the specified x value.
 *
 *  @param  x_value      the x value for which the corresponding y value is to
 *                       be interpolated
 *  @param  graph_number index for the graph whose value is to be interpolated
 *
 *  @return interpolated y value at the specified x value, if the graph number
 *          and x value are valid. 
 */
public float getY_value( float x_value, int graph_number )
{
  if ( graph_number < 0 || graph_number >= graphs.size() )     // no such graph
    return Float.NaN;

  if ( Float.isNaN(x_value) )
    return Float.NaN;
   
  GraphData gd = (GraphData)graphs.elementAt(graph_number);

  if ( x_value < gd.x_vals[0] || x_value > gd.x_vals[gd.x_vals.length-1] )
    return 0.0f;
  
  int index = arrayUtil.get_index_of( x_value, gd.x_vals );

  if ( x_value == gd.x_vals[index] )    // if exact value is in list, return
  {                                     // the corresponding y value.  
    if ( index >= 0 && index < gd.y_vals.length )
      return gd.y_vals[index];            
    else
      return 0.0f;                      // we hit the last x-point of a
  }                                     // histogram so no correponding y

  float x1 = gd.x_vals[index];          // x_value between two listed x_vals 
  float x2 = gd.x_vals[index + 1];            

  if ( gd.y_vals.length < gd.x_vals.length || x1 == x2 )  // histogram, or 
    return gd.y_vals[index];                              // duplicate x values

  float y1 = gd.y_vals[index];                         // otherwise, interpolate
  float y2 = gd.y_vals[index+1];
  return y1 + ( x_value - x1 )*( y2 - y1 ) / ( x2 - x1 );
}


/* ------------------------------ setX_bounds ------------------------------- */
/**
 *  Specify a range of x values to use for the graph.  By default, the x
 *  range is automatically adjusted to the x range of the data.
 *
 *  @param  x_min  The smallest x value to be drawn.
 *  @param  x_max  Yhe largest x value to be drawn.
 */
public void setX_bounds( float x_min, float x_max )
{
  CoordBounds data_bound = getGlobalWorldCoords();

  data_bound.setBounds( x_min, data_bound.getY1(),    // change and "lock" the
                        x_max, data_bound.getY2());   // new x_min, x_max

  initializeWorldCoords( data_bound );

  repaint();
  x_bound_set = true;
}


/* ----------------------------- autoX_bounds ------------------------------ */
/**
 *  Set the x range fo the graph to the x range of data.  This is the default
 *  behavior.
 */ 

public void autoX_bounds( )
{
  x_bound_set = false;
  SetDataBounds();
  repaint();
}


/* ---------------------------- is_autoX_bounds ---------------------------- */
/**
 *  @return flag indicating whether or not the current X bounds have been 
 *          automatically determined from the data x values.
 */
public boolean is_autoX_bounds()
{
  return !x_bound_set;
}


/* ------------------------------ setY_bounds ------------------------------- *//**
 *  Specify a range of y values to use for the graph.  By default, the y
 *  range is automatically adjusted to the y range of the data.
 *
 *  @param  y_min  The smallest y value to be drawn.
 *  @param  y_max  The largest y value to be drawn.
 */
public void setY_bounds( float y_min, float y_max )
{
  CoordBounds data_bound = getGlobalWorldCoords();

  if ( y_min > y_max )
  {
    float temp = y_min;
    y_min = y_max;
    y_max = temp;
  } 

  data_bound.setBounds( data_bound.getX1(), y_min,     // change and "lock" the
                        data_bound.getX2(), y_max );   // new y_min, y_max

  data_bound.invertBounds();               // needed for "upside down" pixel

  initializeWorldCoords( data_bound );     // coordinates

  repaint();
  y_bound_set = true;
}


/* ----------------------------- autoY_bounds ------------------------------ */
/**
 *  Set the y range fo the graph to the y range of data.  This is the default
 *  behavior.
 */

public void autoY_bounds( )
{
  y_bound_set = false;
  SetDataBounds();
  repaint();
}


/* ---------------------------- is_autoY_bounds ---------------------------- */
/**
 *  @return flag indicating whether or not the current Y bounds have been 
 *          automatically determined from the data y values.
 */
public boolean is_autoY_bounds()
{
  return !y_bound_set;
}


/* -------------------------- paintComponent ----------------------------- */
/**
 *  Draw all of the graphs and/or markers.  NOTE: this method should not 
 *  be called directly by applications.  Applications should call repaint().
 */
  public void paintComponent( Graphics g )
  {
    stop_box( current_point, false );   // if the system redraws this without

    stop_crosshair( current_point );    // our knowlege, we've got to get rid
                                        // of the cursors, or the old position
                                        // will be drawn rather than erased
                                        // when the user moves the cursor (due
                                        // to XOR drawing).
    Graphics2D g2 = (Graphics2D)g.create();
    
    SetTransformsToWindowSize();
    int x_offset = 0;
    int y_offset = 0;
   
    CoordBounds bounds = getGlobalWorldCoords();    // temporarily don't clip
    //CoordBounds bounds = getLocalWorldCoords();
   
    float first_x = bounds.getX1();
    float last_x  = bounds.getX2();
    //bounds = getLocalWorldCoords();   

    CoordBounds unscaled_bounds = bounds.MakeCopy();
    unscaled_bounds.scaleBounds(1f,1f/getScaleFactor());
    
    if ( log_scale_x)
    {
      float min = getPositiveXmin();
      float max = getXmax();
      LogScaleUtil logger = new LogScaleUtil(min,max);


      first_x = logger.toDest(first_x);
      last_x = logger.toDest(last_x);
    }
    int height = getHeight();


    g2.setColor( getBackground() );
    g2.fillRect( 0, 0, getWidth(), getHeight() );


    for ( int gr_index = graphs.size()-1; gr_index >= 0; gr_index-- )
    {
      x_offset = x_offset_factor * gr_index; 
      y_offset = y_offset_factor * gr_index; 
      GraphData gd = (GraphData)graphs.elementAt(gr_index);

      boolean is_histogram = false;
      if ( gd.x_vals.length == gd.y_vals.length + 1 )
        is_histogram = true;

      int first_index;
      if ( first_x <= gd.x_vals[ 0 ] )
        first_index = 0;
      else if ( first_x >= gd.x_vals[ gd.x_vals.length-1 ] )
        first_index = gd.x_vals.length-1;
      else
        first_index = arrayUtil.get_index_of( first_x, gd.x_vals );

      if ( first_index > 0 )                     // include one extra point 
        first_index--;                           // to include first segment
                                                 // going off screen

      int last_index;
      if ( last_x <= gd.x_vals[ 0 ] )
        last_index = 0;
      else if ( last_x >= gd.x_vals[ gd.x_vals.length-1 ] )
        last_index = gd.x_vals.length-1;
      else
        last_index = arrayUtil.get_index_of( last_x, gd.x_vals );
      if ( last_index < gd.x_vals.length-1 )     // include one extra point 
        last_index++;                            // to include last segment
                                                 // going off screen
      int n_points = last_index - first_index + 1;
      if ( is_histogram )
        n_points--;

      if ( n_points < 1 )
        return;                                  // should clear the panel and
                                                 // then return
      float x_copy[];
      float y_copy[];
      if ( is_histogram )
      {
        x_copy = new float[ n_points + 1 ];
        System.arraycopy( gd.x_vals, first_index, x_copy, 
                          0, n_points+1 );
      }
      else
      {
        x_copy = new float[ n_points ];
        System.arraycopy( gd.x_vals, first_index, x_copy, 0, n_points );
      }
      y_copy = new float[ n_points ];
      System.arraycopy( gd.y_vals, first_index, y_copy, 0, n_points );
      
      if( log_scale_x )
      {
        float min = getPositiveXmin();
        float max = getXmax();

        LogScaleUtil logger = new LogScaleUtil(min,max);

        if( is_histogram ){
          for(int i = 0; i <= n_points; i++) {
            x_copy[i] = logger.toDest(x_copy[i]);
          }
        }else
          for(int i = 0; i < n_points; i++)
	  {
	    if( x_copy[i] > 0 )
              x_copy[i] = logger.toDest(x_copy[i]);
	    else
	      x_copy[i] = logger.toDest(getPositiveXmin());
          }
      }
  
      if( log_scale_y )
      {
        float min = getPositiveYmin();
        float max = getYmax();
        System.arraycopy( gd.y_vals, first_index, y_copy, 0, n_points );
	
	LogScaleUtil logger = new LogScaleUtil(min,max,unscaled_bounds.getY2(),
	                                       unscaled_bounds.getY1());

        for(int i = 0; i < n_points; i++)
	{
	  if( y_copy[i] > 0 )
	  {
	    //System.out.println("PreLogScale: "+y_copy[i]);
	    //System.out.println("LogScale: "+logger.toDest(y_copy[i]));
            y_copy[i] = logger.toDest( y_copy[i] );
	    //System.out.println("PostLogScale: "+y_copy[i]);
          }
	  // if not positive, move to position off of the graph.
	  else
	    y_copy[i] = logger.toDest(min);
	}
      }
  
       float error_bars_upper[] = null;
       float error_bars_lower[] = null;
      
      if ( gd.getErrorVals() != null )
      {
        float error_copy[] = new float[ n_points ];  
        error_bars_upper = new float[ n_points ];
        error_bars_lower = new float[ n_points ];
       
        System.arraycopy( gd.getErrorVals(), first_index, error_copy,
                          0, n_points); 
        for ( int i = 0; i < n_points ; i++ )
        {
	  if ( log_scale_y)
	   {
	      float min = getPositiveYmin();
	      float max = getYmax();
	      LogScaleUtil logger = new LogScaleUtil(min,max);
	      error_bars_upper[i] = logger.toDest( logger.toSource(y_copy[i]) + 
			      error_copy[i] );
	      error_bars_lower[i] = logger.toDest( logger.toSource(y_copy[i]) - 
			      error_copy[i] );
	   }   
	  else{						  
           error_bars_upper[i] = y_copy[i] + error_copy[i]; 
           error_bars_lower[i] = y_copy[i] - error_copy[i];
	  }  
        }
        getLocal_transform().MapYListTo(error_bars_upper); // map errors from WC to
        getLocal_transform().MapYListTo(error_bars_lower); // pixels
      }
         
      getLocal_transform().MapTo( x_copy, y_copy );       // map from WC to pixels

      g2.setStroke(strokeType(gd.linetype, gr_index));
      
      if ( x_copy.length == y_copy.length )            // Function data
      { 
       //if transparent do not draw line
       if(gd.transparent) {}
       else { 
        if ( remove_hidden_lines )
        {
          int x_int[] = new int[ n_points + 2 ];
          int y_int[] = new int[ n_points + 2 ];
          x_int[0] = (int)( x_copy[0] ) + x_offset;
          y_int[0] = height;
          for ( int i = 0; i < n_points; i++ )
          {
            x_int[i+1] = (int)( x_copy[i] ) + x_offset;
            y_int[i+1] = (int)( y_copy[i] ) - y_offset;
          }
          x_int[n_points+1] = (int)( x_copy[n_points-1] ) + x_offset;
          y_int[n_points+1] = height;
          g2.setColor( getBackground() );                     // solid fill to
	  g2.fillPolygon( x_int, y_int, n_points + 2 );       // hide lines
          System.arraycopy( x_int, 1, x_int, 0, n_points );  // now draw the
          System.arraycopy( y_int, 1, y_int, 0, n_points );  // data points
          g2.setColor( gd.color );                            // themselves
	  g2.drawPolyline( x_int, y_int, n_points );
        }
        else
        {
          int x_int[] = new int[ n_points ];
          int y_int[] = new int[ n_points ];
          for ( int i = 0; i < n_points; i++ )
          {
            x_int[i] = (int)( x_copy[i] ) + x_offset;
            y_int[i] = (int)( y_copy[i] ) - y_offset;
          }
          g2.setColor( gd.color );
          g2.drawPolyline( x_int, y_int, n_points );
        }
       } 
         
        /*
          Draw point markers if they are selected
        */ 
	if (gd.marktype != 0)
	{
          int x_int[] = new int[ n_points ];
          int y_int[] = new int[ n_points ];
          for ( int i = 0; i < n_points; i++ )
          {
            x_int[i] = (int)( x_copy[i] ) + x_offset;
            y_int[i] = (int)( y_copy[i] ) - y_offset;
          }
	  g2.setStroke(new BasicStroke(1));
          int size = gd.marksize;
	  g2.setColor( gd.markcolor );
	  int type = gd.marktype;
          for ( int i = 0; i < n_points - 1; i++ )
          {
	     if ( type == DOT )
              g2.drawLine( x_int[i], y_int[i], 
                           x_int[i], y_int[i] );      
             else if ( type == PLUS )
             {
               g2.drawLine( x_int[i]-size, y_int[i],
                            x_int[i]+size, y_int[i]      );      
               g2.drawLine( x_int[i],      y_int[i]-size, 
                            x_int[i],      y_int[i]+size );      
             }
             else if ( type == STAR )
             {
               g2.drawLine( x_int[i]-size, y_int[i],
                            x_int[i]+size, y_int[i]      );      
               g2.drawLine( x_int[i],      y_int[i]-size,
                            x_int[i],      y_int[i]+size );      
               g2.drawLine( x_int[i]-size, y_int[i]-size,
                            x_int[i]+size, y_int[i]+size );      
               g2.drawLine( x_int[i]-size, y_int[i]+size,
                            x_int[i]+size, y_int[i]-size );      
             }
             else if ( type == BOX )
             {
               g2.drawLine( x_int[i]-size, (y_int[i]-size), 
                            x_int[i]-size, (y_int[i]+size) );      
               g2.drawLine( x_int[i]-size, y_int[i]+size,
                            x_int[i]+size, y_int[i]+size );      
               g2.drawLine( x_int[i]+size, y_int[i]+size,
                            x_int[i]+size, y_int[i]-size );      
               g2.drawLine( x_int[i]+size, y_int[i]-size,
                            x_int[i]-size, y_int[i]-size );     
             }
             else if(type == CROSS)
             {
               g2.drawLine( x_int[i]-size, y_int[i]-size, 
                            x_int[i]+size, y_int[i]+size );      
               g2.drawLine( x_int[i]-size, y_int[i]+size, 
                            x_int[i]+size, y_int[i]-size );  
             }
             else	//BAR 
             {    
            	 g2.drawLine(x_int[i] ,y_int[i]-size ,x_int[i] ,y_int[i]+size );
             }    
	  } 
	} 
  
        /*
          Draw error bars if they are selected
        */ 
        if (gd.getErrorLocation() != 0 && gd.getErrorVals() != null)
        {
          int x_int[] = new int[ n_points ];
          int y_int[] = new int[ n_points ];
          for ( int i = 0; i < n_points; i++ )
          {
            x_int[i] = (int)( x_copy[i] ) + x_offset;
            y_int[i] = (int)( y_copy[i] ) - y_offset;
          }
          Line2D.Float line1 = new Line2D.Float();
          Line2D.Float line2 = new Line2D.Float();
          Line2D.Float line3 = new Line2D.Float();
          int size = 1;
          int loc = gd.getErrorLocation();
	  g2.setStroke(new BasicStroke(1));
	  g2.setColor( gd.errorcolor );
          for ( int i = 0; i < n_points; i++ )
          {
             if ( loc == ERROR_AT_POINT )
             {
               line1.setLine( x_int[i], error_bars_upper[i]-y_offset, 
	  	                     x_int[i], error_bars_lower[i]-y_offset);
               g2.draw(line1);
               line2.setLine( x_int[i] + size, error_bars_upper[i]-y_offset,
                              x_int[i] - size, error_bars_upper[i]-y_offset);
               g2.draw( line2 );
               line3.setLine( x_int[i] + size, error_bars_lower[i]-y_offset,
                              x_int[i] - size, error_bars_lower[i]-y_offset);   
               g2.draw( line3 );
             }
             else if (loc == ERROR_AT_TOP)
             {
               line1.setLine(x_int[i], error_bars_upper[i] - y_int[i], 
	  	             x_int[i], error_bars_lower[i] - y_int[i]);
               g2.draw(line1);
               line2.setLine(x_int[i] + size, error_bars_upper[i] - y_int[i], 
                             x_int[i] - size, error_bars_upper[i] - y_int[i]);
               g2.draw( line2 );
               line3.setLine(x_int[i] + size, error_bars_lower[i] - y_int[i],
                             x_int[i] - size, error_bars_lower[i] - y_int[i]);
               g2.draw( line3 );
             }

          }
        }
      }

      else if ( is_histogram )  // Histogram data
      { 
       //if transparent do not draw line
       if(gd.transparent) {}
       else { 	
        if ( remove_hidden_lines )
        {
          int x_int[] = new int[ 2*y_copy.length + 2 ];
          int y_int[] = new int[ 2*y_copy.length + 2 ];
          x_int[0] = (int)( x_copy[0] ) + x_offset;
          y_int[0] = height;
          for ( int i = 0; i < 2*y_copy.length; i++ )
          {
            x_int[i+1] = (int)( x_copy[(i+1)/2] ) + x_offset;
            y_int[i+1] = (int)( y_copy[i/2] ) - y_offset;
          }
          x_int[ x_int.length - 1 ] = (int)( x_copy[y_copy.length-1] )+x_offset;
          y_int[ x_int.length - 1 ] = height;

          g2.setColor( getBackground() );                       // solid fill to
          g2.fillPolygon( x_int, y_int, 2*y_copy.length + 2 );  // hide lines

          System.arraycopy( x_int, 1, x_int, 0, 2*y_copy.length ); //now draw 
          System.arraycopy( y_int, 1, y_int, 0, 2*y_copy.length ); //data points
          g2.setColor( gd.color );                                  //themselves
          g2.drawPolyline( x_int, y_int, 2*y_copy.length );
        }
        else
        {
          int x_int[] = new int[ 2*y_copy.length ];
          int y_int[] = new int[ 2*y_copy.length ];
          for ( int i = 0; i < 2*y_copy.length ; i++ )
          {
            x_int[i] = (int)( x_copy[(i+1)/2] ) + x_offset;
            y_int[i] = (int)( y_copy[i/2] ) - y_offset;
          }
          g2.setColor( gd.color );
          g2.drawPolyline( x_int, y_int, 2*y_copy.length );
        }
       }
        
        /*
          Draw point markers if they are selected
        */ 
	if (gd.marktype != 0)
	{
          int x_int[] = new int[ n_points ];
          int y_int[] = new int[ n_points ];
          for ( int i = 0; i < n_points; i++ )
          {
            x_int[i] = (int)( x_copy[i] ) + x_offset;
            y_int[i] = (int)( y_copy[i] ) - y_offset;
          }
	  g2.setStroke(new BasicStroke(1));
          int size = gd.marksize;
	  g2.setColor( gd.markcolor );
	  int type = gd.marktype;
          for ( int i = 0; i < n_points - 1; i++ )
          {
	     int x_midpt = ((x_int[i] + x_int[i+1])/2);
	     if ( type == DOT )
              g2.drawLine( x_midpt, y_int[i], 
	  			x_midpt, y_int[i] );      
             else if ( type == PLUS )
             {
               g2.drawLine( x_midpt-size, y_int[i],
	  		      x_midpt+size, y_int[i]      );      
              g2.drawLine( x_midpt,      y_int[i]-size, 
	  			x_midpt,      y_int[i]+size );      
             }
             else if ( type == STAR )
             {
               g2.drawLine( x_midpt-size, y_int[i],
                            x_midpt+size, y_int[i]      );      
               g2.drawLine( x_midpt,      y_int[i]-size,
                            x_midpt,      y_int[i]+size );      
               g2.drawLine( x_midpt-size, y_int[i]-size,
                            x_midpt+size, y_int[i]+size );      
               g2.drawLine( x_midpt-size, y_int[i]+size,
                            x_midpt+size, y_int[i]-size );      
             }
             else if ( type == BOX )
             {
               g2.drawLine( x_midpt-size, (y_int[i]-size), 
                            x_midpt-size, (y_int[i]+size) );      
               g2.drawLine( x_midpt-size, y_int[i]+size,
                            x_midpt+size, y_int[i]+size );      
               g2.drawLine( x_midpt+size, y_int[i]+size,
                            x_midpt+size, y_int[i]-size );      
               g2.drawLine( x_midpt+size, y_int[i]-size,
                            x_midpt-size, y_int[i]-size );     
             }
             else if (type == CROSS)
             {
               g2.drawLine( x_midpt-size, y_int[i]-size,
                            x_midpt+size, y_int[i]+size );      
               g2.drawLine( x_midpt-size, y_int[i]+size,
                            x_midpt+size, y_int[i]-size );      
             }
             else		//BAR
             {
                g2.drawLine(x_midpt, y_int[i]-size, x_midpt, y_int[i]+size);
             }
	  } 
	}
        
        /*
          Draw error bars if they are selected
        */ 
        if (gd.getErrorLocation() != 0 && gd.getErrorVals() != null)
        {
          float x_midpt;
          int x_int[] = new int[ n_points ];
          int y_int[] = new int[ n_points ];
          for ( int i = 0; i < n_points; i++ )
          {
            x_int[i] = (int)( x_copy[i] ) + x_offset;
            y_int[i] = (int)( y_copy[i] ) - y_offset;
          }
          Line2D.Float line1 = new Line2D.Float();
          Line2D.Float line2 = new Line2D.Float();
          Line2D.Float line3 = new Line2D.Float();
          int size = 1;
          int loc = gd.getErrorLocation();

	  g2.setStroke(new BasicStroke(1));
	  g2.setColor( gd.errorcolor );


          for ( int i =0 ; i <  n_points - 1; i++ )
          {
             x_midpt = ((x_int[i] + x_int[i+1])/2);
             if ( loc == ERROR_AT_POINT )
             {
               line1.setLine( x_midpt,  error_bars_upper[i]-y_offset, 
	  	            x_midpt,  error_bars_lower[i]-y_offset);
               g2.draw(line1);
               line2.setLine( x_midpt + size, error_bars_upper[i]-y_offset,
                              x_midpt - size, error_bars_upper[i]-y_offset);
               g2.draw( line2 );
               line3.setLine( x_midpt + size, error_bars_lower[i]-y_offset,
                              x_midpt - size, error_bars_lower[i]-y_offset);   
               g2.draw( line3 );
             }

            else if (loc == ERROR_AT_TOP)
             {
               line1.setLine(x_midpt, error_bars_upper[i] - y_int[i], 
	  	             x_midpt, error_bars_lower[i] - y_int[i]);
               g2.draw(line1);
               line2.setLine(x_midpt + size, error_bars_upper[i] - y_int[i], 
                             x_midpt - size, error_bars_upper[i] - y_int[i]);
               g2.draw( line2 );
               line3.setLine(x_midpt + size, error_bars_lower[i] - y_int[i],
                             x_midpt - size, error_bars_lower[i] - y_int[i]);   
               g2.draw( line3 );
            }
          }   
        }
      }
      else 
       System.out.println("ERROR: x&y arrays don't match in GraphJPanel.paint");
    } 

    g2.dispose();
  }


/* ---------------------------- getPreferredSize ------------------------- */
/**
 *  Calculate the preferred size based on whether or not the graph should be
 *  scrolled horizontally. 
 */
public Dimension getPreferredSize()
{
    if ( preferred_size != null )     // if someone has specified a preferred
      return preferred_size;          // size, just use it.  
                                     
                                      // Otherwise, calculate a preferred width 
    int rows = 0;                     // based on the data length, if scrolling
    int cols = 0;                     // is being done.
    GraphData gd;
                                 // if we're doing horizontal scrolling, find
                                 // the maximum number of data points in any 
                                 // graph and use that for the preferred width.
    if ( h_scroll )
      for ( int i = 0; i < graphs.size(); i++ )
      {
        gd = (GraphData)graphs.elementAt(i);
        if ( cols < gd.x_vals.length-1 )  
          cols = gd.x_vals.length-1;
      }

    return new Dimension( cols, rows );
}


/**
 *  Force the specified interval to be non-degnerate.  If points are NaN
 *  or are equal, form an appropriate default non-degenerate interval.
 */
private float[] FixInterval( float min, float max )
{
   if ( Float.isNaN( min ) || Float.isNaN( max ) )
   {
     min = 0;            // take usable defaults
     max = 1;
   }
   if ( min == max )    // split them up
   {
      if ( max == 0 )
        max = 1;
      else if ( max > 0 )           // provide some headroom above graph
      {                             // and start at 0
        min = -0.1f * max;
        max =  1.1f * max;
      }
      else  // max < 0
      {
        max = -0.1f * min;
        min =  1.1f * min;
      }
   }
   
   float vals[] = new float[2];
   vals[0] = min;
   vals[1] = max;
   return vals;
}


private void set_auto_data_bound()
{
   auto_data_bound = new CoordBounds();
  /* GraphData gd = (GraphData)graphs.elementAt(0);
   auto_data_bound.setBounds( gd.x_vals, gd.y_vals );
   for ( int i = 1; i < graphs.size(); i++ )
   {
     gd = (GraphData)graphs.elementAt(i);
     auto_data_bound.growBounds( gd.x_vals, gd.y_vals );
   }
*/
   float xmin, xmax, ymin, ymax;
                                        // get min/max, but force the
                                        // interval to be non-degenerate
   xmin = getXmin();
   xmax = getXmax();
   float fixed_vals[] = FixInterval( xmin, xmax );
   xmin = fixed_vals[0];
   xmax = fixed_vals[1];

   ymin = getYmin();
   ymax = getYmax();
   fixed_vals = FixInterval( ymin, ymax );
   ymin = fixed_vals[0];
   ymax = fixed_vals[1];

   if( getLogScaleX() )
     xmin = getPositiveXmin();
   if( getLogScaleY() )
     ymin = getPositiveYmin();
   
   auto_data_bound.setBounds( xmin, ymin, xmax, ymax );
   auto_data_bound.scaleBounds( 1.0f, getScaleFactor() );
}


/* --------------------------- SetDataBounds ----------------------------- */
private void SetDataBounds()
{
    float x1, y1, x2, y2;
                                       // get both the currently set WC bounds
                                       // and the automatically scaled bounds
    CoordBounds current_bound = getGlobalWorldCoords().MakeCopy();
    current_bound.invertBounds();
                                      // choose new y_bounds based on flag
    if ( y_bound_set )
    {
      y1 = current_bound.getY1();
      y2 = current_bound.getY2();
    }
    else
    {
      y1 = auto_data_bound.getY1();
      y2 = auto_data_bound.getY2();
    }

                                      // choose new x_bounds based on flag
    if ( x_bound_set )
    {
      x1 = current_bound.getX1();
      x2 = current_bound.getX2();
    }
    else
    {
      x1 = auto_data_bound.getX1();
      x2 = auto_data_bound.getX2();
    }

    if ( y1 > y2 )
    {
      float temp = y1;
      y1 = y2;
      y2 = temp;
    }

    CoordBounds data_bound =  new CoordBounds( x1, y1, x2, y2 );
    data_bound.invertBounds();               // needed for "upside down" pixel
                                             // coordinates
  
    CoordBounds local_bounds = getLocalWorldCoords();
    // If "don't reset local bounds and local bounds are within the
    // global bounds, only reset the world coordinates.

    if( !reset_local )
      setGlobalWorldCoords(data_bound);
    // Otherwise reinitialize both.
    else
      initializeWorldCoords( data_bound );
}


/**
 * This method will return the scale factor used on the y-axis to create a
 * border above and below the graph.
 *
 *  @return Scale factor used to scale y-axis.
 */
public float getScaleFactor()
{
  return 1.05f;
}


/* ------------------------------getYmin--------------------------------*/
/**
 *  Get the minimum y value from the current list of graphs
 */
public float getYmin()
{
  if ( min_max_y_valid )
    return miny;
  //System.out.println("Calculating Y min");

  // check number of graphs
  if( graphs.size() == 0 || ((GraphData)graphs.elementAt(0)).y_vals == null )
    return Float.NaN;
    GraphData gd = (GraphData)graphs.elementAt(0);
    float [] yvals = gd.y_vals;
                                                                                
    miny =  yvals[0];
    maxy =  yvals[0];
    min_positive_y = Float.POSITIVE_INFINITY;               
    float val;
    for (int line=0; line < graphs.size(); line++)
        {
          gd = (GraphData)graphs.elementAt(line);
          yvals = gd.y_vals;
           for (int i=0; i < yvals.length; i++)
           {
              val = yvals[i];
              if (val < miny)
                miny = val;
              else if (val > maxy)       // NOTE: since this is an "inner loop"
                maxy = val;              // it helps to NOT compare to maxy 
                                         // if it was already less than miny 

              if( val > 0 && val < min_positive_y )
	        min_positive_y = val;
	   }
        }
    min_max_y_valid = true;
    return miny;
}


/*------------------------------- getYmax -----------------------------------*/
/**
 *  Get the max y value from the current list of graphs
 */
public float getYmax()
{
  if ( !min_max_y_valid )
    getYmin();            // go through calcluation of min and max y

  return maxy;
}


/*---------------------------- getPositiveYmin ------------------------------*/
/**
 *  Get the minimum y value that is greater than 0, 
 *  from the current list of graphs.  If all values are less than or
 *  equal to zero, this will return Float.POSITIVE_INFINITY.
 */
public float getPositiveYmin()
{
  if ( !min_max_y_valid )
    getYmin();            // go through calcluation of min and max y

  return min_positive_y;
}


/* ------------------------------getXmin--------------------------------*/     
/**
 *  Get the minimum x value from the current list of graphs
 */
public float getXmin()
{
  if ( min_max_x_valid )
    return minx;
  // System.out.println("Calculating X min");

  // check number of graphs
  if( graphs.size() == 0 || ((GraphData)graphs.elementAt(0)).y_vals == null )
    return Float.NaN;
    GraphData gd = (GraphData)graphs.elementAt(0);
    float [] xvals = gd.x_vals;
    
    minx =  xvals[0];
    maxx =  xvals[0];
    min_positive_x = Float.POSITIVE_INFINITY;
    float val;
    for (int line=0; line < graphs.size(); line++)
        {
          gd = (GraphData)graphs.elementAt(line);
          xvals = gd.x_vals;
           for (int i=0; i < xvals.length; i++)
           {
              val = xvals[i];
              if (val < minx)
                minx = val;
              else if (val > maxx)       // NOTE: since this is an "inner loop"
                maxx = val;              // it helps to not compare with maxx
                                         // if it was already less than minx

              if( val > 0 && val < min_positive_x )
	        min_positive_x = val;
           }
        }
    min_max_x_valid = true;
    return minx;
}


/*------------------------------- getXmax -----------------------------------*/
/**
 *  Get the maximum x value from the current list of graphs
 */
public float getXmax()
{
  if ( !min_max_x_valid )   // go through calculation of min and max x
    getXmin();

  return maxx;
}


/*---------------------------- getPositiveXmin -----------------------------*/
/**
 *  Get the minimum x value that is greater than 0, 
 *  from the current list of graphs. If all values are less than or
 *  equal to zero, this will return Float.POSITIVE_INFINITY.
 */
public float getPositiveXmin()
{
  if ( !min_max_x_valid )   // go through calculation of min and max x
    getXmin();

  return min_positive_x;
}


/*
 * This listener flags whether the current local region is a zoomed region
 * or the global bounds. This allows a zoomed region to remain zoomed even
 * after the pointed at graph is updated.
 */
private class ZoomListener implements ActionListener
{
  public void actionPerformed( ActionEvent ae )
  {
    String command = ae.getActionCommand();
    if( command.equals(ZOOM_IN) )
      reset_local = false;
    else if( command.equals(RESET_ZOOM) )
      reset_local = true;
  }
}


/*--------------------------getLocalLogWorldCoords()------------------------*/
/*
 * This method returns positive current local world coordinate bounds. Like
 * getLocalWorldCoords(), this method gives the bounds of the zoomed region.
 * However, if any of the bounds are negative, they are replaced with the
 * smallest positive value for that interval.
 *
 *  @return Positive local world coord bounds.
 *
public CoordBounds getLocalLogWorldCoords()
{
  CoordBounds world_coords = super.getLocalWorldCoords();
  // If x2 < x1, see if x2 is less than zero. If so, replace the negative
  // number with the minimum positive value.
  if( world_coords.getX1() > world_coords.getX2() )
  {
    if( world_coords.getX2() < 0 )
    {
      // Call this to set min_positive_x.
      getXmin();
      world_coords.setBounds( world_coords.getX1(), world_coords.getY1(),
    			      min_positive_x, world_coords.getY2() );
    }
  }
  // If x1 < x2, check to make sure x1 is positive.
  else if( world_coords.getX1() < 0 )
  {
    // Call this to set min_positive_x.
    getXmin();
    world_coords.setBounds( min_positive_x, world_coords.getY1(),
                            world_coords.getX2(), world_coords.getY2() );
  }

  // Do the same steps above for the y axis.
  if( world_coords.getY1() > world_coords.getY2() )
  {
    if( world_coords.getY2() < 0 )
    {
      // Call this to set min_positive_y.
      getYmin();
      world_coords.setBounds( world_coords.getX1(), world_coords.getY1(),
    			      world_coords.getX2(), min_positive_y );
    }
  }
  else if( world_coords.getY1() < 0 )
  {
    // Call this to set min_positive_y.
    getYmin();
    world_coords.setBounds( world_coords.getX1(), min_positive_y,
                            world_coords.getX2(), world_coords.getY2() );
  }
  return world_coords;
}
*/

/* -------------------------------- Main ------------------------------- */

  /* Basic main program for testing purposes only. */
  public static void main(String[] args)
  {
    JFrame f = new JFrame("Test for ImageJPanel");
    f.setBounds(0,0,500,500);
    GraphJPanel graph = new GraphJPanel();
    f.getContentPane().add(graph);
    f.setVisible(true);

    float g1_x_vals[] = { 0, (float).2, (float).4, 1 };
    float g1_y_vals[] = { 0, (float).3, (float).2, 1 };
    float g1_e_vals[] = { 0.1f, 0.2f, 0.3f, 0.4f };

    float g2_x_vals[] = { 0, 1 };
    float g2_y_vals[] = { 1, 0 };

    float g3_x_vals[] = { 0, (float).5, (float).6, 1};
    float g3_y_vals[] = { (float).1, (float).2, (float).7, (float).6 };

    float g4_x_vals[] = { 0, (float).4, (float).6, 1};
    float g4_y_vals[] = { (float).3, (float).1, (float).4, (float).2 };

    float g5_x_vals[] = { 0, -.4f, .6f, 1};
    float g5_y_vals[] = { .3f, .1f, .4f, -.2f };

    graph.setBackground(Color.white);
    graph.setData( g1_x_vals, g1_y_vals, 0, false );
    graph.setErrors( g1_e_vals, 11, 1, true );
    graph.setColor( Color.black, 0, false );
    graph.setStroke( TRANSPARENT, 0, false);
    graph.setLineWidth(1,0,true);
    graph.setMarkColor(Color.green,0,false);
    graph.setMarkType(BOX, 0, false);

    graph.setData( g2_x_vals, g2_y_vals, 1, true );
    graph.setColor( Color.red, 1, true );
    graph.setStroke(DOTTED, 1, true);

    graph.setData( g3_x_vals, g3_y_vals, 2, true );
    graph.setColor( Color.green, 2, false );
    graph.setStroke(LINE, 2, true);
    graph.setMarkColor(Color.red,2,true);
    graph.setMarkType(CROSS, 2, true);
   
    graph.setData( g4_x_vals, g4_y_vals, 3, true );
    graph.setColor( Color.blue, 3, false );
    graph.setStroke( DASHDOT, 3, true);
    graph.setMarkColor(Color.black,2,true);
    graph.setMarkType(STAR, 3, true);
    
    graph.setData( g5_x_vals, g5_y_vals, 4, true );
    System.out.println("Pos X Min: "+graph.getPositiveXmin());
    System.out.println("Pos Y Min: "+graph.getPositiveYmin());
  }
}
