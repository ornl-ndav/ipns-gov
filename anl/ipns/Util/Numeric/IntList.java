/*
 * @(#)IntList.java     0.1   2000/07/21  Tom Worton, Dennis Mikkelson
 *                                    
 *  Utilities to switch between a string represntation and array representation
 *  of a list of integers.
 *
 *  $Log$
 *  Revision 1.3  2000/11/10 22:47:48  dennis
 *  Added code to main test program to test behavior with unordered lists of
 *  integers.
 *
 *
 *  Revision 1.2  2000/07/21 18:56:30  dennis
 *  Now can convert empty string to empty array of ints.
 *
 *
 */
package DataSetTools.util;

import java.util.*;
import java.io.*;
import java.lang.Math;
import DataSetTools.util.*;


/**
 *  Utilities to switch between a string represntation and array representation
 *  of a list of integers.  The string representation is composed of
 *  individual integers and integer ranges separated by commas.  Integer
 *  ranges are specified using a ":" to separate the first integer in the
 *  range from the last integer in the range.  Eg. "2,5,-10:-8,1:3"  
 *  Duplicates are removed when the string representation is converted to an
 *  array representation.  Also, the values in the array are kept in 
 *  increasing order, so the example would be [-10,-9,-8,1,2,3,5]
 */
public class IntList {

private static final int BUFFER_SIZE_INCREMENT = 100;

/* --------------------------------- ToString --------------------------- */
/**
 *   Convert a list of integer values into a string form, using "," to 
 *   separate individual values and ":" to specify a range of values as in
 *   2,5,7:10
 *
 *   @param  ilist  a sorted array filled of integer values
 *
 *   @return  A string representation of the list of integers.
 */

public static String ToString( int[] ilist ) 
{
    if ( ilist.length <= 0 )              // filter out degenerate case
      return "";

    int    i  = 0;                       // Add the first one to the list
    String s1 = "" + ilist[i];

    while (i < ilist.length-1 )          // while more integers, collapse
    {                                    // sequences of integers if possible

      if( (i+1 < ilist.length) && (ilist[i+1] == ilist[i]+1)) 
      {
        while( (i+1 < ilist.length) && (ilist[i+1] == ilist[i]+1)) 
          i = i+1;
        s1 = s1 + ":" + ilist[i]; 
      }
      else 
      {
        i = i+1;
        s1 = s1 + "," + ilist[i]; 
      }
    }

    return s1;
}

/* ------------------------------ ToArray ------------------------------- */
/**
 *  Convert a list of integers specified by a string such as "1,4,8:11" to
 *  an ordered array of integers.
 *
 *  @param  string_list  The string specifying the list of integers
 *  
 *  @return  An array containing the complete list of integer values, in 
 *           increasing order.  If there are no valid integers specified,
 *           this returns an empty array.
 */
public static int[] ToArray( String string_list ) 
{
                                  // strip blanks from the original string
  String instring = ""; 
  for ( int i = 0; i < string_list.length(); i++ )
    if ( !Character.isWhitespace( string_list.charAt(i) ) )
      instring += string_list.charAt(i);

                                   // make a tokenizer for the non-blank string
  boolean         getDelim=true;
  StringTokenizer t = new StringTokenizer(instring, ",:", getDelim);
  
  int new_list[];
  int ilist[] = new int[0];
  int i1, 
      i2;

  int is = 0;  

  if ( !t.hasMoreTokens() )      // trap degenerate string 
    return ilist;

  try                            // get the first integer is there is one
  {
    int value = Integer.parseInt(t.nextToken()); //Get first number
    i1 = value;
    ilist = new int[BUFFER_SIZE_INCREMENT];
    ilist[0] = value;
  }
  catch ( NumberFormatException e )
  {
    return ilist;
  }
                               // extract remaining delimiter & integer pairs
  while ( t.hasMoreTokens() ) 
  {
    String delim = t.nextToken();             // get the delimiter

    try 
    {
      i2 = Integer.parseInt(t.nextToken());   // try to get an integer
    }
    catch ( NumberFormatException e )
    {
      return RightSizeList( ilist, is+1 );
    }

    if (delim.compareTo(":") == 0)          // if delimeter was ":", fill out
    {                                       // the range of values, starting
      for (int i = i1+1; i<=i2; i++)        // with the last value we extracted
      {
        is = is + 1;
        new_list = AppendToList( i, ilist, is );
        if ( new_list == null )
          is--;
        else
          ilist = new_list;
      } 
      i1 = i2;                              // save the last value
    } 
    else                                    // otherwise just add the new
    {                                       // value
      is = is+1;
      new_list = AppendToList( i2, ilist, is );
      if ( new_list == null )
        is--;
      else
        ilist = new_list;
      i1 = i2;                              // save the last value
    } 
  }

  return RightSizeList( ilist, is+1 );
} 

/* -------------------------- RightSizeList --------------------------- */
/**
 *  Get a list of just the right size to hold the specified number of
 *  of elements of the current list and copy the current list to the new
 *  list.
 *
 *  @param list     The current list that may be partially filled
 *  @param size     The number of positions used at the start of the list
 *
 *  @return A list of length "size" filled with the first "size" elements 
 *          of the original list. 
 */

private static int[] RightSizeList( int list[], int size )
{
                                              // now make a new array that is
  int final_list[] = new int[ size ];         // just the right size
  for ( int i=0; i < size; i++ )
    final_list[i] = list[i];

  return final_list;
}


/* ---------------------------- AppendToList ---------------------------- */
/**
 *  Append a new integer value to the specified list if it's not already
 *  in the list and then sort the list.
 *
 *  @param  new_int   The integer to be inserted in the list if it's not
 *                    already there.
 *  @param  list      The current list of integers.
 *  @param  position  The position where the list is to be added.  This 
 *                    should be one beyond the last position in the list
 *                    that is currently used. 
 *
 *  @return A reference to a new array containing the original integers plus
 *          the new integer if it was not already there.  If the integer was
 *          was already in the list, this returns null. 
 */

private static int[] AppendToList( int new_int, int list[], int position )
{
                                  // if it's already in the list, return null
   if ( arrayUtil.get_index_of( new_int, list, 0, position-1 ) >= 0 )
     return null;

                                  // expand the buffer if needed 
   int new_list[];
   if ( position >= list.length )
   {
     new_list = new int[ list.length + BUFFER_SIZE_INCREMENT ];
     for ( int i = 0; i < list.length; i++ )
       new_list[i] = list[i];
   } 
   else
     new_list = list;
                                         // insert the new element in the list
                                         // where it belongs in the sorted list
   int k = position;
   while ( k > 0 && new_int < list[ k-1 ] )
   {
     list[ k ] = list[ k-1 ];
     k--;
   }
   list[ k ] = new_int;

   return new_list;
}


/* ---------------------------------------------------------------------------
 *
 * main method is a driver routine to test both ToString and ToArray
 *
 */

