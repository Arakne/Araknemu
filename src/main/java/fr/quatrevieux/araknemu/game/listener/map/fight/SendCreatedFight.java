package fr.quatrevieux.araknemu.game.listener.map.fight;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.fight.FightService;
import fr.quatrevieux.araknemu.game.fight.event.FightCreated;
import fr.quatrevieux.araknemu.game.fight.team.FightTeam;
import fr.quatrevieux.araknemu.network.game.out.fight.exploration.AddTeamFighters;
import fr.quatrevieux.araknemu.network.game.out.fight.exploration.ShowFight;
import fr.quatrevieux.araknemu.network.game.out.fight.exploration.FightsCount;

/**
 * Send to the exploration map the new created fight
 */
final public class SendCreatedFight implements Listener<FightCreated> {
    final private ExplorationMapService mapService;
    final private FightService fightService;

    public SendCreatedFight(ExplorationMapService mapService, FightService fightService) {
        this.mapService = mapService;
        this.fightService = fightService;
    }

    @Override
    public void on(FightCreated event) {
        ExplorationMap map = mapService.load(event.fight().map().id());

        map.send(new ShowFight(event.fight()));

        for (FightTeam team : event.fight().teams()) {
            map.send(new AddTeamFighters(team));
        }

        map.send(new FightsCount(fightService.fightsByMap(map.id()).size()));
    }

    @Override
    public Class<FightCreated> event() {
        return FightCreated.class;
    }
}
