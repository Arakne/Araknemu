/*
 * This file is part of Araknemu.
 *
 * Araknemu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Araknemu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Araknemu.  If not, see <https://www.gnu.org/licenses/>.
 *
 * Copyright (c) 2017-2022 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.util;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Replacement of {@link Executors} class for create {@link ScheduledExecutorService} instances
 *
 * By default, this factory will only forward calls to {@link Executors} for create executors.
 * A testing mode can be enabled using {@link ExecutorFactory#enableTestingMode()} which allows direct execution,
 * and keep tracking of all created instances for perform a shutdown when calling {@link ExecutorFactory#resetTestingExecutor()}
 */
public final class ExecutorFactory {
    private static boolean testing = false;
    private static boolean directExecution = true;
    private static final List<TestingExecutor> testingExecutors = new ArrayList<>();

    /**
     * Disable constructor
     */
    private ExecutorFactory() { }

    /**
     * Create a single thread executor service
     * If testing mode is enabled, a TestingExecutor is returned
     *
     * @see Executors#newSingleThreadScheduledExecutor()
     */
    public static ScheduledExecutorService createSingleThread() {
        if (testing) {
            return createTestingExecutor();
        }

        return Executors.newSingleThreadScheduledExecutor();
    }

    /**
     * Create an executor service with the requested amount of threads in the pool
     * If testing mode is enabled, a TestingExecutor is returned
     *
     * @see Executors#newScheduledThreadPool(int)
     */
    public static ScheduledExecutorService create(int corePoolSize) {
        if (testing) {
            return createTestingExecutor();
        }

        return Executors.newScheduledThreadPool(corePoolSize);
    }

    /**
     * Enable testing mode
     *
     * In testing mode all created executors instance are kept to perform shutdown on reset
     * Also all created executor will support direct execution (i.e. submitted tasks will be executed by the current thread)
     *
     * If testing mode is enabled, {@link ExecutorFactory#resetTestingExecutor()} should be called after tests
     * for free created threads
     *
     * /!\ This method must not be called on production environment
     *
     * @see ExecutorFactory#resetTestingExecutor() For reset all created executors
     * @see ExecutorFactory#enableDirectExecution() For enable direct execution (default mode)
     * @see ExecutorFactory#disableDirectExecution() For disable direct execution
     */
    public static void enableTestingMode() {
        testing = true;
        directExecution = true;
    }

    /**
     * Disable testing mode
     *
     * @see ExecutorFactory#enableTestingMode() For enable it
     */
    public static void disableTestingMode() {
        resetTestingExecutor();
        testing = false;
    }

    /**
     * Shutdown all created executors
     *
     * This method must be called after tests execution to free threads and memory
     * This method is also enabled the direct execution mode
     *
     * Note: this method as no effects if testing mode is disabled
     */
    public static void resetTestingExecutor() {
        for (TestingExecutor executor : testingExecutors) {
            try {
                executor.shutdownNow();
            } catch (RuntimeException e) {
                // Ignore
            }
        }

        testingExecutors.clear();
        directExecution = true;
    }

    /**
     * Enable direct execution of task
     * This is the default mode on testing mode.
     *
     * When enabled, all actions submitted using {@link java.util.concurrent.ExecutorService#execute(Runnable)}
     * or {@link java.util.concurrent.ExecutorService#submit(Runnable)} will be executed on the current thread
     * instead of the internal executor one.
     *
     * This mode has no impact on scheduled tasks, which will be executed asynchronously.
     *
     * Note: this method as no effects if testing mode is disabled
     *
     * @see ExecutorFactory#disableDirectExecution() For disable this mode
     */
    public static void enableDirectExecution() {
        directExecution = true;
    }

    /**
     * Disable direct execution of task
     *
     * After this method call, all calls to created executors will be executed asynchronously.
     *
     * Note: this method as no effects if testing mode is disabled
     *
     * @see ExecutorFactory#enableDirectExecution() For re-enabled this mode
     */
    public static void disableDirectExecution() {
        directExecution = false;
    }

    private static ScheduledExecutorService createTestingExecutor() {
        final TestingExecutor executor = new TestingExecutor();

        testingExecutors.add(executor);

        return executor;
    }

    /**
     * Implementation of ScheduledExecutorService allowing direct execution
     * All calls are forwarded to a real executor service
     */
    private static class TestingExecutor extends AbstractExecutorService implements ScheduledExecutorService {
        private final ScheduledExecutorService inner = Executors.newSingleThreadScheduledExecutor((r) -> {
            final Thread thread = new Thread(r);
            thread.setName("Testing");

            return thread;
        });

        @Override
        public ScheduledFuture<@Nullable ?> schedule(Runnable command, long delay, TimeUnit unit) {
            return inner.schedule(command, delay, unit);
        }

        @Override
        @SuppressWarnings({"override.return", "override.param"})
        public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
            return inner.schedule(callable, delay, unit);
        }

        @Override
        public ScheduledFuture<@Nullable ?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
            return inner.scheduleAtFixedRate(command, initialDelay, period, unit);
        }

        @Override
        public ScheduledFuture<@Nullable ?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
            return inner.scheduleWithFixedDelay(command, initialDelay, delay, unit);
        }

        @Override
        public void shutdown() {
            inner.shutdown();
        }

        @Override
        public List<Runnable> shutdownNow() {
            return inner.shutdownNow();
        }

        @Override
        public boolean isShutdown() {
            return inner.isShutdown();
        }

        @Override
        public boolean isTerminated() {
            return inner.isTerminated();
        }

        @Override
        public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
            return inner.awaitTermination(timeout, unit);
        }

        @Override
        public void execute(Runnable runnable) {
            if (directExecution) {
                runnable.run();
            } else {
                inner.execute(runnable);
            }
        }
    }
}
