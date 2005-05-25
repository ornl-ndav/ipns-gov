/*
 * File: AbstractComponentSwapper.java
 *
 * Copyright (C) 2005, Mike Miller
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
 *  Revision 1.3  2005/05/25 20:28:45  dennis
 *  Now calls convenience method WindowShower.show() to show
 *  the window, instead of instantiating a WindowShower object
 *  and adding it to the event queue.
 *
 *  Revision 1.2  2005/05/06 21:15:22  millermi
 *  - Modified linking of components due to change in TableViewComponent.
 *
 *  Revision 1.1  2005/03/28 05:54:04  millermi
 *  - Initial Version - This is a building block in the new viewer
 *    structure.
 *
 */
 package gov.anl.ipns.ViewTools.Layouts;
 
 import java.awt.event.ActionEvent;
 import java.awt.event.ActionListener;
 
 import gov.anl.ipns.Util.Numeric.floatPoint2D;
 import gov.anl.ipns.Util.Sys.SharedMessages;
 import gov.anl.ipns.ViewTools.Components.AxisInfo;
 import gov.anl.ipns.ViewTools.Components.IViewComponent;
 import gov.anl.ipns.ViewTools.Components.IVirtualArray;
 import gov.anl.ipns.ViewTools.Components.IVirtualArray2D;
 import gov.anl.ipns.ViewTools.Components.ObjectState;
 import gov.anl.ipns.ViewTools.Components.VirtualArray2D;
 import gov.anl.ipns.ViewTools.Components.TwoD.IViewComponent2D;
 import gov.anl.ipns.ViewTools.Components.TwoD.ImageViewComponent;
 import gov.anl.ipns.ViewTools.Components.TwoD.TableViewComponent;
 import gov.anl.ipns.ViewTools.Components.ViewControls.IColorScaleAddible;
 import gov.anl.ipns.ViewTools.UI.ActionValueEvent;
 
