/*
 * File: IXmlIO.java
 *
 * Copyright (C) 2002, Dennis Mikkelson, Ruth Mikkelson
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
 *  $Log$
 *  Revision 1.1  2002/06/05 13:48:25  dennis
 *  Interface for IO of objects to XML files.
 *
 *
 */

package  DataSetTools.dataset;
import   java.io.*;

/**
 *  Interface for objects that can read and write themselves in XML format
 *  to byte streams. 
 */
public interface IXmlIO 
{
  public static final int BASE64 = 0;     // flags for write mode, use base 64 
  public static final int NORMAL = 1;     // or expand values in ASCII format

  /**
   *  Write the state of this object out to the specified stream in XML format. 
   *
   *  @param  stream   The stream to write to.
   *  @param  mode     Flag indicating whether or not to write the value in
   *                   base 64 encoding.
   *
   *  @return true if the write was successful, false otherwise.
   */
  public boolean XMLwrite( OutputStream stream, int mode ); 


  /**
   *  Read the state of this object from the specified stream in XML format.
   *
   *  @param  stream   The stream to read from.
   *
   *  @return true if the read was successful, false otherwise.
   */
  public boolean XMLread( InputStream stream );
}
