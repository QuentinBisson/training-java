import angular from 'angular'

import {COMMONS_MODULE_NAME} from './config';
import {pager} from './components'
import {AuthenticationService, ComputerService} from './services'

const commonsModule = angular.module(COMMONS_MODULE_NAME, []);

commonsModule
    .component('pager', pager)
    .service('computerService', ComputerService)
    .service('authenticationService', AuthenticationService);

export default commonsModule