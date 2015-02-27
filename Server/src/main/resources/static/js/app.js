App = Ember.Application.create();

App.IndexRoute = Ember.Route.extend({
	model : function() {
		return [ 'red', 'yellow', 'blue' ];
	},
	setupController : function(controller) {
		controller.set('title', "Roquito-Application platform for mobile developers");
	}
});

App.IndexController = Ember.Controller.extend({
	appName : 'Roquito'
});

