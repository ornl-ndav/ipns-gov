/*
 * @(#)LiveDataMonitor.java
 *
 *  Programmer: Dennis Mikkelson
 *
 *  $Log$
 *  Revision 1.4  2001/02/16 22:08:00  dennis
 *  Now the main program will get the instrument computer name
 *  or IP address from the command line.
 *  Fixed bug which caused auto updates for a DataSet to be missed
 *  if the DataSet was first hidden by "unchecking" the Show box and
 *  then made visible by pressing the Update button.
 *
 *  Revision 1.3  2001/02/16 16:40:21  dennis
 *  Changed order of buttons in GUI panel.
 *  Added some @see comments.
 *
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
 *
 *  This class is a JPanel that contains a user interface to control a
 *  LiveDataManager, which can periodically update DataSets from a 
 *  LiveDataServer.
 *
 *  @see NetComm.LiveDataServer
 *  @see DataSetTools.retriever.LiveDataRetriever
 *  @see DataSetTools.retriever.LiveDataManager
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
  *  Construct a LiveDataMonitor GUI, and a LiveDataManager to control the
  *  updates of live data from the specified data source.  This also
  *  makes the connection to the LiveDataServer via a LiveDataRetriever.
  *
  *  @param  data_source_name  The name or IP address of the system that is
  *                            running the LiveDataServer.
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

      add( button );
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
      data_manager.setUpdateIgnoreFlag( my_index, false );
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
     if ( args.length < 1 )
     {
       System.out.println("Please enter the instrument computer name ");
       System.out.println("or IP address on the command line ");
       System.exit(1);
     } 

     String instrument_computer = args[0];
     LiveDataMonitor monitor = new LiveDataMonitor( instrument_computer );

     JFrame frame = new JFrame( instrument_computer );
     frame.setBounds(0,0,350,150);
     frame.getContentPane().add( monitor );
 
     frame.setVisible( true );
  }
}
