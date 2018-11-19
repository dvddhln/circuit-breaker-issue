package circuitruntime;



import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;


import javax.ws.rs.GET;
import javax.ws.rs.Path;


@Path("/")
@RegisterProvider(ResponseMapper.class)
@RegisterRestClient
public interface RestService {

    @GET
    @Path("/operation")
    public String operation();
}
