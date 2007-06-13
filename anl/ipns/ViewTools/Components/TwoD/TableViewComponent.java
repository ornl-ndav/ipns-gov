/*
 * File: TableViewComponent.java
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
 *  Revision 1.13  2007/06/13 15:30:23  rmikk
 *  The PanViewController is now notified when the data is changed
 *
 *  Revision 1.12  2007/06/12 20:39:42  rmikk
 *  Set POINTED_AT_CHANGED to agree with IObserver's POINTED_AT_CHANGED
 *
 *  Revision 1.11  2007/06/05 20:06:41  rmikk
 *  The row labels and column labels now correspond to the values given in the
 *     corresponding axisInfo
 *
 *  Revision 1.10  2007/03/16 17:01:39  dennis
 *  Added method getWorldToArrayTransform().
 *
 *  Revision 1.9  2005/05/25 20:28:36  dennis
 *  Now calls convenience method WindowShower.show() to show
 *  the window, instead of instantiating a WindowShower object
 *  and adding it to the event queue.
 *
 *  Revision 1.8  2005/05/06 21:13:25  millermi
 *  - Added methods for converting between coordinate systems.
 *  - get/setPointedAt() parameter is now in terms of world coordinates
 *    instead of (column,row) coordinates.
 *
 *  Revision 1.7  2005/03/17 01:28:48  millermi
 *  - Changed ImageJPanel to ImageJPanel2. When the PanViewControl was
 *    switched over to the ImageJPanel2, passing it an ImageJPanel caused
 *    it not to appear.
 *  - Removed public static final Strings for POINTED_AT_CHANGED and
 *    SELECTED_CHANGED since these are defined by IViewComponent.
 *  - Removed "Predecimal" option from FormatControl since it served
 *    very little useful purpose.
 *  - TableViewComponent now remains in a stable state if null is passed
 *    in for data. Previously, errors occurred.
 *
 *  Revision 1.6  2004/12/05 05:46:15  millermi
 *  - Fixed Eclipse warnings.
 *
 *  Revision 1.5  2004/08/17 20:58:38  millermi
 *  - Added menu item under Help menu for table commands.
 *
 *  Revision 1.4  2004/08/17 01:23:54  millermi
 *  - Now implements IPreserveState.
 *  - Added code to repaint TVC after setObjectState() is called.
 *  - getObjectState() no longer returns new ObjectState().
 *
 *  Revision 1.3  2004/08/13 02:59:46  millermi
 *  - Fixed javadocs.
 *
 *  Revision 1.2  2004/08/06 18:51:24  millermi
 *  - Added colorscale menu item.
 *  - Added THUMBNAIL_COLOR_SCALE ObjectState key to save the colorscale
 *    of the PanViewControl.
 *
 *  Revision 1.1  2004/08/04 18:56:27  millermi
 *  - Initial Version - View component that displays data as a table.
 *
 */
 
package gov.anl.ipns.ViewTools.Components.TwoD;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import gov.anl.ipns.Util.Numeric.floatPoint2D;
import gov.anl.ipns.Util.Sys.WindowShower;
import gov.anl.ipns.ViewTools.Panels.Image.ImageJPanel2;
import gov.anl.ipns.ViewTools.Panels.Image.IndexColorMaker;
import gov.anl.ipns.ViewTools.Panels.Table.TableJPanel;
import gov.anl.ipns.ViewTools.Panels.Table.TableModelMaker;
import gov.anl.ipns.ViewTools.Panels.Transforms.CoordBounds;
import gov.anl.ipns.ViewTools.Panels.Transforms.CoordTransform;
import gov.anl.ipns.ViewTools.Components.Menu.MenuItemMaker;
import gov.anl.ipns.ViewTools.Components.Menu.ViewMenuItem;
import gov.anl.ipns.ViewTools.Components.ViewControls.*;
import gov.anl.ipns.ViewTools.Components.Region.*;
import gov.anl.ipns.ViewTools.Components.AxisInfo;
import gov.anl.ipns.ViewTools.Components.IPreserveState;
import gov.anl.ipns.ViewTools.Components.ObjectState;
import gov.anl.ipns.ViewTools.Components.IVirtualArray2D;
import gov.anl.ipns.ViewTools.Components.VirtualArray2D;

