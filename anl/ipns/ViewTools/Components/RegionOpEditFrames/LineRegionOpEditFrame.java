package gov.anl.ipns.ViewTools.Components.RegionOpEditFrames;

import java.awt.*;
import java.awt.event.*;

import gov.anl.ipns.Util.Numeric.floatPoint2D;
import gov.anl.ipns.ViewTools.Components.Cursor.CursorTag;
import gov.anl.ipns.ViewTools.Components.Cursor.LineCursor;
import gov.anl.ipns.ViewTools.Components.Region.RegionOp;

import javax.swing.*;

/**
 * This class creates a Line Region Editor based on two defining points
 * in the world coordinate system
 * @author Josh Oakgrove
 *
 */
public class LineRegionOpEditFrame extends RegionOpEditFrame
{

private static int VALUE_JUMP = 5;
  
  private boolean dimensionsSelected = false;
  
  private JRadioButton dimensions;

  private JPanel DimensionPanel;
  private JTextField widthField;
  private JTextField heightField;
  
  private float p1x;
  private float p1y;
  private float p2x;
  private float p2y;
  
  private int regionIndex;
  
  /**
   * constructor takes two points:
   * pt1---first end point
   * pt2---second end point
   * @param pt1 floatPoint2D in world coordinate system--first end point
   * @param pt2 floatPoint2D in world coordinate system--second end point
   * @param op Operation to go with the Region
   * @param index The index of the RegionOp with in the RegionOpList
   */
  public LineRegionOpEditFrame( floatPoint2D pt1, floatPoint2D pt2,
                               RegionOp.Operation op,int index )
  {
    super("Line Editor",op);
    super.setBounds(700,390, 400, 290);
    super.setResizable(false);
    super.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    this_editor = this;
    
    center.addActionListener(new radioButtonListener());
    cenY.addActionListener(new textFieldListener());
    cenX.addActionListener(new textFieldListener());
    p1x = pt1.x;
    p1y = pt1.y;
    p2x = pt2.x;
    p2y = pt2.y;

    calculateDimensions();
    regionIndex = index;
    
//  set approprate values for draw
    //send Draw message
  }
  
  public int getRegionIndex()
  {
    return regionIndex;
  }
  
  public floatPoint2D[] getDefiningPoints()
  {
    floatPoint2D[] points = new floatPoint2D[2];
    points[0]=new floatPoint2D(p1x, p1y);
    points[1]=new floatPoint2D(p2x, p2y);
    return points;
  }
  
  public CursorTag getTypeCursor()
  {
    LineCursor cursor = null;
    return cursor;
  }
  
  protected void buildDefiningPanel()
  {
    DefiningPanel = new JPanel();
    DefiningPanel.setLayout(new BoxLayout(DefiningPanel,BoxLayout.Y_AXIS));
    buildDimensionPanel();
    DefiningPanel.add(DimensionPanel);
  }
  
  private void buildDimensionPanel()
  {
    DimensionPanel = new JPanel(new BorderLayout());
    DimensionPanel.setBorder(BorderFactory.createEtchedBorder());
    dimensions = new JRadioButton("Dimensions");
    dimensions.addActionListener(new radioButtonListener());
    dimensions.addKeyListener(new PositionKeyListener());
    radioGroup.add(dimensions);
    JPanel CPanel = new JPanel();
    CPanel.setLayout(new BoxLayout(CPanel,BoxLayout.Y_AXIS));
    JLabel xLabel = new JLabel("Width");
    widthField = new JTextField(6);
    widthField.addActionListener(new textFieldListener());
    JLabel yLabel = new JLabel("Height");
    heightField = new JTextField(6);
    heightField.addActionListener(new textFieldListener());
    JPanel XPanel = new JPanel();
    XPanel.add(xLabel);
    XPanel.add(widthField);
    CPanel.add(XPanel);
    
    JPanel YPanel = new JPanel();
    YPanel.add(yLabel);
    YPanel.add(heightField);
    CPanel.add(YPanel);
    
    DimensionPanel.add(dimensions,BorderLayout.NORTH);
    DimensionPanel.add(CPanel, BorderLayout.CENTER);
  }
  
