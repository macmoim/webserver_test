<?php
function rest_get_image_list($category) {
	$image_info = array ();
	
	// normally this info would be pulled from a database.
	// build JSON array.
	
	$mysqli = new mysqli ( "localhost", "root", "111111", 'db_chat_member_test' );
	if ($mysqli->connect_errno) {
		echo "Failed to connect to MySQL: (" . $mysqli->connect_errno . ") " . $mysqli->connect_error;
	}
	$sql_query = "SELECT id, user_id, title, upload_filename, upload_date, thumb_img_path
	                   FROM posts";
	$sql_order_by = " ORDER BY upload_date";
	if (isset($category)) {
		
		if (strcmp($category, "Latest") == 0) {

		} else if (strcmp($category, "Popular") == 0) {
			$sql_order_by = " ORDER BY rank";
		} else {
			$sql_query .= " WHERE category = '$category'";	
		}
		
	}
	$sql_query .= $sql_order_by;
	
	if ($result = $mysqli->query ( $sql_query )) {
		
		if (count($result) > 0) {
			
			$post_info = array ();
			while ( $row = $result->fetch_assoc () ) {
				
				
				array_push ( $post_info, array (
					"id" => $row ['id'],
					"title" => $row ['title'],
					"user_id" => $row ['user_id'],
					"filename" => $row ['upload_filename'],
					"date" => $row ['upload_date'],
					"img_path" => $row ['thumb_img_path']
				) );
				
				
			}
			// array_push($user_list, 'user_info');
			$image_list = array (
					'ret_val' => "success",
					'post_info' => $post_info 
			);
		} else {
			$image_list = array (
					'ret_val' => "fail",
					'ret_detail' => "fail to get user info"
			);
		}
	} else {
		$image_list = array (
					'ret_val' => "fail",
					'ret_detail' => "no post data in db"
			);
	}
	
	$mysqli->close ();
	
	return $image_list;
}

function rest_get_image_list_by_timestamp($category, $timestamp) {
	$image_info = array ();
	
	// normally this info would be pulled from a database.
	// build JSON array.
	
	$mysqli = new mysqli ( "localhost", "root", "111111", 'db_chat_member_test' );
	if ($mysqli->connect_errno) {
		echo "Failed to connect to MySQL: (" . $mysqli->connect_errno . ") " . $mysqli->connect_error;
	}
	$sql_query = "SELECT id, user_id, title, upload_filename, upload_date, thumb_img_path
	                   FROM posts WHERE upload_date > '$timestamp'";
	$sql_order_by = "";
	if (isset($category)) {
		
		if (strcmp($category, "Latest") == 0) {
			
		} else if (strcmp($category, "Popular") == 0) {
			$sql_order_by = " ORDER BY rank";
		} else {
			$sql_query .= " && category = '$category'";	
		}
		
	}
	$sql_query .= $sql_order_by;
	$post_info = array ();
	if ($result = $mysqli->query ( $sql_query )) {
		
		if (count($result) > 0) {
			
			
			while ( $row = $result->fetch_assoc () ) {
				
				
				array_push ( $post_info, array (
					"id" => $row ['id'],
					"title" => $row ['title'],
					"user_id" => $row ['user_id'],
					"filename" => $row ['upload_filename'],
					"date" => $row ['upload_date'],
					"img_path" => $row ['thumb_img_path']
				) );
				
				
			}
			// array_push($user_list, 'user_info');
			$image_list = array (
					'post_info' => $post_info,
					'ret_val' => 'success',
					 
			);
		} else {
			$post_info['ret_success'] = 'fail';
			$post_info['ret_detail'] = 'fail to get user info: no rows';
		}
	} else {
		$post_info['ret_success'] = 'fail';
		$post_info['ret_detail'] = 'fail to get user info: no sql results';
	}
	
	$mysqli->close ();
	
	return $image_list;
}

// $possible_url = array (
// 		"get_thumb_images" 
// );

// $value = "An error has occurred";

// if (isset ( $_POST ["action"] ) && in_array ( $_POST ["action"], $possible_url )) {
// 	$value = getImageList ();
// } else {
// 	$value = "Missing argument";
// }

// // return JSON array
// exit ( json_encode ( $value ) );
?>