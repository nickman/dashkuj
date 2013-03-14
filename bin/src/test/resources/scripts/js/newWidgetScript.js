var widget = this.widget;

this.on('load', function(data){
  // Nothing to do
});

this.on('transmission', function(data){
  var salesNumber = widget.find('#salesNumber');
  salesNumber.text('$'+data.revenue).hide().fadeIn();
});
