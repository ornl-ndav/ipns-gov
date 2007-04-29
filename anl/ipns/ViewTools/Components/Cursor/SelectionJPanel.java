/*
 * File:  SelectionJPanel.java
 *
 * Copyright (C) 2003, Mike Miller
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
 * This work was supported by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 * Modified:
 *
 *  $Log$
 *  Revision 1.22  2007/04/29 20:26:07  dennis
 *  Added button to undo the previous selection.
 *  Now all buttons use one instance of a ButtonListener.
 *
 *  Revision 1.21  2007/04/29 18:26:04  dennis
 *  Made UNION the default operation type returned by getOperation()
 *
 *  Revision 1.20  2007/04/28 05:58:40  dennis
 *  Added (and now send) messages for OPACITY_CHANGED and
 *  COLOR_CHANGED.
 *  Added javadocs on constructor.
 *
 *  Revision 1.19  2007/04/28 03:26:30  dennis
 *  Refactored and added controls to complement current selection
 *  and choose operations to be used when combining regions.
 *  (Joshua Oakgrove, Terry Farmer, Galina Pozharsky)
 *
 *  Revision 1.18  2007/03/23 20:28:57  dennis
 *  Now checks for cursor being an instance of DoubleWedgeCursor
 *  before checking for WedgeCursor.  This change is needed to
 *  properly find the cursor type, now that DoubleWedgeCursor
 *  extends WedgeCursor.
 *
 *  Revision 1.17  2007/03/16 18:30:49  dennis
 *  Now handles cirlces and ellipses.
 *  Minor code clean up.
 *
 *  Revision 1.16  2004/03/12 01:33:23  millermi
 *  - Changed package and imports.
 *
 *  Revision 1.15  2004/03/09 21:06:47  millermi
 *  - Added ToolTips to editors for user assistance.
 *
 *  Revision 1.14  2003/12/30 00:39:38  millermi
 *  - Added Annular selection capabilities.
 *  - Changed SelectionJPanel.CIRCLE to SelectionJPanel.ELLIPSE
 *
 *  Revision 1.13  2003/12/23 02:02:46  millermi
 *  - Added enableCursor(), disableCursor(), and isEnabled() to
 *    allow for some selections to be made unavailable.
 *
 *  Revision 1.12  2003/12/18 22:37:14  millermi
 *  - Removed code which restricts selections on the border of the image.
 *
 *  Revision 1.11  2003/12/11 17:12:42  millermi
 *  - Now restricts selection if any of the defining points
 *    are outside of the viewport.
 *
 *  Revision 1.10  2003/10/16 05:00:04  millermi
 *  - Fixed java docs errors.
 *
 *  Revision 1.9  2003/09/02 20:49:07  millermi
 *  - Allows for changing selection type, as long as selection
 *    was not started.
 *  - Fixed bug where wedge and dbl_wedge were selectable after
 *    key was released.
 *
 *  Revision 1.8  2003/08/26 03:40:24  millermi
 *  - Added functionality for double wedge, including panel button.
 *
 *  Revision 1.7  2003/08/21 18:18:59  millermi
 *  - Added ability to handle XOR_Cursor3pt class.
 *
 *  Revision 1.6  2003/08/18 20:53:19  millermi
 *  - Added JButton controls to simulate a keyboard event, making the
 *    selection process more user friendly.
 *  - Added getControls() so outside classes can access the new controls.
 *
 *  Revision 1.5  2003/08/07 17:55:56  millermi
 *  - Replaced elipse selection framework with line selection capabilities.
 *
 *  Revision 1.4  2003/06/06 14:37:38  dennis
 *  Temporarily commented out call to setFocusable() so that
 *  it will compile under jdk 1.3.1.  The call to setFocusable()
 *  should be re-enabled when we start using jdk 1.4.x.
 *
 *  Revision 1.3  2003/06/05 22:13:57  dennis
 *   - Fixed keyboard listener problems by adding setFocusable()
 *     in Constructor.  (Mike Miller)
 *
 *  Revision 1.2  2003/05/29 14:13:22  dennis
 *  Made three changes (Mike Miller)
 *    -extends ActiveJPanel not CoordJPanel
 *    -added messages for actionListeners
 *    -added double click feature to remove last/all selected
 * 
 *  Revision 1.1  2003/05/24 17:32:20  dennis
 *  Initial version of cursor selection. (Mike Miller)
 *
 */

package gov.anl.ipns.ViewTools.Components.Cursor;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import gov.anl.ipns.Util.Sys.ColorSelector;
import gov.anl.ipns.Util.Sys.WindowShower;
import gov.anl.ipns.ViewTools.UI.ActiveJPanel;

import gov.anl.ipns.ViewTools.Components.Region.RegionOp;
import gov.anl.ipns.ViewTools.Components.ViewControls.ControlSlider;
import gov.anl.ipns.ViewTools.Panels.Cursors.XOR_Cursor;
import gov.anl.ipns.ViewTools.Panels.Cursors.BoxCursor;

/**
 * This class allows for the drawing of rubber band box/circle/point cursors.
 * It is used by the SelectionOverlay to select regions of the underlying 
 * JPanel.
 */
public class SelectionJPanel extends ActiveJPanel
{
 /**
  * "RESET_SELECTED" - This message String is sent to listeners when the
  * all of the selections are removed.
  */ 
  public static final String RESET_SELECTED = "Clear All";
  
