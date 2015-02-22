App = Ember.Application.create();

App.Router.map(function() {
	this.route('login');
	this.route('signup');
});

App.IndexRoute = Ember.Route.extend({
	model : function() {
		return [ 'red', 'yellow', 'blue' ];
	},
	setupController : function(controller) {
		// `controller` is the instance of IndexController
		controller.set('title',
				"Roquito-Application platform for mobile developers");
	}
});

App.IndexController = Ember.Controller.extend({
	appName : 'Roquito'
});

App.SignupRoute = Ember.Route.extend({});

App.SignupController = Ember.Controller.extend({
	actions : {
		submit : function() {
			alert('voila');
		},

		cancel : function() {
		}
	}
});
