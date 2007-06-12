/**
* File: GraphTagFrame.java
 *
 * Copyright (C) 2007, Ruth Mikkelson 
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
 *  $Log$
 *  Revision 1.2  2007/06/12 21:23:56  rmikk
 *  Fixed a reverse problem with the column mode
 *
 *  Revision 1.1  2007/06/12 20:28:28  rmikk
 *  Initial CheckIn. This produces a JFrame that tags along on the boundary of
 *    another JFrame, displaying values of slices in a VirtualArray2D that should
 *    correspond to the values in an area of the original JFrame. The area and
 *    the tag frame line up.
 *
 */

package gov.anl.ipns.ViewTools.Panels;

import gov.anl.ipns.Util.Sys.FinishJFrame;
import gov.anl.ipns.ViewTools.Panels.Transforms.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import gov.anl.ipns.ViewTools.Components.*;
import gov.anl.ipns.ViewTools.Panels.Graph.*;
import gov.anl.ipns.ViewTools.UI.ActiveJPanel;


/**
 * This is a general purpose window that can tag along with another JFrame
 * and display graphs corresponding to some interior region of the JFrame.
 * 
 * @author Ruth
 * 
 */
public class GraphTagFrame extends FinishJFrame implements ActionListener {

   private String       Mode;                        // must be ROW, COL, or
                                                                    // TIME

   public static final String ROW  = "Row";                // Mode Value

   public static final String COL  = "Col";                // Mode Value

   public static final String TIME = "Time";               // Mode Value

   JFrame               Frame2Tag;

   CoordBounds          ViewerPixelRange_TaggedFrame;

   CoordBounds          ViewerRowColRange;

   CoordBounds          Frame2TagRange;

   IVirtualArray2D      data;

   float[]              TimeValues;

   GraphJPanel          graph;

   int                  pointedAtRow , pointedAtCol , pointedAtTime;

   JMenuItem            time;


   /**
    *   Constructor for this Tag Frame
    *  Frame2Tag            The JFrame that the tag frame is attached to
    *  ViewerPixelRange_TaggedFrame  Range of Area to follow in pixels relative
    *                       to the Frame2Tag Window
    *  ViewerRowColRange   The range of Rows and Columns to display. These
    *                correspond to the rows and columns in data. This does 
    *                not correspond to all the rows and columns in data if 
    *                there is a zoom in action
    *  data            The data. data.getValue( row, col) is used to get the
    *                  values that are displayed
    *  TimeValues     The values in the axis perpendicular to the row/ col 
    *                 values.  Use setPointedAt to alter these TimeValues
    *  pointedAtRow    The row that is being pointedAt.
    *  pointedAtCol    The column that is being pointedAt
    *  pointedAtTime   The time being pointedAt
    *  
    *  NOTE: There are no listeners for when Frame2Tag is moved or resized, 
    *     You have to create these listemers along with those for zooming
    *     or changing the portion of data that is being viewed, and all the 
    *     data is changed then call the appropriate methods below to handle
    *     to execute the change.
    *   
    *  @see #SetTag2FrameChanged( JFrame )
    *  @see #setViewerPixelRange(JFrame,CoordBounds)
    *  @see #setViewerRowColRange( CoordBounds )
    *  @see #setPointedAtChanged(int row , int col , int timeChan ,float[] xvalues)
    *  @see #setNewData(IVirtualArray2D , CoordBounds)
    *  @see #actionPerformed( ActionEvent )
    *     
    *     
    *   NOTE:  There is a utility, getTagFrame, that returns a TagFrame with
    *   Component listeners for the JFrame and interior Component
    *   
    *   @see #getTagFrameInstance( Container, CoordBounds,IVirtualArray2D,
    *                     float[],int,int,int)
    *   
    *   
    */
   public GraphTagFrame( JFrame Frame2Tag,
                         CoordBounds ViewerPixelRange_TaggedFrame,
                         CoordBounds ViewerRowColRange, 
                         IVirtualArray2D data,
                         float[] TimeValues, 
                         int pointedAtRow, 
                         int pointedAtCol,
                          int pointedAtTime ) {

      super();
      Mode = TIME;
      this.Frame2Tag = Frame2Tag;
      this.ViewerPixelRange_TaggedFrame = arrangeIncreasing( 
                                       ViewerPixelRange_TaggedFrame );
      this.ViewerRowColRange = arrangeIncreasing( ViewerRowColRange );
      this.data = data;
      
      this.TimeValues = TimeValues;
      this.pointedAtRow = pointedAtRow - 1;
      this.pointedAtCol = pointedAtCol - 1;
      this.pointedAtTime = pointedAtTime;
      
      Rectangle B = Frame2Tag.getBounds();
      Frame2TagRange = new CoordBounds( B.x , B.y , B.x + B.width , B.y
               + B.height );
      
      if( TimeValues == null )
         Mode = ROW;
      
      getContentPane().setLayout( new GridLayout( 1 , 1 ) );
      
      JMenu jm = new JMenu( " Mode" );
      ButtonGroup Buttons = new ButtonGroup();
      
      JMenuItem row = new JMenuItem( ROW );
      JMenuItem col = new JMenuItem( COL );
      time = new JMenuItem( TIME );
      
      Buttons.add( row );
      Buttons.add( col );
      Buttons.add( time );
      
      jm.add( row );
      jm.add( col );
      jm.add( time );
      
      row.addActionListener( this );
      col.addActionListener( this );
      time.addActionListener( this );
      if( time != null ) {
         time.setSelected( true );
      }
      else {
         row.setSelected( true );
         time.setEnabled( false );
      }

      JMenuBar bar = new JMenuBar();
      bar.add( jm );
      this.setJMenuBar( bar );


      setTitle( Mode );

      setGraph();
      setPosition();
   }

   
   
