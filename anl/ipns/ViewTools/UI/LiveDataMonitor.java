/*
 * File:  LiveDataMonitor.java
 *
 * Copyright (C) 2001, Dennis Mikkelson
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
 * This work was supported by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 * Modified:
 *
 *  $Log$
 *  Revision 1.26  2003/03/07 22:59:50  dennis
 *  destroy() method now shutsdown the LiveDataManager thread,
 *  closes any viewers launched from the LiveDataMonitor and
 *  closes and PartialDS_Selector dialogs.
 *  Added "listener" to get APPLY and EXIT messages from the
 *  PartialDS_Selector dialogs.
 *  Added checkbox for getting partial DataSets.
 *  Tracks currently specified partial DataSet command and
 *  uses it in the update process, if the partial DataSet
 *  check box is selected.
 *
 *  Revision 1.25  2003/03/04 20:48:29  dennis
 *  Major usability improvements.
 *  Listeners to the Show, Update, Auto and Record controls now
 *  use SwingWorker() to start a separate thread for operations
 *  that take a lot of time.
 *  The new LiveDataManager.getDataSetName() method is used when
 *  laying out the control panel, so that the control panel
 *  appears quickly without having to first load the DataSets.
 *
 *  Revision 1.24  2002/12/01 16:12:31  dennis
 *  Now verifies that viewer is not null before destroying it.
 *
 *  Revision 1.23  2002/11/27 23:13:34  pfpeterson
 *  standardized header
 *
 *  Revision 1.22  2002/04/18 22:00:57  dennis
 *  Fixed name problem that prevented compiling with jdk1.3.1_03
 *  (Though it did compile with 1.4.0)
 *
 *  Revision 1.21  2002/04/18 21:34:24  dennis
 *  If the LiveDataManager Thread sends a message to this LiveDataMonitor,
 *  the request is put into a queue and run by the Swing Event handling
 *  thread using SwingUtilities.invokeLater().  This guarantees that the
 *  Swing drawing that is needed in response to the message will be done
 *  in the Swing Event handling thread.
 *
 *  Revision 1.20  2002/04/10 16:02:55  dennis
 *  Removed automatic request for update after requesting that a DataSet be
 *  shown.
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
import ExtTools.SwingWorker;
import NetComm.*;

/**
 *
 *  This class is a JPanel that contains a user interface to control a
 *  LiveDataManager, which can periodically update DataSets from a 
 *  LiveDataServer.  It is "observed" by the Isaw main application, so that
 *  it can send DataSets to Isaw when the record button is pressed.
 *
 *  @see NetComm.LiveDataServer
 *  @see DataSetTools.retriever.LiveDataRetriever
 *  @see DataSetTools.retriever.LiveDataManager
 */

