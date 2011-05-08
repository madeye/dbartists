<?php

$cookie_jar_index = 'cookie.txt';

$url = "http://music.douban.com/chart";
$ch = curl_init();
curl_setopt($ch, CURLOPT_URL, $url);
curl_setopt($ch, CURLOPT_HEADER, 0);
curl_setopt($ch, CURLOPT_COOKIEJAR, $cookie_jar_index);
curl_setopt($ch, CURLOPT_COOKIEFILE, $cookie_jar_index);
curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
$content = curl_exec($ch);
curl_close($ch);

if ($content != false) {

    // pop
    echo "#genre\n";
    echo "POP\n";
	
	// pop div
	$pattern = '@<div id="pop_artist"[\s\S]*</div>@';
	preg_match($pattern, $content, $pop);
	
    // artists
    echo "#artists\n";
    $pattern = '@<a class="face"[\s\S]+?</h3>@';
    preg_match_all($pattern, $pop[0], $matches);
    for ($i = 0; $i < count($matches[0]); $i++) {
        $match = $matches[0][$i];

        // artist name
		preg_match('@<div class="intro">[\s\S]+?</h3>@', $match, $intro);
        preg_match('@(/">)[\s\S]+?(</a>)@', $intro[0], $name);
        $name = preg_replace('@/">@', '', $name[0]);
        $name = preg_replace('@</a>@', '', $name);
		$name = preg_replace('/&amp;/', '&', $name);
        echo $name."\n";

        // artist pic
        preg_match('/(<img src=")[\s\S]+?(")/', $match, $pic);
        $pic = preg_replace('/<img src="/', '', $pic[0]);
        $pic = preg_replace('/"/', '', $pic);
        echo $pic."\n";

        // artist url
        preg_match('/(<a href=")[\s\S]+?(")/', $match, $url);
        $url = preg_replace('/<a href="/', '', $url[0]);
        $url = preg_replace('/"/', '', $url);
        echo $url."\n";
        
    }

}


?>
