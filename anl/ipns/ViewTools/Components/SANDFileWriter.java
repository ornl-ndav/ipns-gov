/*
 * File:  SANDFileWriter.java
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
 * Revision 1.1  2004/01/24 02:27:01  millermi
 * - Initial Version - This class writes the results from
 *   the SANDWedgeViewer to a file. The file has three
 *   columns: Q, Intensity, and Error bounds.
 * - Additional information is attached to the top of
 *   the file via the header parameter.
 *
 */ 
package DataSetTools.components.View;

import DataSetTools.dataset.Data;
import DataSetTools.util.SharedData;
import DataSetTools.util.TextWriter;

/**
 * This class will write out the results of the SANDWedgeViewer to a file.
 * The file has three columns: Q, Intensity, and Error, IN THAT ORDER.
 */
public class SANDFileWriter
{
 /*
  * This constructor cannot be called by the user, it is called by
  * static methods of the class.
  */
  private SANDFileWriter( String filename, String header, float[] q,
                          float[] intensity, float[] error )
  {
    StringBuffer output = new StringBuffer();
    // if the header is valid, add it to the output with one line of space.
    if( header != null && !header.equals("") )
      output.append( header ).append('\n');
    
    // make sure arrays are of same size, if not, use length of smallest array.
    int length = q.length;
    if( length > intensity.length )
      length = intensity.length;
    if( length > error.length )
      length = error.length;
    // create file with 3 columns: Q, Intensity, Error
    // Columns are separated by a tab (\t)
    for( int i = 0; i < length; i++ )
    {
      output.append(Float.toString(q[i])).append('\t');          // Q
      output.append(Float.toString(intensity[i])).append('\t');  // Intensity
      output.append(Float.toString(error[i])).append('\n');      // Error
    }
    TextWriter.writeASCII( filename, output.toString() );
  }
 
 /**
  * Use this method to make a 200 x 200 file.
  *
  *  @param  filename Includes path
  *  @param  header   Additional information placed at the top of the file.
  *  @param  ds       DataSet that contains the Q, Intensity, and Error values.
  */ 
  public static void makeFile( String filename, String header, Data data )
  {
    new SANDFileWriter( filename,
                        header,
                        data.getX_scale().getXs(),
                        data.getY_values(),
                        data.getErrors() );
  }
 
 /**
  * Use this method to create a file of a specified size.
  *
  *  @param  filename  Includes path
  *  @param  header    Additional information placed at the top of the file.
  *  @param  q         Array of Q values
  *  @param  intensity Array of intensity values
  *  @param  error     Array of error values
  */
  public static void makeFile( String filename, String header, float[] q,
                               float[] intensity, float[] error )
  {
    new SANDFileWriter( filename, header, q, intensity, error );
  }
 
 /**
  * For use in testing purposes only.
  *
  *  @param  args The filename, rows, and columns, IF SPECIFIED
  */ 
  public static void main( String args[] )
  {
    String filepath = SharedData.getProperty("Data_Directory"); 
    if( !filepath.endsWith("/") )
      filepath = filepath + "/";
    String filename = "wegtest.dat";
    if( args.length > 0 )
    {
      filename = args[0];
    }
    String head = "This is a test output file for the SANDWedgeViewer";
    float[] q = {.1f,.2f,.3f,.4f,.5f,.9f,.8f,.7f,.6f};
    float[] intensity = {.01f,.02f,.03f,.04f,.05f,.09f,.08f,.07f,.06f};
    float[] error = {.001f,.002f,.003f,.004f,.005f,.009f,.008f,.007f,.006f};
    
    String file = filepath + filename;
    SANDFileWriter.makeFile( file, head, q, intensity, error );

    System.out.println("***File [" + filename + "] saved in " + 
    		       SharedData.getProperty("Data_Directory") + "***" );
  }
}
