/* 
 * File: AnnularRegionOpEditFrame.java 
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
 * Revision 1.5  2007/07/10 18:37:19  oakgrovej
 * Added use of ValuatorPanels
 *
 * Revision 1.4  2007/07/02 19:59:47  oakgrovej
 * Added Copyright notice & log message
 * 
 */ 
package gov.anl.ipns.ViewTools.Components.RegionOpEditFrames;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.*;

import gov.anl.ipns.Util.Numeric.floatPoint2D;
import gov.anl.ipns.ViewTools.Components.Cursor.AnnularCursor;
import gov.anl.ipns.ViewTools.Components.Cursor.CursorTag;
import gov.anl.ipns.ViewTools.Components.Cursor.WedgeCursor;
import gov.anl.ipns.ViewTools.Components.Region.RegionOp;
import gov.anl.ipns.ViewTools.Components.Region.RegionOp.Operation;
import gov.anl.ipns.ViewTools.UI.ValuatorPanels.RadiusValuatorPanel;


/**
 * This class creates a Annular Region Editor based on three defining points
 * in the world coordinate system
 * @author Josh Oakgrove
 *
 */

public class AnnularRegionOpEditFrame extends RegionOpEditFrame
{
private static int VALUE_JUMP = 5;
  
  private JPanel Radius1Panel;
  private JPanel Radius2Panel;
  
  private RadiusValuatorPanel Rad1Valuator;
  private RadiusValuatorPanel Rad2Valuator;
    
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
    super.setBounds(700,390, 370, 277);
    super.setResizable(false);
    super.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    this_editor = this;
    
    p1x=pt1.x;
    p1y=pt1.y;
    p2x=pt2.x;
    p2y=pt2.y;
    p3x=pt3.x;
    p3y=pt3.y;
    
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
    Rad1Valuator.setValue((float)Math.sqrt(xVal*xVal + yVal*yVal));
    
    //set radius 2
    xVal = p3x - p1x;
    yVal = p3y - p1y;
    Rad2Valuator.setValue((float)Math.sqrt(xVal*xVal + yVal*yVal));
  }
  
  private void setDefiningPoints()
  {
    //center point
    p1y = Float.parseFloat(cenY.getText());
    p1x = Float.parseFloat(cenX.getText());
    
    //point on inner circle
    p2y = p1y;
    p2x = p1x + Rad1Valuator.getValue();
    
    //point on outer circle
    p3y = p1y;
    p3x = p1x + Rad2Valuator.getValue();
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
    if(center.isSelected())
    {
      p1y -= VALUE_JUMP;
      p2y -= VALUE_JUMP;
      p3y -= VALUE_JUMP;
      cenY.setText(""+p1y);
    }
    else if (Rad1Valuator.isSelected())
    {
      Rad1Valuator.setValue(Rad1Valuator.getValue()-VALUE_JUMP);
      setDefiningPoints();
    }
    else if (Rad2Valuator.isSelected())
    {
      Rad2Valuator.setValue(Rad2Valuator.getValue()-VALUE_JUMP);
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
    else if (Rad1Valuator.isSelected())
    {
      Rad1Valuator.setValue(Rad1Valuator.getValue()-VALUE_JUMP);
      setDefiningPoints();
    }
    else if (Rad2Valuator.isSelected())
    {
      Rad2Valuator.setValue(Rad2Valuator.getValue()-VALUE_JUMP);
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
    else if (Rad1Valuator.isSelected())
    {
      Rad1Valuator.setValue(Rad1Valuator.getValue()+VALUE_JUMP);
      setDefiningPoints();
    }
    else if (Rad2Valuator.isSelected())
    {
      Rad2Valuator.setValue(Rad2Valuator.getValue()+VALUE_JUMP);
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
    else if (Rad1Valuator.isSelected())
    {
      Rad1Valuator.setValue(Rad1Valuator.getValue()+VALUE_JUMP);
      setDefiningPoints();
    }
    else if (Rad2Valuator.isSelected())
    {
      Rad2Valuator.setValue(Rad2Valuator.getValue()+VALUE_JUMP);
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
    Rad1Valuator = new RadiusValuatorPanel("Inner Radius",0,radioGroup);
    Radius1Panel = Rad1Valuator.getPanel();
    Radius1Panel.addPropertyChangeListener(new PanelListener());
  }
  
  private void buildRadius2Panel()
  {
    Rad2Valuator = new RadiusValuatorPanel("Outer Radius",0,radioGroup);
    Radius2Panel = Rad2Valuator.getPanel();
    Radius2Panel.addPropertyChangeListener(new PanelListener());
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
    }
  }
  
  private class PanelListener implements PropertyChangeListener
  {
    public void propertyChange(PropertyChangeEvent e)
    {
      //System.out.println("Panel Listener");
      Object source = e.getSource();
      if( source.equals(Radius1Panel) )
      {
        setDefiningPoints();
        this_editor.firePropertyChange(DRAW_CURSOR,1,2);
      }
      
      else if( source.equals(Radius2Panel) )
      {
        setDefiningPoints();
        this_editor.firePropertyChange(DRAW_CURSOR,1,2);
      }
      
    }
    
  }
  
  
 /* public static void main(String[]args)
  {
    AnnularRegionOpEditFrame test = new AnnularRegionOpEditFrame();
    test.setVisible(true);
  }*/
}