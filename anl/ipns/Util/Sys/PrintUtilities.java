/*
 * File:  PrintUtilities.java
 *
 * Copyright (C) 2002, Alok Chatterjee, Ruth Mikkelson
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
 * Contact : Alok Chatterjee <achatterjee@anl.gov>
 *           Intense Pulsed Neutron Source Division
 *           Argonne National Laboratory
 *           9700 South Cass Avenue, Bldg 360
 *           Argonne, IL 60439-4845, USA
 *
 * This work was supported by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 * Modified:
 *
 *  $Log$
 *  Revision 1.13  2005/03/23 22:02:28  dennis
 *  Reformated for legibility.
 *
 *  Revision 1.12  2004/06/22 15:55:21  robertsonj
 *  The printerDialog now remembers the last printer chosen and sets itself
 *  to that printer automatically.  it will only remember during one session
 *  of ISAW.  When you start Isaw again it will revert back to the system
 *  default printer.
 *
 *  Revision 1.12  2004/06/22 robertsonj
 *  The last chosen printer is now the printer that is selected for printing.
 * 
 *  Revision 1.11  2004/05/27 19:14:10  robertsonj
 *  *** empty log message ***
 *
 *  Revision 1.10  2004/03/12 17:21:55  hammonds
 *  Moved from IsawGUI to gov.anl.ipns.Util.Sys
 *
 *  Revision 1.9  2002/12/10 22:14:18  pfpeterson
 *  Fixed javadoc
 *
 *  Revision 1.8  2002/11/27 23:27:07  pfpeterson
 *  standardized header
 *
 *  Revision 1.7  2002/05/31 19:40:08  chatterjee
 *  Left edge was not getting printed. Fixed now.
 *
 *  Revision 1.6  2002/05/31 16:31:17  chatterjee
 *  Bug
 *
*/

package gov.anl.ipns.Util.Sys;

import java.awt.*;
import java.awt.print.*;
import java.util.*;
import java.io.*;
import javax.print.*;
import javax.print.attribute.*;
import javax.swing.RepaintManager;

/** A simple utility class that lets you very simply print
 *  an arbitrary component. Just pass the component to the
 *  PrintUtilities.printComponent. The component you want to
 *  print doesn't need a print method and doesn't have to
 *  implement any interface or do anything special at all.
 *  <P>
 *  If you are going to be printing many times, it is marginally more 
 *  efficient to first do the following:
 *  <PRE>
 *    PrintUtilities printHelper = new PrintUtilities(theComponent);
 *  </PRE>
 *  then later do printHelper.print(). But this is a very tiny
 *  difference, so in most cases just do the simpler
 *  PrintUtilities.printComponent(componentToBePrinted).
 *
 *  7/99 Marty Hall, http://www.apl.jhu.edu/~hall/java/
 *  May be freely used or adapted.
 */

//getting nullpointerexceptions at save and load.  do something about this.

public class PrintUtilities implements Printable {

  private Component componentToBePrinted;
  private static Properties myProperties = new Properties();
  private FileOutputStream bw;
  private FileInputStream br;

  public static void printComponent(Component c) {
    new PrintUtilities(c).print();
  }
  
  public PrintUtilities(Component componentToBePrinted) {
    this.componentToBePrinted = componentToBePrinted;
    // create a file outputstream/fileinputstream to save and load the 
    // properties file
    try
    {  
       bw = new FileOutputStream("properties.dat");
       br = new FileInputStream("properties.dat");
    }
    catch(IOException io)
    {
      System.out.println("IO Exception: " + io );
    }
    // make sure there is something in the properties file before trying 
    // to load data from it
    try
    {
      if (br.available() != 0)
	myProperties.load(br);
    }
    catch(IOException io1)
    {
      System.out.println("IO Exception: " + io1);
    }
    // componentToBePrinted.validate();
    // componentToBePrinted.show();
  }
  
