package testng.testrunnerfactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileHelper
{
    public static void writeFile( String fileName, String content )
    {
        FileWriter out = null;
        try
        {
            File target = new File( System.getProperty("user.dir"), "target" ).getCanonicalFile();
            File listenerOutput = new File( target, fileName );
            out = new FileWriter( listenerOutput, true );
            out.write( content );
            out.flush();
        }
        catch ( IOException e )
        {
            throw new RuntimeException( e );
        }
        finally
        {
            if ( out != null )
            {
                try
                {
                    out.close();
                }
                catch ( IOException e )
                {
                    throw new RuntimeException( e );
                }
            }
        }
    }
}
