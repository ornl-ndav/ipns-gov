/*
 * File:  DSFieldString.java
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
 *  Revision 1.5  2004/03/11 23:07:19  rmikk
 *  Add the correct package name to all files
 *
 *  Revision 1.4  2002/11/27 23:23:49  pfpeterson
 *  standardized header
 *
 */
package gov.anl.ipns.Util.SpecialStrings;

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
