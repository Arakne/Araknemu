package fr.quatrevieux.araknemu.game.event.listener.player.spell;

import fr.quatrevieux.araknemu.game.event.Listener;
import fr.quatrevieux.araknemu.game.event.spell.SpellBoostChanged;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.network.game.out.spell.SpellBoost;

/**
 * Send spell boost modifier to client
 */
final public class SendSpellBoost implements Listener<SpellBoostChanged> {
    final private GamePlayer player;

    public SendSpellBoost(GamePlayer player) {
        this.player = player;
    }

    @Override
    public void on(SpellBoostChanged event) {
        player.send(new SpellBoost(event.spellId(), event.modifier(), event.value()));
    }

    @Override
    public Class<SpellBoostChanged> event() {
        return SpellBoostChanged.class;
    }
}
