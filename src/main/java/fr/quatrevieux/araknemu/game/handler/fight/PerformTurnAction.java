package fr.quatrevieux.araknemu.game.handler.fight;

import fr.quatrevieux.araknemu.game.fight.turn.FightTurn;
import fr.quatrevieux.araknemu.game.fight.turn.action.ActionType;
import fr.quatrevieux.araknemu.network.exception.ErrorPacket;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.game.in.game.action.GameActionRequest;
import fr.quatrevieux.araknemu.network.game.out.fight.action.NoneAction;
import fr.quatrevieux.araknemu.network.in.PacketHandler;

/**
 * Perform a game action into the fight
 */
final public class PerformTurnAction implements PacketHandler<GameSession, GameActionRequest> {
    @Override
    public void handle(GameSession session, GameActionRequest packet) throws Exception {
        try {
            FightTurn turn = session.fighter().turn();

            turn.perform(turn.actions().create(ActionType.byId(packet.type()), packet.arguments()));
        } catch (Exception e) {
            throw new ErrorPacket(new NoneAction(), e);
        }
    }

    @Override
    public Class<GameActionRequest> packet() {
        return GameActionRequest.class;
    }
}
