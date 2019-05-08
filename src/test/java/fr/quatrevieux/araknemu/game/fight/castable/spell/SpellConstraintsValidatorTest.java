package fr.quatrevieux.araknemu.game.fight.castable.spell;

import fr.quatrevieux.araknemu.data.value.Interval;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.castable.spell.SpellConstraintsValidator;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.turn.FightTurn;
import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.game.spell.SpellConstraints;
import fr.quatrevieux.araknemu.network.game.out.info.Error;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class SpellConstraintsValidatorTest extends FightBaseCase {
    private Fight fight;
    private Fighter fighter;
    private FightTurn turn;
    private SpellConstraintsValidator validator;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        fight = createFight();
        fighter = player.fighter();
        turn = new FightTurn(fighter, fight, Duration.ofSeconds(30));
        turn.start();

        fighter.move(fight.map().get(185));

        validator = new SpellConstraintsValidator();
    }

    @Test
    void success() {
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(spell.apCost()).thenReturn(4);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.range()).thenReturn(new Interval(1, 10));
        Mockito.when(constraints.requiredStates()).thenReturn(new int[0]);
        Mockito.when(constraints.forbiddenStates()).thenReturn(new int[0]);

        assertNull(validator.validate(turn, spell, fight.map().get(186)));
    }

    @Test
    void error() {
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(spell.apCost()).thenReturn(7);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.range()).thenReturn(new Interval(1, 10));
        Mockito.when(constraints.requiredStates()).thenReturn(new int[0]);
        Mockito.when(constraints.forbiddenStates()).thenReturn(new int[0]);

        assertEquals(
            Error.cantCastNotEnoughActionPoints(6, 7).toString(),
            validator.validate(turn, spell, fight.map().get(186)).toString()
        );
    }
}
