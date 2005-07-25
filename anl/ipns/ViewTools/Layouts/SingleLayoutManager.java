/*
 * File: SingleLayoutManager.java
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
 *  Revision 1.4  2005/07/25 20:51:04  kramer
 *  Modified the imports so that the new ContourViewComponent (from the
 *  package gov.anl.ipns.ViewTools.Components.TwoD.Contour package) is used.
 *
 *  Revision 1.3  2005/07/20 17:29:47  kramer
 *
 *  Modified the getSharedControls() method to support the
 *  ContourViewComponent.  Currently, the colorscale intensity control is
 *  shared.
 *
 *  Revision 1.2  2005/05/25 20:28:46  dennis
 *  Now calls convenience method WindowShower.show() to show
 *  the window, instead of instantiating a WindowShower object
 *  and adding it to the event queue.
 *
 *  Revision 1.1  2005/03/28 05:54:06  millermi
 *  - Initial Version - This is a building block in the new viewer
 *    structure.
 *
 */
 package gov.anl.ipns.ViewTools.Layouts;
 
 import gov.anl.ipns.Util.Sys.SharedMessages;
import gov.anl.ipns.ViewTools.Components.IViewComponent;
import gov.anl.ipns.ViewTools.Components.IVirtualArray;
import gov.anl.ipns.ViewTools.Components.IVirtualArray2D;
import gov.anl.ipns.ViewTools.Components.IVirtualArrayList1D;
import gov.anl.ipns.ViewTools.Components.ObjectState;
import gov.anl.ipns.ViewTools.Components.Menu.ViewMenuItem;
import gov.anl.ipns.ViewTools.Components.TwoD.ImageViewComponent;
import gov.anl.ipns.ViewTools.Components.TwoD.TableViewComponent;
import gov.anl.ipns.ViewTools.Components.TwoD.Contour.ContourViewComponent;
import gov.anl.ipns.ViewTools.Components.ViewControls.ViewControl;

