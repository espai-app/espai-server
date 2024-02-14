<?php

namespace app\espai\wordpress;

class EventSerial {
  
  public $id;
  public $name;
  
  public static function of($serial) {
    $result = new EventSerial();
    
    foreach ($serial as $key => $value) {
      if (property_exists($result, $key)) {
        $result->$key = $value;
      }
    }
    
    return $result;
  }
  
}

