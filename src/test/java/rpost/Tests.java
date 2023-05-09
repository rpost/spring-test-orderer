package rpost;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;

class Test1 extends BaseTest {

    @MockBean
    Dummy dummy;

    @Test
    void hehe() throws InterruptedException {
        Thread.sleep(500);
    }
}


@Order(1)
class Test2 extends BaseTest {
    @Test
    void hehe2() throws InterruptedException {
        Thread.sleep(500);
    }
}

class Dummy {}