package fr.quatrevieux.araknemu.game.fight;

import fr.quatrevieux.araknemu.core.event.*;
import fr.quatrevieux.araknemu.game.fight.castable.effect.EffectsHandler;
import fr.quatrevieux.araknemu.game.fight.event.FightCancelled;
import fr.quatrevieux.araknemu.game.fight.event.FightLeaved;
import fr.quatrevieux.araknemu.game.fight.event.FightStarted;
import fr.quatrevieux.araknemu.game.fight.event.FightStopped;
import fr.quatrevieux.araknemu.game.fight.exception.InvalidFightStateException;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.map.FightMap;
import fr.quatrevieux.araknemu.game.fight.module.FightModule;
import fr.quatrevieux.araknemu.game.fight.state.*;
import fr.quatrevieux.araknemu.game.fight.team.FightTeam;
import fr.quatrevieux.araknemu.game.fight.turn.FightTurnList;
import fr.quatrevieux.araknemu.game.fight.type.FightType;
import fr.quatrevieux.araknemu.game.world.util.Sender;
import org.apache.commons.lang3.time.StopWatch;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
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
    final private List<FightModule> modules = new ArrayList<>();

    final private ListenerAggregate dispatcher = new DefaultListenerAggregate();
    final private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    final private FightTurnList turnList = new FightTurnList(this);
    final private EffectsHandler effects = new EffectsHandler(this);

    final private StopWatch duration = new StopWatch();

    public Fight(int id, FightType type, FightMap map, List<FightTeam> teams, StatesFlow statesFlow) {
        this.id = id;
        this.type = type;
        this.map = map;
        this.teams = teams;
        this.statesFlow = statesFlow;
    }

    public Fight(int id, FightType type, FightMap map, List<FightTeam> teams) {
        this(id, type, map, teams, new StatesFlow(
            new NullState(),
            new InitialiseState(),
            new PlacementState(),
            new ActiveState(),
            new FinishState()
        ));
    }

    /**
     * Register a module
     */
    public void register(FightModule module)
    {
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
     * Get all fighters
     */
    public List<Fighter> fighters() {
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
        return executor.schedule(action, delay.toMillis(), TimeUnit.MILLISECONDS);
    }

    /**
     * Execute an action into the fight executor thread
     *
     * @param action Action to execute
     */
    public void execute(Runnable action) {
        executor.execute(action);
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
        if (active()) {
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
     * Destroy fight after terminated
     */
    public void destroy() {
        executor.shutdown();

        teams.clear();
        map.destroy();
    }
}
