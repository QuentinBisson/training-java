import angular from 'angular'
import ngRoute from 'angular-route'

import routes from './routes'
import commons from './../commons'

import {computerList, dashboard, dashboardActions, dashboardTitle} from './components'

import {DASHBOARD_MODULE_NAME} from './config';

const dashboardModule = angular
    .module(DASHBOARD_MODULE_NAME, ['ngRoute', 'commons'])
    .config(routes)
    .component('dashboard', dashboard)
    .component('dashboardActions', dashboardActions)
    .component('dashboardTitle', dashboardTitle)
    .component('computerList', computerList);

export default dashboardModule