 /**
  * "RESET_LAST_SELECTED" - This message String is sent to listeners when the
  * last selection is removed.
  */ 
  public static final String RESET_LAST_SELECTED = "UNDO";
  
 /**
  * "REGION_SELECTED" - This message String is sent to listeners when a
  * region selection occurs.
  */ 
  public static final String REGION_SELECTED = "REGION_SELECTED";
 
 /**
  * "Box" - This message String is used to select/deselect the box cursor.
  */ 
  public static final String BOX = "Box";
 
 /**
  * "Ellipse" - This message String is used to select/deselect the ellipse 
  *             cursor.  The region this pertains to is the EllipseRegion. 
  *             Ellipse is used because scaling does not always yield an 
  *             exact circle.
  */ 
  public static final String ELLIPSE = "Ellipse";

 /**
  * "Circle" - This message String is used to select/deselect the circle 
  *             cursor.  
  */
  public static final String CIRCLE = "Circle";
 
 /**
  * "Line" - This message String is used to select/deselect the line cursor.
  */ 
  public static final String LINE = "Line";
 
 /**
  * "Point" - This message String is used to select/deselect the point cursor.
  */ 
  public static final String POINT = "Point";
 
 /**
  * "Wedge" - This message String is used to select/deselect the wedge cursor.
  */ 
  public static final String WEDGE = "Wedge";
 
 /**
  * "Double Wedge" - This message String is used to select/deselect the
  * double wedge cursor.
  */ 
  public static final String DOUBLE_WEDGE = "Double Wedge";

 /**
  * "Ring" - This message String is used to select/deselect the annular
  *             cursor.
  */ 
  public static final String RING = "Ring";
 
 /**
  * "all" - This message String is used to select/deselect all of the cursors.
  */ 
  public static final String ALL = "all";
  
  /**
   * "Complement Current Selection" - This message String is used to 
   * complement the current selected array of points.
   */
  public static final String COMPLEMENT_CURRENT_SELECTION = "Complement";
  
  public static final String UNION = "Union";

  public static final String INTERSECT = "Intersect";

  public static final String INTERSECT_COMPLEMENT = "Intersect Complement";

  public static final String COLOR_CHANGED = ColorSelector.COLOR_CHANGED;

  public static final String OPACITY_CHANGED = "Opacity_Changed";
  
  private AnnularCursor ring;
  private BoxCursor box;
  private CircleCursor circle;
  private EllipseCursor ellipse;
  private PointCursor point;
  private LineCursor line;
  private WedgeCursor wedge;
  private DoubleWedgeCursor dblwedge;
  private boolean isAdown;   // true if A is pressed (for RESET_SELECTED)
  private boolean isBdown;   // true if B is pressed (for box selection)
  private boolean isCdown;   // true if C is pressed (for circle selection)
  private boolean isEdown;   // true if E is pressed (for ellipse selection)
  private boolean isDdown;   // true if D is pressed (for dbl wedge selection)
  private boolean isLdown;   // true if L is pressed (for line selection)
  private boolean isPdown;   // true if P is pressed (for point selection)
  private boolean isRdown;   // true if R is pressed (for annular selection)
  private boolean isWdown;   // true if W is pressed (for wedge selection)
  private boolean doing_box;      // true if box selection started
  private boolean doing_circle;   // true if circle selection started
  private boolean doing_ellipse;  // true if ellipse selection started
  private boolean doing_dblwedge; // true if wedge selection started
  private boolean doing_line;     // true if line selection started
  private boolean doing_point;    // true if point selection started
  private boolean doing_ring;     // true if annular selection started
  private boolean doing_wedge;    // true if wedge selection started
  private boolean firstRun;       // true if 3pt cursor hasn't drawn midpoint
  private boolean disableBox;      // initially enabled.
  private boolean disableCircle;   // initially enabled.
  private boolean disableEllipse;  // initially enabled.
  private boolean disableDblWedge; // initially enabled.
  private boolean disableLine;     // initially enabled.
  private boolean disablePoint;    // initially enabled.
  private boolean disableRing;     // initially enabled.
  private boolean disableWedge;    // initially enabled.
  private boolean disableAll;      // initially enabled, used to disable
  private boolean unionOpSelected;
  private boolean intersectOpSelected;
  private boolean intersectComplementOpSelected;
  
  private String name;
  private transient SelectionEditor editor;

//buttons for making selections, used by editor.
  private JButton[] sjpbuttons;
  public static final String EDITOR_BOUNDS = "Editor Bounds";
  private Rectangle editor_bounds = new Rectangle(0, 0, 430, 390);
  private float opacity = 1.0f; // value [0,1] where 0 is clear,
  private Color reg_color;
  private String[] ops = {UNION,INTERSECT,INTERSECT_COMPLEMENT};
  private JComboBox opChooser;
  private JLabel opLabel = new JLabel("Operation");
  
