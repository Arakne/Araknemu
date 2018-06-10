package fr.quatrevieux.araknemu.game.listener.fight.fighter;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.fight.event.FightLeaved;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.network.game.out.fight.CancelFight;

/**
 * Send to the fighter that he has leave the fight
 */
final public class SendFightLeaved implements Listener<FightLeaved> {
    final private PlayerFighter fighter;

    public SendFightLeaved(PlayerFighter fighter) {
        this.fighter = fighter;
    }

    @Override
    public void on(FightLeaved event) {
        fighter.send(new CancelFight());
    }

    @Override
    public Class<FightLeaved> event() {
        return FightLeaved.class;
    }
}
