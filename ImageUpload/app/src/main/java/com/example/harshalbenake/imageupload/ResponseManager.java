package com.example.harshalbenake.imageupload;

import android.content.Context;
import android.content.res.Resources;

/**
 * This class is used to maintain status and response.
 * Created by <b>Harshal Benake</b> on 24/08/15.
 */
public class ResponseManager {
    public int status;
    public String response;
    public String message;

    public static final int timeout = 30000;
    public static final int exception = -1;
    public static final int SC_BAD_GATEWAY = 502;
    public static final int SC_FORBIDDEN = 403;
    public static final int SC_GATEWAY_TIMEOUT = 504;
    public static final int SC_INTERNAL_SERVER_ERROR = 500;
    public static final int SC_NOT_FOUND = 404;
    public static final int SC_OK = 200;
    public static final int SC_REQUEST_TIMEOUT = 408;
    public static final int SC_SERVICE_UNAVAILABLE = 503;
    public static final int SC_UNAUTHORIZED = 401;

    /**
     * This method is used to handled response messages which are not getting onto web services.
     * @param status
     * @return String message
     */
    public static String handledResponseMessage(Context context,int status){
        Resources res=context.getResources();
        String message="Server Error";
        switch (status) {
            case SC_BAD_GATEWAY:
                message=res.getString(R.string.default_server_error_sc_bad_gateway);
                break;
            case SC_FORBIDDEN:
                message=res.getString(R.string.default_server_error_sc_forbidden);
                break;
            case SC_GATEWAY_TIMEOUT:
                message=res.getString(R.string.default_server_error_sc_gateway_timeout);
                break;
            case SC_INTERNAL_SERVER_ERROR:
                message=res.getString(R.string.default_server_error_sc_internal_server_error);
                break;
            case SC_NOT_FOUND:
                message=res.getString(R.string.default_server_error_sc_not_found);
                break;
            case SC_REQUEST_TIMEOUT:
                message=res.getString(R.string.default_server_error_sc_request_timeout);
                break;
            case SC_SERVICE_UNAVAILABLE:
                message=res.getString(R.string.default_server_error_sc_service_unavailable);
                break;
            case SC_UNAUTHORIZED:
                message=res.getString(R.string.default_server_error_sc_unauthorized);
                break;
            default:
                break;
        }
        return message;
    }
}
