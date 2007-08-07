/**
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
 * @author Ruth
 *
 */
abstract public class ViewComponent2DwSelection implements IViewComponent2D {



   /**
    * 
    */
   public ViewComponent2DwSelection() {

   
   }


   /* (non-Javadoc)
    * @see gov.anl.ipns.ViewTools.Components.TwoD.IViewComponent2D#dataChanged(gov.anl.ipns.ViewTools.Components.IVirtualArray2D)
    */
   @Override
   abstract public void dataChanged( IVirtualArray2D v2d );

   /* (non-Javadoc)
    * @see gov.anl.ipns.ViewTools.Components.TwoD.IViewComponent2D#getPointedAt()
    */
   @Override
   public floatPoint2D getPointedAt() {

      // TODO Auto-generated method stub
      return null;
   }


   
 

   /* (non-Javadoc)
    * @see gov.anl.ipns.ViewTools.Components.TwoD.IViewComponent2D#getWorldToArrayTransform()
    */
   @Override
   abstract public CoordTransform getWorldToArrayTransform(); 


   /* (non-Javadoc)
    * @see gov.anl.ipns.ViewTools.Components.TwoD.IViewComponent2D#setPointedAt(gov.anl.ipns.Util.Numeric.floatPoint2D)
    */
   @Override
   abstract public void setPointedAt( floatPoint2D fpt ) ;


   /* (non-Javadoc)
    * @see gov.anl.ipns.ViewTools.Components.TwoD.IViewComponent2D#setSelectedRegions(gov.anl.ipns.ViewTools.Components.Region.Region[])
    */
   @Override
   abstract public void setSelectedRegions( Region[] rgn ) ;


   /* (non-Javadoc)
    * @see gov.anl.ipns.ViewTools.Components.IViewComponent#addActionListener(java.awt.event.ActionListener)
    */
   @Override
   abstract public void addActionListener( ActionListener act_listener ) ;


   /* (non-Javadoc)
    * @see gov.anl.ipns.ViewTools.Components.IViewComponent#dataChanged()
    */
   @Override
   abstract public void dataChanged() ;


   /* (non-Javadoc)
    * @see gov.anl.ipns.ViewTools.Components.IViewComponent#getControls()
    */
   @Override
   abstract public ViewControl[] getControls() ;


   /* (non-Javadoc)
    * @see gov.anl.ipns.ViewTools.Components.IViewComponent#getDisplayPanel()
    */
   @Override
   abstract public JPanel getDisplayPanel() ;


   /* (non-Javadoc)
    * @see gov.anl.ipns.ViewTools.Components.IViewComponent#getMenuItems()
    */
   @Override
   abstract public ViewMenuItem[] getMenuItems() ;

   /* (non-Javadoc)
    * @see gov.anl.ipns.ViewTools.Components.IViewComponent#kill()
    */
   @Override
   abstract public void kill() ;

   /* (non-Javadoc)
    * @see gov.anl.ipns.ViewTools.Components.IViewComponent#removeActionListener(java.awt.event.ActionListener)
    */
   @Override
   abstract public void removeActionListener( ActionListener act_listener ) ;


   /* (non-Javadoc)
    * @see gov.anl.ipns.ViewTools.Components.IViewComponent#removeAllActionListeners()
    */
   @Override
   abstract public void removeAllActionListeners() ;


   /* (non-Javadoc)
    * @see gov.anl.ipns.ViewTools.Components.IPreserveState#getObjectState(boolean)
    */
   @Override
   abstract public ObjectState getObjectState( boolean is_default ) ;


   /* (non-Javadoc)
    * @see gov.anl.ipns.ViewTools.Components.IPreserveState#setObjectState(gov.anl.ipns.ViewTools.Components.ObjectState)
    */
   @Override
   abstract public void setObjectState( ObjectState new_state ) ;


   /**
    * -> returns RegionOpList for specified name
    */
   public RegionOpListWithColor getSelectedRegions( String name){
      
      return getSelectionOverlay().getSelectedRegions(  name ); 
   }
   
   /**
    *  reset RegionOpList for specified name
    */
   public void setSelectedRegions( RegionOpList regOp, String name){
      
      getSelectionOverlay().setSelectedRegions(regOp , name );
   }
   
   /**
    *   -> add RegionOp to specified name
    */
   public void addSelection( RegionOp RegionOp, String  name ){
      
      getSelectionOverlay().addSelection(  RegionOp , name );
   }

   
   /**
    *         -> returns array of String names
    */
   public String[] getSelectionNames(){
      
      return getSelectionOverlay().getAllNames();
   }
   
   /**
    *            -> get name for currently active selection
    */
   public String getCurrentName() {
      
      return getSelectionOverlay().getCurrentName();
      
   }
   
   /**
    *         -> removes name from overlay (entire list)
    *         
    * @param name
    */
   public void removeSelection(  String name ){
      
      getSelectionOverlay().removeSelection( name );
   }
   
   /**
    *          -> clear named selection, list remains
    *          
    * @param name
    */
   public void clearSelection(  String name ){
      
      getSelectionOverlay().clearSelection(  name  );
   }

   
   /**
    *     -> returns selected Point[] for specified name
    * @param name
    */
   public java.awt.Point[] getSelectedPoints( String name){
      
      return getSelectionOverlay().getSelectedPoints(  name  );
   }

   /**
    *   -> add named selection overlay, if named 
    *    selection doesn't exist.  Shows editor
    *  for specified name if show is true.
    *  
    * @param name
    * @param show
    */
   public void enableSelection( String name, boolean show){
      
      getSelectionOverlay().enableSelection( name , show );
   }
   
   /**
    *           -> turns off any current selection editor and
                                    turns off selection overlay
    */
   public void disableSelection(){
      
      getSelectionOverlay().disableSelection();
   }
   
   /**
    *     -> leaves selection overlay as is, but turns off the Editor
   */
    public void disableSelectionEditor(){
       
       getSelectionOverlay().disableSelectionEditor();
    }
                                    

    public void setColor( String  name, java.awt.Color Color){
       
       getSelectionOverlay().setColor(   name,  Color);
       
    }
    
    public void setOpacity(  String name, float Opacity){
       
       getSelectionOverlay().setOpacity( name, Opacity);
       
    }
    
    
    public java.awt.Color getColor(  String name){
       
       return getSelectionOverlay().getColor(   name);
       
    }
    public float getOpacity( String name){
       
       return getSelectionOverlay().getOpacity( name);
    }
    
   abstract protected SelectionOverlay getSelectionOverlay();
   
 
}
