package DataSetTools.components.View;
import DataSetTools.dataset.*;
import javax.swing.*;
import java.awt.event.*;



public class DataSetData implements IVirtualArray1D
  {
     private DataSet ds;
  
 
   
    int[]  selectedInd ;
   float maxy;
   float minx;
   float maxx;
   float miny;
   

   public DataSetData( DataSet DS)
     {
       ds = DS;
      
       selectedInd = ds.getSelectedIndices();
       minx=maxx=miny=maxy= Float.NaN;
  
      }

   public AxisInfo2D  getAxisInfo( boolean x_axis)
     {
      
      if( x_axis)
         return  new AxisInfo2D(findminX(), findmaxX(), 
               ds.getX_label(), ds.getX_units(), true);
      else

         return  new AxisInfo2D(findminY(), findmaxY(), 
               ds.getY_label(), ds.getY_units(), true);
     }


   
   private float findminX()
     { 
       if( !Float.isNaN(minx))
          return minx;
       
        float [] xvals;

	for (int line=0; line < getNumlines(); line++)
        {xvals = getXValues(line);
	 minx =  xvals[0];
         maxx =  xvals[0];
           for (int i=1; i < getNumPoints(line); i++)
	   {
	      if (xvals[i] < minx)
	   	minx = xvals[i];
	      if (xvals[i] > maxx)
		maxx = xvals[i];
	   }
         }
        return minx;
     }

  private float findmaxX()
    {
     findminX();
     return maxx;
    }
   private float findminY()
     {
       if( !Float.isNaN(miny))
          return miny;

	float [] yvals;


	for (int line=0; line < getNumlines(); line++)
        { yvals = getYValues(line);
          miny =  yvals[0];
          maxy =  yvals[0];
           for (int i=1; i < yvals.length; i++)
	   {
	      if (yvals[i] < miny)
	   	miny = yvals[i];
	      if (yvals[i] > maxy)
		maxy = yvals[i];
	   }
	}
        return miny;
     }

  private float findmaxY()
    {
     findminY();
     return maxy;
    }

     
   public String getTitle()
     {
       return ds.getTitle();
      }

  public void setTitle( String title )
     {
         
     }

  public float[] getXValues( int line_number )
    {     
      if( line_number < 0)
        return null;
      if( line_number >= getNumlines())
        return null;

      float[] x = ds.getData_entry( selectedInd[line_number]).getX_scale().
                  getXs();
      return x;

    }

  public void setXValues( float[] values, int line_number )
    {

    }




  public float[] getYValues( int line_number )
    {
      if( line_number < 0)
        return null;
      if( line_number >= getNumlines())
        return null;

      float[] y = ds.getData_entry( selectedInd[line_number]).getY_values();
      return y;
    }




  public void setYValues( float[] values, int line_number )
    {

    }

  public float [] getErrorValues( int line_number )
  {
     return ds.getData_entry( selectedInd[line_number]).getErrors( );
  }

  public int getGroupID( int line_number )
  {
     return ds.getData_entry( selectedInd[line_number]).getGroup_ID( );
  }

  public void setAllValues( float value )
  { 

  }





  
 /** Returns the number of x values in the line line_number
 */
  public int getNumPoints( int line_number)
  {
    if( line_number < 0)
         return 0;
    if( line_number >= getNumlines())
        return 0;
    return ds.getData_entry( selectedInd[ line_number]).getX_scale().getNum_x();

  }

  public int getNumlines()
    { 
      if( selectedInd == null)
         return 0;
      return selectedInd.length;
    }


   public JComponent[] getSharedControls()
    {
     return null;
    }
   public JComponent[] getPrivateControls()
    {
     JComponent[] Res = new JComponent[1];
     
     return new JComponent[0];
    }
   
   public void addActionListener( ActionListener listener)
    {

    }
   public void removeActionListener( ActionListener listener)
    {

    }
   public void removeAllActionListeners()
    {

    }


  }
