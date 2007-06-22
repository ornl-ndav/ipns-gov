package gov.anl.ipns.ViewTools.Components.RegionOpEditFrames;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.*;

import gov.anl.ipns.Util.Numeric.floatPoint2D;
import gov.anl.ipns.ViewTools.Components.Cursor.AnnularCursor;
import gov.anl.ipns.ViewTools.Components.Cursor.CursorTag;
import gov.anl.ipns.ViewTools.Components.Cursor.WedgeCursor;
import gov.anl.ipns.ViewTools.Components.Region.RegionOp;
import gov.anl.ipns.ViewTools.Components.Region.RegionOp.Operation;

/**
 * This class creates a Annular Region Editor based on three defining points
 * in the world coordinate system
 * @author Josh Oakgrove
 *
 */

public class AnnularRegionOpEditFrame extends RegionOpEditFrame
{
private static int VALUE_JUMP = 5;
  
  private boolean innerRadiusSelected = false;
  private boolean outerRadiusSelected = false;
  
  private JRadioButton radius1;
  private JRadioButton radius2;
  
  private JPanel Radius1Panel;
  private JPanel Radius2Panel;
  
  private JTextField rad1Field;
  private JTextField rad2Field;
    
  private float p1x;
  private float p1y;
  private float p2x;
  private float p2y;
  private float p3x;
  private float p3y;
  
  private int regionIndex;

  /**
   * constructor takes in three points:
   * pt1---center of the ring
   * pt2---point on inner radius
   * pt3---point on outer radius
   * @param pt1 World coordinate floatPoint2D -- center
   * @param pt2 World coordinate floatPoint2D -- point on inner radius
   * @param pt3 World coordinate floatPoint2D -- point on outer radius
   * @param op Operation to go with the Region
   * @param index The index of the RegionOp with in the RegionOpList
   */
  public AnnularRegionOpEditFrame(floatPoint2D pt1, floatPoint2D pt2,
      floatPoint2D pt3,RegionOp.Operation op,int index)
  {
    super("Ring Editor",op);
    super.setBounds(700,390, 350, 277);
    super.setResizable(false);
    super.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    this_editor = this;
    
    p1x=pt1.x;
    p1y=pt1.y;
    p2x=pt2.x;
    p2y=pt2.y;
    p3x=pt3.x;
    p3y=pt3.y;
    
    center.addActionListener(new radioButtonListener());
    cenX.addActionListener(new textFieldListener());
    cenY.addActionListener(new textFieldListener());
    
    setEditorValues();
    regionIndex = index;
  }
  
  private void setEditorValues()
  {
    //center
    cenX.setText(""+p1x);
    cenY.setText(""+p1y);
    
    //set radius 1
    float xVal = p2x - p1x;
    float yVal = p2y - p1y;
    rad1Field.setText(""+(float)Math.sqrt(xVal*xVal + yVal*yVal));
    
    //set radius 2
    xVal = p3x - p1x;
    yVal = p3y - p1y;
    rad2Field.setText(""+(float)Math.sqrt(xVal*xVal + yVal*yVal));
  }
  
  private void setDefiningPoints()
  {
    //center point
    p1y = Float.parseFloat(cenY.getText());
    p1x = Float.parseFloat(cenX.getText());
    
    //point on inner circle
    p2y = p1y;
    p2x = p1x + Float.parseFloat(rad1Field.getText());
    
    //point on outer circle
    p3y = p1y;
    p3x = p1x + Float.parseFloat(rad2Field.getText());
  }
  
  public void dispose()
  {
    super.dispose();
    this_editor.firePropertyChange(CANCEL,1,2);
    
  }

  public floatPoint2D[] getDefiningPoints()
  {
    floatPoint2D[] points = new floatPoint2D[3];
    points[0]=new floatPoint2D(p1x,
                               p1y);
    points[1]=new floatPoint2D(p2x,
                               p2y);
    points[2]=new floatPoint2D(p3x,
                               p3y);
    return points;
  }

  public int getRegionIndex()
  {
    return regionIndex;
  }

