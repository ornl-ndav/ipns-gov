/*
 * File: SelectionOverlay.java
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
 * This work was supported by the National Science Foundation under grant
 * number DMR-0218882, and by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 * Modified:
 *
 *  $Log$
 *  Revision 1.58  2007/07/29 19:20:49  dennis
 *  Minor cleanup.  Removed some commented out variables that are
 *  no longer used.  Simplified references by current_bounds fields.
 *
 *  Revision 1.57  2007/07/23 20:47:03  dennis
 *  Now updates the pixel local transform BEFORE drawing the interior
 *  rather than after drawing the interior, but BEFORE drawing the
 *  region boundary.  This is a partial fix to the problem where the
 *  interior is not draw properly.  However, there still seems to
 *  be a problem relating to the cursors and the drawing of the
 *  region boundaries.
 *  Removed local list of listeners, and routines to manage the list,
 *  since that functionality is provided by the ActionJPanel class,
 *  which this (indirectly) extends.
 *
 *  Revision 1.56  2007/07/21 02:33:23  dennis
 *  Replacing paint() with paintComponent(), again.
 *  (Previous change was lost.)
 *
 *  Revision 1.55  2007/07/12 16:53:41  oakgrovej
 *  Added closeWindows() and closeEditors() methods
 *  removed some of the excessive Paint() calls
 *
 *  Revision 1.54  2007/07/11 18:36:12  dennis
 *  Replaced paint() by paintComponent, removed call to super.paint(),
 *  and now work with a Graphics2D object that is a copy of the original
 *  Graphics object.
 *
 *  Revision 1.53  2007/07/10 18:41:00  oakgrovej
 *  Changed the line thickness drawn over a region.
 *
 *  Revision 1.52  2007/06/25 18:55:23  oakgrovej
 *  increased the cursor thickness for the line cursor
 *
 *  Revision 1.51  2007/06/22 21:17:57  oakgrovej
 *  The wedge and the double wedge when clicked on, pass the whole 
 *  defining points array into the editor.
 *
 *  Revision 1.50  2007/06/15 22:49:00  oakgrovej
 *  Added vector to hold list of editors
 *  added cursor to be drawn if editing
 *  getAllNames()
 *  paint method paints a cursor if there is one
 *  RegionEditorPropertyChangeListener
 *  click message recieved
 *
 *  Revision 1.49  2007/05/28 20:36:50  dennis
 *  Added method showEditor() to pop up the editor for the named
 *  regionOpList.
 *  Modified method getRegionOpListWithColor() to create a new regionOpList,
 *  save it in the Hashtable of regionOpLists, and return a reference to the
 *  new regionOpList, if the named regionOpList does not already exist.
 *  (Jonathan Morck)
 *  Added java docs to these methods. (dennis)
 *
 *  Revision 1.48  2007/04/29 20:29:02  dennis
 *  Now uses removeLast() method from SelectionJPanel, to UNDO the
 *  last operation.
 *
 *  Revision 1.47  2007/04/29 18:25:00  dennis
 *  Removed unused imports.
 *  Fixed reference to static constant to be in terms of class name.
 *
 *  Revision 1.46  2007/04/28 05:54:06  dennis
 *  Added support for OPACITY_CHANGED and COLOR_CHANGED messages.
 *
 *  Revision 1.45  2007/04/28 03:32:09  dennis
 *  Refactored and added/modified methods to deal with named
 *  RegionOpLists. (Joshua Oakgrove, Galina Pozharsky, Terry Farmer,
 *  Chad Diller, Jonathan Morck).
 *
 *  Revision 1.44  2007/04/07 21:23:00  dennis
 *  Removed unused import.
 *
 *  Revision 1.43  2007/03/16 18:44:13  dennis
 *  Adapted to work with new Region classes.
 *  No longer keeps world to array transformation as state information,
 *  but gets the current world to array transform from the IViewComponent2D
 *  object it is overlaid on.
 *
 *  Revision 1.42  2007/03/11 04:32:53  dennis
 *  Added code to set up transformation from Array coordinates to
 *  the world coordinate system.  Added method to apply this
 *  transform. Added code to in paintPointArray() method to map
 *  the centers of selected array positions to pixels.
 *
 *  Revision 1.41  2007/03/07 21:47:20  dennis
 *  Added paintPointArray() method from Josh Oakgrove and Terry Farmer.
 *  This routine will be used to fill the interior of regions selected
 *  on an ImageViewComponent.  These changes are not yet complete, and
 *  this method is not called yet.
 *
 *  Revision 1.40  2005/05/25 20:28:33  dennis
 *  Now calls convenience method WindowShower.show() to show
 *  the window, instead of instantiating a WindowShower object
 *  and adding it to the event queue.
 *
 *  Revision 1.39  2005/01/20 23:05:52  millermi
 *  - Added super.paint(g) to paint method.
 *
 *  Revision 1.38  2004/05/18 19:40:17  millermi
 *  - Changed layout of editor to BoxLayout.
 *  - Make use of set...() methods to set private variables.
 *
 *  Revision 1.37  2004/04/02 20:58:33  millermi
 *  - Fixed javadoc errors
 *
 *  Revision 1.36  2004/03/15 23:53:53  dennis
 *  Removed unused imports, after factoring out the View components,
 *  Math and other utils.
 *
 *  Revision 1.35  2004/03/12 03:14:15  serumb
 *  Change package and imports.
 *
 *  Revision 1.34  2004/02/14 03:37:23  millermi
 *  - Replaced all WCRegion code with Region class.
 *  - if regions are in setObjectState(), REGION_ADDED message
 *    now sent to listeners.
 *  - Removed "Selected" from method names.
 *
 *  Revision 1.33  2004/02/06 23:23:43  millermi
 *  - Changed how editor bounds were stored in the ObjectState,
 *    removed check if visible.
 *
 *  Revision 1.32  2004/01/30 22:16:13  millermi
 *  - Changed references of messaging Strings from the IViewControl
 *    to the respective control that sent the message.
 *
 *  Revision 1.31  2004/01/29 08:16:28  millermi
 *  - Updated the getObjectState() to include parameter for specifying
 *    default state.
 *  - Added static variables DEFAULT and PROJECT to IPreserveState for
 *    use by getObjectState()
 *
 *  Revision 1.30  2004/01/07 17:54:33  millermi
 *  - Fixed javadoc errors
 *
 *  Revision 1.29  2004/01/03 04:36:13  millermi
 *  - help() now uses html tool kit to display text.
 *  - Replaced all setVisible(true) with WindowShower.
 *
 *  Revision 1.28  2003/12/30 00:39:37  millermi
 *  - Added Annular selection capabilities.
 *  - Changed SelectionJPanel.CIRCLE to SelectionJPanel.ELLIPSE
 *
 *  Revision 1.27  2003/12/29 06:35:04  millermi
 *  - Added addSelectedRegion() so regions could be added
 *    through commands and not just through the GUI.
 *  - Made paint() more robust, if region has null defining
 *    point, the region is deleted from the list of regions.
 *
 *  Revision 1.26  2003/12/23 02:21:36  millermi
 *  - Added methods and functionality to allow enabling/disabling
 *    of selections.
 *  - Fixed interface package changes where applicable.
 *
 *  Revision 1.25  2003/12/20 21:37:29  millermi
 *  - implemented kill() so editor and help windows are now
 *    disposed when the kill() is called.
 *
 *  Revision 1.24  2003/12/20 20:07:39  millermi
 *  - Added clearSelectedRegions() so selections can be cleared by
 *    method call.
 *
 *  Revision 1.23  2003/12/18 22:54:37  millermi
 *  - 3 defining points are now passed to the EllipseRegion.
 *    The new point, the center is used in case the selection
 *    is made at the edge of the image.
 *
 *  Revision 1.22  2003/11/21 02:59:55  millermi
 *  - Now saves editor bounds before dispose() is called on
 *    the editor.
 *
 *  Revision 1.21  2003/11/18 01:00:17  millermi
 *  - Made non-save dependent private variables transient.
 *
 *  Revision 1.20  2003/10/20 22:46:53  millermi
 *  - Added private class NotVisibleListener to listen
 *    when the overlay is no longer visible. When not
 *    visible, any editor that is visible will be made
 *    invisible too. This will not dispose the editor,
 *    just setVisible(false).
 *
 *  Revision 1.19  2003/10/16 05:00:09  millermi
 *  - Fixed java docs errors.
 *
 *  Revision 1.18  2003/10/02 23:04:42  millermi
 *  - Added java docs to all public static variables.
 *  - Added constructor to take in ObjectState information.
 *
 *  Revision 1.17  2003/09/24 01:33:41  millermi
 *  - Added static variables to be used as keys by ObjectState
 *  - Added methods setObjectState() and getObjectState() to adjust to
 *    changes made in OverlayJPanel.
 *  - Added componentResized() listener to set editor bounds
 *    when the editor is resized.
 *
 *  Revision 1.16  2003/08/26 03:41:05  millermi
 *  - Added functionality and help() comments for double wedge selection.
 *
 *  Revision 1.15  2003/08/21 18:18:17  millermi
 *  - Updated help() to reflect new controls in editor.
 *  - Added capabilities for wedge selection
 *
 *  Revision 1.14  2003/08/18 20:52:40  millermi
 *  - Added "Add Selection" controls to SelectionEditor so user no longer
 *    needs to know the keyboard events to make selections.
 *  - Added javadoc comments.
 *
 *  Revision 1.13  2003/08/14 22:57:44  millermi
 *  - ControlSlider for editor now ranging from 0-100 which
 *    decreased the number of increments in the slider.
 *
 *  Revision 1.12  2003/08/14 21:48:11  millermi
 *  - Added toFront() to SelectionEditor to display it over the viewer.
 *
 *  Revision 1.11  2003/08/14 17:11:57  millermi
 *  - Added SelectionEditor class
 *  - Now capable of changing the selection color and also the opacity
 *    of the selections.
 *  - Edited help() to provide more description.
 *
 *  Revision 1.10  2003/08/11 23:45:23  millermi
 *  - grouped multiple sendMessage() statements into one statement
 *    after all if statements.
 *  - Added static variable ALL_REGIONS_REMOVED for messaging.
 *
 *  Revision 1.9  2003/08/08 15:54:24  millermi
 *  - Edited Revision 1.8 so it did not exceed 80 characters per line.
 *  - Now uses method getWorldCoordPoints() to access WCRegion class.
 *    This method is more efficient because it returns the whole
 *    array of points, as opposed to getWorldCoordPointAt() which
 *    requires multiple method calls.
 *
 *  Revision 1.8  2003/08/07 22:47:55  millermi
 *  - Added line selection capabilities
 *  - Changed Help menu for REMOVE ALL SELECTIONS from "Double" to "Single" 
 *    click
 *  - Usage of Region class changed to WCRegion class and adapted for generic
 *    number of points
 *
 *  Revision 1.7  2003/08/07 17:57:41  millermi
 *  - Added line selection capabilities
 *  - Changed Help menu for REMOVE ALL SELECTIONS from "Double" to "Single"
 *    click
 *
 *  Revision 1.6  2003/08/06 13:56:45  dennis
 *  - Added sjp.setOpaque(false) to constructor. Fixes bug when
 *    Axis Overlay is turned off and Selection Overlay is on.
 *
 *  Revision 1.5  2003/07/25 14:39:34  dennis
 *  - Constructor now takes component of type IZoomAddible instead of
 *    IAxisAddible2D
 *  - Private class Region now moved to an independent file to allow for use by
 *    components using the selection overlay.
 *  - Added public methods addActionListener(), removeActionListener(),
 *    removeAllActionListeners(), and private method sendMessage() to allow
 *    listeners for when a selection occurs.
 *  - Added getSelectedRegion()
 *    (Mike Miller)
 *
 *  Revision 1.4  2003/06/17 13:21:37  dennis
 *  (Mike Miller)
 *  - Made selections zoomable. clipRect() method was added to paint
 *    to restrict the painted area to only that directly above
 *    the center panel.
 *
 *  Revision 1.3  2003/06/09 14:47:19  dennis
 *  Added static method help() to display commands via the HelpMenu.
 *  (Mike Miller)
 *
 *  Revision 1.2  2003/06/05 22:07:21  dennis
 *     (Mike Miller)
 *   - Added resize capability
 *   - Corrected duplication error
 *   - Added private class Region which includes the current_bounds for each
 *     region in the regions vector.
 *   - Added getFocus() method to fix keylistener problems
 *
 *  Revision 1.1  2003/05/29 14:29:20  dennis
 *  Initial version, current functionality does not support
 *  annotation editing or annotation deletion. (Mike Miller)
 * 
 */

