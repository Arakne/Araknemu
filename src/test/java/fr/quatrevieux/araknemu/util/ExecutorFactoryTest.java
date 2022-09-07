package fr.quatrevieux.araknemu.util;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

class ExecutorFactoryTest {
    @BeforeEach
    void setUp() {
        ExecutorFactory.enableTestingMode();
    }

    @AfterEach
    void tearDown() {
        ExecutorFactory.resetTestingExecutor();
    }

    @Test
    void directExecution() {
        ExecutorFactory.enableTestingMode();
        ExecutorFactory.enableDirectExecution();

        AtomicBoolean b = new AtomicBoolean();

        ExecutorFactory.createSingleThread().execute(() -> {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
            }
            b.set(true);
        });

        assertTrue(b.get());
    }

    @Test
    void disableDirectExecution() {
        ExecutorFactory.disableDirectExecution();

        AtomicBoolean b = new AtomicBoolean();

        ExecutorFactory.createSingleThread().execute(() -> {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
            }
            b.set(true);
        });

        assertFalse(b.get());
    }

    @Test
    void resetTestingExecutorsShouldStopExecutors() {
        ScheduledExecutorService e1 = ExecutorFactory.createSingleThread();
        ScheduledExecutorService e2 = ExecutorFactory.createSingleThread();

        ExecutorFactory.resetTestingExecutor();

        assertTrue(e1.isShutdown());
        assertTrue(e2.isShutdown());
    }

    @Test
    void createExecutorOnTestingMode() {
        ScheduledExecutorService e = ExecutorFactory.createSingleThread();

        assertNotSame(ExecutorFactory.createSingleThread(), e);
        assertEquals("TestingExecutor", e.getClass().getSimpleName());
        assertEquals("TestingExecutor", ExecutorFactory.create(5).getClass().getSimpleName());
    }

    @Test
    void createWithoutTestingMode() {
        ExecutorFactory.disableTestingMode();

        ScheduledExecutorService e1 = ExecutorFactory.createSingleThread();
        ScheduledExecutorService e2 = ExecutorFactory.create(2);

        assertNotEquals("TestingExecutor", e1.getClass().getSimpleName());
        assertNotEquals("TestingExecutor", e2.getClass().getSimpleName());

        e1.shutdown();
        e2.shutdown();
    }
}
