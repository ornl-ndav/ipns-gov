/*
 * File:  SerializeUtil.java
 *
 * Copyright (C) 2003, Chris M. Bouzek
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
 *           Chris Bouzek <coldfusion78@yahoo.com>
 *           Department of Mathematics, Statistics and Computer Science
 *           University of Wisconsin-Stout
 *           Menomonie, WI 54751, USA
 *
 * This work was supported by the National Science Foundation under grant
 * number DMR-0218882.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 * $Log$
 * Revision 1.2  2004/03/11 22:17:04  millermi
 * - Changed package names and replaced SharedData with
 *   SharedMessages class.
 *
 * Revision 1.1  2003/10/30 02:10:47  bouzekc
 * Added to CVS.
 *
 *
 */
package gov.anl.ipns.Util.File;

import java.io.*;


/**
 * Utility class for reading and writing generic Objects from and to a File.
 */
public class SerializeUtil {
  //~ Constructors *************************************************************

  /**
   * Don't let anyone instantiate this class.
   */
  private SerializeUtil(  ) {}

  //~ Methods ******************************************************************

  /**
   * Testbed.
   */
  public static void main( String[] args ) {
    //create a String, print, serialize, deserialize, print
    String test = "Test1";
    System.out.println( "Before serialization " + test );

    File junkFile = new File( "SerializeUtilTest.dat" );

    //write to the file
    writeObjectToFile( test, junkFile );
    System.out.println( 
      "After serialization " + readObjectFromFile( junkFile ) );

    //now test it with a String file name
    test = "Test2";
    System.out.println( "Before serialization " + test );

    String junkFileName = "SerializeUtilTest.dat";

    //write to the file
    writeObjectToFile( test, junkFileName );
    System.out.println( 
      "After serialization " + readObjectFromFile( junkFileName ) );
  }

  /**
   * Reads an Object from a file.  It automatically closes the InputStream and
   * handles Exceptions.
   *
   * @param file The file to read from.
   *
   * @return The Object read if successful, null otherwise.
   */
  public static Object readObjectFromFile( File file ) {
    ObjectInputStream in = null;

    try {
      in = new ObjectInputStream( new FileInputStream( file ) );

      return in.readObject(  );
    } catch( IOException ioe ) {
      return null;
    } catch( ClassNotFoundException cnfe ) {
      return null;
    } finally {
      if( in != null ) {
        try {
          in.close(  );
        } catch( IOException ioe ) {
          //drop it on the floor
        }
      }
    }
  }

  /**
   * Reads an Object from a file.  It automatically closes the InputStream and
   * handles Exceptions.
   *
   * @param fileName The name of the file to read from.
   *
   * @return The Object read if successful, null otherwise.
   */
  public static Object readObjectFromFile( String fileName ) {
    return readObjectFromFile( new File( fileName ) );
  }

  /**
   * Writes an Object to a file.  This will overwrite anything currently in the
   * File.  It also automatically closes the OutputStream and handles
   * Exceptions.
   *
   * @param obj The Object to write to a file.
   * @param file The file to write to.
   *
   * @return true if the write succeeded, false otherwise.
   */
  public static boolean writeObjectToFile( Object obj, File file ) {
    ObjectOutputStream out = null;

    try {
      out = new ObjectOutputStream( new FileOutputStream( file ) );
      out.writeObject( obj );

      return true;
    } catch( IOException ioe ) {
      return false;
    } finally {
      if( out != null ) {
        try {
          out.close(  );
        } catch( IOException ioe ) {
          //drop it on the floor
        }
      }
    }
  }

  /**
   * Writes an Object to a file.  This will overwrite anything currently in the
   * File.  It also automatically closes the OutputStream and handles
   * Exceptions.
   *
   * @param obj The Object to write to a file.
   * @param fileName The file name to write to.
   *
   * @return true if the write succeeded, false otherwise.
   */
  public static boolean writeObjectToFile( Object obj, String fileName ) {
    return writeObjectToFile( obj, new File( fileName ) );
  }
}
