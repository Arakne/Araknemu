package fr.quatrevieux.araknemu.game.spell.boost.spell;

import fr.quatrevieux.araknemu.data.value.EffectArea;
import fr.quatrevieux.araknemu.data.value.SpellTemplateEffect;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.spell.boost.MapSpellModifiers;
import fr.quatrevieux.araknemu.game.spell.boost.SpellModifiers;
import fr.quatrevieux.araknemu.game.spell.boost.SpellsBoosts;
import fr.quatrevieux.araknemu.game.spell.boost.spell.BoostedSpellEffect;
import fr.quatrevieux.araknemu.game.spell.effect.SpellTemplateEffectAdapter;
import fr.quatrevieux.araknemu.game.spell.effect.area.CellArea;
import fr.quatrevieux.araknemu.game.spell.effect.area.CircleArea;
import fr.quatrevieux.araknemu.game.spell.effect.target.EffectTarget;
import fr.quatrevieux.araknemu.game.spell.effect.target.SpellEffectTarget;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class BoostedSpellEffectTest extends GameBaseCase {
    private Map<SpellsBoosts.Modifier, Integer> map;
    private SpellModifiers modifiers;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        map = new HashMap<>();
        modifiers = new MapSpellModifiers(1, map);
    }

    @Test
    void simpleEffect() {
        BoostedSpellEffect effect = new BoostedSpellEffect(
            new SpellTemplateEffectAdapter(
                new SpellTemplateEffect(123, 5, 10, 0, 5, 25, "a"),
                new CircleArea(new EffectArea(EffectArea.Type.CIRCLE, 1)),
                SpellEffectTarget.DEFAULT
            ),
            modifiers
        );

        map.put(SpellsBoosts.Modifier.DAMAGE, 5);
        map.put(SpellsBoosts.Modifier.HEAL, 5);

        assertEquals(123, effect.effect());
        assertEquals(5, effect.min());
        assertEquals(10, effect.max());
        assertEquals(0, effect.boost());
        assertEquals(0, effect.special());
        assertEquals(5, effect.duration());
        assertEquals(25, effect.probability());
        assertEquals("a", effect.text());
        assertEquals(EffectArea.Type.CIRCLE, effect.area().type());
        assertEquals(1, effect.area().size());
        assertEquals(SpellEffectTarget.DEFAULT, effect.target());
    }

    @Test
    void damageEffect() {
        BoostedSpellEffect effect = new BoostedSpellEffect(
            new SpellTemplateEffectAdapter(
                new SpellTemplateEffect(95, 5, 10, 0, 5, 25, "a"),
                new CellArea(),
                SpellEffectTarget.DEFAULT
            ),
            modifiers
        );

        map.put(SpellsBoosts.Modifier.DAMAGE, 5);
        map.put(SpellsBoosts.Modifier.HEAL, 10);

        assertEquals(95, effect.effect());
        assertEquals(5, effect.min());
        assertEquals(10, effect.max());
        assertEquals(5, effect.boost());
    }

    @Test
    void damageEffectFixed() {
        BoostedSpellEffect effect = new BoostedSpellEffect(
            new SpellTemplateEffectAdapter(
                new SpellTemplateEffect(95, 5, 0, 0, 5, 25, "a"),
                new CellArea(),
                SpellEffectTarget.DEFAULT
            ),
            modifiers
        );

        map.put(SpellsBoosts.Modifier.DAMAGE, 5);
        map.put(SpellsBoosts.Modifier.HEAL, 10);

        assertEquals(95, effect.effect());
        assertEquals(5, effect.min());
        assertEquals(0, effect.max());
        assertEquals(5, effect.boost());
    }

    @Test
    void healEffect() {
        BoostedSpellEffect effect = new BoostedSpellEffect(
            new SpellTemplateEffectAdapter(
                new SpellTemplateEffect(81, 5, 10, 0, 5, 25, "a"),
                new CellArea(),
                SpellEffectTarget.DEFAULT
            ),
            modifiers
        );

        map.put(SpellsBoosts.Modifier.DAMAGE, 5);
        map.put(SpellsBoosts.Modifier.HEAL, 10);

        assertEquals(81, effect.effect());
        assertEquals(5, effect.min());
        assertEquals(10, effect.max());
        assertEquals(10, effect.boost());
    }

    @Test
    void healEffectFixed() {
        BoostedSpellEffect effect = new BoostedSpellEffect(
            new SpellTemplateEffectAdapter(
                new SpellTemplateEffect(108, 5, 0, 0, 5, 25, "a"),
                new CellArea(),
                SpellEffectTarget.DEFAULT
            ),
            modifiers
        );

        map.put(SpellsBoosts.Modifier.DAMAGE, 5);
        map.put(SpellsBoosts.Modifier.HEAL, 10);

        assertEquals(108, effect.effect());
        assertEquals(5, effect.min());
        assertEquals(0, effect.max());
        assertEquals(10, effect.boost());
    }
}
