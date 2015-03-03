App.LoginRoute = App.AuthRoute.extend({
	model: function(){
		return App.Login.create()
	},
	
	setupController : function(controller, model) {
		controller.set("login", model);
		controller.set("errorMessage", "");
	}
});

App.LogoutRoute = Ember.Route.extend({
	beforeModel: function(){
		App.Session.set('token', '');
		var navController = this.controllerFor('navbar');
		navController.send('reloadNav');
		
		this.transitionTo('/');
	}
});