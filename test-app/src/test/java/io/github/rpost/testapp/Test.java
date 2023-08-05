package io.github.rpost.testapp;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class Test1 {

    @Test
    void test() {
    }
}

@TestPropertySource(properties = "dummy.property=true")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class Test2 {


    @Test
    void test() {
    }

}

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class Test3 {

    @Test
    void test() {
    }
}

@Component
class ContextLogger {

    static final Logger log = LoggerFactory.getLogger(ContextLogger.class);

    @EventListener
    public void logStartup(ContextRefreshedEvent event) {
        log.warn("refreshed {}", event.getApplicationContext());
    }

    @EventListener
    public void logShutdown(ContextClosedEvent event) {
        log.warn("shutdown {}", event.getApplicationContext());
    }
}
