<?php
 session_start();
 $conn = mysql_connect("localhost", "root", "");
 if(!$conn) die("Error connecting");
 $db = mysql_select_db($_SESSION["gamename"], $conn);
 if(!$db) die("Error selecting db");
 
 $q = "update users
       set password = '".md5($_POST["psw"])."'
	   where username = '".$_SESSION["usr"]."'";
 $res = mysql_query($q, $conn);
 if(!$res)
  die("Error resetting password");
 else
  echo "Password resetted!";
?>