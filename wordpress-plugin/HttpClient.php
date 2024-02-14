<?php
namespace app\espai\wordpress;

// avoid script kiddies
defined( 'ABSPATH' ) or die( 'Sorry, please don\'t try this.' );


/**
 * API client for doing REST requests.
 */
class HttpClient {
  
  private static $_instance = null;
  
  public static function getInstance($baseUrl, $apiKey) {
    if (null === self::$_instance) {
      self::$_instance = new self($baseUrl, $apiKey);
    }
    
    return self::$_instance;
  }
  
  private $baseUrl;
  private $apiKey;
  
  private $cache = array();
  
  /**
   * Init the Client.
   * 
   * @param string $baseUrl the API's base URL
   * @param type $apiKey the API key to authenticate requests
   */
  private function __construct($baseUrl, $apiKey) {
    
    // make sure base URL ends with a slash
    if (substr($baseUrl, strlen($baseUrl) - 1) != "/") {
      $baseUrl = $baseUrl . "/";
    }
    $this->baseUrl = $baseUrl;
    $this->apiKey = $apiKey;
  }
  
  /**
   * Do a GET request.
   * 
   * @param string $url the URL relative to the base URL
   * @param array() $params the query params
   * @return Object the deserialized response
   */
  public function GET($url, $params = []) {  
    
    // combine base URL and given URL
    $requestUrl = $this->getRequestUrl($url);
    
    // build and attach query string?
    if (count($params) > 0) {
      $queryString = "";
      foreach($params as $key => $param) {
        if (strlen($queryString) > 0) {
          $queryString .= "&";
        }
        $queryString .= $key . '=' . urlencode($param);
      }

      if (strpos($requestUrl, "?") === FALSE) {
        $requestUrl .= "?" . $queryString;
      } else {
        $requestUrl .= "&" . $queryString;
      }
    }
    
    if (array_key_exists($requestUrl, $this->cache)) {
      return $this->cache[$requestUrl];
    }
    
    // prepare header
    $header = array();
    $header[] = "Accept: application/json";
    $header[] = "Authorization: Bearer " . $this->apiKey;
    
    // do request
    $request = curl_init($requestUrl);
    curl_setopt($request, CURLOPT_HEADER, FALSE);
    curl_setopt($request, CURLOPT_HTTPHEADER, $header);
    curl_setopt($request, CURLOPT_RETURNTRANSFER, TRUE);
    $response = json_decode(curl_exec($request));
    
    $this->cache[$requestUrl] = $response;
    
    // return result
    return $response;
  }
  
  /**
   * Do a POST request.
   * 
   * @param string $url the URL relative to the base URL
   * @param Object $payload the object to send
   * @return Object the deserialized response
   */
  public function POST($url, $payload = NULL) {
    
    // combine base URL and given URL
    $requestUrl = $this->getRequestUrl($url);
    
    // create request
    $header = array();
    $header[] = "Accept: application/json";
    $header[] = "Authorization: Bearer " . $this->apiKey;
    $header[] = 'Content-Type: application/json';
    
    // create request
    $request = curl_init($requestUrl);
    curl_setopt($request, CURLOPT_HTTPHEADER, $header);
    curl_setopt($request, CURLOPT_RETURNTRANSFER, 1);
    curl_setopt($request, CURLOPT_POST, 1);
    
    // add payload?
    if ($payload != NULL) {
      
      // encode and add payload
      $encodedPayload = json_encode($payload, JSON_PRETTY_PRINT);
      curl_setopt($request, CURLOPT_POSTFIELDS, $encodedPayload);
    }
    
    curl_setopt($request, CURLOPT_VERBOSE, true);
    
    $response = curl_exec($request);
    
    return json_decode($response);
  }
  
  /**
   * Combines base URL with the given URL.
   * 
   * @param string $path the URL relative to the base URL
   * @return string the full URL
   */
  private function getRequestUrl($path) {
    return HttpClient::combineUrl($this->baseUrl, $path);
  }
  
  /**
   * Combines base URL and path to a full URL.
   * 
   * @param string $baseUrl the base URL
   * @param string $path a path relative to the base URL
   * @return string the combined URL
   */
  public static function combineUrl($baseUrl, $path) {
    if (substr($baseUrl, strlen($baseUrl) - 1) != "/") {
      $baseUrl = $baseUrl . "/";
    }
    
    if (substr($path, 0, 1) == "/") {
      $path = substr($path, 1);
    }
    return $baseUrl . $path;
  }
  
}