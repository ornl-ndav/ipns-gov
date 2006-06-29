/*
 * File:  SampleDataSetPG.java
 *
 * Copyright (C) 2006, Ruth Mikkelson
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307, USA.
 *
 * Contact : Ruth Mikkelson <mikkelsonr@uwstout.edu>
 *           Department of Mathematics, Statistics and Computer Science
 *           University of Wisconsin-Stout
 *           Menomonie, WI 54751, USA
 *
 * This work was supported by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 *
 * Modified:
 *
 * $Log$
 * Revision 1.1  2006/06/29 22:18:22  rmikk
 * Initial checkin for SampleDataSetPG that only allows for SampleDataSet Choices
 *
 * Revision 1.2  2006/06/29 21:54:23  rmikk
 * Added or fixed the GPL
 *

 */
package gov.anl.ipns.Parameters;

import javax.swing.*;
import java.util.*;

import DataSetTools.dataset.Attribute;
import DataSetTools.dataset.DataSet;
import java.awt.*;


/**
 * 
 * Class to deal with lists of DataSets.
 * @author Ruth Mikkelson
 *
 */
public class SampleDataSetPG extends DataSetPG_base {

   Vector<DataSet> List= new Vector<DataSet>();
   JPanel  entryWidget =  null;
   JComboBox choices = null;
 
   
   /**
    * Constructor
    * @param name   The Prompt for this data set
    * @param val    An initial data set value
    * @throws IllegalArgumentException
    */
   public SampleDataSetPG( String name, Object val ) throws IllegalArgumentException {
      
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
      
      SampleDataSetPG res = new SampleDataSetPG( getName(), ds_value);
      
      for( int i=0; i < List.size(); i++)
         res.AddItem( List.elementAt( i ));
      
      return res;
      
   }

   
   // converts null data set to the empty data set and checks
   // that O is a data set
   private static DataSet ToDataSet( Object O){
      if( O == null)
         return  DataSetTools.dataset.DataSet.EMPTY_DATA_SET;
      if( isSampleDataSet( O))
         return (DataSet)O;
      throw new IllegalArgumentException("Initial value must be null "+
               "or a SampleDataSet in SampleDataSetPG");
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
      if( !isSampleDataSet( obj))
         throw new IllegalArgumentException( "Can only select from "+
                  "DataSets in DataSetPG");
      if( !List.contains( obj))
         List.addElement( (DataSet)obj);
      
   }
   
   /**
    * Checks that the given object is a DataSet with a SAMPLE_DATA as its
    * DS_TYPE Attribute.
    *
    * @param ds The Object to check against the above criteria.
    *
    * @return true if it is a DataSet.
    */
   private static boolean isSampleDataSet( Object ds ) {
     if( ds == null ) {
       return true;
     } else if( ds instanceof DataSet ) {
       String type = ( String )( ( DataSet )ds ).getAttributeValue( 
           Attribute.DS_TYPE );

       if( ( type == null ) || type.equals( Attribute.SAMPLE_DATA ) ) {
         return true;
       }
     }

     return false;
   }
}
