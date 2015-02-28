App = Ember.Application.create();

App.ApplicationRoute = Ember.Route.extend({
	setupController: function(controller) {
	    controller.set('hasToken', App.Session.get('token'));
	}
});

App.ApplicationController = Ember.Controller.extend({
});

App.AuthenticatedRoute = Ember.Route.extend({
	beforeModel: function(transition) {
		if (!App.Session.get('token')) {
			this.redirectToLogin(transition);
		}
	},
	redirectToLogin: function(transition) {
		var loginController = this.controllerFor('login');
		loginController.set('attemptedTransition', transition);
		
		this.transitionTo('login');
	}
});

App.IndexRoute = Ember.Route.extend({
	setupController : function(controller) {
		//setup properties related to index controller!
		controller.set("test", "puran is awesome");
	}
});

App.IndexController = Ember.Controller.extend({
});

App.Session = Ember.Object.extend({
	user: {},
	token: localStorage.token,
	tokenChanged: function() {
		localStorage.token = this.get('token');
	}.observes('token')
}).create();

App.Validator = Ember.Object.extend({

	validateEmail:function(email) {
		var re = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
	    return re.test(email);
	},
	
	isEmpty:function(value) {
		return value === undefined || value === '';
	},
	
	isValid : function(value) {
		return value !== undefined && value.trim() != '';
	}
	
}).create();

App.NetworkService = Ember.Object.extend({

	jsonPOST : function(urlPath, jsonObject, callback) {
		var url = urlPath;
		if (App.Validator.isValid(App.Session.token)) {
			url = url +  "?access_token=" + App.Session.accessToken;
		}
		Ember.$.ajax({
		    type: 'POST',
		    url: url,
		    data: JSON.stringify (jsonObject),
		    contentType: "application/json",
		    dataType: 'json',
		    success: function(response) { 
		    	callback(response); 
		    },
		    error: function(XMLHttpRequest, textStatus, errorThrown) {
		        callback(textStatus, errorThrown);
		    }
		});
	},
	
	jsonGET : function(params, callback) {
		
	}
}).create();