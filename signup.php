<?php
$id = $_POST ['member_id'];
$pw = $_POST ['member_password'];
$alias = $_POST ['member_alias'];
$email = $_POST ['member_email'];
$dbname = 'db_chat_member_test';
$sql_query = "insert into member (user_id, password, user_alias, email) values('$id', '$pw', '$alias', '$email')";

$mysqli = new mysqli ( "localhost", "root", "111111", $dbname );
if ($mysqli->connect_errno) {
	echo "Failed to connect to MySQL: (" . $mysqli->connect_errno . ") " . $mysqli->connect_error;
}

$create_table = "create table if not exists member(
id int primary key auto_increment,
user_id varchar(12) unique,
password varchar(10),
user_alias varchar(100),
email varchar(100)
);";

$mysqli->query ( $create_table );

$mysqli->query ( $sql_query );

if ($mysqli->insert_id) {
	// 성공 처리
	echo 'insert success';
	// index.html로 이동
	header ( "location:login.html" );
} else {
	// 실패 처리
	echo 'insert fail';
}

$mysqli->close ();

/*
 * //update
 * $sql = "UPDATE table..";
 * if($result = $mysqli->query($sql)) {
 * // 성공 처리
 * } else {
 * // 실패 처리
 * echo $mysqli->error;
 * }
 */

/*
 * //select
 * $sql = "SELECT * FROM table";
 * while($result = $mysqli->query($sql)) {
 * $row = $result->fetch_array();
 * echo $row['field'];
 * }
 */
?> 