/*
 * File:  StringArrayPG.java
 *
 * Copyright (C) 2006, Ruth Mikkelson
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
 * Contact : Ruth Mikkelson <mikkelsonr@uwstout.edu>
 *           Department of Mathematics, Statistics and Computer Science
 *           University of Wisconsin-Stout
 *           Menomonie, WI 54751, USA
 *
 * This work was supported by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 *
 * Modified:
 *
 * $Log$
 * Revision 1.3  2006/06/29 21:54:22  rmikk
 * Added or fixed the GPL
 *
 * Revision 1.2  2006/06/27 22:27:39  rmikk
 * Incorporated Conversions.get_StringVector to ensure the the initial value
 * was a Vector of Strings
 *
 * Revision 1.1  2006/06/27 21:54:58  rmikk
 * Initial checkin for a parameterGUI for entering and editing medium sized
 * lists of Strings
 *
 * Revision 1.2  2006/06/27 21:46:29  rmikk
 * Fixed the FloatArrayPG to use FloatPG for one entry
 *
 * Revision 1.1  2006/06/27 21:23:56  rmikk
 * Initial checkin for a ParameterGUI for entering medium sized lists of Floats
 *
 */
package gov.anl.ipns.Parameters;

import java.util.Vector;
import java.beans.*;
/**
 * Subclass of VectorPG to deal with one-dimensional Float arrays.
 */
public class StringArrayPG extends VectorPG {
  //~ Constructor *************************************************************

  /**
   * Creates a new FloatArrayPG object.
   *
   * @param name The name of this IntegerArrayPG.
   * @param val The value of this IntegerArrayPG.
   */
  public StringArrayPG( String name, Object val ) {
    super( name, Conversions.get_StringVector( val) );
    setParam( new StringPG( "Enter a String ", 0 ) );
  }

  
  //~ Methods ******************************************************************

  
  
  /**
   * Extract a Vector of Strings for the concrete subclass, from
   * the specified object.   If a proper value for this PG 
   * can't be obtained from the specified object, an  exception
   * will be thrown.
   *
   * @param  obj  The Object specifying the value for this PG.
   *
   * @throws IllegalArgumentException if a Vector of Strings
   *         cannot be extracted from the specified object. 
   */
 public Vector getVectorValue(Object obj) throws IllegalArgumentException {
		if (obj == null)
			return new Vector();
		if (!(obj instanceof Vector))
			throw new IllegalArgumentException(
					"Improper format for Vector<Integer> values");
		Vector Res = new Vector();
		for (int i = 0; i < ((Vector) obj).size(); i++)
			Res.addElement(Conversions.get_String(((Vector) obj).elementAt(i)));

		return Res;
	}
 
 
 /**
  * Construct a copy of this StringArray object.
  *
  * @return A copy of this StringArrayPG, with the same name and value.
  */
 public Object getCopy(){
	 StringArrayPG iog = new StringArrayPG( getName(),(Object) vec_value);
	 return iog;
	 
 }
 
 
 public void propertyChange( PropertyChangeEvent evt){
	 
 }
 


}
