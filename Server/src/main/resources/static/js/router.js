App.Router.map(function() {
	this.route('login');
	this.route('signup');
	this.route('logout');
	
	this.resource('user', function() {
		this.route('index', { path: '/' });
	    this.resource('apps', function() {
	    	this.route('new');
	    });
	});
});