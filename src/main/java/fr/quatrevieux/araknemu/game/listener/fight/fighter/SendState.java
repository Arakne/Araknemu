package fr.quatrevieux.araknemu.game.listener.fight.fighter;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.fighter.event.FighterStateChanged;
import fr.quatrevieux.araknemu.network.game.out.fight.action.ActionEffect;

/**
 * Send the fighter state to clients
 */
final public class SendState implements Listener<FighterStateChanged> {
    final private Fight fight;

    public SendState(Fight fight) {
        this.fight = fight;
    }

    @Override
    public void on(FighterStateChanged event) {
        switch (event.type()) {
            case ADD:
                fight.send(ActionEffect.addState(event.fighter(), event.state()));
                break;

            case REMOVE:
                fight.send(ActionEffect.removeState(event.fighter(), event.state()));
                break;
        }
    }

    @Override
    public Class<FighterStateChanged> event() {
        return FighterStateChanged.class;
    }
}
