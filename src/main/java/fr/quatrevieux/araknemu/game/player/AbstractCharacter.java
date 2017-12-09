package fr.quatrevieux.araknemu.game.player;

import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.game.account.GameAccount;

/**
 * Base class for characters and players
 */
abstract public class AbstractCharacter implements PlayableCharacter {
    final private GameAccount account;
    final protected Player entity;

    public AbstractCharacter(GameAccount account, Player entity) {
        this.account = account;
        this.entity = entity;
    }

    @Override
    public void print(Printer printer) {
        printer
            .colors(entity.colors())
            .name(entity.name())
            .race(entity.race())
            .sex(entity.sex())
            .level(entity.level())
            .id(entity.id())
            .gfxID(10 * entity.race().ordinal() + entity.sex().ordinal())
            .server(entity.serverId())
        ;
    }

    @Override
    public int id() {
        return entity.id();
    }

    @Override
    public GameAccount account() {
        return account;
    }
}
