
 
package DataSetTools.components.View.OneD;

import javax.swing.*; 
import javax.swing.event.*;
import java.io.Serializable;
import java.awt.*;
import java.lang.*;
import java.lang.Object.*;
import java.awt.event.*;
import java.awt.Rectangle.*;
import java.util.*; 
import java.awt.font.FontRenderContext;

import DataSetTools.util.*;  //floatPoint2D
import DataSetTools.math.*;
import DataSetTools.components.image.*;//GraphJPanel & ImageJPanel & CoordJPanel
import DataSetTools.components.View.Transparency.*; //Axis Overlays
import DataSetTools.components.View.*;  // IVirtualArray1D
import DataSetTools.components.View.ViewControls.*;
import DataSetTools.dataset.*;


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
public class FunctionViewComponent implements IFunctionComponent1D, 
                                           ActionListener,
                  DataSetTools.components.View.TwoD.IAxisAddible2D
{
   private IVirtualArray1D Varray1D;  //An object containing our array of data
   private Point[] selectedset; //To be returned by getSelectedSet()   
   private Vector Listeners = null;   
   private JPanel big_picture = new JPanel();    
   private GraphJPanel gjp;
   // for component size and location adjustments
   //private ComponentAltered comp_listener;
   private Rectangle regioninfo;
   private Vector transparencies = new Vector();
   private int precision;
   private Font font;
   private LinkedList controls = new LinkedList();
 
   private JPanel panel1 = new JPanel();
   private JPanel panel2 = new JPanel();
   private JPanel panel3 = new JPanel();

   private JLabel label1 = new JLabel("Line Selected");
   private JLabel label2 = new JLabel("Line Style");
   private JLabel label3 = new JLabel("Line Width");

   private JComboBox LineBox;
   private JComboBox LineStyleBox;
   private JComboBox LineWidthBox;

   private String[] lines;
   private String[] line_type;
   private String[] line_width;

   private JButton lineColor;
   private int line_index = 0;
   private float linewidth = 1;	
   private JColorChooser colors = new JColorChooser(Color.black);


  /**
   * Constructor that takes in a virtual array and creates an graphjpanel
   * to be viewed in a border layout.
   */
   public FunctionViewComponent( IVirtualArray1D varr )  
   {
     lines = new String[varr.getNumlines()];
    for (int i = 0; i < varr.getNumlines(); i++)
    {	
       lines[i] = "Line_"+(i+1);
    } 
    LineBox = new JComboBox(lines);

    line_type = new String[5];
    line_type[0] = "Solid"; 
    line_type[1] = "Dashed";     
    line_type[2] = "Dotted"; 
    line_type[3] = "Dash Dot Dot"; 
    line_type[4] = "Transparent";
    LineStyleBox = new JComboBox(line_type);

    line_width = new String[5];
    line_width[0] = "1";
    line_width[1] = "2";
    line_width[2] = "3";
    line_width[3] = "4";
    line_width[4] = "5";

    LineWidthBox = new JComboBox(line_width);


    lineColor = new JButton("Line Color");

    BoxLayout theBox1 = new BoxLayout(panel1, BoxLayout.X_AXIS);
    BoxLayout theBox2 = new BoxLayout(panel2, BoxLayout.X_AXIS);
    BoxLayout theBox3 = new BoxLayout(panel3, BoxLayout.X_AXIS);

    panel1.add(label1);
    panel1.add(LineBox);
    panel2.add(label2);
    panel2.add(LineStyleBox);
    panel3.add(label3);
    panel3.add(LineWidthBox);
    
      Varray1D = varr; // Get reference to varr
      precision = 4;
      font = FontUtil.LABEL_FONT2;
      gjp = new GraphJPanel();
      //Make gjp correspond to the data in f_array
      int num_lines = varr.getNumlines();
      boolean bool = false;
      for (int i = 0; i < num_lines; i++)
      {
		
	gjp.setData(varr.getXValues(i),
		    varr.getYValues(i),i, bool); 
	if(i >= num_lines -2)
	    bool = true;

      }
    gjp.setBackground(Color.white);
    gjp.setColor( Color.black, 0, false );
    gjp.setStroke( gjp.strokeType(gjp.LINE,linewidth), 0, false);
    gjp.setMarkType(Color.blue, gjp.BOX, 0, false);

    
    gjp.setColor( Color.red, 1, true );
    gjp.setStroke( gjp.strokeType(gjp.LINE,linewidth), 1, true);

    
    gjp.setColor( Color.green, 2, false );
    gjp.setStroke( gjp.strokeType(gjp.LINE,linewidth), 2, true);
    gjp.setMarkType(Color.red, gjp.CROSS, 2, true);


      ImageListener gjp_listener = new ImageListener();
      gjp.addActionListener( gjp_listener );
                  
      ComponentAltered comp_listener = new ComponentAltered();   
      gjp.addComponentListener( comp_listener );
      
      AxisInfo2D xinfo = varr.getAxisInfo(AxisInfo2D.XAXIS);
      AxisInfo2D yinfo = varr.getAxisInfo(AxisInfo2D.YAXIS);
      
      gjp.initializeWorldCoords( new CoordBounds( xinfo.getMin(),
                                                  yinfo.getMax(),      
                                                  xinfo.getMax(),
						  yinfo.getMin() ) ); 
      Listeners = new Vector();
      buildViewComponent(gjp); // initializes big_picture to jpanel containing
                               // the background and transparencies 		       
      buildViewControls(gjp);   
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
         return new AxisInfo2D( gjp.getLocalWorldCoords().getX1(),
	               gjp.getLocalWorldCoords().getX2(),
		       Varray1D.getAxisInfo(AxisInfo2D.XAXIS).getLabel(),
		       Varray1D.getAxisInfo(AxisInfo2D.XAXIS).getUnits(),
		       Varray1D.getAxisInfo(AxisInfo2D.XAXIS).getIsLinear() );
      // if false return y info
      return new AxisInfo2D( gjp.getLocalWorldCoords().getY1(),
	               gjp.getLocalWorldCoords().getY2(),
		       Varray1D.getAxisInfo(AxisInfo2D.YAXIS).getLabel(),
		       Varray1D.getAxisInfo(AxisInfo2D.YAXIS).getUnits(),
		       Varray1D.getAxisInfo(AxisInfo2D.YAXIS).getIsLinear() );
   }
   
  /**
   * This method returns a rectangle containing the location and size
   * of the grapgjpanel.
   *
   *  @return The region info about the graphjpanel
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
      return Varray1D.getTitle();
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
    
  /**
   * This method will return the local coordinate bounds of the center
   * jpanel. To be implemented, the center may have to be a coordjpanel.
   */
   public CoordBounds getLocalCoordBounds()
   {
      //return local_bounds;
      return gjp.getLocalWorldCoords().MakeCopy();
   }

  /**
   * This method will return the global coordinate bounds of the center
   * jpanel. To be implemented, the center may have to be a coordjpanel.
   */
   public CoordBounds getGlobalCoordBounds()
   {
      //return global_bounds;
      return gjp.getGlobalWorldCoords().MakeCopy();
   }


 
  /**
   * This method adjusts the crosshairs on the graphjpanel.
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
      
      //set the cursor position on GraphJPanel
      gjp.setCurrent_WC_point( fpt ); 
      
      System.out.println("");
   }
  
  /**
   * This method creates a selected region to be displayed over the graphjpanel
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
//      float[][][] f_array = Varray1D.getRegionValues( 0, 1000000, 0, 1000000 );
//System.out.println("f_array:" + f_array);
//     gjp.setData(f_array, true);
   }
  
  /**
   * To be continued...
   */ 
   public void dataChanged( IVirtualArray1D pin_varray ) // pin == "passed in"
   {
      System.out.println("Now in void dataChanged(VirtualArray1D pin_varray)");

      //get the complete 2D array of floats from pin_varray
//      float[][][] f_array = Varray1D.getRegionValues( 0, 1000000, 0, 1000000 );
//System.out.println("f_array:" + f_array);
//      gjp.setData(f_array, true);  
      
//      System.out.println("Value of first element: " + f_array[0][0] );
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

           JComponent[] Res = new JComponent[4];
	   Res[0] = (JComponent) panel1;
           LineBox.addActionListener(new ControlListener());

	   Res[1] = (JComponent)panel2;	
	   LineStyleBox.addActionListener(new ControlListener());

	   Res[2] = (JComponent)panel3;
	   LineWidthBox.addActionListener(new ControlListener());


	   Res[3] = (JComponent)lineColor;
	   lineColor.addActionListener(new ControlListener());

           return Res;

     // return new JComponent[0];
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
   *  @return JPanel containing graphjpanel in the center of a borderlayout.  
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
     fpt = gjp.getCurrent_WC_point();
     
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
   private void buildViewComponent( GraphJPanel panel )
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
      // add background and transparency to the master layout
      
      JPanel master = new JPanel();
      OverlayLayout overlay = new OverlayLayout(master);
      master.setLayout(overlay);
      for( int trans = 0; trans < transparencies.size(); trans++ )
         master.add((OverlayJPanel)transparencies.elementAt(trans)); 
      master.add(background);
 
      big_picture = master;
   }
   
  /*
   * This method constructs the controls required by the FunctionViewComponent
   */
   private void buildViewControls( GraphJPanel gJpanel )
   {

   }
   
  //***************************Assistance Classes******************************
  /*
   * ComponentAltered monitors if the graphjpanel has been resized. If so,
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
   * ImageListener monitors if the graphjpanel has sent any messages.
   * If so, process the message and relay it to the viewer.
   */
   private class ImageListener implements ActionListener
   {
      public void actionPerformed( ActionEvent ae )
      {
         String message = ae.getActionCommand();     
         //System.out.println("Graph sent message " + message );

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
   
   private class ControlListener implements ActionListener
   {  
      public void actionPerformed( ActionEvent ae )
      {
	String message = ae.getActionCommand();
	//System.out.println("Graph sent message " + message );
        
	if(message.equals("Line Color"))
	{
        
	   //colors.setPreviewPanel(new JPanel());
           Color c = colors.showDialog(null, "color chart", Color.black);
           if (c != null)
	      gjp.setColor(c,line_index, true);
	

	
	}
	else if(message.equals("comboBoxChanged"))
	{
	   // System.out.println("action" + LineBox.getSelectedItem());
	   // System.out.println("action" + LineBox.getSelectedIndex());
           if (ae.getSource() == LineBox)
	      line_index = LineBox.getSelectedIndex();

	   else if (ae.getSource() == LineStyleBox)
	   {
	      if (LineStyleBox.getSelectedItem().equals("Solid"))
	      {
		gjp.setStroke( gjp.strokeType(gjp.LINE,linewidth), 
				line_index, true);
	      }

	      if (LineStyleBox.getSelectedItem().equals("Dashed"))
	      {
		gjp.setStroke( gjp.strokeType(gjp.DASHED,linewidth), 
				line_index, true);
	      }

	      if (LineStyleBox.getSelectedItem().equals("Dotted"))
	      {System.out.println("in dotted if");
		gjp.setStroke( gjp.strokeType(gjp.DOTTED,linewidth), 
				line_index, true);
	      }
	      if (LineStyleBox.getSelectedItem().equals("Dash Dot Dot"))
	      {
		gjp.setStroke( gjp.strokeType(gjp.DASHDOT,linewidth), 
				line_index, true);
	      }
	      if (LineStyleBox.getSelectedItem().equals("Transparent"))
	      {System.out.println("in here");
  		gjp.setStroke(gjp.strokeType(gjp.DASHED,0),
                              line_index, true);
	      }   
	   }
           else if (ae.getSource() == LineWidthBox)
           {
               linewidth = LineWidthBox.getSelectedIndex() + 1;
               
	      if (LineStyleBox.getSelectedItem().equals("Solid"))
	      {
		gjp.setStroke( gjp.strokeType(gjp.LINE,linewidth), 
				line_index, true);
	      }

	      if (LineStyleBox.getSelectedItem().equals("Dashed"))
	      {
		gjp.setStroke( gjp.strokeType(gjp.DASHED,linewidth), 
				line_index, true);
	      }

	      if (LineStyleBox.getSelectedItem().equals("Dotted"))
	      {System.out.println("in dotted if");
		gjp.setStroke( gjp.strokeType(gjp.DOTTED,linewidth), 
				line_index, true);
	      }
	      if (LineStyleBox.getSelectedItem().equals("Dash Dot Dot"))
	      {
		gjp.setStroke( gjp.strokeType(gjp.DASHDOT,linewidth), 
				line_index, true);
	      }
              if (LineStyleBox.getSelectedItem().equals("Transparent"))
	      {
  		gjp.setStroke(gjp.strokeType(gjp.DASHED,0),
                              line_index, true);
	      }
           }
	}

      }
   }
	  


 
      
  /*
   * MAIN - Basic main program to test a FunctionViewComponent object
   */
   public static void main( String args[] ) 
   {
  /*      float g1_x_vals[] = { 0, (float).02, (float).04, (float).1 };
          float g1_y_vals[] = { 0, (float)-.3, (float)-.2, -1 };

          float g2_x_vals[] = { 0, (float).1 };
          float g2_y_vals[] = { -1, 0 };

          float g3_x_vals[] = { 0, (float).05, (float).06, (float).1};
          float g3_y_vals[] = { (float)-.1, (float)-.2, (float)-.7, (float)-.6 };

	
        //Make a sample 2D array
	VirtualArray1D va1D = new VirtualArray1D(5,3); 
        va1D.setAxisInfoVA( AxisInfo2D.XAXIS, .001f, .1f, 
                           "TestX","TestUnits", true );
	va1D.setAxisInfoVA( AxisInfo2D.YAXIS, 0f, -1f, 
                            "TestY","TestYUnits", true );
	va1D.setTitle("Main Test");
	//Fill the 3D array with the function 
	va1D.setXValues(g1_x_vals, 0, 0);
	va1D.setXValues(g2_x_vals, 1, 0);
	va1D.setXValues(g3_x_vals, 2, 0);
	va1D.setYValues(g1_y_vals, 0, 0);
	va1D.setYValues(g2_y_vals, 1, 0);
	va1D.setYValues(g3_y_vals, 2, 0);

        //Construct a FunctionViewComponent with array2D
	FunctionViewComponent fvc = new FunctionViewComponent(va1D);
   
  */
       if( args == null)
          System.exit(0);
       DataSet[] DSS = (new IsawGUI.Util()).loadRunfile( args[0]);

       int k = DSS.length-1;
       DSS[k].setSelectFlag(0,true);
       DSS[k].setSelectFlag(3, true);
        DataSetData ArrayHandler = new DataSetData( DSS[k]);

       AxisInfo2D  xaxis= ArrayHandler.getAxisInfo(true);
       AxisInfo2D  yaxis= ArrayHandler.getAxisInfo(false);
       System.out.println("ArrayHandler info"+    xaxis.getMax()+","+
         xaxis.getMin()+","+yaxis.getMax()+","+yaxis.getMin());
       if( java.lang.Float.isNaN(xaxis.getMax()))
         try{
            int c = System.in.read();
            }
         catch( Exception sss){}


      FunctionViewComponent fvc = new FunctionViewComponent(ArrayHandler);

        //A tester frame to throw the bottom and top JPanel into **********
        JFrame f = new JFrame("ISAW FunctionViewComponent");  
	f.setBounds(0,0,500,500);
	Container c = f.getContentPane();
	c.add(fvc.getDisplayPanel());
	  
	f.show(); //display the frame
	
	JFrame f2 = new JFrame("ISAW GraphViewControls");
	Container cpain = f2.getContentPane();
	cpain.setLayout( new BoxLayout(cpain,BoxLayout.Y_AXIS));  
	
	JComponent[] controls = fvc.getSharedControls();
	for( int i = 0; i < controls.length; i++ )
	   cpain.add(controls[i]);
	f2.setBounds(0,0,200,(100 * controls.length));
	cpain.validate();  
	f2.show(); //display the frame
   }
}

