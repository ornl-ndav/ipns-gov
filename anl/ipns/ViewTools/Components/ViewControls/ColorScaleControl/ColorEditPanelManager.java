/* 
 * File: ColorEditPanelManager.java
 *
 * Copyright (C) 2008, Dennis Mikkelson
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
 * Contact : Dennis Mikkelson <mikkelsond@uwstout.edu>
 *           Department of Mathematics, Statistics and Computer Science
 *           University of Wisconsin-Stout
 *           Menomonie, WI 54751, USA
 *
 *  Last Modified:
 * 
 *  $Author$
 *  $Date$            
 *  $Revision$
 */


package gov.anl.ipns.ViewTools.Components.ViewControls.ColorScaleControl;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.border.TitledBorder;

import gov.anl.ipns.ViewTools.Components.ViewControls.*;
import gov.anl.ipns.ViewTools.Panels.Image.*;
import gov.anl.ipns.Util.Sys.*;

/**
 *  This class coordinates the color scale editor and an 
 *  ImageJPanel2 object.  When the user clicks the Update button
 *  on the editor, the corresponding color scale information will
 *  be passed on to the ImageJPanel2.
 */
public class ColorEditPanelManager extends ViewControl 
{
  ImageJPanel2    ijp;
  ColorEditPanel  color_editor;
  JFrame          frame;

  public static final String COLOR_SCALE_CHANGED = "Color Scale Changed";  

  public ColorEditPanelManager( String title, ImageJPanel2 ijp )
  {
    super( title );

    this.ijp = ijp;    

    JButton edit_button = new JButton("Edit Color Scale");    
    edit_button.addActionListener( new EditButtonListener() );
    this.setLayout( new GridLayout(1,1) );
    this.add( edit_button ); 

    color_editor = new ColorEditPanel( ijp.getDataMin(), ijp.getDataMax() );
    color_editor.addActionListener( new EditorListener() );
    frame = new JFrame("Color Scale Editor"); 
    frame.add( color_editor );
    frame.setBounds( 0, 0, 400, 500 );
    frame.setDefaultCloseOperation( WindowConstants.HIDE_ON_CLOSE );
  } 

  @Override
  public void setControlValue(Object value)
  {
  }

  @Override
  public Object getControlValue()
  {
    return null;
  }

  @Override
  public ViewControl copy()
  {
    return this;           // Why is the copy method required? 
                           // Is it OK to return a "shallow" copy?
  }


  /**
   *  Get the ColorEditPanel information and pass it on to the image 
   *  display.
   */
  private void doUpdate()
  {
    ColorScaleInfo info = null;

    Object value = color_editor.getControlValue();

    if ( value instanceof ColorScaleInfo )
      info = (ColorScaleInfo)value;
    else
    {
      SharedMessages.addmsg("Value NOT ColorScaleInfo in " +
                            " ColorEditPanelManager, class is: " +
                              value.getClass() );
      return;
    }

    float   min        = info.getTableMin(); 
    float   max        = info.getTableMax(); 
    float   prescale   = info.getPrescale();

    String  cs_name    = info.getColorScaleName();
    boolean two_sided  = info.isTwoSided();
    int     num_colors = info.getNumColors(); 

    byte[]  table      = info.getColorIndexTable();
    boolean isLog      = info.isLog();

    ijp.setNamedColorModel( cs_name, two_sided, num_colors, false );

    ijp.changeColorIndexTable( table,
                               isLog,
                               min,
                               max,
                               true );
  }


  /**
   *  Listener class to pop up the ColorEditPanel window when the user
   *  presses the Edit button.
   */
  public class EditButtonListener implements ActionListener
  {
     public void actionPerformed( ActionEvent ae )
     {
        send_message( COLOR_SCALE_CHANGED );
        frame.setVisible( true );
     }
  } 


  /**
   *  Listener class to pop up do the update of the image from the 
   *  color edit panel information and possibly close the ColorEditPanel
   *  window if the user presses the Cancel or Done buttons.
   */
  public class EditorListener implements ActionListener
  {
     public void actionPerformed( ActionEvent ae )
     {
       String message = ae.getActionCommand();

       if ( message.equals( ColorEditPanel.doneMessage ) )
       {
         doUpdate();
         frame.setVisible(false);
       }
       else if ( message.equals( ColorEditPanel.updateMessage ) )
         doUpdate();
       else if ( message.equals( ColorEditPanel.cancelMessage ) )
         frame.setVisible(false);
     }
  }


}
