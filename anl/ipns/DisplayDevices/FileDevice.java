/*
 * $Log$
 * Revision 1.5  2007/07/16 14:52:05  dennis
 * Added parameter, with_controls, to the display method, so that
 * any device type can easily display viewers with or without the
 * controls.
 *
 * Revision 1.4  2007/07/13 21:22:59  amoe
 * Updated NOP comment in getBounds() .
 *
 * Revision 1.3  2007/07/13 01:26:58  amoe
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

public class FileDevice extends GraphicsDevice
{
  private String file_name;
  private BufferedImage bimg = null;
  
  public FileDevice(String file_name)
  {
    this.file_name = file_name;
  }

  /**
   * Set an attribute for the FileDevice.
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
   * Flush any pending output. For FileDevice, this saves an image to a file.
   */
  @Override
  public void print() 
  {
    ImageRenderWriter.write(bimg,file_name);
  }
  
  /**
   * Flush and closes any pending output for the FileDevice.
   */
  @Override
  public void close() 
  {
    // do nothing?    
  }
  
  /**
   * @return - This returns a Vector with two floats: width and height of a 
   * specific device.
   */
  @Override
  public Vector getBounds() 
  {
    // NOP, the file resolution is limitless.
    return null;
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
  @Override
  public void display( IDisplayable disp, boolean with_controls ) 
  {
    JComponent jcomp = disp.getJComponent( with_controls );
    
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
    
    FileDevice prv_dev = new FileDevice("/home/moea/fd_out.jpg");
    prv_dev.setRegion(50,50,650,550);
    
    prv_dev.display(va2d_disp,false);
    prv_dev.print();
  }

}

