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
 * Revision 1.21  2003/07/02 22:31:49  serumb
 * Fixed ImageView display problem.
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

package DataSetTools.components.image;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.awt.image.*;
import java.awt.event.*;
import javax.swing.*;
import DataSetTools.util.*;
import DataSetTools.math.*;
import DataSetTools.components.ThreeD.*;
import java.lang.Object.*;
import java.awt.geom.*;

/**
 *  A GraphJPanelUdTest object maintains and draws a list of graphs.
 */ 

public class GraphJPanel extends    CoordJPanel 
                         implements Serializable
{
  public Vector  graphs;

  private boolean         y_bound_set = false;
  private boolean         x_bound_set = false;
  private int             x_offset_factor = 0;  
  private int             y_offset_factor = 0;  
  private boolean         remove_hidden_lines = false;
  private CoordBounds     auto_data_bound;


  public static final int DOT   = 1;
  public static final int PLUS  = 2;
  public static final int STAR  = 3;
  public static final int BOX   = 4;
  public static final int CROSS = 5;
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

    graphs = new Vector();
    graphs.addElement( gd );

    h_scroll = false;
    v_scroll = false;

    y_bound_set = false;
    x_bound_set = false;

    set_auto_data_bound();
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

    if ( graph_num == graphs.size() )                  // add a new graph
      graphs.addElement( new GraphData() ); 
   
    GraphData gd = (GraphData)graphs.elementAt( graph_num );   
    gd.x_vals = x_vals;
    gd.y_vals = y_vals;
    
    set_auto_data_bound();
    SetDataBounds();

    if ( redraw )
      repaint(); 

    return true;
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


/*------------------------------ setStroke --------------------------------*/
/**
 *  Set the Stroke for the particular line in the graph.  
 *
 *  @param  theStroke  the basic stroke type for a graph
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
public boolean setStroke(BasicStroke theStroke, int graph_num, boolean redraw)
  {
    if ( graph_num < 0 || graph_num >= graphs.size() )    // no such graph
      return false;

    GraphData gd = (GraphData)graphs.elementAt( graph_num );
    gd.Stroke = theStroke; 

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

public BasicStroke getStroke(int graph_num)
  { 
    if ( graph_num < 0 || graph_num >= graphs.size() )    // no such graph
       return new BasicStroke();

    GraphData gd = (GraphData)graphs.elementAt( graph_num );
    return gd.Stroke;
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

    if (key == DASHED)
    {   
       float dash1[] = {10.0f};
       BasicStroke dashed = new BasicStroke(gd.linewidth, 
                                       BasicStroke.CAP_SQUARE, 
                                       BasicStroke.JOIN_BEVEL, 
                                       10.0f, dash1, 0.0f);
       return dashed;
    }
    else if (key == DOTTED)
    { 
       float dots1[] = {0,6,0,6};
       BasicStroke dotted = new BasicStroke(gd.linewidth, BasicStroke.CAP_ROUND,
				            BasicStroke.JOIN_BEVEL,
				            0, dots1, 0);
       return dotted;
    }
    else if (key == LINE)
    {
       BasicStroke stroke = new BasicStroke(gd.linewidth);
       return stroke;
    }
    else if (key ==DASHDOT)
    {
       float[] dash2 = {6.0f, 4.0f, 2.0f, 4.0f, 2.0f, 4.0f};
       BasicStroke dashdot = new BasicStroke(gd.linewidth, BasicStroke.CAP_BUTT,
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
 *  @param  lineWidth   the integer value for the line width
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

/* --------------------------- setMultiPlotOffsets ------------------------ */
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
    return gd.y_vals[index];            // the corresponding y value.  

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
 *  @param  x_min  the smallest x value to be drawn
 *  @parma  x_max  the largest x value to be drawn
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
 *  @param  y_min  the smallest y value to be drawn
 *  @parma  y_max  the largest y value to be drawn
 */
public void setY_bounds( float y_min, float y_max )
{
  CoordBounds data_bound = getGlobalWorldCoords();

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



/* --------------------------------- paint ------------------------------- */
/**
 *  Draw all of the graphs.
 */
  public void paint( Graphics g )
  {
    stop_box( current_point, false );   // if the system redraws this without
    stop_crosshair( current_point );    // our knowlege, we've got to get rid
                                        // of the cursors, or the old position
                                        // will be drawn rather than erased
                                        // when the user moves the cursor (due
                                        // to XOR drawing).
    super.paint(g);
    Graphics2D g2 = (Graphics2D)g;
    
    SetTransformsToWindowSize();
    int x_offset = 0;
    int y_offset = 0;

    CoordBounds bounds = getLocalWorldCoords();
    float first_x = bounds.getX1();
    float last_x  = bounds.getX2();	


    int height = getHeight();



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
  
      float error_bars_upper[] = null;
      float error_bars_lower[] = null;
      if ( gd.getErrorVals() != null )
      {
        //System.out.println("Copying errors " + gr_index + ", " + n_points );
        error_bars_upper = new float[ n_points ];
        error_bars_lower = new float[ n_points ]; 

        for ( int i = 0; i < n_points; i++ )
        {
           error_bars_upper[i] = y_copy[i] + gd.getErrorVals()[i]; 
           error_bars_lower[i] = y_copy[i] - gd.getErrorVals()[i];
        }
        local_transform.MapYListTo(error_bars_upper);
        local_transform.MapYListTo(error_bars_lower);
      }
      
        local_transform.MapTo( x_copy, y_copy );       // map from WC to pixels
      if(gd.transparent) {
         g2.setComposite((Composite)AlphaComposite.getInstance(
                                    AlphaComposite.SRC_OVER, 0.0f));
      }
      else {
         g2.setComposite((Composite)AlphaComposite.getInstance(
                                    AlphaComposite.SRC_OVER, 1.0f));
      }  
         

      g2.setStroke(gd.Stroke);
      
      if ( x_copy.length == y_copy.length )            // Function data
      { 
        
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
        /* 
          Sets the graphics two d back to visible to show point markers 
          and error bars for transparent lines.
        */
         if(gd.transparent)
         {
           g2.setComposite((Composite)AlphaComposite.getInstance(
                                      AlphaComposite.SRC_OVER, 1.0f));
         } 
         
        /*
          Draw point markers if they are selected
        */ 
	if (gd.marktype != 0)
	{
	  g2.setStroke(new BasicStroke(1));
          int size = gd.marksize;
	  g2.setColor( gd.markcolor );
	  int type = gd.marktype;
          for ( int i = 0; i < n_points; i++ )
          {
	     
	     if ( type == DOT )
              g2.drawLine( (int)x_copy[i], (int)y_copy[i], 
	  			(int)x_copy[i], (int)y_copy[i] );      
             else if ( type == PLUS )
             {
               g2.drawLine( (int)x_copy[i]-size, (int)y_copy[i],
	  		      (int)x_copy[i]+size, (int)y_copy[i]      );      
              g2.drawLine( (int)x_copy[i],      (int)y_copy[i]-size, 
	  			(int)x_copy[i],      (int)y_copy[i]+size );      
             }
             else if ( type == STAR )
             {
               g2.drawLine( (int)x_copy[i]-size, (int)y_copy[i],
	  			 (int)x_copy[i]+size, (int)y_copy[i]      );      
               g2.drawLine( (int)x_copy[i],      (int)y_copy[i]-size,
	  			 (int)x_copy[i],      (int)y_copy[i]+size );      
               g2.drawLine( (int)x_copy[i]-size, (int)y_copy[i]-size,
	  			 (int)x_copy[i]+size, (int)y_copy[i]+size );      
               g2.drawLine( (int)x_copy[i]-size, (int)y_copy[i]+size,
				 (int)x_copy[i]+size, (int)y_copy[i]-size );      
             }
             else if ( type == BOX )
             {
               g2.drawLine( (int)x_copy[i]-size, (int)(y_copy[i]-size), 
	 		(int)x_copy[i]-size, (int)(y_copy[i]+size) );      
               g2.drawLine( (int)x_copy[i]-size, (int)y_copy[i]+size,
			 (int)x_copy[i]+size, (int)y_copy[i]+size );      
               g2.drawLine( (int)x_copy[i]+size, (int)y_copy[i]+size,
	 		 (int)x_copy[i]+size, (int)y_copy[i]-size );      
               g2.drawLine( (int)x_copy[i]+size, (int)y_copy[i]-size,
	 		 (int)x_copy[i]-size, (int)y_copy[i]-size );     
             }
             else   // type = CROSS
             {
               g2.drawLine( (int)x_copy[i]-size, (int)y_copy[i]-size,
	  		 (int)x_copy[i]+size, (int)y_copy[i]+size );      
               g2.drawLine( (int)x_copy[i]-size, (int)y_copy[i]+size,
	  		 (int)x_copy[i]+size, (int)y_copy[i]-size );      
             }    
	  } 
	} 
  
        /*
          Draw error bars if they are selected
        */ 
        if (gd.getErrorLocation() != 0)
        {
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
               line1.setLine( x_copy[i], error_bars_upper[i], 
	  	                     x_copy[i], error_bars_lower[i]);
               g2.draw(line1);
               line2.setLine( x_copy[i] + size, error_bars_upper[i],
                                     x_copy[i] - size, error_bars_upper[i]);
               g2.draw( line2 );
               line3.setLine( x_copy[i] + size, error_bars_lower[i],
                                     x_copy[i] - size, error_bars_lower[i]);   
               g2.draw( line3 );
             }

          }
        }



      }
      else if ( is_histogram )  // Histogram data
      { 
	
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
          for ( int i = 0; i < 2*y_copy.length; i++ )
          {
            x_int[i] = (int)( x_copy[(i+1)/2] ) + x_offset;
            y_int[i] = (int)( y_copy[i/2] ) - y_offset;
          }
          //System.out.println("the stroke width:" + gd.Stroke.getLineWidth());
          g2.setColor( gd.color );
          g2.drawPolyline( x_int, y_int, 2*y_copy.length );
        }
        /* 
          Sets the graphics twod back to visible to show point markers 
          and error bars for transparent lines.
        */
         if(gd.transparent)
         {
           g2.setComposite((Composite)AlphaComposite.getInstance(
                                      AlphaComposite.SRC_OVER, 1.0f));
         } 
        
        /*
          Draw point markers if they are selected
        */ 
	if (gd.marktype != 0)
	{
	  g2.setStroke(new BasicStroke(1));
          int size = gd.marksize;
	  g2.setColor( gd.markcolor );
	  int type = gd.marktype;
          for ( int i = 0; i < n_points; i++ )
          {
	     int x_midpt = (int)((x_copy[i] + x_copy[i+1])/2);
	     if ( type == DOT )
              g2.drawLine( x_midpt, (int)y_copy[i], 
	  			x_midpt, (int)y_copy[i] );      
             else if ( type == PLUS )
             {
               g2.drawLine( x_midpt-size, (int)y_copy[i],
	  		      x_midpt+size, (int)y_copy[i]      );      
              g2.drawLine( x_midpt,      (int)y_copy[i]-size, 
	  			x_midpt,      (int)y_copy[i]+size );      
             }
             else if ( type == STAR )
             {
               g2.drawLine( x_midpt-size, (int)y_copy[i],
	  			 x_midpt+size, (int)y_copy[i]      );      
               g2.drawLine( x_midpt,      (int)y_copy[i]-size,
	  			 x_midpt,      (int)y_copy[i]+size );      
               g2.drawLine( x_midpt-size, (int)y_copy[i]-size,
	  			 x_midpt+size, (int)y_copy[i]+size );      
               g2.drawLine( x_midpt-size, (int)y_copy[i]+size,
				 x_midpt+size, (int)y_copy[i]-size );      
             }
             else if ( type == BOX )
             {
               g2.drawLine( x_midpt-size, (int)(y_copy[i]-size), 
	 		x_midpt-size, (int)(y_copy[i]+size) );      
               g2.drawLine( x_midpt-size, (int)y_copy[i]+size,
			 x_midpt+size, (int)y_copy[i]+size );      
               g2.drawLine( x_midpt+size, (int)y_copy[i]+size,
	 		 x_midpt+size, (int)y_copy[i]-size );      
               g2.drawLine( x_midpt+size, (int)y_copy[i]-size,
	 		 x_midpt-size, (int)y_copy[i]-size );     
             }
             else   // type = CROSS
             {
               g2.drawLine( x_midpt-size, (int)y_copy[i]-size,
	  		 x_midpt+size, (int)y_copy[i]+size );      
               g2.drawLine( x_midpt-size, (int)y_copy[i]+size,
	  		 x_midpt+size, (int)y_copy[i]-size );      
             }    
	  } 
	}
        
        /*
          Draw error bars if they are selected
        */ 
        if (gd.getErrorLocation() != 0)
        {
          //local_transform.MapYListTo(error_bars_copy);
          Line2D.Float line1 = new Line2D.Float();
          Line2D.Float line2 = new Line2D.Float();
          Line2D.Float line3 = new Line2D.Float();
          int size = 1;
          int loc = gd.getErrorLocation();
        //  int x_val = getZoom_region().x;
        //  int y_val = getZoom_region().y;
        //  int x_width = getZoom_region().width;
        //  int y_height = getZoom_region().height;

	  g2.setStroke(new BasicStroke(1));
	  g2.setColor( gd.errorcolor );


          for ( int i =0 ; i <  n_points; i++ )
          {
             float x_midpt = ((x_copy[i] + x_copy[i+1])/2);
             if ( loc == ERROR_AT_POINT )
             {
               line1.setLine( x_midpt, error_bars_upper[i], 
	  	            x_midpt, error_bars_lower[i]);
               g2.draw(line1);
               line2.setLine( x_midpt + size, error_bars_upper[i],
                                     x_midpt - size, error_bars_upper[i]);
               g2.draw( line2 );
               line3.setLine( x_midpt + size, error_bars_lower[i],
                                     x_midpt - size, error_bars_lower[i]);   
               g2.draw( line3 );
             }

            else if (loc == ERROR_AT_TOP)
             {
               line1.setLine(x_copy[i], error_bars_upper[i] - y_copy[i], 
	  	             x_copy[i], error_bars_lower[i] - y_copy[i]);
               g2.draw(line1);
               line2.setLine(x_copy[i] + size, error_bars_upper[i] - y_copy[i], 
                             x_copy[i] - size, error_bars_upper[i] - y_copy[i]);
               g2.draw( line2 );
               line3.setLine(x_copy[i] + size, error_bars_lower[i] - y_copy[i],
                             x_copy[i] - size, error_bars_lower[i] - y_copy[i]);   
               g2.draw( line3 );
             }
          }   
        }

      }
      else 
       System.out.println("ERROR: x&y arrays don't match in GraphJPanel.paint");
    } 

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


