<?php
function getImageList() {
	$image_info = array ();
	
	// normally this info would be pulled from a database.
	// build JSON array.
	
	$mysqli = new mysqli ( "localhost", "root", "111111", 'db_chat_member_test' );
	if ($mysqli->connect_errno) {
		echo "Failed to connect to MySQL: (" . $mysqli->connect_errno . ") " . $mysqli->connect_error;
	}
	$sql_query = "SELECT id, user_id, upload_filename, upload_date, thumb_img_path
	                   FROM posts";
	if ($result = $mysqli->query ( $sql_query )) {
		$row = $result->fetch_array ();
		if (isset ( $row ['thumb_img_path'] )) {
			// echo $row['user_alias'];
			// echo $row['email'];
			$post_info = array ();

			
			$post_info = array ();
			while ( $row = $result->fetch_assoc () ) {
				// echo $row['user_id'];
				// echo $row['user_alias'];
				// echo $row['email'];
				
				array_push ( $post_info, array (
						"id" => $row ['id'],
						"user_id" => $row ['user_id'],
						"filename" => $row ['upload_filename'],
						"date" => $row ['upload_date'],
						"img_path" => $row ['thumb_img_path'] 
				) );
			}
			// array_push($user_list, 'user_info');
			$image_list = array (
					'post_info' => $post_info 
			);
		} else {
			echo 'fail to get user info';
		}
	}
	
	$mysqli->close ();
	
	return $image_list;
}

$possible_url = array (
		"get_thumb_images" 
);

$value = "An error has occurred";

if (isset ( $_POST ["action"] ) && in_array ( $_POST ["action"], $possible_url )) {
	$value = getImageList ();
} else {
	$value = "Missing argument";
}

// return JSON array
exit ( json_encode ( $value ) );
?>