package fr.quatrevieux.araknemu.game.listener.player.spell;

import fr.quatrevieux.araknemu.data.living.repository.player.PlayerSpellRepository;
import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.player.spell.event.SpellUpgraded;
import fr.quatrevieux.araknemu.game.player.GamePlayer;

/**
 * Save the upgraded spell
 *
 * @todo optimize save
 */
final public class SaveUpgradedSpell implements Listener<SpellUpgraded> {
    final private GamePlayer player;
    final private PlayerSpellRepository repository;

    public SaveUpgradedSpell(GamePlayer player, PlayerSpellRepository repository) {
        this.player = player;
        this.repository = repository;
    }

    @Override
    public void on(SpellUpgraded event) {
        player.save();
        repository.add(event.entry().entity());
    }

    @Override
    public Class<SpellUpgraded> event() {
        return SpellUpgraded.class;
    }
}
