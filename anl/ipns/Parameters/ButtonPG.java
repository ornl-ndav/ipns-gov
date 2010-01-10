package gov.anl.ipns.Parameters;

import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.lang.ref.WeakReference;
import java.util.Vector;

import javax.swing.*;

//import gov.anl.ipns.Util.Messaging.IObservable;



/**
 * This parameterGUI is just a button that has listeners.  When pressed the listeners are invoked
 * It can be used to pop up a larger dialog box or show information available to the listeners.
 * If a listener also implements IReturnValue a value can be returned via the setValue method.
 *  The return value is an Object 
 * @author Ruth
 *
 */
public class ButtonPG extends ParameterGUI implements IParameter ,
         IParameterGUI //, IObservable
{
   
   private static String NULL = "(null)";
   String Text;
   Vector<WeakReference<ActionListener>> listeners;
   JButton button = null;
   JPanel panel;
   boolean retValue = false;//If true a spot for result on GUI is displayed
   
   Object Value = null;
   JTextArea OutPut;//Spot for returned value
   public ButtonPG( String Text)
   {
      super(Text, false);
      this.Text = Text;
      OutPut = null;
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
      if( act instanceof IReturnValue)
         {
            retValue = true;
            ((IReturnValue)act).setRecipient( this);
         }
      if( button!= null)
         button.addActionListener(  act );
   }
   
   public void removeActionListener( ActionListener act)
   {
      if( act == null)
      return;
        for( int i= listeners.size()-1;i>=0; i--)
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
     int n=2;
     if( retValue ) n++;
     panel.setLayout( new GridLayout(1,n) );
     panel.add( button);
     if( n>2 )
     {
        if( OutPut == null)
           OutPut= new JTextArea( NULL);
        panel.add( OutPut );
     }else
        OutPut = null;
     panel.add( new JLabel());
     return panel;
   }


   @Override
   public Object getValue()
   {
       
      if( OutPut == null ||!retValue )
         return null;
      if( OutPut.getText() == NULL)
      {
         Value = null;
      }
      if(Value != null)
         return Value;
      return new Object();
   }


   @Override
   public void setValue( Object value )
   {

      Value = value;
      if( retValue)
         if( value != null)
            OutPut.setText( value.toString() );
         else
            OutPut.setText( NULL );
      

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
