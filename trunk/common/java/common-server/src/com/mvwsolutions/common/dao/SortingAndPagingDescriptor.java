package com.mvwsolutions.common.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Holds the query sorting & paging spec (by property names) for server-side/db
 * queries.
 * @author smineyev with permission, Marcus
 * 
 */

public class SortingAndPagingDescriptor {
    // 1. sorting options
    public static class SortingOption {
        private String property;

        private String direction; // one of {"asc", "desc"}; unrecognized

        // values & null are treated as "asc"

        public SortingOption(String property, String direction) {
            super();
            this.property = property;
            this.direction = direction;
        }

        public SortingOption(String property, boolean asc) {
            super();
            this.property = property;
            this.direction = asc ? "asc" : "desc";
        }
        
        /**
         * @return the direction
         */
        public String getDirection() {
            return direction;
        }

        /**
         * @return the property
         */
        public String getProperty() {
            return property;
        }
    }

    // data members for Sortings // // ordered list; never returns null
    private List<SortingOption> sortingOptions = new ArrayList<SortingOption>();

    // 2. paging options
    // data members
    private int startIndex = -1; // (ignorable) -1 ==> all records returned

    private int pageSize = -1; // (ignorable) -1 ==> all records returned

    // Constructors:
    public SortingAndPagingDescriptor() {
    };

    public SortingAndPagingDescriptor(final SortingOption... sortingOptions) {
        if (sortingOptions != null) {
            this.getSortingOptions().addAll(Arrays.asList(sortingOptions));
        }
    };

    public SortingAndPagingDescriptor(final int startIndex, final int pageSize) {
        this.pageSize = pageSize;
        this.startIndex = startIndex;

    };
    
    public SortingAndPagingDescriptor(
            final int startIndex, final int pageSize, SortingOption... sortingOptions) {
        this.pageSize = pageSize;
        this.startIndex = startIndex;
        if (sortingOptions != null) {
            this.getSortingOptions().addAll(Arrays.asList(sortingOptions));
        }
    }
    

    public List<SortingOption> getSortingOptions() {
        return sortingOptions;
    }

    //
    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    /**
     * @param sortingOptions
     *            the sortingOptions to set
     */
    public void setSortingOptions(java.util.List<SortingOption> sortingOptions) {
        this.sortingOptions = sortingOptions;
    };

}

