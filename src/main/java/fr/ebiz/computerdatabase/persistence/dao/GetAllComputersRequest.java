package fr.ebiz.computerdatabase.persistence.dao;

import fr.ebiz.computerdatabase.persistence.dao.ComputerDao.SortColumn;

import java.io.Serializable;
import java.util.Objects;

public class GetAllComputersRequest implements Serializable {

    private String query;
    private int page;
    private int pageSize;
    private SortColumn column;
    private SortOrder order;

    /**
     * Create a builder.
     *
     * @return The new builder instance
     */
    public static GetAllComputersRequestBuilder builder() {
        return new GetAllComputersRequestBuilder();
    }

    public String getQuery() {
        return query;
    }

    public int getPage() {
        return page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public SortColumn getColumn() {
        return column;
    }

    public SortOrder getOrder() {
        return order;
    }

    public int getOffset() {
        return page * pageSize;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GetAllComputersRequest that = (GetAllComputersRequest) o;
        return getPage() == that.getPage() &&
                getPageSize() == that.getPageSize() &&
                Objects.equals(getQuery(), that.getQuery()) &&
                getColumn() == that.getColumn() &&
                getOrder() == that.getOrder();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getQuery(), getPage(), getPageSize(), getColumn(), getOrder());
    }

    public static class GetAllComputersRequestBuilder {
        GetAllComputersRequest request;

        /**
         * Constructor.
         */
        GetAllComputersRequestBuilder() {
            request = new GetAllComputersRequest();
        }

        /**
         * Set the query.
         *
         * @param query The query to set
         * @return The builder
         */
        public GetAllComputersRequestBuilder query(String query) {
            request.query = query;
            return this;
        }

        /**
         * Set the page.
         *
         * @param page The page to set
         * @return The builder
         */
        public GetAllComputersRequestBuilder page(int page) {
            request.page = page;
            return this;
        }

        /**
         * Set the pageSize.
         *
         * @param pageSize The pageSize to set
         * @return The builder
         */
        public GetAllComputersRequestBuilder pageSize(int pageSize) {
            request.pageSize = pageSize;
            return this;
        }

        /**
         * Set the column.
         *
         * @param column The column to set
         * @return The builder
         */
        public GetAllComputersRequestBuilder column(SortColumn column) {
            request.column = column;
            return this;
        }

        /**
         * Set the order.
         *
         * @param order The order to set
         * @return The builder
         */
        public GetAllComputersRequestBuilder order(SortOrder order) {
            request.order = order;
            return this;
        }


        /**
         * Return the request.
         *
         * @return The dashboard request
         */
        public GetAllComputersRequest build() {
            return request;
        }

    }
}
