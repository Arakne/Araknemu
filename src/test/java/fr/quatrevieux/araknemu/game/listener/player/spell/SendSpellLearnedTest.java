package fr.quatrevieux.araknemu.game.listener.player.spell;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.living.entity.player.PlayerSpell;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.player.spell.event.SpellLearned;
import fr.quatrevieux.araknemu.game.player.spell.SpellBookEntry;
import fr.quatrevieux.araknemu.game.spell.SpellService;
import fr.quatrevieux.araknemu.network.game.out.info.Information;
import fr.quatrevieux.araknemu.network.game.out.spell.SpellList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

class SendSpellLearnedTest extends GameBaseCase {
    private SendLearnedSpell listener;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushSpells();
        listener = new SendLearnedSpell(
            gamePlayer(true)
        );

        requestStack.clear();
    }

    @Test
    void onJoinGame() throws SQLException, ContainerException {
        PlayerSpell entity = new PlayerSpell(1, 2, false);

        listener.on(
            new SpellLearned(
                new SpellBookEntry(entity, container.get(SpellService.class).get(2))
            )
        );

        requestStack.assertAll(
            new SpellList(gamePlayer().properties().spells()),
            Information.spellLearn(2)
        );
    }
}