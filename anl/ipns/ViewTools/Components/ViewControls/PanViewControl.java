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
 *  Revision 1.13  2004/03/12 03:05:12  millermi
 *  - Changed package, fixed imports.
 *
 *  Revision 1.12  2004/03/02 22:12:52  dennis
 *  Now only calls update if the PanViewControl "isShowing()".  This
 *  fixes a problem where the PanViewControl would "flash" on the
 *  screen when it state was changed, even though it was on a tabbed
 *  pane that was currently hidden behind another tabbed pane.
 *
 *  Revision 1.11  2004/02/06 20:26:09  millermi
 *  - Added ObjectState key and methods.
 *
 *  Revision 1.10  2004/01/05 18:14:06  millermi
 *  - Replaced show()/setVisible(true) with WindowShower.
 *  - Removed excess imports.
 *
 *  Revision 1.9  2003/12/30 23:17:47  millermi
 *  - Added setMaximumSize(200,200) to limit the size of the pan
 *    control to 200 x 200.
 *
 *  Revision 1.8  2003/12/20 04:42:50  millermi
 *  - changed super.repaint() in refreshData() to update(). This
 *    was needed to consistently paint the overlay over the image.
 *
 *  Revision 1.7  2003/12/17 20:32:26  millermi
 *  - made refreshData() private, now is called by the repaint()
 *    method which is overloaded.
 *  - Fixed bug which did not allow the image to refresh properly.
 *
 *  Revision 1.6  2003/11/25 23:33:18  millermi
 *  - Bug Fix - panel_size in refreshData is set to the
 *    size of the containing panel and not the size of
 *    the whole PanViewControl.
 *
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
 
 package gov.anl.ipns.ViewTools.Components.ViewControls;

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
 
 import gov.anl.ipns.ViewTools.Panels.Transforms.*;
 import gov.anl.ipns.ViewTools.Panels.Cursors.BoxCursor;
 import gov.anl.ipns.ViewTools.Panels.Image.ImageJPanel;
 import gov.anl.ipns.ViewTools.Components.ObjectState;
 import gov.anl.ipns.ViewTools.Components.Cursor.TranslationJPanel;
 import gov.anl.ipns.ViewTools.Components.Transparency.TranslationOverlay;
 import gov.anl.ipns.Util.Numeric.floatPoint2D;
 import gov.anl.ipns.Util.Sys.WindowShower;
 
/**
 * This view control is used to "pan" an image. Adding a PanViewControl
 * will cause a thumbnail of the image to appear in the control panel.
 */
public class PanViewControl extends ViewControl
{
 // ---------------------ObjectState Keys---------------------------------
 /**
  * "Overlay" - This constant String is a key for referencing the state
  * information about the width allocated for the label.
  * The value that this key references is of type String.
  */
  public static final String OVERLAY = "Overlay";
  
  private ThumbnailJPanel panel;       // thumbnail of actual CoordJPanel
  private Image panel_image;
  private CoordJPanel actual_cjp;      // reference to actual CoordJPanel
  private TranslationOverlay overlay;  // where the region outline is drawn.
  private double data_width = 0;
  private double data_height = 0;
  
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
    setLayout( new OverlayLayout(this) );
    setPreferredSize( new Dimension(0,150) );
    setMaximumSize( new Dimension(200,200) );
    add(overlay);
    add(panel);
    addMouseListener( new PanMouseAdapter() );
    // this listener will preserve the aspect ratio of the control
    addComponentListener( new MaintainAspectRatio() );
  }
 
 /**
  * This method will get the current values of the state variables for this
  * object. These variables will be wrapped in an ObjectState.
  *
  *  @param  isDefault Should selective state be returned, that used to store
  *                    user preferences common from project to project?
  *  @return if true, the default state containing user preferences,
  *          if false, the entire state, suitable for project specific saves.
  */ 
  public ObjectState getObjectState( boolean isDefault )
  {
    ObjectState state = super.getObjectState(isDefault);
    state.insert( OVERLAY, overlay.getObjectState(isDefault) );
    return state;
  }
     
 /**
  * This method will set the current state variables of the object to state
  * variables wrapped in the ObjectState passed in.
  *
  *  @param  new_state
  */
  public void setObjectState( ObjectState new_state )
  {
    // call setObjectState of ViewControl, sets title if one exists.
    super.setObjectState( new_state );
    Object temp = new_state.get(OVERLAY);
    if( temp != null )
    {
      overlay.setObjectState(new_state);
    }
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
  * This will overload the repaint method. This method calls refreshData()
  */
  public void repaint()
  {
    refreshData();
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
    WindowShower shower = new WindowShower(f);
    java.awt.EventQueue.invokeLater(shower);
    shower = null;
  }
  
 /*
  * Call this method to repaint the thumbnail whenever the actual image changes.
  * This method calls the repaint method, so calling refreshData() will 
  * correctly update the thumbnail.
  */ 
  private void refreshData()
  {
    setImageDimension();
    double aspect_ratio = setAspectRatio();
    if( actual_cjp instanceof ImageJPanel )
    { 
      Dimension panel_size = panel.getSize();
      panel_image = ((ImageJPanel)actual_cjp).getThumbnail( panel_size.width, 
                            (int)(panel_size.width*aspect_ratio) ); 
      if( panel_image != null )
      {
        panel.setImage(panel_image);
	// have to use update so Overlay is always displayed over the image.
	if( getGraphics() != null && isShowing() )
	  update( getGraphics() );
        //super.repaint();
      }
    }
    else
      super.repaint();
  }
  
 /*
  * This method will find the dimension of the data we are working with.
  */ 
  private void setImageDimension()
  {
    if( actual_cjp instanceof ImageJPanel )
    {
      data_width = (double)((ImageJPanel)actual_cjp).getNumDataColumns();
      data_height = (double)((ImageJPanel)actual_cjp).getNumDataRows();
    }
  }
 
 /*
  * This method uses aspect ratio of height/width to keep the ratio of the
  * panviewcontrol similar to that of the data.
  */ 
  private double setAspectRatio()
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
    setPreferredSize( new Dimension( (int)pan_width, (int)pan_height ) );
    setSize( new Dimension( (int)pan_width, (int)pan_height ) );
    return aspect_ratio;
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
