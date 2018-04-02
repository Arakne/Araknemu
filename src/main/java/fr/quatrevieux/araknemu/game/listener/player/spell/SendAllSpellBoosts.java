package fr.quatrevieux.araknemu.game.listener.player.spell;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.player.event.GameJoined;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.game.spell.boost.SpellModifiers;
import fr.quatrevieux.araknemu.game.spell.boost.SpellsBoosts;
import fr.quatrevieux.araknemu.network.game.out.spell.SpellBoost;

/**
 * Send all spells boosts on join game
 */
final public class SendAllSpellBoosts implements Listener<GameJoined> {
    final private GamePlayer player;

    public SendAllSpellBoosts(GamePlayer player) {
        this.player = player;
    }

    @Override
    public void on(GameJoined event) {
        for (SpellModifiers modifiers : player.spells().boosts().all()) {
            for (SpellsBoosts.Modifier modifier : SpellsBoosts.Modifier.values()) {
                if (modifiers.has(modifier)) {
                    player.send(
                        new SpellBoost(
                            modifiers.spellId(),
                            modifier,
                            modifiers.value(modifier)
                        )
                    );
                }
            }
        }
    }

    @Override
    public Class<GameJoined> event() {
        return GameJoined.class;
    }
}
