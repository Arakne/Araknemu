package fr.quatrevieux.araknemu.game.listener.player;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.player.event.PlayerEmoteSent;
import fr.quatrevieux.araknemu.network.game.out.basic.EmoteToPlayers;

public class SendEmoteToOtherPlayers implements Listener<PlayerEmoteSent> {

    private ExplorationMap map;
    private Fight fight;

    public SendEmoteToOtherPlayers(ExplorationMap map) {
        this.map = map;
    }

    public SendEmoteToOtherPlayers(Fight fight) {
        this.fight = fight;
    }

    @Override
    public Class<PlayerEmoteSent> event() {
        return PlayerEmoteSent.class;
    }

    @Override
    public void on(PlayerEmoteSent event) {
        if(map == null) {
            fight.send(new EmoteToPlayers(event.player(), event.emote()));
        } else {
            map.send(new EmoteToPlayers(event.player(), event.emote()));
        }
    }
    
}
