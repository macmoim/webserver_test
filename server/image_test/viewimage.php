<?php
if (isset ( $_REQUEST ['id'] )) {
	// get the file with the id from database
	include "dbconfig.php";
	
	$mysqli = new mysqli ( $dbhost, $dbusr, $dbpass, $dbname );
	if ($mysqli->connect_errno) {
		echo "Failed to connect to MySQL: (" . $mysqli->connect_errno . ") " . $mysqli->connect_error;
	}
	
	$id = $_REQUEST ['id'];
	echo "id=" . $id;
	$query = "SELECT upload_filename, db_filename, filepath, filesize, width, height, file_type, upload_date
	                   FROM image_files WHERE id = '$id'";
	
	$result = $mysqli->query ( $query );
	if ($mysqli->error) {
		echo "fail to select dberror = " . $mysqli->error;
	}
	
	$row = mysqli_fetch_array ( $result, MYSQLI_ASSOC );
	
	echo "dbfilename : " . $row ['db_filename'];
	
	$name = $_SERVER ['DOCUMENT_ROOT'] . "/web_test/image_test/upload_image/" . $row ['db_filename'];
	$imagesize = getimagesize ( $name );
	// echo "<td><a href='javascript:image_view(\"{$row['db_filename']}\",{$imagesize[0]},{$imagesize[1]});'>".htmlspecialchars($row['upload_filename'])."</a></td>";
	showImage ( $row ['db_filename'] );
	
	// echo '<img src="data:'.$row['file_type'].';'.$row['db_filename'].'"/>'; // 이미지 뿌리기
	$mysqli->close ();
} else {
	echo "no id";
}
function showImage($image_name) {
	if (isset ( $image_name )) {
		
		// 이미지 파일명
		
		// 이미지 전체경로를 포함한 이미지명
		$image_path = $_SERVER ['DOCUMENT_ROOT'] . '/web_test/image_test/upload_image/' . $image_name;
		echo "image_path : " . $image_path;
		
		// 넘어온 이미지경로의 존재여부와 파일여부 확인
		if (file_exists ( $image_path ) && is_file ( $image_path )) {
			
			// 넘어온 파일 확장자 추출
			$tmp_name = pathinfo ( $image_path );
			$ext = strtolower ( $tmp_name ['extension'] );
			
			// 지정된 확장자만 보여주도록 필터링
			if ($ext == 'jpg' || $ext = 'gif' || $ext = 'png' || $ext = 'bmp') {
				
				// 이미지 크기정보와 사이즈를 얻어옴
				$img_info = getimagesize ( $image_path );
				$filesize = filesize ( $image_path );
				
				echo "<div><a href=\"url\"><img src=\"'$image_name'\" border=\"0\" /></a></div>";
				
				// 이미지 전송을 위한 헤더설정
				// header("Content-Type: {$img_info['mime']}\n");
				// header("Content-Disposition: inline;filename='$image_name'\n");
				// header("Content-Length: $filesize\n");
				
				// 이미지 내용을 읽어들임
				// readfile($image_path);
			} // if
		} // if
	} // if
}
?>