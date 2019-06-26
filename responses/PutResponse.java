package responses;

import server.Resource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class PutResponse extends Response {

  private int bodyLength = 0;

  public PutResponse( Resource resource, int statusCode ) throws IOException {
    super( resource );
    reasonPhrase = "Created";
    this.statusCode = statusCode;
  }

  public boolean createFile( String fileName, byte[] body ) throws IOException {
    try {
      Path file = Paths.get( stripSlash( fileName ) );
      body = removeNullFromBody( body );
      Files.write( file, body );
      bodyLength = body.length;
      this.body = body;
      return true;
    }catch( Exception e ) {
      return false;
    }
  }

  private byte[] removeNullFromBody( byte[] body ) {
    int index = 0;
    for( ; index < body.length; index++ ) {
      if( body[index] == 0 ) {
        break;
      }
    }
    return Arrays.copyOf( body, index );
  }

  @Override
  public void read() throws IOException {
    if( headers.containsKey( "Content-Length" ) ) {
      headers.replace( "Content-Length", String.valueOf( bodyLength ) );
    }else {
      headers.put( "Content-Length", String.valueOf( bodyLength ) );
    }
  }

  private static String stripSlash( String fileName ) {
    return fileName.replace( "//", "/" );
  }
}
