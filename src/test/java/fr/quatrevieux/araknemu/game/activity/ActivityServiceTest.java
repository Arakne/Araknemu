package fr.quatrevieux.araknemu.game.activity;

import fr.quatrevieux.araknemu.game.GameBaseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;

import java.time.Duration;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

class ActivityServiceTest extends GameBaseCase {
    private Logger logger;
    private ActivityService service;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        logger = Mockito.mock(Logger.class);
        service = new ActivityService(configuration.activity(), logger);
    }

    @Test
    void simpleTask() throws InterruptedException {
        Consumer<Logger> action = Mockito.mock(Consumer.class);

        Task task = new SimpleTask(action);

        service.execute(task);

        Thread.sleep(10);

        Mockito.verify(action).accept(logger);
        Mockito.verify(logger).info("Start task {}", task);
        Mockito.verify(logger).info("End task {}", task);
    }

    @Test
    void executeWithDelay() throws InterruptedException {
        Consumer<Logger> action = Mockito.mock(Consumer.class);

        Task task = new SimpleTask(action).setDelay(Duration.ofMillis(100));

        service.execute(task);

        Mockito.verify(action, Mockito.never()).accept(logger);

        Thread.sleep(200);
        Mockito.verify(action).accept(logger);
    }

    @Test
    void executeFailedShouldRetry() throws InterruptedException {
        RuntimeException exception = new RuntimeException("my error");

        Consumer<Logger> action = Mockito.mock(Consumer.class);
        Mockito.doThrow(exception).when(action).accept(logger);

        Task task = new SimpleTask(action)
            .setName("my task")
            .setDelay(Duration.ZERO)
            .setMaxTries(2)
            .setRetryDelay(Duration.ZERO)
        ;

        service.execute(task);
        Thread.sleep(50);

        Mockito.verify(action, Mockito.times(3)).accept(logger);
        Mockito.verify(logger, Mockito.times(2)).info("Retry execute the task {}", task);
        Mockito.verify(logger, Mockito.times(3)).error("Execution failed : my error for task my task", exception);
    }
}
