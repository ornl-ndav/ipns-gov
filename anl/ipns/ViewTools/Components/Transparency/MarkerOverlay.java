/*
 * File: MarkerOverlay.java
 *
 * Copyright (C) 2004, Mike Miller
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
 * Primary   Mike Miller <millermi@uwstout.edu>
 * Contact:  Student Developer, University of Wisconsin-Stout
 *           
 * Contact : Dennis Mikkelson <mikkelsond@uwstout.edu>
 *           Department of Mathematics, Statistics and Computer Science
 *           University of Wisconsin-Stout
 *           Menomonie, WI 54751, USA
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
 *  Revision 1.1  2004/03/26 21:28:59  millermi
 *  - Initial Check in - Allows users to programmatically
 *    place markers at points of interest.
 *
 */
package gov.anl.ipns.ViewTools.Components.Transparency;

import javax.swing.*; 
import javax.swing.text.html.HTMLEditorKit;
import java.util.Vector;
import java.awt.*;
import java.awt.event.*;

import gov.anl.ipns.ViewTools.Components.TwoD.ImageViewComponent;
import gov.anl.ipns.ViewTools.Components.*;
import gov.anl.ipns.Util.Numeric.floatPoint2D;
import gov.anl.ipns.Util.Sys.WindowShower;
import gov.anl.ipns.ViewTools.Panels.Transforms.*;

/**
 * This class allows a user to place markers near a region on a
 * view component. Since this class extends an OverlayJPanel,
 * which extends a JPanel, this class is already serializable.
 */
public class MarkerOverlay extends OverlayJPanel
{
  // these public variables are used to preserve the annotation state    
 /**
  * "Editor Bounds" - This constant String is a key for referencing the state
  * information about the size and bounds of the Marker Editor window. 
  * The value that this key references is a Rectangle. The Rectangle contains
  * the dimensions for the editor.
  */
  public static final String EDITOR_BOUNDS  = "Editor Bounds";
  
  private static JFrame helper = null;
  // panel overlaying the center jpanel
  private transient Rectangle current_bounds;
  private Vector markers;
  private transient IZoomAddible component;	 // component being passed
  private Rectangle editor_bounds = new Rectangle(0,0,200,(35 * 5) );
  private transient CoordTransform pixel_local;
  
 /**
  * 
  *
  *  @param  iza - component must implement IZoomAddible interface
  */ 
  public MarkerOverlay(IZoomAddible iza)
  {
    super();
    this.setLayout( new GridLayout(1,1) );
    
    component = iza;
    markers = new Vector(); 
    current_bounds =  component.getRegionInfo();
    Rectangle temp = component.getRegionInfo();
    CoordBounds pixel_map = 
 		 new CoordBounds( (float)temp.getX(), 
 				  (float)temp.getY(),
 				  (float)(temp.getX() + temp.getWidth()),
 				  (float)(temp.getY() + temp.getHeight() ) );
    pixel_local = new CoordTransform( pixel_map, 
 				      component.getLocalCoordBounds() );
  }
  
 /**
  * 
  *
  *  @param  iza - component must implement IZoomAddible interface
  *  @param  state - ObjectState this overlay is being set to.
  */ 
  public MarkerOverlay(IZoomAddible iza, ObjectState state)
  {
    this(iza);
    setObjectState(state);
  }

