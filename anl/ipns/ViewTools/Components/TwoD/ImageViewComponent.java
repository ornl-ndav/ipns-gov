/*
 * File: ImageViewComponent.java
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
 *  Revision 1.5  2003/05/20 19:46:16  dennis
 *  Now creates a brightness control slider. (Mike Miller)
 *
 *  Revision 1.4  2003/05/16 15:25:12  dennis
 *  Implemented dataChanged() method.
 *  Added grid lines to test image to aid in testing.
 *
 *  Revision 1.3  2003/05/16 14:59:11  dennis
 *  Calculates space needed for labels, and adjusts space as the component
 *  is resized.  (Mike Miller)
 *
 */
 
package DataSetTools.components.View.TwoD;

import javax.swing.*; 
import javax.swing.event.*;
import java.io.Serializable;
import java.awt.*;
import java.lang.*;
import java.awt.event.*;
import java.awt.Rectangle.*;
import java.util.*; 
import java.awt.font.FontRenderContext;

import DataSetTools.util.*;  //floatPoint2D
import DataSetTools.math.*;
import DataSetTools.components.image.*; //ImageJPanel & CoordJPanel
import DataSetTools.components.View.Transparency.*;
import DataSetTools.components.View.*;  // IVirtualArray2D
import DataSetTools.components.View.ViewControls.*;

// component changes
import java.applet.Applet;

// Component location and resizing within the big_picture
import java.awt.event.ComponentAdapter.*;
import java.awt.geom.*;

/**
 * This class allows the user to view data in the form of an image. Meaning
 * is given to the data by way of overlays, which add calibration, selection,
 * and annotation abilities.
 */