import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * This class is responsible for displaying a single ComponentSwapper.
 * No data manipulation is done by this class, however an extending class
 * could override the setData() method to perform data manipulation.
 */
 public class SingleLayoutManager extends AbstractLayoutManager
 {
  /**
   * "Component Swapper" - This String key references the state of the
   * AbstractComponentSwapper visible within this layout manager. This
   * value this key references is an ObjectState.
   */
   public static final String COMPONENT_SWAPPER = "Component Swapper";
   private AbstractComponentSwapper swapper;
   private ObjectState restore_state = null;
   
  /**
   * Default Contructor - No data is displayed.
   */
   public SingleLayoutManager()
   {
     setData(null);
   }
   
  /**
   * Constructor - Create a SingleLayoutManager with the given swapper.
   *
   *  @param  acs The AbstractComponentSwapper to be displayed.
   */
   public SingleLayoutManager( AbstractComponentSwapper acs )
   {
     super();
     swapper = acs;
     if( swapper == null )
     {
       JPanel null_display = new JPanel();
       null_display.add(new JLabel("No AbstractComponentSwapper to display."));
       add(null_display);
     }
     else
     {
       add(swapper);
       // Make layout manager listen to changes in the swapper.
       addActionValueListener(swapper);
       // Make swapper listen to changes in the layout manager.
       swapper.addActionValueListener(this);
     }
   }
   
  /**
   * Constructor - Create a SingleLayoutManager given some data contained
   * in an IVirtualArray. This "smart" constructor will automatically
   * choose the correct AbstractComponentSwapper to use.
   *
   *  @param  data Data to be displayed by the SingleLayoutManager.
   */
   public SingleLayoutManager( IVirtualArray data )
   {
     setData(data);
   }
   
  /**
   * Constructor - Create a SingleLayoutManager given some data contained
   * in an IVirtualArray. This "smart" constructor will automatically
   * choose the correct AbstractComponentSwapper to use.
   *
   *  @param  data Data to be displayed by the SingleLayoutManager.
   */
   public SingleLayoutManager( IVirtualArray data, ObjectState state )
   {
     restore_state = state;
     setData(data);
     setObjectState(state);
   }
  
  /**
   * Get the current state of this LayoutManager.
   *
   *  @param  is_default Is desired state for preferences (default=true) or 
   *		         a project (session=false) save? Use IPreserveState
   *		         variables for parameter.
   *  @return The current ObjectState of this LayoutManager.
   */
   public ObjectState getObjectState( boolean is_default )
   {
     ObjectState state = new ObjectState();
     if( swapper != null )
       state.insert( COMPONENT_SWAPPER, swapper.getObjectState(is_default) );
     return state;
   }
   
  /**
   * Set the state of the LayoutManager to new_state.
   *
   *  @param  new_state The new state of the LayoutManager.
   */ 
   public void setObjectState( ObjectState new_state )
   {
     // Do nothing if state is null.
     if( new_state == null )
       return;
     boolean rebuild_and_repaint = false;
     Object value = new_state.get(COMPONENT_SWAPPER);
     if( value != null && swapper != null )
     {
       swapper.setObjectState((ObjectState)value);
       rebuild_and_repaint = true;
     }
     
     // Rebuild and repaint GUI if necessary.
     if( rebuild_and_repaint )
     {
       validate();
       repaint();
     }
   }
   
  /**
   * Set the data to be displayed by this LayoutManager.
   *
   *  @param  data The data to be displayed.
   */
   public void setData( IVirtualArray data )
   {
     // Make sure there is data to display.
     if( data != null )
     {
       // If IVirtualArray2D, use ComponentSwapper2D.
       if( data instanceof IVirtualArray2D )
       {
         // If swapper exists, and it can handle an IVirtualArray2D
         if( swapper != null && swapper instanceof ComponentSwapper2D )
           swapper.setData((IVirtualArray2D)data);
         // Otherwise create a new swapper that can handle an IVirtualArray2D.
         else
	 {
	   removeAll();
	   // If constructor with ObjectState was called, use the 
	   // ComponentSwapper2D constructor with state.
	   if( restore_state != null )
	   {
	     swapper = new ComponentSwapper2D( (IVirtualArray2D)data,
	                    (ObjectState)restore_state.get(COMPONENT_SWAPPER) );
	     // After initialized, and state is set, reset the restore_state
	     // to null.
	     restore_state = null;
	   }
	   else
             swapper = new ComponentSwapper2D((IVirtualArray2D)data);
	   add(swapper);
           // Make layout manager listen to changes in the swapper.
           addActionValueListener(swapper);
           // Make swapper listen to changes in the layout manager.
           swapper.addActionValueListener(this);
         }
       }
       else if( data instanceof IVirtualArrayList1D )
       {
         System.out.println("SingleLayoutManager.setData() not yet supported "+
	                    "for IVirtualArrayList1D.");
       }
       else
       {
         SharedMessages.addmsg("Data of type ["+data.getClass()+
	                       "] not recognized by SingleLayoutManager.");
       }
     }
     // Else no data to display.
     else
     {
       // Let swapper handle null case.
       removeAll();
       JPanel null_display = new JPanel();
       null_display.add(new JLabel("No data to display."));
       add(null_display);
     }
   }
   
  /**
   * Get a unique list of controls that will be shared with controls from
   * other IComponentLayoutManagers.
   *
   *  @return Unique list of controls to be shared/linked with controls from
   *          other IComponentLayoutManagers.
   */
   public ViewControl[] getSharedControls()
   {
     if( swapper == null )
       return new ViewControl[0];
     IViewComponent ivc = swapper.getViewComponent();
     ViewControl[] controls = ivc.getControls();
     if( ivc instanceof ImageViewComponent )
     {
       ViewControl[] shared = new ViewControl[2];
       shared[0] = controls[0]; // Intensity Slider
       shared[0].setSharedKey("Intensity");
       shared[1] = controls[2]; // Cursor Readout
       shared[1].setSharedKey("CursorReadout");
       // Build list of visible control indices.
       int[] indices = new int[]{1,3,4,5,6,7};
       swapper.setVisibleControls( indices );
       return shared;
     }
     else if( ivc instanceof TableViewComponent )
     {
       ViewControl[] shared = new ViewControl[1];
       shared[0] = controls[0]; // FormatControl
       shared[0].setSharedKey("Format");
       swapper.setVisibleControls( new int[]{1} );
       return shared;
     }
     else if ( ivc instanceof ContourViewComponent )
     {
        ViewControl[] shared = new ViewControl[1];
          shared[0] = controls[0];
          shared[0].setSharedKey("Intensity");
        //this is the list of indices of controls that 
        //should be visible from the array 'ivc.getControls()'
        int[] indices = new int[] {1,2,3,4,5,6};
        swapper.setVisibleControls(indices);
        return shared;
     }
     else
     {
       System.out.println("IViewComponent ["+ivc.getClass()+"] unsupported "+
                          "by SingleLayoutManager.getSharedControls()");
       return null;
     }
   }
   
  /**
   * Get the ViewMenuItems that are to be displayed by this LayoutManager.
   *
   *  @return Unique list of menu items to be displayed by the ViewManager.
   */
   public ViewMenuItem[] getLayoutMenuItems()
   {
     // Since this layout manager has no menu items of it's own, return
     // an empty list if the swapper is null.
     if( swapper == null )
       return new ViewMenuItem[0];
     // Return the menu items of the swapper.
     return swapper.getSwapperMenuItems();
   }
   
  /**
   * The SingleLayoutManager only contains one AbstractComponentSwapper.
   * No matter what index is passed in, the same swapper will be returned.
   *
   *  @param  index Index of swapper, unimportant for SingleLayoutManager.
   *  @return The AbstractComponentSwapper displayed by this layout manager.
   */
   public AbstractComponentSwapper getSwapper(int index) { return swapper; }
   
  /**
   * Get the number of AbstractComponentSwappers used by the
   * SingleLayoutManager.
   *
   *  @return  The number of AbstractComponentSwappers used by this manager.
   */
   public int getSwapperCount() { return 1; }
   /*
   public static void main(String args[])
   {
     
   }*/
 }
