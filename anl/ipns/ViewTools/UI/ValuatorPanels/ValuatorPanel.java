package gov.anl.ipns.ViewTools.UI.ValuatorPanels;

import javax.swing.*;

public abstract class ValuatorPanel
{
  
  protected JPanel mainPanel;
  
  public ValuatorPanel()
  {

  }
  
  protected void buildMainPanel()
  {
    mainPanel = new JPanel();
    mainPanel.setBorder(BorderFactory.createEtchedBorder());
  }
  
  public JPanel getPanel()
  {
    return mainPanel;
  }
  
}
