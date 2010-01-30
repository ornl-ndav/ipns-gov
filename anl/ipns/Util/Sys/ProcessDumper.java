/* 
 * File: ProcessDumper.java
 *
 * Copyright (C) 2009, Dennis Mikkelson
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
 * This work was supported by the Spallation Neutron Source Division
 * of Oak Ridge National Laboratory, Oak Ridge, TN, USA.
 *
 *  Last Modified:
 * 
 *  $Author: $
 *  $Date: $            
 *  $Revision: $
 */

package gov.anl.ipns.Util.Sys;

import java.io.BufferedReader;

/**
 *  This class will dump the stdout and stderr streams from a separate process
 *  (executed via SimpleExec.Exec()) to the console.
 */
public class ProcessDumper extends Thread 
{
  BufferedReader reader = null;
  String         name = null;
  public ProcessDumper( BufferedReader reader, String name )
  {
    this.reader = reader;
    this.name = name;
  }
  
  public void run()
  {
    try
    {
      String line = reader.readLine();
      while ( line != null )
      {
        System.out.println( line );
        line = reader.readLine();
      }
      reader.close();
    }
    catch ( Exception ex)
    {
      System.out.println("EXCEPTION reading from process buffer " + name );
      ex.printStackTrace();
    }
  }  
}
