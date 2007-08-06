/*
 * $Log$
 * Revision 1.10  2007/08/06 21:17:20  oakgrovej
 * Added control over printable area.
 *
 * Revision 1.9  2007/08/02 21:02:06  oakgrovej
 * Will cut off any empty space of the larger component below or to the right
 * of the smaller compnents within.
 *
 * Revision 1.8  2007/08/02 15:34:11  oakgrovej
 * Uses the bounds: x_pos, y_pos, width, height to set the region of a component passed in to display().  The component is set into a larger component at the particular region.
 *   I also started some code as an attempt to control the margins; it's commented out.
 *
 * Revision 1.7  2007/07/26 22:48:38  amoe
 * -Removed un-needed imports.
 * -Added ORIENTATION and COPIES static final variables.
 * -Removed the setting of a default width and height in the constructor.
 * -Removed debug console prints.
 * -Updated main method.
 *
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
import gov.anl.ipns.Util.Sys.PrintUtilities2;

import java.awt.Color;
import java.util.Hashtable;
import java.util.Vector;

import javax.print.attribute.Attribute;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.MediaPrintableArea;
import javax.print.attribute.standard.OrientationRequested;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

public class PrinterDevice extends GraphicsDevice
{
  public static String ORIENTATION = "orientation";
  public static String COPIES = "copies";
  
  public static String LANDSCAPE = "landscape";
  public static String PORTRAIT = "portrait";
  
  private Hashtable<String,Float> printableAreaValues;
  
  private Hashtable<String, Attribute> attributes = 
    new Hashtable<String, Attribute>();
  private Hashtable<String, Object> values = new Hashtable<String, Object>();
  
  protected HashPrintRequestAttributeSet aset;
  
  private JComponent jcomp;
  private String printer_name;
  private int max_x = 0;
  private int max_y = 0;
  
  public PrinterDevice(String printer_name)
  {
    this.printer_name = printer_name;
    jcomp = new JPanel();
    jcomp.setBounds(0, 0, 600, 600);
    jcomp.setLayout(null);
    jcomp.setBackground(Color.white);
    //jcomp.setBorder(new EtchedBorder());
    aset = new HashPrintRequestAttributeSet();
    buildAttributes();
    buildValues();
    buildPrintableAreaValues();
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
    
    if( name.equals("printableareax") ||
        name.equals("printableareay") ||
        name.equals("printableareawidth") ||
        name.equals("printableareaheight") )
    {
      printableAreaValues.put(name, (Float) value);
      return;
    }
    
    Attribute attrib = (Attribute)Util.TranslateKey(attributes, name);
    
    if( attrib instanceof Copies && value instanceof Integer )
      attrib = new Copies((Integer)value);
    
    aset.add(attrib);
    /*Attribute[] blah = aset.toArray();
    for(int i=0;i<blah.length;i++)
      System.out.println(blah[i]);//*/
  }
  
  /**
   * Flush any pending output. For PrinterDevice, this prints the image from
   * the printer.
   */
  @Override
  public void print()
  {
    // this is an atempt to alter margins.
    
    try
    {
      MediaPrintableArea area = new MediaPrintableArea(
        (Float)Util.TranslateKey(printableAreaValues, "printableareax"),
        (Float)Util.TranslateKey(printableAreaValues, "printableareay"),
        (Float)Util.TranslateKey(printableAreaValues, "printableareawidth"),
        (Float)Util.TranslateKey(printableAreaValues, "printableareaheight"),
        MediaPrintableArea.INCH);
      aset.add(area);
    }
    catch(Exception e)
    {System.out.println(e);}//*/
    
    PrintUtilities2.print(jcomp, printer_name, aset);
    //PrintUtilities2.print_with_dialog(jcomp);
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
    jcomp.setBounds(x_pos, y_pos, width, height);
    
    if( x_pos + width > max_x )
      max_x = x_pos + width;
    if( y_pos + height > max_y )
      max_y = y_pos + height;
    
    this.jcomp.add(jcomp);
    this.jcomp.setSize(max_x,max_y);
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
  
  //part of an atempt to alter margins
  private void buildPrintableAreaValues()
  {
    printableAreaValues = new Hashtable<String, Float>();
    printableAreaValues.put("printableareax", 0f);
    printableAreaValues.put("printableareay", 0f);
    printableAreaValues.put("printableareawidth", 500f);
    printableAreaValues.put("printableareaheight", 500f);
  }//*/
  
  /*
  public static void main(String[] args)throws Exception
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
    VirtualArray2D_Displayable va2d_disp =  
                               new VirtualArray2D_Displayable( v2d, type);
    
    va2d_disp.setViewAttribute("preserve aspect ratio", "true");
    va2d_disp.setViewAttribute("two sided", false);
    va2d_disp.setViewAttribute("color control east", "false");
    va2d_disp.setViewAttribute("color control west", true);
    
    PrinterDevice pr_dev = new PrinterDevice("hp4000_A140");
    
    pr_dev.setRegion(50,50,850,550);
    
    pr_dev.display(va2d_disp,false);    
    
    pr_dev.setDeviceAttribute(ORIENTATION, LANDSCAPE);
    pr_dev.setDeviceAttribute(COPIES, 1);    
    pr_dev.print();
    
    pr_dev.setDeviceAttribute(ORIENTATION, PORTRAIT);
    pr_dev.setDeviceAttribute(COPIES, 1);    
    pr_dev.print();    
  }//*/

}
