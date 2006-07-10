/*
 * File:  IntegerArrayPG.java
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
 * Revision 1.4  2006/07/10 16:25:05  dennis
 * Change to new Parameter GUIs in gov.anl.ipns.Parameters
 *
 * Revision 1.3  2006/07/03 21:06:53  dennis
 * Cleaned up oddity regarding type casts/autoboxing.
 *
 * Revision 1.2  2006/06/27 22:26:56  rmikk
 * Incorporated Conversions.get_IntegerVector to ensure the the initial
 *   value passed in by the constructor was a valid IntegerVector
 *
 * Revision 1.1  2006/06/27 19:50:00  rmikk
 * A parameterGUI that allows for the entering of a  medium sized list of integers.
 * Editing can be done with the ArrayJFrame
 *
 * Revision 1.16  2005/06/10 15:27:42  rmikk
 * Gave a more descriptive label for what is to be entered
 *
 * Revision 1.15  2005/06/07 15:05:47  rmikk
 * Made the initial button better  represent the data to be entered
 *
 * Revision 1.14  2004/05/11 18:23:50  bouzekc
 * Added/updated javadocs and reformatted for consistency.
 *
 * Revision 1.13  2003/12/16 00:06:00  bouzekc
 * Removed unused imports.
 *
 * Revision 1.12  2003/10/11 19:19:16  bouzekc
 * Removed clone() as the superclass now implements it using reflection.
 *
 * Revision 1.11  2003/09/09 23:06:28  bouzekc
 * Implemented validateSelf().
 *
 * Revision 1.10  2003/08/28 03:38:40  bouzekc
 * Changed innerParameter assignment to call to setParam().
 *
 * Revision 1.9  2003/08/28 02:32:36  bouzekc
 * Modified to work with new VectorPG.
 *
 * Revision 1.8  2003/08/15 23:50:05  bouzekc
 * Modified to work with new IParameterGUI and ParameterGUI
 * classes.  Commented out testbed main().
 *
 * Revision 1.7  2003/06/23 16:12:25  bouzekc
 * Reformatted for consistent indenting.
 *
 * Revision 1.6  2003/06/23 14:52:44  bouzekc
 * Removed duplicate inner ActionListener class.  Now uses
 * PGActionListener.
 *
 * Revision 1.5  2003/06/18 20:36:41  pfpeterson
 * Changed calls for NxNodeUtils.Showw(Object) to
 * DataSetTools.util.StringUtil.toString(Object)
 *
 * Revision 1.4  2003/06/09 22:30:06  rmikk
 * Added a clone method
 *
 * Revision 1.3  2003/05/25 18:42:49  rmikk
 * Added GPL
 *
 */
package gov.anl.ipns.Parameters;

import java.util.Vector;
import java.beans.*;
/**
 * Subclass of VectorPG to deal with one-dimensional int arrays.
 */
public class IntegerArrayPG extends VectorPG {
  //~ Constructors *************************************************************

  /**
   * Creates a new IntegerArrayPG object.
   *
   * @param name The name of this IntegerArrayPG.
   * @param val The value of this IntegerArrayPG.
   */
  public IntegerArrayPG( String name, Object val ) {
    super( name, Conversions.get_IntegerVector(val) );
    setParam( new IntegerPG( "Enter an Integer ", new Integer( 0 ) ) );
  }

  
  //~ Methods ******************************************************************

  /*
   * Testbed.
   */
  /*public static void main( String args[] ){
     JFrame jf = new JFrame("Test");
     jf.getContentPane().setLayout( new GridLayout( 1,2));
     IntegerArrayPG IaPg = new IntegerArrayPG( "Enter Int list", null);
     IaPg.initGUI(null);
     jf.getContentPane().add(IaPg.getGUIPanel());
     JButton  jb = new JButton("Result");
     jf.getContentPane().add(jb);
     jb.addActionListener( new PGActionListener( IaPg));
     jf.setSize( 500,100);
     jf.invalidate();
     jf.show();
     }*/
  
  /**
   * Extract a Vector of the type required by the concrete subclass, from
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
			Res.addElement(Conversions.get_int(((Vector) obj).elementAt(i)));

		return Res;
	}
 
 
 /**
  * Construct a copy of this IParameter object.
  *
  * @return A copy of this IParameter, with the same name and value.
  */
 public Object clone(){
	 IntegerArrayPG iog = new IntegerArrayPG( getName(), vec_value);
	 return iog;
	 
 }
 
 
 public void propertyChange( PropertyChangeEvent evt){
	 
 }
 


}
