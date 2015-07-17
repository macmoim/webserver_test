<?php
// This is the API, 2 possibilities: show the app list or show a specific app by id.
// This would normally be pulled from a database but for demo purposes, I will be hardcoding the return values.
function get_userinfo_by_id($user_id) {
	$user_info = array ();
	
	// normally this info would be pulled from a database.
	// build JSON array.
	
	$mysqli = new mysqli ( "localhost", "root", "111111", 'db_chat_member_test' );
	if ($mysqli->connect_errno) {
		echo "Failed to connect to MySQL: (" . $mysqli->connect_errno . ") " . $mysqli->connect_error;
	}
	$sql_query = "select user_alias, email from member where user_id = '$user_id'";
	if ($result = $mysqli->query ( $sql_query )) {
		$row = $result->fetch_array ();
		if (isset ( $row ['user_alias'] ) && isset ( $row ['email'] )) {
			// echo $row['user_alias'];
			// echo $row['email'];
			$user_info = array (
					"alias" => $row ['user_alias'],
					"email" => $row ['email'] 
			);
		} else {
			echo 'fail to get user info';
		}
	}
	
	$mysqli->close ();
	
	return $user_info;
}
function get_user_list() {
	// normally this info would be pulled from a database.
	// build JSON array
	$user_list = array ();
	
	$mysqli = new mysqli ( "localhost", "root", "111111", 'db_chat_member_test' );
	if ($mysqli->connect_errno) {
		echo "Failed to connect to MySQL: (" . $mysqli->connect_errno . ") " . $mysqli->connect_error;
	}
	$sql_query = "select user_id, user_alias, email from member";
	if ($result = $mysqli->query ( $sql_query )) {
		// $row = $result->fetch_assoc();
		// echo $row['user_id'];
		
		if ($result->num_rows > 0) {
			// output data of each row
			
			$user_info = array ();
			while ( $row = $result->fetch_assoc () ) {
				// echo $row['user_id'];
				// echo $row['user_alias'];
				// echo $row['email'];
				
				array_push ( $user_info, array (
						"id" => $row ['user_id'],
						"alias" => $row ['user_alias'],
						"email" => $row ['email'] 
				) );
			}
			// array_push($user_list, 'user_info');
			$user_list = array (
					'user_info' => $user_info 
			);
		} else {
			echo "0 results";
		}
		
		$mysqli->close ();
	}
	
	return $user_list;
}

$possible_url = array (
		"get_user_list",
		"get_user_info" 
);

$value = "An error has occurred";

if (isset ( $_POST ["action"] ) && in_array ( $_POST ["action"], $possible_url )) {
	switch ($_POST ["action"]) {
		case "get_user_list" :
			$value = get_user_list ();
			break;
		case "get_user_info" :
			if (isset ( $_POST ["id"] ))
				$value = get_userinfo_by_id ( $_POST ["id"] );
			else
				$value = "Missing argument";
			break;
	}
}

// return JSON array
exit ( json_encode ( $value ) );
?>