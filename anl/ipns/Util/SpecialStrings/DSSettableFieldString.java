/*
 * @(#)DSSettableFieldString.java  0.1  2000/10/25  Dennis Mikkelson
 *
 *  $Log$
 *  Revision 1.1  2000/11/07 16:30:09  dennis
 *  Class for names of DataSet fields that can be set by the user.
 *
 *
 */
package DataSetTools.util;

import java.io.*;

/**
 * The DSSettableFieldString class is used to pass parameters between the 
 * field setting operator and the GUI so that appropriate GUI components 
 * can be created to get the input values from the user. 
 */
public class DSSettableFieldString  extends     SpecialString
                                    implements  IStringList,
                                                Serializable 
{
   static private final String strings[] = { DSFieldString.TITLE,
                                             DSFieldString.X_LABEL, 
                                             DSFieldString.X_UNITS,
                                             DSFieldString.Y_LABEL, 
                                             DSFieldString.Y_UNITS,
                                             DSFieldString.POINTED_AT_ID,
                                             DSFieldString.POINTED_AT_INDEX,
                                             DSFieldString.SELECTED_GROUPS  };
   public DSSettableFieldString( )
   {
     super( "" );
   }

   public DSSettableFieldString( String message )
   {
     super( message );
   }

  /* --------------------------- num_strings ------------------------------ */
  /**
   *  Get the number of Strings contained in this list of Strings.
   *
   *  @return  the number of Strings in the list of Strings.
   */

  public int num_strings()
  {
     return strings.length; 
  }


  /* ----------------------------- getString ----------------------------- */
  /**
   *  Get a copy of the String in the specified position in this list
   *  of Strings.
   *
   *  @param   position  The position in the list from which the string is to
   *                     be obtained.
   *
   *  @return  A copy of the String in the given position in the list,
   *           if the position is valid, or null of the position is not valid.
   */

  public String getString( int position )
  {
     if ( position < 0 || position >= strings.length )
       return null;
     else
       return strings[ position ];
  }

}
