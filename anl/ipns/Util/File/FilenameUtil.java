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
 *           Menomonie, WI 54751, USA
 *
 * This work was supported by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 * Modified:
 *
 *  $Log$
 *  Revision 1.18  2003/11/20 19:36:51  rmikk
 *  Added a method, URLSpacetoSpace, to convert %20
 *  to spaces.
 *
 *  Revision 1.17  2003/05/28 20:51:40  pfpeterson
 *  Changed System.getProperty to SharedData.getProperty
 *
 *  Revision 1.16  2003/03/11 16:14:14  pfpeterson
 *  fixCase(String) now tries both all upper and all lower after not
 *  finding the specified file. Also shortened names for imported
 *  packages.
 *
 *  Revision 1.15  2003/03/05 20:52:12  pfpeterson
 *  Changed SharedData.status_pane.add(String) to SharedData.addmsg(String)
 *
 *  Revision 1.14  2003/02/13 20:57:49  pfpeterson
 *  Deprecated fixSeparator and renamed the method to setForwardSlash.
 *
 *  Revision 1.13  2003/01/27 14:57:17  rmikk
 *  Change methods docdir and helpdir to use system dependent
 *      slashes so they work in the browser control
 *
 *  Revision 1.12  2002/12/11 16:59:06  pfpeterson
 *  Updated javadocs of fixSeparator(String)
 *
 *  Revision 1.11  2002/11/27 23:23:49  pfpeterson
 *  standardized header
 *
 *  Revision 1.10  2002/08/15 18:45:07  pfpeterson
 *  Now replaces "//" in fixSeparator as well.
 *
 *  Revision 1.9  2002/08/12 18:51:02  pfpeterson
 *  Updated the documentation to reflect what fixSeparator actually does.
 *
 *  Revision 1.8  2002/05/29 22:17:30  pfpeterson
 *  fixSeparator will now change all '' to '/'.
 *
 *  Revision 1.7  2002/03/06 19:27:19  pfpeterson
 *  Print message in status pane if local version of help is not found.
 *
 *  Revision 1.6  2002/03/04 20:31:34  pfpeterson
 *  Updated help finder to be more rhobust.
 *
 *  Revision 1.5  2002/02/14 22:41:11  pfpeterson
 *  Added method to locate files in the docs subdirectory of ISAW: docDir().
 *
 *  Revision 1.4  2001/12/18 23:06:43  pfpeterson
 *  Fixed a windows bug. A local file should be listed as 'file:///' not 'file://'. It works under linux as well.
 *
 *  Revision 1.3  2001/12/11 17:54:13  pfpeterson
 *  Added the method 'String helpDir(String helpFile)' which returns the full name of the helpFile to be used. The searchpath is (in order) Help_Directory, $HOME/IsawHelp, $CLASSPATH/IsawHelp, or online.
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
   *  with "/".
   *
   *  @param  file_name  A file name string possibly containing improper
   *                     separators.
   *
   *  @return  A string containing the file name with all separators replaced 
   *           by "/".
   *
   *  @deprecated  replaced by {@link #setForwardSlash(String)}
   */
  public static String fixSeparator( String file_name )
  {
    return setForwardSlash(file_name);
  }

  /**
   *  Replace all occurrences of the possible file separators "/" "\" "\\"
   *  with "/".
   *
   *  @param  file_name  A file name string possibly containing improper
   *                     separators.
   *
   *  @return  A string containing the file name with all separators replaced 
   *           by "/".
   */
  public static String setForwardSlash( String file_name )
  {
    String separator = "/"; //File.separator;

    String result = StringUtil.replace( file_name, "\\\\", separator );
    result = StringUtil.replace( result, "\\", separator );
    result = StringUtil.replace( result, "//", separator );
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

    // try the provided file name
    File file = new File ( file_name );
    if ( file.exists() )
      return file_name;

    String name = InstrumentType.getFileName( file_name );
    String path = InstrumentType.getPath( file_name );
    char   first_char = name.charAt(0);

    // try the other case
    if ( Character.isUpperCase( first_char ) )
      file_name = path + name.toLowerCase();
    else
      file_name = path + name.toUpperCase();
    file = new File ( file_name );
    if ( file.exists() )
      return file_name;

    // try the other other case
    if ( Character.isUpperCase( first_char ) )
      file_name = path + name.toUpperCase();
    else
      file_name = path + name.toLowerCase();
    file = new File ( file_name );
    if ( file.exists() )
      return file_name;

    // failed so return null
    return null;
  }

   /**
     *  Replaces character string "%20" by a space. The "%20" replaces spaces 
     *  in URL names.
     *  @param  S   The string that is to have the "%20"'s removed
     *  @return  The string with the "%20"'s replaced by spaces
     */
   public static String URLSpacetoSpace( String S){
     String Res="";
     int j=0;
     if( S == null)
        return S;
     j=0;
     for( int i = S.indexOf("%20"); (i>=0)&&(j < S.length()); ){
        Res =Res + S.substring( j,i)+" ";
        j = i+3;
        i =S.indexOf("%20",j);
     }
    if( j <S.length())
      Res +=S.substring( j);
    return Res;
    }
    /**
     * Produces the appropriate directory for a given document
     *
     * @param docFile The name of the document to be used
     *
     * @return A string containing a link to either the local document
     *         or a null string indicating failure.
     */
    public static String docDir( String docFile ){
	String S=SharedData.getProperty("Docs_Directory");
        char pathSep = File.separatorChar;
	  if( S == null ){
	      S=SharedData.getProperty("ISAW_HOME");
	  }
	  if( S == null ){
	      SharedData.addmsg("ISAW_HOME is not defined in Properties file");
	      return null; // do nothing
	  }
	  S=S.trim();
	  S=StringUtil.setFileSeparator(S);
	  if( S.charAt( S.length()-1 ) != pathSep ){
	      S=S+pathSep;
	  }
          String dirr="html"+pathSep;
	  if( (new File(S+docFile)).exists() ){
	      S=S+docFile;
	  }else if( (new File(S+dirr+docFile)).exists() ){
	      S=S+"html"+pathSep+docFile;
	  }else if( (new File(S+"docs"+pathSep+dirr+docFile)).exists() ){
	      S=S+"docs/html/"+docFile;
	  }else{
	      SharedData.addmsg("CANNOT FIND DOCUMENT: "+docFile+"in "+S+" or"
                                +S+dirr+docFile+ " or "+S+"docs"+pathSep+dirr);
	      return null; // file doesn't exist
	  }
	      
	  S="file:///"+S;
	  return S;
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
	String S = SharedData.getProperty("Help_Directory");
        if(S!=null){
            S=S.trim();
        }

	//System.out.println("1: Source is "+S); 

	// fix the string up and check that helpFile exists there
	if(S != null ){
	    S = StringUtil.setFileSeparator(S);
	    if( S.length()<1 ){ S=null; 
	    }else if("\\/".indexOf(S.charAt(S.length()-1))<0){
		S=S+File.separator; }

	    if( new File(S+helpFile).exists()){
	    }else{ S=null; }
	}

	//System.out.println("2: Source is "+S); 

	// if helpFile is not in help_file then try in $HOME
	if( S == null ){
	    S=SharedData.getProperty("user.dir").trim();
	    if( S.length()>0 ){
		if( "\\/".indexOf(S.charAt(S.length()-1)) < 0 ){
		    S=S+File.separator;
		}
	    }
	    S=StringUtil.setFileSeparator(S);
	    if(!new File(S+"IsawHelp"+File.separator+helpFile).exists()){
		S=null;
	    }else{ 
		S=S+"IsawHelp"+File.separator;
	    }
	}

	//System.out.println("3: Source is "+S); 

	
	// if helpFile still hasn't been found look throughout the classpath
	if( S != null ){
	    S = S + helpFile;
        }else{
	    String CP = SharedData.getProperty("java.class.path").replace( '\\','/');
	    int s, t ;
            for( s = 0; (s < CP.length()) && (S == null); s++){
		t = CP.indexOf( File.pathSeparator, s+1);
		if( t < 0){ t = CP.length(); }
		S = CP.substring(s,t) .trim();
		if( S.length() > 0 ){
		    if ( S.charAt( S.length() -1) != File.separatorChar){ 
			S = S + File.separator;
		    }
		}
		if(new File(S+"IsawHelp"+File.separator+helpFile).exists()){
		    S= S + "IsawHelp"+File.separator+helpFile;
		}else{
		    S = null;
		}
	    }
	}
	
	//System.out.println("4: Source is "+S); 

	// either it has been found or just give the URL
	if( S == null ){
            SharedData.addmsg("File ("+helpFile
                              +") not found. Using version at "+URLDIR);
	    S = URLDIR+helpFile;
	}else{ 
	    S = "file:///" + S; 
	}


	//System.out.println("5: Source is "+S); 
    
	return S;
    }

}
