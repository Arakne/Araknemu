package fr.quatrevieux.araknemu.game.activity;

import fr.quatrevieux.araknemu.game.GameConfiguration;
import org.slf4j.Logger;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Handle game activity
 */
final public class ActivityService {
    final private Logger logger;
    final private ScheduledExecutorService executor;

    public ActivityService(GameConfiguration.ActivityConfiguration configuration, Logger logger) {
        this.logger = logger;
        this.executor = Executors.newScheduledThreadPool(configuration.threadsCount());
    }

    /**
     * Execute the task
     */
    public void execute(Task task) {
        executor.schedule(
            () -> {
                try {
                    logger.info("Start task {}", task);
                    task.execute(logger);
                    logger.info("End task {}", task);
                } catch (RuntimeException e) {
                    logger.error("Execution failed : " + e.getMessage() + " for task " + task, e);

                    if (task.retry(this)) {
                        logger.info("Retry execute the task {}", task);
                    } else {
                        logger.warn("Cannot retry the task {}", task);
                    }
                }
            },
            task.delay().toMillis(),
            TimeUnit.MILLISECONDS
        );
    }
}
