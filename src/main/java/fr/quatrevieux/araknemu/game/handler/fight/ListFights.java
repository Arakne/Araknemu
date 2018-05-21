package fr.quatrevieux.araknemu.game.handler.fight;

import fr.quatrevieux.araknemu.game.fight.FightService;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.game.in.fight.ListFightsRequest;
import fr.quatrevieux.araknemu.network.game.out.fight.exploration.FightList;
import fr.quatrevieux.araknemu.network.in.PacketHandler;

/**
 * List fights on the current map
 */
final public class ListFights implements PacketHandler<GameSession, ListFightsRequest> {
    final private FightService service;

    public ListFights(FightService service) {
        this.service = service;
    }

    @Override
    public void handle(GameSession session, ListFightsRequest packet) {
        session.write(new FightList(service.fightsByMap(session.exploration().map().id())));
    }

    @Override
    public Class<ListFightsRequest> packet() {
        return ListFightsRequest.class;
    }
}
