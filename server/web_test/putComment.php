<?php
function saveComment() {
	include "./image_test/dbconfig.php";
	
	$mysqli = new mysqli ( $dbhost, $dbusr, $dbpass, $dbname );
	if ($mysqli->connect_errno) {
		echo "Failed to connect to MySQL: (" . $mysqli->connect_errno . ") " . $mysqli->connect_error;
	}
	
	$create_table = "CREATE TABLE if not exists post_comment (
					id int auto_increment,
					post_id int,
					post_user_id varchar(30),
					comment_user_id varchar(30),
					comment varchar(100),
					upload_date DATETIME DEFAULT CURRENT_TIMESTAMP,
					PRIMARY KEY (id)
					);";
	
	$mysqli->query ( $create_table );
	
	$upload_date = date ( "Y-m-d H:i:s" );
	$query = sprintf ( "INSERT INTO post_comment
		(post_id, post_user_id, comment_user_id, comment, upload_date)
		VALUES ('%s', '%s','%s','%s','%s')", 
			$_POST ["post_id"], $_POST ["post_user_id"], $_POST ["comment_user_id"], $_POST ["comment"], $upload_date);
	
	$mysqli->query ( $query );
	
	if ($mysqli->error) {
		echo "Failed to insert post_comment db: (" . $mysqli->error . ") ";
	}
	$insert_id = $mysqli->insert_id;
	
	
	
	$comment_saving_info = array (
			"id" => $insert_id
	);
	$mysqli->close ();
	return $comment_saving_info;
}

$value = "An error has occurred";
// echo 'start: ';
if (isset ( $_POST ["post_id"] ) && isset ( $_POST ["post_user_id"] )
		&& isset ( $_POST ["comment_user_id"] ) && isset ( $_POST ["comment"] )) {
	
	$value = saveComment();
	
} else {
	$value = "Missing argument";
}
// return JSON array
exit ( json_encode ( $value ) );
?>