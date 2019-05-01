package fr.quatrevieux.araknemu.game.exploration.sprite;

import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.world.creature.Sprite;
import fr.quatrevieux.araknemu.game.world.map.Direction;
import org.apache.commons.lang3.StringUtils;

/**
 * Sprite for exploration player
 *
 * The sprite type ID MUST be the class id
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Game.as#L764
 */
final public class PlayerSprite implements Sprite {
    final private ExplorationPlayer exploration;

    public PlayerSprite(ExplorationPlayer exploration) {
        this.exploration = exploration;
    }

    @Override
    public int id() {
        return exploration.player().spriteInfo().id();
    }

    @Override
    public int cell() {
        return exploration.position().cell();
    }

    @Override
    public Direction orientation() {
        return exploration.orientation();
    }

    @Override
    public Type type() {
        return Type.PLAYER;
    }

    @Override
    public String name() {
        return exploration.player().name();
    }

    /**
     * cell;direction;bonus;id;name;type,title;gfxID^"scaleX"x"scaleY";sex;alignment;color1;color2;color3;accessories;aura;emote;emoteTimer;guildName;guildEmblem;restrictions;mount,mountColor1,mountColor2,mountColor3
     */
    @Override
    public String toString() {
        return
            cell() + ";" +
            orientation().ordinal() + ";" +
            "0;" + // bonus
            id() + ";" +
            name() + ";" +
            exploration.player().spriteInfo().race().ordinal() + ";" + // @todo title
            exploration.player().spriteInfo().gfxId() + "^" + exploration.player().spriteInfo().size() + ";" +
            exploration.player().spriteInfo().sex().ordinal() + ";" +
            ";" + // @todo alignment
            StringUtils.join(exploration.player().spriteInfo().colors().toHexArray(), ';') + ";" +
            exploration.player().spriteInfo().accessories() + ";" +
            ";" + // @todo aura
            ";;" + // @todo emote; emote timer
            ";;" + // @todo guild; guild emblem
            Integer.toString(exploration.restrictions().toInt(), 36) + ";"
            // @todo mount
        ;
    }
}
