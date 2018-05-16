package fr.quatrevieux.araknemu.game.fight.castable.effect;

import fr.quatrevieux.araknemu.data.value.EffectArea;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.spell.effect.area.CircleArea;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class TargetResolverTest extends FightBaseCase {
    private Fight fight;

    private Fighter caster;
    private Fighter target;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        fight = createFight();

        caster = player.fighter();
        target = other.fighter();

        caster.move(fight.map().get(185));
        target.move(fight.map().get(150));
    }

    @Test
    void withoutArea() {
        TargetResolver resolver = new TargetResolver(caster, target.cell());

        AtomicReference<Fighter> ref = new AtomicReference<>();
        AtomicInteger inc = new AtomicInteger();

        resolver.fighters(fighter -> {inc.incrementAndGet(); ref.set(fighter);});

        assertSame(target, ref.get());
        assertEquals(1, inc.get());
    }

    @Test
    void withoutAreaOnEmptyCell() {
        TargetResolver resolver = new TargetResolver(caster, fight.map().get(123));

        AtomicInteger inc = new AtomicInteger();

        resolver.fighters(fighter -> inc.incrementAndGet());

        assertEquals(0, inc.get());
    }

    @Test
    void withAreaMultipleFighters() {
        TargetResolver resolver = new TargetResolver(caster, fight.map().get(123));

        resolver.area(new CircleArea(new EffectArea(EffectArea.Type.CIRCLE, 15)));

        List<Fighter> fighters = new ArrayList<>();

        resolver.fighters(fighters::add);

        assertCollectionEquals(fighters, caster, target);
    }

    @Test
    void mapWithArea() {
        TargetResolver resolver = new TargetResolver(caster, fight.map().get(123));

        resolver.area(new CircleArea(new EffectArea(EffectArea.Type.CIRCLE, 15)));

        List<Fighter> fighters = new ArrayList<>();

        Stream<Boolean> stream = resolver.map(fighters::add);

        stream.forEach(Assertions::assertTrue);

        assertCollectionEquals(fighters, caster, target);
    }
}
