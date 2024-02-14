<?php

namespace app\espai\wordpress;

class Reservation {
  
  public $id;
  public $company;
  public $givenName;
  public $surname;
  public $address;
  public $postcode;
  public $city;
  public $phone;
  public $email;
  
  public $message;
  
  public $event;
  public $childEvents = [];
  public $tickets = [];
  
  public $extras = [];
  public $acceptTos;
  
  public static function validate($reservation) {
    $espai = Espai::getInstance();
    $httpClient = HttpClient::getInstance($espai->baseUrl, $espai->apiKey);
    $errors = $httpClient->POST('/webservice/reservations/validate', $reservation);
    return $errors;
  }
  
  public static function send($reservation) {
    $espai = Espai::getInstance();
    $httpClient = HttpClient::getInstance($espai->baseUrl, $espai->apiKey);
    $httpClient->POST('/webservice/reservations/submit', $reservation);
  }
  
}
