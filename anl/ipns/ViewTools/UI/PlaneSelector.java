/*
 * File:  .java 
 *             
 * Copyright (C) 2003, Ruth Mikkelson
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
 * Revision 1.3  2004/03/12 00:23:57  serumb
 * Changed Package.
 *
 * Revision 1.2  2004/01/22 02:06:03  bouzekc
 * Removed unused imports.
 *
 * Revision 1.1  2003/05/19 15:15:39  rmikk
 * Initial Checkin
 *
 */

package gov.anl.ipns.ViewTools.UI;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.awt.event.*;


/**
*  This class is a button that pops a JFrame to allow for selecting planes in
*  a 3D set of data
*/
public class PlaneSelector extends JButton  implements ActionListener{

   /**
   *  Card Types
   */
   public static final int  CARD_3POINT_PLANE = 0;
   public static final int  CARD_DIRECTION    =1; 
   public static final int NCARD_TYPES = 2;


   int[] cards;                              //card[i] contains the card type of the ith card
   int[] cardType_to_pos = new int[NCARD_TYPES];
   String[] cardType_to_name ={"3 Points","Direction"};
   MJPanel[] Layer;
 
   float[][] points ;// the 4 points for the plane. The 0th- 2nd is one plane.
                     

   /**
   *   Constructor
   * @param Button_label   The string that is the text on the button and title of the JFrame
   */
   public PlaneSelector( String Button_label)
      { 
       super( Button_label);

       cards = null;
      
       points = null;
       addActionListener( this);
       Layer = null;
       Arrays.fill(cardType_to_pos, -1);;
      
      }

   /*
   * Constructor.  The label on the button and Frame is "Select Plane(s)"
   */
   public PlaneSelector()
      {
       this( "Select Plane(s)");

      }

    boolean showing= false;
    JTabbedPane jtabPane=null;
    /**
    *   Sets up the JFrame for the complex selections that determine a plane
    *   through the 3D data.
    */
    public void actionPerformed( ActionEvent evt)
      {
       if( showing)
          return;
       JFrame jf = new JFrame( evt.getActionCommand());
       jf.addWindowListener( new MyWindowListener());
       jtabPane = new JTabbedPane();
       if( Layer != null)
          for( int i=0; i< Layer.length ; i++)
            { Layer[i].add( Box.createVerticalGlue() );
              jtabPane.add( Layer[i], cardType_to_name[i]);
             }
       jf.getContentPane().add(jtabPane);
       jf.setDefaultCloseOperation( javax.swing.WindowConstants.DISPOSE_ON_CLOSE );


       jf.setSize( 400,600);
       jf.show();
       showing = true;
       jf.invalidate();

      }
     
   private class MyWindowListener extends WindowAdapter
      {

       public void windowClosed(WindowEvent e)
          {
             showing = false;
            

            }
      } //MyWindowListener 


   /**
   *  Adds a new tabbed pane of the given CardType
   *  @param  CardType  The type of card, eg.CARD_3POINT_PLANE or CARD_DIRECTION
   *
   *NOTE: A maximum of one CardType can occur in the tabbed pane
   */
   public void add( int CardType)
      {
        
       int L=1;
       if( cards != null)
           L = cards.length + 1;
       int[] cards1 = new int[ L];
       if( cards != null)
           java.lang.System.arraycopy( cards,0,cards1,0,L-1);
       cards1[L-1]= CardType;
       cards = cards1;
       
       MJPanel[] Layer1 = new MJPanel[L];
       if( Layer != null )
           java.lang.System.arraycopy( Layer,0,Layer1,0,L-1);
       MJPanel mj=  createNewCard( CardType);
       if( mj== null)
         return;
       Layer1[ L-1] = mj;
       Layer = Layer1;
       cardType_to_pos[CardType] = L-1;
         
      }


   /**
   *  Appends any jpanel to the bottom(BoxLayout) to the tab of the given CardType. 
   *  @param CardType  the CardType of the tab pane, e.g. CARD_3POINT_PLANE
   *  @param jpanel    The JPANEL that will be added to the bottom of this card(tab)
   */
   public void append( int CardType, JPanel  jpanel)
      {
       if( CardType < 0)
           return;
       if( CardType >= NCARD_TYPES )
           return;
       int cardPos = cardType_to_pos[ CardType] ;
       if( Layer == null)
           return;
       if( Layer.length < cardPos)
           return;
       Layer[cardPos].add( jpanel);


      }

    // Creates a new card of the given CardType
    private MJPanel createNewCard( int CardType)
      {

       if( CardType < 0)
         return null;
       if( CardType > NCARD_TYPES)
          return null;
       if(cardType_to_pos[ CardType] >=0)
          return null;
       if( CardType == CARD_3POINT_PLANE)
           return new Plane3Points();
       else
           return new PlaneStep();
      }

    


