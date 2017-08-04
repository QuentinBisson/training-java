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