/*
 * File:FileDevice.java 
 *  
 * Copyright (C) 2007     Andy Moe
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
 * Contact :  Dennis Mikkelson<mikkelsond@uwstout.edu>
 *            MSCS Department
 *            Menomonie, WI. 54751
 *            (715)-232-2291
 *
 * This work was supported by the National Science Foundation under grant
 * number DMR-0426797, and by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 *
 *  Last Modified:
 * 
 *  $Author$
 *  $Date$            
 *  $Revision$
 *
 * Modified:
 *
 * $Log$
 * Revision 1.9  2007/08/22 15:12:47  rmikk
 * Added GPL
 *
 * Revision 1.8  2007/08/02 15:37:40  oakgrovej
 * Allowed for multiple components to be displayed in the file.  Any empty 
 * space below or to the right of the components added will be cut off.
 *
 * Revision 1.7  2007/07/31 19:13:47  amoe
 * -Added static final default image width and height.
 * -Set the image to the default size when the current size has not been set 
 *  (when it's -1).
 *
 * Revision 1.6  2007/07/17 16:16:37  oakgrovej
 * Added Throws Exception where needed
 *
 * Revision 1.5  2007/07/16 14:52:05  dennis
 * Added parameter, with_controls, to the display method, so that
 * any device type can easily display viewers with or without the
 * controls.
 *
 * Revision 1.4  2007/07/13 21:22:59  amoe
 * Updated NOP comment in getBounds() .
 *
 * Revision 1.3  2007/07/13 01:26:58  amoe
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

import java.awt.image.BufferedImage;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JPanel;

public class FileDevice extends GraphicsDevice
{
  private String file_name;
  private BufferedImage bimg = null;
  private JComponent jcomp;
  private int max_x = 0;
  private int max_y = 0;
  
  private final int DEFAULT_IMAGE_WIDTH = 1280;
  private final int DEFAULT_IMAGE_HEIGHT= 1024;
  
  public FileDevice(String file_name)
  {
    this.file_name = file_name;
    x_pos = 0;
    y_pos = 0;
    width = DEFAULT_IMAGE_WIDTH;
    height = DEFAULT_IMAGE_HEIGHT;
    jcomp = new JPanel();
    jcomp.setBounds(0, 0, width, height);
    jcomp.setLayout(null);
    
  }

  /**
   * Set an attribute for the FileDevice.
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
   * Flush any pending output. For FileDevice, this saves an image to a file.
   */
  @Override
  public void print() 
  {
    ImageRenderWriter.write(bimg,file_name);
  }
  
  /**
   * Flush and closes any pending output for the FileDevice.
   */
  @Override
  public void close() 
  {
    // do nothing?    
  }
  
  /**
   * @return - This returns a Vector with two floats: width and height of a 
   * specific device.
   */
  @Override
  public Vector getBounds() 
  {
    // NOP, the file resolution is limitless.
    return null;
  }

  /**
   * Display the specified IDisplayable with the specified region, view type, 
   * line, and graph attributes.
   * 
   * @param disp          - IDisplayable to be displayed.
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
    jcomp.setBounds( x_pos, y_pos, width, height );
    
    if( x_pos + width > max_x )
      max_x = x_pos + width;
    if( y_pos + height > max_y )
      max_y = y_pos + height;
      
    this.jcomp.add(jcomp);
    this.jcomp.setSize(max_x,max_y);
    bimg = ImageRenderWriter.render(this.jcomp);
  }
 
  public static void main(String[] args)throws Exception
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
    
    FileDevice prv_dev = new FileDevice("/home/moea/fd_out.jpg");
    //prv_dev.setRegion(50,50,650,550);
    
    prv_dev.display(va2d_disp,false);
    prv_dev.print();
  }

}

