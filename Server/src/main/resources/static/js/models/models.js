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

App.Application = Ember.Object.extend({
	applicationId : "",
    applicationName : "",
    applicationDescription : "",
    userId : "",
    applicationKey : "",
    applicationSecret : "",
    websiteLink : "",
    pricingPlan : "",
});

App.ApplicationConfig = Ember.Object.extend({
	applicationId:"",
	androidGCMKey: "",
    apnsDevCertFileId : "",
    apnsDevCertFileName : "",
    apnsDevCertPassword : "",
    apnsProdCertFileId : "",
    apnsProdCertFileName : "",
    apnsProdCertPassword : ""
});

App.Tab = Ember.Object.extend({
	name : "",
	active : false
});
