/* 
 * File: DoubleWedgeRegionOpEditFrame.java 
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
 * Revision 1.8  2007/08/23 21:06:31  dennis
 * Removed unused imports.
 *
 * Revision 1.7  2007/07/30 20:27:42  oakgrovej
 * Commented out System.out.print()
 *
 * Revision 1.6  2007/07/10 18:37:36  oakgrovej
 * Added use of ValuatorPanels
 *
 * Revision 1.5  2007/07/02 20:00:55  oakgrovej
 * Added Copyright notice & log message
 * Set Title of JFrame to Double Wedge Editor
 * 
 */ 
package gov.anl.ipns.ViewTools.Components.RegionOpEditFrames;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.*;

import gov.anl.ipns.Util.Numeric.floatPoint2D;
import gov.anl.ipns.ViewTools.Components.Cursor.CursorTag;
import gov.anl.ipns.ViewTools.Components.Cursor.WedgeCursor;
import gov.anl.ipns.ViewTools.Components.Region.RegionOp;
import gov.anl.ipns.ViewTools.UI.ValuatorPanels.AngleValuatorPanel;
import gov.anl.ipns.ViewTools.UI.ValuatorPanels.RadiusValuatorPanel;



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
  private RadiusValuatorPanel RadiusValuator;
  private AngleValuatorPanel AxisValuator;
  private AngleValuatorPanel IncludedValuator;
  private JPanel AxisAnglePanel;
  private JPanel IncludedAnglePanel;
    
  private float p1x;
  private float p1y;
  private float p2x;
  private float p2y;
  private float p3x;
  private float p3y;
  
  private int regionIndex;

  /**
   * constructor takes in three points:
   * wedgePoints[0]---center point
   * wedgePoints[1]---corner point
   * wedgePoints[2]---rotation point
   * @param wedgePoints floatPoint2D[] in world coordinate system
   * @param op Operation to go with the Region
   * @param index The index of the RegionOp with in the RegionOpList
   */
  public DoubleWedgeRegionOpEditFrame(floatPoint2D[] wedgePoints
      ,RegionOp.Operation op,int index)
  {
    super("Double Wedge Editor",op);
    super.setBounds(700,390, 450, 280);
    super.setResizable(false);
    super.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    this_editor = this;
    
    setEditorValues(wedgePoints);
    
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
    RadiusValuator.setValue(rad);
    
//  set the angles
    AxisValuator.setAngle((int)Math.round(wedgePoints[5].x+
        wedgePoints[5].y/2.0));
    IncludedValuator.setAngle((int)wedgePoints[5].y);
  }
  
  private void setDefiningPoints()
  {
    //uses the angles, center point, and radius to get the 
    //three defining points: center, corner and point on axis.
    p1x = Float.parseFloat(cenX.getText());
    p1y = Float.parseFloat(cenY.getText());
    float rad = RadiusValuator.getValue();
    float Dx = rad*(float)Math.cos(Math.PI*
                           (float)AxisValuator.getAngle()/180.0);
    float Dy = rad*(float)Math.sin(Math.PI*
                           (float)AxisValuator.getAngle()/180.0);
    p3x = p1x + Dx;
    p3y = p1y + Dy;
    
    float Dx2 = rad*(float)Math.cos(Math.PI*
        ((float)IncludedValuator.getAngle()/2.0+
        (float)AxisValuator.getAngle())/180.0);
            //  --------------rotation alternative-------------------
            //Dx*(float)Math.cos(Math.PI*
                            //((float)includedAngleSlider.getValue())/360.0)-
            //Dy*(float)Math.sin(Math.PI*
                            //((float)includedAngleSlider.getValue())/360.0);
    float Dy2 = rad*(float)Math.sin(Math.PI*
        ((float)IncludedValuator.getAngle()/2.0+
        (float)AxisValuator.getAngle())/180.0);
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
    if(center.isSelected())
    {
      p1y -= VALUE_JUMP;
      p2y -= VALUE_JUMP;
      p3y -= VALUE_JUMP;
      cenY.setText(""+p1y);
    }
    else if (RadiusValuator.isSelected())
    {
      RadiusValuator.addToValue(-VALUE_JUMP);
      setDefiningPoints();
    }
    firePropertyChange(DRAW_CURSOR, 1, 2);
  }

  public void Left()
  {
    if(center.isSelected())
    {
      p1x -= VALUE_JUMP;
      p2x -= VALUE_JUMP;
      p3x -= VALUE_JUMP;
      cenX.setText(""+p1x);
    }
    else if (RadiusValuator.isSelected())
    {
      RadiusValuator.addToValue(-VALUE_JUMP);
      setDefiningPoints();
    }
    firePropertyChange(DRAW_CURSOR, 1, 2);
  }

  public void Right()
  {
    if(center.isSelected())
    {
      p1x += VALUE_JUMP;
      p2x += VALUE_JUMP;
      p3x += VALUE_JUMP;
      cenX.setText(""+p1x);
    }
    else if (RadiusValuator.isSelected())
    {
      RadiusValuator.addToValue(VALUE_JUMP);
      setDefiningPoints();
    }
    firePropertyChange(DRAW_CURSOR, 1, 2);
  }

  public void Up()
  {
    if(center.isSelected())
    {
      p1y += VALUE_JUMP;
      p2y += VALUE_JUMP;
      p3y += VALUE_JUMP;
      cenY.setText(""+p1y);
    }
    else if (RadiusValuator.isSelected())
    {
      RadiusValuator.addToValue(VALUE_JUMP);
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
    
    RadiusValuator = new RadiusValuatorPanel("Radius",0,radioGroup);
    RadiusPanel = RadiusValuator.getPanel();
    RadiusPanel.addPropertyChangeListener(new PanelListener());
 
  }
  
  private void buildAxisAnglePanel()
  {
    AxisValuator = 
      new AngleValuatorPanel("Axis Angle",-180,180,0);
    AxisAnglePanel = AxisValuator.getPanel();
    AxisAnglePanel.addPropertyChangeListener(new PanelListener());
  }
  
  private void buildIncludedAnglePanel()
  {
    IncludedValuator = 
      new AngleValuatorPanel("Included Angle",0,180,0);
    IncludedAnglePanel = IncludedValuator.getPanel();
    IncludedAnglePanel.addPropertyChangeListener(new PanelListener());
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
      
      if (source.equals(cenX))
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
            
      this_editor.firePropertyChange(DRAW_CURSOR,1,2);
    }
  }
  
  private class PanelListener implements PropertyChangeListener
  {
    public void propertyChange(PropertyChangeEvent e)
    {
      //System.out.println("Panel Listener");
      Object source = e.getSource();
      if( source.equals(RadiusPanel) )
      {
        //System.out.println("firing draw cursor");
        setDefiningPoints();
        this_editor.firePropertyChange(DRAW_CURSOR,1,2);
      }
      
      else if( source.equals(AxisAnglePanel) )
      {
        setDefiningPoints();
        this_editor.firePropertyChange(DRAW_CURSOR,1,2);
      }
      
      else if( source.equals(IncludedAnglePanel) )
      {
        setDefiningPoints();
        this_editor.firePropertyChange(DRAW_CURSOR,1,2);
      }
      
    }
    
  }
  
  /*public static void main(String[]args)
  {
    WedgeRegionOpEditFrame2 test = new WedgeRegionOpEditFrame2();
    test.setVisible(true);
  }*/
  
}