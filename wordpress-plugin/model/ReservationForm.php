<?php

namespace app\espai\wordpress;

class ReservationForm {

  public $name;
  public $termsConditionsUrl;
  public $privacyUrl;
  public $fields;
  
  public static function getReservationForm($reservation) {
    $espai = Espai::getInstance();
    $httpClient = HttpClient::getInstance($espai->baseUrl, $espai->apiKey);
    $form = $httpClient->POST('/webservice/reservations/reservationForm', Reservation::toPayload($reservation));
    return $form;
  }
  
  public static function of($form) {
    $result = new ReservationFormField();
    
    foreach ($form as $key => $value) {
      if (property_exists($result, $key)) {
        switch ($key) {
          case "fields":
            $result->$key = [];
            foreach ($value as $item) {
              $result->$key[] = ReservationFormField::of($item);
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

class ReservationFormField {

  public $label;
  public $name;
  public $presets;
  public $required;
  
  public static function of($field) {
    $result = new ReservationFormField();
    
    foreach ($field as $key => $value) {
      if (property_exists($result, $key)) {
        switch ($key) {
          case "presets":
            $result->$key = [];
            foreach ($value as $item) {
              $result->$key[] = $item;
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
