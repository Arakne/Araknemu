package fr.quatrevieux.araknemu.game.exploration.interaction.action.fight;

import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.ActionType;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.ExplorationActionRegistry;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightService;
import fr.quatrevieux.araknemu.game.fight.team.FightTeam;

/**
 * Register fight actions
 */
final public class FightActionsFactories implements ExplorationActionRegistry.SelfRegisterable {
    final private FightService fightService;

    public FightActionsFactories(FightService fightService) {
        this.fightService = fightService;
    }

    @Override
    public void register(ExplorationActionRegistry factory) {
        factory.register(ActionType.JOIN_FIGHT, this::join);
    }

    private JoinFight join(ExplorationPlayer player, ActionType action, String[] arguments) {
        Fight fight = fightService.getFromMap(player.map().id(), Integer.parseInt(arguments[0]));

        return new JoinFight(
            player,
            fight,
            findTeamById(fight, Integer.parseInt(arguments[1]))
        );
    }

    private FightTeam findTeamById(Fight fight, int id) {
        return fight.teams().stream().filter(team -> team.id() == id).findFirst().get();
    }
}
