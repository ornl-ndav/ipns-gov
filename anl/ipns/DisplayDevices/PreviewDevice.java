/*
 * $Log$
 * Revision 1.1  2007/07/12 15:45:05  amoe
 * Initial commit.
 *
 */
package gov.anl.ipns.DisplayDevices;

import gov.anl.ipns.Util.File.ImageRenderWriter;
import gov.anl.ipns.Util.Sys.StringUtil;
import gov.anl.ipns.Util.Sys.WindowShower;
import gov.anl.ipns.ViewTools.Components.AxisInfo;
import gov.anl.ipns.ViewTools.Components.IVirtualArray;
import gov.anl.ipns.ViewTools.Components.IVirtualArray2D;
import gov.anl.ipns.ViewTools.Components.IVirtualArrayList1D;
import gov.anl.ipns.ViewTools.Components.OneD.DataArray1D;
import gov.anl.ipns.ViewTools.Components.OneD.VirtualArrayList1D;
import gov.anl.ipns.ViewTools.Components.TwoD.Contour.ContourViewComponent;
import gov.anl.ipns.ViewTools.Displays.Display;
import gov.anl.ipns.ViewTools.Displays.Display1D;
import gov.anl.ipns.ViewTools.Displays.Display2D;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.WindowEvent;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

import DataSetTools.dataset.DataSet;
import DataSetTools.dataset.FunctionTable;
import DataSetTools.dataset.UniformXScale;
import DataSetTools.util.SharedData;
import DataSetTools.viewer.ViewManager;

public class PreviewDevice extends GraphicsDevice
{
  private BufferedImage bimg = null;
  private JFrame display_frame = (JFrame)super.display_window;
  
  public PreviewDevice()
  {
  }
  
  /**
   * Set an attribute for the ScreenDevice.
   * 
   * @param name - Name (key) for the attribute.
   * @param value - Value for the attribute
   */
  public void setDeviceAttribute(String name, Object value)
  {
    
  }
  
  /**
   * Flush any pending output. For PreviewDevice, this displays a JFrame 
   * containing an image rendering of a viewer. 
   */
  public void print()
  {
    //do nothing
  }
  
  /**
   * This closes the display for ScreenDevice.
   */
  public void close()
  {
    if( super.display_window != null )
    {
      WindowEvent win_ev = new WindowEvent( super.display_window, 
          WindowEvent.WINDOW_CLOSING );
      display_window.dispatchEvent(win_ev);
    }
  }
  
  /**
   * @return - This returns a Vector with two floats: width and height of a 
   * specific device.
   */
  /*
   * NOTE: Should this class be in the super-class? (amoe)
   */
  public Vector getBounds()
  {
    Vector<Float> v = new Vector<Float>();
    v.add(width);
    v.add(height);
    return v;
  }
  
  /**
   * Specify the region for the next viewer that is displayed.  Viewers will 
   * NOT show controls by default.
   * 
   * @param x - X Position
   * @param y - Y Position
   * @param w - Width
   * @param h - Height
   */
  /*
   * NOTE: Should this class be in the super-class? (amoe)
   */
  public void setRegion(int x, int y, int w, int h)
  {
    super.x_pos = x;
    super.y_pos = y;
    super.width = w;
    super.height = h;
  }
  
  /**
   * Display the specified DataSet in the specified region, using the specified
   * view type, line, and graph attributes.
   * 
   * @param ds - Dataset to be displayed.
   * @param view_type - The type of view to be displayed.
   */
  public void display(DataSet ds, String view_type)
  {  
    ViewManager view = new ViewManager(ds, view_type,false);
    JPanel content_pane = (JPanel)view.getContentPane();
    
    display_frame = new JFrame(); //JFrame that holds the preview image
    
    // Setting content pane size so the image will be rendered into this
    // resolution.
    content_pane.setSize((int)super.width,(int)super.height);    
    bimg = ImageRenderWriter.render( content_pane );
    
    PreviewPanel pp = new PreviewPanel(bimg);
    pp.setPreferredSize(new Dimension((int)bimg.getWidth(),(int)bimg.getHeight()));    
    
    display_frame.setLocation((int)super.x_pos,(int)super.y_pos);

    display_frame.add(pp);
    display_frame.setTitle("PreviewGraphics - ViewManager");
    display_frame.pack();
    display_frame.setVisible(true);
  }
  
