/*
 * File: TabbedViewControl.java
 *
 * Copyright (C) 2005, Dominic Kramer
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
 * Primary   Dominic Kramer <kramerd@uwstout.edu>
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
 * Modified:
 * $Log$
 * Revision 1.4  2005/07/12 16:54:46  kramer
 * Now the setObjectState() method calls super.setObjectState() to set the
 * state that the superclass maintains.
 *
 * Revision 1.3  2005/06/16 13:49:25  kramer
 *
 * Added methods to get the selected tab and to get the ViewControl on the
 * selected tab.
 *
 * Revision 1.2  2005/06/14 21:19:15  kramer
 *
 * Added methods to set the selected tab (given its index or the ViewControl
 * on the tab).
 *
 * Also added javadocs that pointed out that currently only ViewControl
 * objects can be added to the tabbed pane (this is because they are
 * inherently Components).
 *
 * Revision 1.1  2005/06/13 19:40:42  kramer
 *
 * This is a ViewControl that contains a number of other ViewControls in
 * it in a tabbed pane.
 *
 */
package gov.anl.ipns.ViewTools.Components.ViewControls;

import gov.anl.ipns.Util.Sys.WindowShower;
import gov.anl.ipns.ViewTools.Components.ObjectState;

import java.awt.Component;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.WindowConstants;

/**
 * This is a 'container' ViewControl that contains a number of ViewControls 
 * in it.  Each ViewControl is placed in its own tab on a tabbed pane on 
 * this ViewControl.  The 
 * {@link #setControlValue(Object) setControlValue(Object)} method can be used 
 * to set the control value for any ViewControl located on this tabbed pane.  
 * Next, the {@link #getControlValue() getControlValue()} method is used to 
 * get the control value of the ViewControl on the currently selected 
 * tab.  However, the {@link #getControlValue(int) getControlValue(int)} 
 * method can be used to get the control value of a ViewControl on any 
 * tab.
 * <p>
 * Notice that this ViewControl can only add other 
 * {@link gov.anl.ipns.ViewTools.Components.ViewControls.ViewControl 
 * ViewControl} objects to the tabbed pane.  It cannot add 
 * {@link gov.anl.ipns.ViewTools.Components.ViewControls.IViewControl 
 * IViewControl} objects to the tabbed pane.  This is because an object 
 * must be a {@link java.awt.Component Component} to be added to the 
 * tabbed pane.  A ViewControl is an object that implements IViewControl 
 * and is a Component.  Thus, it can be used as an IViewControl and can be 
 * added to the tabbed pane.
 */
public class TabbedViewControl extends ViewControl
{
   /**
    * "VIEW_CONTROL_STATE_AT_" - This static constant string is the key for 
    * referencing the state information for the ViewControl on a given 
    * tab.  The value that this key references is an ObjectState.  
    * <p>
    * To use this key to get the ObjectState of the ViewControl that is 
    * located on a particluar tab, concatenate the index of the tab to the 
    * end of this key.  The following code gives an example:
    * <p>
    * <code><br>
    *   int i = 2;<br>
    *   String key = VIEW_CONTROL_STATE_AT+i;<br>
    *   <br>
    *   //now 'key' can be used to reference the ObjectState of the <br>
    *   //the ViewControl that is located on the tab with index 2<br>
    *   //(Note:  indexing starts at 0.  Thus, the first tab has an <br>
    *   //        index of 0)<br>
    * </code>
    */
   public static final String VIEW_CONTROL_STATE_AT = "VIEW_CONTROL_STATE_AT_";
   
   /**
    * This is the tabbed pane that contains all of the ViewControls.  
    * Each ViewControl is placed on its own tab.
    */
   private JTabbedPane tabPane;
   
   /**
    * Constructs this ViewControl such that it contains a tabbed pane 
    * containing each of the ViewControls given in the Vector 
    * <code>viewControlVec</code> where each ViewControl is placed on 
    * its own tab.  
    * <p>
    * Notice:  The String returned from the {@link ViewControl#getTitle() 
    * getTitle()} method invoked on the ViewControl of a particular tab is 
    * used as the title of that tab.
    * 
    * @param con_title The title of this ViewControl
    * @param viewControlVec Contains the ViewControls to be placed on the 
    *                       tab pane.  If this parameter is <code>null</code> 
    *                       nothing will be placed on the tabbed pane.  Also, 
    *                       only the elements in the Vector that are 
    *                       ViewControls will be placed on the tabbed pane.
    */
   public TabbedViewControl(String con_title, Vector viewControlVec)
   {
      super(con_title);
      
      Vector validVec = new Vector();
      if (viewControlVec!=null)
      {
         Object curOb;
         for (int i=0; i<viewControlVec.size(); i++)
         {
            curOb = viewControlVec.elementAt(i);
            if ( (curOb != null) && (curOb instanceof ViewControl) )
               validVec.add(curOb);
         }
      }
      
      tabPane = new JTabbedPane();
      ViewControl curControl;
      for (int i=0; i<validVec.size(); i++)
      {
         curControl = (ViewControl)validVec.elementAt(i);
         tabPane.addTab(curControl.getTitle(), curControl);
      }
      
      add(tabPane);
   }
   
