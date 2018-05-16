package fr.quatrevieux.araknemu.game.spell.effect.area;

import fr.quatrevieux.araknemu.data.value.EffectArea;
import fr.quatrevieux.araknemu.data.world.repository.environment.MapTemplateRepository;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.fight.map.FightMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CrossAreaTest extends GameBaseCase {
    private FightMap map;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMaps();
        map = new FightMap(container.get(MapTemplateRepository.class).get(10340));
    }

    @Test
    void resolveSize0() {
        assertEquals(
            Collections.singleton(map.get(123)),
            new CrossArea(new EffectArea(EffectArea.Type.LINE, 0)).resolve(map.get(123), map.get(123))
        );
    }

    @Test
    void resolve() {
        assertCollectionEquals(
            new CrossArea(new EffectArea(EffectArea.Type.LINE, 3)).resolve(map.get(123), map.get(137)),

            map.get(123),

            map.get(109),
            map.get(95),
            map.get(81),

            map.get(138),
            map.get(153),
            map.get(168),

            map.get(137),
            map.get(151),
            map.get(165),

            map.get(108),
            map.get(93),
            map.get(78)
        );
    }
}