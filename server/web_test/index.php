<?php

ini_set('display_startup_errors',1);
ini_set('display_errors',1);
error_reporting(-1);

require __DIR__.'/vendor/autoload.php';


$app = new \Slim\Slim();
$app->contentType('text/html; charset=utf-8');

$app->get('/', function () {
    echo "Welcome to Slim";
});

$app->get('/post/:id', function ($id) {
	include 'post.php';
	$value = rest_get($id);
	exit ( json_encode ( $value ) );
});

$app->get('/post/user/:user_id', function ($user_id) {
	include 'post.php';
	$value = get_post($user_id);
	exit ( json_encode ( $value ) );
});

$app->post('/post', function () use ($app) {
	include 'post.php';
	$value = rest_post();
	exit ( json_encode ( $value ) );

});

$app->post('/post/image', function () use ($app) {
	include 'putImage.php';
	$value = saveImageFile();
	exit ( json_encode ( $value ) );

});

$app->get('/star/:user_id/:post_id', function ($user_id, $post_id) {
	include 'star.php';
	$value = rest_get($user_id, $post_id);
	exit ( json_encode ( $value ) );
});

$app->post('/star', function () use ($app) {
	include 'star.php';
	$value = rest_post();
	exit ( json_encode ( $value ) );
});

$app->get('/like/:user_id(/)(/:post_id)', function ($user_id, $post_id = NULL) {
	include 'like.php';
	if (isset($post_id)) {
		$value = rest_get($user_id, $post_id);
	} else {
		$value = get_my_like($user_id);
	}
	
	exit ( json_encode ( $value ) );
});

$app->post('/like', function () use ($app) {
	include 'like.php';
	$value = rest_post();
	exit ( json_encode ( $value ) );
});

$app->get('/comment/:post_id', function ($post_id) {
	include 'comment.php';
	$value = rest_get($post_id);
	exit ( json_encode ( $value ) );
});

$app->post('/comment', function () use ($app) {
	include 'comment.php';
	$value = rest_post();
	exit ( json_encode ( $value ) );
});

$app->get('/profile/:user_id', function ($user_id) {
	include 'profile.php';
	$value = rest_get($user_id);
	exit ( json_encode ( $value ) );
});

$app->post('/profile', function () use ($app) {
	include 'profile.php';
	$value = rest_post();
	exit ( json_encode ( $value ) );
});

$app->post('/profile/image', function () use ($app) {
	include 'putProfileImage.php';
	$value = saveImageFile();
	exit ( json_encode ( $value ) );
});

$app->get('/thumbImageList/:category(/)(/:timeStamp)', function ($category, $timeStamp=NULL) {
	include __DIR__.'\image_test\thumbnailImage.php';
	if (isset($byTimeStamp)) {
		$value = rest_get_image_list_by_timestamp($category, $timeStamp);
	} else {
		$value = rest_get_image_list($category);
	}
	
	exit ( json_encode ( $value ) );
});

$app->run();
?>