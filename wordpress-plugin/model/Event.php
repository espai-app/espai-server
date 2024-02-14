<?php

namespace app\espai\wordpress;

class Event {
  
  public $id;
  public $date;
  public $time;
  public $title;
  public $version;
  public $production;
  public $venue;
  public $host;
  public $coHost;
  public $availableTickets;
  public $ticketLimit;
  public $mandatory;
  public $eventSerial;
  public $attachments;
  public $childEvents;
  public $prices;
  
  public static function get($eventId) {
    $espai = Espai::getInstance();
    
    $httpClient = HttpClient::getInstance($espai->baseUrl, $espai->apiKey);
    $event = $httpClient->GET(
            '/webservice/season/' . $espai->seasonId . '/events/' . $eventId);
    
    return Event::of($event);
  }
  
  public static function listForEventSerial($serialId) {
    $espai = Espai::getInstance();
    
    $httpClient = HttpClient::getInstance($espai->baseUrl, $espai->apiKey);
    $events = $httpClient->GET(
            '/webservice/season/' . $espai->seasonId . '/events', 
            ['eventSerialId' => $serialId]);
    
    return self::convertList($events);
  }

  public static function listForProduction($productionId) {
    $espai = Espai::getInstance();
    
    $httpClient = HttpClient::getInstance($espai->baseUrl, $espai->apiKey);
    $events = $httpClient->GET(
            '/webservice/season/' . $espai->seasonId . '/events', 
            ['productionId' => $productionId]);
    
    return self::convertList($events);
  }
  
  public static function listForVenue($venueId) {
    $espai = Espai::getInstance();
    
    $httpClient = HttpClient::getInstance($espai->baseUrl, $espai->apiKey);
    $events = $httpClient->GET(
            '/webservice/season/' . $espai->seasonId . '/events', 
            ['venueId' => $venueId]);
    
    return self::convertList($events);
  }
  
  private static function convertList($eventList) {
    $result = [];
    foreach ($eventList as $e) {
      $result[] = self::of($e);
    }
    
    return $result;
  }
  
  public static function of($event) {
    $result = new Event();
    
    foreach ($event as $key => $value) {
      if (property_exists($result, $key)) {
        switch ($key) {
          case "production": 
            $result->$key = Production::of($value); 
            break;
          case "venue": 
            $result->$key = Venue::of($value);
            break;
          case "host":
          case "coHost":
            $result->$key = Presenter::of($value); 
            break;
          case "eventSerial":
            $result->$key = EventSerial::of($value);
            break;
          case "childEvents":
            $result->$key = [];
            foreach ($value as $child) {
              $result->$key[] = Event::of($child);
            }
            break;
          case "prices":
            $result->$key = [];
            foreach ($value as $price) {
              $result->$key[] = EventTicketPrice::of($price);
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
