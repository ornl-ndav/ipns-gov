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
  
  private boolean point1Selected = false;
  private boolean point2Selected = false;
  
  private JRadioButton point1;
  private JRadioButton point2;

  private JPanel Point1Panel;
  private JPanel Point2Panel;
  private JTextField p1xField;
  private JTextField p1yField;
  private JTextField p2xField;
  private JTextField p2yField;
  
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
    buildPoint1Panel();
    buildPoint2Panel();
    DefiningPanel.add(Point1Panel);
    DefiningPanel.add(Point2Panel);
  }
  
  private void buildPoint1Panel()
  {
    Point1Panel = new JPanel(new BorderLayout());
    Point1Panel.setBorder(BorderFactory.createEtchedBorder());
    point1 = new JRadioButton("Point 1");
    point1.addActionListener(new radioButtonListener());
    point1.addKeyListener(new PositionKeyListener());
    radioGroup.add(point1);
    JPanel CPanel = new JPanel();
    CPanel.setLayout(new BoxLayout(CPanel,BoxLayout.Y_AXIS));
    JLabel xLabel = new JLabel("X");
    p1xField = new JTextField(6);
    p1xField.addActionListener(new textFieldListener());
    JLabel yLabel = new JLabel("Y");
    p1yField = new JTextField(6);
    p1yField.addActionListener(new textFieldListener());
    JPanel XPanel = new JPanel();
    XPanel.add(xLabel);
    XPanel.add(p1xField);
    CPanel.add(XPanel);
    
    JPanel YPanel = new JPanel();
    YPanel.add(yLabel);
    YPanel.add(p1yField);
    CPanel.add(YPanel);
    
    Point1Panel.add(point1,BorderLayout.NORTH);
    Point1Panel.add(CPanel, BorderLayout.CENTER);
  }
  
  private void buildPoint2Panel()
  {
    Point2Panel = new JPanel(new BorderLayout());
    Point2Panel.setBorder(BorderFactory.createEtchedBorder());
    point2 = new JRadioButton("Point 2");
    point2.addActionListener(new radioButtonListener());
    point2.addKeyListener(new PositionKeyListener());
    radioGroup.add(point2);
    JPanel CPanel = new JPanel();
    CPanel.setLayout(new BoxLayout(CPanel,BoxLayout.Y_AXIS));
    JLabel xLabel = new JLabel("X");
    p2xField = new JTextField(6);
    p2xField.addActionListener(new textFieldListener());
    JLabel yLabel = new JLabel("Y");
    p2yField = new JTextField(6);
    p2yField.addActionListener(new textFieldListener());
    JPanel XPanel = new JPanel();
    XPanel.add(xLabel);
    XPanel.add(p2xField);
    CPanel.add(XPanel);
    
    JPanel YPanel = new JPanel();
    YPanel.add(yLabel);
    YPanel.add(p2yField);
    CPanel.add(YPanel);
    
    Point2Panel.add(point2,BorderLayout.NORTH);
    Point2Panel.add(CPanel, BorderLayout.CENTER);
  }
  
  private void calculateDimensions()
  {
    float w = Math.abs(p2x-p1x);
    float h = Math.abs(p1y-p2y);
    p1xField.setText("" + p1x);
    p1yField.setText("" + p1y);
    p2xField.setText("" + p2x);
    p2yField.setText("" + p2y);
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
    else if (point1Selected)
    {
      p1y -= VALUE_JUMP/2.0f;
    }
    else if (point2Selected)
    {
      p2y -= VALUE_JUMP/2.0f;
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
    else if (point1Selected)
    {
      p1x -= VALUE_JUMP/2.0f;
    }
    else if (point2Selected)
    {
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
    else if (point1Selected)
    {
      p1x += VALUE_JUMP/2.0f;
    }
    else if (point2Selected)
    {
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
    else if (point1Selected)
    {
      p1y += VALUE_JUMP/2.0f;
    }
    else if (point2Selected)
    {
      p2y += VALUE_JUMP/2.0f;
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
      if ( source.equals(p1xField) )
      {
        //System.out.println("Width changed");
        point1.setSelected(true);
        p1x = Float.parseFloat(numericText);
      }
      
      else if( source.equals(p1yField) )
      {
//      System.out.println("height changed");
        point1.setSelected(true);
        p1y = Float.parseFloat(numericText);
      }
      
      else if ( source.equals(p2xField) )
      {
        //System.out.println("Width changed");
        point2.setSelected(true);
        p2x = Float.parseFloat(numericText);
      }
      
      else if( source.equals(p2yField) )
      {
//      System.out.println("height changed");
        point2.setSelected(true);
        p2y = Float.parseFloat(numericText);
      }
      
      else if ( source.equals(cenX) )
      {
        center.setSelected(true);
        float w = Math.abs(p2x-p1x);
        p1x = Float.parseFloat(numericText) - w/2.0f;
        p2x = Float.parseFloat(numericText) + w/2.0f;
      }
      
      else if ( source.equals(cenY) )
      {
        center.setSelected(true);
        float h = Math.abs(p1y-p2y);
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
        point1Selected = false;
        point2Selected = false;
      }
      else if( message.equals("Point 1"))
      {
        centerSelected = false;
        point1Selected = true;
        point2Selected = false;
      }
      else if( message.equals("Point 2"))
      {
        centerSelected = false;
        point1Selected = false;
        point2Selected = true;
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