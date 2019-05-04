package fr.quatrevieux.araknemu.game.fight.ending;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.operation.FighterOperation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class EndFightResultsTest extends FightBaseCase {
    private Fight fight;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        fight = createFight();
    }

    @Test
    void applyToLoosers() {
        Fighter winner = Mockito.mock(Fighter.class);
        Fighter looser = Mockito.mock(Fighter.class);

        FighterOperation operation = Mockito.mock(FighterOperation.class);

        EndFightResults results = new EndFightResults(fight, Collections.singletonList(winner), Collections.singletonList(looser));

        assertSame(operation, results.applyToLoosers(operation));
        Mockito.verify(winner, Mockito.never()).apply(operation);
        Mockito.verify(looser).apply(operation);
    }

    @Test
    void applyToWinners() {
        Fighter winner = Mockito.mock(Fighter.class);
        Fighter looser = Mockito.mock(Fighter.class);

        FighterOperation operation = Mockito.mock(FighterOperation.class);

        EndFightResults results = new EndFightResults(fight, Collections.singletonList(winner), Collections.singletonList(looser));

        assertSame(operation, results.applyToWinners(operation));
        Mockito.verify(winner).apply(operation);
        Mockito.verify(looser, Mockito.never()).apply(operation);
    }
}