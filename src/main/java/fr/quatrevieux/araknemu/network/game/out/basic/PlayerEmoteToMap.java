package fr.quatrevieux.araknemu.network.game.out.basic;

import fr.quatrevieux.araknemu.network.game.GameSession;

final public class PlayerEmoteToMap {
    final private String emote;
    final private GameSession session;

    public PlayerEmoteToMap(String emote, GameSession session) {
        this.emote = emote;
        this.session = session;
    }

    @Override
    public String toString() {
        return "cS"+session.player().id()+"|"+emote;
    }
}
