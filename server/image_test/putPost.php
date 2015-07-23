<?php


$value = "An error has occurred";

// echo 'request temp title : '.$_FILES['image']['tmp_name'].'<br>';
// echo 'request 11 title : '.$_POST["title"].'<br>';

if (isset ( $_POST ['test'] )) {
	
	// echo 'temp filename : '.$_FILES['image']['tmp_name'].'<br>';
	// echo 'temp filesize : '.$_FILES['image']['size'].'<br>';
	$ids = array();
	$ret = array();
	$arr = $_POST['test'];
	echo "test ".array_values($arr);
	foreach($_POST['test'] as $val) {
		$ids[] = $val;
	}
	$ids = implode(',', $ids);
	$value = $ret = array("first"=>$ids[0],
			"second"=>$ids[1],
			"third"=>$ids[2],
			"forth"=>$ids[3],
			"fifth"=>$ids[4]
	);
} else {
	$value = "Missing argument";
}
// return JSON array
exit ( json_encode ( $value ) );
?>