package fr.quatrevieux.araknemu.game.listener.fight;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.event.FightStarted;
import fr.quatrevieux.araknemu.network.game.out.fight.BeginFight;

/**
 * Send packet for start the fight
 */
final public class SendFightStarted implements Listener<FightStarted> {
    final private Fight fight;

    public SendFightStarted(Fight fight) {
        this.fight = fight;
    }

    @Override
    public void on(FightStarted event) {
        fight.send(new BeginFight());
    }

    @Override
    public Class<FightStarted> event() {
        return FightStarted.class;
    }
}
