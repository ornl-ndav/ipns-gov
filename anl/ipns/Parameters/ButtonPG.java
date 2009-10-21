package gov.anl.ipns.Parameters;

import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.lang.ref.WeakReference;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import gov.anl.ipns.Util.Messaging.IObservable;



/**
 * This parameterGUI is just a button that has listeners.  When pressed the listeners are invoked
 * It can be used to pop up a larger dialog box or show information available to the listeners.
 * @author Ruth
 *
 */
public class ButtonPG extends ParameterGUI implements IParameter ,
         IParameterGUI , IObservable
{
   String Text;
   Vector<WeakReference<ActionListener>> listeners;
   JButton button = null;
   JPanel panel;
   public ButtonPG( String Text)
   {
      super(Text, false);
      this.Text = Text;
      listeners = new  Vector<WeakReference<ActionListener>> ();
   }
   
   public void addActionListener( ActionListener act)
   {
      if( act == null)
         return;
      for( int i=0;i< listeners.size(); i++)
         if( listeners.elementAt(i)!= null)
      {
         ActionListener e = listeners.elementAt( i ).get(); 
         if( e != null && e == act)
            return;
      }
      listeners.addElement(  new WeakReference<ActionListener>(act) );
      if( button!= null)
         button.addActionListener(  act );
   }
   
   public void removeActionListener( ActionListener act)
   {
      if( act == null)
      return;
        for( int i=0;i< listeners.size(); i++)
      if( listeners.elementAt(i)!= null)
   {
      ActionListener e = listeners.elementAt( i ).get(); 
      if( e != null && e == act)
         {
          listeners.remove( i );
          if( button != null)
             button.removeActionListener(  e );
          return;
         }
   }
      
   }
   
   public void removeAllActionListeners()
   {
      listeners.clear();
   }
   @Override
   public Object clone()
   {

      ButtonPG BPG = new ButtonPG( Text);
      for( int i=0; i< listeners.size(); i++)
         BPG.addActionListener(  listeners.elementAt(i).get() );
      if( panel != null || button != null)
         BPG.getWidget();
      return BPG;
   }


   @Override
   protected void destroyWidget()
   {
      if( panel != null)
      {
         panel.removeAll();
         panel = null;
      }
     if( button != null)
     {
       for( int i=0; i< listeners.size(); i++)
          button.removeActionListener(  listeners.elementAt(i).get() );
       button.removeAll();
       button = null;
     }
   

   }


   @Override
   protected JPanel getWidget()
   {

      if( panel != null)
         return panel;
      button = new JButton( Text);
      for( int i=0; i< listeners.size(); i++)
         button.addActionListener( listeners.elementAt( i ).get());
     panel = new JPanel();
     panel.setLayout( new GridLayout(1,2) );
     panel.add( button);
     panel.add( new JLabel());
     return panel;
   }


   @Override
   public Object getValue()
   {

      // TODO Auto-generated method stub
      return "";
   }


   @Override
   public void setValue( Object value )
   {

   

   }


   @Override
   public void clear()
   {

 

   }


   @Override
   public String getStringValue() throws IllegalArgumentException
   {

      // TODO Auto-generated method stub
      return "";
   }


   @Override
   public boolean hasChanged()
   {

      // TODO Auto-generated method stub
      return false;
   }


   @Override
   public void setEnabled( boolean on_off )
   {

  button.setEnabled( on_off );

   }

}
