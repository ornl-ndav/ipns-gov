/*
 * $Log$
 * Revision 1.3  2007/07/13 01:28:42  amoe
 * - Removed display( IVirtualArray ) and display( DataSet )
 * - Added display( IDisplayable ) and display( JComponent )
 * - Cleaned up comments
 *
 * Revision 1.2  2007/07/12 19:53:11  dennis
 * Added "stub" for method to display a Displayable.
 *
 * Revision 1.1  2007/07/12 15:45:05  amoe
 * Initial commit.
 *
 */
package gov.anl.ipns.DisplayDevices;

import gov.anl.ipns.DisplayDevices.IDisplayable;
import gov.anl.ipns.DisplayDevices.VirtualArray2D_Displayable;
import gov.anl.ipns.Util.File.ImageRenderWriter;
import gov.anl.ipns.ViewTools.Components.VirtualArray2D;

import java.awt.image.BufferedImage;
import java.util.Vector;

import javax.swing.JComponent;

public class PrinterDevice extends GraphicsDevice
{
  private String printer_name;
  private BufferedImage bimg = null;
  
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
  @Override
  public void setDeviceAttribute(String name, Object value) 
  {
    // TODO Auto-generated method stub
    
  }
  
  /**
   * Flush any pending output. For PrinterDevice, this prints the image from
   * the printer.
   */
  @Override
  public void print() 
  {
    // TODO Auto-generated method stub
    
  }

  /**
   * Flush and closes any pending output for the PrinterDevice.
   */
  @Override
  public void close()
  {
    // TODO Auto-generated method stub
    
  }
  
  /**
   * @return - This returns a Vector with two floats: width and height of a 
   * specific device.
   */
  @Override
  public Vector getBounds() 
  {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * Display the specified IDisplayable with the specified region, view type, 
   * line, and graph attributes.
   * 
   * @param disp - IDisplayable to be displayed.
   */
  @Override
  public void display(IDisplayable disp) 
  {
    JComponent jcomp = disp.getJComponent(false);
    
    display(jcomp); 
  }

  /**
   * Display the specified JComponent with the specified region, view type, 
   * line, and graph attributes.
   * 
   * @param jcomp - JComponent to be displayed.
   */
  @Override
  public void display(JComponent jcomp) 
  {
    jcomp.setSize( (int)width, (int)height );
    bimg = ImageRenderWriter.render(jcomp);  
  }
  
  public static void main(String[] args)
  {
    String type = "ImageV2D";
    VirtualArray2D v2d = new VirtualArray2D( 
             new float[][]{
                      { 1,1,1,1,1,1,1,1,1 },
                      { 2,2,2,2,2,2,2,2,2 },
                      { 3,3,3,3,3,3,3,3,3 },
                      { 4,4,4,4,4,4,4,4,4 },
                      { 5,5,5,5,5,5,5,5,5 },
                      { 6,6,6,6,6,6,6,6,6 }
                      
             });
    VirtualArray2D_Displayable va2d_disp =  
                               new VirtualArray2D_Displayable( v2d, type);
    
    va2d_disp.setViewAttribute("ColorModel", "Rainbow");
    va2d_disp.setViewAttribute("Axes Displayed", new Integer(2));
    
    PrinterDevice pr_dev = new PrinterDevice("");
    pr_dev.setRegion(50,50,650,550);
    
    pr_dev.display(va2d_disp);
    pr_dev.print();
  }

}
