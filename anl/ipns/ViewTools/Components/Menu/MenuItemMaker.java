/*
 * File: MenuItemMaker.java
 *
 * Copyright (C) 2003, Mike Miller
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
 * Primary   Mike Miller <millermi@uwstout.edu>
 * Contact:  Student Developer, University of Wisconsin-Stout
 *
 * Contact : Dennis Mikkelson <mikkelsond@uwstout.edu>
 *           Department of Mathematics, Statistics and Computer Science
 *           University of Wisconsin-Stout
 *           Menomonie, WI 54751, USA
 *
 * This work was supported by the National Science Foundation under grant
 * number DMR-0218882, and by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 * $Log$
 * Revision 1.7  2004/08/06 18:52:12  millermi
 * - Added HTML to javadocs to make them legible.
 *
 * Revision 1.6  2004/04/29 06:26:24  millermi
 * - Added MarkerOverlay to overlay list.
 *
 * Revision 1.5  2004/03/11 23:52:51  rmikk
 * Fixed Package names
 *
 * Revision 1.4  2003/10/16 05:00:05  millermi
 * - Fixed java docs errors.
 *
 * Revision 1.3  2003/09/11 06:26:33  millermi
 * - Added static methods for ColorScaleMenu and OverlayMenu.
 * - Added functionality for multiple listeners.
 * - Added additional documentation for clarity.
 *
 * Revision 1.2  2003/08/14 20:38:34  millermi
 * - Additional javadoc comments provided by Chris Bouzek.
 *
 * Revision 1.1  2003/08/14 17:14:32  millermi
 * - Initial Version - Simplifies creating layered JMenu or JMenuItem by
 *   using Strings and Vectors.
 *
 */

package gov.anl.ipns.ViewTools.Components.Menu;

import java.util.Vector;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JMenuBar;
import javax.swing.JFrame;

import gov.anl.ipns.ViewTools.Panels.Image.IndexColorMaker;

/**
 * This class provides an easy way to build menu items with submenus. Using
 * two classes, either vectors or strings, a JMenuItem will be build. If the
 * vector element is a vector with more than one element, a JMenu is created.
 * Otherwise, if the element is a string or vector with one element, a JMenuItem
 * is created. A listener is required for each vector and this listener is
 * shared by all strings contained in that vector. 
 * Listeners are not assigned layer by layer, but rather vector by vector.
 * Layered vectors of the first "branch" will be assigned listeners prior to
 * any vectors in the second "branch". See example below for details.
 *
 * Example (This example demonstrates behavior of makeMenuItem(). The method
 *          makeMultiMenuItem() differs only in that the "names" vector would
 *          contain multiple "Trees", thus requiring no listener.)
 *<BR><BR>
 * Tree (vector)            // first listener here, unused listener <BR>
 * -TreeTitle (string)      // first string of the vector labels the JMenu<BR>
 * -Branch1 (vector)        // second listener here <BR>
 * --Branch1Title (string)  // first string of the vector labels the JMenu <BR>
 * --Branch1.a (string)     // first JMenuItem in Branch1, uses 2nd listener<BR>
 * --BranchI (vector)	    // third listener here <BR>
 * ---BranchITitle (string) // first string of vector labels the JMenu <BR>
 * ---BranchI.a (string)    // first JMenuItem in BranchI, uses 3rd listener<BR>
 * ---BranchI.b (string)    // second JMenuItem in BranchI,uses 3rd listener<BR>
 * -Branch2 (vector)        // fourth listener here, unused listener <BR>
 * --Branch2Title (string)  // first string of the vector labels the JMenu <BR>
 * --BranchII (vector)      // fifth listener here <BR>
 * ---BranchII(string)      // if only one element, BranchII is a JMenuItem
 *                             that uses 5th listener <BR><BR>
 * ** NOTICE: Not all listeners are used. Listeners are only used if the JMenu
 *            contains at least one JMenuItem. If the JMenu contains other
 *            JMenus, the listener will not be used.
 *
 * The menu from the above would require five listeners and appear as follows:
 *<BR><BR>
 *    Tree -> Branch1 -> Branch1.a
 *                       BranchI -> BranchI.a
 *                                  BranchI.b
 *            Branch2 -> BranchII 
 */
public class MenuItemMaker
{
  static private Vector temp_listeners = new Vector();
  static private int listener_index = 0;

