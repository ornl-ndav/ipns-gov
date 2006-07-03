/*
 * File:  FloatArrayArrayPG.java
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
 * Revision 1.2  2006/07/03 21:13:58  dennis
 * Explicitly create new object, rather than use autoboxing.
 *
 * Revision 1.1  2006/06/30 14:15:53  rmikk
 * Initial checkin for FloatArrayArrayPG. This ParameterGUI allows for entering
 *   and maintaining a Vector of Vector of Floats
 *
 *
 */
package gov.anl.ipns.Parameters;

import java.util.Vector;
import java.beans.*;
/**
 * Subclass of VectorPG to deal with two-dimensional Float "arrays",i.e.
 * Vector of Vector of Flaots
 */
public class FloatArrayArrayPG extends VectorPG {
  //~ Constructor *************************************************************

  /**
   * Creates a new FloatArrayArrayPG object.
   *
   * @param name   the prompt for entering this Vector of Vector of Floats
   * @param val The initial value for this FloatArrayArrayPG.
   */
  public FloatArrayArrayPG( String name, Object val ) {
    super( name, get_Vector_Vector_Float(val) );
    setParam( new FloatArrayPG( "Enter an Float Arrray", new Integer(0) ) );
  }

  
  //~ Methods ******************************************************************

  
  
  /**
   * Extract a Vector of Vector of Floats for the concrete subclass, from
   * the specified object.  
   *
   * @param  obj  The Object specifying the value for this PG.
   *
   * @param the corresponding Vector of  Vector of Floats
   *
   * @throws IllegalArgumentException if a Vector of the required type
   *         cannot be extracted from the specified object. 
   */
 public Vector getVectorValue(Object obj) throws IllegalArgumentException {
		return get_Vector_Vector_Float( obj);
	}
 
 
 /**
  * Construct a copy of this IParameter object.
  *
  * @return A copy of this IParameter, with the same name and value.
  */
 public Object getCopy(){
	 FloatArrayArrayPG iog = new FloatArrayArrayPG( getName(),(Object) vec_value);
	 return iog;
	 
 }
 
 /**
  *  Not used
  */
 public void propertyChange( PropertyChangeEvent evt){
	 
 }
 
 
 // Base method for extracting a Vector of Vector of Floats from
 // a given object
 private static Vector get_Vector_Vector_Float( Object obj) throws 
           IllegalArgumentException{
    if( obj == null){
       return new Vector();
    }
   
    obj = Conversions.ToVec(  obj);
    if( (obj == null) || !(obj instanceof Vector)) throw
      new IllegalArgumentException( " improper FloatArrayArray value");
    Vector Res = new Vector();
    for( int i=0; i< ((Vector)obj).size(); i++){
       Object elt =((Vector)obj).elementAt( i);
       Res.addElement( Conversions.get_FloatVector( elt));
    }
    return Res;
 }


}
