package logger;

import responses.Response;
import request.Request;
import server.ResponseFactory;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.*;

public class Logger {
  private File file;
  private String IPaddress;
  private BufferedWriter output = null;


  public Logger( String logFilePath ) throws IOException {

    try {
      file = new File( logFilePath );
      if( !file.exists() ) {
        if( !(new File( getDirectory( logFilePath ) ).exists()) ) {
          Path path = FileSystems.getDefault().getPath( getDirectory( logFilePath ) );
          Files.createDirectory( path );
        }
        file.createNewFile();
      }
    }catch( IOException e ) {
      e.printStackTrace();
    }
  }

  private String getDirectory( String uri ) {
    int index = 0;
    if( uri.equals( "" ) ) {
      return null;
    }
    for( int i = 0; i < uri.length(); i++ ) {
      if( uri.charAt( i ) == '/' ) {
        index = i;
      }
    }
    return uri.substring( 0, index );
  }

  public synchronized void write( Request request, Response response ) throws IOException {
    try {
      output = new BufferedWriter( new FileWriter( file, true ) );
      String outputIndicator = "-";
      String log = IPaddress + " " + outputIndicator + " " + getUserID( getPassword( request.getHeaders() ) ) + " " + getTime() +
              " " + getRequestLine( request ) + " " + response.getStatusCode() + " " +
              getEntrySize( response ) + "\n";
      output.write( log );
    }catch( IOException e ) {
      e.printStackTrace();
    }finally {
      if( output != null ) {
        output.close();
      }
    }
  }

  private String getTime() {
    Date now = new Date();
    return ("[" + new SimpleDateFormat( "dd/MMM/yyyy:HH:mm:ss Z", Locale.ENGLISH ).format( now ) + "]");
  }

  private String getUserID( String authInfo ) {

    if( authInfo == null ) {
      return "-";
    }
    String credentials = new String(
            Base64.getDecoder().decode( authInfo ),
            Charset.forName( "UTF-8" ) );

    String[] tokens = credentials.split( ":" );
    return tokens[0];
  }

  private String getPassword( HashMap<String, String> hashMap ) {
    for( Map.Entry<String, String> entry : hashMap.entrySet() ) {
      if( entry.getKey().equals( ResponseFactory.AUTH ) ) {
        return entry.getValue().replace( "Basic ", "" );
      }
    }
    return null;
  }

  private String getRequestLine( Request request ) {
    return ("\"" + request.getVerb() + " " + request.getUri() + " " + request.getHttpVersion() + "\"");
  }

  public void setIPaddress( String IPaddress ) {
    this.IPaddress = IPaddress.replace( "/", "" );
  }

  private String getEntrySize( Response response ) {
    if( response.getContentSize() != null ) {
      return response.getContentSize();
    }
    return "-";
  }
}
