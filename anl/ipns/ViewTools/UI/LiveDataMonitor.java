/*
 * @(#)LiveDataMonitor.java
 *
 *  Programmer: Dennis Mikkelson
 *
 *  $Log$
 *  Revision 1.2  2001/02/15 23:18:05  dennis
 *  This version now works, using a default flow layout
 *
 *  Revision 1.1  2001/02/15 22:52:10  dennis
 *  Initial form of class for monitoring data from a
 *  remote LiveDataServer object.
 *
 *
 */

package DataSetTools.components.ui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import DataSetTools.dataset.*;
import DataSetTools.viewer.*;
import DataSetTools.retriever.*;

/**
 */

public class LiveDataMonitor extends    JPanel
                             implements Serializable 
{
  TextValueUI      time_widget;  
  LiveDataManager  data_manager;
  ViewManager      viewers[] = new ViewManager[0];
  String           source_name;
  JCheckBox        checkbox[] = new JCheckBox[0];
 
 /* ------------------------------ CONSTRUCTOR ---------------------------- */
 /** 
  *
  */
  public LiveDataMonitor( String data_source_name )
  { 
    setLayout( new FlowLayout() );

    time_widget = new TextValueUI( "Update time(sec)", 30 );
    time_widget.setLimits( 10, 600 );
    time_widget.addActionListener( new UpdateTimeListener() );
 
    data_manager = new LiveDataManager( data_source_name );
    data_manager.setUpdateInterval( time_widget.getValue() );
    viewers = new ViewManager[ data_manager.numDataSets() ]; 
    checkbox = new JCheckBox[ data_manager.numDataSets() ];
 
    for ( int i = 0; i < data_manager.numDataSets(); i++ )
    {
      DataSet ds = data_manager.getDataSet( i );
      JLabel  label  = new JLabel( ds + ":");
      add( label );

      JButton button = new JButton("Update");
      UpdateButtonListener button_listener = new UpdateButtonListener( i );
      button.addActionListener( button_listener );
      add( button );

      checkbox[i] = new JCheckBox( "Show" );
      if ( i < 2 )
      {
        viewers[i] = new ViewManager( ds, IViewManager.IMAGE );
        data_manager.setUpdateIgnoreFlag( i, false );
        checkbox[i].setSelected( true );
      }
      else
      {
        viewers[i] = null;
        data_manager.setUpdateIgnoreFlag( i, true );
        checkbox[i].setSelected( false );
      }
      ShowCheckboxListener checkbox_listener = new ShowCheckboxListener( i );
      checkbox[i].addActionListener( checkbox_listener );
      add( checkbox[i] );
    }

    add( time_widget );
  }


 /* ------------------------------------------------------------------------
  *
  *  INTERNAL CLASSES
  *
  */
    
  /* ------------------------ UpdateTimeListener -------------------------- */

  private class UpdateTimeListener implements ActionListener
  {
    public void actionPerformed( ActionEvent e )
    {
      data_manager.setUpdateInterval( time_widget.getValue() );
    }
  };

  /* ----------------------- UpdateButtonListener ------------------------- */

  private class UpdateButtonListener implements ActionListener
  {
    int     my_index;

    public UpdateButtonListener( int index )
    {
      my_index = index;
    }

    public void actionPerformed( ActionEvent e )
    {
      if ( viewers[ my_index ] == null  ||
          !viewers[ my_index ].isVisible() )    // make another viewer
      {
        DataSet ds = data_manager.getDataSet( my_index );
        viewers[my_index] = new ViewManager( ds, IViewManager.IMAGE );
      }

      data_manager.UpdateDataSetNow( my_index ); 
      checkbox[ my_index ].setSelected( true );
    }
  }


  /* ----------------------- ShowCheckboxListener ---------------------- */

  private class ShowCheckboxListener implements ActionListener
  {
    int     my_index;

    public ShowCheckboxListener( int index )
    { 
      my_index = index;
    }

    public void actionPerformed( ActionEvent e )
    { 
      JCheckBox check_box = (JCheckBox)(e.getSource());

      if ( check_box.isSelected() )
      {
        if ( viewers[ my_index ] == null  ||
            !viewers[ my_index ].isVisible() )    // make another viewer
        { 
          DataSet ds = data_manager.getDataSet( my_index );
          viewers[my_index] = new ViewManager( ds, IViewManager.IMAGE );
        }
        data_manager.UpdateDataSetNow( my_index );
        data_manager.setUpdateIgnoreFlag( my_index, false );
      }
      else
      {
        data_manager.setUpdateIgnoreFlag( my_index, true );
        viewers[my_index].destroy();
      }
    }
  }


/* -------------------------------------------------------------------------
 *
 * MAIN 
 *
 */
  public static void main(String[] args)
  {
     String instrument_computer = "dmikk.mscs.uwstout.edu";

     LiveDataMonitor monitor = new LiveDataMonitor( instrument_computer );

     JFrame frame = new JFrame( instrument_computer );
     frame.setBounds(0,0,200,250);
     frame.getContentPane().add( monitor );
 
     frame.setVisible( true );
  }
}
