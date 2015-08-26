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

$app->get('/post/image/:id', function ($id) {
	include 'post.php';
	$value = rest_get_images_name($id);
	exit ( json_encode ( $value ) );
});

$app->post('/post', function () use ($app) {
	include 'post.php';
	$value = rest_post();
	exit ( json_encode ( $value ) );
});

$app->post('/post/html/update', function () use ($app) {
	include 'post.php';
	$value = rest_post_html_update();
	exit ( json_encode ( $value ) );
});

$app->post('/post/image', function () use ($app) {
	include 'putImage.php';
	$value = saveImageFile();
	exit ( json_encode ( $value ) );

});

$app->delete('/post/:id', function ($id) {
	include 'post.php';
	$value = rest_delete($id);
	exit ( json_encode ( $value ) );
});

$app->put('/post/:id', function ($id) use ($app) {
	include 'post.php';
	$put_vars = $app->request->put();
	$images_name = null;
	if (isset($put_vars['images_name'])) {
		$images_name = $put_vars['images_name'];

		unset($put_vars['images_name']);
	}
	
	$thumb_img_url = null;
	if (array_key_exists('thumb_img_path', $put_vars)) {
		$thumb_img_url = $put_vars['thumb_img_path'];
	}
	$keys = array_keys($put_vars);
	$values = array_values($put_vars);
	$value = rest_put($id, $keys, $values, $images_name, $thumb_img_url);
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

$app->put('/star/:id/:star', function ($id, $star) use ($app) {
	$put_vars = $app->request->put();
	include 'star.php';
	$value = rest_put($id, $star, $put_vars['post_id']);
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

$app->put('/like/:id/:like', function ($id, $like) use ($app) {
	$put_vars = $app->request->put();
	include 'like.php';
	$value = rest_put($put_vars['id'], $put_vars['like']);
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
	$put_vars = $app->request->post();

	$keys = array_keys($put_vars);
	$values = array_values($put_vars);
	$value = rest_post($keys, $values);
	exit ( json_encode ( $value ) );
});

$app->put('/profile/:id', function ($id) use ($app) {
	include 'profile.php';
	$put_vars = $app->request->put();

	$keys = array_keys($put_vars);
	$values = array_values($put_vars);
	$value = rest_put($id, $keys, $values);
	exit ( json_encode ( $value ) );
});

$app->post('/profile/image', function () use ($app) {
	include 'putProfileImage.php';
	$value = saveImageFile();
	exit ( json_encode ( $value ) );
});

$app->post('/profile/image/update', function () use ($app) {
	include 'putProfileImage.php';
	$value = updateImageFile($app->request->post('filename_old'));	
	exit ( json_encode ( $value ) );
});

$app->get('/thumbImageList/:category(/)(/:timeStamp)', function ($category, $timeStamp=NULL) {
	include __DIR__.'\image_test\thumbnailImage.php';
	if (isset($timeStamp)) {
		$value = rest_get_image_list_by_timestamp($category, $timeStamp);
	} else {
		$value = rest_get_image_list($category);
	}
	
	exit ( json_encode ( $value ) );
});

$app->run();
?>