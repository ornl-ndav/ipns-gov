/*
 * File: IComponentSwapper.java
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
 *  Revision 1.3  2005/06/06 20:39:28  kramer
 *  Added CONTOUR as a possible view.
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
 
 import gov.anl.ipns.ViewTools.Components.IPreserveState;
 import gov.anl.ipns.ViewTools.Components.IViewComponent;
 import gov.anl.ipns.ViewTools.Components.IVirtualArray;
 import gov.anl.ipns.ViewTools.Components.Menu.ViewMenuItem;
 import gov.anl.ipns.ViewTools.UI.ActionValueListener;

/**
 * This interface defines methods for classes which group similar
 * IViewComponents into one view. Classes implementing this interface will
 * arrange the IViewComponent and its controls onto a single panel.
 *
 * @see gov.anl.ipns.ViewTools.Components.IViewComponent
 * @see gov.anl.ipns.ViewTools.Components.IVirtualArray
 */
 public interface IComponentSwapper extends IPreserveState
 {
  // ------------------------------View Types-------------------------------
  /**
   * "Table" - This variable specifies the TableViewComponent as an
   * IViewComponent display type for any given ComponentSwapper.
   */
   public static final String TABLE = "Table";
  /**
   * "Graph" - This variable specifies the FunctionViewComponent as an
   * IViewComponent display type for any given ComponentSwapper.
   */
   public static final String GRAPH = "Graph";
  /**
   * "Image" - This variable specifies the ImageViewComponent as an
   * IViewComponent display type for any given ComponentSwapper.
   */
   public static final String IMAGE = "Image";
   public static final String CONTOUR = "Contour";
  /**
   * List of all available views for any ComponentSwapper. If a view is added,
   * it should be added to this list.
   */
   public static final String[] total_views = 
                                    new String[]{TABLE,GRAPH,IMAGE,CONTOUR};
  // ----------------------------Display Schemes-----------------------------
  /**
   * 0 - This variable is a ViewControl display scheme for any given
   * ComponentSwapper. This option specifies all controls to be shown.
   * Use this value as a parameter where display_scheme is needed.
   */
   public static final int SHOW_ALL = 0;
  /**
   * 1 - This variable is a ViewControl display scheme for any given
   * ComponentSwapper. This option specifies all controls to be hidden.
   * Use this value as a parameter where display_scheme is needed.
   */
   public static final int SHOW_NONE = 1;
  /**
   * 2 - This variable is a ViewControl display scheme for any given
   * ComponentSwapper. This option specifies controls to be shown
   * according to IComponentSwapper internal defaults.
   * Use this value as a parameter where display_scheme is needed.
   */
   public static final int SHOW_DEFAULT = 2;
  /**
   * 3 - This variable is a ViewControl display scheme for any given
   * ComponentSwapper. This option specifies controls to be shown
   * according to indices passed in via setVisibleControls(int,int[]).
   * Use this value as a parameter where display_scheme is needed.
   */
   public static final int USER_DEFINED = 3;
   
  /**
   * Get all available IViewComponent types displayable by this swapper. Types
   * are specified by int keys defined by the IComponentSwapper interface.
   *
   *  @return List of available IViewComponent types that can be displayed
   *          by this swapper.
   */
   public String[] getViewTypes();
   
  /**
   * Get the currently visible IViewComponent type displayed by this swapper.
   * Types are specified by int keys defined by the IComponentSwapper interface.
   *
   *  @return Current IViewComponent type that is being displayed
   *          by this swapper.
   */
   public String  getVisibleViewType();
   
  /**
   * Set the currently visible IViewComponent type displayed by this swapper.
   * Types are specified by String keys defined by the IComponentSwapper
   * interface.
   *
   *  @param  view_type Current IViewComponent type that is to be displayed
   *                    by this swapper.
   */
   public void setVisibleViewType( String view_type );
   
  /**
   * Get the list of control indices hidden by this swapper for the visible
   * IViewComponent. The indices are consistent with the array of
   * ViewControls returned by the IViewComponent.
   *
   *  @return int[] of indices corresponding to controls in the list of
   *          ViewControls returned by IViewComponent.getControls(). If
   *          no controls are hidden, an empty array (int[0]) is returned.
   *          If there is no visible ViewComponent, null is returned.
   */ 
   public int[] getHiddenControls();
   
  /**
   * Get the list of control indices shown by this swapper for the Visible
   * IViewComponent. The indices are consistent with the array of
   * ViewControls returned by the IViewComponent.
   *
   *  @return int[] of indices corresponding to controls in the list of
   *          ViewControls returned by IViewComponent.getControls(). If
   *          no controls exist, an empty array (int[0]) is returned.
   *          If there is no visible ViewComponent, null is returned.
   */ 
   public int[] getVisibleControls();
   
  /**
   * Set the visible controls of the visible IViewComponent based on
   * a predetermined scheme.
   *
   *  @param  display_scheme Predefined method for choosing visible controls. 
   */ 
   public void setVisibleControls( int display_scheme );
   
  /**
   * Set the visible controls of the visible IViewComponent based on
   * a list of control indices. Control indices not included will be
   * hidden. The indices are consistent with the array of ViewControls
   * returned by the IViewComponent.
   *
   *  @param  control_indices List of indices for choosing visible controls. 
   */ 
   public void setVisibleControls( int[] control_indices );
   
  /**
   * This method gets the visible IViewComponent. Use this method to get
   * a complete list of component controls and component menu items. Also,
   * specialized settings for each IViewComponent can be set by upper levels.
   *
   *  @return IViewComponent currently displayed by the IComponentSwapper.
   */ 
   public IViewComponent getViewComponent();
   
  /**
   * Get ViewMenuItems generated by the class implementing IComponentSwapper.
   * This list should include a ViewMenuItem to swap between IViewComponents.
   *
   *  @return List of ViewMenuItems unique to this IComponentSwapper.
   */ 
   public ViewMenuItem[] getSwapperMenuItems();
  
  /**
   * This method sets the data to be displayed by the ComponentSwapper.
   * Since data specific knowledge is needed, the extending ComponentSwapper
   * must provide the implementation.
   *
   *  @param  iva The data to be displayed by the swapper.
   */ 
   public void setData(IVirtualArray iva);
   
  /**
   * Add an ActionValueListener to this class which will listen to the events
   * of all IViewComponents contained by this IComponentSwapper.
   *
   *  @param  avl The ActionValueListener that listens to ActionValueEvents
   *              generated by this IComponentSwapper.
   */ 
   public void addActionValueListener( ActionValueListener avl );
   
  /**
   * Remove an existing ActionValueListener from this class.
   *
   *  @param  avl The ActionValueListener that listens to ActionValueEvents
   *              generated by this IComponentSwapper.
   */ 
   public void removeActionValueListener( ActionValueListener avl );
   
  /**
   * Remove all existing ActionValueListeners of this class.
   */ 
   public void removeAllActionValueListeners();
 }
