/*
 * File: PrinterDevice.java 
 *  
 * Copyright (C) 2007     Andy Moe
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307, USA.
 *
 * Contact :  Dennis Mikkelson<mikkelsond@uwstout.edu>
 *            MSCS Department
 *            Menomonie, WI. 54751
 *            (715)-232-2291
 *
 * This work was supported by the National Science Foundation under grant
 * number DMR-0426797, and by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 *
 *
 * Modified:
 *
 * $Log$
 * Revision 1.14  2007/09/09 16:08:44  rmikk
 * Added code to handle the case where no printer can be found.
 *
 * Revision 1.13  2007/08/22 15:26:15  rmikk
 * Added GPL
 * Size of large JComponent not calculated until a display command is given.
 *   Hopefully all attributes( especially paper size and orientation) have been set.
 *   Kept track of orientation
 *   Set Default MediaSize to be MediaSizeName.NA_LETTER
 *   Implemented getBounds to get the imageable width and height corresponding
 *       to the MediaSizeName and orientation( flips width and height on landscape)
 *    Added letter and legal attributes for orientation keys.
 *
 * Revision 1.12  2007/08/20 19:34:15  rmikk
 * Eliminated code to  set the MediaPrintableArea. It turned out to be 500 inches
 *   by 500 inches
 *
 * Revision 1.11  2007/08/08 16:45:22  oakgrovej
 * Added private class PrintThread in order to foce the print to start later
 *
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

import javax.print.DocFlavor;
import javax.print.attribute.Attribute;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.*;
import javax.print.attribute.standard.OrientationRequested;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
//import javax.swing.border.EtchedBorder;
import javax.print.*;

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
  
  private JComponent jcomp ;
  private String printer_name;

  private boolean portrait;
  
  public PrinterDevice(String printer_name)
  {
    this.printer_name = printer_name;
    /*jcomp = new JPanel();
    jcomp.setBounds(0, 0, 600, 600);
    jcomp.setLayout(null);
    jcomp.setBackground(Color.white);
    */
    jcomp = null;
    portrait = true;
    //jcomp.setBorder(new EtchedBorder());
    aset = new HashPrintRequestAttributeSet();
    aset.add(  javax.print.attribute.standard.MediaSizeName.NA_LETTER );
    PrintService pservice = PrintUtilities2.get_print_service( this.printer_name , aset );
    if( pservice == null)
       aset = new HashPrintRequestAttributeSet();
    buildAttributes();
    buildValues();
    buildPrintableAreaValues();
  }
  
  
  // Sets up jcomp if it is not null.  Its dimensions should be determined
  //    after the paper size and orientation attributes have been set.
  private void setUpContainerComponent(){
     
     if( jcomp != null)
        return;
     jcomp = new JPanel();
     
     Vector<Float> V = getBounds();
     jcomp.setBounds(0, 0,V.firstElement().intValue(), 
                           V.lastElement().intValue());
     
     jcomp.setLayout(null);
     jcomp.setBackground(Color.white);     
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
      if( value.equals( "landscape" )) 
         portrait = false;
      else 
         portrait = true;
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
    
    PrintThread runPrint = new PrintThread();
    
    SwingUtilities.invokeLater(runPrint);
  }

  /**
   * Flush and closes any pending output for the PrinterDevice.
   */
 
  public void close()
  {
    // TODO invoke a print if not done and something is  pending
    
  }
  
  /**
   * @return - This returns a Vector with two floats: width and height of a 
   * specific device.
   * 
   * NOTE: Call this method after attributes are set, especially the paper size
   *        and orientation attributes.
   */
 
  public Vector getBounds() 
  {
    
     PrintService pservice = PrintUtilities2.get_print_service( this.printer_name , aset );
     
     MediaPrintableArea[] X = null;
     
     if( pservice != null)
        X=(MediaPrintableArea[])pservice.getSupportedAttributeValues
                                                       ( MediaPrintableArea.class , 
                                                         DocFlavor.SERVICE_FORMATTED.PRINTABLE, 
                                                         aset );
     
     Vector<Float> Result = new Vector<Float>();
     float width=8f, 
             height=10.5f;
     
     if( X != null && X.length >0 ){
        
        width = X[0].getWidth(  MediaPrintableArea.INCH );
        height =X[0].getHeight(  MediaPrintableArea.INCH );
     }
       
     
     if( portrait){
        
        Result.add( 72*width);
        Result.add( 72*height);
        
     }else{
        
        Result.add( 72*height);
        Result.add( 72*width);
        
     }
     
     //This gets the total page size.  Can possibly be used reduce margins by setting
     // the media size in aset--though getSupportedAttributeValues is supposed to get max
     
    /* MediaSize ms = MediaSize.getMediaSizeForName(MediaSizeName.NA_LETTER );
     float[]F = ms.getSize( MediaSize.INCH);
     System.out.println("Media size="+F[0]+","+F[1] );
     */
     
     return Result;
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
 
    setUpContainerComponent();
    this.jcomp.add(jcomp);
  
  }
  
  private void buildAttributes()
  {
    attributes.put( "letter" , javax.print.attribute.standard.MediaSize.NA.LETTER );
    
    attributes.put( "legal" , javax.print.attribute.standard.MediaSize.NA.LEGAL );
    attributes.put("orientation.portrait", OrientationRequested.PORTRAIT);
    attributes.put("orientation.landscape", OrientationRequested.LANDSCAPE);
    attributes.put("copies", new Copies(1));
  }
  
  private void buildValues()
  {
    values.put("portrait", ".portrait");
    values.put("landscape", ".landscape");
  }
  
  //part of an attempt to alter margins
  private void buildPrintableAreaValues()
  {
    printableAreaValues = new Hashtable<String, Float>();
    printableAreaValues.put("printableareax", 0f);
    printableAreaValues.put("printableareay", 0f);
    printableAreaValues.put("printableareawidth", 500f);
    printableAreaValues.put("printableareaheight", 500f);
  }//*/
  
  private class PrintThread implements Runnable
  {

    public void run()
    {
       setUpContainerComponent();
      PrintUtilities2.print(jcomp, printer_name, aset);
      
    }
    
  }
  
  
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
