package fr.quatrevieux.araknemu.game.fight.castable.validator;

import fr.quatrevieux.araknemu.data.value.Interval;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.castable.spell.LaunchedSpells;
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

/**
 *
 */
class SpellLaunchValidatorTest extends FightBaseCase {
    private Fight fight;
    private Fighter fighter;
    private FightTurn turn;
    private SpellLaunchValidator validator;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        fight = createFight();
        fighter = player.fighter();
        turn = new FightTurn(fighter, fight, Duration.ofSeconds(30));
        turn.start();

        validator = new SpellLaunchValidator();
    }

    @Test
    void validateWithoutLaunchedSpellHistory() {
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(spell.id()).thenReturn(5);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.launchPerTurn()).thenReturn(1);
        Mockito.when(constraints.launchPerTarget()).thenReturn(1);
        Mockito.when(constraints.launchDelay()).thenReturn(1);

        assertNull(validator.validate(turn, spell, fighter.cell()));
    }

    @Test
    void validateWithLaunchedSpellValid() {
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(spell.id()).thenReturn(5);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.launchPerTurn()).thenReturn(2);
        Mockito.when(constraints.launchPerTarget()).thenReturn(2);
        Mockito.when(constraints.launchDelay()).thenReturn(0);

        fighter.attach(new LaunchedSpells());
        fighter.attachment(LaunchedSpells.class).push(spell, fighter.cell());

        assertNull(validator.validate(turn, spell, fighter.cell()));
    }

    @Test
    void validateWithLaunchedSpellError() {
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(spell.id()).thenReturn(5);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.launchPerTurn()).thenReturn(1);
        Mockito.when(constraints.launchPerTarget()).thenReturn(1);
        Mockito.when(constraints.launchDelay()).thenReturn(0);

        fighter.attach(new LaunchedSpells());
        fighter.attachment(LaunchedSpells.class).push(spell, fighter.cell());

        assertEquals(
            Error.cantCast().toString(),
            validator.validate(turn, spell, fighter.cell()).toString()
        );
    }
}
