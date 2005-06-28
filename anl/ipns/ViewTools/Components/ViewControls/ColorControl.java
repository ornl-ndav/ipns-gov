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
 * Revision 1.2  2005/06/28 22:19:46  kramer
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
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

/**
 * 
 */
public class ColorControl extends ViewControl
{
   public static final String COLOR_CHANGED = ColorSelector.COLOR_CHANGED;
   
   public static final String COLOR_KEY = "Color";
   public static final String LABEL_KEY = "Label";
   
   public static final Color DEFAULT_COLOR = Color.GRAY;
   public static final String DEFAULT_LABEL_TEXT = "";
   
   private ColorfulButton colorButton;
   private ColorSelector colorSelector;
   private JFrame selectorFrame;
   private JLabel label;
   
   public ColorControl(String con_title, Color initialColor, int colorModel)
   {
      this(con_title, "", initialColor, colorModel);
   }
   /**
    * @param con_title
    */
   public ColorControl(String con_title, String labelText, 
                       Color initialColor, int colorModel)
   {
      super(con_title);
      
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
      
      //create the button that is used to display the selected color and 
      //to open the color selector frame
      colorButton = new ColorfulButton(colorSelector.getSelectedColor());
       colorButton.addActionListener(new ButtonListener());
       
      //create the label that is placed next to the button
      label = new JLabel(labelText);
       
      //add the components to the control
      setLayout(new BorderLayout());
       add(label, BorderLayout.WEST);
       add(colorButton, BorderLayout.CENTER);
   }

   public void setControlValue(Object value)
   {
      if (value==null || !(value instanceof Color))
         return;
      
      setSelectedColor((Color)value);
   }

   public Object getControlValue()
   {
      return getSelectedColor();
   }
   
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
   
   public void setObjectState(ObjectState state)
   {
      if (state==null)
         return;
      
      Object val = state.get(COLOR_KEY);
      if (val!=null)
         setSelectedColor((Color)val);
      
      val = state.get(LABEL_KEY);
      if (val!=null)
         label.setText((String)val);
   }

   public ViewControl copy()
   {
      ColorControl copy = new ColorControl(getTitle(), 
                                           getLabelText(), 
                                           getSelectedColor(), 
                                           colorSelector.getModel());
      copy.setObjectState(getObjectState(false));
      return copy;
   }
   
   public Color getSelectedColor()
   {
      return colorButton.getColor();
   }
   
   public void setSelectedColor(Color color)
   {
      if (color==null)
         return;
      
      colorButton.setColor(color);
   }
   
   public String getLabelText()
   {
      return label.getText();
   }
   
   public void setLabelText(String text)
   {
      label.setText(text);
   }
   
   public void enableLabel(String text)
   {
      setLabelText(text);
   }
   
   public void disableLabel()
   {
      setLabelText("");
   }
   
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
   
   private class ColorChangeListener implements ActionListener
   {
      public void actionPerformed(ActionEvent event)
      {
         if (event.getActionCommand().equals(ColorSelector.COLOR_CHANGED))
         {
            colorButton.setColor(colorSelector.getSelectedColor());
            repaint();
            send_message(COLOR_CHANGED);
         }
      }
   }
   
   private class ButtonListener implements ActionListener
   {
      public void actionPerformed(ActionEvent event)
      {
         selectorFrame.setVisible(!selectorFrame.isVisible());
      }
   }
   
   private class ColorfulButton extends JButton
   {
      public static final float DEFAULT_HORIZONTAL_MARGIN = 0.1f;
      public static final float DEFAULT_VERTICAL_MARGIN = 0.1f;
      
      private final Color DISABLE_COLOR = new Color(128, 128, 128, 120);
      
      private Color color;
      
      private float horizMargin;
      private float vertMargin;
      
      public ColorfulButton(Color color)
      {
         this(color, DEFAULT_HORIZONTAL_MARGIN, DEFAULT_VERTICAL_MARGIN);
      }
      
      public ColorfulButton(Color color, 
                            float topMargin, 
                            float sideMargin)
      {
         super();
         
         setColor(color);
         setHorizontalMargin(topMargin);
         setVertMargin(sideMargin);
      }
      
      public Color getColor()
      {
         return color;
      }
      
      public void setColor(Color color)
      {
         if (color!=null)
            this.color = color;
      }
      
      public float getHorizontalMargin()
      {
         return horizMargin;
      }
      
      public void setHorizontalMargin(float margin)
      {
         if (margin<0 || margin>0.5)
            this.horizMargin = DEFAULT_HORIZONTAL_MARGIN;
         else
            this.horizMargin = margin;
      }
      
      public float getVerticalMargin()
      {
         return vertMargin;
      }
      
      public void setVertMargin(float margin)
      {
         if (margin<0 || margin>0.5)
            this.vertMargin = DEFAULT_VERTICAL_MARGIN;
         else
            this.vertMargin = margin;
      }
      
      public void paintComponent(Graphics g)
      {
         super.paintComponent(g);
         
         Dimension dim = getSize();
         int topMargin = (int)(getHorizontalMargin()*dim.getHeight());
         int sideMargin = (int)(getVerticalMargin()*dim.getWidth());
         
         int width = (int)(dim.getWidth()-2*sideMargin);
         int height = (int)(dim.getHeight()-2*topMargin);
         
         g.setColor(getColor());
         g.fillRect(sideMargin, topMargin, width, height);
         
         if (!isEnabled())
         {
            g.setColor(DISABLE_COLOR);
            g.fillRect(1, 1, (int)(dim.getWidth()-1), 
                             (int)(dim.getHeight()-1));
         }
      }
   }
}
