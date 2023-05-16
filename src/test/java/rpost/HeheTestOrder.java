package rpost;

import org.junit.jupiter.api.ClassDescriptor;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.ClassOrdererContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.MergedContextConfiguration;
import org.springframework.test.context.TestContextManager;
import org.springframework.test.context.cache.ContextCache;
import org.springframework.test.context.support.DefaultTestContext;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;

public class HeheTestOrder implements ClassOrderer {

    static final Logger log = LoggerFactory.getLogger(BaseTest.class);

    @Override
    public void orderClasses(ClassOrdererContext context) {
        System.setProperty(ContextCache.MAX_CONTEXT_CACHE_SIZE_PROPERTY_NAME, "1");

        List<? extends Class<?>> classes = context.getClassDescriptors().stream().map(ClassDescriptor::getTestClass).collect(Collectors.toList());
        for (Class<?> clazz : classes) {
            try {
                Field mergedContextConfigurationField = DefaultTestContext.class.getDeclaredField("mergedContextConfiguration");
                mergedContextConfigurationField.setAccessible(true);
                TestContextManager testContextManager = new TestContextManager(clazz);
                DefaultTestContext testContext = (DefaultTestContext) testContextManager.getTestContext();
                MergedContextConfiguration mergedContextConfiguration = (MergedContextConfiguration) mergedContextConfigurationField.get(testContext);
                log.warn("{} - {} ", clazz.getName(), mergedContextConfiguration.hashCode());
            } catch (IllegalAccessException | NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
