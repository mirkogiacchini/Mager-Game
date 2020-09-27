<?php
 session_start();
 $_SESSION["gamename"] = "Mager";
 
 $conn = mysql_connect("localhost", "root", "");
 if(!$conn) die("Error connecting");
 $db = mysql_select_db($_SESSION["gamename"], $conn);
 if(!$db) die("Error selecting db");
?>
	
<html>
 <head>
 </head>
 
 <body>
 
  <form action="login.php" method="post">
   <fieldset>
    <legend> Login </legend>
    username: <input type="text" name="usr" required> </input> <br>
    password: <input type="password" name="psw" required> </input> <br>
    <input type="submit" value="send"> </input>
    <input type="reset" value="reset"> </input>
   </fieldset>  
  </form>
  
  <form action="register.php" method="post">
   <fieldset>
    <legend> Register </legend>
    username: <input type="text" name="usr" required> </input> <br>
    password: <input type="password" name="psw" required> </input> <br>
	email: <input type="email" name="email"> </input> <br>
    <input type="submit" value="send"> </input>
    <input type="reset" value="reset"> </input>
   </fieldset>  
  </form>
  
  <form action="recover.php" method="post">
   <fieldset>
    <legend> Password lost? </legend>
    username: <input type="text" name="usr" required> </input> <br>
	email: <input type="email" name="email" required> </input> <br>
    <input type="submit" value="send"> </input>
    <input type="reset" value="reset"> </input>
   </fieldset>  
  </form>
  
  <br> <br>
  
  <table border="1">
   <tr>
    <th> Position </th>
	<th> Player </th>
	<th> Level </th>
	<th> Exp </th>
	<th> Pen </th>
   </tr>
   
  <?php
   $q = "select username, level, exp, pen
	     from users
		 order by level desc, exp desc, pen desc";
   $res = mysql_query($q, $conn);
   
   $lprev = -1;
   $eprev = -1;
   $pprev = -1;
   $pos = 0;
   for($i=0; $i<min(10, mysql_num_rows($res)); $i++)
   {
	 $row = mysql_fetch_array($res);  
	 if($row["level"] != $lprev || $row["exp"] != $eprev || $row["pen"] != $pprev)
      $pos = $i+1;
     
	 echo "<tr>";
      echo "<td> ".$pos."</td>
	        <td> ".$row["username"]."</td>
			<td> ".$row["level"]."</td>
			<td> ".$row["exp"]."</td>
			<td> ".$row["pen"]."</td>";
	 echo "</tr>";
	 
     $lprev = $row["level"];
     $eprev = $row["exp"];
     $pprev = $row["pen"];	 
   }
  ?>
  
  </table>
  
  <br> <br>
  
  <form action="searchleaderboard.php" method="post">
   search player on leaderboard: <input type="text" name="username" required> </input> <br>
   <input type="submit" value="search"> </input>
  </form>
  
 </body>
</html>