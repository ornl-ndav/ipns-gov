/*
 * @(#)DataDirectoryString.java  0.1  2000/07/31  Dennis Mikkelson
 *
 *  $Log$
 *  Revision 1.1  2000/07/31 15:40:55  dennis
 *  Now includes default constructor
 *
 *
 */
package DataSetTools.util;

import java.io.*;

/**
 * The DataDirectoryString class is used to pass a directory string between 
 * operators and the GUI so that appropriate GUI components can be created
 * to get the input values from the user. 
 */
public class DataDirectoryString  extends     SpecialString
                                  implements  Serializable 
{
   public DataDirectoryString( ) 
   {
     super("");
   }


   public DataDirectoryString( String message )
   {
     super( message );
   }

}
