/*
 * File:  TranslationJPanel.java
 *
 * Copyright (C) 2003, Mike Miller
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
 * Primary   Mike Miller <millermi@uwstout.edu>
 * Contact:  Student Developer, University of Wisconsin-Stout
 *
 * Contact : Dennis Mikkelson <mikkelsond@uwstout.edu>
 *           Department of Mathematics, Statistics and Computer Science
 *           University of Wisconsin-Stout
 *           Menomonie, WI 54751, USA
 *
 * This work was supported by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 * Modified:
 *
 *  $Log$
 *  Revision 1.1  2003/10/27 08:47:48  millermi
 *  - Initial Version - This class was created to enable users
 *    panning options for images too large to view in the
 *    viewport.
 *
 */

package DataSetTools.components.View.Cursor;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import DataSetTools.components.image.CoordJPanel;
import DataSetTools.components.image.CoordBounds;
import DataSetTools.components.image.CoordTransform;
import DataSetTools.components.View.Cursor.XOR_PanCursor;
import DataSetTools.components.View.Cursor.BoxPanCursor;
import DataSetTools.util.floatPoint2D;

/**
 * This class allows for the drawing of a translating box cursor.
 * It is used by the TranslationOverlay to view different regions within
 * an image too large for the viewport.
 */
public class TranslationJPanel extends CoordJPanel
{
 /**
  * "Bounds Changed" - This message String is used by ActionListeners
  * to tell listeners that the viewport or local bounds have changed.
  */
  public static final String BOUNDS_CHANGED = "Bounds Changed";
  
  private BoxPanCursor box;
  private Point differ = new Point(0,0);   // the x,y distance from the current
                                           // mouse point to the topleft corner
					   // of the rectangle. This allows
					   // users to translate without the 
					   // box jumping to the current point.
  
 /**
  * Constructor adds listeners to this TranslationJPanel. This JPanel is
  * also contructed to receive keyboard events.
  */ 
  public TranslationJPanel()
  {     
    setEventListening(false);
    box = new BoxPanCursor(this);
    requestFocus();
    setViewPort( new CoordBounds(0,0,0,0) ); 
    addMouseListener( new SelectMouseAdapter() );
    addMouseMotionListener( new SelectMouseMotionAdapter() );
    addComponentListener( new ResizedListener() );
  }
  
 /**
  * Set the size of the viewport. This method must be called whenever the
  * local bounds of the CoordJPanel are changed. Also call this method to
  * initialize a viewport size.
  *
  *  @param  viewport CoordBounds representing the viewable area.
  */ 
  public void setViewPort( CoordBounds viewport )
  {
    setViewPort( new Point( (int)viewport.getX1(),(int)viewport.getY1()), 
                 new Point( (int)viewport.getX2(),(int)viewport.getY2() ) ); 
  }
  
 /**
  * Set the size of the viewport. This method must be called whenever the
  * local bounds of the CoordJPanel are changed. Also call this method to
  * initialize a viewport size.
  *
  *  @param  vp1 Top-left corner of the viewable area.
  *  @param  vp2 Bottom-right corner of the viewable area.
  */ 
  public void setViewPort( Point vp1, Point vp2 )
  {
    setLocalWorldCoords( new CoordBounds( vp1.x, vp1.y, vp2.x, vp2.y ) );
    //System.out.println("TJP: " + getLocalWorldCoords().toString() );
    floatPoint2D wctopleft  = new floatPoint2D( (float)vp1.x,
                                                (float)vp1.y );
    floatPoint2D wcbotright = new floatPoint2D( (float)vp2.x,
                                                (float)vp2.y );
    // convert from wc to pixel
    Point pixeltopleft = convertToPixelPoint(wctopleft);
    Point pixelbotright = convertToPixelPoint(wcbotright);
    
    //System.out.println("TJP Pixel: " + pixeltopleft.toString() + "..." +
    //                   pixelbotright.toString() );
    
    box.init( pixeltopleft, pixelbotright );
    send_message(BOUNDS_CHANGED);
  }
  
