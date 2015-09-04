<?php
ini_set('display_startup_errors',1);
ini_set('display_errors',1);
error_reporting(-1);

function rest_get($id) {
	$post_info = array ();
	
	// normally this info would be pulled from a database.
	// build JSON array.
	
	include "./image_test/dbconfig.php";
	
	$mysqli = new mysqli ( $dbhost, $dbusr, $dbpass, $dbname );
	if ($mysqli->connect_errno) {
		echo "Failed to connect to MySQL: (" . $mysqli->connect_errno . ") " . $mysqli->connect_error;
	}
	$sql_query = "SELECT posts.user_id as p_user_id, title, upload_filename, db_filename, filepath, upload_date, category, thumb_img_path, rank,
					count(like_bool) as likes_sum, profiles.user_name
					FROM posts 
					LEFT JOIN likes
					ON likes.post_id = posts.id AND likes.like_bool = '1'
					LEFT JOIN profiles 
                    			ON profiles.user_id = posts.user_id
					WHERE posts.id = '$id'";

	if ($result = $mysqli->query ( $sql_query )) {
		$row = $result->fetch_array ();
		if (isset ( $row ['db_filename'] )) {
			$post_info = array (
					"user_id" => $row ['p_user_id'],
					"user_name" => $row ['user_name'],
					"title" => $row ['title'],
					"upload_filename" => $row ['upload_filename'],
					"db_filename" => $row ['db_filename'],
					"filepath" => $row ['filepath'],
					"upload_date" => $row ['upload_date'],
					"category" => $row ['category'],
					"thumb_img_path" => $row['thumb_img_path'],
					"rank" => $row['rank'],
					"like_sum" => $row['likes_sum'],
					"ret_val" => "success"
			);
		} else {
			// echo 'fail to get user info';
			$post_info = array (
					'ret_val' => "fail",
					'ret_detail' => "fail to get post info"
			);
		}
	} else {
		$post_info = array (
					'ret_val' => "fail",
					'ret_detail' => "no post data in db"
		);
	}
	
	$mysqli->close ();
	
	return $post_info;
}

function get_post($user_id) {
	$post_info = array ();
	
	// normally this info would be pulled from a database.
	// build JSON array.
	
	include "./image_test/dbconfig.php";
	
	$mysqli = new mysqli ( $dbhost, $dbusr, $dbpass, $dbname );
	if ($mysqli->connect_errno) {
		echo "Failed to connect to MySQL: (" . $mysqli->connect_errno . ") " . $mysqli->connect_error;
	}
	$sql_query = "SELECT id,user_id, title, upload_filename, db_filename, filepath, upload_date, thumb_img_path
	                   FROM posts WHERE user_id = '$user_id'";
	if ($result = $mysqli->query ( $sql_query )) {
	
		if (count ( $result ) > 0) {
			
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

			$image_list = array (
					'my_post' => $post_info,
					'ret_val' => "success"
			);
		}else{
			// echo 'fail to get user info';
			$image_list = array (
					'ret_val' => "fail",
					'ret_detail' => "fail to get my post info"
			);
		}
	} else {
		$image_list = array (
					'ret_val' => "fail",
					'ret_detail' => "no my post data in db"
			);
	}

	$mysqli->close ();
	
	return $image_list;
}

