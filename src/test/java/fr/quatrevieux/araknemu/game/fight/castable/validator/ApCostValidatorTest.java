package fr.quatrevieux.araknemu.game.fight.castable.validator;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.turn.FightTurn;
import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.network.game.out.info.Error;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ApCostValidatorTest extends FightBaseCase {
    private Fight fight;
    private Fighter fighter;
    private FightTurn turn;
    private ApCostValidator validator;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        fight = createFight();
        fighter = player.fighter();
        turn = new FightTurn(fighter, fight, Duration.ofSeconds(30));
        turn.start();

        validator = new ApCostValidator();
    }

    @Test
    void notEnoughAp() {
        Spell spell = Mockito.mock(Spell.class);

        Mockito.when(spell.apCost()).thenReturn(10);

        assertEquals(
            Error.cantCastNotEnoughActionPoints(6, 10).toString(),
            validator.validate(turn, spell, fight.map().get(123)).toString()
        );
    }

    @Test
    void success() {
        Spell spell = Mockito.mock(Spell.class);

        Mockito.when(spell.apCost()).thenReturn(5);

        assertNull(validator.validate(turn, spell, fight.map().get(123)));
    }
}
