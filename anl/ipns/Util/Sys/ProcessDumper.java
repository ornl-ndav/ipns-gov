package gov.anl.ipns.Util.Sys;

import java.io.BufferedReader;

public class ProcessDumper extends Thread 
{
  BufferedReader reader = null;
  String         name = null;
  public ProcessDumper( BufferedReader reader, String name )
  {
    this.reader = reader;
    this.name = name;
  }
  
  public void run()
  {
	try
	{
//    System.out.println("Started Dumper for " + name );
	  String line = reader.readLine();
      while ( line != null )
      {
        System.out.println( line );
        line = reader.readLine();
      }
      reader.close();
//    System.out.println("Ended Dumper for " + name );
    }
    catch ( Exception ex)
    {
      System.out.println("EXCEPTION reading from process buffer " + name );
      ex.printStackTrace();
    }
  }  
}
