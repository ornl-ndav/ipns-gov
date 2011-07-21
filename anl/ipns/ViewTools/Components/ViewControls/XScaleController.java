/*
 * File: XScaleController.java
 *
 * Copyright (C) 2005, Chad Jones
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
 * Primary   Chad Jones <cjones@cs.utk.edu>
 * Contact:  Student Developer, University of Tennessee
 *
 * Contact : Dennis Mikkelson <mikkelsond@uwstout.edu>
 *           Department of Mathematics, Statistics and Computer Science
 *           University of Wisconsin-Stout
 *           Menomonie, WI 54751, USA
 *
 * This work was supported by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 * Modified:
 *
 *  $Log$
 *  Revision 1.1  2005/07/22 19:46:23  cjones
 *  Cleaned up code. Added a XScaleController that uses AnimationController to
 *  handle frames in a scene.
 *
 */

package gov.anl.ipns.ViewTools.Components.ViewControls;

import java.io.*;
import java.util.Vector;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import DataSetTools.components.ui.XScaleChooserUI;
import DataSetTools.dataset.UniformXScale;
import DataSetTools.dataset.XScale;
import gov.anl.ipns.ViewTools.UI.AnimationController;

/**
 * A XScaleController object is a GUI component for controlling a sequence
 * of displayed images.  It includes buttons to start, stop and single step
 * the frames and includes a textual display that gives the current frame 
 * value and allows the user to advance to a specified frame value.  An array
 * of float values that map the frame numbers to some physically meaningful
 * values can also be provided.  If an array of frame values is provided, the
 * frame numbers will be restricted to the range of indices for the array.
 * 
 * 
 * When the frame is changed by a user using the AnimationController, the action 
 * message  XSCALE_CHANGED is sent to all listeners.
 * 
 * @see gov.anl.ipns.ViewTools.UI.AnimationController 
 */

public class XScaleController extends    ViewControl 
                                 implements Serializable,  ActionListener 
{
  /**
    * 
    */
   private static final long serialVersionUID = 1L;
   private XScaleChooserUI xscale_ui;
  public static String  XSCALE_CHANGED =" XSCALE_CHANGED";
  private String      units_label  = "";
  private String      border_label ="";
 
 /* ------------------------------ CONSTRUCTOR ---------------------------- */
 /** 
  *  Construct an XScaleController with no associated values, blank 
  *  border title, blank text label .  
  */
  public XScaleController(String border_label, String units_label, float x_min, float x_max, int n_steps)  
  {
  	super("XScale Chooser");
  	xscale_ui = new XScaleChooserUI(border_label, units_label,  x_min,  x_max,  n_steps );
  	xscale_ui.addActionListener(  this );
  	this.units_label= units_label;
  	this.border_label = border_label;
    add(xscale_ui);
  }

 /* -------------- View Control ----------------------------------------- */
  /**
   * Set value associated with this control.
   *
   * @param  value The XScale to be associated with the  this Control
   */
   public void setControlValue(Object value)
   {
    
   	   setControlValue( value, true );
   }
   
   /**
    * Set value associated with this control.
    *
    *  @param  value The XScale to be associated with the  this Control
    *  
    *          
    *  @param  notify  if true, all XScaleController listeners will be notified, otherwise
    *                  they will not be notified.  This should be false when set from another
    *                  source besides the underlying gui.
    */
   public void setControlValue( Object value, boolean notify)
   {
      if( value == null)
         return;
      if( !(value instanceof XScale))
         return;
      XScale xscl = (XScale) value;
      if( !notify)
         xscale_ui.removeActionListener(  this );
      xscale_ui.set(units_label,xscl.getStart_x( ), xscl.getEnd_x( ), xscl.getNum_x( ));
      xscale_ui.addActionListener(  this  );
      if( notify)
         send_message( XSCALE_CHANGED );
   }
   
  /**
   * Get value associated with this control that will change and need to be
   * updated.
   *
   *  @return Value associated with the current frame.
   */
   public Object getControlValue()
   {
     return xscale_ui.getXScale( );
   }
   
  /**
   * This method will make an exact copy of the control.
   *
   *  @return A new, identical instance of the control.
   */
   public ViewControl copy()
   {
      XScale xscl = (XScale)getControlValue();
   	  XScaleController control_copy = new XScaleController(border_label,units_label, 
   	                xscl.getStart_x( ), xscl.getEnd_x( ), xscl.getNum_x( ));
   	 return control_copy;
   }
  
  
  
 
  /**
   * Should only come from animator  when frame number is changed
   * 
   * @param evt  The action event
   */
  public void actionPerformed( ActionEvent evt)
  {
    
     if( evt.getSource() .equals(  xscale_ui ))
        super.send_message(  XSCALE_CHANGED);
     
  
  }
  
 

/* -------------------------------------------------------------------------
 *
 * MAIN  ( Basic main program for testing purposes only. )
 *
 */
    public static void main(String[] args)
    {
      JFrame f = new JFrame("Test for XScale Controller");
      f.setBounds(0,0,200,150);
      final XScaleController control  = new XScaleController("Test","sec",1000,18000,100);

      f.getContentPane().setLayout( new GridLayout(1,1) );
      f.getContentPane().add(control);

      XScale xscl = new UniformXScale( 800,1200,6);
     
      control.addActionListener( new ActionListener()
       {
         public void actionPerformed(ActionEvent e)
         {
           String action = e.getActionCommand();
           System.out.println("In Main, command = " + action );
           XScale xscl= (XScale)control.getControlValue( );
           System.out.println( "Val="+xscl.getStart_x( )+","+xscl.getEnd_x( )+","+xscl.getNum_x( ));
         }
       });

      control.setControlValue( xscl, false );
      f.setVisible(true);
    }
}
