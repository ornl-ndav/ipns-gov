/*
 * @(#)LiveDataMonitor.java
 *
 *  Programmer: Dennis Mikkelson
 *
 *  $Log$
 *  Revision 1.8  2001/03/02 17:01:25  dennis
 *  Added tests for data_manager == null in methods to get info from
 *  the data_manager.
 *  Added rudimentary destroy() method.  THIS IS NOT COMPLETE.
 *
 *  Revision 1.7  2001/02/22 23:19:12  dennis
 *  Now includes separate check box that determines whether
 *  or not the DataSet is automatically updated.
 *  Also, if the DataSets are switched to a different run,
 *  the labels will be updated when the GUI controls are adjusted.
 *
 *  Revision 1.6  2001/02/22 21:06:04  dennis
 *  Moved code that builds the GUI into a private method SetUpGUI().
 *
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
  private String           data_source_name = "";
  private LiveDataManager  data_manager = null;
  private ViewManager      viewers[]  = new ViewManager[0];
  private JCheckBox        show_box[] = new JCheckBox[0];
  private JCheckBox        auto_box[] = new JCheckBox[0];
  private JLabel           ds_label[] = new JLabel[0];
 
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
    this.data_source_name = data_source_name;

    data_manager = new LiveDataManager( data_source_name );
    int num_ds   = data_manager.numDataSets();

    if ( num_ds == 0 )
    { setLayout( new GridLayout( 1, 1 ) );
      JLabel error_label = new JLabel( data_source_name + "  HAS NO DATA SETS");
      add( error_label );
      return;
    }

    SetUpGUI();
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
    if ( data_manager != null )
      return data_manager.numDataSets();
    else
      return 0;
  }

/**
 * Get the type of the specified data set from the current data source.
 * The type is an integer flag that indicates whether the data set contains
 * monitor data or data from other detectors.
 */

  public int getType( int data_set_num )
  {
    if ( data_manager != null )
      return data_manager.getType( data_set_num );
    else
      return Retriever.INVALID_DATA_SET;
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
    if ( data_manager != null )
      return data_manager.getDataSet( data_set_num );
    else
      return (DataSet)(DataSet.EMPTY_DATA_SET.clone());
  }


/**
 *
 */
  public void destroy()
  {
    // SHOULD FIRST SHUTDOWN THE data_manager... NOT YET IMPLEMENTED

    removeAll();                    // get rid of all of the components
    ResetPrivateData();
  }

 /* ------------------------------------------------------------------------
  *
  *  PRIVATE METHODS 
  *
  */

  /* ----------------------- ResetPrivateData --------------------------- */

  private void ResetPrivateData()
  {
    data_source_name = "";
    data_manager = null;
    viewers  = new ViewManager[0];
    show_box = new JCheckBox[0];
    auto_box = new JCheckBox[0];
    ds_label = new JLabel[0];
  }

  /* ------------------------------ SetUpGUI ------------------------------- */

  private void SetUpGUI()
  {
    int num_ds   = data_manager.numDataSets();

                                        // make lists to save references to the
                                        // viewers and "Show" checkboxes
    viewers  = new ViewManager[ num_ds ];
    show_box = new JCheckBox[ num_ds ];
    auto_box = new JCheckBox[ num_ds ];
    ds_label = new JLabel[ num_ds ];
                                                  // Clear the panel and set up
                                                  // a new layout using a box.
    removeAll();                                  
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
    source_label.setPreferredSize( new Dimension(50, 10) );
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

      DataSet ds   = data_manager.getDataSet( i );
      ds_label[i]  = new JLabel( ds.getTitle() );
      ds_label[i].setFont( FontUtil.BORDER_FONT );

      JButton button = new JButton("Update");
      button.setFont( FontUtil.LABEL_FONT );
      UpdateButtonListener button_listener = new UpdateButtonListener( i );
      button.addActionListener( button_listener );

      show_box[i] = new JCheckBox( "Show" );
      show_box[i].setFont( FontUtil.LABEL_FONT );
      show_box[i].setSelected( false );
      ShowCheckboxListener show_box_listener = new ShowCheckboxListener( i );
      show_box[i].addActionListener( show_box_listener );


      viewers[i] = null;                            // by default, don't show
      data_manager.setUpdateIgnoreFlag( i, true );  // or update any of the 
                                                    // DataSets

      auto_box[i] = new JCheckBox( "Auto" );
      auto_box[i].setFont( FontUtil.LABEL_FONT );
      auto_box[i].setSelected( false );
      AutoCheckboxListener auto_box_listener = new AutoCheckboxListener( i );
      auto_box[i].addActionListener( auto_box_listener ); 

      panel.add( ds_label[i] );                  // Add the components for this
      panel.add( show_box[i] );                  // DataSet to the current panel
      panel.add( auto_box[i] );
      panel.add( button );

      panel_box.add( panel );                    // Add the panel to this
                                                 // LiveDataMonitor
    }

    border = new TitledBorder( LineBorder.createBlackLineBorder(),
                               "Auto Update Interval( 0 - 10 Min )" );
    border.setTitleFont( FontUtil.BORDER_FONT );
    time_slider.setBorder( border );

    panel_box.add( time_slider );
  }

 /**
  *  Check if any of the DataSet titles have changed and adjust the labels if
  *  they have.
  */
  private void FixLabels()
  {
    for ( int i = 0; i < ds_label.length; i++ )
    {
      DataSet ds = data_manager.getDataSet( i );
      String cur_title = ds.getTitle();
      if ( !cur_title.equalsIgnoreCase( ds_label[i].getText() ) )
        ds_label[i].setText( cur_title );
    } 
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

      FixLabels();
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
      System.out.println("UpdateButtonListener called...");
      if ( viewers[ my_index ] == null  ||
          !viewers[ my_index ].isVisible() )    // make another viewer
      {
        DataSet ds = data_manager.getDataSet( my_index );
        viewers[my_index] = new ViewManager( ds, IViewManager.IMAGE );
      }

      data_manager.UpdateDataSetNow( my_index ); 
      show_box[ my_index ].setSelected( true );   // update implies we show it

      FixLabels();
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
      System.out.println("ShowCheckboxListener called...");
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
      }
      else
      {
        viewers[my_index].destroy();
        auto_box[my_index].setSelected( false );  // Don't show it implies
                                                  // don't auto update it.
        data_manager.setUpdateIgnoreFlag( my_index, true );
      }

      FixLabels();
    }
  }


  /* ----------------------- AutoCheckboxListener ---------------------- */

  private class AutoCheckboxListener implements ActionListener,
                                                Serializable
  {
    int     my_index;

    public AutoCheckboxListener( int index )
    {
      my_index = index;
    }

    public void actionPerformed( ActionEvent e )
    {
      System.out.println("AutoCheckboxListener called...");
      JCheckBox check_box = (JCheckBox)(e.getSource());

      if ( check_box.isSelected() )
      {
        if ( viewers[ my_index ] == null  ||
            !viewers[ my_index ].isVisible() )    // make another viewer
        {
          DataSet ds = data_manager.getDataSet( my_index );
          viewers[my_index] = new ViewManager( ds, IViewManager.IMAGE );
        }
        show_box[ my_index ].setSelected( true );   // Auto update implies 
                                                    // we also show it.
        data_manager.setUpdateIgnoreFlag( my_index, false );
      }
      else
        data_manager.setUpdateIgnoreFlag( my_index, true );

      FixLabels();
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
     frame.setBounds( 0, 0, 350, 350 );
     frame.getContentPane().add( monitor );
 
     frame.setVisible( true );
  }
}