  public CursorTag getTypeCursor()
  {
    AnnularCursor cursor = null;
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
    else if (innerRadiusSelected)
    {
      rad1Field.setText(""+
          (Float.parseFloat(rad1Field.getText())-VALUE_JUMP));
      setDefiningPoints();
    }
    else if (outerRadiusSelected)
    {
      rad2Field.setText(""+
          (Float.parseFloat(rad2Field.getText())-VALUE_JUMP));
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
    else if (innerRadiusSelected)
    {
      rad1Field.setText(""+
          (Float.parseFloat(rad1Field.getText())-VALUE_JUMP));
      setDefiningPoints();
    }
    else if (outerRadiusSelected)
    {
      rad2Field.setText(""+
          (Float.parseFloat(rad2Field.getText())-VALUE_JUMP));
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
    else if (innerRadiusSelected)
    {
      rad1Field.setText(""+
          (Float.parseFloat(rad1Field.getText())+VALUE_JUMP));
      setDefiningPoints();
    }
    else if (outerRadiusSelected)
    {
      rad2Field.setText(""+
          (Float.parseFloat(rad2Field.getText())+VALUE_JUMP));
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
    else if (innerRadiusSelected)
    {
      rad1Field.setText(""+
          (Float.parseFloat(rad1Field.getText())+VALUE_JUMP));
      setDefiningPoints();
    }
    else if (outerRadiusSelected)
    {
      rad2Field.setText(""+
          (Float.parseFloat(rad2Field.getText())+VALUE_JUMP));
      setDefiningPoints();
    }
    firePropertyChange(DRAW_CURSOR, 1, 2);
  }
  
  protected void buildDefiningPanel()
  {
    DefiningPanel = new JPanel();
    DefiningPanel.setLayout(new BoxLayout(DefiningPanel,BoxLayout.Y_AXIS));
    buildRadius1Panel();
    buildRadius2Panel();
    DefiningPanel.add(Radius1Panel);
    DefiningPanel.add(Radius2Panel);
    
  }
  
  private void buildRadius1Panel()
  {
    Radius1Panel = new JPanel(new BorderLayout());
    Radius1Panel.setBorder(BorderFactory.createEtchedBorder());
    radius1 = new JRadioButton("Inner Radius");
    radius1.addActionListener(new radioButtonListener());
    radius1.addKeyListener(new PositionKeyListener());
    radioGroup.add(radius1);
    JPanel CPanel = new JPanel();
    JLabel radLabel = new JLabel("Length");
    rad1Field = new JTextField(6);
    rad1Field.addActionListener(new textFieldListener());
    CPanel.add(radLabel);
    CPanel.add(rad1Field);
    
    Radius1Panel.add(radius1,BorderLayout.NORTH);
    Radius1Panel.add(CPanel, BorderLayout.CENTER);
  }
  
  private void buildRadius2Panel()
  {
    Radius2Panel = new JPanel(new BorderLayout());
    Radius2Panel.setBorder(BorderFactory.createEtchedBorder());
    radius2 = new JRadioButton("Outer Radius");
    radius2.addKeyListener(new PositionKeyListener());
    radius2.addActionListener(new radioButtonListener());
    radioGroup.add(radius2);
    JPanel CPanel = new JPanel();
    JLabel radLabel = new JLabel("Length");
    rad2Field = new JTextField(6);
    rad2Field.addActionListener(new textFieldListener());
    //add to listeners
    CPanel.add(radLabel);
    CPanel.add(rad2Field);
    
    Radius2Panel.add(radius2,BorderLayout.NORTH);
    Radius2Panel.add(CPanel, BorderLayout.CENTER);
  }
  
  private class radioButtonListener implements ActionListener
  {
    public void actionPerformed(ActionEvent e)
    {
      String message = e.getActionCommand();
      if (message.equals("Center"))
      {
        centerSelected = true;
        innerRadiusSelected = false;
        outerRadiusSelected = false;
      }
      else if( message.equals("Inner Radius"))
      {
        centerSelected = false;
        innerRadiusSelected = true;
        outerRadiusSelected = false;
      }
      else if( message.equals("Outer Radius"))
      {
        centerSelected = false;
        innerRadiusSelected = false;
        outerRadiusSelected = true;
      }
    }
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
      if ( source.equals(cenX))
      {
        cenX.setText(numericText);
      }
      
      else if(source.equals(cenY))
      {
        cenY.setText(numericText);
      }
      
      else if(source.equals(rad1Field))
      {
        rad1Field.setText(numericText);
      }
      
      else if(source.equals(rad2Field))
      {
        rad2Field.setText(numericText);
      } 
      setDefiningPoints();
      firePropertyChange(DRAW_CURSOR,1,2);
    }  
  }
  
  
 /* public static void main(String[]args)
  {
    AnnularRegionOpEditFrame test = new AnnularRegionOpEditFrame();
    test.setVisible(true);
  }*/
}