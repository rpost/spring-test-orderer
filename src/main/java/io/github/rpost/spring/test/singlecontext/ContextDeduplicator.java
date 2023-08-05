package io.github.rpost.spring.test.singlecontext;

import org.junit.jupiter.api.ClassDescriptor;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.ClassOrdererContext;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.util.AnnotationUtils;
import org.springframework.test.context.*;
import org.springframework.test.context.cache.DefaultCacheAwareContextLoaderDelegate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.DefaultBootstrapContext;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

//class SimpleClassDescriptor implements ClassDescriptor {
//
//    private final Class<?> testClass;
//
//    public SimpleClassDescriptor(Class<?> testClass) {
//        this.testClass = testClass;
//    }
//
//    @Override
//    public Class<?> getTestClass() {
//        return testClass;
//    }
//
//    @Override
//    public String getDisplayName() {
//        return "";
//    }
//
//    @Override
//    public boolean isAnnotated(Class<? extends Annotation> annotationType) {
//        return AnnotationUtils.isAnnotated(testClass, annotationType);
//    }
//
//    @Override
//    public <A extends Annotation> Optional<A> findAnnotation(Class<A> annotationType) {
//        return AnnotationUtils.findAnnotation(testClass, annotationType);
//    }
//
//    @Override
//    public <A extends Annotation> List<A> findRepeatableAnnotations(Class<A> annotationType) {
//        return AnnotationUtils.findRepeatableAnnotations(testClass, annotationType);
//    }
//}

public class ContextDeduplicator implements ClassOrderer, BeforeAllCallback, AfterAllCallback {

    private static final Map<Class<?>, MccCachingTestContextBootstrapper> CACHE = new HashMap<>();
    private static final List<Class<?>> lastOnesOfThatContext = new ArrayList<>();

    @Override
    public void orderClasses(ClassOrdererContext context) {
        List<ClassDescriptor> classDescriptors = new ArrayList<>(context.getClassDescriptors());
        List<ClassDescriptor> springTests = classDescriptors.stream().filter(ContextDeduplicator::isSpringTest).collect(Collectors.toList());
        List<List<ClassDescriptor>> clustered = clusterByEquality(springTests, ContextDeduplicator::createMcc);
        for (List<ClassDescriptor> cluster : clustered) {
            ClassDescriptor lastOneOfThatContext = cluster.get(cluster.size() - 1);
            lastOnesOfThatContext.add(lastOneOfThatContext.getTestClass());
        }
        List<ClassDescriptor> orderedSpringTests = flatten(clustered);
        context.getClassDescriptors().sort(Comparator.comparing(orderedSpringTests::indexOf));
    }

    private static boolean isSpringTest(ClassDescriptor classDescriptor) {
        return isSpringTest(classDescriptor.getTestClass());
    }

    private static boolean isSpringTest(Class<?> clazz) {
        return AnnotationUtils.findRepeatableAnnotations(clazz, ExtendWith.class)
                .stream()
                .flatMap(extendWith -> Stream.of(extendWith.value()))
                .anyMatch(aClass -> aClass.equals(SpringExtension.class));
    }

    private static MergedContextConfiguration createMcc(ClassDescriptor classDescriptor) {
        Class<?> testClass = classDescriptor.getTestClass();
        return createTestContextBootstrapper(testClass).buildMergedContextConfiguration();
    }

    private static MccCachingTestContextBootstrapper createTestContextBootstrapper(Class<?> testClass) {
        BootstrapContext bootstrapContext = new DefaultBootstrapContext(testClass, new NoCacheContextLoaderDelegate());
        TestContextBootstrapper testContextBootstrapper = PublicBootstrapUtils.resolveTestContextBootstrapper(bootstrapContext);
        MccCachingTestContextBootstrapper mccCachingTestContextBootstrapper = new MccCachingTestContextBootstrapper(testContextBootstrapper);
        CACHE.put(testClass, mccCachingTestContextBootstrapper);
        return mccCachingTestContextBootstrapper;
    }

    private static <T, K> List<List<T>> clusterByEquality(List<T> list, Function<T, K> keyExtractor) {
        Map<K, List<T>> clustered = new LinkedHashMap<>();
        for (T element : list) {
            clustered.compute(
                    keyExtractor.apply(element),
                    (k, v) -> {
                        if (v == null) {
                            return new ArrayList<>(Arrays.asList(element));
                        } else {
                            v.add(element);
                            return v;
                        }
                    }
            );
        }
        return new ArrayList<>(clustered.values());
    }

    private static <T> List<T> flatten(List<List<T>> listOfLists) {
        listOfLists.get(1);
        return listOfLists.stream().flatMap(List::stream).collect(Collectors.toList());
    }

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        Class<?> testClass = context.getRequiredTestClass();
        if (isSpringTest(testClass)) {
            ExtensionContext.Namespace namespace = ExtensionContext.Namespace.create(SpringExtension.class);
            ExtensionContext.Store store = context.getRoot().getStore(namespace);
            store.getOrComputeIfAbsent(
                    testClass,
                    aClass -> new TestContextManager(CACHE.get(testClass)),
                    TestContextManager.class
            );
        }
    }

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        Class<?> testClass = context.getRequiredTestClass();
        if (lastOnesOfThatContext.contains(testClass)) {
            MccCachingTestContextBootstrapper mccCachingTestContextBootstrapper = CACHE.get(testClass);
            CacheAwareContextLoaderDelegate cacheAwareContextLoaderDelegate = mccCachingTestContextBootstrapper.getBootstrapContext().getCacheAwareContextLoaderDelegate();
            cacheAwareContextLoaderDelegate.closeContext(mccCachingTestContextBootstrapper.buildMergedContextConfiguration(), null /* TODO */);
        }
    }

}


class MccCachingTestContextBootstrapper implements TestContextBootstrapper {

    private final TestContextBootstrapper delegate;
    private MergedContextConfiguration mergedContextConfiguration;

    MccCachingTestContextBootstrapper(TestContextBootstrapper delegate) {
        this.delegate = delegate;
    }

    @Override
    public void setBootstrapContext(BootstrapContext bootstrapContext) {
        delegate.setBootstrapContext(bootstrapContext);
    }

    @Override
    public BootstrapContext getBootstrapContext() {
        return delegate.getBootstrapContext();
    }

    @Override
    public TestContext buildTestContext() {
        return delegate.buildTestContext();
    }

    @Override
    public MergedContextConfiguration buildMergedContextConfiguration() {
        if (mergedContextConfiguration == null) {
            mergedContextConfiguration = delegate.buildMergedContextConfiguration();
        }
        return mergedContextConfiguration;
    }

    @Override
    public List<TestExecutionListener> getTestExecutionListeners() {
        return delegate.getTestExecutionListeners();
    }
}


