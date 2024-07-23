package dev.shreeya;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TestcontainersTest extends AbstractTestcontainers {



    // Below method is used to test our test-container is created and running or not.
    @Test
    void canStartPostgresDB() {
    assertThat(postgreSQLContainer.isRunning()).isTrue();
    assertThat(postgreSQLContainer.isCreated()).isTrue();
    //assertThat(postgreSQLContainer.isHealthy()).isTrue();
    }


    }


   
