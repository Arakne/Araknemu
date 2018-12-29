package fr.quatrevieux.araknemu.game.exploration.npc;

import fr.quatrevieux.araknemu.game.world.creature.Sprite;
import fr.quatrevieux.araknemu.game.world.map.Direction;
import org.apache.commons.lang3.StringUtils;

/**
 * Sprite for the NPC
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Game.as#L630
 */
final public class NpcSprite implements Sprite {
    final private GameNpc npc;

    public NpcSprite(GameNpc npc) {
        this.npc = npc;
    }

    @Override
    public int id() {
        return npc.id();
    }

    @Override
    public int cell() {
        return npc.cell();
    }

    @Override
    public Direction orientation() {
        return npc.orientation();
    }

    @Override
    public Type type() {
        return Type.NPC;
    }

    @Override
    public String name() {
        return Integer.toString(npc.template().id());
    }

    @Override
    public String toString() {
        return
            cell() + ";" +
            orientation().ordinal() + ";" +
            "0;" + // Bonus
            id() + ";" +
            name() + ";" +
            type().id() + ";" +
            npc.template().gfxId() + "^" + npc.template().scaleX() + "x" + npc.template().scaleY() + ";" +
            npc.template().sex().ordinal() + ";" +
            StringUtils.join(npc.template().colors().toHexArray(), ';') + ";" +
            npc.template().accessories() + ";" +
            (npc.template().extraClip() != -1 ? npc.template().extraClip() : "") + ";" +
            npc.template().customArtwork()
        ;
    }
}
