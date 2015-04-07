var main = function() {
  $('.article').click(function() {
    $('.article').removeClass('current');//previous selected tab lai remove garcha 
    $('.description').hide();// hide the description of the previous selected tab

    $(this).addClass('current'); //click garda select hune tab..
    $(this).children('.description').show();// click garda select hune tab ko description..
  });

  }

$(document).ready(main);