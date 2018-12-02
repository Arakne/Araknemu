package fr.quatrevieux.araknemu.game.listener.player.spell;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.living.entity.player.PlayerSpell;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.player.spell.event.SpellUpgraded;
import fr.quatrevieux.araknemu.game.player.spell.SpellBookEntry;
import fr.quatrevieux.araknemu.game.spell.SpellService;
import fr.quatrevieux.araknemu.network.game.out.account.Stats;
import fr.quatrevieux.araknemu.network.game.out.spell.UpdateSpell;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

class SendUpgradedSpellTest extends FightBaseCase {
    private SendUpgradedSpell listener;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushSpells();

        listener = new SendUpgradedSpell(
            gamePlayer(true)
        );

        requestStack.clear();
    }

    @Test
    void onSpellUpgraded() throws ContainerException, SQLException {
        SpellBookEntry entry = new SpellBookEntry(
            new PlayerSpell(1, 202, false, 3, 3),
            container.get(SpellService.class).get(202)
        );

        listener.on(new SpellUpgraded(entry));

        requestStack.assertAll(
            new UpdateSpell(entry),
            new Stats(gamePlayer().properties())
        );
    }

    @Test
    void onSpellUpgradedDuringFight() throws Exception {
        createFight();
        PlayerFighter fighter = player.fighter();
        fighter.init();
        fighter.life().alter(fighter, -100);

        SpellBookEntry entry = new SpellBookEntry(
            new PlayerSpell(1, 202, false, 3, 3),
            container.get(SpellService.class).get(202)
        );

        requestStack.clear();

        listener.on(new SpellUpgraded(entry));

        requestStack.assertAll(
            new UpdateSpell(entry),
            new Stats(fighter.properties())
        );
    }
}