package fr.quatrevieux.araknemu.game.monster.group;

import fr.quatrevieux.araknemu.game.monster.Monster;
import fr.quatrevieux.araknemu.game.world.creature.Creature;
import fr.quatrevieux.araknemu.game.world.creature.Operation;
import fr.quatrevieux.araknemu.game.world.creature.Sprite;
import fr.quatrevieux.araknemu.game.world.map.Direction;

import java.util.List;

/**
 * Group of monsters
 * The group is the only creature which can be found on exploration for interacting with {@link Monster}
 */
final public class MonsterGroup implements Creature {
    final private int id;
    final private List<Monster> monsters;

    final private MonsterGroupSprite sprite;

    private Direction orientation;
    private int cell;

    public MonsterGroup(int id, List<Monster> monsters, Direction orientation, int cell) {
        this.id = id;
        this.monsters = monsters;
        this.orientation = orientation;
        this.cell = cell;

        this.sprite = new MonsterGroupSprite(this);
    }

    @Override
    public Sprite sprite() {
        return sprite;
    }

    @Override
    public int id() {
        return Sprite.Type.MONSTER_GROUP.toSpriteId(id);
    }

    @Override
    public int cell() {
        return cell;
    }

    @Override
    public Direction orientation() {
        return orientation;
    }

    @Override
    public void apply(Operation operation) {
        // @todo
    }

    /**
     * Monsters that compose the group
     */
    public List<Monster> monsters() {
        return monsters;
    }
}
