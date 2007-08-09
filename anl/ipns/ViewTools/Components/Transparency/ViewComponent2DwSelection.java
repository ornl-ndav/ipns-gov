/*
 * File:  ViewComponent2DwSelection.java
 *
 * Copyright (C) 2007, Ruth Mikkelson
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
 * $Log$
 * Revision 1.4  2007/08/09 14:37:34  rmikk
 * Added abstract methods to reflect changes initiated in SelectionOverlay to the
 *    corresponding GUI Elements without sending out events that would cause
 *    further changes
 *
 * Revision 1.3  2007/08/08 15:07:11  rmikk
 * Added documentation, GPL
 * Changed one method, disableSelection, to disableOverlay and implemented it
 *   by sending messages to anyone who can turn the overlay on and off to do so.
 *
 *
 */
package gov.anl.ipns.ViewTools.Components.Transparency;

import java.awt.event.ActionListener;
import javax.swing.JPanel;

import gov.anl.ipns.Util.Numeric.floatPoint2D;
import gov.anl.ipns.ViewTools.Components.IVirtualArray2D;
import gov.anl.ipns.ViewTools.Components.ObjectState;
import gov.anl.ipns.ViewTools.Components.Menu.ViewMenuItem;
import gov.anl.ipns.ViewTools.Components.Region.Region;
import gov.anl.ipns.ViewTools.Components.TwoD.IViewComponent2D;
import gov.anl.ipns.ViewTools.Components.ViewControls.ViewControl;
import gov.anl.ipns.ViewTools.Panels.Transforms.CoordTransform;
import gov.anl.ipns.ViewTools.Components.Region.*;

/**
 * This class contains methods to handle the SelectionOverlay programmatically
 * instead of from GUI elements.  Most implementations call corresponding
 * methods in the SelectionOverlay
 *
 */
abstract public class ViewComponent2DwSelection implements IViewComponent2D {
 
   /* (non-Javadoc)
    * @see gov.anl.ipns.ViewTools.Components.TwoD.IViewComponent2D#dataChanged(gov.anl.ipns.ViewTools.Components.IVirtualArray2D)
    */
   abstract public void dataChanged( IVirtualArray2D v2d );


   /* (non-Javadoc)
    * @see gov.anl.ipns.ViewTools.Components.TwoD.IViewComponent2D#getPointedAt()
    */
   abstract public floatPoint2D getPointedAt();


   /* (non-Javadoc)
    * @see gov.anl.ipns.ViewTools.Components.TwoD.IViewComponent2D#getWorldToArrayTransform()
    */
   abstract public CoordTransform getWorldToArrayTransform(); 


   /* (non-Javadoc)
    * @see gov.anl.ipns.ViewTools.Components.TwoD.IViewComponent2D#setPointedAt(gov.anl.ipns.Util.Numeric.floatPoint2D)
    */
   abstract public void setPointedAt( floatPoint2D fpt ) ;


   /* (non-Javadoc)
    * @see gov.anl.ipns.ViewTools.Components.TwoD.IViewComponent2D#setSelectedRegions(gov.anl.ipns.ViewTools.Components.Region.Region[])
    */
   abstract public void setSelectedRegions( Region[] rgn ) ;


   /* (non-Javadoc)
    * @see gov.anl.ipns.ViewTools.Components.IViewComponent#addActionListener(java.awt.event.ActionListener)
    */
   abstract public void addActionListener( ActionListener act_listener ) ;


   /* (non-Javadoc)
    * @see gov.anl.ipns.ViewTools.Components.IViewComponent#dataChanged()
    */
   abstract public void dataChanged() ;


   /* (non-Javadoc)
    * @see gov.anl.ipns.ViewTools.Components.IViewComponent#getControls()
    */
   abstract public ViewControl[] getControls() ;


   /* (non-Javadoc)
    * @see gov.anl.ipns.ViewTools.Components.IViewComponent#getDisplayPanel()
    */
   abstract public JPanel getDisplayPanel() ;


   /* (non-Javadoc)
    * @see gov.anl.ipns.ViewTools.Components.IViewComponent#getMenuItems()
    */
   abstract public ViewMenuItem[] getMenuItems() ;

   /* (non-Javadoc)
    * @see gov.anl.ipns.ViewTools.Components.IViewComponent#kill()
    */
   abstract public void kill() ;

   /* (non-Javadoc)
    * @see gov.anl.ipns.ViewTools.Components.IViewComponent#removeActionListener(java.awt.event.ActionListener)
    */
   abstract public void removeActionListener( ActionListener act_listener ) ;


   /* (non-Javadoc)
    * @see gov.anl.ipns.ViewTools.Components.IViewComponent#removeAllActionListeners()
    */
   abstract public void removeAllActionListeners() ;


   /* (non-Javadoc)
    * @see gov.anl.ipns.ViewTools.Components.IPreserveState#getObjectState(boolean)
    */
   abstract public ObjectState getObjectState( boolean is_default ) ;


   /* (non-Javadoc)
    * @see gov.anl.ipns.ViewTools.Components.IPreserveState#setObjectState(gov.anl.ipns.ViewTools.Components.ObjectState)
    */
   abstract public void setObjectState( ObjectState new_state ) ;


   /**
    * Returns RegionOpList associated with the  specified name
    * 
    * @param name  The name of the region of interest
    * 
    * @return the Structure with the color and opacity and the list
    *    of operations and regions the operation operates on.
    */
   public RegionOpListWithColor getSelectedRegions( String name){
      
      return getSelectionOverlay().getSelectedRegions(  name ); 
   }
   

