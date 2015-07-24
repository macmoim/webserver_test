<?php
    include "dbconfig.php";
    
	// db 연결
	$mysqli = new mysqli($dbhost, $dbusr, $dbpass, $dbname);
	if ($mysqli->connect_errno) {
		echo "Failed to connect to MySQL: (" . $mysqli->connect_errno . ") " . $mysqli->connect_error;
	}
 
    $query = "SELECT id, image, title, filesize, img_type
                     FROM images ORDER BY 'id'";
 
    
	$result = $mysqli->query($query);
		  if ($mysqli->error) {
			echo "fail to store dberror = ".$mysqli->error;
		  }
 
    while($row = mysqli_fetch_array($result, MYSQLI_ASSOC)){
	/*echo "type:".$row['img_type'];
				header("Content-type:$row['img_type']");
				echo $row['image'];*/
				
		echo 'Title : '.$row['title'].'<br>';
		echo '<img src="data:'.$row['img_type'].';base64,'.base64_encode( $row['image'] ).'"/><br>'; // 이미지 뿌리기
               
			   
    }
 
    $mysqli->close();
?>