/**
 * The TableViewComponent is used to display data in a table. Currently, this
 * the tables will only display rectangular arrays, ragged arrays must be
 * put into a rectangular array.
 */
public class TableViewComponent implements IViewComponent2D, IPreserveState
{
 /**
  * "Thumbnail Colorscale Changed" - This messaging String is sent out to all
  * listeners when the thumbnail colorscale has been changed.
  */
  public static final String THUMBNAIL_COLOR_SCALE_CHANGED = ("Thumbnail "+
                                                          "Colorscale Changed");
 
 // **************************** ObjectState Keys ****************************
 /**
  * "TableJPanel" - This constant String key references the ObjectState
  * stored by the TableJPanel. The value this key references is an ObjectState.
  * @see gov.anl.ipns.ViewTools.Panels.Table.TableJPanel
  */
  public static final String TABLEJPANEL = "TableJPanel";
  
 /**
  * "Format Control" - This constant String key references the ObjectState
  * stored by the FormatControl. The value this key references is an
  * ObjectState.
  * @see gov.anl.ipns.ViewTools.Components.ViewControls.FormatControl
  */ 
  public static final String FORMAT_CONTROL = "Format Control";
  
 /**
  * "Panning Tool" - use this static String to verify that the title of
  * the ViewControl returned is that of the PanViewControl. The value this
  * key references is of type String.
  */
  public static final String PAN_NAME = "Panning Tool";
  
 /**
  * "Thumbnail Colorscale" - This String key references the colorscale of
  * the thumbnail image for the PanViewControl. The value this key
  * references is of type String.
  */
  public static final String THUMBNAIL_COLOR_SCALE = "Thumbnail ColorScale";
  
  private TableJPanel tjp;
  private Vector Listeners;
  private ViewControl[] controls;
  private ViewMenuItem[] menus;
  private IVirtualArray2D varray;
  private ImageJPanel2 ijp; // For use exclusively by the PanViewControl.
  private String colorscale; // Colorscale of the ijp.
  private boolean setVisibleLocationCalled = false; // This is needed to ignore
                                           // messages sent out by the tjp when
			                   // setVisibleLocation() is called.
  private JFrame helper = null;
  
 /**
  * Constructor - Data is passed in the form of an IVirtualArray2D to be display
  * by this component in a table. To view 1D or 3D data with the
  * TableViewComponent, define the data as an IVirtualArray2D.
  * <BR><BR><I>
  * Note: All IVirtualArray2D data is assumed to be rectangular. If ragged
  * 1D data is put into an IVirtualArray2D, be sure to "rectangularize" the
  * data. </I> 
  *
  *  @param  array Array of data wrapped in an IVirtualArray2D.
  */
  public TableViewComponent( IVirtualArray2D array )
  {
    // Initialize the ImageJPanel2 so it may be used by the PanViewControl.
    ijp = new ImageJPanel2();
    colorscale = IndexColorMaker.HEATED_OBJECT_SCALE_2;
    Listeners = new Vector();
    dataChanged(array);
    buildControls();
    buildMenu();
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
    Object temp = new_state.get(TABLEJPANEL);
    if( temp != null )
    {
      tjp.setObjectState((ObjectState)temp);
      redraw = true;  
    }
    
    temp = new_state.get(FORMAT_CONTROL);
    if( temp != null )
    {
      controls[0].setObjectState((ObjectState)temp);
      redraw = true;  
    }
    
    temp = new_state.get(THUMBNAIL_COLOR_SCALE);
    if( temp != null )
    {
      setThumbnailColorScale((String)temp);
      redraw = true;  
    }
    
    if( redraw )
    {
      tjp.repaint();
      controls[0].validate();
      controls[0].repaint();
    }
  }
  
 /**
  * This method will get the current values of the state variables for this
  * object. These variables will be wrapped in an ObjectState.
  *
  *  @param  is_default True if default state, use static variable.
  *  @return if true, the selective default state, else the state for with
  *          all possible saved values.
  */ 
  public ObjectState getObjectState( boolean is_default )
  {
    ObjectState state = new ObjectState();
    state.insert( TABLEJPANEL, tjp.getObjectState(is_default) );
    state.insert( FORMAT_CONTROL, controls[0].getObjectState(is_default) );
    state.insert( THUMBNAIL_COLOR_SCALE, getThumbnailColorScale() );
    return state;
  }
  
