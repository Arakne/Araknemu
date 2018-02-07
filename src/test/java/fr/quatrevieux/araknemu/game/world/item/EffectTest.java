package fr.quatrevieux.araknemu.game.world.item;

import fr.quatrevieux.araknemu.data.constant.Effect;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EffectTest {
    @Test
    void byId() {
        assertSame(Effect.ADD_DODGE_AP, Effect.byId(160));
        assertSame(Effect.ADD_CHANCE, Effect.byId(123));
    }
}