package config;

import java.io.IOException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.HashMap;

public class Htpassword extends ConfigurationReader {
  private HashMap<String, String> passwords;

  public Htpassword( String filename ) throws IOException {
    super( filename );

    this.passwords = new HashMap<String, String>();
    this.load();
  }

  protected void parseLine( String line ) {

    String[] tokens = line.split( ":" );

    if( tokens.length == 2 ) {
      passwords.put( tokens[0], tokens[1].replace( "{SHA}", "" ).trim() );
    }
  }

  @Override
  public void load() throws IOException {
    while( hasMoreLines() ) {
      parseLine( getLine() );
    }
  }

  public boolean isAuthorized( String authInfo ) {
    String credentials = new String(
            Base64.getDecoder().decode( authInfo ),
            Charset.forName( "UTF-8" ) );

    String[] tokens = credentials.split( ":" );
    return verifyPassword( tokens[0], tokens[1] );
  }

  public boolean verifyPassword( String username, String password ) {
    String pass = passwords.get( username );
    if( pass == null ) {
      pass = "";
    }
    return pass.equals( encryptClearPassword( password ) );
  }

  private String encryptClearPassword( String password ) {
    try {
      MessageDigest mDigest = MessageDigest.getInstance( "SHA-1" );
      byte[] result = mDigest.digest( password.getBytes() );
      return Base64.getEncoder().encodeToString( result );
    }catch( Exception e ) {
      return "";
    }
  }
}
