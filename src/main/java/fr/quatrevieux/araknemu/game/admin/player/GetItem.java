/*
 * This file is part of Araknemu.
 *
 * Araknemu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Araknemu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Araknemu.  If not, see <https://www.gnu.org/licenses/>.
 *
 * Copyright (c) 2017-2020 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.admin.player;

import fr.quatrevieux.araknemu.common.account.Permission;
import fr.quatrevieux.araknemu.data.constant.Effect;
import fr.quatrevieux.araknemu.data.value.ItemTemplateEffectEntry;
import fr.quatrevieux.araknemu.data.world.transformer.ItemEffectsTransformer;
import fr.quatrevieux.araknemu.game.admin.AbstractCommand;
import fr.quatrevieux.araknemu.game.admin.AdminPerformer;
import fr.quatrevieux.araknemu.game.admin.exception.CommandException;
import fr.quatrevieux.araknemu.game.admin.formatter.Link;
import fr.quatrevieux.araknemu.game.item.Item;
import fr.quatrevieux.araknemu.game.item.ItemService;
import fr.quatrevieux.araknemu.game.item.effect.ItemEffect;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Get an item for the player
 */
final public class GetItem extends AbstractCommand {
    final private GamePlayer player;
    final private ItemService service;

    public GetItem(GamePlayer player, ItemService service) {
        this.player = player;
        this.service = service;
    }

    @Override
    protected void build(Builder builder) {
        builder
            .description("Add an item to the player")
            .help(
                formatter -> formatter
                    .synopsis("getitem [options] item_id [quantity=1]")

                    .options("--max", "Generate item with maximized stats")
                    .options("--each", "Regenerate item stats for each [quantity] items instead of generate the same item with [quantity]")
                    .options("--effects",
                        "Set the item effects\n" +
                        "The effects should be a list of effects separated with comma ','. Available formats :\n" +
                        "Item template format : 64#b#f#0#1d5+10,7d#b#0#0#0d0+11,9a#f#0#0#0d0+15\n" +
                        "Simplified format    : INFLICT_DAMAGE_NEUTRAL:11:15,ADD_VITALITY:11,SUB_AGILITY:15\n" +
                        "This option is not compatible with --max option.\n" +
                        "If a range value is set for a characteristic effect, a random value will be generated\n"
                    )

                    .example("getitem 2425", "Generate a random 'Amulette du Bouftou'")
                    .example("!getitem 2425 3", "Generate 3 random 'Amulette du Bouftou', and ensure that the admin user is the target")
                    .example("${player:Robert} getitem 39", "Add to Robert the 'Petite Amulette du Hibou'")
                    .example("getitem --max 2425", "Generate an 'Amulette du Bouftou' with max stats")
                    .example("getitem --effects 5b#1#32#0#,5c#1#32#0#,5d#1#32#0#,5e#1#32#0#,5f#1#32#0# 40", "Cheated 'Petite Epée de Boisaille'")
                    .example("getitem --effects STOLEN_WATER:1:50,STOLEN_EARTH:1:50,STOLEN_WIND:1:50,STOLEN_FIRE:1:50,STOLEN_NEUTRAL:1:50 40", "Same as above")

                    .seeAlso("/ui itemsummoner", "Show the item picker", Link.Type.EXECUTE)
            )
            .requires(Permission.MANAGE_PLAYER)
        ;
    }

    @Override
    public String name() {
        return "getitem";
    }

    @Override
    public void execute(AdminPerformer performer, List<String> arguments) throws CommandException {
        // @todo argument struct
        boolean max = false;
        boolean each = false;

        List<ItemTemplateEffectEntry> effects = null;

        int i;

        for (i = 1; i < arguments.size() && arguments.get(i).startsWith("--"); ++i) {
            switch (arguments.get(i)) {
                case "--max":
                    max = true;
                    break;
                case "--each":
                    each = true;
                    break;
                case "--effects":
                    effects = parseEffects(arguments.get(++i));
                    break;
                default:
                    throw new CommandException(arguments.get(0), "Undefined option " + arguments.get(i));
            }
        }

        if (i == arguments.size()) {
            throw new CommandException(arguments.get(0), "Missing argument item_id");
        }

        final int itemId = Integer.parseInt(arguments.get(i));

        int quantity = arguments.size() > i + 1 ? Integer.parseInt(arguments.get(i + 1)) : 1;
        int times = 1;

        if (each) {
            times = quantity;
            quantity = 1;
        }

        for (int j = 0; j < times; ++j) {
            final Item item = effects == null
                ? service.create(itemId, max)
                : service.retrieve(itemId, effects)
            ;

            player.inventory().add(item, quantity);

            performer.success("Generate {} '{}'", quantity, item.template().name());

            if (!item.effects().isEmpty()) {
                performer.success("Effects :");

                for (ItemEffect effect : item.effects()) {
                    performer.success("\t{}", effect.toString());
                }
            }
        }
    }

    private List<ItemTemplateEffectEntry> parseEffects(String value) throws CommandException {
        if (value.contains("#")) {
            return new ItemEffectsTransformer().unserialize(value);
        }

        final List<ItemTemplateEffectEntry> effects = new ArrayList<>();

        for (String strEffect : StringUtils.split(value, ",")) {
            final String[] parts = StringUtils.split(strEffect, ":", 5);
            final Effect effect;

            try {
                effect = Effect.valueOf(parts[0].toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new CommandException("getitem", "Undefined effect " + parts[0]);
            }

            effects.add(
                new ItemTemplateEffectEntry(
                    effect,
                    parts.length > 1 ? Integer.parseInt(parts[1]) : 0,
                    parts.length > 2 ? Integer.parseInt(parts[2]) : 0,
                    parts.length > 3 ? Integer.parseInt(parts[3]) : 0,
                    parts.length > 4 ? parts[4]                   : ""
                )
            );
        }

        return effects;
    }
}
