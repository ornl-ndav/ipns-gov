
 
package DataSetTools.components.View.OneD;

import javax.swing.*;
import java.awt.*;
import java.lang.*;
import java.awt.event.*;
import DataSetTools.components.View.IViewComponent;
/**
 * Any class that implements this interface will interpret and display
 * data in a usable form. Examples include images, tables, and graphs.
 */
public interface IViewComponent1D extends IViewComponent
{
  /**
   * This method is a notification to the view component that the selected
   * point has changed.
   */ 
   public void setPointedAt( Point pt );
  
  /**
   *  Returns the point in World coords that is being pointed at.
   */ 
   public Point getPointedAt( );
  
}

