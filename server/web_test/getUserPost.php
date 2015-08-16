<?php
function get_post($user_id) {
	$post_info = array ();
	
	// normally this info would be pulled from a database.
	// build JSON array.
	
	$mysqli = new mysqli ( "localhost", "root", "111111", 'db_chat_member_test' );
	if ($mysqli->connect_errno) {
		echo "Failed to connect to MySQL: (" . $mysqli->connect_errno . ") " . $mysqli->connect_error;
	}
	$sql_query = "SELECT id,user_id, title, upload_filename, db_filename, filepath, upload_date, thumb_img_path
	                   FROM posts WHERE user_id = '$user_id'";
	if ($result = $mysqli->query ( $sql_query )) {
	
		if (count ( $result ) > 0) {
			
			while ( $row = $result->fetch_assoc () ) {
				
				array_push ( $post_info, array (
					"id" => $row ['id'],
					"title" => $row ['title'],
					"user_id" => $row ['user_id'],
					"filename" => $row ['upload_filename'],
					"date" => $row ['upload_date'],
					"img_path" => $row ['thumb_img_path']
				) );
			}

			$image_list = array (
					'my_post' => $post_info 
			);
		}else{
			echo 'fail to get user info';
		}
	}

	$mysqli->close ();
	
	return $image_list;
}

$value = "An error has occurred";

if (isset ( $_POST ["user_id"] )) {
	$value = get_post ( $_POST ["user_id"] );
} else {
	$value = "Missing argument";
}

// return JSON array
exit ( json_encode ( $value ) );
?>