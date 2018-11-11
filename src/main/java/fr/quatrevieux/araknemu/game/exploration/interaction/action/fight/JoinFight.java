package fr.quatrevieux.araknemu.game.exploration.interaction.action.fight;

import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.Action;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.ActionQueue;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.ActionType;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.JoinFightError;
import fr.quatrevieux.araknemu.game.fight.exception.JoinFightException;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.fight.state.PlacementState;
import fr.quatrevieux.araknemu.game.fight.team.FightTeam;
import fr.quatrevieux.araknemu.network.game.out.game.action.GameActionResponse;

/**
 * Try to join a fight
 */
final public class JoinFight implements Action {
    final private ExplorationPlayer player;
    final private Fight fight;
    final private FightTeam team;

    public JoinFight(ExplorationPlayer player, Fight fight, FightTeam team) {
        this.player = player;
        this.fight = fight;
        this.team = team;
    }

    @Override
    public void start(ActionQueue queue) {
        if (player.interactions().busy()) {
            error(JoinFightError.CANT_YOU_R_BUSY);
            return;
        }

        if (!(fight.state() instanceof PlacementState)) {
            error(JoinFightError.CANT_DO_TOO_LATE);
            return;
        }

        if (player.map().id() != fight.map().id()) {
            error(JoinFightError.CANT_BECAUSE_MAP);
            return;
        }

        fight.execute(() -> {
            try {
                fight.state(PlacementState.class).joinTeam(new PlayerFighter(player.player()), team);
            } catch (JoinFightException e) {
                error(e.error());
            }
        });
    }

    @Override
    public ExplorationPlayer performer() {
        return player;
    }

    @Override
    public ActionType type() {
        return ActionType.JOIN_FIGHT;
    }

    @Override
    public Object[] arguments() {
        return new Object[0];
    }

    private void error(JoinFightError error) {
        player.send(
            new GameActionResponse(
                "",
                ActionType.JOIN_FIGHT,
                Integer.toString(player.id()),
                new Object[] {Character.toString(error.error())}
            )
        );
    }
}
