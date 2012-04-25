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
import java.util.LinkedList;
import java.util.List;


public class ListenerSet<L> implements Iterable<L> {
	private List<L> listeners;

	public ListenerSet() {
		this.listeners = new LinkedList<L>();
	}

	public boolean register(L listener) {
		Validate.isNonNull(listener, "listener");

        for (L l : this.listeners) {
            if (l == listener) return false;
        }

        return this.listeners.add(listener);
	}

	public boolean deregister(L listener) {
        Validate.isNonNull(listener, "listener");

        for (int i = 0; i < this.listeners.size(); i++) {
            if (this.listeners.get(i) == listener) {
                this.listeners.remove(i);
                return true;
            }
        }

        return false;
    }

	@Override
	public Iterator<L> iterator() {
		return this.listeners.iterator();
	}
}
