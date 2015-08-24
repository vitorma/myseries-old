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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.CoreMatchers.either;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.Assert.assertThat;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.NoSuchElementException;

import mobi.myseries.shared.ListenerSet;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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

        assertThat(listeners, containsInAnyOrder(listener));
    }

    @Test
    public void everyListenerMustNotBeAvailableAfterBeingDeregistered() {
        Object listener = new Object();
        listeners.register(listener);

        listeners.deregister(listener);

        assertThat(listeners, not(containsInAnyOrder(listener)));
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

    @SuppressWarnings("unchecked")
    @Test
    public void equivalentButDifferentListenersArentTheSameInRegistering() {
        // Given
        Object firstListener = new EqualBy(1);
        Object secondListener = new EqualBy(1);

        listeners.register(firstListener);

        // When
        assertThat(listeners.register(secondListener), is(true));

        // Then
        assertThat(listeners, containsInAnyOrder(sameInstance(firstListener),
                                                 sameInstance(secondListener)));
    }

    @Test
    public void equivalentButDifferentListenersArentTheSameInDeregistering() {
        // Given
        Object firstListener = new EqualBy(1);
        Object secondListener = new EqualBy(1);

        listeners.register(firstListener);

        // When
        assertThat(listeners.deregister(secondListener), is(false));

        // Then
        assertThat(listeners, contains(sameInstance(firstListener)));
    }

    @Test
    public void iterationShouldBeKeptAfterGarbageCollection() {
        // Given
        Object firstListener = new Object();
        Object secondListener = new Object();
        Object thirdListener = new Object();

        listeners.register(firstListener);
        listeners.register(secondListener);
        listeners.register(thirdListener);

        Iterator<Object> it = listeners.iterator();

        // Start iterating
        assertThat(it.next(), is(either(sameInstance(firstListener))
                                    .or(sameInstance(secondListener))
                                    .or(sameInstance(thirdListener))));

        // After an element is collected
        secondListener = null;
        System.gc();

        // Then the iteration should not stop
        assertThat(it.next(), is(either(sameInstance(firstListener))
                                    .or(sameInstance(thirdListener))));
    }

    @Test
    public void iterationShouldBeKeptAfterListenerRegistration() {
        // Given
        Object firstListener = new Object();
        Object secondListener = new Object();
        Object thirdListener = new Object();

        listeners.register(firstListener);
        listeners.register(secondListener);

        Iterator<Object> it = listeners.iterator();

        // Start iterating
        assertThat(it.next(), is(either(sameInstance(firstListener))
                                    .or(sameInstance(secondListener))));

        // After an element is collected
        listeners.register(thirdListener);

        // Then the iteration should not stop
        assertThat(it.next(), is(either(sameInstance(firstListener))
                                    .or(sameInstance(secondListener))
                                    .or(sameInstance(thirdListener))));

        assertThat(it.next(), is(either(sameInstance(firstListener))
                                 .or(sameInstance(secondListener))
                                 .or(sameInstance(thirdListener))));
    }

    @Test
    public void iterationShouldBeKeptAfterListenerDeregistration() {
        // Given
        Object firstListener = new Object();
        Object secondListener = new Object();
        Object thirdListener = new Object();

        listeners.register(firstListener);
        listeners.register(secondListener);
        listeners.register(thirdListener);

        Iterator<Object> it = listeners.iterator();

        // Start iterating
        assertThat(it.next(), is(either(sameInstance(firstListener))
                                    .or(sameInstance(secondListener))
                                    .or(sameInstance(thirdListener))));

        // After an element is collected
        listeners.deregister(secondListener);

        // Then the iteration should not stop
        assertThat(it.next(), is(either(sameInstance(firstListener))
                                    .or(sameInstance(thirdListener))));
    }

    @Test(expected=NoSuchElementException.class)
    public void iteratorShouldThrowExceptionWhenThereIsNoElements() {
        listeners.iterator().next();
    }

    @Test(expected=NoSuchElementException.class)
    public void iteratorShouldThrowExceptionWhenThereIsNoMoreElements() {
        Object listener = new Object();

        listeners.register(listener);

        Iterator<Object> it = listeners.iterator();

        it.next();
        it.next();
    }

    @Test
    public void iteratorShouldRemoveCollectedElements() {
        Object firstListener = new Object();
        Object secondListener = new Object();

        listeners.register(firstListener);
        listeners.register(secondListener);

        firstListener = null;
        System.gc();

        Iterator<Object> it = listeners.iterator();

        assertThat(it.next(), sameInstance(secondListener));
        assertThat(it.hasNext(), is(false));
    }

    @Test(expected=NoSuchElementException.class)
    public void iteratorShouldThrowExceptionWhenElementsAreCollected() {
        Object firstListener = new Object();
        Object secondListener = new Object();

        listeners.register(firstListener);
        listeners.register(secondListener);

        firstListener = null;
        System.gc();

        Iterator<Object> it = listeners.iterator();

        assertThat(it.next(), sameInstance(secondListener));
        it.next();
    }

    @Test(expected=IllegalArgumentException.class)
    public void itCannotRegisterNullListeners() {
        new ListenerSet<Object>().register(null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void itCannotDeregisterNullListeners() {
        new ListenerSet<Object>().deregister(null);
    }

    /**
     * This class has its equivalence relation based on the id provided in the constructor.
     */
    private static class EqualBy {
        private int id;

        public EqualBy(int id) {
            this.id = id;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + this.id;
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            EqualBy other = (EqualBy) obj;
            if (this.id != other.id)
                return false;
            return true;
        }
    }
}
