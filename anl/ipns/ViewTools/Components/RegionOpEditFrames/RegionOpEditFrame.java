/* 
 * File: RegionOpEditFrame.java 
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
 * Revision 1.7  2007/08/23 21:06:32  dennis
 * Removed unused imports.
 *
 * Revision 1.6  2007/07/02 20:02:56  oakgrovej
 * Added Copyright notice & log message
 * commented out a line that was causing an error with the done button command
 * 
 */ 
package gov.anl.ipns.ViewTools.Components.RegionOpEditFrames;


import gov.anl.ipns.Util.Numeric.floatPoint2D;
import gov.anl.ipns.ViewTools.Components.Cursor.CursorTag;
import gov.anl.ipns.ViewTools.Components.Region.RegionOp.Operation;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.*;
import javax.swing.*;

/**
 * This class is designed to create some common functionality
 * for all region editors
 * @author Josh Oakgrove
 *
 */
public abstract class RegionOpEditFrame extends JFrame
{
  public static String DRAW_CURSOR = "Draw Cursor";
  public static String CANCEL = "Cancel";
  public static String DONE = "Done";
  public static String DRAW_REGION = "Draw Region";
  
  private Operation operation;
  private Operation[] operations = {Operation.UNION,
                                    Operation.INTERSECT, 
                                    Operation.INTERSECT_COMPLEMENT,};
  private String[] opStrings = {"Union",
                                "Intersect",
                                "Intersect_Complement"};
  
  private JPanel PositionPanel;
  private JPanel OpPanel;
  private JPanel ButtonPanel;
  private JPanel MovementPanel;
  protected JPanel DefiningPanel;
  private JPanel CenterPanel;
  private JPanel LRUDPanel;
  
  protected ButtonGroup radioGroup = new ButtonGroup();
  protected JRadioButton center;
  protected JTextField cenX = new JTextField(6);
  protected JTextField cenY = new JTextField(6);
  private JButton up;
  private JButton down;
  private JButton right;
  private JButton left;
  private JButton done;
  private JButton draw;
  private JButton cancel;
  protected JFrame this_editor;
  
  protected boolean centerSelected = false;
  
  public RegionOpEditFrame(String name,Operation op)
  {
    super(name);
    this.setBounds(700,390, 230, 250);
    this.addWindowFocusListener(new editWindowFocusListener());
    operation = op;
    Container contentPane = getContentPane();
    contentPane.setLayout(new BoxLayout(contentPane,BoxLayout.Y_AXIS));
    buildPositionPanel();
    buildOpPanel();
    buildButtonPanel();
    contentPane.add(PositionPanel);
    contentPane.add(OpPanel);
    contentPane.add(ButtonPanel);
  }
  
  private void buildPositionPanel()
  {
    PositionPanel = new JPanel();
    PositionPanel.setLayout(new BoxLayout(PositionPanel,BoxLayout.X_AXIS));
    buildMovementPanel();
    buildDefiningPanel();
    PositionPanel.add(MovementPanel);
    if(DefiningPanel!=null)
      PositionPanel.add(DefiningPanel);
  }
  
  private void buildOpPanel()
  {
    OpPanel= new JPanel(new BorderLayout());
    OpPanel.setBorder(BorderFactory.createEtchedBorder());
    JPanel cPanel = new JPanel();
    JLabel OpLabel = new JLabel("Operation");
    JComboBox opChooser = new JComboBox(opStrings);
    
    for( int i=0; i<operations.length; i++ )
    {
      if( operations[i].equals(operation))
        opChooser.setSelectedIndex(i);
    }
    
    cPanel.add(OpLabel);
    cPanel.add(opChooser);
    opChooser.setEditable(false);
    opChooser.addActionListener(new comboBoxListener());
    OpPanel.add(cPanel,BorderLayout.CENTER);
  }
  
  private void buildButtonPanel()
  {
    ButtonPanel = new JPanel();
    ButtonPanel.setBorder(BorderFactory.createEtchedBorder());
    ButtonPanel.setLayout(new BoxLayout( ButtonPanel,BoxLayout.X_AXIS ));
    JPanel BPanel = new JPanel();
    
    done = new JButton("Done");
    done.addActionListener(new buttonListener());
    draw = new JButton("Draw");
    draw.addActionListener(new buttonListener());
    cancel = new JButton("Cancel");
    cancel.addActionListener(new buttonListener());
    
    BPanel.add(done);
    BPanel.add(draw);
    BPanel.add(cancel);
    ButtonPanel.add(BPanel);
  }
  
  private void buildMovementPanel()
  {
    MovementPanel = new JPanel();
    MovementPanel.setLayout(new BoxLayout(MovementPanel,BoxLayout.Y_AXIS));
    buildCenterPanel();
    buildLRUDPanel();
    MovementPanel.add(CenterPanel);
    MovementPanel.add(LRUDPanel);
  }
  
  protected void buildDefiningPanel()
  {
    DefiningPanel = null;
  }
  
  private void buildCenterPanel()
  {
    CenterPanel = new JPanel(new BorderLayout());
    CenterPanel.setBorder(BorderFactory.createEtchedBorder());
    center = new JRadioButton("Center");
    center.addKeyListener(new PositionKeyListener());
    radioGroup.add(center);
    center.setSelected(true);
    centerSelected = true;
    JPanel CPanel = new JPanel();
    JLabel xLabel = new JLabel("X");
    JLabel yLabel = new JLabel("Y");
    CPanel.add(xLabel);
    CPanel.add(cenX);
    CPanel.add(yLabel);
    CPanel.add(cenY);
    
    CenterPanel.add(center,BorderLayout.NORTH);
    CenterPanel.add(CPanel, BorderLayout.CENTER);
  }
  
