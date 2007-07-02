/* 
 * File: EllipseRegionOpEditFrame.java 
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
 * Revision 1.3  2007/07/02 20:01:03  oakgrovej
 * Added Copyright notice & log message
 * 
 */ 
package gov.anl.ipns.ViewTools.Components.RegionOpEditFrames;

import java.awt.BorderLayout;
import java.awt.event.*;

import gov.anl.ipns.Util.Numeric.floatPoint2D;
import gov.anl.ipns.ViewTools.Components.Cursor.CursorTag;
import gov.anl.ipns.ViewTools.Components.Cursor.EllipseCursor;
import gov.anl.ipns.ViewTools.Components.Region.RegionOp;
import gov.anl.ipns.ViewTools.Components.Region.RegionOp.Operation;

import javax.swing.*;

/**
 * This class creates an Ellipse Region Editor based on two defining points
 * in the world coordinate system
 * @author Josh Oakgrove
 *
 */
public class EllipseRegionOpEditFrame extends RegionOpEditFrame
{

private static float VALUE_JUMP = 5.0f;
  
  private boolean dimensionsSelected = false;
  
  private JRadioButton dimensions;
  private JCheckBox forceCircle;

  private JPanel DimensionPanel;
  private JPanel CirclePanel;
  
  private JTextField widthField;
  private JTextField heightField;
  private JTextField radiusField;
  
  private float crx ;
  private float cry ;
  private float ctx ;
  private float cty ;
  
  private int regionIndex;
  
  /**
   * constructor takes two points:
   * pt1 -- the corner of the bounding box
   * pt2 -- the center of the ellipse
   * @param corn floatPoint2D in world coordinate system corner of bounding box
   * @param cent floatPoint2D in world coordinate system center of ellipse
   * @param op Operation to go with the Region
   * @param index The index of the RegionOp with in the RegionOpList
   */
  public EllipseRegionOpEditFrame(floatPoint2D corn, floatPoint2D cent,
                                  RegionOp.Operation op,int index)
  {
    super("Ellipse Editor",op);
    super.setBounds(700,390, 400, 284);
    super.setResizable(false);
    super.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    this_editor = this;
        
    center.addActionListener(new radioButtonListener());
    cenY.addActionListener(new textFieldListener());
    cenX.addActionListener(new textFieldListener());
    crx = corn.x;
    cry = corn.y;
    ctx = cent.x;
    cty = cent.y;

    calculateDimensions();
    
    regionIndex = index;
  }
  
  /**
   * Gives back the points defining the Ellipse
   * 
   * points[0]----top left corner
   * points[1]----bottom right corner
   * points[2]----center
   * 
   * @return the defining points
   */
  public floatPoint2D[] getDefiningPoints()
  {
    floatPoint2D[] points = new floatPoint2D[3];
    points[0]=new floatPoint2D(crx,cry);
    points[2]=new floatPoint2D(ctx,cty);
    points[1]=new floatPoint2D(points[0].x-2.0f*(points[0].x-points[2].x),
                               points[0].y-2.0f*(points[0].y-points[2].y));
    return points;
  }

  public int getRegionIndex()
  {
    return regionIndex;
  }
  
  public CursorTag getTypeCursor()
  {
    EllipseCursor cursor=null;
    return cursor;
  }
  
  public void Down()
  {
    if(centerSelected)
    {
      cry -= VALUE_JUMP;
      cty -= VALUE_JUMP;
    }
    else if (dimensionsSelected && forceCircle.isSelected())
    {
      float rad = Float.parseFloat(radiusField.getText());
      radiusField.setText(""+(rad-VALUE_JUMP));
      radToDefPoints();
    }
    else if (dimensionsSelected)
    {
      if(cry>cty)
        cry -= VALUE_JUMP/2.0f;
      else
        cry += VALUE_JUMP/2.0f;
    }
    calculateDimensions();
  }

  public void Left()
  {
    if(centerSelected)
    {
      crx -= VALUE_JUMP;
      ctx -= VALUE_JUMP;
    }
    else if (dimensionsSelected && forceCircle.isSelected())
    {
      float rad = Float.parseFloat(radiusField.getText());
      radiusField.setText(""+(rad-VALUE_JUMP));
      radToDefPoints();
    }
    else if (dimensionsSelected)
    {
      if(crx>ctx)
        crx -= VALUE_JUMP/2.0f;
      else
        crx += VALUE_JUMP/2.0f;
    }
    calculateDimensions();
  }

