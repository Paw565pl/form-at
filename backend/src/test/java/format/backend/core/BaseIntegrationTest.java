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
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.mongodb.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;

@Profile("test")
@Testcontainers
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
    protected static final MongoDBContainer mongoDBContainer =
            new MongoDBContainer(DockerImageName.parse("mongo:8-noble"));

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
