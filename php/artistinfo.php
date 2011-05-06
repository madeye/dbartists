<?php
$url = $_REQUEST['url'];
$f = new SaeFetchurl();
$content = $f->fetch($url);
if ($content != false) {

    // artist
    echo "#artist\n";
    $pattern = '@<title>[\s\S]+?</title>@';
	preg_match($pattern, $content, $matches);
    $match = $matches[0];
    $match = preg_replace('/<title>/', '', $match);
    $match = preg_replace('/的小站/', '', $match);
    $match = preg_replace('@</title>@', '', $match);
    $match = preg_replace('@[\r\n]*@', '', $match);
    echo trim($match)."\n";


    // genre
    echo "#genre\n";
    $pattern = '@genre_page[\s\S]+?</a>@';
	preg_match($pattern, $content, $matches);
    $match = $matches[0];
    preg_match('/(>)[\s\S]+?(<)/', $match, $genre);
    $genre = preg_replace('/</', '', $genre[0]);
    $genre = preg_replace('/>/', '', $genre);
    echo $genre."\n";

    // member
    echo "#member\n";
    $pattern = '@>成员</span>[\s\S]+?</a>@';
	preg_match($pattern, $content, $matches);
    $match = $matches[0];
    preg_match('/(">)[\s\S]+?(<)/', $match, $member);
    $member = preg_replace('/">/', '', $member[0]);
    $member = preg_replace('/</', '', $member);
    echo trim($member)."\n";

    // company
    echo "#company\n";
    $pattern = '@唱片公司</span>[\s\S]+?<br/>@';
	preg_match($pattern, $content, $matches);
    $match = $matches[0];
    preg_match('/(：)[\s\S]+?(<)/', $match, $company);
    $company = preg_replace('/：/', '', $company[0]);
    $company = preg_replace('/</', '', $company);
    $company = preg_replace('/&amp;/', ' ', $company);
    echo trim($company)."\n";
}
?>
