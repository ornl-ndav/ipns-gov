package IsawGUI;

import java.awt.*;
import javax.swing.*;
import java.awt.print.*;

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
    System.out.println("Inside printutilities");
    if (printJob.printDialog())
      try {
        printJob.print();
      } catch(PrinterException pe) {
        System.out.println("Error printing: " + pe);
      }
  }

  public int print(Graphics g, PageFormat pageFormat, int pageIndex) {
     int screenResolution = getToolkit().getScreenResolution();
     double pixelsPerPoint = (double)screenResolution/72d;
     
     int keepWidth = getSize().width;
     int keepHeight = getSize().height;
     if (pageIndex == 0) 
     {
        pageWidth = (int)(pageFormat.geImageableWidth()*pixelsPerPoint);
        pageHeight = (int)(pageFormat.geImageableHeight()*pixelsPerPoint);

        setSize(pageWidth, pageHeight);
        Graphics temp = graphics.create();
        printAll(temp);
        temp = null;

        int new Height = getPreferredSize().height;
        int (newHeight%pageHeight ==0)
        numPages = newHeight/pageHeight + 1;

     }

     else if (pageIndex >= numPages)
     {
        return (Printable.NO_SUCH_PAGE);
     } 

    int newXOrigin = (int) (pageFormat.getImageableX()*pixelsPerPoint);
    int newYOrigin = (int) (pageFormat.getImageableY()*pixelsPerPoint);

    setSize(pageWidth, pageHeight);

    if(grahics instanceof Graphics2D)
    {
       Graphics2D g2d = (Graphics2D)grahics;
       g2D.scale(1/pixelsPerPoint, i/pixelsPerPoint);
    } 
    graphics.translate(newXOrigin, newYOrigin - (pageIndex*pageHeight);
    graphics.setClip(0,(pageIndex*pageHeight), pageWidth,pageHeight);
    printAll(graphics);
    setSize(graphics);

    return(Printable.PAGE_EXISTS);




/*
   if (pageIndex > 0) {
      return(NO_SUCH_PAGE);
    } else {
      Graphics2D g2d = (Graphics2D)g;

System.out.println("Inside printutilities x nad y are "+pageFormat.getImageableX());
System.out.println("Inside printutilities x nad y are "+pageFormat.getImageableY());

      g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
      disableDoubleBuffering(componentToBePrinted);
      componentToBePrinted.paint(g2d);
      enableDoubleBuffering(componentToBePrinted);
      return(PAGE_EXISTS);
    }

 */

  }

  /** The speed and quality of printing suffers dramatically if
   *  any of the containers have double buffering turned on.
   *  So this turns if off globally.
   *  @see enableDoubleBuffering
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
