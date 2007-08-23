/* 
 * File: BoxRegionOpEditFrame.java 
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
 * Revision 1.5  2007/08/23 21:06:31  dennis
 * Removed unused imports.
 *
 * Revision 1.4  2007/07/10 18:37:27  oakgrovej
 * Added use of ValuatorPanels
 *
 * Revision 1.3  2007/07/02 19:59:56  oakgrovej
 * Added Copyright notice & log message
 * 
 */ 
package gov.anl.ipns.ViewTools.Components.RegionOpEditFrames;

import gov.anl.ipns.Util.Numeric.floatPoint2D;
import gov.anl.ipns.ViewTools.Components.Cursor.BoxPanCursor;
import gov.anl.ipns.ViewTools.Components.Cursor.CursorTag;
import gov.anl.ipns.ViewTools.Components.Region.RegionOp;
import gov.anl.ipns.ViewTools.UI.ValuatorPanels.PointValuatorPanel;

import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.*;

/**
 * This class creates a Box Region Editor based on two defining points
 * in the world coordinate system
 * @author Josh Oakgrove
 *
 */
public class BoxRegionOpEditFrame extends RegionOpEditFrame
{
  private static float VALUE_JUMP = 5;
  
  private JPanel DimensionPanel;
  
  private PointValuatorPanel dimValuator;
  
  private float p1x ;
  private float p1y ;
  private float p2x ;
  private float p2y ;
  
  
  private int regionIndex;
  
  /**
   * constructor takes in two points:
   * pt1---first corner point
   * pt2---seconed corner point
   * @param pt1 floatPoint2D in world coordinate system --- first corner point
   * @param pt2 floatPoint2D in world coordinate system --- second corner point
   * @param op Operation to go with the Region
   * @param index The index of the RegionOp with in the RegionOpList
   */
  public BoxRegionOpEditFrame( floatPoint2D pt1, floatPoint2D pt2,
                               RegionOp.Operation op,int index )
  {
    super("Box Editor",op);
    super.setBounds(700,390, 400, 280);
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
    points[0]=new floatPoint2D(p1x,p1y);
    points[1]=new floatPoint2D(p2x,p2y);
    return points;
  }
  
  private void setDefiningPoints()
  {
    float w = dimValuator.getPoint().x;
    float h = dimValuator.getPoint().y;
    p1x = Float.parseFloat(cenX.getText()) - .5f * w;
    p1y = Float.parseFloat(cenY.getText()) + .5f * h;
    p2x = Float.parseFloat(cenX.getText()) + .5f * w;
    p2y = Float.parseFloat(cenY.getText()) - .5f * h;
  }
  
  public CursorTag getTypeCursor()
  {
    BoxPanCursor cursor = null;
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
    dimValuator = new PointValuatorPanel
            ("Dimensions","Width","Height", 0, 0, radioGroup);
    DimensionPanel = dimValuator.getPanel();
    DimensionPanel.addPropertyChangeListener(new PanelListener());
  }
  
  private void calculateDimensions()
  {
    float w = Math.abs(p2x-p1x);
    float h = Math.abs(p1y-p2y);
    dimValuator.setPoint(new floatPoint2D(w,h));
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
    if(center.isSelected())
    {
      p1y -= VALUE_JUMP;
      p2y -= VALUE_JUMP;
    }
    else if (dimValuator.isSelected())
    {
      p1y -= VALUE_JUMP/2.0f;
      p2y += VALUE_JUMP/2.0f;
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
    else if (dimValuator.isSelected())
    {
      p1x += VALUE_JUMP/2.0f;
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
    else if (dimValuator.isSelected())
    {
      p1x -= VALUE_JUMP/2.0f;
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
    else if (dimValuator.isSelected())
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
      
      if (source.equals(cenX))
      {
        center.setSelected(true);
        float w = dimValuator.getPoint().x;
        p1x = Float.parseFloat(numericText) - w/2.0f;
        p2x = Float.parseFloat(numericText) + w/2.0f;
      }
      
      else if (source.equals(cenY))
      {
        center.setSelected(true);
        float h = dimValuator.getPoint().y;
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
      if( source.equals(DimensionPanel) )
      {
        setDefiningPoints();
      }
      
      this_editor.firePropertyChange(DRAW_CURSOR,1,2);
      
    }
    
  }
  
  
  /*public static void main(String[]args)
  {
    BoxRegionOpEditFrame2 test = new BoxRegionOpEditFrame2
                                (new floatPoint2D(40,60),new floatPoint2D(60,40));
    test.setVisible(true);
  }*/
}