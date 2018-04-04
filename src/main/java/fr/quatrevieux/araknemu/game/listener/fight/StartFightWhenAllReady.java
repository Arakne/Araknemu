package fr.quatrevieux.araknemu.game.listener.fight;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.event.FighterReadyStateChanged;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.state.PlacementState;

/**
 * Start the fight when all fighters are ready
 */
final public class StartFightWhenAllReady implements Listener<FighterReadyStateChanged> {
    final private Fight fight;
    final private PlacementState state;

    public StartFightWhenAllReady(Fight fight, PlacementState state) {
        this.fight = fight;
        this.state = state;
    }

    @Override
    public void on(FighterReadyStateChanged event) {
        if (event.ready() && fight.fighters().stream().allMatch(Fighter::ready)) {
            state.startFight();
        }
    }

    @Override
    public Class<FighterReadyStateChanged> event() {
        return FighterReadyStateChanged.class;
    }
}
