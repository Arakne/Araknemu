package fr.quatrevieux.araknemu.game.fight.ai.util;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.ai.AI;
import fr.quatrevieux.araknemu.game.fight.ai.action.ActionGenerator;
import fr.quatrevieux.araknemu.game.fight.ai.action.DummyGenerator;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.fight.turn.action.cast.Cast;
import fr.quatrevieux.araknemu.game.spell.Spell;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SpellCasterTest extends FightBaseCase {
    private Fight fight;
    private SpellCaster caster;
    private PlayerFighter fighter;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        fight = createFight();
        fighter = player.fighter();

        AI ai = new AI(fighter, new ActionGenerator[] {new DummyGenerator()});
        fight.nextState();
        fight.turnList().start();
        ai.start(fight.turnList().current().get());

        caster = new SpellCaster(ai);
    }

    @Test
    void validate() {
        Spell spell = fighter.spells().get(3);

        assertFalse(caster.validate(spell, fight.map().get(5)));
        assertTrue(caster.validate(spell, fight.map().get(210)));
    }

    @Test
    void create() {
        Spell spell = fighter.spells().get(3);

        Cast cast = caster.create(spell, fight.map().get(210));

        assertSame(spell, cast.spell());
        assertSame(fight.map().get(210), cast.target());
        assertSame(fighter, cast.caster());
    }
}
