package fr.quatrevieux.araknemu.game.event.listener.service;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.living.repository.player.PlayerSpellRepository;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.event.common.PlayerLoaded;
import fr.quatrevieux.araknemu.game.event.listener.player.spell.SaveLearnedSpell;
import fr.quatrevieux.araknemu.game.event.listener.player.spell.SaveSpellPosition;
import fr.quatrevieux.araknemu.game.event.listener.player.spell.SendLearnedSpell;
import fr.quatrevieux.araknemu.game.event.listener.player.spell.SendSpellList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class AddSpellListenersTest extends GameBaseCase {
    private AddSpellListeners listener;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        listener = new AddSpellListeners(container.get(PlayerSpellRepository.class));
    }

    @Test
    void onPlayerLoaded() throws SQLException, ContainerException {
        listener.on(
            new PlayerLoaded(gamePlayer())
        );

        assertTrue(gamePlayer().dispatcher().has(SendSpellList.class));
        assertTrue(gamePlayer().dispatcher().has(SaveSpellPosition.class));
        assertTrue(gamePlayer().dispatcher().has(SaveLearnedSpell.class));
        assertTrue(gamePlayer().dispatcher().has(SendLearnedSpell.class));
    }
}