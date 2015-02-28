App.Router.map(function() {
	this.route('login');
	this.route('signup');
	this.route('user', {path: '/users/:user_id'});
});