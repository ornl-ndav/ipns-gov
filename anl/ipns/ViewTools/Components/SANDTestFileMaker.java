/*
 * File:  SANDTestFileMaker.java
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
 * Revision 1.1  2004/01/23 22:53:52  millermi
 * - Initial Version - This class is used to create test files
 *   for the SANDWedgeViewer. In the future it may be generalized
 *   to take in x, y, and value ranges.
 *
 */
 
/**
 * This class was made to easily produce SAND files for testing the 
 * SANDWedgeViewer. If the dimension is not specified on the file,
 * 200 x 200 is assumed to be the dimension.
 */
package DataSetTools.components.View;

import DataSetTools.util.SharedData;
import DataSetTools.util.TextWriter;
import DataSetTools.dataset.UniformXScale;

public class SANDTestFileMaker
{
 /*
  * This constructor cannot be called by the user, it is called by
  * static methods of the class.
  */
  private SANDTestFileMaker(String filename, int rows, int cols )
  {
    float value = 0;
    StringBuffer output = new StringBuffer();
    // random world coord bounds
    float[] wc_x = new UniformXScale( -.4f, .4f, cols ).getXs();
    float[] wc_y = new UniformXScale( -.4f, .4f, rows ).getXs();
    for( int i = 0; i < cols; i++ )
    {
      for( int j = 0; j < rows; j++ )
      {
        if( i%20 == 0 && j%20 == 0 )
	{
	  //value = i - j;
	  value = 100;
	}
	else
	  value = 0;
	// create synthetic file with 4 columns: Qx, Qy, Value, Error
	// Columns are separated by a tab (\t)
	output.append(Float.toString(wc_x[i])).append('\t');  // Qx
	output.append(Float.toString(wc_y[j])).append('\t');  // Qy
	output.append(Float.toString(value)).append('\t');    // Value
	output.append(Float.toString(0.1f)).append('\n');     // Error
      }
    }
    TextWriter.writeASCII( filename, output.toString() );
  }
 
 /**
  * Use this method to make a 200 x 200 file.
  *
  *  @param  filename Includes path
  */ 
  public static void makeTestFile( String filename )
  {
    new SANDTestFileMaker( filename, 200, 200 );
  }
 
 /**
  * Use this method to create a file of a specified size.
  *
  *  @param  filename Includes path
  */
  public static void makeTestFile( String filename, int rows, int columns )
  {
    new SANDTestFileMaker( filename, rows, columns );
  }
 
 /**
  * Use this method to create a file. The String argument passed is assumed
  * to be the filename WITHOUT the path. The file is stored in
  * the Data_Directory as specified by the IsawProps.dat file.
  * If 3 arguments exist: filename, rows, and columns, the size of the file
  * will vary from the assumed 200 x 200.
  * If no argument is passed, the file will have the name: sndtest.dat with
  * dimension 200 x 200.
  *
  *  @param  args The filename, rows, and columns, IF SPECIFIED
  */ 
  public static void main( String args[] )
  {
    String filepath = SharedData.getProperty("Data_Directory");
    String filename = "sndtest.dat";
    int rows = 200;
    int cols = 200;
    if( args.length > 0 )
    {
      filename = args[0];
      if( args.length > 2 )
      {
        try
	{
	  rows = Integer.parseInt(args[1]);
	  cols = Integer.parseInt(args[2]);
	}
	catch( NumberFormatException nfe )
	{
	  rows = -1;
	  cols = -1;
	}
      }
    }
    
    String file = filepath + filename;
    if( rows < 0 || cols < 0 )
      SANDTestFileMaker.makeTestFile( file );
    else
      SANDTestFileMaker.makeTestFile( file, rows, cols );

    System.out.println("***File [" + filename + "] saved in " + 
    		       SharedData.getProperty("Data_Directory") + "***" );
  }
}
