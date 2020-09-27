<?php
 session_start();
 $conn = mysql_connect("localhost", "root", "");
 if(!$conn) die("Error connecting");
 $db = mysql_select_db($_SESSION["gamename"], $conn);
 if(!$db) die("Error selecting db");
 
 $usr = $_POST["username"];
 $q1 = "select level, exp, pen
        from users
	    where username = '".$usr."'";
		
 $res = mysql_query($q1, $conn);
 if(mysql_num_rows($res) > 0)
 {
   $row = mysql_fetch_array($res);
   
   $q2 = "select count(*) as pos
          from users u1, users u2
          where u1.username = '".$usr."' 
		        and (u1.level < u2.level or (u1.level = u2.level and u1.exp < u2.exp) or (u1.level = u2.level and u1.exp = u2.exp and u1.pen < u2.pen))";
   $res2 = mysql_query($q2, $conn);
   
   $row2 = mysql_fetch_array($res2);
   
   echo "Username: ".$usr."<br>";
   echo "Position: ".($row2[0]+1)."<br>";
   echo "Level: ".$row["level"]."<br>";
   echo "Exp: ".$row["exp"]."<br>";
   echo "Pen: ".$row["pen"]."<br>";   
 }
 else
  die("Player doesn't exist");
?>