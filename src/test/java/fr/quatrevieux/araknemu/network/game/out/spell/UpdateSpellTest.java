package fr.quatrevieux.araknemu.network.game.out.spell;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.living.entity.player.PlayerSpell;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.player.spell.SpellBookEntry;
import fr.quatrevieux.araknemu.game.spell.SpellService;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class UpdateSpellTest extends GameBaseCase {
    @Test
    void generate() throws ContainerException, SQLException {
        dataSet.pushSpells();

        assertEquals(
            "SUK202~3~d",
            new UpdateSpell(
                new SpellBookEntry(
                    new PlayerSpell(1, 202, false, 3, 3),
                    container.get(SpellService.class).get(202)
                )
            ).toString()
        );
    }
}