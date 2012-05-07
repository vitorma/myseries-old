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
    private ListenerNode<L> firstSentinel;
    private ListenerNode<L> lastSentinel;

    public ListenerSet() {
        this.firstSentinel = new ListenerNode<L>(null);
        this.lastSentinel = this.firstSentinel;

        this.firstSentinel.setNext(this.lastSentinel);
        this.lastSentinel.setPrevious(this.firstSentinel);
    }

    public boolean register(L listener) {
        Validate.isNonNull(listener, "listener");

        if (isRegistered(listener)) {
            return false;
        }

        ListenerNode<L> newLastListener = new ListenerNode<L>(listener);
        ListenerNode<L> oldLastListener = this.lastSentinel.previous();

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
        return new ListenerIterator<L>(this.firstSentinel, this.lastSentinel);
    }

    private static class ListenerNode<L> {
        private WeakReference<L> listener;

        private ListenerNode<L> next;
        private ListenerNode<L> previous;

        public ListenerNode(L listener) {
            this.listener = new WeakReference<L>(listener);
        }

        public L listener() {
            return this.listener.get();
        }

        public ListenerNode<L> next() {
            return this.next;
        }

        public void setNext(ListenerNode<L> next) {
            this.next = next;
        }

        public ListenerNode<L> previous() {
            return this.previous;
        }

        public void setPrevious(ListenerNode<L> previous) {
            this.previous = previous;
        }
    }

    private static class ListenerIterator<L> implements Iterator<L> {
        private ListenerNode<L> lastSentinelOfTheList;
        private ListenerNode<L> thisListener;

        public ListenerIterator(ListenerNode<L> firstListener, ListenerNode<L> lastSentinel) {
            this.thisListener = firstListener;
            this.lastSentinelOfTheList = lastSentinel;
        }

        private ListenerNode<L> nextListener() {
            return this.thisListener.next();
        }

        private ListenerNode<L> previousListener() {
            return this.thisListener.previous();
        }

        @Override
        public boolean hasNext() {
            this.removeNextCollectedListeners();

            return this.nextListener() != this.lastSentinelOfTheList;
        }

        private void removeNextCollectedListeners() {
            for (ListenerNode<L> nextListener = this.nextListener();
                    nextListener != this.lastSentinelOfTheList && nextListener.listener() == null;
                    nextListener = this.nextListener()) {

                new ListenerIterator<L>(nextListener, this.lastSentinelOfTheList).remove();
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
            ListenerNode<L> nextListener = this.nextListener();
            ListenerNode<L> previousListener = this.previousListener();

            previousListener.setNext(nextListener);
            nextListener.setPrevious(previousListener);
        }
    }
}