  private void buildLRUDPanel()
  {
    LRUDPanel = new JPanel();
    LRUDPanel.setLayout(new BoxLayout(LRUDPanel,BoxLayout.Y_AXIS));
    LRUDPanel.setBorder(BorderFactory.createEtchedBorder());
    up = new JButton("Up");
    left = new JButton("L");
    right = new JButton("R");
    down = new JButton("Dn");
    //listeners
    up.addActionListener(new buttonListener());
    left.addActionListener(new buttonListener());
    right.addActionListener(new buttonListener());
    down.addActionListener(new buttonListener());
    up.addKeyListener(new PositionKeyListener());
    left.addKeyListener(new PositionKeyListener());
    right.addKeyListener(new PositionKeyListener());
    down.addKeyListener(new PositionKeyListener());
    JPanel UPanel = new JPanel();
    UPanel.add(up);
    JPanel LRPanel = new JPanel();
    LRPanel.add(left);
    LRPanel.add(right);
    JPanel DPanel = new JPanel();
    DPanel.add(down);
    
    LRUDPanel.add(UPanel);
    LRUDPanel.add(LRPanel);
    LRUDPanel.add(DPanel);
  }
  

  public void dispose()
  {
    super.dispose();
  }
  
  public Operation getOp()
  {
    return operation;
  }
  
  public abstract floatPoint2D[] getDefiningPoints();
  public abstract int getRegionIndex();
  public abstract CursorTag getTypeCursor();
  public abstract void Up();
  public abstract void Down();
  public abstract void Left();
  public abstract void Right();
  
  private class buttonListener implements ActionListener
  {
    public void actionPerformed(ActionEvent e)
    {
      String message = e.getActionCommand();
      //System.out.println(message);
      if( message.equals("Up"))
      {
        //System.out.println("Up: buttonListener");
        Up();
        this_editor.firePropertyChange(DRAW_CURSOR,1,2);
      }
      
      else if( message.equals("R"))
      {
        //System.out.println("R");
        Right();
        this_editor.firePropertyChange(DRAW_CURSOR,1,2);
      }
      
      else if( message.equals("L"))
      {
        //System.out.println("L");
        Left();
        this_editor.firePropertyChange(DRAW_CURSOR,1,2);
      }
      
      else if( message.equals("Dn"))
      {
        //System.out.println("Dn");
        Down();
        this_editor.firePropertyChange(DRAW_CURSOR,1,2);
      }
      
      else if( message.equals("Draw"))
      {
        //System.out.println("Draw");
        //this_editor.firePropertyChange(DRAW_CURSOR,1,2);
        this_editor.firePropertyChange(DRAW_REGION,1,2);
        //System.out.println("after fire");
      }
      
      else if( message.equals("Done"))
      {
        //System.out.println("Done");
        //the next line was producing a problem with editing the 
        //wedge and double wedge regions.
        //this_editor.firePropertyChange(DRAW_CURSOR,1,2);
        this_editor.firePropertyChange(DONE,1,2);
      }
      
      else if( message.equals("Cancel"))
      {
        //System.out.println("Cancel");
        this_editor.dispose();
      }    
    }
  }
  
  protected class PositionKeyListener implements KeyListener
  {
    public void keyPressed( KeyEvent e )
    {
      int code = e.getKeyCode();
      if( code == KeyEvent.VK_UP)
      {
        //System.out.println("Up: keyPressed");
        Up();
        this_editor.firePropertyChange(DRAW_CURSOR,1,2);
      }
      
      if( code == KeyEvent.VK_DOWN)
      {
        //System.out.println("Dn");
        Down();
        this_editor.firePropertyChange(DRAW_CURSOR,1,2);
      }
      
      if( code == KeyEvent.VK_RIGHT)
      {
        //System.out.println("R");
        Right();
        this_editor.firePropertyChange(DRAW_CURSOR,1,2);
      }
      
      if( code == KeyEvent.VK_LEFT)
      {
        //System.out.println("L");
        Left();
        this_editor.firePropertyChange(DRAW_CURSOR,1,2);
      }
    }

    public void keyReleased(KeyEvent arg0)
    {
      // 
        
    }

    public void keyTyped(KeyEvent arg0)
    {
        
    }
  }
  
  private class comboBoxListener implements ActionListener
  {

    public void actionPerformed(ActionEvent e)
    {
      JComboBox comboBox = (JComboBox)e.getSource();
      for(int i = 0;i<opStrings.length;i++)
      {
        if(opStrings[i].equals(comboBox.getSelectedItem()))
        {
          comboBox.setSelectedIndex(i);
          operation = operations[i];
        }
      }
      
    }
        
  }
  
  protected class editWindowFocusListener implements WindowFocusListener
  {

    public void windowGainedFocus(WindowEvent arg0)
    {
      this_editor.firePropertyChange(DRAW_CURSOR, 1, 2);
      
    }

    public void windowLostFocus(WindowEvent arg0)
    {
      // TODO Auto-generated method stub
      
    }
    
  }
  
  //for testing
  /*public static void main(String[]args)
  {
    RegionOpEditFrame2 test = new RegionOpEditFrame2("");
    test.setVisible(true);
  }*/
}