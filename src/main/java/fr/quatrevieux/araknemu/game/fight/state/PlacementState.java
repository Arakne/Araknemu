package fr.quatrevieux.araknemu.game.fight.state;

import fr.quatrevieux.araknemu.game.event.fight.FightJoined;
import fr.quatrevieux.araknemu.game.fight.Fight;

/**
 * Placement before start fight
 */
final public class PlacementState implements FightState {
    private long startTime;
    private Fight fight;

    @Override
    public void start(Fight fight) {
        this.fight = fight;
        startTime = System.currentTimeMillis();

        fight.fighters().forEach(fighter -> fighter.dispatch(new FightJoined(fight, fighter)));
    }

    @Override
    public int id() {
        return 2;
    }

    /**
     * Get the remaining placement time, in milliseconds
     */
    public long remainingTime() {
        return (fight.type().placementTime() * 1000 + startTime) - System.currentTimeMillis();
    }
}
