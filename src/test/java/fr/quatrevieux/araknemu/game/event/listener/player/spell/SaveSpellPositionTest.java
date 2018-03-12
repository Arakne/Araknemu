package fr.quatrevieux.araknemu.game.event.listener.player.spell;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.data.living.entity.player.PlayerSpell;
import fr.quatrevieux.araknemu.data.living.repository.player.PlayerSpellRepository;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.event.spell.SpellMoved;
import fr.quatrevieux.araknemu.game.player.spell.SpellBookEntry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SaveSpellPositionTest extends GameBaseCase {
    private SaveSpellPosition listener;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.use(PlayerSpell.class);

        listener = new SaveSpellPosition(
            container.get(PlayerSpellRepository.class)
        );
    }

    @Test
    void onSpellMoved() throws ContainerException {
        PlayerSpell entity = new PlayerSpell(1, 2, true, 5, 12);

        listener.on(
            new SpellMoved(
                new SpellBookEntry(entity, null)
            )
        );

        assertEquals(12, dataSet.refresh(entity).position());
    }
}