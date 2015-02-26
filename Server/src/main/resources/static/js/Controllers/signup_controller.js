App.SignupController = Ember.Controller.extend({
	actions : {
		createAccount : function() {
			this.set('errorMessage', null);
			
			var user = this.get("user");
			if (user.email === '' || user.fullName === '' || user.password === '' || user.confirmPassword === '') {
				this.set('errorMessage', 'Missing required input');
				return;
			}
			if (user.password !== user.confirmPassword) {
				this.set('errorMessage', 'Passwords doesn\'t match');
				return;
			}
		},

		cancel : function() {
			this.transitionTo('index');
		}
	}
});
