package server;

import config.Htaccess;
import config.HttpdConf;
import config.MimeTypes;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Resource {
  private String absolutePath = "";
  private String path = "";
  private boolean isScript = false;
  private MimeTypes mimes;
  private HttpdConf config;
  private String fileExtension = "";
  private String uri = "";
  private ArrayList<String> queryStrings = new ArrayList<>();


  public Resource( String uri, HttpdConf config, MimeTypes mimes ) {

    this.config = config;
    this.mimes = mimes;
    this.uri = uri;
    resolvePath( uri );
  }

  public void resolvePath( String uri ) {
    if( config.getAlias( uri ) != null ) {
      absolutePath = config.getAlias( uri );
    }else if( config.getScriptAlias( stripUri( uri ) ) != null ) {
      absolutePath = config.getScriptAlias( stripUri( removeQueryStrings( uri ) ) ) + getScriptFile( removeQueryStrings( uri ) );
      getQueryStrings( uri );
      if( fileExists() ) {
        isScript = true;
      }
    }else {
      if( uri.length() > 0 ) {
        uri = uri.substring( 1 );
      }
      absolutePath = config.getDocumentRoot() + uri;
    }
    path = absolutePath.replace( uri, "" );
    if( !isFile() && !isScript ) {
      absolutePath += config.getDirectoryIndex();
    }
  }

  private String removeQueryStrings( String line ) {
    int queryStart = line.indexOf( "?" );
    return line.substring( 0, queryStart > 0 ? queryStart : line.length() );
  }

  private void getQueryStrings( String uri ) {
    List<String> queries;
    uri = uri.substring( uri.indexOf( "?" ) + 1, uri.length() );
    queries = Arrays.asList( uri.split( "&" ) );
    if( !uri.contains( "=" ) ) {
      return;
    }
    for( int index = 0; index < queries.size(); index++ ) {
      queryStrings.add( queries.get( index ) );
    }
  }

  private String stripUri( String uri ) {
    int slashes = 0, index;
    if( uri.equals( "" ) ) {
      return null;
    }
    for( index = 0; index < uri.length(); index++ ) {
      if( uri.charAt( index ) == '/' ) {
        slashes++;
      }
      if( slashes >= 2 ) {
        break;
      }
    }
    return slashes >= 2 ? uri.substring( 0, index + 1 ) : uri;
  }

  private String getScriptFile( String uri ) {
    int slashes = 0, index;
    if( uri.equals( "" ) ) {
      return null;
    }
    for( index = 0; index < uri.length(); index++ ) {
      if( uri.charAt( index ) == '/' ) {
        slashes++;
      }
      if( slashes >= 2 ) {
        break;
      }
    }
    return slashes >= 2 ? uri.substring( index + 1, uri.length() ) : uri;
  }

  public String getPath() {
    return path;
  }

  public boolean isFile() {
    if( absolutePath.contains( "." ) ) {
      fileExtension = absolutePath.substring( absolutePath.lastIndexOf( "." ) + 1 );
      return true;
    }
    return false;
  }

  public String getContentType() {
    String mime = null;
    if( isFile() ) {
      mime = mimes.lookup( fileExtension );
    }
    if( mime == null ) {
      return mimes.getDefaultMimeType();
    }
    return mime;
  }

  public boolean fileExists() {
    return new File( absolutePath ).exists();
  }

  private Boolean isValidExtension( String extension ) {
    return mimes.extExist( extension );
  }

  public String getAbsolutePath() {
    return absolutePath;
  }

  public boolean isScript() {
    return isScript;
  }

  public boolean isProtected() {
    return ( new File( getPath() + config.getAccessFile() ).exists() );
  }

  public Htaccess getHtaccess() throws IOException {
    if( isProtected() ) {
      return new Htaccess( getPath() + config.getAccessFile() );
    }else {
      return null;
    }
  }

  public HttpdConf getConfig() {
    return config;
  }

  public ArrayList<String> getQueryStrings() {
    return queryStrings;
  }
}
