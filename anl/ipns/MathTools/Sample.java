/*
 * File:  Sample.java
 *
 * Copyright (C) 2000, Dennis Mikkelson
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
 * Contact : Dennis Mikkelson <mikkelsond@uwstout.edu>
 *           Department of Mathematics, Statistics and Computer Science
 *           University of Wisconsin-Stout
 *           Menomonie, WI. 54751
 *           USA
 *
 * This work was supported by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 * Modified:
 *
 *  $Log$
 *  Revision 1.6  2002/04/04 19:16:23  dennis
 *  Added SmoothResample() methods.  Currently these just call
 *  Resample().  The implementation should be changed to include
 *  smoothing.
 *
 *  Revision 1.5  2001/04/25 20:56:43  dennis
 *  Added copyright and GPL info at the start of the file.
 *
 */

package DataSetTools.math;

import DataSetTools.util.*;

/**
 *  Histogram and function resampling and rebinning operations
 *  some of which were originally in tof_calc.java
 */

public final class Sample 
{
  /*
   * Don't let anyone instantiate this class.
   */
  private Sample() {}


  /* ------------------------------ ReBin ---------------------------------- */
  /**
   * Constructs a new histogram by rearranging the counts of an input histogram
   * into a new set of bins in a new histogram.  If a new histogram bin 
   * overlaps part of a bin of the input histogram, a number of counts
   * proportional to the amount of the input bin covered by the new bin is 
   * assigned to the new bin.  If a new histogram bin entirely contains an
   * input histogram bin, all of the counts from the input bin are assigned 
   * to the new bin.  The bins of the new histogram recieve the total of all 
   * such counts from bins in the input histogram that they contain, or 
   * partially overlap.  Also see the method "ResampleBin".
   *
   * @param   iX[]      Array of bin boundaries for the input histogram.  These
   *                    can be an arbitrary non-decreasing sequence of X values.
   * @param   iHist[]   Array of histogram values for the input histogram. The
   *                    length of iHist[] must be one less than the length of
   *                    iX[].
   * @param   nX[]      Array of bin boundaries for the new histogram.  These
   *                    can be an arbitrary non-decreasing sequence of X values.
   * @param   nHist[]   Array of histogram values for the new histogram. The
   *                    length of nHist[] must be one less than the length of
   *                    nX[].
   */

