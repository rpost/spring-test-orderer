package rpost;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;

class Test1 extends BaseTest {

    @MockBean
    Dummy dummy;

    @Test
    void test() {
        sleep();
    }
}


class Test2 extends BaseTest {
    @Test
    void test() {
        sleep();
    }
}

class Test3 extends BaseTest {
    @Test
    void test() {
        sleep();
    }
}

class Test4 extends BaseTest {

    @Nested
    class SubTest1 {
        @Test
        void test() {
            sleep();
        }
    }
}

class Dummy {}