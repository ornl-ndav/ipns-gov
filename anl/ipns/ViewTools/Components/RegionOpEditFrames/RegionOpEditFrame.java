package gov.anl.ipns.ViewTools.Components.RegionOpEditFrames;


import gov.anl.ipns.Util.Numeric.floatPoint2D;
import gov.anl.ipns.ViewTools.Components.Cursor.CursorTag;
import gov.anl.ipns.ViewTools.Components.Region.RegionOp;
import gov.anl.ipns.ViewTools.Components.Region.RegionOp.Operation;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

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
        this_editor.firePropertyChange(DRAW_CURSOR,1,2);
        this_editor.firePropertyChange(DRAW_REGION,1,2);
        //System.out.println("after fire");
      }
      
      else if( message.equals("Done"))
      {
        //System.out.println("Done");
        this_editor.firePropertyChange(DRAW_CURSOR,1,2);
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
  
  //for testing
  /*public static void main(String[]args)
  {
    RegionOpEditFrame2 test = new RegionOpEditFrame2("");
    test.setVisible(true);
  }*/
}