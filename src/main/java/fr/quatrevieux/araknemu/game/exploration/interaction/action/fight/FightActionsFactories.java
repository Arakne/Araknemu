package fr.quatrevieux.araknemu.game.exploration.interaction.action.fight;

import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.ActionType;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.ExplorationActionRegistry;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightService;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterFactory;
import fr.quatrevieux.araknemu.game.fight.team.FightTeam;

/**
 * Register fight actions
 */
final public class FightActionsFactories implements ExplorationActionRegistry.SelfRegisterable {
    final private FightService fightService;
    final private FighterFactory fighterFactory;

    public FightActionsFactories(FightService fightService, FighterFactory fighterFactory) {
        this.fightService = fightService;
        this.fighterFactory = fighterFactory;
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
            findTeamById(fight, Integer.parseInt(arguments[1])),
            fighterFactory
        );
    }

    private FightTeam findTeamById(Fight fight, int id) {
        return fight.teams().stream().filter(team -> team.id() == id).findFirst().get();
    }
}
