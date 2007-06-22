package gov.anl.ipns.ViewTools.Components.RegionOpEditFrames;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import gov.anl.ipns.Util.Numeric.floatPoint2D;
import gov.anl.ipns.ViewTools.Components.Cursor.CursorTag;
import gov.anl.ipns.ViewTools.Components.Cursor.PointCursor;
import gov.anl.ipns.ViewTools.Components.Region.RegionOp;

import javax.swing.*;

/**
 * This class creates a point Region Editor based on one defining point
 * in the world coordinate system
 * @author Josh Oakgrove
 *
 */
public class PointRegionOpEditFrame extends RegionOpEditFrame
{
  private static int VALUE_JUMP = 5;  
    
  private float p1x;
  private float p1y;

  private int regionIndex;
  private int pointIndex;
  
  /**
   * constructor takes a single point in the World coordinate system
   * @param pt1 floatPoint2D in world coordinate system
   * @param op Operation to go with the Region
   * @param rIndex The index of the RegionOp with in the RegionOpList
   * @param pIndex The index of the point in the Point Region
   */
  public PointRegionOpEditFrame( floatPoint2D pt1,
      RegionOp.Operation op,int rIndex,int pIndex)
  {
      super("Point Editor",op);
      super.setBounds(700,390, 260, 290);
      super.setResizable(false);
      super.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
      this_editor = this;

      p1x=pt1.x;
      p1y=pt1.y;
   
      center.setSelected(true);
      cenX.setText(""+p1x);
      cenY.setText(""+p1y);
      cenX.addActionListener(new textFieldListener());
      cenY.addActionListener(new textFieldListener());
      regionIndex = rIndex;
      pointIndex = pIndex;

      //set approprate values for draw
      //send Draw message
  }
  
  public int getRegionIndex()
  {
    return regionIndex;
  }
  
  public int getPointIndex()
  {
    return pointIndex;
  }

  public floatPoint2D[] getDefiningPoints()
  {
    floatPoint2D[] points = new floatPoint2D[1];
    points[0]=new floatPoint2D(p1x,p1y);
    
    return points;
  }

  public CursorTag getTypeCursor()
  {
    PointCursor cursor = null;
    return cursor;
  }

  public void dispose()
  {
    super.dispose();
    this_editor.firePropertyChange(CANCEL,1,2);
  }
  
  public void Down()
  {
    p1y -= VALUE_JUMP;
    cenY.setText(""+p1y); 
    firePropertyChange(DRAW_CURSOR, 1, 2);
  }

  public void Left()
  {
    p1x -= VALUE_JUMP;
    cenX.setText(""+p1x);
    firePropertyChange(DRAW_CURSOR, 1, 2);
  }

  public void Right()
  {
    p1x += VALUE_JUMP;
    cenX.setText(""+p1x);
    firePropertyChange(DRAW_CURSOR, 1, 2);
  }

  public void Up()
  {
    p1y += VALUE_JUMP;
    cenY.setText(""+p1y);
    firePropertyChange(DRAW_CURSOR, 1, 2);
  }

  /*protected void buildDefiningPanel()
  {
    DefiningPanel = new JPanel();
    JPanel xPanel = new JPanel();
    JPanel yPanel = new JPanel();
    JLabel xLable = new JPanel();
  }*/
  private class textFieldListener implements ActionListener
  {

    public void actionPerformed(ActionEvent e)
    {
      Object source = e.getSource();
      ////System.out.println("you changed the text.");
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
      if ( source.equals(cenX) )
      {
        //System.out.println("Point 1 x changed");
        cenX.setText(numericText);
        p1x = Float.parseFloat(numericText);
        this_editor.firePropertyChange(DRAW_CURSOR,1,2);
      }

      else if(source.equals(cenY))
      {
        //System.out.println("Point 1 y changed");
        cenY.setText(numericText);
        p1y = Float.parseFloat(numericText);
        this_editor.firePropertyChange(DRAW_CURSOR,1,2);
      }
    }
  }


  /*public static void main(String[]args)
  {
    PointRegionOpEditFrame test = new PointRegionOpEditFrame();
    test.setVisible(true);
  }*/
 }