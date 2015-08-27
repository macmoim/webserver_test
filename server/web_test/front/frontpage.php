<?php

function shareHtml($htmlUrl) {

	$serverPath = $_SERVER ['DOCUMENT_ROOT'] ."/web_test/image_test/upload_html/";
	// echo 'path: '.$serverPath.$htmlUrl;

	if(!file_exists($serverPath.$htmlUrl)) {  // file check
		echo 'file not found';
		exit();
	}

	htmlHeader();

	$toDeleteString = ":8080";
	$toReplaceString = "";

	$myfile = fopen($serverPath.$htmlUrl, "r") or die("Unable to open file!");
	// Output one line until end-of-file
	while(!feof($myfile)) {
	  // echo str_replace($toDeleteString, $toReplaceString, fgets($myfile)) . "<br>";
		echo fgets($myfile) . "<br>";
	}
	fclose($myfile);

	htmlFooter();

}


function htmlHeader() {
	echo "<!DOCTYPE html>\n";
	echo "<html>\n";
	echo "<head>\n";
	    echo "<meta name=\"viewport\" content=\"initial-scale=1.0, user-scalable=no\">\n";
	    echo "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n";
	    echo "<link rel=\"stylesheet\" type=\"text/css\" href=\"normalize.css\">\n";
	    echo "<link rel=\"stylesheet\" type=\"text/css\" href=\"style_viewer.css\">\n";
	echo "</head>\n";
	echo "<body>\n";
	echo "<div id=\"editor\" contentEditable=\"false\"></div>\n";
	echo "<script type=\"text/javascript\" src=\"rich_editor.js\"></script>\n";
}

function htmlFooter() {
	echo "</body>\n";
	echo "</html>\n";
}
?>