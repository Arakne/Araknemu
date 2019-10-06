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
 * Copyright (c) 2017-2019 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.activity;

import fr.quatrevieux.araknemu.game.GameBaseCase;
import io.github.artsok.RepeatedIfExceptionsTest;
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

    @RepeatedIfExceptionsTest
    void simpleTask() throws InterruptedException {
        Consumer<Logger> action = Mockito.mock(Consumer.class);

        Task task = new SimpleTask(action);

        service.execute(task);

        Thread.sleep(10);

        Mockito.verify(action).accept(logger);
        Mockito.verify(logger).info("Start task {}", task);
        Mockito.verify(logger).info(Mockito.eq("End task {} in {}ms"), Mockito.eq(task), Mockito.anyLong());
    }

    @RepeatedIfExceptionsTest
    void executeWithDelay() throws InterruptedException {
        Consumer<Logger> action = Mockito.mock(Consumer.class);

        Task task = new SimpleTask(action).setDelay(Duration.ofMillis(100));

        service.execute(task);

        Mockito.verify(action, Mockito.never()).accept(logger);

        Thread.sleep(200);
        Mockito.verify(action).accept(logger);
    }

    @RepeatedIfExceptionsTest
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

    @RepeatedIfExceptionsTest
    void periodic() throws InterruptedException {
        Consumer<Logger> action = Mockito.mock(Consumer.class);

        Task task = new SimpleTask(action).setDelay(Duration.ofMillis(10));

        service.periodic(task);

        Thread.sleep(100);
        Mockito.verify(action, Mockito.atLeast(2)).accept(logger);
    }

    @RepeatedIfExceptionsTest
    void periodicWithException() throws InterruptedException {
        RuntimeException exception = new RuntimeException("my error");

        Consumer<Logger> action = Mockito.mock(Consumer.class);
        Mockito.doThrow(exception).when(action).accept(logger);

        Task task = new SimpleTask(action).setDelay(Duration.ofMillis(10)).setName("my task");

        service.periodic(task);

        Thread.sleep(100);
        Mockito.verify(logger, Mockito.atLeast(2)).error("Execution failed : my error for task my task", exception);
    }
}