/* *************************************************************
 * *********Basic controls for the Selection Overlay************
 * *************************************************************
 * Keyboard Event    * Mouse Event       * Action         *
 ***************************************************************
 * press B       * Press/Drag mouse  * box selection       *
 * press C       * Press/Drag mouse  * circle selection    *
 * press L       * Press/Drag mouse  * line selection      *
 * press P       * Press/Drag mouse  * point selection     * 
 * none          * Double click      * clear last selected *
 * press A (all) * Single click      * clear all selected  *
 ***************************************************************
 * Important: 
 * All keyboard events must be done prior to mouse events.
 */

package gov.anl.ipns.ViewTools.Components.Transparency;

import javax.swing.*;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Enumeration;
import java.util.Vector;
import java.util.Hashtable;
import java.lang.Math;

import gov.anl.ipns.ViewTools.Panels.Cursors.*;
import gov.anl.ipns.ViewTools.Components.ObjectState;
import gov.anl.ipns.ViewTools.Components.Region.*;
import gov.anl.ipns.ViewTools.Components.Cursor.*;
import gov.anl.ipns.ViewTools.Components.TwoD.IViewComponent2D;
import gov.anl.ipns.Util.Numeric.floatPoint2D;
import gov.anl.ipns.Util.Sys.WindowShower;
import gov.anl.ipns.ViewTools.Panels.Transforms.*;
import gov.anl.ipns.ViewTools.Components.Region.RegionOpListWithColor;
import gov.anl.ipns.ViewTools.Components.RegionOpEditFrames.*;

/**
 * This class allows users to select a region for calculation purposes.
 * Three types of regions may currently be selected: point, box, and circle.
 * The selected region will initially show up in white.  Since this class
 * extends an OverlayJPanel, which extends a JPanel, this class is
 * already serializable.
 */
public class SelectionOverlay extends OverlayJPanel {
  /**
   * "REGION_ADDED" - This constant String is an Action Listener message
   * sent out when a new region has been selected.
   */
  public static final String REGION_ADDED = "REGION_ADDED";

  /**
   * "REGION_REMOVED" - This constant String is an Action Listener message
   * sent out when a region has been deselected/removed.
   */
  public static final String REGION_REMOVED = "REGION_REMOVED";

  /**
   * "ALL_REGIONS_REMOVED" - This constant String is an Action Listener message
   * sent out when all regions have been deselected/removed.
   */
  public static final String ALL_REGIONS_REMOVED = "ALL_REGIONS_REMOVED";

  // these variables are used to preserve the Selection state.
  /**
   * "Selected Regions" - This constant String is a key for referencing the
   * state information about which regions have been selected.
   * The value that this key references is a Vector of Region instances.
   */
  public static final String SELECTED_REGIONS = "Selected Regions";

  /**
   * "Selection Color" - This constant String is a key for referencing the
   * state information about the color of the selection outlines.
   * The value that this key references is of type Color.
   */
  public static final String SELECTION_COLOR = "Selection Color";

  /**
   * "Opacity" - This constant String is a key for referencing the
   * state information about the invisibility of the selection outline.
   * The value that this key references is a primative float on the range
   * [0,1], with 0 = transparent, 1 = opaque.
   */
  public static final String OPACITY = "Opacity";

  /**
   * "Editor Bounds" - This constant String is a key for referencing the state
   * information about the size and bounds of the Selection Editor window. 
   * The value that this key references is a Rectangle. The Rectangle contains
   * the dimensions for the editor.
   */
  //public static final String EDITOR_BOUNDS = "Editor Bounds";

  private static JFrame helper = null;

  private transient SelectionJPanel sjp; // panel overlaying the center jpanel

  private transient IZoomAddible component; // component being passed

  private Hashtable<String, RegionOpListWithColor> regionOpLists;

  // used for repaint by SelectListener 
  private transient SelectionOverlay this_panel;

  private transient Rectangle current_bounds;

  private transient CoordTransform pixel_local; // pixel coords to WC

  //private transient SelectionEditor editor;

  private String regionName = "Default";
  private RegionOp.Operation operation = RegionOp.Operation.UNION;
  private Vector<RegionOpEditFrame> Editors = new Vector<RegionOpEditFrame>();
  private floatPoint2D[] cursorPoints;
  private CursorTag cursor;