 /**
  * Constructor adds listeners to this SelectionJPanel. All boolean values
  * are set to false. This JPanel is also contructed to receive keyboard
  * events.
  */ 
  public SelectionJPanel()
  { 
    isAdown = false;
    isBdown = false;
    isCdown = false;
    isDdown = false;
    isLdown = false;
    isPdown = false;
    isRdown = false;
    isWdown = false;
    
    doing_box = false;
    doing_circle = false;
    doing_ellipse = false;
    doing_line = false;
    doing_point = false;
    doing_wedge = false;
    doing_dblwedge = false;
    doing_ring = false;
    unionOpSelected = false;
    intersectOpSelected = false;
    intersectComplementOpSelected = false;
    
    // initialize all selections to enabled.
    String[] enable = {ALL};
    enableCursor( enable );
    
    firstRun = true;
  
    box = new BoxCursor(this);
    circle = new CircleCursor(this);
    ellipse = new EllipseCursor(this);
    line = new LineCursor(this);
    point = new PointCursor(this);
    wedge = new WedgeCursor(this);
    dblwedge = new DoubleWedgeCursor(this);
    ring = new AnnularCursor(this);
    
    requestFocus();
    
    addMouseListener( new SelectMouseAdapter() );
    addMouseMotionListener( new SelectMouseMotionAdapter() );
    addKeyListener( new SelectKeyAdapter() );
    reg_color = Color.white;
    sjpbuttons = getControls();
    opChooser = new JComboBox(ops);
    opChooser.addActionListener(new JComboBoxListener());
    opChooser.setSelectedIndex(0);
    editor = new SelectionEditor();
    addComponentListener(new NotVisibleListener());
  }
  

 /**
  *  Construct a SelectionJPanel for entry of a compound selection,
  *  with a specified name, draw color and transparency value.
  *
  *  @param name         The name of the list of region, operator pairs
  *                      being constructed
  *  @param color        The color to use when drawing the regions
  *  @param transparency The transparency value to use when drawing the regions
  *
  */
  public SelectionJPanel(String name, Color color, float transparency )
  { 
      isAdown = false;
      isBdown = false;
      isCdown = false;
      isDdown = false;
      isLdown = false;
      isPdown = false;
      isRdown = false;
      isWdown = false;
      
      doing_box = false;
      doing_circle = false;
      doing_ellipse = false;
      doing_line = false;
      doing_point = false;
      doing_wedge = false;
      doing_dblwedge = false;
      doing_ring = false;
      
      this.name = name;
      // initialize all selections to enabled.
      String[] enable = {ALL};
      enableCursor( enable );
      
      firstRun = true;
    
      box = new BoxCursor(this);
      circle = new CircleCursor(this);
      ellipse = new EllipseCursor(this);
      line = new LineCursor(this);
      point = new PointCursor(this);
      wedge = new WedgeCursor(this);
      dblwedge = new DoubleWedgeCursor(this);
      ring = new AnnularCursor(this);
      unionOpSelected = false;
      intersectOpSelected = false;
      intersectComplementOpSelected = false;
      
      requestFocus();
      
      addMouseListener( new SelectMouseAdapter() );
      addMouseMotionListener( new SelectMouseMotionAdapter() );
      addKeyListener( new SelectKeyAdapter() );
      reg_color = color;
      setOpacity(transparency);
      sjpbuttons = getControls();
      opChooser = new JComboBox(ops);
      opChooser.addActionListener(new JComboBoxListener());
      opChooser.setSelectedIndex(0);
      editor = new SelectionEditor();
      addComponentListener(new NotVisibleListener());
    }

  
 /* ------------------------ set_cursor ------------------------------ */
 /**
  *  Move the rubber band cursor to the specified pixel point. If the
  *  rubber band region was not previously started, this will specifiy the
  *  initial point for that region.  If the rubber band region was previously
  *  started, this will specify a new location for the ending point of the
  *  region.
  *
  *  @param cursor  The type of cursor that was selected. 
  *  @param current  The point where the rubber band region should be drawn
  */
  public void set_cursor( XOR_Cursor cursor, Point current )
  {
    if( cursor instanceof BoxCursor )
    {
      if ( doing_box )
        cursor.redraw( current );
      else
      {
        cursor.start( current );
        cursor.redraw( current );
        doing_box = true;
      }
    }
    else if( cursor instanceof CircleCursor )
    {
      if ( doing_circle )
       cursor.redraw( current );
      else
      {
       cursor.start( current );
       cursor.redraw( current );
       doing_circle = true;
      }
    }  
    else if( cursor instanceof EllipseCursor )
    {
      if ( doing_ellipse )
        cursor.redraw( current );
      else
      {
        cursor.start( current );
        cursor.redraw( current );
        doing_ellipse = true;
      }
    }
    else if( cursor instanceof LineCursor )
    {
      if ( doing_line )
        cursor.redraw( current );
      else
      {
        cursor.start( current );
        cursor.redraw( current );
        doing_line = true;
      }
    }       
    else if( cursor instanceof PointCursor )
    {
      if ( doing_point )
        cursor.redraw( current );
      else
      {
        cursor.start( current );
        cursor.redraw( current );
        doing_point = true;
      }
    }  
  }


 /* --------------- set_cursor for XOR_Cursor3pt --------------------- */
 /**
  *  Move the rubber band cursor to the specified pixel point. If the
  *  rubber band region was not previously started, this will specifiy the
  *  initial point for that region.  If the rubber band region was previously
  *  started, this will specify a new location for the ending point of the
  *  region.
  *
  *  @param  cursor  The type of 3 pt cursor that was selected. 
  *  @param  current  The point where the rubber band region should be drawn
  */
  public void set_cursor( XOR_Cursor3pt cursor, Point current )
  {
                                            // check DoubleWedgeCursor first
                                            // since DoubleWedgeCursor extends
                                            // WedgeCursor
    if( cursor instanceof DoubleWedgeCursor )
    {
      if ( doing_dblwedge )
        cursor.redraw( current );
      else
      {
        cursor.start( current );
        cursor.redraw( current );
        doing_dblwedge = true;
      }
    }
    else if( cursor instanceof WedgeCursor )
    {
      if ( doing_wedge )
        cursor.redraw( current );
      else
      {
        cursor.start( current );
        cursor.redraw( current );
        doing_wedge = true;
      }
    }
    else if( cursor instanceof AnnularCursor )
    {
      if ( doing_ring )
        cursor.redraw( current );
      else
      {
        cursor.start( current );
        cursor.redraw( current );
        doing_ring = true;
      }
    }
  }


