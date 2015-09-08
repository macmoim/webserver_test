<?php
function sendMsgToGcm($post_user_id, $user_id, $push_msg) {

	include "./image_test/dbconfig.php";
	$debug_msg = "not work";
	$mysqli = new mysqli ( $dbhost, $dbusr, $dbpass, $dbname );
	
	if ($mysqli->connect_errno) {
		echo "Failed to connect to MySQL: (" . $mysqli->connect_errno . ") " . $mysqli->connect_error;
	}

	$sql_query = "select gcm_instant_ids.user_id, instant_id, profiles.user_name as post_user_name
	from gcm_instant_ids 
    left join profiles on gcm_instant_ids.user_id = profiles.user_id
	where gcm_instant_ids.user_id = '$post_user_id'";

	$instantIdInfos = array ();
	$post_user_name;
    if ($result = $mysqli->query ( $sql_query )) {
		
		if (count($result) > 0) {
			$row = $result->fetch_assoc ();
			array_push ( $instantIdInfos, $row['instant_id']);
			$post_user_name = $row['post_user_name'];
			
		} else {
			return "no instant_id";
		}
	} else {
		return "no instant_id";
	}

	$sql_query = "select user_name
	from profiles 
	where user_id = '$user_id'";

	$user_name;
    if ($result = $mysqli->query ( $sql_query )) {
		
		if (count($result) > 0) {
			$row = $result->fetch_assoc ();
			$user_name = $row['user_name'];
			
		} else {
			return "no user_name";
		}
	} else {
		return "no user_name";
	}

	if ($mysqli->error) {
		echo "Failed to select gcmdb : (" . $mysqli->error . ") ";
	}


	$headers = array(
	'Content-Type:application/json',
	'Authorization:key=AIzaSyArwTyrB6Je2H282zBGqqgrwOtS0VdT8K0'
	);
	$arr=array();
	$arr['data']=array();
	$msg = $user_name." 님이 ".$post_user_name."님의 글에 ".$push_msg;
	$arr['data']['message']=$msg;
	$arr['registration_ids']=array();
	$arr['registration_ids']=$instantIdInfos;
	// array_push ( $arr['registration_ids'], $instantIdInfos);
	// $arr['registration_ids'][0]= $instantIdInfos;
	$ch = curl_init();
	curl_setopt($ch, CURLOPT_URL,'https://android.googleapis.com/gcm/send');
	curl_setopt($ch, CURLOPT_HTTPHEADER,$headers);
	curl_setopt($ch, CURLOPT_POST,true);
	curl_setopt($ch, CURLOPT_RETURNTRANSFER,true);
	curl_setopt($ch, CURLOPT_SSL_VERIFYPEER,false);
	curl_setopt( $ch, CURLOPT_SSL_VERIFYHOST, false);
	curl_setopt($ch, CURLOPT_POSTFIELDS,json_encode($arr));
	$response = curl_exec($ch);
	// echo $response;
	curl_close($ch);
}

function rest_post_instantId() {
	include "./image_test/dbconfig.php";
	$debug_msg = "not work";
	$mysqli = new mysqli ( $dbhost, $dbusr, $dbpass, $dbname );
	if ($mysqli->connect_errno) {
		echo "Failed to connect to MySQL: (" . $mysqli->connect_errno . ") " . $mysqli->connect_error;
	}
	
	$create_table = "CREATE TABLE if not exists gcm_instant_ids (
					id int auto_increment,
					user_id varchar(30) unique,
					instant_id varchar(300),
					PRIMARY KEY (id)
					);";
	
	$mysqli->query ( $create_table );

	$query_insert = sprintf ( "INSERT INTO gcm_instant_ids
			(user_id, instant_id)
			VALUES ('%s','%s') ON DUPLICATE KEY UPDATE instant_id = VALUES(instant_id)",
				$_POST['user_id'],$_POST['instant_id']);
		
	$mysqli->query ( $query_insert );
	
	$ret['ret_val'] = "success";
	
	if ($mysqli->error) {
		$ret['ret_val'] = "fail";
		$ret['ret_detail'] = $mysqli->error;
	}

	return $ret;
}


?>