  /**
   * Constructor creates an overlay with a SelectionJPanel that shadows the
   * center panel of the IZoomAddible component.
   *
   *  @param  iza - IZoomAddible component
   */
  public SelectionOverlay(IZoomAddible iza) {
    super();
    this.setLayout(new GridLayout(1, 1));
    sjp = new SelectionJPanel( regionName, Color.RED, 1.0f );
    sjp.setOpaque(false);
    component = iza;

    regionOpLists = new Hashtable<String, RegionOpListWithColor>();
    regionOpLists.put(regionName, new RegionOpListWithColor());

    this_panel = this;

    this.add(sjp);
    sjp.addActionListener(new SelectListener());
    current_bounds = component.getRegionInfo();
    CoordBounds pixel_map = new CoordBounds(
                                  current_bounds.x,
                                  current_bounds.y,
                                  current_bounds.x + current_bounds.width,
                                  current_bounds.y + current_bounds.height );

    pixel_local = new CoordTransform( pixel_map, 
                                      component.getLocalCoordBounds());
    sjp.requestFocus();
  }


  /**
   * Constructor creates an SelectionOverlay with previous state information.
   *
   *  @param  iza - IZoomAddible component
   *  @param  state - ObjectState of this overlay
   */
  public SelectionOverlay(IZoomAddible iza, ObjectState state) {
    this(iza);
    setObjectState(state);
  }


  /**
   * Contains/Displays control information about this overlay.
   */
  public static void help() {
    helper = new JFrame("Help for Selection Overlay");
    helper.setBounds(0, 0, 600, 400);
    JEditorPane textpane = new JEditorPane();
    textpane.setEditable(false);
    textpane.setEditorKit(new HTMLEditorKit());
    String text = "<H1>Description:</H1>"
        + "<P>The Selection Overlay is used to selection regions of "
        + "data for analysis. The selected region will initially be "
        + "outlined in white, unless otherwise specified.</P>"
        + "<H2>Commands for Selection Overlay</H2>" + "<P>Note:<BR>"
        + "- These commands will NOT work if the Annotation "
        + "Overlay checkbox IS checked or if the Selection "
        + "Overlay IS NOT checked.<BR>"
        + "- Zooming on the image is only allowed if this overlay "
        + "is turned off.</P>" + "<H2>Image Commands:</H2>"
        + "<P>Click/Drag/Release Mouse w/B_Key pressed>"
        + "ADD BOX SELECTION<BR>"
        + "Click/Drag/Release Mouse w/C_Key pressed>"
        + "ADD ELLIPSE SELECTION<BR>"
        + "Click/Drag/Release Mouse w/D_Key pressed>"
        + "ADD DOUBLE WEDGE SELECTION<BR>"
        + "Click/Drag/Release Mouse w/L_Key pressed>"
        + "ADD LINE SELECTION<BR>"
        + "Click/Drag/Release Mouse w/P_Key pressed>"
        + "ADD POINT SELECTION<BR>"
        + "Click/Drag/Release Mouse w/R_Key pressed>"
        + "ADD RING SELECTION<BR>"
        + "Click/Drag/Release Mouse w/W_Key pressed>"
        + "ADD WEDGE SELECTION<BR>"
        + "Double Click Mouse>REMOVE LAST SELECTION<BR>"
        + "Single Click Mouse w/A_Key>REMOVE ALL SELECTIONS</P>"
        + "<H2>Selection Editor Commands <BR>"
        + "(Edit button under Selection Overlay Control)</H2><P>"
        + "Click on button corresponding to region type in editor, "
        + "then on image Click/Drag/Release mouse to ADD SELECTION"
        + "<BR>Move slider to CHANGE OPACITY OF SELECTION. If highly "
        + "opaque, lines show bright. Low opacity makes selections "
        + "clear or transparent.<BR>"
        + "Click on \"Change Color\" to CHANGE COLOR OF SELECTION.</P>";

    textpane.setText(text);
    JScrollPane scroll = new JScrollPane(textpane);
    scroll
        .setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    helper.getContentPane().add(scroll);
    WindowShower.show(helper);
  }


  /**
   * This method will set the current state variables of the object to state
   * variables wrapped in the ObjectState passed in.
   *
   *  @param new_state
   */
  public void setObjectState(ObjectState new_state) {
    boolean redraw = false; // if any values are changed, repaint overlay.
    Object temp = new_state.get(SELECTED_REGIONS);
    if (temp != null) {
      regionOpLists = ((Hashtable) temp);
      redraw = true;
      // only send message if region was added.
      if (regionOpLists.size() > 0)
        send_message(REGION_ADDED);
    }

    temp = new_state.get(SELECTION_COLOR);
    if (temp != null) {
      sjp.setRegionColor((Color) temp);
      redraw = true;
    }

    temp = new_state.get(OPACITY);
    if (temp != null) {
      sjp.setOpacity(((Float) temp).floatValue());
      redraw = true;
    }

    temp = new_state.get(SelectionJPanel.EDITOR_BOUNDS);
    if (temp != null) {
      sjp.setEditorBounds((Rectangle) temp);
    }

    if (redraw)
      this_panel.repaint();
  }


  /**
   * This method will get the current values of the state variables for this
   * object. These variables will be wrapped in an ObjectState.
   *
   *  @param  isDefault Should selective state be returned, that used to store
   *                    user preferences common from project to project?
   *  @return if true, the default state containing user preferences,
   *          if false, the entire state, suitable for project specific saves.
   */
  public ObjectState getObjectState(boolean isDefault) {
    ObjectState state = new ObjectState();
    state.insert(SELECTION_COLOR, sjp.getColor());
    state.insert(OPACITY, new Float(sjp.getOpacity()));
    state.insert(SelectionJPanel.EDITOR_BOUNDS, sjp.getEditorBounds());

    // load these for project specific instances.
    if (!isDefault) {
      state.insert(SELECTED_REGIONS, regionOpLists);
    }

    return state;
  }


  /**
   * This method sets the opaqueness of the selection. Values will fall in the
   * interval [0,1], with 1 being opaque, and 0 being transparent. 
   *
   *  @param  value - on interval [0,1]
   */
  public void setOpacity(float value) {
    setRegionOpListOpacity( regionName, value );
  }


  //TODO CHANGED HERE
  /**
   * Set the opacity for the specified RegionOpList
   *
   * @param  name   the name of the RegionOpList to change
   * @param  value  the new opacity value to use when drawing the
   *                specified RegionOpList
   */
  public void setRegionOpListOpacity( String name, float value ){
    if ( value > 1 ) {
      getRegionOpListWithColor(name).setOpacity(1.0f);
    }
    else if ( value < 0 ){
      getRegionOpListWithColor(name).setOpacity(0.0f);
    }
    else{
      getRegionOpListWithColor(name).setOpacity(value);
    }
    this_panel.repaint();
  }


  /**
   * This method is used to view an instance of the Selection Editor.
   */
  public void editSelection() {
    sjp.editSelection();
  }


  /**
   *  This method with get a reference to the specified RegionOpListWithColor
   *  from the list of named regionOpLists, if the named regionOpList exists.
   *  If the name has not been previously used, this method creates a new
   *  empty regionOpList with that name, saves it, and returns a reference
   *  to the new list.
   *
   *  @param  name  The name of the regionOpListWithColor that should be 
   *                returned.
   *
   *  @return  The regionOpListWithColor, specified by the name, will be
   *           returned.  This will be a new empty list, if the named list
   *           did not previously exist.
   */
  public RegionOpListWithColor getRegionOpListWithColor( String name ) {
    if (regionOpLists.containsKey(name)) {
      return regionOpLists.get(name);
    } else {
      RegionOpListWithColor temp = new RegionOpListWithColor();
      regionOpLists.put(name, temp);
      return temp;
    }
  }


  /**
   * This method gets the vector containing all of the selected regions. All
   * regions in the vector are in a Region wrapper.
   *
   *  @return region vector
   */
  public Vector getRegions() {
    Vector regions = new Vector();
    Vector<RegionOp> regionOps = getRegionOpListWithColor(regionName).getList();
    for (RegionOp op : regionOps) {
      regions.add(op.getRegion());
    }
    return regions;
  }
  
