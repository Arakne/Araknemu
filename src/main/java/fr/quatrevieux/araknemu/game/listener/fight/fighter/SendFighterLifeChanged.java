package fr.quatrevieux.araknemu.game.listener.fight.fighter;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.fighter.event.FighterLifeChanged;
import fr.quatrevieux.araknemu.network.game.out.fight.action.ActionEffect;

/**
 * Send the damage or heal
 */
final public class SendFighterLifeChanged implements Listener<FighterLifeChanged> {
    final private Fight fight;

    public SendFighterLifeChanged(Fight fight) {
        this.fight = fight;
    }

    @Override
    public void on(FighterLifeChanged event) {
        fight.send(ActionEffect.alterLifePoints(event.caster(), event.fighter(), event.value()));
    }

    @Override
    public Class<FighterLifeChanged> event() {
        return FighterLifeChanged.class;
    }
}
