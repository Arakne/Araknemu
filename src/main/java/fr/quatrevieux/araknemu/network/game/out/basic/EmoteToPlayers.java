package fr.quatrevieux.araknemu.network.game.out.basic;

import fr.quatrevieux.araknemu.game.player.GamePlayer;

final public class EmoteToPlayers {
    final private String emote;
    final private GamePlayer player;

    public EmoteToPlayers(GamePlayer player, String emote) {
        this.emote = emote;
        this.player = player;
    }

    @Override
    public String toString() {
        return "cS"+player.id()+"|"+emote;
    }
}
