package io.github.rpost.spring.test.singlecontext;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.MergedContextConfiguration;
import org.springframework.test.context.cache.DefaultCacheAwareContextLoaderDelegate;

public class NoCacheContextLoaderDelegate extends DefaultCacheAwareContextLoaderDelegate {

    private final Object lock = new Object();

    private static MergedContextConfiguration config;
    private static ApplicationContext applicationContext;

    @Override
    public ApplicationContext loadContext(MergedContextConfiguration mergedContextConfiguration) {
        synchronized (lock) {
            if (mergedContextConfiguration.equals(config)) {
                return applicationContext;
            }
            if (applicationContext != null) {
                closeContext(applicationContext);
                applicationContext = null;
                config = null;
            }
            try {
                applicationContext = loadContextInternal(mergedContextConfiguration);
                config = mergedContextConfiguration;
                return applicationContext;
            } catch (Exception e) {
                throw new IllegalStateException("Failed to load ApplicationContext", e);
            }
        }
    }

    private void closeContext(ApplicationContext applicationContext1) {
        if (applicationContext1 instanceof ConfigurableApplicationContext) {
            ((ConfigurableApplicationContext) applicationContext1).close();
        }
    }

    @Override
    public void closeContext(MergedContextConfiguration mergedContextConfiguration, DirtiesContext.HierarchyMode hierarchyMode) {
        synchronized (lock) {
            if (mergedContextConfiguration.equals(config)) {
                closeContext(applicationContext);
            }
        }
    }
}