  /**
   * Display the specified IVirtualArray2D in the specified region, using the 
   * specified view type, line and graph attributes.
   * 
   * @param iva - IVirtualArray2D to be displayed.
   * @param view_type - The type of view to be displayed.
   */
  public void display(IVirtualArray iva, String view_type)
  {
    Display display_view = null;
    JPanel content_pane = null;
    int view_flag;
    
    //checking if the IVirtualArray iva is a recogized data structure
    if(iva instanceof IVirtualArrayList1D)
    {
      //checking if the view type is compatible with the IVirtualArray iva
      if( view_type != ViewManager.SELECTED_GRAPHS && 
          view_type != ViewManager.TABLE )
      {
        System.err.println("'"+view_type+"' is not a compatible view for '"+
            iva.getClass().getSimpleName()+"'.");
        return;
      }
      
      view_flag = translateDSVFlag(view_type);
      display_view = new Display1D((IVirtualArrayList1D)iva,view_flag,Display1D.CTRL_NONE);
    }
    else if(iva instanceof IVirtualArray2D)
    {
      //checking if the view_type is compatible with the IVirtualArray iva
      if( view_type != ViewManager.CONTOUR && 
          view_type != ViewManager.IMAGE   &&
          view_type != ViewManager.TABLE)
      {
        System.err.println("'"+view_type+"' is not a compatible view for '"+
            iva.getClass().getSimpleName()+"'.");
        return;
      }
      
      view_flag = translateDSVFlag(view_type);      
      display_view = new Display2D((IVirtualArray2D)iva,view_flag,Display2D.CTRL_NONE);
    }
    else
    {
      //if the IVirtualArray is not recognized, then leave the method
      System.err.println("'"+iva.getClass().getSimpleName()+
          "' is not a recognized data structure in PreviewDevice.");
      return;
    }
    
    content_pane = (JPanel)display_view.getContentPane();
    display_frame = new JFrame(); //JFrame that holds the preview image
    
    // Setting content pane size so the image will be rendered into this
    // resolution.
    content_pane.setSize((int)super.width,(int)super.height);    
    bimg = ImageRenderWriter.render( content_pane );
    
    PreviewPanel pp = new PreviewPanel(bimg);
    pp.setPreferredSize(new Dimension((int)bimg.getWidth(),(int)bimg.getHeight()));    
    
    display_frame.setLocation((int)super.x_pos,(int)super.y_pos);

    display_frame.add(pp);
    display_frame.setTitle("PreviewGraphics - ViewManager");
    display_frame.pack();
    display_frame.setVisible(true);    
  }

  private class PreviewPanel extends Component 
  {
    BufferedImage bimg;
      
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
  
  public static void main(String[] args)
  {
    //  build my 2-D data
    int num_x_vals = 360;
    float sin_x[] = new float[num_x_vals];
    float sin_y[] = new float[num_x_vals];
    float sin_err[] = new float[num_x_vals];
    float cos_x[] = new float[num_x_vals];
    float cos_y[] = new float[num_x_vals];
    float cos_err[] = new float[num_x_vals];
    for ( int i = 0; i < num_x_vals; i++ )
    {
      // contruct sine function
      sin_x[i] = ((float)i)*(float)Math.PI/180f;
      sin_y[i] = (float)Math.sin((double)sin_x[i]);
      sin_err[i] = ((float)i)*.001f;
      // construct cosine function
      cos_x[i] = ((float)i)*(float)Math.PI/180f;
      cos_y[i] = (float)Math.cos((double)cos_x[i]);
      cos_err[i] = sin_err[i];
    }
    DataArray1D sine_graph = new DataArray1D( sin_x, sin_y, sin_err );
    sine_graph.setTitle("Sine Function");
    DataArray1D cosine_graph = new DataArray1D( cos_x, cos_y, cos_err );
    cosine_graph.setTitle("Cosine Function");
    Vector trig = new Vector();
    trig.add(sine_graph);
    trig.add(cosine_graph);
    // Put data array into a VirtualArrayList1D wrapper
    IVirtualArrayList1D va1D = new VirtualArrayList1D( trig );
    // Give meaningful range, labels, units, and linear or log display method.
    AxisInfo info = va1D.getAxisInfo( AxisInfo.X_AXIS );
    va1D.setAxisInfo( AxisInfo.X_AXIS, info.getMin(), info.getMax(), 
                "Angle","Radians", AxisInfo.LINEAR );
    info = va1D.getAxisInfo( AxisInfo.Y_AXIS );
    va1D.setAxisInfo( AxisInfo.Y_AXIS, info.getMin(), info.getMax(), 
          "Length","Unit Length", AxisInfo.LINEAR );
    va1D.setTitle("Sine and Cosine Function"); 
    
    DataSet ds = new DataSet();
    
    float[][] testData = ContourViewComponent.getTestDataArr(41,51,3,4);
    for (int i=0; i<testData.length; i++)
       ds.addData_entry(
             new FunctionTable(new UniformXScale(0, 
                               testData[i].length-1, 
                               testData[i].length),
                               testData[i], 
                               i));
    
    IVirtualArray2D va2D = ContourViewComponent.getTestData(51, 51, 3.0, 4.0);
    va2D.setAxisInfo( AxisInfo.X_AXIS, 0f, 10000f, 
           "TestX","TestUnits", AxisInfo.LINEAR );
    va2D.setAxisInfo( AxisInfo.Y_AXIS, 0f, 1500f, 
           "TestY","TestYUnits", AxisInfo.LINEAR );
    va2D.setAxisInfo( AxisInfo.Z_AXIS, 0f, 1f, "Z", "Units",
               AxisInfo.PSEUDO_LOG );
    va2D.setTitle("ScreenDevice Test");
    
    //Testing FileDevice
    PreviewDevice pd = new PreviewDevice();
    
    pd.setRegion(50,75,700,700);
    pd.display(ds,ViewManager.THREE_D);
    
    pd.setRegion(700,100,450,300);
    pd.display(va2D,ViewManager.IMAGE);
    
    pd.setRegion(700,500,550,400);
    pd.display(va1D,ViewManager.TABLE);
  }

}
