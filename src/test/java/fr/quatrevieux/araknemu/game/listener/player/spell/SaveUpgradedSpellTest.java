package fr.quatrevieux.araknemu.game.listener.player.spell;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.data.living.entity.player.PlayerSpell;
import fr.quatrevieux.araknemu.data.living.repository.player.PlayerSpellRepository;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.player.spell.event.SpellUpgraded;
import fr.quatrevieux.araknemu.game.player.spell.SpellBookEntry;
import fr.quatrevieux.araknemu.game.spell.SpellService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class SaveUpgradedSpellTest extends GameBaseCase {
    private SaveUpgradedSpell listener;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        listener = new SaveUpgradedSpell(
            gamePlayer(true),
            container.get(PlayerSpellRepository.class)
        );
    }

    @Test
    void onSpellUpgraded() throws ContainerException, SQLException {
        PlayerSpell spell = dataSet.push(new PlayerSpell(1, 202, false, 2, 3));

        spell.setLevel(3);
        gamePlayer().spells().setUpgradePoints(5);

        SpellBookEntry entry = new SpellBookEntry(spell, container.get(SpellService.class).get(202));

        listener.on(new SpellUpgraded(entry));

        assertEquals(3, dataSet.refresh(spell).level());
        assertEquals(5, dataSet.refresh(new Player(1)).spellPoints());
    }
}