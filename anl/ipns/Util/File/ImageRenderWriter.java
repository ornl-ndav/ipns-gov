/*
 * File:  ImageRenderWriter.java
 *
 * Copyright (C) 2007, Andrew Moe, Ruth Mikkelson
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
 * $Log$
 * Revision 1.4  2007/07/31 18:30:44  amoe
 * Added static final fields for the default image width and height.
 *
 * Revision 1.3  2007/07/02 23:03:42  amoe
 * -Created new static methods render(..) and write(..) .
 * -Changed renderAndWrite(..) to static.
 * -Changed renderAndWrite to just use the render(..) and write(..)
 * methods.
 * -Removed the constructor now that the public methods are static.
 *
 * Revision 1.2  2007/06/28 17:49:36  amoe
 * Fixed copyright date.
 *
 * Revision 1.1  2007/06/28 17:48:24  amoe
 * Initial commit.
 *
 */
package gov.anl.ipns.Util.File;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
//import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
//import java.util.Vector;

//import gov.anl.ipns.Util.Sys.WindowShower;
//import gov.anl.ipns.ViewTools.Components.AxisInfo;
//import gov.anl.ipns.ViewTools.Components.IVirtualArray2D;
//import gov.anl.ipns.ViewTools.Components.IVirtualArrayList1D;
//import gov.anl.ipns.ViewTools.Components.ComponentView.DataSetSwapper;
//import gov.anl.ipns.ViewTools.Components.OneD.DataArray1D;
//import gov.anl.ipns.ViewTools.Components.OneD.FunctionViewComponent;
//import gov.anl.ipns.ViewTools.Components.OneD.VirtualArrayList1D;
//import gov.anl.ipns.ViewTools.Components.TwoD.Contour.ContourViewComponent;
//import gov.anl.ipns.ViewTools.Displays.Display1D;
//import gov.anl.ipns.ViewTools.Displays.Display2D;
//import gov.anl.ipns.ViewTools.Layouts.ComponentViewManager;
//import gov.anl.ipns.ViewTools.UI.JQuickFrame;

//import DataSetTools.dataset.DataSet;
//import DataSetTools.dataset.FunctionTable;
//import DataSetTools.dataset.UniformXScale;

//import DataSetTools.viewer.DataSetViewer;
//import DataSetTools.viewer.DataSetViewerMaker1;
//import DataSetTools.viewer.IViewManager;
//import DataSetTools.viewer.ViewManager;
//import DataSetTools.viewer.ViewerState;

import javax.swing.JComponent;
//import javax.swing.JMenu;
//import javax.swing.JTextField;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;

/**
 * This class provides an easy way to render <code>BufferedImage</code>s from 
 * <code>JComponent</code>s and write them to a file. The rendering and 
 * writing processes are done in separate threads and executed later on an 
 * event stack [uses <code>SwingUtilities.invokeLater(..)</code>].  This 
 * ensures that the image is rendered and written after the JComponent is 
 * completely built.
 */
public class ImageRenderWriter
{ 
  public static final int IMAGE_DEFAULT_WIDTH  = 1000;
  public static final int IMAGE_DEFAULT_HEIGHT = 1000;
  
  /**
   * This method renders an image from a <code>JComponent</code> and writes it 
   * to a file based on an format extension in the file_name. 
   * 
   * @param source - The <code>JComponent</code> that the image will be 
   *                 rendered from. 
   * @param file_name - The image will be output to this filename and/or 
   *                    address.  For example, "<code>image.jpg</code>" will 
   *                    be output to ISAW's root directory. 
   *                    "<code>/home/user/image.jpg</code>" will be output to 
   *                    the given directory.  As of JDK 1.6, the known 
   *                    possible image formats are: .jpg, .png, .bmp, .gif .   
   */
  public static void renderAndWrite(JComponent source,String file_name)
  {
    ImageRenderWriter.write(ImageRenderWriter.render(source),file_name);
  }
  
