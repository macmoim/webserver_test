<?php


function saveImageFiles($post_id) {
	include "serverconfig.php";
	if (isset ( $_POST ["title"] ) && count($_FILES) > 0) {
	} else {
		$value = "Missing argument";
		return $value;
	}

	include "./image_test/dbconfig.php";
	$debug_msg = "not work";
	$mysqli = new mysqli ( $dbhost, $dbusr, $dbpass, $dbname );
	if ($mysqli->connect_errno) {
		echo "Failed to connect to MySQL: (" . $mysqli->connect_errno . ") " . $mysqli->connect_error;
	}
	
	$create_table = "CREATE TABLE if not exists pages (
					id int auto_increment,
					post_id varchar(30),
					content varchar(500),
					img_path varchar(300),
					page_index int,
					PRIMARY KEY (id)
					);";
	
	$mysqli->query ( $create_table );

	$length = count($_FILES);
	// echo 'files size '.$length;
	//echo 'files name '.$_FILES ['image'] ['tmp_name'];
	$resultArr = array();
	$arr = array();
	$content_index = 0;
	$thumbnail_path;
	foreach ($_FILES as $file) {
		// echo 'filename '.$file['name'];
		
	// echo 'temp filename : '.$_FILES['image']['tmp_name'].'<br>';
	// echo 'temp filesize : '.$_FILES['image']['size'].'<br>';
		$time = explode ( ' ', microtime () );
		$fileName = $time [1] . substr ( $time [0], 2, 6 ) . '.' ."jpg";
		$filePath = $uploadImageFolder;//$_SERVER ['DOCUMENT_ROOT'] . '/web_test/image_test/upload_image/';
		$thumbPath = $thumbnailFolder;//$_SERVER ['DOCUMENT_ROOT'] . '/web_test/image_test/thumbnails/';
		list ( $width, $height ) = getimagesize ( $file['tmp_name'] );

		// 9. 업로드 파일을 새로 만든 파일명으로 변경 및 이동
		if(!is_dir($filePath)){
			@mkdir($filePath);
		}

		if (move_uploaded_file ( $file ['tmp_name'], $filePath . $fileName )) {

			if ($_POST['thumbnail_index'] == $content_index) {
				$thumbnail_path = $fileName;
			}
			
			$query = sprintf ( "INSERT INTO pages
			(post_id, page_index, content, img_path)
			VALUES ('%s', '%s', '%s', '%s')", $post_id, $content_index, $_POST ["content".$content_index++], $fileName);
	
			$mysqli->query ( $query );
			array_push($arr, array("file_url"=>$fileName, 
				"width" => $width,
				"height" => $height));
			
		} else {
			exit ( "업로드 실패" );
			$resultArr = array (
					'ret_val' => "fail"
			);

			return $resultArr;
		} // if
	}

	$resultArr = array (
					'ret_val' => "success",
					'file_info' => $arr,
					'thumbnail_path' => $thumbnail_path 
			);
	
		
	return $resultArr;
}

// $value = "nothing";
// // echo 'start: ';
// if (isset ( $_POST ["title"] ) && $_FILES ['image'] ['size'] > 0) {
	
// 	$value = saveImageFile();
	
// } else {
// 	$value = "Missing argument";
// }
// // return JSON array
// exit ( json_encode ( $value ) );
?>