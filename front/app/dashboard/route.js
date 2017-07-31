import {DASHBOARD_COMPONENT_NAME} from './config'

export default function routeConfig($routeProvider) {
    $routeProvider
        .when('/', {
            template: `<${DASHBOARD_COMPONENT_NAME}></${DASHBOARD_COMPONENT_NAME}>`
        });
}

routeConfig.$inject = ["$routeProvider"];