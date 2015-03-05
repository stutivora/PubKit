App.CheckAuthRoute = Ember.Route.extend({
	beforeModel:function() {
		if (App.Session.isLoggedIn()) {
			this.transitionTo('/user');
		}
	}
});

App.LoginRequiredRoute = Ember.Route.extend({
	beforeModel:function() {
		if (!App.Session.isLoggedIn()) {
			this.transitionTo('/login');
		}
	}
});

App.SignupRoute = App.CheckAuthRoute.extend({
	model: function(){
		return App.User.create()
	},
	
	setupController : function(controller, model) {
		controller.set("user", model);
		controller.set("errorMessage", "");
	}
});

App.LoginRoute = App.CheckAuthRoute.extend({
	model: function(){
		return App.Login.create()
	},
	
	setupController : function(controller, model) {
		controller.set("login", model);
		controller.set("errorMessage", "");
	}
});

App.LogoutRoute = App.LoginRequiredRoute.extend({
	beforeModel: function(){
		App.Session.clearSession();
		
		var navController = this.controllerFor('navbar');
		navController.send('reloadNav');
		this.transitionTo('/');
	}
});

App.UserIndexRoute = App.LoginRequiredRoute.extend({
	renderTemplate: function() {
		this._super(this, arguments);
	    
	    var navController = this.controllerFor('navbar');
		navController.send('reloadNav');
	},
});

App.AppsNewRoute = App.LoginRequiredRoute.extend({
	model: function(){
		return App.Application.create()
	},
	
	setupController : function(controller, model) {
		controller.set("application", model);
		controller.set("errorMessage", "");
	}
});