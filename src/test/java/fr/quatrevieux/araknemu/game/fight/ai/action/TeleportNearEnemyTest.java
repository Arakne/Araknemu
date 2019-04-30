package fr.quatrevieux.araknemu.game.fight.ai.action;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.ai.AI;
import fr.quatrevieux.araknemu.game.fight.ai.factory.ChainAiFactory;
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

class TeleportNearEnemyTest extends FightBaseCase {
    private Fighter fighter;
    private Fight fight;

    private TeleportNearEnemy action;
    private AI ai;

    private FightTurn turn;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        fight = createFight();
        fight.register(new AiModule(fight, new ChainAiFactory()));
        fight.register(new CommonEffectsModule(fight));

        fighter = player.fighter();

        fight.nextState();

        fight.turnList().start();

        action = new TeleportNearEnemy();

        ai = new AI(fighter, new ActionGenerator[] { new DummyGenerator() });
        ai.start(turn = fight.turnList().current().get());
    }

    @Test
    void success() throws SQLException {
        dataSet.pushFunctionalSpells();
        player.properties().spells().learn(container.get(SpellService.class).get(142));
        player.properties().spells().entry(142).entity().setLevel(5);

        action.initialize(ai);

        Optional<Action> result = action.generate(ai);

        assertTrue(result.isPresent());
        assertInstanceOf(Cast.class, result.get());

        Cast cast = (Cast) result.get();

        assertEquals(142, cast.spell().id());
        assertEquals(110, cast.target().id());
    }

    @Test
    void alreadyNearEnemy() throws SQLException {
        dataSet.pushFunctionalSpells();
        player.properties().spells().learn(container.get(SpellService.class).get(142));
        player.properties().spells().entry(142).entity().setLevel(5);

        action.initialize(ai);

        fighter.move(fight.map().get(110));

        Optional<Action> result = action.generate(ai);

        assertFalse(result.isPresent());
    }

    @Test
    void notEnoughAP() throws SQLException {
        dataSet.pushFunctionalSpells();
        player.properties().spells().learn(container.get(SpellService.class).get(142));
        player.properties().spells().entry(142).entity().setLevel(5);

        action.initialize(ai);

        turn.points().useActionPoints(5);

        Optional<Action> result = action.generate(ai);

        assertFalse(result.isPresent());
    }

    @Test
    void noAP() throws SQLException {
        dataSet.pushFunctionalSpells();
        player.properties().spells().learn(container.get(SpellService.class).get(142));
        player.properties().spells().entry(142).entity().setLevel(5);

        action.initialize(ai);

        turn.points().useActionPoints(6);

        Optional<Action> result = action.generate(ai);

        assertFalse(result.isPresent());
    }

    @Test
    void noTeleportSpells() {
        action.initialize(ai);

        Optional<Action> result = action.generate(ai);

        assertFalse(result.isPresent());
    }
}
