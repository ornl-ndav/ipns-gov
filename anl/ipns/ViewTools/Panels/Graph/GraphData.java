/*
 * File: GraphData.java
 *
 * Copyright (C) 2000, Dennis Mikkelson
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
 * Revision 1.20  2005/03/11 19:49:41  serumb
 * Added get and set Object State methods and changed Basic Stroke variable
 * to use an integer key.
 *
 * Revision 1.19  2004/08/17 03:53:01  ffr
 * Bugfix: Make sure we copy the last error bar in setErrorVals
 *
 * Revision 1.18  2004/03/15 23:53:54  dennis
 * Removed unused imports, after factoring out the View components,
 * Math and other utils.
 *
 * Revision 1.17  2004/03/12 01:32:03  rmikk
 * Fixed package names
 *
 * Revision 1.16  2003/11/06 01:20:27  serumb
 * Changed the initial size for the point markers to be bigger.
 *
 * Revision 1.15  2003/10/21 00:49:23  serumb
 * Fixed javadoc error.
 *
 * Revision 1.14  2003/08/28 23:04:57  dennis
 * Default graph now is line joining (0,0) with (1,0.001)
 *
 * Revision 1.13  2003/07/17 21:50:40  serumb
 * Added if block to check if the Error Values are null.
 *
 * Revision 1.12  2003/07/03 16:08:14  serumb
 * Added methods get_x_vals and get_y_vals, and added java docs commemts.
 *
 * Revision 1.11  2003/07/02 22:31:49  serumb
 * Fixed ImageView display problem.
 *
 * Revision 1.10  2003/07/02 17:09:58  serumb
 * Added a boolean variable to keep track of a lines transparency.
 *
 * Revision 1.9  2003/06/20 16:19:04  serumb
 * Added methods and variables to set and get error values.
 *
 * Revision 1.8  2003/06/16 23:20:32  dennis
 * Initializes the stroke field with a new BasicStroke(1).
 *
 * Revision 1.7  2003/06/13 19:47:29  serumb
 * Added a variable for point marker size and initialized the mark color to red.
 *
 * Revision 1.6  2003/06/06 14:37:15  serumb
 * added a variable of type BasicStroke for different line styles
 * added a variable, markcolor, to determine point marker color
 *
 * Revision 1.5  2002/11/27 23:13:18  pfpeterson
 * standardized header
 *
 */

package gov.anl.ipns.ViewTools.Panels.Graph;

import java.io.*;
import java.awt.*;
import gov.anl.ipns.ViewTools.Components.*;

