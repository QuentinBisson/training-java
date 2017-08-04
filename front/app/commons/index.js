import angular from 'angular'

import {COMMONS_MODULE_NAME} from './config';
import {pager} from './components'
import ComputerService from './services'

const commonsModule = angular.module(COMMONS_MODULE_NAME, []);

commonsModule
    .component('pager', pager)
    .service('computerService', ComputerService);

export default commonsModule