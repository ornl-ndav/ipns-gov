/*
 * File:  Display3DFramesExample.java
 *
 * Copyright (C) 2005, Chad Jones
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
 * Primary   Chad Jones <cjones@cs.utk.edu>
 * Contact:  Student Developer, University of Tennessee
 *
 * Contact : Dennis Mikkelson <mikkelsond@uwstout.edu>
 *           Department of Mathematics, Statistics and Computer Science
 *           University of Wisconsin-Stout
 *           Menomonie, WI 54751, USA
 *
 * This work was supported by the National Science Foundation under grant
 * number DMR-0218882.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 *  Modified:
 *
 *  $Log$
 *  Revision 1.1  2005/07/22 19:48:40  cjones
 *  Updated Display3DExample with better test data. Also, added a
 *  Display3DframesExample to show how to use Display3D when data has
 *  frames.
 *
 * 
 */
 
// this line is optional, but should reflect the path where the file is found.
package gov.anl.ipns.ViewTools.Examples;

import gov.anl.ipns.MathTools.Geometry.Vector3D;

import gov.anl.ipns.ViewTools.Displays.Display3D;
import gov.anl.ipns.ViewTools.Components.IPhysicalArray3DList;
import gov.anl.ipns.ViewTools.Components.PhysicalArray3DList;
import gov.anl.ipns.Util.Sys.WindowShower;

/**
 * This class is a simple example of how to use the Display3D class 
 * with frames.
 */
public class Display3DFramesExample
{
 /*
  * Main program, this is where the work is done.
  */
  public static void main( String args[] )
  {
  	int detectorsize = 100;
  	int numdetectors = 2;
  	
  	IPhysicalArray3DList[] data = new IPhysicalArray3DList[numdetectors];

    // Create 2 square panels
    // PANEL 1
  	float[] time_vals = {1.5f, 2.6f, 3.6f, 5.2f };
  	DataSetTools.dataset.XScale time = new DataSetTools.dataset.VariableXScale(time_vals);
  	
    data[0] = new PhysicalArray3DList(detectorsize, time);
    for(int i = 0; i < 10; i++)
      for(int j = 0; j < 10; j++)
      {
      	float[] vals = { j*30+i*10-150, j*30+i*10-2*150, j*30+i*10-3*150, j*30+i*10-4*150 }; 
      	data[0].set(i*10+j, // Index
      			  new Vector3D(i*50, 400, j*50), // 3D Coordinates
                  new Vector3D(11, 13, 12),  // Box volume
                  new Vector3D(1, 0, 0), // Orientation (X-Axis)
				  new Vector3D(0, 1, 0), // Orientation (Y-Axis)
				  vals); // Value
      }
  	data[0].setArrayID(0);  // Panel ID
    
    // PANEL 2
    data[1] = new PhysicalArray3DList(detectorsize, time);
    for(int i = 0; i < 10; i++)
      for(int j = 0; j < 10; j++)
      {
      	float[] vals = { j*30+i*10+750, j*30+i*10+2*750, j*30+i*10+3*750, j*30+i*10+4*750 }; 
      	
        data[1].set(i*10+j, // Index
			      new Vector3D(i*50, -400, j*50), // 3D Coordinates
                  new Vector3D(9, 10, 9), // Box Volume
                  new Vector3D(4, 0, -2), // Orientation (X-Axis)
				  new Vector3D(1, 1, 2),  // Orientation (Y-Axis)
				  vals); // Value
      }
    data[1].setArrayID(1); // Panel ID
    
    // Create display with Controls
    Display3D display = new Display3D(data, 0, Display3D.CTRL_ALL);
    
    // This will show the graphical display.
    WindowShower.show(display);
  }
}
