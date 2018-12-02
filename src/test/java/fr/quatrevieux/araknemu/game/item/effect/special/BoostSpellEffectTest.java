package fr.quatrevieux.araknemu.game.item.effect.special;

import fr.quatrevieux.araknemu.data.constant.Effect;
import fr.quatrevieux.araknemu.data.value.ItemTemplateEffectEntry;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.item.effect.SpecialEffect;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.game.spell.boost.SpellsBoosts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BoostSpellEffectTest extends GameBaseCase {
    private BoostSpellEffect handler;
    private GamePlayer player;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        handler = new BoostSpellEffect(SpellsBoosts.Modifier.DAMAGE);
        player = gamePlayer(true);
    }

    @Test
    void applyFirstTime() {
        handler.apply(
            new SpecialEffect(handler, Effect.SPELL_ADD_DAMAGE, new int[] {3, 0, 15}, ""),
            player
        );

        assertEquals(15, player.properties().spells().boosts().modifiers(3).damage());
    }

    @Test
    void applyWillAddToCurrentBoost() {
        player.properties().spells().boosts().set(3, SpellsBoosts.Modifier.DAMAGE, 5);

        handler.apply(
            new SpecialEffect(handler, Effect.SPELL_ADD_DAMAGE, new int[] {3, 0, 15}, ""),
            player
        );

        assertEquals(20, player.properties().spells().boosts().modifiers(3).damage());
    }

    @Test
    void relieve() {
        player.properties().spells().boosts().set(3, SpellsBoosts.Modifier.DAMAGE, 20);

        handler.relieve(
            new SpecialEffect(handler, Effect.SPELL_ADD_DAMAGE, new int[] {3, 0, 15}, ""),
            player
        );

        assertEquals(5, player.properties().spells().boosts().modifiers(3).damage());
    }

    @Test
    void create() {
        assertEquals(
            new SpecialEffect(handler, Effect.SPELL_ADD_DAMAGE, new int[] {3, 0, 5}, ""),
            handler.create(new ItemTemplateEffectEntry(Effect.SPELL_ADD_DAMAGE, 3, 0, 5, ""), true)
        );
    }
}
