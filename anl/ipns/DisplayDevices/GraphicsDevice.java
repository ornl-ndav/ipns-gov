/*
 * $Log$
 * Revision 1.5  2007/07/16 14:50:31  dennis
 * Added parameter, with_controls, to the display method, so that the
 * display can be done with or without controls for any GraphicsDevice.
 * Added public static final strings with names for the derived classes,
 * FILE, PRINTER, SCREEN, PREVIEW.  Implmenented the getInstance()
 * method.
 *
 * Revision 1.4  2007/07/13 21:19:01  amoe
 * Removed the max_width and the max_height.  Now, these bounds
 * will be defined in the sub-classes of GraphicsDevice.
 *
 * Revision 1.3  2007/07/13 01:36:43  amoe
 * - Removed graph specific hash tables
 * - Removed  abstract methods display( IVirtualArray ) and display( DataSet )
 * - Added abstract methods display( IDisplayable ) and display( JComponent )
 * - Added static methods that call the abstract methods.  These static methods
 *   are the same name, but a different signature as the abstract methods.
 * - Cleaned up comments
 *
 * Revision 1.2  2007/07/12 19:52:40  dennis
 * Added abstract method to display a Displayable object.
 *
 * Revision 1.1  2007/07/12 15:45:05  amoe
 * Initial commit.
 *
 */
package gov.anl.ipns.DisplayDevices;

import gov.anl.ipns.DisplayDevices.IDisplayable;

import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JComponent;

/**
 * This is an abstract class that for ScreenDevice, FileDevice, and 
 * PrinterDevice.  It defines the requirements and base fields and methods
 * for these classes.
 */
public abstract class GraphicsDevice 
{
  public static final String PRINTER = "Printer";
  public static final String FILE    = "File";
  public static final String SCREEN  = "Screen";
  public static final String PREVIEW = "Preview";

  protected float x_pos = -1;
  protected float y_pos = -1;
  protected float width = -1;
  protected float height = -1;
  
  protected Hashtable requested_attributes;
  

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
  public void setRegion(int x, int y, int w, int h)
  {
    x_pos = x;
    y_pos = y;
    width = w;
    height = h;
  }

  
  /**
   * Display the specified IDisplayable with the specified region, view type, 
   * line, and graph attributes.
   * 
   * @param disp          - IDisplayable to be displayed.
   * @param with_controls - boolean indicating whether to include any 
   *                        associated controls, or just display the
   *                        component showing the data.
   */
  public abstract void display( IDisplayable disp, boolean with_controls );

  
  /**
   * Display the specified JComponent with the specified region, view type, 
   * line, and graph attributes.
   * 
   * @param jcomp - JComponent to be displayed.
   */
  public abstract void display(JComponent jcomp);


  /**
   * Construct and return a graphics device of the specified type, using
   * the specified file or printer name, if needed. 
   *
   * @param type  The type of GraphicsDevice to construct,
   *              GraphicsDevice.PRINTER, GraphicsDevice.FILE, 
   *              GraphicsDevice.SCREEN, etc.
   *
   * @param name  The file or printer name for file or printer devices.
   *              This parameter is ignored for other device types.
   *
   * @return  A new GraphicsDevice of the requested type, or a new
   *          ScreenDevice if an error occured during the construction
   *          of the requested device.
   */  
  public static GraphicsDevice getInstance( String type, String name )
  {
    if ( GraphicsDevice.FILE.equals(name) )
      return new FileDevice( name );

    else if ( GraphicsDevice.PRINTER.equals(name) )
      return new PrinterDevice( name );

    else if ( GraphicsDevice.PREVIEW.equals(name) )
      return new PreviewDevice();
  
    else 
      return new ScreenDevice();
  }
  

  /**
   * Set an attribute such as portrait/landscape, page size, file
   * resolution, or file format.
   * 
   * @param gd    - The graphics device for which the attribute is set.
   * @param name  - Name (key) for the attribute.
   * @param value - Value for the attribute
   */
  public static void setDeviceAttribute( GraphicsDevice gd, 
                                         String         name,
                                         Object         value )
  {
    gd.setDeviceAttribute(name,value);
  }

  
  /**
   * Flush any pending output.  For printer, print a page. No-Op for Screen.
   * For File, flush the writer.
   *
   * @param gd    - The graphics device to which output will be sent.
   */
  public static void print(GraphicsDevice gd)
  {
    gd.print();
  }

  
  /**
   * This will flush and close a file, flush and print a page, or just free
   * resources for a display
   *
   * @param gd    - The graphics device which will be closed.
   */
  public static void close(GraphicsDevice gd)
  {
    gd.close();
  }

  
  /**
   * @return - This returns a Vector with two floats: width and height of a 
   * specific device.
   *
   * @param gd    - The graphics device whose bounds will be returned.
   */
  public static Vector getBounds(GraphicsDevice gd)
  {
    return gd.getBounds();
  }

  
  /**
   * Specify the region for the next viewer that is displayed. 
   * 
   * @param gd - The graphics device where the region will be used.
   * @param x  - X Position
   * @param y  - Y Position
   * @param w  - Width
   * @param h  - Height
   */
  public static void setRegion(GraphicsDevice gd, int x, int y, int w, int h)
  {
    gd.setRegion(x,y,w,h);
  }

  
  /**
   * Display the specified IDisplayable with the specified region, view type, 
   * line, and graph attributes.
   * 
   * @param gd            - The graphics device where the display will be sent.
   * @param disp          - IDisplayable to be displayed.
   * @param with_controls - boolean indicating whether to include any 
   *                        associated controls, or just display the
   *                        component showing the data.
   */
  public static void display( GraphicsDevice gd, 
                              IDisplayable   disp, 
                              boolean        with_controls )
  {
    gd.display( disp, with_controls );
  }

  
  /**
   * Display the specified JComponent with the specified region, view type, 
   * line, and graph attributes.
   * 
   * @param jcomp - JComponent to be displayed.
   */
  public static void display(GraphicsDevice gd, JComponent jcomp)
  {
    gd.display(jcomp);
  }
  
}
