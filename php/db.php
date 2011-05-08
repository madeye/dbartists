<?php
$url = $_REQUEST['url'];
$ch = curl_init();
curl_setopt($ch, CURLOPT_URL, $url);
curl_setopt($ch, CURLOPT_HEADER, 1);
curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
$content = curl_exec($ch);
curl_close($ch);

if ($content != false) {

    // artist
    echo "#artist\n";
    $pattern = '@<title>[\s\S]+?</title>@';
    preg_match_all($pattern, $content, $matches);
    for ($i = 0; $i < count($matches[0]); $i++) {
        $match = $matches[0][$i];
        $match = preg_replace('/<title>/', '', $match);
        $match = preg_replace('/的小站/', '', $match);
        $match = preg_replace('@</title>@', '', $match);
        $match = preg_replace('@[\r\n]*@', '', $match);
		$match = preg_replace('/&amp;/', '&', $match);
        echo trim($match)."\n";
    }


    // titles
    echo "#titles\n";
    $pattern = '/encodeURIComponent\("[\s\S]+?"\),/';
    preg_match_all($pattern, $content, $matches);
    for ($i = 0; $i < count($matches[0]); $i++) {
        $match = $matches[0][$i];
        $name = preg_replace('/encodeURIComponent\("/', '', $match);
        $name = preg_replace('/"\),/', '', $name);
        echo $name."\n";
    }

    // urls
    echo "#urls\n";
    $pattern = "/url : '(\S)*'/";
    preg_match_all($pattern, $content, $matches);
    for ($i = 0; $i < count($matches[0]); $i++) {
        $match = $matches[0][$i];
        $split = preg_split("/'/", $match);
        $url = $split[1];
        echo base64_decode($url)."\n";
    }
}
?>
