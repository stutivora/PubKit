App = Ember.Application.create();

App.Router.map(function () {
    this.route('login');
    this.route('signup');
});

App.IndexRoute = Ember.Route.extend({
    model: function () {
        return ['red', 'yellow', 'blue'];
    }
});

