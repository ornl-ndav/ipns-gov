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
 
 package DataSetTools.components.View.ViewControls;

 import javax.swing.JComponent;
 import javax.swing.JFrame;
 import javax.swing.JComboBox;
 import javax.swing.AbstractButton;
 import java.awt.event.ActionEvent;
 import java.awt.event.ActionListener;
 
 import DataSetTools.util.WindowShower;
 
/**
 * This class is to quickly convert JComponents to ViewControls. However,
 * only components extending an AbstractButton or JComboBox will have listeners.
 */
public class ViewControlMaker extends ViewControl
{
  private JComponent component;
  private ViewControlMaker this_panel;
  
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
  * Change component to a new JComponent.
  *
  *  @param  c - component
  */ 
  public void changeComponent(JComponent c)
  {
    component = c;
  }
  
  private class ActionList implements ActionListener
  {
    public void actionPerformed( ActionEvent e )
    {
      //System.out.println("ActionCommand: " + e.getActionCommand() );
      this_panel.send_message( e.getActionCommand() );
    }
  }  
 
 /*
  *  For testing purposes only
  */
  public static void main(String[] args)
  { 
    String[] list = {"1","2","3"};
    ViewControlMaker jcb = new ViewControlMaker( new JComboBox(list) );
    JFrame frame = new JFrame("JComboBox Test");
    frame.setBounds(0,0,200,200);
    frame.getContentPane().add(jcb);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    WindowShower shower = new WindowShower(frame);
    java.awt.EventQueue.invokeLater(shower);
  }

}
