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
 * Revision 1.2  2007/06/28 17:49:36  amoe
 * Fixed copyright date.
 *
 * Revision 1.1  2007/06/28 17:48:24  amoe
 * Initial commit.
 *
 */
package gov.anl.ipns.Util.File;

import gov.anl.ipns.Util.SpecialStrings.ErrorString;
import gov.anl.ipns.Util.SpecialStrings.SaveFileString;
import gov.anl.ipns.Util.SpecialStrings.SpecialString;
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

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
//import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
//import java.util.Vector;

import javax.swing.JComponent;
//import javax.swing.JMenu;
//import javax.swing.JTextField;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;

/**
 * This class takes a <code>JComponent</code>, renders an image from it, and 
 * saves it to a file. The rendering and writing processes are done in 
 * separate threads and executed later on an event stack 
 * [uses <code>SwingUtilities.invokeLater(..)</code>].  This ensures that the 
 * image is rendered and written after the JComponent is completely built.
 */
public class ImageRenderWriter
{
  private JComponent source;  
  private String file_name;  
  private int width;
  private int height;  
  private String extension;
   
  /**
   * Creates an ImageRenderWriter object.
   * 
   * @param source - The JComponent that the image will be rendered from.
   * @param file_name - The image will be outputed to this filename and/or 
   *                    address.  For example, "<code>image.jpg</code>" will 
   *                    be output to ISAW's root directory. 
   *                    "<code>/home/user/image.jpg</code>" will be output to 
   *                    the given directory.  The filename must contain the 
   *                    name of the file with an image format extension! 
   *                    As of JDK 1.6, the known possible image formats are:
   *                    .jpg, .png, .bmp, .gif .                    
   */
  public ImageRenderWriter(JComponent source, String file_name)
  {
    this.source = source;
    this.file_name = file_name;
    this.width = source.getWidth();
    this.height = source.getHeight();    
    
    //making sure that the JComponent size is at least greater than 0.
    if( width <=0) 
    {
      width = 500;
      source.setSize(width,height);
    }    
    if( height <= 0)
    {
      height = 500;
      source.setSize(width,height);
    }
    
    //this ensures that the image will be the specified size
    source.setMinimumSize(new Dimension(width, height));
    source.setPreferredSize(new Dimension(width, height));
  }
  
  /**
   * Render and writes an image from the JComponent to the indicated file in 
   * the format specified by the extension of the file.
   * @author Andrew Moe
   * @author Ruth Mikkelson
   */
  public Object renderAndWrite()
  {
    SpecialString temp_extension = parseFileExtension(file_name);
    //If extension parse fails, then quit the method and return error string
    if(temp_extension instanceof SaveFileString) 
    {                                             
      extension = temp_extension.toString();        
    }
    else
      return temp_extension;    
           
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
    SwingUtilities.invokeLater( 
        new ImageSaverThread( bimg, file_name, extension));
        
    if(jwin != null)
    {
      jwin.dispose();
    }    
    
    return "Success";
  }
   
  /**
   * This method parses a file format extenstion from a filename.
   * @author Andrew Moe
   * @author Ruth Mikkelson
   */
  private SpecialString parseFileExtension(String file_name)
  {
    int i = file_name.lastIndexOf('.');
    if( i<= 0)
    {
      return new ErrorString(
          "ERROR: The Filename must have an extension for type of save");
    }

    String extension = file_name.substring( i+1).toLowerCase();
    if( extension == null)
    {
      return new ErrorString("Save FileName must have an extension");
    }
    
    if( extension.length() <2)
    {
      return new ErrorString(
          "Save FileName must have an extension with more than 1 character");
    }
    
    return new SaveFileString(extension);
  }
  
  /**
   * Thread for painting the JComponent to the BufferedImage
   * @author Andrew Moe
   * @author Ruth Mikkelson
   */
  private class ImageRendererThread extends Thread
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
   * Thread to write the BufferedImage to the output file.
   * @author Andrew Moe
   * @author Ruth Mikkelson
   */
  private class ImageSaverThread extends Thread
  {
    private BufferedImage bimg;
    private String save_file_name;
    private String extension;
    
    public ImageSaverThread(BufferedImage bimg, String save_file_name, 
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
    //DataSetViewer dsv = ViewManager.getDataSetView( 
    //                    ds, IViewManager.IMAGE, null);
    //dsv.setSize(new Dimension(600, 500));
    
    //DataSetSwapper
    //DataSetSwapper dss = new DataSetSwapper(va2D,"Image");
    //dss.setSize(new Dimension(600, 500));
    
    //Display
    //Display1D display = new Display1D(va1D,Display1D.GRAPH,Display1D.CTRL_ALL);
    Display2D display = new Display2D(va2D,Display2D.IMAGE,Display2D.CTRL_ALL);
        
    //WindowShower.show(display);
    JComponent jp = ((JComponent)display.getContentPane());
    jp.setSize(600,500);
    
    //JTextField jm = new JTextField("TestTextField");
    //jm.setSize(200,200);
    
    //Test
    ImageRenderWriter irw = 
                  new ImageRenderWriter(jp,"/home/moea/image_test.png");
    System.out.println(irw.renderAndWrite());
    
    System.exit(0);
  }
  //*/

}
