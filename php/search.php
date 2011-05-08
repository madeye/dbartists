<?php

$cookie_jar_index = 'cookie.txt';

$url = "http://www.douban.com/search?cat=2002&search_text=".urlencode(urldecode($_REQUEST['p']));
$ch = curl_init();
curl_setopt($ch, CURLOPT_URL, $url);
curl_setopt($ch, CURLOPT_HEADER, 0);
curl_setopt($ch, CURLOPT_COOKIEJAR, $cookie_jar_index);
curl_setopt($ch, CURLOPT_COOKIEFILE, $cookie_jar_index);
curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
$content = curl_exec($ch);
curl_close($ch);

if ($content != false) {

    //TOP
    echo "#genre\n";
    echo "search\n";

    // artists
    echo "#artists\n";
    $pattern = '@<a class="nbg"[\s\S]+?.jpg"@';
    preg_match_all($pattern, $content, $matches);
    for ($i = 0; $i < count($matches[0]); $i++) {
        $match = $matches[0][$i];

        // artist name
        preg_match('/(title=")[\s\S]+?(")/', $match, $name);
        $name = preg_replace('/title="/', '', $name[0]);
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
