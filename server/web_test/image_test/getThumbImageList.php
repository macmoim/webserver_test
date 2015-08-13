<?php
function getImageList() {
	$image_info = array ();
	
	// normally this info would be pulled from a database.
	// build JSON array.
	
	$mysqli = new mysqli ( "localhost", "root", "111111", 'db_chat_member_test' );
	if ($mysqli->connect_errno) {
		echo "Failed to connect to MySQL: (" . $mysqli->connect_errno . ") " . $mysqli->connect_error;
	}
	$sql_query = "SELECT id, user_id, title, upload_filename, upload_date, thumb_img_path
	                   FROM posts";
	$sql_order_by = " ORDER BY upload_date";
	if (isset($_POST['category'])) {
		$ctg = $_POST['category'];
		if (strcmp($ctg, "Latest") == 0) {

		} else if (strcmp($ctg, "Popular") == 0) {
			$sql_order_by = " ORDER BY rank";
		} else {
			$sql_query .= " WHERE category = '$ctg'";	
		}
		
	}
	$sql_query .= $sql_order_by;
	
	if ($result = $mysqli->query ( $sql_query )) {
		
		if (count($result) > 0) {
			
			$post_info = array ();
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