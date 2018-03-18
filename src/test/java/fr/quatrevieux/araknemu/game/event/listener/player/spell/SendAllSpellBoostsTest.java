package fr.quatrevieux.araknemu.game.event.listener.player.spell;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.event.common.GameJoined;
import fr.quatrevieux.araknemu.game.spell.boost.SpellsBoosts;
import fr.quatrevieux.araknemu.network.game.out.spell.SpellBoost;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class SendAllSpellBoostsTest extends GameBaseCase {
    private SendAllSpellBoosts listener;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        listener = new SendAllSpellBoosts(gamePlayer(true));

        requestStack.clear();
    }

    @Test
    void withoutBoosts() {
        listener.on(new GameJoined());

        requestStack.assertEmpty();
    }

    @Test
    void withBoosts() throws SQLException, ContainerException {
        gamePlayer().spells().boosts().boost(3, SpellsBoosts.Modifier.DAMAGE, 15);
        gamePlayer().spells().boosts().boost(3, SpellsBoosts.Modifier.RANGE, 3);
        gamePlayer().spells().boosts().boost(6, SpellsBoosts.Modifier.AP_COST, 2);

        listener.on(new GameJoined());

        requestStack.assertAll(
            new SpellBoost(3, SpellsBoosts.Modifier.RANGE, 3),
            new SpellBoost(3, SpellsBoosts.Modifier.DAMAGE, 15),
            new SpellBoost(6, SpellsBoosts.Modifier.AP_COST, 2)
        );
    }
}
