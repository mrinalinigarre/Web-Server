package responses;

import server.Resource;
import java.io.IOException;

public class UnauthorizedResponse extends Response {

  public UnauthorizedResponse( Resource resource, int statusCode ) throws IOException {
    super( resource );
    reasonPhrase = "Unauthorized";
    this.statusCode = statusCode;
    headers.put( "WWW-Authenticate", "Basic" );
  }

  @Override
  public void read() throws IOException {
    String newBody = statusCode + " Unauthorized";
    body = newBody.getBytes();
  }
}