   /**
    * Constructs this ViewControl exactly the same as the constructor 
    * {@link #TabbedViewControl(String, Vector) 
    * TabbedViewControl(String, Vector)} would.  However, it sets this 
    * ViewControl's title to be the empty String.
    * 
    * @param viewControlVec Contains the ViewControls to be placed on the 
    *                       tab pane.  If this parameter is <code>null</code> 
    *                       nothing will be placed on the tabbed pane.  Also, 
    *                       only the elements in the Vector that are 
    *                       ViewControls will be placed on the tabbed pane.
    * 
    * @see #TabbedViewControl(String, Vector)
    */
   public TabbedViewControl(Vector viewControlVec)
   {
      this("", viewControlVec);
   }

   /**
    * Used to set the value of one of the ViewControls contained on the 
    * tabbed pane.  Also, the tab containing this ViewControl 
    * is made the currently selected tab.
    * 
    * @param value This object must be a {@link TabbedViewControl.TabValuePair 
    *              TabbedViewControl.TabValuePair} object.  
    *              <ol>
    *                <li>
    *                  The {@link TabbedViewControl.TabValuePair#getIndex() 
    *                  getIndex()} method is invoked on this object to find 
    *                  the index of the tab containing the {@link ViewControl 
    *                  ViewControl} to use.  
    *                </li>
    *                <li>
    *                  The {@link ViewControl#setControlValue(Object) 
    *                  setControlValue(Object)} method is invoked on this 
    *                  {@link ViewControl ViewControl} object.
    *                </li>
    *                <li>
    *                  The value passed to the 
    *                  {@link ViewControl#setControlValue(Object) 
    *                  setControlValue(Object)} method is the value returned 
    *                  from the 
    *                  {@link TabbedViewControl.TabValuePair#getValue() 
    *                  getValue()} method that is invoked on the 
    *                  {@link TabbedViewControl.TabValuePair 
    *                  TabbedViewControl.TabValuePair} argument passed to this 
    *                  method.
    *                </li>
    *              </ol>
    *              Note:  If the index found from the 
    *                     {@link TabbedViewControl.TabValuePair#getIndex() 
    *                     getIndex()} method is invalid or if value is null 
    *                     or isn't an object of type 
    *                     {@link TabbedViewControl.TabValuePair 
    *                     TabbedViewControl.TabValuePair} nothing is done.
    */
   public void setControlValue(Object value)
   {
      if ( (value==null) || !(value instanceof TabValuePair) )
         return;
      
      TabValuePair tab = (TabValuePair)value;
      //check if the tab's index is valid
      int index = tab.getIndex();
      if ( index<0 || index>=tabPane.getTabCount())
      {
         System.out.println("Warning:  TabbedViewControl.setControlValue() " +
                            "the given tab index is invalid");
         return;
      }
      
      //set the tab's ViewControl's value
      ViewControl control = getViewControlAt(index);
      if (control==null)
      {
         System.out.println("Warning:  TabbedViewControl.setControlValue() " +
                            "could not find the ViewControl at tab "+index);
         return;
      }
      System.out.println("Found control type "+control.getClass().getName());
      System.out.println("Using values "+tab.getValue());
      control.setControlValue(tab.getValue());
      
      //make the currently modified tab the selected one
      tabPane.setSelectedIndex(index);
   }
   
   /**
    * Used to get the control value of the ViewControl on the currently 
    * selected tab.
    * 
    * @return The value returned from the 
    *         {@link ViewControl#getControlValue() getControlValue()} 
    *         invoked on the {@link ViewControl ViewControl} located on the 
    *         currently selected tab.  Note:  If none of the tab are 
    *         currently selected or if there isn't a ViewControl on the 
    *         selected tab, <code>null</code> is returned.
    * 
    * @see #getControlValue(int)
    */
   public Object getControlValue()
   {
      return getControlValue(tabPane.getSelectedIndex());
   }
   
