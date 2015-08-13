<?php
function get_my_like($user_id) {
	$like_info = array ();
	
	// normally this info would be pulled from a database.
	// build JSON array.
	include "./image_test/dbconfig.php";
	$mysqli = new mysqli ( $dbhost, $dbusr, $dbpass, $dbname );
	if ($mysqli->connect_errno) {
		echo "Failed to connect to MySQL: (" . $mysqli->connect_errno . ") " . $mysqli->connect_error;
	}
	$sql_query = "SELECT posts.id, posts.user_id as p_user_id, posts.title, posts.upload_filename, posts.upload_date, posts.thumb_img_path FROM posts JOIN likes ON posts.id = likes.post_id WHERE likes.user_id = '$user_id'";
	
	if ($result = $mysqli->query ( $sql_query )) {
		if (count ( $result ) > 0) {
			
			while ( $row = $result->fetch_assoc () ) {
				
				
				array_push ( $like_info, array (
					"id" => $row ['id'],
					"title" => $row ['title'],
					"user_id" => $row ['p_user_id'],
					"filename" => $row ['upload_filename'],
					"date" => $row ['upload_date'],
					"img_path" => $row ['thumb_img_path']
				) );
				
				
			}
			// array_push($user_list, 'user_info');
			$image_list = array (
					'like_info' => $like_info 
			);
		} else {
			echo 'fail to get user info';
		}
	}
	
	$mysqli->close ();
	
	return $image_list;
}

$value = "An error has occurred";

if (isset ( $_POST ["user_id"] )) {
	$value = get_my_like ( $_POST ["user_id"] );
} else {
	$value = "Missing argument";
}

// return JSON array
exit ( json_encode ( $value ) );
?>