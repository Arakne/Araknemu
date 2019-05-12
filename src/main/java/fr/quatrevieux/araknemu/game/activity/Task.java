package fr.quatrevieux.araknemu.game.activity;

import org.slf4j.Logger;

import java.time.Duration;

/**
 * Activity task
 *
 * @see ActivityService#execute(Task)
 */
public interface Task {
    /**
     * Execute the action
     */
    public void execute(Logger logger);

    /**
     * The waiting delay before execute the task
     */
    public Duration delay();

    /**
     * Try to retry execute the task
     *
     * @return true if can retry, or false if cannot (or reach max tries limit)
     */
    public boolean retry(ActivityService service);

    /**
     * Get the task name
     */
    public String toString();
}
