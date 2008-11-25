package gov.anl.ipns.ViewTools.Components.ViewControls.ColorScaleControl;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.border.TitledBorder;

import gov.anl.ipns.ViewTools.Components.ViewControls.*;
import gov.anl.ipns.ViewTools.Panels.Image.*;

/**
 *  This class coordinates the color scale editor and an 
 *  ImageJPanel2 object.  When the user clicks the Update button
 *  on the editor, the corresponding color scale information will
 *  be passed on to the ImageJPanel2.
 */
public class ColorEditPanelManager extends ViewControl 
{
  ImageJPanel2    ijp;
  ColorEditPanel  color_editor;
  JFrame          frame;

  public static final String COLOR_SCALE_CHANGED = "Color Scale Changed";  

  public ColorEditPanelManager( String title, ImageJPanel2 ijp )
  {
    super( title );

    this.ijp = ijp;    

    JButton edit_button = new JButton("Edit Color Scale");    
    edit_button.addActionListener( new EditButtonListener() );
    this.setLayout( new GridLayout(1,1) );
    this.add( edit_button ); 

    color_editor = new ColorEditPanel( ijp.getDataMin(), ijp.getDataMax() );
    color_editor.addActionListener( new EditorListener() );
    frame = new JFrame("Color Scale Editor"); 
    frame.add( color_editor );
    frame.setBounds( 0, 0, 400, 500 );
    frame.setDefaultCloseOperation( WindowConstants.HIDE_ON_CLOSE );
  } 

  @Override
  public void setControlValue(Object value)
  {
  }

  @Override
  public Object getControlValue()
  {
    return null;
  }

  @Override
  public ViewControl copy()
  {
    return this;           // Why is the copy method required? 
                           // Is it OK to return a "shallow" copy?
  }


  private void doUpdate()
  {
    System.out.println("UPDATE IMAGE DISPLAY FROM THE COLOR EDITOR INFO");
    float min      = color_editor.getLocalMin(); 
    float max      = color_editor.getLocalMax(); 
    float prescale = color_editor.getPrescale();
    String cs_name = color_editor.getColorScale();
    byte[] table   = color_editor.getColorMapping();
    boolean isLog  = color_editor.getLogScale();

    boolean two_sided = false;  // NOTE: In the future, we could allow this.

    ijp.setNamedColorModel( cs_name, two_sided, false );

    ijp.changeColorIndexTable( table,
                               isLog,
                               min,
                               max,
                               true );
  }


  public class EditButtonListener implements ActionListener
  {
     public void actionPerformed( ActionEvent ae )
     {
        System.out.println("SHOW COLOR SCALE EDITOR");
        send_message( COLOR_SCALE_CHANGED );
        frame.setVisible( true );
     }
  } 

  public class EditorListener implements ActionListener
  {
     public void actionPerformed( ActionEvent ae )
     {
       String message = ae.getActionCommand();

       System.out.println("Editor Update Pushed");

       if ( message.equals( ColorEditPanel.doneMessage ) )
       {
         doUpdate();
         frame.setVisible(false);
       }
       else if ( message.equals( ColorEditPanel.updateMessage ) )
         doUpdate();
       else if ( message.equals( ColorEditPanel.cancelMessage ) )
         frame.setVisible(false);
     }
  }


}
