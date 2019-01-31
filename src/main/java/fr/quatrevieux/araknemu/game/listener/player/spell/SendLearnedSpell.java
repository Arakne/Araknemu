package fr.quatrevieux.araknemu.game.listener.player.spell;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.game.player.spell.event.SpellLearned;
import fr.quatrevieux.araknemu.network.game.out.info.Information;
import fr.quatrevieux.araknemu.network.game.out.spell.SpellList;

/**
 * Send spell new learned spell
 */
final public class SendLearnedSpell implements Listener<SpellLearned> {
    final private GamePlayer player;

    public SendLearnedSpell(GamePlayer player) {
        this.player = player;
    }

    @Override
    public void on(SpellLearned event) {
        player.send(new SpellList(player.scope().properties().spells()));
        player.send(Information.spellLearn(event.entry().spell().id()));
    }

    @Override
    public Class<SpellLearned> event() {
        return SpellLearned.class;
    }
}
