package responses;

import server.Resource;
import java.io.IOException;

public class NotFoundResponse extends Response {

  public NotFoundResponse( Resource resource, int statusCode ) throws IOException {
    super( resource );
    this.statusCode = statusCode;
    reasonPhrase = "Not found";
  }

  @Override
  public void read() throws IOException {
    String newBody = statusCode + " " + reasonPhrase;
    body = newBody.getBytes();
  }
}