  public void print() 
  {
    PrinterJob printJob = PrinterJob.getPrinterJob();
    // String printerName = "houdini HP LaserJet 4000 PS in A140";
	
    // PageFormat newFormat = printJob.defaultPage();
    HashPrintRequestAttributeSet aset2 = new HashPrintRequestAttributeSet();

    // docflavor in order to use the printservicelookup to get the available 
    // printers so we can make sure that the printer is installed before 
    // trying to print to it
    DocFlavor myFormat = DocFlavor.SERVICE_FORMATTED.PRINTABLE;
    PrintService[] services =
                       PrintServiceLookup.lookupPrintServices(myFormat, aset2);

    // find the printer that matches the printer you have saved in the 
    // properties file
    if (!(myProperties.isEmpty()))
    {
      for (int i = 0; i < services.length; i++ )
      {
        if ( myProperties.getProperty("IsawDefaultPrinter").
                                       equals(services[i].getName()))
        {
           try
           {
             printJob.setPrintService(services[i]);	
           }
           catch(PrinterException pe1)
           {
             System.out.println("Error Printing : " + pe1);
           }
        }
      }	
    }	
    printJob.setPrintable(this);
    if (printJob.printDialog(aset2))
    {
      try
      { 
        // System.out.println("aft dialog pageFormat=" + 
        //                     newFormat.getOrientation()); 
        printJob.print(aset2);
      }
      catch(PrinterException pe) 
      {
        System.out.println("Error printing: " + pe);
      }
    }
    PrintService myService = printJob.getPrintService();

    // set the last printer you used into the properties file and save it 
    // there for later use
    setDefaultPrinterProperty("IsawDefaultPrinter", myService.getName(), bw);
 }


  public int print(Graphics g, PageFormat pageFormat, int pageIndex) 
  {
   // System.out.println("in print pageFormat="+pageFormat.getOrientation()+","+
   //                     pageFormat.LANDSCAPE+","+pageFormat.PORTRAIT); 
	
    if (pageIndex > 0) 
      return(NO_SUCH_PAGE);
    else 
    {
      Graphics2D g2d = (Graphics2D)g;
       
      Rectangle R = componentToBePrinted.getBounds();
      // Font F = componentToBePrinted.getFont();
      // int fSize = F.getSize();
      int dpi = componentToBePrinted.getToolkit().getScreenResolution();
      double xscale,
             yscale;

      xscale = (double)(pageFormat.getImageableWidth())/(R.width);
      yscale=(double)(pageFormat.getImageableHeight())/(R.height);

      if(yscale < xscale) 
         xscale= yscale; 

      g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
      g2d.scale(xscale ,xscale); 

      double w = R.width*xscale;
      double h = R.height*xscale;
      if ( w > pageFormat.getImageableWidth() ) 
         w = pageFormat.getImageableWidth();
      if ( h > pageFormat.getImageableHeight() ) 
         h = pageFormat.getImageableHeight();

      // g2d.translate(pageFormat.getImageableX(),pageFormat.getImageableY() );
                   
      disableDoubleBuffering(componentToBePrinted);
      componentToBePrinted.paint(g2d);
      enableDoubleBuffering(componentToBePrinted);
      return(PAGE_EXISTS);
    }
  }

  /** The speed and quality of printing suffers dramatically if
   *  any of the containers have double buffering turned on.
   *  So this turns if off globally.
   *  @see #enableDoubleBuffering(Component)
   */
  public static void disableDoubleBuffering(Component c) 
  {
    RepaintManager currentManager = RepaintManager.currentManager(c);
    currentManager.setDoubleBufferingEnabled(false);
  }

  /** Re-enables double buffering globally. */
  
  public static void enableDoubleBuffering(Component c) 
  {
    RepaintManager currentManager = RepaintManager.currentManager(c);
    currentManager.setDoubleBufferingEnabled(true);
  }

  public static void setDefaultPrinterProperty( String           name, 
                                                String           value, 
                                                FileOutputStream bw )
  {
    myProperties.setProperty(name, value);
    myProperties.save(bw,name);
  }

}
