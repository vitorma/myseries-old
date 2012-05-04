/*
 *   ListenerSetTests.java
 *
 *   Copyright 2012 MySeries Team.
 *
 *   This file is part of MySeries.
 *
 *   MySeries is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   MySeries is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with MySeries.  If not, see <http://www.gnu.org/licenses/>.
 */

package mobi.myseries.test.unit.shared;

import java.lang.ref.WeakReference;

import mobi.myseries.shared.ListenerSet;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.matchers.JUnitMatchers.*;

public class ListenerSetTests {

    private ListenerSet<Object> listeners;

    @Before
    public void setUp() {
        listeners = new ListenerSet<Object>();
    }

    @After
    public void tearDown() {
        listeners = null;
    }

    @Test
    public void itMustThereBeNoListenersBeforeRegistration() {
        assertThat(listeners.iterator().hasNext(), is(false));
    }

    @Test
    public void everyListenerMustBeAvailableAfterBeingRegistered() {
        Object listener = new Object();

        listeners.register(listener);

        assertThat(listeners, hasItem(listener));
    }

    @Test
    public void everyListenerMustNotBeAvailableAfterBeingDeregistered() {
        Object listener = new Object();
        listeners.register(listener);

        listeners.deregister(listener);

        assertThat(listeners, not(hasItem(listener)));
    }

    @Test
    public void anyListenerCanBeRegisteredOnlyOnce() {
        Object listener = new Object();
        listeners.register(listener);

        assertThat(listeners.register(listener), is(false));
    }

    @Test
    public void anyListenerMustBeDeregisteredOnlyOnce() {
        Object listener = new Object();

        listeners.register(listener);
        listeners.deregister(listener);

        assertThat(listeners.deregister(listener), is(false));
    }

    @Test
    public void listenersMustBeWeaklyReferenced() {
        // Given
        Object listener = new Object();

        WeakReference<Object> reference = new WeakReference<Object>(listener);
        assertThat(reference.get(), is(listener));

        listeners.register(listener);

        // When
        listener = null;
        System.gc();

        // Then
        assertThat(reference.get(), nullValue());
    }

    @Test(expected=IllegalArgumentException.class)
    public void itCannotRegisterNullListeners() {
        new ListenerSet<Object>().register(null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void itCannotDeregisterNullListeners() {
        new ListenerSet<Object>().deregister(null);
    }
}
