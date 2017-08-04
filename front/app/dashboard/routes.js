export default function routeConfig($routeProvider) {
    $routeProvider
        .when('/', {
            template: `<dashboard></dashboard>`
        });
}

routeConfig.$inject = ["$routeProvider"];