<?php
function rest_get($id) {
	$post_info = array ();
	
	// normally this info would be pulled from a database.
	// build JSON array.
	
	$mysqli = new mysqli ( "localhost", "root", "111111", 'db_chat_member_test' );
	if ($mysqli->connect_errno) {
		echo "Failed to connect to MySQL: (" . $mysqli->connect_errno . ") " . $mysqli->connect_error;
	}
	$sql_query = "SELECT id, user_id, user_name, user_email, user_score, user_gender, user_intro, profile_img_url
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
					"profile_img_url" => $row ['profile_img_url']
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
						profile_img_url varchar(100)
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
						user_id varchar(12) unique,
						user_name varchar(10),
						user_email varchar(20),
						user_gender varchar(10),
						user_score varchar(10),
						user_intro varchar(500),
						profile_img_url varchar(20)
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
						user_id varchar(12) unique,
						user_name varchar(10),
						user_email varchar(20),
						user_gender varchar(10),
						user_score varchar(10),
						user_intro varchar(500),
						profile_img_url varchar(20)
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