<?php 
	if(isset($_GET['trigger']) && $_GET['trigger'] == 1) {
		error_reporting(E_ALL);
		exec('gpio write 7 0');
		usleep(1000000);
		exec('gpio write 7 1');
	}
?>
<!DOCTYPE html>
<html>
	<head>
		<title>VarnerNet Opener</title>
		<link rel="apple-touch-icon" href="apple-touch-icon-iphone.png" />
		<link rel="apple-touch-icon" sizes="72x72" href="apple-touch-icon-ipad.png" />
		<link rel="apple-touch-icon" sizes="114x114" href="apple-touch-icon-iphone-retina-display.png" />		
		<link rel="stylesheet" href="/css/style.css" type="text/css">
		<meta name="apple-mobile-web-app-capable" content="yes">	
		<script type="text/javascript" src="/js/jquery-1.10.2.min.js"></script>    
		<script type="text/javascript" src="/js/script.js"></script>    

	</head>
	<body>
		<div class='awrap'>
      <a href='/?trigger=1'></a>
		</div>
	</body>
</html>
