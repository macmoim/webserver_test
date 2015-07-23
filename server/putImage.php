<?php

function saveImageFile() {
	$arr = array();
	// echo 'temp filename : '.$_FILES['image']['tmp_name'].'<br>';
	// echo 'temp filesize : '.$_FILES['image']['size'].'<br>';
		$time = explode ( ' ', microtime () );
		$fileName = $time [1] . substr ( $time [0], 2, 6 ) . '.' ."jpg";
		$filePath = $_SERVER ['DOCUMENT_ROOT'] . '/web_test/image_test/upload_image/';
		// 9. 업로드 파일을 새로 만든 파일명으로 변경 및 이동
		if (move_uploaded_file ( $_FILES ['image'] ['tmp_name'], $filePath . $fileName )) {
			
			
			$arr = array("file_url"=>$fileName);
		} else {
			exit ( "업로드 실패" );
		} // if
	return $arr;
}

$value = "nothing";
// echo 'start: ';
if (isset ( $_POST ["title"] ) && $_FILES ['image'] ['size'] > 0) {
	
	$value = saveImageFile();
	
} else {
	$value = "Missing argument";
}
// return JSON array
exit ( json_encode ( $value ) );
?>