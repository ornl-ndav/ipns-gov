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
 *  Revision 1.4  2001/12/18 23:06:43  pfpeterson
 *  Fixed a windows bug. A local file should be listed as 'file:///' not 'file://'. It works under linux as well.
 *
 *  Revision 1.3  2001/12/11 17:54:13  pfpeterson
 *  Added the method 'String helpDir(String helpFile)' which returns the full name of the helpFile to be used. The searchpath is (in order) Help_Directory, $HOME/IsawHelp, $CLASSPATH/IsawHelp, or online.
 *
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

    /**
     * Produces the appropriate directory for a given help
     *
     * @param helpFile The name of the helpfile to be used
     *
     * @return A string containing a link to either the local helpfile
     *         or one on the web.
     */
    public static String helpDir( String helpFile ){
	// define the location of the URL version
	final String URLDIR="http://www.pns.anl.gov/isaw/IsawHelp/";


	// start the string as being the value of helpdirectory
	String S = System.getProperty("Help_Directory").trim();

	//System.out.println("1: Source is "+S); 

	// fix the string up and check that helpFile exists there
	if(S != null ){
	    S = DataSetTools.util.StringUtil.fixSeparator(S);
	    if( S.length()<1 ){ S=null; 
	    }else if("\\/".indexOf(S.charAt(S.length()-1))<0){
		S=S+java.io.File.separator; }

	    if( new File(S+helpFile).exists()){
	    }else{ S=null; }
	}

	//System.out.println("2: Source is "+S); 

	// if helpFile is not in help_file then try in $HOME
	if( S == null ){
	    S=System.getProperty("user.dir").trim();
	    if( S.length()>0 ){
		if( "\\/".indexOf(S.charAt(S.length()-1)) < 0 ){
		    S=S+java.io.File.separator;
		}
	    }
	    S=DataSetTools.util.StringUtil.fixSeparator(S);
	    if(!new File(S+"IsawHelp"+java.io.File.separator+helpFile).exists()){
		S=null;
	    }else{ 
		S=S+"IsawHelp"+java.io.File.separator;
	    }
	}

	//System.out.println("3: Source is "+S); 

	
	// if helpFile still hasn't been found look throughout the classpath
	if( S != null ){
	    S = S + helpFile;
        }else{
	    String CP = System.getProperty("java.class.path").replace( '\\','/');
	    int s, t ;
            for( s = 0; (s < CP.length()) && (S == null); s++){
		t = CP.indexOf( java.io.File.pathSeparator, s+1);
		if( t < 0){ t = CP.length(); }
		S = CP.substring(s,t) .trim();
		if( S.length() > 0 ){
		    if ( S.charAt( S.length() -1) != '/'){ 
			S = S + "/";
		    }
		}
		if(new File(S+"IsawHelp"+java.io.File.separator+helpFile).exists()){
		    S= S + "IsawHelp"+java.io.File.separator+helpFile;
		}else{
		    S = null;
		}
	    }
	}
	
	//System.out.println("4: Source is "+S); 

	// either it has been found or just give the URL
	if( S == null ){
	    S = URLDIR+helpFile;
	}else{ 
	    S = "file:///" + S; 
	}


	//System.out.println("5: Source is "+S); 
    
	return S;
    }

}
