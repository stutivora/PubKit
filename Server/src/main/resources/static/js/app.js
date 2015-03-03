App = Ember.Application.create();


App.Session = Ember.Object.extend({
	userId: "",
	userName:"",
	token: localStorage.token,
	
	tokenChanged: function() {
		localStorage.token = this.get('token');
	}.observes('token'),
	
	isLoggedIn : function() {
		return (this.get('token') != undefined && this.get('token') != '' && this.get('token') != null);
	}
	
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

App.AuthRoute = Ember.Route.extend({
	beforeModel:function() {
		if (App.Session.isLoggedIn()) {
			this.transitionTo('/user');
		}
	}
});

App.ApplicationRoute = Ember.Route.extend({
});

App.ApplicationController = Ember.Controller.extend({
});

App.NavbarController = Ember.ArrayController.extend({
	init: function() {
	    this.set('hasToken', (App.Session.get('token') != '' && App.Session.get('token') != null));
	},
	actions: {
		reloadNav: function(){
			this.set('hasToken', (App.Session.get('token') != '' && App.Session.get('token') != null));
		}
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