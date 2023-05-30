package rpost;

import org.junit.jupiter.api.ClassDescriptor;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.ClassOrdererContext;
import org.springframework.test.context.TestContextManager;
import org.springframework.test.context.support.DefaultTestContext;

import java.util.List;
import java.util.stream.Collectors;

public class HeheTestOrder implements ClassOrderer {
    @Override
    public void orderClasses(ClassOrdererContext context) {
        List<? extends Class<?>> classes = context.getClassDescriptors().stream().map(ClassDescriptor::getTestClass).collect(Collectors.toList());
        for (Class<?> clazz : classes) {
            TestContextManager testContextManager = new TestContextManager(clazz);
            DefaultTestContext testContext = (DefaultTestContext) testContextManager.getTestContext();

        }
    }
}