 /**
  * Add a listener to this view component. A listener will be notified
  * when a selected point or region changes on the view component.
  *
  *  @param  act_listener
  */
  public void addActionListener( ActionListener act_listener )
  {	     
    for ( int i = 0; i < Listeners.size(); i++ )    // don't add it if it's
      if ( Listeners.elementAt(i).equals( act_listener ) ) // already there
        return;

    Listeners.add( act_listener ); // Otherwise add act_listener
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
  * Retrieve the jpanel that this component constructs.
  *
  *  @return The JPanel containing the GUI TableViewComponent.
  */
  public JPanel getDisplayPanel()
  {
    return tjp;
  }
 
 /**
  * Returns all of the controls needed by this view component
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
  * This method is a notification to the view component that the selected
  * point (cell) has changed. This assumes that fpt is in world coordinates.
  *
  *  @param  fpt - current cell specified in (x,y) world coordinates.
  */ 
  public void setPointedAt( floatPoint2D fpt )
  {
    // If null, do nothing.
    if( fpt == null )
      return;
    tjp.setPointedAtCell( getColumnRowAtWorldCoords(fpt) );
  }
 
 /**
  * Get the current pointed-at cell. The pointed-at cell has a highlighted
  * border. The current cell is specified by a floatPoint2D in world
  * coordinates.
  *
  *  @return The current point in world coordinates.
  */ 
  public floatPoint2D getPointedAt()
  {
    return getWorldCoordsAtColumnRow(tjp.getPointedAtCell());
  }

 /**
  * Given an array of points, a selection overlay can be created.
  *
  *  @param  rgn - array of regions
  */ 
  public void setSelectedRegions( Region[] rgn )
  {
    tjp.setSelectedRegions(TableJPanel.convertToTableRegions(rgn));
  }
 
 /**
  * Retrieve array of regions generated by the selection overlay. If none
  * of the TableRegions are deselected, a list of TableRegions is returned.
  * If a deselected TableRegion exists, a single PointRegion containing
  * a list of selected cells will be returned in the array.
  *
  *  @return The selected regions, either a list of TableRegions or a single
  *          PointRegion.
  */
  public Region[] getSelectedRegions()
  {
    // This can be more complex, if any unselected regions are included in
    // the list, return a point list.
    TableRegion[] table_regions = tjp.getSelectedRegions();
    int index = 0;
    while( index < table_regions.length && table_regions[index].isSelected() )
      index++;
    // If there are no unselected TableRegions, return the list of TableRegions.
    if( index == table_regions.length )
      return table_regions;
    // If an unselected TableRegion is included, return a single PointRegion
    // containing all of the selected cells.
    else
    {
      Region[] single_pt_region = new Region[1];
      single_pt_region[0] = tjp.getSelectedCells();
      return single_pt_region;
    }
  }
  
 /**
  * This method is invoked to notify the view component when the data
  * has changed within the same array.
  */ 
  public void dataChanged()
  {
    tjp.repaint();
    if( controls != null && controls.length >=1){
       ((PanViewControl)controls[1]).makeNewPanImage = true;
    }
  }
  
 /**
  * This method is invoked to notify the view component when a new set of
  * data needs to be displayed. 
  *
  *  @param  array - virtual array of data
  */ 
  public void dataChanged(IVirtualArray2D array)
  {
    varray = array;
    // Make sure data is valid.
    String[] rowNames = null;
    if( varray != null )
    {
      //Make ijp correspond to the data in f_array
      ijp.setData(varray, true);
      AxisInfo xinfo = varray.getAxisInfo(AxisInfo.X_AXIS);
      AxisInfo yinfo = varray.getAxisInfo(AxisInfo.Y_AXIS);
    
      ijp.initializeWorldCoords( new CoordBounds( xinfo.getMin(),
    						  yinfo.getMax(),      
    						  xinfo.getMax(),
    						  yinfo.getMin() ) );
      float Delta = (yinfo.getMax()-yinfo.getMin())/varray.getNumRows();
      rowNames = new String[ varray.getNumRows()];
      float Val = yinfo.getMin()+Delta/2f;
      for( int i= 0; i< rowNames.length; i++){
         rowNames[i] = ""+Val;
         Val += Delta;
      }
         
    }
    tjp = null;
    tjp = new TableJPanel( TableModelMaker.getModel(varray, getColumnNames( varray )) );
    tjp.setColumnAlignment( SwingConstants.RIGHT );
    tjp.addActionListener( new TableListener() );
    if( rowNames != null)
       tjp.setRowLabels( rowNames);
    dataChanged();
  }
  
  private String[] getColumnNames( IVirtualArray2D varray){
     String[] Res = new String[ varray.getNumColumns()];
     
     AxisInfo x_axis = varray.getAxisInfo( AxisInfo.X_AXIS );
     float L = (x_axis.getMax() - x_axis.getMin() )/Res.length;
     float S = x_axis.getMin() + L/2f;
     for( int i=0; i< Res.length; i++){
        Res[i] =""+S;
        S+=L;
     }
     return Res;
  }
  
 /**
  * This method is called by the viewer to inform the view component
  * it is no longer needed. In turn, the view component closes all windows
  * created by it before closing.
  */
  public void kill()
  {
    if( helper != null )
      helper.dispose();
  }
  
 // ************************ Miscellaneous Methods ***************************
 
 /**
  * Get the colorscale of the thumbnail image representing the data in the
  * table.
  *
  *  @return String colorscale code as defined by the IndexColorMaker class.
  *  @see gov.anl.ipns.ViewTools.Panels.Image.IndexColorMaker
  */
  public String getThumbnailColorScale()
  {
    return colorscale;
  }
  
 /**
  * Set the colorscale of the thumbnail image representing the data in the
  * table.
  *
  *  @param  color_scale String code as defined by the IndexColorMaker class.
  *  @see gov.anl.ipns.ViewTools.Panels.Image.IndexColorMaker
  */
  public void setThumbnailColorScale( String color_scale )
  {
    // If data is null, no data to display.
    if( varray == null )
      return;
    colorscale = color_scale;
    boolean isTwoSided = false;
    if( ijp.getDataMin() < 0 )
      isTwoSided = true;
    ijp.setNamedColorModel( colorscale, isTwoSided, true );
    ((PanViewControl)controls[1]).repaint();
    sendMessage(THUMBNAIL_COLOR_SCALE_CHANGED);
  }
 
 /*
  * Here are methods to translate between coordinate systems.
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
    CoordBounds imagebounds = ijp.getImageCoords();
    CoordBounds wcbounds = ijp.getGlobalWorldCoords();
    CoordTransform image_to_wc = new CoordTransform( imagebounds, wcbounds );
    return new floatPoint2D( image_to_wc.MapXTo(col_row_pt.x),
                             image_to_wc.MapYTo(col_row_pt.y) );
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
  * Builds the view controls for this component.
  */
  private void buildControls()
  {
    // If no data to display, only build FormatControl.
    if( varray == null )
    {
      controls = new ViewControl[1];
    }
    else
      controls = new ViewControl[2];
    
    // ******* controls[0] *******
    // Build the combo boxes with format options.
    Vector options = new Vector();
    Object[] alignment = {"Alignment","Left","Center","Right"};/*
    Object[] predecimal = {"Predecimal Digits","Unlimited","0","1","2","3","4",
                           "5","6","7","8","9","10"};*/
    Object[] postdecimal = {"Postdecimal Digits","Unlimited","0","1","2","3",
                            "4","5","6","7","8","9","10"};
    options.add(alignment);
    //options.add(predecimal);
    options.add(postdecimal);
    controls[0] = new FormatControl(options);
    controls[0].addActionListener( new ControlListener() );
    controls[0].setTitle(FORMAT_CONTROL);
    ((FormatControl)controls[0]).setToolTipText(0,"Table Text Alignment");/*
    ((FormatControl)controls[0]).setToolTipText(1,"Number of digits before "+
                                                  "decimal.");*/
    ((FormatControl)controls[0]).setToolTipText(1,"Number of digits after "+
                                                  "decimal.");
    
    // If no data to display, do not build PanViewControl
    if( varray == null )
    {
      return;
    }
    
    // ******* controls[1] *******
    controls[1] = new PanViewControl(ijp);
    ((PanViewControl)controls[1]).setTitle(PAN_NAME);
    ((PanViewControl)controls[1]).setGlobalBounds(
         new CoordBounds(0,0,varray.getNumColumns()-1,varray.getNumRows()-1) );
    ((PanViewControl)controls[1]).setLocalBounds(
         new CoordBounds(0,0,varray.getNumColumns()-1,varray.getNumRows()-1) );
    ((PanViewControl)controls[1]).enableStretch(false);
    ((PanViewControl)controls[1]).addActionListener( new ControlListener() );
  }
  
 /*
  * Build the menu items for this component.
  */
  private void buildMenu()
  {
    menus = new ViewMenuItem[2];
    
    menus[0] = new ViewMenuItem( ViewMenuItem.PUT_IN_OPTIONS,
                 MenuItemMaker.getColorScaleMenu( new ColorChangedListener()) );
    
    Vector help_item = new Vector();
    help_item.add("Table");
    Vector help_listener = new Vector();
    help_listener.add( new HelpListener() );
    menus[1] = new ViewMenuItem( ViewMenuItem.PUT_IN_HELP,
                 MenuItemMaker.makeMenuItem( help_item, help_listener ) );
  }
  
 /*
  * This listener will listen to any table messages.
  */
  private class TableListener implements ActionListener
  {
    public void actionPerformed( ActionEvent ae )
    {
      String message = ae.getActionCommand();
      if( message.equals(TableJPanel.SELECTED_CHANGED) )
        sendMessage(SELECTED_CHANGED);
      else if( message.equals(TableJPanel.POINTED_AT_CHANGED) )
        sendMessage(TableJPanel.POINTED_AT_CHANGED);
      else if( message.equals(TableJPanel.VIEWPORT_CHANGED) )
      {
	// If varray was null, PanViewControl was never created, so do nothing.
	if( controls.length <= 1 )
	  return;
        // When the PanViewControl's viewport is moved, the setVisibleLocation()
	// is called, causing this message to be sent. If this is the case,
	// ignore the message.
        if( setVisibleLocationCalled )
	{
	  setVisibleLocationCalled = false;
	  return;
	}
        Rectangle viewport = tjp.getVisibleRectangle();
	// This code must be added since the width and height become negative
	// if the last row and/or column are entirely visible.
	if( viewport.width < 0 )
	  viewport.width = varray.getNumColumns() + viewport.width + 1;
	if( viewport.height < 0 )
	  viewport.height = varray.getNumRows() + viewport.height + 1;
	((PanViewControl)controls[1]).setLocalBounds(
	                   new CoordBounds( viewport.x, viewport.y,
			                    viewport.x+viewport.width,
					    viewport.y+viewport.height ) );
	((PanViewControl)controls[1]).repaint();
      }
    }
  }
  
 /*
  * This listens for messages sent out by the ViewControls of
  * this view component.
  */ 
  private class ControlListener implements ActionListener
  {
    public void actionPerformed( ActionEvent ae )
    {
      String message = ae.getActionCommand();
      if( message.equals(FormatControl.FORMAT_CHANGED) )
      {
        FormatControl fc = (FormatControl)ae.getSource();
        int changed_index = fc.getLastChangedIndex();
        int select_index = fc.getLastChangedSelectedIndex();
	
	// Since the first index is always a label for the combo box, do nothing
	// if this label is selected.
	if( select_index == 0 )
	  return;
	// Changes to alignment occurred.
	if( changed_index == 0 )
	{
	  if( select_index == 1 )
	  {
	    tjp.setColumnAlignment( SwingConstants.LEFT );
	    tjp.repaint();
	  }
	  else if( select_index == 2 )
	  {
	    tjp.setColumnAlignment( SwingConstants.CENTER );
	    tjp.repaint();
	  }
	  else if( select_index == 3 )
	  {
	    tjp.setColumnAlignment( SwingConstants.RIGHT );
	    tjp.repaint();
	  }
	}/* --- Removed predecimal option ---
	// Changes to predecimal digits displayed occurred.
	else if( changed_index == 1 )
	{
	  // Subtract two since two entries exist in list before numbers.
	  tjp.setNumberFormat( (select_index-2), (fc.getSelectedIndex(2)-2) );
	}*/
	// Changes to postdecimal digits displayed occurred.
	else if( changed_index == 1 )
	{
	  // Subtract two since two entries exist in list before numbers.
	  tjp.setNumberFormat( (fc.getSelectedIndex(1)-2), (select_index-2) );
	}
      } // end if( FormatControl )
      else if( message.equals(PanViewControl.BOUNDS_MOVED) )
      {
        CoordBounds bounds = ((PanViewControl)ae.getSource()).getLocalBounds();
	// Calling setVisibleLocation() will cause the VIEWPORT_CHANGED message
	// to be sent out. Ignore this message if it is sent out as a result
	// of calling this method.
	setVisibleLocationCalled = true;
	tjp.setVisibleLocation( new Point( (int)bounds.getX1(),
	                                   (int)bounds.getY1()) );
      }
    }
  } // End of controllistener
  
 /*
  * This listens for messages sent out by the ViewMenuItems of
  * this view component.
  */ 
  private class ColorChangedListener implements ActionListener
  {
    public void actionPerformed( ActionEvent ae )
    {
      setThumbnailColorScale( ae.getActionCommand() );
    }
  }
  
 /*
  * This listens for messages sent out by the ViewMenuItems of
  * this view component.
  */ 
  private class HelpListener implements ActionListener
  {
    public void actionPerformed( ActionEvent ae )
    {
      helper = TableJPanel.help();
      WindowShower.show(helper);
    }
  }
 
 /**
  * Test Program - use for testing only.
  *
  *  @param  args Command line arguments, these are ignored.
  */ 
  public static void main( String args[] )
  {
    JFrame frame = new JFrame("TableViewComponent Test");
    frame.setBounds(0,0,600,300);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
    int row = 50;
    int col = 30;
    float[][] values = new float[row][col];
    for( int i = 0; i < row; i++ )
    {
      for( int j = 0; j < col; j++ )
      {
        values[i][j] = (float)(i * j)*1.1f;
      }
    }
    VirtualArray2D iva = new VirtualArray2D(values);
    TableViewComponent tvc = new TableViewComponent( iva );
    // Make TableRegions to select/deselect cells.
    Region[] reg = new Region[4];
    floatPoint2D[] def_pts1 = new floatPoint2D[2];
    def_pts1[0] = new floatPoint2D(5f,4f);
    def_pts1[1] = new floatPoint2D(7f,11f);
    reg[0] = new BoxRegion( def_pts1 );
    
    floatPoint2D[] def_pts2 = new floatPoint2D[2];
    def_pts2[0] = new floatPoint2D(9f,2f);
    def_pts2[1] = new floatPoint2D(15f,5f);
    reg[1] = new BoxRegion( def_pts2 );
    
    floatPoint2D[] def_pts3 = new floatPoint2D[3];
    def_pts3[0] = new floatPoint2D(0,10f);
    def_pts3[1] = new floatPoint2D(4f,14f);
    def_pts3[2] = new floatPoint2D(2f,12f);
    reg[2] = new EllipseRegion( def_pts3 );
    
    floatPoint2D[] def_pts4 = new floatPoint2D[2];
    def_pts4[0] = new floatPoint2D(2f,12f);
    def_pts4[1] = new floatPoint2D(2f,12f);
    reg[3] = new TableRegion( def_pts4, false );
    
    //tvc.setThumbnailColorScale( IndexColorMaker.MULTI_SCALE );
    ObjectState state = tvc.getObjectState(IPreserveState.PROJECT);
    tvc.setThumbnailColorScale( IndexColorMaker.MULTI_SCALE );
    tvc.setObjectState(state);
    tvc.setSelectedRegions(reg);
    tvc.setSelectedRegions( tvc.getSelectedRegions() );
    tvc.setPointedAt(new floatPoint2D(.2f,.5f));
    System.out.println("Testing Pointed At (.2,.5): "+tvc.getPointedAt());
    frame.getContentPane().add(tvc.getDisplayPanel());
    WindowShower.show(frame);
    
    JFrame ctrlframe = new JFrame("TVC Controls");
    ctrlframe.setBounds(0,0,300,400);
    ctrlframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    ViewControl[] my_controls = tvc.getControls();
    ctrlframe.getContentPane().setLayout( new javax.swing.BoxLayout(
                                       ctrlframe.getContentPane(),
                                       javax.swing.BoxLayout.Y_AXIS) );
    for( int i = 0; i < my_controls.length; i++ )
      ctrlframe.getContentPane().add( my_controls[i] );
    if( my_controls.length > 0 )
      WindowShower.show(ctrlframe); 
  }
}
