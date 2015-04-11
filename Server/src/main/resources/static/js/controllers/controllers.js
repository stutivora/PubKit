App.ApplicationController = Ember.Controller.extend({
	init: function() {
	    this.set('hasToken', App.Session.isLoggedIn());
	}
});

App.NavbarController = Ember.ArrayController.extend({
	init: function() {
	    this.set('hasToken', App.Session.isLoggedIn());
	},
	actions: {
		reloadNav: function(){
			this.set('hasToken', App.Session.isLoggedIn());
		}
	 }
});

App.SignupController = Ember.Controller.extend({
	inProgress : false,
    buttonText : "Create Account",
    
	actions : {
		createAccount : function() {
			this.set('errorMessage', null);
			
			var user = this.get("user");

			var validationError = false;
			var validator = App.Validator;
			var errorMessage = '';
			
			if (validator.isEmpty(user.email) || validator.isEmpty(user.fullName) || 
					validator.isEmpty(user.password) || validator.isEmpty(user.confirmPassword)) {
				errorMessage = 'Missing required input';
				validationError = true;
			}
			if (!validationError && user.password !== user.confirmPassword) {
				errorMessage = 'Passwords doesn\'t match';
				validationError = true;
			}
			if (!validationError && !App.Validator.validateEmail(user.email)) {
				errorMessage = 'Invalid Email';
				validationError = true;
			}
			
			var self = this;
			if (validationError) {
				this.set('errorMessage', errorMessage);
				
				//hack to do the refresh after this event loop!
				Ember.run.later(function(){
	                self.set('inProgress', false);
	            }, 1);
				
				return;
			}
			
			//ready to POST
			App.NetworkService.jsonPOST("/users", user, function(response, error) {
				if (App.Validator.isValidResponse(response)) {
					if (response.error) {
						self.set('errorMessage', response.errorMessage);
					} else {
						self.transitionToRoute('login');
					}
				} else {
					self.set('errorMessage', 'Sorry, Error creating user');
				}
				Ember.run.later(function(){
	                self.set('inProgress', false);
	            }, 1);
			});
		},

		cancel : function() {
			this.transitionToRoute('index');
		}
	}
});

App.LoginController = Ember.Controller.extend({
	inProgress : false,
    buttonText : "Log In",
    
	actions : {
		login : function() {
			this.set('errorMessage', null);

			var login = this.get("login");
			
			var validationError = false;
			var errorMessage = '';
			if (App.Validator.isEmpty(login.email)
					|| App.Validator.isEmpty(login.password)) {
				errorMessage ='Both email and password required';
				validationError = true;
			}
			if (!validationError && !App.Validator.validateEmail(login.email)) {
				errorMessage = 'Invalid Email';
				validationError = true;
			}
			var self = this;
			if (validationError) {
				this.set('errorMessage', errorMessage);
				}
			var self = this;
			if (validationError) {
				Ember.run.later(function(){
	                self.set('inProgress', false);
	            }, 1);
				
				return;
			}
			
			// ready to LOG IN
			App.NetworkService.jsonPOST("/users/login", login, function(response, error) {
				if (App.Validator.isValidResponse(response)) {
					if (response.error) {
						self.set('errorMessage', response.errorMessage);
					} else {
						App.Session.saveUserInfo(response);
						
						self.transitionToRoute('user');
					}
				} else {
					self.set('errorMessage', 'Oops, Something went wrong. Please try again.');
				}
				Ember.run.later(function(){
	                self.set('inProgress', false);
	            }, 1);
			});
		},

		cancel : function() {
			this.transitionToRoute('index');
		}
	}
});

App.AppsNewController = Ember.Controller.extend({
	inProgress : false,
    buttonText : "Register",
    
	actions : {
		registerApplication : function() {
			this.set('errorMessage', null);
			var newApp = this.get("application");
			
			if (App.Validator.isEmpty(newApp.applicationName)
					|| App.Validator.isEmpty(newApp.applicationDescription) 
					|| App.Validator.isEmpty(newApp.websiteLink)) {
				this.set('errorMessage', 'Missing required input');
				Ember.run.later(function(){
	                self.set('inProgress', false);
	            }, 1);
				
				return;
			}
			newApp.userId = App.Session.userId;
			newApp.pricingPlan = "free";
			
			var self = this;
			App.NetworkService.jsonPOST("/applications", newApp, function(response, error) {
				if (App.Validator.isValidResponse(response)) {
					if (response.error) {
						self.set('errorMessage', response.errorMessage);
					} else {
						self.transitionToRoute('user');
					}
				} else {
					self.set('errorMessage', 'Oops, Something went wrong. Please try again.');
				}
				Ember.run.later(function(){
	                self.set('inProgress', false);
	            }, 1);
			});
		},
		
		cancel : function() {
			this.transitionToRoute('user');
		}
	}
});

