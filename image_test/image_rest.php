<?php
function get_image_by_id($id) {
	$image_info = array ();
	
	// normally this info would be pulled from a database.
	// build JSON array.
	
	$mysqli = new mysqli ( "localhost", "root", "111111", 'db_chat_member_test' );
	if ($mysqli->connect_errno) {
		echo "Failed to connect to MySQL: (" . $mysqli->connect_errno . ") " . $mysqli->connect_error;
	}
	$sql_query = "SELECT upload_filename, db_filename, filepath, filesize, width, height, file_type, upload_date
	                   FROM image_files WHERE id = '$id'";
	if ($result = $mysqli->query ( $sql_query )) {
		$row = $result->fetch_array ();
		if (isset ( $row ['db_filename'] )) {
			// echo $row['user_alias'];
			// echo $row['email'];
			$image_info = array (
					"image" => $row ['db_filename'],
					"title" => $row ['upload_filename'],
					"width" => $row ['width'],
					"height" => $row ['height'] 
			);
		} else {
			echo 'fail to get user info';
		}
	}
	
	$mysqli->close ();
	
	return $image_info;
}
function get_image_list() {
	// normally this info would be pulled from a database.
	// build JSON array
	$image_list = array ();
	
	$mysqli = new mysqli ( "localhost", "root", "111111", 'db_chat_member_test' );
	if ($mysqli->connect_errno) {
		echo "Failed to connect to MySQL: (" . $mysqli->connect_errno . ") " . $mysqli->connect_error;
	}
	$sql_query = "SELECT id, upload_filename, db_filename, filepath, filesize, width, height, file_type, upload_date
	                   FROM image_files";
	if ($result = $mysqli->query ( $sql_query )) {
		// $row = $result->fetch_assoc();
		// echo $row['user_id'];
		
		if ($result->num_rows > 0) {
			// output data of each row
			
			$image_info = array ();
			while ( $row = $result->fetch_assoc () ) {
				// echo $row['user_id'];
				// echo $row['user_alias'];
				// echo $row['email'];
				
				array_push ( $image_info, array (
						"id" => $row ['id'],
						"image" => $row ['db_filename'],
						"title" => $row ['upload_filename'],
						"img_type" => $row ['file_type'],
						"width" => $row ['width'],
						"height" => $row ['height'],
						"date" => $row ['upload_date'] 
				) );
			}
			// array_push($user_list, 'user_info');
			$image_list = array (
					'image_info' => $image_info 
			);
		} else {
			echo "0 results";
		}
		
		/*
		 * foreach ($row as $userinfo) {
		 * if (isset($userinfo['user_id']) && isset($userinfo['user_alias']) && isset($userinfo['email'])) {
		 * echo $userinfo['user_id'];
		 * echo $userinfo['user_alias'];
		 * echo $userinfo['email'];
		 *
		 * array_push($user_list, array("id"=>$userinfo['user_id'], "alias"=>$userinfo['user_alias'], "email"=>$userinfo['email']));
		 * } else {
		 * echo 'fail to get user list';
		 *
		 * }
		 * }
		 */
		
		$mysqli->close ();
	}
	
	return $image_list;
}

$possible_url = array (
		"get_image",
		"get_all_images" 
);

$value = "An error has occurred";

if (isset ( $_POST ["action"] ) && in_array ( $_POST ["action"], $possible_url )) {
	switch ($_POST ["action"]) {
		case "get_all_images" :
			$value = get_image_list ();
			break;
		case "get_image" :
			if (isset ( $_POST ["id"] ))
				$value = get_image_by_id ( $_POST ["id"] );
			else
				$value = "Missing argument";
			break;
	}
}

// return JSON array
exit ( json_encode ( $value ) );
?>