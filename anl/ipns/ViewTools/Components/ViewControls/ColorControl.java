/*
 * File: ColorControl.java
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
 * Revision 1.4  2005/07/12 17:00:40  kramer
 * Added javadocs.  Also, now when the user wants to select a color, the
 * color selector displayed has "Ok", "Cancel", and "Reset" options.
 *
 * Revision 1.3  2005/06/29 18:48:38  kramer
 *
 * Added javadocs.
 *
 * Revision 1.2  2005/06/28 22:19:46  kramer
 *
 * Added a label to the view control which can be used to describe what
 * color the button on this control is supposed to modify.
 *
 * Revision 1.1  2005/06/28 16:05:54  kramer
 *
 * This is a ViewControl that allows the user to select a color to use.
 * The selected color is displayed on a button on this control and when the
 * button is pressed a color chooser is displayed.
 *
 */
package gov.anl.ipns.ViewTools.Components.ViewControls;

import gov.anl.ipns.Util.Sys.ColorSelector;
import gov.anl.ipns.ViewTools.Components.ObjectState;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

/**
 * This is a type of <code>ViewControl</code> that allows the user to 
 * select colors.  This control contains a button and an optional label.  
 * The button has a square located at its center which is colored with 
 * the selected color to allow the user to visualize the color.  
 * When the user presses the button, a color selector is displayed to 
 * allow the user to select a color.  As he/she selects a color, the 
 * color of the button changes also.  Also, whenever the color is changed, 
 * a {@link #COLOR_CHANGED COLOR_CHANGED} message is sent to all listeners.
 */
public class ColorControl extends ViewControl
{
//-------------------=[ Listener messages ]=----------------------------------//
   /**
    * This is the message sent out to all listeners when the currently 
    * selected color has changed.
    */
   public static final String COLOR_CHANGED = ColorSelector.COLOR_CHANGED;
// -----------------=[ End listener messages ]=-------------------------------//
   
//------------------=[ ObjectState keys ]=------------------------------------//
   /**
    * "Color" - This static constant String is a key for referencing the 
    * current selected color.  The value that this key references is a 
    * <code>Code</code> object.
    */
   public static final String COLOR_KEY = "Color";
   /**
    * "Label" - This static constant String is a key for referencing the 
    * text of the label on this control.  The value that this key references 
    * is a String.
    */
   public static final String LABEL_KEY = "Label";
//----------------=[ End ObjectState keys ]=----------------------------------//
   
//--------------------=[ Default values ]=------------------------------------//
   /** "Color.GRAY" - The default selected color. */
   public static final Color DEFAULT_COLOR = Color.GRAY;
   /** "" (empty string) - The default label's text. */
   public static final String DEFAULT_LABEL_TEXT = "";
//------------------=[ End default values ]=----------------------------------//
   
//---------------=[ Components used with this control ]=----------------------//
   /**
    * The button that displays the currently selected color.
    */
   private ColorfulButton colorButton;
   /**
    * The label that is located next to the button on this control.
    */
   private JLabel label;
   /**
    * The frame that holds the controls to allow the user to select a 
    * color.  This frame is displayed when this control's button is 
    * pressed.
    */
   private ColorSelectorFrame selectorFrame;
//------------=[ End omponents used with this control ]=----------------------//
   
//-----------------------=[ Constructors ]=-----------------------------------//
   /**
    * Constructs this control with the initial title, color, and color 
    * selector type.  The control's label will not be visible.
    * 
    * @param con_title This control's title.
    * @param initialColor The initially selected color.
    * @param colorModel An integer code that describes which graphical 
    *                   elements should be placed on the color selector 
    *                   displayed to allow the user to select a color.  
    *                   This code is one of the integer fields 
    *                   specified in the {@link ColorSelector ColorSelector} 
    *                   class.
    */
   public ColorControl(String con_title, Color initialColor, int colorModel)
   {
      this(con_title, "", initialColor, colorModel);
   }
   
