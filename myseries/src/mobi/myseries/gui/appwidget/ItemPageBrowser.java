/*
 *   ItemPageBrowser.java
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

package mobi.myseries.gui.appwidget;

import mobi.myseries.shared.Validate;

public class ItemPageBrowser {
    public static final int FIRST_PAGE = 1;

    private int numberOfItems;
    private int itemsPerPage;
    private int firstPage;
    private int lastPage;
    private int currentPage;

    private ItemPageBrowser(int numberOfItems, int itemsPerPage) {
        this.numberOfItems = numberOfItems;
        this.itemsPerPage = itemsPerPage;
        this.firstPage = FIRST_PAGE;
        this.currentPage = FIRST_PAGE;
        this.lastPage = this.calculateLastPage();
    }

    public static ItemPageBrowser from(int numberOfItems, int itemsPerPage) {
        Validate.isNonNegative(numberOfItems, "numberOfItems");
        Validate.isNonNegative(itemsPerPage, "itemsPerPage");

        return new ItemPageBrowser(numberOfItems, itemsPerPage);
    }

    public ItemPageBrowser navigateAccordingToAction(String action) {
        if (Action.GO_TO_FIRST.equals(action)) {
            return this.goToFirstPage();
        }

        if (Action.GO_TO_PREVIOUS.equals(action)) {
            return this.goToPreviousPage();
        }

        if (Action.GO_TO_NEXT.equals(action)) {
            return this.goToNextPage();
        }

        if (Action.GO_TO_LAST.equals(action)) {
            return this.goToLastPage();
        }

        return this;
    }

    public ItemPageBrowser goToPage(int page) {
        if (!this.includesPage(page)) {
            return this.goToFirstPage();
        }

        return this.setCurrentPage(page);
    }

    public ItemPageBrowser goToFirstPage() {
        return this.setCurrentPage(this.firstPage);
    }

    public ItemPageBrowser goToLastPage() {
        return this.setCurrentPage(this.lastPage);
    }

    public ItemPageBrowser goToPreviousPage() {
        return this.setCurrentPage(this.calculatePreviousPage());
    }

    public ItemPageBrowser goToNextPage() {
        return this.setCurrentPage(this.calculateNextPage());
    }

    public int numberOfPages() {
        return this.lastPage;
    }

    public int currentPage() {
        return this.currentPage;
    }

    public int firstItemOfCurrentPage() {
        return (this.currentPage - 1) * this.itemsPerPage;
    }

    public int lastItemOfCurrentPage() {
        return this.numberOfItemsUntilCurrentPage() - 1;
    }

    public boolean isCurrentlyAtFirstPage() {
        return this.currentPage == this.firstPage;
    }

    public boolean isCurrentlyAtLastPage() {
        return this.currentPage == this.lastPage;
    }

    private ItemPageBrowser setCurrentPage(int page) {
        this.currentPage = page;
        return this;
    }

    private boolean includesPage(int page) {
        return page >= this.firstPage && page <= this.lastPage;
    }

    private int calculateLastPage() {
        if (this.numberOfItems == 0 || this.itemsPerPage == 0) {
            return FIRST_PAGE;
        }

        return this.numberOfItems % this.itemsPerPage == 0 ?
               this.numberOfItems / this.itemsPerPage :
               this.numberOfItems / this.itemsPerPage + 1;
    }

    private int calculatePreviousPage() {
        return this.isCurrentlyAtFirstPage() ? this.firstPage : this.currentPage - 1;
    }

    private int calculateNextPage() {
        return this.isCurrentlyAtLastPage() ? this.lastPage : this.currentPage + 1;
    }

    private int numberOfItemsUntilCurrentPage() {
        return this.isCurrentlyAtLastPage() ? this.numberOfItems : this.currentPage * this.itemsPerPage;
    }
}
