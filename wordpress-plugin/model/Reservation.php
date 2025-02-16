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
  
  public static function checkTicketAvailability($reservation) {
    $espai = Espai::getInstance();
    $httpClient = HttpClient::getInstance($espai->baseUrl, $espai->apiKey);
    $errors = $httpClient->POST('/webservice/reservations/checkTicketAvailability', self::toPayload($reservation));
    return $errors;
  }
  
  public static function validate($reservation) {
    $espai = Espai::getInstance();
    $httpClient = HttpClient::getInstance($espai->baseUrl, $espai->apiKey);
    $errors = $httpClient->POST('/webservice/reservations/validate', self::toPayload($reservation));
    return $errors;
  }
  
  public static function send($reservation) {
    $espai = Espai::getInstance();
    $httpClient = HttpClient::getInstance($espai->baseUrl, $espai->apiKey);
    $httpClient->POST('/webservice/reservations/submit', self::toPayload($reservation));
  }
  
  private static function toPayload(Reservation $reservation) {
    $payload = [];
    $payload['company'] = $reservation->company;
    $payload['givenName'] = $reservation->givenName;
    $payload['surname'] = $reservation->surname;
    $payload['address'] = $reservation->address;
    $payload['city'] = $reservation->city;
    $payload['phone'] = $reservation->phone;
    $payload['email'] = $reservation->email;
    
    $payload['message'] = $reservation->message;
    $payload['event'] = $reservation->event->id;
    if (!empty($reservation->childEvents)) {
      $payload['childEvents'] = [];
      foreach ($reservation->childEvents as $c) {
        $payload['childEnvents'][] = $c->id;
      }
    }
    
    if (!empty($reservation->tickets)) {
      $payload['tickets'] = [];
      foreach ($reservation->tickets as $key => $value) {
        $payload['tickets'][$key] = (int) $value;
      }
    }
    $payload['extras'] = $reservation->extras;
    $payload['acceptTos'] = isset($reservation->acceptTos) 
            ? $reservation->acceptTos : false;
    
    return $payload;
  }
  
}
