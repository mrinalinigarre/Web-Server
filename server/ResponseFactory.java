package server;

import config.Htaccess;
import request.Request;
import responses.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ResponseFactory {

  private static final int STATUS_CODE_BAD_REQUEST = 400;
  private static final int STATUS_CODE_CREATED = 201;
  private static final int STATUS_CODE_NO_CONTENT = 204;
  private static final int STATUS_CODE_OK = 200;
  private static final int STATUS_CODE_NOT_MODIFIED = 304;
  private static final int STATUS_CODE_UNAUTHORIZED = 401;
  private static final int STATUS_CODE_FORBIDDEN = 403;
  private static final int STATUS_CODE_NOT_FOUND = 404;
  public static final int STATUS_CODE_INTERNAL_SERVER_ERROR = 500;
  public static final String AUTH = "Authorization";
  private static int code;
  private static String userID = "-";

  public static Response getResponse( Request request, Resource resource ) throws IOException {

    Response response = null;
    Htaccess htaccess = resource.getHtaccess();
    String verb = request.getVerb();
    if( htaccess != null ) {
      htaccess.load();
    }

    boolean accessCheck = false;
    if( verb.equals( "" ) || request.getUri().equals( "" ) || request.getHttpVersion().equals( "" ) ) {
      response = new BadRequestResponse( resource, STATUS_CODE_BAD_REQUEST );
    }else if( resource.isProtected() ) {
      if( !containsAuth( request.getHeaders() ) ) {
        code = STATUS_CODE_UNAUTHORIZED;
        response = new UnauthorizedResponse( resource, code );
      }else if( !htaccess.getHtpassword().isAuthorized( getPassword( request.getHeaders() ) ) ) {
        code = STATUS_CODE_FORBIDDEN;
        response = new UnauthorizedResponse( resource, code );
      }else {
        accessCheck = true;
        userID = getUsername( getPassword( request.getHeaders() ) );
      }
    }else {
      accessCheck = true;
    }

    if( accessCheck ) {
      if( !verb.equals( "PUT" ) && !resource.fileExists() ) {
        code = STATUS_CODE_NOT_FOUND;
        response = new NotFoundResponse( resource, code );
      }else if( verb.equals( "PUT" ) || (!verb.equals( "PUT" ) && resource.fileExists()) ) {
        if( resource.isScript() && resource.fileExists() ) {
          getSuccessCode( verb );
          response = new ScriptResponse( resource, request.getBody(), request.getHeaders(), code );
          (( ScriptResponse ) response).passVersionToArgument( request.getHttpVersion() );
        }else if( verb.equals( "PUT" ) ) {
          getSuccessCode( request.getVerb() );
          response = new PutResponse( resource, code );
          (( PutResponse ) response).createFile( resource.getConfig().getDocumentRoot() + request.getUri(), request.getBody() );
        }else if( verb.equals( "GET" ) ) {
          try {
            if( modifiedSince( getDate( request.getHeader( "If-Modified-Since" ) ), resource ) ) {
              getSuccessCode( verb );
              response = new SuccessResponse( resource, STATUS_CODE_OK );
            }else {
              code = STATUS_CODE_NOT_MODIFIED;
              response = new NotModifiedResponse( resource, code );
            }
          }catch( ParseException e ) {
            response = new BadRequestResponse( resource, STATUS_CODE_BAD_REQUEST );
          }
        }else if( verb.equals( "DELETE" ) ) {
          getSuccessCode( verb );
          response = new DeleteResponse( resource, code );
          DeleteResponse.deleteFile( resource.getConfig().getDocumentRoot() + request.getUri() );
        }else if( verb.equals( "HEAD" ) ) {
          getSuccessCode( verb );
          response = new HeadResponse( resource, code );
        }else {
          getSuccessCode( verb );
          response = new SuccessResponse( resource, code );
        }
      }
    }

    if( response != null ) {
      response.setHttpVersion( request.getHttpVersion() );
      response.setUserID( userID );
    }
    return response;
  }

  private static Date getDate( String dateString ) {
    Date newDate;
    if( dateString == null ) {
      return null;
    }
    try {
      SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "EEE, dd MMM yyyy HH:mm:ss ZZZ", Locale.ENGLISH );
      simpleDateFormat.setTimeZone( TimeZone.getTimeZone( "GMT" ) );
      newDate = simpleDateFormat.parse( dateString );
    }catch( ParseException e ) {
      return null;
    }
    return newDate;
  }

  public static Date getLongDate( long date ) {
    Date newDate;
    try {
      SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "EEE, dd MMM yyyy HH:mm:ss ZZZ", Locale.ENGLISH );
      simpleDateFormat.setTimeZone( TimeZone.getTimeZone( "GMT" ) );
      newDate = simpleDateFormat.parse( simpleDateFormat.format( date ) );
    }catch( ParseException e ) {
      return null;
    }
    return newDate;
  }

  private static boolean modifiedSince( Date modifiedOn, Resource resource ) throws ParseException {
    long fileModified = (new File( resource.getAbsolutePath() )).lastModified();
    Date fileLastModified = getLongDate( fileModified );
    if( modifiedOn == null ) {
      return true;
    }
    if( fileLastModified != null ) {
      if( fileLastModified.after( modifiedOn ) ) {
        return true;
      }else {
        return false;
      }
    }
    return false;
  }


  private static int getSuccessCode( String verb ) {
    switch( verb ) {
      case "PUT":
        code = STATUS_CODE_CREATED;
        break;
      case "DELETE":
        code = STATUS_CODE_NO_CONTENT;
        break;
      case "POST":
        code = STATUS_CODE_OK;
        break;
      case "GET":
      case "HEAD":
        code = STATUS_CODE_OK;
        break;
    }
    return code;
  }

  private static boolean containsAuth( HashMap<String, String> hashMap ) {
    return hashMap.containsKey( AUTH );
  }

  private static String getPassword( HashMap<String, String> hashMap ) {
    for( Map.Entry<String, String> entry : hashMap.entrySet() ) {
      if( entry.getKey().equals( AUTH ) ) {
        return entry.getValue().replace( "Basic ", "" );
      }
    }
    return null;
  }

  private static String getUsername( String authInfo ) {
    String credentials = new String(
            Base64.getDecoder().decode( authInfo ),
            Charset.forName( "UTF-8" ) );

    String[] tokens = credentials.split( ":" );
    return tokens[0];
  }
}
