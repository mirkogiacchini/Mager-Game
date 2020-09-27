<?php
 session_start();
 
 function getNeededExp($lv)
 {  
   if($lv > 90)
	$l = 1 + sin(($lv - 90) * 3.14 / 180);
   else
	$l = sin($lv * 3.14 / 180); //all'aumentare del livello aumenta il fattore 'l'
   return round($l * 1000);
 }
 
 $conn = mysql_connect("localhost", "root", "");
 if(!$conn) die("Error connecting");
 if(!mysql_select_db($_SESSION["gamename"], $conn)) die("Error selecting db");
 
 $usr = $_POST["usr"];
 $psw = $_POST["psw"]; 
 $_SESSION["usr"] = $usr;

 $q = "select * from users where username = '".$usr."' and password = '".md5($psw)."'";
 $res = mysql_query($q, $conn);
 if(mysql_num_rows($res) == 0)
  die("Username or password are wrong"); 

 $record = mysql_fetch_array($res);

 echo "<table border=1>
        <tr>
		 <th> Player </th>
		 <th> Level </th>
		 <th> Exp </th>
		 <th> Pen </th>
		</tr>
		<tr>
		 <td> ".$record["username"]." </td>
		 <td> ".$record["level"]." </td>
		 <td> ".$record["exp"]."/".getNeededExp($record["level"])." </td>
		 <td> ".$record["pen"]." </td>
		</tr>
       </table>
	   <br> <br>";
 
 $q = "select distinct i.description
       from items i, pl_obj_relations pi
	   where i.id = pi.obj and pi.player = '".$usr."'";
 $res = mysql_query($q, $conn);
 echo "<table border=1>
         <tr>
		  <th> Item Description </th>
		 </tr>";
 while($data = mysql_fetch_array($res)) 
 {
   echo "<tr>
         <td> ".$data[0]."</td>	 
        </tr>";
 }
 echo "</table> <br> <br>";
?>

<html>
 <head>
 </head>
 
 <body>
  <fieldset>
   <legend> Change data </legend>
   <form action="changedata.php" method="POST">
    insert new password: <input type="password" name="psw"> </input> <br>
    insert new email: <input type="email" name="email" placeholder=<?php echo "'current: ".$record["email"]."'"; ?>> </input> <br>
    <input type="submit" value="change data!"> </input>
    <input type="reset" value="clean"> </input>
   </form>
  </fieldset>
 </body>
</html>