   // Sets up the graph to be shown in the TagFrame
   private void setGraph() {

      int length = 0;
      
      if( Mode == TIME )
         
         if( TimeValues == null ) {
            length = 0;
            return;
         }
         else
            length = TimeValues.length;
      
      else if( Mode == ROW )
         
         length = (int) ( ViewerRowColRange.getX2() - 
                                          ViewerRowColRange.getX1() );
      
      else if( Mode == COL )
         
         length = (int) ( ViewerRowColRange.getY2() - 
                                         ViewerRowColRange.getY1() );
      
      else
         return;
      
      
      length = Math.abs( length );
      float[] graphValues = new float[ length ];
      float[] xvals = new float[ length ];
      float miny = Float.NaN , maxy = Float.NaN;

      if( Mode == TIME ) {
         
         graphValues = TimeValues;


         for( int i = 0 ; i < length ; i++ ) {
            xvals[ i ] = i;
            if( Float.isNaN( miny ) )
               
               miny = maxy = TimeValues[ 0 ];
            
            else if( TimeValues[ i ] < miny )
               
               miny = TimeValues[ i ];
            
            else if( TimeValues[ i ] > maxy )
                                                                                              
               maxy = TimeValues[ i ];

         }
         
      }
      else if( Mode == ROW ) {
         
         int start = (int) ( ViewerRowColRange.getX1() + .5 );

         for( int col = (int) ( ViewerRowColRange.getX1() + .5 ) ; col <= 
                                      ViewerRowColRange.getX2() ; col++ ) {
            
            graphValues[ col - start ] = data.getDataValue( pointedAtRow, col);
            xvals[ col - start ] = col;        
                     
            if( Float.isNaN( miny )) 
               
               miny = maxy = graphValues[ col - start ];
            
            else if( graphValues[ col - start ] < miny )
               
               miny = graphValues[ col - start ];
            
            else if( graphValues[ col - start ] > maxy )
               
               maxy = graphValues[ col - start ];

         }

      }
      else if( Mode == COL ) {
         
         // row=0 on GraphJPanel is at the bottom. Do another invert
         int start = (int) ViewerRowColRange.getY2();

         for( int row = (int) ViewerRowColRange.getY2() ; row >= ViewerRowColRange
                  .getY1() ; row-- ) {
            
            graphValues[ - ( row - start ) ] = data.getDataValue( row ,
                     pointedAtCol );
            xvals[ - ( row - start ) ] = - ( row - start );
            
            if( Float.isNaN( miny ) )
               
               miny = maxy = graphValues[ - ( row - start ) ];
            
            else if( graphValues[ - ( row - start ) ] < miny )
               
               miny = graphValues[ - ( row - start ) ];
            
            else if( graphValues[ - ( row - start ) ] > maxy )
               
               maxy = graphValues[ - ( row - start ) ];
            
         }

      }
      
      else
         
         return;

      graph = new GraphJPanel();
      
      if( ! Mode.equals( COL ) ) {
         
         graph.setX_bounds( xvals[ 0 ] + .5f , xvals[ xvals.length - 1 ] - .5f );
         graph.setData( xvals , graphValues );
         graph.setY_bounds( miny , maxy );
         
      }
      else {
         
         graph.setData( graphValues , xvals );
         graph.setY_bounds( xvals[ 0 ] - .5f , xvals[ xvals.length - 1 ] + .5f );
         graph.setX_bounds( miny , maxy );
         
      }

      // INdicate pointed At??????? in the graph
      getContentPane().removeAll();

      getContentPane().add( graph );
      invalidate();
      repaint();
      validate();
      repaint();

   }


   
   //Arranges coord so x1<x2 and y1<y2
   private CoordBounds arrangeIncreasing( CoordBounds coord ) {

      if( coord == null )
         return null;
      
      float x1 , x2 , y1 , y2;
      
      if( coord.getX1() > coord.getX2() ) {
         
         x1 = coord.getX2();
         x2 = coord.getX1();
         
      }
      else {

         x2 = coord.getX2();
         x1 = coord.getX1();
         
      }
      if( coord.getY1() > coord.getY2() ) {
         
         y1 = coord.getY2();
         y2 = coord.getY1();
         
      }
      else {

         y2 = coord.getY2();
         y1 = coord.getY1();
         
      }
      
      return new CoordBounds( x1 , y1 , x2 , y2 );
   }


   
   
   
   /**
    * This method is used only if the Frame that is tagged is moved but not
    * resized.
    * 
    * @param Frame2Tag
    *           The frame that is tagged If the frame is resized and moved use
    *           setViewerPixelRange
    */
   public void SetTag2FrameChanged( JFrame Frame2Tag ) {

      if( Frame2Tag == null )
         return;
      
      Rectangle R = Frame2Tag.getBounds();
      
      if( R.x == Frame2TagRange.getX1() )
         if( R.y == Frame2TagRange.getY1() )
            if( R.width == Frame2TagRange.getX2() - Frame2TagRange.getX1() )
               if( R.height == Frame2TagRange.getY2() - Frame2TagRange.getY1() )
                  return;
      
      Frame2TagRange = new CoordBounds( R.x , R.y , R.x + R.width , R.height
               + R.y );
      
      setPosition();

   }

   
   

