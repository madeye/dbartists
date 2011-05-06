<?php
$url = $_REQUEST['url'];
$f = new SaeFetchurl();
$content = $f->fetch($url);
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