  /**
   * Gets the names of the regionOpLists.
   * @return String array containing the names of the regionOpLists
   */
  public String[] getAllNames()
  {
    Vector<String> allTheNames = new Vector<String>();
    Enumeration<String> theNames = regionOpLists.keys();
    while(theNames.hasMoreElements())
       allTheNames.addElement(theNames.nextElement());
    Object[] allNamesObj = allTheNames.toArray();
    String[] allNames = new String[allNamesObj.length];
    for (int i=0;i<allNamesObj.length;i++)
      allNames[i] = allNamesObj[i].toString();
    return allNames;
  }
  
  public SelectionJPanel getSelectionJPanel()
  {
    return sjp;
  }


  /**
   * Remove all selections from the overlay.
   */
  public void clearRegions() {
    getRegionOpListWithColor(regionName).getList().clear();
    send_message(ALL_REGIONS_REMOVED);
  }


  /**
   * This method allows a user to add a region with a method instead of by
   * using the GUI.
   *
   *  @param  reg The array of Regions to be added.
   */
  public void addRegions( Region[] reg ) {
    // ignore if null
    if (reg == null || reg.length == 0)
      return;
    // add all regions in the array.
    for (int i = 0; i < reg.length; i++) {
      regionOpLists.get(regionName).add(
          new RegionOp(reg[i], RegionOp.Operation.UNION));
    }

    // send message that region was added.
    send_message(REGION_ADDED);
  }


  /**
   * This method sets the draw color for the selected regions in the 
   * default RegionOpList. 
   *
   *  @param  color The color to use when drawing the default selected regions
   */
  public void setRegionColor( Color color ) {
    setRegionOpListColor( regionName, color );
  }


  //TODO ADDED STUFF HERE
  /**
   * This method sets the draw color for the selected regions in the 
   * specified RegionOpList. 
   *
   * @param  name  The name of the RegionOpList whose color is to be changed
   * @param  color The color to use when drawing the default selected regions
   */
  public void setRegionOpListColor( String name, Color color ){
    getRegionOpListWithColor(name).setColor(color);
    this_panel.repaint();
  }


  /**
   * This method gives focus to the SelectionJPanel, which is overlayed on the
   * center of the IZoomAddible component.
   */
  public void getFocus() {
    sjp.requestFocus();
  }


  /**
   * This method is called by to inform the overlay that it is no
   * longer needed. In turn, the overlay closes all windows created
   * by it before closing.
   */
  public void kill() {
    //editor.dispose();
    if (helper != null)
      helper.dispose();
  }


  /**
   * This method will disable the selections and cursors included in the names
   * list. Names are defined by static Strings in the SelectionJPanel class.
   *
   *  @param  select_names List of selection names defined by
   *                       SelectionJPanel class.
   *  @see gov.anl.ipns.ViewTools.Components.Cursor.SelectionJPanel
   */
  public void disableSelection( String[] select_names ) {
    sjp.disableSelection(select_names);
  }


  /**
   *  This method will show (or hide) the selection editor for the specified
   *  regionOpList.  
   *
   *  @param  name       The name of the regionOpList for which the editor
   *                     should be shown, or hidden.
   *
   *  @param  show_hide  flag indicating whether to show (true) or hide (false)
   *                     the editor for the specified regionOpList.
   */
  public void showEditor(String name, boolean show_hide)
  {
    if(sjp != null && !show_hide)
    {
      // System.out.println("1");
      this_panel.remove(sjp);
      sjp = null;
    }
    else if(sjp == null && !show_hide)
    {
      // System.out.println("2");
      return;
    }
    else if(sjp == null && show_hide)
    {
      // System.out.println("3");
      sjp = new SelectionJPanel(name,Color.RED,1.0f);
      sjp.setOpaque(false);
      this_panel.add(sjp);
      regionName = name;
      sjp.addActionListener(new SelectListener());
      sjp.requestFocus();
      this_panel.editSelection();
    }
    else if(sjp != null && show_hide)
    {
      // System.out.println("4");
      RegionOpEditFrame[] EditorsCopy = new RegionOpEditFrame[Editors.size()];
      Editors.copyInto(EditorsCopy);
      for(int j=0;j<EditorsCopy.length;j++)
        EditorsCopy[j].dispose();
      sjp.closeEditor();
      //Editors.removeAllElements();
      this_panel.remove(sjp);
      sjp = null;
      sjp = new SelectionJPanel(name,Color.RED,1.0f);
      sjp.setOpaque(false);
      this_panel.add(sjp);
      regionName = name;
      sjp.addActionListener(new SelectListener());
      sjp.requestFocus();
      this_panel.editSelection();
    }
  }
  

  public void closeWindows()
  {
    sjp.closeEditor();
    closeEditors();
  }
  

  public void closeEditors()
  {
    RegionOpEditFrame[] EditorsCopy = new RegionOpEditFrame[Editors.size()];
    Editors.copyInto(EditorsCopy);
    for(int j=0;j<EditorsCopy.length;j++)
      EditorsCopy[j].dispose();
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
    sjp.enableSelection(select_names);
  }


