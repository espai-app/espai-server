<?php

namespace app\espai\wordpress;

class Production {

  public $id;
  public $title;
  public $description;
  public $durationInMinutes;
  public $fromAge;
  public $toAge;
  public $rating;
  public $tags;
  public $attachments;

  public $productionCountries;
  public $productionYear;
  public $director;
  public $starring;
  public $book;

  public static function getById($productionId) {
    $espai = Espai::getInstance();

    $httpClient = HttpClient::getInstance($espai->baseUrl, $espai->apiKey);
    $production = $httpClient->GET(
            '/webservice/season/' . $espai->seasonId . '/productions/' . $productionId);

    return self::of($production);
  }

  public static function list() {
    $espai = Espai::getInstance();

    $httpClient = HttpClient::getInstance($espai->baseUrl, $espai->apiKey);
    $productions = $httpClient->GET('/webservice/season/' . $espai->seasonId . '/productions');

    return self::convertList($productions);
  }

  private static function convertList($productionList) {
    $result = [];
    foreach ($productionList as $e) {
      $result[] = self::of($e);
    }

    return $result;
  }

  public static function of($production) {
    $result = new Production();

    foreach ($production as $key => $value) {
      if (property_exists($result, $key)) {
        switch ($key) {
          case "tags":
            $result->$key = [];
            foreach ($value as $tagName => $list) {
              $result->$key[$tagName] = $list;
            }
            break;
          case "attachments":
            $result->$key = [];
            foreach ($value as $categoryName => $list) {
              $convertedList = [];
              foreach ($list as $attachment) {
                $convertedList[] = Attachment::of($attachment);
              }
              $result->$key[$categoryName] = $convertedList;
            }
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
