<?php

function putProfile(){
	$user_id = $_POST ['user_id'];
	$name = $_POST ['user_name'];
	$email = $_POST ['user_email'];
	$gender = $_POST ['user_gender'];
	$score = $_POST ['user_score'];
	$bookmark = $_POST ['user_bookmark'];
	$dbname = 'db_chat_member_test';

	$mysqli = new mysqli ( "localhost", "root", "111111", $dbname );
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
						user_bookmark varchar(20)
						);";

	$sql_query = "insert into profiles (user_id, user_name, user_email, user_gender, user_score, user_bookmark) 
	values('$user_id', '$name', '$email','$gender','$score', '$bookmark')";
	
	$mysqli->query ( $create_table );

	$mysqli->query ( $sql_query );

	if ($mysqli->error) {
		echo "Failed to insert profiles db: (" . $mysqli->error . ") ";
	}
	$insert_id = $mysqli->insert_id;

	if ($mysqli->insert_id) {
		echo 'insert success';
		$value = "success";
	} else {
		echo 'insert fail';
		$value = "insert fail";
	}

	$mysqli->close ();
	return $value;
}

$value = "An error has occurred";

if (isset ( $_POST ["user_id"])){
	$value = putProfile();
}
exit ( json_encode ( $value ) );
?>