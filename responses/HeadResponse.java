package responses;

import server.Resource;
import java.io.IOException;

public class HeadResponse extends Response {

  public HeadResponse( Resource resource, int statusCode ) throws IOException {
    super( resource );
    reasonPhrase = "Success";
    this.statusCode = statusCode;
  }

  @Override
  public void read() throws IOException {
    body = null;
    headers.remove( "Content-Length" );
  }
}
