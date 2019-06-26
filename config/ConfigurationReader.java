package config;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public abstract class ConfigurationReader {

  private BufferedReader source;
  private String line;

  public ConfigurationReader( String fileName ) throws IOException {
    try {
      source = new BufferedReader( (new FileReader( fileName )) );
    }catch( IOException e ) {
      source = null;
    }
  }

  public boolean hasMoreLines() throws IOException {
    line = nextLine();
    return line == null ? false : true;
  }

  public String nextLine() throws IOException {
    try {
      return source.readLine();
    }catch( Exception e ) {
      return null;
    }
  }

  public String getLine() {
    return line;
  }

  public abstract void load() throws IOException;
}