   /**
    * Constructs this control with the initial title, label, color, and color 
    * selector type.
    * 
    * @param con_title This control's title.
    * @param labelText The label that is placed on this control next to the 
    *                  button.
    * @param initialColor The initially selected color.
    * @param colorModel An integer code that describes which graphical 
    *                   elements should be placed on the color selector 
    *                   displayed to allow the user to select a color.  
    *                   This code is one of the integer fields 
    *                   specified in the {@link ColorSelector ColorSelector} 
    *                   class.
    */
   public ColorControl(String con_title, String labelText, 
                       Color initialColor, int colorModel)
   {
      super(con_title);
      
      //create the frame that contains the color selector
      this.selectorFrame = new ColorSelectorFrame("Select a color", 
                                                  initialColor, 
                                                  colorModel);
        this.selectorFrame.addActionListener(new ColorChangeListener());
        this.selectorFrame.
           setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
      
      //create the button that is used to display the selected color and 
      //to open the color selector frame
      this.colorButton = 
         new ColorfulButton(this.selectorFrame.getSelectedColor());
        this.colorButton.addActionListener(new ButtonListener());
       
      //create the label that is placed next to the button
      label = new JLabel(labelText);
       
      //add the components to the control
      setLayout(new BorderLayout());
       add(label, BorderLayout.WEST);
       add(colorButton, BorderLayout.CENTER);
   }
//----------------------=[ End constructors ]=--------------------------------//
   
//---------------=[ Methods extended from ViewControl ]=----------------------//
   /**
    * Used to set this control's 'value'.  For this control, this value is 
    * the currently selected color.  
    * 
    * @param value A <code>Color</code> object which represents the currently 
    *              selected color.
    */
   public void setControlValue(Object value)
   {
      if (value==null || !(value instanceof Color))
         return;
      
      setSelectedColor((Color)value);
   }

   /**
    * Used to get this control's value.  For this control, this value is the 
    * currently selected color.
    * 
    * @return A <code>Color</code> representing the currently selected color.
    */
   public Object getControlValue()
   {
      return getSelectedColor();
   }
   
   /**
    * Used to get the state information about this control.  This 
    * state information includes the currently selected color and the 
    * text displayed in the label on this control.
    * 
    * @param isDefault If true the default state of this control will be 
    *                  returned.  If false the current state of this 
    *                  control will be returned.
    * @return The current state of this control.
    */
   public ObjectState getObjectState(boolean isDefault)
   {
      ObjectState state = super.getObjectState(isDefault);
      
      if (isDefault)
      {
         state.insert(COLOR_KEY, DEFAULT_COLOR);
         state.insert(LABEL_KEY, DEFAULT_LABEL_TEXT);
      }
      else
      {
         state.insert(COLOR_KEY, getSelectedColor());
         state.insert(LABEL_KEY, label.getText());
      }
      
      return state;
   }
   
   /**
    * Used to set the current state of this control.  This state includes 
    * the currently selected color and the text that is displayed on this 
    * control's label.
    * 
    * @param state An encapsulation of the newly selected color and 
    *              the label's new text.
    */
   public void setObjectState(ObjectState state)
   {
      if (state==null)
         return;
      
      //set the state that the superclass maintains
      super.setObjectState(state);
      
      Object val = state.get(COLOR_KEY);
      if (val!=null)
         setSelectedColor((Color)val);
      
      val = state.get(LABEL_KEY);
      if (val!=null)
         label.setText((String)val);
   }

   /**
    * Used to get an exact copy of this control.
    * 
    * @return A deep copy of this control.
    */
   public ViewControl copy()
   {
      ColorControl copy = new ColorControl(getTitle(), 
                                           getLabelText(), 
                                           getSelectedColor(), 
                                           selectorFrame.getSelectionModel());
      copy.setObjectState(getObjectState(false));
      return copy;
   }
//-------------=[ End methods extended from ViewControl ]=--------------------//
   
//----------------------=[ Convience methods ]=-------------------------------//
   /**
    * Used to get the current selected color.
    * 
    * @return The current selected color.
    */
   public Color getSelectedColor()
   {
      return colorButton.getColor();
   }
   
   /**
    * Used to set the current selected color.
    * 
    * @param color The current selected color.  
    *              If this is <code>null</code> 
    *              it is ignored.
    */
   public void setSelectedColor(Color color)
   {
      if (color==null)
         return;
      
      colorButton.setColor(color);
   }
   
