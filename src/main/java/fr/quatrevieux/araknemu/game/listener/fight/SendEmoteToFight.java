package fr.quatrevieux.araknemu.game.listener.fight;

import java.util.stream.Collectors;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.player.event.PlayerEmote;
import fr.quatrevieux.araknemu.network.game.out.basic.EmoteToPlayers;

public class SendEmoteToFight implements Listener<PlayerEmote> {

    @Override
    public Class<PlayerEmote> event() {
        return PlayerEmote.class;
    }

    @Override
    public void on(PlayerEmote event) {
        event.player().fighter().fight().fighters()
        .stream().filter(p -> p instanceof PlayerFighter)
        .collect(Collectors.toList())
        .forEach(p -> ((PlayerFighter)p).send(new EmoteToPlayers(event.player(), event.emote())));
    }
    
}