  /**
   * Overrides paintComponent method. This method will paint the selected 
   * regions.
   *
   *  @param  g - graphics object
   */
  public void paintComponent(Graphics g) 
  {
    // System.out.println("SelectionOverlay paintComponent()");

    Graphics2D g2d = (Graphics2D) g.create();

    current_bounds = component.getRegionInfo(); // current size of center
    sjp.setBounds(current_bounds);

    // this limits the paint window to the size of the background image.
    g2d.clipRect( current_bounds.x, 
                  current_bounds.y,
                  current_bounds.width, 
                  current_bounds.height );

    // Update the current mapping to pixel coordinates before drawing the
    // region interior OR boundaries, in case it has changed.
    CoordBounds pixel_map = new CoordBounds(
                  current_bounds.x,
                  current_bounds.y,
                  current_bounds.x + current_bounds.width,
                  current_bounds.y + current_bounds.height );
    pixel_local.setSource(pixel_map);
    pixel_local.setDestination(component.getLocalCoordBounds());

    AlphaComposite ac;

    if ( component instanceof IViewComponent2D )  // draw region interior
    {   
      IViewComponent2D ivc = (IViewComponent2D) component;
      CoordTransform world_to_array = ivc.getWorldToArrayTransform();
      CoordTransform array_global = CoordTransform.inverse(world_to_array);

      for( RegionOpListWithColor list:regionOpLists.values() ) 
      {
        Point[] point_array = list.getSelectedPoints(world_to_array);

        g2d.setColor(list.getColor());
        ac = AlphaComposite.getInstance( AlphaComposite.SRC_OVER, 
                                         list.getOpacity() );
        g2d.setComposite(ac);
        paintPointArray(g2d, point_array, array_global);
      }
    }

    //get all the regions and draw outlines 

    floatPoint2D[] fp=null;
    Point[] p=null;
    Region region;
    boolean nullfound = false;

                // Change the opaqueness of the selections              
                //TODO CHANGE HERE
    for( RegionOpListWithColor list:regionOpLists.values() ) {
      g2d.setColor(list.getColor());
      ac = AlphaComposite.getInstance( AlphaComposite.SRC_OVER, 
                                       list.getOpacity() );
      g2d.setComposite(ac);

      for ( RegionOp regionOp:list.getList() ) {
        region = regionOp.getRegion();
        if ( region != null ) {
          fp = region.getDefiningPoints();
          p  = new Point[fp.length];
          for (int i = 0; i < fp.length; i++) {
            if (fp[i] == null){
              nullfound = true;
            }
            else{
              p[i] = convertToPixelPoint(fp[i]);
            }
          }
        }
        else
          nullfound = true; 

      if( !nullfound ){
        if ( region instanceof EllipseRegion ) {
          g2d.drawOval( p[0].x, 
                        p[0].y,
                        p[1].x - p[0].x, 
                        p[1].y - p[0].y);
        } 
        else if ( region instanceof BoxRegion ) {
          g2d.drawRect( p[0].x, 
                        p[0].y, 
                        p[1].x - p[0].x, 
                        p[1].y - p[0].y );
        } 
        else if ( region instanceof LineRegion ) {
          g2d.drawLine( p[0].x, 
                        p[0].y, 
                        p[1].x, 
                        p[1].y );
        } 
        else if ( region instanceof PointRegion ) {
          g2d.drawLine( p[0].x - 5, 
                        p[0].y, 
                        p[0].x + 5,
                        p[0].y );
          g2d.drawLine( p[0].x, 
                        p[0].y - 5, 
                        p[0].x, 
                        p[0].y + 5);
        } 
        else if ( region instanceof DoubleWedgeRegion ) {
          /* p[0]   = center pt of circle that arc is taken from
           * p[1]   = last mouse point/point at intersection of line and arc
           * p[2]   = reflection of p[1]
           * p[3]   = top left corner of bounding box around arc's total circle
           * p[4]   = bottom right corner of bounding box around arc's circle
           * p[5].x = startangle, the directional vector in degrees
           * p[5].y = degrees covered by arc.
           */
           // Since p[5] is not a point, but angular measures, 
           //they are a direct
           // cast from float to int, no convertion needed.
           p[p.length - 1].x = (int) fp[p.length - 1].x;
           p[p.length - 1].y = (int) fp[p.length - 1].y;

           g2d.drawLine( 2 * p[0].x - p[1].x, 
                         2 * p[0].y - p[1].y,
                         p[1].x, 
                         p[1].y );
           g2d.drawLine( 2 * p[0].x - p[2].x, 
                         2 * p[0].y - p[2].y,
                         p[2].x, 
                         p[2].y );

           g2d.drawArc( p[3].x, 
                        p[3].y, 
                        p[4].x - p[3].x, 
                        p[4].y - p[3].y, 
                        p[5].x, 
                        p[5].y );
           g2d.drawArc( p[3].x, 
                        p[3].y, 
                        p[4].x - p[3].x, 
                        p[4].y - p[3].y, 
                        p[5].x + 180, 
                        p[5].y );
        } 
        else if ( region instanceof WedgeRegion ) {
          /* p[0]   = center pt of circle that arc is taken from
           * p[1]   = last mouse point/point at intersection of line and arc
           * p[2]   = reflection of p[1]
           * p[3]   = top left corner of bounding box around arc's total circle
           * p[4]   = bottom right corner of bounding box around arc's circle
           * p[5].x = startangle, the directional vector in degrees
           * p[5].y = degrees covered by arc.
           */
           // Since p[5] is not a point, but angular measures, 
           //they are a direct
           // cast from float to int, no convertion needed.
           p[p.length - 1].x = (int) fp[p.length - 1].x;
           p[p.length - 1].y = (int) fp[p.length - 1].y;

           g2d.drawLine(p[0].x, p[0].y, p[1].x, p[1].y);
           g2d.drawLine(p[0].x, p[0].y, p[2].x, p[2].y);

           g2d.drawArc( p[3].x, 
                        p[3].y, 
                        p[4].x - p[3].x, 
                        p[4].y - p[3].y, 
                        p[5].x, 
                        p[5].y );
        } 
        else if (region instanceof AnnularRegion) {
          /* p[0]   = center pt of circle
           * p[1]   = top left corner of bounding box of inner circle
           * p[2]   = bottom right corner of bounding box of inner circle
           * p[3]   = top left corner of bounding box of outer circle
           * p[4]   = bottom right corner of bounding box of outer circle
           */
           g2d.drawOval( p[1].x, p[1].y, p[2].x - p[1].x, p[2].y - p[1].y );

           g2d.drawOval( p[3].x, p[3].y, p[4].x - p[3].x, p[4].y - p[3].y );
         }
         nullfound = false;
       }
     }
   }
    if(cursor != null)
    {
      g2d.setColor(Color.white);
      g2d.setXORMode( Color.black );
      //g2d.setStroke(new BasicStroke
      //    (2,BasicStroke.CAP_SQUARE,BasicStroke.JOIN_BEVEL));
      //PointCursor defPt = new PointCursor(this_panel);
        if(cursor instanceof BoxPanCursor)
        {
          //((XOR_PanCursor)cursor).init(cursorPoints[0], cursorPoints[1]);
          ((XOR_PanCursor)cursor).draw(g2d,
              convertToPixelPoint(cursorPoints[0]),
              convertToPixelPoint(cursorPoints[1]));
          //defPt.draw(g2d, convertToPixelPoint(cursorPoints[0]),null);
          //defPt.draw(g2d, convertToPixelPoint(cursorPoints[1]),null);
        }
        else if(cursor instanceof EllipseCursor)
        {
          g2d.setStroke(new BasicStroke
              (1,BasicStroke.CAP_SQUARE,BasicStroke.JOIN_BEVEL));
          ((EllipseCursor)cursor).draw(g2d,
              convertToPixelPoint(cursorPoints[2]),
              convertToPixelPoint(cursorPoints[1]));
          //defPt.draw(g2d, convertToPixelPoint(cursorPoints[0]),null);
        }
        else if(cursor instanceof DoubleWedgeCursor)
        {
          ((DoubleWedgeCursor)cursor).draw(g2d,
              convertToPixelPoint(cursorPoints[0]),
              convertToPixelPoint(cursorPoints[2]),
              convertToPixelPoint(cursorPoints[1]));
          //defPt.draw(g2d, convertToPixelPoint(cursorPoints[0]),null);
          //defPt.draw(g2d, convertToPixelPoint(cursorPoints[1]),null);
          //defPt.draw(g2d, convertToPixelPoint(cursorPoints[2]),null);
        }
        else if(cursor instanceof WedgeCursor)
        {
          ((WedgeCursor)cursor).draw(g2d,
              convertToPixelPoint(cursorPoints[0]),
              convertToPixelPoint(cursorPoints[2]),
              convertToPixelPoint(cursorPoints[1]));
          
          //System.out.println("cursorPTs2:");
          //for(int i=0;i<((WedgeCursor)cursor).region().length;i++)
          //  System.out.println(""+((WedgeCursor)cursor).region()[i]);
          
          //defPt.draw(g2d, convertToPixelPoint(cursorPoints[0]),null);
          //defPt.draw(g2d, convertToPixelPoint(cursorPoints[1]),null);
          //defPt.draw(g2d, convertToPixelPoint(cursorPoints[2]),null);
        }
        else if(cursor instanceof AnnularCursor)
        {
          ((AnnularCursor)cursor).draw(g2d, 
              convertToPixelPoint(cursorPoints[0]), 
              convertToPixelPoint(cursorPoints[1]), 
              convertToPixelPoint(cursorPoints[2]));
          
          //defPt.draw(g2d, convertToPixelPoint(cursorPoints[0]),null);
          //defPt.draw(g2d, convertToPixelPoint(cursorPoints[1]),null);
          //defPt.draw(g2d, convertToPixelPoint(cursorPoints[2]),null);
        }
        else if(cursor instanceof PointCursor)
        {
          ((PointCursor)cursor).draw(g2d, 
              convertToPixelPoint(cursorPoints[cursorPoints.length-1]), null);
        }
        else if(cursor instanceof LineCursor)
        {
         g2d.setStroke(new BasicStroke
             (2,BasicStroke.CAP_SQUARE,BasicStroke.JOIN_BEVEL));
          ((LineCursor)cursor).draw(g2d, 
              convertToPixelPoint(cursorPoints[0]),
              convertToPixelPoint(cursorPoints[1]));
          //defPt.draw(g2d, convertToPixelPoint(cursorPoints[0]),null);
          //defPt.draw(g2d, convertToPixelPoint(cursorPoints[1]),null);
        }
    }
    g2d.dispose();
  } // end of paintComponent()


  /**
   *  Map an (x,y) pair from array coordinates to pixel coordinates
   *
   *  @param  x             The x-coordinate (i.e. column number) of a position
   *                        in the array of data values
   *  @param  y             The y-coordinate (i.e. row number) of a position
   *                        in the array of data values
   *  @param  array_global  The tranformation from array (col,row) to the
   *                        world coordinates
   *
   *  @return A point containing the on screen pixel coordinates corresponding
   *          to the specified (x,y) location in the data array. 
   */
  private Point ArrayToPixel( int x, int y, CoordTransform array_global ) {

    floatPoint2D point = new floatPoint2D( x + 0.5f, y + 0.5f );
    point = array_global.MapTo( point );
    point = pixel_local.MapFrom( point );
    return new Point( Math.round( point.x ), Math.round( point.y ) );
  }


