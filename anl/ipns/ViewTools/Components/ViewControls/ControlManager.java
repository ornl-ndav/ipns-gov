/*
 * File: ControlManager.java
 *
 * Copyright (C) 2005, Mike Miller
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
 * Primary   Mike Miller <millermi@uwstout.edu>
 * Contact:  Student Developer, University of Wisconsin-Stout
 *           
 * Contact : Dennis Mikkelson <mikkelsond@uwstout.edu>
 *           Department of Mathematics, Statistics and Computer Science
 *           University of Wisconsin-Stout
 *           Menomonie, WI 54751, USA
 *
 * This work was supported by the National Science Foundation under grant
 * number DMR-0218882, and by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 * Modified:
 *
 *  $Log$
 *  Revision 1.4  2005/03/28 05:57:32  millermi
 *  - Added copy() which will make an exact copy of the ViewControl.
 *
 *  Revision 1.3  2005/03/20 19:53:24  millermi
 *  - Added state method getInstance() which now makes the ControlManager
 *    a Singleton class, only one instance can exist, unless a class
 *    is within the ViewControls package.
 *  - Made constructor protected, so it can only be used for testing
 *    by ViewControls.
 *
 *  Revision 1.2  2005/03/20 05:31:42  millermi
 *  - Changed registerControl(key,control) to registerControl(control)
 *    since ViewControls now have key contained within them.
 *  - Removed key parameter from makeManagerTestWindow() since key
 *    is now contained within ViewControls.
 *
 *  Revision 1.1  2005/03/09 22:31:25  millermi
 *  - Initial Version - Allows the linking or sharing of ViewControls.
 *
 */
 package gov.anl.ipns.ViewTools.Components.ViewControls;
 
 import java.awt.Component;
 import java.awt.GridLayout;
 import java.awt.event.ActionEvent;
 import java.awt.event.ActionListener;
 import java.util.Enumeration;
 import java.util.Hashtable;
 import java.util.Vector;
 import javax.swing.JFrame;
 import javax.swing.JLabel;
 import javax.swing.JPanel;
 
 import gov.anl.ipns.Util.Sys.SharedMessages;
 import gov.anl.ipns.Util.Sys.WindowShower;
