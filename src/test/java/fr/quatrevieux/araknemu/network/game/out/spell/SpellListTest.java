package fr.quatrevieux.araknemu.network.game.out.spell;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.living.entity.player.PlayerSpell;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.core.event.DefaultListenerAggregate;
import fr.quatrevieux.araknemu.game.player.spell.SpellBook;
import fr.quatrevieux.araknemu.game.player.spell.SpellBookEntry;
import fr.quatrevieux.araknemu.game.spell.SpellService;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class SpellListTest extends GameBaseCase {
    @Test
    void generate() throws ContainerException, SQLException {
        dataSet.pushSpells();

        SpellBook book = new SpellBook(
            new DefaultListenerAggregate(),
            dataSet.createPlayer(1),
            Arrays.asList(
                new SpellBookEntry(
                    new PlayerSpell(1, 3, true, 5, 1),
                    container.get(SpellService.class).get(3)
                ),
                new SpellBookEntry(
                    new PlayerSpell(1, 6, true, 2, 3),
                    container.get(SpellService.class).get(6)
                )
            )
        );

        assertEquals(
            "SL3~5~b;6~2~d",
            new SpellList(book).toString()
        );
    }
}