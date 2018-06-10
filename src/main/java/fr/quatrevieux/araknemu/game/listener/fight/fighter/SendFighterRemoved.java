package fr.quatrevieux.araknemu.game.listener.fight.fighter;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.event.FighterRemoved;
import fr.quatrevieux.araknemu.network.game.out.game.RemoveSprite;

/**
 * Send to fight the removed fighter
 */
final public class SendFighterRemoved implements Listener<FighterRemoved> {
    final private Fight fight;

    public SendFighterRemoved(Fight fight) {
        this.fight = fight;
    }

    @Override
    public void on(FighterRemoved event) {
        fight.send(new RemoveSprite(event.fighter().sprite()));
    }

    @Override
    public Class<FighterRemoved> event() {
        return FighterRemoved.class;
    }
}
