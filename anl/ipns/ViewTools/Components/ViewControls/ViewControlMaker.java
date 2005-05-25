/*
 * File: ViewControlMaker.java
 *
 * Copyright (C) 2003, Mike Miller
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
 *  Revision 1.10  2005/05/25 20:28:43  dennis
 *  Now calls convenience method WindowShower.show() to show
 *  the window, instead of instantiating a WindowShower object
 *  and adding it to the event queue.
 *
 *  Revision 1.9  2005/03/28 05:57:32  millermi
 *  - Added copy() which will make an exact copy of the ViewControl.
 *
 *  Revision 1.8  2005/03/20 05:37:02  millermi
 *  - Modified main() to reflect parameter changes to
 *    ControlManager.makeManagerTestWindow().
 *
 *  Revision 1.7  2005/03/09 22:36:10  millermi
 *  - Added methods get/setControlValue() and messaging of VALUE_CHANGED
 *    to enable controls to be linked.
 *  - Added "cm" as parameter to main() to test control with the
 *    ControlManager.
 *
 *  Revision 1.6  2004/03/12 02:36:16  millermi
 *  - Changed package, fixed imports.
 *
 *  Revision 1.5  2004/01/29 08:20:45  millermi
 *  - Now implements IPreserveState, thus state can now be saved for
 *    all ViewControls. Each control is responsible for detailed
 *    state information.
 *
 *  Revision 1.4  2004/01/05 18:14:06  millermi
 *  - Replaced show()/setVisible(true) with WindowShower.
 *  - Removed excess imports.
 *
 *  Revision 1.3  2003/10/16 05:00:15  millermi
 *  - Fixed java docs errors.
 *
 *  Revision 1.2  2003/08/06 13:53:16  dennis
 *  - Added functionality for JComboBox.(Mike Miller)
 *
 *  Revision 1.1  2003/06/06 18:49:25  dennis
 *  - Initial Version, used to convert JComponents to ViewControls. Only
 *    components derived from AbstractButton will have listeners.
 *    (Mike Miller)
 *
 *
 */
 
 package gov.anl.ipns.ViewTools.Components.ViewControls;

 import javax.swing.JComponent;
 import javax.swing.JFrame;
 import javax.swing.JComboBox;
 import javax.swing.JButton;
 import javax.swing.AbstractButton;
 import java.awt.event.ActionEvent;
 import java.awt.event.ActionListener;
 
 import gov.anl.ipns.Util.Sys.WindowShower;
 
/**
 * This class is to quickly convert JComponents to ViewControls. However,
 * only components extending an AbstractButton or JComboBox will have listeners.
 * Because of the generality of this class, the setControlValue() and
 * getControlValue() methods are unimplemented. Also, VALUE_CHANGED is not
 * sent out when a value is changed.
 */
public class ViewControlMaker extends ViewControl
{
  private JComponent component;
  private ViewControlMaker this_panel;
  private boolean ignore_change = false;
  private boolean button_pressed = false;
  
 /**
  * Contructor - builds a ViewControl out of the JComponent
  *
  *  @param  comp JComponent of the ViewControl
  */ 
  public ViewControlMaker(JComponent comp)
  {
    super("");
    component = comp;
    this.add(component);
    if( component instanceof AbstractButton )
      ((AbstractButton)component).addActionListener( new ActionList() );
    else if ( component instanceof JComboBox )
      ((JComboBox)component).addActionListener( new ActionList() );
    this_panel = this;     
  }
     
 /**
  * Get component of the view control.
  *
  *  @return component
  */
  public JComponent getComponent()
  {
    return component;
  }
  
 /**
  * This method is just a stub, since multiple JComponents could be passed
  * in and there is no way to standardize the input.
  *
  *  @param  value Value unimportant.
  */
  public void setControlValue(Object value)
  {
    // If no value, do nothing.
    if( value == null )
      return;
    if( component instanceof AbstractButton )
    {
      // Make sure value is valid.
      if( !(value instanceof Boolean) )
        return; 
      // Do nothing if button was not pressed.
      if( !((Boolean)value).booleanValue() )
        return;
      ignore_change = true;
      ((AbstractButton)component).doClick();
      ignore_change = false;
    }
    else if ( component instanceof JComboBox )
    {
      // Make sure Integer is passed in.
      if( !(value instanceof Integer) )
        return;
      int int_value = ((Integer)value).intValue();
      // Do nothing if int_value is not a valid index. If -1, then none are
      // selected.
      if( int_value >= ((JComboBox)component).getItemCount() || int_value < -1 )
        return;
      ignore_change = true;
      ((JComboBox)component).setSelectedIndex(int_value);
      ignore_change = false;
    }
  }
  
 /**
  * This method is just a stub, since multiple JComponents could be passed
  * in and there is no way to standardize the output.
  *
  *  @return null.
  */
  public Object getControlValue()
  {
    if( component instanceof AbstractButton )
    {
      return new Boolean(button_pressed);
    }
    else if ( component instanceof JComboBox )
    {
      return new Integer(((JComboBox)component).getSelectedIndex());
    }
    // else return null
    return null;
  }
  
 /**
  * This method is not supported by the ViewControlMaker. Because this class
  * only knows about a component, it cannot make a copy of that component.
  * This method will still return a ViewControlMaker, but it will display
  * the message "ViewControlMaker.copy() unsupported".
  *
  *  @return Does not return an exact copy, only an instance. 
  */
  public ViewControl copy()
  {
    return new ViewControlMaker( 
             new javax.swing.JLabel("ViewControlMaker.copy() unsupported") );
  }
  
  private class ActionList implements ActionListener
  {
    public void actionPerformed( ActionEvent e )
    {
      //System.out.println("ActionCommand: " + e.getActionCommand() );
      this_panel.send_message( e.getActionCommand() );
      // This if statement will prevent VALUE_CHANGED to be sent out when
      // the setControlValue() method is called.
      if( !ignore_change )
      {
        button_pressed = true;
        send_message(VALUE_CHANGED);
	button_pressed = false;
      }
    }
  }  
 
 /*
  *  For testing purposes only
  */
  public static void main(String[] args)
  {
    // If cm is passed in, test with control manager.
    if( args.length > 0 && args[0].equalsIgnoreCase("cm") )
    {
      String[] alist = {"A1","A2","A3"};
      String[] blist = {"B1","B2","B3"};
      JComboBox cbox1 = new JComboBox(alist);
      JComboBox cbox2 = new JComboBox(blist);

      ViewControl[] controls = new ViewControl[4];
      controls[0] = new ViewControlMaker(cbox1);
      controls[0].setTitle("Combobox1");
      controls[0].setSharedKey("Combobox");
      controls[1] = new ViewControlMaker(cbox2);
      controls[1].setTitle("Combobox2");
      controls[1].setSharedKey("Combobox");
      controls[2] = new ViewControlMaker(new JButton("Button1"));
      controls[2].setTitle("Button1");
      controls[2].setSharedKey("Button");
      controls[3] = new ViewControlMaker(new JButton("Button2"));
      controls[3].setTitle("Button2");
      controls[3].setSharedKey("Button");
    
      JFrame frame = ControlManager.makeManagerTestWindow( controls );
      WindowShower.show(frame);
      return;
    }
    String[] list = {"1","2","3"};
    ViewControlMaker jcb = new ViewControlMaker( new JComboBox(list) );
    JFrame frame = new JFrame("JComboBox Test");
    frame.setBounds(0,0,200,200);
    frame.getContentPane().add(jcb);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    WindowShower.show(frame);
  }

}
