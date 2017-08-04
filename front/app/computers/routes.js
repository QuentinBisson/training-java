export default function routeConfig($routeProvider) {
    $routeProvider
        .when('/computers/:id', {
            template: `<computers></computers>`
        });
}

routeConfig.$inject = ["$routeProvider"];