<?php
 session_start();
 
 $conn = mysql_connect("localhost", "root", "");
 if(!$conn) die("Error connecting");
 $db = mysql_select_db($_SESSION["gamename"], $conn);
 if(!$db) die("Error selecting db");
 
 $psw = $_POST["psw"];
 $email = $_POST["email"];
 if($psw != "")
 {
   $q = "update users
         set password = '".md5($psw)."'
		 where username = '".$_SESSION["usr"]."'"; 
   $res = mysql_query($q, $conn);
   if(!$res)
    echo "Error changing password<br>";
   else
	echo "Password changed successfully<br>";
 }
 
 if($email != "")
 {
   $q = "update users
         set email = '".$email."'
		 where username = '".$_SESSION["usr"]."'"; 
   $res = mysql_query($q, $conn);
   if(!$res)
    echo "Error changing email<br>";
   else
	echo "Email changed successfully<br>";	 
 }
?>