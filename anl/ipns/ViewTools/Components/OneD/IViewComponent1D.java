
 
package DataSetTools.components.View.OneD;

import javax.swing.*;
import java.awt.*;
import java.lang.*;
import java.awt.event.*;

/**
 * Any class that implements this interface will interpret and display
 * data in a usable form. Examples include images, tables, and graphs.
 */
public interface IViewComponent1D
{
  /*
   * These variables are messaging strings for use by action listeners.
   */
   public static final String POINTED_AT_CHANGED = "POINTED_AT_CHANGED";
   public static final String SELECTED_CHANGED = "SELECTED_CHANGED";
   
  /**
   * This method is a notification to the view component that the selected
   * point has changed.
   */ 
   public void setPointedAt( Point pt );
  
  /**
   *  Returns the point in World coords that is being pointed at.
   */ 
   public Point getPointedAt( );
  
  /**
   * Given an array of points, a selection overlay can be created.
   */ 
   public void setSelectedSet( Point[] pts );
  
  /**
   * Retrieve array of points for selection overlay
   */
   public Point[] getSelectedSet();
   
  /**
   * To be continued...
   */ 
   public void dataChanged();
   
  /**
   * Add a listener to this view component. A listener will be notified
   * when a selected point or region changes on the view component.
   */
   public void addActionListener( ActionListener act_listener );
   
  /**
   * Remove a specified listener from this view component.
   */ 
   public void removeActionListener( ActionListener act_listener );
  
  /**
   * Remove all listeners from this view component.
   */ 
   public void removeAllActionListeners();
  
  /**
   * To be continued...
   */ 
   public JComponent[] getSharedControls();

  /**
   * To be continued...
   */   
   public JComponent[] getPrivateControls();

}