 /* -------------------------- stop_cursor ---------------------------- */
 /**
  *  Stop the rubber band cursor and set the current position to the
  *  specifed pixel coordinate point. 
  *
  *  @param  cursor   the type of cursor being used.
  *  @param  current  the point to record as the current point,
  *          in pixel coordinates
  */
  public void stop_cursor( XOR_Cursor cursor, Point current )
  {
    if( cursor instanceof BoxCursor )
    {  
      if ( doing_box )
      {
        cursor.redraw( current );
        cursor.stop( current );
        doing_box = false;
      }
    }
    else if( cursor instanceof CircleCursor )
    {  
      if ( doing_circle )
      {
        cursor.redraw( current );
        cursor.stop( current );
        doing_circle = false;
      }
    }  
    else if( cursor instanceof EllipseCursor )
    {
      if ( doing_ellipse )
      {
        cursor.redraw( current );
        cursor.stop( current );
        doing_ellipse = false;
      }
    }
    else if( cursor instanceof LineCursor )
    {  
      if ( doing_line )
      {
        cursor.redraw( current );
        cursor.stop( current );
        doing_line = false;
      }
    }       
    else if( cursor instanceof PointCursor )
    {  
      if ( doing_point )
      {
      cursor.redraw( current );
      cursor.stop( current );
      doing_point = false;
      }
    }     
  }


 /* -------------------------- stop_cursor ---------------------------- */
 /**
  *  Stop the rubber band cursor and set the current position to the
  *  specifed pixel coordinate point. 
  *
  *  @param  cursor   the type of cursor being used.
  *  @param  current  the point to record as the current point,
  *          in pixel coordinates
  */
  public void stop_cursor( XOR_Cursor3pt cursor, Point current )
  {
                                            // check DoubleWedgeCursor first
                                            // since DoubleWedgeCursor extends
                                            // WedgeCursor
    if( cursor instanceof DoubleWedgeCursor )
    {  
      if ( doing_dblwedge )
      {
        if( firstRun )
        {
          cursor.redraw( current );
          cursor.midpoint( current );
        }
        else
        {
          cursor.redraw( current );
          cursor.stop( current );
          doing_dblwedge = false;
        }
      }
    }
    else if( cursor instanceof WedgeCursor )
    {
      if ( doing_wedge )
      {
        if( firstRun )
        {
          cursor.redraw( current );
          cursor.midpoint( current );
        }
        else
        {
          cursor.redraw( current );
          cursor.stop( current );
          doing_wedge = false;
        }
      }
    }
    else if( cursor instanceof AnnularCursor )
    {  
      if ( doing_ring )
      {
      if( firstRun )
        {
          cursor.redraw( current );
          cursor.midpoint( current );
        }
        else
        {
          cursor.redraw( current );
          cursor.stop( current );
          doing_ring = false;
        }
      }
    }  
  }
  

 /**
  * Since there are 4 types of xor cursors, this method gives us
  * which cursor was used.
  *
  *  @param  cursor - string label
  *  @return cursor - actual XOR_Cursor corresponding to the string label.
  */ 
  public XOR_Cursor getCursor( String cursor )
  {
    if( cursor.equals(BOX) )
      return box;
    else if( cursor.equals(CIRCLE) )
      return circle;
    else if( cursor.equals(ELLIPSE) )
      return ellipse;
    else if( cursor.equals(LINE) )
      return line;     
    else //( cursor.equals(POINT) )
      return point;
  }

  
 /**
  * Since there are other types of cursors, this method gives us
  * the xor 3pt cursor that was used.
  *
  *  @param  cursor - string label
  *  @return cursor - actual XOR_Cursor corresponding to the string label.
  */ 
  public XOR_Cursor3pt get3ptCursor( String cursor )
  {
    if( cursor.equals(WEDGE) )
      return wedge;
    else if( cursor.equals(DOUBLE_WEDGE) )
      return dblwedge;
    else //if( cursor.equals(RING) )
      return ring;
  }

 
 /**
  * This method returns controls to be used as an alternative to key events
  * when making selections. Also included is a RESET_SELECTED button. If the
  * disableCursor() method has been called, disabled buttons will be grayed-out.
  *
  *  @return array of JButtons, each button corresponding to a region.
  */ 
  public JButton[] getControls()
  {
    ButtonListener button_listener = new ButtonListener();
    JButton[] controls = new JButton[11];
    controls[0] = new JButton(BOX);
    controls[0].addActionListener( button_listener );
    controls[0].setToolTipText("Shortcut Key: Hold B");
    if( disableBox )
      controls[0].setEnabled(false);
    
    controls[1] = new JButton(CIRCLE);
    controls[1].addActionListener( button_listener );
    controls[1].setToolTipText("Shortcut Key: Hold C");
    if( disableCircle )
      controls[1].setEnabled(false);

    controls[2] = new JButton(ELLIPSE);
    controls[2].addActionListener( button_listener );
    controls[2].setToolTipText("Shortcut Key: Hold E");
    if( disableEllipse )
      controls[1].setEnabled(false);
    
    controls[3] = new JButton(DOUBLE_WEDGE);
    controls[3].addActionListener( button_listener );
    controls[3].setToolTipText("Shortcut Key: Hold D");
    if( disableDblWedge )
      controls[3].setEnabled(false);
    
    controls[4] = new JButton(LINE);
    controls[4].addActionListener( button_listener );
    controls[4].setToolTipText("Shortcut Key: Hold L");
    if( disableLine )
      controls[4].setEnabled(false);
    
    controls[5] = new JButton(POINT);
    controls[5].addActionListener( button_listener );
    controls[5].setToolTipText("Shortcut Key: Hold P");
    if( disablePoint )
      controls[5].setEnabled(false);
    
    controls[6] = new JButton(RING);
    controls[6].addActionListener( button_listener );
    controls[6].setToolTipText("Shortcut Key: Hold R");
    if( disableRing )
      controls[6].setEnabled(false);
    
    controls[7] = new JButton(WEDGE);
    controls[7].addActionListener( button_listener );
    controls[7].setToolTipText("Shortcut Key: Hold W");
    if( disableWedge )
      controls[7].setEnabled(false);
    
    controls[8] = new JButton(COMPLEMENT_CURRENT_SELECTION);
    controls[8].addActionListener( button_listener );

    controls[9] = new JButton(RESET_LAST_SELECTED);
    controls[9].addActionListener( button_listener );
    
    controls[10] = new JButton(RESET_SELECTED);
    controls[10].addActionListener( button_listener );
    controls[10].setToolTipText("Shortcut Key: Hold A");
    if( disableAll )
      controls[10].setEnabled(false);
    
    return controls;
  }
  

