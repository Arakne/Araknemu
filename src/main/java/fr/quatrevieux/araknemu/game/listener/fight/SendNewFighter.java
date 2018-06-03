package fr.quatrevieux.araknemu.game.listener.fight;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.event.FighterAdded;
import fr.quatrevieux.araknemu.network.game.out.game.AddSprites;

import java.util.Collections;

/**
 * Send the new added fighter
 */
final public class SendNewFighter implements Listener<FighterAdded> {
    final private Fight fight;

    public SendNewFighter(Fight fight) {
        this.fight = fight;
    }

    @Override
    public void on(FighterAdded event) {
        fight.send(new AddSprites(Collections.singleton(event.fighter().sprite())));
    }

    @Override
    public Class<FighterAdded> event() {
        return FighterAdded.class;
    }
}
