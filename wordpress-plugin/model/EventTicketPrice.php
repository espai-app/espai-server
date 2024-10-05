<?php

namespace app\espai\wordpress;

class EventTicketPrice {

  public $id;
  public $seatCategory;
  public $priceCategory;
  public $description;
  public $price;

  public static function of($price) {
    $result = new EventTicketPrice();

    foreach ($price as $key => $value) {
      if (property_exists($result, $key)) {
        $result->$key = $value;
      }
    }

    return $result;
  }

}