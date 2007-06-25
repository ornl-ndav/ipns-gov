package gov.anl.ipns.ViewTools.Components.RegionOpEditFrames;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import gov.anl.ipns.Util.Numeric.floatPoint2D;
import gov.anl.ipns.ViewTools.Components.Cursor.CursorTag;
import gov.anl.ipns.ViewTools.Components.Cursor.WedgeCursor;
import gov.anl.ipns.ViewTools.Components.Region.RegionOp;
import gov.anl.ipns.ViewTools.Components.Region.RegionOp.Operation;


/**
 * This class creates a Double Wedge Region Editor based on three defining 
 * points in the world coordinate system
 * @author Josh Oakgrove
 *
 */
public class DoubleWedgeRegionOpEditFrame extends RegionOpEditFrame
{
private static int VALUE_JUMP = 5;
  
  private boolean radiusSelected = false;
  
  private JRadioButton radius;
  private JPanel RadiusPanel;
  private JPanel AxisAnglePanel;
  private JPanel IncludedAnglePanel;
  private JTextField radiusField;
  private JTextField axisAngleField;
  private JTextField includedAngleField;
  private JSlider axisAngleSlider;
  private JSlider includedAngleSlider;
  
  private float p1x;
  private float p1y;
  private float p2x;
  private float p2y;
  private float p3x;
  private float p3y;
  
  private int regionIndex;

  /**
   * constructor takes in three points:
   * pt1---center point
   * pt2---corner point
   * pt3---rotation point
   * @param pt1 floatPoint2D in world coordinate system -- center point
   * @param pt2 floatPoint2D in world coordinate system -- corner point
   * @param pt3 floatPoint2D in world coordinate system -- rotation point
   * @param op Operation to go with the Region
   * @param index The index of the RegionOp with in the RegionOpList
   */
  public DoubleWedgeRegionOpEditFrame(floatPoint2D[] wedgePoints
      ,RegionOp.Operation op,int index)
  {
    super("Wedge Editor",op);
    super.setBounds(700,390, 450, 280);
    super.setResizable(false);
    super.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    this_editor = this;
    
    setEditorValues(wedgePoints);
    
    center.addActionListener(new radioButtonListener());
    cenY.addActionListener(new textFieldListener());
    cenX.addActionListener(new textFieldListener());
    
    
    regionIndex = index;
  }
  
  private void setEditorValues(floatPoint2D[] wedgePoints)
  {
    //takes the wedge points and sets the defining points along 
    //with the radius, angles, and center point
    
    //set the defining points
    floatPoint2D rotationPt = new floatPoint2D();
    rotationPt.y = wedgePoints[2].y-.5f*(
        wedgePoints[2].y-wedgePoints[1].y);
    rotationPt.x = wedgePoints[2].x-.5f*(
        wedgePoints[2].x-wedgePoints[1].x);
    
    p1x = wedgePoints[0].x;
    p1y = wedgePoints[0].y;
    p2x = wedgePoints[1].x;
    p2y = wedgePoints[1].y;
    p3x = rotationPt.x;
    p3y = rotationPt.y;
    
    //set the center
    cenX.setText(""+p1x);
    cenY.setText(""+p1y);
    
    /*set the angles
    axisAngleSlider.setValue((int)Math.round(wedgePoints[5].x+
        wedgePoints[5].y/2.0));
    includedAngleSlider.setValue((int)wedgePoints[5].y);*/
    
    //set the radius
    //radius = sqrt(Dx^2+Dy^2)
    float Dx = Math.abs(p1x-p2x);
    float Dy = Math.abs(p1y-p2y);
    float rad = (float)Math.sqrt(Dx*Dx+Dy*Dy);
    radiusField.setText(""+rad);
    
//  set the angles
    axisAngleField.setText(""+(int)Math.round(wedgePoints[5].x+
        wedgePoints[5].y/2.0));
    includedAngleField.setText(""+(int)wedgePoints[5].y);
    
    axisAngleSlider.setValue((int)Math.round(wedgePoints[5].x+
        wedgePoints[5].y/2.0));
    includedAngleSlider.setValue((int)wedgePoints[5].y);
  }
  
