/**
 * File:  FilenameUtil.java
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
 *  Revision 1.2  2001/04/25 22:24:17  dennis
 *  Added copyright and GPL info at the start of the file.
 *
 *  Revision 1.1  2000/08/03 21:41:14  dennis
 *  Now includes FilenameUtil.fixCase() to check for case errors in basic
 *  filename
 *
 */ 
package DataSetTools.util;

import java.awt.*;
import java.io.*;
import DataSetTools.instruments.*;

/**
 *  Provide utilitiy for fixing file names
 */

public class FilenameUtil
{
  /**
   *  Don't instantiate this class, just use the Fonts provided.
   */
  private FilenameUtil() {}


  /**
   *  Replace all occurrences of the possible file separators "/" "\" "\\"
   *  with the file separator needed for the local system.
   *
   *  @param  file_name  A file name string possibly containing improper
   *                     separators.
   *
   *  @return  A string containing the file name with all separators replaced 
   *           by the system dependent separtator needed on the local system. 
   */
  public static String fixSeparator( String file_name )
  {
    String separator = File.separator;

    String result = StringUtil.replace( file_name, "\\\\", separator );
    result = StringUtil.replace( result, "\\", separator );
    result = StringUtil.replace( result, "/", separator );

    return result;
  }


  /**
   *  Adjust the case of a file name if the specified file does not exist
   *
   *  @param  file_name  A file name string possibly with the wrong case. 
   *
   *  @return  A string containing the file name with the actual file name
   *           ( excluding the path ) changed to a different case, if the
   *           original file did not exist, but the file with the changed
   *           case does exist.  If the file can't be found this returns null.
   */
  public static String fixCase( String file_name )
  {
    boolean opened_ok = true;

    File file = new File ( file_name );
    if ( file.exists() )
      return file_name;

    String name = InstrumentType.getFileName( file_name );
    String path = InstrumentType.getPath( file_name );
    char   first_char = name.charAt(0);
    if ( Character.isUpperCase( first_char ) )
      file_name = path + name.toLowerCase();
    else
      file_name = path + name.toUpperCase();

    file = new File ( file_name );
    if ( file.exists() )
      return file_name;

    return null;
  }


}