 /**
  * This method replaces the class ColorScaleMenu, a commonly used menu
  * for choosing color scales of an image.
  *
  *  @return al action listener for JMenu containing colorscale options
  */
  public static JMenu getColorScaleMenu(ActionListener al)
  { 
    Vector colorscale = new Vector();
    Vector listener = new Vector();
    colorscale.add("Color Scale...");
      colorscale.add(IndexColorMaker.HEATED_OBJECT_SCALE);
      colorscale.add(IndexColorMaker.HEATED_OBJECT_SCALE_2);
      colorscale.add(IndexColorMaker.GRAY_SCALE);
      colorscale.add(IndexColorMaker.NEGATIVE_GRAY_SCALE);
      colorscale.add(IndexColorMaker.GREEN_YELLOW_SCALE);
      colorscale.add(IndexColorMaker.RAINBOW_SCALE);
      colorscale.add(IndexColorMaker.OPTIMAL_SCALE);
      colorscale.add(IndexColorMaker.MULTI_SCALE);
      colorscale.add(IndexColorMaker.SPECTRUM_SCALE);
    
    listener.add(al);
    
    return (JMenu)makeMenuItem( colorscale, listener );
  }

 /**
  * This method provides a list of overlays, commonly used for help menu by view
  * components.
  *
  *  @return al action listener for JMenu containing list of overlays
  */
  public static JMenu getOverlayMenu(ActionListener al)
  { 
    Vector listener = new Vector();     
    Vector overlay = new Vector();
    overlay.add("Overlays");
      overlay.add("Annotation");
      overlay.add("Axis");
      overlay.add("Marker");
      overlay.add("Selection");
    
    listener.add(al);
    
    return (JMenu)makeMenuItem( overlay, listener );
  }

 /**
  * This method is used if each element of the Vector is to be placed under
  * separate menus or menu items. Each element in this parameter Vector will
  * have a corresponding JMenuItem returned in the array. A listener must exist
  * for each vector in the param. vector, but since the param. vector is not a
  * JMenu itself, no listener is required for the param. vector.
  *
  *  @param  names The names of the menu items.
  *  @param  listeners The ActionListener which listens to "clicks" on the menu.
  *  @return array of JMenuItems with names.size() elements.
  */
  public static JMenuItem[] makeMultiMenuItems( Vector names,
                                                Vector listeners )
  {
    int num_menus = names.size();
    JMenuItem[] menuitems = new JMenuItem[num_menus];
    listener_index = 0;
    temp_listeners = listeners;
    
    for( int i = 0; i < num_menus; i++ )
      menuitems[i] = getMenuItem( names.elementAt(i) );
      
    return menuitems;
  }
 
 /**
  * This method creates a JMenuItem for each of the vector elements. However,
  * all of the JMenuItems are then placed under a JMenu corresponding to the
  * first element of the vector. Thus, only one JMenuItem is returned. A 
  * listener must exist for the "names" vector and each additional vector in it.
  *
  * @param  names The names of the menu items.
  * @param  listeners The vector containing ActionListeners which listen
  *         to "clicks" on a JMenuItem.
  * @return JMenuItem which has names.size() elements.
  */
  public static JMenuItem makeMenuItem( Vector names, Vector listeners )
  {
    listener_index = 0;
    temp_listeners = listeners;
    return getMenuItem(names);
  }
  
 /**
  * This method uses recursion to create a JMenuItem heirarchy.
  *
  * @param  names The names of the JMenuItem elements.
  */
  private static JMenuItem getMenuItem( Object names )
  {
    JMenuItem menuitem;
    // this variable is required to make sure all JMenuItems within a JMenu
    // have the same listener
    int temp_listener_index = listener_index;
    // System.out.println("Init index: " + listener_index );
    
    // This will occur if the object passed is not of type Vector or String
    if( !( names instanceof Vector ) && !( names instanceof String ) )
      return new JMenuItem("MenuItemMaker Class Problem");
    if( names instanceof Vector )
    { 
      listener_index++;
      
      // if listener index is greater than the number of listeners, return
      // error JMenu
      if( listener_index > temp_listeners.size() )
        return menuitem = new JMenu("MenuItemMaker Insufficient Listeners");
      Vector vectemp = (Vector)names;
      if( vectemp.size() > 1 )
      {
        if( vectemp.elementAt(0) instanceof String )
	{
          menuitem = new JMenu( (String)vectemp.elementAt(0) );
	  vectemp.removeElementAt(0);
        }
	// this occurs if the first element of the vector is not a string
	else 
          menuitem = new JMenu("MenuItemMaker Format Problem");
        int temp_index = 0;
	for( int i = 0; i < vectemp.size(); i++ )
	{
	  // temp_index will keep track of the greatest index value
	  if( listener_index > temp_index )
	    temp_index = listener_index;
	  // if string, reset index to init index of this vector, this will
	  // ensure that all JMenuItems within a JMenu have the same listener.
	  if( vectemp.elementAt(i) instanceof String )
	    listener_index = temp_listener_index;
	  // else vector, use the max index so it can be incremented correctly
	  else
	    listener_index = temp_index;
	  //System.out.println("TempIndex/ListenerIndex: " + temp_index + "/" + 
	  //                   listener_index);
          menuitem.add( getMenuItem( vectemp.elementAt(i) ) );
	  
	  // at the end, reset the listener_index to the max value stored in
	  // temp_index
	  if( i == vectemp.size() - 1 )
            listener_index = temp_index;
	}
	return menuitem;
      }
      else
      {
        menuitem = new JMenuItem( (String)vectemp.elementAt(0) );
        menuitem.addActionListener( (ActionListener)temp_listeners.elementAt(
	                            temp_listener_index) );
        //System.out.println("else size = 1 index: " + temp_listener_index );
        return menuitem;
      }
    }
    // if string, then must be a menuitem.
    else
    {
      menuitem = new JMenuItem( (String)names );
      menuitem.addActionListener( (ActionListener)temp_listeners.elementAt(
	                          temp_listener_index) );
      //System.out.println((String)names + " : " + temp_listener_index );
      return menuitem;
    }
  }
  