  private void setDefiningPoints()
  {
    //uses the angles, center point, and radius to get the 
    //three defining points: center, corner and point on axis.
    p1x = Float.parseFloat(cenX.getText());
    p1y = Float.parseFloat(cenY.getText());
    float rad = Float.parseFloat(radiusField.getText());
    float Dx = rad*(float)Math.cos(Math.PI*
                           (float)axisAngleSlider.getValue()/180.0);
    float Dy = rad*(float)Math.sin(Math.PI*
                           (float)axisAngleSlider.getValue()/180.0);
    p3x = p1x + Dx;
    p3y = p1y + Dy;
    
    float Dx2 = rad*(float)Math.cos(Math.PI*
        ((float)includedAngleSlider.getValue()/2.0+
        (float)axisAngleSlider.getValue())/180.0);
            //  --------------rotation alternative-------------------
            //Dx*(float)Math.cos(Math.PI*
                            //((float)includedAngleSlider.getValue())/360.0)-
            //Dy*(float)Math.sin(Math.PI*
                            //((float)includedAngleSlider.getValue())/360.0);
    float Dy2 = rad*(float)Math.sin(Math.PI*
        ((float)includedAngleSlider.getValue()/2.0+
        (float)axisAngleSlider.getValue())/180.0);
            //  --------------rotation alternative-------------------
            //Dx*(float)Math.sin(Math.PI*
                            //((float)includedAngleSlider.getValue())/360.0)+
            //Dy*(float)Math.cos(Math.PI*
                            //((float)includedAngleSlider.getValue())/360.0);
    p2x = p1x + Dx2;
    p2y = p1y + Dy2;
    //System.out.println("center = ("+p1x+","+p1y+")\n"+
                       //"corner = ("+p3x+","+p3y+")\n"+
                       //"rotati = ("+p2x+","+p2y+")");
  }
  public void dispose()
  {
    super.dispose();
    this_editor.firePropertyChange(CANCEL,1,2);
    
  }

  public floatPoint2D[] getDefiningPoints()
  {
    floatPoint2D[] points = new floatPoint2D[3];
    points[0]=new floatPoint2D(p1x, p1y);
    points[1]=new floatPoint2D(p2x, p2y);
    points[2]=new floatPoint2D(p3x, p3y);
    return points;
  }

  public int getRegionIndex()
  {
    return regionIndex;
  }

  public CursorTag getTypeCursor()
  {
    WedgeCursor cursor = null;
    return cursor;
  }
  
  public void Down()
  {
    if(centerSelected)
    {
      p1y -= VALUE_JUMP;
      p2y -= VALUE_JUMP;
      p3y -= VALUE_JUMP;
      cenY.setText(""+p1y);
    }
    else if (radiusSelected)
    {
      radiusField.setText(""+
          (Float.parseFloat(radiusField.getText())-VALUE_JUMP));
      setDefiningPoints();
    }
    firePropertyChange(DRAW_CURSOR, 1, 2);
  }

  public void Left()
  {
    if(centerSelected)
    {
      p1x -= VALUE_JUMP;
      p2x -= VALUE_JUMP;
      p3x -= VALUE_JUMP;
      cenX.setText(""+p1x);
    }
    else if (radiusSelected)
    {
      radiusField.setText(""+
          (Float.parseFloat(radiusField.getText())-VALUE_JUMP));
      setDefiningPoints();
    }
    firePropertyChange(DRAW_CURSOR, 1, 2);
  }

  public void Right()
  {
    if(centerSelected)
    {
      p1x += VALUE_JUMP;
      p2x += VALUE_JUMP;
      p3x += VALUE_JUMP;
      cenX.setText(""+p1x);
    }
    else if (radiusSelected)
    {
      radiusField.setText(""+
          (Float.parseFloat(radiusField.getText())+VALUE_JUMP));
      setDefiningPoints();
    }
    firePropertyChange(DRAW_CURSOR, 1, 2);
  }

  public void Up()
  {
    if(centerSelected)
    {
      p1y += VALUE_JUMP;
      p2y += VALUE_JUMP;
      p3y += VALUE_JUMP;
      cenY.setText(""+p1y);
    }
    else if (radiusSelected)
    {
      radiusField.setText(""+
          (Float.parseFloat(radiusField.getText())+VALUE_JUMP));
      setDefiningPoints();
    }
    firePropertyChange(DRAW_CURSOR, 1, 2);
  }
  
  protected void buildDefiningPanel()
  {
    DefiningPanel = new JPanel();
    DefiningPanel.setLayout(new BoxLayout(DefiningPanel,BoxLayout.Y_AXIS));
    buildRadiusPanel();
    buildAxisAnglePanel();
    buildIncludedAnglePanel();
    DefiningPanel.add(RadiusPanel);
    DefiningPanel.add(AxisAnglePanel);
    DefiningPanel.add(IncludedAnglePanel);
  }
  
  private void buildRadiusPanel()
  {
    radiusField = new JTextField(6);
    RadiusPanel = new JPanel();
    RadiusPanel.setBorder(BorderFactory.createEtchedBorder());
    radius = new JRadioButton("Radius");
    radius.addActionListener(new radioButtonListener());
    radius.addKeyListener(new PositionKeyListener());
    radioGroup.add(radius);
    radiusField.addActionListener(new textFieldListener());
    
    RadiusPanel.add(radius);
    RadiusPanel.add(radiusField);
  }
  
