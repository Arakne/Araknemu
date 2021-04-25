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
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.OptionDef;
import org.kohsuke.args4j.spi.OptionHandler;
import org.kohsuke.args4j.spi.Parameters;
import org.kohsuke.args4j.spi.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Get an item for the player
 */
public final class GetItem extends AbstractCommand<GetItem.Options> {
    private final GamePlayer player;
    private final ItemService service;

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
    public Options createArguments() {
        return new Options();
    }

    @Override
    public void execute(AdminPerformer performer, Options options) throws CommandException {
        for (int j = 0; j < options.times(); ++j) {
            final Item item = options.hasCustomEffects()
                ? service.retrieve(options.itemId(), options.effects())
                : service.create(options.itemId(), options.max())
            ;

            player.inventory().add(item, options.quantity());

            performer.success("Generate {} '{}'", options.quantity(), item.template().name());

            if (!item.effects().isEmpty()) {
                performer.success("Effects :");

                for (ItemEffect effect : item.effects()) {
                    performer.success("\t{}", effect.toString());
                }
            }
        }
    }

    public static final class Options {
        @Option(name = "--max")
        private boolean max = false;

        @Option(name = "--each")
        private boolean each = false;

        @Option(name = "--effects", handler = EffectsConverter.class)
        private List<ItemTemplateEffectEntry> effects = null;

        @Argument(required = true, index = 0, metaVar = "item id")
        private int itemId;

        @Argument(index = 1, metaVar = "quantity")
        private int quantity = 1;

        public boolean max() {
            return max;
        }

        public void setMax(boolean max) {
            this.max = max;
        }

        public boolean each() {
            return each;
        }

        public void setEach(boolean each) {
            this.each = each;
        }

        public List<ItemTemplateEffectEntry> effects() {
            return effects;
        }

        public boolean hasCustomEffects() {
            return effects != null;
        }

        public void setEffects(List<ItemTemplateEffectEntry> effects) {
            this.effects = effects;
        }

        public int itemId() {
            return itemId;
        }

        public void setItemId(int itemId) {
            this.itemId = itemId;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public int times() {
            return each ? quantity : 1;
        }

        public int quantity() {
            return each ? 1 : quantity;
        }
    }

    public static class EffectsConverter extends OptionHandler<ItemTemplateEffectEntry> {
        public EffectsConverter(CmdLineParser parser, OptionDef option, Setter<? super ItemTemplateEffectEntry> setter) {
            super(parser, option, setter);
        }

        @Override
        public int parseArguments(Parameters params) throws CmdLineException {
            for (ItemTemplateEffectEntry effect : parseEffects(params.getParameter(0))) {
                setter.addValue(effect);
            }

            return 1;
        }

        @Override
        public String getDefaultMetaVariable() {
            return "effects,...";
        }

        private List<ItemTemplateEffectEntry> parseEffects(String value) throws CmdLineException {
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
                    throw new CmdLineException(owner, "Undefined effect " + parts[0], e);
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
}
