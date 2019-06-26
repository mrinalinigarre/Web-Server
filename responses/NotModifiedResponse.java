package responses;

import server.Resource;
import server.ResponseFactory;
import java.io.File;
import java.io.IOException;

public class NotModifiedResponse extends Response {

  public NotModifiedResponse( Resource resource, int statusCode ) throws IOException {
    super( resource );
    this.statusCode = statusCode;
    reasonPhrase = "Not Modified";
    headers.put( "Last-Modified", (ResponseFactory.getLongDate( new File( resource.getAbsolutePath() ).lastModified() )).toString() );
  }

  @Override
  public void read() throws IOException {
    String newBody = statusCode + " " + reasonPhrase;
    body = newBody.getBytes();
    headers.replace( "Content-Length", String.valueOf( body.length ) );
  }
}
