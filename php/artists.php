<?php

$cookie_jar_index = 'cookie.txt';

$genre = $_REQUEST['g'];
$page = $_REQUEST['p'];
$url = "http://music.douban.com/artists/genre_page/".$genre."/".$page;
$ch = curl_init();
curl_setopt($ch, CURLOPT_URL, $url);
curl_setopt($ch, CURLOPT_HEADER, 0);
curl_setopt($ch, CURLOPT_COOKIEJAR, $cookie_jar_index);
curl_setopt($ch, CURLOPT_COOKIEFILE, $cookie_jar_index);
curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
$content = curl_exec($ch);
curl_close($ch);

if ($content != false) {
    // name
    echo "#genre\n";
    $pattern = '@豆瓣音乐人-(\S)*</title>@';
    preg_match_all($pattern, $content, $matches);
    $match = $matches[0][0];
    $match = trim($match);
    $match = preg_replace('/豆瓣音乐人-/', '', $match);
    $match = preg_replace('@</title>@', '', $match);
    echo $match."\n";

    // artists
    echo "#artists\n";
    $pattern = '@<a class="artist_photo"[\s\S]+?./>@';
    preg_match_all($pattern, $content, $matches);
    for ($i = 0; $i < count($matches[0]); $i++) {
        $match = $matches[0][$i];

        // artist name
        preg_match('/(alt=")[\s\S]+?(")/', $match, $name);
        $name = preg_replace('/alt="/', '', $name[0]);
        $name = preg_replace('/"/', '', $name);
		$name = preg_replace('/&amp;/', '&', $name);
        echo $name."\n";

        // artist pic
        preg_match('/(src=")[\s\S]+?(")/', $match, $pic);
        $pic = preg_replace('/src="/', '', $pic[0]);
        $pic = preg_replace('/"/', '', $pic);
        echo $pic."\n";

        // artist url
        preg_match('/(href=")[\s\S]+?(")/', $match, $url);
        $url = preg_replace('/href="/', '', $url[0]);
        $url = preg_replace('/"/', '', $url);
        echo $url."\n";
        
    }

}


?>
