/*
 * $Log$
 * Revision 1.2  2007/07/12 19:52:40  dennis
 * Added abstract method to display a Displayable object.
 *
 * Revision 1.1  2007/07/12 15:45:05  amoe
 * Initial commit.
 *
 */
package gov.anl.ipns.DisplayDevices;

import java.awt.Component;
import java.awt.Window;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JFrame;

import DataSetTools.dataset.DataSet;
import DataSetTools.viewer.ViewManager;

import gov.anl.ipns.ViewTools.Components.IVirtualArray;
import gov.anl.ipns.ViewTools.Components.IVirtualArray2D;
import gov.anl.ipns.ViewTools.Displays.Display1D;
import gov.anl.ipns.ViewTools.Displays.Display2D;

/**
 * This is an abstract class that for ScreenDevice, FileDevice, and 
 * PrinterDevice.  It defines the requirements and base fields and methods
 * for these classes.
 */
public abstract class GraphicsDevice 
{
  protected Hashtable requested_attributes;
  
  protected Hashtable selected_graph_attributes;
  protected Hashtable graph_view_comp_attributes;
  protected Hashtable image_view_comp_attributes;
  protected Hashtable contour_view_comp_attributes;

  /*
   * NOTE: I added this so every extended class can have a common display
   * window. (amoe)
   */
  protected Window display_window = null;
  
  /*
   * Default bounds
   *  
   *  NOTE: I added these so getBounds() would have something to get and
   *  setRegion(..) would have something to set. (amoe)
   */
  protected float x_pos = -1;
  protected float y_pos = -1;
  protected float width = -1;
  protected float height = -1;
  
  public GraphicsDevice()
  {    
  }
  
  /**
   * Set an attribute such as portrait/landscape, page size, file
   * resolution, or file format.
   * 
   * @param name - Name (key) for the attribute.
   * @param value - Value for the attribute
   */
  public abstract void setDeviceAttribute(String name,Object value);
  
  /**
   * Flush any pending output.  For printer, print a page. No-Op for Screen.
   * For File, flush the writer.
   */
  public abstract void print();
  
  /**
   * This will close and flush a file, flush and print a page, or just free
   * resources for a display
   */
  public abstract void close();
  
  /**
   * @return - This returns a Vector with two floats: width and height of a 
   * specific device.
   */
  /*
   * ??In which units should this return: inches, cm, pixels, points..??
   */
  public abstract Vector getBounds();
  
  /**
   * Specify the region for the next viewer that is displayed.  Viewers will 
   * NOT show controls by default.
   * 
   * @param x - X Position
   * @param y - Y Position
   * @param w - Width
   * @param h - Height
   */
  public abstract void setRegion(int x, int y, int w, int h);
  
  /**
   * Background, gridlines, x, y, range title, etc.  for OVERALL display,
   * NOT a particular index.
   * 
   * @param name
   * @param value
   */
  public void setViewAttribute(String name, Object value)
  {
    
  }
  
  /**
   * Set characteristics for one graph line identified by the index of that 
   * graph in the DataSet being desplayed.  NOTE 1: Some processing of the 
   * style info may be needed since the index is a separate integer variable 
   * here, but may be part of the property name String in the ObjectState.
   * 
   * @param index - Index of graph line
   * @param name - Attribute name
   * @param value - Attribute value
   */
  /*
   * NOTE: I added the 'index' argument, since it was listed in the 
   * description, and not in the UML. (amoe)
   */
  public void setLineAttribute(int index, String name, Object value)
  {
    
  }

  /**
   * Display the specified IDisplayable in the specified region.
   * 
   * @param displayable - The IDisplayable object to be displayed.
   */
  public abstract void display( Displayable displayable );


  /**
   * Display the specified DataSet in the specified region, using the specified
   * view type, line, and graph attributes.
   * 
   * @param ds - Dataset to be displayed.
   * @param view_type - The type of view to be displayed.
   */
  public abstract void display(DataSet ds, String view_type);
  
  /**
   * Display the specified IVirtualArray2D in the specified region, using the 
   * specified view type, line and graph attributes.
   * 
   * @param iva - IVirtualArray2D to be displayed.
   * @param view_type - The type of view to be displayed.
   */
  public abstract void display(IVirtualArray iva, String view_type);
  
  protected int translateDSVFlag(String dsv_flag)
  {
    //ViewManager.CONTOUR
    //ViewManager.DIFFERENCE_GRAPH
    //ViewManager.HKL_SLICE
    //ViewManager.IMAGE
    //ViewManager.POINTEDAT_TABLE
    //ViewManager.SCROLLED_GRAPHS
    //ViewManager.SELECTED_GRAPHS
    //ViewManager.TABLE
    //ViewManager.THREE_D    
    //Display1D.GRAPH
    //Display1D.TABLE
    //Display2D.CONTOUR
    //Display2D.IMAGE
    //Display2D.TABLE
    
    //public static final String DS_CONTOUR          = ViewManager.CONTOUR;
    //public static final String DS_DIFFERENCE_GRAPH = ViewManager.DIFFERENCE_GRAPH;
    //public static final String DS_HKL_SLICE        = ViewManager.HKL_SLICE;
    //public static final String DS_IMAGE            = ViewManager.IMAGE;
    //public static final String DS_POINTEDAT_TABLER = ViewManager.POINTEDAT_TABLE;
    //public static final String DS_SCROLLED_GRAPHS  = ViewManager.SCROLLED_GRAPHS;
    //public static final String DS_SELECTED_GRAPHS  = ViewManager.SELECTED_GRAPHS;
    //public static final String DS_TABLE            = ViewManager.TABLE;
    //public static final String DS_THREE_D          = ViewManager.THREE_D;  
    //public static final String VA_GRAPH   = ViewManager.SELECTED_GRAPHS;
    //public static final String VA_TABLE   = ViewManager.TABLE;
    //public static final String VA_CONTOUR = ViewManager.CONTOUR;
    //public static final String VA_IMAGE   = ViewManager.IMAGE;
    
    if((dsv_flag == ViewManager.SELECTED_GRAPHS))
    {
      return Display1D.GRAPH;
    }
    else if(dsv_flag == ViewManager.TABLE)
    {
      return Display1D.TABLE;
    }
    else if(dsv_flag == ViewManager.CONTOUR)
    {
      return Display2D.CONTOUR;
    }
    else if(dsv_flag == ViewManager.IMAGE)
    {
      return Display2D.IMAGE;
    }
    
    return -1;
  }
}
