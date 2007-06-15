package gov.anl.ipns.ViewTools.Components.RegionOpEditFrames;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import gov.anl.ipns.Util.Numeric.floatPoint2D;
import gov.anl.ipns.ViewTools.Components.Cursor.CursorTag;
import gov.anl.ipns.ViewTools.Components.Cursor.EllipseCursor;
import gov.anl.ipns.ViewTools.Components.Region.RegionOp;
import gov.anl.ipns.ViewTools.Components.Region.RegionOp.Operation;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

/**
 * This class creates an Ellipse Region Editor based on two defining points
 * in the world coordinate system
 * @author Josh Oakgrove
 *
 */
public class EllipseRegionOpEditFrame extends JFrame implements RegionOpEditFrame
{

private static int VALUE_JUMP = 5;
  
  private boolean positionSelected = false;
  private boolean cornerPointSelected = false;
  private boolean centerPointSelected = false;
  
  private JRadioButton position;
  private JRadioButton corner;
  private JRadioButton center;
  private JPanel movementPanel;
  private JPanel buttonPanel;
  private JPanel pointPanel;
  private JPanel positionPanel;
  private JPanel point1Panel;
  private JPanel point2Panel;
  private JPanel LRPanel;
  
  private JTextField crx = new JTextField(6);
  private JTextField cry = new JTextField(6);
  private JTextField ctx = new JTextField(6);
  private JTextField cty = new JTextField(6);
  
  private ButtonGroup radioGroup = new ButtonGroup();
  private JFrame this_editor;
  private int regionIndex;
  private RegionOp.Operation operation;
  
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
    super("Ellipse Editor");
    this.setBounds(700,390, 295, 220);
    this.setResizable(false);
    this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    this_editor = this;
    
    Container contentPane = super.getContentPane();
    contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
    
    crx.setText(""+corn.x);
    cry.setText(""+corn.y);
    ctx.setText(""+cent.x);
    cty.setText(""+cent.y);
    buildMovementPanel();
    buildButtonPanel();
    contentPane.add(movementPanel);
    contentPane.add(buttonPanel);
    addKeyListener(new PositionKeyListener());
    
