package gov.anl.ipns.Util.Sys;

import java.awt.Window;

import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

/**
 * This Ancestor listener disposes a Window when the ancestor of a 
 * component is removed.  Components that spawn new windows should use
 * this to eliminate windows that they spawn after the window showing
 * a component is removed.
 * 
 * @author ruth
 *
 */
public class WindowAncestorListener implements AncestorListener
{

   Window window;
   
   public WindowAncestorListener( Window window)
   {
      this.window = window;
   }
   @Override
   public void ancestorAdded(AncestorEvent arg0)
   {

      
   }

   @Override
   public void ancestorMoved(AncestorEvent arg0)
   {

      
   }

   /**
    * If the ancestor of a given component( some window) is closed so
    * the component cannot be displayed,  this will dispose of the
    * window( possibly spawned by the component) passed in with the
    * constructor
    */
   @Override
   public void ancestorRemoved(AncestorEvent arg0)
   {
      
      if( window != null)
      {
         window.removeAll( );
         window.dispose( );
      }

   }

}
