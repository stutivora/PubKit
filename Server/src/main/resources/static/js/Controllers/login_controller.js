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
			App.NetworkService.jsonPOST("/users/login", {
				"email" : login.email,
				"password" : login.password
			}, function(response, error) {
				if (response !== undefined && response != {} && response !== ""
						&& response !== "error") {
					if (response.error) {
						self.set('errorMessage', response.errorMessage);
					} else {
						App.Session.set('token', response.token);
						this.transitionTo("/users/" + response.userId);
					}
				} else {
					self.set('errorMessage',
							'Oops, Something went wrong. Please try again.');
				}
			});
		},

		cancel : function() {
			this.transitionTo('index');
		}
	}
});
