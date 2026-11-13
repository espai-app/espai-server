<?php

namespace app\espai\wordpress;

class Production {

  public $id;
  public $productionType;
  public $title;
  public $description;
  public $durationInMinutes;
  public $rating;
  public $recommendedAge;

  public $metadata;
  public $versions;
  public $attachments;

  public static function getById($productionId) {
    $espai = Espai::getInstance();

    $httpClient = HttpClient::getInstance($espai->baseUrl, $espai->apiKey);
    $production = $httpClient->GET(
            '/webservice/season/' . $espai->seasonId . '/productions/' . $productionId);

    return self::of($production);
  }

  public static function list($productionTypeId) {
    $espai = Espai::getInstance();

    $httpClient = HttpClient::getInstance($espai->baseUrl, $espai->apiKey);
    $productions = $httpClient->GET('/webservice/season/' . $espai->seasonId . '/productions?productionTypeId=' . $productionTypeId);

    return self::convertList($productions);
  }

  public static function listForVenue($venueSlug) {
    $espai = Espai::getInstance();

    $httpClient = HttpClient::getInstance($espai->baseUrl, $espai->apiKey);
    $productions = $httpClient->GET('/webservice/season/' . $espai->seasonId . '/venues/' . $venueSlug . '/productions');

    return self::convertList($productions);
  }

  public static function getPoster($productionId, $width = 400, $height = 300) {
    $espai = Espai::getInstance();
    return $espai->baseUrl . 'webservice/attachments/first/fit/production/' . $productionId . '/Poster/' . $width . '/' . $height;
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
          case 'metadata':
            $result->$key = [];
            foreach ($value as $metadata) {
              switch ($metadata->productionMetaField->fieldType) {
                //case 'TAG':
                //  $result->$key[$metadata->productionMetaField->label] = json_decode($metadata->value);
                //  break;
                default:
                  if (property_exists($metadata, 'value')) {
                    $result->$key[$metadata->productionMetaField->label] = $metadata->value;
                  }
                  break;
              }
            }
            break;
          case 'versions':
            $result->$key = [];
            foreach ($value as $version) {
              $result->$key[] = ProductionVersion::of($version);
            }
            break;
          case 'attachments':
            $result->$key = [];
            foreach ($value as $attachment) {
              $currentAttachment = Attachment::of($attachment);
              if (!isset($result->$key[$currentAttachment->category])) {
                $result->$key[$currentAttachment->category] = [];
              }
              $result->$key[$currentAttachment->category][] = $currentAttachment;
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