 //Will send action event whose command is "POINT_CHANGED"
    /**
    *  Adds an ActionListener that will be notified when the corresponding point is changed
    *  with an action command "POINT_CHANGED"
    *  @param ptListener  the point listener
    *  @param CardType    the type of card of the tabbed pane, eg. CARD_3POINT_PLANE
    *  @param PointPos    Determines which of the points on the tabbed pane is to be used
    */
    public void addPointListener( ActionListener ptListener, int CardType, int PointPos)
      {
       if( CardType < 0)
           return;
       if( CardType >= NCARD_TYPES )
           return;
       int cardPos = cardType_to_pos[ CardType];
       if( Layer == null)
           return;
       if( Layer.length < cardPos)
          return;
       Layer[cardPos].addPointListener( ptListener, PointPos);
      }


    private Vector PlaneListeners = new Vector();
    /**  
     *  When the Plane changes it will notify these listeners with the
     *  ActionCommand  "PLANE_CHANGED"
     */
    public void addPlaneChangeListener( ActionListener PlListener)
      {
       PlaneListeners.addElement( PlListener);
      }


    /**
    *   Sets the coordinates of the point corresponding to the PointPos'th point in the tabbed pane
    *   corresponding to the given CardType.  The new values are stored in the values array
    */
    public void setPoint( int CardType, int PointPos, float[] values)
      {
       if( CardType < 0)
          return;
       if( CardType >= NCARD_TYPES )
          return;
       int cardPos = cardType_to_pos[ CardType];
       if( Layer == null)
          return;
       if( Layer.length < cardPos)
          return;
       Layer[cardPos].setPoint( PointPos, values);

      }

    /**
    *  Gets the point on the tabbed pane with the corresponding CardType and position PointPos
    */
    public float[] getPoint(int CardType, int PointPos)
      {
       if( CardType < 0)
          return null;
       if( CardType >= NCARD_TYPES )
          return null;
       int cardPos = cardType_to_pos[ CardType];
       if( Layer == null)
         return null;
       if( Layer.length < cardPos)
         return null;
       return Layer[cardPos].getPoint( PointPos);

      }

    /**
    *   Sets the labels for the coordinates of the points, etc.
    *
    *   NOTE: Currently the coordinates are labelled Qx, Qy, and Qz
    */
    public void setLabel( int CardType, int LabelPos, String[] values)
      {
       if( CardType < 0)
         return;
       if( CardType >= NCARD_TYPES )
         return;
       int cardPos = cardType_to_pos[ CardType];
       if( Layer == null)
          return;
       if( Layer.length < cardPos)
          return;
       Layer[cardPos].setLabel( LabelPos, values);

      }


    /**
    *    All Tabbed planes must extend MJPanel
    */ 
    abstract class MJPanel  extends JPanel
      { 
       JLabel[] Labell = new JLabel[3];  //Labels the 1st,2nd,and 3rd coordinates
       
       /**
       *   Constructor for MJPanel, a JPanel with a vertical BoxLayout and methods to
       *   get and set points
       */
       public MJPanel()
         { 
          super();
          BoxLayout bl = new BoxLayout( this, BoxLayout.Y_AXIS);
          this.setLayout( bl);
          Labell[0]= new JLabel("Qx", javax.swing.SwingConstants.CENTER);
          Labell[1]= new JLabel("Qy", javax.swing.SwingConstants.CENTER);
          Labell[2]= new JLabel("Qz", javax.swing.SwingConstants.CENTER);
         }

       /**
       *     Sets the LabelPos'th label to the corresponding values
       */
       public void setLabel( int LabelPos, String[] values)
         {
          if( LabelPos < 0)
            return;
          if( LabelPos >0)
            return;
          if(values == null)
            return;
          for( int i = 0; i< 3; i++)
            if(values.length > i)
              Labell[i].setText( ""+ values[i]);
         }

       /**
       *   Gets the value of the PointPos'th point 
       */
       public abstract float[] getPoint( int PointPos);

       /**
       *   Sets the PointPos'th point to the values in values
       */
       public abstract void setPoint(  int PointPos, float[] values);

       /**
       *  Adds a ActionListener for point changes.  The actionCommand is "POINT_CHANGED"
       */
       public abstract void addPointListener( ActionListener ptListener, int PointPos);

      }

    /**
    *   Allows entering or selecting 3 points to determine a plane
    */
    class Plane3Points extends MJPanel  implements ActionListener 
      {
       JTextField[][] points = new JTextField[3][3];
    