 /**
  * This method allows users to disable cursors that were previously enabled.
  *
  *  @param  cursor A list of cursor names to be disabled. Cursor names are
  *                 defined by the static strings provided by this class.
  */ 
  public void disableCursor( String[] cursor )
  {
    for( int c = 0; c < cursor.length; c++ )
    {
      if( cursor[c].equals(ALL) )
      {
        disableAll = true;
        disableBox = true;
        disableCircle = true;
        disableEllipse = true;
        disableDblWedge = true;
        disableLine = true;
        disablePoint = true;
        disableRing = true;
        disableWedge = true;
      }
      else if( cursor[c].equals(BOX) )
        disableBox = true;
      else if( cursor[c].equals(CIRCLE) )
        disableCircle = true;
      else if( cursor[c].equals(ELLIPSE) )
        disableEllipse = true;
      else if( cursor[c].equals(DOUBLE_WEDGE) )
        disableDblWedge = true;
      else if( cursor[c].equals(LINE) )
        disableLine = true;
      else if( cursor[c].equals(POINT) )
        disablePoint = true;
      else if( cursor[c].equals(RING) )
        disableRing = true;
      else if( cursor[c].equals(WEDGE) )
        disableWedge = true;
    }
  }

 
 /**
  * This method allows users to enable cursors that were previously disabled.
  *
  *  @param  cursor A list of cursor names to be enabled. Cursor names are
  *                 defined by the static strings provided by this class.
  */ 
  public void enableCursor( String[] cursor )
  {
    for( int c = 0; c < cursor.length; c++ )
    {
      if( cursor[c].equals(ALL) )
      {
        disableAll = false;
        disableBox = false;
        disableCircle = false;
        disableEllipse = false;
        disableDblWedge = false;
        disableLine = false;
        disablePoint = false;
        disableRing = false;
        disableWedge = false;
      }
      else if( cursor[c].equals(BOX) )
        disableBox = false;
      else if( cursor[c].equals(CIRCLE) )
        disableCircle = false;
      else if( cursor[c].equals(ELLIPSE) )
        disableEllipse = false;
      else if( cursor[c].equals(DOUBLE_WEDGE) )
        disableDblWedge = false;
      else if( cursor[c].equals(LINE) )
        disableLine = false;
      else if( cursor[c].equals(POINT) )
        disablePoint = false;
      else if( cursor[c].equals(RING) )
        disableRing = false;
      else if( cursor[c].equals(WEDGE) )
        disableWedge = false;
    }
  }
 

 /**
  * This method is used to check to see if cursors have been disabled.
  *
  *  @param  cursor The cursor name as defined by the static strings provided
  *                 by this class.
  *  @return Value answers this questions: Is cursor enabled? true = yes.
  */ 
  public boolean isEnabled(String cursor)
  {
    // since this class is concerned with disabling, the boolean variables
    // are true if disabled. for this reason, all returns are !disableXXX
    if( cursor.equals(ALL) )
      return !disableAll;
    if( cursor.equals(BOX) )
      return !disableBox;
    if( cursor.equals(CIRCLE) )
      return !disableCircle;
    if( cursor.equals(ELLIPSE) )
      return !disableEllipse;
    if( cursor.equals(DOUBLE_WEDGE) )
      return !disableDblWedge;
    if( cursor.equals(LINE) )
      return !disableLine;
    if( cursor.equals(POINT) )
      return !disablePoint;
    if( cursor.equals(RING) )
      return !disableRing;
    if( cursor.equals(WEDGE) )
      return !disableWedge;
    return false; // should never get here.
  }

  
  /**
   * This method sets all the colors for the selected regions.
   *
   *  @param  color
   */
  public void setRegionColor(Color color) {
    reg_color = color;
    send_message( COLOR_CHANGED );
  }

  
  public Color getColor()
  {
    return reg_color;
  }
  

