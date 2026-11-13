<?php

namespace app\espai\wordpress;

class ProductionVersion {

  public $id;
  public $versionName;
  public $mediaFormat;
  public $spokenLanguage;
  public $audioDescription;
  public $subtitles;
  public $closedCaption;

  public static function of($version) {

    $result = new ProductionVersion();
    foreach ($version as $key => $value) {
      $result->$key = $value;
    }

    return $result;
  }

}
