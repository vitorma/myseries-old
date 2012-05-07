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

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.NoSuchElementException;


public class ListenerSet<L> implements Iterable<L> {
    private ListenerNode firstSentinel;
    private ListenerNode lastSentinel;

    public ListenerSet() {
        this.firstSentinel = new ListenerNode(null);
        this.lastSentinel = this.firstSentinel;

        this.firstSentinel.setNext(this.lastSentinel);
        this.lastSentinel.setPrevious(this.firstSentinel);
    }

    public boolean register(L listener) {
        Validate.isNonNull(listener, "listener");

        if (isRegistered(listener)) {
            return false;
        }

        ListenerNode newLastListener = new ListenerNode(listener);
        ListenerNode oldLastListener = this.lastSentinel.previous();

        oldLastListener.setNext(newLastListener);
        newLastListener.setPrevious(oldLastListener);
        newLastListener.setNext(this.lastSentinel);
        this.lastSentinel.setPrevious(newLastListener);

        return true;
    }

    public boolean deregister(L listener) {
        Validate.isNonNull(listener, "listener");

        for (Iterator<L> it = this.iterator(); it.hasNext();) {
            if (it.next() == listener) {
                it.remove();
                return true;
            }
        }

        return false;
    }

    private boolean isRegistered(L listener) {
        for (Iterator<L> it = this.iterator(); it.hasNext();) {
            if (it.next() == listener) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Iterator<L> iterator() {
        return new ListenerIterator(this.firstSentinel);
    }

    private class ListenerNode {
        private WeakReference<L> listener;

        private ListenerNode next;
        private ListenerNode previous;

        public ListenerNode(L listener) {
            this.listener = new WeakReference<L>(listener);
        }

        public L listener() {
            return this.listener.get();
        }

        public ListenerNode next() {
            return this.next;
        }

        public void setNext(ListenerNode next) {
            this.next = next;
        }

        public ListenerNode previous() {
            return this.previous;
        }

        public void setPrevious(ListenerNode previous) {
            this.previous = previous;
        }
    }

    private class ListenerIterator implements Iterator<L> {
        private ListenerNode thisListener;

        public ListenerIterator(ListenerNode firstListener) {
            this.thisListener = firstListener;
        }

        private ListenerNode nextListener() {
            return this.thisListener.next();
        }

        private ListenerNode previousListener() {
            return this.thisListener.previous();
        }

        @Override
        public boolean hasNext() {
            this.removeNextCollectedListeners();

            return this.nextListener() != lastSentinel;
        }

        private void removeNextCollectedListeners() {
            for (ListenerNode nextListener = this.nextListener();
                    nextListener != lastSentinel && nextListener.listener() == null;
                    nextListener = this.nextListener()) {

                new ListenerIterator(nextListener).remove();
            }
        }

        @Override
        public L next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }

            this.thisListener = this.nextListener();
            return this.thisListener.listener();
        }

        @Override
        public void remove() {
            ListenerNode nextListener = this.nextListener();
            ListenerNode previousListener = this.previousListener();

            previousListener.setNext(nextListener);
            nextListener.setPrevious(previousListener);
        }
    }
}
