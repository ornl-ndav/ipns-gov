/*
 * @(#)IntListString.java  0.1  2000/07/31  Dennis Mikkelson
 *
 *  $Log$
 *  Revision 1.1  2000/07/31 15:40:58  dennis
 *  Now includes default constructor
 *
 *
 */
package DataSetTools.util;

import java.io.*;

/**
 * The IntListString class is used to pass a list of integers in a string form 
 * between operators and the GUI so that appropriate GUI components can be 
 * created to get the input values from the user. 
 */
public class IntListString  extends     SpecialString
                            implements  Serializable 
{
   public IntListString( )
   {
     super( "" );
   }


   public IntListString( String message )
   {
     super( message );
   }

}
