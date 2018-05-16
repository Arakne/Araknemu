package fr.quatrevieux.araknemu.game.admin.debug;

import fr.quatrevieux.araknemu.common.account.Permission;
import fr.quatrevieux.araknemu.data.value.EffectArea;
import fr.quatrevieux.araknemu.data.world.repository.environment.MapTemplateRepository;
import fr.quatrevieux.araknemu.game.admin.AbstractCommand;
import fr.quatrevieux.araknemu.game.admin.AdminPerformer;
import fr.quatrevieux.araknemu.game.admin.AdminUser;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.fight.map.FightMap;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffectService;
import fr.quatrevieux.araknemu.game.spell.effect.area.*;
import fr.quatrevieux.araknemu.game.world.map.MapCell;
import fr.quatrevieux.araknemu.network.game.out.game.FightStartPositions;
import fr.quatrevieux.araknemu.util.Base64;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Display effect area
 */
final public class Area extends AbstractCommand {
    final private SpellEffectService service;

    public Area(SpellEffectService service) {
        this.service = service;
    }

    @Override
    protected void build(Builder builder) {
        builder
            .requires(Permission.DEBUG)
            .description("Display spell effect area")
            .help(
                "area [area string]",
                "\tarea string :",
                "\tContains two chars, the first one is the area type, the second if the size in pseudo base 64",
                "\tTypes :",
                Arrays.stream(EffectArea.Type.values())
                    .map(type -> "\t\t" + type.name() + " : " + type.c())
                    .collect(Collectors.joining("\n")),
                "\tExample :",
                "\tCb : Circle of size 1",
                "\tTc : Perpendicular line of size 2"
            )
        ;
    }

    @Override
    public String name() {
        return "area";
    }

    @Override
    public void execute(AdminPerformer performer, List<String> arguments) {
        AdminUser user = AdminUser.class.cast(performer);

        EffectArea area = new EffectArea(
            EffectArea.Type.byChar(arguments.get(1).charAt(0)),
            Base64.ord(arguments.get(1).charAt(1))
        );

        ExplorationMap map = user.player().exploration().map();

        List<Integer> cells = service.area(area)
            .resolve(
                map.get(user.player().position().cell()),
                map.get(user.player().position().cell())
            )
            .stream()
            .map(MapCell::id)
            .collect(Collectors.toList())
        ;

        user.send(
            new FightStartPositions(
                new List[] {
                    cells,
                    new ArrayList()
                },
                0
            )
        );
    }
}
