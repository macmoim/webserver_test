<?php
function get_like($post_id, $post_user_id) {
	$post_info = array ();
	
	// normally this info would be pulled from a database.
	// build JSON array.
	include "./image_test/dbconfig.php";
	$mysqli = new mysqli ( $dbhost, $dbusr, $dbpass, $dbname );
	if ($mysqli->connect_errno) {
		echo "Failed to connect to MySQL: (" . $mysqli->connect_errno . ") " . $mysqli->connect_error;
	}
	$sql_query = "SELECT comment_user_id, comment, upload_date
	                   FROM post_comment WHERE post_id = '$post_id' AND post_user_id = '$post_user_id'";
	
	if ($result = $mysqli->query ( $sql_query )) {
		
		if (count($result) > 0) {
			
			$comment_info = array ();
			while ( $row = $result->fetch_assoc () ) {
				
				
				array_push ( $comment_info, array (
					"comment_user_id" => $row ['comment_user_id'],
					"comment" => $row ['comment'],
					"upload_date" => $row ['upload_date'],
				) );
				
				
			}
			// array_push($user_list, 'user_info');
			$comment_list = array (
					'comment_info' => $comment_info 
			);
		} else {
			echo 'fail to get user info';
		}
	}
	
	$mysqli->close ();
	
	return $comment_list;
}

$possible_url = array (
		"get_post" 
);

$value = "An error has occurred";

if (isset ( $_POST ["post_id"] ) && isset ( $_POST ["post_user_id"] )) {
	$value = get_like ( $_POST ["post_id"], $_POST ["post_user_id"] );
} else {
	$value = "Missing argument";
}

// return JSON array
exit ( json_encode ( $value ) );
?>