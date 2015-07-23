<?php
function get_post($thumb_path) {
	$post_info = array ();
	
	// normally this info would be pulled from a database.
	// build JSON array.
	
	$mysqli = new mysqli ( "localhost", "root", "111111", 'db_chat_member_test' );
	if ($mysqli->connect_errno) {
		echo "Failed to connect to MySQL: (" . $mysqli->connect_errno . ") " . $mysqli->connect_error;
	}
	$sql_query = "SELECT user_id, title, upload_filename, db_filename, filepath, upload_date
	                   FROM posts WHERE thumb_img_path = '$thumb_path'";
	if ($result = $mysqli->query ( $sql_query )) {
		$row = $result->fetch_array ();
		if (isset ( $row ['db_filename'] )) {
			$post_info = array (
					"user_id" => $row ['user_id'],
					"title" => $row ['title'],
					"upload_filename" => $row ['upload_filename'],
					"db_filename" => $row ['db_filename'],
					"filepath" => $row ['filepath'],
					"upload_date" => $row ['upload_date'] 
			);
		} else {
			echo 'fail to get user info';
		}
	}
	
	$mysqli->close ();
	
	return $post_info;
}

$possible_url = array (
		"get_post" 
);

$value = "An error has occurred";

if (isset ( $_POST ["thumb_path"] )) {
	$value = get_post ( $_POST ["thumb_path"] );
} else {
	$value = "Missing argument";
}

// return JSON array
exit ( json_encode ( $value ) );
?>