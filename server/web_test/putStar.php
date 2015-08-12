<?php
function saveStar() {
	include "./image_test/dbconfig.php";
	$debug_msg = "not work";
	$mysqli = new mysqli ( $dbhost, $dbusr, $dbpass, $dbname );
	if ($mysqli->connect_errno) {
		echo "Failed to connect to MySQL: (" . $mysqli->connect_errno . ") " . $mysqli->connect_error;
	}
	
	$create_table = "CREATE TABLE if not exists stars (
					id int auto_increment,
					user_id varchar(30),
					star int,
					post_id int,
					post_user_id varchar(30),
					PRIMARY KEY (id)
					);";
	
	$mysqli->query ( $create_table );
	
	$query_update = sprintf ( "UPDATE stars
				SET star = '%s' WHERE user_id = '%s' AND post_id = '%s' AND post_user_id = '%s'",
			$_POST["star"], $_POST["user_id"],$_POST["post_id"],$_POST["post_user_id"]);
	
	$mysqli->query ( $query_update );
	// check update success
	if ($mysqli->affected_rows == 0) {
		$query_insert = sprintf ( "INSERT INTO stars
			(user_id, star, post_id, post_user_id)
			VALUES ('%s','%s', '%s','%s')", 
				$_POST["user_id"],$_POST["star"],$_POST["post_id"],$_POST["post_user_id"]);
		
		$mysqli->query ( $query_insert );
		
		 $debug_msg = "insert star success";
		
		if ($mysqli->error) {
			echo "Failed to insert like db: (" . $mysqli->error . ") ";
		}
		
	} else {
	}
	
	$insert_id = $mysqli->insert_id;
	
	$like_saving_info = array (
			"id" => $insert_id
	);
	$mysqli->close ();
	return $like_saving_info;//$debug_msg;
}

$value = "An error has occurred";
// echo 'start: ';
if (isset ( $_POST ["post_id"] ) && isset ( $_POST ["post_user_id"] )
		&& isset ( $_POST ["user_id"] ) && isset ( $_POST ["star"] )) {
	$value = saveStar();
	
} else {
	$value = "Missing argument";
}
// return JSON array
exit ( json_encode ( $value ) );
?>