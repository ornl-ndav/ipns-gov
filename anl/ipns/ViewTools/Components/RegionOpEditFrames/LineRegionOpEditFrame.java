/* 
 * File: LineRegionOpEditFrame.java 
 *  
 * Copyright (C) 2007     Joshua Oakgrove 
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
 *            HH237H 
 *            Menomonie, WI. 54751 
 *            (715)-232-2291 
 * 
 * This work was supported by the National Science Foundation under grant 
 * number DMR-0426797, and by the Intense Pulsed Neutron Source Division 
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA. 
 * 
 * 
 * Modified: 
 * 
 * $Log$
 * Revision 1.6  2007/08/23 21:06:32  dennis
 * Removed unused imports.
 *
 * Revision 1.5  2007/07/10 18:37:51  oakgrovej
 * Added use of ValuatorPanels
 *
 * Revision 1.4  2007/07/02 20:01:41  oakgrovej
 * Added Copyright notice & log message
 * 
 */ 
package gov.anl.ipns.ViewTools.Components.RegionOpEditFrames;

import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import gov.anl.ipns.Util.Numeric.floatPoint2D;
import gov.anl.ipns.ViewTools.Components.Cursor.CursorTag;
import gov.anl.ipns.ViewTools.Components.Cursor.LineCursor;
import gov.anl.ipns.ViewTools.Components.Region.RegionOp;
import gov.anl.ipns.ViewTools.UI.ValuatorPanels.PointValuatorPanel;

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

  private JPanel Point1Panel;
  private JPanel Point2Panel;
  private PointValuatorPanel Point1Valuator;
  private PointValuatorPanel Point2Valuator;
  
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
    Point1Valuator = new PointValuatorPanel(
                "Point 1","X","Y",p1x,p1y,radioGroup);
    Point1Panel = Point1Valuator.getPanel();
    Point1Panel.addPropertyChangeListener(new PanelListener());
  }
  
  private void buildPoint2Panel()
  {
    Point2Valuator = new PointValuatorPanel(
        "Point 2","X","Y",p2x,p2y,radioGroup);
    Point2Panel = Point2Valuator.getPanel();
    Point2Panel.addPropertyChangeListener(new PanelListener());
  }
  
  private void calculateDimensions()
  {
    float w = Math.abs(p2x-p1x);
    float h = Math.abs(p1y-p2y);
    Point1Valuator.setPoint(new floatPoint2D(p1x,p1y));
    Point2Valuator.setPoint(new floatPoint2D(p2x,p2y));
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
  
  private void setDefiningPoints()
  {
    floatPoint2D point = Point1Valuator.getPoint();
    p1x = point.x;
    p1y = point.y;
    point = Point2Valuator.getPoint();
    p2x = point.x;
    p2y = point.y;
  }
  
  public void dispose()
  {
    super.dispose();
    this_editor.firePropertyChange(CANCEL,1,2);
  }
  
  public void Down()
  {
    if(center.isSelected())
    {
      p1y -= VALUE_JUMP;
      p2y -= VALUE_JUMP;
    }
    else if (Point1Valuator.isSelected())
    {
      p1y -= VALUE_JUMP/2.0f;
    }
    else if (Point2Valuator.isSelected())
    {
      p2y -= VALUE_JUMP/2.0f;
    }
    calculateDimensions();
  }

  public void Left()
  {
    if(center.isSelected())
    {
      p1x -= VALUE_JUMP;
      p2x -= VALUE_JUMP;
    }
    else if (Point1Valuator.isSelected())
    {
      p1x -= VALUE_JUMP/2.0f;
    }
    else if (Point2Valuator.isSelected())
    {
      p2x -= VALUE_JUMP/2.0f;
    }
    calculateDimensions();
  }

  public void Right()
  {
    if(center.isSelected())
    {
      p1x += VALUE_JUMP;
      p2x += VALUE_JUMP;
    }
    else if (Point1Valuator.isSelected())
    {
      p1x += VALUE_JUMP/2.0f;
    }
    else if (Point2Valuator.isSelected())
    {
      p2x += VALUE_JUMP/2.0f;
    }
    calculateDimensions();
  }

  public void Up()
  {
    if(center.isSelected())
    {
      p1y += VALUE_JUMP;
      p2y += VALUE_JUMP;
    }
    else if (Point1Valuator.isSelected())
    {
      p1y += VALUE_JUMP/2.0f;
    }
    else if (Point2Valuator.isSelected())
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
      
      if ( source.equals(cenX) )
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
  
  private class PanelListener implements PropertyChangeListener
  {
    public void propertyChange(PropertyChangeEvent e)
    {
      //System.out.println("Panel Listener");
      Object source = e.getSource();
      if( source.equals(Point1Panel) )
      {
        setDefiningPoints();
      }
      
      else if( source.equals(Point2Panel) )
      {
        setDefiningPoints();
      }
      this_editor.firePropertyChange(DRAW_CURSOR,1,2);
      
    }
    
  }
  
  /*public static void main(String[]args)
  {
    LineRegionOpEditFrame2 test = new LineRegionOpEditFrame2
                (new floatPoint2D(40,60),new floatPoint2D(60,40));
    test.setVisible(true);
  }*/
}