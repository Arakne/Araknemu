package fr.quatrevieux.araknemu.game.item.effect.special;

import fr.quatrevieux.araknemu.data.constant.Effect;
import fr.quatrevieux.araknemu.data.value.ItemTemplateEffectEntry;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.game.player.characteristic.SpecialEffects;
import fr.quatrevieux.araknemu.game.item.effect.SpecialEffect;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AddSpecialEffectTest extends GameBaseCase {
    private GamePlayer player;
    private AddSpecialEffect handler;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        player = gamePlayer(true);
        handler = new AddSpecialEffect(SpecialEffects.Type.DISCERNMENT);
    }

    @Test
    void apply() {
        handler.apply(
            new SpecialEffect(handler, Effect.ADD_DISCERNMENT, new int[] {15, 0, 0}, ""),
            player
        );

        assertEquals(15, player.properties().characteristics().specials().get(SpecialEffects.Type.DISCERNMENT));
    }

    @Test
    void relieve() {
        player.properties().characteristics().specials().add(SpecialEffects.Type.DISCERNMENT, 15);

        handler.relieve(
            new SpecialEffect(handler, Effect.ADD_DISCERNMENT, new int[] {15, 0, 0}, ""),
            player
        );

        assertEquals(0, player.properties().characteristics().specials().get(SpecialEffects.Type.DISCERNMENT));
    }

    @Test
    void createMaximizeWithRange() {
        SpecialEffect effect = handler.create(
            new ItemTemplateEffectEntry(Effect.ADD_DISCERNMENT, 10, 25, 0, ""),
            true
        );

        assertEquals(25, effect.arguments()[0]);
        assertEquals(0, effect.arguments()[1]);
        assertEquals(0, effect.arguments()[2]);
        assertEquals("0d0+25", effect.text());
    }

    @Test
    void createMaximizeWithFixed() {
        SpecialEffect effect = handler.create(
            new ItemTemplateEffectEntry(Effect.ADD_DISCERNMENT, 10, 0, 0, ""),
            true
        );

        assertEquals(10, effect.arguments()[0]);
        assertEquals(0, effect.arguments()[1]);
        assertEquals(0, effect.arguments()[2]);
        assertEquals("0d0+10", effect.text());
    }

    @Test
    void createRandomWithFixed() {
        SpecialEffect effect = handler.create(
            new ItemTemplateEffectEntry(Effect.ADD_DISCERNMENT, 10, 0, 0, ""),
            false
        );

        assertEquals(10, effect.arguments()[0]);
        assertEquals(0, effect.arguments()[1]);
        assertEquals(0, effect.arguments()[2]);
        assertEquals("0d0+10", effect.text());
    }

    @Test
    void createRandom() {
        SpecialEffect effect = handler.create(
            new ItemTemplateEffectEntry(Effect.ADD_DISCERNMENT, 10, 25, 0, ""),
            false
        );

        assertBetween(10, 25, effect.arguments()[0]);
    }
}
