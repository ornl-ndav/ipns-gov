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
   boolean is_x_linear = true;
   boolean is_y_linear = true;
   

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
               ds.getX_label(), ds.getX_units(), is_x_linear);
      else

         return  new AxisInfo2D(findminY(), findmaxY(), 
               ds.getY_label(), ds.getY_units(), is_y_linear);
     }


   
   private float findminX()
     { 
       if( !Float.isNaN(minx))
          return minx;
       
        float [] xvals;

        xvals = getXValues(0);
	minx =  xvals[0];
        maxx =  xvals[0];
	for (int line=0; line < getNumlines(); line++)
        {xvals = getXValues(line);
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

	yvals = getYValues(0);
        miny =  yvals[0];
        maxy =  yvals[0];

	for (int line=0; line < getNumlines(); line++)
	{ yvals = getYValues(line);
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




  public void  setYValues( float[] values, int line_number )
    {

    }
  
  public float[] getXVals_ofIndex(int index)
  {
     if( index < 0)
        return null;
     if( index >= getNumGraphs())
        return null;
     return ds.getData_entry(index).getX_values();
  }
  public float[] getYVals_ofIndex(int index)
  {
     if( index < 0)
        return null;
     if( index >= getNumGraphs())
        return null;
     return ds.getData_entry(index).getY_values();
  }
    

  public float [] getErrorValues( int line_number )
  {
     if( line_number < 0)
        return null;
     if( line_number >= getNumlines())
        return null;
     return ds.getData_entry( selectedInd[line_number]).getErrors( );
  }
  
  public float[] getErrorVals_ofIndex(int index)
  {
     if( index < 0)
        return null;
     if( index >= getNumGraphs())
        return null;
     return ds.getData_entry( index ).getErrors( );
  }

  public int getGroupID( int line_number )
  {
     if( line_number < 0)
        return 0;
     if( line_number >= getNumlines())
        return 0;
     return ds.getData_entry( selectedInd[line_number]).getGroup_ID( );
  }

  public int getPointedAtGraph()
  {
     return ds.getPointedAtIndex();
  }

  public int[] getSelectedGraphs()
  {
     return selectedInd;
  }

  public boolean isSelected(int index) 
  {
     return ds.isSelected(index);
  }
  
  public int getNumGraphs()
  {
     return ds.getNum_entries();
  }
  public void setAllValues( float value )
  { 

  }

  public void set_x_linear(boolean isLinear) 
  {
    is_x_linear = isLinear;
  }
  
  public void set_y_linear(boolean isLinear) 
  {
    is_y_linear = isLinear;
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
