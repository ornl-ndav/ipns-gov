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
import javax.swing.*;
import java.awt.print.*;

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

public class PrintUtilities implements Printable {
  private Component componentToBePrinted;

  public static void printComponent(Component c) {
    new PrintUtilities(c).print();
  }
  
  public PrintUtilities(Component componentToBePrinted) {
    this.componentToBePrinted = componentToBePrinted;
  }
  
  public void print() {
    PrinterJob printJob = PrinterJob.getPrinterJob();
    printJob.setPrintable(this);
    if (printJob.printDialog())
      try {//System.out.println("aft dialog pageFormat="+printJob.getDefaultPage().getOrientation()); 
        printJob.print();
      } catch(PrinterException pe) {
        System.out.println("Error printing: " + pe);
      }
  }

  public int print(Graphics g, PageFormat pageFormat, int pageIndex) {
    //System.out.println("in print pageFormat="+pageFormat.getOrientation()+","+
    //    pageFormat.LANDSCAPE+","+pageFormat.PORTRAIT); 
    if (pageIndex > 0) {
      return(NO_SUCH_PAGE);
    } else {
      Graphics2D g2d = (Graphics2D)g;
       
      Rectangle R = componentToBePrinted.getBounds();
      //Font F= componentToBePrinted.getFont();
     // int fSize= F.getSize();
      int dpi= componentToBePrinted.getToolkit().getScreenResolution();
      double xscale,yscale;
      xscale = (double)(pageFormat.getImageableWidth())/(R.width);
      yscale=(double)(pageFormat.getImageableHeight())/(R.height);
      if(yscale < xscale) 
         xscale= yscale; 
      g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
      g2d.scale(xscale ,xscale); 
     double w=R.width*xscale;
     double h = R.height*xscale;
     if( w > pageFormat.getImageableWidth()) w=pageFormat.getImageableWidth();
     if( h > pageFormat.getImageableHeight()) h=pageFormat.getImageableHeight();


     //g2d.translate(pageFormat.getImageableX(),pageFormat.getImageableY() );
                   
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
  public static void disableDoubleBuffering(Component c) {
    RepaintManager currentManager = RepaintManager.currentManager(c);
    currentManager.setDoubleBufferingEnabled(false);
  }

  /** Re-enables double buffering globally. */
  
  public static void enableDoubleBuffering(Component c) {
    RepaintManager currentManager = RepaintManager.currentManager(c);
    currentManager.setDoubleBufferingEnabled(true);
  }
}
