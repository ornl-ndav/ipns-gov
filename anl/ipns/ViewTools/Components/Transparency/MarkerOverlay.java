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
 *  Revision 1.7  2004/08/11 23:22:48  millermi
 *  - Added removeMarker(Marker) so markers can be removed
 *    by reference.
 *
 *  Revision 1.6  2004/05/11 01:38:44  millermi
 *  - Removed unused variables.
 *
 *  Revision 1.5  2004/04/29 06:44:12  millermi
 *  - Revised the help() panel.
 *
 *  Revision 1.4  2004/04/23 17:29:05  millermi
 *  - Replaced code for color chooser with the new ColorSelector class.
 *
 *  Revision 1.3  2004/04/07 20:43:00  millermi
 *  - Now uses string and intcode list from Marker class to construct
 *    the combo box.
 *
 *  Revision 1.2  2004/04/07 01:21:44  millermi
 *  - Added javadocs to addMarker() and removeMarker()
 *  - Added MarkerEditor, enabling users to interactively edit the markers.
 *  - Added editMarker() method which creates an instance of the MarkerEditor.
 *
 *  Revision 1.1  2004/03/26 21:28:59  millermi
 *  - Initial Check in - Allows users to programmatically
 *    place markers at points of interest.
 *
 */
package gov.anl.ipns.ViewTools.Components.Transparency;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.border.LineBorder;
import javax.swing.text.html.HTMLEditorKit;
import java.util.Vector;
import java.util.Enumeration;
import java.awt.*;
import java.awt.event.*;

import gov.anl.ipns.ViewTools.Components.TwoD.ImageViewComponent;
import gov.anl.ipns.ViewTools.Components.*;
import gov.anl.ipns.ViewTools.Components.ViewControls.ControlSlider;
import gov.anl.ipns.Util.Numeric.floatPoint2D;
import gov.anl.ipns.Util.Sys.ColorSelector;
import gov.anl.ipns.Util.Sys.WindowShower;
import gov.anl.ipns.ViewTools.Panels.Transforms.*;

/**
 * This class allows a user to place markers near a region on a
 * view component. Since this class extends an OverlayJPanel,
 * which extends a JPanel, this class is already serializable.
 */
public class MarkerOverlay extends OverlayJPanel
{
  // these public variables are used to preserve the overlay state   
 /**
  * "Markers" - This constant String is a key for referencing the state
  * information about the markers created by this marker overlay. 
  * The value that this key references is a Vector of Markers.
  */
  public static final String MARKERS  = "Markers";
     
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
  private transient MarkerOverlay this_overlay;
  private Vector markers;
  private transient IZoomAddible component;	 // component being passed
  private Rectangle editor_bounds = new Rectangle(0,0,200,(35 * 5) );
  private transient CoordTransform pixel_local;
  private transient MarkerEditor editor;
  