function rest_search($text) {
	$image_list = array ();
	
	// normally this info would be pulled from a database.
	// build JSON array.
	
	include "./image_test/dbconfig.php";
	
	$mysqli = new mysqli ( $dbhost, $dbusr, $dbpass, $dbname );
	if ($mysqli->connect_errno) {
		echo "Failed to connect to MySQL: (" . $mysqli->connect_errno . ") " . $mysqli->connect_error;
	}
	$sql_query = "SELECT id, user_id, title, upload_filename, db_filename, filepath, upload_date, category, thumb_img_path
	                   FROM posts WHERE title LIKE '%$text%'";
	if ($result = $mysqli->query ( $sql_query )) {
		
		if (count ( $result ) > 0) {
			$post_info = array();
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

			$image_list = array (
					'post_info' => $post_info,
					'ret_val' => "success"
			);
		}else{
			// echo 'fail to get user info';
			$image_list = array (
					'ret_val' => "fail",
					'ret_detail' => "fail to get my post info"
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

function resize_image($file, $w, $h, $crop = FALSE) {
	list ( $width, $height ) = getimagesize ( $file );
	$r = $width / $height;
	if ($crop) {
		if ($width > $height) {
			$width = ceil ( $width - ($width * abs ( $r - $w / $h )) );
		} else {
			$height = ceil ( $height - ($height * abs ( $r - $w / $h )) );
		}
		$newwidth = $w;
		$newheight = $h;
	} else {
		if ($w / $h > $r) {
			$newwidth = $h * $r;
			$newheight = $h;
		} else {
			$newheight = $w / $r;
			$newwidth = $w;
		}
	}
	$src = imagecreatefromjpeg ( $file );
	$dst = imagecreatetruecolor ( $newwidth, $newheight );
	imagecopyresampled ( $dst, $src, 0, 0, 0, 0, $newwidth, $newheight, $width, $height );

	return $dst;
}
function rest_post() {
	include "serverconfig.php";
	include "./image_test/dbconfig.php";
	$debug_msg = '';
// 	echo "saveHTMLFile filename: ".$_FILES ['html_file'] ['name'];
	if (! isset ( $_FILES ['html_file'] )) {
		$debug_msg =  "업로드 파일 존재하지 않음";
		return $debug_msg;
	}
	
	if ($_FILES ['html_file'] ['error'] > 0) {
		switch ($_FILES ['html_file'] ['error']) {
			case 1 :
				$debug_msg = "php.ini 파일의 upload_max_filesize 설정값을 초과함(업로드 최대용량 초과)";
				return $debug_msg;
			case 2 :
				$debug_msg = "Form에서 설정된 MAX_FILE_SIZE 설정값을 초과함(업로드 최대용량 초과)";
				return $debug_msg;
			case 3 :
				$debug_msg = "파일 일부만 업로드 됨";
				return $debug_msg;
			case 4 :
				$debug_msg = "업로드된 파일이 없음";
				return $debug_msg;
			case 6 :
				$debug_msg = "사용가능한 임시폴더가 없음";
				return $debug_msg;
			case 7 :
				$debug_msg = "디스크에 저장할수 없음";
				return $debug_msg;
			case 8 :
				$debug_msg = "파일 업로드가 중지됨";
				return $debug_msg;
			default :
				$debug_msg = "시스템 오류가 발생";
				return $debug_msg;
		} // switch
	}
	$ableExt = array (
			'html'
	);
	$path = pathinfo ( $_FILES ['html_file'] ['name'] );
	$ext = strtolower ( $path ['extension'] );
	
	if (! in_array ( $ext, $ableExt )) {
		$debug_msg = "허용되지 않는 확장자입니다.";
		return $debug_msg;
	}
	
	$mysqli = new mysqli ( $dbhost, $dbusr, $dbpass, $dbname );
	if ($mysqli->connect_errno) {
		echo "Failed to connect to MySQL: (" . $mysqli->connect_errno . ") " . $mysqli->connect_error;
	}
	
	$create_table = "CREATE TABLE if not exists posts (
					id int auto_increment,
					user_id varchar(30),
					title varchar(100),
					upload_filename varchar(100),
					db_filename varchar(100),
					filepath varchar(100),
					filesize int(8),
					file_type VARCHAR(40),
					upload_date DATETIME DEFAULT CURRENT_TIMESTAMP,
					thumb_img_path varchar(100),
					category varchar(20),
					rank float,
					PRIMARY KEY (id)
					);";
	
	$mysqli->query ( $create_table );
	
	do {
	
		// 6. 새로운 파일명 생성(마이크로타임과 확장자 이용)
		$time = explode ( ' ', microtime () );
		$fileName = $time [1] . substr ( $time [0], 2, 6 ) . '.' . strtoupper ( $ext );
	
		// 중요 이미지의 경우 웹루트(www) 밖에 위치할 것을 권장(예제 편의상 아래와 같이 설정)
		$filePath = $uploadHTMLFolderForClient;//'http://localhost:8080/web_test/image_test/upload_html/';//$_SERVER ['DOCUMENT_ROOT'] . '/web_test/image_test/upload_html/';
		$fileServerPath = $uploadHTMLFolder;//$_SERVER ['DOCUMENT_ROOT'] . '/web_test/image_test/upload_html/';
		if(!is_dir($fileServerPath)){
			@mkdir($fileServerPath);
		}
	
		// 7. 생성한 파일명이 DB내에 존재하는지 체크
		$query = sprintf ( "SELECT no FROM image_files WHERE db_filename = '%s'", $fileName );
		$result = $mysqli->query ( $query );
		if ($result === NULL) {
			break;
		}
	
		// 생성한 파일명이 중복하는 경우 새로 생성해서 체크를 반복(동시저장수가 대량이 아닌경우 중복가능 희박)
	} while ( $result != NULL && $result->num_rows > 0 );
	
	// db에 저장할 정보 가져옴
	$upload_filename = $mysqli->real_escape_string ( $_FILES ['html_file'] ['name'] );
	$file_size = $_FILES ['html_file'] ['size'];
	$file_type = $_FILES ['html_file'] ['type'];
	// set time to Seoul.
	date_default_timezone_set("Asia/Seoul");
	$upload_date = date ( "Y-m-d H:i:s" );
	
	// create and save thumbnail
	$thumbPath = $thumbnailFolderForClient;//'http://localhost:8080/web_test/image_test/thumbnails/';
	$thumb_url = isset($_POST['thumb_img_url']) ? $_POST['thumb_img_url'] : null;
	$thumbimagename = save_thumbnail($thumb_url);

	$mysqli->autocommit ( false );
	
	$query = sprintf ( "INSERT INTO posts
		(user_id, title, upload_filename,db_filename,filepath,filesize,file_type,upload_date,thumb_img_path,category)
		VALUES ('%s', '%s', '%s','%s','%s','%s','%s','%s','%s','%s')", $_POST ["user_id"], $_POST ["title"], $upload_filename, $fileName, $filePath, $file_size, $file_type, $upload_date, $thumbimagename, $_POST ["category"]);
	
	$mysqli->query ( $query );
	
	if ($mysqli->error) {
		echo "Failed to insert posts db: (" . $mysqli->error . ") ";
	}
	$insert_id = $mysqli->insert_id;
	
	
	
	if ($mysqli->affected_rows > 0) {
		// 9. 업로드 파일을 새로 만든 파일명으로 변경 및 이동
		if (move_uploaded_file ( $_FILES ['html_file'] ['tmp_name'], $fileServerPath . $fileName )) {
				
				
			$mysqli->commit ();
		} else {
			$mysqli->rollback ();
			$debug_msg = "업로드 실패";
			return $debug_msg;
		} // if
	}
	$html_saving_info = array (
			"id" => $insert_id
	);
	$mysqli->close ();

	if (isset($_POST['images_name'])) {
		$html_saving_info['ret_detail'] = insert_post_images($insert_id, $_POST['images_name']);	
	}
	

	$html_saving_info['ret_val'] = "success";
	return $html_saving_info;
}

function save_thumbnail($thumb_img_url) {
	include "serverconfig.php";
	// create and save thumbnail
	// save thumbnail
	$imageServerPath = $uploadImageFolder;//$_SERVER ['DOCUMENT_ROOT'] . '/web_test/image_test/upload_image/';
	$thumbServerPath = $thumbnailFolder;//$_SERVER ['DOCUMENT_ROOT'] . '/web_test/image_test/thumbnails/';
	$defaultImagePath = $defaultImageFolder;//$_SERVER ['DOCUMENT_ROOT'] . '/web_test/image_test/';
	$imageName;

	if (isset($thumb_img_url)) {
		$imageName = $thumb_img_url;
	} else {
		$imageName = 'default_backdrop_img.jpg';
		if(!is_dir($imageServerPath)){
			@mkdir($imageServerPath);
		}	

	// copy default image file 
		if(!file_exists($imageServerPath.$imageName)) {  // file check
		      if(!copy($defaultImagePath.$imageName, $imageServerPath.$imageName)) { //copy 
		            echo "<center>default image file copy error</center>"; // fail 
		      } else if(file_exists($imageServerPath.$imageName)) { // success

		      } 
	 	}
	}

	if(!is_dir($thumbServerPath)){
			@mkdir($thumbServerPath);
	}
	
	$exif_data = exif_read_data ( $imageServerPath . $imageName, 0, true );
	
	$exist_thumbnail = false;
	foreach ( $exif_data as $key => $section ) {
		if (in_array ( "THUMBNAIL", $section )) {
			$exist_thumbnail = true;
			break;
		}
	}
	if ($exist_thumbnail) {
		$thumbData = exif_thumbnail($thumbServerPath.$imageName, $thumb_width, $thumb_height, $thumb_type);
		$thumb = imagecreatefromstring($thumbData);
	} else {
		$thumb_width = 200;
		$thumb_height = 200;
		$thumb = resize_image ( $imageServerPath . $imageName, 200, 200 );
	}
	if(!is_dir($thumbServerPath)){
		@mkdir($thumbServerPath);
	}
	if (imagejpeg($thumb,$thumbServerPath.$imageName,100)) {
	} else {
		// 실패시 db에 저장했던 내용 취소를 위한 롤백
		echo  "thumbnail 실패";
		exit ();
	} // if
	return $imageName;
	
}

function insert_post_images($post_id, $images_name) {
	include "./image_test/dbconfig.php";
	include "serverconfig.php";

	$img_name_arr = explode(":", $images_name);

	$mysqli = new mysqli ( $dbhost, $dbusr, $dbpass, $dbname );
	if ($mysqli->connect_errno) {
		echo "Failed to connect to MySQL: (" . $mysqli->connect_errno . ") " . $mysqli->connect_error;
	}
	
	$create_table = "CREATE TABLE if not exists post_images (
					id int auto_increment,
					post_id int,
					image_filename varchar(100) unique,
					PRIMARY KEY (id)
					);";
	
	$mysqli->query ( $create_table );

	$imageServerPath = $uploadImageFolder;//$_SERVER ['DOCUMENT_ROOT'] . '/web_test/image_test/upload_image/';

	$ret = "";
	$arr_size = count($img_name_arr);
	foreach ($img_name_arr as $imgname) {
		$query = sprintf ( "INSERT INTO post_images
										(post_id, image_filename)
										VALUES ('%s', '%s')", $post_id, $imgname);
	
		$mysqli->query ( $query );
		
		if ($mysqli->error) {
			$ret =  "Failed to insert post_images db: (" . $mysqli->error . ") ";
		}
		$insert_id = $mysqli->insert_id;
		$ret .= "id:".$insert_id." success. ";
	}
	
	
	return $ret;
}

function delete_post_images($post_id) {
	include "./image_test/dbconfig.php";
	

	$mysqli = new mysqli ( $dbhost, $dbusr, $dbpass, $dbname );
	if ($mysqli->connect_errno) {
		echo "Failed to connect to MySQL: (" . $mysqli->connect_errno . ") " . $mysqli->connect_error;
	}
	
	$query = "SELECT image_filename FROM post_images WHERE post_id = '$post_id'";

	if ($result = $mysqli->query ( $query )) {
		
		if (count($result) > 0) {
			
			$images_name = array ();
			while ( $row = $result->fetch_assoc () ) {
				
				
				array_push ( $images_name, $row ['image_filename']);
				
				
			}

			delete_images_by_name($images_name);
			
			

			$sql_query = "DELETE FROM post_images WHERE post_id = '$post_id'";

			$mysqli->query ( $sql_query );
			if ($mysqli->error) {
				echo 'delete post images error : '.$mysqli->error;
				return FALSE;
			}

			return TRUE;
		} else {
			//echo 'delete post images no select result';
			return TRUE;
		}
		return FALSE;
	}
}

function delete_images_by_name($images_name) {
	include "serverconfig.php";
	$imageServerPath = $uploadImageFolder;//$_SERVER ['DOCUMENT_ROOT'] . '/web_test/image_test/upload_image/';
	$thumbServerPath = $thumbnailFolder;//$_SERVER ['DOCUMENT_ROOT'] . '/web_test/image_test/thumbnails/';
	foreach ($images_name as $var) {
		if (file_exists($imageServerPath.$var)) {
			// do not delete default image.
			if (strcmp("default_backdrop_img.jpg", $var)) {
				unlink($imageServerPath.$var);	
			} else {

			}
			
		}	
		if (file_exists($thumbServerPath.$var)) {
			// do not delete default image.
			if (strcmp("default_backdrop_img.jpg", $var)) {
				unlink($thumbServerPath.$var);	
			} else {

			}
			
		}
	}
}

function rest_delete($id) {
	include "serverconfig.php";
	$post_to_delete_info = array ();
	$thumbFolderPath = $thumbnailFolder;//$_SERVER ['DOCUMENT_ROOT'] . '/web_test/image_test/thumbnails/';
	$htmlFolderPath = $uploadHTMLFolder;//$_SERVER ['DOCUMENT_ROOT'] . '/web_test/image_test/upload_html/';
	$imgfilename = array();
	
	// normally this info would be pulled from a database.
	// build JSON array.
	
	include "./image_test/dbconfig.php";
	
	$mysqli = new mysqli ( $dbhost, $dbusr, $dbpass, $dbname );
	if ($mysqli->connect_errno) {
		echo "Failed to connect to MySQL: (" . $mysqli->connect_errno . ") " . $mysqli->connect_error;
	}

	$sql_query_select = "SELECT db_filename, thumb_img_path
	                   FROM posts WHERE id = '$id'";
	if ($result = $mysqli->query ( $sql_query_select )) {
		$row = $result->fetch_array ();
		if (isset ( $row ['db_filename'] )) {
			$imgfilename = explode("/", $row['thumb_img_path']);
			$post_to_delete_info = array (
					"db_filename" => $htmlFolderPath.$row ['db_filename'],
					"thumb_img_path" => $thumbFolderPath.end($imgfilename)
			);
		} else {
			echo 'fail to get post_to_delete_info';
		}
	}

	if (count($post_to_delete_info) > 0) {
		// delete HTML file
		if (file_exists($post_to_delete_info['db_filename'])) {
				unlink($post_to_delete_info['db_filename']);
		}
		// delete thumbnail image file
		if (file_exists($post_to_delete_info['thumb_img_path'])) {
			// do not delete defaul thumbnail image.
			if (strcmp("default_backdrop_img.jpg", end($imgfilename))) {
				unlink($post_to_delete_info['thumb_img_path']);
			} else {
				
			}
		}
	}
	

	$ret = array();
	if (delete_post_images($id)) {
		$sql_query = "DELETE FROM posts WHERE id = '$id'";

		$mysqli->query ( $sql_query );
		

		if ($mysqli->affected_rows > 0) {
			
			$ret['ret_val'] = "success";
		} else {
			$ret['ret_val'] = "fail";
		}
		
		
	} else {
		$ret['ret_val'] = "fail";
	}

	$mysqli->close ();
	
	
	
	return $ret;
}

function rest_put($post_id, $keys, $values, $images_name, $thumbnail_img_url) {
	include "./image_test/dbconfig.php";
	include "serverconfig.php";
	
	$mysqli = new mysqli ( $dbhost, $dbusr, $dbpass, $dbname );
	if ($mysqli->connect_errno) {
		echo "Failed to connect to MySQL: (" . $mysqli->connect_errno . ") " . $mysqli->connect_error;
	}
	// delete old html file
	$sql_query = "SELECT db_filename FROM posts WHERE id = '$post_id'";

	if ($result = $mysqli->query ( $sql_query )) {
		$row = $result->fetch_array ();
		if (isset ( $row ['db_filename'] )) {
			delete_html_file($row ['db_filename']);
		} else {
			echo 'fail to select old html file from db';
		}
	}

	if (isset($images_name)) {
		// delete old image file and add new image file
		
		$img_name_arr = explode(':', $images_name);
		
		
		$img_to_delete = array();
		$img_to_update = array();

		$query = "SELECT image_filename FROM post_images WHERE post_id = '$post_id'";

		if ($result = $mysqli->query ( $query )) {
			
			if ($result->num_rows > 0) {
				$db_result = array();
				while ( $row = $result->fetch_assoc () ) {
					array_push ( $db_result, $row['image_filename']);
				}
				
				
				
				$img_to_delete = array_diff($db_result, $img_name_arr);
				
				$img_to_update = array_diff($img_name_arr, $db_result);
				
				

				delete_images_by_name($img_to_delete);
				
			} else {
				//echo 'delete post images no select result';
				$img_to_update = $img_name_arr;
			}

		}

		if (count($img_to_delete) > 0) {
			foreach ($img_to_delete as $imgname) {
				

				$sql_query = "DELETE FROM post_images WHERE post_id = '$post_id' 
									AND image_filename = '$imgname'";

				$mysqli->query ( $sql_query );
				
				if ($mysqli->error) {
					echo "Failed to delete post images : (" . $mysqli->error . ") ";
				}
				
			}
		}

		if (count($img_to_update) > 0) {
			foreach ($img_to_update as $imgname) {
				$query = sprintf ( "INSERT INTO post_images
												(post_id, image_filename)
												VALUES ('%s', '%s')", $post_id, $imgname);
			
				$mysqli->query ( $query );
				
				if ($mysqli->error) {
					echo "Failed to insert post_images db: (" . $mysqli->error . ") ";
				}
				
			}
		}
		
	}
	
	save_thumbnail($thumbnail_img_url);



	$thumbUserPath = $thumbnailFolderForClient;//'http://localhost:8080/web_test/image_test/thumbnails/';

	// update post
	$sql_query = "UPDATE posts SET ";

	$arr_size = count($keys);
	for ($count=0; $count<$arr_size; $count++) {
		if (strcmp($keys[$count], "thumb_img_path")) {
			// not equal
			$sql_query .=" $keys[$count] = '$values[$count]'";
		} else {
			// equal. add thumbnail server path to thumb_img_path
			$thumb_path = $thumbUserPath.$values[$count];
			$sql_query .=" $keys[$count] = '$thumb_path'";
		}
		
		if ($count != $arr_size-1) {
			$sql_query .= ", ";
		}
	}
	$sql_query .= " WHERE id = '$post_id'";

	$mysqli->query ( $sql_query );

	if ($mysqli->error) {
		echo "Failed to update posts db: (" . $mysqli->error . ") ";
	}

	$ret = array();
	if ($mysqli->affected_rows == 0) {
		$ret['ret_val'] = "fail";
		
		
		
	} else {
		$ret['ret_val'] = "success";
		
		
	}

	$mysqli->close ();

	return $ret;
}

function delete_html_file($filename_old) {
	include "serverconfig.php";
	$fileServerPath = $uploadHTMLFolder;//$_SERVER ['DOCUMENT_ROOT'] . '/web_test/image_test/upload_html/';

	if(!is_dir($fileServerPath)){
		return FALSE;
	}

	if (file_exists($fileServerPath.$filename_old)) {
		unlink($fileServerPath.$filename_old);
		return TRUE;
	}
	return FALSE;

}

function rest_get_images_name($post_id) {
	$image_name_list = array ();
	
	// normally this info would be pulled from a database.
	// build JSON array.
	
	include "./image_test/dbconfig.php";
	
	$mysqli = new mysqli ( $dbhost, $dbusr, $dbpass, $dbname );
	if ($mysqli->connect_errno) {
		echo "Failed to connect to MySQL: (" . $mysqli->connect_errno . ") " . $mysqli->connect_error;
	}
	$sql_query = "SELECT image_filename
	                   FROM post_images WHERE post_id = '$post_id'";
	if ($result = $mysqli->query ( $sql_query )) {
		
		if (count($result) > 0) {
			
			$image_name_info = array();
			while ( $row = $result->fetch_assoc () ) {
				
				
				array_push ( $image_name_info, array (
					"image_filename" => $row ['image_filename'],
					
				) );
				
				
			}
			// array_push($user_list, 'user_info');
			$image_name_list = array (
					'image_name_info' => $image_name_info 
			);
		} else {
			echo 'fail to get user info';
		}
	}
	
	$mysqli->close ();
	
	return $image_name_list;
}

function rest_post_html_update() {
	include "serverconfig.php";
	if (! isset ( $_FILES ['html_file'] )) {
		return ( "업로드 파일 존재하지 않음" );
	}
	
	if ($_FILES ['html_file'] ['error'] > 0) {
		switch ($_FILES ['html_file'] ['error']) {
			case 1 :
				return ( "php.ini 파일의 upload_max_filesize 설정값을 초과함(업로드 최대용량 초과)" );
			case 2 :
				return ( "Form에서 설정된 MAX_FILE_SIZE 설정값을 초과함(업로드 최대용량 초과)" );
			case 3 :
				return ( "파일 일부만 업로드 됨" );
			case 4 :
				return ( "업로드된 파일이 없음" );
			case 6 :
				return ( "사용가능한 임시폴더가 없음" );
			case 7 :
				return ( "디스크에 저장할수 없음" );
			case 8 :
				return ( "파일 업로드가 중지됨" );
			default :
				return ( "시스템 오류가 발생" );
		} // switch
	}
	$ableExt = array (
			'html'
	);
	$path = pathinfo ( $_FILES ['html_file'] ['name'] );
	$ext = strtolower ( $path ['extension'] );
	
	if (! in_array ( $ext, $ableExt )) {
		echo  "허용되지 않는 확장자입니다.";
		exit ();
	}

	$time = explode ( ' ', microtime () );
	$fileName = $time [1] . substr ( $time [0], 2, 6 ) . '.' . strtoupper ( $ext );

	// 중요 이미지의 경우 웹루트(www) 밖에 위치할 것을 권장(예제 편의상 아래와 같이 설정)
	$fileServerPath = $uploadHTMLFolder;//$_SERVER ['DOCUMENT_ROOT'] . '/web_test/image_test/upload_html/';
	if(!is_dir($fileServerPath)){
		@mkdir($fileServerPath);
	}

	$ret = array();
	if (move_uploaded_file ( $_FILES ['html_file'] ['tmp_name'], $fileServerPath . $fileName )) {
				
		$ret['ret_val'] = "success";	
		$ret['updated_filename'] = $fileName;
		
	} else {
		
		$ret['ret_val'] = "fail";
	} // if
		
	return $ret;
}
?>