 /**
  * Contains/Displays control information about this overlay.
  */
  public static void help()
  {
    helper = new JFrame("Help for Marker Overlay");
    helper.setBounds(0,0,600,400);
    
    JEditorPane textpane = new JEditorPane();
    textpane.setEditable(false);
    textpane.setEditorKit( new HTMLEditorKit() );
    String text = "<H1>Description:</H1><P>" +
                  "The Marker Overlay is used to add on-screen markers " +
 		  "to emphasize important points on the display.</P>" +
                  "<H2>Commands for Marker Overlay</H2>";
    textpane.setText(text);
    JScrollPane scroll = new JScrollPane(textpane);
    scroll.setVerticalScrollBarPolicy(
 				    JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    helper.getContentPane().add(scroll);
    WindowShower shower = new WindowShower(helper);
    java.awt.EventQueue.invokeLater(shower);
    shower = null;
  }
   
 /**
  * This method will set the current state variables of the object to state
  * variables wrapped in the ObjectState passed in.
  *
  *  @param new_state
  */
  public void setObjectState( ObjectState new_state )
  { /*
    boolean redraw = false;  // if any values are changed, repaint overlay.
    Object temp = new_state.get(NOTES);
    if( temp != null )
    {
      notes = ((Vector)temp);
      redraw = true;  
    }
    
    if( redraw )
      this_panel.repaint(); */
  }
 
 /**
  * This method will get the current values of the state variables for this
  * object. These variables will be wrapped in an ObjectState.
  *
  *  @param  isDefault Should selective state be returned, that used to store
  *		       user preferences common from project to project?
  *  @return if true, the default state containing user preferences,
  *	     if false, the entire state, suitable for project specific saves.
  */ 
  public ObjectState getObjectState( boolean isDefault )
  {
    ObjectState state = new ObjectState();
    state.insert( EDITOR_BOUNDS, editor_bounds );
   /*
    // load these for project specific instances.
    if( !isDefault )
    {
      state.insert( NOTES, notes );
    }
    */
    return state;
  }
  
 /**
  * Add a marker to the MarkerOverlay to be shown on the display.
  *
  *  @param  type The type of marker to be used.
  *  @param  locations The locations of the markers in world coordinates.
  *  @param  color The color of the marker(s) being added.
  *  @param  radius The radius of the circle circumscribing the marker in
  *                 world coordinates.
  *  @param  behave_on_zoom How the cursor reacts to zooming, either resizes
  *                         or maintains the same size.
  */
  public void addMarker( int type, floatPoint2D[] locations, Color color,
                         float radius, int behave_on_zoom )
  {
    addMarker( new Marker(type,locations,color,radius,behave_on_zoom) );
  }
  
 /**
  *
  */
  public void addMarker( Marker marker )
  {
    if( marker != null )
      markers.add(marker);
    repaint();
  }
  
  public void removeMarker( int index )
  {
    markers.remove(index);
    repaint();
  }

 /**
  * Allows toplevel components to remove all markers.
  */
  public void clearMarkers()
  {
    markers.clear();
    repaint();
  }

 /**
  * This method sets the line color of all annotations. Initially set to black.
  *
  *  @param  color
  */
  public void setAllMarkerColor( Color color )
  {
    for( int m = 0; m < markers.size(); m++ )
    {
      ((Marker)markers.elementAt(m)).setColor(color);
    }
    repaint();
  }
     
 /**
  * This method is called by to inform the overlay that it is no
  * longer needed. In turn, the overlay closes all windows created
  * by it before closing.
  */ 
  public void kill()
  {
    if( helper != null )
      helper.dispose();
  }

 /**
  * Overrides paint method. This method will paint the annotations.
  *
  *  @param  g - graphics object
  */
  public void paint(Graphics g) 
  {  
    Graphics2D g2d = (Graphics2D)g;
    
    current_bounds = component.getRegionInfo(); // current size of center 
    g2d.clipRect( (int)current_bounds.getX(),
 		  (int)current_bounds.getY(),
 		  (int)current_bounds.getWidth(),
 		  (int)current_bounds.getHeight() ); 
    // the current pixel coordinates
    CoordBounds pixel_map = 
 	    new CoordBounds( (float)current_bounds.getX(), 
 			     (float)current_bounds.getY(),
 			     (float)(current_bounds.getX() + 
 				     current_bounds.getWidth()),
 			     (float)(current_bounds.getY() + 
 				     current_bounds.getHeight() ) );
    pixel_local.setSource( pixel_map );
    pixel_local.setDestination( component.getLocalCoordBounds() );
    for( int m = 0; m < markers.size(); m++ )
    {
      ((Marker)markers.elementAt(m)).setCurrentTransform(pixel_local);
      ((Marker)markers.elementAt(m)).draw(g2d);
    }
  } // end of paint()
 
 /**
  * For testing purposes only.
  */ 
  public static void main( String args[] )
  {    
    int row = 10;
    int col = 10;
    float test_array[][] = new float[row][col];
    for ( int i = 0; i < row; i++ )
      for ( int j = 0; j < col; j++ )
        test_array[i][j] = i - j;
    VirtualArray2D va2D = new VirtualArray2D( test_array );
    va2D.setAxisInfo( AxisInfo.X_AXIS, -100f, 100f, 
    		        "TestX","TestUnits", true );
    va2D.setAxisInfo( AxisInfo.Y_AXIS, -200f, 200f, 
    			"TestY","TestYUnits", true );
    va2D.setTitle("MarkerOverlay Test");
    ImageViewComponent ivc = new ImageViewComponent(va2D);
    //ivc.addActionListener( new IVCListener() );
    Marker mark = new Marker( Marker.CIRCLE, new floatPoint2D(), Color.blue,
    			      3f, Marker.RESIZEABLE );
    floatPoint2D[] loc_array = new floatPoint2D[4];
    loc_array[0] = new floatPoint2D(-50f,-100f);
    loc_array[1] = new floatPoint2D(50f,-100f);
    loc_array[2] = new floatPoint2D(-50f,100f);
    loc_array[3] = new floatPoint2D(50f,100f);
    Marker mark2 = new Marker( Marker.STAR, loc_array, Color.red, 5f, 
                               Marker.STATIC );
    MarkerOverlay mo = new MarkerOverlay(ivc);
    mo.addMarker( mark );
    mo.addMarker( mark2 );
    
    JPanel container = new JPanel();
    OverlayLayout overlay = new OverlayLayout(container);
    container.setLayout(overlay);
    container.add(mo);
    container.add(ivc.getDisplayPanel());
    
    JFrame display = new JFrame("Marker Overlay Test");
    display.getContentPane().add(container);
    display.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    display.setBounds(0,0,500,500);
    
    // show the display tester
    WindowShower shower = new WindowShower(display);
    java.awt.EventQueue.invokeLater(shower);
    shower = null;/*
    class IVCListener implements ActionListener
    {
      public void actionPerformed( ActionEvent ae )
      {
        if(ae.getActionCommand().equals(ImageViewComponent.POINTED_AT_CHANGED) )
        {
          floatPoint2D current = ivc.getPointedAt();
	  Marker mark = new Marker( Marker.PLUS, current, Color.blue,
	                            3f, true );
	  mo.addMarker( mark );
        }  
      }
    }*/
  
  } // end main
  
}
