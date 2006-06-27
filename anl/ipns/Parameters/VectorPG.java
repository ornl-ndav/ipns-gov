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
 * Revision 1.1  2006/06/27 19:48:52  rmikk
 * abstract superclass for several ParameterGUI's that allow for medium sized
 * lists that can be editted by the ArrayJFrame
 *
 * Revision 1.48  2005/06/10 15:28:37  rmikk
 * Gave a more descriptive label for what is to be entered
 *
 * Revision 1.47  2005/06/07 15:10:37  rmikk
 * Fixed a timing problem with getting values
 *
 * Revision 1.46  2004/03/12 21:13:49  bouzekc
 * Added clear() method.
 *
 * Revision 1.45  2003/12/15 02:44:08  bouzekc
 * Removed unused imports.
 *
 * Revision 1.44  2003/11/25 03:02:33  bouzekc
 * Now only tries to clone the Label if it has been initialized.
 *
 * Revision 1.43  2003/11/23 02:12:18  bouzekc
 * Now properly clones the label.
 *
 * Revision 1.42  2003/11/20 01:45:19  bouzekc
 * Removed addPropertyChangeListener(), removePropertyChangeListener() methods.
 * All requests for these should go through ParameterGUI.  Made getEntryFrame()
 * public so that external Listeners can be added to the internal
 * ArrayJEntryFrame.
 *
 * Revision 1.41  2003/11/19 04:06:54  bouzekc
 * This class is now a JavaBean.  Added code to clone() to copy all
 * PropertyChangeListeners.
 *
 * Revision 1.40  2003/10/11 19:05:47  bouzekc
 * Now implements clone() using reflection.  Changed return type of getParam()
 * from IParameterGUI to ParameterGUI.  Removed unused imports and variables.
 *
 * Revision 1.39  2003/09/23 02:29:02  bouzekc
 * Removed getInnerParam() method as it duplicated another method's
 * functionality.
 *
 * Revision 1.38  2003/09/13 23:29:47  bouzekc
 * Moved calls from setValid(true) to validateSelf().
 *
 * Revision 1.37  2003/09/10 00:18:28  bouzekc
 * Removed space in the setting of type for derived classes.
 *
 * Revision 1.36  2003/09/09 23:56:19  bouzekc
 * Removed a this.type = TYPE in the constructor, as it conflicts with
 * ArrayPG's type.
 *
 * Revision 1.35  2003/09/09 23:00:58  bouzekc
 * Added method to assist derived classes in implementing validateSelf().
 * Added method to retrieve internal parameter.
 *
 * Revision 1.34  2003/09/03 23:35:15  bouzekc
 * Changed direct this.value call to getValue().
 *
 * Revision 1.33  2003/09/03 14:54:04  bouzekc
 * Removed inner ActionListener class (as it is now in ArrayEntryJFrame).
 *
 * Revision 1.32  2003/08/30 20:00:02  bouzekc
 * Added javadoc comments to initGUi to signal that derived classes should
 * use this method in general.
 *
 * Revision 1.31  2003/08/30 19:47:12  bouzekc
 * Now uses ArrayEntryJFrame.
 *
 * Revision 1.30  2003/08/28 03:36:53  bouzekc
 * Made innerParameter private and added a method to set the parameter so that
 * the type can be set more accurately in the derived classes.
 *
 * Revision 1.29  2003/08/28 02:50:53  bouzekc
 * Changed call from addPCLtoVector to super.addPropertyChangeListener().
 *
 * Revision 1.28  2003/08/28 02:31:22  bouzekc
 * Now makes use of ParameterGUI's constructors.  Removed setEnabled() and
 * getType() methods.  Changed instance variable param to innerParam and
 * made it protected.
 *
 * Revision 1.27  2003/08/26 18:26:34  bouzekc
 * Made the internal ArrayEntryJPanel protected so that subclasses can use
 * it.  Removed layout setup for getEntryWidget().
 *
 * Revision 1.26  2003/08/22 20:12:06  bouzekc
 * Modified to work with getEntryWidget().
 *
 * Revision 1.25  2003/08/22 01:25:45  bouzekc
 * Removed erroneous getgetEntryWidget()() method.
 *
 * Revision 1.24  2003/08/19 21:01:54  bouzekc
 * Removed old getEntryWidget() reference from initGUI().
 *
 * Revision 1.23  2003/08/19 18:49:56  rmikk
 * Arrays retain their initial values.
 * An empty vector is returned instead of null
 *
 * Revision 1.22  2003/08/16 02:09:54  bouzekc
 * Now properly adds PropertyChangeListeners which exist before the GUI is
 * created.
 *
 * Revision 1.21  2003/08/15 23:56:21  bouzekc
 * Modified to work with new IParameterGUI and ParameterGUI.
 *
 * Revision 1.20  2003/08/15 03:59:22  bouzekc
 * Removed init() method.  Now relies on init(Vector).
 *
 * Revision 1.19  2003/07/10 18:29:42  bouzekc
 * Replaced deprecated show() with setVisible(boolean).
 *
 * Revision 1.18  2003/07/07 22:40:05  bouzekc
 * Now uses a class constant for the "Data Changed" event,
 * and checks to be sure that the event is correct in
 * propertyChange().
 *
 * Revision 1.17  2003/07/07 21:51:30  bouzekc
 * Reorganized methods according to access privilege.
 *
 * Revision 1.16  2003/07/07 21:42:18  bouzekc
 * Fixed bug where the JDialog wasn't fully listening to
 * window closing events and removed extraneous comments.
 *
 * Revision 1.15  2003/07/01 14:44:15  bouzekc
 * Uses a JDialog again.
 *
 * Revision 1.14  2003/06/30 22:26:33  bouzekc
 * Fixed comment to reflect change in ArrayEntryJPanel.
 *
 * Revision 1.13  2003/06/24 20:23:11  bouzekc
 * Cleaned up import statements.
 *
 * Now implements PropertyChanger.
 *
 * Fixed comments running over 80 columns.
 *
 * Added method comments.
 *
 * Removed inner MJPanel class, now
 * uses ArrayEntryJPanel.
 *
 * Fixed PropertyChangeListener methods to add the listener
 * parameter rather than "this".
 *
 * Split showGUI method into showEntryPanel and makeentryPanel
 * methods.
 *
 * Added hook in actionPerformed to get the ActionEvent name.
 *
 * Renamed MyWindowListener inner class to
 * VectorPGWindowListener and changed access to private.
 *
 * Now hides the data entry window when the "OK" button is clicked.
 *
 * Closing the window now triggers a property change event.
 *
 * Revision 1.12  2003/06/23 22:30:20  bouzekc
 * Reorganization of methods and inner classes into blocks
 * in order to separate functionality out.
 *
 * Revision 1.11  2003/06/23 15:20:56  bouzekc
 * Renamed the buttons to slightly less offensive names.
 *
 * Revision 1.10  2003/06/23 15:00:32  bouzekc
 * Removed testbed and inner ActionListener class.
 *
 * Revision 1.9  2003/06/23 13:53:35  bouzekc
 * Reformatted for consistent indenting.
 *
 * Revision 1.8  2003/06/18 20:36:41  pfpeterson
 * Changed calls for NxNodeUtils.Showw(Object) to
 * DataSetTools.util.StringUtil.toString(Object)
 *
 * Revision 1.7  2003/06/10 14:54:07  pfpeterson
 * Commented out a call that was meant to be a comment.
 *
 * Revision 1.6  2003/06/10 14:42:21  rmikk
 * Now implements ParamUsesString
 * All GUI elements are now created first in init()
 *
 * Revision 1.5  2003/06/09 22:35:52  rmikk
 * Changed JFrames to JDialog's in a JFrame so they work with
 *    the JParametersDialog system
 * The entry widget that gets one element of a Vector is now
 *    in the same window as the JList of values.
 * Reduced the need to click as many buttons to get a list in
 *
 * Revision 1.4  2003/06/06 18:49:44  pfpeterson
 * Made abstract and removed clone method.
 *
 * Revision 1.3  2003/05/25 19:09:16  rmikk
 * -Added more documentation
 * -Revised the Property Change handling
 * -Fixed details to get VectorPG of VectorPG to work
 *
 * Revision 1.2  2003/05/21 20:10:30  pfpeterson
 * Turned MyActionList into a private class so there is not conflicts
 * when compiling the whole package.
 *
 * Revision 1.1  2003/05/21 17:33:39  rmikk
 * -Initial Checkin.  Base for other intermediate length array entries
 *
 */
package gov.anl.ipns.Parameters;




import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;


import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JPanel;


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
	    PanelwButton = new JPanel();
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
}

}
