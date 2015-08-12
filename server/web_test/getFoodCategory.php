<?php
function get_categories() {
	$categories = array ();
	
	// normally this info would be pulled from a database.
	// build JSON array.
	
	$mysqli = new mysqli ( "localhost", "root", "111111", 'db_chat_member_test' );
	if ($mysqli->connect_errno) {
		echo "Failed to connect to MySQL: (" . $mysqli->connect_errno . ") " . $mysqli->connect_error;
	}
	$sql_query = "SELECT distinct category FROM posts";
	if ($result = $mysqli->query ( $sql_query )) {
		
		if (count($result) > 0) {
			
			$arr = array();
			while ( $row = $result->fetch_assoc () ) {
				array_push ( $arr, array (
					"category" => $row ['category']
				) );
			}
			$categories = array (
					'category_list' => $arr 
			);
		} else {
			echo 'fail to get categories';
		}


	}
	
	$mysqli->close ();
	
	return $categories;
}


$value = "An error has occurred";


$value = get_categories();


// return JSON array
exit ( json_encode ( $value ) );
?>