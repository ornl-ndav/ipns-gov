/*
 * File:  HKLorQ_SelectorUI.java 
 *
 * Copyright (C) 2004, Dennis Mikkelson
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
 * This work was supported by the National Science Foundation under grant
 * number DMR-0218882.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 * Modified:
 *
 * $Log$
 * Revision 1.2  2004/03/03 23:18:34  dennis
 * Added isEnabled() method.
 *
 * Revision 1.1  2004/02/03 23:42:39  dennis
 * Component to use for specifying display and selection modes as
 * HKL or QXYZ.
 *
 */

package DataSetTools.components.ui;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import DataSetTools.util.*;


/**
 *  This class allows the user to select whether Q or HKL units should be
 *  used.
 */

public class HKLorQ_SelectorUI extends    ActiveJPanel
                               implements Serializable
{
  public static final String MODE_CHANGED = "Mode Changed";

  private int HKL_MODE  = SliceSelectorUI.HKL_MODE;
  private int QXYZ_MODE = SliceSelectorUI.QXYZ_MODE;

  private JComboBox selector;
  private int       mode = HKL_MODE;


  /* --------------------------- constructor --------------------------- */
  /**
   *  Construct the HKLorQ_SelectorUI with the default mode of HKL.
   *
   *  @param prefix  String prefix displayed in combo box before HKL or Q
   */
  public HKLorQ_SelectorUI( String prefix )
  {
    setLayout( new GridLayout( 1, 1 ) );

    selector = new JComboBox();
    selector.setFont( FontUtil.LABEL_FONT );

    selector.addItem( prefix + " in HKL" );
    selector.addItem( prefix + " in Q"   );
    add( selector ); 

    selector.addActionListener( new SelectorListener() );
    setMode( HKL_MODE );
  }

  /* --------------------------- setEnabled ----------------------------- */
  /**
   *  Control whether or not the user can adjust the value. 
   *
   *  @param  enabled  pass in true if the control is to be enabled, false
   *                   otherwise.
   */
  public void setEnabled( boolean enabled )
  {
    selector.setEnabled( enabled ); 
  }


  /* --------------------------- isEnabled ----------------------------- */
  /**
   *  Check whether or not the user can adjust the value. 
   *
   *  @return true if the control is enabled, false otherwise.
   */
  public boolean isEnabled()
  {
    return selector.isEnabled();
  }


  /* --------------------------- setMode ----------------------------- */
  /**
   *  Set the displayed option this selector to be HKL or Q
   *
   *  @param  new_mode  The integer code for the new mode, one of 
   *                    SliceSelector.HKL_MODE or SliceSelector.QXYZ_MODE.
   */
  public void setMode( int new_mode )
  { 
    if ( new_mode >= HKL_MODE &&  new_mode <= QXYZ_MODE  ) 
    {
      mode = new_mode;

      if ( mode != selector.getSelectedIndex() )          // update display if 
        selector.setSelectedIndex( mode );                // needed
    }
  }


  /* --------------------------- getMode ----------------------------- */
  /**
   *  Get the displayed option this selector.
   *
   *  @return  The integer code for the mode, one of 
   *           SliceSelector.HKL_MODE or SliceSelector.QXYZ_MODE.
   */
  public int getMode()
  {
    int mode = selector.getSelectedIndex();
    
    if ( mode == 0 )
      return SliceSelectorUI.HKL_MODE;
    else
      return SliceSelectorUI.QXYZ_MODE; 
  }

  
  /* -----------------------------------------------------------------------
   *
   * Private Classes
   *
   */


  /* -------------------------- SelectorListener ------------------------- */
  /*
   *  Listener for the combo box that chooses whether to use HKL or Q.
   */ 
  private class SelectorListener implements ActionListener,
                                            Serializable
  {
     public void actionPerformed( ActionEvent e )
     {
       int new_mode = selector.getSelectedIndex();
       if ( new_mode != mode )
       {
         mode = new_mode;
         send_message( MODE_CHANGED );
       }
     }
  }


  /* ---------------------------- main ----------------------------- */
  /** 
   *  main program for testing purposes.
   */ 
   public static void main( String[] args )
   {
      JFrame f = new JFrame("Test for HKLorQSelectorUI");

      f.setBounds(0,0,200,100);

      // final HKLorQ_SelectorUI selector = new HKLorQ_SelectorUI("Select in ");
      final HKLorQ_SelectorUI selector = new HKLorQ_SelectorUI("Display in ");
      selector.setMode( SliceSelectorUI.QXYZ_MODE );
      selector.setEnabled( true );

      f.getContentPane().setLayout( new GridLayout(1,1) );
      f.getContentPane().add( selector );

      selector.addActionListener( new ActionListener()
       {
         public void actionPerformed(ActionEvent e)
         {
           System.out.println("Selected: " + selector.getMode() );
         }
       });

      f.setVisible(true);
    }

}
