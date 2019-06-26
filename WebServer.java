import config.HttpdConf;
import config.MimeTypes;
import logger.Logger;
import server.Worker;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class WebServer {
  private HttpdConf configuration;
  private MimeTypes mimeTypes;
  private final String configDirectory = "./conf/";

  private static WebServer webServer;
  Thread threadWorker;
  Logger logger;

  public WebServer() throws IOException {
    configuration = new HttpdConf( configDirectory + "httpd.conf" );
    mimeTypes = new MimeTypes( configDirectory + "mime.types" );
    logger = new Logger( configuration.getLogFile() );
  }

  private static WebServer getInstance() throws IOException {
    if( webServer == null ) {
      webServer = new WebServer();
    }
    return webServer;
  }

  public void start() throws IOException, SocketException {

    Socket client;
    ServerSocket socket = new ServerSocket( Integer.parseInt( configuration.getPort() ) );

    while( true ) {
      client = socket.accept();

      threadWorker = new Thread( new Worker( client, configuration, mimeTypes, logger ) );
      threadWorker.start();
    }
  }

  public static void main( String[] args ) throws IOException {
    WebServer server = WebServer.getInstance();
    server.start();
  }
}
