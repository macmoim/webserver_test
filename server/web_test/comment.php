<?php
function rest_get($post_id) {
	$post_info = array ();
	
	// normally this info would be pulled from a database.
	// build JSON array.
	include "./image_test/dbconfig.php";
	$mysqli = new mysqli ( $dbhost, $dbusr, $dbpass, $dbname );
	if ($mysqli->connect_errno) {
		echo "Failed to connect to MySQL: (" . $mysqli->connect_errno . ") " . $mysqli->connect_error;
	}
	//$sql_query = "SELECT comment_user_id, comment, upload_date
	//                   FROM post_comment WHERE post_id = '$post_id' AND post_user_id = '$post_user_id'";

	$sql_query = "SELECT * FROM post_comment LEFT JOIN profiles 
						ON post_comment.comment_user_id = profiles.user_id 
						WHERE post_comment.post_id = '$post_id'";
	
	if ($result = $mysqli->query ( $sql_query )) {
		
		if (count($result) > 0) {
			
			$comment_info = array ();
			while ( $row = $result->fetch_assoc () ) {
				
				
				array_push ( $comment_info, array (
					"comment_user_id" => $row ['comment_user_id'],
					"comment" => $row ['comment'],
					"upload_date" => $row ['upload_date'],
					"user_profile_img_url" => $row ['profile_img_url']
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

function rest_post() {
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
	
	// set time to Seoul.
	date_default_timezone_set("Asia/Seoul");
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

// $value = "An error has occurred";
// $method = $_SERVER['REQUEST_METHOD'];
// $request = explode("/", substr(@$_SERVER['PATH_INFO'], 1));

// switch ($method) {
//   case 'PUT':
//     rest_put($request);  
//     break;
//   case 'POST':
//     $value = rest_post();  
//     break;
//   case 'GET':
//   	//printf('request %s', var_dump($request));
//     $value = rest_get($request[0]);  
//     break;
//   case 'DELETE':
//     rest_delete($request);  
//     break;
//   default:
//   	$value = "Missing argument fail comment rest";
//     rest_error($request);  
//     break;
// }

// // return JSON array
// exit ( json_encode ( $value ) );
?>