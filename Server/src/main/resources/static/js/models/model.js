App.User = Ember.Object.extend({
    userId : "",
    email : "",
    password : "",
    confirmPassword:"",
    fullName : "",
    company : "",
    profilePicUrl : "",
    
    save: function () {
    	this.confirmPassword = "";
        $.post({
          url: "/users",
          data: JSON.stringify( this.toArray() ),
          success: function ( data ) {
        	  alert(data)
            // your data should already be rendered with latest changes
            // however, you might want to change status from something to "saved" etc.
          }
        });
    }
});

