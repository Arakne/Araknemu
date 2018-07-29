package fr.quatrevieux.araknemu.game.listener.fight.fighter;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.fight.fighter.event.FighterCharacteristicChanged;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.network.game.out.account.Stats;

/**
 * Send fighter stats when characteristics has changed
 */
final public class SendStats implements Listener<FighterCharacteristicChanged> {
    final private PlayerFighter fighter;

    public SendStats(PlayerFighter fighter) {
        this.fighter = fighter;
    }

    @Override
    public void on(FighterCharacteristicChanged event) {
        fighter.send(new Stats(fighter.properties()));
    }

    @Override
    public Class<FighterCharacteristicChanged> event() {
        return FighterCharacteristicChanged.class;
    }
}