  public static boolean ReBin( float iX[], float iHist[], 
                               float nX[], float nHist[] )
  {
    int  num_i, num_n;
    int  n, k, i, il;
    float  nXa, nXb,
           iXa, iXb,
           iXmin, iXmax,
           nXmin, nXmax;
    float  iXtemp;
    float  sum;

                                /* do some basic checks on validity of data */
    num_i = iX.length - 1;
    if ( num_i <= 0 )
    {
      System.out.println("ERROR in ReBin ... not enough input X values");
      return( true );
    }
    iXmin = iX[ 0 ];
    iXmax = iX[ num_i ];

    num_n = nX.length - 1;
    if ( num_n <= 0 )
    {
      System.out.println("ERROR in ReBin ... not enough output X values");
      return( false );
    }
    nXmin = nX[ 0 ];
    nXmax = nX[ num_n ];

    if ( iHist.length != num_i )
    {
      System.out.println("ERROR in ReBin ... iHist size wrong");
      return( false );
    }
    if ( nHist.length != num_n )
    {
      System.out.println("ERROR in ReBin ... nHist size wrong");
      return( false );
    }

                                              /* check for degenerate cases */
    if ( (nXmax <= iXmin) || (nXmin >= iXmax) ||
         (nXmin >= nXmax) || (iXmin >= iXmax)  )
    {
      for ( n = 0; n < nHist.length; n++ )
        nHist[n] = 0.0f;
      return( true );
    }

                                  /* advance on new interval to first point */
                                  /* nXa  >= start of input interval.       */
    n   = 0;
    nXa = nXmin;
    while ( nXa < iXmin )
    {
      nHist[n] = 0.0f;
      n++;
      nXa = nX[n];
    }
                               /* advance on input interval to first point  */
                               /* >= nXa (if possible).  As we go, find sum */
                               /* of initial bins in input histogram.       */
    sum = 0.0f;
    i   = 0;
    iXa = iXmin;
    while ( (iXa < nXa) && (i < num_i) )
    {
      sum = sum + iHist[i];
      i++;
      iXa = iX[i];
    }
                                /* if we exceeded nXa, correct for partial  */
                                /* bin and set iXa back to GLB of nXa       */
    if ( (iXa > nXa) && (i > 0) )
    {
       sum = sum - iHist[i-1] * (iXa - nXa)/(iXa - iX[i-1]);
       i--;
       iXa = iX[i];
    } 
                               /* if there is an initial output bin for the */
                               /* sum of the initial input bins, save sum.  */
    if ( n > 0 )
      nHist[n-1] = sum;

                               /* now deal with general case.  At this point */
                               /* we know iXa = GLB( nXa )                   */
    while ( (n < num_n) && (nXa < iXmax) )
    {
      nXb = nX[ n+1 ];
      il = i + 1;
      while ( (il < num_i) && (iX[il] < nXb) )
        il++;

      iXb = iX[ il ];
      if ( il == i+1 )                /* [iXa, iXb] is just one subinterval  */
        {
                                   /* add portion of histogram corresponding */
                                   /* overlapping part of the two intervals  */
                                   /* [iXa, iXb] and [nXa, nXb]              */

          if ( iXb < nXb )            /* we're at the end of input intervals */
            sum = iHist[i] * ( iXb - nXa ) / ( iXb - iXa );
          else              
            sum = iHist[i] * ( nXb - nXa ) / ( iXb - iXa );
        }

      else                          /* [iXa, iXb] contains >= 2 subintervals */
        {
                                             /* start with part of first bin */
          iXtemp = iX[ i+1 ];
          sum = iHist[i] * ( iXtemp - nXa ) / ( iXtemp - iXa );

          for ( k = i + 1; k < il - 1; k++ )           /* add up middle bins */
            sum = sum + iHist[k];
          
          if ( iXb < nXb )                /* we're at end of input intervals */
                                          /* so add all of last bin          */
            sum = sum + iHist[il-1];
          else                                       /* add part of last bin */
            {
              iXtemp = iX[il - 1];
              sum = sum + iHist[il-1] * (nXb - iXtemp)/(iXb -iXtemp);
            }
        }

      nHist[n] = sum;                   /* save result in new histogram */

      n++;                           /* advance to next bin in new histogram */
      nXa = nXb;
                                               /* advance in input histogram */
                                               /* keeping iXa = GLB(nXa)     */ 
      i   = il - 1;
      iXa = iX[i];  
    } 
                                  /* fill out rest of new histogram (if any) */ 
                                  /* with zeros.                             */

    for ( k = n; k < num_n; k++ )
      nHist[k] = 0.0f;

    return( true );
  }


  /* ------------------------------ ReBin ---------------------------------- */
  /**
   * Constructs a new histogram by rearranging the counts of an input histogram
   * into a new set of bins in a new histogram.  If a new histogram bin
   * overlaps part of a bin of the input histogram, a number of counts
   * proportional to the amount of the input bin covered by the new bin is
   * assigned to the new bin.  If a new histogram bin entirely contains an
   * input histogram bin, all of the counts from the input bin are assigned
   * to the new bin.  The bins of the new histogram recieve the total of all
   * such counts from bins in the input histogram that they contain, or
   * partially overlap.  Also see the method "ResampleBin".
   *
   * This version also accepts an array of error values for the input histogram
   * and calculates a new array of error values for the new histogram.
   *
   * @param   iX[]      Array of bin boundaries for the input histogram.  These
   *                    can be an arbitrary non-decreasing sequence of X values.
   * @param   iHist[]   Array of histogram values for the input histogram. The
   *                    length of iHist[] must be one less than the length of
   *                    iX[].
   * @param   iErr[]    Array of error values for the input histogram. The
   *                    length of iErr[] must be one less than the length of
   *                    iX[].
   * @param   nX[]      Array of bin boundaries for the new histogram.  These
   *                    can be an arbitrary non-decreasing sequence of X values
   * @param   nHist[]   Array of histogram values for the new histogram. The
   *                    length of nHist[] must be one less than the length of
   *                    nX[].
   * @param   nErr[]    Array of error values for the new histogram. The
   *                    length of nHist[] must be one less than the length of
   *                    nX[].
   */

