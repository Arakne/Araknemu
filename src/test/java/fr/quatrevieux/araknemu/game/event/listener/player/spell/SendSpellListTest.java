package fr.quatrevieux.araknemu.game.event.listener.player.spell;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.event.common.GameJoined;
import fr.quatrevieux.araknemu.network.game.out.spell.SpellList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class SendSpellListTest extends GameBaseCase {
    private SendSpellList listener;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        listener = new SendSpellList(
            gamePlayer(true)
        );

        requestStack.clear();
    }

    @Test
    void onJoinGame() throws SQLException, ContainerException {
        listener.on(
            new GameJoined()
        );

        requestStack.assertLast(
            new SpellList(gamePlayer().spells())
        );
    }
}