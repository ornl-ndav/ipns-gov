package gov.anl.ipns.ViewTools.UI.ValuatorPanels;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class AngleValuatorPanel extends ValuatorPanel
{
  private static int TEXT_FIELD_SIZE= 4;
  public static String ANGLE_VALUE_CHANGED = "Angle Changed";
  
  private JSlider angleSlider;
  private JTextField angleField;
  
  private int start;
  private int end;
  private int current;
  private String labelName;
  
  public AngleValuatorPanel(String name, int startValue, 
                            int endValue, int currentValue)
  {
    super();
    labelName = name;
    start = startValue;
    end = endValue;
    current = currentValue;
    buildMainPanel();
  }
  
  public int getAngle()
  {
    return angleSlider.getValue();
  }
  
  public void setAngle(int newAng)
  {
    angleSlider.setValue(newAng);
  }
  
  protected void buildMainPanel()
  {
    super.buildMainPanel();
    angleSlider = new JSlider(start,end);
    angleSlider.setValue(current);
    angleSlider.setMajorTickSpacing(20);
    angleSlider.setPaintTicks(true);
    angleSlider.setValueIsAdjusting(false);
    mainPanel.setLayout(new BorderLayout());
    
    JPanel NPanel = new JPanel();
    JLabel angleLabel = new JLabel(labelName);
    angleField = new JTextField(TEXT_FIELD_SIZE);
    angleField.setText(""+current);
    angleField.addActionListener(new textFieldListener());
    NPanel.add(angleLabel);
    NPanel.add(angleField);
    angleSlider.addChangeListener(new sliderListener());


    mainPanel.add(NPanel,BorderLayout.NORTH);
    mainPanel.add(angleSlider,BorderLayout.CENTER);
  }
    
  private class sliderListener implements ChangeListener
  {

    public void stateChanged(ChangeEvent e)
    {
      angleField.setText(""+angleSlider.getValue());
      mainPanel.firePropertyChange(ANGLE_VALUE_CHANGED, 1, 2);
    }
    
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
      while (degrees>end)
        degrees -= (end-start);
      if( start<0 && isNegative)
        degrees = -degrees;
      
      angleField.setText(""+degrees);
      angleSlider.setValue(degrees);
      
      mainPanel.firePropertyChange(ANGLE_VALUE_CHANGED, 1, 2);
    }
  }
  
  public static void main(String[]args)
  {
    JFrame test = new JFrame("Test");
    test.setSize(200, 100);
    AngleValuatorPanel avpanel = new AngleValuatorPanel("SomeAngle",0,180,100);
    test.getContentPane().add(avpanel.getPanel());
    test.setVisible(true);
  }
  
}