  public static void main(String[] args) 
  {
    int [] ilist; 

    for (int i = 0; i < args.length; i++) 
    {
      String instring = args[i];
      System.out.println("  ");

      System.out.println("Converting '" + instring + "' to a list of numbers");
      ilist = ToArray(instring);

      // Output list of integers
      System.out.println("The list is ");
      System.out.println("------------");
      for (int j=0; j<ilist.length; j++)
        System.out.println(ilist[j]);

      // Now convert ilist back to a string
      String result = ToString( ilist);
      System.out.println("The result is " + result );
    }

    // test string with blanks 
    System.out.println("Test using string with blanks ...." );
    String s = "  1,3  :  5 ";
    System.out.println("The list is " + s );
    ilist = ToArray( s );
    for (int j=0; j<ilist.length; j++)
      System.out.println(ilist[j]);

    // test list with repeated values
    ilist = new int[10];
    for ( int i = 0; i < 10; i++ )
    {
      if ( i < 5 )
        ilist[i] = i;
      else
        ilist[i] = i-1;
      System.out.print( ""+ilist[i]+"," );
    }
    System.out.println();
    System.out.println( "list with repeats: "+ ToString( ilist ) );

    // test unordered list
    ilist[0]=7; 
    ilist[1]=5; 
    ilist[2]=5; 
    ilist[3]=5; 
    for ( int i = 0; i < 10; i++ )
      System.out.print( ""+ilist[i]+"," );
    System.out.println();
    System.out.println( "unordered list with repeats: "+ ToString( ilist ) );

  } 

}  // end List class
