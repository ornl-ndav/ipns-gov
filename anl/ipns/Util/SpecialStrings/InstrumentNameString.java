/*
 * @(#)InstrumentNameString.java  0.1  2000/07/31  Dennis Mikkelson
 *
 *  $Log$
 *  Revision 1.1  2000/07/31 15:40:57  dennis
 *  Now includes default constructor
 *
 *
 */
package DataSetTools.util;

import java.io.*;

/**
 * The InstrumentNameString class is used to pass the name of an instrument 
 * between operators and the GUI so that appropriate GUI components can be 
 * created to get the input values from the user. 
 */
public class InstrumentNameString  extends     SpecialString
                                   implements  Serializable 
{
   public InstrumentNameString( )
   {
     super("");
   }


   public InstrumentNameString( String message )
   {
     super( message );
   }

}
