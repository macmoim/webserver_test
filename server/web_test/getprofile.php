<?php
function get_post($id) {
	$post_info = array ();
	
	// normally this info would be pulled from a database.
	// build JSON array.
	
	$mysqli = new mysqli ( "localhost", "root", "111111", 'db_chat_member_test' );
	if ($mysqli->connect_errno) {
		echo "Failed to connect to MySQL: (" . $mysqli->connect_errno . ") " . $mysqli->connect_error;
	}
	$sql_query = "SELECT user_id, user_name, user_email, user_score, user_gender, user_intro, profile_img_url
	                   FROM profiles WHERE user_id = '$id'";
	if ($result = $mysqli->query ( $sql_query )) {
		$row = $result->fetch_array ();
		if (isset ( $row ['user_name'] )) {
			$post_info = array (
					"user_id" => $row ['user_id'],
					"user_name" => $row ['user_name'],
					"user_email" => $row ['user_email'],
					"user_score" => $row ['user_score'],
					"user_gender" => $row ['user_gender'],
					"user_intro" => $row ['user_intro'],
					"profile_img_url" => $row ['profile_img_url']
			);
		} else {
			echo 'fail to get user info';
		}
	}
	
	$mysqli->close ();
	
	return $post_info;
}

$possible_url = array (
		"get_profiles" 
);

$value = "An error has occurred";

if (isset ( $_POST ["user_id"] )) {
	$value = get_post ( $_POST ["user_id"] );
} else {
	$value = "Missing argument";
}

// return JSON array
exit ( json_encode ( $value ) );
?>