       JButton[] Point = new JButton[3];
       JButton  OK ;
       public Plane3Points()
         {
          super();
          JPanel  jp = new JPanel( new GridLayout( 5,4));
          jp.add( new JLabel(""));

          for( int i=0; i<3;i++)
            Point[i]= new JButton("Point "+(i+1));

          for( int i=0;i<3; i++)
            jp.add(Labell[i]);

          for (int i=0;i<3;i++)
           for(int j=0; j<3; j++)
             {points[i][j]= new JTextField(12);
              if( j==0)
               jp.add( Point[i]);
              jp.add( points[i][j]);
              }
          OK = new JButton( "OK");
          OK.addActionListener( this);
          jp.add(OK);
          jp.add( new JLabel(""));
          jp.add( new JLabel(""));
          this.add( jp);
         }  
  

       public float[] getPoint( int PointPos)
         {
          float[] vals = new float[3];
          if( PointPos < 0)
            return null;
          if( PointPos > 2)
            return null;
          try{
          for( int i=0; i< 3; i++)
            vals[i] = (new Float( points[PointPos][i].getText())).floatValue();
             }
          catch( Exception s)
             {
               return null;
             }
          return vals;
         }


       public void setPoint(  int PointPos, float[] values)
         {
          if( PointPos < 0)
             return;
          if( PointPos >2)
             return;
          if( values == null)
             return;
          if( values.length < 3)
             return;
          for( int i=0; i< 3; i++)
            points[PointPos][i].setText(  (new Float( values[i])).toString());

          //notifyPlaneListeners();
         }

       public void addPointListener( ActionListener ptListener, int PointPos) 
         { 
          if( PointPos <0)
            return;
          if( PointPos >2)
            return;
          Point[PointPos].addActionListener( new PointChangeListener(ptListener) );

         } 


       public void notifyPlaneListeners()
         {
          for( int i=0; i< PlaneListeners.size(); i++)
            ((ActionListener)(PlaneListeners.elementAt(i))).actionPerformed(
                  new ActionEvent( this, ActionEvent.ACTION_PERFORMED,"PLANE_CHANGED"));
 
         }


       public void actionPerformed( ActionEvent evt)
         {
           notifyPlaneListeners();
         }       
      }//Plane3Points

    class PlaneStep extends MJPanel  implements ActionListener
      {
       JTextField[][] Points = new JTextField[3][3];
       JButton[] Buttons = new JButton[3];

       public PlaneStep()
         {
          super();
          for( int i=0; i<3; i++)
             for( int j=0; j<3; j++)
                Points[i][j] = new JTextField("");
          String[] Blabels={"Start Point","End Point","Direction"};
          for( int i=0; i<3; i++)
             Buttons[i] = new JButton( Blabels[i] ); 

          JPanel jp = new JPanel( new GridLayout(5,4));
          jp.add( new JLabel(""));
          for( int i=0;i < 3; i++)
             jp.add(Labell[i]);
          for( int i = 0; i < 4; i++)
             {
              if( i ==2)
                 jp.add( new JLabel(""));
              else if( i<2)
                 jp.add( Buttons[i]);
              else
                 jp.add( Buttons[ i-1 ]);
               
              for( int j=0;j<3;j++)
                 if( i ==2)
                    jp.add( new JLabel(""));
                 else if( i<2)
                    jp.add( Points[i][j]);
                 else
                   jp.add( Points[ i-1 ][j]);   


             }
          Buttons[2].addActionListener( this );
          this.add(jp);
         }
 
     

       public float[] getPoint( int PointPos)
         {
          float[] Res = new float[3];
          if( PointPos < 0)
             return null;
          if( PointPos >2)
             return null;
          try{
          for( int i=0; i<3; i++)
             Res[i] = (new Float( Points[PointPos][i].getText())).floatValue();
             }
          catch( Exception s)
             {
              return null;
             }
          return Res;

         }


       public void setPoint(  int PointPos, float[] values)
         {
          if( PointPos < 0)
             return;
          if( PointPos > 2)
             return;
          if( values == null)
             return;
          for( int i=0; i< values.length; i++)
             Points[PointPos][i].setText( ""+values[i]);
         }


       public void addPointListener( ActionListener ptListener, int PointPos)
         {  
          if( PointPos < 0)
             return;
          if( PointPos > 2)
             return;
          Buttons[ PointPos].addActionListener( new PointChangeListener( ptListener) );
         }

       public void actionPerformed( ActionEvent evt)
         { 
          try{
            for( int j=0; j<3; j++)
              Points[2][j].setText( (new Float(
                                      new Float( Points[1][j].getText()).floatValue() -
                                       new Float( Points[0][j].getText()).floatValue())). 
                                      toString()
                                 ); 
              }
          catch( Exception ss){}
           
           
         }

      }//PlaneStep



