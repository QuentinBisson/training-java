export const dashboard = {
    templateUrl: 'app/dashboard/templates/dashboard.html',
    controller: function (computerService) {
        this.page = 0;
        this.pageSize = 10;

        this.$onInit = function () {
            let promise = computerService
                .getAll(this.page, this.pageSize);

            promise
                .then(data => console.log(data), err => console.log(err));
        }
    }
};

export const dashboardTitle = {
    bindings: {
        elements: '<'
    },
    template: `
        <h1 id="homeTitle">
            $ctrl.elements computers found
        </h1>
    `
};

export const computerList = {
    bindings: {
        computers: '<'
    },
    templateUrl: 'app/dashboard/templates/computer-list.html'
};

export const dashboardActions = {
    templateUrl: 'app/dashboard/templates/dashboard-actions.html'
};
