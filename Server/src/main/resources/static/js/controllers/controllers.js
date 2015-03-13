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

App.SignupController = Ember.Controller.extend({
	inProgress : false,
    buttonText : "Create Account",
    
	actions : {
		createAccount : function() {
			this.set('errorMessage', null);
			
			var user = this.get("user");

			var validationError = false;
			var validator = App.Validator;
			var errorMessage = '';
			
			if (validator.isEmpty(user.email) || validator.isEmpty(user.fullName) || 
					validator.isEmpty(user.password) || validator.isEmpty(user.confirmPassword)) {
				errorMessage = 'Missing required input';
				validationError = true;
			}
			if (!validationError && user.password !== user.confirmPassword) {
				errorMessage = 'Passwords doesn\'t match';
				validationError = true;
			}
			if (!validationError && !App.Validator.validateEmail(user.email)) {
				errorMessage = 'Invalid Email';
				validationError = true;
			}
			
			var self = this;
			if (validationError) {
				this.set('errorMessage', errorMessage);
				
				//hack to do the refresh after this event loop!
				Ember.run.later(function(){
	                self.set('inProgress', false);
	            }, 1);
				
				return;
			}
			
			//ready to POST
			App.NetworkService.jsonPOST("/users", user, function(response, error) {
				if (App.Validator.isValidResponse(response)) {
					if (response.error) {
						self.set('errorMessage', response.errorMessage);
					} else {
						self.transitionToRoute('login');
					}
				} else {
					self.set('errorMessage', 'Sorry, Error creating user');
				}
				Ember.run.later(function(){
	                self.set('inProgress', false);
	            }, 1);
			});
		},

		cancel : function() {
			this.transitionToRoute('index');
		}
	}
});

App.LoginController = Ember.Controller.extend({
	inProgress : false,
    buttonText : "Log In",
    
	actions : {
		login : function() {
			this.set('errorMessage', null);

			var login = this.get("login");
			
			var validationError = false;
			var errorMessage = '';
			if (App.Validator.isEmpty(login.email)
					|| App.Validator.isEmpty(login.password)) {
				errorMessage ='Both email and password required';
				validationError = true;
			}
			if (!validationError && !App.Validator.validateEmail(login.email)) {
				errorMessage = 'Invalid Email';
				validationError = true;
			}
			
			var self = this;
			if (validationError) {
				Ember.run.later(function(){
	                self.set('inProgress', false);
	            }, 1);
				
				return;
			}
			
			// ready to LOG IN
			App.NetworkService.jsonPOST("/users/login", login, function(response, error) {
				if (App.Validator.isValidResponse(response)) {
					if (response.error) {
						self.set('errorMessage', response.errorMessage);
					} else {
						App.Session.saveUserInfo(response);
						
						self.transitionToRoute('user');
					}
				} else {
					self.set('errorMessage', 'Oops, Something went wrong. Please try again.');
				}
				Ember.run.later(function(){
	                self.set('inProgress', false);
	            }, 1);
			});
		},

		cancel : function() {
			this.transitionToRoute('index');
		}
	}
});

App.AppsNewController = Ember.Controller.extend({
	inProgress : false,
    buttonText : "Register",
    
	actions : {
		registerApplication : function() {
			this.set('errorMessage', null);
			var newApp = this.get("application");
			
			if (App.Validator.isEmpty(newApp.applicationName)
					|| App.Validator.isEmpty(newApp.applicationDescription) 
					|| App.Validator.isEmpty(newApp.websiteLink)) {
				this.set('errorMessage', 'Missing required input');
				Ember.run.later(function(){
	                self.set('inProgress', false);
	            }, 1);
				
				return;
			}
			newApp.userId = App.Session.userId;
			newApp.pricingPlan = "free";
			
			var self = this;
			App.NetworkService.jsonPOST("/applications", newApp, function(response, error) {
				if (App.Validator.isValidResponse(response)) {
					if (response.error) {
						self.set('errorMessage', response.errorMessage);
					} else {
						self.transitionToRoute('user');
					}
				} else {
					self.set('errorMessage', 'Oops, Something went wrong. Please try again.');
				}
				Ember.run.later(function(){
	                self.set('inProgress', false);
	            }, 1);
			});
		},
		
		cancel : function() {
			this.transitionToRoute('user');
		}
	}
});

App.UserAppController = Ember.Controller.extend({

	updateTab : function(selectedTab) {
		var userApp = this.get('userApp');
		var tabs = userApp.tabs;
		for (index = 0; index < tabs.length; ++index) {
		    var tab = tabs[index];
		    if (tab.name == selectedTab) {
				tab.set('active', true);
			} else {
				tab.set('active', false);
			}
		}
		userApp.set('tabs', tabs);
	},
	
	updateApnsDevCertFile : function(certFile) {
		var userApp = this.get('userApp');
		var application = userApp.get('application');
		application.set('apnsDevCertFile', "Puran_Singh_SARKI");
		userApp.set('application', application);
		
		this.set('userApp', userApp);
	},
	
	actions : {
		tabAction: function (tabName) {
			this.updateTab(tabName);
		},
		
		updateAppConfig : function() {
			if (apnsDevCertFile.files[0] == undefined || apnsDevCertFile.files[0] == null) {
				return;
			}
			var uploadData = new FormData();
			uploadData.append("file", apnsDevCertFile.files[0]);
			uploadData.append("applicationId", this.userApp.application.applicationId);
			uploadData.append("fileType", "img");
			
//			App.NetworkService.uploadFileData("/upload_cert", uploadData, function(response) {
//				if (App.Validator.isValidResponse(response)) {
//					if (response.error) {
//						self.set('apnsDevCertUploadError', response.errorMessage);
//					} else {
//						self.transitionToRoute('user');
//					}
//				}
//			});
		}
	}
});



