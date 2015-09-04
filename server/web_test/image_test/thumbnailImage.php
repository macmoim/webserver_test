<?php
function rest_get_image_list($category) {
	$image_info = array ();
	
	// normally this info would be pulled from a database.
	// build JSON array.
	
	include "dbconfig.php";
	
	$mysqli = new mysqli ( $dbhost, $dbusr, $dbpass, $dbname );
	if ($mysqli->connect_errno) {
		echo "Failed to connect to MySQL: (" . $mysqli->connect_errno . ") " . $mysqli->connect_error;
	}
	$sql_query = "SELECT posts.id as p_id, posts.user_id as p_user_id, title, upload_filename, upload_date, thumb_img_path, rank, profiles.user_name as profile_user_name
						, count(like_bool) as likes_sum
	                   FROM posts 
                       LEFT JOIN profiles ON posts.user_id = profiles.user_id
                       LEFT JOIN likes ON likes.post_id = posts.id AND likes.like_bool = '1'";


	$sql_group_by = " GROUP BY posts.id";
	$sql_order_by = " ORDER BY upload_date";
	if (isset($category)) {
		
		if (strcmp($category, "Latest") == 0) {

		} else if (strcmp($category, "Popular") == 0) {
			$sql_order_by = " ORDER BY rank";
		} else {
			$sql_query .= " WHERE category = '$category'";	
		}
		
	}
	$sql_query .= $sql_group_by;
	$sql_query .= $sql_order_by;
	
	if ($result = $mysqli->query ( $sql_query )) {
		
		if (count($result) > 0) {
			
			$post_info = array ();
			while ( $row = $result->fetch_assoc () ) {
				
				
				array_push ( $post_info, array (
					"id" => $row ['p_id'],
					"title" => $row ['title'],
					"user_id" => $row ['p_user_id'],
					"filename" => $row ['upload_filename'],
					"date" => $row ['upload_date'],
					"img_path" => $row ['thumb_img_path'],
					"user_name" => $row ['profile_user_name'],
					"like_sum" => $row ['likes_sum'],
					"score" => $row ['rank'],
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
	
	include "dbconfig.php";
	
	$mysqli = new mysqli ( $dbhost, $dbusr, $dbpass, $dbname );
	if ($mysqli->connect_errno) {
		echo "Failed to connect to MySQL: (" . $mysqli->connect_errno . ") " . $mysqli->connect_error;
	}
	$sql_query = "SELECT posts.id as p_id, posts.user_id as p_user_id, title, upload_filename, upload_date, thumb_img_path, rank, profiles.user_name as profile_user_name
	                   FROM posts LEFT JOIN profiles ON posts.user_id = profiles.user_id WHERE upload_date > '$timestamp'";
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
					"id" => $row ['p_id'],
					"title" => $row ['title'],
					"user_id" => $row ['p_user_id'],
					"filename" => $row ['upload_filename'],
					"date" => $row ['upload_date'],
					"img_path" => $row ['thumb_img_path'],
					"user_name" => $row ['profile_user_name'],
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