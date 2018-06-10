package fr.quatrevieux.araknemu.game.listener.fight.fighter;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.fight.event.FighterRemoved;

/**
 * Clear the fighter when removed from fight
 */
final public class ClearFighter implements Listener<FighterRemoved> {
    @Override
    public void on(FighterRemoved event) {
        event.fighter().cell().removeFighter();
    }

    @Override
    public Class<FighterRemoved> event() {
        return FighterRemoved.class;
    }
}
