package fr.quatrevieux.araknemu.game.listener.map;

import java.util.stream.Collectors;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.player.event.PlayerEmoteSent;
import fr.quatrevieux.araknemu.network.game.out.basic.EmoteToPlayers;

public class SendEmoteToMap implements Listener<PlayerEmoteSent> {

    @Override
    public Class<PlayerEmoteSent> event() {
        return PlayerEmoteSent.class;
    }

    @Override
    public void on(PlayerEmoteSent event) {
        event.player().exploration().map().creatures()
        .stream().filter(p -> p instanceof ExplorationPlayer)
        .collect(Collectors.toList())
        .forEach(p -> ((ExplorationPlayer)p).send(new EmoteToPlayers(event.player(), event.emote())));
    }
    
}
