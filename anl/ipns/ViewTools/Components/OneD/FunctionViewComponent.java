/*
 * File FunctionViewComponent.java
 *
 * 
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
 * 
 *           
 * Contact : Dennis Mikkelson <mikkelsond@uwstout.edu>
 *           Department of Mathematics, Statistics and Computer Science
 *           University of Wisconsin-Stout
 *           Menomonie, WI 54751, USAB
 *
 * This work was supported by the National Science Foundation under grant
 * number DMR-0218882, and by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 * Modified:
 *
 *  $Log$
 *  Revision 1.18  2003/08/05 23:24:31  serumb
 *  Now check for linear and log axes.
 *
 *  Revision 1.17  2003/07/31 16:50:34  serumb
 *  Draw the pointed at line to the correct zoomed region.
 *
 *  Revision 1.16  2003/07/30 20:58:44  serumb
 *  Implement LogAxisAddible2D.
 *
 *  Revision 1.15  2003/07/25 14:48:36  dennis
 *  Now implements IZoomTextAddible. (Needed to work with new heirarchy
 *  of interfaces for overlays.)
 *
 *  Revision 1.14  2003/07/17 20:38:54  serumb
 *  Implemented the dataChanged methods, and added methods for
 *  drawing the graphs.
 *
 *  Revision 1.13  2003/07/10 21:51:01  serumb
 *  Added control to split pane for showing the function controls
 *  and took out all all references to data sets.
 *
 *  Revision 1.12  2003/07/08 16:34:48  serumb
 *  Took out the controls and moved them to DataSetTools/
 *  components/View/ViewControls/FunctionControls.java.
 *
 *  Revision 1.10  2003/07/02 17:21:22  serumb
 *  Added a control for line shift change, added code comments,
 *  and moved the controls variables to buildViewControls().
 * 
 *
 */
package DataSetTools.components.View.OneD;

import DataSetTools.components.ParametersGUI.*;
import DataSetTools.components.View.*;  // IVirtualArray1D
import DataSetTools.components.View.TwoD.*;
import DataSetTools.components.View.Transparency.*;  //Axis Overlays
import DataSetTools.components.View.ViewControls.*;
import DataSetTools.components.View.TwoD.*;
import DataSetTools.components.image.*;  //GraphJPanel & CoordJPanel
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
import java.beans.PropertyChangeEvent;
import java.beans.*;

/**
 * This class allows the user to view data in the form of an image. Meaning
 * is given to the data by way of overlays, which add calibration, selection,
 * and annotation abilities.
 */
