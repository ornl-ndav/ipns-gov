package gov.anl.ipns.ViewTools.UI.ValuatorPanels;

import gov.anl.ipns.Util.Numeric.floatPoint2D;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.*;

public class RadiusValuatorPanel extends ValuatorPanel
{
  private static int TEXT_FIELD_SIZE= 6;
  private static float VALUE_JUMP = 5;
  public static String RADIUS_VALUE_CHANGED = "Radius Changed";
  
  private JTextField valueField;

  private JRadioButton radius;
  private ButtonGroup radioGroup;
  
  private String mainLabel;

  float val;
  
  public RadiusValuatorPanel(String radioLabel, float value, ButtonGroup bg)
  {
    super();
    mainLabel = radioLabel;
    val = value;
    radioGroup = bg;
    buildMainPanel();
  }
  
  public float getValue()
  {
   return val;
  }
  
  public void setValue(float newVal)
  {
    val = newVal;
    valueField.setText(""+val);
  }
  
  public boolean isSelected()
  {
    return radius.isSelected();
  }
  
  public void addToValue(float addVal)
  {
    val = Float.parseFloat(valueField.getText());
    val += addVal;
    valueField.setText(""+val);
    mainPanel.firePropertyChange(RADIUS_VALUE_CHANGED, 1, 2);
  }
  
  protected void buildMainPanel()
  {
    super.buildMainPanel();
    mainPanel.setBorder(BorderFactory.createEtchedBorder());
    radius = new JRadioButton(mainLabel);
    radius.addKeyListener(new PositionKeyListener());
    radioGroup.add(radius);
    
    valueField = new JTextField(TEXT_FIELD_SIZE);
    valueField.setText(""+val);
    valueField.addActionListener(new textFieldListener());
    
    mainPanel.add(radius);
    mainPanel.add(valueField);
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
      
      valueField.setText(numericText);
      mainPanel.firePropertyChange(RADIUS_VALUE_CHANGED, 1, 2);
    }
  }
  
  private class PositionKeyListener implements KeyListener
  {
    public void keyPressed( KeyEvent e )
    {
      if( radius.isSelected())
      {
        int code = e.getKeyCode();
        val = Float.parseFloat(valueField.getText());
        if( code == KeyEvent.VK_UP)
        {
          val += VALUE_JUMP;
          valueField.setText(""+val);
        }
      
        if( code == KeyEvent.VK_DOWN)
        {
          val -= VALUE_JUMP;
          valueField.setText(""+val);
        }
        
        if( code == KeyEvent.VK_RIGHT)
        {
          val += VALUE_JUMP;
          valueField.setText(""+val);
        }
      
        if( code == KeyEvent.VK_LEFT)
        {
          val -= VALUE_JUMP;
          valueField.setText(""+val);
        }
        
        mainPanel.firePropertyChange(RADIUS_VALUE_CHANGED, 1, 2);
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
  
  public static void main(String[]args)
  {
    JFrame test = new JFrame("Test");
    test.setSize(200, 150);
    RadiusValuatorPanel rvpanel = 
      new RadiusValuatorPanel("SomeRadius", 200.5f, new ButtonGroup());
    test.getContentPane().add(rvpanel.getPanel());
    test.setVisible(true);
  }
  
}