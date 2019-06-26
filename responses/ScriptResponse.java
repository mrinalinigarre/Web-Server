package responses;

import server.Resource;
import server.ResponseFactory;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScriptResponse extends Response {
  String commandLineSwitches = "";
  String httpVersion = "";
  ArrayList<String> queryStrings = new ArrayList<>();
  HashMap<String, String> headers = new HashMap<>();

  public ScriptResponse( Resource resource, byte[] body, HashMap<String, String> headers, int statusCode ) throws IOException {
    super( resource );
    reasonPhrase = "OK";
    this.statusCode = statusCode;
    queryStrings = resource.getQueryStrings();
    this.headers = headers;
    addHeadersWithHttp();
    addQueryFromBody( body );
  }

  @Override
  public void read() throws IOException {
    if( !runScript( resource.getAbsolutePath() ) ) {
      this.statusCode = ResponseFactory.STATUS_CODE_INTERNAL_SERVER_ERROR;
      reasonPhrase = "Internal server error";
      String newBody = "\n" + statusCode + " " + reasonPhrase;
      body = newBody.getBytes();
      headers.put( "Content-Length", String.valueOf( body.length ) );
    }
  }

  private void addQueryFromBody( byte[] body ) {
    String convertBody = convertByteToString( body );
    queryStrings.add( convertBody );
  }

  private void addHeadersWithHttp() {
    for( Map.Entry<String, String> entry : headers.entrySet() ) {
      queryStrings.add( "HTTP_" + entry.getKey() );
    }
  }

  private String convertByteToString( byte[] body ) {
    StringBuilder byteToString = new StringBuilder();
    for( int i = 0; i < body.length; i++ ) {
      if( body[i] != 0 ) {
        byteToString.append( ( char ) body[i] );
      }
    }
    return byteToString.toString();
  }

  protected String[] parseLine( String line ) {
    return line.split( " " );
  }

  public void passVersionToArgument( String httpVersion ) {
    this.httpVersion += httpVersion + " ";
  }

  private List<String> buildArgumentList( String program, String commandLineSwitches, String uri ) {
    List<String> arguments = new ArrayList<String>();
    arguments.add( program );
    arguments.add( commandLineSwitches );
    arguments.add( uri );
    arguments.add( httpVersion );
    arguments.addAll( queryStrings );

    return arguments;
  }

  private boolean runScript( String uri ) throws IOException {
    InputStreamReader input;
    InputStreamReader error;
    BufferedReader bufferedReader;
    String newBody = "";
    bufferedReader = new BufferedReader( new FileReader( uri ) );
    String line = bufferedReader.readLine();
    String program;
    String programAndArgs[];

    if( line.contains( "#!" ) ) {
      programAndArgs = parseLine( line );
      program = programAndArgs[0].substring( 2 );
      for( int index = 1; index < programAndArgs.length; index++ ) {
        commandLineSwitches += programAndArgs[index];
      }
    }else {
      return false;
    }

    try {
      ProcessBuilder processBuilder = new ProcessBuilder( buildArgumentList( program, commandLineSwitches, uri ) );
      input = new InputStreamReader( processBuilder.start().getInputStream() );
      error = new InputStreamReader( processBuilder.start().getErrorStream() );

      int character = 0;
      if( error.read() != -1 ) {
        return false;
      }

      while( (character = input.read()) != -1 ) {
        newBody += ( char ) character;
      }

      body = new byte[newBody.length()];
      body = newBody.getBytes();
      return true;
    }catch( Exception e ) {
      System.out.println( e.getCause() + " " + e.getMessage() );
      return false;
    }
  }
}
