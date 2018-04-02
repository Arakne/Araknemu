package fr.quatrevieux.araknemu.game.listener.fight;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.event.FighterPlaceChanged;
import fr.quatrevieux.araknemu.network.game.out.fight.FighterPositions;

/**
 * Send fighters positions on changed
 */
final public class SendFighterPositions implements Listener<FighterPlaceChanged> {
    final private Fight fight;

    public SendFighterPositions(Fight fight) {
        this.fight = fight;
    }

    @Override
    public void on(FighterPlaceChanged event) {
        fight.send(new FighterPositions(fight.fighters()));
    }

    @Override
    public Class<FighterPlaceChanged> event() {
        return FighterPlaceChanged.class;
    }
}
