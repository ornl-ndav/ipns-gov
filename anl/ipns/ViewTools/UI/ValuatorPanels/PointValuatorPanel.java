package gov.anl.ipns.ViewTools.UI.ValuatorPanels;

import gov.anl.ipns.Util.Numeric.floatPoint2D;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.*;

public class PointValuatorPanel extends ValuatorPanel
{
  private static int TEXT_FIELD_SIZE= 6;
  private static float VALUE_JUMP = 5;
  public static String POINT_VALUE_CHANGED = "Point Changed";
  
  private JTextField value1Field;
  private JTextField value2Field;
  private JRadioButton point;
  private ButtonGroup radioGroup;
  
  private String mainLabel;
  private String value1Label;
  private String value2Label;
  float val1;
  float val2;
  
  public PointValuatorPanel(String radioLabel, String name1, String name2,
                            float value1, float value2, ButtonGroup bg)
  {
    super();
    mainLabel = radioLabel;
    value1Label = name1;
    value2Label = name2;
    val1 = value1;
    val2 = value2;
    radioGroup = bg;
    buildMainPanel();
  }
  
  public floatPoint2D getPoint()
  {
    val1 = Float.parseFloat(value1Field.getText());
    val2 = Float.parseFloat(value2Field.getText());
    return new floatPoint2D(val1,val2);
  }
  
  public void setPoint(floatPoint2D newPoint)
  {
    val1 = newPoint.x;
    value1Field.setText(""+val1);
    val2 = newPoint.y;
    value2Field.setText(""+val2);
  }
  
  public void setEditable(boolean editable)
  {
    value1Field.setEditable(editable);
    value2Field.setEditable(editable);
  }
  
  public boolean isSelected()
  {
    return point.isSelected();
  }
  
  public void addToValue1(float addVal)
  {
    val1 = Float.parseFloat(value1Field.getText());
    val1 += addVal;
    value1Field.setText(""+val1);
    mainPanel.firePropertyChange(POINT_VALUE_CHANGED, 1, 2);
  }
  
  public void addToValue2(float addVal)
  {
    val2 = Float.parseFloat(value2Field.getText());
    val2 += addVal;
    value2Field.setText(""+val2);
    mainPanel.firePropertyChange(POINT_VALUE_CHANGED, 1, 2);
  }
  
  protected void buildMainPanel()
  {
    super.buildMainPanel();
    mainPanel.setLayout(new BorderLayout());
    mainPanel.setBorder(BorderFactory.createEtchedBorder());
    point = new JRadioButton(mainLabel);
    point.addKeyListener(new PositionKeyListener());
    radioGroup.add(point);
    JPanel CPanel = new JPanel();
    CPanel.setLayout(new BoxLayout(CPanel,BoxLayout.Y_AXIS));
    JLabel xLabel = new JLabel(value1Label);
    value1Field = new JTextField(TEXT_FIELD_SIZE);
    value1Field.setText(""+val1);
    value1Field.addActionListener(new textFieldListener());
    JLabel yLabel = new JLabel(value2Label);
    value2Field = new JTextField(TEXT_FIELD_SIZE);
    value2Field.setText(""+val2);
    value2Field.addActionListener(new textFieldListener());
    JPanel XPanel = new JPanel();
    XPanel.add(xLabel);
    XPanel.add(value1Field);
    CPanel.add(XPanel);
    
    JPanel YPanel = new JPanel();
    YPanel.add(yLabel);
    YPanel.add(value2Field);
    CPanel.add(YPanel);
    
    mainPanel.add(point,BorderLayout.NORTH);
    mainPanel.add(CPanel, BorderLayout.CENTER);
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
      
      if( source.equals(value1Field) )
      {
        value1Field.setText(numericText);
      }
      else if( source.equals(value2Field) )
      {
        value2Field.setText(numericText);
      }
      mainPanel.firePropertyChange(POINT_VALUE_CHANGED, 1, 2);
    }
  }
  
  private class PositionKeyListener implements KeyListener
  {
    public void keyPressed( KeyEvent e )
    {
      if( point.isSelected())
      {
        int code = e.getKeyCode();
        val1 = Float.parseFloat(value1Field.getText());
        val2 = Float.parseFloat(value2Field.getText());
        if( code == KeyEvent.VK_UP)
        {
          val2 += VALUE_JUMP;
          value2Field.setText(""+val2);
        }
      
        if( code == KeyEvent.VK_DOWN)
        {
          val2 -= VALUE_JUMP;
          value2Field.setText(""+val2);
        }
        
        if( code == KeyEvent.VK_RIGHT)
        {
          val1 += VALUE_JUMP;
          value1Field.setText(""+val1);
        }
      
        if( code == KeyEvent.VK_LEFT)
        {
          val1 -= VALUE_JUMP;
          value1Field.setText(""+val1);
        }
        
        mainPanel.firePropertyChange(POINT_VALUE_CHANGED, 1, 2);
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
    PointValuatorPanel pvpanel = 
      new PointValuatorPanel("SomePoint","X","Y", 
                200.5f, 250.5f, new ButtonGroup());
    test.getContentPane().add(pvpanel.getPanel());
    test.setVisible(true);
  }
  
}