/*
 * @(#)DSFieldString.java  0.1  2000/07/31  Dennis Mikkelson
 *
 *  $Log$
 *  Revision 1.2  2000/11/07 15:33:44  dennis
 *  Now implements IStringList with methods to get the number of strings and
 *  the individual strings.
 *
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
                            implements  IStringList,
                                        Serializable 
{
  static public final String  TITLE            = "Title";
  static public final String  X_LABEL          = "X label";
  static public final String  X_UNITS          = "X units";
  static public final String  Y_LABEL          = "Y label";
  static public final String  Y_UNITS          = "Y units";
  static public final String  MAX_SAMPLES      = "Max Number of X Values"; 
  static public final String  X_RANGE          = "X Range"; 
  static public final String  Y_RANGE          = "Y Range"; 
  static public final String  POINTED_AT_ID    = "Pointed At Group ID"; 
  static public final String  POINTED_AT_INDEX = "Pointed At Index"; 
  static public final String  NUM_GROUPS       = "Num Groups";
  static public final String  GROUP_IDS        = "Group IDs";
  static public final String  NUM_SELECTED     = "Num Selected";
  static public final String  SELECTED_GROUPS  = "Selected IDs";

   static private final String strings[] = { TITLE,
                                             X_LABEL, 
                                             X_UNITS,
                                             Y_LABEL, 
                                             Y_UNITS,
                                             MAX_SAMPLES,
                                             X_RANGE,
                                             Y_RANGE,
                                             POINTED_AT_ID,
                                             POINTED_AT_INDEX,
                                             NUM_GROUPS,
                                             GROUP_IDS,
                                             NUM_SELECTED,
                                             SELECTED_GROUPS };
   public DSFieldString( )
   {
     super( "" );
   }

   public DSFieldString( String message )
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
