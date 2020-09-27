<?php
 session_start();
 
 if($_SESSION["random_code"] != $_POST["code"])
  die("Codes are different!");
?>

<html>
 <head> </head>
 <body>
  <form action="setpsw.php" method="POST">
   new password: <input type="password" name="psw" required> </input> <br>
   <input type="submit" value="reset psw"> </input>
  </form>
 </body>
</html>