   /**
    *  Resets the RegionOpList for a given region name.
    *  
    *  @param regOp  the list of operations with associated region.
    *                This could be a RegionOpListWithColor
    *                
    */
   public void setSelectedRegions( RegionOpList regOp, String name){
      
      getSelectionOverlay().setSelectedRegions(regOp , name );
   }

   
   /**
    *   Adds RegionOp to end of the list of RegionOp's associated
    *   with the given name.
    *   
    *   @param RegionOp  the operations with its associated region.
    *   
    *   @param  name the name associated with the RegionOpList where
    *                the RegionOp is added. NOTE: "Default" is the
    *                name if no name is known.
    */
   public void addSelection( RegionOp RegionOp, String  name ){
      
      getSelectionOverlay().addSelection(  RegionOp , name );
   }

   
   /**
    *   Returns an array of names for names selections
    *   
    *   @return an array of names for names selections
    */
   public String[] getSelectionNames(){
      
      return getSelectionOverlay().getAllNames();
   }

   
   /**
    * Returns the name for currently active selection
    * 
    * @return the name for currently active selection
    */
   public String getCurrentName() {
      
      return getSelectionOverlay().getCurrentName();
   }
   

   /**
    * Removes the named selection from all lists
    *         
    * @param name  the name of the named selection
    */
   public void removeSelection(  String name ){
      
      getSelectionOverlay().removeSelection( name );
   }

   
   /**
    *  Removes the RegionOps associated with the named selection
    *          
    * @param name  the name of the named selection
    */
   public void clearSelection(  String name ){
      
      getSelectionOverlay().clearSelection(  name  );
   }

   
   /**
    * Returns selected Point[] for specified name
    * 
    * @param name  the name of the named selection
    */
   public java.awt.Point[] getSelectedPoints( String name){
      
      return getSelectionOverlay().getSelectedPoints(  name  );
   }


   /**
    *   Adds named selection overlay, if named  selection doesn't exist.  
    *   Shows editor window for creating new selections for specified name if
    *   show is true.
    *  
    * @param name   the name of the named selection
    * 
    * @param show   if true, the window to specify new selections
    *               will appear.
    */
   public void enableSelection( String name, boolean show){
      
      boolean newName = getSelectionOverlay().enableSelection( name , show );
      GUIshowOnlySelectionNames( newName ) ;
   }

   
   /**
    *  
    *  Disables(enables) the selection overlay. If disabled all
    *  windows are hidden
    *  
    *  @param hide_show   if true hide the selection overlay otherwise
    *                     show the selection overlay
    */
   public void disableOverlay( boolean hide_show){
      
      GUIshowOnlySelectionOverlayOn( !hide_show );
      
   }

   
  /**
   *   Leaves selection overlay as is, but turns off all Editor windows
   */
    public void disableSelectionEditor(){
       
       getSelectionOverlay().disableSelectionEditor();
    }
                                    

    
    /**
     * Sets the color for drawing the selected regions associated with the
     * named selection
     * 
     * @param name  the name of the named selection
     * @param Color the new color to draw the selected regions associated with
     *              the given name
     */
    public void setColor( String  name, java.awt.Color Color){
       
       getSelectionOverlay().setColor(   name,  Color);
    }

    
    
    /**
     * Sets the opacity for drawing the selected regions associated with the
     * named selection
     * 
     * @param name  the name of the named selection
     * @param Opacity the new opacity to draw the selected regions associated with
     *              the given name
     */
    public void setOpacity(  String name, float Opacity){
       
       getSelectionOverlay().setOpacity( name, Opacity);
    }
    
    
    
    /**
     * Gets the color for drawing the selected regions associated with the
     * named selection
     * 
     * @param name  the name of the named selection
     * @return  the color that the selected regions associated with
     *              the given name uses to draw its selected regions
     */
    public java.awt.Color getColor(  String name){
       
       return getSelectionOverlay().getColor(   name);
    }


    
    /**
     * Gets the opacity for drawing the selected regions associated with the
     * named selection
     * 
     * @param name  the name of the named selection
     * @return  the opacity that the selected regions associated with
     *              the given name uses to draw its selected regions
     */
    public float getOpacity( String name){
       
       return getSelectionOverlay().getOpacity( name);
    }

    
    
   /**
    * Returns a reference to the selection overlay
    * 
    * @return a reference to the selection overlay
    */
   abstract protected SelectionOverlay getSelectionOverlay();
   
   
   /**
    * Makes the GUI element that indicates that the SelectionOverlay is
    * on or off is showing the given state. It must NOT send messages
    * that the state has changed.  This is used when the state has changed
    * and the GUI element only has to reflect that change correctly,
    * 
    * @param on_off  if true the GUI system should indicate that the
    *                SelectionOverlay is on, otherwise it is off
    */
   abstract protected void GUIshowOnlySelectionOverlayOn( boolean on_off);
   
   /**
    * Makes the GUI element that indicates the name list for the
    * named selections in the SelectionOverlay reflect a change in state. 
    * This is used when the state has changed and the GUI element only
    * has to reflect that change correctly,
    * 
    * @see getSelectionNames
    * @see getCurrentName
    */
   abstract protected void GUIshowOnlySelectionNames( boolean newName );
   
  
   
 
}
