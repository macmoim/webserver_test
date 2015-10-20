<?php

// $post_id = $_GET ['post_id'];

// include $_SERVER ['DOCUMENT_ROOT']."/web_test/image_test/dbconfig.php";
// include $_SERVER ['DOCUMENT_ROOT']."/web_test/serverconfig.php";
	
// shareHtml(9);

function getPostData($slqi, $id) {
	$sql_query = "SELECT posts.user_id as p_user_id, title, posts.upload_date, category, thumb_img_path, rank,
					mylike.like_sum, profiles.user_name, profiles.profile_img_url, 
                    profiles.user_email,
					post_c.comment_sum,
                    mypage.page_sum,
					mypage.content, mypage.img_path
					FROM posts 
					LEFT JOIN ( SELECT post_id, count(like_bool) as like_sum FROM likes WHERE post_id = '$id' AND like_bool = '1' ) mylike
					ON mylike.post_id = posts.id
					LEFT JOIN profiles 
                    			ON profiles.user_id = posts.user_id
                    LEFT JOIN ( SELECT post_id, count(comment) as comment_sum from post_comment WHERE post_id = '$id' ) post_c
								ON post_c.post_id = posts.id 
                    LEFT JOIN ( SELECT post_id, count(page_index) as page_sum, GROUP_CONCAT(content SEPARATOR '||;') as content, GROUP_CONCAT(img_path SEPARATOR '||;') as img_path from pages WHERE post_id = '$id' ) mypage
                    			ON posts.id = mypage.post_id
					WHERE posts.id = '$id'";

	if ($result = $slqi->query ( $sql_query )) {
		$row = $result->fetch_array ();
		if (isset ( $row ['title'] )) {
			// echo 'user_id '.$row['p_user_id'];
			$post_info = array (
					"user_id" => $row ['p_user_id'],
					"user_name" => $row ['user_name'],
					"title" => $row ['title'],
					"upload_date" => $row ['upload_date'],
					"category" => $row ['category'],
					"thumb_img_path" => $row['thumb_img_path'],
					"rank" => $row['rank'],
					"like_sum" => $row['like_sum'],
					"profile_img_url" => $row['profile_img_url'],
					"page_content" => $row['content'],
					"img_path" => $row['img_path'],
					"user_email" => $row['user_email'],
					"comment_sum" => $row['comment_sum'],
					"page_sum" => $row['page_sum'],
					"ret_val" => "success"
			);
		} else {
			// echo 'fail to get user info';
			echo 'fail to get post info\n';
			$post_info = array (
					'ret_val' => "fail",
					'ret_detail' => "fail to get post info"
			);
		}
	} else {
		echo 'no post data in db\n';
		$post_info = array (
					'ret_val' => "fail",
					'ret_detail' => "no post data in db"
		);
	}

	return $post_info;
}



// select
// $sql_query = "select  from member where user_id = '$id' and password='$pw'";
// if ($result = $mysqli->query ( $sql_query )) {
// 	$row = $result->fetch_array ();
// 	if (isset ( $row ['user_id'] )) {
// 		echo $row ['user_id'];
// 		$_SESSION ['is_logged'] = 'YES';
// 		$_SESSION ['user_id'] = $id;
// 	} else {
// 		echo 'fail to login';
// 		$_SESSION ['is_logged'] = 'NO';
// 		$_SESSION ['user_id'] = '';
// 	}
// 	header ( "location:login_done.php" );
// }

// $mysqli->close ();

function shareHtml($post_id) {
	include $_SERVER ['DOCUMENT_ROOT']."/web_test/image_test/dbconfig.php";
	include $_SERVER ['DOCUMENT_ROOT']."/web_test/serverconfig.php";
	$mysqli = new mysqli ( $dbhost, $dbusr, $dbpass, $dbname );
	if ($mysqli->connect_errno) {
		echo "Failed to connect to MySQL: (" . $mysqli->connect_errno . ") " . $mysqli->connect_error;
	}

	$post_info = getPostData($mysqli, $post_id);
	$content_arr = explode('||;', $post_info['page_content']);
	$img_arr = explode('||;', $post_info['img_path']);



	$mysqli->close ();

	
	$serverPath = $uploadHTMLFolder;//$_SERVER ['DOCUMENT_ROOT'] ."/web_test/image_test/upload_html/";
	// echo 'path: '.$serverPath.$htmlUrl;

	// if(!file_exists($serverPath.$htmlUrl)) {  // file check
	// 	echo 'file not found';
	// 	exit();
	// }

	htmlHeader();

	// echo("<script>console.log('PHP: ".count($content_arr)."');</script>\n");

	showFirstPage($post_info);

	for ($i = 0; $i < count($content_arr); $i++) {
		$hasNext = true;
		$hasPrev = true;
		if ($i == 0) {
			$hasPrev = false;
		}
		if ($i == (count($content_arr)-1)) {
			$hasNext = false;
		}
		showContents($i, $content_arr[$i], $img_arr[$i], $hasPrev, $hasNext);
	}



	// $toDeleteString = ":8080";
	// $toReplaceString = "";

	// $myfile = fopen($serverPath.$htmlUrl, "r") or die("Unable to open file!");
	// // Output one line until end-of-file
	// while(!feof($myfile)) {
	//   // echo str_replace($toDeleteString, $toReplaceString, fgets($myfile)) . "<br>";
	// 	echo fgets($myfile) . "<br>";
	// }
	// fclose($myfile);

	// echo 'user_id ' . $post_info['user_id'];
	


	htmlFooter();

}

