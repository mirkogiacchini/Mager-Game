<?php
 session_start();
 require_once "Mail-1.3.0\Mail-1.3.0\Mail.php";
 
 $conn = mysql_connect("localhost", "root", "");
 if(!$conn) die("Error connecting to DB");
 $db = mysql_select_db($_SESSION["gamename"], $conn);
 if(!$db) die("Error selecting db");
 
 $usr = $_POST["usr"];
 $email = $_POST["email"];
 
 $_SESSION["usr"] = $usr;
 
 $q = "select *
       from users 
	   where username = '".$usr."' and email = '".$email."'";
 $res = mysql_query($q, $conn);
 if(mysql_num_rows($res) > 0)
 {
   $len = 10;
   $cstrong = true;
   $_SESSION["random_code"] = bin2hex(openssl_random_pseudo_bytes($len, $cstrong));	 
   $msg = "Your code to change password: ".$_SESSION["random_code"];	 
   $from = "your-email-here"; #host email
   $to = $email;
   $subject = $_SESSION["gamename"]." - recover password";
   $headers = array('From' => $from, 'To' => $to, 'Subject' => $subject);
   
   $smtp = Mail::factory('smtp', array(
                'host' => 'ssl://smtp.gmail.com',
                'port' => '465',
                'auth' => true,
                'username' => 'your-email-here', #host email
                'password' => 'your-email-password'
    ));
	$mail = $smtp->send($to, $headers, $msg);
	if(PEAR::isError($mail)) 
     die("Error sending email");
    else
	 echo "We sent you a code, check your email and insert the code below<br>";
 }
 else
  die("Username or email are wrong!");
?>

<html>
 <head>
 </head>
 
 <body>
  <form action="resetpsw.php" method="POST">
   your code: <input type="text" name="code" required> </input> <br>
   <input type="submit" value="send"> </input>
  </form>
 </body>
</html>