public class LiveDataMonitor extends    JPanel
                             implements IObservable,
                                        Serializable
{
  public static final Color  BACKGROUND  = Color.white;
  public static final Color  FOREGROUND  = Color.black;
  public static final Color  ALERT_COLOR = Color.red;
  private String           data_source_name = "";
  private String           current_status   
                                   = RemoteDataRetriever.NOT_CONNECTED_STRING;
  private LiveDataManager  data_manager = null;
  private JLabel           status_label = new JLabel();
  private JLabel           source_label = new JLabel();
  private ViewManager      viewers[]    = new ViewManager[0];
  private JCheckBox        show_box[]   = new JCheckBox[0];
  private JCheckBox        auto_box[]   = new JCheckBox[0];
  private JCheckBox        partial_box[]= new JCheckBox[0];
  private PartialDS_Selector selector[] = new PartialDS_Selector[0];
  private GetDataCommand     command[]  = new GetDataCommand[0];
  private JButton          button[]     = new JButton[0];
  private JButton          record[]     = new JButton[0];
  private JLabel           ds_label[]   = new JLabel[0];
  private JPanel           panel[]      = new JPanel[0];
  private IObserverList    observers;
  private Vector           event_queue;          // queue for passing the action
                                                 // event from the DataManager
                                                 // to the event thread
  private boolean  debug_LDM = false;

 
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
    event_queue = new Vector();
    this.data_source_name = data_source_name;

    data_manager = new LiveDataManager( data_source_name );
    data_manager.addActionListener( new DataManagerListener() );
    observers = new IObserverList();

    SetUpGUI();
  }


  /**
   *  Add the specified object to the list of observers to notify when this
   *  observable object changes.
   *
   *  @param  iobs   The observer object that is to be notified.
   *
   */
   public void addIObserver( IObserver iobs )
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
   public void deleteIObserver( IObserver iobs )
   {
     observers.deleteIObserver( iobs );
   }

  /**
   *  Remove all objects from the list of observers to notify when this
   *  observable object changes.
   */
   public void deleteIObservers( )
   {
     observers.deleteIObservers();
   }

  /**
   *  Notify all observers in the list ( by calling their update(,) method )
   *  that the observed object has changed.
   *
   * @param  reason        Object indicating the nature of the change in the
   *                       observable object, or specifying the action that the
   *                       observer should take.  This is passed as the second
   *                       parameter to the update(,) method of the observers
   *                       and will typically be a String.
   */
   public void notifyIObservers( Object reason )
   {
     observers.notifyIObservers( this, reason );
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
     if ( debug_LDM )
      System.out.println("LiveDataMonitor.getDataSet("+data_set_num+") called");

     if ( data_manager != null )
     {
       DataSet temp_ds = data_manager.getDataSet( data_set_num ); 
       if ( temp_ds != null )
         return (DataSet)(temp_ds.clone());
     }
     return null;
   }


  /**
   *  Close this LiveDataMonitor by breaking the connection to the 
   *  LiveDataManager, stopping the LiveDataManager thread and removing
   *  all components from this panel.
   */
   public void destroy()
   {
     data_manager.removeAllActionListeners();
     data_manager.stop_eventually(); 

     for ( int i = 0; i < viewers.length; i++ )
     {
       if ( viewers[i] != null )
       {
         viewers[i].destroy();
         viewers[i] = null;
       }
       if ( selector[i] != null )
         selector[i].dispose();
     }

     removeAll();                    // get rid of all of the components
   }

 /* ------------------------------------------------------------------------
  *
  *  PRIVATE METHODS 
  *
  */


  private void ExpandStorage()
  {
    int num_ds     = data_manager.numDataSets();
    int old_length = viewers.length;

    if ( num_ds > old_length )
    {                                                         // get more space
      PartialDS_Selector new_selector[] = new PartialDS_Selector[ num_ds ];
      GetDataCommand     new_command[]  = new GetDataCommand[ num_ds ];
      ViewManager new_viewers[]     = new ViewManager[ num_ds ];
      JCheckBox   new_show_box[]    = new JCheckBox[ num_ds ];
      JCheckBox   new_auto_box[]    = new JCheckBox[ num_ds ];
      JCheckBox   new_partial_box[] = new JCheckBox[ num_ds ];
      JButton     new_button[]   = new JButton[ num_ds ];
      JButton     new_record[]   = new JButton[ num_ds ];
      JLabel      new_ds_label[] = new JLabel[ num_ds ];
      JPanel      new_panel[]    = new JPanel[ num_ds ];

      for ( int i = 0; i < viewers.length; i++ )             // save the old
      {                                                      // objects
        new_viewers[i]     = viewers[i];
        new_show_box[i]    = show_box[i];
        new_auto_box[i]    = auto_box[i];
        new_partial_box[i] = partial_box[i];
        new_selector[i]    = selector[i];
        new_command[i]     = command[i];
        new_button[i]   = button[i];
        new_record[i]   = record[i];
        new_ds_label[i] = ds_label[i];
        new_panel[i]    = panel[i];
      }
                                                             // shift over to
      viewers     = new_viewers;                             // the new ones
      show_box    = new_show_box;
      auto_box    = new_auto_box;
      partial_box = new_partial_box;
      selector    = new_selector;
      command     = new_command;
      button   = new_button;
      record   = new_record;
      ds_label = new_ds_label;
      panel    = new_panel;
                                                             // construct new
                                                             // labels, etc.
      for ( int i = old_length; i < viewers.length; i++ )
      {
        viewers[i]  = null;
        selector[i] = null;

        panel[i] = new JPanel();                // use a separate "sub" panel
        panel[i].setLayout( new FlowLayout() ); // for each possible DataSet
        panel[i].setBackground( BACKGROUND );
        TitledBorder border = 
           new TitledBorder( LineBorder.createBlackLineBorder(),"DataSet #"+i);
        border.setTitleFont( FontUtil.BORDER_FONT );
        border.setTitleColor( FOREGROUND );
        panel[i].setBorder( border );

        show_box[i] = new JCheckBox( "Show" );
        show_box[i].setFont( FontUtil.BORDER_FONT );
        show_box[i].setSelected( false );
        show_box[i].setBackground( BACKGROUND );
        ShowCheckboxListener show_box_listener = new ShowCheckboxListener( i );
        show_box[i].addActionListener( show_box_listener );

        auto_box[i] = new JCheckBox( "Auto" );
        auto_box[i].setFont( FontUtil.BORDER_FONT );
        auto_box[i].setBackground( BACKGROUND );
        auto_box[i].setSelected( false );
        AutoCheckboxListener auto_box_listener = new AutoCheckboxListener( i );
        auto_box[i].addActionListener( auto_box_listener );

        partial_box[i] = new JCheckBox( "Partial" );
        partial_box[i].setFont( FontUtil.BORDER_FONT );
        partial_box[i].setBackground( BACKGROUND );
        partial_box[i].setSelected( false );
        PartialCheckboxListener partial_box_listener = 
                                            new PartialCheckboxListener( i );
        partial_box[i].addActionListener( partial_box_listener );

        selector[i] = null;
        command[i]  = data_manager.getDefaultCommand(i);

        button[i] = new JButton("Update");
        button[i].setFont( FontUtil.BORDER_FONT );
        button[i].setBackground( BACKGROUND );
        UpdateButtonListener button_listener = new UpdateButtonListener( i );
        button[i].addActionListener( button_listener );

        record[i] = new JButton("Record");
        record[i].setFont( FontUtil.BORDER_FONT );
        record[i].setBackground( BACKGROUND );
        RecordButtonListener record_listener = new RecordButtonListener( i );
        record[i].addActionListener( record_listener );

        ds_label[i]  = new JLabel( "DATA SET" );
        ds_label[i].setFont( FontUtil.BORDER_FONT );
        ds_label[i].setForeground( FOREGROUND );

        panel[i].add( ds_label[i] );             // Add the components for this
        panel[i].add( show_box[i] );             // DataSet to the current panel
        panel[i].add( auto_box[i] );
        panel[i].add( partial_box[i] );
        panel[i].add( button[i] );
        panel[i].add( record[i] );
      }
    }
  }
 

  /* ------------------------------ ErrorMessage --------------------------- */

  private String ErrorMessage()
  {
    if ( data_manager == null )
      return "ERROR: No DataManager!!!";

    int code = data_manager.numDataSets();

    if ( code >= 0 )
      return "" + code + " DataSets";       
 
    return  RemoteDataRetriever.error_message( code );
  }


  /* ------------------------------ SetUpGUI ------------------------------- */

  synchronized private void SetUpGUI()
  {
    setBackground( BACKGROUND );
    setForeground( FOREGROUND );

    ExpandStorage();
                                                  // Clear the panel and set up
                                                  // a new layout using a box.
    setVisible( false );
    removeAll();                                  
    setLayout( new GridLayout( 1, 1 ) );
    Box panel_box = new Box( BoxLayout.Y_AXIS );
    add( panel_box );
                                                            // Set up the update
                                                            // time control
    JSlider time_slider = new JSlider( JSlider.HORIZONTAL, 0, 600, 
                                       (int)data_manager.getUpdateInterval() );

    time_slider.setMinimumSize( new Dimension(50, 20) );
    time_slider.setPaintTicks( true );
    time_slider.setMajorTickSpacing( 60 );
    time_slider.setMinorTickSpacing( 20 );
    time_slider.addChangeListener( new UpdateTimeListener() );
    time_slider.setBackground( BACKGROUND );
    data_manager.setUpdateInterval( time_slider.getValue() );

    JPanel label_panel  = new JPanel();
    label_panel.setLayout( new GridLayout( 1, 1 ) );

    source_label.setText( data_source_name.toUpperCase() );
    source_label.setFont( FontUtil.BORDER_FONT );
    source_label.setBackground( BACKGROUND );
    source_label.setForeground( FOREGROUND );
    source_label.setHorizontalAlignment( SwingConstants.CENTER );
    source_label.setHorizontalTextPosition( SwingConstants.CENTER );
    source_label.setVerticalAlignment( SwingConstants.CENTER );
    source_label.setVerticalTextPosition( SwingConstants.CENTER );

    status_label.setText( ErrorMessage() );
    status_label.setFont( FontUtil.BORDER_FONT );
    status_label.setBackground( BACKGROUND );
    status_label.setForeground( FOREGROUND );
    status_label.setHorizontalAlignment( SwingConstants.CENTER );
    status_label.setHorizontalTextPosition( SwingConstants.CENTER );
    status_label.setVerticalAlignment( SwingConstants.CENTER );
    status_label.setVerticalTextPosition( SwingConstants.CENTER );

    TitledBorder border = new TitledBorder( LineBorder.createBlackLineBorder(),
                                            "Data Source:" );
    border.setTitleFont( FontUtil.BORDER_FONT );
    border.setTitleColor( FOREGROUND );
    label_panel.setBorder( border );
    label_panel.add( source_label );
    label_panel.add( status_label );
    label_panel.setBackground( BACKGROUND );
    panel_box.add( label_panel );

    if ( debug_LDM )
      System.out.println("LiveDataMonitor.SetUpGUI, n_ds = " + 
                          data_manager.numDataSets() );

    for ( int i = 0; i < data_manager.numDataSets(); i++ )
    {
      panel_box.add( panel[i] );                 // Add the panel to this
                                                 // LiveDataMonitor

      if ( auto_box[i].isSelected() )
        data_manager.setUpdateIgnoreFlag( i, false );
      else
        data_manager.setUpdateIgnoreFlag( i, true );

      if ( viewers[i] != null && !viewers[i].isVisible()  )  
      {
        viewers[i].destroy();                  // get rid of invisible viewers
        viewers[i] = null;  
      }
    }

    border = new TitledBorder( LineBorder.createBlackLineBorder(),
                               "Auto Update Interval( 0 - 10 Min )" );
    border.setTitleFont( FontUtil.BORDER_FONT );
    border.setTitleColor( FOREGROUND );
    time_slider.setBorder( border );

    panel_box.add( time_slider );

    FixLabels();
    validate(); 
    setVisible( true );
  }


 /* ------------------------------ FixLabels ------------------------------ */
 /**
  *  Check if any of the DataSet titles have changed and adjust the labels if
  *  they have.
  */
  private void FixLabels()
  {
    if (ds_label == null || ds_label.length == 0 )
      return;

    int num_labels = data_manager.numDataSets();
    if ( num_labels > ds_label.length )
      num_labels = ds_label.length;

    for ( int i = 0; i < num_labels; i++ )
    {
      String cur_title = data_manager.getDataSetName( i );
      if ( cur_title != null )
      {
        if ( !cur_title.equalsIgnoreCase( ds_label[i].getText() ) )
          ds_label[i].setText( cur_title );
      }
      else 
        ds_label[i].setText( "NO DATA SET" );
    } 
  }


 /* ------------------------------------------------------------------------
  *
  *  INTERNAL CLASSES
  *
  */

  /* ------------------------ PartialDSListener -------------------------- */
 
  private class PartialDSListener implements IObserver
  {
    int my_index;

    public PartialDSListener( int index )
    {
      my_index = index;
    }

    public void update( Object pdss, Object reason )
    {
      if ( reason instanceof String )
      {
        if ( reason.equals( PartialDS_Selector.APPLY ) )
        {
          command[my_index] = ((PartialDS_Selector)pdss).getCommand();
        }
        else if ( reason.equals( PartialDS_Selector.EXIT ) )
        {
          System.out.println("EXIT");
          partial_box[my_index].setSelected(false);
          ((PartialDS_Selector)pdss).dispose();
          selector[my_index] = null;
        }
      }
    }
  }

    
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
      if ( data_manager.isGettingDS() )           // ignore update request if
        return;                                   // the DataManager is busy

      button[my_index].setEnabled(false); 

      // create a subclass of SwingWorker to update and get the DataSet 
      final SwingWorker worker = new SwingWorker()   // ----- INLINE CLASS DEF
      {
        public Object construct()
        {
          if ( partial_box[my_index].isSelected() )
            data_manager.UpdateDataSetNow( command[my_index] );
          else
            data_manager.UpdateDataSetNow( my_index );

          return data_manager.getDataSet( my_index ); // could return anything
        }

        public void finished()        // when finished() is called, the DataSet
        {                             // will have been updated in the
                                      // ViewManager, so we can get it and
                                      // display it if needed.

          if ( viewers[ my_index ] == null  ||      // must make another viewer
              !viewers[ my_index ].isVisible() )    // first, to show it
          {
            DataSet ds = data_manager.getDataSet( my_index );
            if ( ds != null )
            {
              viewers[my_index] = new ViewManager( ds, IViewManager.IMAGE );
              viewers[my_index].addWindowListener(
                                         new ViewManagerListener(my_index));
            }
          }
          // else the UpdateDataSetNow() method will have notified an existing
          // viewer that the DataSet it's showing has been updated.
 
          show_box[ my_index ].setSelected( true );  // update implies show it
          button[my_index].setEnabled(true);
          FixLabels();
         }
       };
      worker.start();
    }
  }


  /* ----------------------- RecordButtonListener ------------------------- */

  private class RecordButtonListener implements ActionListener,
                                                Serializable
  {
    int     my_index;

    public RecordButtonListener( int index )
    {
      my_index = index;
    }

    public void actionPerformed( ActionEvent e )
    {
       record[my_index].setEnabled(false);

       // create a subclass of SwingWorker to get the DataSet
       final SwingWorker worker = new SwingWorker()   // ----- INLINE CLASS DEF
       {
         public Object construct()  // calling getDataSet will make sure there
         {                          // is a DataSet in the ViewManager's local  
                                    // list.
           return data_manager.getDataSet( my_index );
         }

         public void finished()      // when finished() is called, we can       
         {                           // quickly get a reference to the local   
                                    // copy of the DataSet from the ViewManager
           DataSet ds = data_manager.getDataSet(my_index);
           if ( ds != null )
           {
             ds = (DataSet)ds.clone();
             observers.notifyIObservers( this, ds );
           }

           record[my_index].setEnabled(true);
           FixLabels();
         }
       };
      worker.start();
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
      show_box[my_index].setEnabled(false);

      if ( check_box.isSelected() )               // use thread to get and
      {                                           // display the DataSet
                                                  // if it's not already shown
        if ( viewers[ my_index ] == null  ||
            !viewers[ my_index ].isVisible() )    // make a new viewer
        { 
          // create a subclass of SwingWorker to get the DataSet
          final SwingWorker worker = new SwingWorker()   //-----INLINE CLASS DEF
          {
            public Object construct() // calling getDataSet will make sure there
            {                         // is a DataSet in the ViewManager's local
                                      // list.
              return data_manager.getDataSet( my_index );
            }

            public void finished()  // when finished() is called, we can
            {                       // quickly get a reference to the local
                                    // copy of the DataSet from the ViewManager
              DataSet ds = data_manager.getDataSet(my_index);
              if ( ds != null )
              {
                viewers[my_index] = new ViewManager( ds, IViewManager.IMAGE );
                viewers[my_index].addWindowListener(
                                            new ViewManagerListener(my_index));
              }
              show_box[my_index].setEnabled(true);
              FixLabels();
            }
          };
          worker.start();
        }
      }
      else
      {
        if ( viewers != null && viewers[my_index]!= null )
          viewers[my_index].destroy();
        auto_box[my_index].setSelected( false );  // Don't show it implies
                                                  // don't auto update it.
        data_manager.setUpdateIgnoreFlag( my_index, true );
        show_box[my_index].setEnabled(true);
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
      JCheckBox check_box = (JCheckBox)(e.getSource());

      if ( check_box.isSelected() )
      {
        if ( viewers[ my_index ] == null  ||
            !viewers[ my_index ].isVisible() )    // make another viewer
        {
          auto_box[my_index].setEnabled(false);
          // create a subclass of SwingWorker to get the DataSet
          final SwingWorker worker = new SwingWorker()   //-----INLINE CLASS DEF
          {
            public Object construct() // calling getDataSet will make sure there
            {                         // is a DataSet in the ViewManager's local
                                      // list.
              return data_manager.getDataSet( my_index );
            }

            public void finished()  // when finished() is called, we can
            {                       // quickly get a reference to the local
                                    // copy of the DataSet from the ViewManager
              DataSet ds = data_manager.getDataSet(my_index);
              if ( ds != null )
              {
                viewers[my_index] = new ViewManager( ds, IViewManager.IMAGE );
                viewers[my_index].addWindowListener(
                                            new ViewManagerListener(my_index));
              }
              auto_box[my_index].setEnabled(true);
              FixLabels();
            }
          };
          worker.start();

        }
        data_manager.setUpdateIgnoreFlag( my_index, false );
        show_box[ my_index ].setSelected( true );   // Auto update implies 
                                                    // we also show it.
      }
      else
        data_manager.setUpdateIgnoreFlag( my_index, true );

      FixLabels();
    }
  }


  /* ----------------------- PartialCheckboxListener ---------------------- */

  private class PartialCheckboxListener implements ActionListener,
                                                   Serializable
  {
    int                 my_index;

    public PartialCheckboxListener( int index )
    {
      my_index = index;
    }

    public void actionPerformed( ActionEvent e )
    {
      JCheckBox check_box = (JCheckBox)(e.getSource());

      if ( check_box.isSelected() )
      {
        selector[my_index] = new PartialDS_Selector( command[my_index] );
        selector[my_index].addIObserver( new PartialDSListener( my_index ) ); 
      }
      else
      {
        selector[my_index].dispose();  
        selector[my_index] = null;
      }

      FixLabels();
    }
  }


  /* ----------------------- DataManagerListener ---------------------- */

  private class DataManagerListener implements ActionListener,
                                               Serializable
  {
    public void actionPerformed( ActionEvent e )       // since this is called
    {                                                  // from a different 
      event_queue.addElement(e);                       // thread, save the 
      Runnable actionRunnable = new Runnable()         // event in a queue and 
      {                                                // use this "Runnable"
        public void run()                              // to actually carry out 
        {                                              // the requested action 
          if ( event_queue.size() <= 0 )               // in the Swing event 
            return;                                    // handling thread.
                                                         
          ActionEvent event = (ActionEvent)event_queue.elementAt(0);
          event_queue.removeElementAt(0);
          run_actionPerformed( event );
        }
      };
      SwingUtilities.invokeLater( actionRunnable );
    }

    public void run_actionPerformed( ActionEvent e )
    {
      String message = e.getActionCommand();
      if ( debug_LDM )
        System.out.println("DataManagerListener got message: " + message );

      if ( message.startsWith( LiveDataManager.DATA_CHANGED ) )
        SetUpGUI();

      else
      { 
        if ( message.startsWith( RemoteDataRetriever.DAS_OFFLINE_STRING )  ||
             message.startsWith( RemoteDataRetriever.DATA_OLD_STRING )    )
        { 
          status_label.setForeground( ALERT_COLOR );
          status_label.setText( message ); 
        }
        else
        {
          status_label.setForeground( FOREGROUND );
          status_label.setText( message ); 
        }

        current_status = message;
      }
    }
  }


  /* ------------------------ ViewManagerListener -------------------- */

  private class ViewManagerListener extends WindowAdapter
  {
    int index;
    public ViewManagerListener( int index )
    {
      this.index = index;
    }

    public void windowClosing(WindowEvent ev)
    {
      if ( index < show_box.length )
        show_box[ index ].setSelected( false );

      if ( index < auto_box.length )
        auto_box[ index ].setSelected( false );

      if ( index < viewers.length )
        viewers [ index ] = null;
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
     instrument_computer += ";;;";
     LiveDataMonitor monitor = new LiveDataMonitor( instrument_computer );

     JFrame frame = new JFrame( "Live Data Monitor" );
     frame.setBounds( 20, 20, 450, 350 );
     frame.getContentPane().add( monitor );
 
     frame.setVisible( true );
  }
}
