package gov.anl.ipns.ViewTools.Panels;

import javax.swing.*;
import javax.swing.event.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StringListChoiceViewer extends JPanel  implements ChangeListener,
                                                               ActionListener
{

   String[] Choices;
   JTextArea text;
   JSpinner  spinner;
   int  selected;
  
   public StringListChoiceViewer( String[] Choices, int nrows, int ncols)
   {
      super();
      setLayout( new BorderLayout());
      this.Choices = Choices;
      text = new JTextArea( nrows, ncols);
      spinner = new JSpinner( new SpinnerNumberModel(1,1, Choices.length,1));
      spinner.addChangeListener( this );
      text.setText( Choices[0] );
      selected = -1;
      JButton select = new JButton("Select");
      select.addActionListener( this);
      //-------------------------------------
      JPanel panel = new JPanel();
      BoxLayout bl = new BoxLayout( panel, BoxLayout.X_AXIS);
      panel.setLayout( bl );
      panel.add(  Box.createHorizontalGlue( ) );
      panel.add(  select );
      panel.add(  new JLabel("Choice", SwingConstants.RIGHT) );
      panel.add( spinner);
      add( panel, BorderLayout.NORTH );
      //-------------------------------
      add( new JScrollPane(text), BorderLayout.CENTER );
   }
   
   public void setNewStringList( String[] Choices)
   {
      this.Choices = Choices;
      spinner.setModel( new SpinnerNumberModel(1,1, Choices.length,1));
      text.setText( Choices[0] );
      selected = -1;
   }
   
   public int getSelectedChoice()
   {
      return selected;
   }
   
   public int getLastViewedChoice()
   {
      return ((Number)spinner.getValue()).intValue()-1;
   }
   

  
   @Override
   public void actionPerformed(ActionEvent arg0)
   {

      selected = ((Integer)spinner.getValue()).intValue()-1;
      
   }

   @Override
   public void stateChanged(ChangeEvent arg0)
   {

      int show =((Integer)spinner.getValue()).intValue()-1;
      text.setText(  Choices[show] );
      
   }

   /**
    * @param args
    */
   public static void main(String[] args)
   {
     String[] choices={"abc","def","ghi","jkl"};
     StringListChoiceViewer L = new StringListChoiceViewer( choices, 5,12);
     
     //JOptionPane.showMessageDialog( null , L );
     //System.out.println( L.getSelectedChoice()+","+L.getLastViewedChoice());
     JFrame jf = new JFrame();
     jf.getContentPane().setLayout(  new GridLayout(1,1) );
     jf.getContentPane().add(  L );
     jf.setSize( 300,500);
     jf.setVisible(  true );

   }

}