 /*
  * Testing purposes only
  */
  public static void main( String args[] )
  {
    JFrame tester = new JFrame("MenuItemMaker Test");
    tester.setBounds(0,0,250,100);
    tester.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    JMenuBar menus = new JMenuBar();
    menus.add( new JMenu("Tree") );
    tester.setJMenuBar(menus);
    
    Vector trunk = new Vector();
    Vector branchA = new Vector();
    Vector branchB = new Vector();
    Vector branchC = new Vector();
    Vector branchD = new Vector();
    Vector branchE = new Vector();
    
    for( int i = 0; i < 6; i++ )
    {
      if( i < 3 )
        branchA.add("Branch A." + i );
      if( i < 4 )
        branchB.add("Branch B." + i );
      if( i < 5 )
        branchC.add("Branch C." + i );
      branchD.add("Branch D." + i );
    }
    
    // now branchD & C are submenus of branchA & B
    branchA.insertElementAt(branchD,1);
    branchB.add(branchC);
    
    // add a final menu to each branch
    branchA.add("Last BranchA");
    branchB.add("Last BranchB");
    branchC.add("Last BranchC");
    branchD.add("Last BranchD");
    branchE.add("Branch E");
    
    // these vectors will appear under Tree
    trunk.add(branchA);
    trunk.add(branchB);
    trunk.add(branchE);
    Vector listeners = new Vector();
    listeners.add( new MenuListener() );
    listeners.add( new MenuListener2() );
    listeners.add( new MenuListener3() );
    listeners.add( new MenuListener4() );
    listeners.add( new MenuListener5() );
    /* 
    JMenuItem[] branches = MenuItemMaker.makeMultiMenuItems( trunk, listeners );
    for( int b = 0; b < branches.length; b++ )
      menus.getMenu(0).add(branches[b]);
    
    ** comment out the code below to test makeMultiMenuItems() method called in
       comments above */
    listeners.add( new MenuListener() );
    trunk.insertElementAt("Main",0);
    JMenuItem branch = MenuItemMaker.makeMenuItem( trunk, listeners );
    menus.getMenu(0).add(branch);
    /* **end comments here */
    
    menus.getMenu(0).add(MenuItemMaker.getColorScaleMenu(new MenuListener() ) );
    menus.getMenu(0).add(MenuItemMaker.getOverlayMenu(new MenuListener2() ) );
    
    tester.setVisible(true);
  }
 
 /*
  * Only here for use by main() to test listeners.
  */   
  private static class MenuListener implements ActionListener
  {
    public void actionPerformed( ActionEvent ae )
    {
      System.out.println(ae.getActionCommand() + " pressed." );
    }
  }
 
 /*
  * Only here for use by main() to test listeners.
  */   
  private static class MenuListener2 implements ActionListener
  {
    public void actionPerformed( ActionEvent ae )
    {
      System.out.println(ae.getActionCommand() + " pressed2." );
    }
  }
 
 /*
  * Only here for use by main() to test listeners.
  */   
  private static class MenuListener3 implements ActionListener
  {
    public void actionPerformed( ActionEvent ae )
    {
      System.out.println(ae.getActionCommand() + " pressed3." );
    }
  }
 
 /*
  * Only here for use by main() to test listeners.
  */   
  private static class MenuListener4 implements ActionListener
  {
    public void actionPerformed( ActionEvent ae )
    {
      System.out.println(ae.getActionCommand() + " pressed4." );
    }
  }
 
 /*
  * Only here for use by main() to test listeners.
  */   
  private static class MenuListener5 implements ActionListener
  {
    public void actionPerformed( ActionEvent ae )
    {
      System.out.println(ae.getActionCommand() + " pressed5." );
    }
  }

}
