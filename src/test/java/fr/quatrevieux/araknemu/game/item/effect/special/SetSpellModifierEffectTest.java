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

class SetSpellModifierEffectTest extends GameBaseCase {
    private SetSpellModifierEffect handler;
    private GamePlayer player;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        handler = new SetSpellModifierEffect(SpellsBoosts.Modifier.SET_DELAY);
        player = gamePlayer(true);
    }

    @Test
    void applyFirstTime() {
        handler.apply(
            new SpecialEffect(handler, Effect.SPELL_SET_DELAY, new int [] {3, 0, 2}, ""),
            player
        );

        assertEquals(2, player.spells().boosts().modifiers(3).fixedDelay());
    }

    @Test
    void applyAlreadySetBetterBoost() {
        player.spells().boosts().set(3, SpellsBoosts.Modifier.SET_DELAY, 1);

        handler.apply(
            new SpecialEffect(handler, Effect.SPELL_SET_DELAY, new int [] {3, 0, 2}, ""),
            player
        );

        assertEquals(1, player.spells().boosts().modifiers(3).fixedDelay());
    }

    @Test
    void applyAlreadyWorstBoost() {
        player.spells().boosts().set(3, SpellsBoosts.Modifier.SET_DELAY, 4);

        handler.apply(
            new SpecialEffect(handler, Effect.SPELL_SET_DELAY, new int [] {3, 0, 2}, ""),
            player
        );

        assertEquals(2, player.spells().boosts().modifiers(3).fixedDelay());
    }

    @Test
    void relieveNotCurrentBoost() {
        player.spells().boosts().set(3, SpellsBoosts.Modifier.SET_DELAY, 4);

        handler.relieve(
            new SpecialEffect(handler, Effect.SPELL_SET_DELAY, new int [] {3, 0, 2}, ""),
            player
        );

        assertEquals(4, player.spells().boosts().modifiers(3).fixedDelay());
    }

    @Test
    void relieveSuccess() {
        player.spells().boosts().set(3, SpellsBoosts.Modifier.SET_DELAY, 2);

        handler.relieve(
            new SpecialEffect(handler, Effect.SPELL_SET_DELAY, new int [] {3, 0, 2}, ""),
            player
        );

        assertFalse(player.spells().boosts().modifiers(3).has(SpellsBoosts.Modifier.SET_DELAY));
    }

    @Test
    void create() {
        assertEquals(
            new SpecialEffect(handler, Effect.SPELL_SET_DELAY, new int[] {3, 0, 5}, ""),
            handler.create(new ItemTemplateEffectEntry(Effect.SPELL_SET_DELAY, 3, 0, 5, ""), true)
        );
    }
}