   /**
    * Used to get an ObjectState object that encapsulates the ObjecStates  
    * of all of the ViewControls located this the tabbed pane.  
    * See the {@link #VIEW_CONTROL_STATE_AT VIEW_CONTROL_STATE_AT} 
    * documentation to learn how to reference each of these ObjectStates.
    * 
    * @param isDefault <ul>
    *                    <li>
    *                      If true the default configuration for each 
    *                      ViewControl is used.
    *                    </li>
    *                    <li>
    *                      If false the current configuration of each 
    *                      ViewControl is used.
    *                    </li>
    *                  </ul>
    * 
    * @return An Object state holding the current configuration of all of the 
    *         ViewControls on this tabbed pane.
    */
   public ObjectState getObjectState( boolean isDefault )
   {
      ObjectState state = super.getObjectState(isDefault);
      
      int tabCount = tabPane.getTabCount();
      for (int i=0; i<tabCount; i++)
         state.insert(VIEW_CONTROL_STATE_AT+i, 
                      getViewControlAt(i).getObjectState(isDefault));
         
      return state;
   }
   
   /**
    * Used to set the ObjectState of each of the ViewControls located on 
    * this tabbed pane.  See the 
    * {@link #VIEW_CONTROL_STATE_AT VIEW_CONTROL_STATE_AT} 
    * documentation to learn how to determine the key that references each 
    * ObjectState.
    * 
    * @param state An ObjectState that holds the ObjecState of each of the 
    *              ViewControls on this tabbed pane.
    */
   public void setObjectState( ObjectState state )
   {
      if (state==null)
         return;
      
      //set the state that the superclass maintains
      super.setObjectState(state);
      
      Object curVal;
      int tabCount = tabPane.getTabCount();
      for (int i=0; i<tabCount; i++)
      {
         curVal = state.get(VIEW_CONTROL_STATE_AT+i);
         if ( (curVal != null) && (curVal instanceof ObjectState) )
            getViewControlAt(i).setObjectState((ObjectState)curVal);
      }
   }
   
   /**
    * Used to get an exact copy of this ViewControl
    * 
    * @return A copy of this ViewControl.
    */
   public ViewControl copy()
   {
      TabbedViewControl tvc = new TabbedViewControl(this.getTitle(),
                                                    this.getViewControls());
      tvc.setObjectState(this.getObjectState(false));
      return tvc;
   }
   
   /**
    * Testbed.
    * 
    * @param args Unused.
    */
   public static void main(String[] args)
   {
      Vector controlVec = new Vector();
        controlVec.add(new ControlCheckbox(false));
        controlVec.add(new FieldEntryControl(2));
        
        ControlSlider slider = new ControlSlider();
          slider.showTicks(true);
        controlVec.add(slider);
      
      TabbedViewControl tabs = new TabbedViewControl("Test Title", controlVec);
      
      JFrame frame = new JFrame();
        frame.getContentPane().add(tabs);
        frame.pack();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
      
      WindowShower.show(frame);
      
      try
      {
         Thread.sleep(1000*1);
         TabbedViewControl.TabValuePair pair = 
            new TabbedViewControl.TabValuePair(new Boolean(true),0);
         tabs.setControlValue(pair);
         
         Thread.sleep(1000*1);
         pair.setValue(new String[]{null, "a", "b"});
         pair.setIndex(1);
         tabs.setControlValue(pair);
         
         Thread.sleep(1000*1);
         TabValuePair pair3 = new TabValuePair(new Float(74.78),2);
         tabs.setControlValue(pair3);
      }
      catch (InterruptedException e)
      {
         e.printStackTrace();
      }
   }
   
   /**
    * Used to get the ViewControl on the tab with the given index.
    * 
    * @param index The index of the tab to reference.  Note:  Indexing starts 
    *              at 0.  Thus, the first tab has index 0.
    * 
    * @return The ViewControl on the tab with the given index.  Note:  If 
    *         <code>index</code> is invalid or if there isn't a 
    *         ViewControl on the tab <code>null</code> is returned.
    */
   public ViewControl getViewControlAt(int index)
   {
      if (index<0 || index>=tabPane.getTabCount())
         return null;
      
      Component comp = tabPane.getComponentAt(index);
      if (comp instanceof ViewControl)
         return (ViewControl)comp;
      else
         return null;
   }
   
   /**
    * Used to get the index of the tab that the given ViewControl is located 
    * on.
    * 
    * @param control The ViewControl whose tab's index is to be located.
    * @return The index of the tab that contains the ViewControl 
    *         <code>control</code>.  Note:  If <code>control</code> is 
    *         <code>null</code> or if no tabs contain <code>control</code>
    *         -1 is returned.
    */
   public int getIndexForViewControl(ViewControl control)
   {
      if (control==null)
         return -1;
      else
         return tabPane.indexOfComponent(control);
   }
   
