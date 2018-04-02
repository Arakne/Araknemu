package fr.quatrevieux.araknemu.network.game.out.account;

import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.network.game.out.object.ItemSerializer;
import org.apache.commons.lang3.StringUtils;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Confirm character select and start game
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Account.as#L764
 */
final public class CharacterSelected {
    final private GamePlayer character;

    public CharacterSelected(GamePlayer character) {
        this.character = character;
    }

    @Override
    public String toString() {
        return
            "ASK|" +
            character.id() + "|" +
            character.name() + "|" +
            character.experience().level() + "|" +
            "|" + // @todo guild
            character.spriteInfo().sex().ordinal() + "|" +
            character.spriteInfo().gfxId() + "|" +
            StringUtils.join(character.spriteInfo().colors().toHexArray(), '|') + "|" +
            StreamSupport.stream(character.inventory().spliterator(), false)
                .map(ItemSerializer::new)
                .map(Object::toString)
                .collect(Collectors.joining(";"))
        ;
    }
}
