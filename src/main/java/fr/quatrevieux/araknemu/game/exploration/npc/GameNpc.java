package fr.quatrevieux.araknemu.game.exploration.npc;

import fr.quatrevieux.araknemu.data.world.entity.environment.npc.Npc;
import fr.quatrevieux.araknemu.data.world.entity.environment.npc.NpcTemplate;
import fr.quatrevieux.araknemu.game.world.creature.Creature;
import fr.quatrevieux.araknemu.game.world.creature.Operation;
import fr.quatrevieux.araknemu.game.world.creature.Sprite;
import fr.quatrevieux.araknemu.game.world.map.Direction;

/**
 * Living NPC
 */
final public class GameNpc implements Creature {
    final private Npc entity;
    final private NpcTemplate template;

    final private Sprite sprite;

    public GameNpc(Npc entity, NpcTemplate template) {
        this.entity = entity;
        this.template = template;

        this.sprite = new NpcSprite(this);
    }

    @Override
    public Sprite sprite() {
        return sprite;
    }

    @Override
    public int id() {
        return Sprite.Type.NPC.toSpriteId(entity.id());
    }

    @Override
    public int cell() {
        return entity.position().cell();
    }

    @Override
    public Direction orientation() {
        return entity.orientation();
    }

    @Override
    public void apply(Operation operation) {
        operation.onNpc(this);
    }

    NpcTemplate template() {
        return template;
    }
}
