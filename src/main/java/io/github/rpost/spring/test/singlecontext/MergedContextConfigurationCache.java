package io.github.rpost.spring.test.singlecontext;

import org.springframework.test.context.MergedContextConfiguration;
import org.springframework.test.context.TestContextBootstrapper;

import java.util.HashMap;
import java.util.Map;

public class MergedContextConfigurationCache {
    public static final Map<Class<?>, TestContextBootstrapper> CACHE = new HashMap<>();

}