function showFirstPage($local_post_info) {
	include $_SERVER ['DOCUMENT_ROOT']."/web_test/serverconfig.php";
	$index = -1;
	$page_id = 'page'.$index;
	echo "<div data-role=\"page\" data-theme=\"b\" data-position=\"fixed\" id=\"$page_id\">\n";

	echo "<div data-role=\"main\" class=\"ui-content\">\n";



	$div_img_id = 'img'.$index;
	echo "<div id=\"$div_img_id\" style=\"float:left;\" contentEditable=\"false\">\n";

	$img_path = $local_post_info['profile_img_url'];
	echo "<img class=\"circular-image\" src=\"$img_path\" alt=\"profileimg\" height=\"50\" width=\"50\">\n";
	echo "</div>\n";

	echo "<div style=\"float:left; margin-left:30px;\">\n";
	echo "<span readonly style=\"vertical-align:middle;\">\n";
	echo $local_post_info['user_name'];
	echo "<br>";
	echo $local_post_info['user_email'];
	echo "</span>\n";
	echo "</div>\n";



	// echo "<span readonly style=\"margin:10px; vertical-align:middle;\">\n";
	// echo $local_post_info['user_email'];
	// echo "</span>\n";
	
	
	echo "</div>\n";

	echo "<div style=\"float:none; margin-left:20px;\" contentEditable=\"false\">\n";
	$img_path = $uploadImageFolderForClient.$local_post_info['thumb_img_path'];
	echo "<img src=\"$img_path\" alt=\"foodimg\" height=\"180\" width=\"240\">\n";
	echo "<p style=\"font: bold; font-size:2em\"> ";
	echo $local_post_info['title'];
	echo " </p>\n";
	echo "<p> ";
	echo $local_post_info['upload_date'];
	echo " </p>\n";

	echo "<p> ";
	$likeCount = $local_post_info['like_sum'] == null ? 0 : $local_post_info['like_sum'];
	$score = $local_post_info['rank'] == null ? 0 : $local_post_info['rank'];
	echo $likeCount.' likes  '.$score.' scores';
	echo " </p>\n";

	echo "<p> ";
	$commentCount = $local_post_info['comment_sum'] == null ? 0 : $local_post_info['comment_sum'];
	echo $local_post_info['page_sum'].' contents  '.$commentCount.' comments';
	echo " </p>\n";

	echo "</div>\n";

	$div_content_id = 'content'.$index;
	echo "<div id=\"$div_content_id\" align=\"right\" style=\"float:none; margin-right:20px;\" contentEditable=\"false\">\n";
	$next_page_id = '#page'.($index+1);
	echo "<a href=\"$next_page_id\" data-transition=\"slide\">Next</a>\n";
	echo "</div>\n";
	
	echo "</div>\n";
}

function showContents($index, $content, $img, $hasPrev, $hasNext) {
	include $_SERVER ['DOCUMENT_ROOT']."/web_test/serverconfig.php";
	// echo 'showContents '.$index."\n";
	$page_id = 'page'.$index;
	echo "<div data-role=\"page\" data-theme=\"b\" data-position=\"fixed\" id=\"$page_id\">\n";

	echo "<div data-role=\"main\" class=\"ui-content\">\n";
	$div_img_id = 'img'.$index;
	echo "<div id=\"$div_img_id\" contentEditable=\"false\">\n";

	$temp_local_server_path = $uploadImageFolderForClient;
	$img_path = $temp_local_server_path.$img;
	echo "<img class=\"displayed\" src=\"$img_path\" alt=\"foodimg\" height=\"225\" width=\"300\">\n";
	echo "</div>\n";

	$div_content_id = 'content'.$index;
	echo "<div id=\"$div_content_id\" contentEditable=\"false\">\n";

	
	echo "<textarea readonly rows=\"10\" cols=\"50\">\n";
	echo $content;
	echo "</textarea>\n";
	// if ($hasPrev) {
	echo "<div align=\"left\" style=\"float:left;\">\n";
		$prev_page_id = '#page'.($index-1);
		echo "<a href=\"$prev_page_id\" data-transition=\"slide\" data-direction=\"reverse\" >Prev</a>\n";
	echo "</div>\n";
	// }
	if ($hasNext) {
		echo "<div align=\"right\" style=\"float:right;\">\n";
		$next_page_id = '#page'.($index+1);
		echo "<a href=\"$next_page_id\" data-transition=\"slide\">Next</a>\n";	
		echo "</div>\n";
	}
	echo "</div>\n";
	
	echo "</div>\n";
	
	echo "</div>\n";
}


function htmlHeader() {
	echo "<!DOCTYPE html>\n";
	echo "<html>\n";
	echo "<head>\n";
	    echo "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, user-scalable=no\">\n";
	    echo "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n";
	    // echo "<link rel=\"stylesheet\" type=\"text/css\" href=\"normalize.css\">\n";
	    echo "<link rel=\"stylesheet\" type=\"text/css\" href=\"/web_test/front/style_viewer.css\">\n";
	    echo "<link rel=\"stylesheet\" href=\"http://code.jquery.com/mobile/1.4.5/jquery.mobile-1.4.5.min.css\">\n";
		echo "<script src=\"http://code.jquery.com/jquery-1.11.3.min.js\"></script>\n";
		echo "<script src=\"http://code.jquery.com/mobile/1.4.5/jquery.mobile-1.4.5.min.js\"></script>\n";
		echo "<script type=\"text/javascript\" src=\"/web_test/front/frontpage2.js\"></script>\n";
	echo "</head>\n";
	echo "<body>\n";
	// echo "<div id=\"editor\" contentEditable=\"false\"></div>\n";
	// echo "<script type=\"text/javascript\" src=\"rich_editor.js\"></script>\n";
}

function htmlFooter() {
	echo "</body>\n";
	echo "</html>\n";
}

?> 