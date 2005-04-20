/*
 * File:  FunctionControls.java
 *
 * Copyright (C) 2003 Brent Serum
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
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA and by
 * the National Science Foundation under grant number DMR-0218882.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 * Modified:
 *
 * $Log$
 * Revision 1.51  2005/04/20 21:25:06  dennis
 * Removed unused imports.
 *
 * Revision 1.50  2005/03/28 05:53:59  serumb
 * Add methods for data changed and re-initialization also uses the new
 * class RangeControl.
 *
 * Revision 1.49  2005/03/14 19:23:37  serumb
 * Added calls for getting the button controls and the combobox controls.
 *
 * Revision 1.48  2005/03/11 19:45:42  serumb
 * Added get and set Object State methods and changed the call to
 * set stroke to use an integer key.
 *
 * Revision 1.47  2005/02/04 22:52:54  millermi
 * - ImageListener now listens for
 *   FunctionViewComponent.POINTED_AT_CHANGED to update range values
 *   when a new pointed at graph is selected.
 *
 * Revision 1.46  2005/02/01 03:13:15  millermi
 * - Fixed x and y range control bounds when log axes are selected.
 *
 * Revision 1.45  2005/01/10 16:21:37  rmikk
 * Fixed code to get error bars to correspond to spectra
 *
 * Revision 1.44  2004/12/08 22:03:48  serumb
 * Set the x and y range controls on zoom and reset zoom.
 *
 * Revision 1.43  2004/11/12 03:34:03  millermi
 * - Reversed min/max for y-scale to reflect changed to
 *   fvc.getAxisInformation().
 *
 * Revision 1.42  2004/11/11 19:49:37  millermi
 * - Changed LogScaleUtil functions to match reimplementation.
 *
 * Revision 1.41  2004/11/05 22:02:44  millermi
 * - Revised method for generating log cursor readouts.
 * - Removed getLogScale().
 *
 * Revision 1.40  2004/10/06 20:32:50  serumb
 * Removed the log scale slider control because it is not used with
 * the true log scale.
 *
 * Revision 1.39  2004/09/15 21:55:47  millermi
 * - Updated LINEAR, TRU_LOG, and PSEUDO_LOG setting for AxisInfo class.
 *   Adding a second log required the boolean parameter to be changed
 *   to an int. These changes may affect any ObjectState saved configurations
 *   made prior to this version.
 *
 * Revision 1.38  2004/07/28 19:38:35  robertsonj
 * Changed the curser readouts to read the correct log coordinates depending
 * on which scale is chosen to be logerithmic
 *
 * Revision 1.37  2004/07/02 20:12:32  serumb
 * Added control to set the shift factor for the offset.
 *
 * Revision 1.36  2004/06/16 22:11:21  serumb
 * Repaint components after changes made to the graph.
 *
 * Revision 1.35  2004/06/10 23:28:26  serumb
 * Add a legend control.
 *
 * Revision 1.34  2004/05/20 16:33:53  serumb
 * Removed unused variables.
 *
 * Revision 1.33  2004/04/21 02:34:40  millermi
 * - Fixed bug that incorrectly named selected indices.
 *
 * Revision 1.32  2004/04/16 20:25:41  millermi
 * - Now uses new methods from the IVirtualArrayList1D.
 *
 * Revision 1.31  2004/03/15 23:52:01  dennis
 * Cleaned up imports and changed references to static fields to
 * be interms of the class.
 *
 * Revision 1.30  2004/03/12 22:57:52  serumb
 * Now uses IVirtualArrayList1D in place of IVirtualArray1D.
 *
 * Revision 1.29  2004/03/12 21:08:46  serumb
 * Took out references to DataSetData, replaced them with
 * calls to setAxisInfo from IVirtualArray1D.
 *
 * Revision 1.28  2004/03/12 03:31:47  dennis
 * Moved to package gov.anl.ipns.ViewTools.Components.ViewControls
 *
 * Revision 1.27  2004/03/12 02:55:56  millermi
 * - Changed package, fixed most of the imports.
 *
 * Revision 1.26  2004/03/11 23:35:41  serumb
 * Removed String entry objects.
 *
 * Revision 1.25  2004/03/10 19:34:22  serumb
 * Added kill method.
 *
 * Revision 1.24  2004/03/10 16:38:19  serumb
 * Changed the action listener to listen for the correct
 * message when listening to the axis and annotation
 * overlay controls.
 *
 * Revision 1.23  2004/01/30 22:29:23  millermi
 * - Added new messaging String paths when listened for by action
 *   listeners.
 *
 * Revision 1.22  2004/01/09 21:12:46  serumb
 * Fixed problem with x and y range when in
 * log axes.
 *
 * Revision 1.21  2004/01/09 20:32:56  serumb
 * Utilize getLocalLogWorldCoords to correct log
 * transformations.
 *
 * Revision 1.20  2004/01/06 23:11:15  serumb
 * Put in the correct bounds for all log scale util.
 *
 * Revision 1.19  2004/01/06 22:50:24  serumb
 * Put in the correct bounds for the log scale util.
 *
 * Revision 1.18  2003/12/18 22:42:13  millermi
 * - This file was involved in generalizing AxisInfo2D to
 *   AxisInfo. This change was made so that the AxisInfo
 *   class can be used for more than just 2D axes.
 *
 * Revision 1.17  2003/11/06 01:37:50  serumb
 * Set the initial colors for the error bar and point marker buttons.
 *
 * Revision 1.15  2003/11/05 17:49:51  serumb
 * Changed the cursor output strings to X and Y.
 *
 * Revision 1.14  2003/10/31 18:14:32  dennis
 * Since the frame containing the controls may be reused,
 * call validate() to get the displayed contents updated properly.
 *
 * Revision 1.13  2003/10/31 17:55:06  dennis
 * Added constructor that builds the controls in an existing frame.
 * Changed the close_frame() and display_controls() methods so that
 * the current position of the frame is retained when opening and
 * closing the controls.
 *
 * Revision 1.12  2003/10/21 20:20:01  serumb
 * Now uses the CursorOutputControl.
 *
 * Revision 1.11  2003/09/11 21:21:48  serumb
 * Added control to show the cursor readings.
 *
 * Revision 1.10  2003/08/29 18:58:13  serumb
 * Change the color of the button text for the color buttons to the
 * color selected.
 *
 * Revision 1.9  2003/08/08 21:05:21  serumb
 * Added get_frame() method.
 *
 * Revision 1.8  2003/08/08 18:29:49  serumb
 * Updates x and y range values when message is recieved.
 *
 * Revision 1.7  2003/08/06 19:33:19  serumb
 * Added controls for adding grid lines.
 *
 * Revision 1.6  2003/08/05 23:28:31  serumb
 * Zoom controls adjust to log and linear axes.
 *
 * Revision 1.5  2003/07/31 18:51:20  serumb
 * Changed the log scale listener so it only acts if the
 * logarithmic axies are set.
 *
 * Revision 1.4  2003/07/30 20:54:24  serumb
 * Added control slider for the log scale, and a combobox to set
 * axies logarithmic.
 *
 * Revision 1.3  2003/07/17 20:40:51  serumb
 * Changed the zoom controls to fit better, and allow more
 * freedom for the user.
 *
 * Revision 1.2  2003/07/10 21:46:42  serumb
 * Added controls for zooming on the y axis.
 *
 */