 /**
  * 
  *
  *  @param  iza - component must implement IZoomAddible interface
  */ 
  public MarkerOverlay(IZoomAddible iza)
  {
    super();
    this.setLayout( new GridLayout(1,1) );
    this_overlay = this;
    component = iza;
    markers = new Vector();
    editor = new MarkerEditor(); 
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
 		  "to emphasize important points on the display. Each " +
		  "viewer will define how markers can be specified. " +
		  "MARKERS ARE NOT INTERACTIVELY SET, to interactively " +
		  "add marks to a display, use the AnnotationOverlay.</P>" +
                  "<H2>Commands for Marker Overlay</H2>" +
		  "<i>First, select the marker(s) of interest. All other " +
		  "operations after this point will affect the specified " +
		  "marker.</i><br>" +
		  "<b>Marker Types:</b> Select the visual appearance of the " +
		  "marker.<br>" +
		  "<b>Resize Marker on Zoom:</b> Do you want the markers to " +
		  "stay the same size (disabled), or grow (enabled) when a " +
		  "zoom on the image occurs?<br>" +
		  "<b>Adjust Marker Size:</b> Allows you to choose the marker "+
		  "size.<br>" +
		  "<b>Select New Color:</b> Select the color of the current " +
		  "marker.";
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
  { 
    boolean redraw = false;  // if any values are changed, repaint overlay.
    Object temp = new_state.get(MARKERS);
    if( temp != null )
    {
      markers = ((Vector)temp);
      redraw = true;  
    }
    
    temp = new_state.get(EDITOR_BOUNDS);
    if( temp != null )
    {
      editor_bounds = ((Rectangle)temp);
      redraw = true;  
    }
    
    if( redraw )
      repaint();
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
   
    // load these for project specific instances.
    if( !isDefault )
    {
      state.insert( MARKERS, markers );
    }
    
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
  * Add a marker to the MarkerOverlay to be shown on the display. An
  * advantage to using this method is that the marker class has a constructor
  * to allow creation of one marker without putting its location into an array.
  *
  *  @param  marker The marker to be displayed.
  */
  public void addMarker( Marker marker )
  {
    if( marker != null )
    {
      markers.add(marker);
      repaint();
      editor.updateMarkerList();
    }
  }
  
 /**
  * Remove the given marker from the list of markers. If the marker is not in
  * the list, nothing will be done.
  *
  *  @param  marker The marker to be removed.
  */
  public void removeMarker( Marker marker )
  {
    if( marker != null )
    {
      markers.remove(marker);
      repaint();
      editor.updateMarkerList();
    }
  }
 
 /**
  * Remove the marker at the given index. If negative one (-1) is passed in,
  * the last marker will be removed. Invalid indices will be ignored.
  *
  *  @param  index Index of the marker to be removed. The first index is zero.
  */ 
  public void removeMarker( int index )
  {
    // If size is zero, there is nothing to remove.
    if( markers.size() == 0 )
      return;
    // if index is -1, remove last marker.
    if( index == -1 )
      markers.remove( markers.size() - 1 );
    // else if an invalid index, do nothing
    else if( index < 0 || index >= markers.size() )
      return;
    else
      markers.remove(index);
    repaint();
    editor.updateMarkerList();
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
  * This method sets the color of all the markers. Initially set to black.
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
  * This method is used to view an instance of the Marker Editor.
  */ 
  public void editMarker()
  {
    if( editor.isVisible() )
    {
      editor.toFront();
      editor.requestFocus();
    }
    else
    {
      editor_bounds = editor.getBounds();
      editor.dispose();
      editor = new MarkerEditor();
      WindowShower shower = new WindowShower(editor);
      java.awt.EventQueue.invokeLater(shower);
      shower = null;
      editor.toFront();
    }
  }
     
 /**
  * This method is called by to inform the overlay that it is no
  * longer needed. In turn, the overlay closes all windows created
  * by it before closing.
  */ 
  public void kill()
  {
    editor.dispose();
    if( helper != null )
      helper.dispose();
  }

 /**
  * Overrides paint method. This method will paint the markers.
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
 
 /*
  * This is an editor for the markers.
  */
  private class MarkerEditor extends JFrame
  {
    private MarkerEditor this_editor;
    private JComboBox markerlist;
    private JComboBox markertypelist;
    private ButtonGroup resizeable;
    private ControlSlider size_adjuster;
    // String list of marker types
    private String[] markertypes;
    // Array that parallels the marker types, these are the int codes defined
    // by the Marker class.
    private int[] markercodes;
    protected MarkerEditor()
    {
      this_editor = this;
      setTitle("Marker Editor");
      this_editor.setBounds(0,0,435,350);
      getContentPane().setLayout( new GridLayout(1,1) );
      setDefaultCloseOperation( JFrame.HIDE_ON_CLOSE );
      // initialize the marker types array, First value is a label, then copy
      // the array over.
      String[] temptypes = Marker.getStringArray();
      int[] tempcodes = Marker.getIntCodeArray();
      markertypes = new String[temptypes.length];
      markertypes[0] = "Marker Types";
      markercodes = new int[tempcodes.length];
      markercodes[0] = -1;
      for( int i = 1; i < tempcodes.length; i++ )
      {
        markertypes[i] = temptypes[i-1];
	markercodes[i] = tempcodes[i-1];
      }
      buildPane();
    }
    
    protected void updateMarkerList()
    {
      buildPane();
    }
    
    private void setResizeable( boolean isOn )
    {
      if( resizeable != null )
      {
	Enumeration on_off = resizeable.getElements();
        JRadioButton nextbutton = (JRadioButton)on_off.nextElement();
	if( isOn )
	{
	  if( !nextbutton.isSelected() )
            resizeable.setSelected(nextbutton.getModel(),true);
	}
	else
	{
	  // move to off option
	  nextbutton = (JRadioButton)on_off.nextElement();
	  if( !nextbutton.isSelected() )
            resizeable.setSelected(nextbutton.getModel(),true);
	}
      }
    }
    
    private void buildPane()
    {
      getContentPane().removeAll();
      // construct the first line of controls
      markerlist = new JComboBox();
      markerlist.addItemListener( new MarkerListListener() );
      markerlist.addItem("All Markers");
      String combotitle = "";
      for( int i = 0; i < markers.size(); i++ )
      {
        combotitle = markers.elementAt(i) + " " + (i+1);
        markerlist.addItem( combotitle );
      }
      JButton remove = new JButton("Remove");
      remove.addActionListener( new ButtonListener() );
      JPanel line1controls = new JPanel( new BorderLayout() );
      TitledBorder border = 
	  new TitledBorder(LineBorder.createBlackLineBorder(),
	                   "Select Marker of Interest");
      border.setTitleFont( gov.anl.ipns.ViewTools.UI.FontUtil.BORDER_FONT ); 
      line1controls.setBorder( border );
      line1controls.add(markerlist, BorderLayout.CENTER);
      line1controls.add(remove, BorderLayout.EAST );
      // construct second line of controls
      markertypelist = new JComboBox(markertypes);
      markertypelist.addItemListener( new MarkerTypeListener() );
      //JPanel resize_with_label = new JPanel( new GridLayout(2,1) );
      //resize_with_label.add( new JLabel("Resize on Zoom", JLabel.CENTER) );
      JPanel resize_radios = new JPanel( new GridLayout(2,1) );
      TitledBorder radioborder = 
	  new TitledBorder(LineBorder.createBlackLineBorder(),
	                   "Resize Marker on Zoom");
      radioborder.setTitleFont(gov.anl.ipns.ViewTools.UI.FontUtil.BORDER_FONT); 
      resize_radios.setBorder( radioborder );
      JRadioButton enable_resize = new JRadioButton("Enable Resize");
      JRadioButton disable_resize = new JRadioButton("Disable Resize");
      enable_resize.addActionListener( new ButtonListener() );
      disable_resize.addActionListener( new ButtonListener() );
      resize_radios.add(enable_resize);
      resize_radios.add(disable_resize);
      //resize_with_label.add(resize_radios);
      resizeable = new ButtonGroup();
      resizeable.add( enable_resize );
      resizeable.add( disable_resize );
      setResizeable(true);
      JPanel line2controls = new JPanel( new BorderLayout() );
      line2controls.add(markertypelist, BorderLayout.CENTER);
      line2controls.add(resize_radios, BorderLayout.EAST);
      // find maximum world coord value to be used as upper bound for
      // the controlslider.
      CoordBounds wc_bounds = component.getGlobalCoordBounds();
      float xmax = Math.abs(wc_bounds.getX1());
      float ymax = Math.abs(wc_bounds.getY1());
      if( xmax < Math.abs(wc_bounds.getX2()) )
        xmax = Math.abs(wc_bounds.getX2());
      if( ymax < Math.abs(wc_bounds.getY1()) )
        ymax = Math.abs(wc_bounds.getY1());
      if( ymax < Math.abs(wc_bounds.getY2()) )
        ymax = Math.abs(wc_bounds.getY2());
      // find the minimum of the two maximums.
      float max = xmax;
      if( max > ymax )
        max = ymax;
      // Use upper bound of max/2 since size is a radial dimension.
      size_adjuster = new ControlSlider(0,max/2f,100);
      size_adjuster.setMajorTickSpace(.25f);
      size_adjuster.setMinorTickSpace(.05f);
      size_adjuster.setTitle("Adjust Marker Size");
      size_adjuster.addActionListener(new SizeAdjustListener());
      JPanel chooser = new JPanel( new BorderLayout() );
      TitledBorder chooserborder = 
	  new TitledBorder(LineBorder.createBlackLineBorder(),
	                   "Select New Color");
      chooserborder.setTitleFont(
                          gov.anl.ipns.ViewTools.UI.FontUtil.BORDER_FONT ); 
      chooser.setBorder( chooserborder );
      ColorSelector color_selector = new ColorSelector( ColorSelector.SWATCH );
      color_selector.addActionListener( new ColorChangedListener() );
      JButton close = new JButton("Close");
      close.addActionListener( new ButtonListener() );
      // build chooser panel, with label, JColorChooser, and close button.
      chooser.add(color_selector, BorderLayout.NORTH);
      chooser.add(close, BorderLayout.CENTER);
      JPanel upper_ctrls = new JPanel( new GridLayout(3,1) );
      upper_ctrls.add(line1controls);
      upper_ctrls.add(line2controls);
      upper_ctrls.add(size_adjuster);
      
      // build container of all controls
      JPanel container = new JPanel( new GridLayout(2,1) );
      container.add(upper_ctrls);
      container.add(chooser);
      // add container to frame
      getContentPane().add(container);
      // update the changes.
      this_editor.validate();
      this_editor.repaint();
    }
    
    private class ColorChangedListener implements ActionListener
    {
      public void actionPerformed( ActionEvent ae )
      {
        Color newcolor = ((ColorSelector)ae.getSource()).getSelectedColor();
	int index = markerlist.getSelectedIndex();
	// if "All Markers"
	if( index == 0 )
	{
          for( int i = 0; i < markers.size(); i++ )
	  {
	    ((Marker)markers.elementAt(i)).setColor( newcolor );
	  }
	}
	// else find the marker, remembering that "All Markers" is the first
	// indexed element.
	else
	{
	  ((Marker)markers.elementAt(index-1)).setColor( newcolor );
	}
	this_overlay.repaint();
      }
    }
    
    private class ButtonListener implements ActionListener
    {
      public void actionPerformed( ActionEvent ae )
      {
        String message = ae.getActionCommand();
        if( message.equals("Remove") )
	{
	  removeMarker( markerlist.getSelectedIndex() - 1 );
	  this_overlay.repaint();
	  buildPane();
	}
        else if( message.equals("Close") )
        {  
	  editor_bounds = this_editor.getBounds(); 
          this_editor.dispose();
          this_overlay.repaint();
        }
        else if( message.equals("Enable Resize") )
        {
	  int index = markerlist.getSelectedIndex();
          // If not "All Markers", get the specific information about this
	  // marker.
          if( index != 0 )
          {
	    Marker selectmark = ((Marker)markers.elementAt(index-1));
	    selectmark.setBehavior( Marker.RESIZEABLE );
	  }
        }
        else if( message.equals("Disable Resize") )
        {
	  int index = markerlist.getSelectedIndex();
          // If not "All Markers", get the specific information about this
	  // marker.
          if( index != 0 )
          {
	    Marker selectmark = ((Marker)markers.elementAt(index-1));
	    selectmark.setBehavior( Marker.STATIC );
	  }
        }
      }
    }
    
    private class MarkerTypeListener implements ItemListener
    {
      public void itemStateChanged( ItemEvent ie )
      {
        if( ie.getStateChange() == ItemEvent.SELECTED )
	{
	  // Get the marker code for the selected marker type.
	  int marktype = markercodes[markertypelist.getSelectedIndex()];
	  int index = markerlist.getSelectedIndex();
          // if "All Markers"
          if( index == 0 )
          {
            for( int i = 0; i < markers.size(); i++ )
            {
              ((Marker)markers.elementAt(i)).setMarkerType( marktype );
            }
          }
          // else find the marker, remembering that "All Markers" is the first
          // indexed element.
          else
          {
            ((Marker)markers.elementAt(index-1)).setMarkerType( marktype );
          }
          this_overlay.repaint();
	}
      }
    }
    
    private class MarkerListListener implements ItemListener
    {
      public void itemStateChanged( ItemEvent ie )
      {
        if( ie.getStateChange() == ItemEvent.SELECTED )
	{
	  int index = markerlist.getSelectedIndex();
          // If not "All Markers", get the specific information about this
	  // marker.
          if( index != 0 )
          {
	    Marker selectmark = ((Marker)markers.elementAt(index-1));
	    // get marker type, set the markertypelist to this type.
            int marktype = selectmark.getMarkerType();
	    int list_index = 0;
	    while( list_index < markertypes.length &&
	           markercodes[list_index] != marktype )
	      list_index++;
            if( list_index < markertypes.length )
	      markertypelist.setSelectedIndex(list_index);
	    // get the resizeable info
	    int behave = selectmark.getBehavior();
	    if( behave == Marker.STATIC )
	      setResizeable(false);
	    else
	      setResizeable(true);
	    size_adjuster.setValue( selectmark.getSize() );
	  }
	  // if "All Markers", set defaults
	  else
	  {
	    if( markertypelist != null )
	      markertypelist.setSelectedIndex(0);
	    setResizeable(true);
	  }
	  this_editor.repaint();
          this_overlay.repaint();
	}
      }
    }
    
    private class SizeAdjustListener implements ActionListener
    {
      public void actionPerformed( ActionEvent ae )
      {
        if( ae.getActionCommand().equals(ControlSlider.SLIDER_CHANGED) )
	{
	  float new_size = ((ControlSlider)ae.getSource()).getValue();
	  int index = markerlist.getSelectedIndex();
          // if "All Markers"
          if( index == 0 )
          {
            for( int i = 0; i < markers.size(); i++ )
            {
              ((Marker)markers.elementAt(i)).resize( new_size );
            }
          }
          // else find the marker, remembering that "All Markers" is the first
          // indexed element.
          else
          {
            ((Marker)markers.elementAt(index-1)).resize( new_size );
          }
          this_overlay.repaint();
	}
      }
    }
  }
 
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
    
    mo.editMarker();
    
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
