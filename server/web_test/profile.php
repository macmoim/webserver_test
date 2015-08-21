<?php
function rest_get($id) {
	$post_info = array ();
	
	// normally this info would be pulled from a database.
	// build JSON array.

	update_user_ranking();
	
	$mysqli = new mysqli ( "localhost", "root", "111111", 'db_chat_member_test' );
	if ($mysqli->connect_errno) {
		echo "Failed to connect to MySQL: (" . $mysqli->connect_errno . ") " . $mysqli->connect_error;
	}
	$sql_query = "SELECT id, user_id, user_name, user_email, user_score, user_gender, user_intro, profile_img_url, user_ranking
	                   FROM profiles WHERE user_id = '$id'";
	if ($result = $mysqli->query ( $sql_query )) {
		$row = $result->fetch_array ();
		if (isset ( $row ['user_name'] )) {
			$post_info = array (
					"id" => $row ['id'],
					"user_id" => $row ['user_id'],
					"user_name" => $row ['user_name'],
					"user_email" => $row ['user_email'],
					"user_score" => $row ['user_score'],
					"user_gender" => $row ['user_gender'],
					"user_intro" => $row ['user_intro'],
					"profile_img_url" => $row ['profile_img_url'],
					"user_ranking" => $row['user_ranking']
			);
		} else {
			echo 'fail to get user info';
		}
	}
	
	$mysqli->close ();
	
	return $post_info;
}

function rest_post( $keys, $values){
	$dbname = 'db_chat_member_test';

	$mysqli = new mysqli ( "localhost", "root", "111111", $dbname );
	if ($mysqli->connect_errno) {
		echo "Failed to connect to MySQL: (" . $mysqli->connect_errno . ") " . $mysqli->connect_error;
	}

	$create_table = "create table if not exists profiles(
						id int primary key auto_increment,
						user_id varchar(30) unique,
						user_name varchar(30),
						user_email varchar(30),
						user_gender varchar(10),
						user_score varchar(10),
						user_intro varchar(500),
						profile_img_url varchar(100),
						user_ranking int
						);";

	$sql_query = "insert into profiles (";

	$arr_size = count($keys);
	for ($count=0; $count<$arr_size; $count++) {
		$sql_query .=" $keys[$count] ";
		if ($count != $arr_size-1) {
			$sql_query .= ", ";
		}
	}
	$sql_query .= ") values (";
	$arr_size = count($values);
	for ($count=0; $count<$arr_size; $count++) {
		$sql_query .=" '$values[$count]' ";
		if ($count != $arr_size-1) {
			$sql_query .= ", ";
		}
	}
	$sql_query .= ")";
	
	$mysqli->query ( $create_table );

	$mysqli->query ( $sql_query );

	if ($mysqli->error) {
		echo "Failed to insert profiles db: (" . $mysqli->error . ") ";
	}
	$insert_id = $mysqli->insert_id;

	$ret = array();
	if ($mysqli->insert_id) {
		$ret['ret_val'] = "success";
		$ret['id'] = $insert_id;
		$arr_size = count($keys);
		for ($count=0; $count<$arr_size; $count++) {
			// array_push($ret, $keys[$count]=>$values[$count]);
			$ret[$keys[$count]] = $values[$count];
		}
		$value = $ret;
	} else {
		$ret['ret_val'] = "fail";
		$value = $ret;
	}

	$mysqli->close ();
	return $value;
}

function rest_put_all($id, $user_id, $name, $email, $gender, $score, $intro, $img_url){

	$dbname = 'db_chat_member_test';

	include "./image_test/dbconfig.php";
	$mysqli = new mysqli ( $dbhost, $dbusr, $dbpass, $dbname );

	if ($mysqli->connect_errno) {
		echo "Failed to connect to MySQL: (" . $mysqli->connect_errno . ") " . $mysqli->connect_error;
	}

	$create_table = "create table if not exists profiles(
						id int primary key auto_increment,
						user_id varchar(30) unique,
						user_name varchar(30),
						user_email varchar(30),
						user_gender varchar(10),
						user_score varchar(10),
						user_intro varchar(500),
						profile_img_url varchar(100),
						user_ranking int
						);";

	$sql_query = "UPDATE profiles SET user_name = '$name', user_email = '$email',
	 					user_gender = '$gender', user_score = '$score', user_intro = '$intro',
	 					profile_img_url = '$img_url' WHERE id = '$id' ";

	
	$mysqli->query ( $create_table );

	$mysqli->query ( $sql_query );

	if ($mysqli->error) {
		echo "Failed to update profiles db: (" . $mysqli->error . ") ";
	}

	$ret = array();
	if ($mysqli->affected_rows == 0) {
		$ret['ret_val'] = "fail";
		$value = $ret;
		
		
	} else {
		$ret['ret_val'] = "success";
		$ret['id'] = $id;
		$ret['user_id'] = $user_id;
		$ret['user_name'] = $name;
		$ret['user_email'] = $email;
		$ret['user_score'] = $score;
		$ret['user_gender'] = $gender;
		$ret['user_intro'] = $intro;
		$ret['profile_img_url'] = $img_url;
		
		$value = $ret;
	}

	$mysqli->close ();
	return $value;
}

