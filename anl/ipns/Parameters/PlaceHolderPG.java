/**
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
	 * @see gov.anl.ipns.Parameters.NewParameterGUI#getWidget()
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
	 * @see gov.anl.ipns.Parameters.NewParameterGUI#destroyWidget()
	 */
	public void destroyWidget() {
		EntryWidget = null;

	}

	/* 
	 * Since the GUI does not show the value this does nothing
	 * 
	 * @see gov.anl.ipns.Parameters.INewParameterGUI#clear()
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
	 * @see gov.anl.ipns.Parameters.INewParameterGUI#setEnabled(boolean)
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
	 *  @see gov.anl.ipns.Parameters.INewParameter#getCopy()
	 */
	public Object getCopy() {
		return new PlaceHolderPG( getName(), obj_value);
	}

}
