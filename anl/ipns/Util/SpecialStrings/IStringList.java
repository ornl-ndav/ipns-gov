/*
 * @(#)IStringList.java     1.0  2000/10/24  Dennis Mikkelson
 *
 *  $Log$
 *  Revision 1.1  2000/11/07 16:29:10  dennis
 *  Interface to a list of strings. Will be implemented by subclasses of
 *  SpecialString to pass lists of choices to automatically generated
 *  GUI components.
 *
 *
 */

package DataSetTools.util;

import java.io.*;

/**
 * The StringList interface provides an interface that represents an ordered
 * list of strings.
 *
 */

public interface IStringList
{

  /* --------------------------- num_strings ------------------------------ */
  /**
   *  Get the number of Strings contained in this list of Strings.
   *
   *  @return  the number of Strings in the list of Strings.
   */

  public int num_strings();


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

  public String getString( int position );

}
