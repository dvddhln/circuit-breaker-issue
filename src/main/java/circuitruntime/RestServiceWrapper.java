package circuitruntime;

import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.time.temporal.ChronoUnit;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;


@ApplicationScoped
public class RestServiceWrapper {


    @RestClient
    @Inject
    RestService restService;


    @Fallback(fallbackMethod = "operationFailure")
    @CircuitBreaker(successThreshold = 1, requestVolumeThreshold = 2, failureRatio = 0.5,
            delay = 15, failOn = {RuntimeException.class}, delayUnit = ChronoUnit.SECONDS)
    public String operation() {
        return restService.operation();

    }

    public String operationFailure() {
        return "fallback";
    }
}
