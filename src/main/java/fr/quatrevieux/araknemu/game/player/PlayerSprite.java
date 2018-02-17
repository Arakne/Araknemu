package fr.quatrevieux.araknemu.game.player;

import fr.quatrevieux.araknemu.data.constant.Race;
import fr.quatrevieux.araknemu.data.constant.Sex;
import fr.quatrevieux.araknemu.data.value.Colors;
import fr.quatrevieux.araknemu.game.world.creature.Sprite;
import fr.quatrevieux.araknemu.game.world.creature.accessory.Accessories;
import org.apache.commons.lang3.StringUtils;

/**
 * Sprite for player
 *
 * The sprite type ID MUST be the class id
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Game.as#L764
 */
final public class PlayerSprite implements Sprite {
    final private class Printer implements PlayableCharacter.Printer {
        final private String[] parts = new String[20];

        @Override
        public PlayableCharacter.Printer level(int level) {
            return this;
        }

        @Override
        public PlayableCharacter.Printer name(String name) {
            parts[4] = name;

            return this;
        }

        @Override
        public PlayableCharacter.Printer race(Race race) {
            parts[5] = Integer.toString(race.ordinal()); // @todo title

            return this;
        }

        @Override
        public PlayableCharacter.Printer sex(Sex sex) {
            parts[7] = Integer.toString(sex.ordinal());

            return this;
        }

        @Override
        public PlayableCharacter.Printer colors(Colors colors) {
            System.arraycopy(colors.toHexArray(), 0, parts, 9, 3);

            return this;
        }

        @Override
        public PlayableCharacter.Printer id(int id) {
            parts[3] = Integer.toString(id);

            return this;
        }

        @Override
        public PlayableCharacter.Printer gfxID(int gfxID) {
            parts[6] = Integer.toString(gfxID) + "^100x100"; // @todo scale and followers

            return this;
        }

        @Override
        public PlayableCharacter.Printer server(int id) {
            return this;
        }

        @Override
        public PlayableCharacter.Printer accessories(Accessories accessories) {
            parts[12] = accessories.toString();

            return this;
        }
    }

    final private GamePlayer player;

    public PlayerSprite(GamePlayer player) {
        this.player = player;
    }

    @Override
    public int id() {
        return player.id();
    }

    @Override
    public int cell() {
        return player.position().cell();
    }

    @Override
    public Type type() {
        return Type.PLAYER;
    }

    @Override
    public String name() {
        return player.entity.name();
    }

    /**
     * cell;direction;bonus;id;name;type,title;gfxID^"scaleX"x"scaleY";sex;alignment;color1;color2;color3;accessories;aura;emote;emoteTimer;guildName;guildEmblem;restrictions;mount,mountColor1,mountColor2,mountColor3
     */
    @Override
    public String toString() {
        Printer printer = new Printer();

        player.print(printer);

        String[] parts = printer.parts;

        parts[0] = Integer.toString(cell());
        parts[1] = "0"; // @todo direction
        parts[2] = "0"; // bonus

        for (int i = 0; i < parts.length; ++i) {
            if (parts[i] == null) {
                parts[i] = "";
            }
        }

        return StringUtils.join(parts, ";");
    }
}
