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
 *           Chris Bouzek <coldfusion78@yahoo.com>
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
 * Revision 1.3  2003/07/17 20:40:51  serumb
 * Changed the zoom controls to fit better, and allow more
 * freedom for the user.
 *
 * Revision 1.2  2003/07/10 21:46:42  serumb
 * Added controls for zooming on the y axis.
 *
 */
package DataSetTools.components.View.ViewControls;
                                                                                                                            
import DataSetTools.components.ParametersGUI.*;

import DataSetTools.components.ui.*;                                                                                                                                               
import DataSetTools.components.View.*;  // IVirtualArray1D
import DataSetTools.components.View.OneD.*;
import DataSetTools.components.View.Transparency.*;  //Axis Overlays
import DataSetTools.components.View.ViewControls.*;
                                                                                                                                               
import DataSetTools.components.image.*;  //GraphJPanel & ImageJPanel & CoordJPanel
                                                                                                                                               
import DataSetTools.dataset.*;
                                                                                                                                               
import DataSetTools.math.*;
                                                                                                                                               
import DataSetTools.util.*;  //floatPoint2D FloatFilter
                                                                                                                                               
// component changes
                                                                                                                                               
import java.applet.Applet;
                                                                                                                                               
import java.awt.*;
import java.awt.Rectangle;
import java.awt.color.ColorSpace;
import java.awt.event.*;
                                                                                                                                               
// Component location and resizing within the big_picture
import java.awt.event.ComponentAdapter.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.*;
                                                                                                                                               
import java.io.Serializable;
                                                                                                                                               
import java.lang.*;
import java.lang.Object.*;
                                                                                                                                               
import java.util.*;
                                                                                                                                               
import javax.swing.*;
import javax.swing.event.*;
                                                                                                                                               
                                                                             

