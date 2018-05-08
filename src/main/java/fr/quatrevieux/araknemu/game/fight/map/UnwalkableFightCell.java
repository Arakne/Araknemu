package fr.quatrevieux.araknemu.game.fight.map;

import fr.quatrevieux.araknemu.data.world.entity.environment.MapTemplate;
import fr.quatrevieux.araknemu.game.fight.exception.FightMapException;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;

import java.util.Optional;

/**
 * Non walkable fight cell
 */
final public class UnwalkableFightCell implements FightCell {
    final private MapTemplate.Cell template;
    final private int id;

    public UnwalkableFightCell(MapTemplate.Cell template, int id) {
        this.template = template;
        this.id = id;
    }

    @Override
    public int id() {
        return id;
    }

    @Override
    public boolean walkable() {
        return false;
    }

    @Override
    public boolean walkableIgnoreFighter() {
        return false;
    }

    @Override
    public boolean sightBlocking() {
        return !template.lineOfSight();
    }

    @Override
    public Optional<Fighter> fighter() {
        return Optional.empty();
    }

    @Override
    public void set(Fighter fighter) {
        throw new FightMapException("Cannot add fighter on unwalkable cell");
    }

    @Override
    public void removeFighter() {
        throw new FightMapException("Cannot remove fighter on unwalkable cell");
    }
}
