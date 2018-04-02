package fr.quatrevieux.araknemu.game.handler.fight;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.exception.FightException;
import fr.quatrevieux.araknemu.game.fight.state.PlacementState;
import fr.quatrevieux.araknemu.network.exception.ErrorPacket;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.game.in.fight.FighterChangePlace;
import fr.quatrevieux.araknemu.network.game.out.fight.ChangeFighterPlaceError;
import fr.quatrevieux.araknemu.network.in.PacketHandler;

/**
 * Change the fighter place before start the fight
 */
final public class ChangeFighterStartPlace implements PacketHandler<GameSession, FighterChangePlace> {
    @Override
    public void handle(GameSession session, FighterChangePlace packet) throws Exception {
        Fight fight = session.fighter().fight();

        try {
            fight
                .state(PlacementState.class)
                .changePlace(
                    session.fighter(),
                    fight.map().get(packet.cellId())
                )
            ;
        } catch (FightException e) {
            throw new ErrorPacket(new ChangeFighterPlaceError());
        }
    }

    @Override
    public Class<FighterChangePlace> packet() {
        return FighterChangePlace.class;
    }
}
