package responses;

import server.Resource;
import java.io.IOException;


public class SuccessResponse extends Response {

  public SuccessResponse( Resource resource, int statusCode ) throws IOException {
    super( resource );
    this.statusCode = statusCode;
    reasonPhrase = "OK";
  }
}
