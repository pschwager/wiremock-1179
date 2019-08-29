package issue1179;

import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.io.File;
import java.io.IOException;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

public class MinimalTest {
    @RegisterExtension
    public static WireMockExtension wireMockExtension = new WireMockExtension();
    private TestClient underTest;

    private class TestClient {
        private WebTarget endpoint;

        public TestClient(int port) {
            ClientBuilder clientBuilder = ClientBuilder.newBuilder();
            clientBuilder.register(MultiPartFeature.class);
            Client client = clientBuilder.build();
            endpoint = client.target("http://localhost:" + port);
        }

        public void upload() throws IOException {
            FileDataBodyPart fileDataBodyPart = new FileDataBodyPart("file", File.createTempFile("wiremocktest", null), MediaType.APPLICATION_OCTET_STREAM_TYPE);

            try (final FormDataMultiPart multiPart = new FormDataMultiPart()) {
                multiPart.bodyPart(fileDataBodyPart);

                endpoint.path("upload").request(MediaType.APPLICATION_JSON_TYPE).post(Entity.entity(multiPart, multiPart.getMediaType()));
            }
        }
    }

    @BeforeEach
    void setup() {
        underTest = new TestClient(wireMockExtension.port());
    }

    @Test
    void test() throws IOException {
        underTest.upload();
    }
}
