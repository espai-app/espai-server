<?php

namespace app\espai\wordpress;

class HallCapacity {

  public $seatCategoryId;
  public $seatCategoryName;
  public $capacity;

  public static function convertList($capacityList) {
    $result = [];
    foreach ($capacityList as $e) {
      $result[] = self::of($e);
    }

    return $result;
  }

  public static function of($capacity) {
    $result = new HallCapacity();

    foreach ($capacity as $key => $value) {
      if (property_exists($result, $key)) {
        $result->$key = $value;
      }
    }

    return $result;
  }
}
