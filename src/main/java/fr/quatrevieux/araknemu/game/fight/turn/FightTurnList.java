package fr.quatrevieux.araknemu.game.fight.turn;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.turn.event.NextTurnInitiated;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.turn.order.FighterOrderStrategy;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Handle fight turns
 */
final public class FightTurnList {
    final private Fight fight;

    private List<Fighter> fighters;
    private int current;
    private FightTurn turn;
    private AtomicBoolean active = new AtomicBoolean(false);

    public FightTurnList(Fight fight) {
        this.fight = fight;
    }

    /**
     * Initialise the fighters order
     */
    public void init(FighterOrderStrategy orderStrategy) {
        if (fighters != null) {
            throw new IllegalStateException("FightTurnList is already initialised");
        }

        fighters = orderStrategy.compute(fight.teams());
    }

    /**
     * Get all fighters ordered by their turn order
     */
    public List<Fighter> fighters() {
        return fighters;
    }

    /**
     * Get the current turn
     */
    public Optional<FightTurn> current() {
        return Optional.ofNullable(turn);
    }

    /**
     * Get the current turn fighter
     */
    public Fighter currentFighter() {
        return fighters.get(current);
    }

    /**
     * Start the turn system
     */
    public void start() {
        active.set(true);
        current = -1;

        next();
    }

    /**
     * Stop the turn system
     */
    public void stop() {
        if (!active.getAndSet(false)) {
            return;
        }

        if (turn != null) {
            turn.stop();
            turn = null;
        }
    }

    /**
     * Stop the current turn and start the next
     *
     * @todo test start with return false
     */
    void next() {
        turn = null;
        fight.dispatch(new NextTurnInitiated());

        while (active.get()) {
            if (++current == fighters.size()) {
                current = 0;
            }

            if (fighters.get(current).dead()) {
                continue;
            }

            turn = new FightTurn(fighters.get(current), fight, fight.type().turnDuration());

            if (turn.start()) {
                break;
            }
        }
    }
}