public class ImageViewComponent implements IViewComponent2D, 
                                           ActionListener,
					   IAxisAddible2D
{
   private IVirtualArray2D Varray2D;  //An object containing our array of data
   private Point[] selectedset; //To be returned by getSelectedSet()   
   private Vector Listeners = null;   
   private JPanel big_picture = new JPanel();    
   private ImageJPanel ijp;
   // for component size and location adjustments
   //private ComponentAltered comp_listener;
   private Rectangle regioninfo;
   private Vector transparencies = new Vector();
   private int precision;
   private Font font;
   private LinkedList controls = new LinkedList();
   
  /**
   * Constructor that takes in a virtual array and creates an imagejpanel
   * to be viewed in a border layout.
   */
   public ImageViewComponent( IVirtualArray2D varr )  
   {
      Varray2D = varr; // Get reference to varr
      precision = 4;
      font = FontUtil.LABEL_FONT2;
      ijp = new ImageJPanel();
      //Make ijp correspond to the data in f_array
      ijp.setData(varr.getRegionValues(0, 1000000, 0, 1000000), true); 
      ImageListener ijp_listener = new ImageListener();
      ijp.addActionListener( ijp_listener );
                  
      ComponentAltered comp_listener = new ComponentAltered();   
      ijp.addComponentListener( comp_listener );
      
      AxisInfo2D xinfo = varr.getAxisInfoVA(AxisInfo2D.XAXIS);
      AxisInfo2D yinfo = varr.getAxisInfoVA(AxisInfo2D.YAXIS);
      
      ijp.initializeWorldCoords( new CoordBounds( xinfo.getMin(),
                                                  yinfo.getMax(),      
                                                  xinfo.getMax(),
						  yinfo.getMin() ) ); 
      Listeners = new Vector();
      buildViewComponent(ijp); // initializes big_picture to jpanel containing
                               // the background and transparencies 		       
      buildViewControls(ijp);   
   }  
   
  // getAxisInfo(), getRegionInfo(), getTitle(), getPrecision(), getFont() 
  // all required since this component implements IAxisAddible2D
  /**
   * This method returns the info about the specified axis. 
   * 
   *  @param  isX
   *  @return If isX = true, return info about x axis.
   *          If isX = false, return info about y axis.
   */
   public AxisInfo2D getAxisInfo( boolean isX )
   {
      // if true, return x info
      if( isX )
         return new AxisInfo2D( ijp.getLocalWorldCoords().getX1(),
	               ijp.getLocalWorldCoords().getX2(),
		       Varray2D.getAxisInfoVA(AxisInfo2D.XAXIS).getLabel(),
		       Varray2D.getAxisInfoVA(AxisInfo2D.XAXIS).getUnits(),
		       Varray2D.getAxisInfoVA(AxisInfo2D.XAXIS).getIsLinear() );
      // if false return y info
      return new AxisInfo2D( ijp.getLocalWorldCoords().getY1(),
	               ijp.getLocalWorldCoords().getY2(),
		       Varray2D.getAxisInfoVA(AxisInfo2D.YAXIS).getLabel(),
		       Varray2D.getAxisInfoVA(AxisInfo2D.YAXIS).getUnits(),
		       Varray2D.getAxisInfoVA(AxisInfo2D.YAXIS).getIsLinear() );
   }
   
  /**
   * This method returns a rectangle containing the location and size
   * of the imagejpanel.
   *
   *  @return The region info about the imagejpanel
   */ 
   public Rectangle getRegionInfo()
   {
      return regioninfo;
   }    
  
  /**
   * This method will return the title given to the image as specified by
   * the Virtual Array
   *
   *  @return title stored in Virtual Array
   */
   public String getTitle()
   {
      return Varray2D.getTitle();
   }
   
  /**
   * This method will return the precision specified by the user. Precision
   * will be assumed to be 4 if not specified. The overlays will call
   * this method to determine the precision.
   *
   *  @return precision of displayed values
   */
   public int getPrecision() 
   {
      return precision;
   }  
   
  /**
   * This method will return the font used on by the overlays. The axis overlay
   * will call this to determine what font to use.
   *
   *  @return font of displayed values
   */
   public Font getFont()
   {
      return font;
   }
    
  // Methods required since implementing IViewComponent2D
  /**
   * This method adjusts the crosshairs on the imagejpanel.
   * setPointedAt is called from the viewer when another component
   * changes the selected point.
   *
   *  @param  pt
   */
   public void setPointedAt( Point pt )
   {
      System.out.println("Entering: void setPointedAt( Point pt )");
      System.out.println("X value = " + pt.getX() );
      System.out.println("Y value = " + pt.getY() );
      
      //Type cast Point pt  into  floatPoint2D fpt
      floatPoint2D fpt = new floatPoint2D( (float)pt.x, (float)pt.y );
      
      //set the cursor position on ImageJPanel
      ijp.setCurrent_WC_point( fpt ); 
      
      System.out.println("");
   }
  
  /**
   * This method creates a selected region to be displayed over the imagejpanel
   * by an overlay.
   *
   *  @param  pts
   */ 
   public void setSelectedSet( Point[] pts ) 
   {
      // implement after selection overlay has been created
      System.out.println("Entering: void setSelectedSet( Point[] coords )");
      System.out.println("");
   }
  
  /**
   * This method will be called to notify this component of a change in data.
   */
   public void dataChanged()  
   {
      float[][] f_array = Varray2D.getRegionValues( 0, 1000000, 0, 1000000 );
      ijp.setData(f_array, true);
   }
  
  /**
   * To be continued...
   */ 
   public void dataChanged( IVirtualArray2D pin_Varray ) // pin == "passed in"
   {
      System.out.println("Now in void dataChanged(VirtualArray2D pin_Varray)");

      //get the complete 2D array of floats from pin_Varray
      float[][] f_array = Varray2D.getRegionValues( 0, 1000000, 0, 1000000 );

      ijp.setData(f_array, true);  
      
      System.out.println("Value of first element: " + f_array[0][0] );
      System.out.println("Thank you for notifying us");
      System.out.println("");
   }
  
  /**
   * Get selected set specified by setSelectedSet. The selection overlay
   * will need to use this method.
   *
   *  @return selectedset
   */ 
   public Point[] getSelectedSet() //keep the same (for now)
   {
      System.out.println("Entering: Point[] getSelectedSet()");
      System.out.println("");
      return selectedset;
   }
   
  /**
   * Method to add a listener to this component.
   *
   *  @param act_listener
   */
   public void addActionListener( ActionListener act_listener )
   {     
      System.out.print("Entering: void ");
      System.out.println("addActionListener( ActionListener act_listener )");
      
      for ( int i = 0; i < Listeners.size(); i++ )    // don't add it if it's
        if ( Listeners.elementAt(i).equals( act_listener ) ) // already there
          return;

      Listeners.add( act_listener ); //Otherwise add act_listener
      System.out.println("");
   }
  
  /**
   * Method to remove a listener from this component.
   *
   *  @param act_listener
   */ 
   public void removeActionListener( ActionListener act_listener )
   {
      Listeners.remove( act_listener );
   }
  
  /**
   * Method to remove all listeners from this component.
   */ 
   public void removeAllActionListeners()
   {
      Listeners.removeAllElements();
   }
   
   public JComponent[] getSharedControls()
   {  
      JComponent[] jcontrols = new JComponent[controls.size()];
      for( int i = 0; i < controls.size(); i++ )
         jcontrols[i] = (JComponent)controls.get(i);   
      return jcontrols;
   }
   
   public JComponent[] getPrivateControls()
   {
      System.out.println("Entering: JComponent[] getPrivateControls()");
      System.out.println("");
      
      return new JComponent[0];
   }
   
   public JMenuItem[] getSharedMenuItems()
   {
      System.out.println("Entering: JMenuItems[] getSharedMenuItems()");
      System.out.println("");
      
      return new JMenuItem[0];
   }
   
   public JMenuItem[] getPrivateMenuItems()
   {
      System.out.println("Entering: JMenuItems[] getPrivateMenuItems()");
      System.out.println("");
      
      return new JMenuItem[0];
   }
   
  /**
   * Return the "background" or "master" panel
   *
   *  @return JPanel containing imagejpanel in the center of a borderlayout.  
   */
   public JPanel getDisplayPanel()
   {
       return big_picture;   
   }

  /*
   *  Gets the current point
   */
   public Point getCurrentPoint()
   {
     floatPoint2D fpt = new floatPoint2D();
     fpt = ijp.getCurrent_WC_point();
     
     Point pt = new Point((int)fpt.x, (int)fpt.y);
     
     return pt;
   }
   
  /*
   * Tells all listeners about a new action.
   *
   *  @param  message
   */  
   private void sendMessage( String message )
   {
     for ( int i = 0; i < Listeners.size(); i++ )
     {
       ActionListener listener = (ActionListener)Listeners.elementAt(i);
       listener.actionPerformed( new ActionEvent( this, 0, message ) );
     }
   }
  
  // required since implementing ActionListener
  /**
   * To be continued...
   */ 
   public void actionPerformed( ActionEvent e )
   {
     //get POINTED_AT_CHANGED or SELECTED_CHANGED message from e 
     String message = e.getActionCommand();     
     
     //Send message to tester 
     if (message == "POINTED_AT_CHANGED")
         sendMessage(POINTED_AT_CHANGED);
   }
   
  /*
   * This method takes in an imagejpanel and puts it into a borderlayout.
   * Overlays are added to allow for calibration, selection, and annotation.
   */
   private void buildViewComponent( ImageJPanel panel )
   {   
      int westwidth = font.getSize() * precision + 22;
      int southwidth = font.getSize() * 3 + 9;
      // this will be the background for the master panel
      JPanel background = new JPanel(new BorderLayout());
      
      JPanel north = new JPanel(new FlowLayout());
      north.setPreferredSize(new Dimension( 0, 25 ) );
      JPanel east = new JPanel(new FlowLayout());
      east.setPreferredSize(new Dimension( 50, 0 ) );
      JPanel south = new JPanel(new FlowLayout());
      south.setPreferredSize(new Dimension( 0, southwidth ) );
      JPanel west = new JPanel(new FlowLayout());
      west.setPreferredSize(new Dimension( westwidth, 0 ) );
      
      //Construct the background JPanel
	
      background.add(panel, "Center");
      background.add(north, "North");
      background.add(west, "West");
      background.add(south, "South");
      background.add(east, "East" );      
      
      AxisOverlay2D top = new AxisOverlay2D(this);
      transparencies.add(top);       // add the transparency the the vector
      	  
      // create master panel and
      //  add background and transparency to the master layout
      
      JPanel master = new JPanel();
      OverlayLayout overlay = new OverlayLayout(master);
      master.setLayout(overlay);
      for( int trans = 0; trans < transparencies.size(); trans++ )
         master.add((OverlayJPanel)transparencies.elementAt(trans)); 
      master.add(background);

      big_picture = master;
   }
   
  /*
   * This method constructs the controls required by the ImageViewComponent
   */
   private void buildViewControls( ImageJPanel iJpanel )
   {
      ControlSlider intensity_slider = new ControlSlider();
      ControlSlider slider2 = new ControlSlider();
      intensity_slider.setTitle("Intensity Slider");
      slider2.setTitle("Not Attached");
      slider2.showTicks(false);
      intensity_slider.addActionListener( new SliderListener() );
      controls.add(intensity_slider);
      controls.add(slider2);
   }
   
  //***************************Assistance Classes******************************
  /*
   * ComponentAltered monitors if the imagejpanel has been resized. If so,
   * the regioninfo is updated.
   */
   private class ComponentAltered extends ComponentAdapter
   {
      public void componentResized( ComponentEvent e )
      {
         //System.out.println("Component Resized");
	 Component center = e.getComponent();
	 regioninfo = new Rectangle( center.getLocation(), center.getSize() );
	 /*
	 System.out.println("Location = " + center.getLocation() );
	 System.out.println("Size = " + center.getSize() );
	 System.out.println("class is " + center.getClass() );  
	 */
      }
   }

  /*
   * ImageListener monitors if the imagejpanel has sent any messages.
   * If so, process the message and relay it to the viewer.
   */
   private class ImageListener implements ActionListener
   {
      public void actionPerformed( ActionEvent ae )
      {
         String message = ae.getActionCommand();     
         //System.out.println("Image sent message " + message );

         if (message == CoordJPanel.CURSOR_MOVED)
         {
	    //System.out.println("Sending POINTED_AT_CHANGED" );
            sendMessage(POINTED_AT_CHANGED);
	 }
	 if (message == CoordJPanel.ZOOM_IN)
         {
	    //System.out.println("Sending SELECTED_CHANGED " + regioninfo );
	    for(int next = 0; next < transparencies.size(); next++ )
	       ((OverlayJPanel)transparencies.elementAt(next)).repaint();
            sendMessage(SELECTED_CHANGED);
	 }
	 if (message == CoordJPanel.RESET_ZOOM)
         {
	    //System.out.println("Sending SELECTED_CHANGED" );
	    for(int next = 0; next < transparencies.size(); next++ )
	       ((OverlayJPanel)transparencies.elementAt(next)).repaint();
            sendMessage(SELECTED_CHANGED);
	 }
      }      
   }
   
  /*
   * SliderListener moniters activities of a ControlSlider, which is a 
   * control of the ImageViewComponent.
   */
   private class SliderListener implements ActionListener
   { 
      public void actionPerformed( ActionEvent ae )
      {
         ControlSlider control = (ControlSlider)ae.getSource();
         String message = ae.getActionCommand();
                              // set image log scale when slider stops moving
         if ( message == IViewControl.IS_CHANGED )
         {
	    ijp.changeLogScale( control.getValue(), true );
         } 
      }
   } 
      
  /*
   * MAIN - Basic main program to test an ImageViewComponent object
   */
   public static void main( String args[] ) 
   {
        int col = 250;
	int row = 250;
	
        //Make a sample 2D array
	VirtualArray2D va2D = new VirtualArray2D(row, col); 
        va2D.setAxisInfoVA( AxisInfo2D.XAXIS, .001f, .1f, 
                           "TestX","TestUnits", true );
	va2D.setAxisInfoVA( AxisInfo2D.YAXIS, 0f, -1f, 
                            "TestY","TestYUnits", true );
	va2D.setTitle("Main Test");
	//Fill the 2D array with the function x*y
	float ftemp;
	for(int i = 0; i < row; i++)
	{
	    for(int j = 0; j < col; j++)
	    {
		ftemp = i*j;
                if ( i % 25 == 0 )
	          va2D.setDataValue(i, j, i*col); //put float into va2D
                else if ( j % 25 == 0 )
	          va2D.setDataValue(i, j, j*row); //put float into va2D
                else
	          va2D.setDataValue(i, j, ftemp); //put float into va2D
	    }
	}

        //Construct an ImageViewComponent with array2D
	ImageViewComponent ivc = new ImageViewComponent(va2D);

        //A tester frame to throw the bottom and top JPanel into **********
        JFrame f = new JFrame("ISAW ImageViewComponent");  
	f.setBounds(0,0,500,500);
	Container c = f.getContentPane();
	c.add(ivc.getDisplayPanel());
	  
	f.show(); //display the frame
	
	JFrame f2 = new JFrame("ISAW ImageViewControls");
	Container cpain = f2.getContentPane();
	cpain.setLayout( new BoxLayout(cpain,BoxLayout.Y_AXIS));  
	
	JComponent[] controls = ivc.getSharedControls();
	for( int i = 0; i < controls.length; i++ )
	   cpain.add(controls[i]);
	f2.setBounds(0,0,200,(100 * controls.length));
	cpain.validate();  
	f2.show(); //display the frame
   }
}
