$(document).ready(function() {
	$('a').click(function(e) {
		e.preventDefault();
		$.get("/?trigger=1");
	});
});
