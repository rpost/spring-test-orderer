package org.springframework.test.context;

public class PublicBootstrapUtils {

    public static BootstrapContext createBootstrapContext(Class<?> testClass) {
        return BootstrapUtils.createBootstrapContext(testClass);
    }

    public static TestContextBootstrapper resolveTestContextBootstrapper(BootstrapContext bootstrapContext) {
        return BootstrapUtils.resolveTestContextBootstrapper(bootstrapContext);
    }

}
