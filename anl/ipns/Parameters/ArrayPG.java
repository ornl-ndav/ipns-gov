/* File:  ArrayPG.java 
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
 * Contact : Ruth Mikkelson <Mikkelsonr@uwstout.edu>
 *           University of Wisconsin-Stout
 *           Menomonie, Wisconsin 54751
 *           USA
 *
 * This work was supported by the National Science Foundation under grant
 * number DMR-0426797, and by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 *
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 *
 *  $Log$
 *  Revision 1.3  2006/07/10 16:25:03  dennis
 *  Change to new Parameter GUIs in gov.anl.ipns.Parameters
 *
 */
package gov.anl.ipns.Parameters;

import java.util.Vector;

import javax.swing.*;

import java.awt.*;
import gov.anl.ipns.Util.Sys.*;

/**
 *  This class allows for the entry of small lists of data of arbitrary
 *  data types. String entry data types can only include integer,string,
 *  float,boolean, or othe vectors( use [...] to indicate arrays.
 *  
 *  The lists can be separated by spaces or comma's and strings do not
 *  have to use quotes if they have no spaces in them or they do not
 *  represent a legitimate number or boolean value
 * @author Ruth
 *
 */
public class ArrayPG extends VectorPG_base {

	private JPanel entryWidget;
	private JTextField entryField;
	
	
	/**
	 * Constructor
	 * @param name   The prompt for this list of numbers
	 * @param val    The initial value for this list of numbers
	 *               it will be converted to a Vector and throw an
	 *               exception if that is not possible
	 * @throws IllegalArgumentException
	 */
	public ArrayPG(String name, Object val) throws IllegalArgumentException {
		super(name, Conversions.ToVec( val));
		entryWidget  = null;
		entryField = null;
	}
	

	 /**
	  * Retrieves the GUI's current value.  IF the value in the widget does
	  * NOT refer to a Vector then an IllegalArgumentException is thrown.
	  * 
	  * @return The value of the GUI.
	  *
	  * @throws IllegalArgumentException is called if this is called without
	  *         a GUI widget being present, or if the value in the widget doesn't 
	  *         refer to a Vector.
	  */
	public Vector getWidgetValue() throws IllegalArgumentException {
		if( entryField == null)
			throw new IllegalArgumentException( "ArrayPG GUI is not set up");
		return Conversions.StringToVec( entryField.getText());
	}


  /**
   * Sets the GUI's current value.  NOTE: When this method is called from the
   * setValue(obj) method, the validity of the argument has already been 
   * checked.
   *
   * @param value  The Vector reference to set into the GUI widget.
   *
   * @throws IllegalArgumentException is called if this is called without
   *         a GUI widget being present.
   */
	public void setWidgetValue(Vector value) throws IllegalArgumentException {
		entryField.setText( StringUtil.toString( value ) );
	}

	
 /**
   * Returns the vector passed in. This Parameter does not enforce
   * any specific data type on its Vector elements
   * 
   * @param  obj  The Object specifying the value for this PG.
   *
   * @throws IllegalArgumentException if a Vector of the required type
   *         cannot be extracted from the specified object. 
   */	
	public Vector getVectorValue(Object obj) throws IllegalArgumentException {
		if( obj instanceof String)
			obj = Conversions.StringToVec( (String)obj);
		if( obj == null )
			return new Vector();
		if( obj instanceof Vector)
			return (Vector)obj;
		return Conversions.ToVec( obj);
	}
	

  /**
   * Returns the bottom level GUI component in a JPanel.  If it does not 
   * exist it will be created.
   * 
   * @return The GUI panel containing the parameterGUI.
   */
	public JPanel getWidget() {
		
		if( entryWidget != null)
			return entryWidget;
        String InitVal = gov.anl.ipns.Util.Sys.StringUtil.toString( vec_value);
        
		entryWidget = new JPanel( new GridLayout(1,2));
        entryWidget.add( new JLabel( getName()));
        entryField = new JTextField( InitVal);
        entryField.setPreferredSize( new Dimension(2,2));
        entryWidget.add(  entryField );
        entryField.addKeyListener( new PG_KeyListener( this));
        
        return entryWidget;
	}


   /**
	 * Set internal references to the lower level GUI entry widget to null so
	 * that it can be garbage collected. Subsequent calls to getWidget() will
	 * create a new widget.
	 */
	public void destroyWidget() {
		entryWidget = null;
		entryField = null;
	}
	

  /**
   * Enable or disable the GUI Components for entering values. 
   *
   * @param  on_off  Set true to enable the Components for user input.
   */	
	public void setEnabled(boolean on_off) {
		
		if( entryWidget == null)
			return;
		entryWidget.setEnabled( on_off);
		
		for( int i=0; i< entryWidget.getComponentCount(); i++)
			entryWidget.getComponent(i).setEnabled( on_off);
	}

	
  /**
   * Construct a copy of this ArrayPG object.
   *
   * @return A copy of this ArrayPG, with the same name and value.
   */
	public Object clone() {
		ArrayPG res = new ArrayPG( getName(), vec_value);
		return res;
	}


	/**
	 * Adds an element to the end of the current vector value
	 * @param obj  The item to be added
	 */
	public void addItem( Object obj){
	   vec_value.addElement( obj );
	}
}
