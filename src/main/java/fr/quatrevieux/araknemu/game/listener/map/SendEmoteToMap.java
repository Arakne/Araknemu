package fr.quatrevieux.araknemu.game.listener.map;

import java.util.stream.Collectors;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.player.event.PlayerEmote;
import fr.quatrevieux.araknemu.network.game.out.basic.EmoteToPlayers;

public class SendEmoteToMap implements Listener<PlayerEmote> {

    @Override
    public Class<PlayerEmote> event() {
        return PlayerEmote.class;
    }

    @Override
    public void on(PlayerEmote event) {
        event.player().exploration().map().creatures()
        .stream().filter(p -> p instanceof ExplorationPlayer)
        .collect(Collectors.toList())
        .forEach(p -> ((ExplorationPlayer)p).send(new EmoteToPlayers(event.player(), event.emote())));
    }
    
}
