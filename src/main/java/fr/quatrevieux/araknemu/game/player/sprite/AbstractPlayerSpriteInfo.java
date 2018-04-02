package fr.quatrevieux.araknemu.game.player.sprite;

import fr.quatrevieux.araknemu.data.constant.Race;
import fr.quatrevieux.araknemu.data.constant.Sex;
import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.data.value.Colors;
import fr.quatrevieux.araknemu.game.world.creature.accessory.Accessories;

/**
 * Sprite info for using Player entity
 */
abstract public class AbstractPlayerSpriteInfo implements SpriteInfo {
    final private Player entity;

    public AbstractPlayerSpriteInfo(Player entity) {
        this.entity = entity;
    }

    @Override
    public int id() {
        return entity.id();
    }

    @Override
    public String name() {
        return entity.name();
    }

    @Override
    public Colors colors() {
        return entity.colors();
    }

    @Override
    public int gfxId() {
        return 10 * entity.race().ordinal() + entity.sex().ordinal();
    }

    @Override
    public SpriteSize size() {
        return new SpriteSize(100, 100);
    }

    @Override
    public Sex sex() {
        return entity.sex();
    }

    @Override
    public Race race() {
        return entity.race();
    }
}
