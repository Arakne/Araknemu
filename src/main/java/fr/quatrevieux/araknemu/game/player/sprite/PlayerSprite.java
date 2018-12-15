package fr.quatrevieux.araknemu.game.player.sprite;

import fr.quatrevieux.araknemu.data.value.Position;
import fr.quatrevieux.araknemu.game.exploration.Restrictions;
import fr.quatrevieux.araknemu.game.world.creature.Sprite;
import org.apache.commons.lang3.StringUtils;

/**
 * Sprite for player
 *
 * The sprite type ID MUST be the class id
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Game.as#L764
 */
final public class PlayerSprite implements Sprite {
    final private SpriteInfo spriteInfo;
    final private Position position;
    final private Restrictions restrictions;

    public PlayerSprite(SpriteInfo spriteInfo, Position position, Restrictions restrictions) {
        this.spriteInfo = spriteInfo;
        this.position = position;
        this.restrictions = restrictions;
    }

    @Override
    public int id() {
        return spriteInfo.id();
    }

    @Override
    public int cell() {
        return position.cell();
    }

    @Override
    public Type type() {
        return Type.PLAYER;
    }

    @Override
    public String name() {
        return spriteInfo.name();
    }

    /**
     * cell;direction;bonus;id;name;type,title;gfxID^"scaleX"x"scaleY";sex;alignment;color1;color2;color3;accessories;aura;emote;emoteTimer;guildName;guildEmblem;restrictions;mount,mountColor1,mountColor2,mountColor3
     */
    @Override
    public String toString() {
        return
            cell() + ";" +
            "0;" + // @todo direction
            "0;" + // bonus
            id() + ";" +
            name() + ";" +
            spriteInfo.race().ordinal() + ";" + // @todo title
            spriteInfo.gfxId() + "^" + spriteInfo.size() + ";" +
            spriteInfo.sex().ordinal() + ";" +
            ";" + // @todo alignment
            StringUtils.join(spriteInfo.colors().toHexArray(), ';') + ";" +
            spriteInfo.accessories() + ";" +
            ";" + // @todo aura
            ";;" + // @todo emote; emote timer
            ";;" + // @todo guild; guild emblem
            Integer.toString(restrictions.toInt(), 36) + ";"
            // @todo mount
        ;
    }
}