package gov.anl.ipns.ViewTools.Components.ViewControls;

import java.io.Serializable;
import gov.anl.ipns.ViewTools.Panels.Transforms.*; 
import gov.anl.ipns.ViewTools.Components.*;  // IVirtualArrayList1D
import gov.anl.ipns.ViewTools.Components.OneD.*;
import gov.anl.ipns.ViewTools.Components.Transparency.*;  //Axis Overlays
import gov.anl.ipns.ViewTools.Panels.Graph.*;//GraphJPanel
                                                                                                                                               
// component changes
import java.awt.*;
import java.awt.event.*;

// Component location and resizing within the big_picture
import javax.swing.*;
//import javax.swing.border.*;  

/*
 * This class creates the controls and adds them to the control panel.
 */

  public class FunctionControls  implements Serializable,
                                            IPreserveState
  {

  /**
   * "Graph Range" - This constant String is a key for referencing the State
   * information about the x range for the graph to be displayed.
   */
   public static final String GRAPH_RANGE = "Graph Range";

  private transient IVirtualArrayList1D Varray1D;
  private transient FunctionViewComponent fvc;
  private transient GraphJPanel gjp;
  private transient JPanel big_picture = new JPanel();

  private transient JPanel panel1      = new JPanel(  );
  private transient JPanel panel2      = new JPanel(  );
  private transient JPanel panel3      = new JPanel(  );
  private transient JPanel RboxPanel   = new JPanel(  );
  private transient JPanel controlpanel= new JPanel(  );
  private transient JPanel label_panel = new JPanel(  );
  private transient JPanel z_panel     = new JPanel(  );
  private transient String label1      = "Line Selected";
  private transient String label2      = "Line Style";
  private transient String label3      = "Line Width";
  private transient String label4      = "Point Marker";
  private transient String label5      = "Point Marker Size";
  private transient String label6      = "Error Bars";
  private transient String label7      = "Shift";
  private transient String label8      = "Logarithmic Axis";
  private transient String[] lines;
  private transient int[]  SelGraphDSIndx;
  private transient String[] line_type;
  private transient String[] line_width;
  private transient String[] mark_types;
  private transient String[] mark_size;
  private transient String[] bar_types;
  private transient String[] shift_types;
  private transient String[] log_placements;
  private transient ButtonControl LineColor;
  private transient ButtonControl MarkColor;
  private transient ButtonControl ErrorColor;
  private transient int line_index     = 1;
  private transient int linewidth      = 1;
  private transient Box leftBox        = new Box( 1 );
  private transient Box rightBox       = new Box( 1 );
  private transient Box control_box    = new Box( 0 );
  private transient LabelCombobox labelbox1;
  private transient LabelCombobox labelbox2;
  private transient LabelCombobox labelbox3;
  private transient LabelCombobox labelbox4;
  private transient LabelCombobox labelbox5;
  private transient LabelCombobox labelbox6;
  private transient LabelCombobox labelbox7;
  private transient LabelCombobox labelbox8;
  private transient LabelCombobox labelbox9;

  private transient JLabel control_label;
  private transient Font label_font;
  private RangeControl graph_range;
  private transient ControlCheckboxButton axis_checkbox = 
	            new ControlCheckboxButton(true);
  private transient ControlCheckboxButton annotation_checkbox = 
                                    new ControlCheckboxButton(  );
  private transient ControlCheckboxButton legend_checkbox = 
                                    new ControlCheckboxButton(  );
  private transient double log_scale = 10;
  private transient float shift_factor = 1;
  private transient ViewControlsPanel main_panel;
  private transient JFrame the_frame;

  private transient CursorOutputControl cursor;
  private transient ViewControl[] control_list;

  public static final int FRAME_WIDTH  = 480; 
  public static final int FRAME_HEIGHT = 350; 
  
  /**
   *  Constructor that builds the controls in an existing frame.
   */ 
  public FunctionControls( IVirtualArrayList1D varr, 
                           GraphJPanel graph_j_panel,
                           JPanel display_panel, 
                           FunctionViewComponent FVC,
                           JFrame frame ) {
    the_frame = frame;
    main_panel = new ViewControlsPanel();
    Varray1D = varr;
    fvc = FVC;
    gjp = graph_j_panel;
    big_picture = display_panel;
    buildControls();

    main_panel.addViewControl(controlpanel);
    the_frame.setSize( FRAME_WIDTH, FRAME_HEIGHT );
    the_frame.getContentPane().removeAll();
    the_frame.getContentPane().add( (JComponent)main_panel.getPanel() );
    the_frame.validate();
  }

  /**
   *  Constructor that builds the controls in a new frame.
   */ 
  public FunctionControls( IVirtualArrayList1D varr, 
                           GraphJPanel graph_j_panel,
                           JPanel display_panel, 
                           FunctionViewComponent FVC) {
    this( varr, graph_j_panel, display_panel, FVC, 
          new JFrame( "ISAW Function View Controls" ) );
  }

   // setState() and getState() are required by IPreserveState interface
  /**
   * This method will set the current state variables of the object to state
   * variables wrapped in the ObjectState passed in.
   *
   *  @param  new_state
   */
   public void setObjectState( ObjectState new_state )
   {
      Object temp = new_state.get(GRAPH_RANGE);
      if ( temp != null)
      {
        graph_range.setObjectState((ObjectState)temp);
      }
      graph_range.validate();
      graph_range.repaint();
   }   

  /**
   * This method will get the current values of the state variables for this
   * object. These variables will be wrapped in an ObjectState. Keys will be
   * put in alphabetic order.
   */
   public ObjectState getObjectState(boolean isDefault)
   {
     ObjectState state = new ObjectState();
     state.insert( GRAPH_RANGE, graph_range.getObjectState(isDefault) );

     if(! isDefault){
     }

     return state;
    }     
					       
   
  public void buildControls() {
    label_panel.setLayout( new FlowLayout( 1 ) );
    label_font      = new Font( "Times", Font.PLAIN, 16 );
    control_label   = new JLabel( "Controls" );
    control_label.setFont( label_font );
    label_panel.add( control_label );
    
    String group_id;
    
    lines = new String[Varray1D.getNumSelectedGraphs(  )];
    SelGraphDSIndx = new int[Varray1D.getNumSelectedGraphs(  )];
    int index = 0;
    for( int i = 0; i < Varray1D.getNumGraphs(  ); i++ ) {
      if( Varray1D.isSelected(i) )
      {
        group_id   = Varray1D.getGraphTitle( i );
        SelGraphDSIndx[index] = i;
        lines[index++]   = "Group ID:" + group_id;
      }
    }
          
    labelbox1 = new LabelCombobox( label1, lines );
    labelbox1.setBorderVisible(false);    
                                                                               
    line_type      = new String[5];
    line_type[0]   = "Solid";
    line_type[1]   = "Dashed";
    line_type[2]   = "Dotted";
    line_type[3]   = "Dash Dot Dot";
    line_type[4]   = "Transparent";
                                                                                   
    labelbox2   = new LabelCombobox( label2, line_type );
    labelbox2.setBorderVisible(false);                                                                                   
    line_width      = new String[5];
    line_width[0]   = "1";
    line_width[1]   = "2";
    line_width[2]   = "3";
    line_width[3]   = "4";
    line_width[4]   = "5";
    labelbox3       = new LabelCombobox( label3, line_width );
    labelbox3.setBorderVisible(false);                                                                                   
    mark_types      = new String[6];
    mark_types[0]   = "DOT";
    mark_types[1]   = "PLUS";
    mark_types[2]   = "STAR";
    mark_types[3]   = "BOX";
    mark_types[4]   = "CROSS";
    mark_types[5]   = "NO POINT MARKS";
    labelbox4       = new LabelCombobox( label4, mark_types );
    labelbox4.setSelectedIndex( 5 );
    labelbox4.setBorderVisible(false);                                                                                   
    mark_size      = new String[5];
    mark_size[0]   = "1";
    mark_size[1]   = "2";
    mark_size[2]   = "3";
    mark_size[3]   = "4";
    mark_size[4]   = "5";
    labelbox5      = new LabelCombobox( label5, mark_size );
    labelbox5.setSelectedIndex( 1 );
    labelbox5.setBorderVisible(false);                                                                                   
    bar_types      = new String[3];
    bar_types[1]   = "At Points";
    bar_types[2]   = "At Top";
    bar_types[0]   = "None";
    labelbox6      = new LabelCombobox( label6, bar_types );
    labelbox6.setBorderVisible(false);                                                                                   
    shift_types    = new String[3];
    shift_types[0]   = "Diagonal";
    shift_types[1]   = "Vertical";
    shift_types[2]   = "Overlaid";
    labelbox7      = new LabelCombobox( label7, shift_types );
    labelbox7.setSelectedIndex( 2 );
    labelbox7.setBorderVisible(false);

    log_placements = new String[4];
    log_placements[0] = "None";
    log_placements[1] = "X";
    log_placements[2] = "Y";
    log_placements[3] = "X and Y";
    labelbox8      = new LabelCombobox( label8, log_placements );


    String [] factors      = new String[3];
    factors[0]   = "1";
    factors[1]   = "1.5";
    factors[2]   = "2";
    labelbox9      = new LabelCombobox( "Shift Factors", factors );
    labelbox9.setBorderVisible(false);
   
    LineColor   = new ButtonControl( "Line Color" );
    LineColor.setBorderVisible(false);
    MarkColor   = new ButtonControl( "Point Marker Color" );
    MarkColor.setBorderVisible(false);
    MarkColor.getButton().setForeground( Color.red );
    ErrorColor  = new ButtonControl( "Error Bar Color" );
    ErrorColor.setBorderVisible(false);
    ErrorColor.getButton().setForeground( Color.blue );
    axis_checkbox.setTitle( "Axis Overlay" );
    annotation_checkbox.setTitle( "Annotation Overlay" );
    legend_checkbox.setTitle( "Legend Overlay" );
   
    String range_string[] = {"X Range","Y Range"};
    graph_range = new RangeControl(range_string);
    graph_range.setMin(0, gjp.getXmin());
    graph_range.setMax(0, gjp.getXmax());
    graph_range.setMin(1, gjp.getYmin());
    graph_range.setMax(1, gjp.getYmax());
    String the_string[] = {"X ","Y "};
    cursor = new CursorOutputControl(the_string);

    GridLayout G_lout = new GridLayout( 1, 1 );

    panel1.setLayout( G_lout );
    panel2.setLayout( G_lout );
    panel3.setLayout( G_lout );
    panel1.add( LineColor.getButton() );
    panel2.add( MarkColor.getButton() );
    panel3.add( ErrorColor.getButton() );
                                                                                   
    // the left box is the left side of the control panel
    leftBox.add( labelbox1 );
    leftBox.add( labelbox2 );
    leftBox.add( labelbox3 );
    leftBox.add( panel1 );
    leftBox.add( labelbox4 );
    leftBox.add( labelbox5 );
    leftBox.add( panel2 );
    leftBox.add( labelbox6 );
    leftBox.add( panel3 );
    leftBox.add( labelbox7 );
    leftBox.add( labelbox9 );

    control_box.add(leftBox);
    
    rightBox.add( axis_checkbox );
    rightBox.add( annotation_checkbox );
    rightBox.add( legend_checkbox );
    rightBox.add( graph_range );
    rightBox.add( cursor );
    rightBox.add( labelbox8 );
                                                                              
    RboxPanel.setLayout(G_lout);
    RboxPanel.add(rightBox);
                                                                                   
    control_box.add(RboxPanel);
                                                                                   
    controlpanel.setLayout( G_lout );
    controlpanel.add( control_box );
                                                                                   
    labelbox1.addActionListener( new ControlListener(  ) );
    labelbox2.addActionListener( new ControlListener(  ) );
    labelbox3.addActionListener( new ControlListener(  ) );
    labelbox4.addActionListener( new ControlListener(  ) );
    labelbox5.addActionListener( new ControlListener(  ) );
    labelbox6.addActionListener( new ControlListener(  ) );
    LineColor.addActionListener( new ControlListener(  ) );
    MarkColor.addActionListener( new ControlListener(  ) );
    ErrorColor.addActionListener( new ControlListener(  ) );
    axis_checkbox.addActionListener( new ControlListener(  ) );
    annotation_checkbox.addActionListener( new ControlListener(  ) );
    legend_checkbox.addActionListener( new ControlListener(  ) );
    labelbox7.addActionListener( new ControlListener(  ) );
    labelbox9.addActionListener( new ControlListener(  ) );
    labelbox8.addActionListener( new ControlListener(  ) );
    graph_range.addActionListener( new RangeListener(  ) );
    gjp.addActionListener( new ImageListener(  ) );
    fvc.addActionListener( new ImageListener(  ) ); 

    control_list = new ViewControl[17];
    control_list[0] = labelbox1;
    control_list[1] = labelbox2; 
    control_list[2] = labelbox3;
    control_list[3] = labelbox4;
    control_list[4] = labelbox5;
    control_list[5] = labelbox6;
    control_list[6] = LineColor;
    control_list[7] = MarkColor;
    control_list[8] = ErrorColor; 
    control_list[9] = labelbox7;
    control_list[10] = labelbox9;
    control_list[11] = axis_checkbox;
    control_list[12] = annotation_checkbox;
    control_list[13] = legend_checkbox;
    control_list[14] = graph_range;
    control_list[15] = cursor;
    control_list[15] = labelbox8;

  }
 
 /**
   * This Method updates the control that selects the graph to be modified.
   * 
   * @param pin_varray the array containing the graphs
   */  
  public void dataChanged( IVirtualArrayList1D pin_varray ) //pin == "passed in"
  {
    if (Varray1D != pin_varray)
    {
      String group_id;
      Varray1D = pin_varray;
      lines = new String[Varray1D.getNumSelectedGraphs(  )];
      SelGraphDSIndx = new int[Varray1D.getNumSelectedGraphs(  )];
      int index = 0;
      for( int i = 0; i < Varray1D.getNumGraphs(  ); i++ ) {
        if( Varray1D.isSelected(i) )
        {
          group_id   = Varray1D.getGraphTitle( i );
          SelGraphDSIndx[index] = i;
          lines[index++]   = "Group ID:" + group_id;
        }
      }

      labelbox1.setItemList(lines);
    }
  }
  
  /**
    * Method to initialize the View Controls.
    */
  public void reInit()
  {
    // the range control
      LogScaleUtil loggery = new LogScaleUtil(gjp.getPositiveYmin(),
                                              gjp.getYmax(),
                                              gjp.getPositiveYmin(),
                                              gjp.getYmax());
      LogScaleUtil loggerx = new LogScaleUtil(gjp.getPositiveXmin(),
                                              gjp.getXmax(),
                                              gjp.getPositiveXmin(),
                                              gjp.getXmax());

      if(gjp.getLogScaleX() == true && gjp.getLogScaleY() == true) {

        gjp.setZoom_region( loggerx.toDest(graph_range.getMin(0)),
                            loggery.toDest(graph_range.getMax(1)),
                            loggerx.toDest(graph_range.getMax(0)),
                            loggery.toDest(graph_range.getMin(1)) );
      }
      else if(gjp.getLogScaleX() == true && gjp.getLogScaleY() == false) {
        gjp.setZoom_region( loggerx.toDest(graph_range.getMin(0)),
                            graph_range.getMax(1),
                            loggerx.toDest(graph_range.getMax(0)),
                            graph_range.getMin(1) );
      }
      else
      gjp.setZoom_region( graph_range.getMin(0), graph_range.getMax(1),
                          graph_range.getMax(0), graph_range.getMin(1) );
        
   /*
    * sets the line style combo box to the style of the line selected.
    */
     
      line_index = labelbox1.getSelectedIndex(  ) + 1;

      GraphData gd = ( GraphData )gjp.graphs.elementAt( line_index );

     if(
        gjp.getStroke( line_index ) == (
            GraphJPanel.DOTTED) ) {
        labelbox2.setSelectedIndex( 2 );
      } else if(
       gjp.getStroke( line_index ) == (
            GraphJPanel.LINE) ) {
        labelbox2.setSelectedIndex( 0 );
      } else if(
        gjp.getStroke( line_index )==(
            GraphJPanel.DASHED) ) {
       labelbox2.setSelectedIndex( 1 );
      } else if(
        gjp.getStroke( line_index ) == (
            GraphJPanel.DASHDOT) ) {
        labelbox2.setSelectedIndex( 3 );
      } else if(
        gjp.getStroke( line_index ) == (
            GraphJPanel.TRANSPARENT) ) {
        labelbox2.setSelectedIndex( 4 );
      }
     
      /*
        sets the line style combo box to the style of the line selected.
      */
      if( 
        gjp.getStroke( line_index ) == ( 
            GraphJPanel.DOTTED) ) {
        labelbox2.setSelectedIndex( 2 );
      } else if( 
       gjp.getStroke( line_index ) == ( 
            GraphJPanel.LINE) ) {
        labelbox2.setSelectedIndex( 0 );
      } else if( 
        gjp.getStroke( line_index )==( 
            GraphJPanel.DASHED) ) {
        labelbox2.setSelectedIndex( 1 );
      } else if( 
        gjp.getStroke( line_index ) == ( 
            GraphJPanel.DASHDOT) ) {
        labelbox2.setSelectedIndex( 3 );
      } else if( 
        gjp.getStroke( line_index ) == ( 
            GraphJPanel.TRANSPARENT) ) {
        labelbox2.setSelectedIndex( 4 );
      }
      
      /*
        sets the mark size combo box to the mark size of the line selected.
      */
      if( gd.marksize == 1 ) {
        labelbox5.setSelectedIndex( 0 );
      } else if( gd.marksize == 2 ) {
        labelbox5.setSelectedIndex( 1 );
      } else if( gd.marksize == 3 ) {
        labelbox5.setSelectedIndex( 2 );
      } else if( gd.marksize == 4 ) {
        labelbox5.setSelectedIndex( 3 );
      } else if( gd.marksize == 5 ) {
        labelbox5.setSelectedIndex( 4 );
      }
       /*
        sets the mark type combo box to the mark type of the line selected.
      */
      if( gd.marktype == 0 ) {
        labelbox4.setSelectedIndex( 5 );
      } else if( gd.marktype == 1 ) {
        labelbox4.setSelectedIndex( 0 );
      } else if( gd.marktype == 2 ) {
        labelbox4.setSelectedIndex( 1 );
      } else if( gd.marktype == 3 ) {
        labelbox4.setSelectedIndex( 2 );
      } else if( gd.marktype == 4 ) {
        labelbox4.setSelectedIndex( 3 );
      } else if( gd.marktype == 5 ) {
        labelbox4.setSelectedIndex( 4 );
      }
       /*
        sets the line width combo box to the line width 
        of the line selected.
      */
      if( gd.linewidth == 1 ) {
        labelbox3.setSelectedIndex( 0 );
      } else if( gd.linewidth == 2 ) {
        labelbox3.setSelectedIndex( 1 );
      } else if( gd.linewidth == 3 ) {
         labelbox3.setSelectedIndex( 2 );
      } else if( gd.linewidth == 4 ) {
        labelbox3.setSelectedIndex( 3 );
      } else if( gd.linewidth == 5 ) {
        labelbox3.setSelectedIndex( 4 );
      }
       /*
        sets the error bar combo box to the error bar location
        of the line selected.
      */
      if( gd.getErrorLocation(  ) == 0 ) {
        labelbox6.setSelectedIndex( 0 );
      } else if( gd.getErrorLocation(  ) == 11 ) {
        labelbox6.setSelectedIndex( 1 );
      } else if( gd.getErrorLocation(  ) == 12 ) {
        labelbox6.setSelectedIndex( 2 );
      } else if( gd.getErrorLocation(  ) == 13 ) {
        labelbox6.setSelectedIndex( 3 );
      }
       MarkColor.getButton().setForeground( gjp.getMarkColor(line_index) );
       LineColor.getButton().setForeground( gjp.getColor(line_index) );
       ErrorColor.getButton().setForeground( gjp.getErrorColor(line_index) );
  }
  
  public ViewControlsPanel get_panel() {
    return main_panel;
  }
  
  public JFrame get_frame() {
    return the_frame;
  }

  public void display_controls() {
    the_frame.setVisible( true  );  //display the frame
  }

  /**
    * Function that returns an Array of View Controls.
    *
    * @return ViewControl[] array of controls
    */ 
  public ViewControl[] getControlList()
  {
    return control_list;
  }
  
  public void close_frame() {
    // get the current location and reset the bounds, so that 
    // the frame will reappear in the same place.

    Point location =  the_frame.getLocation();
    the_frame.setVisible( false );
    the_frame.setBounds( location.x, location.y, FRAME_WIDTH, FRAME_HEIGHT );
  }
  
  // method to remove panels upon removal of view components 
  public void kill() {
  }

  private class RangeListener implements ActionListener {

    public void actionPerformed(ActionEvent ae)  {
          // System.out.println("Entered: " + graph_range.getTitle() );
          // System.out.println("event " + ae);
          // System.out.println("Min = " + graph_range.getMin(0) );
          // System.out.println("Max = " + graph_range.getMax(0) );
      LogScaleUtil loggery = new LogScaleUtil(gjp.getPositiveYmin(),
                                              gjp.getYmax(),
                                              gjp.getPositiveYmin(),
					      gjp.getYmax());
      LogScaleUtil loggerx = new LogScaleUtil(gjp.getPositiveXmin(),
        			              gjp.getXmax(), 
				              gjp.getPositiveXmin(),
					      gjp.getXmax());

      if(gjp.getLogScaleX() == true && gjp.getLogScaleY() == true) {

        gjp.setZoom_region( loggerx.toDest(graph_range.getMin(0)),
        		    loggery.toDest(graph_range.getMax(1)),
        		    loggerx.toDest(graph_range.getMax(0)),
        		    loggery.toDest(graph_range.getMin(1)) );
      }
      else if(gjp.getLogScaleX() == true && gjp.getLogScaleY() == false) { 
        gjp.setZoom_region( loggerx.toDest(graph_range.getMin(0)), 
        		    graph_range.getMax(1),
        		    loggerx.toDest(graph_range.getMax(0)),
        		    graph_range.getMin(1) );
      }
      else
      gjp.setZoom_region( graph_range.getMin(0), graph_range.getMax(1),
        		  graph_range.getMax(0), graph_range.getMin(1) );
    }
  }
 
 private class ImageListener implements ActionListener {
    //~ Methods ****************************************************************

                                                                                             
    public void actionPerformed( ActionEvent ae ) {
      String message = ae.getActionCommand(  );
       float xmin,xmax,ymin,ymax;
       xmin = gjp.getPositiveXmin();
       xmax = gjp.getXmax();
       ymin = gjp.getPositiveYmin();
       ymax = gjp.getYmax();
       
       LogScaleUtil loggery = new LogScaleUtil(ymin,ymax,ymin,ymax);
       LogScaleUtil loggerx = new LogScaleUtil(xmin,xmax,xmin,xmax); 

      if( message.equals("Reset Zoom") || message.equals("Zoom In") || 
          message.equals(FunctionViewComponent.POINTED_AT_CHANGED) ) {
      
       float x_lower, x_upper, y_lower, y_upper;	      
       CoordBounds range = gjp.getLocalWorldCoords();
       if (range.getY1() > range.getY2())
	       range.invertBounds();
       if (range.getX1() > range.getX2())
       {        
         x_lower = range.getX2();
         x_upper = range.getX1();
       }
       else{
         x_lower = range.getX1();
         x_upper = range.getX2();
       }  
       y_lower = range.getY1();
       y_upper = range.getY2();
       
       // If log, make sure y minimum is positive.
       if (gjp.getLogScaleY()){
         if (ymin >  y_lower)
	      y_lower = ymin;
         // map to log coords
	 y_lower = loggery.toSource(y_lower);
	 y_upper = loggery.toSource(y_upper);
       }
       // If log, make sure x minimum is positive.
       if (gjp.getLogScaleX()){
	 if (xmin >  x_lower)
 	      x_lower = xmin;
         // map to log coords
	 x_lower = loggerx.toSource(x_lower);
	 x_upper = loggerx.toSource(x_upper);
       } 
       
       graph_range.setMin(0, x_lower);
       graph_range.setMax(0, x_upper);
       graph_range.setMin(1, y_lower);
       graph_range.setMax(1, y_upper);

      }	 
      
      else if(message.equals("Cursor Moved")){
         if(gjp.getLogScaleX() == true && gjp.getLogScaleY() == true) {
             cursor.setValue(0,loggerx.toSource(gjp.getCurrent_WC_point().x));
             cursor.setValue(1,loggery.toSource(gjp.getCurrent_WC_point().y));

           }
           else if(gjp.getLogScaleX() == false && gjp.getLogScaleY() == true) {
             cursor.setValue(0,gjp.getCurrent_WC_point().x);
             cursor.setValue(1,loggery.toSource(gjp.getCurrent_WC_point().y));
           }
           else if(gjp.getLogScaleX() == true && gjp.getLogScaleY() == false) {
             cursor.setValue(0,loggerx.toSource(gjp.getCurrent_WC_point().x));
             cursor.setValue(1,gjp.getCurrent_WC_point().y);
           }
           else {
             cursor.setValue(0,gjp.getCurrent_WC_point().x);
             cursor.setValue(1,gjp.getCurrent_WC_point().y);

           } 
      }
    }
 }
  private class ControlListener implements ActionListener {
    //~ Methods ****************************************************************
    public void actionPerformed( ActionEvent ae ) {
      String message = ae.getActionCommand(  );

      // System.out.println( "action command: " + message );
      //System.out.println( "action event: " + ae.getSource() );
     
      /*
         listens for the color buttons and displays a color chooser
         and sets the object to the appropriate color.
     */  
      if( message.equals( "BUTTON_PRESSED" ) ) {
        if( ae.getSource(  ) == LineColor ) {
          Color c = JColorChooser.showDialog( null, "color chart", Color.black );

          if( c != null ) {
            LineColor.getButton().setForeground( c );
            gjp.setColor( c, line_index, true );
          }
        }

        if( ae.getSource(  ) == MarkColor ) {
          Color m = JColorChooser.showDialog( null, "color chart", Color.black );

          if( m != null ) {
            MarkColor.getButton().setForeground( m );
            gjp.setMarkColor( m, line_index, true );
          }
        }
        if( ae.getSource(  ) == ErrorColor ) {
          Color e = JColorChooser.showDialog( null, "color chart", Color.black );

          if( e != null ) {
            ErrorColor.getButton().setForeground( e );
            gjp.setErrorColor( e, line_index, true );
          }
        }
      } else if( message.equals( "Button Pressed" )) {
 
        if( ae.getSource() instanceof ControlCheckboxButton )
           {
             ControlCheckboxButton ccb = (ControlCheckboxButton)ae.getSource();
             if( ccb.getTitle().equals("Axis Overlay") )
             {
               AxisOverlay2D axis = (AxisOverlay2D)big_picture.getComponent(
                                    big_picture.getComponentCount() - 2 );
               axis.editGridLines();
             }
             else if( ccb.getTitle().equals("Annotation Overlay") )
             {
               AnnotationOverlay note = (AnnotationOverlay)
                                 big_picture.getComponent(
                                 big_picture.getComponentCount() - 3 );
               note.editAnnotation();
             }
             else if( ccb.getTitle().equals("Legend Overlay") )
             {
               LegendOverlay legend = (LegendOverlay)
                                 big_picture.getComponent(
                                 big_picture.getComponentCount() - 4 );
               legend.editLegend();
             }
            fvc.paintComponents( );
           }
        /* 
           listens for the edit annotation button and brings up an edit 
           annotation pane.
        */
      } else if( message.equals( "COMBOBOX_CHANGED" ) ) {
        // System.out.println("action" + labelbox1.getSelectedItem());
        // System.out.println("index" + labelbox1.getSelectedIndex());
        // System.out.println("source " + ae.getSource());

        /* 
           gets the index for the line selected. The index is used for the
           line that is pointed at so 1 is added to the line index for 
           selected lines.
        */
        if( ae.getSource(  ) == labelbox1 ) {
          
          line_index = labelbox1.getSelectedIndex(  ) + 1;
          GraphData gd = ( GraphData )gjp.graphs.elementAt( line_index );
     
          /*
            sets the line style combo box to the style of the line selected.
          */
          if( 
            gjp.getStroke( line_index ) == ( 
                GraphJPanel.DOTTED) ) {
            labelbox2.setSelectedIndex( 2 );
          } else if( 
           gjp.getStroke( line_index ) == ( 
                GraphJPanel.LINE) ) {
            labelbox2.setSelectedIndex( 0 );
          } else if( 
            gjp.getStroke( line_index )==( 
                GraphJPanel.DASHED) ) {
            labelbox2.setSelectedIndex( 1 );
          } else if( 
            gjp.getStroke( line_index ) == ( 
                GraphJPanel.DASHDOT) ) {
            labelbox2.setSelectedIndex( 3 );
          } else if( 
            gjp.getStroke( line_index ) == ( 
                GraphJPanel.TRANSPARENT) ) {
            labelbox2.setSelectedIndex( 4 );
          }
          
          /*
            sets the mark size combo box to the mark size of the line selected.
          */
          if( gd.marksize == 1 ) {
            labelbox5.setSelectedIndex( 0 );
          } else if( gd.marksize == 2 ) {
            labelbox5.setSelectedIndex( 1 );
          } else if( gd.marksize == 3 ) {
            labelbox5.setSelectedIndex( 2 );
          } else if( gd.marksize == 4 ) {
            labelbox5.setSelectedIndex( 3 );
          } else if( gd.marksize == 5 ) {
            labelbox5.setSelectedIndex( 4 );
          }

          /*
            sets the mark type combo box to the mark type of the line selected.
          */
          if( gd.marktype == 0 ) {
            labelbox4.setSelectedIndex( 5 );
          } else if( gd.marktype == 1 ) {
            labelbox4.setSelectedIndex( 0 );
          } else if( gd.marktype == 2 ) {
            labelbox4.setSelectedIndex( 1 );
          } else if( gd.marktype == 3 ) {
            labelbox4.setSelectedIndex( 2 );
          } else if( gd.marktype == 4 ) {
            labelbox4.setSelectedIndex( 3 );
          } else if( gd.marktype == 5 ) {
            labelbox4.setSelectedIndex( 4 );
          }

          /*
            sets the line width combo box to the line width 
            of the line selected.
          */
          if( gd.linewidth == 1 ) {
            labelbox3.setSelectedIndex( 0 );
          } else if( gd.linewidth == 2 ) {
            labelbox3.setSelectedIndex( 1 );
          } else if( gd.linewidth == 3 ) {
            labelbox3.setSelectedIndex( 2 );
          } else if( gd.linewidth == 4 ) {
            labelbox3.setSelectedIndex( 3 );
          } else if( gd.linewidth == 5 ) {
            labelbox3.setSelectedIndex( 4 );
          }

          /*
            sets the error bar combo box to the error bar location
            of the line selected.
          */
          if( gd.getErrorLocation(  ) == 0 ) {
            labelbox6.setSelectedIndex( 0 );
          } else if( gd.getErrorLocation(  ) == 11 ) {
            labelbox6.setSelectedIndex( 1 );
          } else if( gd.getErrorLocation(  ) == 12 ) {
            labelbox6.setSelectedIndex( 2 );
          } else if( gd.getErrorLocation(  ) == 13 ) {
            labelbox6.setSelectedIndex( 3 );
          }

           MarkColor.getButton().setForeground( gjp.getMarkColor(line_index) );
           LineColor.getButton().setForeground( gjp.getColor(line_index) );
           ErrorColor.getButton().setForeground( gjp.getErrorColor(line_index) );
          
        /*
          Sets the appropriate line style
        */
        } else if( ae.getSource(  ) == labelbox2 ) {
          if( labelbox2.getSelectedItem(  ).equals( "Solid" ) ) {
            gjp.setTransparent(false, line_index, false);
            gjp.setStroke( 
              GraphJPanel.LINE, line_index, true );
          }

          if( labelbox2.getSelectedItem(  ).equals( "Dashed" ) ) {
            gjp.setTransparent(false, line_index, false);
            gjp.setStroke( 
              GraphJPanel.DASHED, line_index, true );
          }

          if( labelbox2.getSelectedItem(  ).equals( "Dotted" ) ) {
            gjp.setTransparent(false, line_index, false);
            gjp.setStroke( 
              GraphJPanel.DOTTED, line_index, true );
          }

          if( labelbox2.getSelectedItem(  ).equals( "Dash Dot Dot" ) ) {
             gjp.setTransparent(false, line_index, false);
             gjp.setStroke( 
              GraphJPanel.DASHDOT, line_index, true );  
          }

          if( labelbox2.getSelectedItem(  ).equals( "Transparent" ) ) {
             gjp.setTransparent(true, line_index, true);
             gjp.setStroke( 
              GraphJPanel.TRANSPARENT, line_index, true );
          }

        /*
           sets the appropriate line width
        */
        } else if( ae.getSource(  ) == labelbox3 ) {
          linewidth = labelbox3.getSelectedIndex(  ) + 1;

          gjp.setLineWidth( linewidth, line_index, true );

          if( labelbox2.getSelectedItem(  ).equals( "Solid" ) ) {
            gjp.setStroke( 
              GraphJPanel.LINE, line_index, true );
          }

          if( labelbox2.getSelectedItem(  ).equals( "Dashed" ) ) {
            gjp.setStroke( 
              GraphJPanel.DASHED, line_index, true );
          }

          if( labelbox2.getSelectedItem(  ).equals( "Dotted" ) ) {
            gjp.setStroke( 
              GraphJPanel.DOTTED, line_index, true );
          }

          if( labelbox2.getSelectedItem(  ).equals( "Dash Dot Dot" ) ) {
            gjp.setStroke( 
              GraphJPanel.DASHDOT, line_index, true );
          }
        /* 
          Listens for a point marker change and sets the appropriate
          point marker type.
        */  

        } else if( ae.getSource(  ) == labelbox4 ) {
          if( labelbox4.getSelectedItem(  ).equals( "DOT" ) ) {
            gjp.setMarkType( GraphJPanel.DOT, line_index, true );
          } else if( labelbox4.getSelectedItem(  ).equals( "PLUS" ) ) {
            gjp.setMarkType( GraphJPanel.PLUS, line_index, true );
          } else if( labelbox4.getSelectedItem(  ).equals( "STAR" ) ) {
            gjp.setMarkType( GraphJPanel.STAR, line_index, true );
          } else if( labelbox4.getSelectedItem(  ).equals( "BOX" ) ) {
            gjp.setMarkType( GraphJPanel.BOX, line_index, true );
          } else if( labelbox4.getSelectedItem(  ).equals( "CROSS" ) ) {
            gjp.setMarkType( GraphJPanel.CROSS, line_index, true );
          } else if( 
            labelbox4.getSelectedItem(  ).equals( "NO POINT MARKS" ) ) {
            gjp.setMarkType( 0, line_index, true );
          }

        /* 
          Listens for a point marker size  change and sets the appropriate
          point marker size.
        */  
        } else if( ae.getSource(  ) == labelbox5 ) {
          if( labelbox5.getSelectedItem(  ).equals( "1" ) ) {
            gjp.setMarkSize( 1, line_index, true );
          } else if( labelbox5.getSelectedItem(  ).equals( "2" ) ) {
            gjp.setMarkSize( 2, line_index, true );
          } else if( labelbox5.getSelectedItem(  ).equals( "3" ) ) {
            gjp.setMarkSize( 3, line_index, true );
          } else if( labelbox5.getSelectedItem(  ).equals( "4" ) ) {
            gjp.setMarkSize( 4, line_index, true );
          } else if( labelbox5.getSelectedItem(  ).equals( "5" ) ) {
            gjp.setMarkSize( 4, line_index, true );
          }

        /* 
          Listens for a error bar change and sets the appropriate
          error bar location.
        */  
        } else if( ae.getSource(  ) == labelbox6 ) {
          //System.out.println("zoom region:"+ gjp.getZoom_region());
          //CoordBounds data_bound = getGlobalWorldCoords();
          //data_bound.getBounds()
          if( labelbox6.getSelectedItem(  ).equals( "None" ) ) {
            gjp.setErrors( Varray1D.getErrorValues
                  ( SelGraphDSIndx[line_index - 1]  ), 0, 
                           line_index, true );
          } else if( labelbox6.getSelectedItem(  ).equals( "At Points" ) ) {
            gjp.setErrors( Varray1D.getErrorValues
                  ( SelGraphDSIndx[line_index - 1]  ), 
                           GraphJPanel.ERROR_AT_POINT, line_index, true );
          } else if( labelbox6.getSelectedItem(  ).equals( "At Top" ) ) {
            gjp.setErrors( Varray1D.getErrorValues
                  ( SelGraphDSIndx[line_index - 1] ),
                           GraphJPanel.ERROR_AT_TOP, line_index, true );
          }

        /* 
          Listens for a line shift change and sets the appropriate
          line /shift.
        */  
        } else if( ae.getSource( ) == labelbox7) {
            if ( labelbox7.getSelectedItem( ).equals( "Diagonal" ))
              { 
                gjp.setMultiplotOffsets((int)(20 * shift_factor),
                                        (int)( 20 * shift_factor));
                gjp.repaint();
              } 
            else if( labelbox7.getSelectedItem( ).equals( "Vertical" ))
              {
                gjp.setMultiplotOffsets(0,(int)(20 * shift_factor));
                gjp.repaint();
              }
            else if( labelbox7.getSelectedItem( ).equals( "Overlaid" ))
              {
                gjp.setMultiplotOffsets(0,0);
                gjp.repaint();
              }
            else {
              gjp.setMultiplotOffsets(0,0);
              gjp.repaint();
            }
        } else if( ae.getSource( ) == labelbox9) {
            if (labelbox9.getSelectedItem( ).equals( "1" ))
            shift_factor = 1;
            if (labelbox9.getSelectedItem( ).equals( "1.5" ))
            shift_factor = 1.5f;
            if (labelbox9.getSelectedItem( ).equals( "2" ))
            shift_factor = 2;
            
            labelbox7.setSelectedIndex(labelbox7.getSelectedIndex());

        } else if( ae.getSource( ) == labelbox8) {
            AxisOverlay2D note = (AxisOverlay2D)big_picture.getComponent(
                                 big_picture.getComponentCount() - 2);

            if ( labelbox8.getSelectedItem( ).equals( "None" ))
            {
              Varray1D.setAxisInfo(AxisInfo.X_AXIS,
                        Varray1D.getAxisInfo(AxisInfo.X_AXIS).getMin(),
                        Varray1D.getAxisInfo(AxisInfo.X_AXIS).getMax(),
                        Varray1D.getAxisInfo(AxisInfo.X_AXIS).getLabel(),
                        Varray1D.getAxisInfo(AxisInfo.X_AXIS).getUnits(),
                        AxisInfo.LINEAR);
              Varray1D.setAxisInfo(AxisInfo.Y_AXIS,
                        Varray1D.getAxisInfo(AxisInfo.Y_AXIS).getMin(),
                        Varray1D.getAxisInfo(AxisInfo.Y_AXIS).getMax(),
                        Varray1D.getAxisInfo(AxisInfo.Y_AXIS).getLabel(),
                        Varray1D.getAxisInfo(AxisInfo.Y_AXIS).getUnits(),
                        AxisInfo.LINEAR);

              note.setXScale( AxisInfo.LINEAR );
              note.setYScale( AxisInfo.LINEAR );
              note.setTwoSided(false);
              gjp.setLogScaleX(false);
              gjp.setLogScaleY(false);
              fvc.paintComponents(  );
            }
            else if( labelbox8.getSelectedItem().equals( "X" ))
            {
              Varray1D.setAxisInfo(AxisInfo.X_AXIS,
                        Varray1D.getAxisInfo(AxisInfo.X_AXIS).getMin(),
                        Varray1D.getAxisInfo(AxisInfo.X_AXIS).getMax(),
                        Varray1D.getAxisInfo(AxisInfo.X_AXIS).getLabel(),
                        Varray1D.getAxisInfo(AxisInfo.X_AXIS).getUnits(),
                        AxisInfo.TRU_LOG);
              Varray1D.setAxisInfo(AxisInfo.Y_AXIS,
                        Varray1D.getAxisInfo(AxisInfo.Y_AXIS).getMin(),
                        Varray1D.getAxisInfo(AxisInfo.Y_AXIS).getMax(),
                        Varray1D.getAxisInfo(AxisInfo.Y_AXIS).getLabel(),
                        Varray1D.getAxisInfo(AxisInfo.Y_AXIS).getUnits(),
                        AxisInfo.LINEAR);

              note.setXScale( AxisInfo.TRU_LOG );
              note.setYScale( AxisInfo.LINEAR );
              note.setTwoSided(false);
              gjp.setLogScaleX(true);
              gjp.setLogScaleY(false);
              fvc.paintComponents(  );
            }  
            else if( labelbox8.getSelectedItem().equals( "Y" ))
            {
              Varray1D.setAxisInfo(AxisInfo.X_AXIS,
                        Varray1D.getAxisInfo(AxisInfo.X_AXIS).getMin(),
                        Varray1D.getAxisInfo(AxisInfo.X_AXIS).getMax(),
                        Varray1D.getAxisInfo(AxisInfo.X_AXIS).getLabel(),
                        Varray1D.getAxisInfo(AxisInfo.X_AXIS).getUnits(),
                        AxisInfo.LINEAR);
              Varray1D.setAxisInfo(AxisInfo.Y_AXIS,
                        Varray1D.getAxisInfo(AxisInfo.Y_AXIS).getMin(),
                        Varray1D.getAxisInfo(AxisInfo.Y_AXIS).getMax(),
                        Varray1D.getAxisInfo(AxisInfo.Y_AXIS).getLabel(),
                        Varray1D.getAxisInfo(AxisInfo.Y_AXIS).getUnits(),
                        AxisInfo.LINEAR);

              note.setYScale( AxisInfo.TRU_LOG );
              note.setXScale( AxisInfo.LINEAR );
              note.setTwoSided(false);
              gjp.setLogScaleY(true);
              gjp.setLogScaleX(false);
              fvc.paintComponents(  );
            }  
            else if( labelbox8.getSelectedItem().equals( "X and Y" ))
            {
              Varray1D.setAxisInfo(AxisInfo.X_AXIS,
                        Varray1D.getAxisInfo(AxisInfo.X_AXIS).getMin(),
                        Varray1D.getAxisInfo(AxisInfo.X_AXIS).getMax(),
                        Varray1D.getAxisInfo(AxisInfo.X_AXIS).getLabel(),
                        Varray1D.getAxisInfo(AxisInfo.X_AXIS).getUnits(),
                        AxisInfo.TRU_LOG);
              Varray1D.setAxisInfo(AxisInfo.Y_AXIS,
                        Varray1D.getAxisInfo(AxisInfo.Y_AXIS).getMin(),
                        Varray1D.getAxisInfo(AxisInfo.Y_AXIS).getMax(),
                        Varray1D.getAxisInfo(AxisInfo.Y_AXIS).getLabel(),
                        Varray1D.getAxisInfo(AxisInfo.Y_AXIS).getUnits(),
                        AxisInfo.TRU_LOG);

              note.setXScale( AxisInfo.TRU_LOG );
              note.setYScale( AxisInfo.TRU_LOG );
              note.setTwoSided(false);
              gjp.setLogScaleX(true);
              gjp.setLogScaleY(true);
              fvc.paintComponents( );

            }
	    
	    // Reset range of x and y since log and linear may require different
	    // ranges (log sets min to least positive number in negative)
            AxisInfo xinfo = fvc.getAxisInformation(AxisInfo.X_AXIS);
            AxisInfo yinfo = fvc.getAxisInformation(AxisInfo.Y_AXIS);
	    graph_range.setMin(0, xinfo.getMin());
	    graph_range.setMax(0, xinfo.getMax());
	    graph_range.setMin(1, yinfo.getMin());
	    graph_range.setMax(1, yinfo.getMax());
          }  
      } 
        /* 
          Listens for an overlay change and sets the appropriate overlay.
        */  
      else if( message.equals( ControlCheckboxButton.CHECKBOX_CHANGED ) ) {
        ControlCheckboxButton control = 
                              ( ControlCheckboxButton )ae.getSource(  );
        int bpsize              = big_picture.getComponentCount(  );
        if( control.getTitle(  ).equals( "Annotation Overlay" ) ) {

          AnnotationOverlay note = ( AnnotationOverlay )big_picture.getComponent
                  ( big_picture.getComponentCount(  ) - 3 );

          if( !control.isSelected(  ) ) {
            note.setVisible( false );
          } else {
            note.setVisible( true );
            note.getFocus(  );
          }
        }
      
        else if( control.getTitle(  ).equals( "Legend Overlay" ) ) {
          LegendOverlay legend = ( LegendOverlay )big_picture.getComponent
                  ( big_picture.getComponentCount(  ) - 4 );

          if( !control.isSelected(  ) ) {
            legend.setVisible( false );
          } else {
            legend.setVisible( true );
            legend.getFocus(  );
          }
        }
      
        else if(control.getTitle().equals( "Axis Overlay" ) ) {
          JPanel back = (JPanel)big_picture.getComponent( bpsize - 1);
          if( !control.isSelected() ) {
            big_picture.getComponent(bpsize - 2).setVisible(false);

            back.getComponent(1).setVisible(false);
            back.getComponent(2).setVisible(false);
            back.getComponent(3).setVisible(false);
            back.getComponent(4).setVisible(false);
          }
          else {
           back.getComponent(1).setVisible(true);
           back.getComponent(2).setVisible(true);
           back.getComponent(3).setVisible(true);
           back.getComponent(4).setVisible(true);
           big_picture.getComponent(bpsize - 2).setVisible(true);
          }
        }
       fvc.paintComponents(  );
      }  
       fvc.paintComponents(  );
    }
  }

/*--------------------------- main -----------------------------------------
 *  Main program for testing purposes only.
 */

   public static void main( String[] args ) {
/*
    DataSet[] DSS = ( new IsawGUI.Util(  ) ).loadRunfile( 
           "/IPNShome/serumb/ISAW/SampleRuns/GPPD12358.RUN" );    
   
    int k = DSS.length - 1;

    System.out.println(" DSS " + DSS.length);

    DSS[k].setSelectFlag( 0, true );
    DSS[k].setSelectFlag( 3, true );

    DataSetData ArrayHandler = new DataSetData( DSS[k] );
  
    AxisInfo xaxis = ArrayHandler.getAxisInfo( AxisInfo.X_AXIS );
    AxisInfo yaxis = ArrayHandler.getAxisInfo( AxisInfo.Y_AXIS );
    System.out.println(
      "ArrayHandler info" + xaxis.getMax(  ) + "," + xaxis.getMin(  ) + "," +
      yaxis.getMax(  ) + "," + yaxis.getMin(  ));
  
    if( java.lang.Float.isNaN( xaxis.getMax(  ) ) ) {
      try {
        int c = System.in.read(  );
      } catch( Exception sss ) {}
    }
    FunctionViewComponent fvc = new FunctionViewComponent( ArrayHandler );

    IVirtualArrayList1D Varray1D = fvc.getArray(); 
    GraphJPanel graph_panel = new GraphJPanel();
    JPanel main_panel = new JPanel();
    
    FunctionControls fcontrols = new FunctionControls(Varray1D, graph_panel,
                                                      main_panel, fvc); 
    fcontrols.display_controls();
*/
  }  

}


