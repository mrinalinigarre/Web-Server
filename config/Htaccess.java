package config;

import java.io.IOException;

public class Htaccess extends ConfigurationReader {
  private Htpassword userFile;
  private String authName = "";
  private String authType = "";

  public Htaccess( String fileName ) throws IOException {
    super( fileName );
  }

  protected void parseLine( String line ) throws IOException {

    String[] tokens = line.split( " " );

    switch( tokens[0] ) {
      case "AuthUserFile":
        userFile = new Htpassword( replaceQuotes( tokens[1] ) );
        break;
      case "AuthType":
        authType = tokens[1];
        break;
      case "AuthName":
        for( int i = 1; i < tokens.length; i++ ) {
          authName += replaceQuotes( tokens[i] + " " );
        }
        break;
      case "Require":
        String require = tokens[1];
        break;
    }
  }

  public void load() throws IOException {
    while( hasMoreLines() ) {
      parseLine( getLine() );
    }
  }

  public Htpassword getHtpassword() {
    return userFile;
  }

  private String replaceQuotes( String oldString ) {
    return oldString.replace( "\"", "" );
  }
}
