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
	allTabs : function() {
		var tabs = Array();
		tabs.addObject(App.Tab.create({
			name:'Settings',
			active:true
		}));
		tabs.addObject(App.Tab.create({
			name:'Push',
			active:false
		}));
		tabs.addObject(App.Tab.create({
			name:'Logs',
			active:false
		}));
		
		return tabs;
	},
	
	model: function(params) {
		var userApp = Ember.Object.extend({
			tabs: this.allTabs(),
			application: {}
		}).create();
		
		var applicationId = params.app_id;
		var savedApps = App.Session.applications;
		if (savedApps != null && savedApps.length > 0) {
			var appCount = savedApps.length;
			for (var i = 0; i < appCount; i++) {
			    var application = App.Session.applications[i];
			    if (application.applicationId === applicationId) {
			    	userApp.set('application', application);
			    	return userApp;
			    }
			}
		} else {
			var self = this;
			return App.NetworkService.jsonGET("/applications/"+applicationId, function(response) {
				if (App.Validator.isValidResponse(response)) {
					if (response.application) {
						userApp.set('application', response.application);
						return userApp;
					}
				}
				self.handleError(response);
			});
		}
	},
	
	setupController : function(controller, model) {
		var appConfig = App.ApplicationConfig.create({
			androidGcmKey:"333333"
		});
		
		controller.set('userApp', model);
		controller.set('appConfig', appConfig)
	},
	
	actions: {
		invalidateModel: function() {
			Ember.Logger.log('Route is now refreshing...');
			this.refresh();
		}
	}
});