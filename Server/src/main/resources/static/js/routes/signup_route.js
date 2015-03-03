App.SignupRoute = App.AuthRoute.extend({
	beforeModel:function() {
		if (App.Session.isLoggedIn()) {
			this.transitionTo('/user');
		}
	},
	model: function(){
		return App.User.create()
	},
	
	setupController : function(controller, model) {
		controller.set("user", model);
		controller.set("errorMessage", "");
	}
});