  public static boolean ReBin( float iX[], float iHist[], float iErr[],
                               float nX[], float nHist[], float nErr[] )
  {
    int  num_i, num_n;
    int  n, k, i, il;
    float  nXa, nXb,
           iXa, iXb,
           iXmin, iXmax,
           nXmin, nXmax;
    float  iXtemp;
    float  sum, 
           err,
           err_sum;

                                /* do some basic checks on validity of data */
    num_i = iX.length - 1;
    if ( num_i <= 0 )
    {
      System.out.println("ERROR in ReBin ... not enough input X values");
      return( true );
    }
    iXmin = iX[ 0 ];
    iXmax = iX[ num_i ];

    num_n = nX.length - 1;
    if ( num_n <= 0 )
    {
      System.out.println("ERROR in ReBin ... not enough output X values");
      return( false );
    }
    nXmin = nX[ 0 ];
    nXmax = nX[ num_n ];

    if ( iHist.length != num_i )
    {
      System.out.println("ERROR in ReBin ... iHist size wrong");
      return( false );
    }
    if ( nHist.length != num_n )
    {
      System.out.println("ERROR in ReBin ... nHist size wrong");
      return( false );
    }

    if ( iErr.length != num_i )
    {
      System.out.println("ERROR in ReBin ... iErr size wrong");
      System.out.println("num_i = "+num_i + " iErr length = "+iErr.length);
      return( false );
    }
    if ( nErr.length != num_n )
    {
      System.out.println("ERROR in ReBin ... nErr size wrong");
      return( false );
    }

                                              /* check for degenerate cases */
    if ( (nXmax <= iXmin) || (nXmin >= iXmax) ||
         (nXmin >= nXmax) || (iXmin >= iXmax)  )
    {
      for ( n = 0; n < nHist.length; n++ )
      {
        nHist[n] = 0.0f;
        nErr[n]  = 0.0f;
      }
      return( true );
    }

                                  /* advance on new interval to first point */
                                  /* nXa  >= start of input interval.       */
    n   = 0;
    nXa = nXmin;
    while ( nXa < iXmin )
    {
      nHist[n] = 0.0f;
      nErr[n]  = 0.0f;
      n++;
      nXa = nX[n];
    }
                               /* advance on input interval to first point  */
                               /* >= nXa (if possible).  As we go, find sum */
                               /* of initial bins in input histogram.       */
    sum     = 0.0f;
    err_sum = 0.0f;
    i       = 0;
    iXa     = iXmin;
    while ( (iXa < nXa) && (i < num_i) )
    {
      sum     = sum + iHist[i];
      err_sum = err_sum + iErr[i] * iErr[i];
      i++;
      iXa = iX[i];
    }
                                /* if we exceeded nXa, correct for partial  */
                                /* bin and set iXa back to GLB of nXa       */
    if ( (iXa > nXa) && (i > 0) )
    {
       sum     = sum - iHist[i-1] * (iXa - nXa)/(iXa - iX[i-1]);
       err     = iErr[i-1] * (nXa - iX[i-1])/(iXa - iX[i-1]);
       err_sum = err_sum - iErr[i-1] * iErr[i-1] + err * err;
       i--;
       iXa = iX[i];
    }
                               /* if there is an initial output bin for the */
                               /* sum of the initial input bins, save sum.  */
    if ( n > 0 )
    {
      nHist[n-1] = sum;
      nErr[n-1]  = (float)Math.sqrt( err_sum );
    }
                               /* now deal with general case.  At this point */
                               /* we know iXa = GLB( nXa )                   */
    while ( (n < num_n) && (nXa < iXmax) )
    {
      nXb = nX[ n+1 ];
      il = i + 1;
      while ( (il < num_i) && (iX[il] < nXb) )
        il++;

      iXb = iX[ il ];
      if ( il == i+1 )                /* [iXa, iXb] is just one subinterval  */
        {
                                   /* add portion of histogram corresponding */
                                   /* overlapping part of the two intervals  */
                                   /* [iXa, iXb] and [nXa, nXb]              */

          if ( iXb < nXb )            /* we're at the end of input intervals */
          {
            sum = iHist[i] * ( iXb - nXa ) / ( iXb - iXa );
            err = iErr[i]  * ( iXb - nXa ) / ( iXb - iXa );
            err_sum = err * err;
          }
          else
          {
            sum = iHist[i] * ( nXb - nXa ) / ( iXb - iXa );
            err = iErr[i]  * ( nXb - nXa ) / ( iXb - iXa );
            err_sum = err * err;
          }
        }

      else                          /* [iXa, iXb] contains >= 2 subintervals */
        {
                                             /* start with part of first bin */
          iXtemp  = iX[ i+1 ];
          sum     = iHist[i] * ( iXtemp - nXa ) / ( iXtemp - iXa );
          err     = iErr[i]  * ( iXtemp - nXa ) / ( iXtemp - iXa );
          err_sum = err * err;

          for ( k = i + 1; k < il - 1; k++ )           /* add up middle bins */
          {
            sum     = sum + iHist[k];
            err_sum = err_sum + iErr[k] * iErr[k];
          }

          if ( iXb < nXb )                /* we're at end of input intervals */
          {                               /* so add all of last bin          */
            sum = sum + iHist[il-1];
            err_sum = err_sum + iErr[il-1] * iErr[il-1];
          }
          else                                       /* add part of last bin */
          {
            iXtemp = iX[il - 1];
            sum = sum + iHist[il-1] * (nXb - iXtemp)/(iXb -iXtemp);
            err = iErr[il-1] * (nXb - iXtemp)/(iXb -iXtemp);
            err_sum = err_sum + err * err;
          }
        }

      nHist[n] = sum;                   /* save result in new histogram */
      nErr[n]  = (float)Math.sqrt( err_sum );

      n++;                           /* advance to next bin in new histogram */
      nXa = nXb;
                                               /* advance in input histogram */
                                               /* keeping iXa = GLB(nXa)     */
      i   = il - 1;
      iXa = iX[i];
    }
                                  /* fill out rest of new histogram (if any) */
                                  /* with zeros.                             */

    for ( k = n; k < num_n; k++ )
    {
      nHist[k] = 0.0f;
      nErr[k]  = 0.0f;
    }

    return( true );
  }


