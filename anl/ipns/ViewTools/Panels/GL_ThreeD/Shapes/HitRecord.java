/*
 * File:  HitRecord.java
 *
 * Copyright (C) 2004, Dennis Mikkelson
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
 * This work was supported by the National Science Foundation under grant
 * number DMR-0218882.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 * Modified:
 *
 * $Log$
 * Revision 1.2  2004/08/04 18:35:11  dennis
 * Put error messages for invalid data in the constructor into
 * if (debug) statements.
 *
 * Revision 1.1  2004/07/16 14:48:48  dennis
 * Class to hold information about intersections of a line-of-sight
 * with objects in a 3D scene, as returned by the OpenGL selection
 * mechanism.
 *
 */
package gov.anl.ipns.ViewTools.Panels.GL_ThreeD.Shapes;

/** 
 *  This class contains the information returned by the OpenGL selection
 *  operation, such as the min and max depth and the list of "names" on
 *  the OpenGL name stack.  
 */

public class HitRecord 
{
  private static boolean debug = false;

  private int num_names = 0;
  private int min = Integer.MAX_VALUE,
              max = Integer.MAX_VALUE;
  private int names[] = new int[0];

  /* --------------------------- constructor -------------------------- */
  /**
   *  Construct a HitRecord by extracting the hit information from the
   *  specified buffer returned by OpenGL.
   *
   *  @param  buffer   An array containing the selection information 
   *                   as a sequence of integers in the sequence returned
   *                   by the OpenGL selection mechanism.
   *  @param  start    The index into the buffer where the current hit
   *                   record information starts.
   */
  public HitRecord( int buffer[], int start )
  {
    int n_names = buffer[ start ];
    if ( n_names <= 0 || n_names > 100 )     // something must be wrong with
    {                                        // the hit buffer
      if ( debug )
        System.out.println("Invalid number of names in HitRecord constructor: "
                           + n_names );
      return;
    }

    if ( buffer == null || buffer.length < start + n_names + 3 ) 
    {                                       
      if ( debug )
      {
        System.out.println("Invalid buffer in HitRecord constructor " );
        if ( buffer == null )
          System.out.println("buffer is null " );
        else
          System.out.println("length = " + buffer.length + 
                             ", start = " + start +
                             ", num_names = " + n_names );
      }
      return;
    }

    num_names = n_names;
    min       = buffer[ start+1 ]; 
    max       = buffer[ start+2 ];
    names     = new int[ num_names ];
    for ( int i = 0; i < num_names; i++ )
      names[i] = buffer[ start + i + 3 ];
  }

  /* ----------------------------- numNames ---------------------------- */
  /**
   *  Get the number of names for this hit.
   *
   *  @return the number of names for this hit. 
   */
  public int numNames()
  {
    return num_names;
  }

  /* ----------------------------- lastName ---------------------------- */
  /**
   *  Get the last name on the list of names for this hit.
   *
   *  @return  The top name on the name stack for this hit.
   */
  public int lastName()
  {
    if ( num_names > 0 )
      return names[ num_names - 1 ];
    else
      return GL_Shape.INVALID_LIST_ID;
  }

  /* ----------------------------- getNames ---------------------------- */
  /**
   *  Get the array of names for this hit.
   *
   *  @return  A reference to the array of names for this hit.
   */
  public int[] getNames()
  {
    return names;
  }

  /* ----------------------------- getMin ---------------------------- */
  /**
   *  Get the minimum depth value for this hit.
   *
   *  @return  The minimum distance for this hit.
   */
  public int getMin()
  {
    return min; 
  }

  /* ----------------------------- getMax ---------------------------- */
  /**
   *  Get the maximum depth value for this hit.
   *
   *  @return  The maximum distance for this hit.
   */
  public int getMax()
  {
    return max; 
  }

  /* ---------------------------- toString ------------------------- */
  /**
   *  Convert the information in this HitRecord to a String form for
   *  debugging purposes.
   *
   *  @return  A multi-line string containing the number of names,
   *           min and max distance and the list of names for this 
   *           hit record.
   */
  public String toString()
  {
    if ( num_names <= 0 )
      return "Empty HitRecord";
    
    String result = "num_names " + num_names + "\n";
    result       += " min, max = " + min + ", " + max + "\n";
    result       += " names = ";
    for ( int i = 0; i < num_names; i++ )
      result += " " + names[i];
    result += "\n"; 

    return result;
  }

}
