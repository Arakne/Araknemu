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

package fr.quatrevieux.araknemu.game.admin.debug;

import fr.quatrevieux.araknemu.common.account.Permission;
import fr.quatrevieux.araknemu.data.world.transformer.ItemEffectsTransformer;
import fr.quatrevieux.araknemu.game.admin.AbstractCommand;
import fr.quatrevieux.araknemu.game.admin.AdminPerformer;
import fr.quatrevieux.araknemu.game.admin.AdminUser;
import fr.quatrevieux.araknemu.game.chat.ChannelType;
import fr.quatrevieux.araknemu.game.item.Item;
import fr.quatrevieux.araknemu.game.item.ItemService;
import fr.quatrevieux.araknemu.game.item.effect.ItemEffect;
import fr.quatrevieux.araknemu.network.game.out.chat.MessageSent;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Try to generate item
 */
final public class GenItem extends AbstractCommand {
    final private ItemService service;

    public GenItem(ItemService service) {
        this.service = service;
    }

    @Override
    protected void build(Builder builder) {
        builder
            .description("Try to generate a new item")
            .help(formatter -> formatter.synopsis("genitem [item_id]"))
            .requires(Permission.DEBUG)
        ;
    }

    @Override
    public String name() {
        return "genitem";
    }

    @Override
    public void execute(AdminPerformer performer, List<String> arguments) {
        int param = 1;

        boolean maximize = false;

        if (arguments.get(param).equals("--max")) {
            maximize = true;
            ++param;
        }

        int itemId = Integer.parseInt(arguments.get(param));

        Item item = service.create(itemId, maximize);

        performer.success("Generate item {} ({}) : {}", item.template().name(), item.template().id(), item.getClass().getSimpleName());

        if (!item.effects().isEmpty()) {
            performer.success("Effects :");
            performer.success("===========================");

            for (ItemEffect effect : item.effects()) {
                performer.success(effect.toString());
            }

            performer.success("===========================");
        }

        if (!(performer instanceof AdminUser)) {
            return;
        }

        AdminUser user = (AdminUser) performer;

        user.send(
            new MessageSent(
                user.player(),
                ChannelType.ADMIN,
                "Generated item : °0",
                item.template().id() + "!" + new ItemEffectsTransformer().serialize(item.effects().stream().map(ItemEffect::toTemplate).collect(Collectors.toList()))
            )
        );
    }
}
