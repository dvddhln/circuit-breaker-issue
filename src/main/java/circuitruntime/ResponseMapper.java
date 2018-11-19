package circuitruntime;


import org.eclipse.microprofile.rest.client.ext.ResponseExceptionMapper;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

@Provider
public class ResponseMapper implements ResponseExceptionMapper<Exception> {


    @Override
    public boolean handles(int statusCode, MultivaluedMap<String, Object> headers) {
        Response.Status status = Response.Status.fromStatusCode(statusCode);
        return status.getFamily() != Response.Status.Family.SUCCESSFUL;
    }


    @Override
    public Exception toThrowable(Response response) {
        return new RuntimeException("A runtime exception");
    }
}
