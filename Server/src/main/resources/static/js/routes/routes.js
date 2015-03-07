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
	},
	handleError : function(error) {
		if (error.status == 511 && error.statusText == 'Network Authentication Required') {
			App.Session.clearSession();
			this.transitionTo('login');
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
	model :function() {
		var self = this;
		return App.NetworkService.jsonGET("/users/"+App.Session.userId+"/applications/", function(response) {
			if (App.Validator.isValidResponse(response)) {
				if (response.applications) {
					App.Session.set('applications', response.applications);
					return self.applications;
				}
			}
			self.handleError(response);
		});
	},
	setupController : function(controller, model) {
		controller.set('applications', App.Session.get('applications'));
	}
});

App.AppsNewRoute = App.LoginRequiredRoute.extend({
	model: function(){
		return App.Application.create();
	},
	
	setupController : function(controller, model) {
		controller.set("application", model);
		controller.set("errorMessage", "");
	}
});

App.UserAppRoute = App.LoginRequiredRoute.extend({
	application : {},
	model: function(params) {
		var applicationId = params.app_id;
		var savedApps = App.Session.applications;
		if (savedApps != null && savedApps.length > 0) {
			var appCount = savedApps.length;
			for (var i = 0; i < appCount; i++) {
			    var application = App.Session.applications[i];
			    if (application.applicationId === applicationId) {
			    	this.set('application', application);
			    	return application;
			    }
			}
		} else {
			var self = this;
			return App.NetworkService.jsonGET("/applications/"+applicationId, function(response) {
				if (App.Validator.isValidResponse(response)) {
					if (response.application) {
						self.set('application', response.application);
						return response.application;
					}
				}
				self.handleError(response);
			});
		}
	},
	
	setupController : function(controller, model) {
		var detailTab = App.Tab.create();
		detailTab.name = 'Application Settings';
		detailTab.active = true;
		
		var pushTab = App.Tab.create();
		pushTab.name = 'Logs';
		pushTab.active = false;
		
		var tabs = Array();
		tabs.push(detailTab);
		tabs.push(pushTab);
		
		controller.set("tabs", tabs);
		controller.set("application", this.get('application'));
		detailTab.set('application', this.get('application'));
	}
});