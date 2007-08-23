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
 * Revision 1.5  2007/08/23 21:06:31  dennis
 * Removed unused imports.
 *
 * Revision 1.4  2007/07/10 18:37:45  oakgrovej
 * Added use of ValuatorPanels
 *
 * Revision 1.3  2007/07/02 20:01:03  oakgrovej
 * Added Copyright notice & log message
 * 
 */ 
package gov.anl.ipns.ViewTools.Components.RegionOpEditFrames;

import java.awt.BorderLayout;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import gov.anl.ipns.Util.Numeric.floatPoint2D;
import gov.anl.ipns.ViewTools.Components.Cursor.CursorTag;
import gov.anl.ipns.ViewTools.Components.Cursor.EllipseCursor;
import gov.anl.ipns.ViewTools.Components.Region.RegionOp;
import gov.anl.ipns.ViewTools.UI.ValuatorPanels.PointValuatorPanel;

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
 
  private JCheckBox forceCircle;

  private JPanel DimensionPanel;
  private JPanel CirclePanel;
  private PointValuatorPanel dimValuator;
  
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
  
  private void setDefiningPoints()
  {
    float w = dimValuator.getPoint().x;
    float h = dimValuator.getPoint().y;
    ctx = Float.parseFloat(cenX.getText());
    cty = Float.parseFloat(cenY.getText());
    crx = ctx - .5f * w;
    cry = cty + .5f * h;
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
    if(center.isSelected())
    {
      cry -= VALUE_JUMP;
      cty -= VALUE_JUMP;
    }
    else if (dimValuator.isSelected() && forceCircle.isSelected())
    {
      float rad = Float.parseFloat(radiusField.getText());
      radiusField.setText(""+(rad-VALUE_JUMP));
      radToDefPoints();
    }
    else if (dimValuator.isSelected())
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
    if(center.isSelected())
    {
      crx -= VALUE_JUMP;
      ctx -= VALUE_JUMP;
    }
    else if (dimValuator.isSelected() && forceCircle.isSelected())
    {
      float rad = Float.parseFloat(radiusField.getText());
      radiusField.setText(""+(rad-VALUE_JUMP));
      radToDefPoints();
    }
    else if (dimValuator.isSelected())
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
    if(center.isSelected())
    {
      crx += VALUE_JUMP;
      ctx += VALUE_JUMP;
    }
    else if (dimValuator.isSelected() && forceCircle.isSelected())
    {
      float rad = Float.parseFloat(radiusField.getText());
      radiusField.setText(""+(rad+VALUE_JUMP));
      radToDefPoints();
    }
    else if (dimValuator.isSelected())
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
    if(center.isSelected())
    {
      cry += VALUE_JUMP;
      cty += VALUE_JUMP;
    }
    else if (dimValuator.isSelected() && forceCircle.isSelected())
    {
      float rad = Float.parseFloat(radiusField.getText());
      radiusField.setText(""+(rad+VALUE_JUMP));
      radToDefPoints();
    }
    else if (dimValuator.isSelected())
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
    dimValuator.setPoint(new floatPoint2D(w,h));
    cenY.setText(""+cty);
    cenX.setText(""+ctx);
    float rad = (w+h)/4;
    radiusField.setText(""+rad);
    //System.out.println("Center = ("+ctx+","+cty+")\n"+
    //    "Corner = ("+crx+","+cry+")");
  }
  private void radToDefPoints()
  {
    crx = ctx-Float.parseFloat(radiusField.getText());
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
    dimValuator = new PointValuatorPanel
                ("Dimensions","Width","Height", 0, 0, radioGroup);
    DimensionPanel = dimValuator.getPanel();
    DimensionPanel.addPropertyChangeListener(new PanelListener());
    
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
      
      if (source.equals(cenX))
      {
        center.setSelected(true);
        float w = dimValuator.getPoint().x;
        ctx = Float.parseFloat(numericText);
        crx = ctx + w/2.0f;
      }
      
      else if (source.equals(cenY))
      {
        center.setSelected(true);
        float h = dimValuator.getPoint().y;
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
      
      if( message.equals("Force Circle"))
      {
        if( forceCircle.isSelected())
        {
          dimValuator.setEditable(false);
          radiusField.setEditable(true);
          radToDefPoints();
          calculateDimensions();
          this_editor.firePropertyChange(DRAW_CURSOR, 1, 2);
        }
        else
        {
          dimValuator.setEditable(true);
          radiusField.setEditable(false);
        }
      }
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
    EllipseRegionOpEditFrame2 test = new EllipseRegionOpEditFrame2
                      (new floatPoint2D(60,40),new floatPoint2D(50,50));
    test.setVisible(true);
  }*/
}