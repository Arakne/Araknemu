package fr.quatrevieux.araknemu.game.listener.service;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.living.repository.player.PlayerSpellRepository;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.listener.player.spell.*;
import fr.quatrevieux.araknemu.game.player.event.PlayerLoaded;
import fr.quatrevieux.araknemu.game.listener.player.spell.*;
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
        assertTrue(gamePlayer().dispatcher().has(SaveUpgradedSpell.class));
        assertTrue(gamePlayer().dispatcher().has(SendUpgradedSpell.class));
        assertTrue(gamePlayer().dispatcher().has(SendSpellBoost.class));
        assertTrue(gamePlayer().dispatcher().has(SendAllSpellBoosts.class));
    }
}