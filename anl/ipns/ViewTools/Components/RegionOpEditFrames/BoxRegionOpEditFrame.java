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
 * Revision 1.3  2007/07/02 19:59:56  oakgrovej
 * Added Copyright notice & log message
 * 
 */ 
package gov.anl.ipns.ViewTools.Components.RegionOpEditFrames;

import gov.anl.ipns.Util.Numeric.floatPoint2D;
import gov.anl.ipns.ViewTools.Components.Cursor.BoxPanCursor;
import gov.anl.ipns.ViewTools.Components.Cursor.CursorTag;
import gov.anl.ipns.ViewTools.Components.Region.RegionOp;

import java.awt.*;
import java.awt.event.*;

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
  
  private boolean dimensionsSelected = false;
  
  private JRadioButton dimensions;
  private JPanel DimensionPanel;
  
  JTextField widthField;
  JTextField heightField;
  
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
    points[0]=new floatPoint2D(p1x,p1y);
    points[1]=new floatPoint2D(p2x,p2y);
    return points;
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
//      //System.out.println("height changed");
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
    BoxRegionOpEditFrame2 test = new BoxRegionOpEditFrame2
                                (new floatPoint2D(40,60),new floatPoint2D(60,40));
    test.setVisible(true);
  }*/
}