    regionIndex = index;
    operation = op;
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
    points[0]=new floatPoint2D(Float.parseFloat(crx.getText()),
                               Float.parseFloat(cry.getText()));
    points[2]=new floatPoint2D(Float.parseFloat(ctx.getText()),
                               Float.parseFloat(cty.getText()));
    points[1]=new floatPoint2D(points[0].x-2*(points[0].x-points[2].x),
                               points[0].y-2*(points[0].y-points[2].y));
    return points;
  }

  public Operation getOp()
  {
    return operation;
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
  
  private void buildMovementPanel()
  {
    movementPanel = new JPanel();
    movementPanel.setLayout(new BoxLayout(movementPanel,BoxLayout.X_AXIS));
    //movementPanel.addKeyListener(new MoveKeyListener());
    buildPositionPanel();
    buildPointPanel();
    movementPanel.add(positionPanel);
    movementPanel.add(pointPanel);
  }
  
  private void buildButtonPanel()
  {
    buttonPanel = new JPanel();
    buttonPanel.setBorder(BorderFactory.createEtchedBorder());
    buttonPanel.setLayout(new BoxLayout( buttonPanel,BoxLayout.X_AXIS ));
    //buttonPanel.addKeyListener(new MoveKeyListener());
    JPanel BPanel = new JPanel();
    
    JButton done = new JButton("Done");
    done.addActionListener(new buttonListener());
    JButton draw = new JButton("Draw");
    draw.addActionListener(new buttonListener());
    JButton cancel = new JButton("Cancel");
    cancel.addActionListener(new buttonListener());
    
    BPanel.add(done);
    BPanel.add(draw);
    BPanel.add(cancel);
    buttonPanel.add(BPanel);
  }
  
  private void buildPositionPanel()
  {
    positionPanel = new JPanel();
    positionPanel.setBorder(BorderFactory.createEtchedBorder());
    positionPanel.setLayout(new BoxLayout(positionPanel,BoxLayout.Y_AXIS));
    //positionPanel.addKeyListener(new MoveKeyListener());
    position = new JRadioButton("Position");
    position.addActionListener(new buttonListener());
    position.addKeyListener(new PositionKeyListener());
    radioGroup.add(position);
    JPanel PPanel = new JPanel();
    PPanel.add(position);
    
    buildLRPanel();
    
    //listeners
    positionPanel.add(PPanel);
    positionPanel.add(LRPanel);
  }
  
  private void buildLRPanel()
  {
    LRPanel = new JPanel();
    LRPanel.setLayout(new BorderLayout());
    //LRPanel.addKeyListener(new MoveKeyListener());
    JButton up = new JButton("Up");
    JButton left = new JButton("L");
    JButton right = new JButton("R");
    JButton down = new JButton("Dn");
    //listeners
    up.addActionListener(new buttonListener());
    left.addActionListener(new buttonListener());
    right.addActionListener(new buttonListener());
    down.addActionListener(new buttonListener());
    up.addKeyListener(new PositionKeyListener());
    left.addKeyListener(new PositionKeyListener());
    right.addKeyListener(new PositionKeyListener());
    down.addKeyListener(new PositionKeyListener());
    JPanel NPanel = new JPanel();
    NPanel.add(up);
    LRPanel.add(NPanel,BorderLayout.NORTH);
    JPanel LPanel = new JPanel();
    LPanel.add(left);
    LRPanel.add(LPanel,BorderLayout.WEST);
    JPanel RPanel = new JPanel();
    RPanel.add(right);
    LRPanel.add(RPanel,BorderLayout.EAST);
    JPanel SPanel = new JPanel();
    SPanel.add(down);
    LRPanel.add(SPanel,BorderLayout.SOUTH);
  }
  
  private void buildPointPanel()
  {
    pointPanel = new JPanel();
    pointPanel.setLayout(new BoxLayout(pointPanel,BoxLayout.Y_AXIS));
    //pointPanel.addKeyListener(new MoveKeyListener());
    buildPoint1Panel();
    buildPoint2Panel();
    pointPanel.add(point1Panel);
    pointPanel.add(point2Panel);
  }
  
  private void buildPoint1Panel()
  {
    point1Panel = new JPanel(new BorderLayout());
    point1Panel.setBorder(BorderFactory.createEtchedBorder());
    //point1Panel.addKeyListener(new MoveKeyListener());
    corner = new JRadioButton("Corner Point");
    corner.addActionListener(new buttonListener());
    corner.addKeyListener(new PositionKeyListener());
    radioGroup.add(corner);
    JPanel CPanel = new JPanel();
    //CPanel.addKeyListener(new MoveKeyListener());
    JLabel xLabel = new JLabel("X");
    crx.addActionListener(new textFieldListener());
    JLabel yLabel = new JLabel("Y");
    cry.addActionListener(new textFieldListener());
    CPanel.add(xLabel);
    CPanel.add(crx);
    CPanel.add(yLabel);
    CPanel.add(cry);
    
    point1Panel.add(corner,BorderLayout.NORTH);
    point1Panel.add(CPanel, BorderLayout.CENTER);
  }
  
  private void buildPoint2Panel()
  {
    point2Panel = new JPanel(new BorderLayout());
    point2Panel.setBorder(BorderFactory.createEtchedBorder());
    //point2Panel.addKeyListener(new MoveKeyListener());
    center = new JRadioButton("Center Point");
    center.addKeyListener(new PositionKeyListener());
    center.addActionListener(new buttonListener());
    radioGroup.add(center);
    JPanel CPanel = new JPanel();
    JLabel xLabel = new JLabel("X");
    //CPanel.addKeyListener(new MoveKeyListener());
    ctx.addActionListener(new textFieldListener());
    JLabel yLabel = new JLabel("Y");
    cty.addActionListener(new textFieldListener());
    //add to listeners
    CPanel.add(xLabel);
    CPanel.add(ctx);
    CPanel.add(yLabel);
    CPanel.add(cty);
    
    point2Panel.add(center,BorderLayout.NORTH);
    point2Panel.add(CPanel, BorderLayout.CENTER);
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
      if ( source.equals(crx) )
      {
        //System.out.println("Point 1 x changed");
        corner.setSelected(true);
        crx.setText(numericText);
        this_editor.firePropertyChange(DRAW_CURSOR,1,2);
      }
      
      else if(source.equals(cry))
      {
        //System.out.println("Point 1 y changed");
        corner.setSelected(true);
        cry.setText(numericText);
        this_editor.firePropertyChange(DRAW_CURSOR,1,2);
      }
      
      else if(source.equals(ctx))
      {
        //System.out.println("Point 2 x changed");
        center.setSelected(true);
        ctx.setText(numericText);
        this_editor.firePropertyChange(DRAW_CURSOR,1,2);
      }
      
      else if(source.equals(cty))
      {
        //System.out.println("Point 2 y changed");
        center.setSelected(true);
        cty.setText(numericText);
        this_editor.firePropertyChange(DRAW_CURSOR,1,2);
      }
      
    }
    
    
  }
  
  private class buttonListener implements ActionListener
  {
    public void actionPerformed(ActionEvent e)
    {
      String message = e.getActionCommand();
      ////System.out.println(message);
      if( message.equals("Up"))
      {
        //System.out.println("Up");
        Float y1 = Float.parseFloat(cry.getText());
        y1+=VALUE_JUMP;
        Float y2 = Float.parseFloat(cry.getText());
        y2+=VALUE_JUMP;
        cry.setText(""+y1);
        cty.setText(""+y2);
//      set approprate values for draw
        this_editor.firePropertyChange(DRAW_CURSOR,1,2);
        position.doClick();
      }
      
      else if( message.equals("R"))
      {
        //System.out.println("R");
        Float x1 = Float.parseFloat(crx.getText());
        x1+=VALUE_JUMP;
        Float x2 = Float.parseFloat(ctx.getText());
        x2+=VALUE_JUMP;
        crx.setText(""+x1);
        ctx.setText(""+x2);
//      set approprate values for draw
        this_editor.firePropertyChange(DRAW_CURSOR,1,2);
        position.doClick();
      }
      
      else if( message.equals("L"))
      {
        //System.out.println("L");
        Float x1 = Float.parseFloat(crx.getText());
        x1-=VALUE_JUMP;
        Float x2 = Float.parseFloat(ctx.getText());
        x2-=VALUE_JUMP;
        crx.setText(""+x1);
        ctx.setText(""+x2);
//      set approprate values for draw
        this_editor.firePropertyChange(DRAW_CURSOR,1,2);
        position.doClick();
      }
      
      else if( message.equals("Dn"))
      {
        //System.out.println("Dn");
        Float y1 = Float.parseFloat(cry.getText());
        y1-=VALUE_JUMP;
        Float y2 = Float.parseFloat(cty.getText());
        y2-=VALUE_JUMP;
        cry.setText(""+y1);
        cty.setText(""+y2);
//      set approprate values for draw
        this_editor.firePropertyChange(DRAW_CURSOR,1,2);
        position.doClick();
      }
      
      else if( message.equals("Draw"))
      {
        //System.out.println("Draw");
        this_editor.firePropertyChange(DRAW_CURSOR,1,2);
        this_editor.firePropertyChange(DRAW_REGION,1,2);
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
      
      else if(message.equals("Position"))
      {
        positionSelected = true;
        cornerPointSelected = false;
        centerPointSelected = false;
        //System.out.println("positonSel");
      }
      
      else if(message.equals("Corner Point"))
      {
        positionSelected = false;
        cornerPointSelected = true;
        centerPointSelected = false;
        //System.out.println("point1Sel");
      }
      
      else if(message.equals("Center Point"))
      {
        positionSelected = false;
        cornerPointSelected = false;
        centerPointSelected = true;
        //System.out.println("point2Sel");
      }
    }
  }
  
  private class PositionKeyListener implements KeyListener
  {
    public void keyPressed( KeyEvent e )
    {
      
      //System.out.println("Pressed");
      int code = e.getKeyCode();
      
      if(positionSelected)
      {
        if( code == KeyEvent.VK_UP)
        {
          //System.out.println("Up");
          Float y1 = Float.parseFloat(cry.getText());
          y1+=VALUE_JUMP;
          Float y2 = Float.parseFloat(cty.getText());
          y2+=VALUE_JUMP;
          cry.setText(""+y1);
          cty.setText(""+y2);
//        set approprate values for draw
          this_editor.firePropertyChange(DRAW_CURSOR,1,2);
          position.setSelected(true);
        }
      
        if( code == KeyEvent.VK_DOWN)
        {
          //System.out.println("Dn");
          Float y1 = Float.parseFloat(cry.getText());
          y1-=VALUE_JUMP;
          Float y2 = Float.parseFloat(cty.getText());
          y2-=VALUE_JUMP;
          cry.setText(""+y1);
          cty.setText(""+y2);
//        set approprate values for draw
          this_editor.firePropertyChange(DRAW_CURSOR,1,2);
          position.setSelected(true);
        }
      
        if( code == KeyEvent.VK_RIGHT)
        {
          //System.out.println("R");
          Float x1 = Float.parseFloat(crx.getText());
          x1+=VALUE_JUMP;
          Float x2 = Float.parseFloat(ctx.getText());
          x2+=VALUE_JUMP;
          crx.setText(""+x1);
          ctx.setText(""+x2);
//        set approprate values for draw
          this_editor.firePropertyChange(DRAW_CURSOR,1,2);
          position.setSelected(true);
        }
      
        if( code == KeyEvent.VK_LEFT)
        {
          //System.out.println("L");
          Float x1 = Float.parseFloat(crx.getText());
          x1-=VALUE_JUMP;
          Float x2 = Float.parseFloat(ctx.getText());
          x2-=VALUE_JUMP;
          crx.setText(""+x1);
          ctx.setText(""+x2);
//        set approprate values for draw
          this_editor.firePropertyChange(DRAW_CURSOR,1,2);
          position.setSelected(true);
        }
      }
      
      else if( cornerPointSelected )
      {
        //change X,Y values of point1 for direction key pressed
        if( code == KeyEvent.VK_UP)
        {
          //System.out.println("Up");
          Float y1 = Float.parseFloat(cry.getText());
          y1+=VALUE_JUMP;
          cry.setText(""+y1);
//        set approprate values for draw
          this_editor.firePropertyChange(DRAW_CURSOR,1,2);
        }
      
        if( code == KeyEvent.VK_DOWN)
        {
          //System.out.println("Dn");
          Float y1 = Float.parseFloat(cry.getText());
          y1-=VALUE_JUMP;
          cry.setText(""+y1);
//        set approprate values for draw
          this_editor.firePropertyChange(DRAW_CURSOR,1,2);
        }
      
        if( code == KeyEvent.VK_RIGHT)
        {
          //System.out.println("R");
          Float x1 = Float.parseFloat(crx.getText());
          x1+=VALUE_JUMP;
          crx.setText(""+x1);
//        set approprate values for draw
          this_editor.firePropertyChange(DRAW_CURSOR,1,2);
        }
      
        if( code == KeyEvent.VK_LEFT)
        {
          //System.out.println("L");
          Float x1 = Float.parseFloat(crx.getText());
          x1-=VALUE_JUMP;
          crx.setText(""+x1);
//        set approprate values for draw
          this_editor.firePropertyChange(DRAW_CURSOR,1,2);
        }
      }
      
      else if( centerPointSelected )
      {
        if( code == KeyEvent.VK_UP)
        {
          //System.out.println("Up");
          Float y2 = Float.parseFloat(cty.getText());
          y2+=VALUE_JUMP;
          cty.setText(""+y2);
//        set approprate values for draw
          this_editor.firePropertyChange(DRAW_CURSOR,1,2);
        }
      
        if( code == KeyEvent.VK_DOWN)
        {
          //System.out.println("Dn");
          Float y2 = Float.parseFloat(cty.getText());
          y2-=VALUE_JUMP;
          cty.setText(""+y2);
//        set approprate values for draw
          this_editor.firePropertyChange(DRAW_CURSOR,1,2);
        }
      
        if( code == KeyEvent.VK_RIGHT)
        {
          //System.out.println("R");
          Float x2 = Float.parseFloat(ctx.getText());
          x2+=VALUE_JUMP;
          ctx.setText(""+x2);
//        set approprate values for draw
          this_editor.firePropertyChange(DRAW_CURSOR,1,2);
        }
      
        if( code == KeyEvent.VK_LEFT)
        {
          //System.out.println("L");
          Float x2 = Float.parseFloat(ctx.getText());
          x2-=VALUE_JUMP;
          ctx.setText(""+x2);
//        set approprate values for draw
          this_editor.firePropertyChange(DRAW_CURSOR,1,2);
        }
      }
    }


    public void keyReleased(KeyEvent e)
    {
      //System.out.println("Released");
      
    }

    public void keyTyped(KeyEvent e)
    {
      //System.out.println("Typed");
      
    }
  }
  
  /*public static void main(String[]args)
  {
    EllipseRegionOpEditFrame test = new EllipseRegionOpEditFrame();
    test.setVisible(true);
  }*/
}