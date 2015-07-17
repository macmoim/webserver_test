<?php

function save_image() {
	include "dbconfig.php";
	// normally this info would be pulled from a database.
	// build JSON array.
	
	// 1. 업로드 파일 존재여부 확인
	if (! isset ( $_FILES ['image'] )) {
		exit ( "업로드 파일 존재하지 않음" );
	} // if
	  
	// 2. 업로드 오류여부 확인
	if ($_FILES ['image'] ['error'] > 0) {
		switch ($_FILES ['image'] ['error']) {
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
	} // if
	  
	// 3. 업로드 제한용량 체크(서버측에서 5M로 제한)
	if ($_FILES ['image'] ['size'] > 5242880) {
		exit ( "업로드 최대용량 초과" );
	} // if
	  
	// 4. 업로드 허용 확장자 체크(보편적인 jpg,jpeg,gif,png,bmp 확장자만 필터링)
	$ableExt = array (
			'jpg',
			'jpeg',
			'gif',
			'png',
			'bmp' 
	);
	$path = pathinfo ( $_FILES ['image'] ['name'] );
	$ext = strtolower ( $path ['extension'] );
	
	if (! in_array ( $ext, $ableExt )) {
		exit ( "허용되지 않는 확장자입니다." );
	} // if
	  
	// 5. MIME를 통해 이미지파일만 허용(2차 확인)
	  // $ableImage = array('image/jpeg', 'image/JPG', 'image/X-PNG', 'image/PNG', 'image/png', 'image/x-png', 'image/gif','image/bmp','image/pjpeg');
	  // if(!in_array($_FILES['image']['type'], $ableImage)) {
	  // exit("지정된 이미지만 허용됩니다.");
	  // }//if
	  
	// 6. DB에 저장할 이미지 정보 가져오기(width,height 값 활용)
	$img_size = getimagesize ( $_FILES ['image'] ['tmp_name'] );
	
	// DB연결
	$mysqli = new mysqli ( $dbhost, $dbusr, $dbpass, $dbname );
	if ($mysqli->connect_errno) {
		echo "Failed to connect to MySQL: (" . $mysqli->connect_errno . ") " . $mysqli->connect_error;
	}
	
	$create_table = "CREATE TABLE if not exists image_files( 
					id int auto_increment, 
					upload_filename varchar(100), 
					db_filename varchar(100), 
					filepath varchar(100), 
					filesize int(8),
					file_type VARCHAR(40),
					upload_date DATETIME DEFAULT CURRENT_TIMESTAMP,
					width int(8), 
					height int(8), 
					PRIMARY KEY (id) 
					);";
	
	$mysqli->query ( $create_table );
	
	// do~while: 새로만든 파일명이 중복일경우 반복하기 위한 루틴
	do {
		
		// 6. 새로운 파일명 생성(마이크로타임과 확장자 이용)
		$time = explode ( ' ', microtime () );
		$fileName = $time [1] . substr ( $time [0], 2, 6 ) . '.' . strtoupper ( $ext );
		
		// 중요 이미지의 경우 웹루트(www) 밖에 위치할 것을 권장(예제 편의상 아래와 같이 설정)
		$filePath = $_SERVER ['DOCUMENT_ROOT'] . '/web_test/image_test/upload_image/';
		
		// 7. 생성한 파일명이 DB내에 존재하는지 체크
		$query = sprintf ( "SELECT no FROM image_files WHERE db_filename = '%s'", $fileName );
		$result = $mysqli->query ( $query );
		if ($result === NULL) {
			break;
		}
		
		// 생성한 파일명이 중복하는 경우 새로 생성해서 체크를 반복(동시저장수가 대량이 아닌경우 중복가능 희박)
	} while ( $result != NULL && $result->num_rows > 0 );
	
	// db에 저장할 정보 가져옴
	$upload_filename = $mysqli->real_escape_string ( $_FILES ['image'] ['name'] );
	$file_size = $_FILES ['image'] ['size'];
	$file_type = $_FILES ['image'] ['type'];
	$upload_date = date ( "Y-m-d H:i:s" );
	// $ip = $_SERVER['REMOTE_ADDR'];
	
	// 오토커밋 해제
	$mysqli->autocommit ( false );
	
	// 8. db에 업로드 파일 및 새로 생성된 파일정보등을 저장
	$query = sprintf ( "INSERT INTO image_files
		(upload_filename,db_filename,filepath,filesize,file_type,upload_date,width,height) 
		VALUES ('%s','%s','%s','%s','%s','%s',%d,%d)", $upload_filename, $fileName, $filePath, $file_size, $file_type, $upload_date, $img_size [0], $img_size [1] );
	
	$mysqli->query ( $query );
	
	if ($mysqli->error) {
		echo "Failed to insert image db: (" . $mysqli->error . ") ";
	}
	$insert_id = $mysqli->insert_id;
	
	// DB에 파일내용 저장 성공시
	if ($mysqli->affected_rows > 0) {
		
		// 9. 업로드 파일을 새로 만든 파일명으로 변경 및 이동
		if (move_uploaded_file ( $_FILES ['image'] ['tmp_name'], $filePath . $fileName )) {
			
			// 10. 성공시 db저장 내용을 적용(커밋)
			$mysqli->commit ();
		} else {
			// 실패시 db에 저장했던 내용 취소를 위한 롤백
			$mysqli->rollback ();
			exit ( "업로드 실패" );
		} // if
	} // if
	
	$image_saving_info = array (
			"id" => $insert_id 
	);
	$mysqli->close ();
	
	return $image_saving_info;
}

$possible_url = array (
		"save_image" 
);

$value = "An error has occurred";

// echo 'request temp title : '.$_FILES['image']['tmp_name'].'<br>';
// echo 'request 11 title : '.$_POST["title"].'<br>';

if (isset ( $_POST ["title"] ) && $_FILES ['image'] ['size'] > 0) {
	
	// echo 'temp filename : '.$_FILES['image']['tmp_name'].'<br>';
	// echo 'temp filesize : '.$_FILES['image']['size'].'<br>';
	$value = save_image ();
} else {
	$value = "Missing argument";
}
// return JSON array
exit ( json_encode ( $value ) );
?>