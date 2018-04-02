package fr.quatrevieux.araknemu.network.game.out.account;

import fr.quatrevieux.araknemu.game.account.AccountCharacter;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;

/**
 * List all account characters
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Account.as#L471
 */
final public class CharactersList {
    final private long remainingTime;
    final private Collection<AccountCharacter> characters;

    public CharactersList(long remainingTime, Collection<AccountCharacter> characters) {
        this.remainingTime = remainingTime;
        this.characters = characters;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("ALK");

        sb.append(remainingTime).append('|').append(characters.size());

        for (AccountCharacter character : characters) {
            sb
                .append('|')
                .append(character.id()).append(';')
                .append(character.spriteInfo().name()).append(';')
                .append(character.level()).append(';')
                .append(character.spriteInfo().gfxId()).append(';')
                .append(StringUtils.join(character.spriteInfo().colors().toHexArray(), ';')).append(';')
                .append(character.spriteInfo().accessories()).append(';')
                .append(';') // @todo merchant
                .append(character.serverId()).append(';')
                .append(';') // @todo is dead
                .append(';') // @todo dead count
                // @todo level max
            ;
        }

        return sb.toString();
    }
}