function rest_put($id, $keys, $values){

	$dbname = 'db_chat_member_test';

	include "./image_test/dbconfig.php";
	$mysqli = new mysqli ( $dbhost, $dbusr, $dbpass, $dbname );
	if ($mysqli->connect_errno) {
		echo "Failed to connect to MySQL: (" . $mysqli->connect_errno . ") " . $mysqli->connect_error;
	}

	$create_table = "create table if not exists profiles(
						id int primary key auto_increment,
						user_id varchar(30) unique,
						user_name varchar(30),
						user_email varchar(30),
						user_gender varchar(10),
						user_score varchar(10),
						user_intro varchar(500),
						profile_img_url varchar(100),
						user_ranking int
						);";

	$sql_query = "UPDATE profiles SET ";

	$arr_size = count($keys);
	for ($count=0; $count<$arr_size; $count++) {
		$sql_query .=" $keys[$count] = '$values[$count]'";
		if ($count != $arr_size-1) {
			$sql_query .= ", ";
		}
	}
	$sql_query .= " WHERE id = '$id'";

	$mysqli->query ( $create_table );

	$mysqli->query ( $sql_query );

	if ($mysqli->error) {
		echo "Failed to update profiles db: (" . $mysqli->error . ") ";
	}

	$ret = array();
	if ($mysqli->affected_rows == 0) {
		$ret['ret_val'] = "fail";
		$value = $ret;
		
		
	} else {
		$ret['ret_val'] = "success";
		
		$value = $ret;
	}

	$mysqli->close ();
	return $value;
}

function update_user_ranking() {
	include "./image_test/dbconfig.php";
	$debug_msg = "not work";
	$mysqli = new mysqli ( $dbhost, $dbusr, $dbpass, $dbname );
	
	if ($mysqli->connect_errno) {
		echo "Failed to connect to MySQL: (" . $mysqli->connect_errno . ") " . $mysqli->connect_error;
	}

	// add user_ranking COLUMN if not exists COLUMN user_ranking in TABLE profiles
	$table_name = "profiles";
	$column_name = "user_ranking";
	$check_rank_query = sprintf ( "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME='%s' AND COLUMN_NAME = '%s'", 
				$table_name, $column_name);
	$result = $mysqli->query($check_rank_query);
	$exists = $result->num_rows ? TRUE : FALSE;
	if ($exists) {
	} else {
		$alter_add_query = sprintf ( "ALTER TABLE %s ADD %s int", 
				$table_name, $column_name);
		$mysqli->query($alter_add_query);
	}

	// alter user_id column to UNIQUE
	$alter_unique_query = "ALTER TABLE profiles ADD UNIQUE (user_id)";
	$mysqli->query($alter_unique_query);
	if ($mysqli->error) {
		echo "alter user_id column to unique error ".$mysqli->error;
	}

	// sql query get user ranking from db order by like count and rank average.
	$get_ranking_query = 
	"SELECT count(like_bool) as count_sum, likes.user_id, posts.rank, likes.post_user_id, AVG(posts.rank) as rank_avg FROM likes 
	JOIN posts ON likes.post_user_id = posts.user_id 
	WHERE posts.rank > 0 AND likes.like_bool = 1 
	GROUP BY posts.user_id 
	ORDER BY count_sum DESC, rank_avg DESC";

	$result = $mysqli->query($get_ranking_query);
	$ranking_count = $result->num_rows;
	if ($ranking_count > 0) {
		$ranking_arr = array();
		$score_arr = array();
		while ( $row = $result->fetch_assoc () ) {
			array_push($ranking_arr, $row['post_user_id']);
			array_push($score_arr, $row['rank_avg']);
			
		}
		
		
		$ranking_update_query = "UPDATE profiles SET user_ranking = CASE ";

		$length = count($ranking_arr);
		for ($i = 0; $i < $length; $i++) {
			$ranking = $i+1;
			$ranking_update_query .= "WHEN user_id = '$ranking_arr[$i]' THEN '$ranking' ";
		}

		$ranking_update_query .= "ELSE 'user_id' END , ";

		$ranking_update_query .= "user_score = CASE ";

		$length = count($score_arr);
		for ($i = 0; $i < $length; $i++) {
			
			$ranking_update_query .= "WHEN user_id = '$ranking_arr[$i]' THEN '$score_arr[$i]' ";
		}

		$ranking_update_query .= "ELSE 'user_id' END ";

		$ranking_update_query .= "WHERE user_id in (";
		for ($i = 0; $i < $length; $i++) {
			$ranking_update_query .= "'$ranking_arr[$i]'";
			if ($i != ($length-1)) {
				$ranking_update_query .= ", ";
			}
		}
		$ranking_update_query .= ")";

		// echo "my ranking query: ".$ranking_update_query;
			
		$mysqli->query($ranking_update_query);
		if ($mysqli->error) {
			echo "Failed to update user ranking to profiles TABLE : (" . $mysqli->error . ") ";
		}
	}

	
	
	$mysqli->close();
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
//     $value = rest_get($request[0]);  
//     break;
//   case 'DELETE':
//     rest_delete($request);  
//     break;
//   default:
//   	$value = "Missing argument fail profile rest";
//     rest_error($request);  
//     break;
// }

// // return JSON array
// exit ( json_encode ( $value ) );
?>