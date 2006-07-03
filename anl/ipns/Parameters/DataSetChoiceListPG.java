/*
 * File:  DataSetChoiceListPG.java
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
 * Modified:
 *
 * $Log$
 * Revision 1.1  2006/07/03 23:14:50  dennis
 * Factored out and rearranged code from DataSetPG, so that this could
 * serve as an abstract base class for all PG's that use a combo box
 * to choose from a list of DataSets of some type.
 *
 * Revision 1.2  2006/06/29 21:54:23  rmikk
 * Added or fixed the GPL
 *
 * Revision 1.1  2006/06/29 21:49:23  rmikk
 * Initial checkin for DataSetPG that allows a user to select from a drop down
 * combo of datasets
 */
package gov.anl.ipns.Parameters;

import javax.swing.*;
import java.util.*;
import DataSetTools.dataset.Attribute;
import DataSetTools.dataset.DataSet;
import java.awt.*;

/**
 * 
 * Class to deal with lists of DataSets of any type
 * @author Ruth Mikkelson
 *
 */
public abstract class DataSetChoiceListPG extends DataSetPG_base {

   protected Vector<DataSet> ds_list     = new Vector<DataSet>();
   private   JPanel          entryWidget = null;
   private   JComboBox       choices     = null;
   private   String          my_type     = null;

   /**
    * Constructor
    *
    * @param name   The Prompt for this data set
    * @param val    An initial data set value
    * @param type   String giving the type of the DataSet,
    *               in the form listed in DataSetTools.dataset.Attribute,
    *               or null to accept any type of DataSet.
    *  
    * @throws IllegalArgumentException
    */
   public DataSetChoiceListPG( String name, Object val, String type ) 
                                  throws IllegalArgumentException {
      super( name, null );
      my_type = type;

      ds_value = ToDataSet(val);   // this will throw an exception if
                                   // the type of the DataSet is wrong
      ds_list.addElement( ds_value );
   }


   /** 
    * @see gov.anl.ipns.Parameters.DataSetPG_base#getWidgetValue()
    */
   public DataSet getWidgetValue() throws IllegalArgumentException {

     if( entryWidget == null) throw
       new IllegalArgumentException("GUI is not available in " +
                                     this.getClass() + ".getWidgetValue()" );
     
     return (DataSet)choices.getSelectedItem();
   }


   /** 
    * @see gov.anl.ipns.Parameters.DataSetPG_base#setWidgetValue(DataSetTools.dataset.DataSet)
    */
  public void setWidgetValue( DataSet value ) throws IllegalArgumentException {
     
      if( (entryWidget == null) || (choices == null) ) throw
        new IllegalArgumentException("GUI is not available in " +
                                       this.getClass() + ".setWidgetValue()" );
      
      if( !ds_list.contains( value ) ) throw
        new IllegalArgumentException("DataSet not one of the choices in " + 
                                       this.getClass() + ".setWidgetValue()" );
      
      choices.setSelectedItem( value );
   }


   /** 
    * @see gov.anl.ipns.Parameters.NewParameterGUI#getWidget()
    */
   public JPanel getWidget() {
      
      if( entryWidget != null)
         return entryWidget;
      
      entryWidget = new JPanel( new GridLayout( 1, 2 ) );
      
      entryWidget.add( new JLabel( getName() ) );
      
      choices = new JComboBox( ds_list );
      entryWidget.add( choices );
      choices.setEditable( false );
      choices.addActionListener( new PG_ActionListener( this ));
      choices.setSelectedIndex( 0 );
      
      return entryWidget;
   }
   

   /** 
    * @see gov.anl.ipns.Parameters.NewParameterGUI#destroyWidget()
    */
   public void destroyWidget() {
      
     entryWidget = null;
     choices = null;
     ds_list.clear();
    
     ds_value = DataSet.EMPTY_DATA_SET;
   }


   /**
    * @see gov.anl.ipns.Parameters.INewParameterGUI#setEnabled(boolean)
    */
   public void setEnabled( boolean on_off ) {
      
     if( entryWidget == null )
        return;

     entryWidget.setEnabled( on_off );
     entryWidget.getComponent( 0 ).setEnabled( on_off );
     choices.setEnabled( on_off );
   }


   /*
    * converts null data set to the empty data set and checks
    * that obj is a data set of the correct type.
    */
   protected DataSet ToDataSet( Object obj ){

      if( obj == null )
         return  DataSetTools.dataset.DataSet.EMPTY_DATA_SET;

      if( typeOK( obj ) )
         return (DataSet)obj;

      throw new IllegalArgumentException("Initial value must be "+
               "correct type or null " + " in " + this.getClass() );
   }
   
   
   /**
    * Adds the given object to the list of choices when the GUI
    * appears.  Note that no new elements can be added
    * after the entry widget is created.
    * 
    * @param obj   The new value to be added to the list of 
    *               choices
    */
   public void AddItem( Object obj ) {
      
      if( entryWidget != null )
        return;
      
      if( obj == null )
        return;

      if( !typeOK( obj ) )    // quietly return, since this will
        return;               // happen in the subclasses
 
      if( !ds_list.contains( obj ) )
         ds_list.addElement( (DataSet)obj );
   }
   

   /**
    * Checks that the given object is a DataSet of the correct type 
    * based on it's DS_TYPE Attribute.  In the general case,
    * DataSetPG, any type DataSet is acceptable.
    *
    * @param ds The Object to check against the above criteria.
    *
    * @return true if it is a DataSet.
    */
   protected boolean typeOK( Object ds ) {

     if ( ds == null )
       return true;

     if ( ds == DataSet.EMPTY_DATA_SET )
       return true;

     if ( ds instanceof DataSet )
     {
       if ( my_type == null )         // accept all types
         return true;

       String type = (String)( (DataSet)ds ).getAttributeValue( Attribute.DS_TYPE );

       if( ( type == null ) || type.equals( my_type ) )
         return true;
     }

     return false;
   }

}
