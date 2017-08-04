import angular from 'angular';

// Mount components as modules
const app = angular
    .module('app', [
        'computers',
        'dashboard'
    ]);