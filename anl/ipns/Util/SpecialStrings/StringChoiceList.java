/*
 * File:  StringChoiceList.java
 *
 * Copyright (C) 2001, Dennis Mikkelson
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
 *  Revision 1.5  2003/05/06 16:58:19  rmikk
 *  Removed a static reference to the string list so several
 *    instances can have different lists
 *
 *  Revision 1.4  2002/11/27 23:23:49  pfpeterson
 *  standardized header
 *
 *  Revision 1.3  2001/11/27 18:38:18  dennis
 *     1. Set the string value to the first in the list
 *     2. Added "some" tests for null lists. (Ruth)
 *
 */

package DataSetTools.util;

import java.io.*;

/**
 * A StringChoiceList is the base class for a class that holds a list of 
 * string choices.  One of the strings is designated as the current choice 
 * and can be obtained from the toString() method inherited from the 
 * SpecialString class.  Derived classes define specific lists of strings.
 */

public class StringChoiceList extends    SpecialString
                              implements IStringList,
                                         Serializable
{
 protected String strings[] = { "DEFAULT" };  // derived classes should
                                                      // replace this with
                                                      // their own list.

  /* ---------------------------- Constructor --------------------------- */
  /**
   *  Make a StringChoiceList object with a default choice.
   */
  public StringChoiceList( )
  {
    super( "" );
    setString("");
  }

  /** This constructor sets the list of strings without subclassing
  */
   public StringChoiceList( String[]  list)
    {super("");
     if( list == null)
       strings = new String[0];
     else
       strings = list;
     
     setString("");
     
     }
 
  
  /* ---------------------------- setString ----------------------------- */
  /**
   * Set the string value of a special string to the specified message.
   * if the specified string is not found in the list, the first string
   * in the list is used by default.
   *
   * @param   message   The string that to be set as the value of this
   *                    SpecialString object.
   */
  public void setString( String message )
  {
    boolean found = false;
    int     i     = 0;
    while ( (i < strings.length) && !found )
      if ( strings[i].equals( message ) )
        found = true;
      else
        i++;

    if ( found )
      super.setString( message );
    else
    {
      if ( strings.length > 0 )
        super.setString( strings[0] );
      else
        super.setString("");
    }
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