   /**
    * Used to get all of the ViewControls located on the tabbed pane.  The 
    * ViewControls are placed in the Vector in the same order that they are 
    * located on the tabbed pane.
    * 
    * @return A Vector of all of the ViewControls on the tabbed pane.
    */
   public Vector getViewControls()
   {
      Vector viewVec = new Vector();
      
      ViewControl curControl;
      int tabCount = tabPane.getTabCount();
      for (int i=0; i<tabCount; i++)
         if ( (curControl = getViewControlAt(i)) != null )
            viewVec.add(curControl);
      
      return viewVec;
   }
   
   /**
    * Used to get the control value of the ViewControl on the given tab.
    * 
    * @param index The index of the tab to use.  Note:  Indexing starts at 0.  
    *              Therefore, the first tab on the tabbed pane has index 0.
    * 
    * @return The value returned from the 
    *         {@link ViewControl#getControlValue() getControlValue()} 
    *         invoked on the {@link ViewControl ViewControl} located on the 
    *         specified tab.  Note:  If <code>index</code> is invalid or 
    *         if there isn't a ViewControl on the tab, 
    *         <code>null</code> is returned.
    */
   public Object getControlValue(int index)
   {
      //check if there is no currently selected tab
      if (index<0 || index>=tabPane.getTabCount())
         return null;
      
      ViewControl control = getViewControlAt(index);
      if (control==null)
         return null;
      else
         return control.getControlValue();
   }
   
   /**
    * Used to determine the number of tabs located on the tabbed pane.
    * 
    * @return The number of tabs on the tabbed pane.
    */
   public int getNumTabs()
   {
      return tabPane.getTabCount();
   }
   
   /**
    * Makes the tab with the given index the selected tab.  Note:  If 
    * the index given is invalid, nothing is done.
    * 
    * @param index The index of the tab which will become the selected tab.
    */
   public void setSelectedTab(int index)
   {
      if (index>=0 && index<getNumTabs())
         tabPane.setSelectedIndex(index);
   }
   
   /**
    * Makes the tab with the given ViewControl the selected tab.  Note:  If 
    * <code>control</code> is <code>null</code> or if no tabs contain 
    * <code>control</code>, nothing is done.
    * 
    * @param control The ViewControl which identifies the tab to become the 
    *                selected tab.
    */
   public void setSelectedTab(ViewControl control)
   {
      setSelectedTab(getIndexForViewControl(control));
   }
   
   /**
    * Used to determine which tab is selected.
    * 
    * @return The index of the selected tab or -1 if no tabs are selected.
    */
   public int getSelectedTab()
   {
      return tabPane.getSelectedIndex();
   }
   
   /**
    * Used to get the ViewControl located on the selected tab.
    * 
    * @return The ViewControl on the selected tab or <code>null</code> if 
    *         no tabs are selected.
    */
   public ViewControl getSelectedViewControl()
   {
      return getViewControlAt(getSelectedTab());
   }
   
   /**
    * Class that encapsulates a value (for a ViewControl) and the index of 
    * the tab that contains the ViewControl.
    * 
    * @see TabbedViewControl#setControlValue(Object)
    */
   public static class TabValuePair
   {
      /** The value that is to be used with the ViewControl. */
      private Object value;
      /** The index of the tab that contains the ViewControl. */
      private int index;
      
      /**
       * Constructs this object with the given value and index.
       * @param value The value that is to be given to the ViewControl.
       * @param index The index of the tab containing the ViewControl.
       */
      public TabValuePair(Object value, int index)
      {
         this.value = value;
         this.index = index;
      }
      
      /**
       * Get the value that is to be given to the ViewControl.
       * 
       * @return The value to give to the ViewControl.
       */
      public Object getValue()
      {
         return value;
      }
      
      /**
       * Set the value that is to be given to the ViewControl.
       * 
       * @param value The value that is to be given to the ViewControl.
       */
      public void setValue(Object value)
      {
         this.value = value;
      }
      
      /**
       * Get the index of the tab that contains the ViewControl to use.
       * 
       * @return The index of the tab that contains the ViewControl to use.
       */
      public int getIndex()
      {
         return index;
      }
      
      /**
       * Set the index of the tab that contains the ViewControl to use.
       * 
       * @param index The index of the tab that contains the 
       *              ViewControl to use.
       */
      public void setIndex(int index)
      {
         this.index = index;
      }
   }
}
