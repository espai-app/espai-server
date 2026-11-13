<?php

namespace app\espai\wordpress;

class Season {

  public $id;
  public $name;
  public $start;
  public $end;

  public static function get() {
    $espai = Espai::getInstance();

    $httpClient = HttpClient::getInstance($espai->baseUrl, $espai->apiKey);
    $season = $httpClient->GET('/webservice/season/' . $espai->seasonId);

    return self::of($season);
  }

  public static function of($season) {
    $result = new Season();

    foreach ($season as $key => $value) {
      $result->$key = $value;
    }

    return $result;
  }

}