   /**
    * This method is invoked when a frame is resized.
    * 
    * @param Frame2Tag
    *           The frame that is tagged
    * @param ViewerPixelRange
    *           the new position of the image to be paralleled.
    */
   public void setViewerPixelRange( JFrame Frame2Tag ,
            CoordBounds ViewerPixelRange ) {

      if( Frame2Tag == null )
         return;
      
      if( ViewerPixelRange == null )
         return;

      Rectangle R = Frame2Tag.getBounds();
      
      if( this.ViewerPixelRange_TaggedFrame.equals( ViewerPixelRange ) )
         if( R.x == Frame2TagRange.getX1() )
            if( R.y == Frame2TagRange.getY1() )
               if( R.width == Frame2TagRange.getX2() - Frame2TagRange.getX1() )
                  if( R.height == Frame2TagRange.getY2()
                           - Frame2TagRange.getY1() )
                     return;
      
      if( ! CheckBounds( new CoordBounds( R.x , R.y , R.x + R.width , R.y
               + R.height ) , 0f , 0f , Float.POSITIVE_INFINITY ,
               Float.POSITIVE_INFINITY ) )
         return;
      

      Frame2TagRange = new CoordBounds( R.x , R.y , R.x + R.width , R.y
               + R.height );
      
      if( ! CheckBounds( ViewerPixelRange , 0 , 0 , R.x + R.width , R.y
               + R.width ) ) {
         
         setPosition();
         return;
         
      }
      
      this.ViewerPixelRange_TaggedFrame = arrangeIncreasing( ViewerPixelRange );
      setPosition();
   }


   
   
   private boolean CheckBounds( CoordBounds Rect , 
                                float x1 , 
                                float y1 ,
                                float x2 , 
                                float y2 ) {

      if( Rect == null )
         return false;
      
      if( Float.isNaN( x1 ) || Float.isNaN( x2 ) || Float.isNaN( y1 )
               || Float.isNaN( y2 ) )
         return false;
      
      if( Rect.getX1() < x1 )
         
         return false;
      
      if( Rect.getY1() < y1 )
         
         return false;
      
      if( Rect.getX2() > x2 )
         
         return false;
      
      if( Rect.getY2() > y2 )
         
         return false;
      
      return true;

   }


   
   
