<?php
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
function saveHTMLFile() {
	include "serverconfig.php";
	include "./image_test/dbconfig.php";
// 	echo "saveHTMLFile filename: ".$_FILES ['html_file'] ['name'];
	if (! isset ( $_FILES ['html_file'] )) {
		exit ( "업로드 파일 존재하지 않음" );
	}
	
	if ($_FILES ['html_file'] ['error'] > 0) {
		switch ($_FILES ['html_file'] ['error']) {
			case 1 :
				exit ( "php.ini 파일의 upload_max_filesize 설정값을 초과함(업로드 최대용량 초과)" );
			case 2 :
				exit ( "Form에서 설정된 MAX_FILE_SIZE 설정값을 초과함(업로드 최대용량 초과)" );
			case 3 :
				exit ( "파일 일부만 업로드 됨" );
			case 4 :
				exit ( "업로드된 파일이 없음" );
			case 6 :
				exit ( "사용가능한 임시폴더가 없음" );
			case 7 :
				exit ( "디스크에 저장할수 없음" );
			case 8 :
				exit ( "파일 업로드가 중지됨" );
			default :
				exit ( "시스템 오류가 발생" );
		} // switch
	}
	$ableExt = array (
			'html'
	);
	$path = pathinfo ( $_FILES ['html_file'] ['name'] );
	$ext = strtolower ( $path ['extension'] );
	
	if (! in_array ( $ext, $ableExt )) {
		exit ( "허용되지 않는 확장자입니다." );
	}
	
	$mysqli = new mysqli ( $dbhost, $dbusr, $dbpass, $dbname );
	if ($mysqli->connect_errno) {
		echo "Failed to connect to MySQL: (" . $mysqli->connect_errno . ") " . $mysqli->connect_error;
	}
	
	$create_table = "CREATE TABLE if not exists posts (
					id int auto_increment,
					user_id varchar(30) DEFAULT \"khwan07\",
					title varchar(100),
					upload_filename varchar(100),
					db_filename varchar(100),
					filepath varchar(100),
					filesize int(8),
					file_type VARCHAR(40),
					upload_date DATETIME DEFAULT CURRENT_TIMESTAMP,
					thumb_img_path varchar(100),
					PRIMARY KEY (id)
					);";
	
	$mysqli->query ( $create_table );
	
	do {
	
		// 6. 새로운 파일명 생성(마이크로타임과 확장자 이용)
		$time = explode ( ' ', microtime () );
		$fileName = $time [1] . substr ( $time [0], 2, 6 ) . '.' . strtoupper ( $ext );
	
		// 중요 이미지의 경우 웹루트(www) 밖에 위치할 것을 권장(예제 편의상 아래와 같이 설정)
		$filePath = 'http://localhost:8080/web_test/image_test/upload_html/';//$_SERVER ['DOCUMENT_ROOT'] . '/web_test/image_test/upload_html/';
		$fileServerPath = $_SERVER ['DOCUMENT_ROOT'] . '/web_test/image_test/upload_html/';
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
	$upload_date = date ( "Y-m-d H:i:s" );
	
	// create and save thumbnail
	// save thumbnail
	$imageServerPath = $_SERVER ['DOCUMENT_ROOT'] . '/web_test/image_test/upload_image/';
	$thumbServerPath = $_SERVER ['DOCUMENT_ROOT'] . '/web_test/image_test/thumbnails/';
	$thumbPath = 'http://localhost:8080/web_test/image_test/thumbnails/';
	$imageName = $_POST['thumb_img_url'];

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
		exit ( "thumbnail 실패" );
	} // if
	
	$mysqli->autocommit ( false );
	
	$query = sprintf ( "INSERT INTO posts
		(user_id, title, upload_filename,db_filename,filepath,filesize,file_type,upload_date,thumb_img_path)
		VALUES ('khwan07', '%s', '%s','%s','%s','%s','%s','%s','%s')", $upload_filename, $upload_filename, $fileName, $filePath, $file_size, $file_type, $upload_date, $thumbPath.$imageName);
	
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
			exit ( "업로드 실패" );
		} // if
	}
	$html_saving_info = array (
			"id" => $insert_id
	);
	$mysqli->close ();
	return $html_saving_info;
}

$value = "An error has occurred";
// echo 'start: ';
if (isset ( $_POST ["title"] ) && $_FILES ['html_file'] ['size'] > 0) {
	
	$value = saveHTMLFile();
	
} else {
	$value = "Missing argument";
}
// return JSON array
exit ( json_encode ( $value ) );
?>