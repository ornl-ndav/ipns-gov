/*
 * File:  GraphJpanel.java
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
 *           Menomonie, WI. 54751
 *           USA
 *
 * This work was supported by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 * Modified:
 *
 * $Log$
 * Revision 1.7  2001/05/29 15:13:08  dennis
 * Now uses initializeWorldCoords to reset both the local and
 * global transforms.
 *
 * Revision 1.6  2001/05/07 21:20:01  dennis
 * Fixed error in format of documentation.
 *
 * Revision 1.5  2001/05/07 21:04:21  dennis
 * Removed the LocalTransformChanged() method, since the
 * default implementation of this method in CoordJPanel
 * works.
 *
 * Revision 1.4  2001/04/23 21:15:10  dennis
 * Added copyright and GPL info at the start of the file.
 *
 * Revision 1.3  2001/01/29 21:39:17  dennis
 * Now uses CVS version numbers.
 *
 * Revision 1.2  2000/07/10 22:11:49  dennis
 * 7/10/2000 version, many changes and improvements
 *
 * Revision 1.15  2000/05/31 19:00:35  dennis
 * Now uses arraycopy to copy all but the first and last points of the
 * filled area to a new array for drawing the graph when doing hidden line
 * removal.
 *
 * Revision 1.14  2000/05/31 13:55:27  dennis
 * Added hidden line removal
 *
 * Revision 1.13  2000/05/24 22:36:28  dennis
 * Added method to set offsets for plotting multiple graphs.
 *
 *  Revision 1.12  2000/05/18 21:00:15  dennis
 *  Fixed bug that prevented specifying the color of a graph ( >1 ) before
 *  specifying the data.
 *  Also, now draw the graphs in reverse order so that graph #0 is drawn on
 *  top of the other graphs.
 * 
 *  Revision 1.11  2000/05/11 16:53:19  dennis
 *  Added RCS logging
 *
 *  1.1  2000/04/24  Dennis Mikkelson
 *                   Added ability to maintain multiple separate graphs with
 *                   different colors for each graph.
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

/**
 *  A GraphJPanel object maintains and draws a list of graphs.
 */ 

public class GraphJPanel extends    CoordJPanel 
                         implements Serializable
{
  Vector  graphs;

  private boolean         y_bound_set = false;
  private boolean         x_bound_set = false;
  private int             x_offset_factor = 0;  
  private int             y_offset_factor = 0;  
  private boolean         remove_hidden_lines = false;

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
    for ( int i = graphs.size()-1; i >= 0; i-- )
      graphs.removeElementAt( i );

    graphs.addElement( new GraphData() );    
    SetDataBounds();
  }

/* ----------------------------- setColor -------------------------------- */
/**
 *  Set the color for the specified graph.  
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
    if ( graph_num < 0 || graph_num > graphs.size() )    // no such graph
      return false;

    if ( graph_num == graphs.size() )                  // add a new graph
      graphs.addElement( new GraphData() );
 
    GraphData gd = (GraphData)graphs.elementAt( graph_num );
    gd.color = color; 

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


/* ------------------------------ setY_bounds ------------------------------- */
/**
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
    SetTransformsToWindowSize();
    int x_offset = 0;
    int y_offset = 0;
    int height = getHeight();
    for ( int gr_index = graphs.size()-1; gr_index >= 0; gr_index-- )
    {
      x_offset = x_offset_factor * gr_index; 
      y_offset = y_offset_factor * gr_index; 
      GraphData gd = (GraphData)graphs.elementAt(gr_index);
      int n_points = Math.min( gd.x_vals.length, gd.y_vals.length );

      float x_copy[] = (float[])gd.x_vals.clone();
      float y_copy[] = (float[])gd.y_vals.clone();

      local_transform.MapTo( x_copy, y_copy );         // map from WC to pixels

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
 
          g.setColor( getBackground() );                     // solid fill to
          g.fillPolygon( x_int, y_int, n_points + 2 );       // hide lines

          System.arraycopy( x_int, 1, x_int, 0, n_points );  // now draw the
          System.arraycopy( y_int, 1, y_int, 0, n_points );  // data points
          g.setColor( gd.color );                            // themselves
          g.drawPolyline( x_int, y_int, n_points );
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
          g.setColor( gd.color );
          g.drawPolyline( x_int, y_int, n_points );
        }
      }
      else if ( x_copy.length == y_copy.length + 1 )  // Histogram data
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

          g.setColor( getBackground() );                       // solid fill to
          g.fillPolygon( x_int, y_int, 2*y_copy.length + 2 );  // hide lines

          System.arraycopy( x_int, 1, x_int, 0, 2*y_copy.length ); //now draw 
          System.arraycopy( y_int, 1, y_int, 0, 2*y_copy.length ); //data points
          g.setColor( gd.color );                                  //themselves 
          g.drawPolyline( x_int, y_int, 2*y_copy.length );
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
          g.setColor( gd.color );
          g.drawPolyline( x_int, y_int, 2*y_copy.length );
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


/* --------------------------- SetDataBounds ----------------------------- */
private void SetDataBounds()
{
    float x1, y1, x2, y2;
                                       // get both the currently set WC bounds
                                       // and the automatically scaled bounds
    CoordBounds current_bound   = getGlobalWorldCoords();
    current_bound.invertBounds();

    CoordBounds auto_data_bound = new CoordBounds();
    GraphData gd = (GraphData)graphs.elementAt(0);
    auto_data_bound.setBounds( gd.x_vals, gd.y_vals );
    for ( int i = 1; i < graphs.size(); i++ )
    {
      gd = (GraphData)graphs.elementAt(i);
      auto_data_bound.growBounds( gd.x_vals, gd.y_vals );
    }

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

    float g1_x_vals[] = { 0, 1 };
    float g1_y_vals[] = { 0, 1 };

    float g2_x_vals[] = { 0, 1 };
    float g2_y_vals[] = { 1, 0 };

    graph.setData( g1_x_vals, g1_y_vals, 0, false );
    graph.setColor( Color.black, 0, false );

    graph.setData( g2_x_vals, g2_y_vals, 1, true );
    graph.setColor( Color.red, 1, true );
  }
}
