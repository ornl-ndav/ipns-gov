/*
 * File: UniqueIntGenerator.java
 *
 * Copyright (C) 2005, Chad Jones
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
 * This work was supported by the University of Tennessee Knoxville and 
 * the Spallation Neutron Source at Oak Ridge National Laboratory under: 
 *   Support of HFIR/SNS Analysis Software Development 
 *   UT-Battelle contract #:   4000036212
 *   Date:   Oct. 1, 2004 - Sept. 30, 2006
 *
 * Primary   Chad Jones <cjones@cs.utk.edu>
 * Contact:  Student Developer, University of Tennessee
 *
 * Contact : Dennis Mikkelson <mikkelsond@uwstout.edu>
 *           Department of Mathematics, Statistics and Computer Science
 *           University of Wisconsin-Stout
 *           Menomonie, WI 54751, USA
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 *  Modified:
 *
 *  $Log$
 *  Revision 1.1  2005/08/04 22:21:56  cjones
 *  This class provides a method to generate consecutive integers based
 *  off a static integer.  Other classes that need unique integer values
 *  can use this object to ensure they do not pick overlapping ints.
 *
 */

package gov.anl.ipns.Util.Numeric;

/**
 * This class will incrementally generate unique
 * integers using a static int.  The integers will cycle 
 * through INTIAL_INT to Integer.MAX_VALUE.  Whenever 
 * Integer.MAX_VALUE is reached, the generator will 
 * printout a warning message and restart at INTIAL_INT.  
 * The integers generated will increase by increments of 1.  
 */
public class UniqueIntGenerator 
{
  /**
   * Static int for the intial value of the 
   * int generator.  This will be the first 
   * number returned by getNextInt, and also,
   * the number that the generator will reset
   * to when it has cycled through all integers.
   */
  public static final int INTIAL_INT = 1;
	
  private static int increment = 1;
  private static int currentValue = INTIAL_INT-increment;
  
  /**
   * This generates the next integer value from
   * current static integer.  If the integer value
   * reaches the maximum integer value, it will be 
   * reset to the intial value.
   * 
   * @return The next integer of the static int.
   */
  public static int getNextInt()
  {
    currentValue += increment;
    
    if( currentValue >= Integer.MAX_VALUE )
    {
      System.err.println("UniqueIntGenerator.getNextInt():" +
      		             " maximum integer value reached. " +
						 "Restarting cycle at integer " + INTIAL_INT);
      currentValue = INTIAL_INT;
    }
    
    return currentValue;
  }
}
