/*
 * File: ImageViewComponent.java
 *
 * Copyright (C) 2003-2005, Mike Miller
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
 *  Revision 1.100  2007/07/16 14:50:26  rmikk
 *  Removed the color model and two sided status object state entries from
 *     the ImageJPanel2 and put them in the ImageViewComponent
 *
 *  Revision 1.99  2007/07/12 16:49:28  oakgrovej
 *  Added closeWindows() method
 *
 *  Revision 1.98  2007/06/15 22:53:05  oakgrovej
 *  Added controls for making and selecting new selectors
 *
 *  Revision 1.97  2007/06/15 16:57:16  dennis
 *  Commented out some extra calls to validate().
 *
 *  Revision 1.96  2007/06/13 15:29:32  rmikk
 *  The PanViewController is now notified when the data is changed
 *
 *  Revision 1.95  2007/05/28 20:47:11  dennis
 *  Added method showSelector that will show (or hide) an editor panel for
 *  a specified compound region selection. (Jonathan Morck)
 *  Added java docs for this method. (dennis)
 *
 *  Revision 1.94  2007/03/16 18:45:43  dennis
 *  Added method getWorldToArrayTransform, needed to use the new Region
 *  classes.
 *  Removed setRegionTransform() method that is no longer needed.
 *
 *  Revision 1.93  2007/03/11 04:35:46  dennis
 *  Added method setRegionTransforms(), that is used by the
 *  SelectionOverlay, to set up the appropriate mapping between
 *  world coordinates and array coordinates.
 *
 *  Revision 1.92  2007/03/09 18:58:59  dennis
 *  The dataChanged(array) now sets the data for the underlying
 *  ImageJPanel, so that the coordinate mappings are initialized
 *  before the overlays are created.  This fixes a bug where the
 *  getColumnRowAtWorldCoordinate() did not work properly, when
 *  needed by the SelectionOverlay class.
 *
 *  Revision 1.91  2007/02/05 04:33:12  dennis
 *  Removed small adjustment by 0.001 to World Coordinate bounds, which
 *  was not necessary and caused problems with selections containing
 *  the 0th row or column.
 *
 *  Revision 1.90  2006/03/30 23:57:57  dennis
 *  Modified to not require the use of mutator methods for the
 *  virtual arrays.  These changes were required since the concept
 *  of a "mutable" virtual array was separated from the concept of
 *  a virtual array.
 *
 *  Revision 1.89  2005/06/04 23:11:24  rmikk
 *  Fixed it so that when data is changed with a virtual array argument
 *    everything is reinitialized always.  My virtual arrays may indicate a
 *    drastic change in axes, labels, etc. but the virtual array is the same.
 *
 *  Revision 1.88  2005/06/02 22:31:21  dennis
 *  Modified to only use IVirtualArray2D methods after creating a
 *  VirtualArray2D object.
 *
 *  Revision 1.87  2005/05/25 20:28:35  dennis
 *  Now calls convenience method WindowShower.show() to show
 *  the window, instead of instantiating a WindowShower object
 *  and adding it to the event queue.
 *
 *  Revision 1.86  2005/03/21 00:39:38  millermi
 *  - setPointedAt() now calls set_crosshair_WC() so the crosshairs
 *    are displayed when a new pointed at is set.
 *  - setPointedAt() ignores request if current point is already set
 *    to the desired point.
 *  - POINTED_AT_CHANGED message is no longer sent out while zooming in
 *    occurs.
 *  - TableRegions are now ignored by addSelectedRegion() and
 *    setSelectedRegions().
 *
 *  Revision 1.85  2005/03/17 02:08:37  millermi
 *  - Added checkbox menu item for toggling aspect ratio of the image.
 *  - Added additional javadocs to getControls() that tell user what
 *    controls are associated with a given index.
 *
 *  Revision 1.84  2005/03/09 22:29:47  millermi
 *  - Moved much of the constructor information to dataChanged(va2D).
 *  - Added call to buildViewComponent() in dataChanged() if either
 *    calibrated colorscale is displayed.
 *  - Removed big_picture.update(g) from paintComponents(g) thus no longer
 *    requiring a Graphics object to be passed into paintComponents().
 *  - Now written with ImageJPanel2, which allows for any size 2D array.
 *
 *  Revision 1.83  2005/02/03 22:09:38  millermi
 *  - Added sendMessage(COLORSCALE_CHANGED) when slider moves. This
 *    will update any calibrated controlcolorscales. Removing
 *    miscellaneous messages being sent from the ImageViewComponent
 *    caused this bug.
 *
 *  Revision 1.82  2005/01/20 23:33:52  millermi
 *  - Commented out big_picture.update() in paintComponents(). No
 *    longer needed when super.paint() was added to overlay
 *    paint methods.
 *  - Cleaned up messages sent from the ImageViewComponent.
 *    No longer sends messages to listeners that are not defined
 *    at the ViewComponent level.
 *
 *  Revision 1.81  2005/01/19 21:00:20  millermi
 *  - Removed sendMessage(SELECTED_CHANGED) from addSelectedRegion()
 *    and setSelectedRegion() since the SelectedRegionListener
 *    now sends out the message.
 *
 *  Revision 1.80  2005/01/18 23:01:04  millermi
 *  - Changed setImageBounds in getSelectedRegions() to parallel the
 *    ImageJPanel transformation.
 *  - Replaced variables image_bounds and global_bounds with calls to
 *    getLocalCoordBounds() and getGlobalCoordBounds().
 *  - SelectedRegionListener no longer passes SelectionOverlay events. Now
 *    sends out SELECTED_CHANGED when a SelectionOverlay event occurs.
 *
 *  Revision 1.79  2004/12/05 05:43:08  millermi
 *  - Fixed Eclipse warning.
 *
 *  Revision 1.78  2004/11/12 03:36:20  millermi
 *  - Since the min/max of getAxisInformation() are no longer used
 *    to determine the zoom region by the AxisOverlay2D, the AxisInfo
 *    returned by getAxisInformation() now reflects global coordinates
 *    instead of local coordinates.
 *
 *  Revision 1.77  2004/11/05 22:04:47  millermi
 *  - Updated java docs for getAxisInformation().
 *
 *  Revision 1.76  2004/09/15 21:55:48  millermi
 *  - Updated LINEAR, TRU_LOG, and PSEUDO_LOG setting for AxisInfo class.
 *    Adding a second log required the boolean parameter to be changed
 *    to an int. These changes may affect any ObjectState saved configurations
 *    made prior to this version.
 *
 *  Revision 1.75  2004/08/13 02:50:00  millermi
 *  - Now implements IMarkerAddible interface, requiring the methods
 *    removeMarker() and removeAllMarkers() to be added.
 *
 *  Revision 1.74  2004/08/06 18:49:50  millermi
 *  - Added setColorScale(), now called when menu items are used to change
 *    the colorscale.
 *  - IColorScaleAddible.COLORSCALE_CHANGED is now sent out when the
 *    colorscale is changed.
 *
 *  Revision 1.73  2004/08/04 18:55:16  millermi
 *  - Added java doc comments clarifying coordinate systems.
 *  - Added getColumnRowAtPixel(), getColumnRowAtWorldCoords(),
 *    getWorldCoordsAtPixel(), and getWorldCoordsAtColumnRow()
 *    to convert between coordinate systems.
 *  - Added check for null data being passed in. Methods will
 *    now either ignore requests or return dummy values if the
 *    data was null. Controls will also be disabled is data
 *    was null.
 *
 *  Revision 1.72  2004/07/10 04:50:25  millermi
 *  - Added setPrecision() method to allow outside programmers to
 *    set the precision of the displayed numbers.
 *
 *  Revision 1.71  2004/04/29 06:27:05  millermi
 *  - Added MarkerOverlay help to list of menu items.
 *
 *  Revision 1.70  2004/04/12 03:40:38  millermi
 *  - Bug fix: Changed bounds of the setImageBounds() in
 *    getSelectedRegions() method. Previously, the max bounds
 *    were one to large (num. rows and num columns).
 *
 *  Revision 1.69  2004/04/07 01:23:41  millermi
 *  - Added marker overlay, marker control, and static variables for
 *    the control name and ObjectState keys.
 *
 *  Revision 1.68  2004/04/05 03:07:41  millermi
 *  - BUG FIX: Fixed problem where selected regions did not
 *    appear in the correct spot for non-square data. The
 *    fix required the getNumColumns() and getNumRows() to
 *    be swapped in the getSelectedRegions() method.
 *
 *  Revision 1.67  2004/03/30 03:25:03  millermi
 *  - Added a minimum height and width to the preserveAspectImage().
 *    This will prevent the image from "disappearing" after continuous
 *    zooming.
 *  - KNOWN BUG: PanViewControl zoom window no longer matches the
 *    ImageJPanel zoomed region.
 *
 *  Revision 1.66  2004/03/17 20:26:50  dennis
 *  Fixed @see tag that was broken when view components, math and
 *  util were moved to gov package.
 *
 *  Revision 1.65  2004/03/15 23:53:53  dennis
 *  Removed unused imports, after factoring out the View components,
 *  Math and other utils.
 *
 *  Revision 1.64  2004/03/12 15:13:57  millermi
 *  - Shifted controls to put CursorReadout third and to
 *    keep PanViewControl last.
 *  - Added check in buildAspectImage() to make sure background
 *    panel has at least 5 children.
 *
 *  Revision 1.63  2004/03/12 02:46:03  rmikk
 *  Fixed some of the package names
 *
 *  Revision 1.62  2004/03/10 23:37:28  millermi
 *  - Changed IViewComponent interface, no longer
 *    distinguish between private and shared controls/
 *    menu items.
 *  - Combined private and shared controls/menu items.
 *
 *  Revision 1.61  2004/03/10 17:38:49  millermi
 *  - Added cursor readout for the current pointed-at position
 *    in x,y world coordinates. The control was added to the
 *    end of the control list.
 *
 *  Revision 1.60  2004/03/10 16:17:09  millermi
 *  - Made returnFocus() a public method.
 *  - Added getDisplayPanel().requestFocus() to end of
 *    returnFocus() if no overlays are visible.
 *
 *  Revision 1.59  2004/03/09 16:30:36  millermi
 *  - Added title to PanViewControl
 *  - Made all titles of controls static Strings to allow easy
 *    comparison when a list of ViewControls is gotten.
 *  - In buildAspectImage(), use paintComponents() to update
 *    overlays instead of just repaint on each overlay. This
 *    should fix a bug where the axes were not being updated
 *    properly.
 *
 *  Revision 1.58  2004/02/27 23:57:02  millermi
 *  - Replaced variable MAXDATASIZE with getNumRows() or
 *    getNumColumns() so future problems are not encountered
 *    if the array is larger than MAXDATASIZE.
 *  - BUG FIX: Fixed bug found by Dennis where image was not
 *    being updated properly. Now the new local and global
 *    bounds are set in the PanViewControl in dataChanged().
 *  - For dataChanged(new_array), if new_array is not an instanceof
 *    VirtualArray2D, a new array is now created. Previously, the
 *    existing VirtualArray2D instance was used.
 *
 *  Revision 1.57  2004/02/19 23:24:54  millermi
 *  - Added preserveAspectRatio() to ImageViewComponent,
 *    the image dimensions are now similar to that of the selected
 *    region.
 *  - Fixed SELECTED_CHANGED message problems with ImageViewComponent.
 *
 *  Revision 1.56  2004/02/14 03:38:19  millermi
 *  - renamed addSelection() to addSelectedRegion()
 *  - converted all WCRegion code to use the Region class instead.
 *  - removed selectedregions from private data, IVC objectstate no longer
 *    stores the selected regions, this is done by the selection overlay.
 *
 *  Revision 1.55  2004/02/06 23:46:07  millermi
 *  - Updated ObjectState and reInit() so shared values
 *    maintain consistency.
 *  - KNOWN BUG: If default state is saved with AxisOverlay
 *    control unchecked, the border does not disappear like it is
 *    supposed to.
 *
 *  Revision 1.54  2004/02/03 21:44:48  millermi
 *  - Updated javadocs
 *
 *  Revision 1.53  2004/01/30 22:16:12  millermi
 *  - Changed references of messaging Strings from the IViewControl
 *    to the respective control that sent the message.
 *
 *  Revision 1.52  2004/01/29 23:43:58  millermi
 *  - Added Dennis's changes to dataChanged(), which fixed refresh
 *    bug.
 *  - dataChanged(v2d) now checks to see if new virtual array
 *    references current virtual array. Removed check for arrays
 *    of the same size.
 *
 *  Revision 1.51  2004/01/29 08:16:25  millermi
 *  - Updated the getObjectState() to include parameter for specifying
 *    default state.
 *  - Added static variables DEFAULT and PROJECT to IPreserveState for
 *    use by getObjectState()
 *
 *  Revision 1.50  2004/01/07 06:47:39  millermi
 *  - New float Region parameters have been updated.
 *
 *  Revision 1.49  2003/12/30 00:39:40  millermi
 *  - Added Annular selection capabilities.
 *  - Changed SelectionJPanel.CIRCLE to SelectionJPanel.ELLIPSE
 *
 *  Revision 1.48  2003/12/29 06:32:58  millermi
 *  - Added method addSelection() to add selections through
 *    command as opposed to just via the GUI
 *  - Loading state now makes use of the doClick() method
 *    which was added to the ControlCheckboxButton class.
 *    This reduces the repetative code required to manually
 *    set events that should take place when a mouse click
 *    occurs.
 *
 *  Revision 1.47  2003/12/29 02:41:26  millermi
 *  - get/setPointedAt() now uses floatPoint2D to pass information
 *    about the current pointed at instead of java.awt.Point.
 *
 *  Revision 1.46  2003/12/23 02:21:37  millermi
 *  - Added methods and functionality to allow enabling/disabling
 *    of selections.
 *  - Fixed interface package changes where applicable.
 *
 *  Revision 1.45  2003/12/20 22:15:18  millermi
 *  - Now implements kill();
 *
 *  Revision 1.44  2003/12/20 20:08:55  millermi
 *  - Added ability to clear selections.
 *  - Added axis information for z axis.
 *  - replaced getDataMin() and getDataMax() with getValueAxisInfo()
 *    which returns an AxisInfo object containing the min, max, and more.
 *
 *  Revision 1.43  2003/12/18 22:36:29  millermi
 *  - Now allows selections made on border of image.
 *  - Now uses new AxisInfo class.
 *
 *  Revision 1.42  2003/12/17 20:31:06  millermi
 *  - Removed private static final COMPONENT_RESIZED since
 *    recent changes make this variable obsolete.
 *  - Changed PanViewControl.refreshData() to PanViewControl.repaint()
 *    to stay consistent with changes in the PanViewControl.
 *
 *  Revision 1.41  2003/11/26 18:49:45  millermi
 *  - Changed ElipseRegion reference to EllipseRegion.
 *
 *  Revision 1.40  2003/11/21 02:51:08  millermi
 *  - improved efficiency of paintComponents() and other aspects
 *    that repaint the IVC.
 *
 *  Revision 1.39  2003/11/21 00:48:14  millermi
 *  - Changed how the IVC updated the PanViewControl,
 *    now uses method refreshData().
 *
 *  Revision 1.38  2003/11/18 22:32:42  millermi
 *  - Added functionality to allow cursor events to be
 *    traced by the ControlColorScale.
 *
 *  Revision 1.37  2003/11/18 00:58:43  millermi
 *  - Now implements Serializable, requiring many private variables
 *    to be made transient.
 *  - Added method calls to PanViewControl.setLogScale() to update
 *    the PanViewControl image.
 *
 *  Revision 1.36  2003/10/29 20:32:57  millermi
 *  -Added further support for the PanViewControl, including colorscale
 *   and logscale response.
 *
 *  Revision 1.35  2003/10/27 08:49:33  millermi
 *  - Added the PanViewControl so users can now pan
 *    the image if it is larger than the viewport.
 *
 *  Revision 1.34  2003/10/23 05:49:51  millermi
 *  - Uncommented code that makes use of the ObjectState
 *    information from the ImageJPanel. Prior to now,
 *    the ImageJPanel did not preserve ObjectState.
 *
 *  Revision 1.33  2003/10/21 00:48:40  millermi
 *  - Added kill() to keep consistent with new IViewComponent.
 *
 *  Revision 1.32  2003/10/17 16:09:36  millermi
 *  - getObjectState() now returns a copy of the colorscale string.
 *
 *  Revision 1.31  2003/10/16 16:00:08  millermi
 *  - Changed getCurrentPoint() to getPointedAt() to
 *    maintain method name consistency.
 *
 *  Revision 1.30  2003/10/16 05:00:10  millermi
 *  - Fixed java docs errors.
 *
 *  Revision 1.29  2003/10/02 03:32:26  millermi
 *  - Added string values to java docs for public static strings.
 *
 *  Revision 1.28  2003/09/24 00:09:46  millermi
 *  - Added static variables to be used as keys by ObjectState
 *  - Added IPreserveState to implementing interfaces along with methods
 *    setObjectState() and getObjectState() required by this interface.
 *  - Added constructor with ObjectState parameter for creating a new
 *    ImageViewComponent with previous state information.
 *
 *  Revision 1.27  2003/09/11 06:28:30  millermi
 *  - Changed menu to coincide with changed to MenuItemMaker.
 *  - Overlay help menu now created using getOverlayMenu().
 *
 *  Revision 1.26  2003/08/29 23:12:09  millermi
 *  - dataChanged() now sets the title and AxisInfo if the array
 *    is of the same size.
 *
 *  Revision 1.25  2003/08/26 03:41:48  millermi
 *  - Added functionality to private class SelectedRegionListener to
 *    handle double wedge selections.
 *
 *  Revision 1.24  2003/08/21 18:22:57  millermi
 *  - Added capabilities for wedge selection
 *
 *  Revision 1.23  2003/08/14 21:49:00  millermi
 *  - Replaced "for" loop to repaint transparencies with paintComponents() in
 *    private class ImageListener. This updates grid lines in a desirable way.
 *  - Edited Revision 1.22 comments, one comment was left unfinished.
 *
 *  Revision 1.22  2003/08/14 17:07:27  millermi
 *  - Changed control of Selection Overlay from ControlCheckbox to
 *    ControlCheckboxButton.
 *  - Implementing dataChanged(IVArray) to handle different sized arrays.
 *  - Changed menus from specialized class instances to an instance of
 *    MenuItemMaker.java.
 *  - Moved construction of big_picture from buildViewComponent to constructor.
 *    buildViewComponent now only builds the background JPanel. This is useful
 *    when calling buildViewComponent multiple times.
 *  - Added returnFocus() to improve focus transition between overlays. When
 *    overlays are checked and unchecked, sometimes the focus is lost. This
 *    fixes most instances.
 *  - Added menu option to switch between color scales. Now south/east/control
 *    color scales can be controlled by Options>Color Scale>Display Position
 *
 *  Revision 1.21  2003/08/11 23:42:48  millermi
 *  - Changed getSelectedSet() to getSelectedRegions() which now
 *    returns an array of Regions.
 *  - Changed setSelectedSet() to setSelectedRegions() which now
 *    takes parameter of type Region.
 *  - Added MAXDATASIZE so it is no longer a hardcoded value.
 *  - Added select.getFocus() if annotation overlay gets unchecked while the
 *    selection overlay is visible. This ensures key events for the
 *    selection overlay are listened to.
 *
 *  Revision 1.20  2003/08/08 00:19:22  millermi
 *  - Moved initialization of local_bounds and global_bounds
 *  after ijp.initializeWorldCoords() in constructor. Fixes
 *  bug that assigned incorrect world coordinates to
 *  selections and annotations prior to zooming.
 *
 *  Revision 1.19  2003/08/07 15:53:20  dennis
 *  - Removed debug statements, added comments to getAxisInfo() for
 *    dealing with log axes.
 *  - getAxisInfo() no longer allows log scaling for image axes.
 *  - Uncommented code in constructor for overlays, removed code
 *    from buildViewComponent().
 *  - Added static variable COMPONENT_RESIZED
 *  - Added sendMessage(COMPONENT_RESIZED) to inform listeners when
 *    the imagejpanel is resized.
 *    (Mike Miller)
 *
 *  Revision 1.18  2003/08/06 13:54:49  dennis
 *  - Control for AxisOverlay2D changed from ControlCheckbox to a
 *    ControlCheckboxButton.
 *  - Removed creation of overlays from buildViewComponent(),
 *    now in constructor.
 *  - Transparencies vector now is built in constructor.
 *  - Combined checkbox and button controls for annotation overlay
 *    into one ControlCheckboxButton control.
 *  - Uncommented code that adjusts the selection overlay color
 *    depending on the color scale used to view the image.
 *    (Mike Miller)
 *
 *  Revision 1.17  2003/07/25 14:37:55  dennis
 *  - Now also implements IZoomTextAddible so that selection and annotation
 *  overlays may be added. (Mike Miller)
 *
 *  Revision 1.15  2003/07/05 19:47:34  dennis
 *  - Added methods getDataMin() and getDataMax().
 *  - Added capability for one- or two-sided color models. Currently,
 *    only two-sided color models are allowed. Alter line 187 to
 *    enable this feature.  (Mike Miller)
 *
 *  Revision 1.14  2003/06/18 22:16:42  dennis
 *  (Mike Miller)
 *  - Changed how horizontal color scale was added to the view
 *    component, now less wasted space.
 *  - Reduced size of vertical color scale.
 *
 *  Revision 1.13  2003/06/18 13:33:50  dennis
 *  (Mike Miller)
 *  - Now implements IColorScaleAddible, which required the addition of
 *    getColorScale() and getLogScale().
 *  - Added methods setColorControlEast() and setColorControlSouth() to
 *    allow user to add the vertical calibrated color scale to the east
 *    panel, or the horizontal calibrated color scale to the south panel.
 *
 *  Revision 1.12  2003/06/13 14:43:53  dennis
 *  - Added methods and implementation for getLocalCoordBounds() and
 *  getGlobalCoordBounds() to allow selection and annotation overlays to adjust
 *  when a zoom occurs. (Mike Miller)
 *
 *  Revision 1.11  2003/06/09 14:46:34  dennis
 *  Added functional HelpMenu under Options. (Mike Miller)
 *
 *  Revision 1.10  2003/06/06 18:51:00  dennis
 *  Added control for editing annotations. (Mike Miller)
 *
 *  Revision 1.9  2003/06/05 17:15:00  dennis
 *   - Added getFocus() call when Selection/AnnotationOverlay checkbox
 *     is selected. (Mike Miller)
 *
 *  Revision 1.8  2003/05/29 14:34:32  dennis
 *  Three changes: (Mike Miller)
 *   -added SelectionOverlay and its on/off control
 *   -added ControlColorScale to controls
 *   -added AnnotationOverlay and its on/off control
 *
 *  Revision 1.7  2003/05/24 17:33:25  dennis
 *  Added on/off control for Axis Overlay. (Mike Miller)
 * 
 *  Revision 1.6  2003/05/22 13:05:58  dennis
 *  Now returns menu items to place in menu bar.
 *
 *  Revision 1.5  2003/05/20 19:46:16  dennis
 *  Now creates a brightness control slider. (Mike Miller)
 *
 *  Revision 1.4  2003/05/16 15:25:12  dennis
 *  Implemented dataChanged() method.
 *  Added grid lines to test image to aid in testing.
 *
 *  Revision 1.3  2003/05/16 14:59:11  dennis
 *  Calculates space needed for labels, and adjusts space as the component
 *  is resized.  (Mike Miller)
 *
 */
 