/**
 * This ComponentSwapper supports view components that accept IVirtualArray2Ds.
 * Currently, the view components that accept IVirtualArray2Ds are limited to
 * the TableViewComponent and the ImageViewComponent.
 */
 public class ComponentSwapper2D extends AbstractComponentSwapper
 {
   private boolean ignore_change = false;
   private IViewComponent2D current_component = null;
  /**
   * Default Contructor - Results in no display to be shown. Use provided
   * methods to develop a useful display.
   */
   public ComponentSwapper2D()
   {
     this(null);
   }
   
  /**
   * Contructor - Specify data to be displayed. Default display is as a TABLE.
   * Default display of controls is SHOW_ALL.
   *
   *  @param  va2D IVirtualArray2D to be displayed by this swapper.
   */
   public ComponentSwapper2D( IVirtualArray2D va2D )
   {
     this(va2D,TABLE,SHOW_ALL);
   }
   
  /**
   * Contructor - Specify data to be displayed. Default display is as a TABLE.
   * Default display of controls is SHOW_ALL.
   *
   *  @param  va2D IVirtualArray2D to be displayed by this swapper.
   */
   public ComponentSwapper2D( IVirtualArray2D va2D, ObjectState state )
   {
     this(va2D,(String)state.get(VISIBLE_VIEW),SHOW_ALL);
     setObjectState(state);
   }
   
  /**
   * Contructor - Specify data to be displayed, how it is to be displayed,
   * and how controls are to be displayed.
   *
   *  @param  va2D IVirtualArray2D to be displayed by this swapper.
   *  @param  view_type Select the IViewComponent2D to initially display the
   *                    data.
   *  @param  control_scheme Select the scheme for displaying controls.
   */
   public ComponentSwapper2D( IVirtualArray2D va2D, String view_type,
                              int control_scheme )
   {
     // Available views: TABLE, IMAGE
     super(new String[]{TABLE,IMAGE}, va2D, view_type );
     setVisibleControls(control_scheme);
     current_component = (IViewComponent2D)getViewComponent();
   }
   
  /**
   * Contructor - Specify data to be displayed, how it is to be displayed,
   * and how controls are to be displayed.
   *
   *  @param  va2D IVirtualArray2D to be displayed by this swapper.
   *  @param  view_type Select the IViewComponent2D to initially display the
   *                    data.
   *  @param  visible_controls Give a list of control indices to be made
   *                           visible upon creation of the ComponentSwapper.
   */
   public ComponentSwapper2D( IVirtualArray2D va2D, String view_type,
                              int[] visible_controls )
   {
     // Available views: TABLE, IMAGE
     super(new String[]{TABLE,IMAGE}, va2D, view_type );
     setVisibleControls(visible_controls);
     current_component = (IViewComponent2D)getViewComponent();
   }
  
  /*
   * Get the current state of the ComponentSwapper2D.
   *
   *  @param  is_default Is desired state for preferences (default=true) or 
   *		         a project (session=false) save? Use IPreserveState
   *		         variables for parameter.
   *  @return The current ObjectState of the ComponentSwapper2D.
   *
   public ObjectState getObjectState( boolean is_default )
   {
     ObjectState state = super.getObjectState(is_default);
     System.out.println("ComponentSwapper2D.getObjectState() unimplemented");
     return state;
   }
   
   public void setObjectState( ObjectState new_state )
   {
     // Do nothing if state is null.
     if( new_state == null )
       return;
     super.setObjectState(new_state);
     System.out.println("ComponentSwapper2D.setObjectState() unimplemented");
   }
   */
  
  /**
   * This method sets new data into the view components of this class. This
   * swapper will always display the first element in the array since these
   * view components do not support a 2D list.
   *
   *  @param  iva The data to be displayed by the swapper.
   */ 
   public void setData( IVirtualArray iva )
   {
     // Because this ComponentSwapper needs IVirtualArray2D, ensure data
     // is of correct type. Ignore case if iva = null.
     if( iva != null && !(iva instanceof IVirtualArray2D) )
     {
       SharedMessages.addmsg("ERROR - Invalid data type in ComponentSwapper2D."+
                             "set(). Data must be of type [IVirtualArray2D].");
       iva = null;
     }
     // If null, set panel to JPanel with JLabel "No data to display."
     if( iva == null )
     {
       data = null;
       buildPanel();
       return;
     }
     // If iva is not null, but data was previously null...
     if( data == null )
     {
       data = iva;
       // Build the new view component if none exist.
       if( current_tracker == null )
         buildComponent(getVisibleViewType());
       buildPanel();
       // Cause the ComponentSwapper to be redrawn and laid out since things
       // have changed.
       validate();
       repaint();
     }
     // data was valid and iva is valid, call dataChanged() on view components.
     else
     {
       ((IViewComponent2D)getViewComponent()).dataChanged((IVirtualArray2D)iva);
     }
   }
  
  /**
   * This method creates a ComponentTracker containing an IViewComponent2D
   * associated with view_type. Since association of IViewComponents to
   * view types is specific to the ComponentSwapper, classes extending
   * AbstractComponentSwapper must provide the implementation. This method
   * should only be called by the AbstractComponentSwapper.
   *
   *  @param  view_type The view_type that needs to be added to the table.
   */
   protected void buildComponent(String view_type)
   {
     // Make sure there is data to be displayed.
     if( data == null )
       return;
     // If this view is not supported by this component swapper, do nothing.
     if( !isAvailableViewType(view_type) )
       return;
     // Below are the views supported by this ComponentSwapper. Each
     // value is assigned a corresponding view component.
     // If ImageViewComponent requested...
     if( view_type.equals(IMAGE) )
     {
       // Create new tracker.
       current_tracker = new ComponentTracker(
                              new ImageViewComponent((IVirtualArray2D)data) );
       // Add listener to view component so swapper can pass action events
       // to listeners of this swapper.
       current_tracker.getViewComp().addActionListener(new ComponentListener());
     }
     // If TableViewComponent requested...
     else if( view_type.equals(TABLE) )
     {
       // Create new tracker.
       current_tracker = new ComponentTracker(
                              new TableViewComponent((IVirtualArray2D)data) );
       // Add listener to view component so swapper can pass action events
       // to listeners of this swapper.
       current_tracker.getViewComp().addActionListener(new ComponentListener());
     }
   }
  
  /**
   * This method passes information from the previous view component
   * to the new visible view component. Information such as pointed at and
   * colorscale should be set here.
   */ 
   protected void linkComponents()
   {
     IViewComponent2D new_view = (IViewComponent2D)getViewComponent();
     // If this is the first ViewComponent to be created, no need to link
     // the components together.
     if( current_component == null )
     {
       current_component = new_view;
       return;
     }
     // If linkComponents() called on same component, do nothing.
     else if( current_component == new_view )
       return;
     // Transfer current pointed at from old component to new component.
     new_view.setPointedAt(current_component.getPointedAt());
     current_component = null;
     // Update the current component.
     current_component = new_view;
   }
   
  /**
   * This method overrides the valueChanged() provided by ActionValueJPanel
   * in order to perform additional tasks prior to sending the message onto
   * other listeners.
   *
   *  @param  ave The ActionValueEvent being passed to this swapper.
   */
   public void valueChanged( ActionValueEvent ave )
   {
     String action = ave.getActionCommand();
     // If message was generated from the view component displayed by this
     // swapper, ignore the event.
     if( ave.getSource() == getViewComponent() )
     {
       // Pass ActionValueEvent onto listeners.
       super.valueChanged(ave);
       return;
     }
     ignore_change = true;
     if( action.equals(IViewComponent.POINTED_AT_CHANGED) )
     {/*
       // Since Image and TableViewComponents do not return pointed-at in
       // the same coordinate system, convert the table column-row point
       // to an image world coordinate.
       if( getVisibleViewType().equals(IMAGE) && 
           ave.getSource() instanceof TableViewComponent )
       {
         floatPoint2D pt = (floatPoint2D)ave.getNewValue();
	 ImageViewComponent ivc = (ImageViewComponent)getViewComponent();
         ivc.setPointedAt(ivc.getWorldCoordsAtColumnRow(pt.toPoint()));
       }
       // Since Image and TableViewComponents do not return pointed-at in
       // the same coordinate system, convert the image world coordinate
       // to a table column-row point.
       else if( ave.getSource() instanceof ImageViewComponent &&
           getVisibleViewType().equals(TABLE) )
       {
	 ImageViewComponent ivc = (ImageViewComponent)ave.getSource();
	 floatPoint2D col_row = new floatPoint2D(
	                             ivc.getColumnRowAtWorldCoords(
	                                (floatPoint2D)ave.getNewValue()) );
         ((IViewComponent2D)getViewComponent()).setPointedAt(col_row);
       }
       else*/
         ((IViewComponent2D)getViewComponent()).
            setPointedAt((floatPoint2D)ave.getNewValue());
       System.out.println("New Pointed At: "+
                   ((IViewComponent2D)getViewComponent()).getPointedAt() );
     }
     else if( action.equals(IViewComponent.SELECTED_CHANGED) )
     {/*
       ((IViewComponent2D)getViewComponent(getVisibleViewType())).
          setSelectedRegions((Region[])pce.getNewValue());*/
     }
     else if( action.equals(IColorScaleAddible.COLORSCALE_CHANGED) )
     {
       if( getVisibleViewType().equals(IMAGE) )
       {
         ((ImageViewComponent)getViewComponent()).
	   setColorScale( (String)ave.getNewValue() );
       }
       else if( getVisibleViewType().equals(TABLE) )
       {
         ((TableViewComponent)getViewComponent()).
	   setThumbnailColorScale( (String)ave.getNewValue() );
       }
     }
     ignore_change = false;
     // Pass ActionValueEvent onto listeners.
     super.valueChanged(ave);
   }
  
  /*
   * This class is responsible for passing messages from view components to
   * listeners of this swapper.
   */
   private class ComponentListener implements ActionListener
   {
     public void actionPerformed( ActionEvent ae )
     {
       if( ignore_change )
         return;
       String action = ae.getActionCommand();
       IViewComponent2D ivc = (IViewComponent2D)ae.getSource();
       ActionValueEvent val = null;
       if( action.equals(IViewComponent.POINTED_AT_CHANGED) )
       {
         val = new ActionValueEvent( ivc, action, null, ivc.getPointedAt() );
	       
       }
       else if( action.equals(IViewComponent.SELECTED_CHANGED) )
       {
         val = new ActionValueEvent( ivc, action,
	                             null, ivc.getSelectedRegions() );
       }
       else if( action.equals(IColorScaleAddible.COLORSCALE_CHANGED) )
       {
         if( getVisibleViewType().equals(IMAGE) )
         {
           val = new ActionValueEvent( ivc, action, null,
	                           ((ImageViewComponent)ivc).getColorScale() );
         }
       }
       // If value was set, pass it on to listeners.
       if( val != null )
         sendActionValue(val);
     }
   }
   
  /**
   * For testing purposes only...
   * Tests the functionality of the ComponentSwapper2D.
   *
   *  @param  args All inputs are ignored.
   */
   public static void main( String args[] )
   {
     // build my 2-D data
     int row = 200;
     int col = 180;
     float test_array[][] = new float[row][col];
     for ( int i = 0; i < row; i++ )
       for ( int j = 0; j < col; j++ )
     	 test_array[i][j] = i - j;
     
     // Put 2-D data into a VirtualArray2D wrapper
     IVirtualArray2D va2D = new VirtualArray2D( test_array );
     // Give meaningful range, labels, units, and linear or log display method.
     va2D.setAxisInfo( AxisInfo.X_AXIS, 0f, 10000f, 
     			 "TestX","TestUnits", AxisInfo.LINEAR );
     va2D.setAxisInfo( AxisInfo.Y_AXIS, 0f, 1500f, 
     			 "TestY","TestYUnits", AxisInfo.LINEAR );
     va2D.setAxisInfo( AxisInfo.Z_AXIS, 0f, 1f, "Z", "Units",
     		       AxisInfo.PSEUDO_LOG );
     va2D.setTitle("CompSwap2D Display");
     //int[] ctrl_indices = new int[]{0,1,6,3,12};
     ComponentSwapper2D cs2D1 = new ComponentSwapper2D(va2D,IMAGE,SHOW_ALL);
     ComponentSwapper2D cs2D2 = new ComponentSwapper2D(va2D,IMAGE,SHOW_ALL);
     cs2D1.addActionValueListener(cs2D2);
     cs2D2.addActionValueListener(cs2D1);
     AbstractComponentSwapper.testFrame(cs2D1);
     AbstractComponentSwapper.testFrame(cs2D2);
   }
 }
