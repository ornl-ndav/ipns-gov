/*
 * $Log$
 * Revision 1.8  2007/08/20 17:38:25  amoe
 * The following changes were made to undo part of the previous change.
 * -Removed image-to-frame ratio.
 * -Made the image size based on actual pixels of the Viewer.
 *
 * Revision 1.7  2007/07/31 19:10:55  amoe
 * -Added static final default frame width and height.
 * -Added static final image-to-frame ratio.
 * -Set the frame to the default size when the current size has not been set (when it's -1).
 * -Set the image to a size based on a constant ratio and the size of the frame.
 *
 * Revision 1.6  2007/07/17 16:16:54  oakgrovej
 * Added Throws Exception where needed
 *
 * Revision 1.5  2007/07/16 14:52:05  dennis
 * Added parameter, with_controls, to the display method, so that
 * any device type can easily display viewers with or without the
 * controls.
 *
 * Revision 1.4  2007/07/13 21:22:02  amoe
 * -Added screen_bounds.
 * -Finished getBounds().
 *
 * Revision 1.3  2007/07/13 01:28:05  amoe
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
import gov.anl.ipns.DisplayDevices.VirtualArray2D_Displayable;
import gov.anl.ipns.Util.File.ImageRenderWriter;
import gov.anl.ipns.ViewTools.Components.VirtualArray2D;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JFrame;

public class PreviewDevice extends GraphicsDevice
{
  private JFrame display_frame = null;
  
  private final Dimension SCREEN_BOUNDS = 
                         java.awt.Toolkit.getDefaultToolkit().getScreenSize(); 
  
  private final float DEFAULT_FRAME_WIDTH  = SCREEN_BOUNDS.width*0.66f;
  private final float DEFAULT_FRAME_HEIGHT = SCREEN_BOUNDS.height*0.66f;
  
  public PreviewDevice()
  {    
  }
  
  /**
   * Set an attribute for the PreviewDevice.
   * 
   * @param name - Name (key) for the attribute.
   * @param value - Value for the attribute
   */
  @Override
  public void setDeviceAttribute(String name, Object value) 
  {
    // TODO Auto-generated method stub
    
  }

  /**
   * Flush any pending output. For PreviewDevice, this nothing.
   */
  @Override
  public void print() 
  {
    //do nothing    
  }
  
  /**
   * This closes the display for PreviewDevice.
   */
  @Override
  public void close() 
  {
    if( display_frame != null )
    {
      WindowEvent win_ev = new WindowEvent( display_frame, 
          WindowEvent.WINDOW_CLOSING );
      display_frame.dispatchEvent(win_ev);
    } 
  }
  
  /**
   * @return - This returns a Vector with two floats: the maximum width and 
   * height of the PreviewDevice.
   */
  @Override
  public Vector<Float> getBounds() 
  {
    Vector<Float> v = new Vector<Float>();
    v.add((float)SCREEN_BOUNDS.getWidth());
    v.add((float)SCREEN_BOUNDS.getHeight());
    return v;
  }

  /**
   * Display the specified IDisplayable with the specified region, view type, 
   * line, and graph attributes.
   * 
   * @param disp - IDisplayable to be displayed.
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
    display_frame = new JFrame();
    
    PreviewPanel preview;    
    //checks if the size has not been set, if not then set defaults
    if(this.width == -1 || this.height == -1)
    {
      jcomp.setSize((int)(DEFAULT_FRAME_WIDTH), 
                    (int)(DEFAULT_FRAME_HEIGHT));
      preview = new PreviewPanel(ImageRenderWriter.render(jcomp));
      preview.setPreferredSize(new Dimension((int)DEFAULT_FRAME_WIDTH, 
                                             (int)DEFAULT_FRAME_HEIGHT));
    }
    else
    {
      jcomp.setSize((int)(width), 
                    (int)(height));
      preview = new PreviewPanel(ImageRenderWriter.render(jcomp));
      preview.setPreferredSize(new Dimension((int)width, (int)height));
    }    
    
    display_frame.getContentPane().setLayout( new GridLayout(1,1) );
    display_frame.getContentPane().add(preview);
    display_frame.setLocation( (int)x_pos, (int)y_pos );
    display_frame.pack();
    display_frame.setVisible(true);    
  }
 
  /**
   * This inner class is used as a panel to display a rendered 
   * preview of a viewer.
   */
  private class PreviewPanel extends Component 
  {
    private static final long serialVersionUID = 1L;
    private BufferedImage bimg;
      
    public PreviewPanel(BufferedImage bimg) 
    {
      this.bimg = bimg;
    }
      
    public void paint(Graphics g) 
    {
      //---This makes the iamge stretch across the panel--- 
      Dimension size = getSize();
      g.drawImage(bimg, 
          0, 0, size.width, size.height,
          0, 0, bimg.getWidth(null), bimg.getHeight(null),
          null);

      //---This makes the image not stretch across the panel---
      //g.drawImage(bimg,0,0,this);      
    }
  }
  
  public static void main(String[] args) throws Exception
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
                      
             });
    VirtualArray2D_Displayable va2d_disp =  
                               new VirtualArray2D_Displayable( v2d, type);
    
    va2d_disp.setViewAttribute( "preserve aspect ratio", "true");
    va2d_disp.setViewAttribute("two sided", false);
    va2d_disp.setViewAttribute("color control east", "false");
    va2d_disp.setViewAttribute("color control west", true);
    
    PreviewDevice prv_dev = new PreviewDevice();
    //prv_dev.setRegion(50,50,650,550);
    
    prv_dev.display(va2d_disp,false);
  }

}
