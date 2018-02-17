package fr.quatrevieux.araknemu.network.game.out.account;

import fr.quatrevieux.araknemu.data.constant.Race;
import fr.quatrevieux.araknemu.data.constant.Sex;
import fr.quatrevieux.araknemu.data.value.Colors;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.game.player.PlayableCharacter;
import fr.quatrevieux.araknemu.game.player.inventory.InventoryEntry;
import fr.quatrevieux.araknemu.game.world.creature.accessory.Accessories;
import fr.quatrevieux.araknemu.network.game.out.object.ItemSerializer;
import org.apache.commons.lang3.StringUtils;

/**
 * Confirm character select and start game
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Account.as#L764
 */
final public class CharacterSelected {
    final private class Printer implements PlayableCharacter.Printer {
        final private Object[] parts = new Object[10];

        @Override
        public PlayableCharacter.Printer level(int level) {
            parts[2] = level;
            return this;
        }

        @Override
        public PlayableCharacter.Printer name(String name) {
            parts[1] = name;
            return this;
        }

        @Override
        public PlayableCharacter.Printer race(Race race) {
            return this;
        }

        @Override
        public PlayableCharacter.Printer sex(Sex sex) {
            parts[4] = sex.ordinal();
            return this;
        }

        @Override
        public PlayableCharacter.Printer colors(Colors colors) {
            System.arraycopy(colors.toHexArray(), 0, parts, 6, 3);
            return this;
        }

        @Override
        public PlayableCharacter.Printer id(int id) {
            parts[0] = id;
            return this;
        }

        @Override
        public PlayableCharacter.Printer gfxID(int gfxID) {
            parts[5] = gfxID;
            return this;
        }

        @Override
        public PlayableCharacter.Printer server(int id) {
            return this;
        }

        @Override
        public PlayableCharacter.Printer accessories(Accessories accessories) {
            return this;
        }

        public void write(StringBuilder sb) {
            for (Object part : parts) {
                sb.append('|');

                if (part != null) {
                    sb.append(part.toString());
                }
            }
        }
    }

    final private GamePlayer character;

    public CharacterSelected(GamePlayer character) {
        this.character = character;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("ASK");

        Printer printer = new Printer();
        character.print(printer);

        printer.write(sb);

        for (InventoryEntry entry : character.inventory()) {
            sb.append(new ItemSerializer(entry)).append(';');
        }

        return sb.toString();
    }
}
