/*
 * @(#)LiveDataMonitor.java
 *
 *  Programmer: Dennis Mikkelson
 *
 *  $Log$
 *  Revision 1.5  2001/02/20 23:06:11  dennis
 *  - Now uses slider to set the delay between auto updates.
 *  - Also, now uses more elaborate layout of the components
 *    and has descriptive borders on components.
 *  - Finally, added methods to get the number and type of
 *    the DataSets and to get the DataSets from the underlying
 *    LiveDataManager.  (These are just "wrapper" methods.)
 *
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
import javax.swing.event.*;
import javax.swing.border.*;
import java.io.*;
import java.util.*;
import DataSetTools.dataset.*;
import DataSetTools.viewer.*;
import DataSetTools.retriever.*;
import DataSetTools.util.*;

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
  private LiveDataManager  data_manager;
  private ViewManager      viewers[] = new ViewManager[0];
  private JCheckBox        checkbox[] = new JCheckBox[0];
 
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
    data_manager = new LiveDataManager( data_source_name );
    int num_ds   = data_manager.numDataSets();

                                                       // make lists to save 
                                                       // references to the 
                                                       // viewers and "Show" 
                                                       // checkboxes
    viewers = new ViewManager[ num_ds ]; 
    checkbox = new JCheckBox[ num_ds ];
    setLayout( new GridLayout( 1, 1 ) );
    Box panel_box = new Box( BoxLayout.Y_AXIS );
    add( panel_box );
                                                            // Set up the update
                                                            // time control
    JSlider time_slider = new JSlider( JSlider.HORIZONTAL, 0, 600, 60 );
    time_slider.setMinimumSize( new Dimension(50, 20) );
    time_slider.setPaintTicks( true );
    time_slider.setMajorTickSpacing( 60 );
    time_slider.setMinorTickSpacing( 20 );
    time_slider.addChangeListener( new UpdateTimeListener() );
    data_manager.setUpdateInterval( time_slider.getValue() );

    JPanel label_panel  = new JPanel();
    label_panel.setLayout( new GridLayout( 1, 1 ) );
    JLabel source_label = new JLabel( data_source_name.toUpperCase() );
    source_label.setFont( FontUtil.BORDER_FONT );
    source_label.setMinimumSize( new Dimension(50, 10) );
    source_label.setHorizontalAlignment( SwingConstants.CENTER );
    source_label.setHorizontalTextPosition( SwingConstants.CENTER );
    source_label.setVerticalAlignment( SwingConstants.CENTER );
    source_label.setVerticalTextPosition( SwingConstants.CENTER );
    TitledBorder border = new TitledBorder( LineBorder.createBlackLineBorder(),
                                            "Data Source:" );
    border.setTitleFont( FontUtil.BORDER_FONT );
    label_panel.setBorder( border );
    label_panel.add( source_label );
    panel_box.add( label_panel );

    for ( int i = 0; i < data_manager.numDataSets(); i++ )
    {
      JPanel panel = new JPanel();               // use a separate "sub" panel
      panel.setLayout( new FlowLayout() );       // for each possible DataSet 
      border = new TitledBorder( LineBorder.createBlackLineBorder(),
                                 "DataSet #" + i );
      border.setTitleFont( FontUtil.BORDER_FONT );
      panel.setBorder( border );

      DataSet ds = data_manager.getDataSet( i );
      JLabel  label  = new JLabel( ds.getTitle() + ":");
      label.setFont( FontUtil.BORDER_FONT );

      JButton button = new JButton("Update");
      button.setFont( FontUtil.LABEL_FONT );
      UpdateButtonListener button_listener = new UpdateButtonListener( i );
      button.addActionListener( button_listener );

      checkbox[i] = new JCheckBox( "Show" );
      checkbox[i].setFont( FontUtil.LABEL_FONT );
      if ( i < 2 )                               // by default, on show first
      {                                          // two DataSets
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

      panel.add( label );                        // Add the components for this
      panel.add( checkbox[i] );                  // DataSet to the current panel
      panel.add( button );

      panel_box.add( panel );                    // Add the panel to this
                                                 // LiveDataMonitor 
    }

    border = new TitledBorder( LineBorder.createBlackLineBorder(),
                               "Update Time Interval( 0 - 10 Min )" );
    border.setTitleFont( FontUtil.BORDER_FONT );
    time_slider.setBorder( border );

    panel_box.add( time_slider );
  }


/**
 *  Get the number of distinct DataSets from the current data source.
 *  The monitors are placed into one DataSet.  Any sample histograms are
 *  placed into separate DataSets.
 *
 *  @return the number of distinct DataSets in this runfile.
 */
  public int numDataSets()
  {
    return data_manager.numDataSets();
  }

/**
 * Get the type of the specified data set from the current data source.
 * The type is an integer flag that indicates whether the data set contains
 * monitor data or data from other detectors.
 */

  public int getType( int data_set_num )
  {
    return data_manager.getType( data_set_num );
  }


/**
 *  Get the specified DataSet from the current data source.
 *
 *  @param  data_set_num  The number of the DataSet in this runfile
 *                        that is to be read from the runfile.  data_set_num
 *                        must be between 0 and numDataSets()-1
 *
 *  @return the requested DataSet.
 */
  public DataSet getDataSet( int data_set_num )
  {
    return data_manager.getDataSet( data_set_num );
  }


 /* ------------------------------------------------------------------------
  *
  *  INTERNAL CLASSES
  *
  */
    
  /* ------------------------ UpdateTimeListener -------------------------- */

  private class UpdateTimeListener implements ChangeListener,
                                              Serializable
  {
    public void stateChanged( ChangeEvent e )
    {
      JSlider slider = (JSlider)e.getSource();

      if ( !slider.getValueIsAdjusting() )
        data_manager.setUpdateInterval( slider.getValue() );
    }
  };

  /* ----------------------- UpdateButtonListener ------------------------- */

  private class UpdateButtonListener implements ActionListener,
                                                Serializable
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

  private class ShowCheckboxListener implements ActionListener,
                                                Serializable
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

     JFrame frame = new JFrame( "Live Data Monitor" );
     frame.setBounds( 0, 0, 350, 225 );
     frame.getContentPane().add( monitor );
 
     frame.setVisible( true );
  }
}
