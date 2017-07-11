package fr.ebiz.computerdatabase.dto;

import fr.ebiz.computerdatabase.persistence.dao.ComputerDao.OrderType;

public class DashboardRequest {

    private String query;
    private int page;
    private int pageSize;
    private OrderType order;

    /**
     * Create a builder.
     *
     * @return The new builder instance
     */
    public static DashboardRequestBuilder builder() {
        return new DashboardRequestBuilder();
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

    public OrderType getOrder() {
        return order;
    }

    public static class DashboardRequestBuilder {
        DashboardRequest request;

        /**
         * Constructor.
         */
        DashboardRequestBuilder() {
            request = new DashboardRequest();
        }

        /**
         * Set the query.
         *
         * @param query The query to set
         * @return The builder
         */
        public DashboardRequestBuilder query(String query) {
            request.query = query;
            return this;
        }

        /**
         * Set the page.
         *
         * @param page The page to set
         * @return The builder
         */
        public DashboardRequestBuilder page(int page) {
            request.page = page;
            return this;
        }

        /**
         * Set the pageSize.
         *
         * @param pageSize The pageSize to set
         * @return The builder
         */
        public DashboardRequestBuilder pageSize(int pageSize) {
            request.pageSize = pageSize;
            return this;
        }

        /**
         * Set the order.
         *
         * @param order The order to set
         * @return The builder
         */
        public DashboardRequestBuilder order(OrderType order) {
            request.order = order;
            return this;
        }


        /**
         * Return the request.
         *
         * @return The dashboard request
         */
        public DashboardRequest build() {
            return request;
        }

    }
}
