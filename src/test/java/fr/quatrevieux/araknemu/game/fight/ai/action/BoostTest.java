package fr.quatrevieux.araknemu.game.fight.ai.action;

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.ai.AI;
import fr.quatrevieux.araknemu.game.fight.ai.factory.ChainAiFactory;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.Simulator;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.fight.module.AiModule;
import fr.quatrevieux.araknemu.game.fight.module.CommonEffectsModule;
import fr.quatrevieux.araknemu.game.fight.state.PlacementState;
import fr.quatrevieux.araknemu.game.fight.turn.FightTurn;
import fr.quatrevieux.araknemu.game.fight.turn.action.Action;
import fr.quatrevieux.araknemu.game.fight.turn.action.cast.Cast;
import fr.quatrevieux.araknemu.game.spell.SpellService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class BoostTest extends FightBaseCase {
    private Fighter fighter;
    private Fight fight;

    private Boost action;
    private AI ai;

    private FightTurn turn;
    private PlayerFighter allie;
    private PlayerFighter enemy;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        fight = createFight();
        fight.register(new AiModule(fight, new ChainAiFactory()));
        fight.register(new CommonEffectsModule(fight));

        fighter = player.fighter();
        enemy = other.fighter();

        allie = new PlayerFighter(makeSimpleGamePlayer(10));
        fight.state(PlacementState.class).joinTeam(allie, fighter.team());

        fight.nextState();

        fight.turnList().start();

        action = new Boost(fight.attachment(Simulator.class));

        ai = new AI(fighter, new ActionGenerator[] { new DummyGenerator() });
        ai.start(turn = fight.turnList().current().get());
        action.initialize(ai);
    }

    @Test
    void success() throws SQLException {
        dataSet.pushFunctionalSpells();
        player.properties().spells().learn(container.get(SpellService.class).get(42));

        Optional<Action> result = action.generate(ai);

        assertTrue(result.isPresent());
        assertInstanceOf(Cast.class, result.get());

        Cast cast = (Cast) result.get();

        assertEquals(42, cast.spell().id());
        assertEquals(fighter.cell(), cast.target());
    }

    @Test
    void notEnoughAP() {
        turn.points().useActionPoints(5);

        Optional<Action> result = action.generate(ai);

        assertFalse(result.isPresent());
    }

    @Test
    void notAP() {
        turn.points().useActionPoints(6);

        Optional<Action> result = action.generate(ai);

        assertFalse(result.isPresent());
    }

    @Test
    void withAreaSpell() throws SQLException {
        dataSet.pushFunctionalSpells();
        player.properties().spells().learn(container.get(SpellService.class).get(584));

        Optional<Action> result = action.generate(ai);

        assertTrue(result.isPresent());
        assertInstanceOf(Cast.class, result.get());

        Cast cast = (Cast) result.get();

        assertEquals(584, cast.spell().id());
        assertEquals(63, cast.target().id());

        turn.perform(cast);
        turn.terminate();

        assertBetween(4, 5, fighter.characteristics().get(Characteristic.CRITICAL_BONUS));
        assertBetween(4, 5, allie.characteristics().get(Characteristic.CRITICAL_BONUS));
        assertEquals(0, enemy.characteristics().get(Characteristic.CRITICAL_BONUS));
    }
}