  public void Right()
  {
    if(centerSelected)
    {
      crx += VALUE_JUMP;
      ctx += VALUE_JUMP;
    }
    else if (dimensionsSelected && forceCircle.isSelected())
    {
      float rad = Float.parseFloat(radiusField.getText());
      radiusField.setText(""+(rad+VALUE_JUMP));
      radToDefPoints();
    }
    else if (dimensionsSelected)
    {
      if(crx>ctx)
        crx += VALUE_JUMP/2.0f;
      else
        crx -= VALUE_JUMP/2.0f;
    }
    calculateDimensions();
  }

  public void Up()
  {
    if(centerSelected)
    {
      cry += VALUE_JUMP;
      cty += VALUE_JUMP;
    }
    else if (dimensionsSelected && forceCircle.isSelected())
    {
      float rad = Float.parseFloat(radiusField.getText());
      radiusField.setText(""+(rad+VALUE_JUMP));
      radToDefPoints();
    }
    else if (dimensionsSelected)
    {
      if(cry>cty)
        cry += VALUE_JUMP/2.0f;
      else
        cry -= VALUE_JUMP/2.0f;
    }
    calculateDimensions();
  }
  
  private void calculateDimensions()
  {
    float w = Math.abs(crx-ctx)*2.0f;
    float h = Math.abs(cry-cty)*2.0f;
    widthField.setText("" + w);
    heightField.setText("" + h);
    cenY.setText(""+cty);
    cenX.setText(""+ctx);
    float rad = (w+h)/4;
    radiusField.setText(""+rad);
    //System.out.println("Center = ("+ctx+","+cty+")\n"+
    //    "Corner = ("+crx+","+cry+")");
  }
  private void radToDefPoints()
  {
    crx = ctx+Float.parseFloat(radiusField.getText());
    cry = cty+Float.parseFloat(radiusField.getText());
  }
  
  protected void buildDefiningPanel()
  {
    DefiningPanel = new JPanel();
    DefiningPanel.setLayout(new BoxLayout(DefiningPanel,BoxLayout.Y_AXIS));
    buildDimensionPanel();
    buildCirclePanel();
    DefiningPanel.add(DimensionPanel);
    DefiningPanel.add(CirclePanel);
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
  
  private void buildCirclePanel()
  {
    CirclePanel = new JPanel(new BorderLayout());
    CirclePanel.setBorder(BorderFactory.createEtchedBorder());
    forceCircle = new JCheckBox("Force Circle");
    forceCircle.addKeyListener(new PositionKeyListener());
    forceCircle.addActionListener(new radioButtonListener());
    
    JPanel radiusPanel = new JPanel();
    JLabel aveRad = new JLabel("Average Radius");
    radiusField = new JTextField(6);
    radiusField.setEditable(false);
    radiusField.addActionListener(new textFieldListener());
    radiusPanel.add(aveRad);
    radiusPanel.add(radiusField);
    
    CirclePanel.add(forceCircle,BorderLayout.NORTH);
    CirclePanel.add(radiusPanel, BorderLayout.CENTER);
  }
  
  public void dispose()
  {
    super.dispose();
    this_editor.firePropertyChange(CANCEL,1,2);
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
        crx = ctx + Float.parseFloat(numericText)/2.0f;
        calculateDimensions();
        this_editor.firePropertyChange(DRAW_CURSOR,1,2);
      }
      
      else if(source.equals(heightField))
      {
//      System.out.println("height changed");
        dimensions.setSelected(true);
        cry = cty - Float.parseFloat(numericText)/2.0f;
        calculateDimensions();
        this_editor.firePropertyChange(DRAW_CURSOR,1,2);
      }
      
      else if (source.equals(cenX))
      {
        center.setSelected(true);
        float w = Float.parseFloat(widthField.getText());
        ctx = Float.parseFloat(numericText);
        crx = ctx + w/2.0f;
      }
      
      else if (source.equals(cenY))
      {
        center.setSelected(true);
        float h = Float.parseFloat(heightField.getText());
        cty = Float.parseFloat(numericText);
        cry = cty - h/2.0f;
      }
      
      else if (source.equals(radiusField))
      {
        radiusField.setText(numericText);
        radToDefPoints();
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
      
      else if( message.equals("Force Circle"))
      {
        if( forceCircle.isSelected())
        {
          widthField.setEditable(false);
          heightField.setEditable(false);
          radiusField.setEditable(true);
          radToDefPoints();
          calculateDimensions();
          this_editor.firePropertyChange(DRAW_CURSOR, 1, 2);
        }
        else
        {
          widthField.setEditable(true);
          heightField.setEditable(true);
          radiusField.setEditable(false);
        }
      }
    }
  }
  
  /*public static void main(String[]args)
  {
    EllipseRegionOpEditFrame2 test = new EllipseRegionOpEditFrame2
                      (new floatPoint2D(60,40),new floatPoint2D(50,50));
    test.setVisible(true);
  }*/
}