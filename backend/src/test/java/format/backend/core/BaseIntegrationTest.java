package format.backend.core;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import io.minio.MinioClient;
import io.restassured.RestAssured;
import org.bson.Document;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.mongodb.MongoDBAtlasLocalContainer;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = RANDOM_PORT)
public abstract class BaseIntegrationTest {

    @LocalServerPort
    private int port;

    @MockitoBean
    protected MinioClient minioClient;

    @MockitoBean
    protected JwtDecoder jwtDecoder;

    @Autowired
    protected MongoTemplate mongoTemplate;

    @ServiceConnection
    private static final MongoDBAtlasLocalContainer mongoDBContainer =
            new MongoDBAtlasLocalContainer("mongodb/mongodb-atlas-local:8.3").waitingFor(Wait.forHealthcheck());

    static {
        mongoDBContainer.start();
    }

    @BeforeEach
    void setUpRestAssured() {
        RestAssured.port = port;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @AfterEach
    void cleanMongoDB() {
        mongoTemplate
                .getCollectionNames()
                .forEach(c -> mongoTemplate.getCollection(c).deleteMany(new Document()));
    }
}
