/*
 * File:  Display2DExample.java
 *
 * Copyright (C) 2004, Mike Miller
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
 * Revision 1.2  2004/09/15 21:58:30  millermi
 * - Updated LINEAR, TRU_LOG, and PSEUDO_LOG setting for AxisInfo class.
 *   Adding a second log required the boolean parameter to be changed
 *   to an int. These changes may affect any ObjectState saved configurations
 *   made prior to this version.
 *
 * Revision 1.1  2004/03/20 01:39:10  millermi
 * - Initial version - Designed to show introductory users how
 *   to use the Display2D class using java.
 *
 */
// this line is optional, but should reflect the path where the file is found.
package gov.anl.ipns.ViewTools.Examples;
// these five imports must be included in your file.
import gov.anl.ipns.ViewTools.Displays.Display2D;
import gov.anl.ipns.ViewTools.Components.IVirtualArray2D;
import gov.anl.ipns.ViewTools.Components.VirtualArray2D;
import gov.anl.ipns.ViewTools.Components.AxisInfo;
import gov.anl.ipns.Util.Sys.WindowShower;

/**
 * This class is a simple example of how to use the Display2D class.
 */
public class Display2DExample
{
 /*
  * Main program, this is where the work is done.
  */
  public static void main( String args[] )
  {
    // Build a test 2-D array of data.
    // Replace the test_array[][] with useful data.
    int row = 200;
    int col = 200;
    float test_array[][] = new float[row][col];
    for ( int i = 0; i < row; i++ )
      for ( int j = 0; j < col; j++ )
        test_array[i][j] = i - j;
    
    // Put 2-D data into a VirtualArray2D wrapper
    IVirtualArray2D va2D = new VirtualArray2D( test_array );
    // Give meaningful range, labels, units, and linear or log display method.
    // This assigns the above values to the x axis.
    va2D.setAxisInfo( AxisInfo.X_AXIS, 0f, 10000f, 
    		      "X Axis Label","X Axis Units", AxisInfo.LINEAR );
    // This assigns the above values to the y axis.
    va2D.setAxisInfo( AxisInfo.Y_AXIS, 0f, 1500f, 
    		      "Y Axis Label","Y Axis Units", AxisInfo.LINEAR );
    // This assigns the above values to the values to be visualized.
    va2D.setAxisInfo( AxisInfo.Z_AXIS, 0f, 1f, "Value Axis Label",
                      "Value Axis Units", AxisInfo.PSEUDO_LOG );
    va2D.setTitle("Display2D Example");
    // Make instance of a Display2D frame, giving the array, the initial
    // view type, and whether or not to add controls.
    // To view all of the controls, use CTRL_ALL, to display no controls,
    // use CTRL_NONE.
    Display2D display = new Display2D(va2D,Display2D.IMAGE,Display2D.CTRL_ALL);
    
    // This will show the graphical display.
    WindowShower shower = new WindowShower(display);
    java.awt.EventQueue.invokeLater(shower);
    shower = null;
  }
}