App.UserAppController = Ember.Controller.extend({
	updateInProgress : false,
	updateButtonText: 'Update Settings',
	keyValueCount : 1,
	
	updateTab : function(selectedTab) {
		var userApp = this.get('userApp');
		var tabs = userApp.tabs;
		for (index = 0; index < tabs.length; ++index) {
		    var tab = tabs[index];
		    var tabId = '#'+tab.name;
		    if (tab.name == selectedTab) {
				tab.set('active', true);
				Ember.$(tabId).removeClass('hidden');
			} else {
				tab.set('active', false);
				Ember.$(tabId).addClass('hidden');
			}
		    if (selectedTab == 'Settings') {
		    	tab.set('updateButtonText', 'Update Settings');
				tab.set('updateInProgress', false);
		    }
		}
		userApp.set('tabs', tabs);
	},
	
	uploadCert : function(fileObject) {
		var self = this;
		return new Promise(function(resolve, reject) {
			if (fileObject == undefined || fileObject.files[0] == undefined || fileObject.files[0] == null ||
					apnsDevCertFile.files[0] == '') {
				resolve('');
				return;
			}
			var uploadData = new FormData();

			uploadData.append("file", fileObject.files[0]);
			uploadData.append("applicationId", self.userApp.application.applicationId);
			uploadData.append("fileType", "cert");
			
			App.NetworkService.uploadFileData("/upload_cert", uploadData, function(response) {
				if (App.Validator.isValidResponse(response)) {
					if (response.error) {
						reject('error');
					} else {
						resolve(response.uploadId);
					}
				}
			});
		});
	},
	
	handleUpdateConfigError : function() {
		alert('error saving');
	},
	
	saveAppConfig : function(updatedConfig) {
		var self = this;
		App.NetworkService.jsonPOST("/applications/config", updatedConfig, function(response, error) {
			if (App.Validator.isValidResponse(response)) {
				if (response.error) {
					self.set('errorMessage', response.errorMessage);
				} else {
					self.updateTab('Settings');
					Ember.$("#successAlert").removeClass('hidden');
					Ember.run.later(function(){
						Ember.$("#successAlert").addClass('hidden');
		            }, 2000);
				}
			} else {
				self.set('errorMessage', 'Oops, Something went wrong. Please try again.');
			}
		});
	},
	
	actions : {
		tabAction: function (tabName) {
			this.updateTab(tabName);
		},
		
		updateAppConfig : function() {
			var updatedConfig = this.get('appConfig');
			
			var self = this;
			var devFileUploadResult = this.uploadCert(apnsDevCertFile);
			devFileUploadResult.then(function(devCertFileId) {
				if (devCertFileId != '' && devCertFileId != 'error') {
					updatedConfig.apnsDevCertFileId = devCertFileId;
					updatedConfig.set('apnsDevCertFileName', apnsDevCertFile.files[0].name);
				} 
				var prodFileUploadResult = self.uploadCert(apnsProdCertFile);
				prodFileUploadResult.then(function(prodCertFileId) {
					if (prodCertFileId != '') {
						updatedConfig.apnsProdCertFileId = prodCertFileId;
						updatedConfig.set('apnsProdCertFileName', apnsProdCertFile.files[0].name);
					}
					//now trigger save config!!
					self.saveAppConfig(updatedConfig);
				}, function(reason) {
					self.handleUpdateConfigError();
				});
			}, function(reason) {
				self.handleUpdateConfigError();
			});
		},
		
		moreKeyValueFields: function() {
			this.keyValueCount = this.keyValueCount + 1;
			var key = 'key'+this.keyValueCount;
			var value = 'value'+this.keyValueCount;
			
		    $("#apnsForm").append(
		    		'<div class="row">'+
						'<h1></h1>'+
					'</div>'+
		    		'<div class="row">'+
						'<div class="col-xs-2"></div>'+
						'<div class="col-md-2"><h4 class="app-key-text pull-right">Key '+this.keyValueCount+' </h4></div>'+
  						'<div class="col-xs-6"><input id="'+key+'" class="ember-view ember-text-field form-control pull-left app-settings-input" placeholder="" type="text"></div>'+
					'</div>'+
  					'<div class="row">'+
  					  '<h1></h1>'+
  					'</div>'+
  					'<div class="row">'+
  						'<div class="col-xs-2"></div>'+
  						'<div class="col-md-2"><h4 class="app-key-text pull-right">Value '+this.keyValueCount+'</h4></div>'+
						'<div class="col-xs-4"><input id="'+value+'" class="ember-view ember-text-field form-control pull-left app-settings-input" placeholder="" type="text"></div>'+
					'</div>');
		}
	}
});



