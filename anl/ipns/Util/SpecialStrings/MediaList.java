package DataSetTools.util;




public class MediaList extends SpecialString implements IStringList
  {
    String LL[] = { "Console", "File", "Table"};

    public MediaList()
     {
        super("");
     }

    public MediaList( String message )
      {super( message );
      }
    public String getString( int position )
      {if( position < 0)
         return null;
       if( position > 2)
         return null;
       return LL[ position ];
      }
    public int  num_strings()
     { return 3;
     }
      public static void main( String args[] )
      {System.out.println("MediaList");
      }
   }
