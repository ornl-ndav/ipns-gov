/*
 * File: ComponentPrintable.java
 *
 * Copyright (C) 2001, Ruth Mikkelson
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
 * Contact : Ruth Mikkelson <mikkelsonr@uwstout.edu>
 *           Department of Mathematics, Statistics and Computer Science
 *           University of Wisconsin-Stout
 *           Menomonie, WI 54751, USA
 *
 * This work was supported by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 * $Log$
 * Revision 1.1  2006/07/26 16:10:32  dennis
 * Moved ComponentPrintable.java to gov/anl/ipns/Util/Sys
 * so it is with the PrintUtilities class that uses it.
 *
 * Revision 1.5  2006/07/26 15:48:27  dennis
 * Minor cleanup of javadocs and format before moving to the
 * gov/anl/ipns/Util/Sys that contains other print utilities
 * using this.
 *
 * Revision 1.4  2005/12/10 16:05:54  rmikk
 * This class is now a Pageable. It can print JTextComponents to separate
 *    pages.  Pages should not split across lines or images(hopefully)
 *
 * Revision 1.3  2002/11/27 23:27:07  pfpeterson
 * standardized header
 *
 */
//ToDo make Pageable 
//  Method
//    1. getView and dimensions of printable regions
//    2. Get printable area.  scale to width and find npages
//    3. Use view to model and model to view to get 
//       view to model --> end page and start pages (use -1's to ensure fit)
//       paint subrectangle of view:  in print with page
//    4. Use Clip/translate to get correct region.

package gov.anl.ipns.Util.Sys;

import java.awt.*;
import java.awt.print.*;

import javax.swing.*;
import javax.swing.text.*;

public class ComponentPrintable implements Printable, Pageable {

  private Component mComponent;
//  private int[] posPages = null;
  
  /**
   * Constructor for the pageable and printable object. JTextComponents are 
   * the only ones that are pageable.  viewToModel and modelToView methods 
   * are used to make pagebreaks hopefully not across lines or pictures.
   *
   * @param c  The Component to be printed.  All but JTextComponents are 
   *           printed on one page
   */
  public ComponentPrintable(Component c) {
    mComponent = c;
    
    if( c== null)
      return;
    }

  
  /**
   *  Required for Printable interface.  Prints the page to the printer
   *  @param g  the graphics object.  It should be for a printer
   *  @param pageFormat  Landscape/portrait with dimensions
   *  @param pageIndex the index( starting at 0) for the page to be printed
   */
  public int print(Graphics g, PageFormat pageFormat, int pageIndex) {
   
    if( mComponent == null)
      return Printable.NO_SUCH_PAGE;

    if (pageIndex > 0)
      if(!(mComponent instanceof javax.swing.text.JTextComponent))
         return NO_SUCH_PAGE;

    Graphics2D g2 = (Graphics2D)g;
    
    double pageHeight = pageFormat.getImageableHeight();
    double pageWidth = pageFormat.getImageableWidth();
    float xscale =(float)pageWidth/(float)( mComponent.getWidth());
    float yscale=xscale;
    
    if( !(mComponent instanceof javax.swing.text.JTextComponent)){
    
      xscale=(float)(pageFormat.getImageableHeight())/mComponent.getHeight();
    }
    
    if(yscale < xscale) 
      xscale= yscale;
       
    double startPage = -pageIndex*pageHeight;
    Rectangle Clip=null;  
    if( mComponent instanceof javax.swing.text.JTextComponent){
        
      JTextComponent jt =(JTextComponent)mComponent;
      Clip=ViewClip(jt,(float)( pageHeight/xscale),pageIndex);
      if( Clip == null)
        return NO_SUCH_PAGE;
      startPage = Clip.y*xscale;
    }else if(pageIndex*pageHeight >xscale*mComponent.getHeight() )
      return NO_SUCH_PAGE;

    g2.setClip(null);
    g2.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
    	
	g2.translate(0f, -startPage);
    g2.scale(xscale,xscale);
    if(Clip != null)
      g2.setClip(Clip);
	
    boolean wasBuffered = disableDoubleBuffering(mComponent);
    mComponent.paint(g2);
    
    restoreDoubleBuffering(mComponent, wasBuffered);
    
    return PAGE_EXISTS;
  }


  private boolean disableDoubleBuffering(Component c) {
    if (c instanceof JComponent == false) 
      return false;
    JComponent jc = (JComponent)c;
    boolean wasBuffered = jc.isDoubleBuffered();
    jc.setDoubleBuffered(false);
    return wasBuffered;
  }
  

  private void restoreDoubleBuffering(Component c, boolean wasBuffered) {
    
    if (c instanceof JComponent)
      ((JComponent)c).setDoubleBuffered(wasBuffered);
  }
  
  
  /**
   * Required for pageable interface
   * 
   *  @return returns 1 for non-JTextComponents othewise
   *                  Pageable.UNKNOWN_NUMBER_OF_PAGES  is returned
   */
  public int getNumberOfPages(){
    
    if( mComponent instanceof javax.swing.text.JTextComponent )
      return Pageable.UNKNOWN_NUMBER_OF_PAGES;
    
    return 1;
  }
  
  
  /**
   *  Required for the Pageable interface
   *  @param  page the page for which the printable is requested
   * 
   *  @return  this for all pages
   */
  public Printable getPrintable(int page){
    return this; 
  }


  /**
   * Returns the clipping rectangle for pages of the Component
   * @param c   The Component.  
   * @param PageHeight  The Height of a page in Component coordinates
   * @param pageNum   The page number to clip
   * @return  The rectangle giving top left coord and width and height of a 
   *          page
   */
  private  Rectangle ViewClip( JTextComponent c, float PageHeight, int pageNum){
       int starty=0;
       int totHeight = c.getHeight();
       Rectangle Res = new Rectangle();
       for( int i=0;i<=pageNum; i++){//Find width and height of this page
         if( starty >= totHeight)
            return null;
         //int posTop = c.viewToModel(new Point(startx,0));
         int posBottom=c.viewToModel( new Point( 0,starty+(int)PageHeight));
       
         Res.x=0; Res.y =starty;
         Res.width = c.getWidth();

         Rectangle R=null;
         try{
         
            R = c.modelToView( posBottom);
         }catch(Exception ss){
           return null;
         }
         Res.width = c.getWidth();
         Res.height= R.y-starty;
         starty=R.y;
         
       }
      if(Res.width <=0)
        return null;
      if( Res.height <=0)
        return null;
      return Res;
  }
  
  
  /* 
   * TODO:  How do we determine this??????.   It can be set by the printer 
   * dialog box.
   */
  public PageFormat getPageFormat(int arg0) 
                                 throws IndexOutOfBoundsException {
    return null;
  }  
  
}
