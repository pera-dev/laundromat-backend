package com.laundromat.app.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;

@Configuration
public class FirebaseConfiguration {

    private final Logger log = LoggerFactory.getLogger(LiquibaseConfiguration.class);

    @Bean
    public void init() throws IOException {
        FileInputStream refreshToken = new FileInputStream("src/main/resources/config/laundromat-test-firebase-adminsdk.json");

        FirebaseOptions options = new FirebaseOptions.Builder()
            .setCredentials(GoogleCredentials.fromStream(refreshToken))
            .setDatabaseUrl("https://laundromat-test.firebaseio.com")
            .build();

        FirebaseApp.initializeApp(options);
        log.debug("Starting Firebase...");
    }

}
