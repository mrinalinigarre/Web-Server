package config;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class MimeTypes extends ConfigurationReader {
  private HashMap<String, String> types = new HashMap<>();
  private String defaultMimeType = "text/text";

  public MimeTypes( String fileName ) throws IOException {
    super( fileName );
    load();
  }

  @Override
  public void load() throws IOException {
    List<String> splitCurrentLine;
    while( hasMoreLines() ) {
      splitCurrentLine = Arrays.asList( getLine().split( "\\s+" ) );
      switch( splitCurrentLine.get( 0 ) ) {
        case "#":
          break;
        case "\\r\\n":
          break;
        default:
          if( splitCurrentLine.size() > 1 ) {
            for( int i = 1; i < splitCurrentLine.size(); i++ ) {
              types.put( splitCurrentLine.get( i ), splitCurrentLine.get( 0 ) );
            }
          }
          break;
      }
    }
  }

  public String lookup( String extension ) {
    return types.get( extension );
  }

  public boolean extExist( String extension ) {
    return types.containsKey( extension );
  }

  public String getDefaultMimeType() {
    return defaultMimeType;
  }
}
