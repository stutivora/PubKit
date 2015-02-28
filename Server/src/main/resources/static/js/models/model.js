App.User = Ember.Object.extend({
    userId : "",
    email : "",
    password : "",
    confirmPassword:"",
    fullName : "",
    company : "",
    profilePicUrl : "",
});
App.Login = Ember.Object.extend({
    email : "",
    password:"",
});
