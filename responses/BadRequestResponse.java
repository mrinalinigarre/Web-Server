package responses;

import server.Resource;
import java.io.IOException;

public class BadRequestResponse extends Response {

  public BadRequestResponse( Resource resource, int statusCode ) throws IOException {
    super( resource );
    reasonPhrase = "Bad Request";
    this.statusCode = statusCode;
  }

  @Override
  public void read() throws IOException {
    String newBody = statusCode + " " + reasonPhrase;
    body = newBody.getBytes();
  }
}
