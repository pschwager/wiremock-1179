package issue1179;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.VerificationException;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import com.github.tomakehurst.wiremock.verification.NearMiss;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.util.List;

public class WireMockExtension extends WireMockServer implements AfterEachCallback, BeforeEachCallback {
    private static final int RANDOM_PORT = 0;

    @Override
    public void beforeEach(ExtensionContext context) {
        start();
        WireMock.configureFor("localhost", RANDOM_PORT);
    }

    @Override
    public void afterEach(ExtensionContext context) {
        List<LoggedRequest> unmatchedRequests = findAllUnmatchedRequests();
        if (!unmatchedRequests.isEmpty()) {
            List<NearMiss> nearMisses = findNearMissesForAllUnmatchedRequests();
            if (nearMisses.isEmpty()) {
                throw VerificationException.forUnmatchedRequests(unmatchedRequests);
            }
            throw VerificationException.forUnmatchedNearMisses(nearMisses);
        }
        stop();
    }
}