  public String getName()
  {
    return name;
  }

  
  /**
   * This method sets the opaqueness of the selection. Values will fall in the
   * interval [0,1], with 1 being opaque, and 0 being transparent. 
   *
   *  @param  value - on interval [0,1]
   */
  public void setOpacity(float value) {
    if (value > 1)
      opacity = 1.0f;
    else if (value < 0)
      opacity = 0;
    else
      opacity = value;
    send_message( OPACITY_CHANGED );
  }

  
  public Rectangle getEditorBounds()
  {
    return editor_bounds;
  }

  
  public void setEditorBounds(Rectangle bounds)
  {
    editor.setBounds(bounds);
  }

  
  public float getOpacity()
  {
    return opacity;
  }

  
  public RegionOp.Operation getOp()
  {
    if(unionOpSelected)
      return RegionOp.Operation.UNION;
    else if(intersectOpSelected)
      return RegionOp.Operation.INTERSECT;
    else if(intersectComplementOpSelected)
      return RegionOp.Operation.INTERSECT_COMPLEMENT;
    else
      return RegionOp.Operation.UNION;
  }
  

  /**
   * This method is used to view an instance of the Selection Editor.
   */
  public void editSelection() {
    if (editor.isVisible()) {
      editor.toFront();
      editor.requestFocus();
    } else {
      editor_bounds = editor.getBounds();
      editor.dispose();
      editor = new SelectionEditor();
      WindowShower.show(editor);
      editor.toFront();
    }
  }
  
  
  /**
   * This method will disable the selections and cursors included in the names
   * list. Names are defined by static Strings in the SelectionJPanel class.
   *
   *  @param  select_names List of selection names defined by
   *                       SelectionJPanel class.
   *  @see gov.anl.ipns.ViewTools.Components.Cursor.SelectionJPanel
   */
  public void disableSelection(String[] select_names) {
    disableCursor(select_names);
    sjpbuttons = getControls();

    if (editor.isVisible()) {
      editor.dispose();
      editSelection();
    }
  }


  /**
   * This method will enable the selections and cursors included in the names
   * list. Names are defined by static Strings in the SelectionJPanel class.
   *
   *  @param  select_names List of selection names defined by
   *                       SelectionJPanel class.
   *  @see gov.anl.ipns.ViewTools.Components.Cursor.SelectionJPanel
   */
  public void enableSelection(String[] select_names) {
    enableCursor(select_names);
    sjpbuttons = getControls();

    if (editor.isVisible()) {
      editor.dispose();
      editSelection();
    }
  }

  
 /*
  * This method is to check to see if any selections have been started
  */
  private boolean doingAny()
  {
    if( doing_box || doing_circle || doing_ellipse || doing_line || 
  doing_point || doing_wedge || doing_dblwedge ||
  doing_ring )
      return true;
    // else nothing is selecting
    return false;
  }

  
 /*
  * This method sets all of the is*down to false, thus only the newly pressed
  * button will be used.
  */ 
  private void clearButtonPressed()
  {
    isAdown = false;
    isBdown = false;
    isCdown = false;
    isEdown = false;
    isDdown = false;
    isLdown = false;
    isPdown = false;
    isRdown = false;
    isWdown = false;
  }

  
 /*
  * This method is used to restrict the point to the bounds of the 
  * selectionjpanel.
  * 
  private boolean containsPoint( Point p )
  {
    Dimension panelsize = getSize();
    if( p.x < 0 )
      return false;
    else if( p.x > panelsize.width )
      return false;    
    else if( p.y < 0 )
      return false;
    else if( p.y > panelsize.height )
      return false;
    
    return true;
  }*/


 /*
  * Tells the SelectMouseAdapter is any keys are pressed.
  */
  private class SelectKeyAdapter extends KeyAdapter
  {
    public void keyPressed( KeyEvent e )
    {
      //System.out.println("here in keypressed");
      int code = e.getKeyCode();
      // only make a selection if no other selections are in progress
      if( !doingAny() )
      {
        clearButtonPressed();
        if( code == KeyEvent.VK_A && !disableAll )
        {
          isAdown = true;     
        }
        else if ( code == KeyEvent.VK_B && !disableBox )
        {
          isBdown = true;     
        }
        else if ( code == KeyEvent.VK_C && !disableCircle )
        {
          isCdown = true;    
        }
        else if ( code == KeyEvent.VK_E && !disableEllipse )
        {
          isEdown = true;
        }
        else if ( code == KeyEvent.VK_D && !disableDblWedge )
        {
          isDdown = true;   
        }
        else if ( code == KeyEvent.VK_L && !disableLine )
        {
          isLdown = true;    
        }
        else if ( code == KeyEvent.VK_P && !disablePoint )
        {
          isPdown = true;     
        }
        else if ( code == KeyEvent.VK_R && !disableRing )
        {
          isRdown = true;     
        }
        else if ( code == KeyEvent.VK_W && !disableWedge )
        {
          isWdown = true;    
        }
      }             
    }
    
