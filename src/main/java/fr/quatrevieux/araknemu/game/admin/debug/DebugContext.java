package fr.quatrevieux.araknemu.game.admin.debug;

import fr.quatrevieux.araknemu.core.di.Container;
import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.world.repository.environment.MapTemplateRepository;
import fr.quatrevieux.araknemu.game.admin.*;
import fr.quatrevieux.araknemu.game.admin.exception.CommandNotFoundException;
import fr.quatrevieux.araknemu.game.admin.exception.ContextNotFoundException;
import fr.quatrevieux.araknemu.game.item.ItemService;

import java.util.Collection;

/**
 * Context for debug commands
 */
final public class DebugContext implements Context {
    final private Container container;

    final private Context context;

    public DebugContext(Container container) throws ContainerException {
        this.container = container;

        context = configure();
    }

    @Override
    public Command command(String name) throws CommandNotFoundException {
        return context.command(name);
    }

    @Override
    public Collection<Command> commands() {
        return context.commands();
    }

    @Override
    public Context child(String name) throws ContextNotFoundException {
        return context.child(name);
    }

    private Context configure() throws ContainerException {
        return new SimpleContext(new NullContext())
            .add(new GenItem(container.get(ItemService.class)))
            .add(new FightPos(container.get(MapTemplateRepository.class)))
            .add(new Movement(container.get(MapTemplateRepository.class)))
            .add(new MapStats(container.get(MapTemplateRepository.class)))
        ;
    }
}
