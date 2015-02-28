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
						self.transitionTo('login');
					}
				} else {
					self.set('errorMessage', 'Sorry, Error creating user');
				}
			});
		},

		cancel : function() {
			this.transitionTo('index');
		}
	}
});
