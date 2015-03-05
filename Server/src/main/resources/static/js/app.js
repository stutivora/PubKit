App = Ember.Application.create();

App.Router.map(function() {
	this.route('login');
	this.route('signup');
	this.route('logout');
	
	this.resource('user', function() {
		this.route('index', { path: '/' });
	    this.resource('apps', function() {
	    	this.route('new');
	    	this.route('view');
	    });
	});
});

App.Validator = Ember.Object.extend({

	validateEmail:function(email) {
		var re = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
	    return re.test(email);
	},
	
	isEmpty:function(value) {
		return value === undefined || value === '';
	},
	
	isValid : function(value) {
		return value != undefined && value != null && value.trim() != '';
	},
	
	isValidResponse : function(response) {
		return (response !== undefined && response != {} && response !== ""
			&& response !== "error");
	}
}).create();

App.Session = Ember.Object.extend({
	userId: localStorage.userId,
	userName:localStorage.userName,
	accessToken: localStorage.accessToken,
	
	tokenChanged: function() {
		localStorage.accessToken = this.get('accessToken');
	}.observes('accessToken'),
	userIdChanged: function() {
		localStorage.userId = this.get('userId');
	}.observes('userId'),
	userNameChanged: function() {
		localStorage.userName = this.get('userName');
	}.observes('userName'),
	
	isLoggedIn : function() {
		return App.Validator.isValid(this.get('accessToken')) && App.Validator.isValid(this.get('userId'));
	},
	
	saveUserInfo : function(loginResponse) {
		this.set('accessToken', loginResponse.accessToken);
		this.set('userId', loginResponse.userId);
		this.set('userName', loginResponse.userName);
	},
	
	clearSession : function() {
		this.set('accessToken', '');
		this.set('userId', '');
		this.set('userName', '');
	}
	
}).create();

App.NetworkService = Ember.Object.extend({

	jsonPOST : function(urlPath, jsonObject, callback) {
		var url = urlPath;
		if (App.Validator.isValid(App.Session.accessToken)) {
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
	
	jsonGET : function(urlPath, callback) {
		var url = urlPath;
		if (App.Validator.isValid(App.Session.accessToken)) {
			url = url +  "?access_token=" + App.Session.accessToken;
		}
		return Ember.$.getJSON(url).then(function(response) {
			callback(response);
		});
	}
}).create();

App.ApplicationRoute = Ember.Route.extend({
});

App.ApplicationController = Ember.Controller.extend({
});

App.NavbarController = Ember.ArrayController.extend({
	init: function() {
	    this.set('hasToken', App.Session.isLoggedIn());
	},
	actions: {
		reloadNav: function(){
			this.set('hasToken', App.Session.isLoggedIn());
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