/*
 * File:  VectorPG.java
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
 * Revision 1.2  2006/06/27 21:26:19  rmikk
 * Fixed setEnabled so more GUI Elements change
 * Omitted a getValue and used the protected vec_value to prevent errors
 *
 * Revision 1.1  2006/06/27 19:48:52  rmikk
 * abstract superclass for several ParameterGUI's that allow for medium sized
 * lists that can be editted by the ArrayJFrame
 *

 */
package gov.anl.ipns.Parameters;




import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.awt.*;

import java.util.Vector;

import javax.swing.JButton;
import javax.swing.*;


/**
 * This parameterGUI is the parent class of other parameterGUI's whose values
 * are Vectors with a common Object data type for each elements.  This GUI is
 * best for a medium sized list.  The list appears in a list box where the
 * values can be edited deleted, and/or rearranged. A vector of choicelist
 * should go through this constructor.
 */
public abstract class VectorPG extends VectorPG_base
  implements PropertyChangeListener {
  //~ Static fields/initializers ***********************************************

  public static final String DATA_CHANGED = "Data Changed";
  private static final String TYPE        = "Array";

  //~ Instance fields **********************************************************

  private INewParameterGUI innerParam;
  private ArrayEntryJFrame entryFrame = null;
  private JButton vectorButton        = null;
  private JPanel PanelwButton         = null;
  //~ Constructors *************************************************************

  /**
   * Constructor
   *
   * @param name the prompt string that appears on the  GUI( a button) and the
   *        resultant JFrame when the button is pressed The ParameterGUI is
   *        just a button in a JPanel.  When the button is pressed a more
   *        complicated JFrame is created with the list box and editing
   *        buttons.
   * @param val The initial value to set this VectorPG to.
   */
  public VectorPG( String name, Object val ) {
    super( name, val );
  }

 
  //~ Methods ******************************************************************

 



  /**
   * Used to clear out the PG.  This resets the GUI and clears out the values.
   */
  public void clear(  ) {
    super.setValue( new Vector(  ) );
  }

  /**
   * Method to set the inner ArrayEntryJFrame.
   *
   * @param frame The ArrayEntryJFrame to use.
   */
  protected void setEntryFrame( ArrayEntryJFrame frame ) {
    this.entryFrame = frame;
  }

  /**
   * Sets this VectorPG's parameter.  Also resets the type to more accurately
   * show what this VectorPG is an array of.
   */
  protected final void setParam( INewParameterGUI param ) {
    innerParam = param;
  }

  public String getType(){
	  return innerParam.getType(  ) + TYPE ;
  }
  /**
   * Gets this VectorPG's parameter.
   */
  protected final INewParameterGUI getParam(  ) {
    return innerParam;
  }

 
  //------------------------- Interface methods------------------
 

  /**
   * Returns the bottom level GUI component in a JPanel.  If it does not 
   * exist it will be created.
   * 
   * @return The GUI panel containing the parameterGUI.
   */
  public JPanel getWidget(){
	    if( PanelwButton != null)
	    	return PanelwButton;

	   
	    entryFrame = null;

	    
	    Object VV= vec_value; //Get Value before the ArrayEntryJFrame is created
	                           //  because getValue gets the value 1st from the 
	                           //  EntryJFrame
	    entryFrame = new ArrayEntryJFrame( innerParam );
	    entryFrame.addPropertyChangeListener( DATA_CHANGED, this );
	    entryFrame.setValue( VV );
	    vectorButton = new JButton( innerParam.getName(  ) +" Array");
	    vectorButton.addActionListener( entryFrame );
	    PanelwButton = new JPanel( new GridLayout( 1,2));
	    PanelwButton.add( new JLabel("Press the button"));
	    PanelwButton.add(vectorButton);
	    return PanelwButton;
  }


  /**
   * Set internal references to the lower level GUI entry widget to
   * null so that it can be garbage collected.  Subsequent calls to 
   * getWidget() will create a new widget.
   */
  public void destroyWidget(){
	  entryFrame=null;
	  vectorButton = null;
	  PanelwButton= null;
	  innerParam.clear();
	  innerParam.destroyGUIPanel();
  }

  /**
   * Retrieves the GUI's current value.  IF the value in the widget does
   * NOT refer to a Vector then the implementing method should
   * throw an IllegalArgumentException.
   * 
   * @return The value of the GUI.
   *
   * @throws IllegalArgumentException is called if this is called without
   *         a GUI widget being present, or if the value in the widget doesn't 
   *         refer to a Vector.
   */
  public Vector getWidgetValue() throws IllegalArgumentException{
	  Vector Res = entryFrame.getValues();
	  return getVectorValue( Res );
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
  public void setWidgetValue( Vector value ) 
                                          throws IllegalArgumentException{
	  Vector  V = getVectorValue( value);
	  
	  entryFrame.setValue( value );
  }

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
  protected abstract Vector getVectorValue( Object obj )
                                          throws IllegalArgumentException;


/**
 * Enable or disable the GUI widget for entering values. 
 *
 * @param  on_off  Set true to enable the widget for user input.
 */
public void setEnabled( boolean on_off ){
	entryFrame.setEnabled( on_off);
	PanelwButton.setEnabled( on_off);
	vectorButton.setEnabled( on_off);
}

}
