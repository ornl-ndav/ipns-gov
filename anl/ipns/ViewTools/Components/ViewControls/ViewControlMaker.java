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
 *  Revision 1.1  2003/06/06 18:49:25  dennis
 *  - Initial Version, used to convert JComponents to ViewControls. Only
 *    components derived from AbstractButton will have listeners.
 *    (Mike Miller)
 *
 *
 */
 
 package DataSetTools.components.View.ViewControls;

 import javax.swing.*;
 import java.awt.event.*;
 
 
/**
 * This class is to quickly convert JComponents to ViewControls. However,
 * only components extending an AbstractButton will have listeners.
 */
public class ViewControlMaker extends ViewControl
{
   private JComponent component;
   private ViewControlMaker this_panel;
   
   public ViewControlMaker(JComponent comp)
   {
      super("");
      component = comp;
      this.add(component);
      if( component instanceof AbstractButton )
         ((AbstractButton)component).addActionListener( new ActionList() );
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
   *  @param  component
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

}
