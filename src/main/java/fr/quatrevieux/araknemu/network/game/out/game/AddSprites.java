package fr.quatrevieux.araknemu.network.game.out.game;

import fr.quatrevieux.araknemu.game.world.creature.Sprite;

import java.util.Collection;

/**
 * Add sprites to the current map
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Game.as#L434
 */
final public class AddSprites {
    private Collection<? extends Sprite> sprites;

    public AddSprites(Collection<? extends Sprite> sprites) {
        this.sprites = sprites;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(64 * sprites.size());

        sb.append("GM");

        for(Sprite sprite : sprites){
            sb.append("|+").append(sprite);
        }

        return sb.toString();
    }
}