    public void keyReleased( KeyEvent ke )
    {
      //System.out.println("here in keyreleased");

      int code = ke.getKeyCode();

      if ( code == KeyEvent.VK_A )
      {
        isAdown = false;      
      }    
      else 
      if ( code == KeyEvent.VK_B )
      {
        isBdown = false;    
      }
      else if ( code == KeyEvent.VK_C )
      {
        isCdown = false;     
      }
      else if ( code == KeyEvent.VK_E )
      {
        isEdown = false;     
      }
      else if ( code == KeyEvent.VK_D && !doing_dblwedge )
      {
        isDdown = false;     
      }
      else if ( code == KeyEvent.VK_L )
      {
        isLdown = false;     
      }
      else if ( code == KeyEvent.VK_P )
      {
        isPdown = false;     
      } 
      else if ( code == KeyEvent.VK_R && !doing_ring )
      {
        isRdown = false;     
      }
      else if ( code == KeyEvent.VK_W && !doing_wedge )
        isWdown = false;
    }
  }
  
 /*
  * This class receives button events and substitutes them for key events.
  */ 
  private class ButtonListener implements ActionListener
  {
    public void actionPerformed( ActionEvent ae )
    {
      String message = ae.getActionCommand();
      
      // only make a selection if no other selections are in progress
      if ( !doingAny() )
      {
        clearButtonPressed();
        if ( message.equals(BOX) && !disableBox )
        {
          isBdown = true;
        }
        else if ( message.equals(CIRCLE) && !disableCircle )
        {
          isCdown = true;
        }
        else if ( message.equals(ELLIPSE) && !disableEllipse )
        {
          isEdown = true;
        }
        else if ( message.equals(DOUBLE_WEDGE) && !disableDblWedge )
        {
          isDdown = true;
        }
        else if ( message.equals(LINE) && !disableLine )
        {
          isLdown = true;
        }
        else if ( message.equals(POINT) && !disablePoint )
        {
          isPdown = true;
        }
        else if ( message.equals(RING) && !disableRing )
        {
          isRdown = true;
        }
        else if ( message.equals(WEDGE) && !disableWedge )
        {
          isWdown = true;
        }
        else if ( message.equals(RESET_SELECTED) )
        {
          send_message(RESET_SELECTED);
        }
        else if ( message.equals(RESET_LAST_SELECTED) )
        {
          send_message(RESET_LAST_SELECTED);
        }
        else if ( message.equals(COMPLEMENT_CURRENT_SELECTION))
        {
          send_message(COMPLEMENT_CURRENT_SELECTION);
        }
      } // end if !doingAny
    }
  }
  

  private class JComboBoxListener implements ActionListener
  {
    public void actionPerformed(ActionEvent e)
    {
      String message = (String)opChooser.getSelectedItem();
      if ( message.equals(UNION) )
      {
        unionOpSelected = true;
        intersectOpSelected = false;
        intersectComplementOpSelected = false;
      }
      else if ( message.equals(INTERSECT) )
      {
        unionOpSelected = false;
        intersectOpSelected = true;
        intersectComplementOpSelected = false;
      }
      else if ( message.equals(INTERSECT_COMPLEMENT ) )
      {
        unionOpSelected = false;
        intersectOpSelected = false;
        intersectComplementOpSelected = true;
      }
    }
  }


 /*
  * This class used flags set by the SelectKeyAdapter class to determine
  * an action.
  */
  private class SelectMouseAdapter extends MouseAdapter
  {
    public void mouseClicked (MouseEvent e)
    {
      if ( e.getClickCount() == 2 ) 
        send_message( RESET_LAST_SELECTED );
    }

    public void mousePressed ( MouseEvent e )
    {
      //System.out.println("here in mousepressed");
      // if A is pressed, delete all selections.      
      if ( isAdown )
        send_message(RESET_SELECTED);
      else if ( isBdown )
        set_cursor( box, e.getPoint() );
      else if ( isCdown )
        set_cursor( circle, e.getPoint() );
      else if ( isEdown )
        set_cursor( ellipse, e.getPoint() );
      else if ( isDdown )
        set_cursor( dblwedge, e.getPoint() );
      else if ( isLdown )
        set_cursor( line, e.getPoint() ); 
      else if ( isPdown )
        set_cursor( point, e.getPoint() );
      else if ( isRdown )
        set_cursor( ring, e.getPoint() );
      else if ( isWdown )
        set_cursor( wedge, e.getPoint() );
    }

    public void mouseReleased(MouseEvent e)
    {
      //System.out.println("here in mousereleased");
      Point current = e.getPoint();
      //boolean validpoint = containsPoint( current );
      String message = REGION_SELECTED;
      if( doing_box )
      {
        stop_cursor( box, current );
        isBdown = false;
        message += ">" + BOX;
      }
      else if( doing_circle )
      {
        stop_cursor( circle, current );
        isCdown = false;
        message += ">" + CIRCLE;
      }
      else if( doing_ellipse )
      {
        stop_cursor( ellipse, current );
        isEdown = false;
        message += ">" + ELLIPSE;
      }
      else if( doing_line )
      {
        stop_cursor( line, current );
        isLdown = false;
        message += ">" + LINE;
      }
      else if( doing_point )
      {
        stop_cursor( point, current ); 
        isPdown = false;
        message += ">" + POINT; 
      } 
      else if( doing_ring )
      {
        stop_cursor( ring, current ); 
        if( firstRun )
        {
          isRdown = true;
        }
        else
        {
          isRdown = false;
          message += ">" + RING;
        }
        firstRun = !firstRun; 
      }    
      else if( doing_wedge )
      {
        stop_cursor( wedge, current ); 
        if( firstRun )
        {
          isWdown = true;
        }
        else
        {
          isWdown = false;
          message += ">" + WEDGE;
        }
        firstRun = !firstRun; 
      }   
      else if( doing_dblwedge )
      {
        stop_cursor( dblwedge, current ); 
        if( firstRun )
        {
          isDdown = true;
        }
        else
        {
          isDdown = false;
          message += ">" + DOUBLE_WEDGE;
        }
        firstRun = !firstRun; 
      } 
      send_message(message);       
    }
  } 
  

