package fr.quatrevieux.araknemu.game.listener.player.spell;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.player.spell.event.SpellUpgraded;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.network.game.out.account.Stats;
import fr.quatrevieux.araknemu.network.game.out.spell.UpdateSpell;

/**
 * Send upgraded spell success
 */
final public class SendUpgradedSpell implements Listener<SpellUpgraded> {
    final private GamePlayer player;

    public SendUpgradedSpell(GamePlayer player) {
        this.player = player;
    }

    @Override
    public void on(SpellUpgraded event) {
        player.send(new UpdateSpell(event.entry()));
        player.send(new Stats(player));
    }

    @Override
    public Class<SpellUpgraded> event() {
        return SpellUpgraded.class;
    }
}