package gov.anl.ipns.ViewTools.Components.TwoD;

import javax.swing.*; 
import java.io.Serializable;
import java.awt.*;
import java.awt.event.*;
import java.util.Vector; 

import gov.anl.ipns.Util.Numeric.floatPoint2D;
import gov.anl.ipns.Util.Sys.WindowShower;
import gov.anl.ipns.ViewTools.UI.FontUtil;
import gov.anl.ipns.ViewTools.Panels.Image.*;
import gov.anl.ipns.ViewTools.Panels.Transforms.*;
import gov.anl.ipns.ViewTools.Components.Transparency.*;
import gov.anl.ipns.ViewTools.Components.*;
import gov.anl.ipns.ViewTools.Components.ViewControls.*;
import gov.anl.ipns.ViewTools.Components.Menu.*;
import gov.anl.ipns.ViewTools.Components.Region.*;

/**
 * This class allows the user to view data in the form of an image. Meaning
 * is given to the data by way of overlays, which add calibration, selection,
 * and annotation abilities.
 * <BR><BR>
 * The ImageViewComponent uses a series of coordinate systems. The only
 * coordinate system visible to programmers is the "World Coordinate" system,
 * which is the range of x-axis and y-axis values that can be found in
 * the AxisInfo class of each axis. 
 */
