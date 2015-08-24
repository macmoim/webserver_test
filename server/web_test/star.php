<?php
function rest_get($user_id, $post_id) {
	$star_info = array ();
	
	// normally this info would be pulled from a database.
	// build JSON array.
	include "./image_test/dbconfig.php";
	$mysqli = new mysqli ( $dbhost, $dbusr, $dbpass, $dbname );
	if ($mysqli->connect_errno) {
		echo "Failed to connect to MySQL: (" . $mysqli->connect_errno . ") " . $mysqli->connect_error;
	}
	$sql_query = "SELECT id, star
	                   FROM stars WHERE user_id = '$user_id' AND post_id = '$post_id'";
	
	if ($result = $mysqli->query ( $sql_query )) {
		$row = $result->fetch_array ();
		if (isset ( $row ['star'] )) {
			$star_info = array (
					"id" => $row ['id'],
					"star" => $row ['star'],
			);
		} else {
			echo 'fail to get star info';
		}
	}
	
	$mysqli->close ();
	
	return $star_info;
}

function rest_post() {
	include "./image_test/dbconfig.php";
	$debug_msg = "not work";
	$mysqli = new mysqli ( $dbhost, $dbusr, $dbpass, $dbname );
	if ($mysqli->connect_errno) {
		echo "Failed to connect to MySQL: (" . $mysqli->connect_errno . ") " . $mysqli->connect_error;
	}
	
	$create_table = "CREATE TABLE if not exists stars (
					id int auto_increment,
					user_id varchar(30),
					star int,
					post_id int,
					post_user_id varchar(30),
					PRIMARY KEY (id)
					);";
	
	$mysqli->query ( $create_table );

	$query_select = sprintf ( "SELECT star
				FROM stars WHERE user_id = '%s' AND post_id = '%s' AND post_user_id = '%s'",
			 $_POST["user_id"],$_POST["post_id"],$_POST["post_user_id"]);
	
	
	
	// check update success
	$result = $mysqli->query ( $query_select );
	if ($result->num_rows > 0) {
		$query_update = sprintf ( "UPDATE stars
				SET star = '%s' WHERE user_id = '%s' AND post_id = '%s' AND post_user_id = '%s' LIMIT 1",
				$_POST["star"], $_POST["user_id"],$_POST["post_id"],$_POST["post_user_id"]);
		$mysqli->query($query_update);
	} else {
		$query_insert = sprintf ( "INSERT INTO stars
			(user_id, star, post_id, post_user_id)
			VALUES ('%s','%s', '%s','%s')",
				$_POST["user_id"],$_POST["star"],$_POST["post_id"],$_POST["post_user_id"]);
		
		$mysqli->query ( $query_insert );
		
		$debug_msg = "insert star success";
		
		if ($mysqli->error) {
			echo "Failed to insert like db: (" . $mysqli->error . ") ";
		}
	}
	
	$insert_id = $mysqli->insert_id;
	
	$like_saving_info = array (
			"id" => $insert_id
	);
	$mysqli->close ();
	saveRank($insert_id, $_POST["post_id"]);
	return $like_saving_info;//$debug_msg;
}

function saveRank($id, $post_id) {
	include "./image_test/dbconfig.php";
	$debug_msg = "not work";
	$mysqli = new mysqli ( $dbhost, $dbusr, $dbpass, $dbname );
	
	if ($mysqli->connect_errno) {
		echo "Failed to connect to MySQL: (" . $mysqli->connect_errno . ") " . $mysqli->connect_error;
	}
	
	// add rank COLUMN if not exists COLUMN rank in TABLE posts
	// $table_name = "posts";
	// $column_name = "rank";
	// $check_rank_query = sprintf ( "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME='%s' AND COLUMN_NAME = '%s'", 
	// 			$table_name, $column_name);
	// $result = $mysqli->query($check_rank_query);
	// $exists = $result->num_rows ? TRUE : FALSE;
	// if ($exists) {
	// } else {
	// 	$alter_add_query = sprintf ( "ALTER TABLE %s ADD %s float", 
	// 			$table_name, $column_name);
	// 	$mysqli->query($alter_add_query);
	// 	if ($mysqli->error) {
	// 		echo "update post rank error ".$mysqli->error;
	// 	}
	// }
	
	// put rank to TABLE posts
	
	$get_avg_star_query = "SELECT AVG(star) as star_avg from stars where post_id = '$post_id'";
	if ($result = $mysqli->query($get_avg_star_query)) {
		$row = $result->fetch_assoc();
		$rank_point_update_query = sprintf ( "UPDATE posts
				SET rank = '%s' WHERE id = '%s'",
			$row['star_avg'], $post_id);
		$mysqli->query($rank_point_update_query);
		if ($mysqli->error) {
			echo "Failed to insert rank to posts TABLE: (" . $mysqli->error . ") ";
		}
	}
	
	
	$mysqli->close();
}

function rest_put($id, $star, $post_id) {
	include "./image_test/dbconfig.php";
	$debug_msg = "not work";
	$mysqli = new mysqli ( $dbhost, $dbusr, $dbpass, $dbname );
	if ($mysqli->connect_errno) {
		echo "Failed to connect to MySQL: (" . $mysqli->connect_errno . ") " . $mysqli->connect_error;
	}
	
	$create_table = "CREATE TABLE if not exists stars (
					id int auto_increment,
					user_id varchar(30),
					star int,
					post_id int,
					post_user_id varchar(30),
					PRIMARY KEY (id)
					);";
	
	$mysqli->query ( $create_table );

	
	

	$query_update = sprintf ( "UPDATE stars
			SET star = '%s' WHERE id = '%s'",
			$star, $id);
	$mysqli->query($query_update);

	$ret_val = "fail";
	if ($mysqli->affected_rows == 0) {
		
		
	} else {
		$ret_val = "success";
	}
	
	$star_saving_info = array (
			"ret" => $ret_val
	);
	$mysqli->close ();
	saveRank($id, $post_id);
	return $star_saving_info;//$debug_msg;
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
//     //printf('request get %s' , var_dump($request));
//     $value = rest_get($request[0], $request[1]);  
//     break;
//   case 'DELETE':
//     rest_delete($request);  
//     break;
//   default:
//   	$value = "Missing argument fail star rest";
//     rest_error($request);  
//     break;
// }

// // return JSON array
// exit ( json_encode ( $value ) );
?>