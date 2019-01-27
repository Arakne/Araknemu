package fr.quatrevieux.araknemu.game.exploration.npc;

import fr.quatrevieux.araknemu.data.world.entity.environment.npc.Npc;
import fr.quatrevieux.araknemu.data.world.entity.environment.npc.NpcTemplate;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.npc.dialog.NpcQuestion;
import fr.quatrevieux.araknemu.game.world.creature.Creature;
import fr.quatrevieux.araknemu.game.world.creature.Operation;
import fr.quatrevieux.araknemu.game.world.creature.Sprite;
import fr.quatrevieux.araknemu.game.world.map.Direction;

import java.util.Collection;
import java.util.Optional;

/**
 * Living NPC
 */
final public class GameNpc implements Creature {
    final private Npc entity;
    final private NpcTemplate template;
    final private Collection<NpcQuestion> questions;

    final private Sprite sprite;

    public GameNpc(Npc entity, NpcTemplate template, Collection<NpcQuestion> questions) {
        this.entity = entity;
        this.template = template;
        this.questions = questions;

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

    /**
     * Get the initial dialog question of the NPC for the given player
     *
     * @param player The interlocutor player
     *
     * @return The first matching question, or empty if no matching question can be found
     */
    public Optional<NpcQuestion> question(ExplorationPlayer player) {
        return questions.stream()
            .filter(question -> question.check(player))
            .findFirst()
        ;
    }

    NpcTemplate template() {
        return template;
    }
}
