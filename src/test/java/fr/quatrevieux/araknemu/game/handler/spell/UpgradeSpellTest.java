package fr.quatrevieux.araknemu.game.handler.spell;

import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.player.spell.SpellBookEntry;
import fr.quatrevieux.araknemu.game.player.spell.SpellBookService;
import fr.quatrevieux.araknemu.network.exception.ErrorPacket;
import fr.quatrevieux.araknemu.network.game.in.spell.SpellUpgrade;
import fr.quatrevieux.araknemu.network.game.out.account.Stats;
import fr.quatrevieux.araknemu.network.game.out.spell.SpellUpgradeError;
import fr.quatrevieux.araknemu.network.game.out.spell.UpdateSpell;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.helpers.NOPLogger;

import static org.junit.jupiter.api.Assertions.*;

class UpgradeSpellTest extends GameBaseCase {
    private UpgradeSpell handler;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        handler = new UpgradeSpell();

        gamePlayer(true);

        requestStack.clear();
    }

    @Test
    void upgradeSpellNotFound() throws Exception {
        try {
            handler.handle(session, new SpellUpgrade(-1));

            fail("ErrorPacket should be thrown");
        } catch (ErrorPacket e) {
            assertInstanceOf(SpellUpgradeError.class, e.packet());
        }
    }

    @Test
    void upgradeNotEnoughPoints() throws Exception {
        gamePlayer().spells().setUpgradePoints(0);

        try {
            handler.handle(session, new SpellUpgrade(3));

            fail("ErrorPacket should be thrown");
        } catch (ErrorPacket e) {
            assertInstanceOf(SpellUpgradeError.class, e.packet());
        }
    }

    @Test
    void upgradeSuccess() throws Exception {
        gamePlayer().spells().setUpgradePoints(5);

        handler.handle(session, new SpellUpgrade(3));

        SpellBookEntry entry = gamePlayer().spells().entry(3);
        assertEquals(4, gamePlayer().spells().upgradePoints());
        assertEquals(2, entry.spell().level());

        requestStack.assertAll(
            new UpdateSpell(entry),
            new Stats(gamePlayer())
        );

        assertEquals(4, dataSet.refresh(new Player(1)).spellPoints());
        assertEquals(2, dataSet.refresh(entry.entity()).level());
    }
}
