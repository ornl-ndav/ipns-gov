/*
 * File:  SysUtil.java   
 *
 * Copyright (C) 2002, Peter F. Peterson
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
 * Contact : Peter F. Peterson <pfpeterson@anl.gov>
 *           Intense Pulsed Neutron Source Division
 *           Argonne National Laboratory
 *           Argonne, IL 60439-4845
 *           USA
 *
 * This work was supported by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 * Modified:
 *
 *  $Log$
 *  Revision 1.3  2002/10/25 22:16:10  pfpeterson
 *  Added print statements for finding executables if
 *  SharedData.DEBUG is true.
 *
 *  Revision 1.2  2002/10/08 16:32:14  pfpeterson
 *  Improved the getExecInPath method to actually check the path
 *  (if available). Since how java gets the path is kinda goofy,
 *  it still supports the old method of checking a few fixed places.
 *
 *  Revision 1.1  2002/09/17 22:29:40  pfpeterson
 *  Added to CVS.
 *
 *
 *   
 */

package DataSetTools.util;

import  java.io.*;
import  java.net.*;

/**
 * Utility class to ease dealing with the underlying system.
 */
public class SysUtil{
    private static final int BUFF_SIZE=100;
    private static final int MARK_LIMIT=80;

    public static final int LINUX_ONLY=1;
    public static final int WINDOWS_ONLY=2;
    public static final int MAC_ONLY=3;
    public static final int LINUX_WINDOWS=4;
    public static final int LINUX_MAC=5;
    public static final int WINDOWS_MAC=6;
    public static final int ALL_OS=7;


    /* --------------------------- CONSTRUCTOR ----------------------------- */
    /**
     * The class cannot be instantiated
     */
    private SysUtil(){}

    /* -------------------------- STATIC METHODS -------------------------- */
    /**
     * begins the process specified by the command. If the working
     * directory does not matter specify "cwd" as null.
     *
     * @param command The command to run
     * @param cwd The current working directory. If it doesn't matter,
     * specify as null.
     */
    public static Process startProcess(String command, String cwd) 
                                                            throws IOException{
        // must specify a command
        if(command==null) return null;

        // get the working directory
        if(cwd!=null && cwd.length()<=0) cwd=null;
        if(cwd!=null){
            File dir=null;
            if(isDirectory(cwd)){
                dir=new File(cwd);
                return Runtime.getRuntime().exec(command,null,dir);
            }
        }

        // call the shorter version of the command
        return Runtime.getRuntime().exec(command);
    }

    /**
     * Get a BufferedReader attached to the output stream of a process.
     *
     * @param process The process to link to.
     */
    public static BufferedReader getSTDINreader(Process process){
        if(process==null) return null;

        InputStream  in_stream  = process.getInputStream();
        if(in_stream==null) return null;

        InputStreamReader  in_reader  = new InputStreamReader(in_stream);
        if(in_reader==null) return null;

        BufferedReader in  = new BufferedReader(in_reader);
        return in;
    }

    /**
     * Get a BufferedWriter attached to the input stream of a process.
     *
     * @param process The process to link to.
     */
    public static BufferedWriter getSTDOUTwriter(Process process){
        if(process==null) return null;

        OutputStream out_stream = process.getOutputStream();
        if(out_stream==null) return null;

        OutputStreamWriter out_write  = new OutputStreamWriter(out_stream);
        if(out_write==null) return null;

        BufferedWriter out = new BufferedWriter(out_write);
        return out;
    }

    /**
     * Get a BufferedReader attached to the error stream of a process.
     *
     * @param process The process to link to.
     */
    public static BufferedReader getSTDERRreader(Process process){
        if(process==null) return null;

        InputStream  err_stream = process.getErrorStream();
        if(err_stream==null) return null;

        InputStreamReader  err_reader = new InputStreamReader(err_stream);
        if(err_reader==null) return null;

        BufferedReader err = new BufferedReader(err_reader);
        return err;
    }

    /**
     * Read a line of text from the BufferedReader. In some cases this
     * can hang indefinitely (should be fixed).
     */
    public static String readline(BufferedReader in) throws IOException{
        char buff[]=new char[BUFF_SIZE];
        String result=null;
        int go_back=0;
        int size=0;

        while( ! in.ready() ){
            // wait until it is ready
        }

        for( size=0 ; size<BUFF_SIZE ; size++ ){
            if(! in.ready() ){
                if(size==0) return null;
            }else{
                buff[size]=(char)in.read();
                String charac=(new Character(buff[size])).toString();
                if(charac.equals("\n")){ 
                    go_back++;
                    break;
                }else if(buff[size]==-1){
                    go_back++;
                    break;
                }
            }
        }
        //in.read(buff,0,BUFF_SIZE); // default java way that doesn't work well

        if(size<go_back) return "";
        result=new String(buff,0,size);
        /*if(go_back>0){
          result=result.substring(0,result.length()-go_back-1);
          }*/
        return result;
    }

    /**
     * Write a line of text to the BufferedWriter.
     */
    public static void writeline(BufferedWriter out, String text)
                                                          throws IOException{
        out.write(text,0,text.length());
        out.newLine();
        out.flush();
    }

    /**
     * Just print the lines as they come in until the proper phrase comes up.
     */
    public static void jumpline(BufferedReader in, String match)
                                                          throws IOException{
        String output=readline(in);
        while(output.indexOf(match)<0){
            if(output!=null)System.out.println(output);
            output=readline(in);
        }
        if(output!=null)System.out.println(output);
        return;
    }

