/*
 * File:  PlaceHolderPG.java
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
 * Revision 1.3  2006/07/10 16:25:05  dennis
 * Change to new Parameter GUIs in gov.anl.ipns.Parameters
 *
 * Revision 1.2  2006/06/29 21:54:23  rmikk
 * Added or fixed the GPL
 *
 */
package gov.anl.ipns.Parameters;

import javax.swing.*;
import java.awt.*;

/**
 * This ParameterGUI saves an Object value which cannot be alterred by the GUI.
 * It also passes the values around by reference.
 * @author MikkelsonR
 
 */
public class PlaceHolderPG extends ObjectPG_base {

	JPanel EntryWidget = null;
	
	/**
	 * Constructor
	 * @param name   The name to prompt for a value that cannot be changed
	 * @param val    The initial value
	 * @throws IllegalArgumentException
	 */
	public PlaceHolderPG(String name, Object val)
			throws IllegalArgumentException {
		super(name, ToNonNull(val));
		
	}

	
	// Eliminates null values
	private static Object ToNonNull( Object O){
		if( O==null)
			return new Object();
		return O;
	}
	
	
	
	/* 
	 * Just returns the internal value. There is really no GUI
	 * so the GUI cannot change the value
	 * 
	 * @see gov.anl.ipns.Parameters.ObjectPG_base#getWidgetValue()
	 */
	public Object getWidgetValue() throws IllegalArgumentException {
		return obj_value;
	}

	
	
	/* 
	 * There is no widget to set the value into so nothing happens here.
	 * @param  value  the value to be set(ignored) into the non-existent GUI
	 * @see gov.anl.ipns.Parameters.ObjectPG_base#setWidgetValue(java.lang.Object)
	 */
	public void setWidgetValue(Object value) throws IllegalArgumentException {
		
		

	}

	/* 
	 * Just returns the obj value and makes sure it is not null.  If null
	 * a new Object() is returned
	 * 
	 * @param obj  The obj whose value has to be converted to the correct format
	 * 
	 * @return  The converted( non-null) Object
	 * @see gov.anl.ipns.Parameters.ObjectPG_base#getObjectValue(java.lang.Object)
	 */
	public Object getObjectValue(Object obj) throws IllegalArgumentException {
		return ToNonNull( obj);
	}

	/* Creates a widget with two labels that are not changeable
	 * 
	 * @return the JPanel that is the widget
	 * @see gov.anl.ipns.Parameters.ParameterGUI#getWidget()
	 */
	public JPanel getWidget() {
		if( EntryWidget != null )
			return EntryWidget;
		EntryWidget = new JPanel( new GridLayout( 1, 2));
		EntryWidget.add( new JLabel( getName()));
		EntryWidget.add( new JLabel("  "));
		return EntryWidget;
	}

	/* Destroys reference to the widget 
	 * 
	 * @see gov.anl.ipns.Parameters.ParameterGUI#destroyWidget()
	 */
	public void destroyWidget() {
		EntryWidget = null;

	}

	/* 
	 * Since the GUI does not show the value this does nothing
	 * 
	 * @see gov.anl.ipns.Parameters.IParameterGUI#clear()
	 */
	public void clear() {
		
	}

	/* 
	 * Sets the enable status of the EntryWidget and its two children
	 * 
	 * @param on_off  if true the widget will be enabled otherwise it
	 *                will be disabled.  Here it means that the GUI
	 *                will be grayed out or not. This GUI cannot change
	 *                
	 * @see gov.anl.ipns.Parameters.IParameterGUI#setEnabled(boolean)
	 */
	public void setEnabled(boolean on_off) {
		if( EntryWidget == null)
			return;
		EntryWidget.setEnabled( on_off);
		EntryWidget.getComponent( 0 ).setEnabled( on_off);
		EntryWidget.getComponent( 1 ).setEnabled( on_off );

	}

	/* 
	 * Returns a copy of this ParameterGUI. Unfortunately it returns a reference
	 * to the value. For parameters to be passed by reference, this MUST be the
	 * case.
	 * 
	 *  @see gov.anl.ipns.Parameters.IParameter#getCopy()
	 */
	public Object clone() {
		return new PlaceHolderPG( getName(), obj_value);
	}

}
