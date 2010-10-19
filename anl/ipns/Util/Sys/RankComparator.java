
/* 
 * File: RankComparator.java
 *
 * Copyright (C) 2010, Ruth Mikkelson
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
 * This work was supported by the Spallation Neutron Source Division
 * of Oak Ridge National Laboratory, Oak Ridge, TN, USA.
 *
 *  Last Modified:
 * 
 *  $Author:$
 *  $Date:$            
 *  $Rev:$
 */
package gov.anl.ipns.Util.Sys;

import java.util.Arrays;
import java.util.Comparator;

import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;

/**
 * This is a comparator that can be used with Arrays.sort. It will sort
 * the rank list of the data, not the data.  To go through the list in order
 * use for( i= 0; i< ... ) System.out.println( Data[Ranks[i]]);
 * The Data must be an array. It can be an array of primitive types/
 * 
 * @author ruth
 *
 */
public class RankComparator implements Comparator< Integer >
{

   Integer[] Ranks;
   Object Data;
   Comparator<Object> DataComparator;
   
   /**
    * Constructor
    * @param Ranks  An array of Ranks that is to be sorted with Array.sort with comparator
    * @param Data   The data that is an array of data for which the Rank array,when sorted,
    *               contain the indices of the data array in increasing order
    * @param DataComparator  A comparator for individual elements in the data array
    * @see #getNumericDataComparator() , @see #getStringDataComparator()
    */
   public RankComparator(Integer[] Ranks,   Object  Data,   Comparator<Object> DataComparator)
   {
      this.Ranks =Ranks ;
      this.Data = Data;
      this.DataComparator = DataComparator;
      if( Data == null || !Data.getClass( ).isArray( ))
         throw new IllegalArgumentException("Data is not in the correct format");
   }
   
   /**
    * @param  arg0 is index in data array(an element in the rank array)
    * @param  arg1 is index in data array(an element in the rank array)
    */
   @Override
   public int compare(Integer arg0, Integer arg1)
   {
      Object o1 = java.lang.reflect.Array.get( Data , arg0 );
      Object o2 = java.lang.reflect.Array.get( Data , arg1 );
      int x = DataComparator.compare(o1,o2);
     
      return x;
   }
   
   //Utility methods
   
   /**
    * Returns a list of Integer's with values 0,1,2,3,...
    * @param  size the size of the list.
    */
   public static Integer[] MakeRankList( int size)
   {
      if( size < 1)
         return null;
      Integer[] Res = new Integer[size];
      for( int i=0; i< size; i++)
         Res[i]=i;
      return Res;
   }
   
   /**
    * Used for the binary search routine below.  Cannot use java's binSearch
    * @param arg0    Index of the Rank array
    * @param Value   The value to be matched
    * @return       -1,0,or 1 if Data[arg0] is <.==,> Value
    */
   public int compare(Integer arg0, Object Value)
   {
      Object O = java.lang.reflect.Array.get( Data , Ranks[arg0] );
      return DataComparator.compare(O,Value);
   }
   
   /**
    *  Does binary search for a value for the rank sort.
    * @param Value     The Value to match
    * @return  The position in the Rank list of the first match or -insertionPoint -1 if there
    *          is no match. 
    * NOTE: The RankList must be sorted. 
    */
   public  int binSearch(  Object Value)
   {
      int first =0; 
      int last = Ranks.length-1;
      int mid = (first+last)/2;
      while( first < last && mid >=0  && mid < Ranks.length)
      {
         if( compare(mid ,Value)<0)
         {
            first = mid+1;
         }
         else if( compare(mid ,Value)>0)
            last = mid-1;
         else
            return mid;
         if( first <= last)
            mid = (first+last)/2;
      }
      
      if( compare(mid,Value)==0)
         return mid;
      if( mid == Ranks.length-1  && compare(mid,Value)>0)
         return -(mid+1)-1;
      return -mid-1;
   }
   
   /**
    * Returns a comparator for comparing numeric data
    * @return a comparator for comparing numeric data
    */
   public static Comparator<Object> getNumericDataComparator()
   {
      return new NumericDataComparator();
   }
   
   
   /**
    * Returns a comparator for comparing Strings 
    * @return a comparator for comparing Strings 
    */
   public static Comparator<Object> getStringDataComparator()
   {
      return new StringDataComparator();
   }
   
   /**
    * Not implemented yet
    * @return return a comparator for an array.
    */
   public static Comparator<Object> getArrayDataComparator()
   {
      throw new IllegalArgumentException("Not Yet Implemented");
   }
   

   /**
    * Implements a comparator for numeric data
    * @author ruth
    *
    */
   static class NumericDataComparator implements Comparator<Object>
   {

      @Override
      public int compare(Object o1, Object o2)
      {
          if( !(o1 instanceof Number))
             throw new IllegalArgumentException("Data Elements are not Numbers");

          if( !(o2 instanceof Number))
             throw new IllegalArgumentException("Data Elements are not Numbers");
          
          double v1 = ((Number)o1).doubleValue( );
          double v2 = ((Number)o2).doubleValue( );
          if( v1<v2)
             return -1;
          if( v1 > v2)
             return 1;
          return 0;
        
      }
      
   }
   
   /**
    * Implements a comparator for String data
    * @author ruth
    *
    */
  static class StringDataComparator implements Comparator<Object>
   {

      @Override
      public int compare(Object o1, Object o2)
      {
          if( !(o1 instanceof String))
             throw new IllegalArgumentException("Data Elements are not Numbers");

          if( !(o2 instanceof String))
             throw new IllegalArgumentException("Data Elements are not Numbers");
          
          return ((String)o1).compareTo( (String )o2);
        
      }
      
   }
  
  public static void main( String[] args)
  {
     int[] Data = { -3,5,1,17,2,3,8,4,-2,6};
     
     Integer[] ranks = RankComparator.MakeRankList(10 );
     
     RankComparator compare = new RankComparator( ranks, Data,
                                   RankComparator.getNumericDataComparator( ));
     
     Arrays.sort( ranks, compare );
     for( int i=0; i< ranks.length; i++)
        System.out.println( Data[ranks[i]]);
     
     System.out.println( compare.binSearch( -3 ));
     System.out.println( compare.binSearch( -2 ));

     System.out.println( compare.binSearch( 1 ));

     System.out.println( compare.binSearch( 2 ));

     System.out.println( compare.binSearch( 3 ));
     System.out.println( compare.binSearch( 4 ));

     System.out.println( compare.binSearch( 5 ));

     System.out.println( compare.binSearch( 6 ));

     System.out.println( compare.binSearch( 8 ));

     System.out.println( compare.binSearch( 17 ));
     System.out.println( compare.binSearch( 32 ));
     System.out.println( compare.binSearch(7 ));
     System.out.println( compare.binSearch(-8 ));
     
     
     
     
     
     
     
  }
}
