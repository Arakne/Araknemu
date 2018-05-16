package fr.quatrevieux.araknemu.game.spell.effect;

import fr.quatrevieux.araknemu.data.value.EffectArea;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.spell.effect.area.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SpellEffectServiceTest extends GameBaseCase {
    private SpellEffectService service;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        service = new SpellEffectService();
    }

    @Test
    void area() {
        assertSame(CellArea.INSTANCE, service.area(new EffectArea(EffectArea.Type.CELL, 0)));
        assertInstanceOf(CircleArea.class, service.area(new EffectArea(EffectArea.Type.CIRCLE, 3)));
        assertInstanceOf(LineArea.class, service.area(new EffectArea(EffectArea.Type.LINE, 3)));
        assertInstanceOf(CrossArea.class, service.area(new EffectArea(EffectArea.Type.CROSS, 3)));
        assertInstanceOf(PerpendicularLineArea.class, service.area(new EffectArea(EffectArea.Type.PERPENDICULAR_LINE, 3)));
        assertInstanceOf(RingArea.class, service.area(new EffectArea(EffectArea.Type.RING, 3)));
    }
}