  private void buildAxisAnglePanel()
  {
    axisAngleSlider = new JSlider(-180,180);
    axisAngleSlider.setMajorTickSpacing(20);
    axisAngleSlider.setPaintTicks(true);
    axisAngleSlider.setValueIsAdjusting(false);
    AxisAnglePanel = new JPanel(new BorderLayout());
    AxisAnglePanel.setBorder(BorderFactory.createEtchedBorder());
    
    JPanel NPanel = new JPanel();
    JLabel axisAngleLabel = new JLabel("Axis Angle");
    axisAngleField = new JTextField(4);
    axisAngleField.addActionListener(new textFieldListener());
    NPanel.add(axisAngleLabel);
    NPanel.add(axisAngleField);
    axisAngleSlider.addChangeListener(new sliderListener());
    //add to listeners

    AxisAnglePanel.add(NPanel,BorderLayout.NORTH);
    AxisAnglePanel.add(axisAngleSlider,BorderLayout.CENTER);
  }
  
  private void buildIncludedAnglePanel()
  {
    includedAngleSlider = new JSlider(0,180);
    includedAngleSlider.setMajorTickSpacing(20);
    includedAngleSlider.setPaintTicks(true);
    includedAngleSlider.setValueIsAdjusting(false);
    IncludedAnglePanel = new JPanel(new BorderLayout());
    IncludedAnglePanel.setBorder(BorderFactory.createEtchedBorder());
    
    JPanel NPanel = new JPanel();
    includedAngleSlider.addChangeListener(new sliderListener());
    JLabel includedAngleLabel = new JLabel("Included Angle");
    includedAngleField = new JTextField(4);
    includedAngleField.addActionListener(new textFieldListener());
    NPanel.add(includedAngleLabel);
    NPanel.add(includedAngleField);
    
    IncludedAnglePanel.add(NPanel,BorderLayout.NORTH);
    IncludedAnglePanel.add(includedAngleSlider,BorderLayout.CENTER);
  }
  
  private class textFieldListener implements ActionListener
  {
    public void actionPerformed(ActionEvent e)
    {
      Object source = e.getSource();
      //System.out.println("you changed the text.");
      String text = ((JTextField)source).getText();
      String numericText = "";
      boolean isDecimal = false;
      boolean isNegative = false;
      for( int i=0; i<text.length(); i++ )
      {
        if(i==0 &&text.charAt(0)=='-')
          isNegative=true;
        if( Character.isDigit(text.charAt(i)) )
          numericText += text.charAt(i);
        if( text.charAt(i) == '.' && !isDecimal )
        {
          numericText += text.charAt(i);
          isDecimal = true;
        }
      }
      
      int degrees = (int)Math.round(Float.parseFloat(numericText));
      while (degrees>360)
        degrees -= 360;
      
      if ( source.equals(radiusField) )
      {
        //System.out.println("radius changed");
        radiusField.setText(numericText);
        setDefiningPoints();
        
      }
      
      else if (source.equals(cenX))
      {
        if(isNegative)
          cenX.setText("-"+numericText);
        else
          cenX.setText(numericText);
        center.setSelected(true);
        float Dx = Float.parseFloat(cenX.getText()) - p1x;
        p1x += Dx;
        p2x += Dx;
        p3x += Dx;
      }
      
      else if (source.equals(cenY))
      {
        if(isNegative)
          cenY.setText("-"+numericText);
        else
          cenY.setText(numericText);
        center.setSelected(true);
        float Dy = Float.parseFloat(cenY.getText()) - p1y;
        p1y += Dy;
        p2y += Dy;
        p3y += Dy;
      }
      
      else if (source.equals(axisAngleField))
      {
        if(degrees>180)
          degrees -= 360;
        if(isNegative)
        {
          axisAngleField.setText(""+(-degrees));
          axisAngleSlider.setValue(-degrees);
        }
        else
        {
          axisAngleField.setText(""+degrees);
          axisAngleSlider.setValue(degrees);
        }
      }
      
      else if (source.equals(includedAngleField))
      {
        includedAngleField.setText(""+degrees);
        includedAngleSlider.setValue(degrees);
      }
      
      this_editor.firePropertyChange(DRAW_CURSOR,1,2);
    }
  }
  
  private class radioButtonListener implements ActionListener
  {
    public void actionPerformed(ActionEvent e)
    {
      String message = e.getActionCommand();
      if (message.equals("Center"))
      {
        centerSelected = true;
        radiusSelected = false;
      }
      else if( message.equals("Radius"))
      {
        centerSelected = false;
        radiusSelected = true;
      }
    }
  }
  
  private class sliderListener implements ChangeListener
  {

    public void stateChanged(ChangeEvent e)
    {
      if(e.getSource().equals(axisAngleSlider))
      {
        //System.out.println("axisAngleSlider changed");
        axisAngleField.setText(""+axisAngleSlider.getValue());
      }
      
      if(e.getSource().equals(includedAngleSlider))
      {
        //System.out.println("includedAngleSlider changed");
        includedAngleField.setText(""+includedAngleSlider.getValue());
      }
      setDefiningPoints();
      firePropertyChange(DRAW_CURSOR,1,2);
    }
    
  }
  
  /*public static void main(String[]args)
  {
    WedgeRegionOpEditFrame2 test = new WedgeRegionOpEditFrame2();
    test.setVisible(true);
  }*/
  
}