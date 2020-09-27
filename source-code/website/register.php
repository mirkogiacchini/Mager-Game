<?php
 session_start();
 $conn = mysql_connect("localhost", "root", "");
 if(!$conn) die("Error connecting to db");
 if(!mysql_select_db($_SESSION["gamename"], $conn)) die("Error selecting db");

 $usr = $_POST["usr"];
 $psw = md5($_POST["psw"]);
 $email = $_POST["email"];
 
 $qr = "insert into users values('".$usr."', 
                                 '".$psw."',
								 '".$email."',
								 1, 0, 0)";
 
 $res = mysql_query($qr, $conn);
 if(!$res)
  echo "Error registering!";
 else
  echo "Registered successfully";
?>