   /**
    * Invoke this method if the graph is zoomed or a sub or super range of
    * values are intended to be viewed. The position of the view in the tagged
    * frame will not change
    * 
    * @param ViewerRowColRange
    *           the new position of the image to be paralleled.
    */
   public void setViewerRowColRange( CoordBounds ViewerRowColRange ) {

      
              
      if( ! CheckBounds( ViewerRowColRange , - .5f , - .5f ,
               data.getNumRows() - .5f , data.getNumColumns() - .5f ) )
         return;
      
      this.ViewerRowColRange = arrangeIncreasing( ViewerRowColRange );
      
      setGraph();

   }


   
   /**
    * Invoke this method if the pointed at changes
    * 
    * @param row
    *           the new row of interest
    * @param col
    *           the new column of interest
    * @param timeChan
    *           the new channel( not used yet) of interest
    */
   public void setPointedAtChanged( int row , int col , int timeChan ,
            float[] xvalues ) {

      if( pointedAtRow == row - 1 )
         if( pointedAtCol == col - 1 )
            // if( pointedAtTime == timeChan)
            return;
      
      if( row < 1 || col < 1 )
         return;
      
      if( row > data.getNumRows() || col >= data.getNumColumns() )
         return;
      
      if( TimeValues != null )
         if( timeChan < 0 || timeChan >= TimeValues.length )
            return;

      pointedAtRow = row - 1;
      pointedAtCol = col - 1;
      pointedAtTime = timeChan;
      TimeValues = xvalues;
      
      if( xvalues == null )
         
         time.setEnabled( false );
      
      else
         
         time.setEnabled( true );
      
      setGraph();

   }


   
   
   private void setPosition() {

      Insets ins = getInsets();
//      Insets FrameInsets = Frame2Tag.getInsets();
      int x = - 1 , 
          y = - 1 , 
          width = - 1 , 
          height = - 1;
      
      if( Mode == TIME ) {
         
         x = (int) Frame2TagRange.getX1();
         y = (int) Frame2TagRange.getY2();// +ins.bottom;
         width = (int) ( Frame2TagRange.getX2() + ins.right - Frame2TagRange
                  .getX1() );// +ins.left);
         
         height = (int) ( ( Frame2TagRange.getY2() - Frame2TagRange.getY1() ) / 3 + .5 );
         
      }
      else if( Mode == ROW ) {

         x = (int) ( Frame2TagRange.getX1()
                  + ViewerPixelRange_TaggedFrame.getX1() - ins.left );
         y = (int) ( Frame2TagRange.getY2() );// +ins.bottom);
         
         width = (int) ( ViewerPixelRange_TaggedFrame.getX2()
                  - ViewerPixelRange_TaggedFrame.getX1() + ins.left + ins.right );
         height = (int) ( ( Frame2TagRange.getY2() - Frame2TagRange.getY1() ) / 3 + .5 );
         
      }
      else if( Mode == COL ) {
         
         width = (int) ( ( Frame2TagRange.getX2() - Frame2TagRange.getX1() ) / 4 + .5 );
         x = (int) ( Frame2TagRange.getX1() - width );// -ins.right -ins.left
         
         JMenuBar jmen = getJMenuBar();
         int MenSize = jmen.getHeight();

         y = (int) ( Frame2TagRange.getY1()
                  + ViewerPixelRange_TaggedFrame.getY1() - ins.top - (float) MenSize );
         height = (int) ( ViewerPixelRange_TaggedFrame.getY2()
                  - ViewerPixelRange_TaggedFrame.getY1() + ins.top + ins.bottom + MenSize );
         
      }

      setBounds( x , y , width , height );
      invalidate();
      doLayout();
      validate();
      invalidate();
      repaint();

   }

   

   /**
    * 
    * Use if the data in the cells change and/or the total number of rows or
    * columns in the whole array change
    * 
    * @param arr
    *           The data to be used
    * @param RCbounds
    *           The new total number of rows/colums for the new array. (-.5,
    *           -.5, ncols+.5 , nrows +.5)
    */
   public void setNewData( IVirtualArray2D arr , CoordBounds RCbounds ) {

      if( arr == null )
         return;
      
      CoordBounds C = arrangeIncreasing( RCbounds );
      
      if( RCbounds != null ) {
         C = RCbounds.intersect( new CoordBounds( - .5f , - .5f , arr
                  .getNumColumns() , arr.getNumRows() ) );
      }
      

      this.data = arr;
      if( C != null )
         this.ViewerRowColRange = C.intersect( ViewerRowColRange );

      setGraph();
      
   }


   
   /**
    * This method can be used to update the the frame if evt's
    * action command is not  Row, Col, or Time
    * 
    * @param evt  The event to be processed.
    */
   public void actionPerformed( ActionEvent evt ) {

      if( evt.getActionCommand() == ROW ) {
         if( Mode == ROW ) return;
         Mode = ROW;
      }
      else if( evt.getActionCommand() == COL ) {
         if( Mode == COL ) return;

         Mode = COL;

      }
      else if( evt.getActionCommand() == TIME ) {
         if( Mode == TIME ) return;
         Mode = TIME;
      }
      setTitle( Mode );
      setGraph();
      setPosition();


   }


   
   
