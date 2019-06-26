package server;

import config.HttpdConf;
import config.MimeTypes;
import logger.Logger;
import request.Request;
import responses.Response;
import java.io.IOException;
import java.net.Socket;

public class Worker extends Thread {
  private Socket client;
  private MimeTypes mimes;
  private HttpdConf config;
  private Logger logger;

  public Worker( Socket socket, HttpdConf config, MimeTypes mimes, Logger logger ) throws IOException {
    this.client = socket;
    this.config = config;
    this.mimes = mimes;
    this.logger = logger;
    this.logger = new Logger( config.getLogFile() );
  }

  @Override
  public void run() {
    try {
      Request request = new Request( client.getInputStream() );
      request.parse();

      Resource resource = new Resource( request.getUri(), config, mimes );
      Response response = ResponseFactory.getResponse( request, resource );

      if( response != null ) {
        response.send( client.getOutputStream() );
        logger.setIPaddress( client.getInetAddress().toString() );
        logger.write( request, response );
      }

      client.close();
    }catch( IOException e ) {
      // e.printStackTrace();
    }
  }
}
