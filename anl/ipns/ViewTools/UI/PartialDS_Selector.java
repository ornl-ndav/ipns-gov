/*
 * File:  CommandObject.java
 *
 * Copyright (C) 2003, Dennis Mikkelson
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
 *  $Log$
 *  Revision 1.2  2003/03/07 00:13:22  dennis
 *  Now uses the GetDataCommand constructor to make the test command
 *  in the main program.
 *
 *  Revision 1.1  2003/03/06 22:05:49  dennis
 *  GUI for specifiying a list of group IDs, time-of-flight
 *  range, rebin factor and attribute mode for a command to
 *  get a DataSet from a remote server.
 *
 */

package DataSetTools.components.ui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import DataSetTools.retriever.*;
import DataSetTools.dataset.*;
import DataSetTools.util.*;
import NetComm.*;

/**
 *  This class provides a GUI for specifying the group IDs, time-of-flight
 *  range, rebin factor and attribute mode values to be used when getting
 *  a DataSet from a RemoteDataServer.  An initial command object giving 
 *  default values for these quantities is passed into the constructor.  
 *  When a new command object is obtained from this GUI, the fields are 
 *  filled out from the command object, as modified by the GUI.  Objects
 *  using this should add themselves as observers of this PartialDS_Selector,
 *  so that they can be notified when the user hits the Apply button.  Only
 *  two messages are sent... PartialDS_Selector.APPLY and 
 *  PartialDS_Selector.EXIT.  Observers of this object must use the 
 *  getCommand() method to get the new command and use it, when the APPLY 
 *  message is sent.
 */

public class PartialDS_Selector
{
  public static final String APPLY = "Apply";
  public static final String EXIT  = "Exit";

  private GetDataCommand command = null;
  private JDialog        dialog;
  private IntListUI      list_ui;
  private TextRangeUI    tof_range;
  private JComboBox      rebin_box;
  private JComboBox      attr_box;
  private IObserverList  observers; 

  private final String rebin_option_list[] = { "No Rebinning",
                                               "Combine 2 Bins",
                                               "Combine 5 Bins",
                                               "Combine 10 Bins",
                                               "Combine 20 Bins",
                                               "Combine 50 Bins",
                                               "Combine 100 Bins",
                                               "Total Counts"     };
  private final int rebin_code[] = { 1,2,5,10,20,50,100,Integer.MAX_VALUE };

  private final String attr_option_list[] = { "Full Attributes",
                                              "Analysis Attributes",
                                              "NO Attributes"      };
  private final int attr_code[] = { Attribute.FULL_ATTRIBUTES,
                                    Attribute.ANALYSIS_ATTRIBUTES,
                                    Attribute.NO_ATTRIBUTES        };

  private boolean debug = false;

  /**
   *  Construct a PartialDS_Selector using initial values from the 
   *  specified command object.
   *
   *  @param command  A command object that provides intial values for the
   *                  group IDs, Time-of-Flight range etc.  In addition,
   *                  all other fields of the specified command object will
   *                  be copied and returned in a new command object obtained
   *                  by the getCommand() method.
   */

  PartialDS_Selector( GetDataCommand command )
  {
    observers = new IObserverList();

    this.command = command;
                              // Now build the dialog box with components for
                              // entering the control parameters.
                                         
    dialog = new JDialog( new JFrame(), command.getFilename(), false );
    dialog.getContentPane().setLayout( new BorderLayout() );
                                                            // function message 
    JLabel message1 = new JLabel("Parameters For Next Update");  
    message1.setFont( FontUtil.BORDER_FONT2 );
    message1.setHorizontalAlignment( JTextField.CENTER );
    message1.setForeground( Color.black );
    dialog.getContentPane().add( message1, BorderLayout.NORTH );
                                                           
                                                          // panel for controls
    JPanel input_panel = new JPanel();
    TitledBorder border =
          new TitledBorder( LineBorder.createBlackLineBorder(),
                            "DataSet #"+command.getDataSetNumber() );
    border.setTitleFont( FontUtil.BORDER_FONT );
    border.setTitleColor( Color.black );
    input_panel.setBorder( border );

    input_panel.setLayout( new GridLayout(4,1) );
                                                          // group ID widget
    list_ui = new IntListUI( "Group IDs ", "1:30,40");
    input_panel.add( list_ui );
                                                          // TOF widget
    tof_range = new TextRangeUI( "", command.getMin_x(), command.getMax_x() );
    tof_range.setFont( FontUtil.LABEL_FONT2 );
    JLabel tof_label = new JLabel("Time-of-Flight");
    tof_label.setForeground( Color.black );
    JPanel tof_panel = new JPanel();
    tof_panel.setLayout( new GridLayout(1,2) );
    tof_panel.add( tof_label );
    tof_panel.add( tof_range );
    input_panel.add( tof_panel );
                                                          // rebin widget
    rebin_box = new JComboBox( rebin_option_list );
    input_panel.add( rebin_box ); 
                                                          // attribute widget 
    attr_box = new JComboBox( attr_option_list );
    input_panel.add( attr_box );
    
    dialog.getContentPane().add( input_panel, BorderLayout.CENTER );

                                                      // apply & exit buttons
    JButton apply_button = new JButton( APPLY );
    JButton exit_button  = new JButton( EXIT );
    JPanel button_panel = new JPanel();
    button_panel.add( apply_button );
    button_panel.add( exit_button );
    dialog.getContentPane().add( button_panel, BorderLayout.SOUTH );

                                                      // add the listeners
    dialog.addWindowListener( new WindowCloser(this) );
    ButtonListener listener = new ButtonListener( this );
    apply_button.addActionListener( listener );
    exit_button.addActionListener( listener );
                                                      // now display it 
    int width  = 260;
    int height = 230;
    dialog.setSize( width, height );
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    dialog.setLocation( (screenSize.width-width)/2, 
                        (screenSize.height-height)/2 );
    dialog.show();
  }


