package gov.anl.ipns.Parameters;

import java.awt.GridLayout;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.JLabel;
import javax.swing.JPanel;


public class LongPG extends ParamUsesString
{

   long  value;
   private FilteredPG_TextField text_field = null;

   private JPanel     panel   = null;
   private JLabel     label   = null;
   private boolean    enabled = true; 
   
   public LongPG( String name, Object val)
   {
      super( name, true);
      setValue( val );
   }
   @Override
   public Object clone()
   {

      LongPG copy = new LongPG( getName(), new Long( value ) );
      copy.setValidFlag( getValidFlag() );
      return copy;
   }

   public long getlongValue()
   {
      return value;
   }
   @Override
   protected void destroyWidget()
   {
      panel      = null;                     // null out all references to gui 
      text_field = null;                     // components, so that they can be
      label      = null;  
   }

   @Override
   protected JPanel getWidget()
   {
      if( panel == null )                // make new panel with label & TextField 
      {
        panel      = new JPanel( new GridLayout( 1, 2 ) );
        text_field = new FilteredPG_TextField( this, new IntegerFilter() );
        label      = new JLabel( getName() );

        panel.add( label );
        panel.add( text_field );
   
        text_field.addActionListener( new PG_ActionListener( this ) );
      }

      setEnabled( enabled );                 // set widget state from
      setWidgetValue( value );           // current information

      return panel;
   }

   @Override
   public void clear()
   {


      setValue( new Long( 0 ) );

   }

   @Override
   public String getStringValue() throws IllegalArgumentException
   {
      getValue();                    // this will synchronize the int_value with
      // the GUI widget value and throw an 
      // exception if the GUI widget value is bad

      return new Long( value ).toString(); 
   }

   @Override
   public boolean hasChanged()
   {

      if ( !hasGUI() )                        // GUI can't change if it's
         return false;                         // not there!

       try
       {
         long gui_value = getWidgetValue();
       
         if ( gui_value == value )         // no change in value
           return false;

         setValidFlag(false);                  // GUI val doesn't match old val
         return true;
       }
       catch ( Exception exception )
       {
         setValidFlag( false );                // illegal value entered by user
         return true;                          // is considered a change
       }
   }

   @Override
   public void setEnabled(boolean on_off)
   {
      enabled = on_off;
      if ( panel != null )                // panel, box and label are created and
      {                                   // destroyed together, so we can just 
        text_field.setEnabled( on_off );  // check that the panel is there
        label.setEnabled( on_off );
      }
   }

   @Override
   public Object getValue()
   {
      if( hasGUI() )                        // NOTE: getWidgetValue() may throw
        value = getWidgetValue();       //       an IllegalArgumentException 
                                             //       if the widget value does 
                                             //       not represent an integer 
       return new Long( value );
     }
 

   @Override
   public void setValue(Object val)
   {

      try
      {
        if( val != null)
           if( val.getClass( ).isArray( ))
             if( java.lang.reflect.Array.getLength( val )>0)
                val = java.lang.reflect.Array.get( val , 0 );
             else 
                val =new Long(0);
           else if( val instanceof Collection)
           {
              Iterator itr = ((Collection)val).iterator( );
             if( itr.hasNext())
                val = itr.next( );
             else
                val = new Long(0);
           }
         
        if( val == null)
           value = 0;
        else if( val instanceof Number)
           value = ((Number)val).longValue( );
        else if( val instanceof String )
           value = Long.parseLong( ((String)val).trim() );
        else
           value = 0;
      }catch(Exception s)
      {
         value =0;
      }
      if( hasGUI())
         setWidgetValue( value );

   }
   /**
    * Sets the value displayed in the JTextField to the specified value.
    *
    * @param value  The integer value to record in the JTextField.
    *
    * @throws IllegalArgumentException is thrown if this is called without
    *         a GUI widget being present.
    */
   protected void setWidgetValue( long value ) throws IllegalArgumentException
   {
     if ( text_field == null )
       throw new IllegalArgumentException(
               "setWidgetValue() called when no LongPG widget exists");

     text_field.setText( ""+value );
   }

   /**
    * Retrieves the JTextField's current value.  
    * 
    * @return The integer value from the JTextField, if possible.
    *
    * @throws IllegalArgumentException is thrown if this is called without
    *         a GUI widget being present, or if the value is invalid.
    */
   protected long getWidgetValue() throws IllegalArgumentException
   {
     long widget_value = 0;

     if ( text_field == null )
       throw new IllegalArgumentException(
               "getWidgetValue() called when no LongPG widget exists");

     else
     {
        try
        {
           long val = value;
           
            setValue(text_field.getText()) ;
            widget_value = value;
            value = val;
        }catch( Exception s)
        {
           return (long)0;
        }
     }

     return widget_value;
   }
   
  
   /**
    * @param args
    */
   public static void main(String[] args)
   {

      // TODO Auto-generated method stub

   }

}
