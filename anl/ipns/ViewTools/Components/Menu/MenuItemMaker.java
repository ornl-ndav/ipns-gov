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
 * Revision 1.2  2003/08/14 20:38:34  millermi
 * - Additional javadoc comments provided by Chris Bouzek.
 *
 * Revision 1.1  2003/08/14 17:14:32  millermi
 * - Initial Version - Simplifies creating layered JMenu or JMenuItem by
 *   using Strings and Vectors.
 *
 */

package DataSetTools.components.View.Menu;

import java.util.Vector;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JMenuBar;
import javax.swing.JFrame;

/**
 * This class provides an easy way to build menu items with submenus. 
 * Given a vector of strings, a JMenuItem is created for each string.
 * For each vector, a JMenu is produced with a submenu.
 */
public class MenuItemMaker
{
 /**
  * This method is used if each element of the Vector is to be placed under
  * separate menus or menu items. Each element in this parameter Vector will
  * have a corresponding JMenuItem returned in the array.
  *
  *  @param  names The names of the menu items.
  *  @param  listener The ActionListener which listens to "clicks" on the menu.
  *  @return array of JMenuItems equal to the size of the vector passed in.
  */
  public static JMenuItem[] makeMultiMenuItems( Vector names,
                                                ActionListener listener )
  {
    int num_menus = names.size();
    JMenuItem[] menuitems = new JMenuItem[num_menus];
    
    for( int i = 0; i < num_menus; i++ )
      menuitems[i] = getMenuItem( names.elementAt(i), listener );
      
    return menuitems;
  }
 
 /**
  * This method creates a JMenuItem for each of the vector elements. However,
  * all of the JMenuItems are then placed under a JMenu corresponding to the
  * first element of the vector. Thus, only one JMenuItem is returned. 
  *
  * @param  names The names of the menu items.
  * @param  listener The ActionListener which listens to "clicks" on the menu.
  * @return JMenuItem which has names.size() elements.
  */
  public static JMenuItem makeMenuItem( Vector names, ActionListener listener )
  {
    return getMenuItem(names, listener);
  }
  
 /**
  * This method uses recursion to create a JMenuItem heirarchy.
  *
  * @param names The names of the JMenuItem elements.
  * @param  listener The ActionListener which listens to "clicks" on the menu.
  */
  private static JMenuItem getMenuItem( Object names, ActionListener listener )
  {
    JMenuItem menuitem;
    // This will occur if the object passed is not of type Vector or String
    if( !( names instanceof Vector ) && !( names instanceof String ) )
      return new JMenuItem("MenuItemMaker Class Problem");
    if( names instanceof Vector )
    {
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
        for( int i = 0; i < vectemp.size(); i++ )
          menuitem.add( getMenuItem( vectemp.elementAt(i), listener ) );
        return menuitem;
      }
      else
      {
        menuitem = new JMenuItem( (String)vectemp.elementAt(0) );
        menuitem.addActionListener( listener );
        return menuitem;
      }
    }
    // if string, then must be a menuitem.
    else
    {
      menuitem = new JMenuItem( (String)names );
      menuitem.addActionListener( listener );
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
    
    for( int i = 0; i < 6; i++ )
    {
      if( i < 3 )
        branchA.add("Branch A." + Integer.toString(i) );
      if( i < 4 )
        branchB.add("Branch B." + Integer.toString(i) );
      if( i < 5 )
        branchC.add("Branch C." + Integer.toString(i) );
      branchD.add("Branch D." + Integer.toString(i) );
    }
    
    // now branchD & C are submenus of branchA & B
    branchA.add(branchD);
    branchB.add(branchC);
    
    // add a final menu to each branch
    branchA.add("Last BranchA");
    branchB.add("Last BranchB");
    branchC.add("Last BranchC");
    branchD.add("Last BranchD");
    
    // these vectors will appear under Tree
    trunk.add(branchA);
    trunk.add(branchB);
    trunk.add("Branch E");
    
    JMenuItem[] branches = MenuItemMaker.makeMultiMenuItems( trunk, new
                                                             MenuListener() );
    for( int b = 0; b < branches.length; b++ )
      menus.getMenu(0).add(branches[b]);
    /*  This will fail because the first element is a vector.
    JMenuItem branch = MenuItemMaker.makeMenuItem( trunk, new
                                                            MenuListener() );
    menus.getMenu(0).add(branch);
    */
    
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

}
