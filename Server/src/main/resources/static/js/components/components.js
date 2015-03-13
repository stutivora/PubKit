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