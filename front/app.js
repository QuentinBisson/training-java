import angular from 'angular'

import computers from './app/computers';
import dashboard from './app/dashboard';

// Mount components as modules
const app = angular
    .module('app', [
        'computers',
        'dashboard',
        'angular-jwt'
    ])
    .config(function ($httpProvider, jwtOptionsProvider) {
        // Please note we're annotating the function so that the $injector works when the file is minified
        //jwtOptionsProvider.config({
        //  tokenGetter: function() {
        //    return store.get('currentUser');
        //  }
        //});

        //$httpProvider.interceptors.push('jwtInterceptor');
    }).run(function (authenticationService) {
        authenticationService.login('qbisson', 'qbisson', function (loggedIn) {
            console.log(loggedIn);
        })
    });