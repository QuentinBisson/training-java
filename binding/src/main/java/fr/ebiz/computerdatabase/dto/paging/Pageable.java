package fr.ebiz.computerdatabase.dto.paging;

import java.io.Serializable;
import java.util.Objects;

public class Pageable implements Serializable {

    private int page;
    private int elements;

    /**
     * Create a Pageable builder instance.
     *
     * @return a new pageable builder
     */
    public static Pageable.PageableBuilder builder() {
        return new Pageable.PageableBuilder();
    }

    public int getPage() {
        return page;
    }

    public int getElements() {
        return elements;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Pageable pageable = (Pageable) o;
        return getPage() == pageable.getPage() &&
                getElements() == pageable.getElements();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPage(), getElements());
    }

    public static class PageableBuilder {
        private final Pageable pageable;

        /**
         * Constructor.
         */
        PageableBuilder() {
            pageable = new Pageable();
        }

        /**
         * Set the number of requested elements.
         *
         * @param elements The number of requested elements
         * @return the pageable builder instance
         */
        public PageableBuilder elements(int elements) {
            pageable.elements = elements;
            return this;
        }

        /**
         * Set the requested page.
         *
         * @param page The requested page
         * @return the pageable builder instance
         */
        public PageableBuilder page(int page) {
            pageable.page = page;
            return this;
        }

        /**
         * Build the pageable instance.
         *
         * @return the build pageable instance
         */
        public Pageable build() {
            return pageable;
        }
    }
}
