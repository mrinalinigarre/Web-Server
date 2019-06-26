package responses;

import server.Resource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DeleteResponse extends Response {
  public DeleteResponse( Resource resource, int statusCode ) throws IOException {
    super( resource );
    reasonPhrase = "Delete";
    this.statusCode = statusCode;
  }

  @Override
  public void read() throws IOException {
    String newBody = statusCode + " " + reasonPhrase;
    body = newBody.getBytes();
    if( headers.containsKey( "Content-Length" ) ) {
      headers.replace( "Content-Length", String.valueOf( body.length ) );
    }else {
      headers.put( "Content-Length", String.valueOf( body.length ) );
    }
  }

  public static boolean deleteFile( String fileName ) throws IOException {
    try {
      Path file = Paths.get( stripSlash( fileName ) );
      Files.delete( file );
    }catch( Exception e ) {
      e.printStackTrace();
    }
    return true;
  }

  private static String stripSlash( String fileName ) {
    return fileName.replace( "//", "/" );
  }
}
