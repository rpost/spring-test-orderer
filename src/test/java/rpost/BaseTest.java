package rpost;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.test.context.BootstrapWith;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Import(BaseTest.ContextLogger.class)
class BaseTest {

    static final Logger log = LoggerFactory.getLogger(BaseTest.class);

    static class ContextLogger {

        @EventListener
        public void logStartup(ContextRefreshedEvent event) {
            log.warn("refreshed {}", event.getApplicationContext());
        }

        @EventListener
        public void logShutdown(ContextClosedEvent event) {
            log.warn("shutdown {}", event.getApplicationContext());
        }
    }

    protected void sleep() {
        try {
            Thread.sleep(2);
        } catch (InterruptedException e) {
        }
    }

}