  /** 
   * Draw scan lines through the region determined by the array of points.
   * Assumptions: 
   *   The Point[] is completely filled w/ no gaps.  
   *   The Point[] has points in order from left to right starting from 
   *   top row to bottom(like reading).
   *
   * NOTE: This method will only be used for selection from the
   *       ImageViewComponent.  In that case the array of points will contain
   *       Row,col values for array elements of the array be displayed as an
   *       image.  This method will convert the row,col coordinates to 
   *       pixel coordinates before drawing.  That conversion is not done
   *       yet.
   *
   *  @param   g            The Graphics2D object to draw on
   *  @param   p            The array of points to draw scan lines through
   *  @param   array_global The current array to world coordinate transform
   */
  private void paintPointArray( Graphics2D g, 
                                Point[]    p,
                                CoordTransform array_global ) {
    boolean shouldPaint = false;
    int x_initial = -1; // Initial x for draw line command
    int y_initial = -1; // Initial y for draw line command
    int x_final = -1; // Final x for draw line command
    int y_final = -1; // Final y for draw line command
    int lineThickness = 1;

    //----------------------------------Loop through all points
    for (int i = 0; i < p.length; i++) {
      //--------------------------for all points other then last
      if (i != p.length - 1) {
        //-------------------------------to find the initial x & y
        if (x_initial == -1) {
          x_initial = p[i].x;
          y_initial = p[i].y;
        }

        //----------------------------------------------to find the final x & y
        if (!(p[i + 1].x - p[i].x <= 1) || p[i].y != p[i + 1].y) {
          x_final = p[i].x;
          y_final = p[i].y;
          shouldPaint = true;
        }
      } else {
        x_final = p[i].x;
        y_final = p[i].y;

        if (y_final != y_initial) {
          x_initial = p[i].x;
          y_initial = p[i].y;
        }

        shouldPaint = true;
      }

      if (shouldPaint) {
        // first map from array coords to pixel coords
        Point p1 = ArrayToPixel(x_initial, y_initial, array_global);
        Point p2 = ArrayToPixel(x_final, y_final, array_global);
        
        //(p1.x - p2.x)/(x_initial - x_final);
        Point pt1 = ArrayToPixel(1, 1, array_global);
        Point pt2 = ArrayToPixel(2, 2, array_global);
        lineThickness = (int)Math.abs(Math.round
              (.4*((pt1.x - pt2.x))));

        g.setStroke(new BasicStroke
            (lineThickness,BasicStroke.CAP_SQUARE,BasicStroke.JOIN_BEVEL));
        g.drawLine(p1.x, p1.y, p2.x, p2.y);

        x_initial = -1;
        y_initial = -1;
        shouldPaint = false;
      }
    }
  }


  /*
   * Converts from world coordinates to a pixel point on the display 
   * using the current local transformation between a subregion
   * and the full display area.
   *
   * @param fp    floatPoint2D object containing world coordinates
   * 
   * @return A Point object 
   */
  public Point convertToPixelPoint( floatPoint2D fp ) {
    floatPoint2D fp2d = pixel_local.MapFrom(fp);
    return new Point( (int) fp2d.x, (int) fp2d.y );
  }


  /*
   * Converts from pixel coordinates to world coordinates, using the 
   * current local transformation between a subregion and the full
   * display area.
   * 
   * @param  p    Point containing the pixel coordinates of a point on the
   *              display
   *
   * @return a floatPoint2D object containg the world coodinates of the
   *           specified pixel. 
   */
  public floatPoint2D convertToWorldPoint( Point p ) {
    return pixel_local.MapTo(new floatPoint2D((float) p.x, (float) p.y));
  }


  /*
   * SelectListener listens for messages being passed from the SelectionJPanel.
   */
  private class SelectListener implements ActionListener {