  /* --------------------------- ResampleBin ------------------------------ */
  /**
   * Constructs a simple histogram by spreading the counts of one input "bin"
   * from a histogram across a set of equal size bins in another histogram.
   * Also see the method "ReBin". 
   *
   * @param   iXmin     the left  bin boundary of the input bin
   * @param   iXmax     the right bin boundary of the input bin
   * @param   iBin      the counts in in input bin 
   * @param   nXmin     the left  boundary of the new histogram bins
   * @param   nXmax     the right boundary of the new histogram bins
   * @param   nHist[]   array of bin count values in another histogram.  The
   *                    count values MUST have been initialized on input.  This
   *                    function adds counts from the input bin to the values
   *                    initially in the nHist[] bins.
   */

  public static boolean ResampleBin( float  iXmin, 
                                     float  iXmax,
                                     float  iBin,
                                     float  nXmin,
                                     float  nXmax,
                                     float  nHist[]  )
  {
    int      num_n;          
    int      i, 
             n_steps;
    int      n_first, 
             n_last;
    float    counts;
    float    delta_x;
    float    first, 
             last;

                                /* do some basic checks on validity of data */
    num_n = nHist.length;
    if ( (nXmin >= nXmax)         || 
         (iXmin >= iXmax)         ||  
         (num_n <= 0)              )
    {
      System.out.println(" Invalid input data in ResampleBin " + 
                         " in  = " + iXmin + iXmax +
                         " out = " + nXmin + nXmax +
                         " n   = " + num_n );
      return( false );
    }
                                              /* check for degenerate cases */
    if ( (nXmax <= iXmin) || (nXmin >= iXmax) ) 
      return( true );

                                 /* find first grid point in new interval so */
                                 /* first >= start of input interval.        */
    delta_x = (nXmax - nXmin) / num_n;
    n_first = (int) ( (iXmin - nXmin) / delta_x + 1.0f );
    if ( n_first < 0 )
      n_first = 0;
    first = nXmin + n_first * delta_x;

                                  /* find last grid point in new interval so */
                                  /* last <= end of input interval.          */
    n_last = (int) ( (iXmax - nXmin) / delta_x );
    if ( n_last > num_n )
      n_last = num_n;
    last = nXmin + n_last * delta_x;

                                /* check for case where input interval is    */
                                /* entirely contained in one bin of new hist */
    if ( n_first > n_last )
    {
      nHist[n_last] += iBin;  /* all counts go into one bin & we're done */
      return( true );
    }

                                  /* spread bin values across the whole bins */
                                  /* from first to last, if there are any.   */
    if ( n_last > n_first )
    {
      n_steps = n_last - n_first;
      counts  = iBin * delta_x / (iXmax - iXmin);
      for ( i = n_first; i < n_last; i++ )
        nHist[i] += counts;
    }
                                     /* assign part of bin values to first  */
                                     /* partial bin, if there is one.       */
    if ( n_first > 0 )
    {
      if ( iXmin < nXmin )           /* input bin extends beyond new interval */
        counts  = iBin * delta_x / (iXmax - iXmin);
      else
        counts = iBin * (first - iXmin) / (iXmax - iXmin);
      nHist[n_first-1] += counts;
    }
                                       /* assign part of bin values to last */
                                       /* partial bin, if there is one.     */
    if ( n_last < num_n )
    {
      if ( iXmax > nXmax )           /* input bin extends beyond new interval */
        counts  = iBin * delta_x / (iXmax - iXmin);
      else
        counts = iBin * (iXmax - last) / (iXmax - iXmin);
      nHist[n_last] += counts;
    }

    return( true );
  }


/* -------------------------- CLSmoothFunction ------------------------- */
/**
 *  Smooth a function.  This algorithm smooths a tabulated function by 
 *  replacing sets of points with "nearly equal" x values with a new point
 *  obtained by averaging the x and y values.  "x values" are considered
 *  "nearly equal" if the distance between them is less than the total length
 *  of the interval divided by a specified number of steps.
 *  
 *  @param  iX       The original array of x values.  This will be altered and
 *                   the new smoothed x values will be returned in the initial
 *                   part of this array. 
 *  @param  iY       The original array of y values.  This will be altered and
 *                   the new smoothed y values will be returned in the initial
 *                   part of this array. 
 *  @param  err      The original array of error values.  This will be altered
 *                   and the new error values will be returned in the initial
 *                   part of this array. 
 *  @param  n_steps  The approximate number of steps to use for the new 
 *                   smoothed function.
 *
 *  @return The number of entries in iX, iY and err that were used for the 
 *          smoothed function.
 *  
 */
 public static int CLSmooth( float iX[], float iY[], float err[], int n_steps )
 {
   int   i,          // indexes the next point to process in the list
         smoothed_i, // indexes the averaged point to insert in the list 
         n_summed;
   float step,
         x_start,
         x_sum,
         y_sum,
         err_sum;
                                                   // need at least 2 points
   if ( iX.length  <= 1         || 
        iY.length  <  iX.length || 
        err.length <  iX.length || 
        n_steps    <  1 )
     return 0;

   step = ( iX[iX.length-1] - iX[0] ) / n_steps;

   i          = 0;           
   smoothed_i = 0;   
   while ( i < iX.length )  // look for groups of "nearly equal" x values
   {
     x_start  = iX[i];                         // record the current point
     n_summed = 1;
     x_sum    = iX[i];
     y_sum    = iY[i];
     err_sum  = err[i] * err[i];
     i++;                                    // sum all nearly equal points
     while ( i < iX.length && iX[i]-x_start <= step )
     {
       n_summed++;
       x_sum   += iX[i]; 
       y_sum   += iY[i]; 
       err_sum += err[i] * err[i];
       i++;
     } 
                                           // save the average x, y and error
                                           // in the earlier part of the list
     iX [ smoothed_i ] = x_sum / n_summed;
     iY [ smoothed_i ] = y_sum / n_summed;
     err[ smoothed_i ] = (float)Math.sqrt( err_sum ) / n_summed; 
     smoothed_i++;
   }

   return smoothed_i;
 }


/* -------------------------- CLSmoothFunction ------------------------- */
/**
 *  Smooth a function.  This algorithm smooths a tabulated function by
 *  replacing sets of points with "nearly equal" x values with a new point
 *  obtained by averaging the x and y values.  "x values" are considered
 *  "nearly equal" if the distance between them is less than the total length
 *  of the interval divided by a specified number of steps.
 * 
 *  @param  iX       The original array of x values.  This will be altered and
 *                   the new smoothed x values will be returned in the initial
 *                   part of this array.
 *  @param  iY       The original array of y values.  This will be altered and
 *                   the new smoothed y values will be returned in the initial
 *                   part of this array.
 *  @param  n_steps  The approximate number of steps to use for the new
 *                   smoothed function.
 *
 *  @return The number of entries in iX and iY that are used for the
 *          smoothed function.
 *
 */
 public static int CLSmooth( float iX[], float iY[], int n_steps )
 {
   int   i,          // indexes the next point to process in the list
         smoothed_i, // indexes the averaged point to insert in the list
         n_summed;
   float step,
         x_start,
         x_sum,
         y_sum;
                                                   // need at least 2 points
   if ( iX.length  <= 1         ||
        iY.length  <  iX.length ||
        n_steps    <  1 )
     return 0;

   step = ( iX[iX.length-1] - iX[0] ) / n_steps;

   i          = 0;
   smoothed_i = 0;
   while ( i < iX.length )  // look for groups of "nearly equal" x values
   {
     x_start  = iX[i];                         // record the current point
     n_summed = 1;
     x_sum    = iX[i];
     y_sum    = iY[i];
     i++;                                    // sum all nearly equal points
     while ( i < iX.length && iX[i]-x_start <= step )
     {
       n_summed++;
       x_sum   += iX[i];
       y_sum   += iY[i];
       i++;
     }
                                           // save the average x, y 
                                           // in the earlier part of the list
     iX [ smoothed_i ] = x_sum / n_summed;
     iY [ smoothed_i ] = y_sum / n_summed;
     smoothed_i++;
   }
                                                    // zero out the unused part
                                                    // of the arrays 
   for ( int j = smoothed_i; j < iX.length; j++ )
     iX[j] = 0;

   for ( int j = smoothed_i; j < iY.length; j++ )
     iY[j] = 0;
                                                    // return the number of
                                                    // positions now used
   return smoothed_i;
 }

/* ------------------------------- Resample -------------------------------- */
/**
 *  This algorithm resamples a function tablulated at arbitrarily spaced 
 *  increasing points at a new set of increasing points using linear 
 *  interpolation.
 *
 *  @param  iX       The original array of x values.  These MUST be in
 *                   increasing order. 
 *  @param  iY       The original array of y values.  There MUST be as many
 *                   y values as x values
 *  @param  nX       The new set of x values to use.
 *
 *  @return The new set of y-values obtained by interpolating the original
 *  y values at the new x-values.
 *
 */
 public static float[] Resample( float iX[], float iY[], float nX[] )
 {
   if ( iX == null || iY == null || nX == null )   
     return null;

   if ( iX.length <= 0 || iY.length <= 0 || nX.length <= 0 )
   {
     System.out.println("ERROR: Invalid parameters to Sample.Resample().....");
     System.out.println("iX length = " + iX.length );
     System.out.println("iY length = " + iY.length );
     System.out.println("nX length = " + nX.length );
     return null;
   }

   float nY[]    = new float[ nX.length ];
   float first_x = iX[ 0 ];
   float last_x  = iX[ iX.length - 1 ];
   float x;
   for ( int i = 0; i < nX.length; i++ )
   {
     x = nX[i];

     if ( x < first_x || x > last_x )       // assume it's zero outside of 
       nY[i] = 0;                           // the table of values given

     else
       nY[i] = arrayUtil.interpolate( x, iX, iY );     
   } 
   return nY; 
 }


/* ---------------------------- SmoothResample ---------------------------- */
/**
 *  This algorithm resamples a function tablulated at arbitrarily spaced 
 *  increasing points at a new set of increasing points using linear 
 *  interpolation.  In regions where there are significantly more original
 *  x values than new x values, the function will be smoothed to a degree
 *  determined by the smooth_flag parameter.  
 *
 *  @param  iX       The original array of x values.  These MUST be in
 *                   increasing order.
 *  @param  iY       The original array of y values.  There MUST be as many
 *                   y values as x values
 *  @param  nX       The new set of x values to use.
 *
 *  @param smooth_flag  Flag indicating the degree of smoothing to be 
 *                      applied. #### smooth_flag not not currently 
 *                      implemented.
 *
 *  @return The new set of y-values obtained by smoothing or interpolating 
 *  the original y values at the new x-values.
 */
 public static float[] SmoothResample( float iX[], 
                                       float iY[], 
                                       float nX[], 
                                       int   smooth_flag )
 {
   return Resample( iX, iY, nX );    // ##### not properly implemented yet 
 } 


/* ---------------------------- SmoothResample ---------------------------- */
/**
 *  This algorithm resamples a function tablulated at arbitrarily spaced
 *  increasing points at a new set of increasing points using linear
 *  interpolation.  It also calculates new error estimates for the smoothed
 *  values.  In regions where there are significantly more original
 *  x values than new x values, the function will be smoothed to a degree
 *  determined by the smooth_flag parameter.
 *
 *  @param  iX       The original array of x values.  These MUST be in
 *                   increasing order.
 *  @param  iY       The original array of y values.  There MUST be as many
 *                   y values as x values
 *  @param  iErr     The original array of error extimates.  There MUST be
 *                   as many error values as x values. 
 *  @param  nX       The new set of x values to use.
 *
 *  @param smooth_flag  Flag indicating the degree of smoothing to be
 *                      applied. #### smooth_flag not not currently
 *                      implemented.
 *
 *  @return A two dimensional array with two rows.  The first row contains
 *          the new set of y-values obtained by smoothing or interpolating
 *          and the second row contains the corresponding error estimates. 
 */
 public static float[][] SmoothResample( float iX[],
                                       float iY[],
                                       float iErr[],
                                       float nX[],
                                       int   smooth_flag )
 {
   float y[]   = Resample( iX, iY, nX );     // ##### not properly implemented
   float err[] = Resample( iX, iErr, nX );   //       yet.

   float result[][] = new float[2][];
   result[0] = y;
   result[1] = err;
   return result;
 }

}
