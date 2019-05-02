angular.module('baseApp').controller('mainController', function ($scope, $controller) {
    angular.extend(this, $controller('controllerBase', {$scope: $scope}));

    $scope.model.classe = 'partner';

    $scope.query();

    $scope.model.entities.$promise.then(function () {
        $scope.model.totalItems = $scope.model.entities.length;
        $scope.model.totalPages = Math.ceil($scope.model.entities.length / $scope.model.numPerPage);
    });

});
