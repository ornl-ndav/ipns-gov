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
 *  Revision 1.5  2003/11/21 01:27:29  millermi
 *  - Removed println() statements.
 *  - Fixed bug that caused image to be repainted over the cursor.
 *
 *  Revision 1.4  2003/11/21 00:43:03  millermi
 *  - replica of image is now an Image instead of a CoordJPanel.
 *  - Replaced methods that updated the logscale and colorscale
 *    with refreshData() since the Image doesn't know about
 *    these values.
 *
 *  Revision 1.3  2003/11/18 01:00:54  millermi
 *  - Changed CoordJPanel ObjectState key strings to keep consistent
 *    with changes made to the CoordJPanel.
 *
 *  Revision 1.2  2003/10/29 20:34:55  millermi
 *  -Added colorscale and logscale capabilities for the instances
 *   of ImageJPanels
 *  -Added ObjectState info
 *
 *  Revision 1.1  2003/10/27 08:47:48  millermi
 *  - Initial Version - This class was created to enable users
 *    panning options for images too large to view in the
 *    viewport.
 *
 */
 
 package DataSetTools.components.View.ViewControls;

 import java.awt.Image;
 import java.awt.event.ActionListener;
 import java.awt.event.ActionEvent;
 import java.awt.event.ComponentAdapter;
 import java.awt.event.ComponentEvent;
 import java.awt.event.MouseAdapter;
 import java.awt.event.MouseEvent;
 import java.awt.Dimension;
 import java.awt.Rectangle;
 import java.awt.Point;
 import java.awt.Graphics;
 import javax.swing.JFrame;
 import javax.swing.JPanel;
 import javax.swing.OverlayLayout;
 
 import DataSetTools.components.image.CoordJPanel;
 import DataSetTools.components.image.BoxCursor;
 import DataSetTools.components.image.CoordBounds;
 import DataSetTools.components.image.CoordTransform;
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
  private ThumbnailJPanel panel;       // thumbnail of actual CoordJPanel
  private Image panel_image;
  private CoordJPanel actual_cjp;      // reference to actual CoordJPanel
  private TranslationOverlay overlay;  // where the region outline is drawn.
  private double data_width = 0;
  private double data_height = 0;
  // local bounds of the panel must be the same as global bounds, thus
  // the local bounds must be saved separately.
  //private CoordBounds local_bounds = new CoordBounds();
  //private CoordBounds global_bounds = new CoordBounds();
  
 /**
  * Constructor for creating a thumbnail of the CoordJPanel passed in.
  *
  *  @param  cjp The CoordJPanel that will be thumbnailed.
  */
  public PanViewControl(CoordJPanel cjp)
  {
    super("");
    panel = new ThumbnailJPanel();
    overlay = new TranslationOverlay();
    actual_cjp = cjp;
    refreshData();
    overlay.addActionListener( new OverlayListener() );
    setGlobalBounds( cjp.getGlobalWorldCoords() );
    setLocalBounds( cjp.getLocalWorldCoords() );
    //panel.setEventListening(false);
    OverlayLayout layout = new OverlayLayout(this);
    setLayout(layout);
    setPreferredSize( new Dimension(0,150) );
    add(overlay);
    add(panel);
    addMouseListener( new PanMouseAdapter() );
    // this listener will preserve the aspect ratio of the control
    addComponentListener( new MaintainAspectRatio() );
  } 
  
 /**
  * Get the local bounds of the thumbnail. These bounds represent the area
  * viewable by the user and are shown graphically by the cursor. 
  *
  *  @return Bounds of the viewable region.
  */ 
  public CoordBounds getLocalBounds()
  {
    return overlay.getLocalBounds();
  }  
  
 /**
  * Set the local bounds of the thumbnail. These bounds represent the area
  * viewable by the user and are shown graphically by the cursor. 
  *
  *  @param  lb Bounds of the viewable region.
  */ 
  public void setLocalBounds( CoordBounds lb )
  {
    overlay.setLocalBounds( lb.MakeCopy() );
    repaint();
  }  
  
 /**
  * Get the global bounds of the thumbnail. These bounds represent the entire 
  * possible area viewable by the user. 
  *
  *  @return Bounds of the viewable region.
  */ 
  public CoordBounds getGlobalBounds()
  {
    return overlay.getGlobalBounds();
  } 
  
 /**
  * Set the global bounds of the thumbnail. These bounds represent the entire 
  * possible area viewable by the user. If the global bounds are changed, the
  * local bounds are reinitialized to the global bounds.
  *
  *  @param  gb Bounds of the viewable region.
  */ 
  public void setGlobalBounds( CoordBounds gb )
  {
    overlay.setGlobalBounds( gb.MakeCopy() );
    repaint();
  }
  
 /**
  * Call this method to repaint the thumbnail whenever the actual image changes.
  * This method calls the repaint method, so calling refreshData() will 
  * correctly update the thumbnail.
  */ 
  public void refreshData()
  {
    setImageDimension();
    setAspectRatio();
    if( actual_cjp instanceof ImageJPanel )
    { 
      Dimension panel_size = getSize();
      panel_image = ((ImageJPanel)actual_cjp).getThumbnail( panel_size.width, 
                                                           panel_size.height ); 
    }
    panel.setImage(panel_image);
    Graphics g = getGraphics();
    if( g != null )
      update(g);
  }

 /*
  * Test program...
  */
  public static void main(String[] args)
  {
    JFrame f = new JFrame("Test for PanViewControl");
    f.setBounds(0,0,200,200);
    f.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    ImageJPanel test = new ImageJPanel();
    
    float test_array[][] = new float[500][500];
    for ( int i = 0; i < 500; i++ )
      for ( int j = 0; j < 500; j++ )
        test_array[i][j] = i + j;
    
    test.setData(test_array, true);

    PanViewControl pvc = new PanViewControl( test );
    f.getContentPane().add(pvc);
    f.setVisible(true);
  }
  
  private void setImageDimension()
  {
    if( actual_cjp instanceof ImageJPanel )
    {
      data_width = (double)((ImageJPanel)actual_cjp).getNumDataColumns();
      data_height = (double)((ImageJPanel)actual_cjp).getNumDataRows();
    }
  }
  
  private void setAspectRatio()
  { 
    double aspect_ratio = data_height / data_width;
    
    // limit aspect ratio between 2/3 and 3/2
    if( aspect_ratio < (2f/3f) )
      aspect_ratio = 2.0/3.0;
    if( aspect_ratio > (3f/2f) )
      aspect_ratio = 3.0/2.0;
     
    double pan_width = getWidth();
    double pan_height = pan_width * aspect_ratio;
    //System.out.println("Aspect Ratio: " + actual_cjp.getHeight() + "/" +
    //                   actual_cjp.getWidth() );
    //System.out.println("Pan Width: " + pan_width );
    //System.out.println("Pan Height: " + pan_height );
    setMinimumSize( new Dimension( 0, (int)pan_height ) );
    setPreferredSize( new Dimension( (int)pan_width, (int)pan_height ) );
  }

 /*
  * Listens for messages being passed from the TranslationOverlay and passes
  * them onto its listeners.
  */
  private class OverlayListener implements ActionListener
  {
    public void actionPerformed( ActionEvent ae )
    {
      String message = ae.getActionCommand(); /*
      if( message.equals( TranslationJPanel.BOUNDS_CHANGED ) )
      {
        local_bounds = overlay.getLocalBounds();
      }*/
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
  
 /*
  * The primary purpose of this listener is to keep the aspect ratio of this
  * control close to that of the actual image.
  */ 
  private class MaintainAspectRatio extends ComponentAdapter
  {
    public void componentResized( ComponentEvent e )
    {
      refreshData();
    }
  }
  
 /*
  * This private JPanel is used to hold the image of the thumbnail. A
  * separate class was required to repaint the image without interferring
  * with the rest of the PanViewControl.
  */ 
  private class ThumbnailJPanel extends JPanel
  {
    private Image image = null;
    public ThumbnailJPanel()
    {
      super();
    }
    
    public ThumbnailJPanel(Image i)
    {
      super();
      image = i;
    }
    
    public void paint( Graphics g )
    {
      if( image != null )
        g.drawImage( image, 0, 0, this );
    }
    
    public void setImage( Image i )
    {
      image = i;
    }
  }
}
