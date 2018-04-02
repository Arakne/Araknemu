package fr.quatrevieux.araknemu.game.listener.player.spell;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.living.entity.player.PlayerSpell;
import fr.quatrevieux.araknemu.data.living.repository.player.PlayerSpellRepository;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.player.spell.event.SpellLearned;
import fr.quatrevieux.araknemu.game.player.spell.SpellBookEntry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SaveLearnedSpellTest extends GameBaseCase {
    private SaveLearnedSpell listener;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.use(PlayerSpell.class);

        listener = new SaveLearnedSpell(
            container.get(PlayerSpellRepository.class)
        );
    }

    @Test
    void onSpellMoved() throws ContainerException {
        PlayerSpell entity = new PlayerSpell(1, 2, false);

        listener.on(
            new SpellLearned(
                new SpellBookEntry(entity, null)
            )
        );

        assertEquals(1, dataSet.refresh(entity).playerId());
        assertEquals(2, dataSet.refresh(entity).spellId());
        assertEquals(1, dataSet.refresh(entity).level());
        assertEquals(63, dataSet.refresh(entity).position());
    }
}