  /**
   *  Close this dialog.
   */
  public void dispose()
  {
    dialog.dispose();
  }


  /**
   *  Get the current command corresponding to the parameters set in the 
   *  controls.
   *
   *  @return a new command object with its fields filled out from the 
   *          original command object, then modified to match the values set
   *          by the contols on this dialog.
   */
  public GetDataCommand getCommand()
  {
    int rebin_index = rebin_box.getSelectedIndex();
    int attr_index  = attr_box.getSelectedIndex();

    GetDataCommand new_command = new GetDataCommand( 
                                        CommandObject.GET_DS,
                                        command.getUsername(),
                                        command.getPassword(),
                                        command.getFilename(),
                                        command.getDataSetNumber(),
                                        list_ui.getValue(),
                                        tof_range.getMin(),
                                        tof_range.getMax(),
                                        rebin_code[rebin_index],
                                        attr_code[attr_index] );
    return new_command;
  }


  /**
   *  Add the specified object to the list of observers to notify when this
   *  observable object changes.
   *
   *  @param  iobs   The observer object that is to be notified.
   *
   */
   void addIObserver( IObserver iobs )
   {
     observers.addIObserver( iobs );
   }


  /**
   *  Remove the specified object from the list of observers to notify when
   *  this observable object changes.
   *
   *  @param  iobs   The observer object that should no longer be notified.
   *
   */
   void deleteIObserver( IObserver iobs )
   {
     observers.deleteIObserver( iobs );
   }


  /**
   *  Remove all objects from the list of observers to notify when this
   *  observable object changes.
   */
   void deleteIObservers( )
   {
     observers.deleteIObservers();
   }


  /* ----------------------------------------------------------------------
   *
   *  PRIVATE EVENT HANDLING CLASSES
   *
   */

   private class ButtonListener implements ActionListener
   {
      PartialDS_Selector  my_selector;

      public ButtonListener( PartialDS_Selector pdss )
      {
        my_selector = pdss;
      }

      public void actionPerformed( ActionEvent e )
      {
        String command = e.getActionCommand();
        observers.notifyIObservers( my_selector, command );
        if ( command.equals( EXIT ) )
          dialog.dispose();

        if ( debug )
        {
          System.out.println("Command issued: " + command );
          if ( command.equals( APPLY ) )
          {
            System.out.println("Command is : ");
            System.out.println("" +getCommand() );
          }
        }
      }
   }

   private class WindowCloser extends WindowAdapter
   {
      PartialDS_Selector  my_selector;

      public WindowCloser( PartialDS_Selector pdss )
      {
        my_selector = pdss;
      }

     public void windowClosing( WindowEvent event )
     {
       observers.notifyIObservers( my_selector, EXIT );
  
       if ( debug )
         System.out.println("Command issued: " + EXIT );

       dialog.dispose();
     }
   }


  /* ----------------------------------------------------------------------
   *
   *  MAIN (test program) 
   *
   */
  public static void main( String args[] )
  {
    int    ds_num    = 2;
    String ids       = "3:5";
    float  min_x     = 0;
    float  max_x     = 10000;
    int    rebin     = 1;
    int    attr_mode = Attribute.FULL_ATTRIBUTES;

    GetDataCommand command = new GetDataCommand( CommandObject.GET_DS,
                                                 "John Doe",
                                                 "Bad Password",
                                                 "Some File Name",
                                                  ds_num,
                                                  ids,
                                                  min_x, max_x,
                                                  rebin,
                                                  attr_mode );
    PartialDS_Selector pdss = new PartialDS_Selector( command );
  }

}
