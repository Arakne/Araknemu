package fr.quatrevieux.araknemu.game.handler.fight;

import fr.quatrevieux.araknemu.game.fight.FightService;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.game.in.fight.AskFightDetails;
import fr.quatrevieux.araknemu.network.game.out.fight.exploration.FightDetails;
import fr.quatrevieux.araknemu.network.in.PacketHandler;

/**
 * Show details on the fight
 */
final public class ShowFightDetails implements PacketHandler<GameSession, AskFightDetails> {
    final private FightService service;

    public ShowFightDetails(FightService service) {
        this.service = service;
    }

    @Override
    public void handle(GameSession session, AskFightDetails packet) {
        session.write(new FightDetails(service.getFromMap(session.exploration().map().id(), packet.fightId())));
    }

    @Override
    public Class<AskFightDetails> packet() {
        return AskFightDetails.class;
    }
}
