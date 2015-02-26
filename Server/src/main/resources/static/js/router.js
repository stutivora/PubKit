App.Router.map(function() {
	this.route('login');
	 //this.route('signup');
	 this.route('signup', { path: '/signup' });
});




App.SignupRoute = Ember.Route.extend({
	model: function(){
		return App.User.create()
	},
	
	setupController : function(controller, model) {
		controller.set("user", model);
		controller.set("errorMessage", "");
	}
});