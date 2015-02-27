App.SignupRoute = Ember.Route.extend({
	
	model: function(){
		return App.User.create()
	},
	
	setupController : function(controller, model) {
		controller.set("user", model);
		controller.set("errorMessage", "");
	}
});