  /**
   * This method takes a <code>JComponent</code> and returns a 
   * <code>BufferedImage</code> rendering of it.
   * 
   * @param source - The <code>JComponent</code> used to make the rendering.
   * @return - The rendered <code>BufferedImage</code>.
   */
  public static BufferedImage render(JComponent source)
  {
    //making sure the JComponent is set to a valid size
    validateSize(source);
    int width = source.getWidth();
    int height = source.getHeight();
    
    BufferedImage bimg = new BufferedImage( width,height,
        BufferedImage.TYPE_INT_RGB);    
    Graphics2D gr = bimg.createGraphics();    

    JWindow jwin= new JWindow();
    jwin.setSize(width,height);
    jwin.getContentPane().setLayout( new GridLayout(1,1));
    jwin.getContentPane().add(source);
    jwin.pack();
    jwin.validate();

    //Invoke the following operations on EventQueue so that they finish in the
    // correct order.  ImageRendererThread renders the image, and 
    // ImageSaverThread saves it to a file.
    SwingUtilities.invokeLater( new ImageRendererThread( source, gr) );
    
    if(jwin != null)
    {
      jwin.dispose();
    }
      
    return bimg;
  }
  
  /**
   * This method takes a <code>BufferedImage</code> and writes it to a file 
   * with a specified file name.
   * 
   * @param bimg - The <code>BufferedImage</code> that will be used to write 
   *               the file. 
   * @param file_name - The image will be output to this filename and/or 
   *                    address.  For example, "<code>image.jpg</code>" will 
   *                    be output to ISAW's root directory. 
   *                    "<code>/home/user/image.jpg</code>" will be output to 
   *                    the given directory.  The filename must contain the 
   *                    name of the file with an image format extension! 
   *                    As of JDK 1.6, the known possible image formats are:
   *                    .jpg, .png, .bmp, .gif .                    
   */
  public static void write(BufferedImage bimg, String file_name)
  {    
    SwingUtilities.invokeLater(new ImageWriterThread( bimg, file_name, 
        parseFileExtension(file_name)));
  }
    
  /**
   * This method parses a file format extenstion from a filename.  If the 
   * filename does not contain a valid extension or any extension at all,
   * the method will return 'png' by default.
   */
  private static String parseFileExtension(String file_name)
  {
    int i = file_name.lastIndexOf('.');
    if( i<= 0)
    {
      //ERROR: The Filename must have an extension for type of save
      return "png";
    }

    String extension = file_name.substring( i+1 ).toLowerCase();
    if( extension == null)
    {
      //ERROR: Save FileName must have an extension
      return "png";
    }
    
    if( extension.length() <2)
    {
      //ERROR: Save FileName must have an extension with more than 1 character
      return "png";
    }
    
    return extension;
  }
  
  /**
   * This class makes sure that the specified <code>JComponent</code>'s size 
   * is greater than zero.
   * 
   * @param source - The <code>JComponent</code> whose size will be validated.
   */
  private static void validateSize(JComponent source)
  {
    int width = source.getWidth();
    int height = source.getHeight();    
    
    //making sure the source size is at least greater than 0.
    if( width <=0) 
    {
      width = IMAGE_DEFAULT_WIDTH;
      source.setSize(width,height);
    }    
    if( height <= 0)
    {
      height = IMAGE_DEFAULT_HEIGHT;
      source.setSize(width,height);
    }
    
    //this ensures that the image will be the specified size
    source.setMinimumSize(new Dimension(width, height));
    source.setPreferredSize(new Dimension(width, height));
  }
  
  /**
   * Thread for painting the <code>JComponent</code> to the 
   * <code>BufferedImage</code>.
   * @author Andrew Moe
   * @author Ruth Mikkelson
   */
  private static class ImageRendererThread extends Thread
  {
    private JComponent jc = null;
    private Graphics gr = null;
    
    public ImageRendererThread(JComponent jc, Graphics gr)
    {
      this.jc = jc;
      this.gr = gr;
    }
    