 /**
  * Set the bounds for the entire image. This method must be called whenever the
  * global bounds of the CoordJPanel are changed. Also call this method to
  * initialize the global bounds.
  *
  *  @param  viewport CoordBounds representing the viewable area.
  */ 
  public void setGlobalPanelBounds( CoordBounds global )
  {
    setGlobalWorldCoords( global.MakeCopy() );
    //System.out.println("TJP Global: " + getGlobalWorldCoords().toString() );
  }
  
 /**
  * Currently the only type of pan cursor is the box pan cursor, so this method
  * will return that cursor.
  *
  *  @return box The XOR_PanCursor responsible for the visual transformation.
  */ 
  public XOR_PanCursor getBoxCursor()
  {
    return box;
  }

 /*
  * Converts from world coordinates to a pixel point
  */
  private Point convertToPixelPoint( floatPoint2D fp )
  {
    CoordTransform pixel_global = getGlobal_transform();
    floatPoint2D fp2d = pixel_global.MapTo( fp );
    return new Point( (int)fp2d.x, (int)fp2d.y );
  }
 
 /*
  * Converts from pixel coordinates to world coordinates.
  */
  private floatPoint2D convertToWorldPoint( Point p )
  {
    CoordTransform pixel_global = getGlobal_transform();
    return pixel_global.MapFrom( new floatPoint2D((float)p.x, (float)p.y) );
  }

 /*
  * This class handles the "dirty" work before and after translations.
  */
  private class SelectMouseAdapter extends MouseAdapter
  {
    public void mousePressed (MouseEvent e)
    {
      // This information will be used to prevent the viewport from "jumping"
      // to the current point.
      Point topcorner = box.getP1();
      differ = e.getPoint();
      differ.x -= topcorner.x;
      differ.y -= topcorner.y;
    }
    
    public void mouseReleased (MouseEvent e)
    {
      send_message(BOUNDS_CHANGED);
    }

    public void mouseEntered (MouseEvent e)
    {
      requestFocus(); 
    }
  } 
  
 /*
  * Redraw the specified cursor, giving the translating effect.
  */ 
  private class SelectMouseMotionAdapter extends MouseMotionAdapter
  {
    public void mouseDragged(MouseEvent e)
    { 
      // this will prevent the viewport from "jumping" to the current point
      Point current = e.getPoint();
      //System.out.println("Pixel: " + current.toString() + "  WC: " +
      //                   convertToWorldPoint(current).toString() ); 
      current.x -= differ.x;
      current.y -= differ.y; 
      
      box.translate(current); 
      
      // now update the local bounds of this transjpanel after the translation.
      Rectangle region = box.region();
      Point p1 = region.getLocation();
      Point p2 = new Point(p1);
      p2.x += region.getWidth();
      p2.y += region.getHeight();
      floatPoint2D wcp1 = convertToWorldPoint(p1);
      floatPoint2D wcp2 = convertToWorldPoint(p2);
      setLocalWorldCoords( new CoordBounds( wcp1.x, wcp1.y, wcp2.x, wcp2.y ) );
    }
  }  
 
 /*
  * If the component is resized, the bounds of the cursor need to be adjusted.
  * This listener will move and resize the cursor after the component is
  * resized. 
  */ 
  private class ResizedListener extends ComponentAdapter
  {
    public void componentResized( ComponentEvent ce )
    {
      CoordBounds vp = getLocalWorldCoords();
      floatPoint2D wctopleft  = new floatPoint2D( vp.getX1(),
                                                  vp.getY1() );
      floatPoint2D wcbotright = new floatPoint2D( vp.getX2(),
                                                  vp.getY2() );
      // convert from wc to pixel
      Point pixeltopleft = convertToPixelPoint(wctopleft);
      Point pixelbotright = convertToPixelPoint(wcbotright);
    
      box.init( pixeltopleft, pixelbotright );
    }  // end componentResized()   
  } // end ResizedListener  
  
/* -----------------------------------------------------------------------
 *
 * MAIN PROGRAM FOR TEST PURPOSES
 *
 */

  public static void main(String[] args)
  {
    JFrame f = new JFrame("Test for TranslationJPanel");
    f.setBounds(0,0,500,500);
    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    TranslationJPanel panel = new TranslationJPanel();
    f.getContentPane().add(panel);
    f.setVisible(true);
    panel.setViewPort( new CoordBounds(50,50,100,100) );
  }  
}
