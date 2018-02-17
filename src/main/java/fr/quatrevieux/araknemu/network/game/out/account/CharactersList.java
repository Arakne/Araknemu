package fr.quatrevieux.araknemu.network.game.out.account;

import fr.quatrevieux.araknemu.data.constant.Race;
import fr.quatrevieux.araknemu.data.constant.Sex;
import fr.quatrevieux.araknemu.data.value.Colors;
import fr.quatrevieux.araknemu.game.player.PlayableCharacter;
import fr.quatrevieux.araknemu.game.world.creature.accessory.Accessories;

import java.util.Collection;

/**
 * List all account characters
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Account.as#L471
 */
final public class CharactersList {
    final private class Printer implements PlayableCharacter.Printer {
        final private Object[] parts = new Object[13];

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
            return this;
        }

        @Override
        public PlayableCharacter.Printer colors(Colors colors) {
            System.arraycopy(colors.toHexArray(), 0, parts, 4, 3);

            return this;
        }

        @Override
        public PlayableCharacter.Printer id(int id) {
            parts[0] = id;

            return this;
        }

        @Override
        public PlayableCharacter.Printer gfxID(int gfxID) {
            parts[3] = gfxID;

            return this;
        }

        @Override
        public PlayableCharacter.Printer server(int id) {
            parts[9] = id;

            return this;
        }

        @Override
        public PlayableCharacter.Printer accessories(Accessories accessories) {
            parts[7] = accessories;

            return this;
        }

        public void write(StringBuilder sb) {
            for (int i = 0; i < parts.length; ++i) {
                if (i != 0) {
                    sb.append(';');
                }

                if (parts[i] != null) {
                    sb.append(parts[i].toString());
                }
            }
        }
    }

    final private long remainingTime;
    final private Collection<? extends PlayableCharacter> characters;

    public CharactersList(long remainingTime, Collection<? extends PlayableCharacter> characters) {
        this.remainingTime = remainingTime;
        this.characters = characters;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("ALK");

        sb.append(remainingTime).append('|').append(characters.size());

        for (PlayableCharacter character : characters) {
            Printer printer = new Printer();
            character.print(printer);

            printer.write(
                sb.append('|')
            );
        }

        return sb.toString();
    }
}
