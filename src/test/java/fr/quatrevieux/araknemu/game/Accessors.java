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
 * Copyright (c) 2017-2021 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game;

import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.network.game.GameSession;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class Accessors {
    private final Map<Class, Map<String, Field>> fieldCache = new HashMap<>();

    public <T> T readField(Object object, String property) {
        try {
            return (T) getField(object.getClass(), property).get(object);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public Field getField(Class<?> type, String field) {
        return fieldCache
            .computeIfAbsent(type, t -> new HashMap<>())
            .computeIfAbsent(field, f -> {
                for (Class<?> c = type; c != Object.class; c = c.getSuperclass()) {
                    try {
                        Field reflection = c.getDeclaredField(field);
                        reflection.setAccessible(true);

                        return reflection;
                    } catch (NoSuchFieldException e) {

                    }
                }

                throw new RuntimeException("Field " + field + " is not found on class " + type);
            })
        ;
    }

    public GameSession session(Fighter fighter) {
        return session(PlayerFighter.class.cast(fighter).player());
    }

    public GameSession session(GamePlayer player) {
        return readField(player, "session");
    }
}