    public void actionPerformed(ActionEvent ae) {

      String message = ae.getActionCommand();
                        
      SelectionJPanel named_sjp = (SelectionJPanel)ae.getSource();
      String name = named_sjp.getName();
                        
      // clear all selections from the vector
      if (message.equals(SelectionJPanel.RESET_SELECTED)) {

        closeEditors();
        
        getRegionOpListWithColor(name).removeAll();
        send_message(ALL_REGIONS_REMOVED);
        //for( int i=0; i<Editors.size();i++)
        //{
          //System.out.println("Disposing editor "+i);
          //Editors.get(i).dispose();
        //}
        //Editors.clear();
        this_panel.repaint();
      }

      // remove the last selection from the vector
      else if (message.equals(SelectionJPanel.RESET_LAST_SELECTED) ) {

        closeEditors();
        
        RegionOpListWithColor list = getRegionOpListWithColor(name);
        list.removeLast();        
        send_message(REGION_REMOVED);
        //for( int i=0; i<Editors.size();i++)
       // {
         // System.out.println("Disposing editor "+i);
         // Editors.get(i).dispose();
       // }
       // Editors.clear();
        this_panel.repaint();
      }

      else if ( message.equals(SelectionJPanel.COLOR_CHANGED) )
      { 
        RegionOpListWithColor list = getRegionOpListWithColor(name);
        list.setColor( named_sjp.getColor() );
        this_panel.repaint();
      }
      
      else if ( message.equals(SelectionJPanel.OPACITY_CHANGED) )
      { 
        RegionOpListWithColor list = getRegionOpListWithColor(name);
        list.setOpacity( named_sjp.getOpacity() );
        this_panel.repaint();
      }
      
      else if ( message.equals(SelectionJPanel.COMPLEMENT_CURRENT_SELECTION) ){

        getRegionOpListWithColor(name).add(
                            new RegionOp(null,RegionOp.Operation.COMPLEMENT) );
        this_panel.repaint();
      }

      // region is specified by REGION_SELECTED>BOX >ELLIPSE >POINT   
      // if REGION_SELECTED is in the string, find which region 

      else if (message.indexOf(SelectionJPanel.REGION_SELECTED) > -1) {

//      System.out.println("IJP bounds      = " + component.getRegionInfo() ); 
//      System.out.println("current_ bounds = " + current_bounds ); 

        CoordBounds sjp_coords = new CoordBounds( 0, 
                                                  0,
                                                  current_bounds.width,
                                                  current_bounds.height );
        CoordTransform pixel_sjp = 
               new CoordTransform(sjp_coords, component.getLocalCoordBounds());

        operation = named_sjp.getOp();
        boolean regionadded = true;
        
        if (message.indexOf(SelectionJPanel.BOX) > -1) {
          Rectangle box = ((BoxCursor) sjp
              .getCursor(SelectionJPanel.BOX)).region();
          Point p1 = new Point(box.getLocation());
          Point p2 = new Point(p1);
          p2.x += (int) box.getWidth();
          p2.y += (int) box.getHeight();
          
          BoxPanCursor boxCur = new BoxPanCursor(this_panel);
          boxCur.init(p1,p2);
          Region.getInstanceRegion( boxCur,pixel_sjp );
          getRegionOpListWithColor(name).add(
              new RegionOp(Region.getInstanceRegion( boxCur,pixel_sjp ),
                           operation) );
        } 

        else if ( message.indexOf(SelectionJPanel.ELLIPSE) > -1 ) {

          Ellipse ellipse = ((EllipseCursor) sjp
                                .getCursor(SelectionJPanel.ELLIPSE)).region();
          // top-left corner
          Point p1 = new Point(ellipse.getDrawPoint());
          p1.x += current_bounds.x;
          p1.y += current_bounds.y;
          // bottom-right corner
          Point p2 = new Point(ellipse.getCenter());
          p2.x += ellipse.getDx() + current_bounds.x;
          p2.y += ellipse.getDy() + current_bounds.y;
          // center of circle
          Point p3 = new Point(ellipse.getCenter());
          //EllipseCursor ellipseCur = new EllipseCursor(this_panel);
          //ellipseCur.start(p1);
          //ellipseCur.stop(p2);
          p3.x += current_bounds.x;
          p3.y += current_bounds.y;
          floatPoint2D[] tempwcp = new floatPoint2D[3];
          tempwcp[0] = convertToWorldPoint(p1);
          tempwcp[1] = convertToWorldPoint(p2);
          tempwcp[2] = convertToWorldPoint(p3);
          getRegionOpListWithColor(name).add(
                           new RegionOp(new EllipseRegion(tempwcp),operation));
        } 

        else if (message.indexOf(SelectionJPanel.CIRCLE) > -1) {

          Circle circle = ((CircleCursor) sjp
                                  .getCursor(SelectionJPanel.CIRCLE)).region();

          // top-left corner
          Point p1 = new Point(circle.getDrawPoint());
          p1.x += current_bounds.x;
          p1.y += current_bounds.y;

          // bottom-right corner
          Point p2 = new Point(circle.getCenter());
          p2.x += circle.getRadius() + current_bounds.x;
          p2.y += circle.getRadius() + current_bounds.y;

          // center of circle
          Point p3 = new Point(circle.getCenter());
          p3.x += current_bounds.x;
          p3.y += current_bounds.y;
          floatPoint2D[] tempwcp = new floatPoint2D[3];
          tempwcp[0] = convertToWorldPoint(p1);
          tempwcp[1] = convertToWorldPoint(p2);
          tempwcp[2] = convertToWorldPoint(p3);
          getRegionOpListWithColor(name).add(
                           new RegionOp(new EllipseRegion(tempwcp),operation));
        } 

        else if (message.indexOf(SelectionJPanel.LINE) > -1) {

          Line line = ((LineCursor) sjp
                            .getCursor(SelectionJPanel.LINE)).region();
          Point p1 = new Point(line.getP1());
          p1.x += current_bounds.x;
          p1.y += current_bounds.y;
          Point p2 = new Point(line.getP2());
          p2.x += current_bounds.x;
          p2.y += current_bounds.y;
          floatPoint2D[] tempwcp = new floatPoint2D[2];
          tempwcp[0] = convertToWorldPoint(p1);
          tempwcp[1] = convertToWorldPoint(p2);
          getRegionOpListWithColor(name).add(
                             new RegionOp(new LineRegion(tempwcp),operation));
        } 

        else if (message.indexOf(SelectionJPanel.POINT) > -1) {

          // create new point, otherwise regions would be shared.
          Point np = new Point(((PointCursor) sjp
                                 .getCursor(SelectionJPanel.POINT)).region());
          np.x += current_bounds.x;
          np.y += current_bounds.y;
          floatPoint2D[] tempwcp = new floatPoint2D[1];
          tempwcp[0] = convertToWorldPoint(np);
          getRegionOpListWithColor(name).add(
                            new RegionOp(new PointRegion(tempwcp),operation));
        } 

        else if ( message.indexOf(SelectionJPanel.WEDGE) > -1  &&
                  message.indexOf(SelectionJPanel.DOUBLE_WEDGE) == -1) {

          // create new point, otherwise regions would be shared.
          Point[] p_array = (((WedgeCursor) sjp
              .get3ptCursor(SelectionJPanel.WEDGE)).region());
          floatPoint2D[] tempwcp = new floatPoint2D[p_array.length];

          for (int i = 0; i < p_array.length - 1; i++) {
            p_array[i].x += current_bounds.x;
            p_array[i].y += current_bounds.y;
            tempwcp[i] = convertToWorldPoint(p_array[i]);
          }

          // Since these are angles, they do not need transforming
          if (p_array.length > 0) {
            tempwcp[p_array.length - 1] = new floatPoint2D(
                (float) p_array[p_array.length - 1].x,
                (float) p_array[p_array.length - 1].y);
          }

          getRegionOpListWithColor(name).add(
                            new RegionOp(new WedgeRegion(tempwcp),operation));
        } 

        else if ( message.indexOf(SelectionJPanel.DOUBLE_WEDGE) > -1 ) {

          // create new point, otherwise regions would be shared.
          Point[] p_array = (((DoubleWedgeCursor) sjp
              .get3ptCursor(SelectionJPanel.DOUBLE_WEDGE))
              .region());
          floatPoint2D[] tempwcp = new floatPoint2D[p_array.length];
          for (int i = 0; i < p_array.length - 1; i++) {
            p_array[i].x += current_bounds.x;
            p_array[i].y += current_bounds.y;
            tempwcp[i] = convertToWorldPoint(p_array[i]);
          }

          // Since these are angles, they do not need transforming
          if (p_array.length > 0) {
            tempwcp[p_array.length - 1] = new floatPoint2D(
                (float) p_array[p_array.length - 1].x,
                (float) p_array[p_array.length - 1].y);
          }

          getRegionOpListWithColor(name).add(
                       new RegionOp(new DoubleWedgeRegion(tempwcp),operation));
        } 

        else if (message.indexOf(SelectionJPanel.RING) > -1) {

          // create new point, otherwise regions would be shared.
          Point[] p_array = (((AnnularCursor) sjp
                               .get3ptCursor(SelectionJPanel.RING)).region());
          // center of ring
          Point p1 = new Point(p_array[0]);
          p1.x += current_bounds.x;
          p1.y += current_bounds.y;

          // inner top-left corner
          Point p2 = new Point(p1);
          p2.x -= p_array[1].x;
          p2.y -= p_array[1].x;
          // inner bottom-right corner
          Point p3 = new Point(p1);
          p3.x += p_array[1].x;
          p3.y += p_array[1].x;

          // outer top-left corner
          Point p4 = new Point(p1);
          p4.x -= p_array[1].y;
          p4.y -= p_array[1].y;
          // outer bottom-right corner
          Point p5 = new Point(p1);
          p5.x += p_array[1].y;
          p5.y += p_array[1].y;

          floatPoint2D[] tempwcp = new floatPoint2D[5];
          tempwcp[0] = convertToWorldPoint(p1);
          tempwcp[1] = convertToWorldPoint(p2);
          tempwcp[2] = convertToWorldPoint(p3);
          tempwcp[3] = convertToWorldPoint(p4);
          tempwcp[4] = convertToWorldPoint(p5);
          getRegionOpListWithColor(name).add(
                          new RegionOp(new AnnularRegion(tempwcp),operation));
        } 

        else
          // no recognized region was added
          regionadded = false;

        if (regionadded)
        {
          send_message(REGION_ADDED);
          this_panel.repaint();
        }
      }
      //Oakgrove
      else if(message.equals(SelectionJPanel.CLICK))
      {
        //System.out.println("cliccckkkkk!!!");//discover America, Green Eyes
        //is the point on the interior of a region?
        //if no editors are up
        if(Editors.size()==0)
        {
      Point clickPoint = sjp.getClickPoint();
      clickPoint.x += current_bounds.x;
      clickPoint.y += current_bounds.y;
      floatPoint2D fP2D = convertToWorldPoint(clickPoint);
      Vector<RegionOp> regionOps = 
        getRegionOpListWithColor(regionName).getList();
      if(regionOps.size()>0)
      {
        //System.out.println("made it here");
        RegionOp regOp;
        Region reg;
        //Fix this
        for ( int i=0; i<regionOps.size(); i++ )
        {
      regOp = regionOps.get(i);
      reg = regOp.getRegion();
      if(reg instanceof RegionWithInterior)
      {
        if(((RegionWithInterior)reg).isInsideWC(fP2D.x, fP2D.y))
        {
      if(reg instanceof EllipseRegion)
      {
        //System.out.println("pop up Ellipse edit window");
        floatPoint2D[] ellipsePoints = reg.getDefiningPoints();
        EllipseRegionOpEditFrame ellipseEdit = new EllipseRegionOpEditFrame(
            ellipsePoints[0],ellipsePoints[2],regOp.getOp(),i);
        ellipseEdit.addPropertyChangeListener(
              new RegionEditorPropertyListener());
        ellipseEdit.setVisible(true);
        cursor = new EllipseCursor(this_panel);
        cursorPoints = ellipseEdit.getDefiningPoints();
        Editors.add(ellipseEdit);
      }
      
      else if(reg instanceof DoubleWedgeRegion)
      {
        //System.out.println("pop up DWedge Edit");
        floatPoint2D[] dWedgePoints = reg.getDefiningPoints();
        
        DoubleWedgeRegionOpEditFrame dWedgeEdit = 
          new DoubleWedgeRegionOpEditFrame(dWedgePoints,
              regOp.getOp(),i);
        dWedgeEdit.addPropertyChangeListener(
            new RegionEditorPropertyListener());
       dWedgeEdit.setVisible(true);
        cursor = new DoubleWedgeCursor(this_panel);
        cursorPoints = dWedgeEdit.getDefiningPoints();
        Editors.add(dWedgeEdit);
      }
        
      else if(reg instanceof WedgeRegion)
      {
        //System.out.println("pop up Wedge edit window");
        floatPoint2D[] wedgePoints = reg.getDefiningPoints();
        WedgeRegionOpEditFrame wedgeEdit = new WedgeRegionOpEditFrame(
            wedgePoints,regOp.getOp(),i);
        wedgeEdit.addPropertyChangeListener(
            new RegionEditorPropertyListener());
        wedgeEdit.setVisible(true);
        cursor = new WedgeCursor(this_panel);
        cursorPoints = wedgeEdit.getDefiningPoints();
        Editors.add(wedgeEdit);
      }
        
      else if(reg instanceof BoxRegion)
      {
        //System.out.println("pop up Box edit window");
        floatPoint2D[] boxPoints = reg.getDefiningPoints();
        BoxRegionOpEditFrame boxEdit = new BoxRegionOpEditFrame(
          boxPoints[0],boxPoints[1],regOp.getOp(),i);
        //System.out.println("region Index "+i);
        boxEdit.addPropertyChangeListener(new RegionEditorPropertyListener());
        boxEdit.setVisible(true);
        boxEdit.firePropertyChange( RegionOpEditFrame.DRAW_CURSOR,0,0 );
        cursor = new BoxPanCursor(this_panel);
        cursorPoints = boxEdit.getDefiningPoints();
        Editors.add(boxEdit);
      }
      
      else if(reg instanceof AnnularRegion)
      {
        floatPoint2D[] ringPoints = reg.getDefiningPoints();
        floatPoint2D radPoint1 = new floatPoint2D(ringPoints[0].x,
                                                  ringPoints[1].y);
        floatPoint2D radPoint2 = new floatPoint2D(ringPoints[0].x,
                                                  ringPoints[3].y);
        AnnularRegionOpEditFrame ringEdit = new AnnularRegionOpEditFrame(
            ringPoints[0],radPoint1,radPoint2,regOp.getOp(),i);
        ringEdit.addPropertyChangeListener(new RegionEditorPropertyListener());
        ringEdit.setVisible(true);
        cursor = new AnnularCursor(this_panel);
        cursorPoints = ringEdit.getDefiningPoints();
        Editors.add(ringEdit);
      }
        
        }
      }
      else if(reg instanceof PointRegion)
      {
        floatPoint2D[] points = reg.getDefiningPoints();
        for(int j =0;j<points.length;j++)
        {
      if( convertToPixelPoint(points[j]).y<clickPoint.y+5 &&
          convertToPixelPoint(points[j]).y>clickPoint.y-5 &&
          convertToPixelPoint(points[j]).x<clickPoint.x+5 &&
          convertToPixelPoint(points[j]).x>clickPoint.x-5)
      {
        PointRegionOpEditFrame pointEdit = new PointRegionOpEditFrame
                                            (points[j],sjp.getOp(),i,j);
        pointEdit.addPropertyChangeListener(
            new RegionEditorPropertyListener());
        pointEdit.setVisible(true);
        cursor = new PointCursor(this_panel);
        Point index= new Point(j,0);
        ((PointCursor)cursor).start(index);
        cursorPoints = new floatPoint2D[reg.getDefiningPoints().length+1];
        for(int k=0;k<reg.getDefiningPoints().length;k++)
        {
          cursorPoints[k]=reg.getDefiningPoints()[k];
        }
        cursorPoints[reg.getDefiningPoints().length]=pointEdit
            .getDefiningPoints()[0];
        Editors.add(pointEdit);
      }
        }
      }
      
      else if(reg instanceof LineRegion)
      {
        //System.out.println("figure something out");
        floatPoint2D[] points = reg.getDefiningPoints();
        if(fP2D.x<Math.max(points[0].x, points[1].x)+10&&
            fP2D.x>Math.min(points[0].x, points[1].x)-10&& 
            fP2D.y<Math.max(points[0].y, points[1].y)+10&&
            fP2D.y>Math.min(points[0].y, points[1].y)-10)
        {
      LineRegionOpEditFrame lineEdit = new LineRegionOpEditFrame(
          points[0],points[1],sjp.getOp(),i);
      lineEdit.addPropertyChangeListener(new RegionEditorPropertyListener());
      lineEdit.setVisible(true);
      cursor = new LineCursor(this_panel);
      cursorPoints = lineEdit.getDefiningPoints();
      Editors.add(lineEdit);
        }
      }
        }
      }
        }
      }
 
    } // end actionPerformed()   

  } // end SelectListener 
  