public class GraphData implements Serializable,
                                  IPreserveState 
{

 /**
  * "Line Color" - This constant String is a key for referencing the State
  * information about the graph color for the graphs when they are displayed
  * in the GraphJPanel.
  */
 public static final String LINE_COLOR = "Line Color";

 /**
  * "Line Width" - This constant String is a key for referencing the State
  * information about line width for the graphs that are displayed in the 
  * GraphJPanel.
  */
 public static final String LINE_WIDTH = "Line Width";

 /**
   * "Line Type" - This Constant String is a key for referencing the State
   * information about the line type for the graphs displayed 
   * in the GraphJPanel.
   */
 public static final String LINE_TYPE = "Line Type";

 /**
  * "Mark Type" - This Constant String is a key for referencing the State
  * information about the point markers for the graphs displayed in the 
  * GraphJPanel.
  */
 public static final String MARK_TYPE = "Mark Type";
 
 /**
  * "Mark Color" - This Constant String is a key for referencing the State
  * information about the color of the markers displayed
  * in the GraphJPanel.
  */
 public static final String MARK_COLOR = "Mark Color";

 /** 
  * "Mark Size" - This Constant String is a key for referencing the State
  * information about the size of the markers displayed in the 
  * GraphJPanel.
  */
 public static final String MARK_SIZE = "Mark Size";

 /**
  * "Stroke" - This Constant String is a key for referencing the State
  * information about the stroke used to display the graphs in the 
  * GraphJPanel.
  */
// public static final String STROKE = "Stroke";

 /**
  * "Transparent" - This Constant String is a key for referencing the State
  * information to determine if the line should be drawn in the GraphJPanel.
  */
 public static final String TRANSPARENT = "Transparent";

 /**
  * "Error Color" - This Constant String is a key for referencing the State
  * information about the color of the error bars displayed in the 
  * GraphJPanel.
  */
 public static final String ERROR_COLOR = "Error Color";

 /**
  * "Error Location" - This Constant String is a key for referencing the State
  * information about where the error bars are to be displayed in the 
  * GraphJPanel.
  */
 public static final String ERROR_LOCATION = "Error Location";
 
 
  transient float   x_vals[]  = { 0, 1 };
  transient float   y_vals[]  = { 0, .001f };

  public Color  color     = Color.black;
  public int    linetype  = 8;
  public float  linewidth = 1;
  public int    marktype  = 0;
  public Color  markcolor = Color.red;
  public int    marksize  = 2;
//  public BasicStroke Stroke = new BasicStroke(1);
  public boolean transparent = false;
  public Color  errorcolor = Color.blue;

  private transient float[] error_bars = null;
  private transient int errors = 0;

  // public methods

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

    Object temp = new_state.get(LINE_COLOR);
    if ( temp != null)
    {
      color = (Color)temp;
      redraw = true;
    }

    temp = new_state.get(LINE_WIDTH);
    if ( temp != null)
    {
      linewidth = ((Float)temp).floatValue();
      redraw = true;
    }

    temp = new_state.get(LINE_TYPE);
    if ( temp != null)
    {
      linetype = ((Integer)temp).intValue();	    
      redraw = true;
    }

    temp = new_state.get(MARK_TYPE);
    if ( temp != null)
    {
      marktype = ((Integer)temp).intValue();
      redraw = true;
    }

    temp = new_state.get(MARK_COLOR);
    if ( temp != null)
    {
      markcolor = (Color)temp;
    }

/*    temp = new_state.get(STROKE);
    if ( temp != null)
    {
      Stroke = (BasicStroke)temp;
      redraw = true;
    }*/

    temp = new_state.get(TRANSPARENT);
    if ( temp != null)
    {
      transparent = ((Boolean)temp).booleanValue();
      redraw = true;
    }
    
    temp = new_state.get(ERROR_COLOR);
    if ( temp != null)
    {
      errorcolor = (Color)temp;
      redraw = true;
    }

    temp = new_state.get(ERROR_LOCATION);
    if ( temp != null)
    {
      errors = ((Integer)temp).intValue();
      redraw = true;
    }    
   
  }

 /**
   * This method will get the current values of the state variables for this
   * object. These variables will be wrapped in an ObjectState. Keys will be
   * put in alphabetic order.
   */
  public ObjectState getObjectState(boolean isDefault)
  {
   ObjectState state = new ObjectState();
   state.insert(ERROR_COLOR, (Color)errorcolor);
   state.insert(ERROR_LOCATION, new Integer(errors));
   state.insert(LINE_COLOR, (Color)color);
   state.insert(LINE_TYPE, new Integer(linetype));
   state.insert(LINE_WIDTH, new Float(linewidth));
   state.insert(MARK_COLOR, (Color)markcolor);
   state.insert(MARK_SIZE, new Integer(marksize));
   state.insert(MARK_TYPE, new Integer(marktype));
//   state.insert(STROKE, Stroke);
   state.insert(TRANSPARENT, new Boolean(transparent));

   return state;
  }   
/*--------------------------- setErrorVals ----------------------------------*/
/**
  *  Sets the location and the error values from the values passed in.
  *
  *  @param error_loc    the integer constant for the location of the 
  *                     errors. 
  *
  *  @param error_vals  the array of error values.
  *
  **/             
  public void setErrorVals(int error_loc, float[] error_vals)
  {
    errors = error_loc;
    if(error_vals != null) 
    {
       error_bars = new float[error_vals.length];
       System.arraycopy( error_vals, 0 , 
                           error_bars, 0, error_vals.length );
    }
    else error_bars = null;
  }

/*---------------------------- getErrorLocation -----------------------------*/
/**
  * @return  returns the integer constant for the location of the error bars.
  *
  **/  
  public int getErrorLocation()
  {
    return errors;
  }

/*---------------------------- getErrorVals ---------------------------------*/
/**
  * @return  returns the array of error values.
  *
  **/  
  public float[] getErrorVals()
  {
   return error_bars;
  }
  
/*---------------------------- get_x_vals -----------------------------------*/
/**
  * @return  returns the array of x values for the graph.
  *
  **/  
  public float[] get_x_vals()
  {
   return x_vals;
  }

/*---------------------------- get_y_vals -----------------------------------*/
/**
  * @return  returns the y values for the graph.
  *
  **/  
  public float[] get_y_vals()
  {
   return y_vals;
  }

}

