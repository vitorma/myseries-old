/*
 *   ListenerSet.java
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

package mobi.myseries.shared;

import java.util.Iterator;
import java.util.WeakHashMap;


public class ListenerSet<L> implements Iterable<L> {
    // It uses a HashMap because that is the only already implemented collection of weak references
    private WeakHashMap<L, Object> listeners;

    public ListenerSet() {
        this.listeners = new WeakHashMap<L, Object>();
    }

    public boolean register(L listener) {
        Validate.isNonNull(listener, "listener");

        if (this.listeners.keySet().contains(listener)) {
            return false;
        }

        this.listeners.put(listener, null);

        return true;
    }

    public boolean deregister(L listener) {
        Validate.isNonNull(listener, "listener");

        if (this.listeners.keySet().contains(listener)) {
            this.listeners.remove(listener);
            return true;
        }

        return false;
    }

    @Override
    public Iterator<L> iterator() {
        return this.listeners.keySet().iterator();
    }
}