  private class RegionEditorPropertyListener implements PropertyChangeListener
  {

    public void propertyChange(PropertyChangeEvent e)
    {

      if (e.getPropertyName().equals(RegionOpEditFrame.DRAW_CURSOR))
      {
        //System.out.println("getting here!!!1");

        if(e.getSource() instanceof BoxRegionOpEditFrame)
        {
          cursor = new BoxPanCursor(this_panel);
          cursorPoints = ((BoxRegionOpEditFrame)e.getSource())
              .getDefiningPoints();
        }

        else if(e.getSource() instanceof EllipseRegionOpEditFrame)
        {
          cursor = new EllipseCursor(this_panel);
          cursorPoints = ((EllipseRegionOpEditFrame)e.getSource())
              .getDefiningPoints();
        }
        
        else if(e.getSource() instanceof AnnularRegionOpEditFrame)
        {
          cursor = new AnnularCursor(this_panel);
          cursorPoints = ((AnnularRegionOpEditFrame)e.getSource())
              .getDefiningPoints();
        }
        
        else if(e.getSource() instanceof DoubleWedgeRegionOpEditFrame)
        {
          cursor = new DoubleWedgeCursor(this_panel);
          cursorPoints = ((DoubleWedgeRegionOpEditFrame)e.getSource())
              .getDefiningPoints();
        }
         
        else if(e.getSource() instanceof WedgeRegionOpEditFrame)
        {
          cursor = new WedgeCursor(this_panel);
          cursorPoints = ((WedgeRegionOpEditFrame)e.getSource())
              .getDefiningPoints();
        }
        
        //System.out.println("getting here!!!");
        else if(e.getSource() instanceof PointRegionOpEditFrame)
        {
          //System.out.println("getting here!!!");
          cursor = new PointCursor(this_panel);
          Point index = new Point(((PointRegionOpEditFrame)e.getSource())
                  .getPointIndex(),0);
          ((PointCursor)cursor).start(index);
          floatPoint2D[] regPts = getRegionOpListWithColor(sjp.getName())
              .getList().get(((PointRegionOpEditFrame)e.getSource())
              .getRegionIndex()).getRegion().getDefiningPoints();
          cursorPoints= new floatPoint2D[regPts.length+1];
          for (int i=0; i<regPts.length;i++)
            cursorPoints[i] = regPts[i];
          cursorPoints[regPts.length]=((PointRegionOpEditFrame)e.getSource())
                                            .getDefiningPoints()[0];
        }
        else if(e.getSource() instanceof LineRegionOpEditFrame)
        {
          cursor = new LineCursor(this_panel);
          cursorPoints = ((LineRegionOpEditFrame)e.getSource())
              .getDefiningPoints();
        }

       // System.out.println("cursorPTs:");
        //for(int i=0;i<cursorPoints.length;i++)
          //System.out.println(""+cursorPoints[i]);
        this_panel.repaint();
        
      }
      
      if (e.getPropertyName().equals(RegionOpEditFrame.DONE))
      {
                
       // ((RegionOpEditFrame)e.getSource()).dispose();
        RegionOpListWithColor regListWC = regionOpLists.get(sjp.getName());
        Vector<RegionOp> regOpList = regListWC.getList();
        regOpList.setElementAt(
            new RegionOp(
                Region.getInstanceRegion(cursor,cursorPoints),
                ((RegionOpEditFrame)e.getSource()).getOp()),
            ((RegionOpEditFrame)e.getSource()).getRegionIndex());
        ((RegionOpEditFrame)e.getSource()).dispose();
        Editors.removeElement(e.getSource());
        cursor = null;
        sjp.repaint();
      }
      
      if (e.getPropertyName().equals(RegionOpEditFrame.CANCEL))
      {
        Editors.removeElement(e.getSource());
        cursor= null;
        sjp.repaint();
      }
      
      if (e.getPropertyName().equals(RegionOpEditFrame.DRAW_REGION))
      {
        RegionOpListWithColor regListWC = regionOpLists.get(sjp.getName());
        //System.out.println(""+regListWC.getList().get(0));
        Vector<RegionOp> regOpList = regListWC.getList();
        //if (cursor instanceof XOR_Cursor)
          //System.out.println("Ok the cursor is an XOR_Cursor");
        //if (cursor instanceof PointCursor)
          //System.out.println("and it is a PointCursor");
        
       // System.out.println("region.instance call");
       // for(int i=0;i<((PointCursor)cursor).region().length;i++)
          //System.out.println(""+((PointCursor)cursor).region());
        
        regOpList.setElementAt(
            new RegionOp(
                Region.getInstanceRegion(cursor,cursorPoints),
                ((RegionOpEditFrame)e.getSource()).getOp()),
            ((RegionOpEditFrame)e.getSource()).getRegionIndex());
        
        //for (int i=0;i<cursorPoints.length;i++)
          //System.out.println("cursor pt["+i+"] = "+cursorPoints[i]);
        
        sjp.repaint();
      }
      
    }
    
  }
}