/**
 * This class allows control values for similar controls to be shared.
 * Controls are registered under a category key. All controls under a
 * key maintain a consistent value, when one is altered, all are altered.
 * This class has a graphical display which consists of a copy of the first
 * control added under each key. The viewer may chose not to use this display
 * if displaying all shared controls is desired. Because this class is intended
 * to be a singleton, use getInstance() to get the control manager. No other
 * constructors are provided.
 */
 public class ControlManager
 {
   // This will allow only one instance of the ControlManager to exist.
   private static ControlManager this_manager = null;
   // Registry of all category keys and associated controls list.
   private Hashtable ctrl_table;
   private JPanel shared_ctrls_ui;
   private JLabel no_ctrls = new JLabel("No controls to display.");
   
  /**
   * This method will prevent multiple instances of the ControlManager from
   * existing. If one exists, return the reference to it. Otherwise create a
   * new instance.
   *
   *  @return A single instance of the ControlManager.
   */
   public static ControlManager getInstance()
   {
     // Allow only one instance of ControlManager to exist.
     if( this_manager == null )
       this_manager = new ControlManager();
     return this_manager;
   }
   
  /*
   * Constructor - initializes control registry. Prevent anyone from making
   * an instance of the ControlManager without using getInstance(). This
   * forces a single instance of the ControlManager.
   */
   private ControlManager()
   {
     ctrl_table = new Hashtable();
     // Force only one column of controls.
     shared_ctrls_ui = new JPanel( new GridLayout(0,1) );
     shared_ctrls_ui.add( no_ctrls );
   }
   
  /**
   * This method will register a control with the ControlManager. Upon
   * registering, the newly registered control value will be set to match
   * any controls also registered under that key. All controls registered
   * under the same key will be linked together, when one changes, all change.
   * If a control has already been registered, it will not be registered
   * a second time.
   *
   *  @param  vc  The control that is being shared and linked to similar
   *              controls.
   *  @return Returns true if registration is successful,
   *                  false if not successful.
   */ 
   public boolean registerControl( ViewControl vc )
   {
     // Check for invalid control or invalid key.
     if( vc == null || vc.getSharedKey() == null )
       return false;
     String key = vc.getSharedKey();
     Vector category = (Vector)ctrl_table.get(key);
     // If key does not yet exist, create a new category vector for this key.
     if( category == null )
     {
       // Make copy of control and add it to the shared_ctrls_ui panel
       // for graphical display. Also add it to the table. This copy will
       // be used for graphical applications since Java will not allow
       // one instance to be displayed twice.
       ViewControl clone = vc.copy();
       category = new Vector();
       category.add(clone);
       category.add(vc);
       // Add listener to control to monitor when values are changed.
       clone.addActionListener( new ControlListener() );
       vc.addActionListener( new ControlListener() );
       ctrl_table.put( key, category );
       // If first control to be added, remove the "No controls to display"
       // label that was inserted upon creation.
       if( shared_ctrls_ui.getComponentCount() == 1 )
         shared_ctrls_ui.remove(no_ctrls);
       // Add copy of new control.
       shared_ctrls_ui.add(clone);
       shared_ctrls_ui.validate();
       shared_ctrls_ui.repaint();
       // Tell outside programs that registration was successful.
       return true;
     }
     // Else key exists. Now check to see if control is consistent with
     // existing controls.
     Object value1 = vc.getControlValue();
     Object value2 = ((ViewControl)category.elementAt(0)).getControlValue();
     // Check to see if values are null.
     // Make sure value that is returned is consistent with values that
     // existing controls return.
     if( value1 != null &&
         !value1.getClass().getName().equals(value2.getClass().getName()) )
     {
       SharedMessages.addmsg("ERROR in ControlManager.registerControl(). "+
                          "Unable to register "+vc.getTitle()+" because "+
			  "other controls registered under ["+key+"] do not "+
			  "accept "+value2.getClass().getName()+" as a value. "+
			  "IViewControl.getControlValue() must return "+
			  value1.getClass().getName()+"." );
       return false;
     }
     // If vc is already registered, do nothing.
     if( category.contains(vc) )
       return false;
     // Since controls are now going to be linked, ensure that the new control
     // is consistent with the existing controls.
     vc.setControlValue(value2);
     // Add listener to control to monitor when values are changed.
     vc.addActionListener( new ControlListener() );
     // Add control to the category registry.
     category.add(vc);
     return true;
   }
  
  /**
   * This method will unregister a previously registered control. The net
   * effect is that vc will no longer be shared or associated with other
   * controls.
   *
   *  @param  vc The ViewControl being registered.
   *  @return True if found and unregistered, false if not found.
   */ 
   public boolean unregisterControl( ViewControl vc )
   {
     // Make sure vc is valid.
     if( vc == null )
       return false;
     Enumeration keys = ctrl_table.keys();
     boolean foundflag = false;
     boolean temp = false;
     Object current_key;
     Vector current_element;
     boolean gui_ctrl_found = false;
     // Go through all Vectors and see if control is registered.
     while( keys.hasMoreElements() )
     {
       current_key = keys.nextElement();
       // Get category vector corresponding to the current key.
       current_element = (Vector)ctrl_table.get(current_key);
       // Try to remove control from current vector. "temp" will keep track
       // whether the control is removed or not for each key.
       temp = current_element.remove(vc);
       // If current vector contains only one control, that is the cloned
       // control, so remove it from the hashtable.
       if( current_element.size() == 1 )
       {
         ViewControl copy = (ViewControl)current_element.elementAt(0);
	 // Remove control from shared controls JPanel.
	 if( copy != null )
	 {
	   shared_ctrls_ui.remove(copy);
	   gui_ctrl_found = true;
         }
	 // Remove from the "invisible" registry table.
	 ctrl_table.remove(current_key);
       }
       // Since temp is always changing, use foundflag to "remember" if 
       // control is found.
       if( temp == true )
         foundflag = true;
     }
     // If a control displayed be the panel was found, repaint the panel.
     if( gui_ctrl_found )
     {
       // If no shared controls exist, readd the "No controls to display" label.
       if( shared_ctrls_ui.getComponentCount() == 0 )
         shared_ctrls_ui.add( no_ctrls );
       shared_ctrls_ui.validate();
       shared_ctrls_ui.repaint();
     }
     return foundflag;
   }
  
  /**
   * Get the number of controls registered under the specified key. If key
   * is not found, -1 is returned.
   *
   *  @param  key The category that the controls are grouped under.
   *  @return The number of controls associated with the specified key. If
   *          the key is not found, -1 is returned.
   */ 
   public int getControlCount( String key )
   {
     Vector category = (Vector)ctrl_table.get(key);
     // If key not found, return -1.
     if( category == null )
       return -1;
     // Else, return number of controls in vector.
     return category.size();
   }
  
  /**
   * Get the number of keys (categories) registered with the ControlManager.
   *
   *  @return Number of registered keys.
   */ 
   public int getKeyCount()
   {
     return ctrl_table.size();
   }
   
  /**
   * Get the list of registered keys. The array of keys is NOT arranged in
   * a certain order, do not use in order-dependent operations.
   *
   *  @return Returns the list of registered keys.
   */
   public String[] getRegisteredKeys()
   {
     Enumeration keys = ctrl_table.keys();
     String[] key_list = new String[ctrl_table.size()];
     int count = 0;
     while( keys.hasMoreElements() )
       key_list[count++] = (String)keys.nextElement();
     return key_list;
   }
   
  /**
   * Get a graphical collection of shared controls displayed in a single
   * panel. As controls are registered and unregistered, the panel adds
   * and removes expired controls. All controls displayed are copies,
   * allowing for multiple display instances of the "same" control. Only a
   * copy of the first registered control for each key is displayed by
   * this panel.
   *
   *  @return Graphical collection of shared controls displayed in a single
   *          column.
   */
   public JPanel getSharedControlsUI()
   {
     return shared_ctrls_ui;
   }
   
  /*
   * This class will ensure that controls registered under a common key
   * are kept consistent.
   */ 
   private class ControlListener implements ActionListener
   {
     public void actionPerformed( ActionEvent ae )
     {
       // Only do something if message from IViewControl is VALUE_CHANGED
       if( !ae.getActionCommand().equals(IViewControl.VALUE_CHANGED) )
         return;
       ViewControl vc = (ViewControl)ae.getSource();
       Object value = vc.getControlValue();
       Enumeration elements = ctrl_table.elements();
       // Using the foundflag assumes the control will only be registered
       // under one key.
       // while( elements.hasMoreElements() && !foundflag )
       boolean foundflag = false;
       Vector temp;
       while( elements.hasMoreElements() )
       {
         temp = (Vector)elements.nextElement();
         foundflag = temp.contains(vc);
	 // If vector contains control, set value in all other associated
	 // controls.
	 if( foundflag )
	 {
	   int num_ctrls = temp.size();
	   for( int i = 0; i < num_ctrls; i++ )
	   {
	     // Only set value if message did not come from this control.
	     if( vc != temp.elementAt(i) )
	       ((ViewControl)temp.elementAt(i)).setControlValue(value);
	     //else
	     //  System.out.println("Same Control ["+i+"]");
	   }
	 }
       }
     }
   } // End of ControlListener.
   
  /**
   * This method provides a window to test the linking of ViewControls.
   * Use this in the testing of ViewControl.setControlValue() and
   * ViewControl.setControlValue().
   *
   *  @param  controls - ViewControls to be displayed in the JFrame.
   *  @param  keys - The String keys that each control is registered under.
   *  @return A JFrame containing all of the controls plus a ControlCheckbox
   *          to toggle linking of controls.
   */ 
   public static JFrame makeManagerTestWindow( ViewControl[] controls )
   {
     final ViewControl[] final_controls = controls;
     final ControlManager cm = new ControlManager();
     ControlCheckbox registerControl = new ControlCheckbox("Link Controls");
     // Add listener that will register and unregister controls.
     registerControl.addActionListener( new ActionListener(){
         public void actionPerformed( ActionEvent ae )
         {
	   if( ae.getActionCommand().equals(ControlCheckbox.CHECKBOX_CHANGED) )
	   {
	     if( ((ControlCheckbox)ae.getSource()).isSelected() )
	     {
	       for( int i = 0; i < final_controls.length; i++ )
                 cm.registerControl(final_controls[i]);
	       System.out.println("********Controls Registered********");
	       // Display all registered keys.
	       String[] reg_keys = cm.getRegisteredKeys();
	       for( int i = 0; i < reg_keys.length; i++ )
	         System.out.println("Key "+i+": "+reg_keys[i] );
	       System.out.println();
	     }
	     else
	     {
	       for( int i = 0; i < final_controls.length; i++ )
                 cm.unregisterControl(final_controls[i]);
	       System.out.println("*******Controls Unregistered*******");
	       // Display all registered keys.
	       String[] reg_keys = cm.getRegisteredKeys();
	       for( int i = 0; i < reg_keys.length; i++ )
	         System.out.println("Key "+i+": "+reg_keys[i] );
	       System.out.println();
	     }
	   }
         }
       } );
     
     // Build GUI
     final JFrame frame = new JFrame("ControlManager Test");
     frame.getContentPane().setLayout( new GridLayout(0,2) ); // Two columns
     // Allow 50 pixels for each control
     frame.setBounds(0,0,250,50*final_controls.length);
     // Add controls to frame.
     for( int i = 0; i < final_controls.length; i++ )
       frame.getContentPane().add(final_controls[i]);
     frame.getContentPane().add(registerControl); // add registerControl at end.
     frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
     
     // This will register the controls for the first time.
     registerControl.doClick();
     return frame;
   }
   
  /**
   * For testing purposes only...
   *
   *  @param  args Input parameters are ignored.
   */
   public static void main( String[] args )
   {
     final ControlManager cm = new ControlManager();
     final ControlSlider cs1 = new ControlSlider();
     cs1.setTitle("Slider 1");
     cs1.setSharedKey("Slider");
     final ControlSlider cs2 = new ControlSlider();
     cs2.setTitle("Slider 2");
     cs2.setValue(20f);
     cs2.setSharedKey("Slider");
     final ControlCheckbox cc1 = new ControlCheckbox(true);
     cc1.setTitle("Checkbox 1");
     cc1.setSharedKey("Checkbox");
     final ControlCheckbox cc2 = new ControlCheckbox();
     cc2.setTitle("Checkbox 2");
     cc2.setSharedKey("Checkbox");
     
     ControlCheckbox registerControl = new ControlCheckbox("Link Controls");
     // Add listener that will register and unregister controls.
     registerControl.addActionListener( new ActionListener(){
         public void actionPerformed( ActionEvent ae )
         {
           if( ae.getActionCommand().equals(ControlCheckbox.CHECKBOX_CHANGED) )
	   {
	     if( ((ControlCheckbox)ae.getSource()).isSelected() )
	     {
	       // cs1 is reregistered. Testing to make sure it is
	       // not added redundantly.
               cm.registerControl(cs1);
               cm.registerControl(cs2);
               cm.registerControl(cc1);
               cm.registerControl(cc2);
	       System.out.println("Number of Registered Sliders: "+
	                          cm.getControlCount("Slider") );
	       System.out.println("Number of Registered Checkboxes: "+
	                          cm.getControlCount("Checkbox") );
	       // Display all registered keys.
	       String[] keys = cm.getRegisteredKeys();
	       for( int i = 0; i < keys.length; i++ )
	         System.out.println("Key "+i+": "+keys[i] );
	     }
	     else
	     {
	       cm.unregisterControl(cs1);
	       cm.unregisterControl(cs2);
	       cm.unregisterControl(cc1);
	       cm.unregisterControl(cc2);
	       System.out.println("Number of Registered Sliders: "+
	                          cm.getControlCount("Slider") );
	       System.out.println("Number of Registered Checkboxes: "+
	                          cm.getControlCount("Checkbox") );
	       // Display all registered keys.
	       String[] keys = cm.getRegisteredKeys();
	       for( int i = 0; i < keys.length; i++ )
	         System.out.println("Key "+i+": "+keys[i] );
	     }
	   }
         }
       } );
     // This will register the controls for the first time.
     registerControl.doClick();
     /*
     System.out.println("Trying to register Checkbox 1 as a slider...");
     // This should fail since sliders accept floats, checkboxes accept boolean.
     cm.registerControl("Slider",cc1);
     */
     JFrame frame = new JFrame("ControlManager Test");
     frame.getContentPane().setLayout( new GridLayout(3,2) );
     frame.setBounds(0,0,200,200);
     frame.getContentPane().add(cs1);
     frame.getContentPane().add(cs2);
     frame.getContentPane().add(cc1);
     frame.getContentPane().add(cc2);
     frame.getContentPane().add(registerControl);
     frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
     
     // Build frame for displaying the shared controls gui.
     JFrame frame2 = new JFrame("Shared Controls");
     frame2.getContentPane().add(cm.getSharedControlsUI());
     // Allow 50 pixels for each control
     frame2.setBounds(200,0,200,200);
     
     WindowShower shower = new WindowShower(frame);
     java.awt.EventQueue.invokeLater(shower);
     
     shower = new WindowShower(frame2);
     java.awt.EventQueue.invokeLater(shower);
     shower = null;
   }
 } // End of ControlManager
