package fr.quatrevieux.araknemu.game.fight;

import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.fight.builder.ChallengeBuilder;
import fr.quatrevieux.araknemu.game.fight.builder.FightHandler;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.fight.state.ActiveState;
import fr.quatrevieux.araknemu.game.fight.state.PlacementState;
import fr.quatrevieux.araknemu.game.fight.turn.FightTurn;
import fr.quatrevieux.araknemu.game.fight.turn.action.ActionResult;
import fr.quatrevieux.araknemu.game.fight.turn.action.move.Move;
import fr.quatrevieux.araknemu.game.world.map.Direction;
import fr.quatrevieux.araknemu.game.world.map.path.Decoder;
import fr.quatrevieux.araknemu.game.world.map.path.Path;
import fr.quatrevieux.araknemu.game.world.map.path.PathStep;
import fr.quatrevieux.araknemu.network.game.out.fight.BeginFight;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.network.game.out.fight.FighterPositions;
import fr.quatrevieux.araknemu.network.game.out.fight.FighterReadyState;
import fr.quatrevieux.araknemu.network.game.out.fight.JoinFight;
import fr.quatrevieux.araknemu.network.game.out.fight.action.ActionEffect;
import fr.quatrevieux.araknemu.network.game.out.fight.action.FightAction;
import fr.quatrevieux.araknemu.network.game.out.fight.action.StartFightAction;
import fr.quatrevieux.araknemu.network.game.out.fight.turn.FighterTurnOrder;
import fr.quatrevieux.araknemu.network.game.out.fight.turn.FinishTurn;
import fr.quatrevieux.araknemu.network.game.out.fight.turn.StartTurn;
import fr.quatrevieux.araknemu.network.game.out.fight.turn.TurnMiddle;
import fr.quatrevieux.araknemu.network.game.out.game.AddSprites;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

/**
 *
 */
public class FunctionalTest extends GameBaseCase {
    private FightService service;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMaps();

        service = container.get(FightService.class);
    }

    @Test
    void challengeFight() throws Exception {
        FightHandler<ChallengeBuilder> handler = service.handler(ChallengeBuilder.class);

        GamePlayer player = gamePlayer(true);
        GamePlayer other = makeOtherPlayer();
        ExplorationMap map = container.get(ExplorationMapService.class).load(10340);

        Fight fight = handler.start(
            builder -> {
                builder
                    .fighter(player)
                    .fighter(other)
                    .map(map)
                ;
            }
        );

        assertTrue(player.isFighting());
        assertTrue(other.isFighting());

        assertInstanceOf(PlacementState.class, fight.state());

        PlayerFighter fighter1, fighter2;

        if (player.fighter().team().number() == 0) {
            fighter1 = player.fighter();
            fighter2 = other.fighter();
        } else {
            fighter1 = other.fighter();
            fighter2 = player.fighter();
        }

        requestStack.assertOne(new JoinFight(fight));
        requestStack.assertOne(new AddSprites(Arrays.asList(fighter1.sprite(), fighter2.sprite())));

        if (fighter1.cell().id() != 185) {
            fight.state(PlacementState.class).changePlace(fighter1, fight.map().get(185));
            requestStack.assertLast(new FighterPositions(Arrays.asList(fighter1, fighter2)));
        }

        if (fighter2.cell().id() != 150) {
            fight.state(PlacementState.class).changePlace(fighter2, fight.map().get(150));
            requestStack.assertLast(new FighterPositions(Arrays.asList(fighter1, fighter2)));
        }

        assertEquals(185, fighter1.cell().id());
        assertEquals(150, fighter2.cell().id());

        fighter1.setReady(true);
        requestStack.assertLast(new FighterReadyState(fighter1));
        assertInstanceOf(PlacementState.class, fight.state());

        fighter1.setReady(false);
        requestStack.assertLast(new FighterReadyState(fighter1));

        fighter2.setReady(true);
        requestStack.assertLast(new FighterReadyState(fighter2));
        assertInstanceOf(PlacementState.class, fight.state());

        fighter1.setReady(true);
        assertInstanceOf(ActiveState.class, fight.state());
        requestStack.assertOne(new BeginFight());
        requestStack.assertOne(new FighterTurnOrder(fight.turnList()));

        Thread.sleep(210);
        requestStack.assertOne(new TurnMiddle(fight.fighters()));
        requestStack.assertOne(new StartTurn(fight.turnList().current().get()));
        requestStack.clear();

        assertSame(player.fighter(), fight.turnList().current().get().fighter());
        assertNotNull(player.fighter().turn());

        FightTurn currentTurn = player.fighter().turn();
        currentTurn.stop();

        requestStack.assertAll(
            new FinishTurn(currentTurn),
            new TurnMiddle(fight.fighters()),
            new StartTurn(currentTurn = fight.turnList().current().get())
        );

        assertSame(other.fighter(), currentTurn.fighter());

        if (currentTurn.fighter() != fighter1) {
            currentTurn.stop();
        }

        requestStack.clear();

        Move move = new Move(
            fighter1.turn(),
            fighter1,
            new Path<>(
                new Decoder<>(fight.map()),
                Arrays.asList(
                    new PathStep<>(fight.map().get(185), Direction.EAST),
                    new PathStep<>(fight.map().get(199), Direction.SOUTH_WEST),
                    new PathStep<>(fight.map().get(213), Direction.SOUTH_WEST),
                    new PathStep<>(fight.map().get(198), Direction.NORTH_WEST)
                )
            )
        );

        fighter1.turn().perform(move);

        requestStack.assertOne(
            new FightAction(
                new ActionResult() {
                    @Override
                    public int action() {
                        return 1;
                    }

                    @Override
                    public Fighter performer() {
                        return fighter1;
                    }

                    @Override
                    public Object[] arguments() {
                        return new Object[] {"ac5ddvfdg"};
                    }

                    @Override
                    public boolean success() {
                        return true;
                    }
                }
            )
        );

        fighter1.turn().terminate();
        requestStack.assertOne(ActionEffect.usedMovementPoints(fighter1, 3));
        assertEquals(198, fighter1.cell().id());
        assertEquals(0, fighter1.turn().points().movementPoints());
    }
}
