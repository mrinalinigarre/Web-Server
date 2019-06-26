package request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Request {
  private String uri;
  private byte body[];
  private int bodyCounter;
  private String verb;
  private String httpVersion;
  private HashMap<String, String> headers;
  private BufferedReader source;
  private String line;


  private ArrayList<String> verbs;

  private void init() {
    headers = new HashMap<>();
    verbs = new ArrayList<>();
    verb = "";
    httpVersion = "";
    uri = "";
    bodyCounter = 0;
    body = new byte[1000];
    verbs.addAll( Arrays.asList( "GET", "HEAD", "POST", "PUT", "DELETE" ) );
  }

  public Request( InputStream stream ) {
    init();
    source = new BufferedReader( new InputStreamReader( stream ) );
  }

  public void parse() throws IOException {
    boolean hasBody = false;
    boolean readBody = false;

    int lineCount = 0;

    while( hasMoreLines() ) {
      List<String> splitCurrentLine;
      lineCount++;
      if( lineCount == 1 ) {
        splitCurrentLine = splitTheLine( line );
      }else {
        splitCurrentLine = splitTheLineByColon( line );
      }

      if( verbs.contains( splitCurrentLine.get( 0 ) ) && splitCurrentLine.size() == 3 ) {
        verb = splitCurrentLine.get( 0 );
        uri = splitCurrentLine.get( 1 );
        httpVersion = splitCurrentLine.get( 2 );
      }else if( splitCurrentLine.get( 0 ).toLowerCase().equals( "content-length" ) && !splitCurrentLine.get( 1 ).equals( "0" ) ) {
        body = new byte[Integer.valueOf( splitCurrentLine.get( 1 ) )];
        hasBody = true;
      }else if( splitCurrentLine.get( 0 ).equals( "" ) && hasBody ) {
        readBody = true;
      }else if( splitCurrentLine.get( 0 ).equals( "" ) && (!hasBody || readBody) ) {
        break;
      }else if( readBody ) {
        fillBody( line );
        hasBody = false;
      }else {
        fillHeader( splitCurrentLine );
      }
    }
  }

  private boolean hasMoreLines() throws IOException {
    line = nextLine();
    return line != null;
  }

  private String nextLine() throws IOException {
    try {
      return source.readLine();
    }catch( Exception e ) {
      return null;
    }
  }

  private void fillBody( String line ) {
    for( int i = 0; i < line.length(); i++ ) {
      body[bodyCounter++] = ( byte ) line.charAt( i );
    }
  }

  private void fillHeader( List<String> line ) {
    for( int index = 1; index < line.size(); index++ ) {
      headers.put( line.get( 0 ).trim(), line.get( 1 ).trim() );
    }
  }

  private List<String> splitTheLine( String line ) {
    return Arrays.asList( line.split( "\\s+" ) );
  }

  private List<String> splitTheLineByColon( String line ) {
    return Arrays.asList( line.split( ": " ) );
  }

  public String getUri() {
    return uri;
  }

  public byte[] getBody() {
    return body;
  }

  public String getVerb() {
    return verb;
  }

  public int getBodyCounter() {
    return bodyCounter;
  }

  public String getHttpVersion() {
    return httpVersion;
  }

  public String getHeader( String key ) {
    return headers.get( key );
  }

  public HashMap<String, String> getHeaders() {
    return headers;
  }
}