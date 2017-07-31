import angular from 'angular';
import ngRoute from 'angular-route';

import routeConfig from './route';
import dashboard from './component'
import {
    DASHBOARD_MODULE_NAME,
    DASHBOARD_COMPONENT_NAME
} from './config';

const dashboardModule = angular.module(DASHBOARD_MODULE_NAME, ['ngRoute']);
dashboardModule
    .config(routeConfig)
    .component(DASHBOARD_COMPONENT_NAME, dashboard);

export default dashboardModule;