 /*
  * Redraw the specified cursor, giving the rubber band stretch effect.
  */ 
  private class SelectMouseMotionAdapter extends MouseMotionAdapter
  {
    public void mouseDragged(MouseEvent e)
    {
      //System.out.println("here in mousedragged");

      //System.out.println("Point: " + e.getPoint() );
      if ( doing_box )
        set_cursor( box, e.getPoint() );
      else if ( doing_circle )
        set_cursor( circle, e.getPoint() );
      else if ( doing_ellipse )
        set_cursor( ellipse, e.getPoint() );
      else if ( doing_dblwedge )
        set_cursor( dblwedge, e.getPoint() );
      else if ( doing_line )
        set_cursor( line, e.getPoint() );
      else if ( doing_point )
        set_cursor( point, e.getPoint() );
      else if ( doing_ring )
        set_cursor( ring, e.getPoint() );
      else if ( doing_wedge )
        set_cursor( wedge, e.getPoint() );
    }
  }   
  
  
  /*
   * This class is the editor for the Selection Overlay. This is used to 
   * create a selection, change opacity of a selection, and change selection
   * color.
   */
  private class SelectionEditor extends JFrame {
    
    private JPanel pane;
    private SelectionEditor this_editor;

    public SelectionEditor() {
      super("SelectionEditor");
      this.setBounds(editor_bounds);
      this_editor = this;
      pane = new JPanel();
      new BoxLayout(pane, BoxLayout.Y_AXIS);

      // Number of grid rows needed for the selection type buttons,
      // and add one in for the JLabel.
      int gridrows = (int) Math.ceil((double) (sjpbuttons.length + 1) / 3);

      // If number of rows are specified, the number of columns doesn't matter.
      JPanel sjpcontrols = new JPanel(new GridLayout(gridrows, 1));
      sjpcontrols.add(new JLabel("Add Selection"));

      for (int i = 0; i < sjpbuttons.length; i++)
        sjpcontrols.add(sjpbuttons[i]);

      pane.add(sjpcontrols);

      JPanel opPanel = new JPanel();
      opPanel.add(opLabel);
      opPanel.add(opChooser);
      pane.add(opPanel);
      
      ColorSelector color_chooser = new ColorSelector( ColorSelector.SWATCH );
      color_chooser.addActionListener(new ControlListener());

      pane.add(color_chooser);

      // Slider that controls the opaqueness of the selections.
      ControlSlider opacityscale = new ControlSlider(
          "Selection Opacity Scale");
      opacityscale.setStep(.01f);
      opacityscale.setRange(0f, 1f);
      opacityscale.setMajorTickSpace(.2f);
      opacityscale.setMinorTickSpace(.05f);
      opacityscale.setValue(opacity);
      opacityscale.addActionListener(new ControlListener());

      JButton closebutton = new JButton("Close");
      closebutton.addActionListener(new ControlListener());
      JPanel spacer = new JPanel();
      spacer.setPreferredSize(new Dimension(editor_bounds.width / 4, 0));

      JPanel slider_and_close = new JPanel(new BorderLayout());
      slider_and_close.add(opacityscale, BorderLayout.WEST);
      slider_and_close.add(spacer, BorderLayout.CENTER);
      slider_and_close.add(closebutton, BorderLayout.EAST);

      pane.add(slider_and_close);

      this.getContentPane().add(pane);
      this_editor.addComponentListener(new EditorListener());
    }


    /*
     * Private listener for the SelectionEditor. This class listens to all
     * of the controls on the editor.
     */
    class ControlListener implements ActionListener {

      public void actionPerformed(ActionEvent ae) {

        String message = ae.getActionCommand();

        if ( message.equals( ColorSelector.COLOR_CHANGED ) ) {
          setRegionColor( ((ColorSelector) ae.getSource() )
              .getSelectedColor());
        }

        else if (message.equals(ControlSlider.SLIDER_CHANGED)) {
          setOpacity(((ControlSlider) ae.getSource()).getValue());
        } 

        else if (message.equals("Close")) {
          editor_bounds = this_editor.getBounds();
          this_editor.dispose();
        }

        //send repaint message
      }
    }


    class EditorListener extends ComponentAdapter {
      public void componentResized(ComponentEvent we) {
        editor_bounds = editor.getBounds();
      }
    }
  }


  private class NotVisibleListener extends ComponentAdapter {
    public void componentHidden(ComponentEvent ce) {
      editor.setVisible(false);
    }
  }

  
/* -----------------------------------------------------------------------
 *
 * MAIN PROGRAM FOR TEST PURPOSES
 *
 */

  public static void main(String[] args)
  {
    JFrame f = new JFrame("Test for SelectionJPanel");
    f.setBounds(0,0,500,500);
    f.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    SelectionJPanel panel = new SelectionJPanel();
    f.getContentPane().add(panel);
    f.setVisible(true);
    panel.requestFocus();
  }  
}