    public void run()
    {
      //if the JComponent and the graphics are not null, then make a rendering
      if(jc != null && gr != null)
      {
        jc.paint(gr);
      }            
    }
  }
    
  /**
   * Thread to write the <code>BufferedImage</code> to the output file.
   * @author Andrew Moe
   * @author Ruth Mikkelson
   */
  private static class ImageWriterThread extends Thread
  {
    private BufferedImage bimg;
    private String save_file_name;
    private String extension;
    
    public ImageWriterThread(BufferedImage bimg, String save_file_name, 
                            String extension)
    {
      this.bimg = bimg;
      this.save_file_name=save_file_name;
      this.extension= extension;
    }
    
    public void run()
    {     
        try
        {           
          FileOutputStream fout = new FileOutputStream( save_file_name);
          if( !javax.imageio.ImageIO.write( bimg, extension ,fout ) )
          {
            System.out.println("Image failed to save.");
          }
          fout.close(); 
        }
        catch(Exception s)
        {
          System.out.println(
              "Exception in ImageRenderWriter$ImageSaverThread.run() ");
          s.printStackTrace();
        }
    }
  }
   
  /*
  public static void main(String[] args)
  {
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
    va2D.setTitle("FileDevice Test");
    
    
    // build my 2 functions (graphs) consisting of x, y, and error values.
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
    // Each DataArray1D object represents a graph. The parameters are
    // x values, y values, and error values. It is assumed the graph is
    // "selected" or displayed, but not "pointed-at".
    DataArray1D sine_graph = new DataArray1D( sin_x, sin_y, sin_err );
    sine_graph.setTitle("Sine Function");
    DataArray1D cosine_graph = new DataArray1D( cos_x, cos_y, cos_err );
    cosine_graph.setTitle("Cosine Function");
    // put all of the functions into a list structure (Vector)
    Vector trig = new Vector();
    trig.add(sine_graph);
    trig.add(cosine_graph);
    // Put list of functions into a VirtualArrayList1D wrapper. The
    // VirtualArray concept allows you to add information to the x and y axis.
    // The AxisInfo object consists of an axis specification, min, max, label,
    // units, and linear (true) or log (false) display.
    IVirtualArrayList1D va1D = new VirtualArrayList1D( trig );
    // Give meaningful range, labels, units, and linear or log display method.
    AxisInfo info = va1D.getAxisInfo( AxisInfo.X_AXIS );
    va1D.setAxisInfo( AxisInfo.X_AXIS, info.getMin(), info.getMax(), 
                "Angle","Radians", AxisInfo.LINEAR );
    info = va1D.getAxisInfo( AxisInfo.Y_AXIS );
    va1D.setAxisInfo( AxisInfo.Y_AXIS, info.getMin(), info.getMax(), 
          "Length","Unit Length", AxisInfo.LINEAR );
    va1D.setTitle("Sine and Cosine Function");
    
    //DataSetViewer
    DataSetViewer dsv = ViewManager.getDataSetView( 
                        ds, IViewManager.THREE_D, null);
    dsv.setSize(new Dimension(600, 500));
    
    //DataSetSwapper
    //DataSetSwapper dss = new DataSetSwapper(va2D,"Image");
    //dss.setSize(new Dimension(600, 500));
    
    //Display
    Display1D display = new Display1D(va1D,Display1D.GRAPH,Display1D.CTRL_ALL);
    //Display2D display = new Display2D(va2D,Display2D.IMAGE,Display2D.CTRL_ALL);
        
    //WindowShower.show(display);
    JComponent jc = ((JComponent)display.getContentPane());
    jc.setSize(200,200);
    
    //JTextField jm = new JTextField("TestTextField");
    //jm.setSize(200,200);
    
    //Test
    //ImageRenderWriter irw = 
    //              new ImageRenderWriter(jc,"/home/moea/image_test.png");
    //System.out.println(irw.renderAndWrite());
    
    ImageRenderWriter.renderAndWrite(jc,"/home/moea/image_test.png");
    
    //System.exit(0);
  }
  //*/

}
