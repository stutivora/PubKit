App.SignupController = Ember.Controller.extend({
	
	actions : {
		createAccount : function() {
			this.set('errorMessage', null);
			
			var user = this.get("user");
			var validator = App.Validator;
			if (validator.isEmpty(user.email) || validator.isEmpty(user.fullName) || 
					validator.isEmpty(user.password) || validator.isEmpty(user.confirmPassword)) {
				this.set('errorMessage', 'Missing required input');
				return;
			}
			if (user.password !== user.confirmPassword) {
				this.set('errorMessage', 'Passwords doesn\'t match');
				return;
			}
			if (!App.Validator.validateEmail(user.email)) {
				this.set('errorMessage', 'Invalid Email');
				return;
			}
			var self = this;
			//ready to POST
			App.NetworkService.jsonPOST("/users", user, function(response, error) {
				if (response !== undefined || response != {} || response !== "") {
					if (response.error) {
						self.set('errorMessage', response.errorMessage);
					} else {
						self.transitionToRoute('login');
					}
				} else {
					self.set('errorMessage', 'Sorry, Error creating user');
				}
			});
		},

		cancel : function() {
			this.transitionToRoute('index');
		}
	}
});

App.LoginController = Ember.Controller.extend({
	actions : {
		login : function() {
			this.set('errorMessage', null);

			var login = this.get("login");
			if (App.Validator.isEmpty(login.email)
					|| App.Validator.isEmpty(login.password)) {
				this.set('errorMessage', 'Both email and password required');
				return;
			}
			if (!App.Validator.validateEmail(login.email)) {
				this.set('errorMessage', 'Invalid Email');
				return;
			}

			var self = this;
			// ready to LOG IN
			App.NetworkService.jsonPOST("/users/login", login, function(response, error) {
				if (response !== undefined && response != {} && response !== ""
						&& response !== "error") {
					if (response.error) {
						self.set('errorMessage', response.errorMessage);
					} else {
						App.Session.saveUserInfo(response);
						
						self.transitionToRoute('user');
					}
				} else {
					self.set('errorMessage',
							'Oops, Something went wrong. Please try again.');
				}
			});
		},

		cancel : function() {
			this.transitionToRoute('index');
		}
	}
});

App.AppsNewController = Ember.Controller.extend({
	actions : {
		registerApplication : function() {
			this.set('errorMessage', null);
			var newApp = this.get("application");
			
			if (App.Validator.isEmpty(newApp.applicationName)
					|| App.Validator.isEmpty(newApp.applicationDescription) 
					|| App.Validator.isEmpty(newApp.websiteLink)) {
				this.set('errorMessage', 'Missing required input');
				return;
			}
			newApp.userId = App.Session.userId;
			newApp.pricingPlan = "free";
			
			var self = this;
			App.NetworkService.jsonPOST("/applications", newApp, function(response, error) {
				if (response !== undefined && response != {} && response !== "" && response !== "error") {
					if (response.error) {
						self.set('errorMessage', response.errorMessage);
					} else {
						self.transitionToRoute('user');
					}
				} else {
					self.set('errorMessage', 'Oops, Something went wrong. Please try again.');
				}
			});
		},
		
		cancel : function() {
			this.transitionToRoute('user');
		}
	}
});

