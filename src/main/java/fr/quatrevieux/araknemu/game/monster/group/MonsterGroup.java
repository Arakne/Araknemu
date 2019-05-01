package fr.quatrevieux.araknemu.game.monster.group;

import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.map.cell.ExplorationMapCell;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.monster.Monster;
import fr.quatrevieux.araknemu.game.monster.environment.LivingMonsterGroupPosition;
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
    final private LivingMonsterGroupPosition handler;
    final private int id;
    final private List<Monster> monsters;

    final private MonsterGroupSprite sprite;

    private Direction orientation;
    private ExplorationMapCell cell;

    public MonsterGroup(LivingMonsterGroupPosition handler, int id, List<Monster> monsters, Direction orientation, ExplorationMapCell cell) {
        this.handler = handler;
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
    public ExplorationMapCell cell() {
        return cell;
    }

    @Override
    public Direction orientation() {
        return orientation;
    }

    @Override
    public void apply(Operation operation) {
        operation.onMonsterGroup(this);
    }

    /**
     * Monsters that compose the group
     */
    public List<Monster> monsters() {
        return monsters;
    }

    /**
     * Get the group position handler
     */
    public LivingMonsterGroupPosition handler() {
        return handler;
    }

    /**
     * Attack the group and start a PvM fight
     * After this call, the monster group and the player will be removed from map to join the fight
     *
     * @param player The attacker
     *
     * @return The created fight
     *
     * @see fr.quatrevieux.araknemu.game.fight.type.PvmType
     */
    public Fight startFight(ExplorationPlayer player) {
        return handler.startFight(this, player);
    }
}
