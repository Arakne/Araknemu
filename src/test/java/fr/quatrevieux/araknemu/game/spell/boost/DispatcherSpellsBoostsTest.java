package fr.quatrevieux.araknemu.game.spell.boost;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.event.DefaultListenerAggregate;
import fr.quatrevieux.araknemu.game.event.ListenerAggregate;
import fr.quatrevieux.araknemu.game.event.spell.SpellBoostChanged;
import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.game.spell.SpellService;
import fr.quatrevieux.araknemu.game.spell.boost.spell.BoostedSpell;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class DispatcherSpellsBoostsTest extends GameBaseCase {
    private ListenerAggregate dispatcher;
    private DispatcherSpellsBoosts boosts;

    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dispatcher = new DefaultListenerAggregate();
        boosts = new DispatcherSpellsBoosts(new SimpleSpellsBoosts(), dispatcher);
    }

    @Test
    void boost() {
        boosts.set(1, SpellsBoosts.Modifier.DAMAGE, 5);

        AtomicReference<SpellBoostChanged> ref = new AtomicReference<>();
        dispatcher.add(SpellBoostChanged.class, ref::set);

        assertEquals(10, boosts.boost(1, SpellsBoosts.Modifier.DAMAGE, 5));
        assertEquals(1, ref.get().spellId());
        assertEquals(SpellsBoosts.Modifier.DAMAGE, ref.get().modifier());
        assertEquals(10, ref.get().value());
    }

    @Test
    void set() {
        AtomicReference<SpellBoostChanged> ref = new AtomicReference<>();
        dispatcher.add(SpellBoostChanged.class, ref::set);

        assertEquals(5, boosts.boost(1, SpellsBoosts.Modifier.DAMAGE, 5));
        assertEquals(1, ref.get().spellId());
        assertEquals(SpellsBoosts.Modifier.DAMAGE, ref.get().modifier());
        assertEquals(5, ref.get().value());
    }

    @Test
    void unset() {
        boosts.set(1, SpellsBoosts.Modifier.DAMAGE, 5);

        AtomicReference<SpellBoostChanged> ref = new AtomicReference<>();
        dispatcher.add(SpellBoostChanged.class, ref::set);

        boosts.unset(1, SpellsBoosts.Modifier.DAMAGE);

        assertEquals(1, ref.get().spellId());
        assertEquals(SpellsBoosts.Modifier.DAMAGE, ref.get().modifier());
        assertEquals(-1, ref.get().value());
    }

    @Test
    void get() throws SQLException, ContainerException {
        dataSet.pushSpells();

        boosts.set(3, SpellsBoosts.Modifier.DAMAGE, 5);

        Spell spell = container.get(SpellService.class).get(3).level(1);

        Spell boosted = boosts.get(spell);

        assertInstanceOf(BoostedSpell.class, boosted);
        assertEquals(7, boosted.effects().get(0).min());
    }

    @Test
    void all() {
        boosts.set(3, SpellsBoosts.Modifier.DAMAGE, 5);
        boosts.set(6, SpellsBoosts.Modifier.AP_COST, 5);

        assertCount(2, boosts.all());
        assertContainsOnly(MapSpellModifiers.class, boosts.all());
    }
}
