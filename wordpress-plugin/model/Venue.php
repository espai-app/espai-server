<?php

namespace app\espai\wordpress;

class Venue {
  
  public $id;
  public $name;
  public $address;
  public $postcode;
  public $city;
  public $phone;
  public $email;
  
  public static function getById($venueId) {
    $espai = Espai::getInstance();
    
    $httpClient = HttpClient::getInstance($espai->baseUrl, $espai->apiKey);
    $venue = $httpClient->GET(
            '/webservice/season/' . $espai->seasonId . '/venues/' . $venueId);
    
    return venue::of($venue);
  } 
  
  public static function list() {
    $espai = Espai::getInstance();
    
    $httpClient = HttpClient::getInstance($espai->baseUrl, $espai->apiKey);
    $venues = $httpClient->GET('/webservice/season/' . $espai->seasonId . '/venues');
    
    return self::convertList($venues);
  }
  
  private static function convertList($venueList) {
    $result = [];
    foreach ($venueList as $e) {
      $result[] = self::of($e);
    }
    
    return $result;
  }
  
  public static function of($venue) {
    if ($venue == null) {
      return null;
    }
    
    $result = new Venue();
    
    foreach ($venue as $key => $value) {
      if (property_exists($result, $key)) {
        $result->$key = $value;
      }
    }
    
    return $result;
  }
  
  public static function compare($v1, $v2) {
    return strtolower($v1->city . $v1->name) <=> strtolower($v2->city . $v2->name);
  }
}