    /**
     * Method to get the location of the blind executable. Assumed to
     * be right next to the class file.
     */
    public static String getBinLocation(Class klass, String command){
        String result;

        // first try the path (it returns null if not found)
        result=getExecInPath(command);
        if( result!=null && ! result.equals(command) )return result;
        if(SharedData.DEBUG) System.out.println("Trying next to operator");

        // get the class name and repair it
        String myClassName=klass.getClass().getName();
        myClassName="/"+myClassName.replace('.','/')+".class";
        
        // get the url for the class
        URL url=klass.getClass().getResource(myClassName);
        if(url==null) return null;
        String urlStr=url.toString();
    
        // come up with where blind should be
        if(urlStr!=null){
            urlStr=FilenameUtil.fixSeparator(urlStr);
            urlStr=URLDecoder.decode(urlStr);
            if(SharedData.DEBUG)
                System.out.println("File should be at: "+urlStr);
            int from=urlStr.indexOf("/");
            int to=urlStr.lastIndexOf("/");
            if(from<to && from>=0){
                result=urlStr.substring(from,to)+command;
            }
        }

        // confirm that it is there
        if(result!=null &&  fileExists(result)){
            return result;
        }

        // if it gets here it has failed
        if(SharedData.DEBUG)
            System.out.println("Did not find command: "+command);
        return null;
    }

    /**
     * Find the name of the executable in the path. If the path is not
     * available via the system properties (remember "java
     * -DPATH=$PATH" or "java -DPATH=%PATH%") this tries some likely
     * places ($HOME/bin, /usr/local/bin, and /usr/bin in that order
     * for linux) to find the fully qualified command.
     */
    public static String getExecInPath(String command){
        String  com   = null;
        String  path  = null;
        String  sep   = System.getProperty("path.separator");
        boolean found = false;

        // make sure that a command was given
        if(command==null) return null;

        // see if the path is listed in lowercase
        path=System.getProperty("path");
        // and try uppercase
        if(path==null){
            if(SharedData.DEBUG) System.out.println("path not defined");
            path=System.getProperty("PATH");
        }
        // if we are in linux here is a guess of the path
        if(path==null){
            if(SharedData.DEBUG) System.out.println("PATH not defined");
            path=System.getProperty("ISAW_HOME");
            if(isOSokay(LINUX_ONLY)){
                if(SharedData.DEBUG)
                    System.out.println("Adding default linux path");
                path=path+sep+System.getProperty("user.home")+"/bin"+sep
                    +"/usr/local/bin"+sep+"/usr/bin";
            }
        }

        // search through the path for the executable
        int index=path.indexOf(sep);
        while(index>0){
            com=FilenameUtil.fixSeparator(path.substring(0,index)+"/"+command);
            path=path.substring(index+1);
            index=path.indexOf(sep);
            if(fileExists(com)){
                found=true;
                break;
            }
        }
        // one last time for the remainder of the path after the last seperator
        if(! found){
            com=FilenameUtil.fixSeparator(path+"/"+command);
            if(fileExists(com)) found=true;
        }

        if(found){ // return the fully qualified version (if possible)
            if(SharedData.DEBUG) System.out.println("Found exec: "+com);
            return com;
        }else{     // otherwise return what was sent here and hope for the best
            if(SharedData.DEBUG)
                System.out.println("Could not find exec: "+command);
            return command;
        }
    }

    /**
     * Determine if the os is supported. Linux only is the default.
     */
    public static boolean isOSokay(){
        return isOSokay(LINUX_ONLY);
    }

    /**
     * Determine if the os is supported. See the fields for values of
     * accepted.
     */
    public static boolean isOSokay(int accepted){
        if(accepted==ALL_OS) return true;

        String os=System.getProperty("os.name");

        // nothing there must be bad
        if(os==null) return false;

        // fix up the string
        os=os.trim();
        os=os.toLowerCase();

        //create a code for the os
        int osI=0;
        if(os.startsWith("linux")){
            osI=LINUX_ONLY;
        }else if(os.startsWith("windows")){
            osI=WINDOWS_ONLY;
        }else if(os.startsWith("mac")){
            osI=MAC_ONLY;
        }else{
            osI=0;
        }

        if(osI<=0) return false;

        // check for accepted os
        if(osI==LINUX_ONLY){
            return ( accepted==LINUX_ONLY || accepted==LINUX_WINDOWS ||
                                                        accepted==LINUX_MAC );

        }else if(osI==WINDOWS_ONLY){
            return ( accepted==WINDOWS_ONLY || accepted==LINUX_WINDOWS || 
                                                        accepted==WINDOWS_MAC );
        }else if(osI==MAC_ONLY){
            return ( accepted==MAC_ONLY || accepted==LINUX_MAC || 
                                                        accepted==WINDOWS_MAC );
        }else{
            return false;
        }
    }

    /**
     * Determines if the file exists. Encapsulating code.
     */
    public static boolean fileExists(String filename){
            File file=new File(filename);
            return file.exists();
    }

    /**
     * Check that the directoryname specified is a directory
     */
    public static boolean isDirectory(String dirname){
        if(fileExists(dirname)){
            File dir=new File(dirname);
            return dir.isDirectory();
        }
        return false;
    }
}
