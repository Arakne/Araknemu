package fr.quatrevieux.araknemu.game.event.listener.player.spell;

import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.event.spell.SpellBoostChanged;
import fr.quatrevieux.araknemu.game.spell.boost.SpellsBoosts;
import fr.quatrevieux.araknemu.network.game.out.spell.SpellBoost;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SendSpellBoostTest extends GameBaseCase {
    private SendSpellBoost listener;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        listener = new SendSpellBoost(gamePlayer(true));

        requestStack.clear();
    }

    @Test
    void onSpellBoostChanged() {
        listener.on(
            new SpellBoostChanged(5, SpellsBoosts.Modifier.DAMAGE, 15)
        );

        requestStack.assertLast(new SpellBoost(5, SpellsBoosts.Modifier.DAMAGE, 15));
    }
}