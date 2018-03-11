package fr.quatrevieux.araknemu.game.player.spell;

import fr.quatrevieux.araknemu.data.living.entity.player.PlayerSpell;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.spell.SpellService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SpellBookEntryTest extends GameBaseCase {
    private SpellService service;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet
            .pushSpells()
        ;

        service = container.get(SpellService.class);
    }

    @Test
    void getters() {
        SpellBookEntry entry = new SpellBookEntry(
            new PlayerSpell(1, 3, true, 5, 'd'),
            service.get(3)
        );

        assertEquals(3, entry.spell().id());
        assertEquals(5, entry.spell().level());
        assertEquals('d', entry.position());
        assertTrue(entry.classSpell());
    }
}
