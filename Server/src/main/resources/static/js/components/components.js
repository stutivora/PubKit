App.SpinButtonComponent = Ember.Component.extend({
    classNames: ['btn-component'],
    inProgress:false,
    actions:{
        showProgress:function(){
            if(!this.get('inProgress')){
                this.set('inProgress', true);
                this.sendAction('action');
            } 
        }
    }
});

App.FilePickerComponent = Ember.Component.extend({
    classNames: ['file-upload-container'],
    inputId:'',
    outputName:'',
    
    addClickHooks : function() {
    	
    	var inputId = '#'+this.get('inputId');
    	var outputName = '#'+this.get('outputName');
    	
    	Ember.$(inputId).change(function () {
    		var fileName = Ember.$(this).val().replace('C:\\fakepath\\', '');
    		Ember.$(outputName).html(fileName);
    	});
    }.on('didInsertElement'),
    
    actions:{
        showProgress:function(){
        	 this.sendAction('action');
        }
    }
});