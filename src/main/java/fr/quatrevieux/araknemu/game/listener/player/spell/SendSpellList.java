package fr.quatrevieux.araknemu.game.listener.player.spell;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.player.event.GameJoined;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.network.game.out.spell.SpellList;

/**
 * Send spell list on join game
 */
final public class SendSpellList implements Listener<GameJoined> {
    final private GamePlayer player;

    public SendSpellList(GamePlayer player) {
        this.player = player;
    }

    @Override
    public void on(GameJoined event) {
        player.send(new SpellList(player.scope().properties().spells()));
    }

    @Override
    public Class<GameJoined> event() {
        return GameJoined.class;
    }
}
