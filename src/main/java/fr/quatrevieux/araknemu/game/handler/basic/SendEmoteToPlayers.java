package fr.quatrevieux.araknemu.game.handler.basic;

import java.util.List;
import java.util.stream.Collectors;

import fr.quatrevieux.araknemu.core.network.parser.PacketHandler;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.creature.ExplorationCreature;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.game.in.basic.PlayerEmote;
import fr.quatrevieux.araknemu.network.game.out.basic.PlayerEmoteToMap;

/**
 * Send the player emote to other players
 */
final public class SendEmoteToPlayers implements PacketHandler<GameSession, PlayerEmote>{
    @Override
    public void handle(GameSession session, PlayerEmote packet) throws Exception {
        List<ExplorationCreature> playersOnMap = session.player().exploration().map().creatures()
        .stream().filter(p -> p instanceof ExplorationPlayer)
        .collect(Collectors.toList());

        playersOnMap.forEach( p -> ((ExplorationPlayer)p).send(new PlayerEmoteToMap(packet.emote(), session)));

    }

    @Override
    public Class<PlayerEmote> packet() {
        return PlayerEmote.class;
    }
}
