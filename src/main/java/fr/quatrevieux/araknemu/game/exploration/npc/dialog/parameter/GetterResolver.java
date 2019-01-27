package fr.quatrevieux.araknemu.game.exploration.npc.dialog.parameter;

import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;

import java.util.function.Function;

/**
 * Variable resolver using simple lambda getter
 *
 * <code>
 *     VariableResolver getter = new GetterResolver("id", ExplorationPlayer::id);
 * </code>
 */
final public class GetterResolver implements VariableResolver {
    final private String name;
    final private Function<ExplorationPlayer, Object> getter;

    public GetterResolver(String name, Function<ExplorationPlayer, Object> getter) {
        this.name = name;
        this.getter = getter;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public Object value(ExplorationPlayer player) {
        return getter.apply(player);
    }
}
