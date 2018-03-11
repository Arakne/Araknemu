package fr.quatrevieux.araknemu.game.player.spell;

import fr.quatrevieux.araknemu.data.living.entity.player.PlayerSpell;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.spell.SpellService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SpellBookTest extends GameBaseCase {
    private SpellService service;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        service = container.get(SpellService.class);
        dataSet.pushSpells();
    }

    @Test
    void all() {
        List<SpellBookEntry> entries = Arrays.asList(
            new SpellBookEntry(new PlayerSpell(1, 3, true, 5, 'd'), service.get(3)),
            new SpellBookEntry(new PlayerSpell(1, 6, true, 2, 'c'), service.get(6))
        );

        SpellBook book = new SpellBook(entries);

        assertContainsAll(book.all(), entries.toArray());
    }
}