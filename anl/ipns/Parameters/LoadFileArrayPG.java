/*
 * File:  LoadFileArrayPG.java
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
 * Revision 1.2  2006/07/10 16:03:16  dennis
 * Change to new Parameter GUIs in gov.anl.ipns.Parameters
 *
 * Revision 1.1  2006/06/30 14:58:29  rmikk
 * Initial Checkin for LoadFileArrayPG.  This returns a vector of filenames that
 *   can use the Browse button to select( from separate directories too)
 *
 */
package gov.anl.ipns.Parameters;

import java.util.Vector;
import java.beans.*;
/**
 * Subclass of VectorPG to deal with one-dimensional int arrays.
 */
public class LoadFileArrayPG extends VectorPG {
  //~ Constructors *************************************************************
  LoadFilePG lFilePG;
  /**
   * Creates a new IntegerArrayPG object.
   *
   * @param name The name of this IntegerArrayPG.
   * @param val The value of this IntegerArrayPG.
   */
  public LoadFileArrayPG( String name, Object val ) {
    super( name, val );
    lFilePG = new LoadFilePG( "Enter an Filename ", val );
    setParam( lFilePG );
  }

  
  //~ Methods ******************************************************************

  
  /**
   * Extract a Vector of filenames
   * 
   * @param  obj  The Object specifying the value for this PG. This should be
   *              a vector of filenames.
   *              
   *  @return a vector of filenames that exist
   *
   * @throws IllegalArgumentException if a Vector of existing filenames
   *           corresponding to the given object cannot be created.
   */
 public Vector getVectorValue(Object obj) throws IllegalArgumentException {
		if (obj == null)
			return new Vector();
		Vector V = Conversions.ToVec( obj );
		Vector Res = new Vector();
		for (int i = 0; i < V.size(); i++){
         String fileName = (String)(V.elementAt(i).toString());
         if( (new java.io.File( fileName)).exists())
			   Res.addElement( fileName);
         else  throw
            new IllegalArgumentException( "Filename "+i+" does not exists in "+
                    "LoadFileArrayPG");
           
            
      }

		return Res;
	}
 
 
 /**
  * Construct a copy of this LoadFileArrayPG.
  *
  * @return A copy of this LoadFileArrayPG, with the same name and value.
  */
 public Object clone(){
	 LoadFileArrayPG iog = new LoadFileArrayPG( getName(), vec_value);
	 return iog;
	 
 }
 
 
 public void propertyChange( PropertyChangeEvent evt){
	 
 }
 


}