   private TaggedFrameListener getTaggedFrameListener( GraphTagFrame C ) {

      return new TaggedFrameListener( C );
   }

   
   
   

   /**
    *   Return a new GraphTagFrame with several listeners added to take
    *   care of  moving and resizing the Container to be tagged.
    *  NOTE: The container must be inside a JFrame( currently)
    *  
    *  C              The Container whose boundaries the tag frame tags.
    *  ViewerRowColRange   The range of Rows and Columns to display. These
    *                correspond to the rows and columns in data. This does 
    *                not correspond to all the rows and columns in data if 
    *                there is a zoom in action
    *  data            The data. data.getValue( row, col) is used to get the
    *                  values that are displayed
    *  TimeValues     The values in the axis perpendicular to the row/ col 
    *                 values.  Use setPointedAt to alter these TimeValues
    *  pointedAtRow    The row that is being pointedAt.
    *  pointedAtCol    The column that is being pointedAt
    *  pointedAtTime   The time being pointedAt
    *  
    *  NOTE: You still need listeners for zooming and when the data is 
    *     changed.
    *   
    *
    *  @see #setViewerRowColRange( CoordBounds )
    *  @see #setPointedAtChanged(int row , int col , int timeChan ,float[] xvalues)
    *  @see #setNewData(IVirtualArray2D , CoordBounds)
    *  @see #actionPerformed( ActionEvent )
    *     
    */
   public static GraphTagFrame getTagFrameInstance( Container C ,
            CoordBounds ViewerRowColRange , IVirtualArray2D data ,
            float[] timeValues , int pointedAtRow , int pointedAtCol ,
            int pointedAtTime ) {

      CoordBounds PixelRange = new CoordBounds();
      JFrame jf = getJFrameWsubPixelBounds( C , PixelRange );
      if( jf == null ) return null;
      
      
      GraphTagFrame Res = new GraphTagFrame( jf , PixelRange ,
               ViewerRowColRange , data , timeValues , pointedAtRow ,
               pointedAtCol , pointedAtTime );
      
      
      if( Res == null ) return null;
      
      C.addComponentListener( Res.getTaggedFrameListener( Res ) );
      jf.addComponentListener( Res.getTaggedFrameListener( Res ) );
      
      return Res;
   }


   public static JFrame getJFrameWsubPixelBounds( Container basePanel ,
            CoordBounds Result ) {

      Container C = basePanel;
      JFrame jf = null;
      
      int x_offset = 0;
      int y_offset = 0;
      
      for( int i = 0 ; i < 25 && jf == null && C != null ; i++ ) {
         
         Container C1 = C.getParent();
         if( C1 == null )
            
            C = C1;
         
         else {
            
            x_offset += C.getX() + C.getInsets().left;
            y_offset += C.getY() + C.getInsets().top;
            C = C1;
            if( C1 instanceof JFrame ) jf = (JFrame) C1;
            
         }
         
      }
      
      if( jf == null ) return null;
      
      if( Result != null )
         Result.setBounds( x_offset , y_offset , x_offset
                  + basePanel.getWidth() , y_offset + basePanel.getHeight() );
      
      return jf;
   }


}


class TaggedFrameListener extends ComponentAdapter {

   GraphTagFrame TagFrame;


   public TaggedFrameListener( GraphTagFrame TagFrame ) {

      this.TagFrame = TagFrame;
   }


   public void componentResized( ComponentEvent e ) {


      if( e.getSource() instanceof Container )
         if( ! (e.getSource() instanceof JFrame)) { //Will do two moves when JFrame moves
         
         CoordBounds RC = new CoordBounds();
         
         JFrame jf = GraphTagFrame.getJFrameWsubPixelBounds( (Container) e
                  .getSource() , RC );
         
         if( jf == null ) return;

         TagFrame.setViewerPixelRange( jf , RC );
      }
   }


   public void componentMoved( ComponentEvent e ) {

      if( ! ( e.getSource() instanceof JFrame ) ) return;
      
      TagFrame.SetTag2FrameChanged( (JFrame) ( e.getSource() ) );

   }
 
}
