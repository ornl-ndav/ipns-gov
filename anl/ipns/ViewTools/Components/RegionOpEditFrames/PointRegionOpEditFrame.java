package gov.anl.ipns.ViewTools.Components.RegionOpEditFrames;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import gov.anl.ipns.Util.Numeric.floatPoint2D;
import gov.anl.ipns.ViewTools.Components.Cursor.BoxPanCursor;
import gov.anl.ipns.ViewTools.Components.Cursor.CursorTag;
import gov.anl.ipns.ViewTools.Components.Cursor.PointCursor;
import gov.anl.ipns.ViewTools.Components.Region.RegionOp;
import gov.anl.ipns.ViewTools.Components.Region.RegionOp.Operation;

import javax.swing.*;

/**
 * This class creates a Line Region Editor based on one defining points
 * in the world coordinate system
 * @author Josh Oakgrove
 *
 */
public class PointRegionOpEditFrame extends JFrame 
implements RegionOpEditFrame
{
  private static int VALUE_JUMP = 5;
  
  private JLabel point1;
  
  private JPanel movementPanel;
  private JPanel buttonPanel;
  private JPanel pointPanel;
  private JPanel positionPanel;
  private JPanel point1Panel;
  
  private JPanel LRPanel;
  
  private JTextField p1x = new JTextField(6);
  private JTextField p1y = new JTextField(6);

  private JFrame this_editor;
  private int regionIndex;
  private int pointIndex;
  private RegionOp.Operation operation;
  
  /**
   * constructor takes a single point in the World coordinate system
   * @param pt1 floatPoint2D in world coordinate system
   * @param op Operation to go with the Region
   * @param rIndex The index of the RegionOp with in the RegionOpList
   * @param pIndex The index of the point in the Point Region
   */
  public PointRegionOpEditFrame( floatPoint2D pt1,
      RegionOp.Operation op,int rIndex,int pIndex)
  {
      super("Point Editor");
      this.setBounds(700,390, 230, 180);
      this.setResizable(false);
      this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
      this_editor = this;

      Container contentPane = super.getContentPane();
      contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));

      p1x.setText(""+pt1.x);
      p1y.setText(""+pt1.y);
      
      buildMovementPanel();
      buildButtonPanel();
      contentPane.add(movementPanel);
      contentPane.add(buttonPanel);
      addKeyListener(new PositionKeyListener());

      regionIndex = rIndex;
      pointIndex = pIndex;
      operation = op;

      //set approprate values for draw
      //send Draw message
  }
  
  public int getRegionIndex()
  {
    return regionIndex;
  }
  
  public int getPointIndex()
  {
    return pointIndex;
  }

  public RegionOp.Operation getOp()
  {
    return operation;
  }

  public floatPoint2D[] getDefiningPoints()
  {
    floatPoint2D[] points = new floatPoint2D[1];
    points[0]=new floatPoint2D(Float.parseFloat(p1x.getText()),
      Float.parseFloat(p1y.getText()));
    
    return points;
  }

  public CursorTag getTypeCursor()
  {
    PointCursor cursor = null;
    return cursor;
  }

  private void buildMovementPanel()
  {
    movementPanel = new JPanel();
    movementPanel.setLayout(new BoxLayout(movementPanel,BoxLayout.X_AXIS));
    buildLRPanel();
    buildPointPanel();
    movementPanel.add(LRPanel);
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

  private void buildLRPanel()
  {
    LRPanel = new JPanel();
    LRPanel.setLayout(new BorderLayout());
    LRPanel.setBorder(BorderFactory.createEtchedBorder());
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
//  pointPanel.addKeyListener(new MoveKeyListener());
    buildPoint1Panel();
    pointPanel.add(point1Panel);
  }

  private void buildPoint1Panel()
  {
    point1Panel = new JPanel(new BorderLayout());
    point1Panel.setBorder(BorderFactory.createEtchedBorder());
//  point1Panel.addKeyListener(new MoveKeyListener());
    point1 = new JLabel("Point");
    point1.addKeyListener(new PositionKeyListener());
    JPanel CPanel = new JPanel();
    //CPanel.addKeyListener(new MoveKeyListener());
    JLabel xLabel = new JLabel("X");
    p1x.addActionListener(new textFieldListener());
    JLabel yLabel = new JLabel("Y");
    p1y.addActionListener(new textFieldListener());
    JPanel top = new JPanel();
    top.add(xLabel);
    top.add(p1x);
    JPanel bottom =new JPanel();
    bottom.add(yLabel);
    bottom.add(p1y);
    CPanel.add(top);
    CPanel.add(bottom);

    point1Panel.add(point1,BorderLayout.NORTH);
    point1Panel.add(CPanel, BorderLayout.CENTER);
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
      ////System.out.println("you changed the text.");
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
      if ( source.equals(p1x) )
      {
        //System.out.println("Point 1 x changed");
        p1x.setText(numericText);
        this_editor.firePropertyChange(DRAW_CURSOR,1,2);
      }

      else if(source.equals(p1y))
      {
        //System.out.println("Point 1 y changed");
        p1y.setText(numericText);
        this_editor.firePropertyChange(DRAW_CURSOR,1,2);
      }
    }


  }

  private class buttonListener implements ActionListener
  {
    public void actionPerformed(ActionEvent e)
    {
      String message = e.getActionCommand();
//  //System.out.println(message);
      if( message.equals("Up"))
      {
        //System.out.println("Up");
        Float y1 = Float.parseFloat(p1y.getText());
        y1+=VALUE_JUMP;
        p1y.setText(""+y1);
//  set approprate values for draw
        this_editor.firePropertyChange(DRAW_CURSOR,1,2);
      }

      else if( message.equals("R"))
      {
        //System.out.println("R");
        Float x1 = Float.parseFloat(p1x.getText());
        x1+=VALUE_JUMP;
        p1x.setText(""+x1);
//  set approprate values for draw
        this_editor.firePropertyChange(DRAW_CURSOR,1,2);
      }

      else if( message.equals("L"))
      {
        //System.out.println("L");
        Float x1 = Float.parseFloat(p1x.getText());
        x1-=VALUE_JUMP;
        p1x.setText(""+x1);
        //set approprate values for draw
        this_editor.firePropertyChange(DRAW_CURSOR,1,2);
      } 
      
      else if( message.equals("Dn"))
      {
        //System.out.println("Dn");
        Float y1 = Float.parseFloat(p1y.getText());
        y1-=VALUE_JUMP;
        p1y.setText(""+y1);
        //set approprate values for draw
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

  private class PositionKeyListener implements KeyListener
  {
    public void keyPressed( KeyEvent e )
    {

      //System.out.println("Pressed");
      int code = e.getKeyCode();

      if( code == KeyEvent.VK_UP)
      {
        //System.out.println("Up");
        Float y1 = Float.parseFloat(p1y.getText());
        y1+=VALUE_JUMP;
        p1y.setText(""+y1);
        //set approprate values for draw
        this_editor.firePropertyChange(DRAW_CURSOR,1,2);
      }

      if( code == KeyEvent.VK_DOWN)
      {
        //System.out.println("Dn");
        Float y1 = Float.parseFloat(p1y.getText());
        y1-=VALUE_JUMP;
        p1y.setText(""+y1);
//set approprate values for draw
        this_editor.firePropertyChange(DRAW_CURSOR,1,2);
      }

      if( code == KeyEvent.VK_RIGHT)
      {
        //System.out.println("R");
        Float x1 = Float.parseFloat(p1x.getText());
        x1+=VALUE_JUMP;
        p1x.setText(""+x1);
        //set approprate values for draw
        this_editor.firePropertyChange(DRAW_CURSOR,1,2);
      }

      if( code == KeyEvent.VK_LEFT)
      {
        //System.out.println("L");
        Float x1 = Float.parseFloat(p1x.getText());
        x1-=VALUE_JUMP;
        p1x.setText(""+x1);
//set approprate values for draw
        this_editor.firePropertyChange(DRAW_CURSOR,1,2);
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
    PointRegionOpEditFrame test = new PointRegionOpEditFrame();
    test.setVisible(true);
  }*/
 }