public class ImageViewComponent implements IViewComponent2D, 
           /*for Axis/Colorscale*/         IColorScaleAddible,
           /*for Selection/Annotation*/    IZoomTextAddible,
	                                   IMarkerAddible,
	                                   IPreserveState,
					   Serializable
{ 
  // these variables are static names for the controls of the ImageViewComp.
  // Use these to determine which control you are dealing with.
 /**
  * "Intensity Slider" - use this static String to verify that the title of
  * the ViewControl returned is the intensity slider.
  */
  public static final String INTENSITY_SLIDER_NAME = "Intensity Slider";
  
 /**
  * "Color Scale" - use this static String to verify that the title of
  * the ViewControl returned is the color scale from the control panel.
  * This color scale is not the calibrated color scale bordering the image.
  */
  public static final String COLOR_SCALE_NAME = "Color Scale";
  
 /**
  * "Marker Overlay" - use this static String to verify that the title of
  * the ViewControl returned is that of the marker overlay control.
  */
  public static final String MARKER_OVERLAY_NAME = "Marker Overlay";
  
 /**
  * "Axis Overlay" - use this static String to verify that the title of
  * the ViewControl returned is that of the axis overlay control.
  */
  public static final String AXIS_OVERLAY_NAME = "Axis Overlay";
  
 /**
  * "Selection Overlay" - use this static String to verify that the title of
  * the ViewControl returned is that of the selection overlay control.
  */
  public static final String SELECTION_OVERLAY_NAME = "Selection Overlay";
  
 /**
  * "Annotation Overlay" - use this static String to verify that the title of
  * the ViewControl returned is that of the annotation overlay control.
  */
  public static final String ANNOTATION_OVERLAY_NAME = "Annotation Overlay";
  
 /**
  * "Panning Tool" - use this static String to verify that the title of
  * the ViewControl returned is that of the PanViewControl.
  */
  public static final String PAN_NAME = "Panning Tool";
  
 /**
  * "Pointed At" - use this static String to verify that the title of
  * the ViewControl returned is that of the CursorOutputControl responsible
  * for displaying the pointed-at cursor readouts.
  */
  public static final String CURSOR_READOUT_NAME = "Pointed At";

 // these variables preserve the state of the ImageViewComponent
 /**
  * "Precision" - This constant String is a key for referencing the state
  * information about the precision this view component will have. Precision
  * affects the significant digits displayed on the Axis Overlay, among other
  * things. The value that this key references is a primative integer, with
  * value > 0.
  */
  public static final String PRECISION  	 = "Precision";
 
 /**
  * "Font" - This constant String is a key for referencing the state information
  * about the font used by this view component. This font will also be
  * passed on to overlays, such as the Axis, Annotation, and Selection.
  * The value that this key references is of type Font.
  */
  public static final String FONT		 = "Font";
 
 /**
  * "Color Scale" - This constant String is a key for referencing the state
  * information about the color scale used by this view component. The value
  * that this key references is of type String. These string constants may be
  * found in IndexColorMaker.java.
  *
  * @see gov.anl.ipns.ViewTools.Panels.Image.IndexColorMaker
  */
  public static final String COLOR_SCALE	 = "Color Scale";
 
 /**
  * "Log Scale Slider" - This constant String is a key for referencing the state
  * information about the intensity or log scale slider. The value this
  * key references is of type ObjectState.
  */
  public static final String LOG_SCALE_SLIDER  	 = "Log Scale Slider";
 
 /**
  * "Color Control" - This constant String is a key for referencing the state
  * information about the preference of a color scale in the control panel. Of
  * type boolean, if true, an uncalibrated color scale will appear in the
  * control panel.
  */
  public static final String COLOR_CONTROL       = "Color Control";
 
 /**
  * "Color Control East" - This constant String is a key for referencing the
  * state information about the preference of a color scale placed below the
  * image. Of type boolean, if true, a calibrated color scale will appear below
  * the image.
  */
  public static final String COLOR_CONTROL_EAST  = "Color Control East";

 /**
  * "Color Control West" - This constant String is a key for referencing the
  * state information about the preference of a color scale placed to the right
  * of the image. Of type boolean, if true, a calibrated color scale will
  * appear to the right of the image.
  */
  public static final String COLOR_CONTROL_SOUTH = "Color Control West";
 
 /**
  * "Annotation Control" - This constant String is a key for referencing the
  * state information about the control that operates the annotation overlay.
  * The value this key references is of type ObjectState.
  */
  public static final String ANNOTATION_CONTROL  = "Annotation Control";

 /**
  * "Axis Control" - This constant String is a key for referencing the state
  * information about the control that operates the axis overlay. The value this
  * key references is of type ObjectState.
  */
  public static final String AXIS_CONTROL	 = "Axis Control";

 /**
  * "Marker Control" - This constant String is a key for referencing the state
  * information about the control that operates the marker overlay. The value
  * this key references is of type ObjectState.
  */
  public static final String MARKER_CONTROL	 = "Marker Control";

 /**
  * "Selection Control" - This constant String is a key for referencing the
  * state information about the control that operates the selection overlay.
  * The value this key references is of type ObjectState.
  */
  public static final String SELECTION_CONTROL   = "Selection Control";

 /**
  * "ImageJPanel" - This constant String is a key for referencing the state
  * information about the ImageJPanel. Since the ImageJPanel has its own state,
  * this value is of type ObjectState, and contains the state of the
  * ImageJPanel. 
  */
  public static final String IMAGEJPANEL	 = "ImageJPanel";

 /**
  * "AnnotationOverlay" - This constant String is a key for referencing the
  * state information about the Annotation Overlay. Since the overlay has its
  * own state, this value is of type ObjectState, and contains the state of
  * the overlay. 
  */
  public static final String ANNOTATION_OVERLAY  = "AnnotationOverlay";

 /**
  * "AxisOverlay2D" - This constant String is a key for referencing the state
  * information about the Axis Overlay. Since the overlay has its own state,
  * this value is of type ObjectState, and contains the state of the
  * overlay.
  */
  public static final String AXIS_OVERLAY_2D	 = "AxisOverlay2D";

 /**
  * "MarkerOverlay" - This constant String is a key for referencing the state
  * information about the Marker Overlay. Since the overlay has its own state,
  * this value is of type ObjectState, and contains the state of the
  * overlay.
  */
  public static final String MARKER_OVERLAY	 = "MarkerOverlay";

 /**
  * "SelectionOverlay" - This constant String is a key for referencing the
  * state information about the Selection Overlay. Since the overlay has
  * its own state, this value is of type ObjectState, and contains the state
  * of the overlay.
  */
  public static final String SELECTION_OVERLAY   = "SelectionOverlay";

 /**
  * "Preserve Aspect Ratio" - This constant String is a key for referencing the
  * state information about how to draw the ImageJPanel at the center of the
  * display panel. If true, the center image should never get distorted.
  * The value that this key references is a primative boolean.
  */
  public static final String PRESERVE_ASPECT_RATIO   = "Preserve Aspect Ratio";
  
  public static final String SELECTOR_NAMES = "Selectors";
  
  public static final String ADD_SELECTION = "Add Selector";
  
  //An object containing our array of data
  private transient IVirtualArray2D Varray2D;  
  private transient Vector Listeners = null;   
  private transient JPanel big_picture = new JPanel();  
  private transient JPanel background = new JPanel(new BorderLayout());  
  private transient ImageJPanel2 ijp;
  private transient Rectangle regioninfo;
  private transient Vector transparencies = new Vector();
  private int precision;
  private Font font;
  private transient ViewControl[] controls;
  private transient ViewMenuItem[] menus;
  private String colorscale;
  private boolean isTwoSided = true;
  private double logscale = 0;
  private boolean addColorControlEast = false;   // add calibrated color scale
  private boolean addColorControlSouth = false;  // add calibrated color scale
  private boolean addColorControl = true;  // add color scale with controls
  private int north_height = 0;  // These four values give the total height
  private int south_height = 0;  // and width of the north, east, south, and
  private int east_width = 0;    // west components of the background panel to
  private int west_width = 0;    // be used for setting aspect ratio of image.
  private boolean preserve_ratio = false;  // if true, preserve aspect ratio
  private boolean null_data = false;
 
 /**
  * Constructor that takes in a virtual array and creates an imagejpanel
  * to be viewed in a border layout.
  *
  *  @param  varr IVirtualArray2D containing data for image creation
  */
  public ImageViewComponent( IVirtualArray2D varr )  
  {
    font = FontUtil.LABEL_FONT2;
    ijp = new ImageJPanel2();
    colorscale = IndexColorMaker.HEATED_OBJECT_SCALE_2;
    ijp.setNamedColorModel( colorscale, isTwoSided, false);
    setPrecision(4);
    null_data = true;
    if( varr == null )
    {
      Varray2D = new VirtualArray2D(1,1);
      big_picture.add( new JLabel("No data to display as image.") );
      controls = new ViewControl[0];
      menus = new ViewMenuItem[0];
    }
    else
      dataChanged(varr);
  } 
  
 /**
  * Constructor that takes in a virtual array and a previous state to create
  * an imagejpanel to be viewed in a border layout.
  *
  *  @param  varr IVirtualArray2D containing data for image creation
  *  @param  state The state of a previous ImageViewComponent.
  */
  public ImageViewComponent( IVirtualArray2D varr, ObjectState state )  
  {
    this(varr);
    setObjectState(state);
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
    // If the original data passed in was null, do nothing.
    if( null_data )
      return;
    boolean redraw = false;  // if any values are changed, repaint overlay.
    Object temp = new_state.get(FONT);
    if( temp != null )
    {
      font = (Font)temp;
      redraw = true;  
    }  
    
    
    temp = new_state.get(ImageJPanel2.TWO_SIDED);
    if( temp != null )
    {
      isTwoSided = ((Boolean)temp).booleanValue();

      ijp.setNamedColorModel( colorscale, isTwoSided, false);
      redraw = true;  
    }
    temp = new_state.get(PRECISION);
    if( temp != null )
    {
      setPrecision( ((Integer)temp).intValue() );
      redraw = true;  
    }
    
    temp = new_state.get(COLOR_SCALE);
    if( temp != null )
    {
      colorscale = (String)temp; 
      ijp.setNamedColorModel( colorscale, isTwoSided, false);
      redraw = true;  
    } 
    
    
   
    temp = new_state.get(COLOR_CONTROL);
    if( temp != null )
    {
      addColorControl = ((Boolean)temp).booleanValue();
      redraw = true;  
    }   
    
    temp = new_state.get(COLOR_CONTROL_EAST);
    if( temp != null )
    {
      addColorControlEast = ((Boolean)temp).booleanValue();
      redraw = true;  
    }	
    
    temp = new_state.get(COLOR_CONTROL_SOUTH);
    if( temp != null )
    {
      addColorControlSouth = ((Boolean)temp).booleanValue();
      redraw = true;  
    }  
    
    temp = new_state.get(LOG_SCALE_SLIDER);
    if( temp != null )
    {
      ((ControlSlider)controls[0]).setObjectState( (ObjectState)temp );
      // by doing this, the value in the slider will be used to set all
      // other values. This will keep the logscale values consistent.
      logscale = ((ControlSlider)controls[0]).getValue(); 
      redraw = true;  
    } 
    
    temp = new_state.get(IMAGEJPANEL);
    if( temp != null )
    {
      ijp.setObjectState( (ObjectState)temp );
      redraw = true;  
    } 
    
    temp = new_state.get(ANNOTATION_OVERLAY);
    if( temp != null )
    {
      ((OverlayJPanel)transparencies.elementAt(0)).setObjectState( 
					      (ObjectState)temp );
      redraw = true;  
    }  
    
    temp = new_state.get(AXIS_OVERLAY_2D);
    if( temp != null )
    {
      ((OverlayJPanel)transparencies.elementAt(2)).setObjectState( 
					      (ObjectState)temp );
      redraw = true;  
    } 
    
    temp = new_state.get(MARKER_OVERLAY);
    if( temp != null )
    {
      ((OverlayJPanel)transparencies.elementAt(3)).setObjectState( 
					      (ObjectState)temp );
      redraw = true;  
    }  
    
    temp = new_state.get(SELECTION_OVERLAY);
    if( temp != null )
    {
      ((OverlayJPanel)transparencies.elementAt(1)).setObjectState( 
					      (ObjectState)temp );
      redraw = true;  
    }	
    
    temp = new_state.get(ANNOTATION_CONTROL);
    if( temp != null )
    {
      ((ControlCheckboxButton)controls[6]).setObjectState((ObjectState)temp);
      redraw = true;  
    }  	
    
    temp = new_state.get(AXIS_CONTROL);
    if( temp != null )
    {
      ((ControlCheckboxButton)controls[4]).setObjectState((ObjectState)temp);
      redraw = true;  
    }  	
    
    temp = new_state.get(MARKER_CONTROL);
    if( temp != null )
    {
      ((ControlCheckboxButton)controls[3]).setObjectState((ObjectState)temp);
      redraw = true;  
    }
    
    temp = new_state.get(SELECTION_CONTROL);
    if( temp != null )
    {
      ((ControlCheckboxButton)controls[5]).setObjectState((ObjectState)temp);
      redraw = true;  
    }
    
    temp = new_state.get(PRESERVE_ASPECT_RATIO);
    if( temp != null )
    {
      preserveAspectRatio(((Boolean)temp).booleanValue());
      // Keep menu "Preserve Image Aspect Ratio" consistent with state.
      menus[1].getItem().setSelected(((Boolean)temp).booleanValue());
      redraw = true;  
    }
   
    if( redraw )
      reInit();
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
  public ObjectState getObjectState( boolean isDefault )
  {
    // If the original data passed in was null, do nothing.
    if( null_data )
      return new ObjectState();
    ObjectState state = new ObjectState();
    state.insert( ANNOTATION_CONTROL,
              ((ControlCheckboxButton)controls[6]).getObjectState(isDefault) );
    state.insert( ANNOTATION_OVERLAY, 
      ((OverlayJPanel)transparencies.elementAt(0)).getObjectState(isDefault) );
    state.insert( AXIS_CONTROL, 
              ((ControlCheckboxButton)controls[4]).getObjectState(isDefault) );
    state.insert( AXIS_OVERLAY_2D,  
      ((OverlayJPanel)transparencies.elementAt(2)).getObjectState(isDefault) );
    state.insert( COLOR_CONTROL, new Boolean(addColorControl) );
    state.insert( COLOR_CONTROL_EAST, new Boolean(addColorControlEast) );
    state.insert( COLOR_CONTROL_SOUTH, new Boolean(addColorControlSouth) );
    state.insert( ImageJPanel2.TWO_SIDED, new Boolean( isTwoSided));
    state.insert( COLOR_SCALE, new String(colorscale) );
    state.insert( FONT, font );
    state.insert( IMAGEJPANEL, ijp.getObjectState(isDefault) );
    state.insert( LOG_SCALE_SLIDER, 
      ((ControlSlider)controls[0]).getObjectState(isDefault) );
    state.insert( MARKER_CONTROL, 
              ((ControlCheckboxButton)controls[3]).getObjectState(isDefault) );
    state.insert( MARKER_OVERLAY,  
      ((OverlayJPanel)transparencies.elementAt(3)).getObjectState(isDefault) );
    state.insert( PRECISION, new Integer(getPrecision()) );
    state.insert( SELECTION_CONTROL,
              ((ControlCheckboxButton)controls[5]).getObjectState(isDefault) );
    state.insert( SELECTION_OVERLAY,  
      ((OverlayJPanel)transparencies.elementAt(1)).getObjectState(isDefault) );
    state.insert( PRESERVE_ASPECT_RATIO, new Boolean(preserve_ratio) );
    
    return state;
  }

 // ------ add/removeMarker() methods satifsy IMarkerAddible interface ------ 
 /**
  * Add a marker to be displayed on the image. The marker is best used for
  * programmatic highlighting, such as peaks. Use annotations to write text
  * or interactively mark. Remember, marks of the same type, color, and size
  * can be grouped into one marker.
  *
  *  @param  mark The marker or list of markers with the same attributes that
  *               will be displayed on the image.
  */
  public void addMarker( Marker mark )
  {
    // If the original data passed in was null, do nothing.
    if( null_data )
      return;
    ((MarkerOverlay)(transparencies.elementAt(3))).addMarker(mark);
  }
  
 /**
  * Remove mark from the list of markers, causing it to no longer
  * appear on the image. Any marker that is removed will only appear on
  * the image if it is re-added using addMarker().
  *
  *  @param  mark The marker to be removed.
  */
  public void removeMarker( Marker mark )
  {
    // If the original data passed in was null, do nothing.
    if( null_data )
      return;
    ((MarkerOverlay)(transparencies.elementAt(3))).removeMarker(mark);
  }
  
 /**
  * Remove all markers from the MarkerOverlay, causing no markers to appear
  * on the image. Any marker that is removed will only appear on the image
  * if it is re-added using addMarker().
  */
  public void removeAllMarkers()
  {
    // If the original data passed in was null, do nothing.
    if( null_data )
      return;
    ((MarkerOverlay)(transparencies.elementAt(3))).clearMarkers();
  }
  
 /**
  * Call this method to prevent the center image from being distorted. If true,
  * the zoomed image will resize to the aspect ratio of the image rows and
  * columns.
  *
  *  @param  doPreserve If true, the image will not be distorted.
  */
  public void preserveAspectRatio( boolean doPreserve )
  {
    // If the original data passed in was null or if the desired option
    // is already set, do nothing
    if( null_data || doPreserve == preserve_ratio )
      return;
    // add AspectRatio listener only if one has not been added.
    ComponentListener[] bp_list = getDisplayPanel().getComponentListeners();
    if( doPreserve )
    {
      // does a PreserveAspect listener exist
      boolean contains_aspect_listener = false;
      int i = 0;
      // find index of component listeners from PreserveAspect class.
      while( !contains_aspect_listener && (i < bp_list.length) )
      {
        if( bp_list[i] instanceof PreserveAspect )
	{
	  contains_aspect_listener = true;
	}
	i++;
      }
      // add PreserveAspect listener if none have been added.
      if( !contains_aspect_listener )
      {
        getDisplayPanel().addComponentListener( new PreserveAspect() );
      }
    }
    preserve_ratio = doPreserve;
    // Keep menu item "Preserve Image Aspect Ratio" consistent.
    menus[1].getItem().setSelected(preserve_ratio);
    // If image not yet visible, the componentResized() will be called
    // when it is made visible. If image is visible, adjust image now.
    if( getDisplayPanel().isVisible() )
    {
      buildAspectImage();
    }
  }
      
 /**
  * This method will disable the selections included in the names
  * list. Names are defined by static Strings in the SelectionJPanel class.
  *
  *  @param  names List of selection names defined by SelectionJPanel class.
  *  @see gov.anl.ipns.ViewTools.Components.Cursor.SelectionJPanel
  */
  public void disableSelection(String[] names)
  {
    // If the original data passed in was null, do nothing.
    if( null_data )
      return;
    ((SelectionOverlay)(transparencies.elementAt(1))).disableSelection( names );
  }
     
 /**
  * This method will enable the selections included in the names
  * list. Names are defined by static Strings in the SelectionJPanel class.
  *
  *  @param  names List of selection names defined by SelectionJPanel class.
  *  @see gov.anl.ipns.ViewTools.Components.Cursor.SelectionJPanel
  */
  public void enableSelection(String[] names)
  {
    // If the original data passed in was null, do nothing.
    if( null_data )
      return;
    ((SelectionOverlay)(transparencies.elementAt(1))).enableSelection( names );
  } 
  
 // These method are required because this component implements 
 // IColorScaleAddible
 /**
  * This method returns the color scale used by the imagejpanel.
  *
  *  @return colorscale
  */
  public String getColorScale()
  {
    return colorscale;
  }
  
 /**
  *
  */
  public void setColorScale( String color_scale )
  {
    // If not a valid colorscale, ignore request.
    if( !( color_scale.equals(IndexColorMaker.GRAY_SCALE) ||
           color_scale.equals(IndexColorMaker.GREEN_YELLOW_SCALE) ||
           color_scale.equals(IndexColorMaker.HEATED_OBJECT_SCALE) ||
           color_scale.equals(IndexColorMaker.HEATED_OBJECT_SCALE_2) ||
           color_scale.equals(IndexColorMaker.MULTI_SCALE) ||
           color_scale.equals(IndexColorMaker.NEGATIVE_GRAY_SCALE) ||
           color_scale.equals(IndexColorMaker.OPTIMAL_SCALE) ||
           color_scale.equals(IndexColorMaker.RAINBOW_SCALE) ||
           color_scale.equals(IndexColorMaker.SPECTRUM_SCALE) ) )
      return;
    // else change color scale.
    colorscale = color_scale;
    ijp.setNamedColorModel( colorscale, isTwoSided, true );
    ((ControlColorScale)controls[1]).setColorScale( colorscale, 
    						    isTwoSided );	 
    ((PanViewControl)controls[9]).repaint();
    sendMessage(COLORSCALE_CHANGED);
    paintComponents();
  }
  
 /**
  * This method will get the AxisInfo for the value axis. Use this for
  * finding the datamin, datamax, units, and labels for the data.
  *
  *  @return axisinfo about the data being analyzed.
  */
  public AxisInfo getValueAxisInfo()
  {
    // If the original data passed in was null, return dummy values.
    if( null_data )
      return new AxisInfo( 0, 1, "", "", AxisInfo.LINEAR );
    return getAxisInformation( AxisInfo.Z_AXIS );
  }
  
 // The following methods are required by IAxisAddible and ILogAxisAddible
 // and must be implemented because this component implements 
 // IColorScaleAddible which extends ILogAxisAddible which extends 
 // IAxisAddible.
 /**
  * This method returns the info about the specified axis. Currently, axes
  * for the image can only be viewed in linear form. If log axes are required,
  * FunctionViewComponent does this and may provide code to implement this.
  * 
  *  @param  axiscode Use AxisInfo integer codes.
  *  @return If axiscode = AxisInfo.X_AXIS, return info about x axis.
  *	     If axiscode = AxisInfo.Y_AXIS, return info about y axis.
  *	     If axiscode = AxisInfo.Z_AXIS, return info about "value" axis.
  */
  public AxisInfo getAxisInformation( int axiscode )
  {
    // If the original data passed in was null, return dummy values.
    if( null_data )
      return new AxisInfo( 0, 1, "", "", AxisInfo.LINEAR );
    // if true, return x info
    if( axiscode == AxisInfo.X_AXIS )
    {
      return new AxisInfo( ijp.getGlobalWorldCoords().getX1(),
        		   ijp.getGlobalWorldCoords().getX2(),
        		   Varray2D.getAxisInfo(AxisInfo.X_AXIS).getLabel(),
        		   Varray2D.getAxisInfo(AxisInfo.X_AXIS).getUnits(),
        		   AxisInfo.LINEAR );
    }
    // if true, return y info
    if( axiscode == AxisInfo.Y_AXIS )
    {
      return new AxisInfo( ijp.getGlobalWorldCoords().getY1(),
    			   ijp.getGlobalWorldCoords().getY2(),
    			   Varray2D.getAxisInfo(AxisInfo.Y_AXIS).getLabel(),
    			   Varray2D.getAxisInfo(AxisInfo.Y_AXIS).getUnits(),
    			   AxisInfo.LINEAR );
    }
    // else return z info
    return new AxisInfo( ijp.getDataMin(),
        		 ijp.getDataMax(),
        		 Varray2D.getAxisInfo(AxisInfo.Z_AXIS).getLabel(),
        		 Varray2D.getAxisInfo(AxisInfo.Z_AXIS).getUnits(),
        		 AxisInfo.LINEAR );
  }
  
 /**
  * This method returns a rectangle containing the location and size
  * of the imagejpanel.
  *
  *  @return The region info about the imagejpanel
  */ 
  public Rectangle getRegionInfo()
  {
    // If the original data passed in was null, do nothing.
    if( null_data )
      return new Rectangle();
    return regioninfo;
  }    
 
 /**
  * This method will return the title given to the image as specified by
  * the Virtual Array
  *
  *  @return title stored in Virtual Array
  */
  public String getTitle()
  {
    // If the original data passed in was null, do nothing.
    if( null_data )
      return "";
    return Varray2D.getTitle();
  }
  
 /**
  * This method will return the precision specified by the user. Precision
  * will be assumed to be 4 if not specified. The overlays will call
  * this method to determine the precision.
  *
  *  @return precision of displayed values
  */
  public int getPrecision() 
  {
    return precision;
  }
  
 /**
  * This method will set the precision of numbers displayed by the
  * ImageViewComponent. Precision is assumed to be 4 if not specified.
  *
  *  @param  precision of displayed values
  */
  public void setPrecision( int precision ) 
  {
    // If negative or zero, do not set.
    if( precision <= 0 )
      return;
    this.precision = precision;
  }
  
 /**
  * This method will return the font used on by the overlays. The axis overlay
  * will call this to determine what font to use.
  *
  *  @return font of displayed values
  */
  public Font getFont()
  {
    return font;
  }
  
 /**
  * This method will return the local coordinate bounds of the center
  * jpanel. To be implemented, the center may have to be a coordjpanel.
  *
  *  @return local coordinate bounds of ImageJPanel, in world coordinates.
  */
  public CoordBounds getLocalCoordBounds()
  {
    return ijp.getLocalWorldCoords().MakeCopy();
  }
     
 /**
  * This method will return the global coordinate bounds of the center
  * jpanel. To be implemented, the center may have to be a coordjpanel.
  *
  *  @return global coordinate bounds of ImageJPanel, in world coordinates.
  */
  public CoordBounds getGlobalCoordBounds()
  {
    return ijp.getGlobalWorldCoords().MakeCopy();
  }  


 /**
  * Get a copy of the tranformation that maps world coordinates to array
  * (col,row) coordinates for this view component. 
  *
  * @return a CoordTransform object that maps from world coordinates
  *         to array (col,row) coordinates.
  */
  public CoordTransform getWorldToArrayTransform()
  {
    return ijp.getWorldToImageTransform();
  }

  
 /**
  * This method will get the current log scale value for the imagejpanel.
  * This value is on the interval of [0,100].
  *
  *  @return logscale value from the intensity slider
  */ 
  public double getLogScale()
  {
    return logscale;
  }

  
 //****************************************************************************

 // Methods required since this component implements IViewComponent2D
 /**
  * This method sets the pointed-at position and adjusts the crosshairs on
  * the image. setPointedAt() can be called from the viewer to synchronize
  * the pointed-at of this component with the pointed-at of another component.
  *
  *  @param  fpt The current pointed-at floatPoint2D in world coordinates.
  */
  public void setPointedAt( floatPoint2D fpt )
  {
    // If the original data passed in was null, do nothing.
    if( null_data )
      return;
    // If pointed at already set to the desired point, do nothing.
    if( getPointedAt() == fpt || 
        ( getPointedAt().x == fpt.x && getPointedAt().y == fpt.y ) )
      return;
    //set the cursor position on ImageJPanel
    ijp.set_crosshair_WC( fpt );
    //ijp.setCurrent_WC_point(fpt);
  }

 /**
  * This method gets the current pointed-at position in world coordinates. The
  * current pointed-at position is point on the image where the crosshairs
  * intersect. getPointedAt() can be called from the viewer to synchronize
  * the pointed-at of this component with the pointed-at of another component.
  *
  *  @return  The current pointed-at floatPoint2D in world coordinate.
  */
  public floatPoint2D getPointedAt()
  {
    // If the original data passed in was null, return dummy value.
    if( null_data )
      return new floatPoint2D();
    return new floatPoint2D(ijp.getCurrent_WC_point());
  }
  
 /**
  * This method allows users to add a selection without removing previous
  * selections.
  *
  *  @param  world_coord_region The array of regions to be added, with defining
  *                             points in world coord points.
  *  @see    gov.anl.ipns.ViewTools.Components.Transparency.SelectionOverlay
  */
  public void addSelectedRegion( Region world_coord_region )
  {
    // If the original data passed in was null or region passed in
    // is null, do nothing.
    if( null_data || world_coord_region == null )
      return;
    // Since table regions are unsupported by the SelectionOverlay, filter
    // them out.
    if( world_coord_region instanceof TableRegion )
    {
      System.out.println("TableRegions not supported by ImageViewComponent."+
                         "addSelectedRegion()");
      return;
    }
    Region[] reg = new Region[1];
    reg[0] = world_coord_region;
    ((SelectionOverlay)(transparencies.elementAt(1))).addRegions( reg );
    // if selection control is unchecked, turn it on.
    if( !((ControlCheckboxButton)controls[5]).isSelected() )
      ((ControlCheckboxButton)controls[5]).doClick();
    returnFocus();
    // SelectedRegionListener will send out SELECTED_CHANGED message.
  }
 
 /**
  * This method creates a selected region to be displayed over the imagejpanel
  * by the selection overlay. Any previously selected regions will be erased
  * by using this method. To maintain previous selections, use
  * addSelectedRegion(). A stack could be added to allow for undo and redo
  * of selections. 
  * If null is passed as a parameter, the selections will be cleared.
  *
  *  @param  rgn - array of selected Regions
  */ 
  public void setSelectedRegions( Region[] rgn ) 
  {
    // If the original data passed in was null, do nothing.
    if( null_data )
      return;
    ((SelectionOverlay)(transparencies.elementAt(1))).clearRegions();
    // If rgn is null, net effect is to clear regions.
    if( rgn != null )
    {
      // Search through rgn list, count all table regions.
      int counter = 0;
      for( int i = 0; i < rgn.length; i++ )
        if( rgn[i] instanceof TableRegion )
	  counter++;
      // If all elements of rgn are table regions, ignore request.
      if( counter == rgn.length )
        return;
      // If a valid Region exists, add it.
      ((SelectionOverlay)(transparencies.elementAt(1))).addRegions( rgn );
      // if selection control is unchecked, turn it on.
      if( !((ControlCheckboxButton)controls[5]).isSelected() )
        ((ControlCheckboxButton)controls[5]).doClick();
      returnFocus();
    }
    // SelectedRegionListener will send out SELECTED_CHANGED message.
  }
 
 /**
  * Get geometric regions created using the selection overlay.
  *
  *  @return selectedregions
  */ 
  public Region[] getSelectedRegions() //keep the same (for now)
  {
    // If the original data passed in was null, do nothing.
    if( null_data )
      return new Region[0];
    Vector regions = 
       ((SelectionOverlay)transparencies.elementAt(1)).getRegions();
    Region[] selectedregions = new Region[regions.size()];
    // This code will set the world and image bounds. Since only the 
    // ImageViewComponent knows these bounds, it must set them.
    for( int i = 0; i < selectedregions.length; i++ )
    {
    	/*
      ((Region)regions.elementAt(i)).setWorldBounds(ijp.getGlobalWorldCoords());
      // Image bounds are consistent with those set in ImageJPanel.
      ((Region)regions.elementAt(i)).setImageBounds( new CoordBounds( 
                                     0,
                                     0,
                                     Varray2D.getNumColumns(),
                                     Varray2D.getNumRows() ) );
        */
      selectedregions[i] = (Region)regions.elementAt(i);
    }
    return selectedregions;
  } 


 /**
  * This method will be called to notify this component of a change in data.
  */
  public void dataChanged()  
  {
    // If the original data passed in was null, do nothing.
    if( null_data )
      return;
    ijp.setData(Varray2D, true);
    // Since data bounds could have changed, if either calibrated color scale
    // was displayed, redraw them by rebuilding the view component.
    if( addColorControlEast || addColorControlSouth )
      buildViewComponent();
    // This is required since the PanViewControl holds its own bounds.
    ((PanViewControl)controls[9]).setGlobalBounds(getGlobalCoordBounds());
    ((PanViewControl)controls[9]).setLocalBounds(getLocalCoordBounds());
 // ((PanViewControl)controls[9]).validate();  // Need this to resize control.
    ((PanViewControl)controls[9]).makeNewPanImage = true ;
    ((PanViewControl)controls[9]).repaint();
    paintComponents();
  }
 
 /**
  * This method will be called to notify this component of a change in data 
  * and an entirely new VirtualArray is used.
  *
  *  @param  pin_Varray - passed in array
  */ 
  public void dataChanged( IVirtualArray2D pin_Varray ) // pin == "passed in"
  {
    // If the original data passed in was null, do nothing.
    if( pin_Varray == null )
    {
      big_picture.removeAll();
      ijp.removeAllActionListeners();
      big_picture.add( new JLabel("No data to display as image.") );
      null_data = true;
      // Redraw big_picture with new JLabel.
      big_picture.validate();
      big_picture.repaint();
      return;
    }
    // compare references, if not the same, reinitialize the virtual array.
    if( true )//pin_Varray=Varray2D
    { 
      Varray2D = pin_Varray;
      // If IVC was initialized with null data, initialize variables that
      // would have been initialized in the constructor, had the data been
      // valid.
      if( null_data )
      {
        big_picture.removeAll();
        ImageListener ijp_listener = new ImageListener();
        ijp.addActionListener( ijp_listener );
        	    
        ComponentAltered comp_listener = new ComponentAltered();   
        ijp.addComponentListener( comp_listener );
        
        regioninfo = new Rectangle( ijp.getBounds() );
        
        AxisInfo xinfo = Varray2D.getAxisInfo(AxisInfo.X_AXIS);
        AxisInfo yinfo = Varray2D.getAxisInfo(AxisInfo.Y_AXIS);
        
        ijp.initializeWorldCoords( new CoordBounds( xinfo.getMin(),
        					    yinfo.getMax(),	 
        					    xinfo.getMax(),
        					    yinfo.getMin() ) ); 

	null_data = false;
        ijp.setData(Varray2D, false);  // this is needed to setup coord trans
                                       // before overlays are made.  Also
                                       // ijp needs data before checking for
                                       // two-sided model below:
        // two-sided model
        if( ijp.getDataMin() < 0 )
           isTwoSided = true;
        // one-sided model
        else
           isTwoSided = false;
        ijp.setNamedColorModel(colorscale, isTwoSided, false); 
        
        //create transparencies
        AnnotationOverlay top = new AnnotationOverlay(this);
        top.setVisible(false);      // initialize this overlay to off.
        SelectionOverlay nextup = new SelectionOverlay(this);
        nextup.setVisible(false);   // initialize this overlay to off.
        nextup.setRegionColor(Color.magenta);
        nextup.addActionListener( new SelectedRegionListener() );
        AxisOverlay2D bottom_overlay = new AxisOverlay2D(this);
        MarkerOverlay marker_overlay = new MarkerOverlay(this);
        
        // add the transparencies to the transparencies vector
        transparencies.clear();
        transparencies.add(top);
        transparencies.add(nextup);
        transparencies.add(bottom_overlay);
        transparencies.add(marker_overlay); 
        
        OverlayLayout overlay = new OverlayLayout(big_picture);
        big_picture.setLayout(overlay);
        for( int trans = 0; trans < transparencies.size(); trans++ )
          big_picture.add((OverlayJPanel)transparencies.elementAt(trans));
        big_picture.add(background);
        
        Listeners = new Vector();
        buildViewComponent();	 // initializes big_picture to jpanel containing
        			 // the background and transparencies
        buildViewControls(); 
        buildViewMenuItems();
	// Redraw the new image.
//	big_picture.validate();
//	big_picture.repaint();
	//paintComponents();
      }
      else
      {
        // since new data array, remove all selections and annotations.
        ((AnnotationOverlay)(transparencies.elementAt(0))).clearAnnotations();
        ((SelectionOverlay)(transparencies.elementAt(1))).clearRegions();

        AxisInfo xinfo = Varray2D.getAxisInfo(AxisInfo.X_AXIS);
        AxisInfo yinfo = Varray2D.getAxisInfo(AxisInfo.Y_AXIS);
        ijp.initializeWorldCoords( new CoordBounds( xinfo.getMin(),
    					            yinfo.getMax(),
    					            xinfo.getMax(),
    					            yinfo.getMin() ) );
      }
    }
    dataChanged();   // call other dataChanged() method to reset the ijp and 
                     // redraw the display.
  }
  
 /**
  * Method to add a listener to this component.
  *
  *  @param  act_listener
  */
  public void addActionListener( ActionListener act_listener )
  {	     
    for ( int i = 0; i < Listeners.size(); i++ )    // don't add it if it's
      if ( Listeners.elementAt(i).equals( act_listener ) ) // already there
        return;

    Listeners.add( act_listener ); //Otherwise add act_listener
  }
 
 /**
  * Method to remove a listener from this component.
  *
  *  @param  act_listener
  */ 
  public void removeActionListener( ActionListener act_listener )
  {
    Listeners.remove( act_listener );
  }
 
 /**
  * Method to remove all listeners from this component.
  */ 
  public void removeAllActionListeners()
  {
    Listeners.removeAllElements();
  }
 
 /**
  * Returns all of the controls needed by this view component.
  * Controls are as follows:<BR><BR>
  * [0] = Intensity Slider<BR>
  * [1] = Color Scale, No calibrations<BR>
  * [2] = Cursor Readout<BR>
  * [3] = Marker Overlay Control<BR>
  * [4] = Axis Overlay Control<BR>
  * [5] = Selection Overlay Control<BR>
  * [6] = Annotation Overlay Control<BR>
  * [7] = Pan View Control<BR>
  *
  *  @return controls
  */ 
  public ViewControl[] getControls()
  {    
    return controls;
  }
 
 /**
  * Returns all of the menu items needed by this view component
  *
  *  @return menus;
  */ 
  public ViewMenuItem[] getMenuItems()
  {
    return menus;
  }
  
 /**
  * Return the "background" or "master" panel
  *
  *  @return JPanel containing imagejpanel in the center of a borderlayout.  
  */
  public JPanel getDisplayPanel()
  {
    return big_picture;   
  }
  
   
 /**
  * This method is called by the viewer to inform the view component
  * it is no longer needed. In turn, the view component closes all windows
  * created by it before closing.
  */
  public void kill()
  {    
    for( int trans = 0; trans < transparencies.size(); trans++ )
      ((OverlayJPanel)transparencies.elementAt(trans)).kill();
  }
 
 // *************************** Miscellaneous methods ************************* 
 /**
  * This method allows the user to place a VERTICAL color control in the
  * east panel of the view component. If this method is called, it is assumed
  * that the simple color scale in the controls is no longer wanted, so
  * that color scale will disappear.
  *
  *  @param isOn - true if calibrated color scale appears to the right
  *                of the image.
  */
  public void setColorControlEast( boolean isOn )
  {
    // If the original data passed in was null, do nothing.
    if( null_data )
      return;
    
    addColorControlEast = isOn;
    // if calibrated colorscale is requested, turn off control colorscale
    if( addColorControlEast )
    {
      addColorControl = false;
      ((ControlColorScale)controls[1]).setVisible(false);
    }
    buildViewComponent();
  }
  
 /**
  * This method allows the user to place a HORIZONTAL color control in the
  * south panel of the view component. If this method is called, it is assumed
  * that the simple color scale in the controls is no longer wanted, so
  * that color scale will disappear.
  *
  *  @param isOn - true if calibrated color scale appears below the image.
  */   
  public void setColorControlSouth( boolean isOn )
  {
    // If the original data passed in was null, do nothing.
    if( null_data )
      return;
  
    addColorControlSouth = isOn;
    // if calibrated colorscale is requested, turn off control colorscale
    if( addColorControlSouth )
    {
      addColorControl = false;
      ((ControlColorScale)controls[1]).setVisible(false);
    }
    buildViewComponent();
  }
 
 /*
  * Here are methods to outside programmers get access to the other coordinate
  * systems.
  */ 
 /**
  * Get the (x = column, y = row) "image coordinate" point of the image at
  * the "pixel coordinate" point given. This method will map pixel values
  * to image column/row values.
  *
  *  @param  pixel_pt The pixel value (x=row,y=column) of the image.
  *  @return The "Image Coordinate" value of pixel_pt.
  */
  public Point getColumnRowAtPixel( Point pixel_pt )
  {
    // If the original data passed in was null, return dummy values.
    if( null_data )
      return new Point();
    return new Point( ijp.ImageCol_of_PixelCol(pixel_pt.y),
                      ijp.ImageRow_of_PixelRow(pixel_pt.x) );
  }
   
 /**
  * Get the (x = column, y = row) "image coordinate" point of the image at
  * the "world coordinate" point given. This method will map world coordinate
  * values (defined by AxisInfo) to image column/row values.
  *
  *  @param  wc_pt The world coordinate value (x-axis,y-axis) of the image.
  *  @return The "Image Coordinate" value of wc_pt.
  */
  public Point getColumnRowAtWorldCoords( floatPoint2D wc_pt )
  {
    // If the original data passed in was null, return dummy values.
    if( null_data )
      return new Point();

    return new Point( ijp.ImageCol_of_WC_x(wc_pt.x),
                      ijp.ImageRow_of_WC_y(wc_pt.y) );
  }


 /**
  * Get the (x-axis, y-axis) "world coordinate" point of the image at
  * the "pixel coordinate" point given. This method will map pixel values
  * to world coordinate x-axis/y-axis values.
  *
  *  @param  pixel_pt The pixel value (x=row,y=column) of the image.
  *  @return The "World Coordinate" value of pixel_pt.
  */
  public floatPoint2D getWorldCoordsAtPixel( Point pixel_pt )
  {
    // If the original data passed in was null, return dummy values.
    if( null_data )
      return new floatPoint2D();

    return getWorldCoordsAtColumnRow( getColumnRowAtPixel(pixel_pt) );
  }

   
 /**
  * Get the (x-axis,y-axis) "world coordinate" value of the
  * (x = column, y = row) "image coordinate" point given.
  * This method will map image column/row values to world coordinate
  * values (defined by AxisInfo).
  *
  *  @param  col_row_pt The image coordinate value (column,row) of the image.
  *  @return The "World Coordinate" point of col_row_pt.
  */
  public floatPoint2D getWorldCoordsAtColumnRow( Point col_row_pt )
  {
    // If the original data passed in was null, return dummy values.
    if( null_data )
      return new floatPoint2D();
    CoordBounds imagebounds = ijp.getImageCoords();
    CoordBounds wcbounds = ijp.getGlobalWorldCoords();
    CoordTransform image_to_wc = new CoordTransform( imagebounds, wcbounds );
    return new floatPoint2D( image_to_wc.MapXTo(col_row_pt.x),
                             image_to_wc.MapYTo(col_row_pt.y) );
  }
  

  /**
   *  This method will show (or hide) the selection editor for the specified
   *  compound selection.  
   *
   *  @param  name       The name of the compound selection for which the 
   *                     editor should be shown, or hidden.
   *
   *  @param  show_hide  flag indicating whether to show (true) or hide (false)
   *                     the editor for the specified compound selection.
   */
  public void showSelector(String name, boolean show_hide)
  {
     ((SelectionOverlay)(transparencies.elementAt(1))).
                                                   showEditor(name, show_hide);
  }

  public void closeWindows()
  {
    if(transparencies.elementAt(1) != null)
      ((SelectionOverlay)transparencies.elementAt(1)).closeWindows();
  }

 /*
  * Tells all listeners about a new action.
  *
  *  @param  message
  */  
  private void sendMessage( String message )
  {
    for ( int i = 0; i < Listeners.size(); i++ )
    {
      ActionListener listener = (ActionListener)Listeners.elementAt(i);
      listener.actionPerformed( new ActionEvent( this, 0, message ) );
    }
  }
 
 /*
  * This method repaints the ImageViewComponent correctly
  */ 
  private void paintComponents()
  {
    // Get the top-most parent and call it's repaint().
    Component temppainter = big_picture;
    while( temppainter.getParent() != null )
      temppainter = temppainter.getParent();
    temppainter.repaint();
  }
 
 /**
  * This method is will give focus to the correct overlay so that keyboard
  * events are recognized after controls are given focus.
  */ 
  public void returnFocus()
  {
    // If the original data passed in was null, do nothing.
    if( null_data )
      return;
    
    AnnotationOverlay note = (AnnotationOverlay)transparencies.elementAt(0); 
    SelectionOverlay select = (SelectionOverlay)transparencies.elementAt(1); 
    AxisOverlay2D axis = (AxisOverlay2D)transparencies.elementAt(2);
    
    if( note.isVisible() )
      note.getFocus(); 
    else if(select.isVisible() )
      select.getFocus();
    else if(axis.isVisible() )
      axis.getFocus();   
    else
      getDisplayPanel().requestFocus();
  }
 
 /*
  * This method is used by the setObjectState() to set all saved state.
  */ 
  private void reInit()  
  {
    ijp.setNamedColorModel(colorscale, isTwoSided, false);
    ijp.repaint();
    // make sure logscale and two-sided are consistent
    ((AxisOverlay2D)transparencies.elementAt(2)).setTwoSided(isTwoSided);
    ijp.changeLogScale(logscale,true);
    // since flags have already been set, this will put the color scales
    // where they need to be.
    buildViewComponent();    // builds the background jpanel containing
			     // the image and possibly a calibrated color scale
    // this control is an uncalibrated colorscale
    if( addColorControl )
    {
      ((ControlColorScale)controls[1]).setColorScale( colorscale, isTwoSided );
      ((ControlColorScale)controls[1]).setVisible(true);
    }
    else
      ((ControlColorScale)controls[1]).setVisible(false);
    
    // give focus to the top overlay
    returnFocus();
    
    ((PanViewControl)controls[9]).setGlobalBounds(getGlobalCoordBounds());
    ((PanViewControl)controls[9]).setLocalBounds(getLocalCoordBounds());
    ((PanViewControl)controls[9]).repaint();
  } 
  
 /*
  * This method takes in an imagejpanel and puts it into a borderlayout.
  * Overlays are added to allow for calibration, selection, and annotation.
  */
  private void buildViewComponent()
  {  
    north_height = 25;
    south_height = font.getSize() * 3 + 9;
    east_width = 50;
    west_width = font.getSize() * getPrecision() + 22;
    // this will be the background for the master panel
    background.removeAll();
    String title = getValueAxisInfo().getLabel() + " (" + 
                   getValueAxisInfo().getUnits() + ")";
    JPanel north = new JPanel();
    north.setPreferredSize(new Dimension( 0, north_height ) );
    JPanel east; 
    JPanel south;
    // add calibrated color scale to the east panel
    if( addColorControlEast )
    {
      east_width += 40;  // reset east_width to 90 if it has a colorscale
      east = new ControlColorScale( this, ControlColorScale.VERTICAL );
      east.setPreferredSize( new Dimension( east_width, 0 ) );
      //((ControlColorScale)east).setTwoSided(isTwoSided);
      //((ControlColorScale)east).setLogScale(logscale);
      ((ControlColorScale)east).setTitle(title);
    }
    else
    {
      east = new JPanel();
      east.setPreferredSize(new Dimension( east_width, 0 ) );
    }
    // add calibrated color scale to the south panel
    if( addColorControlSouth )
    {
      // divide south panel in two, upper spacer and lower color control
      south = new JPanel( new BorderLayout() );
      // add spacer to upper part of south panel of display panel
      JPanel mininorth = new JPanel();
      mininorth.setPreferredSize( new Dimension( 0, south_height ) );
      south.add( mininorth, "North" );
      // add calibrated color control to lower part of south panel
      ControlColorScale ccs = new ControlColorScale(
				    this,ControlColorScale.HORIZONTAL);
      ccs.setTitle(title);
      south.add( ccs, "Center" );
      south_height += 75; // add 75 to south panel if colorscale.
      south.setPreferredSize( new Dimension( 0, south_height) );
    }
    else
    {
      south = new JPanel();    
      south.setPreferredSize(new Dimension( 0, south_height ) );
    }
 
    JPanel west = new JPanel();
    west.setPreferredSize(new Dimension( west_width, 0 ) );
    
    //Construct the background JPanel

    background.add(ijp, "Center");
    background.add(north, "North");
    background.add(west, "West");
    background.add(south, "South");
    background.add(east, "East" );
    buildAspectImage();
  }
  
 /*
  * This method constructs the controls required by the ImageViewComponent
  */
  private void buildViewControls()
  {
    // Note: If controls are added here, the size of the array controls[]
    // must be incremented.
    // IT IS RECOMMENDED THAT THE PANVIEWCONTROL REMAIN THE LAST CONTROL,
    // Adding a spacer panel to "crunch" controls may result in the
    // PanViewControl getting drawn over any latter controls.
    controls = new ViewControl[10];
    // Control that adjusts the image intensity
    controls[0] = new ControlSlider();
    controls[0].setTitle(INTENSITY_SLIDER_NAME);
    ((ControlSlider)controls[0]).setValue((float)logscale);		  
    controls[0].addActionListener( new ControlListener() );
    // Control that displays uncalibrated color scale
    controls[1] = new ControlColorScale(colorscale, isTwoSided );
    controls[1].setTitle(COLOR_SCALE_NAME);
    // Control that displays current pointed-at.
    String[] cursorlabels = {"X","Y"};
    controls[2] = new CursorOutputControl(cursorlabels);
    controls[2].setTitle(CURSOR_READOUT_NAME);
    // Control that turns marker overlay on/off
    controls[3] = new ControlCheckboxButton(true);
    controls[3].setTitle(MARKER_OVERLAY_NAME);
    controls[3].addActionListener( new ControlListener() );
    // Control that turns axis overlay on/off
    controls[4] = new ControlCheckboxButton(true);
    controls[4].setTitle(AXIS_OVERLAY_NAME);
    controls[4].addActionListener( new ControlListener() );
    // Control that turns selection overlay on/off
    controls[5] = new ControlCheckboxButton();  // initially unchecked
    controls[5].setTitle(SELECTION_OVERLAY_NAME);
    controls[5].addActionListener( new ControlListener() );
    // Control that turns annotation overlay on/off
    controls[6] = new ControlCheckboxButton();  // initially unchecked
    controls[6].setTitle(ANNOTATION_OVERLAY_NAME);
    controls[6].addActionListener( new ControlListener() );
    //  Control that selects a certain selector
    controls[7] = new LabelCombobox(SELECTOR_NAMES,
      ((SelectionOverlay)transparencies.elementAt(1)).getAllNames());
    controls[7].addActionListener( new ControlListener() );
    //  Control that adds a new selector
    controls[8] = new ButtonControl(ADD_SELECTION);
    controls[8].addActionListener( new ControlListener() );
    // Control that displays a thumbnail of the image
    controls[9] = new PanViewControl(ijp);
    controls[9].setTitle(PAN_NAME);
    controls[9].addActionListener( new ControlListener() ); 
  }
  
 /*
  * This method constructs the menu items required by the ImageViewComponent
  */   
  private void buildViewMenuItems()
  {
    Vector colorscale = new Vector();
    Vector position = new Vector();
    Vector choices = new Vector();
    Vector cs_listener = new Vector(); 
    colorscale.add("Color Scale");
    cs_listener.add( new ColorListener() );
    colorscale.add(choices);
     choices.add("Scales");
      cs_listener.add( new ColorListener() );
      choices.add(IndexColorMaker.HEATED_OBJECT_SCALE);
      choices.add(IndexColorMaker.HEATED_OBJECT_SCALE_2);
      choices.add(IndexColorMaker.GRAY_SCALE);
      choices.add(IndexColorMaker.NEGATIVE_GRAY_SCALE);
      choices.add(IndexColorMaker.GREEN_YELLOW_SCALE);
      choices.add(IndexColorMaker.RAINBOW_SCALE);
      choices.add(IndexColorMaker.OPTIMAL_SCALE);
      choices.add(IndexColorMaker.MULTI_SCALE);
      choices.add(IndexColorMaker.SPECTRUM_SCALE);
    colorscale.add(position);
     position.add("Display Position");
      cs_listener.add( new ColorListener() );
      position.add("Control Panel");
      position.add("Below Image (calibrated)");
      position.add("Right of Image (calibrated)");
      position.add("None");
    
    menus = new ViewMenuItem[3];
    JMenuItem scalemenu = MenuItemMaker.makeMenuItem( colorscale,cs_listener );
    menus[0] = new ViewMenuItem( ViewMenuItem.PUT_IN_OPTIONS, scalemenu ); 
    
    JCheckBoxMenuItem do_preserve_aspect = new JCheckBoxMenuItem(
                                             "Preserve Image Aspect Ratio");
    do_preserve_aspect.addActionListener( new AspectListener() );
    menus[1] = new ViewMenuItem( ViewMenuItem.PUT_IN_OPTIONS,
                                 do_preserve_aspect );
    JMenuItem helpmenu = MenuItemMaker.getOverlayMenu( new HelpListener() );
    menus[2] = new ViewMenuItem(ViewMenuItem.PUT_IN_HELP, helpmenu );
  }
  
 /*
  * This method controls the aspect ratio of the imagejpanel by setting the
  * preferred size of the north, south, east, and west panels. This method
  * will only do something if preserveAspectRatio(true) is called.
  */ 
  private void buildAspectImage()
  {
    // Make sure background panel has been initialized. It should have at least
    // five components: a center, north, south, east, and west.
    if( background.getComponentCount() < 5 )
      return;
    // temporary dimension values, don't want to change originals.
    int n_h = north_height;
    int e_w = east_width;
    int s_h = south_height;
    int w_w = west_width;
    // Only do if preserve_ratio flag was set to true
    if( preserve_ratio )
    {
      // the actual desired size of the center panel.
      int center_height = 0;
      int center_width  = 0;
      Dimension dim = getDisplayPanel().getSize();
      // these are the maximum size that the center panel could be.
      int max_height = dim.height - (n_h + s_h);
      int max_width = dim.width - (e_w + w_w);
      // calculate ratio of (rows/columns) or (y/x)
      int image_row_min = ijp.ImageRow_of_WC_y(getLocalCoordBounds().getY1());
      int image_row_max = ijp.ImageRow_of_WC_y(getLocalCoordBounds().getY2());
      int image_col_min = ijp.ImageCol_of_WC_x(getLocalCoordBounds().getX1());
      int image_col_max = ijp.ImageCol_of_WC_x(getLocalCoordBounds().getX2());
      int x = image_col_max - image_col_min;
      int y = image_row_max - image_row_min;
      //System.out.println("y/x: " + y + "/" + x);
      float ratio = Math.abs( (float)y/(float)x );  // rise over run
      // height is greater than width
      if( ratio > 1 )
      {
        center_height = max_height;
        center_width  = Math.round(((float)center_height)/ratio);
	// without a minimum restriction, the image will disappear.
	if( center_width < 10 )
	  center_width = 10;
      }
      else
      {
        center_width  = max_width;
        center_height = Math.round(((float)center_width)*ratio);
	// without a minimum restriction, the image will disappear.
	if( center_height < 10 )
	  center_height = 10;
      }
      // make sure component dimensions are below maximum range
      if( center_width > max_width )
      {
        center_width  = max_width;
        center_height = Math.round(((float)center_width)*ratio);
      }
      else if( center_height > max_height )
      {
        center_height = max_height;
        center_width  = Math.round(((float)center_height)/ratio);
      }
      // Reset sizes of north, east, south, and west panel to consequently
      // resize the center panel.
      int fill_width  = dim.width - ( center_width + e_w + w_w );
      int fill_height = dim.height - ( center_height + n_h + s_h );
      // since dealing with integers, if value is odd, add left over one to
      // west or north.
      if( fill_width % 2 == 1 )  // if fill_width is odd, put extra in w_w
        w_w += 1;
      if( fill_height % 2 == 1 ) // if fill_height is odd, put extra in n_h
        n_h += 1;
      n_h += fill_height/2;  // add integer value of half to f_h
      e_w += fill_width/2;   // add integer value of half to f_w
      s_h += fill_height/2;  // add integer value of half to f_h
      w_w += fill_width/2;   // add integer value of half to f_w 
    }
    // north
    ((JPanel)background.getComponent(1)).setPreferredSize( 
    				new Dimension( 0, n_h ) );    
    // west
    ((JPanel)background.getComponent(2)).setPreferredSize(
    				new Dimension( w_w, 0 ) );
    // south
    ((JPanel)background.getComponent(3)).setPreferredSize(
    				new Dimension( 0, s_h ) );
    // east
    ((JPanel)background.getComponent(4)).setPreferredSize(
    				new Dimension( e_w, 0 ) );
    
    //System.out.println("Dim: [" + center_width + "," + center_height + "]");
    background.invalidate();
    background.validate();
    // reset the center bounds and update the overlays.
    regioninfo = new Rectangle( ijp.getLocation(), ijp.getSize() );
    // this is needed to properly draw the axes.
    paintComponents();
  }
  
 //***************************Assistance Classes******************************
 /*
  * ComponentAltered monitors if the imagejpanel has been resized. If so,
  * the regioninfo is updated.
  */
  private class ComponentAltered extends ComponentAdapter
  {
    public void componentResized( ComponentEvent e )
    {
      // If the original data passed in was null, do nothing.
      if( null_data )
        return;
      //System.out.println("Component Resized");
      Component center = e.getComponent();
      regioninfo = new Rectangle( center.getLocation(), center.getSize() );
      /*
      System.out.println("Location = " + center.getLocation() );
      System.out.println("Size = " + center.getSize() );
      System.out.println("class is " + center.getClass() );  
      */
    }
  }

 /*
  * ImageListener monitors if the imagejpanel has sent any messages.
  * If so, process the message and relay it to the viewer.
  */
  private class ImageListener implements ActionListener
  {
    public void actionPerformed( ActionEvent ae )
    {
      // If the original data passed in was null, do nothing.
      if( null_data )
        return;
      String message = ae.getActionCommand();

      if (message == CoordJPanel.CURSOR_MOVED)
      {
        // If calibrated color scales are included, this will cause the
	// current point to be traced by those color scales.
        if( addColorControlSouth )
	{
	  JPanel south = (JPanel)background.getComponent(3);
	  ControlColorScale cs = (ControlColorScale)south.getComponent(1);
	  cs.setMarker( ijp.ImageValue_at_Cursor() );
	}
        if( addColorControlEast )
	{
	  ControlColorScale east = 
	           (ControlColorScale)background.getComponent(4);
	  east.setMarker( ijp.ImageValue_at_Cursor() );
	}
	// update cursor readout when pointed-at is changed.
	((CursorOutputControl)controls[2]).setValue(0,getPointedAt().x);
	((CursorOutputControl)controls[2]).setValue(1,getPointedAt().y);
	// Do not send out message if zooming in.
	if( !ijp.isDoingBox() )
	  sendMessage(POINTED_AT_CHANGED);
      }
      else if (message == CoordJPanel.ZOOM_IN)
      {
	ImageJPanel2 center = (ImageJPanel2)ae.getSource();
	((PanViewControl)controls[9]).setGlobalBounds(getGlobalCoordBounds());
	((PanViewControl)controls[9]).setLocalBounds(getLocalCoordBounds());
        buildAspectImage();
	paintComponents();
      }
      else if (message == CoordJPanel.RESET_ZOOM)
      {
	ImageJPanel2 center = (ImageJPanel2)ae.getSource();
	((PanViewControl)controls[9]).setGlobalBounds(getGlobalCoordBounds());
	((PanViewControl)controls[9]).setLocalBounds(getLocalCoordBounds());
        buildAspectImage();
	paintComponents();
      }	 
    } 
  }
  
 /*
  * ControlListener moniters activities of all controls 
  * of the ImageViewComponent.
  */
  private class ControlListener implements ActionListener
  { 
    public void actionPerformed( ActionEvent ae )
    {
      // If the original data passed in was null, do nothing.
      if( null_data )
        return;
      String message = ae.getActionCommand();
        		   // set image log scale when slider stops moving
      if ( message == ControlSlider.SLIDER_CHANGED )
      {
        ControlSlider control = (ControlSlider)ae.getSource();
        logscale = control.getValue();
        ijp.changeLogScale( logscale, true );
	((PanViewControl)controls[9]).repaint();
	// Causes any calibrated ControlColorScale to be updated
	// with slider movements.
	sendMessage(COLORSCALE_CHANGED);
      } 
      else if ( message == ControlCheckboxButton.CHECKBOX_CHANGED )
      { 
        int bpsize = big_picture.getComponentCount();
        
        if( ae.getSource() instanceof ControlCheckboxButton )
        {
          ControlCheckboxButton control = 
                		      (ControlCheckboxButton)ae.getSource();
          if( control.getTitle().equals(MARKER_OVERLAY_NAME) )
          {
            // if control is unchecked, don't show the overlay.
	    if( !control.isSelected() )
            {
              ((MarkerOverlay)transparencies.elementAt(3)).setVisible(false);
            }
            else
            {
              ((MarkerOverlay)transparencies.elementAt(3)).setVisible(true);
            }
          }// end of if( axis overlay control ) 
          // if this control turns on/off the axis overlay...
          else if( control.getTitle().equals(AXIS_OVERLAY_NAME) )
          {	
            JPanel back = (JPanel)big_picture.getComponent( bpsize - 1 );
            if( !control.isSelected() )
            {						     // axis overlay
              ((AxisOverlay2D)transparencies.elementAt(2)).setVisible(false);
              back.getComponent(1).setVisible(false);	     // north
              back.getComponent(2).setVisible(false);	     // west
              back.getComponent(3).setVisible(false);	     // south
              back.getComponent(4).setVisible(false);	     // east
              //System.out.println("visible..." + 
               //((AxisOverlay2D)transparencies.elementAt(2)).isVisible() );
            }
            else
            {		   
              back.getComponent(1).setVisible(true);
              back.getComponent(2).setVisible(true);
              back.getComponent(3).setVisible(true);
              back.getComponent(4).setVisible(true);
              ((AxisOverlay2D)transparencies.elementAt(2)).setVisible(true);
            }
          }// end of if( axis overlay control ) 
          else if( control.getTitle().equals(ANNOTATION_OVERLAY_NAME) )
          {
            AnnotationOverlay note = (AnnotationOverlay)
                	             transparencies.elementAt(0); 
            if( !control.isSelected() )
            {
              note.setVisible(false);
            }
            else
            {
              note.setVisible(true);
              note.getFocus();
            }		  
          }
          else if( control.getTitle().equals(SELECTION_OVERLAY_NAME) )
          {
            // if this control turns on/off the selection overlay...
            SelectionOverlay select = (SelectionOverlay)
                	              transparencies.elementAt(1); 
            if( !control.isSelected() )
            {
              select.setVisible(false);
            }
            else
            {
              select.setVisible(true); 
              select.getFocus();
            }
          }
        }		
      } // end if checkbox
      else if( message.equals( ControlCheckboxButton.BUTTON_PRESSED ) )
      {
        if( ae.getSource() instanceof ControlCheckboxButton )
        {
          ControlCheckboxButton ccb = (ControlCheckboxButton)ae.getSource();
          if( ccb.getTitle().equals(AXIS_OVERLAY_NAME) )
          {
            AxisOverlay2D axis = (AxisOverlay2D)transparencies.elementAt(2);
            axis.editGridLines();
          }
          else if( ccb.getTitle().equals(ANNOTATION_OVERLAY_NAME) )
          {
            AnnotationOverlay note = (AnnotationOverlay)
	                             transparencies.elementAt(0); 
            note.editAnnotation();
            note.getFocus();
          }
          else if( ccb.getTitle().equals(SELECTION_OVERLAY_NAME) )
          {
            SelectionOverlay select = (SelectionOverlay)
			              transparencies.elementAt(1); 
            select.editSelection();
            select.getFocus();
          }
          else if( ccb.getTitle().equals(MARKER_OVERLAY_NAME) )
          {
            MarkerOverlay mark = (MarkerOverlay)
			              transparencies.elementAt(3); 
            mark.editMarker();
          }
        }	
      }
      // This message is sent by the pan view control when the viewable
      // subregion changes.
      else if( message.equals( PanViewControl.BOUNDS_CHANGED ) ||
               message.equals( PanViewControl.BOUNDS_MOVED ) ||
	       message.equals( PanViewControl.BOUNDS_RESIZED ) )
      {
        if( ae.getSource() instanceof PanViewControl )
        {
          PanViewControl pvc = (PanViewControl)ae.getSource();
          // since the pan view control has a CoordJPanel in it with the
          // same bounds, set its local bounds to the image local bounds.
          ijp.setLocalWorldCoords( pvc.getLocalBounds() );
          // this method is only here to repaint the image
          ijp.changeLogScale( logscale, true );
	  buildAspectImage();
        }
      }
      //Message from the list of selectors
      else if( message.equals( LabelCombobox.COMBOBOX_CHANGED ) )
      {
        ((SelectionOverlay)transparencies.elementAt(1)).showEditor(
            (String)((LabelCombobox)ae.getSource()).getSelectedItem(), true);
      }
      //Message from the add new selector button.
      else if( message.equals( ButtonControl.BUTTON_PRESSED ) )
      {
//      popup to ask for name of selector. 
        String selName = JOptionPane.showInputDialog(
            "Enter name of selector");
        if (selName != null)
        {
          int itemIndex = ((LabelCombobox)controls[7]).addItem(selName);
          ((LabelCombobox)controls[7]).setSelectedIndex(itemIndex);
        }
      }
      //repaints overlays accurately
      returnFocus();
      paintComponents(); 
    }
  } 

 /*
  * This class relays the message sent out by the ColorScaleMenu
  */  
  private class ColorListener implements ActionListener
  {
    public void actionPerformed( ActionEvent ae )
    {
      // If the original data passed in was null, do nothing.
      if( null_data )
        return;
      String message = ae.getActionCommand();
      // these determine which color scale is to be viewed.
      if( message.equals("Control Panel") )
      {
	((ControlColorScale)controls[1]).setVisible(true);
        addColorControlEast = false;
        addColorControlSouth = false;
	addColorControl = true;
        buildViewComponent();
        
        // this control turns on/off the axis overlay...
        ControlCheckboxButton control = (ControlCheckboxButton)controls[4];
	int bpsize = big_picture.getComponentCount();
        JPanel back = (JPanel)big_picture.getComponent( bpsize - 1 );
	if( !control.isSelected() )
        {						 // axis overlay
         ((AxisOverlay2D)transparencies.elementAt(2)).setVisible(false);
          back.getComponent(1).setVisible(false);	 // north
          back.getComponent(2).setVisible(false);	 // west
          back.getComponent(3).setVisible(false);	 // south
          back.getComponent(4).setVisible(false);	 // east
        }
      }
      else if( message.equals("Below Image (calibrated)") )
      {
	((ControlColorScale)controls[1]).setVisible(false);
        addColorControlEast = false;
        addColorControlSouth = true;
	addColorControl = false;
        buildViewComponent();
        ((ControlCheckboxButton)controls[4]).setSelected(true);
        ((OverlayJPanel)transparencies.elementAt(2)).setVisible(true);
      }
      else if( message.equals("Right of Image (calibrated)") )
      {
	((ControlColorScale)controls[1]).setVisible(false);
        addColorControlEast = true;
        addColorControlSouth = false;
	addColorControl = false;
        buildViewComponent();
        ((ControlCheckboxButton)controls[4]).setSelected(true);
        ((OverlayJPanel)transparencies.elementAt(2)).setVisible(true);
      }
      else if( message.equals("None") )
      {
	((ControlColorScale)controls[1]).setVisible(false);
        addColorControlEast = false;
        addColorControlSouth = false;
	addColorControl = false;
        buildViewComponent();
        ((ControlCheckboxButton)controls[4]).setSelected(true);
        ((OverlayJPanel)transparencies.elementAt(2)).setVisible(true);
      }
      // else change color scale.
      else
      {
	setColorScale(message);
   ijp.setNamedColorModel( colorscale, isTwoSided, false);
	return;
      }
  //###    background.validate();
      paintComponents();
    }
  }

 /*
  * This class relays the message sent out by the HelpMenu
  */  
  private class HelpListener implements ActionListener
  {
     public void actionPerformed( ActionEvent ae )
     {
	String button = ae.getActionCommand();
        if( button.equals("Annotation") )
        {
           //System.out.println("AnnotationHelpMenu");
           AnnotationOverlay.help();
        }
        else if( button.equals("Axis") )
        {
           //System.out.println("AxisHelpMenu");
           AxisOverlay2D.help();
        }
        else if( button.equals("Selection") )
        {
           //System.out.println("SelectionHelpMenu");
           SelectionOverlay.help();  
        }
        else if( button.equals("Marker") )
        {
           //System.out.println("SelectionHelpMenu");
           MarkerOverlay.help();  
        }	
     }
  } 

 /*
  * This class relays messages from the SelectionOverlay whenever a
  * a selected region is added or removed.
  */  
  private class SelectedRegionListener implements ActionListener, Serializable
  {
    public void actionPerformed( ActionEvent ae )
    {
      // If the original data passed in was null, do nothing.
      if( null_data )
        return;
      sendMessage( SELECTED_CHANGED );
    }
  }

 /*
  * This class listens to the "Preserve Image Aspect Ratio" menu item.
  * If checked, preserve aspect ratio, if not, don't.
  */  
  private class AspectListener implements ActionListener
  {
    public void actionPerformed( ActionEvent ae )
    {
      JCheckBoxMenuItem preserve = (JCheckBoxMenuItem)ae.getSource();
      preserveAspectRatio(preserve.isSelected());
    }
  }
  
 /*
  * PreserveAspect monitors if the display panel has been resized. If so,
  * maintain the aspect ratio if the flag has been set.
  */
  private class PreserveAspect extends ComponentAdapter
  {
    public void componentResized( ComponentEvent e )
    {
      // If the original data passed in was null, do nothing.
      if( null_data )
        return;
      buildAspectImage();
    }
  }
     
 /*
  * MAIN - Basic main program to test an ImageViewComponent object
  */
  public static void main( String args[] ) 
  {
    int n_cols = 250;
    int n_rows = 250;
    //Make a sample 2D array

    float arr[][] = new float[ n_rows ][ n_cols ];
    for(int i = 0; i < n_rows; i++)
    {
       for(int j = 0; j < n_cols; j++)
       {
          if ( i % 25 == 0 )
             arr[i][j] = i*n_cols;
          else if ( j % 25 == 0 )
             arr[i][j] = j*n_rows;
          else
             arr[i][j] = i*j;
       }
    }

    //Make a sample 2D array
    IVirtualArray2D va2D = new VirtualArray2D( arr ); 
    va2D.setAxisInfo( AxisInfo.X_AXIS, 0f, 10000f, 
        	       "TestX","TestUnits", AxisInfo.LINEAR );
    va2D.setAxisInfo( AxisInfo.Y_AXIS, 0f, 1500f, 
        		"TestY","TestYUnits", AxisInfo.TRU_LOG );
    va2D.setTitle("Main Test");
    
    JFrame frame = new JFrame("ImageViewComponent Test");
    frame.setBounds(0,0,600,300);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
    ImageViewComponent ivc = new ImageViewComponent(va2D);
    frame.getContentPane().add(ivc.getDisplayPanel());
    
    WindowShower.show(frame);
    
    JFrame ctrlframe = new JFrame("IVC Controls");
    ctrlframe.setBounds(0,0,300,400);
    ctrlframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
    //ivc.dataChanged( null );
    ViewControl[] my_controls = ivc.getControls();
    ctrlframe.getContentPane().setLayout( new javax.swing.BoxLayout(
                                       ctrlframe.getContentPane(),
                                       javax.swing.BoxLayout.Y_AXIS) );
    for( int i = 0; i < my_controls.length; i++ )
      ctrlframe.getContentPane().add( my_controls[i] );
    if( my_controls.length > 0 )
    {
      WindowShower.show(ctrlframe);
    }
  }
}
