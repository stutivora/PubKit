App.AppsNewController = Ember.Controller.extend({
	actions : {
		registerApplication : function() {
			alert('Puran singh rocks');
		},

		cancel : function() {
			this.transitionTo('user');
		}
	}
});