package gov.anl.ipns.Util.File;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MakeBankFile
{

   /**
    * @param args
    */
   public static void main(String[] args)
   {
     int MAX_BANK_NUM = 240;

     SimpleDateFormat sf = new SimpleDateFormat("yyyy_MM_dd");
      
     String date =sf.format( new Date( ) );
     String outFileName ="C:/ISAW/InstrumentInfo/SNS/PG3/PG3_bank_"+date+".xml";
      try
      { 
         FileOutputStream fout = new FileOutputStream( outFileName );
         fout.write( "<?xml version='1.0' encoding='UTF-8'?>\n".getBytes( ) );
         fout.write( "<banking xmlns='http://neutrons.ornl.gov/SNS/ASG/Banking'>\n".getBytes( ) );
         fout.write( "  <facility>SNS</facility>\n".getBytes( ) );
         fout.write( "  <instrument>PG3</instrument>\n".getBytes()   );
         fout.write( ("  <date>"+date+"</date>\n").getBytes() );
         
         for (int i=1; i<=MAX_BANK_NUM; i++)
         {
            fout.write(("  <bank>\n    <number>"+i+"</number>\n    <x_size>154</x_size>\n"+
              "    <y_size>8</y_size>\n    <continuous_list>\n").getBytes() );
            fout.write( ("      <start>"+(i*1250)+"</start>\n" ).getBytes());
            fout.write( ("      <stop>"+(i*1250+1231)+"</stop>\n" ).getBytes());
            fout.write( "    </continuous_list>\n".getBytes() );
            fout.write("  </bank>\n\n".getBytes());
         }
         
         int total_pix = 8 * 154 * (MAX_BANK_NUM - 1); 
         fout.write(("  <total_pixel_number>"+total_pix+"</total_pixel_number>\n").getBytes());
         
         fout.write("</banking>\n".getBytes());
         fout.close( );
         System.out.println("FINISHED");
      } catch( Exception s )
      {
         s.printStackTrace( );
         System.exit( 0 );
      }

   }

}
