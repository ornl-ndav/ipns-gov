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
 * This work was supported by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 */
 
/*
 * Integrate the font and precision into affecting the size of the west panel
 * in the buildViewComponent()
 */ 
package DataSetTools.components.View.TwoD;

import javax.swing.*; 
import java.awt.*;
import java.lang.*;
import java.awt.event.*;
import java.awt.Rectangle.*;
import java.util.*; 

import DataSetTools.util.*;  //floatPoint2D
import DataSetTools.math.*;
import DataSetTools.components.image.*; //ImageJPanel & CoordJPanel
import DataSetTools.components.View.Transparency.*;
import DataSetTools.components.View.*;  // IVirtualArray2D

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
   private ImageJPanel ijp = new ImageJPanel();
   // for component size and location adjustments
   //private ComponentAltered comp_listener;
   private Rectangle regioninfo;
   private Vector transparencies = new Vector();
   private int precision;
   private Font font;
   
  /**
   * Constructor that takes in a virtual array and creates an imagejpanel
   * to be viewed in a border layout.
   */
   public ImageViewComponent( IVirtualArray2D varr )  
   {
      Varray2D = varr; // Get reference to varr
      precision = 4;
      font = FontUtil.LABEL_FONT;
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
   */
   public String getTitle()
   {
      return Varray2D.getTitle();
   }
   
  /**
   * This method will return the precision specified by the user. Precision
   * will be assumed to be 4 if not specified. The axis overlays will call
   * this method to determine the precision.
   */
   public int getPrecision() 
   {
      return precision;
   }  
   
  /**
   * This method will return the font used on by the overlays. The axis overlay
   * will call this to determine what font to use.
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
      System.out.println("Entering: void dataChanged()");
      System.out.println("Thank you for notifying us");
      System.out.println("");
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
      System.out.print("Entering: void ");
      System.out.println("removeActionListener( ActionListener act_listener )");
    
      Listeners.remove( act_listener );
	  
      System.out.println("");
   }
  
  /**
   * Method to remove all listeners from this component.
   */ 
   public void removeAllActionListeners()
   {
      System.out.println("Entering: void removeAllActionListeners()");

      Listeners.removeAllElements();

      System.out.println("");
   }
   
   public JComponent[] getSharedControls()
   {
      System.out.println("Entering: JComponent[] getSharedControls()");
      System.out.println("");
      
      return new JComponent[0];
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
   *
   public Point getCurrentPoint()
   {
     floatPoint2D fpt = new floatPoint2D();
     fpt = ijp.getCurrent_WC_point();
     
     Point pt = new Point((int)fpt.x, (int)fpt.y);
     
     return pt;
   }*/
   
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
   /* ************************ old background model *************************
      // this will be the background for the master panel
      JPanel background = new JPanel(new BorderLayout());
     	
	//NORTH - Put a JLabel in the center of a JPanel's FlowLayout *******
	JPanel north = new JPanel(new FlowLayout());
	
	//String text = "ImageViewComponent";
	JLabel title = new JLabel(" ", JLabel.CENTER);

	north.add(title);
	
	//WEST - Y Tick Marks & Y info ***************************************
	JPanel west = new JPanel(new BorderLayout());
	
        JLabel y_label = new JLabel(" ", JLabel.CENTER );
	y_label.setUI( new VerticalLabelUI(false) );
	
	JLabel y_space = new JLabel(" ", JLabel.CENTER);  // spacer
        y_space.setUI( new VerticalLabelUI(false) );
	  
	JLabel y_ticks = new JLabel(" ", JLabel.CENTER);  // spacer
        y_ticks.setUI( new VerticalLabelUI(false) );  
	
	west.add(y_label, "West");
	west.add(y_space, "Center");
	west.add(y_ticks, "East");
	
        //SOUTH - X Tick Marks & X info **************************************
	JPanel south = new JPanel(new BorderLayout());
	
	JLabel x_ticks = new JLabel(" ", JLabel.CENTER); // spacer
	JLabel x_space = new JLabel(" ", JLabel.CENTER); // spacer
	
	JLabel x_label = new JLabel( " ", JLabel.CENTER );
	
	south.add(x_ticks, "North");
	south.add(x_space, "Center");
	south.add(x_label, "South");
	
	//East - Spacer ******************************************
	JPanel east = new JPanel(new BorderLayout());
	
	JLabel e_space = new JLabel(" ", JLabel.CENTER);  // spacer
        e_space.setUI( new VerticalLabelUI(false) );
	
	JLabel e_space2 = new JLabel(" ", JLabel.CENTER);  // spacer
        e_space2.setUI( new VerticalLabelUI(false) );
	
	JLabel e_space3 = new JLabel(" ", JLabel.CENTER);  // spacer
        e_space3.setUI( new VerticalLabelUI(false) );
	 
	east.add(e_space, "West");
	east.add(e_space2, "Center");
	east.add(e_space3, "East");
      *********************** end of old background *************************/
      
      // this will be the background for the master panel
      JPanel background = new JPanel(new BorderLayout());
      
      JPanel north = new JPanel(new FlowLayout());
      north.setPreferredSize(new Dimension( 0, 25 ) );
      JPanel east = new JPanel(new FlowLayout());
      east.setPreferredSize(new Dimension( 50, 0 ) );
      JPanel south = new JPanel(new FlowLayout());
      south.setPreferredSize(new Dimension( 0, 50 ) );
      JPanel west = new JPanel(new FlowLayout());
      west.setPreferredSize(new Dimension( 70, 0 ) );
      
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
         master.add((JPanel)transparencies.elementAt(trans)); 
      master.add(background);

      big_picture = master;
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
	 Component center;
	 center = e.getComponent();
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
	    System.out.println("Sending SELECTED_CHANGED " + regioninfo );
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
    * MAIN - Basic main program to test an ImageViewComponent object
    */
   public static void main( String args[] ) 
   {
        int col = 250;
	int row = 250;
   
        //Make a sample 2D array
	VirtualArray2D va2D = new VirtualArray2D(row, col); 
        va2D.setAxisInfoVA(AxisInfo2D.XAXIS, .001f, .1f, "TestX","TestUnits", true);
	va2D.setAxisInfoVA(AxisInfo2D.YAXIS, -1f, 0f, "TestY","TestYUnits", true);
	va2D.setTitle("Main Test");
	//Fill the 2D array with the function x*y
	float ftemp;
	for(int i = 0; i < row; i++)
	{
	    for(int j = 0; j < col; j++)
	    {
		ftemp = i*j;
	        va2D.setDataValue(i, j, ftemp); //put float into va2D
	    }
	}

        //Construct an ImageViewComponent with array2D
	ImageViewComponent ivc = new ImageViewComponent(va2D);

        //A tester frame to throw the bottom and top JPanel into *******************************
        JFrame f = new JFrame("ImageViewComponent");  
	f.setBounds(0,0,500,500);
	final Container c = f.getContentPane();
	c.add(ivc.getDisplayPanel());
	  
	f.show(); //display the frame
   }
}
