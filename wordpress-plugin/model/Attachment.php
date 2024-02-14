<?php

namespace app\espai\wordpress;

class Attachment {
  
  public $id;
  public $mediaType;
  public $mimeType;
  public $position;
  public $caption;
  public $location;
  public $copyright;
  
  public static function of($attachment) {
    $espai = Espai::getInstance();
    
    $result = new Attachment();
    foreach ($attachment as $key => $value) {
      if (property_exists($result, $key)) {
        switch ($key) {
          case "location":
            if (substr($value, 0, 1) == '/' ) {
              $result->$key = HttpClient::combineUrl($espai->baseUrl, $value);
            } else {
              $result->$key = $value;
            }
            break;
          default:
            $result->$key = $value;
        }
      }
    }
    
    return $result;
  }
  
}