package runtime;

import com.github.tomakehurst.wiremock.common.ConsoleNotifier;
import com.github.tomakehurst.wiremock.junit.WireMockRule;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.FileAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.net.URI;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;

import circuitruntime.CircuitApplication;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

@RunWith(Arquillian.class)
public class TestCircuit {

    private Client client;

    @Before
    public void setup() throws Exception {
        client = ClientBuilder.newClient();
    }

    @After
    public void tearDown() throws Exception {
        client.close();
    }

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(options().port(8090)
            .notifier(new ConsoleNotifier(true)));

    @ArquillianResource
    protected URI baseURL;

    @Deployment(testable = false)
    public static Archive<?> createDeployment() throws Exception {
        WebArchive deployment = ShrinkWrap.create(WebArchive.class);

        deployment.addPackage(CircuitApplication.class.getPackage());
        deployment.addAsResource("project-defaults.yml", "project-defaults.yml");
        return deployment;
    }

    @Test
    @InSequence(1)
    public void testGetWithOKExpectednoneFallback() throws Exception {

        simulate200Operation();

        String response = client.target(baseURL + "/rest")
                .request().get(String.class);

        Assert.assertEquals("nonefallback", response);

    }

    @Test
    @InSequence(2)
    public void testGetWithOKSecondExpectednoneFallback() throws Exception {

        simulate200Operation();

        String response = client.target(baseURL + "/rest")
                .request().get(String.class);

        Assert.assertEquals("nonefallback", response);

    }

    @Test
    @InSequence(3)
    public void testGetWithFailureFirstExpectedFallback() throws Exception {

        simulateFailureOperation();

        String response = client.target(baseURL + "/rest")
                .request().get(String.class);

        Assert.assertEquals("fallback", response);

    }

    @Test
    @InSequence(4)
    public void testGetWithFailureSecondExpectedFallback() throws Exception {

        simulate200Operation();

        String response = client.target(baseURL + "/rest")
                .request().get(String.class);

        Assert.assertEquals("fallback", response);


    }

    public static void simulate200Operation() {
        stubFor(get(urlPathMatching("/operation"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.TEXT_PLAIN)
                        .withBody("nonefallback")
                        .withStatus(200)));
    }

    public static void simulateFailureOperation() {
        stubFor(get(urlPathMatching("/operation"))
                .willReturn(aResponse()
                        .withStatus(500)));
    }


}
