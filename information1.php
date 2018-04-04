<?php
        $con = mysqli_connect('localhost', 'phpmyadmin', 'kwonmysql','CSE');  //mysql에 접속
        mysqli_set_charset($con, "utf8");  //인코딩
        $result = mysqli_query($con, 'SELECT * FROM Information2');  //학사공지 테이블에 연결
        $response = array();  //배열형태로 저장

        while($row = mysqli_fetch_array($result)){
                array_push($response, array("number"=>$row[0], "title"=>$row[1], "writer"=>$row[2], "time"=>$row[3], "url"=>$row[4], "no"=>$row[5]));  //각 항목에 대한 정보를 row배열에 입력
        }
        header('Content-Type: application/json; charset=utf8');
        $json = json_encode(array("response"=>$response), json_pretty_print+json_unescaped_unicode);
        echo $json;
        mysqli_close($con);
?>
