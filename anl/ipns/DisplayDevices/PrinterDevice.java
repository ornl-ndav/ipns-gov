/*
 * $Log$
 * Revision 1.1  2007/07/12 15:45:05  amoe
 * Initial commit.
 *
 */
package gov.anl.ipns.DisplayDevices;

import java.awt.Dimension;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JPanel;

import gov.anl.ipns.Util.File.ImageRenderWriter;
import gov.anl.ipns.Util.Sys.PrintUtilities;
import gov.anl.ipns.ViewTools.Components.IVirtualArray;
import gov.anl.ipns.ViewTools.Components.IVirtualArray2D;
import gov.anl.ipns.ViewTools.Components.IVirtualArrayList1D;
import gov.anl.ipns.ViewTools.Displays.Display;
import gov.anl.ipns.ViewTools.Displays.Display1D;
import gov.anl.ipns.ViewTools.Displays.Display2D;

import DataSetTools.dataset.DataSet;
import DataSetTools.viewer.ViewManager;

public class PrinterDevice extends GraphicsDevice 
{
  //PrintUtilities printer_device;
  private ViewManager display_source = (ViewManager)super.display_window;
  private BufferedImage bimg = null;
  private String printer_name;
  
  
  public PrinterDevice(String printer_name)
  {
    this.printer_name = printer_name;
  }
  
  /**
   * Set an attribute for the PrinterDevice.
   * 
   * @param name - Name (key) for the attribute.
   * @param value - Value for the attribute
   */
  public void setDeviceAttribute(String name,Object value)
  {
    
  }
  
  /**
   * Flush any pending output. For PrinterDevice, this prints the image from
   * the printer.
   */
  public void print()
  {
    System.out.println("Printing file");
  }
  
  /**
   * Flush and closes any pending output for the PrinterDevice.
   */
  public void close()
  {
    if( display_source != null )
    {
      WindowEvent win_ev = new WindowEvent( display_source, 
          WindowEvent.WINDOW_CLOSING );
      display_source.dispatchEvent(win_ev);
    }
  }
  
  /**
   * @return - This returns a Vector with two floats: width and height of a 
   * specific device.
   */
  /* 
   * NOTE: Should this class be in the super-class? (amoe)
   */
  public Vector getBounds()
  {
    Vector<Float> v = new Vector<Float>();
    v.add(width);
    v.add(height);
    return v;
  }
  
  /**
   * Specify the region for the next viewer that is displayed.  Viewers will 
   * NOT show controls by default.
   * 
   * @param x - X Position
   * @param y - Y Position
   * @param w - Width
   * @param h - Height
   */
  /* 
   * NOTE: Should this class be in the super-class? (amoe)
   */
  public void setRegion(int x, int y, int w, int h)
  {
    super.width = w;
    super.height = h;
    super.x_pos = x;
    super.y_pos = y;
  }
  
  /**
   * Display the specified DataSet in the specified region, using the specified
   * view type, line, and graph attributes.
   * 
   * @param ds - Dataset to be displayed.
   * @param view_type - The type of view to be displayed.
   */
  public void display(DataSet ds, String view_type)
  {
    //close();    //making sure display_frame is clear
    
    display_source = new ViewManager(ds, view_type,false);
    
    JPanel content_pane = (JPanel)((JFrame)display_source).getContentPane();
    content_pane.setSize((int)width,(int)height);
    
    bimg = ImageRenderWriter.render(content_pane);
  }
  
  /**
   * Display the specified IVirtualArray2D in the specified region, using the 
   * specified view type, line and graph attributes.
   * 
   * @param iva - IVirtualArray2D to be displayed.
   * @param view_type - The type of view to be displayed.
   */
  public void display(IVirtualArray iva, String view_type)
  {
    //close();  //making sure display_frame is clear
    
    Display display_view = (Display)super.display_window;
    JPanel content_pane = null;
    int view_flag;
    
    //checking if the IVirtualArray iva is a recogized data structure
    if(iva instanceof IVirtualArrayList1D)
    {
      //checking if the view type is compatible with the IVirtualArray iva
      if( view_type != ViewManager.SELECTED_GRAPHS && 
          view_type != ViewManager.TABLE )
      {
        System.err.println("'"+view_type+"' is not a compatible view for '"+
            iva.getClass().getSimpleName()+"'.");
        return;
      }
      
      view_flag = translateDSVFlag(view_type);      
      display_view = new Display1D((IVirtualArrayList1D)iva,view_flag,Display1D.CTRL_ALL);
    }
    else if(iva instanceof IVirtualArray2D)
    {
      //checking if the view_type is compatible with the IVirtualArray iva
      if( view_type != ViewManager.CONTOUR && 
          view_type != ViewManager.IMAGE   &&
          view_type != ViewManager.TABLE)
      {
        System.err.println("'"+view_type+"' is not a compatible view for '"+
            iva.getClass().getSimpleName()+"'.");
        return;
      }
      
      view_flag = translateDSVFlag(view_type);
      display_view = new Display2D((IVirtualArray2D)iva,view_flag,Display1D.CTRL_ALL);
    }
    else
    {
      //if the IVirtualArray is not recognized, then leave the method
      System.err.println("'"+iva.getClass().getSimpleName()+
          "' is not a recognized data structure in PrinterDevice.");
      return;
    }
    
    content_pane = (JPanel)display_view.getContentPane();    
    content_pane.setSize((int)width,(int)height);    
    bimg = ImageRenderWriter.render(content_pane);
  }
  
  public void setPrinterName(String printer_name)
  {
    this.printer_name = printer_name;
  }
}
