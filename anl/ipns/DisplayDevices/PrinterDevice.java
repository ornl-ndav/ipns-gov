/*
 * $Log$
 * Revision 1.6  2007/07/25 22:26:00  oakgrovej
 * Added Hashtables and logic for setting Attributes: orientation and number of pages are the only ones currently.
 *
 * Revision 1.5  2007/07/17 16:17:05  oakgrovej
 * Added Throws Exception where needed
 *
 * Revision 1.4  2007/07/16 14:52:05  dennis
 * Added parameter, with_controls, to the display method, so that
 * any device type can easily display viewers with or without the
 * controls.
 *
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
import gov.anl.ipns.Util.Sys.PrintUtilities2;
import gov.anl.ipns.ViewTools.Components.VirtualArray2D;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Hashtable;
import java.util.Vector;

import javax.print.attribute.Attribute;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.OrientationRequested;
import javax.swing.JComponent;

public class PrinterDevice extends GraphicsDevice
{
  public static String LANDSCAPE = "landscape";
  public static String Portraite = "landscape";
  
  private Hashtable<String, Attribute> attributes = 
    new Hashtable<String, Attribute>();
  private Hashtable<String, Object> values = new Hashtable<String, Object>();
  
  protected HashPrintRequestAttributeSet aset;
  
  private JComponent jcomp;
  private String printer_name;
  private PrintUtilities2 prinUtil;
  
  public PrinterDevice(String printer_name)
  {
    this.printer_name = printer_name;
    aset = new HashPrintRequestAttributeSet();
    buildAttributes();
    buildValues();
    width = 500;
    height = 500;
  }
  
  /**
   * Set an attribute for the PrinterDevice.
   * 
   * @param name - Name (key) for the attribute.
   * @param value - Value for the attribute
   */
  @Override
  public void setDeviceAttribute(String name, Object value) throws Exception
  {
    name = name.toLowerCase();
    
    if( name.equals("orientation") && value instanceof String)
    {
      value = ((String)value).toLowerCase();
      name += (String)Util.TranslateKey( values, (String)value );
    }
    
    Attribute attrib = (Attribute)Util.TranslateKey(attributes, name);
    
    if( attrib instanceof Copies && value instanceof Integer )
      attrib = new Copies((Integer)value);
    
    System.out.println("setting attribute:\t"+attrib+":     \t"
        +attrib.getClass());
    aset.add(attrib);
  }
  
  /**
   * Flush any pending output. For PrinterDevice, this prints the image from
   * the printer.
   */
  @Override
  public void print() 
  {
    //System.out.println("PrinterDevice.print()");
    Attribute[] array = aset.toArray();
   /* for( int i = 0; i <array.length; i++ )
    {
      System.out.println(array[i]+":\t"+array[i].getClass());
    }*/
    //prinUtil = new PrinterUtilities2(aset);
    prinUtil = new PrintUtilities2(jcomp, printer_name, aset);
    prinUtil.print();
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
    this.jcomp = jcomp;
    this.jcomp.setSize( (int)width, (int)height );  
  }
  
  private void buildAttributes()
  {
    attributes.put("orientation.portrait", OrientationRequested.PORTRAIT);
    attributes.put("orientation.landscape", OrientationRequested.LANDSCAPE);
    attributes.put("copies", new Copies(1));
  }
  
  private void buildValues()
  {
    values.put("portrait", ".portrait");
    values.put("landscape", ".landscape");
  }
  
  /*public static void main(String[] args)throws Exception
  {
    String type = "Image";
    VirtualArray2D v2d = new VirtualArray2D( 
             new float[][]{
                      { 1,1,1,1,1,1,1,1,1 },
                      { 2,2,2,2,2,2,2,2,2 },
                      { 3,3,3,3,3,3,3,3,3 },
                      { 4,4,4,4,4,4,4,4,4 },
                      { 5,5,5,5,5,5,5,5,5 },
                      { 6,6,6,6,6,6,6,6,6 }
                      
             });//
    //VirtualArray2D_Displayable va2d_disp =  
    //                           new VirtualArray2D_Displayable( v2d, type);
    
    //va2d_disp.setViewAttribute("ColorModel", "Rainbow");
    //va2d_disp.setViewAttribute("Axes Displayed", new Integer(2));
    
    PrinterDevice pr_dev = new PrinterDevice("");
    pr_dev.print();
    pr_dev.setDeviceAttribute("orientation", "portrait");
    pr_dev.setDeviceAttribute("orientation", "landscape");
    pr_dev.setDeviceAttribute("copies", 23);
    pr_dev.print();
    ///pr_dev.setRegion(50,50,650,550);
    
    //pr_dev.display(va2d_disp,false);
    //pr_dev.print();
  }//*/

}