    class PointChangeListener  implements ActionListener
      {
       ActionListener actList;
       public PointChangeListener( ActionListener actList)
         { 
          this.actList = actList;
         }


       public void actionPerformed( ActionEvent evt)
         {
          actList.actionPerformed( new ActionEvent(evt.getSource(), evt.getID(),"POINT_CHANGED") );

         }
      }//PointChangeListener
  
    public static float[] readFloat()
      {
       char c;
       float[] v = new float[3];
       try{
       
       for( int i=0; i<3; i++)
         {

          String S ="";
          for( c = (char)System.in.read(); c>='0' || c<='9'||c =='.';
                    c = (char)System.in.read())
             S+=c;
          if( S.length() < 1)
             return v;
          v[i] = ( new Float( S)).floatValue();
          if( c <' ')
             return v;
         }
       return v;
       }
       catch( Exception s)
         { 
          return v;
         }    
      }

    /**
    *   Test program for the PlaneSelector
    */
    public static void main( String args[])
      {
       JTextField[] jtf = new JTextField[3];
       JTextField card ;
       JTextField pos ;
       JButton get, send;
       JFrame jf = new JFrame("Test");
       jf.setSize( 600,600);
       PlaneSelector ps = new PlaneSelector();
       ps.add( PlaneSelector.CARD_3POINT_PLANE );
       ps.add( PlaneSelector.CARD_DIRECTION );
       
       jf.getContentPane().setLayout( new GridLayout( 7, 2));
       jf.getContentPane().add(ps);
       jf.getContentPane().add( new JLabel());
       jf.getContentPane().add( new JLabel("1st float"));
       jtf[0] = new JTextField( 8);
       jf.getContentPane().add( jtf[0]);
       jf.getContentPane().add( new JLabel("2nd float"));
       jtf[1] = new JTextField( 8);
       jf.getContentPane().add( jtf[1]);
       jf.getContentPane().add( new JLabel("3rd float"));
       jtf[2] = new JTextField( 8);
       jf.getContentPane().add( jtf[2]);
     
       jf.getContentPane().add( new JLabel("cardtype"));
       card = new JTextField( 8);
       jf.getContentPane().add( card);
       jf.getContentPane().add( new JLabel("pos"));
       pos = new JTextField( 8);
       jf.getContentPane().add( pos);

       get  = new JButton( "Get");
       send = new JButton("Send");
        jf.getContentPane().add( get);
       jf.getContentPane().add(send);
      
       get.addActionListener( new MgetActionListener( jtf, card, pos, ps));

       send.addActionListener( new MsendActionListener( jtf,card,pos, ps));

       MAction act = new MAction();

       for( int i=0; i< 2; i++)
          for( int j=0; j< 3; j++)
             ps.addPointListener( act, i, j);
      
       ps.addPlaneChangeListener( act);
       
       jf.validate();
       jf.show();


      }//main


}

// Below are some listeners for retrieving info from the PlaneSelector

// notified from the plane and point listeners added to the PlaneSelector
class MAction implements ActionListener
   {
     public void actionPerformed( ActionEvent evt)
      {
        System.out.println("in actionPerformed "+ evt.getActionCommand());
        System.out.println("    source="+evt.getSource());

       }

    }

// From test get button.  Example of how to get data from a PlaneSelector
class MgetActionListener implements ActionListener
  { 
   JTextField[] jtf;
   JTextField card,pos;
   PlaneSelector ps;
   public MgetActionListener( JTextField[] jtf,JTextField  card,JTextField  pos,
          PlaneSelector ps)
      {
       this.jtf = jtf;
       this.card = card;
       this.pos = pos;
        this.ps = ps;
      }

   public void actionPerformed( ActionEvent evt)
      {
        float[] v = ps.getPoint ((new Integer( card.getText())).intValue(),
                       (new Integer( pos.getText())).intValue());
        for( int i=0; i< 3; i++)
          jtf[i].setText(v[i]+""); 

      }
  }

//From test send button.  Example of how to get data to the Plane Selector
class  MsendActionListener  implements ActionListener
  {
   JTextField[] jtf;
   JTextField card,pos;
   PlaneSelector ps;
   public MsendActionListener( JTextField[] jtf,JTextField  card,JTextField  pos,
                               PlaneSelector ps)
      {
       this.jtf = jtf;
       this.card = card;
       this.pos = pos;
       this.ps = ps;
      }


    public void actionPerformed( ActionEvent evt)
      {

       float[] v = new float[3];
       for( int i=0; i< 3; i++)
          v[i] = (new Float( jtf[i].getText())).floatValue();
       for( int i = 0; i<3; i++)
          ps.setPoint( (new Integer( card.getText())).intValue(),
                       (new Integer( pos.getText())).intValue(), v);

      }
  }

