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

public class GraphData implements Serializable 
{
  float  x_vals[]  = { 0, 1 };
  float  y_vals[]  = { 0, .001f };

  public Color  color     = Color.black;
  public int    linetype  = 1;
  public float  linewidth = 1;
  public int    marktype  = 0;
  public Color  markcolor = Color.red;
  public int    marksize  = 2;
  public BasicStroke Stroke = new BasicStroke(1);
  public boolean transparent = false;
  public Color  errorcolor = Color.blue;

  private float[] error_bars = null;
  private int errors = 0;

  // public methods
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
                           error_bars, 0, error_vals.length - 1 );
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

