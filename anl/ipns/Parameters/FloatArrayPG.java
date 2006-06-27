/*
 * File:  FloatArrayPG.java
 *
 * Copyright (C) 2003, Ruth Mikkelson
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
public class FloatArrayPG extends VectorPG {
  //~ Constructor *************************************************************

  /**
   * Creates a new FloatArrayPG object.
   *
   * @param name The name of this IntegerArrayPG.
   * @param val The value of this IntegerArrayPG.
   */
  public FloatArrayPG( String name, Object val ) {
    super( name, val );
    setParam( new FloatPG( "Enter an Float ", 0 ) );
  }

  
  //~ Methods ******************************************************************

  
  
  /**
   * Extract a Vector of floats for the concrete subclass, from
   * the specified object.  If the object is a Vector, it will serve as the
   * value for the PG.  In (special) cases, some attempt may be made to
   * extract a Vector with the correct contents from different types of
   * objects.  The object types that are supported will depend on the
   * concrete PG class, derived from this class, and should be described
   * in the documentation for the concrete PG class.  If a proper value
   * for this PG can't be obtained from the specified object, an
   * exception will be thrown.
   *
   * @param  obj  The Object specifying the value for this PG.
   *
   * @throws IllegalArgumentException if a Vector of the required type
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
			Res.addElement(Conversions.get_float(((Vector) obj).elementAt(i)));

		return Res;
	}
 
 
 /**
  * Construct a copy of this IParameter object.
  *
  * @return A copy of this IParameter, with the same name and value.
  */
 public Object getCopy(){
	 FloatArrayPG iog = new FloatArrayPG( getName(),(Object) vec_value);
	 return iog;
	 
 }
 
 
 public void propertyChange( PropertyChangeEvent evt){
	 
 }
 


}
