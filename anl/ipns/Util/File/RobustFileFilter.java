/*
 * File:  RobustFileFilter.java
 *
 * Copyright (C) 2003, Christopher M. Bouzek
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
 *           Menomonie, WI 54751, USA
 *
 *           Chris Bouzek <coldfusion78@yahoo.com>
 *
 * This work was supported by the National Science Foundation under grant
 * number DMR-0218882.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 * $Log$
 * Revision 1.1  2003/06/13 21:56:56  bouzekc
 * Added to CVS.
 *
 */
package DataSetTools.util;

//import javax.swing.filechooser.FileFilter;
import java.io.*;
import java.util.Vector;

/**
 *  This class adds additional functionality to Java's 
 *  {@link javax.swing.filechooser.FileFilter FileFilter}
 *  In addition to completing the methods that are abstract in FileFilter, it
 *  adds the capabilities to automatically ensure that the desired extension is
 *  put on the file name, as well as adding several other convenience methods.
 *  It holds the valid extensions (such as .jpg) in a String array.  The 
 *  filtered extensions may be lowercase or uppercase, although lowercase is
 *  preferred.
 */
public abstract class RobustFileFilter extends
                                       javax.swing.filechooser.FileFilter
{
  private Vector extensions;
  private String description;

  public RobustFileFilter()
  {
    super();
    //create a smaller Vector than the default size of 10 - three file
    //extensions seems like a good size
    extensions = new Vector(3,2);
  }

  /* --------- javax.swing.filechooser.FileFilter requirements ------------- */

  /**
   *  Whether the given file is accepted or not by this FileFilter.
   *
   *  @param                     The File to test acceptance on.
   * 
   *  @return                    True if file is accepted, false otherwise.
   */
  public boolean accept(File file)
  {
    if(file == null)
      return false;
    
    //want to be able to see directories
    if(file.isDirectory())
      return true;

    return this.acceptFileName(file.getName());
  }

  /**
   *  Gets the description of this FileFilter.
   *
   *  @return                The description of what files this FileFilter
   *                         shows.  A comma delimited list of the extensions
   *                         is returned if the subclass does not set the
   *                         description.
   */ 
  public String getDescription()
  {
    //no description specified, so make one.
    if(description == null)
    {
      StringBuffer s = new StringBuffer();
      for(int i = 0; i < extensions.size(); i++)
      {
        s.append(extensions.elementAt(i).toString());
        s.append(',');
      }

      //tear off the last comma
      description = s.deleteCharAt(s.length() - 1).toString();
    }
    return description;
  }

  /* ----- end javax.swing.filechooser.FileFilter requirements ------------- */
    
  /**
   *  Gets an existing file's suffix.
   *
   *  @param file             The file to retrieve the extension from.
   *
   *  @return                 The extension of the file in lowercase.
   */
  public final String getSuffix(File file)
  {
    //file.getName() returns the File name.
    String s, suffix;
    s = file.getName();
    suffix = null;
    
    //find the start of the extension
    int i = s.lastIndexOf('.');
    
    if (i > 0 && i < (s.length() -1) )
      suffix = s.substring(i).toLowerCase();

    return suffix;
  }

  /**
   *  Whether the given file name is accepted or not by this FileFilter.
   *
   *  @param filename            The file name to check.
   *
   *  @return                    True if file is accepted, false otherwise.
   */
  public final boolean acceptFileName(String filename)
  {
    String temp, ext;
    temp= filename.toLowerCase();

    for(int i = 0; i < extensions.size(); i++)
    {
      ext = (String)(extensions.elementAt(0));
      //does it end with the extension?
      if(temp.endsWith(ext))
        return true;
    }

    return false;
  }

  /** 
   *  Returns the preferred extension for this FileFilter.  Defaults to 
   *  first extension in the list.
   *
   *  @return                 String containing the preferred extension,
   *                          including leading '.'.  Returns null if the
   *                          extension list is empty.
   *                          
   */
  public String getPreferredExtension()
  {
    if(extensions == null || extensions.size() <= 0)
      return null;

    //default: return first extension
    return (String)(extensions.elementAt(0));
  }

  /**
   *  This method changes a File extension by changing the
   *  File name itself.  This is pretty much only useful if
   *  you want to be sure that your files are being saved with 
   *  the correct extension when you are saving a new File.
   */
  public final String appendExtension(String filename)
  {
    String suffix;
    int dotindex;

    dotindex = filename.indexOf('.');

    //file does not have extension, so add one
    if(dotindex < 0) 
    {  
      //get the suffix
      suffix = this.getPreferredExtension();
      filename = filename + suffix;
    }
    return filename;
  }

  /**
   *  Sets the description for this FileFilter.
   *
   *  @param  desc            The description for this FileFilter.  
   */
  protected final void setDescription(String desc)
  {
    description = desc;
  }

  /**
   *  Adds an extension to the list of extensions for this
   *  FileFilter.  The extension is converted to lower case before
   *  being stored.
   *
   *  @param  ext             The extension to add to this FileFilter's
   *                          list of extensions.  A '.' is automatically
   *                          prepended if the extension does not have one.
   */
  protected final void addExtension(String ext)
  {
    //only add the '.' if it is not there
    if(ext.indexOf('.') < 0)
      ext = '.' + ext;
      
    extensions.add(ext.toLowerCase());
  }

  /**
   *  Sets the extension list for this FileFilter.  Useful if you want to 
   *  quickly add lots of extensions.  The extensions area converted to lower 
   *  case before being stored.
   *
   *  @param  extList         The Vector of extensions (Strings) to add to 
   *                          this FileFilterA '.' is automatically
   *                          prepended to each extension if it does not have 
   *                          one.
   */
  protected final void setExtensionList(Vector extList)
  {
    String temp;
    //only add the '.' if it is not there
    for(int i = 0; i < extList.size(); i++)
    {
      temp = (String)extList.elementAt(i);
      if( temp.indexOf('.') < 0 )
        temp = '.' + temp;
      temp = temp.toLowerCase();
    }
    extensions = extList;
  }
}
