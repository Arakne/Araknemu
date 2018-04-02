package fr.quatrevieux.araknemu.game.fight.state;

import fr.quatrevieux.araknemu.core.event.EventsSubscriber;
import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.fight.event.FightJoined;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.event.FighterPlaceChanged;
import fr.quatrevieux.araknemu.game.fight.exception.FightException;
import fr.quatrevieux.araknemu.game.fight.exception.FightMapException;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.listener.fight.SendFighterPositions;

/**
 * Placement before start fight
 */
final public class PlacementState implements FightState, EventsSubscriber {
    private long startTime;
    private Fight fight;

    @Override
    public void start(Fight fight) {
        this.fight = fight;

        fight.dispatcher().register(this);
        startTime = System.currentTimeMillis();

        fight.fighters().forEach(fighter -> fighter.dispatch(new FightJoined(fight, fighter)));
    }

    @Override
    public int id() {
        return 2;
    }

    @Override
    public Listener[] listeners() {
        return new Listener[] {
            new SendFighterPositions(fight)
        };
    }

    /**
     * Get the remaining placement time, in milliseconds
     */
    public long remainingTime() {
        return (fight.type().placementTime() * 1000 + startTime) - System.currentTimeMillis();
    }

    /**
     * Try to change fighter start place
     *
     * @param fighter Fighter to move
     * @param cell The target cell
     */
    synchronized public void changePlace(Fighter fighter, FightCell cell) {
        if (!cell.walkable()) {
            throw new FightMapException("Not walkable");
        }

        if (!fighter.team().startPlaces().contains(cell.id())) {
            throw new FightException("Bad start cell");
        }

        fighter.move(cell);
        fight.dispatch(new FighterPlaceChanged(fighter));
    }
}