private void set_auto_data_bound()
{
   auto_data_bound = new CoordBounds();
   GraphData gd = (GraphData)graphs.elementAt(0);
   auto_data_bound.setBounds( gd.x_vals, gd.y_vals );
   for ( int i = 1; i < graphs.size(); i++ )
   {
     gd = (GraphData)graphs.elementAt(i);
     auto_data_bound.growBounds( gd.x_vals, gd.y_vals );
   }
}


/* --------------------------- SetDataBounds ----------------------------- */
private void SetDataBounds()
{
    float x1, y1, x2, y2;
                                       // get both the currently set WC bounds
                                       // and the automatically scaled bounds
    CoordBounds current_bound   = getGlobalWorldCoords();
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

    CoordBounds data_bound =  new CoordBounds( x1, y1, x2, y2 );

    data_bound.invertBounds();               // needed for "upside down" pixel
    initializeWorldCoords( data_bound );     // coordinates
}


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

    graph.setBackground(Color.white);
    graph.setData( g1_x_vals, g1_y_vals, 0, false );
    graph.setErrors( g1_e_vals, 11, 1, true );
    graph.setColor( Color.black, 0, false );
    graph.setStroke( graph.strokeType(TRANSPARENT,0), 0, false);
    graph.setLineWidth(1,0,true);
    graph.setMarkColor(Color.green,0,false);
    graph.setMarkType(BOX, 0, false);

    graph.setData( g2_x_vals, g2_y_vals, 1, true );
    graph.setColor( Color.red, 1, true );
    graph.setStroke( graph.strokeType(DOTTED,1), 1, true);

    graph.setData( g3_x_vals, g3_y_vals, 2, true );
    graph.setColor( Color.green, 2, false );
    graph.setStroke( graph.strokeType(LINE,2), 2, true);
    graph.setMarkColor(Color.red,2,true);
    graph.setMarkType(CROSS, 2, true);
   

    graph.setData( g4_x_vals, g4_y_vals, 3, true );
    graph.setColor( Color.blue, 3, false );
    graph.setStroke( graph.strokeType(DASHDOT,3), 3, true);
    graph.setMarkColor(Color.black,2,true);
    graph.setMarkType(STAR, 3, true);
  }
}