   /**
    * Used to get the text of the label displayed on this control.
    * 
    * @return This control's label's text.  If the label is supposed to 
    *         be 'invisible', "" (the empty string) is returned.
    */
   public String getLabelText()
   {
      return label.getText();
   }
   
   /**
    * Used to set the text of the label displayed on this control.
    * 
    * @param text The label's new text.  If this value is "" (the empty 
    *             string), the label will be 'invisible'.
    */
   public void setLabelText(String text)
   {
      label.setText(text);
   }
   
   /**
    * Used to make the label on this control visible.
    * 
    * @param text The label's new text.  If this is "" (the empty string), 
    *             the label will still be 'invisible'.
    */
   public void enableLabel(String text)
   {
      setLabelText(text);
   }
   
   /**
    * Used to make the label on this control invisible.
    */
   public void disableLabel()
   {
      setLabelText("");
   }
   
   /**
    * Testbed.  Used to test if this control can be constructed, copied, 
    * and placed in a JMenu.
    * 
    * @param args Unused.
    */
   public static void main(String[] args)
   {
      ColorControl control = new ColorControl("Color Control", 
                                              Color.BLUE, 
                                              ColorSelector.TABBED);
      
      JFrame frame = new JFrame("ColorControl Demo");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.getContentPane().add(control);
        frame.pack();
      frame.setVisible(true);
      
      try
      {
         Thread.sleep(5000);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
      System.out.println("About to make a copy of the GUI");
      
      JFrame copy = new JFrame("ColorControl Demo <copy>");
        copy.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        copy.getContentPane().add(control.copy());
        copy.pack();
        copy.setLocation(10,10);
      copy.setVisible(true);
      
      
      JMenu menu = new JMenu("Color");
        menu.add(control.copy());
      JMenuBar bar = new JMenuBar();
        bar.add(menu);
      
      JFrame menuFrame = new JFrame("Menu Demo");
        menuFrame.setJMenuBar(bar);
        menuFrame.pack();
      menuFrame.setVisible(true);
   }
//--------------------=[ End convience methods ]=-----------------------------//
   
//-----------------------=[ Inner class ]=------------------------------------//
   /**
    * Listens to changes to the currently selected color.  It updates 
    * the color shown on this control's button and relays the message 
    * that the color has changed to all listeners.
    */
   private class ColorChangeListener implements ActionListener
   {
      /**
       * Invoked when the currently selected color has changed.
       */
      public void actionPerformed(ActionEvent event)
      {
         if (event.getActionCommand().equals(ColorSelectorFrame.COLOR_CHANGED))
         {
            colorButton.setColor(selectorFrame.getSelectedColor());
            repaint();
            send_message(COLOR_CHANGED);
         }
      }
   }
   
   /**
    * Listens to when this control's button is pressed.  When it is pressed 
    * the color selector is displayed if it is not already visible or 
    * hidden if it is already displayed.
    */
   private class ButtonListener implements ActionListener
   {
      /**
       * Invoked when this control's button is pressed.
       */
      public void actionPerformed(ActionEvent event)
      {
         selectorFrame.setVisible(!selectorFrame.isVisible());
      }
   }
   
   /**
    * An implementation of a special type of button that maintains a 
    * record of a selected color.  When this button is drawn, a box is 
    * drawn at its center in the same color of the selected color.
    */
   private class ColorfulButton extends JButton
   {
      /** The default value of the top/bottom margin. */
      public static final float DEFAULT_TOP_BOTTOM_MARGIN = 0.1f;
      /** The default value of the left/right margin. */
      public static final float DEFAULT_SIDE_MARGIN = 0.1f;
      
      /**
       * The semi-transparent gray color that is painted over the button 
       * when it is not enabled to make it look like it is not enabled.
       */
      private final Color DISABLE_COLOR = new Color(128, 128, 128, 120);
      
      /** The currently color. */
      private Color color;
      /**
       * The margin at the top/bottom of this button.  This value should 
       * be in the range [0, 0.5] and represents the percent of the 
       * top/bottom of the button that is not painted with the current 
       * color.
       */
      private float topBottomMargin;
      /**
       * The margin at the left/right side of this button.  This value should 
       * be in the range [0, 0.5] and represents the percent of the 
       * left/right side of the button that is not painted with the current 
       * color.
       */
      private float sideMargin;
      
      /**
       * Constructs this button with the given color.  When, the button is 
       * drawn, the top 10%, bottom 10%, left 10%, and right 10% of the 
       * button are not colored with the button's current colors.  These 
       * regions are the button's margins.
       * 
       * @param color The initial selected color.
       */
      public ColorfulButton(Color color)
      {
         this(color, DEFAULT_TOP_BOTTOM_MARGIN, DEFAULT_SIDE_MARGIN);
      }
      
      /**
       * Constructs this button with the given color and margins.
       * 
       * @param color The initial selected color.
       * @param topMargin The margin at the top/bottom of the button.  This 
       *                  value must be in the range [0, 0.5] and 
       *                  represents the percent of the top/bottom of the 
       *                  button that is not painted with the current color.
       * @param sideMargin The margin at the left/right of the button.  This 
       *                   value must be in the range [0, 0.5] and 
       *                   represents the percent of the left/right side 
       *                   of the button that is not painted with the 
       *                   current color.
       */
      public ColorfulButton(Color color, 
                            float topMargin, 
                            float sideMargin)
      {
         super();
         
         setColor(color);
         setTopBottomMargin(topMargin);
         setSideMargin(sideMargin);
      }
      
      /**
       * Used to get the current color.
       * 
       * @return The current color.
       */
      public Color getColor()
      {
         return color;
      }
      
      /**
       * Used to set the current color.
       * 
       * @param color The new current color.  If this 
       *              parameter is <code>null</code> 
       *              nothing is done.
       */
      public void setColor(Color color)
      {
         if (color!=null)
            this.color = color;
      }
      
      /**
       * Used to get the margin at the top and bottom of this button.  
       * This margin is a number in the range [0, 0.5] and represents the 
       * percent of the top and bottom of the button that is not painted 
       * with the current color.
       * <p>
       * For example, if the value returned is <code>0.1f</code>, then 
       * the top 10% and bottom 10% of the button are not painted with 
       * the current color.
       * 
       * @return The percent of the top and bottom of the button that is 
       *         not painted with the current color.  The value returned 
       *         is in the range [0, 0.5].
       */
      public float getTopBottomMargin()
      {
         return topBottomMargin;
      }
      
      /**
       * Used to set the margin at the top and bottom of this button.  
       * This margin should be in the range [0, 0.5] and represents the 
       * percent of the top and bottom of the button that is not painted 
       * with the current color.
       * <p>
       * For example, if the value supplied is <code>0.1f</code>, then 
       * the top 10% and bottom 10% of the button are not painted with 
       * the current color.
       * 
       * @param margin The percent of the top and bottom of the button that 
       *               is not painted with the current color.  This value 
       *               should be in the range [0, 0.5].  If it is not in 
       *               this range the default value is used.
       */
      public void setTopBottomMargin(float margin)
      {
         if (margin<0 || margin>0.5)
            this.topBottomMargin = DEFAULT_TOP_BOTTOM_MARGIN;
         else
            this.topBottomMargin = margin;
      }
      
      /**
       * Used to get the margin at the left and right sides of this button.  
       * This margin is a number in the range [0, 0.5] and represents the 
       * percent of the left and right sides of the button that is not 
       * painted with the current color.
       * <p>
       * For example, if the value returned is <code>0.1f</code>, then 
       * the left 10% and right 10% of the button are not painted with 
       * the current color.
       * 
       * @return The percent of the left and right sides of the button that 
       *         is not painted with the current color.  The value returned 
       *         is in the range [0, 0.5].
       */
      public float getSideMargin()
      {
         return sideMargin;
      }
      
      /**
       * Used to set the margin at the left and right sides of this button.  
       * This margin should be in the range [0, 0.5] and represents the 
       * percent of the left and right sides of the button that is not 
       * painted with the current color.
       * <p>
       * For example, if the value supplied is <code>0.1f</code>, then 
       * the left 10% and right 10% of the button are not painted with 
       * the current color.
       * 
       * @param margin The percent of the left and right sides of the 
       *               button that is not painted with the current color.  
       *               This value should be in the range [0, 0.5].  If it 
       *               is not in this range, the default value is used.
       */
      public void setSideMargin(float margin)
      {
         if (margin<0 || margin>0.5)
            this.sideMargin = DEFAULT_SIDE_MARGIN;
         else
            this.sideMargin = margin;
      }
      
      /**
       * Overriden to cause a box to be painted at the center of this 
       * button colored with the current color.
       */
      public void paintComponent(Graphics g)
      {
         super.paintComponent(g);
         
         //determine the margin sizes in pixels
         Dimension dim = getSize();
         int topMargin = (int)(getTopBottomMargin()*dim.getHeight());
         int sideMargin = (int)(getSideMargin()*dim.getWidth());
         
         //determine the width of the square that is painted to reflect the 
         //current color
         int width = (int)(dim.getWidth()-2*sideMargin);
         int height = (int)(dim.getHeight()-2*topMargin);
         
         //paint the center square
         g.setColor(getColor());
         g.fillRect(sideMargin, topMargin, width, height);
         
         //if the button is not enabled paint semi-transparent gray over 
         //the button
         if (!isEnabled())
         {
            g.setColor(DISABLE_COLOR);
            g.fillRect(1, 1, (int)(dim.getWidth()-1), 
                             (int)(dim.getHeight()-1));
         }
      }
   }

   /**
    * This is a <code>JFrame</code> that contains a <code>ColorSelector</code> 
    * that is used to allow the user to select a color.  This frame contains 
    * three buttons, "Ok", "Cancel", and "Reset" to allow the user to select 
    * a color and then 'roll back' to the previously selected color if 
    * he/she wants to.
    */
   private class ColorSelectorFrame extends JFrame implements ActionListener
   {
      /**
       * This is the message that is sent out to listeners when the 
       * currently selected color changes.
       */
      public static final String COLOR_CHANGED = "Color changed";
      
      /** This is the text displayed on the "Ok" button. */
      private static final String OK_TEXT = "Ok";
      /** This is the text displayed on the "Cancel" button. */
      private static final String CANCEL_TEXT = "Cancel";
      /** This is the text displayed on the "Reset" button. */
      private static final String RESET_TEXT = "Reset";
      
      /**
       * This is the color selector that actually allows the user to 
       * select a color.
       */
      private ColorSelector selector;
      /**
       * The <code>Vector</code> of <code>ActionListeners</code> that are 
       * listening to when the selected color has changed.
       */
      private Vector listeners;
      /**
       * Stores the previously selected color.  This is used to allow the 
       * user to 'roll back' to the previously selected color.
       */
      private Color previousColor;
      /**
       * This listener potentially listens to all 
       * <code>ColorSelector.COLOR_CHANGED</code> messages sent out by the 
       * color selector.  It then relays the messages to the listeners of 
       * this object.  This listener can be told to listen or not listen to 
       * the messages.
       */
      private ContinuousListener contListener;
      
      /**
       * Constructs a <code>ColorSelectorFrame</code> with the given title.  
       * The color selector initially has the given color selected and 
       * the given model describes which controls should be displayed to 
       * allow the user to select a color.
       * 
       * @param title The frame's title.
       * @param initialColor The color that is initially selected by 
       *                     the color selector.
       * @param model Either 
       *              {@link ColorSelector#SWATCH ColorSelector.SWATCH}, 
       *              {@link ColorSelector#BLEND ColorSelector.BLEND}, 
       *              {@link ColorSelector#SLIDER ColorSelector.SLIDER}, or 
       *              {@link ColorSelector#TABBED ColorSelector.TABBED}.  
       *              This code describes which graphical element(s) to 
       *              display on the selector to allow the user to 
       *              select a color.
       */
      public ColorSelectorFrame(String title, Color initialColor, int model)
      {
         super(title);
         
         //create the vector of ActionListeners
         this.listeners = new Vector();
         
         //create the color selector
         this.selector = new ColorSelector(model);
         this.selector.setSelectedColor(initialColor);
         
         //create the listener that will listen directly to the color selector
         this.contListener = new ContinuousListener(true);
         this.selector.addActionListener(contListener);
         
         //set the previous color to be the current color
         this.previousColor = initialColor;
         
         //create the panel containing the "Ok", "Cancel", and "Reset" buttons
         JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
           //create the buttons
           JButton okButton = new JButton(OK_TEXT);
             okButton.addActionListener(this);
           JButton cancelButton = new JButton(CANCEL_TEXT);
             cancelButton.addActionListener(this);
           JButton resetButton = new JButton(RESET_TEXT);
             resetButton.addActionListener(this);
           //add the buttons to the panel
           buttonPanel.add(okButton);
           buttonPanel.add(cancelButton);
           buttonPanel.add(resetButton);
           
         //add the components to the frame
         Container contentPane = getContentPane();
           contentPane.setLayout(new BorderLayout());
           contentPane.add(this.selector, BorderLayout.CENTER);
           contentPane.add(buttonPanel, BorderLayout.SOUTH);
           
         //resize the frame to the best fit
         pack();
      }
      
      /**
       * Used to get the currently selected color.
       * 
       * @return The currently selected color.
       */
      public Color getSelectedColor()
      {
         return selector.getSelectedColor();
      }
      
      /**
       * Used to determine which graphical element(s) are displayed on the 
       * frame to allow the user to select a color.
       * 
       * @return Either {@link ColorSelector#SWATCH ColorSelector.SWATCH}, 
       *         {@link ColorSelector#BLEND ColorSelector.BLEND}, 
       *         {@link ColorSelector#SLIDER ColorSelector.SLIDER}, 
       *         or {@link ColorSelector#TABBED ColorSelector.TABBED}.
       */
      public int getSelectionModel()
      {
         return selector.getModel();
      }
      
      /**
       * Used to determine if {@link #COLOR_CHANGED COLOR_CHANGED} messages 
       * are being sent out every time the user selects a different color 
       * in the color selector or only when the user presses the "Ok" 
       * button.
       * 
       * @return True if a {@link #COLOR_CHANGED COLOR_CHANGED} message is 
       *         sent out every time the user selects a different color.  
       *         False if the messages are only sent out when the user 
       *         pressed the "Ok" button.
       */
      public boolean isNotifyingContinuously()
      {
         return contListener.isListening();
      }
      
      /**
       * Used to set if {@link #COLOR_CHANGED COLOR_CHANGED} messages should 
       * be sent out as soon as the user selects a different color in the 
       * color selector, or if they should only be sent out when the "Ok" 
       * button is pressed.
       * 
       * @param listen True if a {@link #COLOR_CHANGED COLOR_CHANGED} 
       *               message should be sent out every time the user 
       *               selects a different color.  False if the messages 
       *               should only be sent out when the user pressed the 
       *               "Ok" button.
       */
      public void setIsNotifyingContinuously(boolean listen)
      {
         contListener.setIsLIstening(listen);
      }
      
      /**
       * Used to add a listener to this frame.
       * 
       * @param listener The listener that wants to listen to changes in the 
       *                 color selection.
       */
      public void addActionListener(ActionListener listener)
      {
         if (listener==null || listeners.contains(listener))
            return;
         
         listeners.add(listener);
      }
      
      /**
       * Used to remove a listener from this frame.
       * 
       * @param listener The listener that wants to stop listening to changes 
       *                 in the color selection.
       */
      public void removeActionListener(ActionListener listener)
      {
         if (listener==null)
            return;
         
         listeners.remove(listener);
      }
      
      /**
       * Invoked when any of the buttons on this frame are pressed.
       * 
       * @param event Encapsulates which button was pressed.
       */
      public void actionPerformed(ActionEvent event)
      {
         if (event.getActionCommand().equals(RESET_TEXT))
            resetSelectedColor();
         else if (event.getActionCommand().equals(OK_TEXT))
         {
            if (!getSelectedColor().equals(this.previousColor))
               notifyListeners();
            
            this.previousColor = getSelectedColor();
            close();
         }
         else if (event.getActionCommand().equals(CANCEL_TEXT))
         {
            resetSelectedColor();
            close();
         }
      }
      
      /**
       * Used to reset the currently selected color to be the previously 
       * selected color.  The previously selected color is unchanged.  
       * Therefore, after calling this method, the selected color and 
       * previously selected color are the same.
       */
      private void resetSelectedColor()
      {
         selector.setSelectedColor(this.previousColor);
      }
      
      /**
       * Used to send the {@link #COLOR_CHANGED COLOR_CHANGED} message 
       * to all of this frame's <code>ActionListeners</code>.
       */
      private void notifyListeners()
      {
         for (int i=0; i<listeners.size(); i++)
            ((ActionListener)listeners.elementAt(i)).
               actionPerformed(new ActionEvent(this,0,COLOR_CHANGED));
      }
      
      /**
       * Used to close this frame.  Depending on which value was given to 
       * the <code>setDefaultCloseOperation()</code> method, this method 
       * will either, dispose this frame, exit, or make this frame invisible.
       */
      private void close()
      {
         int closeOp = getDefaultCloseOperation();
         if (closeOp==DISPOSE_ON_CLOSE)
            dispose();
         else if (closeOp==EXIT_ON_CLOSE)
            System.exit(0);
         else if (closeOp==HIDE_ON_CLOSE)
            setVisible(false);
      }
      
      /**
       * Used to listen to all of the 
       * {@link ColorSelector#COLOR_CHANGED ColorSelector.COLOR_CHANGED} 
       * messages sent from the enclosing class's <code>ColorSelector</code> 
       * and relay them to the listeners of the enclosing class.  
       * This listener has the functionality to respond to or ignore these 
       * messages.
       */
      private class ContinuousListener implements ActionListener
      {
         /**
          * Describes if this listener should listen to or 
          * ignore the messages it recieves. 
          */
         private boolean listen;
         
         /**
          * Constructs this listen to either listen to or ignore the 
          * messages it recieves.
          * 
          * @param listen If true the listener will listen to the messages 
          *               it recieves.  If false, it will ignore the 
          *               messages it recieves.
          */
         public ContinuousListener(boolean listen)
         {
            this.listen = listen;
         }
         
         /**
          * Used to determine if this listener is listening to or 
          * ignoring the messages it recieves.
          * 
          * @return True if this listener is listening to the messages it 
          *         recieves.  False if it is ignoring the messages.
          */
         public boolean isListening()
         {
            return this.listen;
         }
         
         /**
          * Used to set if this listener should listen to or ignore the 
          * messages it recieves.
          * 
          * @param listen True if this listener should listen to the 
          *               messages it recieves.  False if it should 
          *               ignore the messages it recieves.
          */
         public void setIsLIstening(boolean listen)
         {
            this.listen = listen;
         }
         
         /**
          * Invoked when this listener recieves a message.  If this 
          * listener is set to listen to these messages, it will send 
          * a {@link ColorControl#COLOR_CHANGED ColorControl.COLOR_CHANGED} 
          * message to all listeners of the enclosing class.  Otherwise, 
          * it will ignore the message.
          */
         public void actionPerformed(ActionEvent event)
         {
            if (isListening())
               notifyListeners();
         }
      }
   }
}
//-----------------------=[ End inner class ]=--------------------------------//


/*  Unused code

   /**
    * This is the panel that contains the controls that allows the user 
    * to select a color.
    /
   private ColorSelector colorSelector;
   
   private JFrame selectorFrame;


      //create the graphical element used to select the color
      this.colorSelector = new ColorSelector(colorModel);
        this.colorSelector.setSelectedColor(initialColor);
        this.colorSelector.addActionListener(new ColorChangeListener());
      
      //create the frame that the color selector is placed on
      this.selectorFrame = new JFrame();
        //the window should not close but instead should hide
        this.selectorFrame.setDefaultCloseOperation(
              WindowConstants.HIDE_ON_CLOSE);
        //add the components to the content pane
          //add the color selector
        this.selectorFrame.getContentPane().setLayout(new BorderLayout());
          this.selectorFrame.getContentPane().add(this.colorSelector, 
                                                BorderLayout.CENTER);
          //add the 'close' button
        JPanel buttonpanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
          JButton closeButton = new JButton("Close");
            closeButton.addActionListener(new ActionListener()
            {
               public void actionPerformed(ActionEvent event)
               {
                  selectorFrame.setVisible(false);
               }
            });
          buttonpanel.add(closeButton);
        this.selectorFrame.getContentPane().add(buttonpanel, 
                                                BorderLayout.SOUTH);
        //resize the window and make sure its not visible
        this.selectorFrame.pack();
        this.selectorFrame.setVisible(false);
      

*/
