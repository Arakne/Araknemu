package fr.quatrevieux.araknemu.game.fight.turn;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.turn.event.NextTurnInitiated;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.turn.order.FighterOrderStrategy;

import java.util.List;
import java.util.Optional;

/**
 * Handle fight turns
 */
final public class FightTurnList {
    final private Fight fight;

    private List<Fighter> fighters;
    private int current;
    private FightTurn turn;

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
     * Start the turn system
     */
    public void start() {
        current = -1;

        next();
    }

    /**
     * Stop the current turn and start the next
     *
     * @todo test turn with return false
     * @todo test dead fighter
     */
    void next() {
        turn = null;
        fight.dispatch(new NextTurnInitiated());

        for (;;) {
            if (++current == fighters.size()) {
                current = 0;
            }

//            if (fighters.get(current).dead()) {
//                continue;
//            }

            turn = new FightTurn(fighters.get(current), fight, fight.type().turnDuration());

            if (turn.start()) {
                break;
            }
        }
    }
}
