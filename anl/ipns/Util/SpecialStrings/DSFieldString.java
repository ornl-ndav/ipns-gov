/*
 * @(#)DSFieldString.java  0.1  2000/07/31  Dennis Mikkelson
 *
 *  $Log$
 *  Revision 1.1  2000/07/31 15:40:54  dennis
 *  Now includes default constructor
 *
 *
 */
package DataSetTools.util;

import java.io.*;

/**
 * The DSFieldString class is used to pass parameters between the field setting
 * operators and the GUI so that appropriate GUI components can be created
 * to get the input values from the user. 
 */
public class DSFieldString  extends     SpecialString
                            implements  Serializable 
{
   public DSFieldString( )
   {
     super( "" );
   }

   public DSFieldString( String message )
   {
     super( message );
   }

}
