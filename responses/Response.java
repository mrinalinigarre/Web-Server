package responses;

import server.Resource;
import java.io.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Response {
  protected int statusCode;
  protected String reasonPhrase;
  protected byte[] body;
  private String httpVersion;
  protected Resource resource;
  protected HashMap<String, String> headers;
  private String userID;


  public Response( Resource resource ) throws IOException {
    httpVersion = "HTTP/1.1";
    reasonPhrase = "";
    this.resource = resource;
    headers = new HashMap<>();
    body = new byte[1000];

    File file = new File( resource.getAbsolutePath() );
    if( file.exists() && !resource.isScript() ) {
      body = new byte[( int ) file.length()];
      headers.put( "Content-Length", String.valueOf( body.length ) );
    }

    addDefaultHeaders();
    if( !resource.isScript() ) {
      setContentType();
    }
  }

  public void read() throws IOException {
    File file = new File( resource.getAbsolutePath() );
    if( file.exists() ) {
      FileInputStream fileInputStream = new FileInputStream( file );
      fileInputStream.read( body );
    }
  }

  public void send( OutputStream out ) throws IOException {
    PrintWriter printOut = new PrintWriter( out, true );
    read();
    if( body != null ) {
      setContentLength();
    }
    printOut.println( httpVersion + " " + statusCode + " " + reasonPhrase + "\r\n" + displayHeaders() );
    if( !resource.isScript() ) {
      insertNewLine( out );
    }
    if( body != null ) {
      out.write( body );
    }
    out.close();
  }

  private void insertNewLine( OutputStream out ) throws IOException {
    out.write( '\r' );
    out.write( '\n' );
  }

  private String displayHeaders() {
    StringBuilder header = new StringBuilder();
    for( Map.Entry<String, String> entry : headers.entrySet() ) {
      header.append( entry.getKey() ).append( ": " ).append( entry.getValue() ).append( "\r\n" );
    }
    return header.substring( 0, header.length() - 3 );
  }

  private void setContentType() {
    headers.put( "Content-type", resource.getContentType() );
  }

  private void setContentLength() {
    if( headers.containsKey( "Content-Length" ) ) {
      headers.replace( "Content-Length", String.valueOf( body.length ) );
    }else {
      headers.put( "Content-Length", String.valueOf( body.length ) );
    }
  }

  public void setUserID( String userID ) {
    this.userID = userID;
  }

  public void setHttpVersion( String httpVersion ) {
    this.httpVersion = httpVersion;
  }

  private void addDefaultHeaders() {
    headers.put( "Date", new Date().toString() );
    headers.put( "Server", "Ilya's and Stephen's dope server" );
  }

  public int getStatusCode() {
    return statusCode;
  }

  public String getContentSize() {
    return headers.get( "Content-Length" );
  }
}
