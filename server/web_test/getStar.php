<?php
function get_star($user_id, $post_id, $post_user_id) {
	$star_info = array ();
	
	// normally this info would be pulled from a database.
	// build JSON array.
	include "./image_test/dbconfig.php";
	$mysqli = new mysqli ( $dbhost, $dbusr, $dbpass, $dbname );
	if ($mysqli->connect_errno) {
		echo "Failed to connect to MySQL: (" . $mysqli->connect_errno . ") " . $mysqli->connect_error;
	}
	$sql_query = "SELECT star
	                   FROM stars WHERE user_id = '$user_id' AND post_id = '$post_id' AND post_user_id = '$post_user_id'";
	
	if ($result = $mysqli->query ( $sql_query )) {
		$row = $result->fetch_array ();
		if (isset ( $row ['star'] )) {
			$star_info = array (
					"star" => $row ['star'],
			);
		} else {
			echo 'fail to get user info';
		}
	}
	
	$mysqli->close ();
	
	return $star_info;
}

$value = "An error has occurred";

if (isset ( $_POST ["user_id"] ) &&isset ( $_POST ["post_id"] ) && isset ( $_POST ["post_user_id"] )) {
	$value = get_star (  $_POST ["user_id"] , $_POST ["post_id"], $_POST ["post_user_id"] );
} else {
	$value = "Missing argument";
}

// return JSON array
exit ( json_encode ( $value ) );
?>