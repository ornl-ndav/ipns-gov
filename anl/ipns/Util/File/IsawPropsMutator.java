/*
 * File:  IsawPropsMutator.java
 *
 * Copyright (C) 2004, Chris M. Bouzek
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
 *           Chris Bouzek <coldfusion78@yahoo.com>
 *
 * This work was supported by the National Science Foundation under grant
 * number DMR-0218882.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 * $Log$
 * Revision 1.1  2004/05/11 23:48:03  bouzekc
 * Added to CVS.
 *
 */
package gov.anl.ipns.Util.File;

import DataSetTools.util.*;

import gov.anl.ipns.Util.Sys.*;

import java.io.*;

import java.util.*;


/**
 * This class is for changing values inside of IsawProps.dat.  To use this
 * class, call mutateIsawPropsKey then call writeBackToFile.  If you do not
 * call mutateIsawPropsKey first, then the behavior of this class is
 * indeterminate-it is an internal  singleton class, and mutateIsawPropsKey
 * creates the instance.
 */
public class IsawPropsMutator {
  //~ Static fields/initializers -----------------------------------------------

  private static String           propsFileName;
  private static Vector           lines = new Vector( 10, 2 );
  private static IsawPropsMutator ipm   = null;

  //~ Constructors -------------------------------------------------------------

  /**
   * Do not instantiate.  This constructor will perform set up work for the
   * IsawProps.dat file.
   */
  private IsawPropsMutator(  ) {
    TextFileReader tfr = null;
    propsFileName = StringUtil.setFileSeparator( SharedData.getProperty( 
          "user.home" ) + "\\" ) + "IsawProps.dat";

    //open it up for reading and writing
    try {
      tfr = new TextFileReader( propsFileName );

      //we are good to go at this point-read in each line and store it in a Vector
      while( !tfr.eof(  ) ) {
        //this ignores the EOL character(s)
        lines.add( tfr.read_line(  ) );
      }
    } catch( IOException ioe ) {
      SharedData.addmsg( "IsawPropsMutator error: IsawProps.dat not found." );
    }

    //close up the reader
    if( tfr != null ) {
      try {
        tfr.close(  );
      } catch( IOException ioe ) {
        //discard
      }
    }
  }

  //~ Methods ------------------------------------------------------------------

  /**
   * Testbed.
   */
  public static void main( String[] args ) {
    IsawPropsMutator.mutateIsawPropsKey( "WIZARD_HEIGHT", "480", true );
    IsawPropsMutator.writeBackToFile(  );
  }

  /**
   * Mutates the value for the given key inside IsawProps.dat.  This only
   * modifies the internal values here-you must call writeBackToFile to
   * actually write these to disk.  The current behavior of this is  to
   * replace only the first line that starts with "key=" where key is the key
   * you passed in as a parameter. This seems reasonable, as IsawProps.dat
   * should not contain multiple key values.<br>
   * <br>
   * Note that because of the matching, anything with a # sign in front (as in
   * comments) is essentially  ignored.<br><br>
   *
   * @param key The key name (e.g. WIZARD_HEIGHT)
   * @param val The key value (e.g. 600)
   * @param addNew Whether to add the key-value pair if it does not yet exist.
   */
  public static void mutateIsawPropsKey( String key, String val, boolean addNew ) {
    /* Note to developers: if you wish to use another method to create an instance of this class for
     * whatever reason, you will need the following lines below.  Always treat this class as a singleton.
     */
    if( ipm == null ) {
      ipm = new IsawPropsMutator(  );
    }

    String  line;
    boolean found = false;

    for( int i = 0; ( i < lines.size(  ) ) && !found; i++ ) {
      line = lines.get( i ).toString(  );

      //did we find the key?
      if( line.startsWith( key + "=" ) ) {
        found   = true;

        //one past the equals sign
        line = line.substring( 0, line.indexOf( "=" ) + 1 ) + val;

        //yank it out 
        lines.remove( i );

        //put the new one in
        lines.insertElementAt( line, i );
      }
    }

    //if we couldn't find it and we were told to add a new one
    if( !found && addNew ) {
      lines.add( key + "=" + val );
    }
  }

  /**
   * Writes the internal keys back to IsawProps.dat.
   */
  public static void writeBackToFile(  ) {
    StringBuffer sb = new StringBuffer( 3500 );

    for( int i = 0; i < lines.size(  ); i++ ) {
      sb.append( lines.get( i ) );

      //Windows can handle just the \n, although it is customary to use \r\n.
      sb.append( "\n" );
    }

    TextWriter.writeASCII( propsFileName, sb.toString(  ) );
  }
}
