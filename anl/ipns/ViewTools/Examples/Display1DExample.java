/*
 * File:  Display1DExample.java
 *
 * Copyright (C) 2004, Brent Serum, Mike Miller
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
 * Primary   Mike Miller <millermi@uwstout.edu>
 * Contact:  Student Developer, University of Wisconsin-Stout
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
 * Revision 1.1  2004/04/21 02:37:43  millermi
 * - Initial Version - Example of how the Display1D class to display
 *   functions.
 *
 * Revision 1.1  2004/03/20 01:39:10  millermi
 * - Initial version - Designed to show introductory users how
 *   to use the Display2D class using java.
 *
 */
// this line is optional, but should reflect the path where the file is found.
package gov.anl.ipns.ViewTools.Examples;
// these seven imports must be included in your file.
import java.util.Vector;
import gov.anl.ipns.ViewTools.Displays.Display1D;
import gov.anl.ipns.ViewTools.Components.IVirtualArrayList1D;
import gov.anl.ipns.ViewTools.Components.OneD.VirtualArrayList1D;
import gov.anl.ipns.ViewTools.Components.OneD.DataArray1D;
import gov.anl.ipns.ViewTools.Components.AxisInfo;
import gov.anl.ipns.Util.Sys.WindowShower;

/**
 * This class is a simple example of how to use the Display2D class.
 */
public class Display1DExample
{
 /*
  * Main program, this is where the work is done.
  */
  public static void main( String args[] )
  {
    // build my 2 functions (graphs) consisting of x, y, and error values.
    int num_x_vals = 360;
    float sin_x[] = new float[num_x_vals];
    float sin_y[] = new float[num_x_vals];
    float sin_err[] = new float[num_x_vals];
    float cos_x[] = new float[num_x_vals];
    float cos_y[] = new float[num_x_vals];
    float cos_err[] = new float[num_x_vals];
    for ( int i = 0; i < num_x_vals; i++ )
    {
      // contruct sine function
      sin_x[i] = ((float)i)*(float)Math.PI/180f;
      sin_y[i] = (float)Math.sin((double)sin_x[i]);
      sin_err[i] = ((float)i)*.001f;
      // construct cosine function
      cos_x[i] = ((float)i)*(float)Math.PI/180f;
      cos_y[i] = (float)Math.cos((double)cos_x[i]);
      cos_err[i] = sin_err[i];
    }
    // Each DataArray1D object represents a graph. The parameters are
    // x values, y values, and error values. It is assumed the graph is
    // "selected" or displayed, but not "pointed-at".
    DataArray1D sine_graph = new DataArray1D( sin_x, sin_y, sin_err );
    sine_graph.setTitle("Sine Function");
    DataArray1D cosine_graph = new DataArray1D( cos_x, cos_y, cos_err );
    cosine_graph.setTitle("Cosine Function");
    // put all of the functions into a list structure (Vector)
    Vector trig = new Vector();
    trig.add(sine_graph);
    trig.add(cosine_graph);
    // Put list of functions into a VirtualArrayList1D wrapper. The
    // VirtualArray concept allows you to add information to the x and y axis.
    // The AxisInfo object consists of an axis specification, min, max, label,
    // units, and linear (true) or log (false) display.
    IVirtualArrayList1D va1D = new VirtualArrayList1D( trig );
    // Give meaningful range, labels, units, and linear or log display method.
    AxisInfo info = va1D.getAxisInfo( AxisInfo.X_AXIS );
    va1D.setAxisInfo( AxisInfo.X_AXIS, info.getMin(), info.getMax(), 
    		        "Angle","Radians", true );
    info = va1D.getAxisInfo( AxisInfo.Y_AXIS );
    va1D.setAxisInfo( AxisInfo.Y_AXIS, info.getMin(), info.getMax(), 
    			"Length","Unit Length", true );
    va1D.setTitle("Sine and Cosine Function");
    // Make instance of a Display1D frame, giving the array, the initial
    // view type, and whether or not to add controls.
    Display1D display = new Display1D(va1D,Display1D.GRAPH,Display1D.CTRL_ALL);
    
    // Class that "correctly" draws the display.
    WindowShower shower = new WindowShower(display);
    java.awt.EventQueue.invokeLater(shower);
    shower = null;
  }
}