  private void calculateDimensions()
  {
    float w = Math.abs(p2x-p1x);
    float h = Math.abs(p1y-p2y);
    widthField.setText("" + w);
    heightField.setText("" + h);
    if(p1y>p2y)
      cenY.setText(""+(p2y+h/2.0f));
    else
      cenY.setText(""+(p1y+h/2.0f));
    if(p1x < p2x)
      cenX.setText(""+(p1x+w/2.0f));
    else
      cenX.setText(""+(p2x+w/2.0f));
    //System.out.println("Point1 = ("+p1x+","+p1y+")\n"+
    //    "Point2 = ("+p2x+","+p2y+")");
  }
  
  public void dispose()
  {
    super.dispose();
    this_editor.firePropertyChange(CANCEL,1,2);
  }
  
  public void Down()
  {
    if(centerSelected)
    {
      p1y -= VALUE_JUMP;
      p2y -= VALUE_JUMP;
    }
    else if (dimensionsSelected)
    {
      p1y -= VALUE_JUMP/2.0f;
      p2y += VALUE_JUMP/2.0f;
    }
    calculateDimensions();
  }

  public void Left()
  {
    if(centerSelected)
    {
      p1x -= VALUE_JUMP;
      p2x -= VALUE_JUMP;
    }
    else if (dimensionsSelected)
    {
      p1x += VALUE_JUMP/2.0f;
      p2x -= VALUE_JUMP/2.0f;
    }
    calculateDimensions();
  }

  public void Right()
  {
    if(centerSelected)
    {
      p1x += VALUE_JUMP;
      p2x += VALUE_JUMP;
    }
    else if (dimensionsSelected)
    {
      p1x -= VALUE_JUMP/2.0f;
      p2x += VALUE_JUMP/2.0f;
    }
    calculateDimensions();
  }

  public void Up()
  {
    if(centerSelected)
    {
      p1y += VALUE_JUMP;
      p2y += VALUE_JUMP;
    }
    else if (dimensionsSelected)
    {
      p1y += VALUE_JUMP/2.0f;
      p2y -= VALUE_JUMP/2.0f;
    }
    calculateDimensions();
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
      for( int i=0; i<text.length(); i++ )
      {
        if( Character.isDigit(text.charAt(i)) )
          numericText += text.charAt(i);
        if( text.charAt(i) == '.' && !isDecimal )
        {
          numericText += text.charAt(i);
          isDecimal = true;
        }
      }
      if ( source.equals(widthField) )
      {
        //System.out.println("Width changed");
        dimensions.setSelected(true);
        p1x = Float.parseFloat(cenX.getText()) -
                               Float.parseFloat(numericText)/2.0f;
        p2x = p1x + Float.parseFloat(numericText);
      }
      
      else if(source.equals(heightField))
      {
//      System.out.println("height changed");
        dimensions.setSelected(true);
        p1y = Float.parseFloat(cenY.getText()) +
                               Float.parseFloat(numericText)/2.0f;
        p2y = p1y - Float.parseFloat(numericText);
      }
      
      else if (source.equals(cenX))
      {
        center.setSelected(true);
        float w = Float.parseFloat(widthField.getText());
        p1x = Float.parseFloat(numericText) - w/2.0f;
        p2x = Float.parseFloat(numericText) + w/2.0f;
      }
      
      else if (source.equals(cenY))
      {
        center.setSelected(true);
        float h = Float.parseFloat(heightField.getText());
        p1y = Float.parseFloat(numericText) + h/2.0f;
        p2y = Float.parseFloat(numericText) - h/2.0f;
      }
      
      calculateDimensions();
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
        dimensionsSelected = false;
      }
      else if( message.equals("Dimensions"))
      {
        centerSelected = false;
        dimensionsSelected = true;
      }
    }
  }
  
  /*public static void main(String[]args)
  {
    LineRegionOpEditFrame2 test = new LineRegionOpEditFrame2
                (new floatPoint2D(40,60),new floatPoint2D(60,40));
    test.setVisible(true);
  }*/
}