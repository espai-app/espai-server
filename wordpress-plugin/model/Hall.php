<?php

namespace app\espai\wordpress;

class Hall {

  public $id;
  public $name;
  public $hallCapacities;

  public static function listForVenue($venueSlug) {
    $espai = Espai::getInstance();

    $httpClient = HttpClient::getInstance($espai->baseUrl, $espai->apiKey);
    $hallList = $httpClient->GET('/webservice/season/' . $espai->seasonId . '/venues/' . $venueSlug . '/halls');

    return self::convertList($hallList);
  }

  private static function convertList($hallList) {
    $result = [];
    foreach ($hallList as $e) {
      $result[] = self::of($e);
    }

    return $result;
  }

  public static function of($hall) {
    $result = new Hall();

    foreach ($hall as $key => $value) {
      if (property_exists($result, $key)) {
        switch ($key) {
          case "hallCapacities":
            $result->hallCapacities = HallCapacity::convertList($value);
            break;
          default:
            $result->$key = $value;
            break;
        }
      }
    }

    return $result;
  }

}

