App.UserIndexRoute = Ember.Route.extend({
	renderTemplate: function() {
		this._super(this, arguments);
	    
	    var navController = this.controllerFor('navbar');
		navController.send('reloadNav');
	},
});
