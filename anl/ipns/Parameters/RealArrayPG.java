/*
 * File:  RealArrayPG.java
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
 * Revision 1.2  2006/06/29 21:54:24  rmikk
 * Added or fixed the GPL
 *

 */
package gov.anl.ipns.Parameters;

import gov.anl.ipns.Util.Sys.StringUtil;

import java.awt.GridLayout;
import java.lang.reflect.Array;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.util.*;

/**
 * This is a class is a ParameterGUI that deals with(large) multi(or
 * uni)dimensional arrays of int,float,double,short,byte. The GUI allows for
 * text entry of small lists. Also, the value is passed by reference. The
 * specific data type for this ParameterGUI is determined by the initial value.
 * All othe values are converted to that real array type or if not possible,
 * throws an exception.
 */
public class RealArrayPG extends ObjectPG_base {

   private JPanel     entryWidget;

   private JTextField entryField;

   int                ndims;

   Class              baseClass;


   /**
    * Creates a new RealArrayPG object.
    * 
    * @param name
    *           Prompt string for this RealArrayPG.
    * @param val
    *           Value of this RealArrayPG. This determines the data type of this
    *           ParameterGUI henceforth
    */
   public RealArrayPG( String name, Object val )
            throws IllegalArgumentException {
      
      super( name , Test( val , null ) );
      int ndims = 0;

      // Get info about data type so Clear can replace a large
      // obj_value by one with 0 for each dim
      Class CC = val.getClass();
      if( CC.isArray() ) ndims++ ;
      while( ( CC.getComponentType() != null ) ) {
         CC = CC.getComponentType();
         if( CC.isArray() ) ndims++ ;
      }
      baseClass = CC;

   }


   private static Object Test( Object val , Object thisval )
            throws IllegalArgumentException {

      if( val == null )
         throw new IllegalArgumentException( " RealArray "
                  + "initial value must be a (multi dimensional) array" );
      Class CC = val.getClass();
      if( ! CC.isArray() )
         throw new IllegalArgumentException( " RealArray "
                  + "initial value must be a (multi dimensional) array" );
      if( thisval == null ) {
         Class C = CC.getComponentType();
         while( ( C.getComponentType() != null ) )
            C = C.getComponentType();

         if( C.equals( Integer.TYPE ) ) {
         } else if( C.equals( Float.TYPE ) ) {
         } else if( C.equals( Long.TYPE ) ) {
         } else if( C.equals( Short.TYPE ) ) {
         } else if( C.equals( Byte.TYPE ) ) {
         } else if( C.equals( Double.TYPE ) ) {
         } else
            throw new IllegalArgumentException( "Base type must be "
                     + "primitive and numeric" );

      } else {
         if( ! thisval.getClass().equals( val.getClass() ) )
            throw new IllegalArgumentException( "This value is the wrong type" );
      }
      return val;

   }


   /*
    * @see gov.anl.ipns.Parameters.ObjectPG_base#getWidgetValue()
    */
   public Object getWidgetValue() throws IllegalArgumentException {
      return Conversions.get_RealArray( Conversions.StringToVec( entryField
               .getText() ) , obj_value.getClass() );
   }


   /*
    * @see gov.anl.ipns.Parameters.ObjectPG_base#setWidgetValue(java.lang.Object)
    */
   public void setWidgetValue( Object value ) throws IllegalArgumentException {
      if( entryWidget == null )
         throw new IllegalArgumentException( "GUI is not available" );
      Test( value , obj_value );
      entryField.setText( StringUtil.toString( value ) );

   }


   /*
    * @see gov.anl.ipns.Parameters.ObjectPG_base#getObjectValue(java.lang.Object)
    */
   public Object getObjectValue( Object obj ) throws IllegalArgumentException {
      if( obj_value == null ) // hopefully this happens only at construction
                              // time
         return obj;
      if( obj.getClass().equals( obj_value.getClass() ) ) return obj;
      return Conversions.get_RealArray( obj , obj_value.getClass() );// Should
                                                                     // not do
                                                                     // this
   }


   /*
    * @see gov.anl.ipns.Parameters.NewParameterGUI#getWidget()
    */
   public JPanel getWidget() {

      if( entryWidget != null ) return entryWidget;
      String InitVal = gov.anl.ipns.Util.Sys.StringUtil.toString( obj_value );

      entryWidget = new JPanel( new GridLayout( 1 , 2 ) );
      entryWidget.add( new JLabel( getName() ) );
      entryField = new JTextField( InitVal );
      entryWidget.add( entryField );
      entryField.addKeyListener( new PG_KeyListener( this ) );

      return entryWidget;
   }


   /**
    * Set internal references to the lower level GUI entry widget to null so
    * that it can be garbage collected. Subsequent calls to getWidget() will
    * create a new widget.
    */
   public void destroyWidget() {
      entryWidget = null;
      entryField = null;
   }


   /*
    * 
    * 
    * @see gov.anl.ipns.Parameters.INewParameterGUI#clear()
    */
   public void clear() {
      int[] dims = new int[ ndims ];
      Arrays.fill( dims , 0 );
      obj_value = Array.newInstance( baseClass , dims );

   }


   /**
    * Enable or disable the GUI Components for entering values.
    * 
    * @param on_off
    *           Set true to enable the Components for user input.
    */
   public void setEnabled( boolean on_off ) {

      if( entryWidget == null ) return;
      entryWidget.setEnabled( on_off );

      for( int i = 0 ; i < entryWidget.getComponentCount() ; i++ )
         entryWidget.getComponent( i ).setEnabled( on_off );
   }


   /*
    * 
    * 
    * @see gov.anl.ipns.Parameters.INewParameter#getCopy()
    */
   public Object getCopy() {
      try {
         getValue();
      } catch( Exception s ) {

      }
      return new RealArrayPG( getName() , obj_value );
   }

}
