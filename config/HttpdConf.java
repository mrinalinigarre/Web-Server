package config;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class HttpdConf extends ConfigurationReader {
  private HashMap<String, String> aliases;
  private HashMap<String, String> scriptAliases;
  private String serverRoot;
  private String documentRoot;
  private String port;
  private String logFile;
  private String directoryIndex;
  private String accessFile;

  public HttpdConf( String fileName ) throws IOException {
    super( fileName );
    aliases = new HashMap<>();
    scriptAliases = new HashMap<>();
    directoryIndex = "index.html";
    accessFile = ".htaccess";
    port = "8080";
    load();
  }

  @Override
  public void load() throws IOException {
    List<String> splitCurrentLine;
    while( hasMoreLines() ) {
      splitCurrentLine = splitTheLine( getLine() );
      switch( splitCurrentLine.get( 0 ) ) {
        case "ServerRoot":
          serverRoot = replaceQuotes( splitCurrentLine.get( 1 ) );
          break;
        case "DocumentRoot":
          documentRoot = replaceQuotes( splitCurrentLine.get( 1 ) );
          break;
        case "Listen":
          port = splitCurrentLine.get( 1 );
          break;
        case "LogFile":
          logFile = replaceQuotes( splitCurrentLine.get( 1 ) );
          break;
        case "DirectoryIndex":
          directoryIndex = splitCurrentLine.get( 1 );
          break;
        case "Alias":
          List<String> splitCurrentForAlias = splitTheLine( splitCurrentLine.get( 1 ) );
          aliases.put( splitCurrentForAlias.get( 0 ), replaceQuotes( splitCurrentForAlias.get( 1 ) ) );
          break;
        case "ScriptAlias":
          List<String> splitCurrentForScriptAlias = splitTheLine( splitCurrentLine.get( 1 ) );
          scriptAliases.put( splitCurrentForScriptAlias.get( 0 ), replaceQuotes( splitCurrentForScriptAlias.get( 1 ) ) );
          break;
        case "AccessFileName":
          accessFile = splitCurrentLine.get( 1 );
          break;
      }
    }
  }

  private List<String> splitTheLine( String line ) {
    return Arrays.asList( line.split( " ", 2 ) );
  }

  private String replaceQuotes( String oldString ) {
    return oldString.replace( "\"", "" );
  }

  public String getServerRoot() {
    return serverRoot;
  }

  public String getDocumentRoot() {
    return documentRoot;
  }

  public String getPort() {
    return port;
  }

  public String getAlias( String alias ) {
    return aliases.get( alias );
  }

  public String getDirectoryIndex() {
    return directoryIndex;
  }

  public String getScriptAlias( String scriptAlias ) {
    return scriptAliases.get( scriptAlias );
  }

  public String getLogFile() {
    return logFile;
  }

  public String getAccessFile() {
    return accessFile;
  }
}
