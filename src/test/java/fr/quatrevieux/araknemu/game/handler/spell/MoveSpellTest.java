package fr.quatrevieux.araknemu.game.handler.spell;

import fr.quatrevieux.araknemu.data.living.repository.player.PlayerSpellRepository;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.player.spell.SpellBookService;
import fr.quatrevieux.araknemu.network.game.in.spell.SpellMove;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.helpers.NOPLogger;

import static org.junit.jupiter.api.Assertions.*;

class MoveSpellTest extends GameBaseCase {
    private MoveSpell handler;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        handler = new MoveSpell();
        container.get(SpellBookService.class).preload(NOPLogger.NOP_LOGGER);

        gamePlayer(true);
    }

    @Test
    void moveSpellToFreePlace() throws Exception {
        handler.handle(
            session,
            new SpellMove(3, 5)
        );

        assertEquals(5, gamePlayer().spells().entry(3).position());

        assertEquals(5, dataSet.refresh(gamePlayer().spells().entry(3).entity()).position());
    }

    @Test
    void moveSpellWithAlreadyTakenPlace() throws Exception {
        gamePlayer().spells().entry(17).move(2);
        handler.handle(
            session,
            new SpellMove(3, 2)
        );

        assertEquals(2, gamePlayer().spells().entry(3).position());
        assertEquals(63, gamePlayer().spells().entry(17).position());

        assertEquals(2, dataSet.refresh(gamePlayer().spells().entry(3).entity()).position());
        assertEquals(63, dataSet.refresh(gamePlayer().spells().entry(17).entity()).position());
    }
}
