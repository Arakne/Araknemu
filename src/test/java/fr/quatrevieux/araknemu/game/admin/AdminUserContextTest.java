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
 * Copyright (c) 2017-2019 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.admin;

import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.admin.context.Context;
import fr.quatrevieux.araknemu.game.admin.debug.DebugContext;
import fr.quatrevieux.araknemu.game.admin.exception.ContextException;
import fr.quatrevieux.araknemu.game.admin.exception.ContextNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AdminUserContextTest extends GameBaseCase {
    private AdminUserContext context;

    private Context self;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        context = new AdminUserContext(
            container.get(AdminService.class),
            self = Mockito.mock(Context.class)
        );
    }

    @Test
    void getters() {
        assertSame(self, context.current());
        assertSame(self, context.self());
    }

    @Test
    void getNotFound() {
        assertThrows(ContextNotFoundException.class, () -> context.get("notFound"));
    }

    @Test
    void setGet() throws ContextNotFoundException {
        Context ctx = Mockito.mock(Context.class);

        context.set("ctx", ctx);

        assertSame(ctx, context.get("ctx"));
    }

    @Test
    void setCurrent() {
        Context ctx = Mockito.mock(Context.class);

        context.setCurrent(ctx);

        assertSame(ctx, context.current());
    }

    @Test
    void resolveBadType() {
        assertThrows(ContextException.class, () -> context.resolve("bad_type", ""), "Context type 'bad_type' not found");
    }

    @Test
    void resolveSuccess() throws ContextException {
        assertInstanceOf(DebugContext.class, context.resolve("debug", null));
    }
}
