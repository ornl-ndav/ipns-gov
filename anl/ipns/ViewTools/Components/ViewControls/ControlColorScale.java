/*
 * File: ControlColorScale.java
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
 *  Revision 1.2  2003/06/09 22:36:54  dennis
 *  - Added setEventListening(false) method call for ColorScaleImage to
 *    ignore keyboard/mouse events. (Mike Miller)
 *
 *  Revision 1.1  2003/05/29 14:25:27  dennis
 *  Initial version, displays the possible colors of the ImageViewComponent.
 *  (Mike Miller)
 *
 */
  
 package DataSetTools.components.View.ViewControls;
 
 import javax.swing.*;
 import javax.swing.event.*;
 import java.awt.event.*;
 import java.awt.*;
 
 import DataSetTools.components.image.IndexColorMaker;
 import DataSetTools.components.ui.ColorScaleImage;

/**
 * This class is a ViewControl (ActiveJPanel) with a color scale for use 
 * by ViewComponents. No messages are sent by this control.
 */ 
public class ControlColorScale extends ViewControl
{
   private ColorScaleImage csi;
   private String colorscheme;
   
  /* Possible color schemes as designated by 
   * DataSetTools/components/image/IndexColorMaker.java
   *
   * GRAY_SCALE		 
   * NEGATIVE_GRAY_SCALE   
   * GREEN_YELLOW_SCALE	 
   * HEATED_OBJECT_SCALE   
   * HEATED_OBJECT_SCALE_2 
   * RAINBOW_SCALE	 
   * OPTIMAL_SCALE	 
   * MULTI_SCALE  	 
   * SPECTRUM_SCALE
   */	 
   
  /**
   * Default constructor specifies sets colorscale to the specified.
   */ 
   public ControlColorScale( String colorscale )
   {  
      super("");
      this.setLayout( new GridLayout(1,1) );
      csi = new ColorScaleImage();
      csi.setEventListening(false);
      this.add(csi);
      colorscheme = colorscale; 
      csi.setNamedColorModel( colorscale, true );   
   }
  
  /**
   * Same functionality as default constructor, only this constructor allows
   * for title specification of the border.
   *
   *  @param  title
   */ 
   public ControlColorScale(String title, String colorscale)
   {
      this(colorscale);
      this.setTitle(title);
   }
   
  /**
   * This method sets the color scheme of the ColorScaleImage.
   * Possible color schemes are listed in IndexColorMaker.java.
   *
   *  @param  colorscale
   */
   public void setColorScale( String colorscale )
   {
      colorscheme = colorscale;
      csi.setNamedColorModel( colorscale, true );
   } 
   
  /**
   * This method gets the color scheme of the ColorScaleImage.
   * The scheme will be one listed in IndexColorMaker.java.
   *
   *  @param  color
   */
   public String getColorScale()
   {
      return colorscheme;
   }      

  /*
   *  For testing purposes only
   */
   public static void main(String[] args)
   {
      ControlColorScale color = 
                    new ControlColorScale(IndexColorMaker.MULTI_SCALE);
      JFrame frame = new JFrame();
      frame.getContentPane().setLayout( new GridLayout(1,1) );
      frame.setTitle("ControlColorScale Test");
      frame.setBounds(0,0,230,80);
      frame.getContentPane().add(color);
      color.setTitle("myColorScale"); 
      
      frame.setVisible(true);
   }
}
