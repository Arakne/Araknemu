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
 * Copyright (c) 2017-2020 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.fight;

import fr.quatrevieux.araknemu.core.event.DefaultListenerAggregate;
import fr.quatrevieux.araknemu.core.event.Dispatcher;
import fr.quatrevieux.araknemu.core.event.ListenerAggregate;
import fr.quatrevieux.araknemu.game.fight.castable.effect.EffectsHandler;
import fr.quatrevieux.araknemu.game.fight.event.FightCancelled;
import fr.quatrevieux.araknemu.game.fight.event.FightLeaved;
import fr.quatrevieux.araknemu.game.fight.event.FightStarted;
import fr.quatrevieux.araknemu.game.fight.event.FightStopped;
import fr.quatrevieux.araknemu.game.fight.exception.InvalidFightStateException;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.map.FightMap;
import fr.quatrevieux.araknemu.game.fight.module.FightModule;
import fr.quatrevieux.araknemu.game.fight.state.FightState;
import fr.quatrevieux.araknemu.game.fight.state.StatesFlow;
import fr.quatrevieux.araknemu.game.fight.team.FightTeam;
import fr.quatrevieux.araknemu.game.fight.turn.FightTurnList;
import fr.quatrevieux.araknemu.game.fight.type.FightType;
import fr.quatrevieux.araknemu.game.world.util.Sender;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.logging.log4j.Logger;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Handle fight
 */
final public class Fight implements Dispatcher, Sender {
    final private int id;
    final private FightType type;
    final private FightMap map;
    final private List<FightTeam> teams;
    final private StatesFlow statesFlow;
    final private Logger logger;
    final private List<FightModule> modules = new ArrayList<>();
    final private Map<Class, Object> attachments = new HashMap<>();
    final private ListenerAggregate dispatcher;

    final private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    final private FightTurnList turnList = new FightTurnList(this);
    final private EffectsHandler effects = new EffectsHandler();

    final private StopWatch duration = new StopWatch();

    public Fight(int id, FightType type, FightMap map, List<FightTeam> teams, StatesFlow statesFlow, Logger logger) {
        this.id = id;
        this.type = type;
        this.map = map;
        this.teams = teams;
        this.statesFlow = statesFlow;
        this.logger = logger;
        this.dispatcher = new DefaultListenerAggregate(logger);
    }

    /**
     * Register a module
     */
    public void register(FightModule module) {
        modules.add(module);
        dispatcher.register(module);
        module.effects(effects);
    }

    /**
     * Get the fight id
     */
    public int id() {
        return id;
    }

    /**
     * Get all teams
     */
    public List<FightTeam> teams() {
        return teams;
    }

    /**
     * Get one team
     *
     * @param number The team number
     */
    public FightTeam team(int number) {
        return teams.get(number);
    }

    /**
     * Get all fighters on the fight
     */
    public List<Fighter> fighters() {
        return teams
            .stream()
            .flatMap(fightTeam -> fightTeam.fighters().stream())
            .filter(Fighter::isOnFight)
            .collect(Collectors.toList())
        ;
    }

    /**
     * Get all fighters
     *
     * @param onlyInitialized true to returns only initialized fighter (is on the fight)
     */
    public List<Fighter> fighters(boolean onlyInitialized) {
        if (onlyInitialized) {
            return fighters();
        }

        return teams
            .stream()
            .flatMap(fightTeam -> fightTeam.fighters().stream())
            .collect(Collectors.toList())
        ;
    }

    /**
     * Get the fight map
     */
    public FightMap map() {
        return map;
    }

    /**
     * Get the current fight state
     */
    public FightState state() {
        return statesFlow.current();
    }

    /**
     * Get the current fight state if the type corresponds
     */
    @SuppressWarnings("unchecked")
    public <T extends FightState> T state(Class<T> type) {
        if (!type.isInstance(statesFlow.current())) {
            throw new InvalidFightStateException(type);
        }

        return (T) statesFlow.current();
    }

    /**
     * Start the next fight state
     */
    public void nextState() {
        statesFlow.next(this);
        modules.forEach(module -> module.stateChanged(statesFlow.current()));
    }

    /**
     * Get the fight type
     */
    public FightType type() {
        return type;
    }

    /**
     * Get the turn list
     */
    public FightTurnList turnList() {
        return turnList;
    }

    /**
     * Get the fight effects handle
     */
    public EffectsHandler effects() {
        return effects;
    }

    @Override
    public void send(Object packet) {
        String sPacket = packet.toString();

        for (FightTeam team : teams) {
            team.send(sPacket);
        }
    }

    @Override
    public void dispatch(Object event) {
        dispatcher.dispatch(event);
    }

    /**
     * Schedule an action to perform in fight with delay
     *
     * @param action Action to execute
     * @param delay The delay
     */
    public ScheduledFuture schedule(Runnable action, Duration delay) {
        return executor.schedule(
            () -> {
                try {
                    action.run();
                } catch (Throwable e) {
                    logger.error("Error on fight executor : " + e.getMessage(), e);
                }
            },
            delay.toMillis(),
            TimeUnit.MILLISECONDS
        );
    }

    /**
     * Execute an action into the fight executor thread
     *
     * @param action Action to execute
     */
    public void execute(Runnable action) {
        executor.execute(() -> {
            try {
                action.run();
            } catch (Throwable e) {
                logger.error("Error on fight executor : " + e.getMessage(), e);
            }
        });
    }

    /**
     * Get the fight dispatcher
     */
    public ListenerAggregate dispatcher() {
        return dispatcher;
    }

    /**
     * Start the fight
     */
    public void start() {
        schedule(turnList::start, Duration.ofMillis(200));
        dispatch(new FightStarted(this));

        duration.start();
    }

    /**
     * Stop the fight
     */
    public void stop() {
        duration.stop();
        turnList.stop();

        dispatch(new FightStopped(this));
    }

    /**
     * Cancel the fight
     *
     * Must be called before start the fight
     */
    public void cancel() {
        cancel(false);
    }

    /**
     * Cancel the fight
     *
     * @param force Force cancel the fight even if the fight is started
     */
    public void cancel(boolean force) {
        if (!force && active()) {
            throw new IllegalStateException("Cannot cancel an active fight");
        }

        fighters().forEach(fighter -> fighter.dispatch(new FightLeaved()));
        dispatch(new FightCancelled(this));
        destroy();
    }

    /**
     * Get the fight duration in milliseconds
     */
    public long duration() {
        return duration.getTime();
    }

    /**
     * Check if the fight is active
     */
    public boolean active() {
        return duration.isStarted();
    }

    /**
     * Attach an object to the fight
     */
    public void attach(Object object) {
        attachments.put(object.getClass(), object);
    }

    /**
     * Get an attachment by its type
     */
    @SuppressWarnings("unchecked")
    public <T> T attachment(Class<T> type) {
        return (T) attachments.get(type);
    }

    /**
     * Destroy fight after terminated
     */
    public void destroy() {
        executor.shutdownNow();

        teams.clear();
        map.destroy();
    }
}
