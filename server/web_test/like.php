<?php
function rest_get($user_id, $post_id) {
	$like_info = array ();
	
	// normally this info would be pulled from a database.
	// build JSON array.
	include "./image_test/dbconfig.php";
	$mysqli = new mysqli ( $dbhost, $dbusr, $dbpass, $dbname );
	if ($mysqli->connect_errno) {
		echo "Failed to connect to MySQL: (" . $mysqli->connect_errno . ") " . $mysqli->connect_error;
	}
	$sql_query = "SELECT like_bool
	                   FROM likes WHERE user_id = '$user_id' AND post_id = '$post_id'";
	
	if ($result = $mysqli->query ( $sql_query )) {
		$row = $result->fetch_array ();
		if (isset ( $row ['like_bool'] )) {
			$like_info = array (
					"like" => $row ['like_bool'],
			);
		} else {
			echo 'fail to get like info';
		}
	}
	
	$mysqli->close ();
	
	return $like_info;
}

function rest_post() {
	include "./image_test/dbconfig.php";
	$debug_msg = "not work";
	$mysqli = new mysqli ( $dbhost, $dbusr, $dbpass, $dbname );
	if ($mysqli->connect_errno) {
		echo "Failed to connect to MySQL: (" . $mysqli->connect_errno . ") " . $mysqli->connect_error;
	}
	
	$create_table = "CREATE TABLE if not exists likes (
					id int auto_increment,
					user_id varchar(30),
					like_bool boolean,
					post_id int,
					post_user_id varchar(30),
					PRIMARY KEY (id)
					);";
	
	$mysqli->query ( $create_table );
	
	$query_update = sprintf ( "UPDATE likes
				SET like_bool = '%s' WHERE user_id = '%s' AND post_id = '%s' AND post_user_id = '%s'",
			$_POST["like"], $_POST["user_id"],$_POST["post_id"],$_POST["post_user_id"]);
	
	$mysqli->query ( $query_update );
	// check update success
	if ($mysqli->affected_rows == 0) {
		$query_insert = sprintf ( "INSERT INTO likes
			(user_id, like_bool, post_id, post_user_id)
			VALUES ('%s','%s', '%s','%s')", 
				$_POST["user_id"],$_POST["like"],$_POST["post_id"],$_POST["post_user_id"]);
		
		$mysqli->query ( $query_insert );
		
		 $debug_msg = "insert like success";
		
		if ($mysqli->error) {
			echo "Failed to insert like db: (" . $mysqli->error . ") ";
		}
		
	} else {
	}
	
	$insert_id = $mysqli->insert_id;
	
	$like_saving_info = array (
			"id" => $insert_id
	);
	$mysqli->close ();
	return $like_saving_info;//$debug_msg;
}

$value = "An error has occurred";
$method = $_SERVER['REQUEST_METHOD'];
$request = explode("/", substr(@$_SERVER['PATH_INFO'], 1));

switch ($method) {
  case 'PUT':
    rest_put($request);  
    break;
  case 'POST':
    $value = rest_post();  
    break;
  case 'GET':
    //printf('request get %s' , var_dump($request));
    $value = rest_get($request[0], $request[1]);  
    break;
  case 'DELETE':
    rest_delete($request);  
    break;
  default:
  	$value = "Missing argument fail like rest";
    rest_error($request);  
    break;
}

// return JSON array
exit ( json_encode ( $value ) );
?>