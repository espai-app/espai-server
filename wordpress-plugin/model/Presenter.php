<?php

namespace app\espai\wordpress;

class Presenter {
  
  public $id;
  public $givenName;
  public $surname;
  
  public static function of($presenter) {
    $result = new Presenter();
    
    foreach ($presenter as $key => $value) {
      if (property_exists($result, $key)) {
        $result->$key = $value;
      }
    }
    
    return $result;
  }
  
}
