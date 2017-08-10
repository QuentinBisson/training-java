export default class ComputerService {
    constructor($http, $q) {
        this.endpoint = 'http://localhost:8080/ComputerDatabase/api/computers';
        this.$http = $http;
        this.$q = $q;
    }

    getAll(page, pageSize) {
        let config = {
            params: {
                page,
                pageSize
            },
            headers: {'Accept': 'application/json'}
        };

        let defered = this.$q.defer();
        this.$http.get(this.endpoint, config).then(
            data => defered.resolve(angular.fromJson(data)),
            (err) => defered.reject(err)
        );
        return defered.promise;
    }

    get(id) {
        let defered = this.$q.defer();

        $http.get(`${this.endpoint}/${id}`)
            .then(
                data => defered.resolve(data),
                err => defered.reject(err)
            );

        return deferred.promise;
    }
}

export class AuthenticationService {
    constructor($http) {
        this.$http = $http;
    }

    login(username, password, callback) {
        this.$http.post('http://localhost:8080/ComputerDatabase/oauth/authorize', {
            username: username,
            password: password
        })
            .then(response => {
                // login successful if there's a token in the response
                if (response.token) {
                    // store username and token in local storage to keep user logged in between page refreshes
                    //store.set('currentUser', { username: username, token: response.token });

                    // add jwt token to auth header for all requests made by the $http service
                    $http.defaults.headers.common.Authorization = 'Bearer ' + response.token;

                    // execute callback with true to indicate successful login
                    callback(true);
                } else {
                    // execute callback with false to indicate failed login
                    callback(false);
                }
            });
    }

    logout() {
        // remove user from local storage and clear http auth header
        //store.remove('currentUser');
        $http.defaults.headers.common.Authorization = '';
    }
}