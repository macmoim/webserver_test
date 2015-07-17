<?php
session_start ();
$id = $_POST ['member_id'];
$pw = $_POST ['member_password'];
$dbname = 'db_chat_member_test';

$mysqli = new mysqli ( "localhost", "root", "111111", $dbname );
if ($mysqli->connect_errno) {
	echo "Failed to connect to MySQL: (" . $mysqli->connect_errno . ") " . $mysqli->connect_error;
}

// select
$sql_query = "select user_id from member where user_id = '$id' and password='$pw'";
if ($result = $mysqli->query ( $sql_query )) {
	$row = $result->fetch_array ();
	if (isset ( $row ['user_id'] )) {
		echo $row ['user_id'];
		$_SESSION ['is_logged'] = 'YES';
		$_SESSION ['user_id'] = $id;
	} else {
		echo 'fail to login';
		$_SESSION ['is_logged'] = 'NO';
		$_SESSION ['user_id'] = '';
	}
	header ( "location:login_done.php" );
}

$mysqli->close ();

?> 