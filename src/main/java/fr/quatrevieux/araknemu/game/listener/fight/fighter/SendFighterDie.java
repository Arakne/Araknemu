package fr.quatrevieux.araknemu.game.listener.fight.fighter;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.fighter.event.FighterDie;
import fr.quatrevieux.araknemu.network.game.out.fight.action.ActionEffect;

/**
 * Send fighter die
 */
final public class SendFighterDie implements Listener<FighterDie> {
    final private Fight fight;

    public SendFighterDie(Fight fight) {
        this.fight = fight;
    }

    @Override
    public void on(FighterDie event) {
        fight.send(ActionEffect.fighterDie(event.caster(), event.fighter()));
    }

    @Override
    public Class<FighterDie> event() {
        return FighterDie.class;
    }
}