/*
 * This class creates the controls and adds them to the control panel.
 */

  public class FunctionControls {

  private IVirtualArray1D Varray1D;
  private GraphJPanel gjp;
  private JPanel big_picture;

  private JPanel panel1      = new JPanel(  );
  private JPanel panel2      = new JPanel(  );
  private JPanel panel3      = new JPanel(  );
  private JPanel RboxPanel   = new JPanel(  );
  private JPanel boxPanel    = new JPanel(  );
  private JPanel TFP         = new JPanel(  );
  private JPanel ZBP         = new JPanel(  );
  private JPanel controlpanel= new JPanel(  );
  private JPanel label_panel = new JPanel(  );
  private JPanel z_panel     = new JPanel(  );
  private String label1      = "Line Selected";
  private String label2      = "Line Style";
  private String label3      = "Line Width";
  private String label4      = "Point Marker";
  private String label5      = "Point Marker Size";
  private String label6      = "Error Bars";
  private String label7      = "Shift";
  private JComboBox LineBox  = new JComboBox(  );
  private JComboBox LineStyleBox = new JComboBox(  );
  private JComboBox LineWidthBox = new JComboBox(  );
  private JComboBox PointMarkerBox = new JComboBox(  );
  private JComboBox PointMarkerSizeBox = new JComboBox(  );
  private JComboBox ErrorBarBox = new JComboBox(  );
  private JComboBox ShiftBox = new JComboBox(  );
  private String[] lines;
  private String[] line_type;
  private String[] line_width;
  private String[] mark_types;
  private String[] mark_size;
  private String[] bar_types;
  private String[] shift_types;
  private ButtonControl LineColor;
  private ButtonControl MarkColor;
  private ButtonControl ErrorColor;
  private ButtonControl annotationButton;
  private ButtonControl ZoomButton;
  private int line_index     = 1;
  private int linewidth      = 1;
  private JColorChooser choosecolors = new JColorChooser( Color.black );
  private Box leftBox        = new Box( 1 );
  private Box rightBox       = new Box( 1 );
  private Box box1           = new Box( 1 );
  private Box z_box          = new Box( 0 );
  private Box t_box          = new Box( 0 );
  private Box t1_box         = new Box( 0 );
  private Box t2_box         = new Box( 0 );
  private Box t3_box         = new Box( 0 );
  private Box T_Box          = new Box( 1 );
  private Box control_box    = new Box( 0 );
  private LabelCombobox labelbox1;
  private LabelCombobox labelbox2;
  private LabelCombobox labelbox3;
  private LabelCombobox labelbox4;
  private LabelCombobox labelbox5;
  private LabelCombobox labelbox6;
  private LabelCombobox labelbox7;
  private JLabel z_begin;
  private JLabel z_end;
  private JLabel yz_begin;
  private JLabel yz_end;
  private JLabel control_label;
  private Font label_font;
  private StringEntry start_field;
  private StringEntry end_field;
  private StringEntry y_start_field;
  private StringEntry y_end_field;
  private TextRangeUI x_range; 
  private TextRangeUI y_range; 
  private Box vert_box = new Box(1);
  private ControlCheckbox axis_checkbox = new ControlCheckbox( true );
  private ControlCheckbox annotation_checkbox = new ControlCheckbox(  );

  private ViewControlsPanel main_panel;
  private JFrame the_frame = new JFrame( "ISAW Function View Controls" );
  
  public FunctionControls(IVirtualArray1D varr, GraphJPanel graph_j_panel,
                          JPanel display_panel) {
    main_panel = new ViewControlsPanel();
    Varray1D = varr;
    gjp = graph_j_panel;
    big_picture = display_panel;

    buildControls();

    main_panel.addViewControl(controlpanel);
  }

  public void buildControls() {
    label_panel.setLayout( new FlowLayout( 1 ) );
    label_font      = new Font( "Times", Font.PLAIN, 16 );
    control_label   = new JLabel( "Controls" );
    control_label.setFont( label_font );
    label_panel.add( control_label );
    
    int group_id;
    
    lines = new String[Varray1D.getNumlines(  )];
//?
    for( int i = 0; i < Varray1D.getNumlines(  ); i++ ) {
      group_id   = Varray1D.getGroupID( i );
      lines[i]   = "Group ID:" + group_id;
    }
                                                                                   
    //LineBox = new JComboBox(lines);
    labelbox1 = new LabelCombobox( label1, lines );
                                                                                   
    line_type      = new String[5];
    line_type[0]   = "Solid";
    line_type[1]   = "Dashed";
    line_type[2]   = "Dotted";
    line_type[3]   = "Dash Dot Dot";
    line_type[4]   = "Transparent";
                                                                                   
    //LineStyleBox = new JComboBox(line_type);
    labelbox2   = new LabelCombobox( label2, line_type );
                                                                                   
    line_width      = new String[5];
    line_width[0]   = "1";
    line_width[1]   = "2";
    line_width[2]   = "3";
    line_width[3]   = "4";
    line_width[4]   = "5";
    labelbox3       = new LabelCombobox( label3, line_width );
                                                                                   
    mark_types      = new String[6];
    mark_types[0]   = "DOT";
    mark_types[1]   = "PLUS";
    mark_types[2]   = "STAR";
    mark_types[3]   = "BOX";
    mark_types[4]   = "CROSS";
    mark_types[5]   = "NO POINT MARKS";
    labelbox4       = new LabelCombobox( label4, mark_types );
    labelbox4.setSelected( 5 );
                                                                                   
    mark_size      = new String[5];
    mark_size[0]   = "1";
    mark_size[1]   = "2";
    mark_size[2]   = "3";
    mark_size[3]   = "4";
    mark_size[4]   = "5";
    labelbox5      = new LabelCombobox( label5, mark_size );
                                                                                   
    bar_types      = new String[3];
    bar_types[1]   = "At Points";
    bar_types[2]   = "At Top";
    bar_types[0]   = "None";
    labelbox6      = new LabelCombobox( label6, bar_types );
                                                                                   
    shift_types    = new String[3];
    shift_types[0]   = "Diagonal";
    shift_types[1]   = "Vertical";
    shift_types[2]   = "Overlaid";
    labelbox7      = new LabelCombobox( label7, shift_types );
    labelbox7.setSelected( 2 );
                                                                                   
    LineColor   = new ButtonControl( "Line Color" );
    MarkColor   = new ButtonControl( "Point Marker Color" );
    ErrorColor  = new ButtonControl( "Error Bar Color" );
    
    axis_checkbox.setText( "Axis Overlay" );
    annotation_checkbox.setText( "Annotation Overlay" );
    annotationButton = new ButtonControl( "Edit Annotations" );

    x_range = new TextRangeUI("X Range", 
                      Varray1D.getAxisInfo(AxisInfo2D.XAXIS ).getMin(),
                      Varray1D.getAxisInfo( AxisInfo2D.XAXIS ).getMax());
    y_range = new TextRangeUI("Y Range",Varray1D.getAxisInfo(
                      AxisInfo2D.YAXIS ).getMin(), Varray1D.getAxisInfo(
                      AxisInfo2D.YAXIS ).getMax()); 

    GridLayout G_lout = new GridLayout( 1, 1 );
                                                                                     panel1.setLayout( G_lout );
    panel2.setLayout( G_lout );
    panel3.setLayout( G_lout );
    panel1.add( LineColor.button );
    panel2.add( MarkColor.button );
    panel3.add( ErrorColor.button );
                                                                                   
    // the left box is the left side of the control panel
    leftBox.add( labelbox1.theBox );
    leftBox.add( labelbox2.theBox );
    leftBox.add( labelbox3.theBox );
    leftBox.add( panel1 );
    leftBox.add( labelbox4.theBox );
    leftBox.add( labelbox5.theBox );
    leftBox.add( panel2 );
    leftBox.add( labelbox6.theBox );
    leftBox.add( panel3 );
                                                                                   
    LineBox              = labelbox1.cbox;
    LineStyleBox         = labelbox2.cbox;
    LineWidthBox         = labelbox3.cbox;
    PointMarkerBox       = labelbox4.cbox;
    PointMarkerSizeBox   = labelbox5.cbox;
    ErrorBarBox          = labelbox6.cbox;
    ShiftBox             = labelbox7.cbox;

    control_box.add(leftBox);

    vert_box.add( x_range );
    vert_box.add( y_range );

    TFP.setLayout( G_lout );
    TFP.add( vert_box );
                                                                               
    rightBox.add( axis_checkbox );
    rightBox.add( annotation_checkbox );
    rightBox.add( annotationButton );
    rightBox.add( TFP );
    rightBox.add( labelbox7 );
                                                                                   
    RboxPanel.setLayout(G_lout);
    RboxPanel.add(rightBox);
                                                                                   
    control_box.add(RboxPanel);
                                                                                   
    controlpanel.setLayout( G_lout );
    controlpanel.add( control_box );
                                                                                   
    LineBox.addActionListener( new ControlListener(  ) );
    LineStyleBox.addActionListener( new ControlListener(  ) );
    LineWidthBox.addActionListener( new ControlListener(  ) );
    PointMarkerBox.addActionListener( new ControlListener(  ) );
    PointMarkerSizeBox.addActionListener( new ControlListener(  ) );
    ErrorBarBox.addActionListener( new ControlListener(  ) );
    LineColor.addActionListener( new ControlListener(  ) );
    MarkColor.addActionListener( new ControlListener(  ) );
    ErrorColor.addActionListener( new ControlListener(  ) );
    axis_checkbox.addActionListener( new ControlListener(  ) );
    annotation_checkbox.addActionListener( new ControlListener(  ) );
    annotationButton.addActionListener( new ControlListener(  ) );
    ShiftBox.addActionListener( new ControlListener(  ) );
    x_range.addActionListener( new x_rangeListener(  ) );
    y_range.addActionListener( new y_rangeListener(  ) );
    gjp.addActionListener( new ImageListener(  ) );                                                                             
  }

  public ViewControlsPanel get_panel() {
    return main_panel;
  }

  public void display_controls() {

    the_frame.setBounds( 600, 0, 580, 330 );
    the_frame.getContentPane().add( (JComponent)main_panel.getPanel() );

    the_frame.setVisible( true  );  //display the frame
  }
  public void close_frame() {
    the_frame.setVisible( false );
  }
  
  private class x_rangeListener implements ActionListener {

    public void actionPerformed( ActionEvent ae ) {
          // System.out.println("Entered: " + x_range.getText() );
          // System.out.println("Min = " + x_range.getMin() );
          // System.out.println("Max = " + x_range.getMax() );
           gjp.setZoom_region(x_range.getMin(),y_range.getMax(),
                             x_range.getMax(), y_range.getMin());

         }
  }

  private class y_rangeListener implements ActionListener {

    public void actionPerformed( ActionEvent ae ) {
         //  System.out.println("Entered: " +y_range.getText() );
         //  System.out.println("Min = " + y_range.getMin() );
         //  System.out.println("Max = " + y_range.getMax() );
          gjp.setZoom_region( x_range.getMin(), y_range.getMax(),
                              x_range.getMax(), y_range.getMin());

         }
  }
 private class ImageListener implements ActionListener {
    //~ Methods ****************************************************************
                                                                                             
    public void actionPerformed( ActionEvent ae ) {
      String message = ae.getActionCommand(  );
                                                                                             
      if( message == CoordJPanel.RESET_ZOOM ) {
        //System.out.println("Sending SELECTED_CHANGED" );
        x_range.setMin(Varray1D.getAxisInfo(AxisInfo2D.XAXIS ).getMin()); 
        x_range.setMax(Varray1D.getAxisInfo(AxisInfo2D.XAXIS ).getMax()); 
        y_range.setMin(Varray1D.getAxisInfo(AxisInfo2D.YAXIS ).getMin()); 
        y_range.setMax(Varray1D.getAxisInfo(AxisInfo2D.YAXIS ).getMax()); 
      }
    }
 }
  private class ControlListener implements ActionListener {
    //~ Methods ****************************************************************
                                                                                                   
    public void actionPerformed( ActionEvent ae ) {
      String message = ae.getActionCommand(  );

      //System.out.println( "action command: " + message );
      //System.out.println( "action event: " + ae );
     
      /*
         listens for the color buttons and displays a color chooser
         and sets the object to the appropriate color.
     */  
      if( message.equals( "BUTTON_PRESSED" ) ) {
        if( ae.getSource(  ) == LineColor ) {
          Color c = choosecolors.showDialog( null, "color chart", Color.black );

          if( c != null ) {
            gjp.setColor( c, line_index, true );
          }
        }

        if( ae.getSource(  ) == MarkColor ) {
          Color m = choosecolors.showDialog( null, "color chart", Color.black );

          if( m != null ) {
            gjp.setMarkColor( m, line_index, true );
          }
        }
        if( ae.getSource(  ) == ErrorColor ) {
          Color e = choosecolors.showDialog( null, "color chart", Color.black );

          if( e != null ) {
            gjp.setErrorColor( e, line_index, true );
          }
        }

        /* 
           listens for the edit annotation button and brings up an edit 
           annotation pane.
        */
        if( ae.getSource(  ) == annotationButton ) {
          AnnotationOverlay note = ( AnnotationOverlay )big_picture.getComponent
                        (big_picture.getComponentCount(  ) - 3 );

          note.editAnnotation(  );

          //repaints overlays accurately	
          paintComponents( big_picture.getGraphics(  ) );
        }

      } else if( message.equals( "comboBoxChanged" ) ) {
        // System.out.println("action" + LineBox.getSelectedItem());
        // System.out.println("index" + LineBox.getSelectedIndex());
        // System.out.println("source " + ae.getSource());

        /* 
           gets the index for the line selected. The index is used for the
           line that is pointed at so 1 is added to the line index for 
           selected lines.
        */
        if( ae.getSource(  ) == LineBox ) {
          line_index = LineBox.getSelectedIndex(  ) + 1;

          GraphData gd = ( GraphData )gjp.graphs.elementAt( line_index );
         
          /*
            sets the line style combo box to the style of the line selected.
          */
          if( 
            gjp.getStroke( line_index ).equals( 
                gjp.strokeType( gjp.DOTTED, line_index ) ) ) {
            LineStyleBox.setSelectedIndex( 2 );
          } else if( 
           gjp.getStroke( line_index ).equals( 
                gjp.strokeType( gjp.LINE, line_index ) ) ) {
            LineStyleBox.setSelectedIndex( 0 );
          } else if( 
            gjp.getStroke( line_index ).equals( 
                gjp.strokeType( gjp.DASHED, line_index ) ) ) {
            LineStyleBox.setSelectedIndex( 1 );
          } else if( 
            gjp.getStroke( line_index ).equals( 
                gjp.strokeType( gjp.DASHDOT, line_index ) ) ) {
            LineStyleBox.setSelectedIndex( 3 );
          } else if( 
            gjp.getStroke( line_index ).equals( 
                gjp.strokeType( gjp.TRANSPARENT, line_index ) ) ) {
            LineStyleBox.setSelectedIndex( 4 );
          }
          
          /*
            sets the mark size combo box to the mark size of the line selected.
          */
          if( gd.marksize == 1 ) {
            PointMarkerSizeBox.setSelectedIndex( 0 );
          } else if( gd.marksize == 2 ) {
            PointMarkerSizeBox.setSelectedIndex( 1 );
          } else if( gd.marksize == 3 ) {
            PointMarkerSizeBox.setSelectedIndex( 2 );
          } else if( gd.marksize == 4 ) {
            PointMarkerSizeBox.setSelectedIndex( 3 );
          } else if( gd.marksize == 5 ) {
            PointMarkerSizeBox.setSelectedIndex( 4 );
          }

          /*
            sets the mark type combo box to the mark type of the line selected.
          */
          if( gd.marktype == 0 ) {
            PointMarkerBox.setSelectedIndex( 5 );
          } else if( gd.marktype == 1 ) {
            PointMarkerBox.setSelectedIndex( 0 );
          } else if( gd.marktype == 2 ) {
            PointMarkerBox.setSelectedIndex( 1 );
          } else if( gd.marktype == 3 ) {
            PointMarkerBox.setSelectedIndex( 2 );
          } else if( gd.marktype == 4 ) {
            PointMarkerBox.setSelectedIndex( 3 );
          } else if( gd.marktype == 5 ) {
            PointMarkerBox.setSelectedIndex( 4 );
          }

          /*
            sets the line width combo box to the line width 
            of the line selected.
          */
          if( gd.linewidth == 1 ) {
            LineWidthBox.setSelectedIndex( 0 );
          } else if( gd.linewidth == 2 ) {
            LineWidthBox.setSelectedIndex( 1 );
          } else if( gd.linewidth == 3 ) {
            LineWidthBox.setSelectedIndex( 2 );
          } else if( gd.linewidth == 4 ) {
            LineWidthBox.setSelectedIndex( 3 );
          } else if( gd.linewidth == 5 ) {
            LineWidthBox.setSelectedIndex( 4 );
          }

          /*
            sets the error bar combo box to the error bar location
            of the line selected.
          */
          if( gd.getErrorLocation(  ) == 0 ) {
            ErrorBarBox.setSelectedIndex( 0 );
          } else if( gd.getErrorLocation(  ) == 11 ) {
            ErrorBarBox.setSelectedIndex( 1 );
          } else if( gd.getErrorLocation(  ) == 12 ) {
            ErrorBarBox.setSelectedIndex( 2 );
          } else if( gd.getErrorLocation(  ) == 13 ) {
            ErrorBarBox.setSelectedIndex( 3 );
          }

        /*
          Sets the appropriate line style
        */
        } else if( ae.getSource(  ) == LineStyleBox ) {
          if( LineStyleBox.getSelectedItem(  ).equals( "Solid" ) ) {
            gjp.setTransparent(false, line_index, false);
            gjp.setStroke( 
              gjp.strokeType( gjp.LINE, line_index ), line_index, true );
          }

          if( LineStyleBox.getSelectedItem(  ).equals( "Dashed" ) ) {
            gjp.setTransparent(false, line_index, false);
            gjp.setStroke( 
              gjp.strokeType( gjp.DASHED, line_index ), line_index, true );
          }

          if( LineStyleBox.getSelectedItem(  ).equals( "Dotted" ) ) {
            gjp.setTransparent(false, line_index, false);
            gjp.setStroke( 
              gjp.strokeType( gjp.DOTTED, line_index ), line_index, true );
          }

          if( LineStyleBox.getSelectedItem(  ).equals( "Dash Dot Dot" ) ) {
             gjp.setTransparent(false, line_index, false);
             gjp.setStroke( 
              gjp.strokeType( gjp.DASHDOT, line_index ), line_index, true );  
          }

          if( LineStyleBox.getSelectedItem(  ).equals( "Transparent" ) ) {
             gjp.setTransparent(true, line_index, true);
             gjp.setStroke( 
              gjp.strokeType( gjp.TRANSPARENT, line_index ), line_index, true );  
          }

        /*
           sets the appropriate line width
        */
        } else if( ae.getSource(  ) == LineWidthBox ) {
          linewidth = LineWidthBox.getSelectedIndex(  ) + 1;

          gjp.setLineWidth( linewidth, line_index, true );

          if( LineStyleBox.getSelectedItem(  ).equals( "Solid" ) ) {
            gjp.setStroke( 
              gjp.strokeType( gjp.LINE, line_index ), line_index, true );
          }

          if( LineStyleBox.getSelectedItem(  ).equals( "Dashed" ) ) {
            gjp.setStroke( 
              gjp.strokeType( gjp.DASHED, line_index ), line_index, true );
          }

          if( LineStyleBox.getSelectedItem(  ).equals( "Dotted" ) ) {
            gjp.setStroke( 
              gjp.strokeType( gjp.DOTTED, line_index ), line_index, true );
          }

          if( LineStyleBox.getSelectedItem(  ).equals( "Dash Dot Dot" ) ) {
            gjp.setStroke( 
              gjp.strokeType( gjp.DASHDOT, line_index ), line_index, true );
          }
        /* 
          Listens for a point marker change and sets the appropriate
          point marker type.
        */  

        } else if( ae.getSource(  ) == PointMarkerBox ) {
          if( PointMarkerBox.getSelectedItem(  ).equals( "DOT" ) ) {
            gjp.setMarkType( gjp.DOT, line_index, true );
          } else if( PointMarkerBox.getSelectedItem(  ).equals( "PLUS" ) ) {
            gjp.setMarkType( gjp.PLUS, line_index, true );
          } else if( PointMarkerBox.getSelectedItem(  ).equals( "STAR" ) ) {
            gjp.setMarkType( gjp.STAR, line_index, true );
          } else if( PointMarkerBox.getSelectedItem(  ).equals( "BOX" ) ) {
            gjp.setMarkType( gjp.BOX, line_index, true );
          } else if( PointMarkerBox.getSelectedItem(  ).equals( "CROSS" ) ) {
            gjp.setMarkType( gjp.CROSS, line_index, true );
          } else if( 
            PointMarkerBox.getSelectedItem(  ).equals( "NO POINT MARKS" ) ) {
            gjp.setMarkType( 0, line_index, true );
          }

        /* 
          Listens for a point marker size  change and sets the appropriate
          point marker size.
        */  
        } else if( ae.getSource(  ) == PointMarkerSizeBox ) {
          if( PointMarkerSizeBox.getSelectedItem(  ).equals( "1" ) ) {
            gjp.setMarkSize( 1, line_index, true );
          } else if( PointMarkerSizeBox.getSelectedItem(  ).equals( "2" ) ) {
            gjp.setMarkSize( 2, line_index, true );
          } else if( PointMarkerSizeBox.getSelectedItem(  ).equals( "3" ) ) {
            gjp.setMarkSize( 3, line_index, true );
          } else if( PointMarkerSizeBox.getSelectedItem(  ).equals( "4" ) ) {
            gjp.setMarkSize( 4, line_index, true );
          } else if( PointMarkerSizeBox.getSelectedItem(  ).equals( "5" ) ) {
            gjp.setMarkSize( 4, line_index, true );
          }

        /* 
          Listens for a error bar change and sets the appropriate
          error bar location.
        */  
        } else if( ae.getSource(  ) == ErrorBarBox ) {
          //System.out.println("zoom region:"+ gjp.getZoom_region());
          //CoordBounds data_bound = getGlobalWorldCoords();
          //data_bound.getBounds()
          if( ErrorBarBox.getSelectedItem(  ).equals( "None" ) ) {
            gjp.setErrors( Varray1D.getErrorValues( line_index - 1  ), 0, 
                           line_index, true );
          } else if( ErrorBarBox.getSelectedItem(  ).equals( "At Points" ) ) {
            gjp.setErrors( Varray1D.getErrorValues( line_index - 1  ), 
                           gjp.ERROR_AT_POINT, line_index, true );
          } else if( ErrorBarBox.getSelectedItem(  ).equals( "At Top" ) ) {
            gjp.setErrors( Varray1D.getErrorValues( line_index - 1 ),
                           gjp.ERROR_AT_TOP, line_index, true );
          }

        /* 
          Listens for a line shift change and sets the appropriate
          line shift.
        */  
        } else if( ae.getSource( ) == ShiftBox) {
            if ( ShiftBox.getSelectedItem( ).equals( "Diagonal" ))
              { 
                gjp.setMultiplotOffsets(20,20);
                gjp.repaint();
              } 
            else if( ShiftBox.getSelectedItem( ).equals( "Vertical" ))
              {
                gjp.setMultiplotOffsets(0,20);
                gjp.repaint();
              }
            else if( ShiftBox.getSelectedItem( ).equals( "Overlaid" ))
              {
                gjp.setMultiplotOffsets(0,0);
                gjp.repaint();
              }
            else {
              gjp.setMultiplotOffsets(0,0);
              gjp.repaint();
            }
          } 
         
      } 
        /* 
          Listens for an overlay change and sets the appropriate overlay.
        */  
      else if( message.equals( "CHECKBOX_CHANGED" ) ) {
        ControlCheckbox control = ( ControlCheckbox )ae.getSource(  );
        int bpsize              = big_picture.getComponentCount(  );

        if( control.getText(  ).equals( "Annotation Overlay" ) ) {

          AnnotationOverlay note = ( AnnotationOverlay )big_picture.getComponent( 
              big_picture.getComponentCount(  ) - 3 );

          if( !control.isSelected(  ) ) {
            note.setVisible( false );
          } else {
            note.setVisible( true );
            note.getFocus(  );
          }
        }
        else if(control.getText(  ).equals( "Axis Overlay") ) {
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
      paintComponents( big_picture.getGraphics(  ) );
      }
    }
  }

/*--------------------------- main -----------------------------------------
 *  Main program for testing purposes only.
 */

   public static void main( String[] args ) {

    DataSet[] DSS = ( new IsawGUI.Util(  ) ).loadRunfile( 
           "/IPNShome/serumb/ISAW/SampleRuns/GPPD12358.RUN" );    
   
    int k = DSS.length - 1;

    System.out.println(" DSS " + DSS.length);

    DSS[k].setSelectFlag( 0, true );
    DSS[k].setSelectFlag( 3, true );

    DataSetData ArrayHandler = new DataSetData( DSS[k] );
  
    AxisInfo2D xaxis = ArrayHandler.getAxisInfo( true );
    AxisInfo2D yaxis = ArrayHandler.getAxisInfo( false );                            

    System.out.println(
      "ArrayHandler info" + xaxis.getMax(  ) + "," + xaxis.getMin(  ) + "," +
      yaxis.getMax(  ) + "," + yaxis.getMin(  ));
  
    if( java.lang.Float.isNaN( xaxis.getMax(  ) ) ) {
      try {
        int c = System.in.read(  );
      } catch( Exception sss ) {}
    }
    FunctionViewComponent fvc = new FunctionViewComponent( ArrayHandler );

    IVirtualArray1D Varray1D = fvc.getArray(); 
    GraphJPanel graph_panel = new GraphJPanel();
    JPanel main_panel = new JPanel();
    JFrame f = new JFrame( "ISAW Function View Controls" );
    
    FunctionControls fcontrols = new FunctionControls(Varray1D, graph_panel,
                                                      main_panel);                   
    fcontrols.display_controls();
   // f.setBounds( 0, 0, 580, 330 );
                                                                                   
   // Container c = f.getContentPane(  );
                                                                                   
   // c.add( ((JComponent)fcontrols.get_panel(  ).getPanel()) );
                                                                                  
   // f.show(  );  //display the frame
  }  
   private void paintComponents( Graphics g ) {
    //big_picture.revalidate();
    for( int i = big_picture.getComponentCount(  ); i > 0; i-- ) {
      if( big_picture.getComponent( i - 1 ).isVisible(  ) ) {
        big_picture.getComponent( i - 1 ).update( g );
      }
    }
                                                                                 
    big_picture.getParent(  ).getParent(  ).getParent(  ).
                              getParent(  ).repaint(  );
   } 

}






 


