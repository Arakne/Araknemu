package fr.quatrevieux.araknemu.game.handler.game;

import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightService;
import fr.quatrevieux.araknemu.game.fight.state.PlacementState;
import fr.quatrevieux.araknemu.game.fight.team.FightTeam;
import fr.quatrevieux.araknemu.network.exception.CloseImmediately;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.game.in.game.AskExtraInfo;
import fr.quatrevieux.araknemu.network.game.out.fight.exploration.AddTeamFighters;
import fr.quatrevieux.araknemu.network.game.out.fight.exploration.ShowFight;
import fr.quatrevieux.araknemu.network.game.out.fight.exploration.FightsCount;
import fr.quatrevieux.araknemu.network.game.out.game.AddSprites;
import fr.quatrevieux.araknemu.network.game.out.game.MapReady;
import fr.quatrevieux.araknemu.network.in.PacketHandler;

import java.util.Collection;

/**
 * Load map extra info for join the map
 */
final public class LoadExtraInfo implements PacketHandler<GameSession, AskExtraInfo> {
    final private FightService fightService;

    public LoadExtraInfo(FightService fightService) {
        this.fightService = fightService;
    }

    @Override
    public void handle(GameSession session, AskExtraInfo packet) throws Exception {
        ExplorationMap map = session.exploration().map();

        if (map == null) {
            throw new CloseImmediately("A map should be loaded before get extra info");
        }

        session.write(new AddSprites(map.sprites()));

        Collection<Fight> fights = fightService.fightsByMap(map.id());

        for (Fight fight : fights) {
            if (!(fight.state() instanceof PlacementState)) {
                continue;
            }

            session.write(new ShowFight(fight));

            for (FightTeam team : fight.teams()) {
                map.send(new AddTeamFighters(team));
            }
        }

        map.send(new FightsCount(fights.size()));

        session.write(new MapReady());
    }

    @Override
    public Class<AskExtraInfo> packet() {
        return AskExtraInfo.class;
    }
}
