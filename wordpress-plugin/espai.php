<?php
/**
 * @package espai-wordpress
 * @version 1.0
 */
/*
  Plugin Name: Connector for espai
  Plugin URI: https://www.borowski.it/
  Description: This plugin connects the Wordpress instance to the espai backend.
  Author: Ralph Borowski
  Version: 1.0
  Author URI: https://www.borowski.it/
 */
namespace app\espai\wordpress;
defined('ABSPATH') or die('Sorry, please don\'t try this.');

define('ESPAI_BASE_PATH', __DIR__);
define('ESPAI_PLUGIN', __FILE__);

require_once('HttpClient.php');

require_once('model/Attachment.php');
require_once('model/EventSerial.php');
require_once('model/EventTicketPrice.php');
require_once('model/Presenter.php');
require_once('model/Production.php');
require_once('model/Reservation.php');
require_once('model/Venue.php');
require_once('model/SeasonVenue.php');
require_once('model/Event.php');

require_once('shortcodes/EventListShortcode.php');




class Espai {

  public $baseUrl;
  public $seasonId;
  public $apiKey;

  private static $instance = null;

  private function __contruct() {

  }

  public static function getInstance() {
    if (self::$instance == null) {
      self::$instance = new Espai();
    }

    return self::$instance;
  }
}

$espaiInstance = Espai::getInstance();

$espaiInstance->baseUrl ="https://skwmanager.schulkinowoche.de/";
// $espaiInstance->baseUrl ="http://192.168.56.1:8080/espai/";
$espaiInstance->seasonId = 3001;

class EspaiPlugin {

  public static function init() {
    add_action('init', array(get_called_class(), 'start_session'));
    add_action('init', array(get_called_class(), 'add_rewrite_rules'));
    add_filter('query_vars', array(get_called_class(), 'register_query_vars'));
    add_action('template_include', array(get_called_class(), 'register_template'));
    add_action('wp_enqueue_scripts', array(get_called_class(), 'register_scripts_and_styles'));

    add_filter( 'document_title_parts', array(get_called_class(), 'register_title'));
  }

  public static function activate() {
    self::add_rewrite_rules();
    flush_rewrite_rules();
  }

  public static function deactivate() {
    flush_rewrite_rules();
  }

  public static function start_session() {
    if (!session_id()) {
      session_start();
    }
  }

  public static function add_rewrite_rules() {
    add_rewrite_rule('orte[/]?$', 'index.php?espai_page=venue_index', 'top');
    add_rewrite_rule('orte/([a-z0-9-]+)-([0-9]+)', 'index.php?espai_page=venue_details&espai_venue_id=$matches[2]', 'top');
    add_rewrite_rule('filme[/]?$', 'index.php?espai_page=production_index', 'top');
    add_rewrite_rule('filme/([a-z0-9-]+)-([0-9]+)$', 'index.php?espai_page=production_details&espai_production_id=$matches[2]', 'top');
    add_rewrite_rule('programm[/]?$', 'index.php?espai_page=schedule', 'top');
    add_rewrite_rule('programm/([0-9]{4}-[0-9]{2}-[0-9]{2})[/]?$', 'index.php?espai_page=schedule&espai_date=$matches[1]', 'top');
    add_rewrite_rule('buchen/veranstaltung/([a-z0-9-]+)-([0-9]+)$', 'index.php?espai_page=checkout_event&espai_event_id=$matches[2]', 'top');
    add_rewrite_rule('buchen/adresse', 'index.php?espai_page=checkout_address', 'top');
    add_rewrite_rule('buchen/zusammenfassung', 'index.php?espai_page=checkout_summary', 'top');
    add_rewrite_rule('buchen/danke', 'index.php?espai_page=checkout_success', 'top');
    add_rewrite_rule('buchen/fehler', 'index.php?espai_page=checkout_error', 'top');
  }

  public static function register_query_vars($query_vars) {
    $query_vars[] = 'espai_page';
    $query_vars[] = 'espai_venue_id';
    $query_vars[] = 'espai_production_id';
    $query_vars[] = 'espai_event_id';
    $query_vars[] = 'espai_date';
    return $query_vars;
  }

  public static function register_scripts_and_styles() {
    wp_register_style( 'primeflex', trailingslashit(plugin_dir_url(ESPAI_PLUGIN)) . 'css/primeflex.css');
    wp_enqueue_style( 'primeflex' );

    wp_register_style( 'espai', trailingslashit(plugin_dir_url(ESPAI_PLUGIN)) . 'css/espai.css');
    wp_enqueue_style( 'espai' );
  }

  public static $ALLOWED_PAGES = ['checkout_address', 'checkout_error',
      'checkout_event', 'checkout_success', 'checkout_summary',
      'production_details', 'production_index', 'venue_details',
      'venue_index', 'schedule'];

  public static function register_template($template) {
    $page_name = get_query_var('espai_page');
    if ($page_name == false || $page_name == '' || !in_array($page_name, self::$ALLOWED_PAGES)) {
      return $template;
    }

    return ESPAI_BASE_PATH . '/pages/' . $page_name . '.php';
  }

  public static function register_title($title_parts) {
    global $espai_title;

    if ($espai_title) {
      $title_parts['site'] = $espai_title;
    }

    return $title_parts;
  }

  public static function formatDate($date) {
    return date("d.m.Y", strtotime($date));
  }

  public static function formatTime($time) {
    return substr($time, 0, 5);
  }

  public static function slugify($value) {
    $slug = str_replace(['ä', 'ö', 'ü', 'ß'], ['ae', 'oe', 'ue', 'ss'], strtolower($value));
    $slug = preg_replace('/[^\da-z0-9]/i', '-', $slug);
    $slug = preg_replace('/(-){2,}/i', '-', $slug);
    return $slug;
  }
}

EspaiPlugin::init();
