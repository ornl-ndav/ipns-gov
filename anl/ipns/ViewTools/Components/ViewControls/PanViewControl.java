/*
 * File: PanViewControl.java
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
 * This work was supported by the National Science Foundation under grant
 * number DMR-0218882, and by the Intense Pulsed Neutron Source Division
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
 
 package DataSetTools.components.View.ViewControls;

 import java.awt.event.ActionListener;
 import java.awt.event.ActionEvent;
 import java.awt.event.MouseAdapter;
 import java.awt.event.MouseEvent;
 import java.awt.Dimension;
 import java.awt.Point;
 import java.awt.Graphics;
 import javax.swing.JFrame;
 import javax.swing.OverlayLayout;
 
 import DataSetTools.components.image.CoordJPanel;
 import DataSetTools.components.image.BoxCursor;
 import DataSetTools.components.image.CoordBounds;
 import DataSetTools.components.image.ImageJPanel;
 import DataSetTools.components.View.Cursor.TranslationJPanel;
 import DataSetTools.components.View.Transparency.TranslationOverlay;
 import DataSetTools.util.floatPoint2D;
 
/**
 * This view control is used to "pan" an image. Adding a PanViewControl
 * will cause a thumbnail of the image to appear in the control panel.
 */
public class PanViewControl extends ViewControl
{
  private CoordJPanel panel;           // replica of actual image
  private TranslationOverlay overlay;  // where the region outline is drawn.
  private CoordBounds local_bounds;    // the view port or viewable area
  
 /**
  * Constructor for creating a thumbnail of the CoordJPanel passed in.
  *
  *  @param  cjp The CoordJPanel that will be thumbnailed.
  */
  public PanViewControl(CoordJPanel cjp)
  {
    super("");
    panel = cjp;
    overlay = new TranslationOverlay();
    overlay.setGlobalBounds( cjp.getGlobalWorldCoords() );
    overlay.addActionListener( new OverlayListener() );
    setLocalBounds( cjp.getLocalWorldCoords() );
    panel.setEventListening(false);
    
    OverlayLayout layout = new OverlayLayout(this);
    setLayout(layout);
    setPreferredSize( new Dimension(0,150) );
    add(overlay);
    add(panel);
    addMouseListener( new PanMouseAdapter() );
  }
  
 /**
  * Get the local bounds of the thumbnail. These bounds represent the area
  * viewable by the user and are shown graphically by the cursor. 
  *
  *  @return local_bounds Bounds of the viewable region.
  */ 
  public CoordBounds getLocalBounds()
  {
    return local_bounds.MakeCopy();
  }  
  
 /**
  * Set the local bounds of the thumbnail. These bounds represent the area
  * viewable by the user and are shown graphically by the cursor. 
  *
  *  @param  lb Bounds of the viewable region.
  */ 
  public void setLocalBounds( CoordBounds lb )
  {
    local_bounds = lb.MakeCopy();
    overlay.setLocalBounds(local_bounds);
  }  
  
 /**
  * Set the global bounds of the thumbnail. These bounds represent the entire 
  * possible area viewable by the user. 
  *
  *  @param  gb Bounds of the viewable region.
  */ 
  public void setGlobalBounds( CoordBounds gb )
  {
    panel.setGlobalWorldCoords( gb.MakeCopy() );
    overlay.setGlobalBounds( gb.MakeCopy() );
  }
 
 /**
  * Since the classes that extend the CoordJPanel are affected by a logscale
  * factor, this method is used to inform those classes when the logscale has
  * changed.
  *
  *  @param  s The logscale factor.
  */ 
  public void setLogScale( double s )
  {
    if( panel instanceof ImageJPanel )
      ((ImageJPanel)panel).changeLogScale( s, true );
  }

 /**
  * Test program...
  */
  public static void main(String[] args)
  {
    JFrame f = new JFrame("Test for PanViewControl");
    f.setBounds(0,0,200,200);
    f.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    CoordJPanel test = new CoordJPanel();
    test.setGlobalWorldCoords( new CoordBounds(0,0,200,200) );
    //test.setLocalWorldCoords( new CoordBounds(50,50,100,100) );
    PanViewControl pvc = new PanViewControl( test );
    f.getContentPane().add(pvc);
    f.setVisible(true);
    pvc.setLocalBounds( new CoordBounds(0,0,50,50) );
  }

 /*
  * Listens for messages being passed from the TranslationOverlay and passes
  * them onto its listeners.
  */
  private class OverlayListener implements ActionListener
  {
    public void actionPerformed( ActionEvent ae )
    {
      String message = ae.getActionCommand(); 
      if( message.equals( TranslationJPanel.BOUNDS_CHANGED ) )
      {
        local_bounds = overlay.getLocalBounds();
      }
      send_message(message);
    }
  }
 
 /*
  * If the mouse passes over the region, request focus.
  */ 
  private class PanMouseAdapter extends MouseAdapter
  {  
    public void mouseEntered (MouseEvent e)
    {
      requestFocus();
    }
  }
}