public class FunctionViewComponent implements IFunctionComponent1D,
                                              ActionListener, 
                                              IZoomTextAddible, 
                                              IAxisAddible2D,
                                              ILogAxisAddible2D 
{
  private IVirtualArray1D Varray1D;  //An object containing our array of data
  private Point[] selectedset;  //To be returned by getSelectedSet()   
  private Vector Listeners   = null;
  private JPanel big_picture = new JPanel();
  private GraphJPanel gjp;
  private final int MAX_GRAPHS = 16;
 
  // for component size and location adjustments
  //private ComponentAltered comp_listener;
  private Rectangle regioninfo;
  private CoordBounds local_bounds;
  private CoordBounds global_bounds;
  private Vector transparencies = new Vector(  );
  private int precision;
  private Font font;
  private LinkedList controls = new LinkedList(  );
  private int linewidth      = 1;
  private int line_index     = 0;
  private FunctionControls mainControls;
  private boolean draw_pointed_at = false;
  private boolean isLinear = true;
  /**
   * Constructor that takes in a virtual array and creates an graphjpanel
   * to be viewed in a border layout.
   */
  public FunctionViewComponent( IVirtualArray1D varr ) {

    Varray1D    = varr;  // Get reference to varr
    precision   = 4;
    font        = FontUtil.LABEL_FONT2;
    gjp         = new GraphJPanel(  );
   
  //  buildViewControls();

/*    //Make gjp correspond to the data in f_array
    int num_lines = varr.getNumlines(  );
    boolean bool  = false;
    for( int i = 1; i < num_lines+1; i++ ) {
       gjp.setData( varr.getXValues( i-1 ), varr.getYValues( i-1 ), i, bool );

      if( i >= ( num_lines - 2 ) ) {
        bool = true;
      }

      gjp.setErrors( varr.getErrorValues( i-1 ), 0, i, true );

    }

*/

    gjp.setBackground( Color.white );

    // set initial line styles
    if( varr.getNumlines(  ) > 1 ) {
      gjp.setColor( Color.blue, 2, true );
      gjp.setStroke( gjp.strokeType( gjp.LINE, 2 ), 2, true );
      gjp.setLineWidth( linewidth, 2, false );
    }

    if( varr.getNumlines(  ) > 2 ) {
      gjp.setColor( Color.green, 3, false );
      gjp.setStroke( gjp.strokeType( gjp.LINE, 3 ), 3, true );
      gjp.setLineWidth( linewidth, 3, false );
    }

    ImageListener gjp_listener = new ImageListener(  );

    gjp.addActionListener( gjp_listener );

    ComponentAltered comp_listener = new ComponentAltered(  );

    gjp.addComponentListener( comp_listener );

    regioninfo      = new Rectangle( gjp.getBounds(  ) );
    local_bounds    = gjp.getLocalWorldCoords(  ).MakeCopy(  );
    global_bounds   = gjp.getGlobalWorldCoords(  ).MakeCopy(  );
//7 dd
    setAxisInfo();

    Listeners = new Vector(  );

    buildViewComponent( gjp );  // initializes big_picture to jpanel containing
                                // the background and transparencies
    DrawSelectedGraphs();
    if(draw_pointed_at)
      DrawPointedAtGraph();

    mainControls = new FunctionControls(varr, gjp, getDisplayPanel());
   // buildViewControls( gjp );
  }

  // getAxisInfo(), getRegionInfo(), getTitle(), getPrecision(), getFont() 
  // all required since this component implements IAxisAddible2D

  /**
   * This method initializes the world coords.
   */
  public void setAxisInfo() {
    AxisInfo2D xinfo = getAxisInfo( AxisInfo2D.XAXIS );
    AxisInfo2D yinfo = getAxisInfo( AxisInfo2D.YAXIS );

    gjp.initializeWorldCoords( 
      new CoordBounds( 
        xinfo.getMin(  ), yinfo.getMax(  ),
        xinfo.getMax(  ), yinfo.getMin(  ) ) );
  }
 
  public AxisInfo2D getAxisInfo( boolean isX ) {
    // if true, return x info
    if( isX) {
      if(gjp.getLogScaleX() == true) {
        return new AxisInfo2D( 
        gjp.getLocalLogWorldCoords(gjp.getScale() ).getX1(  ),
        gjp.getLocalLogWorldCoords(gjp.getScale() ).getX2(  ),
        Varray1D.getAxisInfo( AxisInfo2D.XAXIS ).getLabel(  ),
        Varray1D.getAxisInfo( AxisInfo2D.XAXIS ).getUnits(  ),
        Varray1D.getAxisInfo( AxisInfo2D.XAXIS ).getIsLinear(  ) );
      }
      else
      return new AxisInfo2D( 
        gjp.getLocalWorldCoords(  ).getX1(  ),
        gjp.getLocalWorldCoords(  ).getX2(  ),
        Varray1D.getAxisInfo( AxisInfo2D.XAXIS ).getLabel(  ),
        Varray1D.getAxisInfo( AxisInfo2D.XAXIS ).getUnits(  ),
        Varray1D.getAxisInfo( AxisInfo2D.XAXIS ).getIsLinear(  ) );
    }

    // if false return y info
    if(gjp.getLogScaleY() == true) {
      return new AxisInfo2D( 
      gjp.getLocalLogWorldCoords(gjp.getScale() ).getY1(  ),
      gjp.getLocalLogWorldCoords(gjp.getScale() ).getY2(  ),
      Varray1D.getAxisInfo( AxisInfo2D.YAXIS ).getLabel(  ),
      Varray1D.getAxisInfo( AxisInfo2D.YAXIS ).getUnits(  ),
      Varray1D.getAxisInfo( AxisInfo2D.YAXIS ).getIsLinear(  ) );
    }
    else
    return new AxisInfo2D( 
      gjp.getLocalWorldCoords(  ).getY1(  ),
      gjp.getLocalWorldCoords(  ).getY2(  ),
      Varray1D.getAxisInfo( AxisInfo2D.YAXIS ).getLabel(  ),
      Varray1D.getAxisInfo( AxisInfo2D.YAXIS ).getUnits(  ),
      Varray1D.getAxisInfo( AxisInfo2D.YAXIS ).getIsLinear(  ) );
    }

   
  /**
   * This method returns a rectangle containing the location and size
   * of the grapgjpanel.
   *
   *  @return The region info about the graphjpanel
   */
  public Rectangle getRegionInfo(  ) {
    return regioninfo;
  }

  /**
   * This method will return the title given to the image as specified by
   * the Virtual Array
   *
   *  @return title stored in Virtual Array
   */
  public String getTitle(  ) {
    return Varray1D.getTitle(  );
  }

  /**
   * This method will return the precision specified by the user. Precision
   * will be assumed to be 4 if not specified. The overlays will call
   * this method to determine the precision.
   *
   *  @return precision of displayed values
   */
  public int getPrecision(  ) {
    return precision;
  }

  /**
   * This method will return the font used on by the overlays. The axis overlay
   * will call this to determine what font to use.
   *
   *  @return font of displayed values
   */
  public Font getFont(  ) {
    return font;
  }

  /**
   * This method will return the local coordinate bounds of the center
   * jpanel. To be implemented, the center may have to be a coordjpanel.
   */
  public CoordBounds getLocalCoordBounds(  ) {
    //return local_bounds;
    return gjp.getLocalWorldCoords(  ).MakeCopy(  );
  }

  /**
   * This method will return the global coordinate bounds of the center
   * jpanel. To be implemented, the center may have to be a coordjpanel.
   */
  public CoordBounds getGlobalCoordBounds(  ) {
    //return global_bounds;
    return gjp.getGlobalWorldCoords(  ).MakeCopy(  );
  }

  /**
   * This method will return the virtual array. 
   */
  public IVirtualArray1D getArray(  ) {
    return Varray1D;
  }

  /**
   * This method will return the graph JPanel. 
   */
  public GraphJPanel getGraphJPanel(  ) {
    return gjp;
  }
  
  public double getLogScale()
  {
    return mainControls.getLogScale();
  }
  /**
   * This method adjusts the crosshairs on the graphjpanel.
   * setPointedAt is called from the viewer when another component
   * changes the selected point.
   *
   *  @param  pt
   */
  public void setPointedAt( Point pt ) {
    System.out.println( "Entering: void setPointedAt( Point pt )" );
    System.out.println( "X value = " + pt.getX(  ) );
    System.out.println( "Y value = " + pt.getY(  ) );

    //Type cast Point pt  into  floatPoint2D fpt
    floatPoint2D fpt = new floatPoint2D( ( float )pt.x, ( float )pt.y );

    //set the cursor position on GraphJPanel
    gjp.setCurrent_WC_point( fpt );

    System.out.println( "" );
  }

  /**
   * This method creates a selected region to be displayed over the graphjpanel
   * by an overlay.
   *
   *  @param  pts
   */
  public void setSelectedSet( Point[] pts ) {
    // implement after selection overlay has been created
    System.out.println( "Entering: void setSelectedSet( Point[] coords )" );
    System.out.println( "" );
  }

  /**
   * This method will be called to notify this component of a change in data.
   */
  public void dataChanged(  ) {
/*    for(int i = 0; i < Varray1D.getNumlines(); i++)
    {
      float[] x_array = Varray1D.getXValues( i );
      float[] y_array = Varray1D.getYValues( i );

      gjp.setData( x_array, y_array, i, true );
      gjp.setErrors( Varray1D.getErrorValues( i ), 0, i, true );
    }
*/
       gjp.clearData();
       mainControls.close_frame();
       DrawSelectedGraphs();
       if(draw_pointed_at)
       DrawPointedAtGraph();
/*    else{
       mainControls.close_frame();
       DrawSelectedGraphs();
       mainControls = new FunctionControls(Varray1D, gjp, getDisplayPanel());
    }*/
    //if(getDisplayPanel().getGraphics() != null)
    //  paintComponents(getDisplayPanel().getGraphics());
  }
  /**
   * To be continued...
   */
  public void dataChanged( IVirtualArray1D pin_varray )  // pin == "passed in"
   {
    Varray1D = pin_varray;
    //get the complete 2D array of floats from pin_varray
    gjp.clearData();
    mainControls.close_frame();
    DrawSelectedGraphs();
    if(draw_pointed_at)
      DrawPointedAtGraph();  
    mainControls = new FunctionControls(pin_varray, gjp, getDisplayPanel());

    //      System.out.println("Value of first element: " + x_array[0] +
    //							y_array[0] );
    //System.out.println( "Thank you for notifying us" );
    //System.out.println( "" );
  }

  /**
   * Get selected set specified by setSelectedSet. The selection overlay
   * will need to use this method.
   *
   *  @return selectedset
   */
  public Point[] getSelectedSet(  )  //keep the same (for now)
   {
    System.out.println( "Entering: Point[] getSelectedSet()" );
    System.out.println( "" );

    return selectedset;
  }

  /**
   * Method to add a listener to this component.
   *
   *  @param act_listener
   */
  public void addActionListener( ActionListener act_listener ) {
    System.out.print( "Entering: void " );
    System.out.println( "addActionListener( ActionListener act_listener )" );

    for( int i = 0; i < Listeners.size(  ); i++ )  // don't add it if it's

     {
      if( Listeners.elementAt( i ).equals( act_listener ) ) {  // already there

        return;
      }
    }

    Listeners.add( act_listener );  //Otherwise add act_listener
    System.out.println( "" );
  }

  /**
   * Method to remove a listener from this component.
   *
   *  @param act_listener
   */
  public void removeActionListener( ActionListener act_listener ) {
    Listeners.remove( act_listener );
  }

  /**
   * Method to remove all listeners from this component.
   */
  public void removeAllActionListeners(  ) {
    Listeners.removeAllElements(  );
  }

  public JComponent[] getSharedControls(  ) {
    JComponent[] jcontrols = new JComponent[controls.size(  )];

    for( int i = 0; i < controls.size(  ); i++ ) {
      jcontrols[i] = ( JComponent )controls.get( i );
    }

    return jcontrols;
  }

  public JComponent[] getPrivateControls(  ) {
    System.out.println("Entering: JComponent[] getPrivateControls()");
    System.out.println("");
   JPanel[] Res = new JPanel[3];

   JPanel test_p = new JPanel();
   JLabel test_l = new JLabel("Graph View");
   test_p.add(test_l);
   Res[0] = test_p;

   Res[1] = new ControlCheckbox(false);
   ((ControlCheckbox)Res[1]).setText("Function Controls");
   ((ControlCheckbox)Res[1]).addActionListener( new ControlListener() );
    
   Res[2] = new ControlCheckbox(false);
   ((ControlCheckbox)Res[2]).setText("Show Pointed At");
   ((ControlCheckbox)Res[2]).addActionListener( new ControlListener() );
   
   // Res[0]   =  mainControls.get_panel().getPanel();

    return Res;

    // return new JComponent[0];
  }

  public JMenuItem[] getSharedMenuItems(  ) {
    System.out.println( "Entering: JMenuItems[] getSharedMenuItems()" );
    System.out.println( "" );

    return new JMenuItem[0];
  }

  public JMenuItem[] getPrivateMenuItems(  ) {
    System.out.println( "Entering: JMenuItems[] getPrivateMenuItems()" );
    System.out.println( "" );

    return new JMenuItem[0];
  }

  /**
   * Return the "background" or "master" panel
   *
   *  @return JPanel containing graphjpanel in the center of a borderlayout.
   */
  public JPanel getDisplayPanel(  ) {
    return big_picture;
  }

  /*
   *  Gets the current point
   */
  public Point getCurrentPoint(  ) {
    floatPoint2D fpt = new floatPoint2D(  );

    fpt = gjp.getCurrent_WC_point(  );

    Point pt = new Point( ( int )fpt.x, ( int )fpt.y );

    return pt;
  }


  /*
   * Tells all listeners about a new action.
   *
   *  @param  message
   */
  private void sendMessage( String message ) {
    for( int i = 0; i < Listeners.size(  ); i++ ) {
      ActionListener listener = ( ActionListener )Listeners.elementAt( i );

      listener.actionPerformed( new ActionEvent( this, 0, message ) );
    }
  }

  private void paintComponents( Graphics g ) {
   // big_picture.revalidate();

    for( int i = big_picture.getComponentCount(  ); i > 0; i-- ) {
      if( big_picture.getComponent( i - 1 ).isVisible(  )) { 
        big_picture.getComponent( i - 1 ).update( g );
      }
    }
    big_picture.getParent(  ).getParent(  ).getParent(  ).getParent(  ).
                              repaint(  );
  }

  private boolean DrawPointedAtGraph() {
    int pointed_at_line = Varray1D.getPointedAtGraph();
    
    if(pointed_at_line >= 0) {
      Draw_GJP(pointed_at_line, 0, false);
      return true;
    }
    return false;
  }

  private int DrawSelectedGraphs() {
    int num_drawn = 0;
    int draw_count = 0;
    gjp.clearData();
    
    int pointed_at_line = Varray1D.getPointedAtGraph(); 
   
    int num_graphs = Varray1D.getNumGraphs();
    boolean at_max = false;
   // int i = num_graphs - 1;
    for(int i=0; i < num_graphs; i++) {
   // while( !at_max && i >= 0 ) {
      if( Varray1D.isSelected(i) ) {
        draw_count++;
       // if( i == pointed_at_line )
       //   Draw_GJP( i, draw_count, true );
       // else
          Draw_GJP( i, draw_count, false );

        num_drawn++;
      }
      //i--;
      if ( draw_count >= MAX_GRAPHS )
        at_max = true;
    }
    if( DrawPointedAtGraph() )
      num_drawn++;
    
    return num_drawn;
  }

  private void Draw_GJP( int index, int graph_num, boolean pointed_at )
  {
       if(index == 0 && pointed_at == true)
         return; 
       float x[] = Varray1D.getXVals_ofIndex(index);
       float y[] = Varray1D.getYVals_ofIndex(index);
       gjp.setColor( Color.black, graph_num, false );
       gjp.setData( x, y, graph_num, false );     
       gjp.setErrors( Varray1D.getErrorVals_ofIndex( index ), 0, 
                                              graph_num, true);
       if(!draw_pointed_at)
         gjp.setTransparent(true, 0, true);    

  }
   
  // required since implementing ActionListener

  /**
   * To be continued...
   */
  public void actionPerformed( ActionEvent e ) {
    //get POINTED_AT_CHANGED or SELECTED_CHANGED message from e 
    String message = e.getActionCommand(  );

    //Send message to tester 
    if( message.equals(POINTED_AT_CHANGED) ) {
      sendMessage( POINTED_AT_CHANGED );
    }
  }

  /*
   * This method takes in an graphjpanel and puts it into a borderlayout.
   * Overlays are added to allow for calibration, selection, and annotation.
   */
  private void buildViewComponent( GraphJPanel panel ) {
    setAxisInfo();
    int westwidth  = ( font.getSize(  ) * precision ) + 22;
    int southwidth = ( font.getSize(  ) * 3 ) + 22;
    // this will be the background for the master panel
    JPanel background = new JPanel( new BorderLayout(  ) );

    JPanel north = new JPanel( new FlowLayout(  ) );

    north.setPreferredSize( new Dimension( 0, 25 ) );

    JPanel east = new JPanel( new FlowLayout(  ) );

    east.setPreferredSize( new Dimension( 50, 0 ) );

    JPanel south = new JPanel( new FlowLayout(  ) );

    south.setPreferredSize( new Dimension( 0, southwidth ) );

    JPanel west = new JPanel( new FlowLayout(  ) );

    west.setPreferredSize( new Dimension( westwidth, 0 ) );
    //Construct the background JPanel
    background.add( panel, "Center" );
    background.add( north, "North" );
    background.add( west, "West" );
    background.add( south, "South" );
    background.add( east, "East" );

    AnnotationOverlay top = new AnnotationOverlay( this );
    top.setVisible( false );  // initialize this overlay to off.

    AxisOverlay2D bottom = new AxisOverlay2D( this );

    transparencies.add( top );
    transparencies.add( bottom );  // add the transparency the the vector

    JPanel master         = new JPanel(  );
    OverlayLayout overlay = new OverlayLayout( master );

    master.setLayout( overlay );

    for( int trans = 0; trans < transparencies.size(  ); trans++ ) {
      master.add( ( OverlayJPanel )transparencies.elementAt( trans ) );
    }
    master.add( background );

    big_picture = master;
  }

  /*
   * This method constructs the controls required by the FunctionViewComponent
   * it builds the controls to be added to the split pane.
   */
/*  private void buildViewControls( ) {

    }
*/
  /*
   * MAIN - Basic main program to test a FunctionViewComponent object
   */
  public static void main( String[] args ) {
    if( args == null ) {
      System.exit( 0 );
    }

    DataSet[] DSS = ( new IsawGUI.Util(  ) ).loadRunfile( args[0] );

    int k = DSS.length - 1;

    DSS[k].setSelectFlag( 0, true );
    DSS[k].setSelectFlag( 3, true );

    DataSetData ArrayHandler = new DataSetData( DSS[k] );

    AxisInfo2D xaxis = ArrayHandler.getAxisInfo( true );
    AxisInfo2D yaxis = ArrayHandler.getAxisInfo( false );

    System.out.println( 
      "ArrayHandler info" + xaxis.getMax(  ) + "," + xaxis.getMin(  ) + "," +
      yaxis.getMax(  ) + "," + yaxis.getMin(  ) );

    if( java.lang.Float.isNaN( xaxis.getMax(  ) ) ) {
      try {
        int c = System.in.read(  );
      } catch( Exception sss ) {}
    }
    FunctionViewComponent fvc = new FunctionViewComponent( ArrayHandler );

    //A tester frame to throw the bottom and top JPanel into **********
    JFrame f = new JFrame( "ISAW FunctionViewComponent" );

    f.setBounds( 0, 0, 500, 500 );

    Container c = f.getContentPane(  );

    c.add( fvc.getDisplayPanel(  ) );

    f.show(  );  //display the frame

    JFrame f2       = new JFrame( "ISAW GraphViewControls" );
    Container cpain = f2.getContentPane(  );

    cpain.setLayout( new BoxLayout( cpain, BoxLayout.Y_AXIS ) );

    JComponent[] controls = fvc.getSharedControls(  );

    for( int i = 0; i < controls.length; i++ ) {
      cpain.add( controls[i] );
    }

    f2.setBounds( 0, 0, 200, ( 100 * controls.length ) );
    cpain.validate(  );
    f2.show(  );  //display the frame
  }

  //***************************Assistance Classes******************************

  /*
   * ComponentAltered monitors if the graphjpanel has been resized. If so,
   * the regioninfo is updated.
   */
  private class ComponentAltered extends ComponentAdapter {
    //~ Methods ****************************************************************

    public void componentResized( ComponentEvent e ) {
     // System.out.println("Component Resized");
      Component center = e.getComponent(  );
     
      regioninfo = new Rectangle( center.getLocation(  ), center.getSize(  ) );
      paintComponents(big_picture.getGraphics()); 
      /*
         System.out.println("Location = " + center.getLocation() );
         System.out.println("Size = " + center.getSize() );
         System.out.println("class is " + center.getClass() );
       */
    }
  }

  /*
   * ImageListener monitors if the graphjpanel has sent any messages.
   * If so, process the message and relay it to the viewer.
   */
  private class ImageListener implements ActionListener {
    //~ Methods ****************************************************************

    public void actionPerformed( ActionEvent ae ) {
      String message = ae.getActionCommand(  );


      //System.out.println("Graph sent message " + message );
      if( message == CoordJPanel.CURSOR_MOVED ) {
        //System.out.println("Sending POINTED_AT_CHANGED" );
        sendMessage( POINTED_AT_CHANGED );
      }

      if( message == CoordJPanel.ZOOM_IN ) {
        //System.out.println("Sending SELECTED_CHANGED " + regioninfo );
        for( int next = 0; next < transparencies.size(  ); next++ ) {
          ( ( OverlayJPanel )transparencies.elementAt( next ) ).repaint(  );
        }

        sendMessage( SELECTED_CHANGED );
      }

      if( message == CoordJPanel.RESET_ZOOM ) {
        //System.out.println("Sending SELECTED_CHANGED" );
        for( int next = 0; next < transparencies.size(  ); next++ ) {
          ( ( OverlayJPanel )transparencies.elementAt( next ) ).repaint(  );
        }

        sendMessage( SELECTED_CHANGED );
      }
    }
  }
/*  private class ControlPanelListener implements PropertyChangeListener {
    //~ Methods ****************************************************************
  
    public void propertyChange(PropertyChangeEvent evt) {
             System.out.println("the event:" + evt);  
             System.out.println("Name:" + evt.getPropertyName());
               
    }
  }
*/
  private class ControlListener implements ActionListener {
    //~ Methods ****************************************************************

    public void actionPerformed( ActionEvent ae ) {
      String message = ae.getActionCommand(  );

     // System.out.println( "action command: " + message );
     // System.out.println( "action event: " + ae );
      if( message.equals( "CHECKBOX_CHANGED" ) ) {
        ControlCheckbox control = ( ControlCheckbox )ae.getSource(  );
        
        if( control.getText(  ).equals( "Function Controls" ) ) {
          if( control.isSelected(  ) ) {
            mainControls.display_controls();
          } else {
            mainControls.close_frame();
          }
          
        }
        else if( control.getText(  ).equals( "Show Pointed At" ) ) {
          if( control.isSelected(  ) ) {
            draw_pointed_at = true;
          gjp.setTransparent(false, 0, true);
          } else {
              gjp.setTransparent(true, 0, true);
            draw_pointed_at = false;
          }
          paintComponents(big_picture.getGraphics()); 
           
        }
        
      }    
    }
  }
 
}

