package fr.quatrevieux.araknemu.game.fight.turn.action.factory;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.exception.FightException;
import fr.quatrevieux.araknemu.game.fight.turn.FightTurn;
import fr.quatrevieux.araknemu.game.fight.turn.action.ActionType;
import fr.quatrevieux.araknemu.game.fight.turn.action.move.Move;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertThrows;

class TurnActionsFactoryTest extends FightBaseCase {
    private Fight fight;
    private TurnActionsFactory factory;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        fight = createFight();
        factory = new TurnActionsFactory(new FightTurn(player.fighter(), fight, Duration.ofSeconds(30)));
    }

    @Test
    void createActionNotFound() {
        assertThrows(FightException.class, () -> factory.create(ActionType.NONE, new String[] {}));
    }

    @Test
    void createMove() throws Exception {
        player.fighter().move(fight.map().get(185));

        assertInstanceOf(Move.class, factory.create(ActionType.MOVE, new String[] {"ddvfdg"}));
    }
}
