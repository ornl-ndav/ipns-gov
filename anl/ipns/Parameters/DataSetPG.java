/**
 * 
 */
package gov.anl.ipns.Parameters;

import javax.swing.*;
import java.util.*;
import DataSetTools.dataset.DataSet;
import java.awt.*;


/**
 * 
 * Class to deal with lists of DataSets.
 * @author Ruth Mikkelson
 *
 */
public class DataSetPG extends DataSetPG_base {

   Vector<DataSet> List= new Vector<DataSet>();
   JPanel  entryWidget =  null;
   JComboBox choices = null;
 
   
   /**
    * Constructor
    * @param name   The Prompt for this data set
    * @param val    An initial data set value
    * @throws IllegalArgumentException
    */
   public DataSetPG( String name, Object val ) throws IllegalArgumentException {
      super( name , ToDataSet(val) );
      List.addElement( ds_value );
    
   }


   /* 
    * @see gov.anl.ipns.Parameters.DataSetPG_base#getWidgetValue()
    */
   public DataSet getWidgetValue() throws IllegalArgumentException {
     if( entryWidget == null)throw
     new IllegalArgumentException(" GUI is not available to set values into");
     return (DataSet) choices.getSelectedItem();
      
   }


   /* 
    * @see gov.anl.ipns.Parameters.DataSetPG_base#setWidgetValue(DataSetTools.dataset.DataSet)
    */
  public void setWidgetValue( DataSet value )
            throws IllegalArgumentException {
     
      if( (entryWidget == null) || (choices == null) ) throw
         new IllegalArgumentException(" GUI is not available to set values into");
      
      if( !List.contains( value)) throw
      new IllegalArgumentException(" This DataSet is not one "+
               "of the choices in DataSetPG");
      
      choices.setSelectedItem( value );
         

   }


   /* 
    * @see gov.anl.ipns.Parameters.NewParameterGUI#getWidget()
    */
   public JPanel getWidget() {
      
      if( entryWidget != null)
         return entryWidget;
      
      entryWidget = new JPanel( new GridLayout( 1, 2));
      
      entryWidget.add( new JLabel( getName()));
      
      choices = new JComboBox( List );
      entryWidget.add( choices );
      choices.setEditable( false );
      choices.addActionListener( new PG_ActionListener( this ));
      choices.setSelectedIndex( 0 );
      
      return entryWidget;
      
   }
   


   /* 
    * @see gov.anl.ipns.Parameters.NewParameterGUI#destroyWidget()
    */
   public void destroyWidget() {
      
     entryWidget = null;
     choices = null;
     List.clear();
    
     ds_value = DataSet.EMPTY_DATA_SET;

   }


   /* 
    * @see gov.anl.ipns.Parameters.INewParameterGUI#setEnabled(boolean)
    */
   public void setEnabled( boolean on_off ) {
      
     if( entryWidget == null)
        return;
     entryWidget.setEnabled( on_off);
     entryWidget.getComponent( 0 ).setEnabled( on_off );
     choices.setEnabled( on_off );

   }


   /* 
    * @see gov.anl.ipns.Parameters.INewParameter#getCopy()
    */
   public Object getCopy() {
      
      DataSetPG res = new DataSetPG( getName(), ds_value);
      
      for( int i=0; i < List.size(); i++)
         res.AddItem( List.elementAt( i ));
      
      return res;
      
   }

   
   // converts null data set to the empty data set and checks
   // that O is a data set
   private static DataSet ToDataSet( Object O){
      if( O == null)
         return  DataSetTools.dataset.DataSet.EMPTY_DATA_SET;
      if( O instanceof DataSet)
         return (DataSet)O;
      throw new IllegalArgumentException("Initial value must be null "+
               "or a DataSet in DataSetPG");
   }
   
   
   /**
    * Adds the given object to the list of choices when the GUI
    * appears.  Note that no new elements can be added
    * after the entry widget is created.
    * 
    * @param obj   The new value to be added to the list of 
    *               choices
    *               
    * @throws IllegalArgumentException if the object is not
    *            a data set
    */
   public void AddItem( Object obj) throws IllegalArgumentException{
      
      if( entryWidget != null )
         return;
      
      if( obj == null)
         return;
      if( !(obj instanceof DataSet))
         throw new IllegalArgumentException( "Can only select from "+
                  "DataSets in DataSetPG");
      if( !List.contains( obj))
         List.addElement( (DataSet)obj);
      
   }
}
